package swingjs.xml;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;

import javajs.util.AU;
import javajs.util.PT;
import javajs.util.Rdr;
import swingjs.JSUtil;
import swingjs.api.js.DOMNode;

import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.DocumentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Parser;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

@SuppressWarnings({"deprecation"})
public class JSSAXParser implements Parser, XMLReader {

	private EntityResolver resolver;
	private DTDHandler dtdHandler;
	private DocumentHandler docHandler;
	private ContentHandler contentHandler;
	private ErrorHandler errorHandler;
	private boolean havePre;

	public XMLReader getXMLReader() {
		return this;
	}
	
	@Override
	public void setLocale(Locale locale) throws SAXException {
		// N/A
	}

	@Override
	public void setEntityResolver(EntityResolver resolver) {
		this.resolver = resolver;
	}

	@Override
	public void setDTDHandler(DTDHandler handler) {
		this.dtdHandler = handler;
	}

	@Override
	public void setDocumentHandler(DocumentHandler handler) {
		this.docHandler = handler;
	}

	@Override
	public void setErrorHandler(ErrorHandler handler) {
		this.errorHandler = handler;
	}

	@Override
	public void parse(String fileName) throws SAXException, IOException {
		parseXMLString(JSUtil.getFileAsString(fileName));
	}

	@Override
	public void parse(InputSource source) throws SAXException, IOException {
		parse(source, false);
	}

	public void parseXMLString(String data) throws SAXException, IOException {	
		try {
			parseDocument(parseXML(data), false);
		} catch (Exception e) {
			error(e);
		}
	}

	public void parse(InputSource source, DefaultHandler handler) throws SAXException, IOException {
		setContentHandler(handler);
		parse(source, false);
	}

	public void parse(InputSource source, boolean topOnly) throws  SAXException, IOException  {
		Reader rdr = source.getCharacterStream();
		String[] data = new String[1];
		if (rdr == null) {
			InputStream bs = source.getByteStream();
			if (!(bs instanceof BufferedInputStream))
				bs = new BufferedInputStream(bs);
		  data[0] = Rdr.fixUTF((byte[]) Rdr.getStreamAsBytes((BufferedInputStream) bs, null));
		} else {
			if (!(rdr instanceof BufferedReader))
				rdr = new BufferedReader(rdr);
			Rdr.readAllAsString((BufferedReader) rdr, -1, false, data, 0);
		}
		try {
			parseDocument(parseXML(data[0]), topOnly);
		} catch (Exception e) {
			error(e);
		}
	}

	public DOMNode parseXML(String data) {
		return JSUtil.jQuery.parseXML(removeProcessing(data));
	}

	/**
	 * Removal of <?....?> commands, which are not valid in HTML5.
	 * These will be converted later into processing instructions
	 * 
	 * 
	 * @param data
	 * @return reconfigured data
	 */
	private String removeProcessing(String data) {
		if (false && data.indexOf("<?") >= 0) { // doesn't seem to be necessary?
			getUniqueSequence(data);
			data = PT.rep(PT.rep(data,  "<?", "<![CDATA[" + uniqueSeq), "?>", "]]>");
			if (data.startsWith("<!")) {
			data = "<pre>" + data + "</pre>";
			havePre = true;
			}
		}
	  return data;
	}

	private String uniqueSeq;

	private void getUniqueSequence(String data) {
		String s = "~";
		while (data.indexOf("<![CDATA["+s) >= 0)
			s += "~";
		uniqueSeq = s;
	}

	private void error(Exception e) throws SAXException {
		SAXParseException ee = new SAXParseException("Invalid Document", null);
		if (errorHandler == null)
			throw(ee);
		else
			errorHandler.fatalError(ee);
	}

  private boolean ver2;

	private static final int ELEMENT_TYPE = 1;

	private Map<String, String> htPrefixMap = new Hashtable<>();
	
