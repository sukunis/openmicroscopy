package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.BooleanUtils;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.UOSMetadataLogger;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.microscope.CustomViewProperties;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.microscope.MetaDataUI.GUIPlaceholder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;



/**<Profile>
 * 	<Microscope Name=""/>
 * 	<Submodules>
 * 		<ImageData Pos="" Width="" Visible="">
 * 			<Tag1 Name="" Visible="" Value="" Optional=""/>
 * 			<Tag2>
 *  	<\ImageData>
 *	 <\Submodules>
 * <\Profile>
 * @author sukunis
 *
 */
public class UOSProfileReader 
{
	/** Logger for this class. */
    protected static Logger LOGGER = Logger.getLogger(UOSMetadataLogger.class.getName());
    
	private static final String PROFILE = "Profile";
	public static final String MICROSCOPE="Microscope";
	public static final String MIC_NAME="Name";
	public static final String MODULE="Submodules";
	
	public static final String M_POSITION="Position";
	public static final String M_WIDTH="Width";
	public static final String M_VIS="Visible";
	
//	public static final String TAG_VALUE="Value";
//	public static final String TAG_PROP="Optional";
	
	
	public static final String MODULE_IMG="ImageData";
	public static final String MODULE_CHANNEL="ChannelData";
	public static final String MODULE_OBJECTIVE="ObjectiveData";
	public static final String MODULE_DETECTOR="DetectorData";
	public static final String MODULE_EXPERIMENTER="ExperimentData";
	public static final String MODULE_SAMPLE="SampleData";
	public static final String MODULE_LIGHTSRC="LightSourceData";
	public static final String MODULE_LIGHTPATH="LightPathData";
	public static final String MODULE_IMGENV="ImageEnvironmentData";
	public static final String MODULE_PLANE="PlaneData";
	
	
	private CustomViewProperties view;
	
