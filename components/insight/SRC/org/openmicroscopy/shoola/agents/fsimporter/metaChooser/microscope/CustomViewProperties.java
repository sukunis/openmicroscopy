package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.microscope;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import ome.xml.model.Detector;
import ome.xml.model.LightSource;
import ome.xml.model.Objective;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.UOSMetadataLogger;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.ModuleConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.microscope.MetaDataUI.GUIPlaceholder;


public class CustomViewProperties 
{
	public static final String[] MICLIST={"Unspecified","DeltaVision Elite","Leica LSM SP5","Olympus LSM Fluoview",
		"Olympus TIRF 3-Line","Olympus TIRF 4-Line","Olympus TIRF 4-Line LCI","Zeiss Cell Observer SD",
		"Zeiss LSM 510 META NLO","Zeiss TIRF 3"};
	
	/** Logger for this class. */
    private static Logger LOGGER = Logger.getLogger(UOSMetadataLogger.class.getName());
    
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
	
	private List<Submodule> moduleList;
	
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
	
	private List<Objective> micObjList;
	private List<Detector> micDetectorList;
	private List<LightSource> micLightSrcList;
	
	public CustomViewProperties()
	{
		micName="Unspecified";
		moduleList=new ArrayList<Submodule>();
		LOGGER.info("*** Load Custom View Properties from file ***");
	}
	
	public CustomViewProperties(String micName)
	{
		micName="Unspecified";
		moduleList=new ArrayList<Submodule>();
		LOGGER.info("*** Load Custom View Properties ***");
		
		loadView(micName);
	}
	
	
	
	//TODO define views for mics
	private void loadView(String micName) 
	{
		switch (micName) {
		case "Unspecified":
			loadGeneralView();
			break;

		default:
			loadGeneralView();
			break;
		}
		
	}
	
	public void setObjConf(ModuleConfiguration conf)
	{
		oConf=conf;
	}
	public void setImageConf(ModuleConfiguration conf) {
		imageConf=conf;
	}

	public void setChannelConf(ModuleConfiguration conf) {
		channelConf=conf;		
	}

	public void setExperimenterConf(ModuleConfiguration conf) {
		expConf = conf;
	}

	public void setDetectorConf(ModuleConfiguration conf) {
		detectorConf=conf;		
	}

	public void setLightPathConf(ModuleConfiguration conf) {
		lightPathConf=conf;		
	}

	public void setLightSrcConf(ModuleConfiguration conf) {
		lightSrcConf=conf;		
	}

	public void setSampleConf(ModuleConfiguration conf) {
		sampleConf=conf;		
	}

	public void setPlaneConf(ModuleConfiguration conf) {
		planeConf=conf;		
	}

