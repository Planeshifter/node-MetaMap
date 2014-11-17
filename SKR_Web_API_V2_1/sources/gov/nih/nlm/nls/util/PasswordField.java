package gov.nih.nlm.nls.util;

import java.io.*;
import java.util.*;

/**
 * This class prompts the user for a password and attempts to mask input
 *  with "*".
 *<br><br>
 * This code is originally from the Sun Developer Network (SDN) site.<br>
 *    Name: Password Masking in the Java Programming Language<br>
 *    URL: http://java.sun.com/developer/technicalArticles/Security/pwordmask/<br>
 *    Author: Qusay H. Mahmoud with contributions from Alan Sommerer<br>
 *    Date: July 2004
 *<br><br>
 *  Modifications from original code:<br>
 *
 *    14Feb06 - JGM: Removed option of specifying InputStream
 *<br><br>
 * License (from Sun Developer Network site)
 *<br><br>
 * Copyright 1994-2005 Sun Microsystems, Inc. All Rights Reserved.
 *<br><br>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *<br><br>
 *   * Redistribution of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *<br><br>
 *   * Redistribution in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *<br><br>
 * Neither the name of Sun Microsystems, Inc. or the names of contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *<br><br>
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN MICROSYSTEMS, INC. ("SUN") AND
 * ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A
 * RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 * IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT
 * OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR
 * PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY,
 * ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF SUN HAS
 * BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *<br><br>
 * You acknowledge that this software is not designed, licensed or intended for
 * use in the design, construction, operation or maintenance of any nuclear
 * facility.
 * 
 */

public class PasswordField {
 
  /**
   *@param prompt The prompt to display to the user.
   *@return The password as entered by the user.
   */
 
   public static final char[] getPassword(String prompt) 
       throws IOException {
      InputStream in = System.in;
      MaskingThread maskingthread = new MaskingThread(prompt);
      Thread thread = new Thread(maskingthread);
      thread.start();
        
      char[] lineBuffer;
      char[] buf;
      int i;
 
      buf = lineBuffer = new char[128];
 
      int room = buf.length;
      int offset = 0;
      int c;
 
      loop:   while (true) {
         switch (c = in.read()) {
            case -1:
            case '\n':
               break loop;
 
            case '\r':
               int c2 = in.read();
               if ((c2 != '\n') && (c2 != -1)) {
                  if (!(in instanceof PushbackInputStream)) {
                     in = new PushbackInputStream(in);
                  } // fi

                  ((PushbackInputStream)in).unread(c2);
                } // fi
                else {
                  break loop;
                } // else
 
                default:
                   if (--room < 0) {
                      buf = new char[offset + 128];
                      room = buf.length - offset - 1;
                      System.arraycopy(lineBuffer, 0, buf, 0, offset);
                      Arrays.fill(lineBuffer, ' ');
                      lineBuffer = buf;
                   } // fi
                   buf[offset++] = (char) c;
                   break;
         } // switch
      } // loop

      maskingthread.stopMasking();
      if (offset == 0) {
         return null;
      } // fi

      char[] ret = new char[offset];
      System.arraycopy(buf, 0, ret, 0, offset);
      Arrays.fill(buf, ' ');
      return ret;
   } // getPassword
} // PasswordField
