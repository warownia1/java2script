package swingjs.xml;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.namespace.QName;

import org.xml.sax.Attributes;

import javajs.util.PT;
import swingjs.JSUtil;
import swingjs.api.js.DOMNode;

class JSJAXBField {

	private Object javaObject;
	Object entryValue;
	DOMNode boundNode;
	List<Object> boundListNodes;
	String xmlCharacterData; 
	String xmlAttributeData;
	Attributes xmlAttributes;
	String xmlType;
	
	public void clear(Object javaObject) {
		this.javaObject = javaObject;
		this.entryValue = null;
		this.boundNode = null;
		this.boundListNodes = null;
		this.xmlCharacterData = null;
		this.xmlAttributeData = null;
		this.xmlAttributes = null;
		this.xmlType = null;
	}

	public void setCharacters(String ch) {
		xmlCharacterData = ch;
	}

	public void setAttributeData(String val) {
		xmlAttributeData = val;
	}

	public void setAttributes(Attributes attr) {
		xmlAttributes = attr;
		xmlType = attr.getValue("xsi:type");
	}
	
	public void setNode(DOMNode node) {
		if (isArray) {
			if (boundListNodes == null) {
				boundListNodes = new ArrayList<Object>();
			}
			boundListNodes.add(node);
		}
		boundNode = node;
	}
	
	int index;

	String text;
	String javaName;
	String methodName;	
	String javaClassName;

	boolean isAttribute;
	boolean isID;
	boolean isIDREF;
	boolean isValue;
	boolean asList;
	boolean isNillable;
	boolean isArray;
	boolean isByteArray;

	QName qualifiedName = new QName("","","");
	QName qualifiedWrapName;
	Map<String, String> attr;

	String xmlSchema;
	String typeAdapter;
	String mimeType;

	private static final Map<String, String> namespacePrefixes = new Hashtable<String, String>();
	private static int prefixIndex = 1;

	private static String getPrefixFor(String namespace) {
		String prefix = namespacePrefixes.get(namespace);
		if (prefix == null)
			namespacePrefixes.put(namespace, prefix = "ns" + (++prefixIndex));
		return prefix;
	}

	JSJAXBField(JSJAXBClass t, Object[][] adata, Object javaObject, int index, List<String> propOrder) {
		this.javaObject = javaObject;
		this.index = index;
		javaName = (String) adata[0][0];
		javaClassName = (String) adata[0][1];
		isByteArray = javaClassName.equals("byte[]");
		isArray = !isByteArray && javaClassName.indexOf("[]") >= 0;			
		String[] javaAnnotations = (String[]) adata[1];
		attr = new Hashtable<String, String>();
		text = "";
		for (int i = 0; i < javaAnnotations.length; i++) {
			String data = javaAnnotations[i];
			text += data + ";";
			int pt = data.indexOf("(");
			String tag = data.substring(0, (pt < 0 ? data.length() : pt));
			data = (pt < 0 ? "" : data.substring(pt + 1, data.lastIndexOf(")")));
			if (pt >= 0 && data.indexOf("=") >= 0)
				addXMLAttributes(tag, data, attr);
			if (javaName != null && javaName.startsWith("M:")) {
				methodName = javaName.substring(2);
				// annotation can be on set... or get... or is...
				if (methodName.startsWith("set")) {
					methodName = "get" + methodName.substring(3);
				}
				javaName = getJavaNameFromMethodName(methodName);
			}
			processTagName(t, tag, data, propOrder);

		}
		if (javaName != null) {   
			setDefaults();
			if (qualifiedName.getLocalPart().length() == 0)
			qualifiedName = new QName(qualifiedName.getNamespaceURI(), 
					JSJAXBClass.getXmlNameFromClassName(javaName), 
					qualifiedName.getPrefix());
		}
	}

	private void setDefaults() {
		if (xmlSchema == null && typeAdapter == null) {
			if (javaClassName.equals("byte[]"))
				xmlSchema = "base64Binary";
			if (javaClassName.equals("javax.xml.datatype.XMLGregorianCalendar"))
				xmlSchema = "dateTime";
		}
	}


