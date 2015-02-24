import java.io.*; // import input-output classes
import org.xml.sax.*; // import SAX classes
import org.xml.sax.helpers.*; // import SAX helper classes

/**
 * SAX event handler to output in HTML format.
 * 
 * @author 2114299
 * @version Your Submission Date
 */
public class DirectoryHTML extends DefaultHandler {

	/** Number of Stirling subscribers */
	private int stirlingCount = 0;

	/** Total number of subscribers */
	private int totalCount = 0;

	/** HTML file to be written */
	private PrintStream writer;

	/**
	 * Construct an HTML parser.
	 * 
	 * @param baseFile
	 *            base filename to create (without suffix)
	 * @throws input
	 *             -output exception
	 */
	public DirectoryHTML(String baseFile) throws IOException {
		writer = new PrintStream(baseFile + ".html");
	}

	/**
	 * Callback when parser finds character data.
	 * 
	 * @param ch
	 *            character data
	 * @param start
	 *            character start index
	 * @param length
	 *            character count
	 * @throws SAX
	 *             exception
	 */
	public void characters(char ch[], int start, int length) {
		String characters = new String(ch, start, length).trim();
		if (!characters.isEmpty())
			writer.append(characters + " ");
	}

	/**
	 * Callback when parser starts to read a document.
	 * 
	 * @throws SAX
	 *             exception
	 */
	public void startDocument() throws SAXException {
		// Appends the top part of the HTML to the file
		writer.append("<!DOCTYPE html>\n");
		writer.append("   <html>\n");
		writer.append("       <head>\n");
		writer.append("           <title>Phone List</title>\n");
		writer.append("       </head>\n");
		writer.append("       <body>\n");
		writer.append("           <table border='1'>\n");
		writer.append("               <tr><th>Name</th><th>Exchange</th><th>Number</th></tr>\n");
	}

	/**
	 * Callback when parser finds the end of a document.
	 * 
	 * @throws SAX
	 *             exception
	 */
	public void endDocument() throws SAXException {
		// Appends the bottom part of the HTML to the file
		writer.append("             </table>\n");
		writer.append("         <p><strong>Stirling numbers</strong>: "
				+ stirlingCount + "</p>\n");
		writer.append("         <p><strong>Total numbers</strong>: "
				+ totalCount + "</p>\n");
		writer.append("     </body>\n");
		writer.append("</html>\n");
	}

	/**
	 * Callback when parser starts to read an element.
	 * 
	 * @param namespaceURI
	 *            namespace URI
	 * @param localName
	 *            local namespace identifier
	 * @param qName
	 *            qualified name for namespace
	 * @param attributes
	 *            elements attributes
	 * @throws SAX
	 *             exception
	 */
	public void startElement(String namespaceURI, String localName,
			String qName, Attributes attributes) throws SAXException {
		// If the element name is name then start a new table row and table data
		if (qName.equals("name"))
			writer.append("			   <tr><td>");
		else if (qName.equals("phone")) {
			// If the attribute for the current phone element has a length of
			// zero append a dash to the table data
			if (attributes.getLength() == 0)
				writer.append("<td>-</td>");
			// Else process the attribute for the current phone element
			else {
				for (int i = 0; i < attributes.getLength(); i++) {
					// If exchange is equal to 01786 increment the stirlingCount
					// counter and append 01786 to the table
					if (attributes.getValue("exchange").equals("01786")) {
						writer.append("<td>" + attributes.getValue(i) + "</td>");
						stirlingCount++;
						// Else just append the exchange number to the table
					} else {
						writer.append("<td>" + attributes.getValue(i) + "</td>");
					}
				}
			}
			writer.append("<td>");
			totalCount++;
		}
	}

	/**
	 * Callback when parser finds the end of an element.
	 * 
	 * @param namespaceURI
	 *            namespace URI
	 * @param localName
	 *            local namespace identifier
	 * @param qName
	 *            qualified name for namespace
	 * @throws SAX
	 *             exception
	 */
	public void endElement(String namespaceURI, String localName, String qName)
			throws SAXException {
		// If the element name is phone end the table data and table row
		if (qName.equals("phone")) {
			writer.append("</td></tr>\n");
		// Else if the element name is name then just end the table data
		} else if (qName.equals("name")) {
			writer.append("</td>");
		}
	}
}
