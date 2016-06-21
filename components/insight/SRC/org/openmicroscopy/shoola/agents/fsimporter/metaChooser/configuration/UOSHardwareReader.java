package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import ome.units.UNITS;
import ome.units.quantity.Frequency;
import ome.units.quantity.Length;
import ome.units.quantity.Power;
import ome.units.unit.Unit;
import ome.xml.model.Arc;
import ome.xml.model.Detector;
import ome.xml.model.Dichroic;
import ome.xml.model.Filament;
import ome.xml.model.Filter;
import ome.xml.model.GenericExcitationSource;
import ome.xml.model.Laser;
import ome.xml.model.LightEmittingDiode;
import ome.xml.model.LightPath;
import ome.xml.model.LightSource;
import ome.xml.model.Objective;
import ome.xml.model.enums.ArcType;
import ome.xml.model.enums.Correction;
import ome.xml.model.enums.DetectorType;
import ome.xml.model.enums.EnumerationException;
import ome.xml.model.enums.FilamentType;
import ome.xml.model.enums.FilterType;
import ome.xml.model.enums.Immersion;
import ome.xml.model.enums.LaserMedium;
import ome.xml.model.enums.LaserType;
import ome.xml.model.enums.Pulse;
import ome.xml.model.enums.UnitsFrequency;
import ome.xml.model.enums.UnitsLength;
import ome.xml.model.enums.UnitsPower;
import ome.xml.model.enums.handlers.UnitsFrequencyEnumHandler;
import ome.xml.model.enums.handlers.UnitsLengthEnumHandler;
import ome.xml.model.enums.handlers.UnitsPowerEnumHandler;
import ome.xml.model.primitives.PositiveInteger;

import org.apache.commons.lang.BooleanUtils;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.UOSMetadataLogger;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.ElementsCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.LightSourceCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.microscope.MetaDataUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.ExceptionDialog;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * <Hardware>
 * 	<Microscope Name=""/>
 *  <Objectives>
 *  	<Objective>
 *  		<Tag Name="" Value=""/>
 *     		<Tag Name="" Value=""/>
 *     </Objective>
 *     <Objective>
 *  		<Tag Name="" Value=""/>
 *     		<Tag Name="" Value=""/>
 *     </Objective>
 *  </Objectives>
 *  <Detectors>
 *  	<Detector></Detector>
 *  </Detectors>
 *  <LightSources>
 *  	<Laser></Laser>
 *  	<Arc></Arc>
 *  	<Filament></Filament>
 *  	<GES></GES>
 *  	<LED></LED>
 *  </LightSources>
 *  <LightPaths>
 *  	<Filter>
 *  	<Dichroic>
 *  </LightPaths>		
 * <\Hardware>
 * 
 * @author kunis
 *
 */
public class UOSHardwareReader 
{
	private static final String HARDWARE="Hardware";
	private static final String SET_OBJECTIVE="Objectives";
	private static final String SET_DETECTOR="Detectors";
	private static final String SET_LIGHTSRC="LightSources";
	private static final String SET_LIGHTPATH="LightPaths";
	
	private static final String OBJECTIVE="Objective";
	private static final String DETECTOR="Detector";
	private static final String LIGHTSRC_L="Laser";
	private static final String LIGHTSRC_A="Arc";
	private static final String LIGHTSRC_F="Filament";
	private static final String LIGHTSRC_G="GES";
	private static final String LIGHTSRC_LED="LED";
	private static final String LIGHTPATH="LightPath";
	private static final String FILTER="Filter";
	private static final String DICHROIC="Dichroic";
	
	
	/** Logger for this class. */
//    protected static Logger LOGGER = Logger.getLogger(UOSMetadataLogger.class.getName());
	 private static final org.slf4j.Logger LOGGER =
	    	    LoggerFactory.getLogger(UOSHardwareReader.class);
    
    private List<Objective> objectiveList;
    private List<Detector> detectorList;
    private List<LightSource> lightSrcList;
    private List<Filter> lightPathFilterList;
    
    private Unit unit;
    
    
	public UOSHardwareReader(File file)
	{
		if(file==null || !file.exists()){
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
			LOGGER.error("[VIEW_PROP] Can't read hardware file");
			ExceptionDialog ld = new ExceptionDialog("Hardware File Error!", 
					"Can't read given hardware file "+file.getAbsolutePath(),e);
			ld.setVisible(true);
		} 
	}
	
