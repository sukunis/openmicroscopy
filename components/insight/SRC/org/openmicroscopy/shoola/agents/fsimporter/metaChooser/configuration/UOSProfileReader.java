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
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.microscope.MetaDataUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.microscope.MetaDataUI.GUIPlaceholder;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.ExceptionDialog;
import org.slf4j.LoggerFactory;
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
 * 			<Tag Name="" Visible="" Value="" Optional=""/>
 * 			<Tag>
 *  	<\ImageData>
 *	 <\Submodules>
 * <\Profile>
 * @author sukunis
 *
 */
public class UOSProfileReader 
{
	/** Logger for this class. */
//    protected static Logger LOGGER = Logger.getLogger(UOSMetadataLogger.class.getName());
	 private static final org.slf4j.Logger LOGGER =
	    	    LoggerFactory.getLogger(UOSProfileReader.class);
	 
	public static final String PROFILE = "Profile";
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
	
		if(file==null || !file.exists()){
			return;
		}
		view=new CustomViewProperties();
		view.setFile(file);
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();
			Document doc = db.parse(file);
			
			System.out.println("Read "+file.getName());
			//TODO validation of xml
			
			readConfiguration(doc);
			
		} catch (/*ParserConfigurationException | SAXException | IOException*/Exception  e) {
			LOGGER.error("[VIEW_PROP] Can't read property file");
			ExceptionDialog ld = new ExceptionDialog("Property File Error!", 
					"Can't read given property file "+file.getAbsolutePath(),e,
					this.getClass().getSimpleName());
			ld.setVisible(true);
			view=null;
		} 
	}
	
	public CustomViewProperties getDefaultProperties()
	{
		view=new CustomViewProperties();
		view.init();
		return view;
	}
	
	public CustomViewProperties getViewProperties()
	{
		return view;
	}
	
	public static String readMicName(Element node)
	{
		String name="Unspecified";
		if(node!=null){
			NamedNodeMap attr =node.getAttributes();
			
			if(attr!=null && attr.getLength()>0 && attr.getNamedItem(MIC_NAME)!=null)
				name=attr.getNamedItem(MIC_NAME).getNodeValue();
		}
		return name;
	}
	
	private void readConfiguration(Document doc) throws Exception
	{
		NodeList root=doc.getElementsByTagName(PROFILE);
		if(root.getLength() >0){
			Element node=(Element) root.item(0);
			
			view.setMicName(readMicName((Element)(node.getElementsByTagName(MICROSCOPE)).item(0)));
			
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
				throw new Exception("unknown xml scheme!");
			}
		}else{
			throw new Exception("unknown xml scheme!");
		}
	}

	private void loadElement(Element modules,String moduleName) 
	{
		if(modules.getElementsByTagName(moduleName).getLength()>0)
		{
			Element node=(Element) modules.getElementsByTagName(moduleName).item(0);
			NamedNodeMap imgProp=node.getAttributes();
			 boolean vis=false;
			 GUIPlaceholder pos=null;
			 String width=null;
			if(imgProp.getLength()>1){
				 
				if(imgProp.getNamedItem(M_POSITION)!=null)
					pos=GUIPlaceholder.valueOf(imgProp.getNamedItem(M_POSITION).getNodeValue());
				if(imgProp.getNamedItem(M_WIDTH)!=null)
					width=imgProp.getNamedItem(M_WIDTH).getNodeValue();
				if(imgProp.getNamedItem(M_VIS)!=null)
					vis=BooleanUtils.toBoolean(imgProp.getNamedItem(M_VIS).getNodeValue());
				
				if(vis){
					if( !(pos!=null && width!=null && !pos.equals("") && !width.equals(""))){
						vis=false;
						LOGGER.warn("[GUI] module property not complete: "+moduleName);
					}
				}else{
					LOGGER.info("[GUI] hide "+moduleName);
				}
				
			}else{
				LOGGER.warn("[GUI] module property not complete: "+moduleName);
				return ;
			}
			
			addTags(node,moduleName,vis,pos,width);
			
		}else{
			LOGGER.info("[GUI] module not specified: "+moduleName);
		}
		
	}

	private void addTags(Element node, String moduleName,boolean visible, GUIPlaceholder position,String width) 
	{
		ModuleConfiguration conf=new ModuleConfiguration(visible,position,width);
		
		switch (moduleName) {
		case MODULE_IMG:
			conf.loadTags(node);
			view.setImageConf(conf);
			break;
		case MODULE_CHANNEL:
			conf.loadTags(node);
			view.setChannelConf(conf);
			break;
		case MODULE_EXPERIMENTER:
			conf.loadTags(node);
			view.setExperimenterConf(conf);
			break;
		case MODULE_OBJECTIVE:
			conf.loadTags(node);
			view.setObjConf(conf);
			break;
		case MODULE_DETECTOR:
			conf.loadTags(node);
			view.setDetectorConf(conf);
			break;
		case MODULE_LIGHTPATH:
			conf.loadTags(node);
			view.setLightPathConf(conf);
			break;
		case MODULE_LIGHTSRC:
			conf.loadTags(node);
			view.setLightSrcConf(conf);	
			break;
		case MODULE_SAMPLE:
			conf.loadTags(node);
			view.setSampleConf(conf);
			break;
		case MODULE_PLANE:
			conf.loadTags(node);
			view.setPlaneConf(conf);
			break;
		case MODULE_IMGENV:
			conf.loadTags(node);
			view.setImgEnvConf(conf);
			break;
		default:
			LOGGER.warn("[VIEW_PROP] Unknown module: "+moduleName);
			break;
		}
	}




}
