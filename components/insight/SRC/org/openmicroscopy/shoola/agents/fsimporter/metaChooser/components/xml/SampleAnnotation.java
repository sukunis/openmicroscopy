package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.xml;

import java.io.IOException;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import jdk.internal.org.xml.sax.SAXException;
import loci.common.xml.XMLTools;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.UOSMetadataLogger;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.format.ObservedSample;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.format.Sample;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.format.Sample.GridBox;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ome.xml.meta.OMEXMLMetadata;
import ome.xml.model.StructuredAnnotations;
import ome.xml.model.XMLAnnotation;

/**Create a xmlAnnotation element for ome.xml datamodel
 * 
 * @author kunis
 *
 */
public class SampleAnnotation extends XMLAnnotation 
{
	// Base: --Name: SamplePreparation -- Type: OMEXMLSamplePreparation -- modelBaseType: XMLAnnotation -- langBaseType: Object
	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/OME/2015-01";

	/** Logger for this class. */
    protected static Logger LOGGER = Logger.getLogger(UOSMetadataLogger.class.getName());
    
	// StructuredAnnotations_BackReference back reference
	private StructuredAnnotations structuredAnnotations;
	
	public static final String SAMPLE_NS;
	static{
		SAMPLE_NS="uos.de/omero/metadata/sample/2015-04";
	}
	
	private Sample sample;
	
	public static final String SAMPLE_ANNOT_ID="Annotation:Sample";
	
	

	public void setSample( Sample s)
	{
		sample=s;
		setNamespace(NAMESPACE);
		Document doc = XMLTools.createDocument();
		Element r=makeSampleElement(doc);
		setValue(XMLTools.dumpXML(null, doc, r, false));
	}
	
	protected Element makeSampleElement(Document doc)
	{
		Element xmlAnnotElem=doc.createElementNS(SAMPLE_NS, Sample.SAMPLE);
		
		createPrepElem(doc, xmlAnnotElem);
		createRawMaterialElem(doc, xmlAnnotElem);
		createGridBoxElem(doc, xmlAnnotElem);
		
		
		for(int i=0; i< sample.getObservedSampleCount(); i++)
		{
			createObservedSampleElem(doc, xmlAnnotElem,i);
		}
		
		
		return xmlAnnotElem;
		
	}

	private void createObservedSampleElem(Document doc, Element xmlAnnotElem,int i) 
	{

		Element obSample=doc.createElement(ObservedSample.OBS_SAMPLE);

		ObservedSample s=sample.getObservedSample(i);
		if(s!=null){
			//TODO: wo die id setzen?
			obSample.setAttribute(s.OBS_ID, String.valueOf(i));

			Element grid=doc.createElement(s.GRID);
			grid.setAttribute(s.GRID_REF, s.getGridboxID());
			grid.setAttribute(s.GRID_NUMBERX, s.getGridNumberX());
			grid.setAttribute(s.GRID_NUMBERY, s.getGridNumberY());
			obSample.appendChild(grid);

			Element obj=doc.createElement(s.OBJECT);
			obj.setAttribute(s.OBJECT_TYPE, s.getObjectType());
			obj.setAttribute(s.OBJECT_NUMBER, s.getObjectNumber());

			obSample.appendChild(obj);
		}else{
			LOGGER.warning("[SAVE] given OBSERVED SAMPLE is null");
		}
		xmlAnnotElem.appendChild(obSample);
	}

	private void createGridBoxElem(Document doc, Element xmlAnnotElem) {
			Element gridBox=doc.createElement(GridBox.GRID);
			GridBox box=sample.getGridBox();
			if(box==null)
				box=new GridBox("", 0, "");
			gridBox.setAttribute(GridBox.GRID_ID, box.getId());
			gridBox.setAttribute(GridBox.GRID_NR, String.valueOf(box.getNr()));
			gridBox.setAttribute(GridBox.GRID_TYPE, box.getType());
			xmlAnnotElem.appendChild(gridBox);
	}

	private void createPrepElem(Document doc, Element xmlAnnotElem) {
		Element prep=doc.createElement(Sample.PREP);
		String prepDate=sample.getDateAsString();
		String prepDesc=sample.getPrepDescription();
		
		prep.setAttribute(Sample.PREP_DATE, prepDate);
		prep.setAttribute(Sample.PREP_DESCRIPTION,prepDesc);
		
		xmlAnnotElem.appendChild(prep);
	}
	
	private void createRawMaterialElem(Document doc, Element xmlAnnotElem) {
		Element raw=doc.createElement(Sample.RAW);
		String rawCode=sample.getRawMaterialCode();
		String rawDesc=sample.getRawMaterialDesc();
		
		raw.setAttribute(Sample.RAW_CODE, rawCode);
		raw.setAttribute(Sample.RAW_DESC,rawDesc);
		
		xmlAnnotElem.appendChild(raw);
	}
	
