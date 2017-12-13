package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.microscope;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.ModuleConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.TagNames;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.microscope.MetaDataUI.GUIPlaceholder;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.microscope.hardware.LeicaLSMSP5;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.microscope.hardware.OlympusLSMFV1000;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.microscope.hardware.ZeissCellObserverSD;

import ome.units.quantity.Length;
import ome.xml.model.Detector;
import ome.xml.model.Filter;
import ome.xml.model.LightSource;
import ome.xml.model.Objective;
import ome.xml.model.TransmittanceRange;
import ome.xml.model.enums.FilterType;
import ome.xml.model.enums.UnitsLength;
import ome.xml.model.enums.handlers.UnitsLengthEnumHandler;

public abstract class MicroscopeProperties 
{
	private final static String FLUOVIEW1000="Olympus LSM FV1000";
	private final static String LEICASP5="Leica LSM SP5";
	private final static String ZEISSSD="Zeiss Cell Observer SD";
	
	
	public static final String[] availableMics={"",FLUOVIEW1000,LEICASP5,ZEISSSD};
	
	protected ModuleConfiguration imageConfiguration;
	protected ModuleConfiguration lightPathConfiguration;
	protected ModuleConfiguration sampleConfiguration;
	protected ModuleConfiguration channelConfiguration;
	protected ModuleConfiguration lightSrcConfiguration;
	protected ModuleConfiguration imgEnvConfiguration;
	protected ModuleConfiguration expConfiguration;
	protected ModuleConfiguration detectorConfiguration;
	protected ModuleConfiguration oConfiguration;
	
	protected List<Detector> detectors;
	protected List<LightSource> lightSources;
	protected List<Objective> objectives;
	protected List<Object> lightPathObjects;
	
	public List<Detector> getDetectorList()
	{
		return detectors;
	}
	
	public List<Objective> getObjectiveList()
	{
		return objectives;
	}
	
	public List<LightSource> getLightSourceList()
	{
		return lightSources;
	}
	
	public List<Object> getLightPathList()
	{
		return lightPathObjects;
	}
	
	protected abstract List<LightSource> getMicLightSrcList();
	protected abstract List<Object> getMicLightPathFilterList();
	protected abstract List<Detector> getMicDetectorList();
	protected abstract List<Objective> getMicObjectiveList();
	
	/**
	 * Return class for hardware specification, if available.
	 * @param micName
	 * @return
	 */
	public static MicroscopeProperties getMicClass(String micName)
	{
		switch(micName){
		case FLUOVIEW1000:
			return new OlympusLSMFV1000();
		case LEICASP5:
			return new LeicaLSMSP5();
		case ZEISSSD:
			return new ZeissCellObserverSD();
		}
		
		return null;
	}
	
	/**
	 * 
	 * @param microscope name
	 * @return index of given microscope in array availableMics
	 */
	public static int getMicIndex(String microscope) {
//		int result=-1;
		return Arrays.asList(availableMics).indexOf(microscope);
//		for(int index=0; index>availableMics.length; index++){
//			if(availableMics[index].equals(microscope)){
//				return index;
//			}
//		}
//		return result;
	}
	
	/**
	 * No predefinitions possible
	 * @param active
	 * @param pos
	 * @param width
	 * @return
	 */
	protected ModuleConfiguration loadImageConf(boolean active,GUIPlaceholder pos,String width)
	{
		ModuleConfiguration imageConf=new ModuleConfiguration(active, pos, width);
		imageConf.setTag(TagNames.IMG_NAME,null,null,true);
		imageConf.setTag(TagNames.IMG_DESC,null,null,true);
		imageConf.setTag(TagNames.ACQTIME,null,null,true);
		imageConf.setTag(TagNames.DIMXY,null,null,true);
		imageConf.setTag(TagNames.PIXELTYPE,null,null,true);
		imageConf.setTag(TagNames.PIXELSIZE,null,null,true);
		imageConf.setTag(TagNames.DIMZTC,null,null,true);
		imageConf.setTag(TagNames.STAGELABEL,null,null,true);
		imageConf.setTag(TagNames.STEPSIZE,null,null,true);
		imageConf.setTag(TagNames.TIMEINC,null,null,true);
		imageConf.setTag(TagNames.WELLNR,null,null,true);
		
		return imageConf;
	}
	
