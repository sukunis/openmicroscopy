package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.microscope;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import ome.xml.model.Detector;
import ome.xml.model.Filter;
import ome.xml.model.LightPath;
import ome.xml.model.LightSource;
import ome.xml.model.Objective;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.UOSMetadataLogger;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.ModuleConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.TagNames;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.microscope.MetaDataUI.GUIPlaceholder;
import org.slf4j.LoggerFactory;


public class CustomViewProperties 
{
	
	/** Logger for this class. */
	 private static final org.slf4j.Logger LOGGER =
	    	    LoggerFactory.getLogger(CustomViewProperties.class);
    
    private String micName;
    
	static enum MicSubmodule
	{
		IMAGE_DATA,
		OBJECTIVE_DATA,
		DETECTOR_DATA,
		LIGHTSOURCE_DATA,
		CHANNEL_DATA,
		LIGHTPATH_DATA,
		EXPERIMENT_DATA,
		SAMPLE_DATA,
		IMAGEENVIRONMENT_DATA,
		PLANE_DATA
	}
	
//	private List<Submodule> moduleList;
	
	private ModuleConfiguration oConf;
	private ModuleConfiguration detectorConf;
	private ModuleConfiguration lightSrcConf;
	private ModuleConfiguration imageConf;
	private ModuleConfiguration channelConf;
	private ModuleConfiguration lightPathConf;
	private ModuleConfiguration sampleConf;
	private ModuleConfiguration expConf;
	private ModuleConfiguration imgEnvConf;
	private ModuleConfiguration planeConf;
	
	private boolean init;
	
	private List<Objective> micObjList;
	private List<Detector> micDetectorList;
	private List<LightSource> micLightSrcList;
	private List<Filter> micLightPathFilterList;

	private File file;
	
	public CustomViewProperties()
	{
		micName="Unspecified";
		init=false;
//		moduleList=new ArrayList<Submodule>();
		LOGGER.info("*** Load Custom View Properties from file ***");
	}
	
	public void init()
	{
		if(!init)
			loadCommonView();
	}
	

	public void setObjConf(ModuleConfiguration conf)
	{
		oConf=conf;
		init=true;
	}
	public void setImageConf(ModuleConfiguration conf) {
		imageConf=conf;
		init=true;
	}

	public void setChannelConf(ModuleConfiguration conf) {
		channelConf=conf;
		init=true;
	}

	public void setExperimenterConf(ModuleConfiguration conf) {
		expConf = conf;
		init=true;
	}

	public void setDetectorConf(ModuleConfiguration conf) {
		detectorConf=conf;	
		init=true;
	}

	public void setLightPathConf(ModuleConfiguration conf) {
		lightPathConf=conf;	
		init=true;
	}

	public void setLightSrcConf(ModuleConfiguration conf) {
		lightSrcConf=conf;
		init=true;
	}

	public void setSampleConf(ModuleConfiguration conf) {
		sampleConf=conf;	
		init=true;
	}

	public void setPlaneConf(ModuleConfiguration conf) {
		planeConf=conf;	
		init=true;
	}

	public void setImgEnvConf(ModuleConfiguration conf) {
		imgEnvConf=conf;
		init=true;
	}
	
	public ModuleConfiguration getObjConf()
	{
		return oConf;
	}

	public ModuleConfiguration getExpConf() {
		return expConf;
	}


	public ModuleConfiguration getDetectorConf() {
		return detectorConf;
	}

	public ModuleConfiguration getLightSrcConf() {
		return lightSrcConf;
	}

	public ModuleConfiguration getImageConf() {
		return imageConf;
	}

	public ModuleConfiguration getChannelConf() {
		return channelConf;
	}

	public ModuleConfiguration getLightPathConf() {
		return lightPathConf;
	}

	public ModuleConfiguration getSampleConf() {
		return sampleConf;
	}

	public ModuleConfiguration getImgEnvConf() {
		return imgEnvConf;
	}