	public Sample getSample(String xml)
	{
		Sample s=new Sample();
		
		Document annot;
		try {
			annot = XMLTools.parseDOM(xml);
			NodeList nodes= annot.getElementsByTagName(Sample.SAMPLE);
			if(nodes.getLength() >0){
				Element sample =(Element) nodes.item(0);
				getPreparationDataFromXML(s, sample);
				getRawMaterialDataFromXML(s,sample);
				getGridBoxDataFromXML(s,sample);
				getObservedSampleFromXML(s,sample);
			}
		} catch (ParserConfigurationException | org.xml.sax.SAXException
				| IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return s;
	}

	private void getObservedSampleFromXML(Sample s,Element sample) {
		NodeList observedSample=sample.getElementsByTagName(ObservedSample.OBS_SAMPLE);
		//TODO read only one observedSample, extends for more
		if(observedSample!=null && observedSample.getLength()>0)
		{
			ObservedSample os=new ObservedSample();
			
			Element osElem=(Element)observedSample.item(0);
			String id=osElem.getAttribute(ObservedSample.OBS_ID);
			NodeList grid=osElem.getElementsByTagName(ObservedSample.GRID);
			if(grid!=null && grid.getLength()>0){
				Element box=(Element)grid.item(0);
				NamedNodeMap attrs=box.getAttributes();
				Node gNumberX=attrs.getNamedItem(ObservedSample.GRID_NUMBERX);
				if(gNumberX!=null)
					os.setGridNumberX(gNumberX.getNodeValue());
				Node gNumberY=attrs.getNamedItem(ObservedSample.GRID_NUMBERY);
				if(gNumberY!=null)
					os.setGridNumberY(gNumberY.getNodeValue());
				
			}
			
			NodeList onj = osElem.getElementsByTagName(ObservedSample.OBJECT);
			if(onj!=null && onj.getLength()>0){
				Element box=(Element)onj.item(0);
				NamedNodeMap attrs=box.getAttributes();
				Node oNumber = attrs.getNamedItem(ObservedSample.OBJECT_NUMBER);
				Node oType= attrs.getNamedItem(ObservedSample.OBJECT_TYPE);
				
				if(oNumber!=null){
					os.setObjectNumber(oNumber.getNodeValue());
				}
				if(oType!=null){
					os.setObjectType(oType.getNodeValue());
				}
				
			}
			s.setObservedSample(os);
		}
	}

	private void getGridBoxDataFromXML(Sample s,Element sample) {
		NodeList gridBox=sample.getElementsByTagName(GridBox.GRID);
		//TODO read only one gridbox, extends for more
		if(gridBox!=null && gridBox.getLength()>0)
		{
			Element box=(Element)gridBox.item(0);
			NamedNodeMap attrs=box.getAttributes();
			Node id = attrs.getNamedItem(Sample.GridBox.GRID_ID);
			Node nr =attrs.getNamedItem(Sample.GridBox.GRID_NR);
			Node type=attrs.getNamedItem(Sample.GridBox.GRID_TYPE);
			if(nr!=null)
				s.setGridBoxNr(nr.getNodeValue()); 
			if(type!=null)
				s.setGridBoxType(type.getNodeValue());
		}
	}

	private void getPreparationDataFromXML(Sample s, Element sample) {
		NodeList prep=sample.getElementsByTagName(Sample.PREP);
		
		if(prep!=null && prep.getLength()>0)
		{
			Element prepElem=(Element) prep.item(0);
			NamedNodeMap attrs=prepElem.getAttributes();
			Node date=attrs.getNamedItem(Sample.PREP_DATE);
			Node desc=attrs.getNamedItem(Sample.PREP_DESCRIPTION);
			
			if(date!=null){
				s.setPrepDate(date.getNodeValue());
			}
			if(desc!=null){
				s.setPrepDescription(desc.getNodeValue());
			}
		}
	}
	
	private void getRawMaterialDataFromXML(Sample s, Element sample) {
		NodeList raw=sample.getElementsByTagName(Sample.RAW);
		
		if(raw!=null && raw.getLength()>0)
		{
			Element rawElem=(Element) raw.item(0);
			NamedNodeMap attrs=rawElem.getAttributes();
			Node code=attrs.getNamedItem(Sample.RAW_CODE);
			Node desc=attrs.getNamedItem(Sample.RAW_DESC);
			
			if(code!=null){
				s.setRawMaterialCode(code.getNodeValue());
			}
			if(desc!=null){
				s.setRawMaterialDesc(desc.getNodeValue());
			}
		}
	}
	
}