	/**
	 * No predefinitions possible
	 * @param active
	 * @param pos
	 * @param width
	 * @return
	 */
	protected ModuleConfiguration loadLightPathConf(boolean active,GUIPlaceholder pos,String width)
	{
		ModuleConfiguration lightPathConf= new ModuleConfiguration(active,pos,width);
		int index=lightPathConf.addNewElement("Filter",null);
		if(index==-1)
			return lightPathConf;
		lightPathConf.setTag(TagNames.MODEL, null, null, true, index);
		lightPathConf.setTag(TagNames.MANUFAC, null, null, true, index);
		lightPathConf.setTag(TagNames.LP_TYPE, null, null, true, index);
		lightPathConf.setTag(TagNames.FILTERWHEEL, null, null, true, index);
		
		return lightPathConf;
	}

	/**
	 * 
	 */
	protected abstract ModuleConfiguration loadSampleConf(boolean active,GUIPlaceholder pos,String width);
	

	/**
	 * 
	 */
	protected abstract ModuleConfiguration loadLightSrcConf(boolean active,GUIPlaceholder pos,String width);
		

	/**
	 * 
	 */
	protected abstract ModuleConfiguration loadImageEnvConf(boolean active,GUIPlaceholder pos,String width) ;

	/**
	 * 
	 */
	protected abstract ModuleConfiguration loadExperimentConf(boolean active,GUIPlaceholder pos,String width) ;

	/**
	 * 
	 */
	protected abstract ModuleConfiguration loadDetectorConf(boolean active,GUIPlaceholder pos,String width);

	/**
	 * 
	 */
	protected abstract ModuleConfiguration loadObjectiveConf(boolean active,GUIPlaceholder pos,String width) ;

	/**
	 * No predefinitions possible
	 */
	protected ModuleConfiguration loadChannelConf(boolean active,GUIPlaceholder pos,String width) {
		ModuleConfiguration channelConf=new ModuleConfiguration(active,pos,width);
		channelConf.setTag(TagNames.CH_NAME,null,null,true);
		channelConf.setTag(TagNames.COLOR,null,null,true);
		channelConf.setTag(TagNames.FLUOROPHORE,null,null,true);
		channelConf.setTag(TagNames.ILLUMTYPE,null,null,true);
		channelConf.setTag(TagNames.EXPOSURETIME,null,TagNames.EMISSIONWL_UNIT.getSymbol(),true);
		channelConf.setTag(TagNames.EXCITWAVELENGTH,null,TagNames.EXCITATIONWL_UNIT.getSymbol(),true);
		channelConf.setTag(TagNames.EMISSIONWAVELENGTH,null,null,true);
		channelConf.setTag(TagNames.IMAGINGMODE,null,null,true);
		channelConf.setTag(TagNames.ILLUMINATIONMODE,null,null,true);
		channelConf.setTag(TagNames.CONTRASTMETHOD,null,null,true);
		channelConf.setTag(TagNames.NDFILTER,null,null,true);
		channelConf.setTag(TagNames.PINHOLESIZE,null,TagNames.PINHOLESIZE_UNIT.getSymbol(),true);
		return channelConf;
	}

	
	protected Filter getFilter(String model,FilterType type,int transRangeIn,int transRangeOut,UnitsLength unit){
		TransmittanceRange t=new TransmittanceRange();
		if(transRangeIn!=-1){
			t.setCutIn(new Length(transRangeIn, UnitsLengthEnumHandler.getBaseUnit(unit)));
		}
		if(transRangeOut!=-1){
			t.setCutOut(new Length(transRangeOut, UnitsLengthEnumHandler.getBaseUnit(unit)));
		}
	
		
		Filter f= new Filter();
		f.setModel(model);
		f.setType(type);
		f.setTransmittanceRange(t);
		return f;
		
	}
	
	
}
