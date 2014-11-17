package gov.nih.nlm.nls.skr;

import java.lang.*;
import java.util.*;
import java.io.*;
import java.net.PasswordAuthentication;
import java.nio.charset.Charset;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.util.EntityUtils;

import gov.nih.nlm.nls.util.Authenticator;
import gov.nih.nlm.nls.util.PropertyAuthImpl;
import gov.nih.nlm.nls.util.PostUtils;
import gov.nih.nlm.nls.cas.CasAuth;

/**
 * Generic job specific fields and handling routines.
 *<br><br>
 * NOTES:<br>
 *    1. Generic jobs must have commands that reside in /nfsvol/nls/bin
 *       on our internal computer systems.  Specified via the Batch_Command
 *       field.  For example, 'setField("Batch_Command", "MTI -opt1_DCMS -E")'.
 *<br><br>
 *    2. Commands for the Generic jobs must NOT have ".." embedded in their path
 *<br><br>
 *    3. Generic jobs require validation where the Scheduler expects
 *       an "<< EOT >>" end of result marker so it can verify it received a
 *       complete result.  All of the SKR utilities require the addition
 *       of the "-E" option for this purpose.
 *<br><br>
 *    4. Generic jobs also allow you to specify any special environment<br>
 *       variables you might need inorder to run your command.
 *       Specified via the Batch_Env field.  For example, 
 *       'setField("Batch_Env", "NLS=/nfsvol/nls#CC=gcc")'.  Where each
 *       environment variable is separated by a "#". Note that these are not
 *       needed to run the MTI command in the above example, but are here for
 *       illustrative purposes only.
 *
 *       Original Author: Jim Mork (September 18, 2006)
 *       Updated by Willie Rogers to allow for CAS authentication
 *
 * @author	Willie Rogers
 * @version	2.0, June 20, 2011
 */

public class GenericObject
{
  /** url of cas authentication server, property: skrapi.cas.serverurl */
  public final String casAuthServer =
    System.getProperty("skrapi.cas.serverurl",
		       "https://utslogin.nlm.nih.gov/cas/v1/tickets");

  /** url of skr api service, property:  skrapi.serviceurl*/
  public final String service =
    System.getProperty("skrapi.serviceurl",
		       "http://skr.nlm.nih.gov/cgi-bin/SKR/Restricted_CAS/API_batchValidationII.pl");

  /** url of skr Interactive MetaMap api service, property:  skrapi.servicemminterurl*/
  public final String serviceMMInter =
    System.getProperty("skrapi.servicemminterurl",
		       "http://skr.nlm.nih.gov/cgi-bin/SKR/Restricted_CAS/API_MM_interactive.pl");

  /** url of skr Interactive SemRep api service, property:  skrapi.servicesrinterurl*/
  public final String serviceSRInter =
    System.getProperty("skrapi.servicesrinterurl",
		       "http://skr.nlm.nih.gov/cgi-bin/SKR/Restricted_CAS/API_SR_interactive.pl");

  /** cas service ticket */
  private String serviceTicket = "";

  /** authenticator class name, property: nls.service.authenticator,
   * default get username and password from console : @see gov.nih.nls.util.ConsoleAuthImpl
   * see also java.net.Authenticator and java.net.PasswordAuthentication
   */
  private String authenticatorClassName = 
    System.getProperty("nls.service.authenticator", "gov.nih.nlm.nls.util.ConsoleAuthImpl");

  /** get the password for CAS using this method */
  private Authenticator authenticator = null;

  /** container for username and password */
  private PasswordAuthentication pa = null;

  /** service ticket timestamp, when ticket was acquired. */
  private Calendar ticketTimeStamp = Calendar.getInstance();

  /** Final service we will use will be set here */
  private String privService = "";

  /** service ticket timeout: default 0 minutes */
  public final static int ticketTimeout =
    Integer.parseInt(System.getProperty("skrapi.cas.ticket.timeout", "0"));

  /** storage for form elements */
  Map<String,ContentBody> formMap = new HashMap<String,ContentBody>();
  // MultipartEntity formEntity = new MultipartEntity( HttpMultipartMode.BROWSER_COMPATIBLE );
 

  // ************************************************************************

