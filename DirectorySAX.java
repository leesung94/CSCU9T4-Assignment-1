// DirectorySAX.java - Complete

import java.io.*;
import javax.xml.parsers.*;

import org.w3c.dom.*;
import org.xml.sax.*;

/**
  SAX event handler to output in HTML format.

  @author       K. J. Turner
  @version      09/01/14
*/
public class DirectorySAX {

  /**
    Main program to call SAX parser.

    @param args                 command-line arguments
  */
  public static void main(String[] args) {
    parse("Directory");
  }

  /**
    Callback when parser finds character data.

    @param baseFile             base filename to read (without suffix)
  */
  private static void parse(String baseFile) {
    try {
      // get an instance of SAXParserFactory and get an XMLReader from it
      SAXParserFactory factory = SAXParserFactory.newInstance();
      XMLReader reader = factory.newSAXParser().getXMLReader();

      // turn off XML validation
      reader.setFeature("http://xml.org/sax/features/validation", false);

      // register the relevant handler with the parser, choosing one of:
      DirectoryHTML handler = new DirectoryHTML(baseFile);
      reader.setContentHandler(handler);
      reader.setErrorHandler(handler);

      // parse the given file
      InputSource inputSource = new InputSource(baseFile + ".xml");
      reader.parse(inputSource);
    }
    catch (Exception exception) {
      System.err.println("could not parse file - " + exception);
    }
  }

}