	public UOSProfileReader(File file)
	{
		view=new CustomViewProperties();
		if(file==null){
			return;
		}
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();
			Document doc = db.parse(file);
			//TODO validation of xml
			
			readConfiguration(doc);
			
		} catch (ParserConfigurationException | SAXException | IOException e) {
			LOGGER.severe("[VIEW_PROP] Can't read property file");
		} 
	}
	
	public CustomViewProperties getViewProperties()
	{
		return view;
	}
	
	private void readMicName(Element node)
	{
		if(node!=null){
			NamedNodeMap attr =node.getAttributes();
			String name="Unspecified";
			if(attr!=null && attr.getLength()>0 && attr.getNamedItem(MIC_NAME)!=null)
				name=attr.getNamedItem(MIC_NAME).getNodeValue();
			
			view.setMicName(name);
		}
	}
	
	private void readConfiguration(Document doc) 
	{
		NodeList root=doc.getElementsByTagName(PROFILE);
		if(root.getLength() >0){
			Element node=(Element) root.item(0);
			
			try{
				readMicName((Element)(node.getElementsByTagName(MICROSCOPE)).item(0));
			}catch(Exception e){
//				LOGGER.info("No MICROSCOPE NAME given");
			}
			NodeList nodes=node.getElementsByTagName(MODULE);

			if(nodes.getLength() >0){
				Element modules =(Element) nodes.item(0);
				loadElement(modules,MODULE_IMG);
				loadElement(modules,MODULE_CHANNEL);
				loadElement(modules,MODULE_OBJECTIVE);
				loadElement(modules,MODULE_DETECTOR);
				loadElement(modules,MODULE_IMGENV);
				loadElement(modules,MODULE_SAMPLE);
				loadElement(modules,MODULE_PLANE);
				loadElement(modules,MODULE_LIGHTPATH);
				loadElement(modules,MODULE_LIGHTSRC);
				loadElement(modules,MODULE_EXPERIMENTER); 

			}else{
				System.err.println("No submodule specification");
			}
		}
	}

	private boolean loadElement(Element modules,String moduleName) 
	{
		boolean loaded=false;
		if(modules.getElementsByTagName(moduleName).getLength()>0)
		{
			Element node=(Element) modules.getElementsByTagName(moduleName).item(0);
			NamedNodeMap imgProp=node.getAttributes();
			if(imgProp.getLength()>1){
				GUIPlaceholder pos=null; String width=null; boolean vis=false;
				if(imgProp.getNamedItem(M_POSITION)!=null)
					pos=GUIPlaceholder.valueOf(imgProp.getNamedItem(M_POSITION).getNodeValue());
				if(imgProp.getNamedItem(M_WIDTH)!=null)
					width=imgProp.getNamedItem(M_WIDTH).getNodeValue();
				if(imgProp.getNamedItem(M_VIS)!=null)
					vis=BooleanUtils.toBoolean(imgProp.getNamedItem(M_VIS).getNodeValue());
				
				if(vis && pos!=null && width!=null && !pos.equals("") && !width.equals("")){
					addProperty(moduleName, pos, width);
					loaded=true;
				}else{
					System.err.println("Wrong properties : "+moduleName);
					return false;
				}
				
			}else{
				System.err.println("Module property not complete: "+moduleName);
				return false;
			}
			
			addTags(node,moduleName);
			loaded=true;
			
		}else{
			System.out.println("Module not loaded: "+moduleName);
		}
		
		return loaded;
	}

	private void addTags(Element node, String moduleName) 
	{
		switch (moduleName) {
		case MODULE_IMG:
			loadImageTags(node);
			break;
		case MODULE_CHANNEL:
			loadChannelTags(node);
			break;
		case MODULE_EXPERIMENTER:
			loadExperimenterTags(node);
			break;
		case MODULE_OBJECTIVE:
			loadObjectiveTags(node);
			break;
		case MODULE_DETECTOR:
			loadDetectorTags(node);
			break;
		case MODULE_LIGHTPATH:
			loadLightPathTags(node);
			break;
		case MODULE_LIGHTSRC:
			loadLightSrcTags(node);
			break;
		case MODULE_SAMPLE:
			loadSampleTags(node);
			break;
		case MODULE_PLANE:
			loadPlaneTags(node);
			break;
		case MODULE_IMGENV:
			loadImgEnvTags(node);
			break;
		default:
			System.out.println("Unknown module: "+moduleName);
			break;
		}
	}

	private void loadImgEnvTags(Element node) 
	{
		ModuleConfiguration conf=new ModuleConfiguration();
		conf.loadTags(node);
view.setImgEnvConf(conf);		
	}

	private void loadPlaneTags(Element node) {
		ModuleConfiguration conf=new ModuleConfiguration();
		conf.loadTags(node);
view.setPlaneConf(conf);	
	}

	private void loadSampleTags(Element node) {
		ModuleConfiguration conf=new ModuleConfiguration();
		conf.loadTags(node);
view.setSampleConf(conf);	
	}

	private void loadLightSrcTags(Element node) {
		ModuleConfiguration conf=new ModuleConfiguration();
		conf.loadTags(node);
view.setLightSrcConf(conf);	
	}

	private void loadLightPathTags(Element node) {
		ModuleConfiguration conf=new ModuleConfiguration();
		conf.loadTags(node);
view.setLightPathConf(conf);		
	}

	private void loadDetectorTags(Element node) {
		ModuleConfiguration conf=new ModuleConfiguration();
		conf.loadTags(node);
view.setDetectorConf(conf);		
	}

	private void loadObjectiveTags(Element node) {
		ModuleConfiguration conf=new ModuleConfiguration();
		conf.loadTags(node);
		view.setObjConf(conf);
	}

	private void loadExperimenterTags(Element node) {
		ModuleConfiguration conf=new ModuleConfiguration();
		conf.loadTags(node);
view.setExperimenterConf(conf);		
	}

	private void loadChannelTags(Element node) {
		ModuleConfiguration conf=new ModuleConfiguration();
		conf.loadTags(node);
view.setChannelConf(conf);		
	}

	private void loadImageTags(Element node) 
	{
		ModuleConfiguration conf=new ModuleConfiguration();
		conf.loadTags(node);
view.setImageConf(conf);
	}
	

//	
//	private boolean hasElement(Element node, String name)
//	{
//		 NodeList nodeList = node.getElementsByTagName(name);
//		 return nodeList.getLength() != 0 ? true : false;
//	}

	private void addProperty(String moduleName, GUIPlaceholder pos, String width) 
	{
		switch (moduleName) {
		case MODULE_IMG:
			view.addImageData(pos,width);
			break;
		case MODULE_CHANNEL:
			view.addChannelData(pos,width);
			break;
		case MODULE_EXPERIMENTER:
			view.addExperimentData(pos, width);
			break;
		case MODULE_OBJECTIVE:
			view.addObjectiveData(pos, width);
			break;
		case MODULE_DETECTOR:
			view.addDetectorData(pos, width);
			break;
		case MODULE_LIGHTPATH:
			view.addLightPathData(pos, width);
			break;
		case MODULE_LIGHTSRC:
			view.addLightSourceData(pos, width);
			break;
		case MODULE_SAMPLE:
			view.addSampleData(pos, width);
			break;
		case MODULE_PLANE:
			view.addPlaneData(pos, width);
			break;
		case MODULE_IMGENV:
			view.addImageEnvData(pos, width);
			break;
		default:
			System.out.println("Unknown module "+moduleName);
			break;
		}
		
		
	}

//	/**
//	 * @param args
//	 */
//	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//		new UOSProfileReader(new File("src/profileUOSImporter.xml"));
//
//	}

}
