/*
===========================================================================
*
*                            PUBLIC DOMAIN NOTICE
*               National Center for Biotechnology Information
*         Lister Hill National Center for Biomedical Communications
*
*  This software is a "United States Government Work" under the terms of the
*  United States Copyright Act.  It was written as part of the authors' official
*  duties as a United States Government contractor and thus cannot be
*  copyrighted.  This software is freely available to the public for use. The
*  National Library of Medicine and the U.S. Government have not placed any
*  restriction on its use or reproduction.
*
*  Although all reasonable efforts have been taken to ensure the accuracy
*  and reliability of the software and data, the NLM and the U.S.
*  Government do not and cannot warrant the performance or results that
*  may be obtained by using this software or data. The NLM and the U.S.
*  Government disclaim all warranties, express or implied, including
*  warranties of performance, merchantability or fitness for any particular
*  purpose.
*
*  Please cite the authors in any work or product based on this material.
*
===========================================================================
*/

/**
 * Example program for submitting an Interactive MetaMap request.
 *
 * This example shows how to setup a basic Interactive MetaMap request.
 * This runs the latest version of MetaMap with 1011 (2010AB) version
 * of the UMLS Metathesaurus ("KSOURCE", "1011"), with "ignore_word_order" (-i)
 * and "all_derivational_variants" (D) set as arguments to MetaMap.  The
 * default "Human Readable" output will be produced.
 *
 * This example also shows how to setup the constructor with an embedded
 * username and password so your program doesn't prompt for the information.
 *
 * @author	Jim Mork
 * @version	1.0, June 15, 2011
**/


import java.io.*;
import gov.nih.nlm.nls.skr.*;

public class MMInteractiveUser
{
   public static void main(String args[])
   {
        GenericObject myIntMMObj = new GenericObject(100, "username", "password");

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


        myIntMMObj.setField("Email_Address", "");

        StringBuffer buffer = new StringBuffer("A spinal tap was performed and oligoclonal bands were detected in the cerebrospinal fluid.\n");
        String bufferStr = buffer.toString();
        myIntMMObj.setField("APIText", bufferStr);

        myIntMMObj.setField("KSOURCE", "1011");

        // Optional field, program will run default MetaMap if not specified

        myIntMMObj.setField("COMMAND_ARGS", "-iD");

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
} // class MMInteractiveUser