	public void setImgEnvConf(ModuleConfiguration conf) {
		imgEnvConf=conf;		
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

	public void addImageData(GUIPlaceholder pos, String width)
	{
		moduleList.add(new Submodule(MicSubmodule.IMAGE_DATA,pos,Integer.valueOf(width)));
		LOGGER.info("[VIEW_PROP] add image module at "+pos+", "+width);
	}
	
	public void addChannelData(GUIPlaceholder pos, String width)
	{
		moduleList.add(new Submodule(MicSubmodule.CHANNEL_DATA,pos,Integer.valueOf(width)));
		LOGGER.info("[VIEW_PROP] add channel module at "+pos+", "+width);
	}
	public void addObjectiveData(GUIPlaceholder pos, String width)
	{
		moduleList.add(new Submodule(MicSubmodule.OBJECTIVE_DATA,pos,Integer.valueOf(width)));
		LOGGER.info("[VIEW_PROP] add objective module at "+pos+", "+width);
	}
	public void addDetectorData(GUIPlaceholder pos, String width)
	{
		moduleList.add(new Submodule(MicSubmodule.DETECTOR_DATA,pos,Integer.valueOf(width)));
		LOGGER.info("[VIEW_PROP] add detector module at "+pos+", "+width);
	}
	public void addLightSourceData(GUIPlaceholder pos, String width)
	{
		moduleList.add(new Submodule(MicSubmodule.LIGHTSOURCE_DATA,pos,Integer.valueOf(width)));
		LOGGER.info("[VIEW_PROP] add lightSource module at "+pos+", "+width);
	}
	public void addLightPathData(GUIPlaceholder pos, String width)
	{
		moduleList.add(new Submodule(MicSubmodule.LIGHTPATH_DATA,pos,Integer.valueOf(width)));
		LOGGER.info("[VIEW_PROP] add lightPath module at "+pos+", "+width);
	}
	public void addExperimentData(GUIPlaceholder pos, String width)
	{
		moduleList.add(new Submodule(MicSubmodule.EXPERIMENT_DATA,pos,Integer.valueOf(width)));
		LOGGER.info("[VIEW_PROP] add experiment module at "+pos+", "+width);
	}
	public void addSampleData(GUIPlaceholder pos, String width)
	{
		moduleList.add(new Submodule(MicSubmodule.SAMPLE_DATA,pos,Integer.valueOf(width)));
		LOGGER.info("[VIEW_PROP] add sample module at "+pos+", "+width);
	}
	public void addPlaneData(GUIPlaceholder pos, String width)
	{
		moduleList.add(new Submodule(MicSubmodule.PLANE_DATA,pos,Integer.valueOf(width)));
		LOGGER.info("[VIEW_PROP] add plane module at "+pos+", "+width);
	}
	public void addImageEnvData(GUIPlaceholder pos, String width)
	{
		moduleList.add(new Submodule(MicSubmodule.IMAGEENVIRONMENT_DATA,pos,Integer.valueOf(width)));
		LOGGER.info("[VIEW_PROP] add imageEnv module at "+pos+", "+width);
	}
	
	
	public List<Submodule> getModules()
	{
		if(moduleList.isEmpty()){
			loadGeneralView();
		}
		return moduleList;
	}
	
//	private void loadGeneralView()
//	{
//		moduleList.add(new Submodule(MicSubmodule.IMAGE_DATA,GUIPlaceholder.Pos_A,1));
//		moduleList.add(new Submodule(MicSubmodule.IMAGEENVIRONMENT_DATA,GUIPlaceholder.Pos_A,1));
//		moduleList.add(new Submodule(MicSubmodule.PLANE_DATA, GUIPlaceholder.Pos_A, 1));
//		
//		moduleList.add(new Submodule(MicSubmodule.OBJECTIVE_DATA,GUIPlaceholder.Pos_B,1));
//		moduleList.add(new Submodule(MicSubmodule.DETECTOR_DATA,GUIPlaceholder.Pos_C,1));
//		moduleList.add(new Submodule(MicSubmodule.LIGHTSOURCE_DATA,GUIPlaceholder.Pos_D,1));
//		moduleList.add(new Submodule(MicSubmodule.CHANNEL_DATA,GUIPlaceholder.Pos_E,1));
//		moduleList.add(new Submodule(MicSubmodule.LIGHTPATH_DATA,GUIPlaceholder.Pos_F,1));
//		moduleList.add(new Submodule(MicSubmodule.SAMPLE_DATA,GUIPlaceholder.Pos_G,1));
//		moduleList.add(new Submodule(MicSubmodule.EXPERIMENT_DATA,GUIPlaceholder.Pos_H,1));
//	}
	
	private void loadGeneralView()
	{
		LOGGER.info("[VIEW_PROP] Load general view");
		addImageData(GUIPlaceholder.Pos_A,"1");
		addChannelData(GUIPlaceholder.Pos_E,"1");
		addObjectiveData(GUIPlaceholder.Pos_B,"1");
		addDetectorData(GUIPlaceholder.Pos_C,"1");
		addLightSourceData(GUIPlaceholder.Pos_D,"1");
		addLightPathData(GUIPlaceholder.Pos_F,"1");
		addSampleData(GUIPlaceholder.Pos_G,"1");
		addExperimentData(GUIPlaceholder.Pos_H,"1");
		addImageEnvData(GUIPlaceholder.Pos_A,"1");
		addPlaneData(GUIPlaceholder.Pos_A,"1");
		
//		moduleList.add(new Submodule(MicSubmodule.IMAGE_DATA,GUIPlaceholder.Pos_A,1));
//		moduleList.add(new Submodule(MicSubmodule.IMAGEENVIRONMENT_DATA,GUIPlaceholder.Pos_A,1));
//		moduleList.add(new Submodule(MicSubmodule.PLANE_DATA, GUIPlaceholder.Pos_A, 1));
//		
//		moduleList.add(new Submodule(MicSubmodule.OBJECTIVE_DATA,GUIPlaceholder.Pos_B,1));
//		moduleList.add(new Submodule(MicSubmodule.DETECTOR_DATA,GUIPlaceholder.Pos_C,1));
//		moduleList.add(new Submodule(MicSubmodule.LIGHTSOURCE_DATA,GUIPlaceholder.Pos_D,1));
//		moduleList.add(new Submodule(MicSubmodule.CHANNEL_DATA,GUIPlaceholder.Pos_E,1));
//		moduleList.add(new Submodule(MicSubmodule.LIGHTPATH_DATA,GUIPlaceholder.Pos_F,1));
//		moduleList.add(new Submodule(MicSubmodule.SAMPLE_DATA,GUIPlaceholder.Pos_G,1));
//		moduleList.add(new Submodule(MicSubmodule.EXPERIMENT_DATA,GUIPlaceholder.Pos_H,1));
	}
	
	private void loadEMView()
	{
		moduleList.add(new Submodule(MicSubmodule.IMAGE_DATA,GUIPlaceholder.Pos_A,1));
		moduleList.add(new Submodule(MicSubmodule.OBJECTIVE_DATA,GUIPlaceholder.Pos_B,1));
		moduleList.add(new Submodule(MicSubmodule.DETECTOR_DATA,GUIPlaceholder.Pos_C,1));
		moduleList.add(new Submodule(MicSubmodule.LIGHTSOURCE_DATA,GUIPlaceholder.Pos_D,1));
		moduleList.add(new Submodule(MicSubmodule.CHANNEL_DATA,GUIPlaceholder.Pos_E,1));
		moduleList.add(new Submodule(MicSubmodule.SAMPLE_DATA,GUIPlaceholder.Pos_F,2));
		moduleList.add(new Submodule(MicSubmodule.EXPERIMENT_DATA,GUIPlaceholder.Pos_H,1));
	}

	public String getMicName() {
		return micName;
	}

	public void setMicName(String micName) {
		this.micName = micName;
	}

	public List<Objective> getMicObjList() {
		LOGGER.info("[DEBUG] return mic object list of "+micObjList.size());
		return micObjList;
	}

	public void setMicObjList(List<Objective> micObjList) {
		this.micObjList = micObjList;
		LOGGER.info("[DEBUG] set mic object list of "+micObjList.size());
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


	
	
	
}
