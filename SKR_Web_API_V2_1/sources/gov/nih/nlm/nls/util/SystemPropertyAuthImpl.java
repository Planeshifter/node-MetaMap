package gov.nih.nlm.nls.util;

import java.net.*;
import java.awt.*;
import javax.swing.*;

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
 * Supply the system property arguments before specifying main class.
 * <pre>
 *   java -Dnls.service.username=&lt;username&gt; -Dnls.service.password=&lt;password&gt; class args
 * </pre>
 * @author <a href="mailto:wrogers@nls10.nlm.nih.gov">Willie Rogers</a>
 * @version $Id: SystemPropertyAuthImpl.java,v 1.1 2005/05/23 17:38:40 wrogers Exp $
 */
public class SystemPropertyAuthImpl extends Authenticator
{
  private String username = System.getProperty("nls.service.username", "");
  private char[] password = System.getProperty("nls.service.password", "").toCharArray();

  /**
   * Set username from user specified property.  If property is null
   * or not present use default property nls.service.username.
   *
   * @param usernameProperty name of property containing username.
   */
  public void setUsernameViaProperty(String usernameProperty)
  {
    this.username = System.getProperty(usernameProperty, 
				       System.getProperty("nls.service.username"));              
  }
  
  /**
   * Set password from user specified property.If property is null
   * or not present use default property nls.service.password.
   *
   * @param passwordProperty name of property containing password.
   */
  public void setPasswordViaProperty(String passwordProperty)
  {
    this.password = System.getProperty(passwordProperty,
				       System.getProperty("nls.service.password")).toCharArray();
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