	/**
	 * Read the hardware element from given document.
	 * @param doc
	 */
	private void readConfiguration(Document doc) 
	{
		NodeList root=doc.getElementsByTagName(HARDWARE);
		if(root.getLength() >0){
			Element node=(Element) root.item(0);

			try{
				UOSProfileReader.readMicName((Element)(node.getElementsByTagName(
						UOSProfileReader.MICROSCOPE)).item(0));
			}catch(Exception e){
				//				LOGGER.info("No MICROSCOPE NAME given");
			}

			loadElements(node.getElementsByTagName(SET_OBJECTIVE),OBJECTIVE);
			loadElements(node.getElementsByTagName(SET_DETECTOR),DETECTOR);
			loadElements(node.getElementsByTagName(SET_LIGHTPATH),LIGHTPATH);
			loadElements(node.getElementsByTagName(SET_LIGHTSRC),LIGHTSRC_L);
			loadElements(node.getElementsByTagName(SET_LIGHTSRC),LIGHTSRC_A);
			loadElements(node.getElementsByTagName(SET_LIGHTSRC),LIGHTSRC_F);
			loadElements(node.getElementsByTagName(SET_LIGHTSRC),LIGHTSRC_G);
			loadElements(node.getElementsByTagName(SET_LIGHTSRC),LIGHTSRC_LED);
		}
	}

	/**
	 * Extract subNodes of names nodeName from given nodeList list
	 * @param list given list of elements
	 * @param nodeName name of node that should be extract from list
	 */
	private void loadElements(NodeList list, String nodeName) 
	{
		if(list!=null && list.getLength()>0)
		{
			NodeList subNodes=((Element) list.item(0)).getElementsByTagName(nodeName);
			if(subNodes!=null && subNodes.getLength()>0){
				parseElements(subNodes,nodeName);
			}
		}
	}


	/**
	 * Call the parser for elements of given node type
	 */
	private void parseElements(NodeList subNodes, String nodeName) 
	{
		switch(nodeName){
		case OBJECTIVE:
			parseObjectives(subNodes);
			break;
		case DETECTOR:
			parseDetectors(subNodes);
			break;
		case LIGHTPATH:
			parseLightPaths(subNodes);
			break;
		case LIGHTSRC_L:
			parseLightSources(subNodes,new Laser());
			break;
		case LIGHTSRC_A:
			parseLightSources(subNodes,new Arc());
			break;
		case LIGHTSRC_F:
			parseLightSources(subNodes,new Filament());
			break;
		case LIGHTSRC_G:
			parseLightSources(subNodes,new GenericExcitationSource());
			break;
		case LIGHTSRC_LED:
			parseLightSources(subNodes,new LightEmittingDiode());
			break;
		default:
			LOGGER.warn("[CONF] unknown hardware element "+nodeName);
				break;
		}
	}

	/**
	 * Parse LightPath elements of structure
	 * <LightPath>
	 * 	<Filter>
	 * 	</Filter>
	 * 	<Dichroic>
	 *  </Dichroic>
	 * </LightPath>
	 * A lightpath element can contains one or more elements of dichroic and/or filter.
	 * @param subNodes list of <LightPath> elements
	 */
	private void parseLightPaths(NodeList subNodes) 
	{
		if(lightPathFilterList==null)
			lightPathFilterList=new ArrayList<Filter>();
		
		for(int i=0; i<subNodes.getLength(); i++)
		{
			Element node=(Element) subNodes.item(i);
			
			List<Filter> fL=parseLightPath(node);
			if(fL!=null && !fL.isEmpty()){
				lightPathFilterList.addAll(fL);
				LOGGER.info("[HARDWARE] mic available lightPath");
			}
		}
		
	}





	private void parseLightSources(NodeList subNodes,LightSource l) 
	{
		if(lightSrcList==null)
			lightSrcList=new ArrayList<LightSource>();
		
		for(int i=0; i<subNodes.getLength(); i++){
			Element node=(Element) subNodes.item(i);
			
			LightSource o=parseLightSource(node,l);
			if(o!=null){
				lightSrcList.add(o);
				LOGGER.info("[HARDWARE] mic available lightSrc "+o.getModel());
			}
		}
	}