	void registerPrefixes(DOMNode node) {
		String[] names = new String[0];
		/**
		 * @j2sNative names = node.getAttributeNames();
		 */
		for (int i = names.length; --i >= 0;) {
			String name = names[i];
			boolean isDefault;
			if (!(isDefault = name.equals("xmlns")) &&
					!name.startsWith("xmlns:"))
				continue;
			String prefix = (isDefault ? "##default:" : name.substring(6) + ":");
			String val = getAttribute(node, name);
			htPrefixMap.put(prefix, val);
			htPrefixMap.put(val + ":", val);
			htPrefixMap.put(val, prefix);
		}
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	public String getNamespaceForAttributeName(String name) {
		int pt = name.indexOf(":");
		if (pt < 0)
			return "";
		String uri = htPrefixMap.get(name.substring(0, pt + 1));
		if (uri != null)
			return uri;
		System.out.println("!! JSSAXParser could not find " + name);
		return "";
	}
	
	static String getAttribute(DOMNode node, String name) {
		return (/** @j2sNative node.getAttribute(name) || */ null);
	}

/**
   * Using JQuery to reading data from an XHTML document
   * 
   * @param doc
   * @throws SAXException
   */
	private void parseDocument(DOMNode doc, boolean topOnly) throws SAXException {
		if (docHandler == null && contentHandler == null)
			contentHandler = new JSSAXContentHandler();
		ver2 = (contentHandler != null);
		setNode(doc);
		if (ver2)
			contentHandler.startDocument();
		else
			docHandler.startDocument();
		
		// We must continue down until we have the root node.
		
		DOMNode element = (DOMNode) DOMNode.getAttr(doc, "firstChild");
	
		// skipping type 8 (processing directive) and type 10 (doctype) and anything
		// that is not 1 (element)
		
		/**
		 * @j2sNative
		 * 
		 * var type;
		 * while (element && (type = element.nodeType) != 1) {
		 *   element = element.nextSibling;
		 *   }
		 * 
		 */
		
		walkDOMTreePrivate(null, element, havePre, topOnly);
		if (ver2)
			contentHandler.endDocument();
		else
			docHandler.endDocument();
	}

	private char[] tempChars = new char[1024];

	/**
	 * SwingJS: Allow for a top-level only parsing
	 * 
	 * @param node
	 * @param topOnly
	 * @throws SAXException
	 */
	public void walkDOMTree(DOMNode node, boolean topOnly) throws SAXException {
		walkDOMTreePrivate(null, node, false, topOnly);
	}

	private void walkDOMTreePrivate(DOMNode root, DOMNode node, boolean skipTag, boolean topOnly) throws SAXException {
		String localName = ((String) DOMNode.getAttr(node, "localName"));
		String nodeName = (String) DOMNode.getAttr(node, "nodeName");
		String uri = (String) DOMNode.getAttr(node, "namespaceURI");
		if (localName == null) {
			if (topOnly)
				return;
			getTextData(node, true);
		} else if (!skipTag) {
			registerPrefixes(node);
			//System.out.println("JSSAXParser: uri::" + uri + " localName::" + localName + " qName::" + nodeName);
			JSSAXAttributes atts = new JSSAXAttributes(node);
			setNode(node);
			if (ver2)
				contentHandler.startElement(uri, localName, nodeName, atts);
			else
				docHandler.startElement(localName, atts);
		}
		if (root == null)
			root = node;
		boolean isRoot = (node == root);
		node = (DOMNode) DOMNode.getAttr(node, "firstChild");
		while (node != null) {
			if (isRoot || !topOnly)
				walkDOMTreePrivate(root, node, false, topOnly);
			node = (DOMNode) DOMNode.getAttr(node, "nextSibling");
		}
		if (localName == null || skipTag)
			return;
		if (ver2)
			contentHandler.endElement(uri, localName, nodeName);
		else
			docHandler.endElement(localName);
	}

	public static DOMNode[] getChildren(DOMNode node) {
		return (DOMNode[]) DOMNode.getAttr(node, "children");
	}
	
	public static String getSimpleInnerText(DOMNode node) {
		DOMNode[] children = getChildren(node);
		return (children == null || children.length > 0 ? "" 
				: (String) DOMNode.getAttr(node, "textContent"));
	}
	
	private String getTextData(DOMNode node, boolean doProcess) throws SAXException {
		String nodeName = (String) DOMNode.getAttr(node, "nodeName");
		boolean isText = "#text".equals(nodeName);
		if (isText || "#cdata-section".equals(nodeName)) {
			String data = (String) DOMNode.getAttr(node, "textContent");
			if (!doProcess)
				return data;
			if (isText || uniqueSeq == null || !data.startsWith(uniqueSeq)) {
				int len = data.length();
				char[] ch = tempChars;
				if (len > ch.length)
					ch = tempChars = (char[]) AU.ensureLength(ch, len * 2);
				for (int i = len; --i >= 0;)
					ch[i] = data.charAt(i);
				setNode(node);
				if (ver2)
					contentHandler.characters(ch, 0, len);
				else
					docHandler.characters(ch, 0, len);
				return null;
			}
			data = data.substring(uniqueSeq.length());
			String target = data + " ";
			target = target.substring(0, target.indexOf(" "));
			data = data.substring(target.length()).trim();
			if (ver2)
				contentHandler.processingInstruction(target, data);
			else
				docHandler.processingInstruction(target, data);
		}
		return null;
	}

	DOMNode currentNode;
	private void setNode(DOMNode node) {
		this.currentNode = node;
	}
	
	public DOMNode getNode() {
		return currentNode;
	}

	@Override
	public boolean getFeature(String name) throws SAXNotRecognizedException,
			SAXNotSupportedException {
		return (getProperty("\1" + name) != null);
	}

	@Override
	public void setFeature(String name, boolean value)
			throws SAXNotRecognizedException, SAXNotSupportedException {
		setProperty("\1" + name, value ? Boolean.TRUE : null);		
	}

	@Override
	public Object getProperty(String name) throws SAXNotRecognizedException,
			SAXNotSupportedException {
		return (props == null  ? null : props.get(name));
	}

	private Map<String, Object> props;

	@Override
	public void setProperty(String name, Object value)
			throws SAXNotRecognizedException, SAXNotSupportedException {
		if (value == null) {
			if (props != null)
				props.remove(name);
			return;
		}
		if (props == null)
			props = new Hashtable<String, Object>();
		props.put(name, value);
	}

	@Override
	public EntityResolver getEntityResolver() {
		return resolver;
	}

	@Override
	public DTDHandler getDTDHandler() {
		return dtdHandler;
	}

	@Override
	public void setContentHandler(ContentHandler handler) {
		contentHandler = handler;
	}

	@Override
	public ContentHandler getContentHandler() {
		return contentHandler;
	}

	@Override
	public ErrorHandler getErrorHandler() {
		return errorHandler;
	}


}