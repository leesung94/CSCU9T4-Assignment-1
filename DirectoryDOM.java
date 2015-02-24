import java.io.*; // import input-output

import javax.xml.parsers.*; // import parsers
import javax.xml.xpath.*; // import XPath

import org.w3c.dom.*; // import DOM

/**
 * DOM handler to read an XML phone directory, to add to this, and to print it
 * in HTML format.
 * 
 * @author 2114299
 * @version Your Submission date
 */
public class DirectoryDOM {

	/** Document builder */
	private static DocumentBuilder builder = null;

	/** XML document */
	private static Document document = null;

	/** XPath expression */
	private static XPath path = null;

	/** HTML file to be written */
	private static PrintStream writer;

	/**
	 * Main program to call DOM parser.
	 * 
	 * @param args
	 *            command-line arguments
	 */
	public static void main(String[] args) {
		try {
			writer = // open HTML output file
			new PrintStream("Directory.html");
			loadDocument("Directory.xml"); // load directory input file
			appendNumber("Emergency", "999"); // add emergency number
			printHeader(); // print HTML header
			serialiseNode(document); // print nodes in HTML format
			printTrailer(); // print HTML trailer
			writer.close(); // close HTML file
		} catch (Exception exception) {
			System.err.println("could not create HTML file " + exception);
		}
	}

	/**
	 * Append subscriber name and number to directory document.
	 */
	private static void appendNumber(String name, String number) {
		// Creates a new subscriber and the two elements to be added into the
		// subscriber
		Element newSub = document.createElement("subscriber");
		Element newSubName = document.createElement("name");
		Element newSubNumber = document.createElement("phone");
		// Creates two text nodes and passes in the text
		Node subName = document.createTextNode(name);
		Node subNumber = document.createTextNode(number);
		// Adds the two text node to the two elements
		newSubName.appendChild(subName);
		newSubNumber.appendChild(subNumber);
		// Adds the two elements to the subscriber
		newSub.appendChild(newSubName);
		newSub.appendChild(newSubNumber);
		appendSubscriber(newSub);
	}

	/**
	 * Append a subscriber element to the document directory element.
	 * 
	 * @param subscriber
	 *            subscriber element to append
	 */
	private static void appendSubscriber(Element subscriber) {
		NodeList nodes = // get all directory nodes
		document.getElementsByTagName("directory");
		if (nodes != null) { // at least one such node?
			Node directoryNode = nodes.item(0); // get the first (and only) one
			directoryNode.appendChild(subscriber); // append subscriber to this
		}
	}

	/**
	 * Return given attribute of a node if it exists.
	 * 
	 * @param name
	 *            attribute name
	 * @param node
	 *            node to check for attributes
	 * @return attribute value (empty string if not found)
	 */
	private static String getAttribute(String name, Node node) {
		String value = ""; // initialise attribute value
		if (node instanceof Element) { // node is element?
			value = // get attribute of element
			((Element) node).getAttribute(name);
			if (value == null) // value does not exist?
				value = ""; // set empty attribute value
		}
		return (value); // return attribute value
	}

	/**
	 * Set global document by reading the given file.
	 * 
	 * @param filename
	 *            XML file to read
	 */
	private static void loadDocument(String filename) {
		/*
		 * Get a document builder and an XPath evaluator from the relevant
		 * factories. Then parse the given file.
		 */
		try {
			// create a document builder
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory
					.newInstance();
			builder = builderFactory.newDocumentBuilder();

			// create an XPath expression
			XPathFactory xpathFactory = XPathFactory.newInstance();
			path = xpathFactory.newXPath();

			// parse the document for later searching
			document = builder.parse(new File(filename));
		} catch (Exception exception) {
			System.err.println("could not load document " + exception);
		}
	}

	/**
	 * Print HTML header to start HTML and start table.
	 */
	private static void printHeader() {
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
	 * Print HTML trailer to end table, print subscriber counts, and end HTML.
	 */
	private static void printTrailer() {
		// Appends the bottom part of the HTML to the file
		writer.append("             </table>\n");
		writer.append("         <p><strong>Stirling numbers</strong>: "
				+ query("count(/directory/subscriber/phone[@exchange='01786'])")
				+ "</p>\n");
		writer.append("         <p><strong>Total numbers</strong>: "
				+ query("count(/directory/subscriber)") + "</p>\n");
		writer.append("     </body>\n");
		writer.append("</html>\n");
	}

	/**
	 * Get result of XPath query.
	 * 
	 * @param query
	 *            XPath query
	 * @return result of query
	 */
	private static String query(String query) {
		/*
		 * Return the result of evaluating the XPath query.
		 */
		String result = "";
		try {
			result = path.evaluate(query, document);
		} catch (Exception exception) {
			System.err.println("could not perform query - " + exception);
		}
		return (result);
	}

	/**
	 * Loop through child nodes, serialising them in turn.
	 * 
	 * @param node
	 *            some node
	 * @throws input
	 *             -output exception
	 */
	private static void scanChildren(Node node) throws IOException {
		// Checks for child nodes of the current element, if found then attempts
		// to serialise the node
		NodeList children = node.getChildNodes();
		if (children != null)
			for (int i = 0; i < children.getLength(); i++)
				serialiseNode(children.item(i));
	}

	/**
	 * Print node information recursively.
	 * 
	 * @param node
	 *            some node
	 * @throws input
	 *             -output exception
	 */
	private static void serialiseNode(Node node) {
		/*
		 * Check for document, element and text nodes. Output appropriate HTML
		 * for each of these.
		 */
		try {
			switch (node.getNodeType()) {
			case Node.DOCUMENT_NODE:
				scanChildren(node);
				break;
			case Node.ELEMENT_NODE:
				String name = node.getNodeName();
				// If the node name is phone then starts new table row processes
				// the data as well as the child nodes and then ends table row
				if (name.equals("phone")) {
					scanAttributes(node);
					writer.append("<td>");
					scanChildren(node);
					writer.append("</tr>\n");
				}
				// However if the name of the node is name it will start a new
				// table row and table data
				else if (name.equals("name")) {
					writer.append("				<tr><td>");
					scanChildren(node);
				} else {
					scanChildren(node);
				}
				break;
			case Node.TEXT_NODE:
				// If the node happens to be a text node it will process the
				// node and end table data
				String text = node.getNodeValue().trim();
				if (text.length() > 0) {
					writer.append(text + "</td>");
				}
				break;
			}
		} catch (IOException exception) {
			System.err.println("could not serialise document " + exception);
		}
	}

	/**
	 * Loop through attributes, printing them in turn.
	 * 
	 * @param node
	 *            some node
	 * @throws input
	 *             -output exception
	 */
	private static void scanAttributes(Node node) throws IOException {
		NamedNodeMap attributes = node.getAttributes();
		String hasAttribute = getAttribute("exchange", node);
		// If the attribute has contains no text then places a dash in its place
		if (hasAttribute.equals(""))
			writer.append("<td>-</td>");
		// Else it will process the attribute number
		else if (attributes != null) {
			int count = attributes.getLength();
			if (count > 0) {
				for (int i = 0; i < count; i++) {
					Node attribute = attributes.item(i);
					writer.append("<td>" + attribute.getNodeValue() + "</td>");
				}
			}
		}
	}
}
