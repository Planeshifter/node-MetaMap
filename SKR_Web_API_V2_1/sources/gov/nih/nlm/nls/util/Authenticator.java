package gov.nih.nlm.nls.util;

import java.net.PasswordAuthentication;

/**
 * Abstract class of Authenticators.
 *
 * 
 * Created: Thu Apr  7 11:19:45 2011
 *
 * @author <a href="mailto:wjrogers@mail.nih.gov">Willie Rogers</a>
 * @version 1.0
 */
public abstract class Authenticator {

  /**
   * Creates a new <code>Authenticator</code> instance.
   *
   */
  public Authenticator() {
  }
  /**
   * Get password authentication credentials.
   *
   * @return a filled-in PasswordAuthentication instance.
   */
  public PasswordAuthentication getPasswordAuthentication() {
    return null;
  }

}
