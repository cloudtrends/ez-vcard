package ezvcard.io;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import ezvcard.VCard;
import ezvcard.VCardSubTypes;
import ezvcard.VCardVersion;
import ezvcard.types.RawType;
import ezvcard.types.TypeList;
import ezvcard.types.VCardType;
import ezvcard.types.XmlType;
import ezvcard.util.IOUtils;
import ezvcard.util.XCardUtils;

/*
 Copyright (c) 2012, Michael Angstadt
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met: 

 1. Redistributions of source code must retain the above copyright notice, this
 list of conditions and the following disclaimer. 
 2. Redistributions in binary form must reproduce the above copyright notice,
 this list of conditions and the following disclaimer in the documentation
 and/or other materials provided with the distribution. 

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 The views and conclusions contained in the software and documentation are those
 of the authors and should not be interpreted as representing official policies, 
 either expressed or implied, of the FreeBSD Project.
 */

/**
 * Unmarshals XML-encoded vCards into {@link VCard} objects.
 * @author Michael Angstadt
 * @see <a href="http://tools.ietf.org/html/rfc6351">RFC 6351</a>
 */
public class XCardReader implements IParser {
	private static final VCardVersion version = VCardVersion.V4_0;
	private static final VCardNamespaceContext nsContext = new VCardNamespaceContext(version, "v");

	private CompatibilityMode compatibilityMode = CompatibilityMode.RFC;
	private List<String> warnings = new ArrayList<String>();
	private Map<QName, Class<? extends VCardType>> extendedTypeClasses = new HashMap<QName, Class<? extends VCardType>>();

	/**
	 * The <code>&lt;vcard&gt;</code> elements within the XML document.
	 */
	private Iterator<Element> vcardElements;

	/**
	 * @param xml the XML string to read the vCards from
	 * @throws SAXException if there's a problem parsing the XML
	 */
	public XCardReader(String xml) throws SAXException {
		this(xml, CompatibilityMode.RFC);
	}

	/**
	 * @param xml the XML string to read the vCards from
	 * @param compatibilityMode the compatibility mode
	 * @throws SAXException if there's a problem parsing the XML
	 */
	public XCardReader(String xml, CompatibilityMode compatibilityMode) throws SAXException {
		this.compatibilityMode = compatibilityMode;
		try {
			init(new StringReader(xml));
		} catch (IOException e) {
			//reading from string
		}
	}

	/**
	 * @param in the input stream to read the vCards from
	 * @throws IOException if there's a problem reading from the input stream
	 * @throws SAXException if there's a problem parsing the XML
	 */
	public XCardReader(InputStream in) throws SAXException, IOException {
		this(in, CompatibilityMode.RFC);
	}

	/**
	 * @param in the input stream to read the vCards from
	 * @param compatibilityMode the compatibility mode
	 * @throws IOException if there's a problem reading from the input stream
	 * @throws SAXException if there's a problem parsing the XML
	 */
	public XCardReader(InputStream in, CompatibilityMode compatibilityMode) throws SAXException, IOException {
		this(new InputStreamReader(in), compatibilityMode);
	}

	/**
	 * @param file the file to read the vCards from
	 * @throws IOException if there's a problem reading from the file
	 * @throws SAXException if there's a problem parsing the XML
	 */
	public XCardReader(File file) throws SAXException, IOException {
		this(file, CompatibilityMode.RFC);
	}

	/**
	 * @param file the file to read the vCards from
	 * @param compatibilityMode the compatibility mode
	 * @throws IOException if there's a problem reading from the file
	 * @throws SAXException if there's a problem parsing the XML
	 */
	public XCardReader(File file, CompatibilityMode compatibilityMode) throws SAXException, IOException {
		this.compatibilityMode = compatibilityMode;
		FileReader reader = null;
		try {
			reader = new FileReader(file);
			init(reader);
		} finally {
			IOUtils.closeQuietly(reader);
		}
	}

	/**
	 * @param reader the reader to read the vCards from
	 * @throws IOException if there's a problem reading from the reader
	 * @throws SAXException if there's a problem parsing the XML
	 */
	public XCardReader(Reader reader) throws SAXException, IOException {
		this(reader, CompatibilityMode.RFC);
	}

	/**
	 * @param reader the reader to read the vCards from
	 * @param compatibilityMode the compatibility mode
	 * @throws IOException if there's a problem reading from the reader
	 * @throws SAXException if there's a problem parsing the XML
	 */
	public XCardReader(Reader reader, CompatibilityMode compatibilityMode) throws SAXException, IOException {
		this.compatibilityMode = compatibilityMode;
		init(reader);
	}

