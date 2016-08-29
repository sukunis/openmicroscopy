package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.microscope.CustomViewProperties;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagConfiguration;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class UOSProfileWriter 
{
	private CustomViewProperties view;
	
	public UOSProfileWriter()
	{
	}
	
	public void save(File file, CustomViewProperties prop)
	{
		if(file==null  || prop==null){
			System.out.println(file==null?"File null":"File not null");
					System.out.println(prop==null?"Prop null":"Prop not null");
			return;
		}
		
		view=prop;
		view.setFile(file);
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder db=dbf.newDocumentBuilder();
			Document doc = db.newDocument();
			
			writeConfiguration(doc);
			
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(view.getFile());

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

	private void writeConfiguration(Document doc) 
	{
		// root element
		Element root=doc.createElement(UOSProfileReader.PROFILE);
		doc.appendChild(root);
		
		// mic
		Element mic=doc.createElement(UOSProfileReader.MICROSCOPE);
//		Attr attr=doc.createAttribute(UOSProfileReader.MIC_NAME);
//		attr.setValue(view.getMicName());
		root.appendChild(mic);
		mic.setAttribute(UOSProfileReader.MIC_NAME, view.getMicName()!=null ? view.getMicName():"Unspecific");
		
		//submodules
		Element sub=doc.createElement(UOSProfileReader.MODULE);
		root.appendChild(sub);
		
		sub.appendChild(view.getImageConf().toXML(doc, UOSProfileReader.MODULE_IMG));
		sub.appendChild(view.getObjConf().toXML(doc, UOSProfileReader.MODULE_OBJECTIVE));
		sub.appendChild(view.getDetectorConf().toXML(doc, UOSProfileReader.MODULE_DETECTOR));
		sub.appendChild(view.getLightSrcConf().toXML(doc, UOSProfileReader.MODULE_LIGHTSRC));
		
		sub.appendChild(view.getChannelConf().toXML(doc, UOSProfileReader.MODULE_CHANNEL));
		sub.appendChild(view.getLightPathConf().toXML(doc, UOSProfileReader.MODULE_LIGHTPATH));
		sub.appendChild(view.getSampleConf().toXML(doc, UOSProfileReader.MODULE_SAMPLE));
		sub.appendChild(view.getExpConf().toXML(doc, UOSProfileReader.MODULE_EXPERIMENTER));
		
		sub.appendChild(view.getImgEnvConf().toXML(doc, UOSProfileReader.MODULE_IMGENV));
		sub.appendChild(view.getPlaneConf().toXML(doc, UOSProfileReader.MODULE_PLANE));
	}


}
