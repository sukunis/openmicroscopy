package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration;

import java.io.File;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagConfiguration;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class UOSHardwareWriter {

	private static final org.slf4j.Logger LOGGER =
    	    LoggerFactory.getLogger(UOSHardwareWriter.class);
	
	public static final String NS_2016_06_07="uos.de/omero/metadata/cellnanos/2015-06-07";

	private List<List<TagConfiguration>> objectiveList;
	private List<List<TagConfiguration>> detectorList;
	private List<List<TagConfiguration>> lightSrcList;
	private List<List<TagConfiguration>> lightPathFilterList;
	private String micName;
	
	
	public void save(File file)
	{
		if(file==null || 
				(objectiveList==null && detectorList==null && 
				lightSrcList==null && lightPathFilterList==null) ){
			System.out.println("No file or no data to save");
			return;
		}
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder db=dbf.newDocumentBuilder();
			Document doc = db.newDocument();
			
			writeData(doc);
			
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(file);

			// Output to console for testing
			// StreamResult result = new StreamResult(System.out);

			transformer.transform(source, result);

			System.out.println("File saved!");
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void writeData(Document doc) {
//TODO add xml version
		//root element
		Element root = doc.createElement(UOSHardwareReader.HARDWARE);
		doc.appendChild(root);
		
		//mic
		Element mic = doc.createElement(UOSProfileReader.MICROSCOPE);
		root.appendChild(mic);
		mic.setAttribute(UOSProfileReader.MIC_NAME,micName!=null ? micName : "Unspecific");
		
		//Objectives
		Element objs=doc.createElement(UOSHardwareReader.SET_OBJECTIVE);
		root.appendChild(objs);
		addComponents(doc, objs,objectiveList,UOSHardwareReader.OBJECTIVE);
		
		//Detectors
		Element detectors=doc.createElement(UOSHardwareReader.SET_DETECTOR);
		root.appendChild(detectors);
		addComponents(doc, detectors,detectorList,UOSHardwareReader.DETECTOR);
		
		//LightSources
		
		
		//LightPath
		
		
	}

	/**
	 * @param doc
	 * @param objs
	 */
	public void addComponents(Document doc, Element objs,List<List<TagConfiguration>> compList,String name) {
		if(compList==null){
			return;
		}
		for(List<TagConfiguration> list : compList){
			Element obj=doc.createElement(name);
			for(TagConfiguration tag : list){
				obj.appendChild(tagToXML(doc,tag));
			}
			objs.appendChild(obj);
		}
	}

	private Node tagToXML(Document doc, TagConfiguration tag) 
	{
		Element modTag=doc.createElement("Tag");
		modTag.setAttribute(ModuleConfiguration.TAG_NAME, tag.getName());
		modTag.setAttribute(ModuleConfiguration.TAG_VALUE, tag.getValue());
		if(tag.getUnit()!=null){
			modTag.setAttribute(ModuleConfiguration.TAG_UNIT, tag.getUnitSymbol());
		}
		return modTag;
	}

	public void setMicName(String name) {
		micName=name;
	}

	public void setObjectives(List<List<TagConfiguration>> listObj) {
		objectiveList=listObj;		
	}

	public void setDetectors(List<List<TagConfiguration>> listDet) {
		detectorList=listDet;
	}

	public void setLightSource(List<List<TagConfiguration>> listLS) {
		lightSrcList=listLS;		
	}

	public void setLightPath(List<List<TagConfiguration>> listLP) {
		lightPathFilterList=listLP;		
	}

}