  /** default constructor */
  public GenericObject() {
    this.privService = service;
    this.promptCredentials();
    this.pa = this.authenticator.getPasswordAuthentication();
    this.serviceTicket =
      CasAuth.getTicket(casAuthServer, this.pa.getUserName(),
                        new String(this.pa.getPassword()), this.privService);
    this.ticketTimeStamp = Calendar.getInstance();
    this.initFields();
    try {
      this.formMap.put("RUN_PROG", new StringBody
		       ("GENERIC_V", "text/plain", Charset.forName( "UTF-8" )));
      this.formMap.put("SKR_API", new StringBody
		       ("true", "text/plain", Charset.forName( "UTF-8" )));
      this.formMap.put("Batch_Command", new StringBody
		       ("skr", "text/plain", Charset.forName( "UTF-8" )));
    } catch (UnsupportedEncodingException  e) {
      throw new RuntimeException(e);
    }
  } // Default GenericObject

  /**
   * Creates a new GenericObject object using the specified information.
   *
   *  This constructor sets up a Generict with Validation Batch job
   *  request without prompting the user for the username/password
   *  information.  NOTE: Care should be taken when using this since
   *  your authentication information is available in the code!!
   *
   * @param  username
   * @param  password
   */

  public GenericObject(String username, String password) {
    this.privService = service;
    this.promptCredentials();
    this.authenticator = new PropertyAuthImpl();
    ((PropertyAuthImpl)this.authenticator).setUsername(username);
    ((PropertyAuthImpl)this.authenticator).setPassword(password);
    this.pa = this.authenticator.getPasswordAuthentication();
    this.serviceTicket =
      CasAuth.getTicket(casAuthServer, this.pa.getUserName(),
                        new String(this.pa.getPassword()), this.privService);
    this.ticketTimeStamp = Calendar.getInstance();
    this.initFields();
    try {
      this.formMap.put("RUN_PROG", new StringBody
		       ("GENERIC_V", "text/plain", Charset.forName( "UTF-8" )));
      this.formMap.put("SKR_API", new StringBody
		       ("true", "text/plain", Charset.forName( "UTF-8" )));
      this.formMap.put("Batch_Command", new StringBody
		       ("skr", "text/plain", Charset.forName( "UTF-8" )));
    } catch (UnsupportedEncodingException  e) {
      throw new RuntimeException(e);
    }
  } // Default GenericObject with Username/Password specified

  /**
   * Creates a new GenericObject object using the specified information.
   *
   *  This constructor sets up a Generict with Validation Interactive job
   *  request
   *
   * @param  whichInteractive  100 = MetaMap, 200 = SemRep
   */
  public GenericObject(int whichInteractive)
  {
    if(whichInteractive == 200)
      this.privService = serviceSRInter;
    else
      this.privService = serviceMMInter;
    this.promptCredentials();
    this.pa = this.authenticator.getPasswordAuthentication();
    this.serviceTicket =
      CasAuth.getTicket(casAuthServer, this.pa.getUserName(),
                        new String(this.pa.getPassword()), this.privService);
    this.ticketTimeStamp = Calendar.getInstance();
    this.initFields();
    try {
      this.formMap.put("RUN_PROG", new StringBody
		       ("GENERIC_V", "text/plain", Charset.forName( "UTF-8" )));
      this.formMap.put("SKR_API", new StringBody
		       ("true", "text/plain", Charset.forName( "UTF-8" )));
      this.formMap.put("Batch_Command", new StringBody
		       ("skr", "text/plain", Charset.forName( "UTF-8" )));
    } catch (UnsupportedEncodingException  e) {
      throw new RuntimeException(e);
    }
  } // Interactive GenericObject

  /**
   * Creates a new GenericObject object using the specified information.
   *
   *  This constructor sets up a Generict with Validation Interactive job
   *  request without prompting the user for the username/password
   *  information.  NOTE: Care should be taken when using this since
   *  your authentication information is available in the code!!
   *
   * @param  whichInteractive  100 = MetaMap, 200 = SemRep
   * @param  username
   * @param  password
   */

  public GenericObject(int whichInteractive, String username, String password) {
    if(whichInteractive == 200)
      this.privService = serviceSRInter;
    else
      this.privService = serviceMMInter;
    this.promptCredentials();
    this.authenticator = new PropertyAuthImpl();
    ((PropertyAuthImpl)this.authenticator).setUsername(username);
    ((PropertyAuthImpl)this.authenticator).setPassword(password);
    this.pa = this.authenticator.getPasswordAuthentication();
    this.serviceTicket =
      CasAuth.getTicket(casAuthServer, this.pa.getUserName(),
                        new String(this.pa.getPassword()), this.privService);
    this.ticketTimeStamp = Calendar.getInstance();
    this.initFields();
    try {
      this.formMap.put("RUN_PROG", new StringBody
		       ("GENERIC_V", "text/plain", Charset.forName( "UTF-8" )));
      this.formMap.put("SKR_API", new StringBody
		       ("true", "text/plain", Charset.forName( "UTF-8" )));
      this.formMap.put("Batch_Command", new StringBody
		       ("skr", "text/plain", Charset.forName( "UTF-8" )));
    } catch (UnsupportedEncodingException  e) {
      throw new RuntimeException(e);
    }
  }

