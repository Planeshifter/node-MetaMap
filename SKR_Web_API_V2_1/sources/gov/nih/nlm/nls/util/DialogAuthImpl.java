package gov.nih.nlm.nls.util;

import java.net.PasswordAuthentication;
import java.awt.*;
import javax.swing.*;

/**
 * An http authenthicator using a GUI-based dialog.
 *
 * This http 1.1 authenticator uses a GUI-based Dialog (written in
 * Swing) to prompt the user to supply the username and password
 * required to use the web service.
 *
 * @author <a href="mailto:wrogers@nls10.nlm.nih.gov">Willie Rogers</a>
 * @version $Id: DialogAuthImpl.java,v 1.2 2003/08/08 19:03:55 wrogers Exp $
 */


public class DialogAuthImpl extends Authenticator {
  public PasswordAuthentication getPasswordAuthentication() {
      JTextField username = new JTextField();
      JTextField password = new JPasswordField();
      JPanel panel = new JPanel(new GridLayout(2,2));
      panel.add(new JLabel("User Name"));
      panel.add(username);
      panel.add(new JLabel("Password") );
      panel.add(password);
      int option = JOptionPane.showConfirmDialog(null, new Object[] { panel },
                   "Enter Network Password",
                   JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
      if ( option == JOptionPane.OK_OPTION ) {
         String user = username.getText();
         char pass[] = password.getText().toCharArray();
         return new PasswordAuthentication(user, pass);
      } else {
         return null;
      }
   }
}
