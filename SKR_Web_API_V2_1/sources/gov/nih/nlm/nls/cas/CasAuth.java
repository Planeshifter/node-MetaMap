package gov.nih.nlm.nls.cas;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;

/**
 * An example Java client to authenticate against CAS using REST services.
 * Please ensure you have followed the necessary setup found on the <a
 * href="http://www.ja-sig.org/wiki/display/CASUM/RESTful+API">JASIG wiki</a>.
 *
 * <pre>
 * ticket = getTicket(casserverurl, username, password, serviceurl);
 * String String document = getProtectedDocument(serviceurl, ticket);
 * </pre>
 *
 * @author <a href="mailto:jieryn@gmail.com">jesse lauren farinacci</a>
 * @since 3.4.2
 * updated to use HTTP component library (http://hc.apache.org/) by Willie Rogers.
 */
public final class CasAuth
{
  private static final Logger LOG = Logger.getLogger(CasAuth.class.getName());

  private CasAuth()
  {
    // static-only access
  }


  /**
   * Obtain a  Single-Use Proxy Ticket from Central Authentication Server (CAS).
   * @param server authentication server
   * @param username client user name 
   * @param password client password
   * @param service url of service with protected resources
   * @return authentication ticket for service.
   */
  public static String getTicket(final String server, final String username,
				 final String password, final String service)
  {
    notNull(server, "server must not be null");
    notNull(username, "username must not be null");
    notNull(password, "password must not be null");
    notNull(service, "service must not be null");

    return getServiceTicket(server, getTicketGrantingTicket(server, username,
							    password), service);
  }

  /**
   * Obtain a Single-Use Proxy Ticket (also known as service ticket).
   * Request for a Service Ticket:
   * <pre>
   *     POST /cas/v1/tickets/{TGT id} HTTP/1.0
   * </pre>
   *     data:
   * <pre>
   *     service={form encoded parameter for the service url}
   * </pre>     
   *    Sucessful Response:
   * <pre>
   *     200 OK
   *     
   *     ST-1-FFDFHDSJKHSDFJKSDHFJKRUEYREWUIFSD2132
   * </pre>
   *
   * @param server authentication server
   * @param ticketGrantingTicket a Proxy Granting Ticket.
   * @param service url of service with protected resources
   * @return authentication ticket for service.
   *
   */
  private static String getServiceTicket(final String server,
					 final String ticketGrantingTicket, final String service)
  {
    if (ticketGrantingTicket == null)
      return null;

    final HttpClient client = new DefaultHttpClient();

    List<NameValuePair> formparams = new ArrayList<NameValuePair>();
    formparams.add(new BasicNameValuePair("service", service));
    try {
      UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, "UTF-8");
      final HttpPost post = new HttpPost(server + "/" + ticketGrantingTicket);
      post.setEntity(entity);

      // Create a response handler
      ResponseHandler<String> responseHandler = new BasicResponseHandler();
      String responseBody = client.execute(post, responseHandler);
      // System.out.println("response: " + responseBody);
      return responseBody;
    } catch (final   java.io.UnsupportedEncodingException  e) {
      LOG.warning(e.getMessage());
    } catch (final IOException e) {
      LOG.warning(e.getMessage());
    } finally {
      // When HttpClient instance is no longer needed,
      // shut down the connection manager to ensure
      // immediate deallocation of all system resources
      client.getConnectionManager().shutdown();
    }
    return null;
  }

  /**
   * Obtain a Proxy Granting Ticket.
   * Response for a Ticket Granting Ticket Resource  
   * <pre>
   *   POST /cas/v2/tickets HTTP/1.0
   * </pre>
   *   data:
   * <pre>
   *   username=battags&password=password&additionalParam1=paramvalue
   * </pre>
   *  Successful Response:
   * <pre>
   *   201 Created
   *   Location: http://www.whatever.com/cas/v1/tickets/{TGT id}
   * </pre>
   * @param server authentication server
   * @param username client user name 
   * @param password client password
   * @return a Proxy Granting Ticket.
   */
  private static String getTicketGrantingTicket(final String server,
						final String username, final String password)
  {
    final HttpClient client = new DefaultHttpClient();

    List<NameValuePair> formparams = new ArrayList<NameValuePair>();
    formparams.add(new BasicNameValuePair("username", username));
    formparams.add(new BasicNameValuePair("password", password));
    try {
      UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, "UTF-8");
      final HttpPost post = new HttpPost(server);
      post.setEntity(entity);

      ResponseHandler<String> responseHandler = new BasicResponseHandler();
      String response = client.execute(post, responseHandler);
      // System.out.println("response: " + response);
      final Matcher matcher = Pattern.compile(".*action=\".*/(.*?)\".*")
	.matcher(response);
      
      if (matcher.matches()) {
	// System.out.println("ticket: " +  matcher.group(1));
	return matcher.group(1);
      }
    } catch (final   java.io.UnsupportedEncodingException  e) {
      LOG.warning(e.getMessage());
    } catch (final IOException e) {
      LOG.warning(e.getMessage());
    } finally {
      client.getConnectionManager().shutdown();
    }

    return null;
  }

  /**
   * Request for protected document using service ticket.
   * <pre>
   *    GET &lt;resource url>?ticket=<service ticket> HTTP/1.0
   * </pre>
   * example:
   * <pre>
   *   http://wsd.nlm.nih.gov/Restricted/Non-Reviewed_Results/index.shtml?ticket=ST-1-FFDFHDSJKHSDFJKSDHFJKRUEYREWUIFSD2132
   * </pre>
   *   Successful Response:
   * <pre>
   *   200
   * </pre>
   *    document is returned in body of response.
   * <p>
   * @param service url of service with protected resources
   * @param ticket authentication ticket for service.
   * @return document or null if authentication invalid.
   */
  static String getProtectedDocument(String service, String ticket)
  {
    final HttpClient client = new DefaultHttpClient();

    try { 
      final HttpGet getReq = new HttpGet(service + "?ticket=" + ticket);
      ResponseHandler<String> responseHandler = new BasicResponseHandler();
      String responseBody = client.execute(getReq, responseHandler);
      return responseBody;
    } catch (final IOException e) {
      LOG.warning(e.getMessage());
    } finally {
      client.getConnectionManager().shutdown();
    }
    return null;
  }

  private static void notNull(final Object object, final String message)
  {
    if (object == null)
      throw new IllegalArgumentException(message);
  }

  public static void main(final String[] args)
  {
    // final String server = "http://localhost:8080/cas/v1/tickets";
    // final String username = "username";
    // final String password = "password";
    // final String service = "http://localhost:8080/service";

    if (args.length > 1) {
      final String server = "https://utslogin.nlm.nih.gov/cas/v1/tickets";
      final String username = args[1];
      final String password = args[0];
      final String service = "http://wsd.nlm.nih.gov/Restricted/Non-Reviewed_Results/index.shtml";
      String ticket = getTicket(server, username, password, service);
      LOG.info(ticket);
      String document = getProtectedDocument(service, ticket);
      LOG.info(document);
    } else {
      System.out.println("usage: cas.Client username password");
    }
  }
}