	private void parseDetectors(NodeList subNodes) 
	{
		detectorList=new ArrayList<Detector>();
		for(int i=0; i<subNodes.getLength(); i++){
			Element node=(Element) subNodes.item(i);
			
			Detector o=parseDetector(node);
			if(o!=null){
				detectorList.add(o);
				LOGGER.info("[HARDWARE] mic available detector "+o.getModel());
			}
		}
	}


	

	private void parseObjectives(NodeList subNodes)
	{
		objectiveList=new ArrayList<Objective>();
		for(int i=0; i<subNodes.getLength(); i++){
			Element node=(Element) subNodes.item(i);
			
			Objective o=parseObjective(node);
			if(o!=null){
				objectiveList.add(o);
				LOGGER.info("[HARDWARE] mic available objective "+o.getModel());
			}
		}
	}


	private Objective parseObjective(Element node) 
	{
		Objective o=null;
		NodeList tags=node.getElementsByTagName("Tag");
		if(tags!=null && tags.getLength()>0){
			o=new Objective();
			for(int i=0; i<tags.getLength(); i++)
			{
				NamedNodeMap attr=tags.item(i).getAttributes();
				String name=null;String value=null;
				if(attr!=null && attr.getLength()>0)
				{
					if(attr.getNamedItem(ModuleConfiguration.TAG_NAME)!=null){
						name=attr.getNamedItem(ModuleConfiguration.TAG_NAME).getNodeValue();
					}
					if(attr.getNamedItem(ModuleConfiguration.TAG_VALUE)!=null){
						value=attr.getNamedItem(ModuleConfiguration.TAG_VALUE).getNodeValue();
					}
					if(attr.getNamedItem(ModuleConfiguration.TAG_UNIT)!=null){
						try {
							unit=parseUnit(attr.getNamedItem(ModuleConfiguration.TAG_UNIT).getNodeValue(),name);
						} catch (Exception e) {
							LOGGER.warn("[HARDWARE] wrong format of parameters of tag "+name);
							LOGGER.warn("[HARDWARE] "+e.getMessage());
						}
					}
					
					o=setObjectiveVal(o,name,value);
				}	
			}
		}
		return o;
	}
	
	/**
	 * 
	 * @param node
	 * @return empty detector if no tags available
	 */
	private Detector parseDetector(Element node) 
	{
		Detector d=new Detector();
		NodeList tags=node.getElementsByTagName("Tag");
		
		if(tags==null || !(tags.getLength()>0))
			return d;

		for(int i=0, len=tags.getLength(); i<len; i++)
		{
			NamedNodeMap attr=tags.item(i).getAttributes();
			String name=null;String value=null; 
			if(attr!=null && attr.getLength()>0)
			{
				if(attr.getNamedItem(ModuleConfiguration.TAG_NAME)!=null){
					name=attr.getNamedItem(ModuleConfiguration.TAG_NAME).getNodeValue();
				}
				if(attr.getNamedItem(ModuleConfiguration.TAG_VALUE)!=null){
					value=attr.getNamedItem(ModuleConfiguration.TAG_VALUE).getNodeValue();
				}
				if(attr.getNamedItem(ModuleConfiguration.TAG_UNIT)!=null){
					try {
						unit=parseUnit(attr.getNamedItem(ModuleConfiguration.TAG_UNIT).getNodeValue(),name);
					} catch (Exception e) {
						LOGGER.warn("[HARDWARE] wrong format of parameters of tag "+name);
						LOGGER.warn("[HARDWARE] "+e.getMessage());
					}
				}
				d=setDetectorVal(d,name,value);
			}	
		}
		return d;
	}
	