	/**
	 * @param document the XML document to read the vCards from
	 */
	public XCardReader(Document document) {
		this(document, CompatibilityMode.RFC);
	}

	/**
	 * @param document the XML document to read the vCards from
	 * @param compatibilityMode the compatibility mode
	 */
	public XCardReader(Document document, CompatibilityMode compatibilityMode) {
		this.compatibilityMode = compatibilityMode;
		init(document);
	}

	private void init(Reader reader) throws SAXException, IOException {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			dbf.setIgnoringComments(true);
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document document = db.parse(new InputSource(reader));
			init(document);
		} catch (ParserConfigurationException e) {
			//never thrown because we're not doing anything fancy with the configuration
		}
	}

	private void init(Document document) {
		try {
			XPath xpath = XPathFactory.newInstance().newXPath();
			xpath.setNamespaceContext(nsContext);

			String prefix = nsContext.prefix;
			NodeList nodeList = (NodeList) xpath.evaluate("//" + prefix + ":vcards/" + prefix + ":vcard", document, XPathConstants.NODESET);
			vcardElements = XCardUtils.toElementList(nodeList).iterator();
		} catch (XPathExpressionException e) {
			//never thrown, xpath expression is hard coded
		}
	}

	/**
	 * Gets the compatibility mode. Used for customizing the unmarshalling
	 * process based on the application that generated the vCard.
	 * @return the compatibility mode
	 */
	public CompatibilityMode getCompatibilityMode() {
		return compatibilityMode;
	}

	/**
	 * Sets the compatibility mode. Used for customizing the unmarshalling
	 * process based on the application that generated the vCard.
	 * @param compatibilityMode the compatibility mode
	 */
	public void setCompatibilityMode(CompatibilityMode compatibilityMode) {
		this.compatibilityMode = compatibilityMode;
	}

	//@Override
	public void registerExtendedType(Class<? extends VCardType> clazz) {
		extendedTypeClasses.put(getQNameFromTypeClass(clazz), clazz);
	}

	//@Override
	public void unregisterExtendedType(Class<? extends VCardType> clazz) {
		extendedTypeClasses.remove(getQNameFromTypeClass(clazz));
	}

	//@Override
	public List<String> getWarnings() {
		return new ArrayList<String>(warnings);
	}

	//@Override
	public VCard readNext() {
		warnings.clear();

		if (!vcardElements.hasNext()) {
			return null;
		}

		VCard vcard = new VCard();
		vcard.setVersion(version);

		Element vcardElement = vcardElements.next();

		String ns = version.getXmlNamespace();
		List<Element> children = XCardUtils.toElementList(vcardElement.getChildNodes());
		List<String> warningsBuf = new ArrayList<String>();
		for (Element child : children) {
			if ("group".equals(child.getLocalName()) && ns.equals(child.getNamespaceURI())) {
				String group = child.getAttribute("name");
				if (group.length() == 0) {
					group = null;
				}
				List<Element> propElements = XCardUtils.toElementList(child.getChildNodes());
				for (Element propElement : propElements) {
					parseAndAddElement(propElement, group, version, vcard, warningsBuf);
				}
			} else {
				parseAndAddElement(child, null, version, vcard, warningsBuf);
			}
		}

		return vcard;
	}

	/**
	 * Parses a property element from the XML document and adds the property to
	 * the vCard.
	 * @param element the element to parse
	 * @param group the group name or null if the property does not belong to a
	 * group
	 * @param version the vCard version
	 * @param vcard the vCard object
	 * @param warningsBuf the list to add the warnings to
	 */
	private void parseAndAddElement(Element element, String group, VCardVersion version, VCard vcard, List<String> warningsBuf) {
		warningsBuf.clear();

		VCardSubTypes subTypes = parseSubTypes(element);
		VCardType type = createTypeObject(element.getLocalName(), element.getNamespaceURI());
		type.setGroup(group);
		try {
			try {
				type.unmarshalValue(subTypes, element, version, warningsBuf, compatibilityMode);
			} catch (UnsupportedOperationException e) {
				//type class does not support xCard
				warningsBuf.add("Type class \"" + type.getClass().getName() + "\" does not support xCard unmarshalling.  It will be unmarshalled as a " + XmlType.NAME + " property.");
				type = new XmlType();
				type.setGroup(group);
				type.unmarshalValue(subTypes, element, version, warningsBuf, compatibilityMode);
			}
			addToVCard(type, vcard);
		} catch (SkipMeException e) {
			warningsBuf.add(type.getTypeName() + " property will not be unmarshalled: " + e.getMessage());
		} catch (EmbeddedVCardException e) {
			warningsBuf.add(type.getTypeName() + " property will not be unmarshalled: xCard does not supported embedded vCards.");
		} finally {
			warnings.addAll(warningsBuf);
		}
	}

	/**
	 * Parses the property parameters (aka "sub types").
	 * @param element the property's XML element
	 * @return the parsed parameters
	 */
	private VCardSubTypes parseSubTypes(Element element) {
		VCardSubTypes subTypes = new VCardSubTypes();

		List<Element> parametersElements = XCardUtils.toElementList(element.getElementsByTagName("parameters"));
		for (Element parametersElement : parametersElements) { // foreach "<parameters>" element (there should only be 1 though)
			List<Element> paramElements = XCardUtils.toElementList(parametersElement.getChildNodes());
			for (Element paramElement : paramElements) {
				String name = paramElement.getLocalName().toUpperCase();
				List<Element> valueElements = XCardUtils.toElementList(paramElement.getChildNodes());
				if (valueElements.isEmpty()) {
					String value = paramElement.getTextContent();
					subTypes.put(name, value);
				} else {
					for (Element valueElement : valueElements) {
						String value = valueElement.getTextContent();
						subTypes.put(name, value);
					}
				}
			}

			//remove the <parameters> element from the DOM
			element.removeChild(parametersElement);
		}

		return subTypes;
	}

	/**
	 * Creates the appropriate VCardType instance given the vCard property name.
	 * This method does not unmarshal the type, it just creates the type object.
	 * @param name the property name (e.g. "fn")
	 * @param ns the namespace of the element
	 * @return the type that was created
	 */
	private VCardType createTypeObject(String name, String ns) {
		name = name.toUpperCase();

		Class<? extends VCardType> clazz = TypeList.getTypeClass(name);
		if (clazz != null && VCardVersion.V4_0.getXmlNamespace().equals(ns)) {
			try {
				return clazz.newInstance();
			} catch (Exception e) {
				//it is the responsibility of the EZ-vCard developer to ensure that this exception is never thrown
				//all type classes defined in the EZ-vCard library MUST have public, no-arg constructors
				throw new RuntimeException(e);
			}
		} else {
			Class<? extends VCardType> extendedTypeClass = extendedTypeClasses.get(new QName(ns, name.toLowerCase()));
			if (extendedTypeClass != null) {
				try {
					return extendedTypeClass.newInstance();
				} catch (Exception e) {
					//this should never happen because the type class is checked to see if it has a public, no-arg constructor in the "registerExtendedType" method
					throw new RuntimeException("Extended type class \"" + extendedTypeClass.getName() + "\" MUST have a public, no-arg constructor.");
				}
			} else if (name.startsWith("X-")) {
				return new RawType(name);
			} else {
				//add as an XML property
				return new XmlType();
			}
		}
	}

	/**
	 * Adds a type to the vCard.
	 * @param t the type object
	 * @param vcard the vCard
	 */
	private void addToVCard(VCardType t, VCard vcard) {
		Method method = TypeList.getAddMethod(t.getClass());
		if (method != null) {
			try {
				method.invoke(vcard, t);
			} catch (Exception e) {
				//this should NEVER be thrown because the method MUST be public
				throw new RuntimeException(e);
			}
		} else {
			vcard.addExtendedType(t);
		}
	}

	/**
	 * Gets the QName from a type class.
	 * @param clazz the type class
	 * @return the QName
	 */
	private QName getQNameFromTypeClass(Class<? extends VCardType> clazz) {
		try {
			VCardType type = clazz.newInstance();
			QName qname = type.getQName();
			if (qname == null) {
				qname = new QName(version.getXmlNamespace(), type.getTypeName().toLowerCase());
			}
			return qname;
		} catch (Exception e) {
			//there is no public, no-arg constructor
			throw new RuntimeException(e);
		}
	}

	private static class VCardNamespaceContext implements NamespaceContext {
		private final String ns;
		private final String prefix;

		public VCardNamespaceContext(VCardVersion version, String prefix) {
			ns = version.getXmlNamespace();
			this.prefix = prefix;
		}

		//@Override
		public String getNamespaceURI(String prefix) {
			if (prefix.equals(prefix)) {
				return ns;
			}
			return null;
		}

		//@Override
		public String getPrefix(String ns) {
			if (ns.equals(this.ns)) {
				return prefix;
			}
			return null;
		}

		//@Override
		public Iterator<String> getPrefixes(String ns) {
			if (ns.equals(this.ns)) {
				return Arrays.asList(prefix).iterator();
			}
			return null;
		}
	}
}