	public ModuleConfiguration getPlaneConf() {
		return planeConf;
	}


	
	/** TODO: standard view if no profile file is given */
	private void loadCommonView()
	{
		LOGGER.info("[VIEW_PROP] Load general view");
		
		imageConf=new ModuleConfiguration(true,GUIPlaceholder.Pos_A,"1");
		imageConf.setTag(TagNames.IMG_NAME,null,null,null);
		imageConf.setTag(TagNames.ACQTIME,null,null,null);
		imageConf.setTag(TagNames.DIMXY,null,null,null);
		imageConf.setTag(TagNames.PIXELTYPE,null,null,null);
		imageConf.setTag(TagNames.PIXELSIZE,null,null,null);
		imageConf.setTag(TagNames.DIMZTC,null,null,null);
		imageConf.setTag(TagNames.STAGEPOS,null,null,null);
		imageConf.setTag(TagNames.STEPSIZE,null,null,null);
		imageConf.setTag(TagNames.TIMEINC,null,null,null);
		imageConf.setTag(TagNames.WELLNR,null,null,null);
		
		channelConf=new ModuleConfiguration(true,GUIPlaceholder.Pos_E,"1");
		channelConf.setTag(TagNames.CH_NAME,null,null,null);
		channelConf.setTag(TagNames.COLOR,null,null,null);
		channelConf.setTag(TagNames.FLUOROPHORE,null,null,null);
		channelConf.setTag(TagNames.ILLUMTYPE,null,null,null);
		channelConf.setTag(TagNames.EXPOSURETIME,null,TagNames.EMISSIONWL_UNIT.getSymbol(),null);
		channelConf.setTag(TagNames.EXCITWAVELENGTH,null,TagNames.EXCITATIONWL_UNIT.getSymbol(),null);
		channelConf.setTag(TagNames.EMISSIONWAVELENGTH,null,null,null);
		channelConf.setTag(TagNames.IMAGINGMODE,null,null,null);
		channelConf.setTag(TagNames.ILLUMINATIONMODE,null,null,null);
		channelConf.setTag(TagNames.CONTRASTMETHOD,null,null,null);
		channelConf.setTag(TagNames.NDFILTER,null,null,null);
		channelConf.setTag(TagNames.PINHOLESIZE,null,TagNames.PINHOLESIZE_UNIT.getSymbol(),null);
		
		oConf=new ModuleConfiguration(true,GUIPlaceholder.Pos_B,"1");
		oConf.setTag(TagNames.MODEL,null,null,null);
		oConf.setTag(TagNames.MANUFAC,null,null,null);
		oConf.setTag(TagNames.NOMMAGN,null,null,null);
		oConf.setTag(TagNames.CALMAGN,null,null,null);
		oConf.setTag(TagNames.LENSNA,null,null,null);
		oConf.setTag(TagNames.IMMERSION,null,null,null);
		oConf.setTag(TagNames.CORRECTION,null,null,null);
		oConf.setTag(TagNames.WORKDIST,null,TagNames.WORKDIST_UNIT.getSymbol(),null);
		
		oConf.setSettingTag(TagNames.CORCOLLAR,null,null,null);
		oConf.setSettingTag(TagNames.OBJ_MEDIUM,null,null,null);
		oConf.setSettingTag(TagNames.REFINDEX,null,null,null);
		
		detectorConf=new ModuleConfiguration(true,GUIPlaceholder.Pos_C,"1");
		detectorConf.setTag(TagNames.MODEL,null,null,null);
		detectorConf.setTag(TagNames.MANUFAC,null,null,null);
		detectorConf.setTag(TagNames.TYPE,null,null,null);
		detectorConf.setTag(TagNames.ZOOM,null,null,null);
		detectorConf.setTag(TagNames.AMPLGAIN,null,null,null);
		detectorConf.setSettingTag(TagNames.GAIN,null,null,null);
		detectorConf.setSettingTag(TagNames.VOLTAGE,null,TagNames.VOLTAGE_UNIT.getSymbol(),null);
		detectorConf.setSettingTag(TagNames.OFFSET,null,null,null);
		detectorConf.setSettingTag(TagNames.CONFZOOM,null,null,null);
		detectorConf.setSettingTag(TagNames.BINNING,null,null,null);
		detectorConf.setSettingTag(TagNames.SUBARRAY,null,null,null);
		
		expConf=new ModuleConfiguration(true,GUIPlaceholder.Pos_H,"1");
		expConf.setTag(TagNames.TYPE,null,null,null);
		expConf.setTag(TagNames.DESC,null,null,null);
		expConf.setTag(TagNames.EXPNAME,null,null,null);
		expConf.setTag(TagNames.PROJECTNAME,null,null,null);
		expConf.setTag(TagNames.GROUP,null,null,null);
		expConf.setTag(TagNames.PROJECTPARTNER,null,null,null);
		
		imgEnvConf=new ModuleConfiguration(true,GUIPlaceholder.Pos_A,"1");
		imgEnvConf.setTag(TagNames.TEMP,null,null,null);
		imgEnvConf.setTag(TagNames.AIRPRESS,null,null,null);
		imgEnvConf.setTag(TagNames.HUMIDITY,null,null,null);
		imgEnvConf.setTag(TagNames.CO2,null,null,null);
		
		// laser module for lightSrc
		lightSrcConf=new ModuleConfiguration(true,GUIPlaceholder.Pos_D,"1");
		lightSrcConf.setTag(TagNames.MODEL,null,null,null);
		lightSrcConf.setTag(TagNames.MANUFAC,null,null,null);
		lightSrcConf.setTag(TagNames.POWER,null,TagNames.POWER_UNIT.getSymbol(),null);
		lightSrcConf.setTag(TagNames.L_TYPE,null,null,null);
		lightSrcConf.setTag(TagNames.MEDIUM,null,null,null);
		lightSrcConf.setTag(TagNames.FREQMUL,null,null,null);
		lightSrcConf.setTag(TagNames.TUNABLE,null,null,null);
		lightSrcConf.setTag(TagNames.PULSE,null,null,null);
		lightSrcConf.setTag(TagNames.POCKELCELL,null,null,null);
		lightSrcConf.setTag(TagNames.REPRATE,null,TagNames.REPRATE_UNIT_HZ.getSymbol(),null);
		lightSrcConf.setTag(TagNames.PUMP,null,null,null);
		lightSrcConf.setTag(TagNames.WAVELENGTH,null,TagNames.WAVELENGTH_UNIT.getSymbol(),null);
		lightSrcConf.setSettingTag(TagNames.SET_WAVELENGTH,null,null,null);
		lightSrcConf.setSettingTag(TagNames.ATTENUATION,null,null,null);
		
		sampleConf=new ModuleConfiguration(true,GUIPlaceholder.Pos_G,"1");
		sampleConf.setTag(TagNames.PREPDATE,null,null,null);
		sampleConf.setTag(TagNames.PREPDESC,null,null,null);
		sampleConf.setTag(TagNames.RAWCODE,null,null,null);
		sampleConf.setTag(TagNames.RAWDESC,null,null,null);
		sampleConf.setTag(TagNames.GRIDBOXNR,null,null,null);
		sampleConf.setTag(TagNames.GRIDBOXTYPE,null,null,null);
		sampleConf.setTag(TagNames.EXPGRID,null,null,null);
		sampleConf.setTag(TagNames.EXPOBJNR,null,null,null);
		sampleConf.setTag(TagNames.EXPOBJTYPE,null,null,null);
		
		//TODO: plane,
		
	}
	

	public String getMicName() {
		return micName;
	}

	public void setMicName(String micName) {
		this.micName = micName;
	}

	public List<Objective> getMicObjList() {
		return micObjList;
	}

	public void setMicObjList(List<Objective> micObjList) {
		this.micObjList = micObjList;
	}

	public List<Detector> getMicDetectorList() {
		return micDetectorList;
	}

	public void setMicDetectorList(List<Detector> micDetectorList) {
		this.micDetectorList = micDetectorList;
	}

	public List<LightSource> getMicLightSrcList() {
		return micLightSrcList;
	}

	public void setMicLightSrcList(List<LightSource> micLightSrcList) {
		this.micLightSrcList = micLightSrcList;
	}

	public List<Filter> getMicLightPathFilterList() {
		return micLightPathFilterList;
	}
	public void setMicLightPathFilterList(List<Filter> miclightPathFilterList) {
		this.micLightPathFilterList = miclightPathFilterList;
	}

	public void setFile(File file) {
		this.file=file;
	}
	
	public File getFile()
	{
		return file;
	}

	
}