	/**
	 * @param node <LightPath> element
	 * @return empty list if no filter for the lightPath available.
	 */
	private List<Filter> parseLightPath(Element node) 
	{
		NodeList filterList=node.getElementsByTagName(TagNames.FILTER);
		
		if(filterList==null || !(filterList.getLength()>0))
			return null;
		
		List<Filter> fList=new ArrayList<Filter>();
		
		for (int i = 0, len = filterList.getLength(); i < len; i++){
	        Node currentNode = filterList.item(i);
	        if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
	        	Element elem=(Element) currentNode;
	        	Filter f=parseFilter(elem);
	        	if(f!=null)
	        		fList.add(f);
//	        	String fClass=elem.getAttribute(TagNames.FILTER_CLASS);
//	        	if(fClass.equals(TagNames.FILTER_CLASS_EX)){
//	        	}else{
//	        	}
	        }
	    }
		return fList;
	}

	/**
	 * Parse <Filter> element of lightPath
	 * @param node <Filter> element
	 * @return 
	 */
	private Filter parseFilter(Element node) 
	{
		Filter filter=new Filter();
		NodeList tags=node.getElementsByTagName("Tag");
		
		if(tags==null || !(tags.getLength()>0)){
			return null;
		}
		int fieldCounter=0;
		for(int i=0, len=tags.getLength(); i<len; i++)
		{
			NamedNodeMap attr=tags.item(i).getAttributes();
			String name=null;String value=null; 
			if(attr!=null && attr.getLength()>0)
			{
				if(attr.getNamedItem(ModuleConfiguration.TAG_NAME)!=null){
					name=attr.getNamedItem(ModuleConfiguration.TAG_NAME).getNodeValue();
				}
				if(attr.getNamedItem(ModuleConfiguration.TAG_VALUE)!=null){
					value=attr.getNamedItem(ModuleConfiguration.TAG_VALUE).getNodeValue();
				}
				if(name!=null && !name.equals("") && value!=null && !value.equals("")){
					filter=setFilterVal(filter,name,value);
					fieldCounter++;
				}
			}	
		}
		//Check filter is not empty
		if(fieldCounter==0)
			return null;
		
		return filter;
	}

	

	private LightSource parseLightSource(Element node, LightSource l)
	{
		if(l instanceof Laser)
			l= new Laser();
		else if(l instanceof Arc)
			l=new Arc();
		else if (l instanceof Filament)
			l=new Filament();
		else if (l instanceof GenericExcitationSource)
			l=new GenericExcitationSource();
		else if(l instanceof LightEmittingDiode)
			l=new LightEmittingDiode();
		else{
			LOGGER.warn("[HARDWARE] unknown lightSource type");
			return null;
		}
		
		
		NodeList tags=node.getElementsByTagName("Tag");
		if(tags!=null && tags.getLength()>0){
			
			for(int i=0; i<tags.getLength(); i++)
			{
				NamedNodeMap attr=tags.item(i).getAttributes();
				String name=null;String value=null; 
				if(attr!=null && attr.getLength()>0)
				{
					if(attr.getNamedItem(ModuleConfiguration.TAG_NAME)!=null){
						name=attr.getNamedItem(ModuleConfiguration.TAG_NAME).getNodeValue();
					}
					if(attr.getNamedItem(ModuleConfiguration.TAG_VALUE)!=null){
						value=attr.getNamedItem(ModuleConfiguration.TAG_VALUE).getNodeValue();
					}
					if(attr.getNamedItem(ModuleConfiguration.TAG_UNIT)!=null){
						try {
							unit=parseUnit(attr.getNamedItem(ModuleConfiguration.TAG_UNIT).getNodeValue(),name);
						} catch (Exception e) {
							LOGGER.warn("[HARDWARE] wrong format of parameters of tag "+name);
							e.getMessage();
						}
					}
					l=setLightSourceVal(l,name,value);
				}	
			}
		}
		return l;
	}


	private Filter setFilterVal(Filter filter, String name, String val) 
	{
		{
			
			LOGGER.info("[DEBUG] add mic lightpath tag "+name+" = "+val);
			try{
				switch (name) {
				case TagNames.MODEL:
					filter.setModel(val);
					break;
				case TagNames.MANUFAC:
					filter.setManufacturer(val);
					break;
				case TagNames.TYPE:
					filter.setType(val.equals("")? null : FilterType.fromString(val));
					break;
				case TagNames.FILTERWHEEL:
					filter.setFilterWheel(val);
					break;
				default:
					LOGGER.warn("[CONF] unknown tag: "+name );break;
				}
			}catch(Exception e){
				LOGGER.warn("[HARDWARE] can't parse filter tag "+name);
				e.printStackTrace();
			}
		}
		return filter;
	}


	private LightSource setLightSourceVal(LightSource lightSrc, String name, String val) 
	{
		if(name!=null && !name.equals("") && val!=null && !val.equals("")){
			LOGGER.info("[DEBUG] add mic lightSrc tag "+name+" = "+val);
			try{
				switch (name) {
				case TagNames.MODEL:
					lightSrc.setModel(val);
					break;
				case TagNames.MANUFAC:
					lightSrc.setManufacturer(val);
					break;
				case TagNames.L_TYPE:
					LaserType l=LightSourceCompUI.parseLaserType(val);
					if(l!=null)
						((Laser)lightSrc).setType(l);
					break;
				case TagNames.A_TYPE:
					ArcType a=LightSourceCompUI.parseArcType(val);
					if(a!=null)
						((Arc)lightSrc).setType(a);
					break;
				case TagNames.F_TYPE:
					FilamentType value=LightSourceCompUI.parseFilamentType(val);
					if(value!=null)
						((Filament)lightSrc).setType(value);
					break;
				case TagNames.POWER:
					Power p = LightSourceCompUI.parsePower(val, unit);
					lightSrc.setPower(p);
					break;
				case TagNames.MEDIUM:
					LaserMedium lm=LightSourceCompUI.parseMedium(val);
					((Laser)lightSrc).setLaserMedium(lm);
					break;
				case TagNames.FREQMUL:
					PositiveInteger pi=LightSourceCompUI.parseToPositiveInt(val);
					((Laser)lightSrc).setFrequencyMultiplication(pi);
					break;
				case TagNames.TUNABLE:
					((Laser)lightSrc).setTuneable(BooleanUtils.toBoolean(val));
					break;
				case TagNames.PULSE:
					Pulse pu=LightSourceCompUI.parsePulse(val);
					((Laser)lightSrc).setPulse(pu);
					break;
				case TagNames.POCKELCELL:
					((Laser)lightSrc).setPockelCell(BooleanUtils.toBoolean(val));
					break;
				case TagNames.REPRATE:
					Frequency f=null;
					if(unit!=null )
						f=LightSourceCompUI.parseFrequency(val, unit);
					((Laser)lightSrc).setRepetitionRate(f);
					break;
				case TagNames.PUMP:
					//						TODO: ((Laser)lightSrc).linkPump(o);
					break;
				case TagNames.WAVELENGTH:
					Length le = ElementsCompUI.parseToLength(val, unit);
					((Laser)lightSrc).setWavelength(le);
					break;
				case TagNames.MAP:
					//						TODO:((GenericExcitationSource)lightSrc).setMap(value);
					break;
				case TagNames.DESC:
					//					TODO: ((LightEmittingDiode)lightSrc).set
					break;
				default:
					LOGGER.warn("[CONF] unknown tag: "+name );break;
				}
			}catch(Exception e){
				LOGGER.warn("[HARDWARE] can't parse lightSrc tag "+name);
				e.printStackTrace();
			}
		}
		return lightSrc;
	}


	private Detector setDetectorVal(Detector detector, String name, String val) 
	{
		if(name!=null && !name.equals("") && val!=null && !val.equals("")){
			LOGGER.info("[DEBUG] add mic detector tag "+name+" = "+val);
			try{
				switch (name) {
				case TagNames.MODEL: 
					detector.setModel(val);
					break;
				case TagNames.MANUFAC: 
					detector.setManufacturer(val);
					break;
				case TagNames.TYPE:
					DetectorType value= DetectorType.fromString(val);
					detector.setType(value);
					break;
				case TagNames.ZOOM:
					detector.setZoom(Double.valueOf(val));
					break;
				case TagNames.AMPLGAIN:
					detector.setZoom(Double.valueOf(val));
					break;
					//			case L_GAIN:
					//					detector.setGain(Double.valueOf(val));
					//				break;
					//			case L_VOLTAGE:
					//					ElectricPotential value=new ElectricPotential(Double.valueOf(val), voltageUnit);
					//					detector.setVoltage(value);
					//				break;
					//			case L_OFFSET:
					//					detector.setOffset(Double.valueOf(val));
					//				break;
				default: LOGGER.warn("[HARDWARE] unknown tag: "+name );break;
				}
			}catch(Exception e){
				LOGGER.warn("[HARDWARE] can't parse detector tag "+name);
			}
		}
		return detector;
	}


	private Objective setObjectiveVal(Objective o, String name, String value) 
	{
		if(name!=null && !name.equals("") && value!=null && !value.equals("")){
			LOGGER.info("[DEBUG] add mic objective tag "+name+" = "+value);
			try{
				switch (name) {
				case TagNames.MODEL:
					o.setModel(value);
					break;
				case TagNames.MANUFAC:
					o.setManufacturer(value);
					break;
				case TagNames.NOMMAGN:
					o.setNominalMagnification(Double.valueOf(value));
					break;
				case TagNames.CALMAGN:
					o.setCalibratedMagnification(Double.valueOf(value));
					break;
				case TagNames.LENSNA:
					o.setLensNA(Double.valueOf(value));
					
					break;
				case TagNames.IMMERSION:
					Immersion im=Immersion.fromString(value);
					o.setImmersion(im);
					break;
				case TagNames.CORRECTION:
					Correction co = Correction.fromString(value);
					o.setCorrection(co);
					break;
				case TagNames.WORKDIST:
					Length l= new Length(Double.valueOf(value), unit);
					o.setWorkingDistance(l);
					break;
				default:
					LOGGER.warn("[HARDWARE] unknown tag: "+name );break;
				}	
			}catch(Exception e){
				LOGGER.warn("[HARDWARE] can't parse objective tag "+name);
			}
		}
		return o;
	}

	public List<Objective> getObjectives()
	{
		if(objectiveList!=null)
			LOGGER.info("[HARDWARE] hardware definition objectives "+objectiveList.size());
		else
			LOGGER.info("[HARDWARE] hardware definition objectives : null ");
		return objectiveList;
	}
	
	public List<Detector> getDetectors()
	{
		if(detectorList!=null)
			LOGGER.info("[HARDWARE] hardware definition detectors "+detectorList.size());
		else
			LOGGER.info("[HARDWARE] hardware definition detectors : null ");
		return detectorList;
	}
	
	public List<LightSource> getLightSources()
	{
		if(lightSrcList!=null)
			LOGGER.info("[HARDWARE] hardware definition lightSrc "+lightSrcList.size());
		else
			LOGGER.info("[HARDWARE] hardware definition lightSrc : null");
		return lightSrcList;
	}
	
	public List<Filter> getLightPathFilters() 
	{
		if(lightPathFilterList!=null)
			LOGGER.info("[HARDWARE] hardware definition of lightPath "+lightPathFilterList.size());
		else
			LOGGER.info("[HARDWARE] hardware definition of lightPath : null");
		
		return lightPathFilterList;
	}
	
	
	
	
	public static Unit parseUnit(String unitSymbol, String name) throws Exception 
	{
		Unit unit=null;
		
		if(unitSymbol!=null && !unitSymbol.equals("") ){
			switch (name) {
			case TagNames.REPRATE:
				UnitsFrequency u=UnitsFrequency.fromString(unitSymbol);
				unit=UnitsFrequencyEnumHandler.getBaseUnit(u);
				break;
			case TagNames.POWER:
				UnitsPower uP=UnitsPower.fromString(unitSymbol);
				unit=UnitsPowerEnumHandler.getBaseUnit(uP);
				break;
			case TagNames.WAVELENGTH:
				UnitsLength uL=UnitsLength.fromString(unitSymbol);
				unit = UnitsLengthEnumHandler.getBaseUnit(uL);
				break;
			case TagNames.WORKDIST:
				UnitsLength uL2=UnitsLength.fromString(unitSymbol);
				unit = UnitsLengthEnumHandler.getBaseUnit(uL2);
				break;
			case TagNames.VOLTAGE:
				UnitsLength uV=UnitsLength.fromString(unitSymbol);
				unit=UnitsLengthEnumHandler.getBaseUnit(uV);
			default:
				LOGGER.warn("[HARDWARE] no unit available for tag "+name);
				break;
			}
				
			
		}
		return unit;
	}

	

}