	/**
	 * Process the tag name for critical information, possibly updating the main
	 * reference tag.
	 * 
	 * @param tag
	 * @param qname
	 * @param propOrder
	 * @param a
	 */
	private void processTagName(JSJAXBClass t, String tag, String data, List<String> propOrder) {
		// note that we are not checking for JAXBExceptions here
		switch (tag) {
		case "@XmlRootElement":
			t.qname = getName(tag);
			break;
		case "@XmlType":
			t.qualifiedTypeName = getName(tag);
			String order = attr.get("@XmlType:propOrder");
			if (order != null) {
				int[] pt = new int[1];
				while (pt[0] >= 0) {
					String prop = PT.getQuotedStringNext(order, pt).trim();
					if (prop.length() == 0)
						break;
					propOrder.add(prop);
				}
			}
			break;
		case "@XmlAccessorType":
			if (data.indexOf("FIELD") >= 0)
				t.accessorType = JSJAXBClass.TYPE_FIELD;
			else if (data.indexOf("MEMBER") >= 0)
				t.accessorType = JSJAXBClass.TYPE_PUBLIC_MEMBER;
			else if (data.indexOf("PROPERTY") >= 0)
				t.accessorType = JSJAXBClass.TYPE_PROPERTY;
			break;
		case "@XmlSeeAlso":
			// @XmlSeeAlso({Dog.class,Cat.class})
			// class Animal {}
			// class Dog extends Animal {}
			// class Cat extends Animal {}
			break;
		case "@XmlAttribute":
			isAttribute = true;
			qualifiedName = getName(tag);
			break;
		case "@XmlElement":
			qualifiedName = getName(tag);
			isNillable = "true".equals(attr.get("@XmlElement:nillable"));
			break;
		case "@XmlValue":
			t.valueField = this;
			isValue = true;
			break;
		case "@XmlSchemaType":
			xmlSchema = attr.get("@XmlSchemaType:name");
			if (xmlSchema.equals("hexBinary")) {
				xmlSchema = null;
				typeAdapter = "HexBinaryAdapter";
			}
// e.g. 
//			   @XmlSchemaType(name="date")
//			   public XMLGregorianCalendar cal;
//			
//			   @XmlSchemaType(name="hexBinary") <-- does not work with JAXB
			// https://jinahya.wordpress.com/2012/11/08/printing-binary-as-hexbinary-not-base64binary-with-jaxb/
//			   public byte[] hexBytes;
//			 
//			   @XmlSchemaType(name="base64Binary")
//			   public byte[] base64Bytes;
			break;
		case "@XmlJavaTypeAdapter":  
			// typically CollapsedStringAdapter.class
			typeAdapter = attr.get("@XmlJavaTypeAdapter:name");
			if (typeAdapter == null)
				typeAdapter = data;
			typeAdapter = typeAdapter.substring(0, data.length() - 6);
			break;
		case "@XmlID":
			isID = true;
			break;
		case "@XmlIDREF":
			isIDREF = true;
			break;
		case "@XmlList":
			asList = true;
			break;
		case "@XmlMimeType":
			mimeType = attr.get("@XmlMimeType:name");
			// e.g. @XmlMimeType("application/octet-stream")
			// @XmlMimeType("image/jpeg")
			// @XmlMimeType("text/xml; charset=iso-8859-1")
			break;
		case "@XmlElementWrapper":
			qualifiedWrapName = getName(tag);
			break;
		}
	}

	private QName getName(String tag) {
		String name, namespace;
		name = attr.get(tag + ":name");
		namespace = attr.get(tag = ":namespace");
		return new QName(namespace, name == null ? "" : name, namespace == null ? "" : getPrefixFor(namespace));
	}

	private String getJavaNameFromMethodName(String methodName) {
		String javaName = methodName.substring(methodName.startsWith("is") ? 2 : 3);
		String lcaseName = javaName.substring(0, 1).toLowerCase() + javaName.substring(1);
		String name = null;
		if (getJSType(name = javaName).equals("undefined") && getJSType(name = lcaseName).equals("undefined")
				&& methodName.endsWith("Property")) {
			if (getJSType(name = javaName.substring(javaName.length() - 8)).equals("undefined")
					&& getJSType(name = lcaseName.substring(javaName.length() - 8)).equals("undefined")) {
				System.out.println("JSJAXBMarshaller cannot find field for " + methodName);
				name = methodName;
			}
		}
		return name;
	}

	private String getJSType(String javaName) {
		return (/** @j2sNative ( typeof this.javaObject[javaName]) || */
		null);
	}

	private void addXMLAttributes(String tag, String data, Map<String, String> attr) {
		data = "<__ " + removeCommas(data) + " />";
		// System.out.println(data);
		DOMNode doc = JSUtil.jQuery.parseXML(data);
		DOMNode node = DOMNode.firstChild(doc);
		String[] names = node.getAttributeNames();
		for (int i = 0; i < names.length; i++)
			attr.put(tag + ":" + names[i], node.getAttribute(names[i]));
	}

	private String removeCommas(String s) {
		boolean escaped = false;
		for (int i = 0; i < s.length(); i++) {
			switch (s.charAt(i)) {
			case '"':
				escaped = !escaped;
				break;
			case '}':
			case '{':
				if (!escaped)
					s = s.substring(0, i) + "'" + s.substring(i + 1);
				break;
			case ',':
				if (!escaped)
					s = s.substring(0, i) + " " + s.substring(i + 1);
				break;
			case '\\':
				i++;
				break;
			}
		}
		s = s.replace("=true", "=\"true\"").replace("=false", "=\"false\"");
		return s;
	}

