package gov.nih.nlm.nls.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.Map;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Describe class PostUtils here.
 *
 *
 * Created: Tue Apr 12 10:09:59 2011
 *
 * @author <a href="mailto:wjrogers@mail.nih.gov">Willie Rogers</a>
 * @version 1.0
 */
public class PostUtils {

  /**
   * Creates a new <code>PostUtils</code> instance.
   *
   */
  public PostUtils() { }


  /**
   * Build a multipart form entity from supplied entity map.
   *
   * @param elementMap dictionary of form elements.
   * @return multipart form entity 
   */
  public static MultipartEntity buildMultipartEntity( Map<String,ContentBody> elementMap ) {
    MultipartEntity formEntity = new MultipartEntity( HttpMultipartMode.BROWSER_COMPATIBLE );
    for (Map.Entry<String,ContentBody> entry: elementMap.entrySet()) {
      formEntity.addPart(entry.getKey(), entry.getValue());
    }
    return formEntity;
  }

  /**
   * Build a multipart form entity from supplied entity map.
   *
   * @param elementMap dictionary of form elements.
   * @param mode mode of multipart form entity to be constructed.
   * @return multipart form entity 
   */
  public static MultipartEntity buildMultipartEntity( Map<String,ContentBody> elementMap,
						      HttpMultipartMode mode )
  {
    MultipartEntity formEntity = new MultipartEntity( mode );
    for (Map.Entry<String,ContentBody> entry: elementMap.entrySet()) {
      formEntity.addPart(entry.getKey(), entry.getValue());
    }
    return formEntity;
  }

  /**
   * Build a JSON entity from supplied entity map.
   *
   * @param elementMap dictionary of form elements.
   * @return JSON entity.
   */
  public static String buildJSONEntity( Map<String,ContentBody> elementMap ) 
  {
    JSONObject jsonObj = new JSONObject();
    try {
      for (Map.Entry<String,ContentBody> entry: elementMap.entrySet()) {
	if (entry.getValue() instanceof StringBody) {
	  StringWriter sw = new StringWriter();
	  BufferedReader rdr =
	    new BufferedReader(((StringBody)entry.getValue()).getReader());
	  String line;
	  while ((line = rdr.readLine()) != null) {
	    sw.write(line); sw.write('\n');
	  }
	  jsonObj.put(entry.getKey(), sw.toString());
	  rdr.close();
	  sw.close();
	} else if (entry.getValue() instanceof FileBody) {
	  StringWriter sw = new StringWriter();
	  BufferedReader rdr =
	    new BufferedReader(new InputStreamReader
			       (((FileBody)entry.getValue()).getInputStream()));
	  String line;
	  while ((line = rdr.readLine()) != null) {
	    sw.write(line); sw.write('\n');
	  }
	  jsonObj.put(entry.getKey(), sw.toString());
	  rdr.close();
	  sw.close();
	}
      }
    } catch (JSONException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
    return jsonObj.toString();
  }

  /**
   * Build a XML entity from supplied entity map. (not implemented).
   *
   * @param elementMap dictionary of form elements.
   * @return XML entity.
   */
  public static String buildXMLEntity( Map<String,ContentBody> elementMap ) {
    return null;
  }
}
