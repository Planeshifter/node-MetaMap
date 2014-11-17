package gov.nih.nlm.nls.util;

import java.util.*;
import java.io.*;
import java.net.PasswordAuthentication;

/**
 * <p>
 * This class reads .netrc to acquire authentication credentials.
 * Class looks for machinename "skrweb" to find username and password.
 * </p>
 * <p>
 * Add a line of this form to ~/.netrc:
 * <pre>
 * machine skrweb login &lt;username&gt; password &lt;password&gt;
 * </pre>
 * For example:
 * <pre>
 * machine skrweb login enzo password mainframe
 * </pre>
 * </p>
 * <p>
 * Location of .netrc file can be overridden by using system property
 * <tt>nls.server.netrcfile</tt>:
 * <pre>
 * java -Dnls.server.netrcfile=/usr/local/credentials/group.netrc ...
 * </pre>
 * The line above specifies <tt>/usr/local/credentials/group.netrc</tt>
 * as the netrc file.
 * </p>
 * <p>
 * machine name defaults to "skrweb" but can be overriden by property
 * <tt>nls.server.machinename</tt>:
 * <pre>
 * java -Dnls.server.machinename=skr2web  ...
 * </pre>
 * </p>
 *
 * Created: Thu Jun  2 14:26:17 2005
 *
 * @author <a href="mailto:wrogers@nlm.nih.gov">Willie Rogers</a>
 * @version $Id: NetRcAuthImpl.java,v 1.2 2005/11/01 14:57:39 wrogers Exp $
 */
public class NetRcAuthImpl extends Authenticator {
  /** location of netrc file, defaults to ".netrc" in user's home directory */
  private String netrcFilename = 
    System.getProperty("nls.service.netrcfile",
		       System.getProperty("user.home") + "/.netrc");
  /** name of machine in netrc file */
  private String defaultMachinename =
    System.getProperty("nls.service.machinename", "skrweb");
  private String username = null;
  private char[] password = null;

  public NetRcAuthImpl() 
    throws java.io.IOException, java.io.FileNotFoundException
  {
    this.getNetrcCredentials(this.defaultMachinename);
  } // NetRcAuthImpl constructor

  public NetRcAuthImpl(String machinename) 
    throws java.io.IOException, java.io.FileNotFoundException
  {
    this.getNetrcCredentials(machinename);
  } // NetRcAuthImpl constructor
  
  public List split(String line, String delimitors) 
  {
    List tokens = new ArrayList();
    StringTokenizer st = new StringTokenizer(line, delimitors);
    while (st.hasMoreTokens()) {
      tokens.add(st.nextToken());
    }
    return tokens;
  }

  /** @param machinename name in machine field representing service in .netrc file. */
  public void getNetrcCredentials(String machinename)
    throws java.io.IOException, java.io.FileNotFoundException
  {
    BufferedReader reader = 
      new BufferedReader(new FileReader(netrcFilename));
    String line = null;
    while ((line = reader.readLine()) != null) {
      List tokens = this.split(line, "| \t\n\r\f");
      // System.out.println("no of tokens: " + tokens.size());
      if (((String)tokens.get(1)).equals(machinename)) {
	this.username = (String)tokens.get(3);
	this.password = ((String)tokens.get(5)).toCharArray();
	break;
      }
    }
    reader.close();
  }
 
  public PasswordAuthentication getPasswordAuthentication() {
    if ( this.username != null && this.password != null) {
      return new PasswordAuthentication(this.username, this.password);
    } else {
      return null;
    } 
  }
} // NetRcAuthImpl
