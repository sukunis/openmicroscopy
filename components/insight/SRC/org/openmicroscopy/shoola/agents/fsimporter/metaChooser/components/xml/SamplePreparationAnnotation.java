package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.xml;


import java.util.List;

import ome.xml.model.OMEModel;
import ome.xml.model.ReferenceList;
import ome.xml.model.StructuredAnnotations;
import ome.xml.model.XMLAnnotation;
import ome.xml.model.enums.EnumerationException;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.SampleCompUI;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
/**
 * Create a xmlAnnotation element for ome.xml datamodel
 * @author kunis
 *
 */
public class SamplePreparationAnnotation extends XMLAnnotation
{
	// Base: --Name: SamplePreparation -- Type: OMEXMLSamplePreparation -- modelBaseType: XMLAnnotation -- langBaseType: Object
	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/OME/2015-01";
	
	/** Logger for this class */
//	private static final Logger LOGGER=LoggerFactory.getLogger(OMEXMLSamplePreparation.class);
	
	private String id;
	
	private String date;
	
	private String description;
	
//	private List<SampleSupportAnnotation> sampleSupportLinks = new ReferenceList<SampleSupportAnnotation>();
	
	// -- Instance variables --

	// Value property
	private String value;

	// StructuredAnnotations_BackReference back reference
	private StructuredAnnotations structuredAnnotations;
	
	/** Default constructor. */
	public SamplePreparationAnnotation()
	{
		super();
	}
	
	/**
	 * Constructs XMLAnnotation recursively from an XML DOM tree.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @param model Handler for the OME model which keeps track of instances
	 * and references seen during object population.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public SamplePreparationAnnotation(Element element, OMEModel model)
	    throws EnumerationException
	{
		update(element, model);
	}
	
	/** Copy constructor. */
	public SamplePreparationAnnotation(SamplePreparationAnnotation orig)
	{
//		super(orig);
//		value = orig.value;
//		structuredAnnotations = orig.structuredAnnotations;
		
		id=orig.id;
		date=orig.date;
		description=orig.description;
//		sampleSupportLinks=orig.sampleSupportLinks;
		
		
	}
	
	protected Element asXMLElement(Document document, Element XMLAnnotation_element)
	{
		// Creating XML block for XMLAnnotation

		if (XMLAnnotation_element == null)
		{
			XMLAnnotation_element =
					document.createElementNS(NAMESPACE, "XMLAnnotation");
		}

		// Ensure any base annotations add their Elements first
		XMLAnnotation_element = super.asXMLElement(document, XMLAnnotation_element);

		if (value != null)
		{
			Document Value_document = null;
			try
			{
				javax.xml.parsers.DocumentBuilderFactory factory =
					javax.xml.parsers.DocumentBuilderFactory.newInstance();
				factory.setNamespaceAware(false);
				javax.xml.parsers.DocumentBuilder parser =
					factory.newDocumentBuilder();
				org.xml.sax.InputSource is = new org.xml.sax.InputSource();
				is.setCharacterStream(new java.io.StringReader("<Value>"+value+"</Value>"));
				Value_document = parser.parse(is);
			}
			catch (Exception e)
			{
				throw new RuntimeException(e);
			}

			NodeList value_subNodes = Value_document.getChildNodes();
			Node value_subNode = value_subNodes.item(0);
			value_subNode = document.importNode(value_subNode, true);

			XMLAnnotation_element.appendChild(value_subNode);

		}
		if (structuredAnnotations != null)
		{
			// *** IGNORING *** Skipped back reference StructuredAnnotations_BackReference
		}

		return XMLAnnotation_element;
	}
}
