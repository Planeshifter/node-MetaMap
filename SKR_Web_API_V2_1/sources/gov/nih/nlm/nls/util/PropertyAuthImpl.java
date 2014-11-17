package gov.nih.nlm.nls.util;

import java.net.PasswordAuthentication;
import java.awt.*;
import javax.swing.*;
import java.util.Properties;

/**
 * An http authenthicator that uses system properities for initialization.
 *
 * This http 1.1 authenticator is initialized using system properties:
 * <dl>
 *  <dt>nls.service.username 
 *  <dd>username to supply to service 
 *  <dt>nls.service.password 
 *  <dd>password to supply to service 
 * </dl>
 * <pre>
 *   nls.service.username=&lt;username&gt;
 *   nls.service.password=&lt;password&gt;
 * </pre>
 * @author <a href="mailto:wrogers@nls10.nlm.nih.gov">Willie Rogers</a>
 * @version $Id: PropertyAuthImpl.java,v 1.3 2005/05/24 13:40:01 wrogers Exp $
 */
public class PropertyAuthImpl extends Authenticator
{
  private String username = null;
  private char[] password = null;
    
  /**
   * Set username from user specified property.  If property is null
   * or not present use default property nls.service.username.
   *
   * @param properties properties object 
   */
  public void setUsernameViaProperty(Properties properties)
  {
    this.username = properties.getProperty("nls.service.username");
  }
  
  /**
   * Set password from user specified property.If property is null
   * or not present use default property nls.service.password.
   *
   * @param properties properties object 
   */
  public void setPasswordViaProperty(Properties properties)
  {
    this.password = properties.getProperty("nls.service.password").toCharArray();
  }

  /**
   * Set username from supplied string.
   * @param username name of user.
   */
  public void setUsername(String username)
  {
    this.username = username;
  }

  /**
   * Set password from supplied string.
   * @param password password of user.
   */
  public void setPassword(String password) 
  {
    this.password = password.toCharArray();
  }
  
  public PasswordAuthentication getPasswordAuthentication() {
    if ( this.username != null && this.password != null) {
      return new PasswordAuthentication(this.username, this.password);
    } else {
      return null;
    }
  }
}