  // ************************************************************************

  /** Prompt user for username/password. */
  void promptCredentials() {
    try {
      Class authenticatorClass = Class.forName(this.authenticatorClassName);
      // Authenticator.setDefault((Authenticator)authenticatorClass.newInstance());
      this.authenticator = (Authenticator)authenticatorClass.newInstance();
    } catch (java.lang.ClassNotFoundException exception) {
      System.err.println("Class " + authenticatorClassName + " not found!");
      exception.printStackTrace(System.err);
    } catch (java.lang.InstantiationException exception) {
      System.err.println("Unable to instantiate Class " + authenticatorClassName);
      exception.printStackTrace(System.err);
    } catch (java.lang.IllegalAccessException exception) {
      System.err.println("Illegal access of Class " + authenticatorClassName);
      exception.printStackTrace(System.err);
    }
  }

  /**
   * Print content of entity.
   *
   * @param respEntity http response entity
   * @return string containing content of entity.
   */
  String printEntity(HttpEntity respEntity)
    throws IOException
  {
    if (respEntity.getContentType().equals("text/html")) {
      StringBuffer sb = new StringBuffer();
      BufferedReader br = new BufferedReader(new InputStreamReader(respEntity.getContent()));
      String line;
      while ((line = br.readLine()) != null) {
	sb.append(line).append('\n');
      }
      br.close();
      System.out.print("response content: " + sb.toString());
      return sb.toString();
    } 
    return null;
  }

  /**
   * Determine if email entry of form has a well formed email address.
   * @parm addressBody content of email entry of form.
   * @return true if email is well formed.
   */
  public boolean emailIsWellFormed(ContentBody addressBody) {
    if (addressBody != null)
      if (addressBody instanceof StringBody) {
	if (((StringBody)addressBody).getContentLength() == 0) {
	  return false;
	} else {
	  try {
	    StringBuilder sb = new StringBuilder();
	    BufferedReader reader =
	      new BufferedReader(((StringBody)addressBody).getReader());
	    String line;
	    while ((line = reader.readLine()) != null) {
	      sb.append(line);
	    }
	    reader.close();
	    String address = sb.toString();
	    if (address.equals("youraddress@goeshere")) { 
	      System.out.println("Error: "+ address + " is not a valid email address.");
	      return false;
	    }
	    if (address.indexOf("@",1) == -1) { return false; }
	    if (address.indexOf(".",1) == -1) { return false; }
	    return true;
	  } catch (IOException e) {
	    System.err.println("problems reading address string body");
	    e.printStackTrace();
	    throw new RuntimeException(e);
	  }
	}
      }
    return false;
  }

  /**
   * Determine if email entry of form is valid.
   * @return true if email is valid.
   */
  public boolean validEmail() {
    return this.formMap.containsKey("Email_Address") &&
      this.emailIsWellFormed(this.formMap.get("Email_Address"));
  }

  // ************************************************************************

