import java.io.*;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import gov.nih.nlm.nls.skr.*;

public class MMCustom
{

  public static String readFile(String path, Charset encoding)
    throws IOException
  {
    byte[] encoded = Files.readAllBytes(Paths.get(path));
    return new String(encoded, encoding);
  }

   public static void main(String args[])
   {
	String emailAddress = null;
	String username = null;
	String password = null;
  String text = null;

  StringBuffer buffer = new StringBuffer("A spinal tap was performed and oligoclonal bands were detected in the cerebrospinal fluid.\n");
  String bufferStr = buffer.toString();

  try {
    text = readFile("temp.txt", StandardCharsets.UTF_8);
  } catch(IOException e){
    text = bufferStr;
  }

	int i = 0;
	    while (i < args.length) {
	      if (args[i].charAt(0) == '-') {
          if ( args[i].equals("-e") || args[i].equals("--email")) {
      		  i++;
      		  emailAddress = args[i];
      		} else if ( args[i].equals("-u") || args[i].equals("--username")) {
      		  i++;
      		  username = args[i];
      		} else if ( args[i].equals("-p") || args[i].equals("--password")) {
      		  i++;
      		  password = args[i];
      		}
	      i++;
	    }
    }

        GenericObject myIntMMObj = new GenericObject(100, username, password);

        // REQUIRED FIELDS:
        //    -- Email_Address
        //    -- APIText
        //    -- KSOURCE
        //         valid KSOURCE: 99, 06, 09, 0910, 10, 1011
        //         respectively, UMLS 1999, 2006AA, 2009AA, 2009AB,
        //                       2010AA, and 2010AB
        //
        // NOTE: The maximum length is 10,000 characters for APIText.  The
        //       submission script will reject your request if it is larger.


        myIntMMObj.setField("Email_Address", emailAddress);
	      myIntMMObj.setField("SilentEmail", true);

        myIntMMObj.setField("APIText", text);

	      myIntMMObj.setField("KSOURCE", "2014AA");

        myIntMMObj.setField("COMMAND_ARGS", "--XMLn");

        // Submit the job request

        try
        {
           String results = myIntMMObj.handleSubmission();
           System.out.print(results);

        } catch (RuntimeException ex) {
           System.err.println("");
           System.err.print("An ERROR has occurred while processing your");
           System.err.println(" request, please review any");
           System.err.print("lines beginning with \"Error:\" above and the");
           System.err.println(" trace below for indications of");
           System.err.println("what may have gone wrong.");
           System.err.println("");
           System.err.println("Trace:");
           ex.printStackTrace();
        } // catch
   } // main
} // class MMInteractive
