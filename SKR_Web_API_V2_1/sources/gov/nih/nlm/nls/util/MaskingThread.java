package gov.nih.nlm.nls.util;

import java.io.*;
import java.util.*;

/**
 * This class attempts to erase characters echoed to the console.
 *
 * This code is originally from the Sun Developer Network (SDN) site.<br>
 *    Name: Password Masking in the Java Programming Language<br>
 *    URL: http://java.sun.com/developer/technicalArticles/Security/pwordmask/<br>
 *    Author: Qusay H. Mahmoud with contributions from Alan Sommerer<br>
 *    Date: July 2004
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

class MaskingThread extends Thread {
   private volatile boolean stop;
   private char echochar = '*';
 
  /**
   *@param prompt The prompt displayed to the user
   */
   public MaskingThread(String prompt) {
      System.err.print(prompt + " ");
   }
 
  /**
   * Begin masking until asked to stop.
   */
   public void run() {
 
      int priority = Thread.currentThread().getPriority();
      Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
      int i = 0;

      try {
         stop = true;
         while(stop) {
           System.err.print("\010" + echochar);
           try {
              // attempt masking at this rate
              Thread.currentThread().sleep(1);
           }catch (InterruptedException iex) {
              Thread.currentThread().interrupt();
              return;
           }
         }
      } finally { // restore the original priority
         Thread.currentThread().setPriority(priority);
      }
   }
 
  /**
   * Instruct the thread to stop masking.
   */
   public void stopMasking() {
      this.stop = false;
   }
} // MaskingThread