  /**
   * Control the Batch job submission after validating command.
   *
   * @return string containing content of server response.
   */
  public String handleSubmission()
  {
    // address of proxy server
    // HttpHost proxy = new HttpHost("127.0.0.1", 8080, "http");
    HttpClient client = new DefaultHttpClient();

    try {
      // use proxy for client
      // client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);

      // get a new ticket and reset timestamp
      this.serviceTicket =
	CasAuth.getTicket(casAuthServer, this.pa.getUserName(),
			  new String(this.pa.getPassword()), this.privService);
      this.ticketTimeStamp = Calendar.getInstance();
      if (this.validEmail()) {
	MultipartEntity formEntity = PostUtils.buildMultipartEntity( this.formMap );
	HttpPost post = new HttpPost(this.privService + "?ticket=" + this.serviceTicket);
	post.setEntity(formEntity);
	// System.out.println("post request: " + post.getRequestLine() );
	HttpResponse response = client.execute(post);
	if (response.getStatusLine().getStatusCode() == 302) {
	  // System.out.println("PAGE :" + EntityUtils.toString(response.getEntity()));
	  EntityUtils.consume(response.getEntity()); // consume response input to release connection.
	  // ignore 302 redirect and resubmit request with ticket.
	  post = new HttpPost(this.privService + "?ticket=" + this.serviceTicket);
	  post.setEntity(formEntity);
	  // System.out.println("post request: " + post.getRequestLine() );
	  response = client.execute(post);
	  HttpEntity respEntity = response.getEntity();
	  if (respEntity != null) {
	    StringBuffer rtn = new StringBuffer();
	    BufferedReader in = new BufferedReader(new InputStreamReader(respEntity.getContent()));
	    String line = "";
	    while((line = in.readLine()) != null)
	      {
		if(!line.startsWith("NOT DONE LOOP")) {
		  rtn.append(line);
		  rtn.append("\n");
		} // fi
	      }
	    return rtn.toString();
	  }
	} else {
	  HttpEntity respEntity = response.getEntity();
	  if (respEntity != null) {
	    StringBuffer rtn = new StringBuffer();
	    BufferedReader in = new BufferedReader(new InputStreamReader(respEntity.getContent()));
	    String line = "";
	    while((line = in.readLine()) != null)
	      {
		if(!line.startsWith("NOT DONE LOOP")) {
		  rtn.append(line);
		  rtn.append("\n");
		} // fi
	      }
	    return rtn.toString();
	  }
	}
      } else {
	System.err.println("Error: Email Address must be specified");
	throw new RuntimeException();
      }
    } catch (java.io.UnsupportedEncodingException e) {
      //LOG.warning(e.getMessage());
      e.printStackTrace();
      throw new RuntimeException(e);
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    } finally {
      // When HttpClient instance is no longer needed,
      // shut down the connection manager to ensure
      // immediate deallocation of all system resources
      client.getConnectionManager().shutdown();
    }
    return null;
  } // handleSubmission

  // ************************************************************************

  /**
   * Insert and configure Generic Batch specific fields into the fieldsList.
   */
  public void initFields()
  {
    // Note: In RFC 2388 - Returning Values from Forms:
    // multipart/form-data only field name and field value information
    // is transmitted in the http request.  All other information is
    // discarded.  
    try {
      this.formMap.put("SKR_API", new StringBody
		       ("true", "text/plain", Charset.forName( "UTF-8" )));
      this.formMap.put("Batch_Command", new StringBody
		       ("", "text/plain", Charset.forName( "UTF-8" )));
      
      this.formMap.put("Batch_Env", new StringBody
		       ("", "text/plain", Charset.forName( "UTF-8" )));
    } catch (UnsupportedEncodingException  e) {
      throw new RuntimeException(e);
    }
  } // initFields

  // ************************************************************************

  /**
   * Set the stringFieldValue of the requested field/option for this job.
   *
   * @param  fieldName   Name of the field to be updated
   * @param  fieldValue  New String value for the field
   */
  public void setField(String fieldName, String fieldValue)
  {
    try {
      this.formMap.put(fieldName, new StringBody
		       (fieldValue, "text/plain", Charset.forName( "UTF-8" )));
    } catch (UnsupportedEncodingException  e) {
      throw new RuntimeException(e);
    }
  } // setField

  // ************************************************************************

  /**
   * Set the booleanFieldValue of the requested field/option for this job.
   *
   * @param  fieldName    Name of the field to be updated
   * @param  fieldState  New String value for the field
   */
  public void setField(String fieldName, boolean fieldState)
  {
    try {
      this.formMap.put(fieldName, new StringBody
		       (Boolean.toString(fieldState), "text/plain", Charset.forName( "UTF-8" )));
    } catch (UnsupportedEncodingException  e) {
      throw new RuntimeException(e);
    }
  } // setField


  // ************************************************************************

  /**
   * Set file field of the requested field/option for this job.
   *
   * @param  fieldName     Name of the field to be updated
   * @param  localFilename Name of file to add to POST request
   */
  public void setFileField(String fieldName, String localFilename)
  {
    File localFile = new File(localFilename);
    this.formMap.put(fieldName, new FileBody( localFile, "text/plain" ));
  } // setFileField

  /**
   * Set file field of the requested field/option for this job using
   * in-memory buffer.
   *
   * @param  fieldName     Name of the field to be updated
   * @param  bufferFilename Name of file to add to POST request
   */
  public void setFileBufferField(String fieldName, String bufferFilename, String buffer)
  {
    try {
      // this is an inconvient hack to allow the HTTP components to
      // read this as a file.
      File localFile = File.createTempFile("skrapi_" + bufferFilename, null);
      BufferedWriter bw = new BufferedWriter(new FileWriter(localFile));
      bw.write(buffer);
      bw.close();
      this.formMap.put(fieldName, new FileBody( localFile, "text/plain" ));
    } catch (IOException  e) {
      throw new RuntimeException(e);
    }
  } // setFileField

} // class GenericObject
