package gov.nih.nlm.nls.util;

import java.io.*;

/**
 * Simple prompt and read from standard input.
 *
 * @author	Jim Mork
 * @version	1.0, September 18, 2006
 */

public class Utils
{
   /**
    * Prompts the user using text in prompt, reads input from the standard
    * input device, and then returns the read text.
    *
    * @param   prompt  The base level command to be run for display only
    * @return  a String object with the resulting input text
   */

   public static String getTextInput(String prompt) throws IOException
   {
        StringBuffer buffer = new StringBuffer("");
        System.err.print(prompt);
        String Line = "";
           
        BufferedReader in = 
               new BufferedReader(new InputStreamReader(System.in));
        if((Line = in.readLine()) != null)
           buffer.append(Line);

        return(buffer.toString());
   }  // getTextInput
} // class Utils