	@SuppressWarnings("unused")
	public Object getValue() {
		if (entryValue != null)
			return entryValue;
		String javaName = this.javaName;
		Object obj = this.javaObject;
		boolean isMethod = (this.methodName != null);
		/**
		 * @j2sNative
		 *
		 * 			obj = (isMethod ? obj[this.methodName]() : obj[javaName])
		 * 
		 */
		return obj;
	}

	public String toString() {
		return "{" + javaName + "}" + index;
	}

	
	static boolean hasJSData(Object value) {
		if (value == null)
			return false;
		Class<?> cl = value.getClass();
		return (/** @j2sNative (cl.$clazz$ ? !!cl.$clazz$.__ANN__ : 0) || */false);
	}

	// unmarshalling
	
	boolean isNil() {
	  return (xmlAttributes != null && xmlAttributes.getIndex("xsi:nil") >= 0);	
	}
	
	boolean mustUnmarshal() {
		if (isAttribute || asList 
				|| isByteArray
				|| isArray 
				|| qualifiedWrapName != null
				|| isNil() 
				|| simplePackages()
				|| JSJAXBClass.isSimple(javaClassName))
			return false;
		return true;
	}
	
	private boolean simplePackages() {
		return (javaClassName.indexOf(".") < 0 || javaClassName.startsWith("java.")
				|| javaClassName.startsWith("javax.") || javaClassName.startsWith("javajs."));
	}

	public Class<?> getJavaClassForUnmarshaling() throws ClassNotFoundException {
		return Class.forName(javaClassName);
	}
	/**
	 * NOTE: This will not work for private set methods
	 * 
	 * @param value
	 */
	@SuppressWarnings("unused")
	public void setValue(Object value) {
		String m = this.methodName;
		String j = this.javaName;
		Object o = this.javaObject;
			/**
			 * @j2sNative
			 * 
			 * if (m)
			 *   o[m].apply(o, [value]);
			 *     else 
			 *   o[j] = value;
			 */
	}

	public XmlAdapter getAdapter() {
		if (typeAdapter == null)
			return null;
		String adapterClass = "javax.xml.bind.annotation.adapters." + typeAdapter;
		return JSJAXBClass.getAdapter(adapterClass);
	}

	public Object getXMLValue() {
		if (xmlAttributeData != null)
			return xmlAttributeData;
		
		// TODO Auto-generated method stub
		return null;
	}

	public Object getObject() {
		Object o = javaObject;
		String x = javaName;
		/**
		 * return o[x];
		 */
		return null;
	}

	
	/*
	 * notes
	 * 
	 * @XmlAttribute
	 * 
	 * https://docs.oracle.com/javase/8/docs/api/javax/xml/bind/annotation/
	 * XmlAttribute.html
	 * 
	 * This annotation can be used with the following annotations: XmlID, XmlIDREF,
	 * XmlList, XmlSchemaType, XmlValue, <--- NOT for XmlAttribute XmlAttachmentRef,
	 * <--- not relevant? XmlMimeType, <--- BASE64 XmlInlineBinaryData <--- not
	 * relevant? XmlJavaTypeAdapter
	 * 
	 * @XmlElement
	 * 
	 * This annotation can be used with following annotations: XmlID, XmlIDREF,
	 * XmlList, XmlSchemaType, XmlValue, XmlAttachmentRef, <--- not relevant
	 * XmlMimeType, <--- BASE64 XmlInlineBinaryData, <--- not relevant
	 * XmlElementWrapper, XmlJavaTypeAdapter.
	 * 
	 */
	
	
//  https://docs.oracle.com/cd/E13222_01/wls/docs103/webserv/data_types.html
	
//	XML Schema Data Type
	
//	anySimpleType 		java.lang.Object (for xsd:element of this type)
//	anySimpleType 		java.lang.String (for xsd:attribute of this type)
//	base64Binary		byte[]
//	boolean				boolean
//	byte				byte
//	date				javax.xml.datatype.XMLGregorianCalendar
//	dateTime			javax.xml.datatype.XMLGregorianCalendar
//	decimal				java.math.BigDecimal
//	double				double
//	duration			javax.xml.datatype.Duration
//	float				float
//	g					java.xml.datatype.XMLGregorianCalendar
//	hexBinary			byte[]
//	int					int
//	integer				java.math.BigInteger
//	long				long
//	NOTATION			javax.xml.namespace.QName
//	Qname				javax.xml.namespace.QName
//	short				short
//	string				java.lang.String
//	time				java.xml.datatype.XMLGregorianCalendar
//	unsignedByte		short
//	unsignedInt			long	
//	unsignedShort		int 
}



