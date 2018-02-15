package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.microscope;

import info.clearthought.layout.TableLayout;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ome.xml.model.Annotation;
import ome.xml.model.Detector;
import ome.xml.model.DetectorSettings;
import ome.xml.model.Dichroic;
import ome.xml.model.Experiment;
import ome.xml.model.Experimenter;
import ome.xml.model.Filter;
import ome.xml.model.FilterSet;
import ome.xml.model.Image;
import ome.xml.model.ImagingEnvironment;
import ome.xml.model.Instrument;
import ome.xml.model.LightPath;
import ome.xml.model.LightSource;
import ome.xml.model.LightSourceSettings;
import ome.xml.model.MapAnnotation;
import ome.xml.model.OME;
import ome.xml.model.Objective;
import ome.xml.model.ObjectiveSettings;
import ome.xml.model.Pixels;
import ome.xml.model.Plane;
import ome.xml.model.StructuredAnnotations;
import omero.gateway.model.MapAnnotationData;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.MetaDataDialog;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.MetaDataControl;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.MetaDataModel;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.OMEStore;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.format.Sample;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model.ExperimentModel;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model.ImageEnvModel;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model.xml.Channel;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.view.ChannelViewer;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.view.DetectorViewer;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.view.ExperimentViewer;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.view.ImageEnvViewer;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.view.ImageViewer;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.view.LightPathViewer;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.view.LightSourceViewer;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.view.ModuleViewer;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.view.ObjectiveViewer;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.view.SampleViewer;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.ModuleConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.ExceptionDialog;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.ImportUserData;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.MapAnnotationObject;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagData;
import org.openmicroscopy.shoola.util.MonitorAndDebug;
//import org.slf4j.LoggerFactory;
import org.slf4j.LoggerFactory;

/**
 * Works for xsi:schemaLocation="http://www.openmicroscopy.org/Schemas/OME/2015-01 
 * @author Kunis
 *
 */
public class MetaDataUI extends JPanel
{
	public static enum GUIPlaceholder
	{
		Pos_A,
		Pos_B,
		Pos_C,
		Pos_D,
		Pos_E,
		Pos_F,
		Pos_G,
		Pos_H,
		Pos_Bottom
	}
	
	private MetaDataModel model;
	
	private MetaDataControl control;

	private ImportUserData importUserData;
	
	private OME ome;
	
	/** container for viewer **/
	private JTabbedPane experimentPane;
	protected JPanel lightSrcPane2;
	protected JPanel detectorPane2;
	protected JPanel lightPathPane;
	protected JPanel objectivePane;
	protected JTabbedPane channelTab;
	private JTabbedPane imagePane;//tabbed pane with one element
	private JTabbedPane samplePane;
	
	protected JDialog planeDialog;
	
	private JButton loadBtn;
	private JButton saveBtn;
	private JButton saveAllBtn;
	private JButton loadProfileBtn;
	private JButton resetBtn;
	
	GridBagLayout gbl;
	
	/** module viewer */
	private ImageViewer imageUI;
	private ExperimentViewer experimentUI;
	private SampleViewer sampleUI;
	private ObjectiveViewer objectiveUI;
	private ImageEnvViewer imgEnvViewer;
	private LightSourceViewer lightSrcViewer;
	private LightPathViewer lightPathViewer;
	private ChannelViewer channelViewer;
	private DetectorViewer detectorViewer;
	
	
	
	/** true if module is visible*/
	private boolean initChannelUI;
	private boolean initImageUI;
	private boolean initLightPathUI;
	private boolean initLightSrcUI;
	private boolean initDetectorUI;
	private boolean initObjectiveUI;
	private boolean initPlanesUI;
	private boolean initImageEnvUI;
	private boolean initSampleUI;
	private boolean initExperimentUI;
	
	private boolean componentsInit;

	
	private boolean detectorInput;
	private boolean channelInput;
	private boolean lightPathInput;
	private boolean lightSrcInput;
	
	private File file;
	
	private CustomViewProperties customSett;
	private MetaDataDialog parent;
	
	 /** Logger for this class. */
//    private static Logger LOGGER = Logger.getLogger(UOSMetadataLogger.class.getName());
    private static final org.slf4j.Logger LOGGER =
    	    LoggerFactory.getLogger(MetaDataUI.class);

	public static final String CHANGE_IMGDATA = "changeImageData";

	public static final String CHANGE_EXPDATA = "changesExperimentData";

	public static final String CHANGE_SAMPLEDATA = "changesSampleData";

	public static final String CHANGE_OBJDATA = "changesObjectiveData";

	public static final String CHANGE_IMGENVDATA = "changesImgEnvData";

	public static final String CHANGE_DETDATA = "changesDetectorData";

	public static final String CHANGE_LPDATA = "changesLightPathData";

	public static final String CHANGE_LSDATA = "changesLightSrcData";
	
	private boolean directoryPane;

	private int numChannelTabs;

	private ChangeListener channelChangeListener;

	private int lastChannelSelectionIndex;

	private boolean predefinitionsLoaded;

	private boolean showPreValues; 
	
	/**
	 * Constructor
	 * @param parent panel
	 * @param isdir =true if selected node is a directory, else false
	 * @param showPreValues TODO
	 */
	public MetaDataUI(MetaDataDialog parent,boolean isdir, boolean showPreValues)
	{
		MonitorAndDebug.printConsole("# MetaDataUI::new Instance : "+isdir+", "+showPreValues);
		this.setBorder(BorderFactory.createEmptyBorder());
		this.parent=parent;
		customSett=((MetaDataDialog) parent).getCustomViewProperties();
		
		directoryPane=isdir;
		this.showPreValues=showPreValues;
		resetInitialisation();
		
		// create model
		model=new MetaDataModel();
		initModelComponents();
		control=new MetaDataControl(model,this);
					}
					
	/**
	 * Constructor
	 * @param parent panel
	 * @param dir =true if selected node is a directory, else false
	 * @param model
	 * @param showPreValues TODO
	 */
	public MetaDataUI(MetaDataDialog parent,boolean dir,MetaDataModel model, boolean showPreValues)
	{
		MonitorAndDebug.printConsole("# MetaDataUI::new Instance : "+dir+", model,"+showPreValues);
		this.setBorder(BorderFactory.createEmptyBorder());
		customSett=((MetaDataDialog) parent).getCustomViewProperties();
		directoryPane=dir;
		this.showPreValues=showPreValues;
		resetInitialisation();
		
		// create model
		this.model=model;
		// ensure, that a model exists
		if(this.model==null)
			this.model=new MetaDataModel();
			
		
		initModelComponents();
		control=new MetaDataControl(model,this);
	}
	
	private void resetInitialisation()
	{
		componentsInit=false;
		
		initChannelUI=false;
		initImageUI=false;
		initLightPathUI=false;
		initLightSrcUI=false;
		initDetectorUI=false;
		initObjectiveUI=false;
		initPlanesUI=false;
		initImageEnvUI=false;
		initSampleUI=false;
		initExperimentUI=false;
	}
	
	public void resetCustomSettings(CustomViewProperties sett)
	{
		MonitorAndDebug.printConsole("# MetaDataUI::resetCustomSettings()");
		customSett=sett;
		initModelComponents();
	}
	
	/** init modul components respective to settings*/ 
	private void initModelComponents() 
	{
		if(!componentsInit){
			if(customSett.getObjConf()!=null && customSett.getObjConf().isVisible()){
				LOGGER.info("[GUI] -- init OBJECTIVE modul");
				model.addToObjList(customSett.getMicObjList(),false);
				initObjectiveUI=true;
			}
			if(customSett.getDetectorConf()!=null && customSett.getDetectorConf().isVisible()){
				LOGGER.info("[GUI] -- init DETECTOR modul");
				model.addToDetectorList(customSett.getMicDetectorList(), false);
				initDetectorUI=true;
				detectorInput=false;
			}
			if(customSett.getLightSrcConf()!=null && customSett.getLightSrcConf().isVisible()){
				LOGGER.info("[GUI] -- init LIGHTSRC modul");
				model.addToLightSrcList(customSett.getMicLightSrcList(), false);
				initLightSrcUI=true;
			}
			if(customSett.getChannelConf()!=null && customSett.getChannelConf().isVisible()){
				LOGGER.info("[GUI] -- init CHANNEL modul");
				initChannelUI=true;
			}
			if(customSett.getLightPathConf()!=null && customSett.getLightPathConf().isVisible()){
				LOGGER.info("[GUI] -- init LIGHTPATH modul");
				model.addToLightPathList_Filter(customSett.getMicLightPathFilterList(), false);
				initLightPathUI=true;
			}
			if(customSett.getImgEnvConf()!=null && customSett.getImgEnvConf().isVisible()){
				LOGGER.info("[GUI] -- init IMAGEENV modul");
				initImageEnvUI=true;
			}
			if(customSett.getPlaneConf()!=null && customSett.getPlaneConf().isVisible()){
				LOGGER.info("[GUI] -- init PLANE modul");
//				PlaneCompUI pUI=new PlaneCompUI(null);
//				model.addPlaneModul(pUI);
				initPlanesUI=true;
			}
			if(customSett.getExpConf()!=null && customSett.getExpConf().isVisible()){
				LOGGER.info("[GUI] -- init EXPERIMENT modul");
				initExperimentUI=true;
			}
			if(customSett.getSampleConf()!=null && customSett.getSampleConf().isVisible()){
				LOGGER.info("[GUI] -- init SAMPLE modul");
				initSampleUI=true;
			}
			if(customSett.getImageConf()!=null && customSett.getImageConf().isVisible()){
				LOGGER.info("[GUI] -- init IMAGE modul");
				initImageUI=true;
			}

			componentsInit=true;
		}
	}

	/** return model object*/
	public MetaDataModel getModel() throws Exception
	{
		return model;
	}
	/** return model object*/
	public MetaDataModel getSavedModel() throws Exception
	{
		//read input data
		save();
		
		return model;
	}
	

	/** add data from given parent model
	 * @throws Exception */
	public void addData(MetaDataModel parentModel) throws Exception
	{
		if(parentModel==null)
			return;
		
		if(model!=null){
			addExperimentData(parentModel.getExperimentModel(), true);
			
			addImageData(parentModel.getImageData(),true);
			addObjectiveData(parentModel.getObjectiveData(),parentModel.getObjectiveSettings(),true);
			addSampleData(parentModel.getSample(),true);
			addImageEnvData(parentModel.getImgagingEnv(),true);
//			addPlaneData();
			
			if(directoryPane){
				// inheritance for directory
				for(int i=0; i<parentModel.getNumberOfChannels();i++){
					addChannelData(i,parentModel.getChannelData(i),true);
				}
				for(int i=0; i<parentModel.getNumberOfDetectors();i++){
					addDetectorData(i,parentModel.getDetector(i),parentModel.getDetectorSettings(i),true);
				}
				for(int i=0; i<parentModel.getNumberOfLightSrc();i++){
					addLightSrcData(i,parentModel.getLightSourceData(i),parentModel.getLightSourceSettings(i),true);
				}
				for(int i=0; i<parentModel.getNumberOfLightPath();i++){
					addLightPathData(i,parentModel.getLightPath(i),parentModel.getFilterSet(i),true);
				}
				
			}else{
				//inheritance for file
				for(int i=0; i<parentModel.getNumberOfChannels();i++){
					addChannelData(i,parentModel.getChannelData(i),true);
					addLightPathData(i,parentModel.getLightPath(i),parentModel.getFilterSet(i),true);
				}
				for(int i=0; i<parentModel.getNumberOfDetectors();i++){
					addDetectorData(i, parentModel.getDetector(i),parentModel.getDetectorSettings(i), true);
				}

				for(int i=0; i<parentModel.getNumberOfLightSrc(); i++){
					addLightSrcData(i,parentModel.getLightSourceData(i),parentModel.getLightSourceSettings(i),true); 
				}
			}
			
		}
	}
	

	private void addImageEnvData(ImagingEnvironment i, boolean overwrite)
	{
		if(i!=null){
			model.addData(i, overwrite);
		}
	}
	public void addSampleData(Sample s, boolean overwrite) 
	{
		if(s!=null){
			model.addData(s,overwrite);
		}
	}

	private void addObjectiveData(Objective o,ObjectiveSettings os, boolean overwrite) throws Exception 
	{
		if(o!=null) model.addData(o,overwrite);
		if(os!=null) model.addData(os, overwrite);

	}

	private void addLightSrcData(int i, LightSource ls,LightSourceSettings lss, boolean overwrite) throws Exception 
	{
			if(ls!=null) model.addData(ls, overwrite,i); 
			if(lss!=null) model.addData(lss,overwrite,i);
	}

	private void addChannelData(int index, Channel c, boolean overwrite) 
	{
		if(c!=null){
			model.addData(c,overwrite,index);
		}
	}
	
	/** add parent data
	 * @throws Exception */
	private void addDetectorData(int index,Detector d,DetectorSettings ds, boolean overwrite) throws Exception
	{
			if(d!=null){
				model.addData(d,overwrite,index);
			}
			if(ds!=null){
				model.addData(ds, overwrite,index);
			}
	}
	
	private void addLightPathData(int i, LightPath lightPath, FilterSet filterSet, boolean b) throws Exception 
	{
			if(lightPath!=null){
				model.addData(lightPath, b,i);
			}
			if(filterSet!=null){
				model.addData(filterSet, b,i);
	}
	}

	private void addImageData(Image i,boolean overwrite)  
	{
		if( i!=null){
			model.addData(i,overwrite);
		}
	}
	
	public void addExperimentData(ExperimentModel e,boolean overwrite) throws Exception
	{
		if(e!=null){
			model.addData(e, overwrite);
		}
	}
	
	
	/** linke image file */
	public void linkToFile(File file)
	{
		this.file=file;
	}
	
	/** read data from given metadata container 
	 * @param imageIndex TODO*/
	public void readData(OME o, int imageIndex) throws Exception
	{
		if(o !=null)
		{		
			MonitorAndDebug.printConsole("# MetaDataUI::readFileData()...");
				ome=o;
				
				//TODO eigentlich imageList!!!!
				model.setOME(ome);
				model.setImageIndex(imageIndex);
				Image image=ome.getImage(imageIndex);
//				//TODO richtiges project!!
//				if(ome.sizeOfProjectList()>0){
//					Project project=ome.getProject(0);
//				}
				
				if(image!=null){
					//TODO: jedes image hat sein eigenes Model
					model.setImageOMEData(image);
					
					List<Objective> objectives=null;
					List<Detector> detectors=null;
					List<LightSource> lightSources=null;
					List<Filter> filters=null;
					List<Dichroic> dichroics=null;
					List<FilterSet> filterSets=null;
					List<Channel> channels=null;
					List<Plane> planes=null;

					//TODO: no referenced instrument? -> createDummy
					Instrument instrument=image.getLinkedInstrument();
					if(instrument==null){
						LOGGER.warn("[DATA] NO INSTRUMENTS available, create new");
						MonitorAndDebug.printConsole("[DATA] NO INSTRUMENTS available, create new");
						model.createAndLinkNewInstrument(ome); 
					}else{
						objectives=instrument.copyObjectiveList();
						detectors=instrument.copyDetectorList();
						lightSources=instrument.copyLightSourceList();
						filters=instrument.copyFilterList();
						dichroics=instrument.copyDichroicList();
						filterSets=instrument.copyFilterSetList();
						List<Filter> filterList=instrument.copyFilterList();
						model.setFilterList(filterList);
						List<Dichroic> dichroicList=instrument.copyDichroicList();
						model.setDichroicList(dichroicList);
						model.setFilterSetList(filterSets);
					}
					StructuredAnnotations annot=ome.getStructuredAnnotations();

					//TODO: no Pixel data -> dummies ??
					Pixels pixels=image.getPixels();
					if(pixels==null){
						LOGGER.warn("[DATA] NO PIXEL object available");
						MonitorAndDebug.printConsole("[DATA] NO PIXEL object available");
					}else{
						List<ome.xml.model.Channel>ch=pixels.copyChannelList();
						// convert to uos channel type
						if(!ch.isEmpty())
							channels=new ArrayList<>();
						for(int c=0;c<ch.size(); c++ ){
							channels.add(new Channel(ch.get(c)));
						}
						// Java 8:
//						channels=ch.stream().map(e -> (Channel) e).collect(Collectors.toList());
						planes=pixels.copyPlaneList();
					}
					
					
					
					if(componentsInit){
						readImageData(image,objectives,annot);
						readChannelData(channels,lightSources,detectors,filters,dichroics);
						
						readPlaneData(planes);
						readImageEnvData(image);
						readExperimentData(image);
						
						model.addToLightSrcList(lightSources,true);
						model.addToDetectorList(detectors,true);
						model.addToObjList(objectives,true);
						model.addToLightPathList_Filter(filters,true);
						model.addToLightPathList_Dichroic(dichroics,true);
						model.addToLightPathList_FilterSet(filterSets, true);
					}
				}else{
					LOGGER.warn("[DATA] NO IMAGE object available");
					MonitorAndDebug.printConsole("[DATA] NO IMAGE object available");
				}
				MonitorAndDebug.printConsole("... end loadFileData()");
			
		}else{
			LOGGER.warn("[DATA] NOT available METADATA ");
			model.setImageOMEData(null);
		}
	}
	
	

	private void readPlaneData(List<Plane> planes) throws Exception
	{
//		if(initPlanesUI && planes!=null && !planes.isEmpty())
//		{
//				for(int i=0; i<planes.size(); i++){
//					PlaneCompUI pUI;
//					if(i<model.getNumberOfPlanes()){
//						pUI=model.getPlaneModul(i);
//						pUI.addData(planes.get(i));
//					}else{
//						pUI = new PlaneCompUI(planes.get(i));
//						model.addPlaneModul(pUI);
//					}
//					
//				}
//				LOGGER.info("[DATA] -- load PLANE ("+planes.size()+")");
//			
//		}
	}

	private void readImageEnvData(Image image) 
	{
		if(initImageEnvUI){
			ImagingEnvironment i=image.getImagingEnvironment();
			if(i!=null){
				model.addData(i,false);
			}
		}
	}

	
	// TODO: read project Partner from Map annotation
	private void readExperimentData(Image image) 
	{
		if(initExperimentUI)
		{
			// load image linked experiment and experimenter
			Experiment e=image.getLinkedExperiment();
			Experimenter exper=image.getLinkedExperimenter();
			ExperimentModel expModel = model.getExperimentModel();
			String pP=null;
			
			MapAnnotation map=getLinkedCellNanOsAnnotation(image, ome.getStructuredAnnotations());
			if(map!=null){
				pP=expModel.parseProjectPartner(map); 
			}
			
			ExperimentModel expCont=new ExperimentModel(e,exper,pP);
			
			try {
				model.addData(expCont, false);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			
		}
	}

	private void readChannelData(List<Channel> channels,
			List<LightSource> lightSources, List<Detector> detectors, 
			List<Filter> filters, List<Dichroic> dichroics) 
	{
		if(initChannelUI)
		{
			for(int i=0; i<channels.size();i++){
				if(channels.get(i)!=null)
				{
					model.addData(channels.get(i), false,i);
					LOGGER.info("[DATA] -- load CHANNEL data "+channels.get(i).getName());

					try {
						readLightPathData(channels.get(i),i,filters,dichroics);
					} catch (Exception e2) {
						LOGGER.warn("Can't read lightpath data of channel "+i+"! "+e2);
						e2.printStackTrace();
					}
					try {
						readLightSource(channels.get(i),i,lightSources);
					} catch (Exception e1) {
						LOGGER.warn("Can't read lightSrc data of channel "+i+"! "+e1);
						e1.printStackTrace();
					}
					try {
						readDetectorData(channels.get(i),i,detectors);
					} catch (Exception e) {
						LOGGER.warn("Can't read detector data of channel "+i+"! "+e);
						e.printStackTrace();
					}
				}
			}
		}else{
			try {
				readLightPathData(channels.get(0),0,filters,dichroics);
			} catch (Exception e2) {
				LOGGER.warn("Can't read lightpath data of channel 0!"+e2);
				e2.printStackTrace();
			}
			try {
				readLightSource(channels.get(0),0,lightSources);
			} catch (Exception e1) {
				LOGGER.warn("Can't read lightSrc data of channel 0! "+e1);
				e1.printStackTrace();
			}
			try {
				readDetectorData(channels.get(0),0,detectors);
			} catch (Exception e) {
				LOGGER.warn("Can't read detector data of channel 0! "+e);
				e.printStackTrace();
			}
		}
			
	}
	
	


	
	private void readLightPathData(Channel channel, int i, List<Filter> filters, List<Dichroic> dichroics) throws Exception 
	{
		if(initLightPathUI){
			if((filters!=null && !filters.isEmpty()) || (dichroics!=null && !dichroics.isEmpty()))
			{
				boolean dataAvailable=false;
				String[] linkedObj=null;
				
				// get linked lightpath for current channel
				LightPath lp=channel.getLightPath();

				if(lp!=null &&(
						lp.sizeOfLinkedEmissionFilterList()!=0 || 
						lp.sizeOfLinkedExcitationFilterList()!=0 ||
						lp.getLinkedDichroic()!=null)){
					dataAvailable=true;
				}


				if(!dataAvailable){
					LOGGER.info("[DATA] -- LIGHTPATH  data not available");
				}else{
					model.addData(lp,false,i);
				}
			}else{
				LOGGER.info("[DATA] -- LIGHTPATH  data not available");
			}
		}
	}

	private void readLightSource(Channel channel, int i,
			List<LightSource> lightSources) throws Exception 
	{
		if(initLightSrcUI){
			
			if(	lightSources!=null && !lightSources.isEmpty())
			{
				boolean dataAvailable=false;
				String linkedObj=null;
				
				// get linked lightSrc for current channel
				LightSourceSettings ls=channel.getLightSourceSettings();
				LightSource l=null;

				// get linked lightSource from lightSrc list
				if(ls!=null){
					linkedObj=ls.getID();
					int idx=getLightSrcByID(lightSources, linkedObj);
					if(idx!=-1){
						l=lightSources.get(idx);
						dataAvailable=true;
					}
				}else{
					// if only one lightSrc in instruments available, show this
					if(lightSources.size()==1){
						l=lightSources.get(0);
						dataAvailable=true;
					}else{
						LOGGER.info("[DATA] -- more than one unlinked lightSrc available");
					}
				}

				// visualization data
					if(!dataAvailable){
						LOGGER.info("[DATA] -- LIGHTSOURCE  data not available");
					}else{
						model.addData(l,false,i);
						model.addData(ls,false,i);
					}
			}
			else{
				LOGGER.info("[DATA] -- LIGHTSOURCE  data not available");
			}
			
		}
	}

	private void readDetectorData(Channel channel, int i,
			List<Detector> detectors) throws Exception 
	{
		if(initDetectorUI){
			if(detectors!=null && !detectors.isEmpty())
			{
				boolean dDataAvailable=false;
				String linkedDet=null;
				
				DetectorSettings ds=channel.getDetectorSettings();
				Detector d=null;

				// get linked detector from list
				if(ds!=null){
					linkedDet=ds.getID();
					int idx=getDetectorByID(detectors, linkedDet);
					if(idx!=-1){
						d=detectors.get(idx);
						dDataAvailable=true;
					}
				}else{
					// if only one detector in instruments available, show this
					if(detectors.size()==1){
						d=detectors.get(0);
						dDataAvailable=true;
					}else{
						LOGGER.info("[DATA] -- more than one unlinked detectors available");
					}
				}


				if(!dDataAvailable){
					LOGGER.info("[DATA] -- DETECTOR data not available");
				}else{
					model.addData(d,false,i);
					model.addData(ds,false,i);
				}
			}else{
				LOGGER.info("[DATA] -- DETECTOR data not available");
			}
		}
	}

	private void readImageData(Image image, List<Objective> objList, StructuredAnnotations annot) 
	{
			model.addData(image, false);
			try{
				readObjectiveData(image,objList);
			}catch(Exception e){
				LOGGER.warn("Can't read objective data! "+e);
			}
			readSampleData(image,annot);
		
	}

	private void readSampleData(Image image, StructuredAnnotations annot) 
	{
		if(initSampleUI && annot!=null){
			Sample s=getSampleAnnotation(image,annot);
			if(s!=null){
				model.addData(s, false);
			}else{
				LOGGER.info("[DATA] Sample data not available!");
			}
		}
	}

	/**
	 * Read sample data from *.ome file. Sample data should be stored as MapAnnotation of id <MAP_ANNOT_ID>
	 * and linked o current image.
	 * @param image
	 * @param annot
	 * @return
	 */
	private Sample getSampleAnnotation(Image image,StructuredAnnotations annot) 
	{
		MapAnnotation map=getLinkedCellNanOsAnnotation(image, annot);
		Sample s=null;
		if(map!=null)
			s= new Sample(map);
		
		return s;
	}
	
	private MapAnnotation getLinkedCellNanOsAnnotation(Image image, StructuredAnnotations annot)
	{
		MapAnnotation map =null;
		List<Annotation> list=image.copyLinkedAnnotationList();
		String sampleID=null;
		for(int i=0; i<list.size(); i++){
			if(image.getLinkedAnnotation(i).getID().contains(OMEStore.MAP_ANNOT_ID))
				sampleID= image.getLinkedAnnotation(i).getID();
		}
		if(sampleID!=null){
			for(int i=0; i<annot.sizeOfMapAnnotationList();i++){
				if(sampleID.equals(annot.getMapAnnotation(i).getID())){
					map= annot.getMapAnnotation(i);
				}
			}
		}
		return map;
	}

	private void readObjectiveData(Image image, List<Objective> objList) throws Exception
	{
		
		if(initObjectiveUI && objList!=null && !objList.isEmpty())
		{
			boolean oDataAvailable=false;
			String linkedObj=null;
			
			
			ObjectiveSettings os=image.getObjectiveSettings();
			Objective o=null;
			
			// get linked object from objList
			if(os!=null){
				linkedObj=os.getID();
				int idx=getObjectiveByID(objList, linkedObj);
				if(idx!=-1){
					o=objList.get(idx);
					oDataAvailable=true;
				}
			}else{
				// if only one objective in instruments available, show this
				if(objList.size()==1){
					o=objList.get(0);
					oDataAvailable=true;
				}else{
					LOGGER.info("[DATA] -- more than one unlinked objectives available");
				}
			}
			
			if(!oDataAvailable){
				LOGGER.info("[DATA] -- file: OBJECTIVE data not available");
			}else{
				model.addData(o, false); 
				model.addData(os,false); 
			}
		}
		
	}

	/** set ImportUserData */
	public void setImportData(ImportUserData data)
	{
		if(data!=null){
			MonitorAndDebug.printConsole("# MetaDataUI::setImportData()");
			LOGGER.info("[DATA] -- add IMPORT USER data");
			importUserData=data;
			try {
				ExperimentModel expCont=new ExperimentModel();
				expCont.setExperimenter(importUserData.getUser());
				expCont.setGroupName(importUserData.getGroup());
				expCont.setProjectName(importUserData.getProject());
				model.setExtendedData(expCont);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
	
	public void showData() throws Exception
	{
		initLayout();
		
		showExperimentData();
		showImageData();
		showChannelData();
		showSampleData();
		
		revalidate();
		repaint();
	}


	private void showExperimentData() {
		if(initExperimentUI){
			experimentPane=new JTabbedPane();
			ModuleConfiguration expModul=customSett.getExpConf();
			experimentUI=new ExperimentViewer(model.getExperimentModel(), expModul,showPreValues);
			
			JPanel all=new JPanel();
			all.setLayout(new BoxLayout(all,BoxLayout.Y_AXIS));
			all.add(experimentUI);
			
			ModuleConfiguration imgEnvModul=customSett.getImgEnvConf();
			if(imgEnvModul!=null){
				imgEnvViewer=new ImageEnvViewer(model.getImgEnvModel(), imgEnvModul,showPreValues);
				all.add(imgEnvViewer);
			}
			experimentPane.add("Experiment",all);
			addToPlaceholder(experimentPane,expModul.getPosition(), expModul.getWidth());
		}
	}
	
	
	
	private void showImageData() throws Exception
	{
		imagePane=new JTabbedPane();
		if(initImageUI){
			ModuleConfiguration imgModul =customSett.getImageConf();
			imageUI=new ImageViewer(model.getImageModel(), imgModul);
			imagePane.add("Image",imageUI);
			JComponent img=imagePane;

			//TODO position and width from file
			if(initPlanesUI ){
				////			JScrollPane spImage =new GBScrollPane(imageUI);
				img=new JPanel();
				GridBagLayout myGBL=new GridBagLayout();
				img.setLayout(myGBL);
				addComponent(img,myGBL,imagePane,0,0,1,3,1.0,1.0,GridBagConstraints.BOTH);
				
				if(initPlanesUI)
					addComponent(img,myGBL,initPlaneBtn(),0,4,1,1,1.0,0,GridBagConstraints.HORIZONTAL);
			}
			addToPlaceholder(img, imgModul.getPosition(), imgModul.getWidth());
		}
		String name=model.getImageData()!=null ? model.getImageData().getName() : null;

		
		showObjectiveData(name);
		
	}

	private void showObjectiveData(String name) 
	{
		if(initObjectiveUI){
			objectivePane=new JPanel(new CardLayout());
			ModuleConfiguration objModul=customSett.getObjConf();
			objectiveUI=new ObjectiveViewer(model.getObjectiveModel(), objModul,showPreValues,model.getObjList(),parent);
			
			JPanel pane= control.createPropPane(objectiveUI, "Objective", "for image "+name);
			objectivePane.add(pane,name);
			addToPlaceholder(objectivePane, objModul.getPosition(), objModul.getWidth());
		}
	}
	
	private void showSampleData()
	{
		if(initSampleUI){
			samplePane=new JTabbedPane();
			ModuleConfiguration sampleModul=customSett.getSampleConf();
			sampleUI=new SampleViewer(model.getSampleModel(), sampleModul,showPreValues);
			samplePane.add("Sample",sampleUI);
			addToPlaceholder(samplePane, sampleModul.getPosition(), sampleModul.getWidth());
		}
	}

	private void showChannelData() throws Exception 
	{
		channelTab=new JTabbedPane();
		numChannelTabs=0;
		lastChannelSelectionIndex=0;
		channelChangeListener=new ChangeListener() {
			public void stateChanged(ChangeEvent changeEvent) {
				JTabbedPane sourceTabbedPane = (JTabbedPane) changeEvent.getSource();
				int index = sourceTabbedPane.getSelectedIndex();
				if(directoryPane && index==(numChannelTabs-1)){
					addNewChannelTab();
				}else{
					if(model.getNumberOfChannels()>0){
						if(index >=0){
							selectChannel(index);
						}
					}
				}
			}
		};
		
		
		if(initChannelUI){
			ModuleConfiguration chModul=customSett.getChannelConf();
			if(model.getNumberOfChannels()==0){
				channelTab.add(new ChannelViewer(model.getChannelModel(), chModul, 0),"Channel 0",numChannelTabs++);
			}else{
				for(int i=0; i<model.getNumberOfChannels();i++)
				{
					String name= model.getChannelData(i)!=null ? model.getChannelData(i).getName() : "Channel";
					if(directoryPane){
						name= (name==null || name.isEmpty()) ? "Channel "+String.valueOf(i) : name;
					}else{
						name= (name==null || name.isEmpty()) ? ((i==0 ? "Channel " : "# ")+String.valueOf(i)) : name;
					}
					channelTab.add(new ChannelViewer(model.getChannelModel(), chModul, i),name,numChannelTabs++);
				}
				// set last channel as removable
				if(directoryPane && numChannelTabs>2){
					channelTab.setTabComponentAt(numChannelTabs-2, new DemoCustomTab(channelTab));
				}
			}
			
			
			
			if(directoryPane){
				// add tab to add new tab when click
				channelTab.add(new JPanel(),"+",numChannelTabs++);
			}
			
			addToPlaceholder(channelTab,chModul.getPosition(), chModul.getWidth()); 
		}
		
		String chname= model.getChannelData(0)!=null ? model.getChannelData(0).getName() : "Channel 0";
		showLightSourceData(chname,0);
		showDetectorData(chname,0);
		showLightPathData(chname,0);
		
		channelTab.addChangeListener(channelChangeListener);
	}
	
	/**
	 * Add new ChannelTab for directory view. Add also new Detector, LightSrc and LightPath pane.
	 */
	private void addNewChannelTab()
	{
		MonitorAndDebug.printConsole("# MetaDataUI::addNewChannelTab()");
		 int index = numChannelTabs - 1;
	        if (channelTab.getSelectedIndex() == index) { /* if click new tab */
	        	// add new channel model
	        	model.getChannelModel().addData(null, true, index);
	        	channelTab.removeChangeListener(channelChangeListener);
	            /* add new tab */
	        	channelTab.add(new ChannelViewer(model.getChannelModel(), customSett.getChannelConf(), index),
	        			"Channel " + String.valueOf(index), index);
	            /* set last channel tab as custom tab and remove delete sign from tab before*/
	        	if(directoryPane){
	        		if(index>1){
	        			DemoCustomTab lastTab=(DemoCustomTab) channelTab.getComponentAt(index-1);
	        			if(lastTab!=null) lastTab.hideRemove();
	        		}
	        		channelTab.setTabComponentAt(index, new DemoCustomTab(channelTab));
	        	}
	        	
	        	selectChannel(index);
//	        	channelTab.setSelectedIndex(index);
	        	channelTab.addChangeListener(channelChangeListener);
	        	numChannelTabs++;
	        }
	}

	private void setLightPathVisible(String name, int index) 
	{
		if(lightPathPane==null){
			showLightPathData(name,index);
		}else{
			lightPathPane.removeAll();
			lightPathViewer=new LightPathViewer(model.getLightPathModel(),customSett.getLightPathConf(),
					index,model.getHardwareList_LightPath(),parent);
			lightPathPane.add(control.createPropPane(lightPathViewer, "LightPath", "for "+name));
		}
	}

	private void setDetectorVisible(String name, int index)
	{
		if(detectorPane2==null){
			MonitorAndDebug.printConsole("\t...Show Detector for Channel "+name);
			showDetectorData(name,index);
		}else{
			MonitorAndDebug.printConsole("\t...Set visible Detector for Channel "+name);
			detectorPane2.removeAll();
			detectorViewer=new DetectorViewer(model.getDetectorModel(),customSett.getDetectorConf(),
					index,showPreValues,model.getAvailableDetectorsImgData(),parent);
			detectorPane2.add(control.createPropPane(detectorViewer, "Detector", "for "+ name));
		}
	}
	
	private void setLightSrcVisible(String name,int index)
	{
		if(lightSrcPane2==null){
			showLightSourceData(name,index);
		}else{
			lightSrcPane2.removeAll();
			ModuleConfiguration lightSModul=customSett.getLightSrcConf();
			lightSrcViewer=new LightSourceViewer(model.getLightSourceModel(), lightSModul, 
					index,showPreValues,model.getLightSrcHardwareList(),parent);
			lightSrcPane2.add(control.createPropPane(lightSrcViewer, "LightSource", "for "+name));
		}
	}
	
	private void showLightPathData(String name,int index)
	{
		if(initLightPathUI){
			lightPathPane=new JPanel(new CardLayout());
			ModuleConfiguration lightPModul=customSett.getLightPathConf();
			lightPathViewer=new LightPathViewer(model.getLightPathModel(), customSett.getLightPathConf(),
					index,model.getHardwareList_LightPath(),parent);
			lightPathPane.add(control.createPropPane(lightPathViewer, "LightPath", "for "+name));
			addToPlaceholder(lightPathPane, lightPModul.getPosition(), lightPModul.getWidth());
		}
	}
	private void showLightSourceData(String name,int index) 
	{
		if(initLightSrcUI){
			lightSrcPane2=new JPanel(new CardLayout());
			ModuleConfiguration lightSModul=customSett.getLightSrcConf();
			lightSrcViewer=new LightSourceViewer(model.getLightSourceModel(), customSett.getLightSrcConf(),
					index,showPreValues,model.getLightSrcHardwareList(),parent);
			lightSrcPane2.add(control.createPropPane(lightSrcViewer, "LightSource", "for "+name));
			addToPlaceholder(lightSrcPane2, lightSModul.getPosition(), lightSModul.getWidth());
		}
	}

	private void showDetectorData(String name,int index) 
	{
		if(initDetectorUI){
			detectorPane2=new JPanel(new CardLayout());
			ModuleConfiguration detModul=customSett.getDetectorConf();
			detectorViewer=new DetectorViewer(model.getDetectorModel(),customSett.getDetectorConf(),
					index,showPreValues,model.getAvailableDetectorsImgData(),parent);
			detectorPane2.add(control.createPropPane(detectorViewer, "Detector", "for "+ name));
			addToPlaceholder(detectorPane2, detModul.getPosition(), detModul.getWidth());
		}
	}
	
	/**
	 * Shows/brings on top channel number chNr and his linked lightSrc, detector and lightPath.
	 * @param chNr
	 */
	protected void selectChannel(int chNr) 
	{	
		MonitorAndDebug.printConsole("# MetaDataUI::selectChannel() : old = "+lastChannelSelectionIndex+", new = "+chNr);
		if(model.getNumberOfChannels() <chNr )
			return;
		
		// something to save at channel?
		ChannelViewer lastSelection=null;
		if( channelTab.getComponentAt(lastChannelSelectionIndex) instanceof ChannelViewer){
			lastSelection=(ChannelViewer) channelTab.getComponentAt(lastChannelSelectionIndex);
		}
		if(lastSelection!=null && lastSelection.hasDataToSave()){
			List<TagData> list=lastSelection.getChangedTags();
			lastSelection.saveData();
			model.setChangesChannel(list, lastSelection.getIndex());
			model.setMapAnnotationChannel(lastSelection.getMapValuesOfChanges(model.getMapAnnotationChannel(lastSelection.getIndex())),lastSelection.getIndex(), true); 
		}
		
		channelTab.setSelectedIndex(chNr);
		String chName=channelTab.getTitleAt(chNr);
		
		LOGGER.info("[GUI-ACTION] -- select Channel "+chName);
		MonitorAndDebug.printConsole("\t...select Channel "+chName);
		
		// update submodules
		if(initLightSrcUI ){
			boolean input=lightSrcViewer.hasDataToSave();
			int elemIndex=lightSrcViewer.getIndex();
			if(input){
				lightSrcInput=true;
				List<TagData> list=lightSrcViewer.getChangedTags();
				lightSrcViewer.saveData();
				model.setChangesLightSrc(list,elemIndex);
				model.setMapAnnotationLightSrc(
						lightSrcViewer.getMapValuesOfChanges(model.getMapAnnotationLightSrc(elemIndex),lastSelection.getName()), elemIndex, true); 
			}
			// show referenced lightSrc + settings
			setLightSrcVisible(chName, chNr);
			MonitorAndDebug.printConsole("\t...select lightSrc "+chNr+" of "+model.getNumberOfLightSrc());
		}

		if(initDetectorUI ){
			boolean input=detectorViewer.hasDataToSave();
			if(input){
				detectorInput=true;
				int elemIndex=detectorViewer.getIndex();
				List<TagData> list=detectorViewer.getChangedTags();
				HashMap<String,String> map=model.getMapAnnotationDetector(elemIndex);
				detectorViewer.saveData();
				model.setChangesDetector(list,elemIndex);
				model.setMapAnnotationDetector(detectorViewer.getMapValuesOfChanges(map,lastSelection.getName()),elemIndex, true);
			}
			// show referenced detector + settings
			setDetectorVisible(chName, chNr);
		MonitorAndDebug.printConsole("\t...select detector "+chNr+" of "+model.getNumberOfDetectors());
		}

		if(initLightPathUI ){
			boolean input=lightPathViewer.hasDataToSave();
			if(input){
				lightPathInput=true;
				int index=lightPathViewer.getIndex();
				try {
					model.setChangesLightPath(model.getLightPath(index),index);
					model.setMapAnnotationLightPath(
							lightPathViewer.getMapValuesOfChanges(model.getMapAnnotationLightPath(index),lastSelection.getName()),index, true);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				lightPathViewer.saveData();
			}
			//update lightPath
			setLightPathVisible(chName, chNr);
			MonitorAndDebug.printConsole("\t...select lightPath "+chNr+" of "+model.getNumberOfLightPath());
		}
		revalidate();
		repaint();

		lastChannelSelectionIndex=chNr;
	}
	

	private void initLayout()
	{
		//layout
//		gbl = new GridBagLayout();
//		setLayout( gbl );
//
//		gbl.columnWidths = new int[] {50, 50,50,50};
//		
//		double[][] layoutDesign=new double[][]{
//				//X-achse
//				{0.25,0.25,0.25,TableLayout.FILL},
//				//Y-Achse
//				{0.5,0.5}
//		};
		double[][] layoutDesign=new double[][]{
				//X-achse
				{TableLayout.FILL},
				//Y-Achse
//				{0.5,0.5}
				{TableLayout.FILL}
		};
		TableLayout layout=new TableLayout(layoutDesign);
	
//		GridLayout layout=new GridLayout(4,4);
		setLayout(layout);
	}
	


	protected JPanel getButtonPane()
	{
		JPanel buttonPane = new JPanel();
		GridBagLayout layout=new GridBagLayout();
		buttonPane.setLayout(layout);
		Box buttonBox = Box.createHorizontalBox();
		buttonBox.add(resetBtn);
		buttonBox.add(Box.createHorizontalGlue());
		buttonBox.add(loadProfileBtn);
		buttonBox.add(Box.createHorizontalStrut(40));
		buttonBox.add(loadBtn);
		buttonBox.add(Box.createHorizontalStrut(20));
		buttonBox.add(saveBtn);
		buttonBox.add(saveAllBtn);
		addComponent(buttonPane, layout, buttonBox, 2, 4, 1, 1, 0.0, 0.0, GridBagConstraints.NONE);

		return buttonPane;
	}
	
	static void addComponent( Container cont,
			GridBagLayout layout,
			JComponent c,
			int x, int y,
			int width, int height,
			double weightx, double weighty,int fill )
	{
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor=GridBagConstraints.NORTHWEST;
		gbc.fill = fill;
		gbc.gridx = x; gbc.gridy = y;
		gbc.gridwidth = width; gbc.gridheight = height;
		gbc.weightx = weightx; gbc.weighty = weighty;
		layout.setConstraints( c, gbc );
		cont.add( c );
	}
	
	

	
	
	private void addToPlaceholder(JComponent comp1,GUIPlaceholder place, int width )
	{
		JScrollPane comp=new JScrollPane(comp1);
		TableLayout layout=(TableLayout) getLayout();
		switch (place) {
		case Pos_A:
			add(comp,"0,0");
			break;
		case Pos_B:
			insertModule(0,1,comp);
			break;
		case Pos_C:
			insertModule(0,2,comp);
			break;
		case Pos_D:
			insertModule(0,3,comp);
			break;
			//----------------------------------------------
		case Pos_E:
			insertModule(1,0,comp);
			break;
		case Pos_F:
			insertModule(1,1,comp);
			break;
		case Pos_G:
			insertModule(1,2,comp);
			break;
		case Pos_H:
			insertModule(1,3,comp);
			break;
		default:
			LOGGER.error("[GUI] Unknown position for element");
			ExceptionDialog ld = new ExceptionDialog("Property File Error!", 
					"Use unknown position ["+place+"] in given property file.",
					this.getClass().getSimpleName());
			ld.setVisible(true);
			break;
		}
	}

	private void insertModule(int rowIdx, int columnIdx, JComponent comp)
	{
		LOGGER.info("Insert Module at col: "+columnIdx+", row: "+rowIdx);
//		MonitorAndDebug.printConsole("Insert Module at col: "+columnIdx+", row: "+rowIdx);
		TableLayout layout=(TableLayout) getLayout();
		//expand rows?
		int numRow=layout.getNumRow();
		for(int i=numRow;i<rowIdx+1;i++ ){
			layout.insertRow(i, TableLayout.FILL);
		}
		
		int numCol=layout.getNumColumn();
		for(int i=numCol; i<columnIdx+1; i++){
			layout.insertColumn(i, TableLayout.FILL);
		}
		add(comp,columnIdx+","+rowIdx);
	}


	protected int getObjectiveByID(List<Objective> list,String id)
	{
		int result=-1;
		if(id==null || id.equals("") || list==null)
			return result;
		for(int i=0; i<list.size(); i++){
			if(list.get(i).getID().equals(id)){
				return i;
			}
		}
		return result;
	}

	protected int getLightSrcByID(List<LightSource> list,String id)
	{
		int result=-1;
		if(id==null || id.equals("") || list==null)
			return result;
		for(int i=0; i<list.size(); i++){
			if(list.get(i).getID().equals(id)){
				return i;
			}
		}
		return result;
	}
	protected int getDetectorByID(List<Detector> list,String id)
	{
		int result=-1;
		if(id==null || id.equals("") || list==null)
			return result;
		for(int i=0; i<list.size(); i++){
			if(list.get(i).getID().equals(id)){
				return i;
			}
		}
		return result;
	}


	/**
	 * @return
	 */
	protected JButton initPlaneBtn() 
	{
		JButton btnPlanePos=new JButton("Plane/Stage Positions");
//		btnPlanePos.setEnabled(false);
//		btnPlanePos.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				PlaneSliderCompUI plane=null;
//				if(planeDialog==null){
//					if(model.getNumberOfPlanes()>0){
//						plane=new PlaneSliderCompUI(model.getPlaneModulList(), 
//								model.getPixelsDimT(),model.getPixelsDimZ(),model.getPixelsDimC());
//						//Tab of planes
//						planeDialog=createPlaneDialog(plane,"Plane/Stage Positions",400,800);
//						
//					}
//				}
//				planeDialog.setVisible(true);
//			}
//		});
		return btnPlanePos;
	}

	
	
	
	protected JDialog createPlaneDialog(JComponent p,String title, int width, int height)
	{
		JDialog d=new JDialog();
		d.setTitle(title);
		d.setSize(width,height);
		d.setModal(true);
		d.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
//		centerOnParent(d, true);
		d.add(p);
		d.pack();
//		d.setVisible(true);
		return d;
	}
	
	protected JDialog createSaveDialog(JComponent p,String title, int width, int height)
	{
		JDialog d=new JDialog();
		d.setTitle(title);
		d.setSize(width,height);
		d.setModal(true);
		d.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
//		centerOnParent(d, true);
		d.add(p);
		d.setVisible(true);
		d.setResizable(false);
		return d;
	}


	/**
	 * Notice user gui input
	 * @return
	 */
	public boolean userInput()
	{
		MonitorAndDebug.printConsole("# MetaDataUI::userInput()");
		boolean result=false;
		if(initImageUI && imageUI!=null){
			MonitorAndDebug.printConsole("\t ... image : changed data - "+imageUI.inputEvent());
			result=result || imageUI.inputEvent();
		}
		if(initExperimentUI && experimentUI!=null){
			MonitorAndDebug.printConsole("\t ... experiment : changed data - "+experimentUI.inputEvent());
			result=result || experimentUI.inputEvent();
		}
		if(initObjectiveUI && objectiveUI!=null){
			MonitorAndDebug.printConsole("\t ... objective : changed data - "+objectiveUI.inputEvent());
			result=result || objectiveUI.inputEvent();
		}
		if(initSampleUI && sampleUI!=null){
			MonitorAndDebug.printConsole("\t ... sample : changed data - "+sampleUI.inputEvent());
			result=result || sampleUI.inputEvent();
		}
		if(initImageEnvUI && imgEnvViewer!=null){
			MonitorAndDebug.printConsole("\t ... imgEnv : changed data - "+imgEnvViewer.inputEvent());
			result=result || imgEnvViewer.inputEvent();
		}
		
		if(initChannelUI  && model.getNumberOfChannels()>0){
			if(channelTab!=null){
				for(int i=0; i< channelTab.getTabCount(); i++){
					if(channelTab.getComponentAt(i)!=null){
						MonitorAndDebug.printConsole("\t ... channel "+i+" : changed data - "+((ChannelViewer) channelTab.getComponentAt(i)).inputEvent());
						result=result || ((ChannelViewer) channelTab.getComponentAt(i)).inputEvent();
						}
				}
			}
			if(initDetectorUI && detectorViewer!=null){
				MonitorAndDebug.printConsole("\t ... detector : changed data - "+detectorViewer.inputEvent());
				result=result || detectorViewer.inputEvent();
			}
			if(initLightPathUI && lightPathViewer!=null){
				MonitorAndDebug.printConsole("\t ... lightPath : changed data - "+lightPathViewer.inputEvent());
				result=result || lightPathViewer.inputEvent();
			}
			if(initLightSrcUI && lightSrcViewer!=null){
				MonitorAndDebug.printConsole("\t ... lightSrc : changed data - "+lightSrcViewer.inputEvent());
				result=result || lightSrcViewer.inputEvent();
			}
		}else{
			if(initChannelUI && channelTab!=null && channelTab.getTabCount()>0){
				if(channelTab.getComponentAt(0)!=null){
					MonitorAndDebug.printConsole("\t ... channel 0 : changed data - "+((ChannelViewer) channelTab.getComponentAt(0)).inputEvent());
					result=result || ((ChannelViewer) channelTab.getComponentAt(0)).inputEvent();
				}
			}
			if(initDetectorUI && detectorViewer!=null){
				MonitorAndDebug.printConsole("\t ... detector : changed data - "+detectorViewer.inputEvent());
				result=result || detectorViewer.inputEvent();
			}
			if(initLightPathUI && lightPathViewer!=null){
				MonitorAndDebug.printConsole("\t ... lightPath : changed data - "+lightPathViewer.inputEvent());
				result=result || lightPathViewer.inputEvent();
			}
			if(initLightSrcUI && lightSrcViewer!=null){
				MonitorAndDebug.printConsole("\t ... lightSrc : changed data - "+lightSrcViewer.inputEvent());
				result=result || lightSrcViewer.inputEvent();
			}
		}
		
		MonitorAndDebug.printConsole("########## INPUT available: "+result+" ##################");
		
		return result;
	}
	
	
	/**
	 * True if all data of viewer was stored
	 * @return
	 */
	public boolean allDataWasStored()
	{
		boolean result=true;
		if(initImageUI && imageUI!=null){ 
			result=result && imageUI.allDataWasStored();
			MonitorAndDebug.printConsole("\t ... Image data stored - "+imageUI.allDataWasStored());
		}
		if(initExperimentUI && experimentUI!=null){
			result=result && experimentUI.allDataWasStored();
			MonitorAndDebug.printConsole("\t ... experiment data stored - "+experimentUI.allDataWasStored());
		}
		if(initObjectiveUI && objectiveUI!=null){
			result=result && objectiveUI.allDataWasStored();
			MonitorAndDebug.printConsole("\t ... objective data stored - "+objectiveUI.allDataWasStored());
		}
		if(initSampleUI && sampleUI!=null){
			result=result && sampleUI.allDataWasStored();
			MonitorAndDebug.printConsole("\t ... sample data stored - "+sampleUI.allDataWasStored());
		}
		if(initImageEnvUI && imgEnvViewer!=null){
			result=result && imgEnvViewer.allDataWasStored();
			MonitorAndDebug.printConsole("\t ... imgEnv data stored - "+imgEnvViewer.allDataWasStored());
		}
		if(initDetectorUI && detectorViewer!=null){
			result=result && detectorViewer.allDataWasStored();
			MonitorAndDebug.printConsole("\t ... detector data stored - "+detectorViewer.allDataWasStored());
		}
		if(initLightPathUI && lightPathViewer!=null){
			result=result && !lightPathViewer.inputEvent();
			MonitorAndDebug.printConsole("\t ... LightPath data stored - "+(!lightPathViewer.inputEvent()));
		}
		if(initLightSrcUI && lightSrcViewer!=null){
			result=result && lightSrcViewer.allDataWasStored();
			MonitorAndDebug.printConsole("\t ... LightSrc data stored - "+lightSrcViewer.allDataWasStored());
		}
		
		if(initChannelUI  && model.getNumberOfChannels()>0){
			if(channelTab!=null){
				for(int i=0; i< channelTab.getTabCount(); i++){
					if(channelTab.getComponentAt(i)!=null && channelTab.getComponentAt(i) instanceof ChannelViewer){
						result=result && ((ChannelViewer) channelTab.getComponentAt(i)).allDataWasStored();
						MonitorAndDebug.printConsole("\t ... Channel data stored - "+((ChannelViewer) channelTab.getComponentAt(i)).allDataWasStored());
					}else{
						MonitorAndDebug.printConsole("\t ... Channel no tabcomponent init");
					}
				}
			}else{
				MonitorAndDebug.printConsole("\t ... Channel no channeltab");
			}
		}else{
			MonitorAndDebug.printConsole("\t ... Channel no data init");
		}
		return result;
	}
	
	/** notice all added data (input and predefinition values)
	 * @return
	 */
	public boolean hasDataToSave() 
	{
		boolean result=false;
		MonitorAndDebug.printConsole("# MetaDataUI::hasDataToSave()");
		if(initImageUI && imageUI!=null){
			result=result || imageUI.hasDataToSave() || model.getChangesImage()!=null;
			MonitorAndDebug.printConsole("\t ... image : changed data - "+
					(imageUI.hasDataToSave()|| model.getChangesImage()!=null));
		}
		if(initExperimentUI && experimentUI!=null){
			result=result || experimentUI.hasDataToSave() || model.getChangesExperiment()!=null;
			MonitorAndDebug.printConsole("\t ... Experiment : changed data - "+
			(experimentUI.hasDataToSave()||model.getChangesExperiment()!=null));
		}
		if(initObjectiveUI && objectiveUI!=null){
			result=result || objectiveUI.hasDataToSave()|| model.getChangesObject()!=null;
			MonitorAndDebug.printConsole("\t ... Objective : changed data - "+
			objectiveUI.hasDataToSave()+","+( model.getChangesObject()!=null));
		}
		if(initSampleUI && sampleUI!=null){
			result = result || sampleUI.hasDataToSave()|| model.getChangesSample()!=null;
			MonitorAndDebug.printConsole("\t ... Sample : changed data - "+
			(sampleUI.hasDataToSave()||model.getChangesSample()!=null));
		}
		if(initImageEnvUI && imgEnvViewer!=null){
			result=result || imgEnvViewer.hasDataToSave() || model.getChangesImgEnv()!=null;
			MonitorAndDebug.printConsole("\t ... ImageEnv : changed data - "+
					(imgEnvViewer.hasDataToSave()|| model.getChangesImgEnv()!=null));
		}
		
		if(initChannelUI  && model.getNumberOfChannels()>0){
			if(channelTab!=null){
				
				for(int i=0; i< channelTab.getTabCount(); i++){
					if(channelTab.getComponentAt(i)!=null && channelTab.getComponentAt(i) instanceof ChannelViewer){
						result=result || ((ChannelViewer) channelTab.getComponentAt(i)).hasDataToSave();
						MonitorAndDebug.printConsole("\t ... Channel : changed data - "+
							((ChannelViewer) channelTab.getComponentAt(i)).hasDataToSave());
					}else{
						MonitorAndDebug.printConsole("\t ... Channel : no data ");
					}
				}
			}
			
			if(initDetectorUI && detectorViewer!=null){
				result=result || detectorViewer.hasDataToSave() || model.getChangesDetector()!=null;
				MonitorAndDebug.printConsole("\t ... Detector : changed data - "+
						(detectorViewer.hasDataToSave()|| model.getChangesDetector()!=null));
			}
			if(initLightPathUI && lightPathViewer!=null){
				result=result || lightPathViewer.hasDataToSave();
				MonitorAndDebug.printConsole("\t ... LightPath : changed data - "+
						(lightPathViewer.hasDataToSave()));
			}
			if(initLightSrcUI && lightSrcViewer!=null){
				result=result || lightSrcViewer.hasDataToSave()||model.getChangesLightSrc()!=null;
				MonitorAndDebug.printConsole("\t ... LightSrc : changed data - "+
						(lightSrcViewer.hasDataToSave()|| model.getChangesLightSrc()!=null));
			}
//			if(initDetectorUI){
//				result=result || detectorInput;
//				MonitorAndDebug.printConsole("\t ... Detector : changed data - "+detectorInput);
//			}
//			if(initLightPathUI ){
//				result=result || lightPathInput;
//				MonitorAndDebug.printConsole("\t ... Objective : changed data - "+lightPathInput);
//			}
//			if(initLightSrcUI ){
//				result=result || lightSrcInput;
//				MonitorAndDebug.printConsole("\t ... LightSrc : changed data - "+lightSrcInput);
//			}
		}else{
			if(initChannelUI && channelTab!=null && channelTab.getTabCount()>0){
				MonitorAndDebug.printConsole("\t ... Channel tabs: "+channelTab.getTabCount());
				if(channelTab.getComponentAt(0)!=null && channelTab.getComponentAt(0) instanceof ChannelViewer){
					result=result || ((ChannelViewer) channelTab.getComponentAt(0)).hasDataToSave();
					MonitorAndDebug.printConsole("\t ... Channel : changed data - "+
							(((ChannelViewer) channelTab.getComponentAt(0)).hasDataToSave()));
				}else{
					MonitorAndDebug.printConsole("\t ... Channel : no data - ??");
				}
			}
			if(initDetectorUI && detectorViewer!=null){
				result=result || detectorViewer.hasDataToSave() || model.getChangesDetector()!=null;
				MonitorAndDebug.printConsole("\t ... Detector : changed data - "+
						(detectorViewer.hasDataToSave()|| model.getChangesDetector()!=null));
			}
			if(initLightPathUI && lightPathViewer!=null){
				result=result || lightPathViewer.hasDataToSave();
				MonitorAndDebug.printConsole("\t ... LightPath : changed data - "+
						(lightPathViewer.hasDataToSave()));
			}
			if(initLightSrcUI && lightSrcViewer!=null){
				result=result || lightSrcViewer.hasDataToSave()||model.getChangesLightSrc()!=null;
				MonitorAndDebug.printConsole("\t ... LightSrc : changed data - "+
						(lightSrcViewer.hasDataToSave()|| model.getChangesLightSrc()!=null));
			}
		}
		
		model.setDataChange(result);
		return result;
	}

	public void save() 
	{
		String chName=null;
		
		MonitorAndDebug.printConsole("# MetaDataUI::save()");
		if(imageUI!=null){
			if( imageUI.hasDataToSave()){
				List<TagData> list=imageUI.getChangedTags();
				model.setMapAnnotationImage(imageUI.getMapValuesOfChanges(model.getMapAnnotationImage()), false); 
				printList("Image",list);
				imageUI.saveData();
				model.setChangesImage(list);
				imageUI.afterSavingData();
			}
		}
		if(experimentUI!=null){
			if( experimentUI.hasDataToSave()){
				List<TagData> list=experimentUI.getChangedTags();
	//			model.setMapAnnotationExperiment(wrapListToMap(list,model.getMapAnnotationExperiment(),"Experiment"));
				model.setMapAnnotationExperiment(experimentUI.getMapValuesOfChanges(model.getMapAnnotationExperiment()), false); 
				printList("Experiment",list);
				experimentUI.saveData();
				model.setChangesExperiment(list);
				experimentUI.afterSavingData();
			}
		}
		if(sampleUI!=null ){
			if( sampleUI.hasDataToSave()){
				List<TagData> list=sampleUI.getChangedTags();
	//			model.setMapAnnotationSample(wrapListToMap(list,model.getMapAnnotationSample(),"Sample"));
				model.setMapAnnotationSample(sampleUI.getMapValuesOfChanges(model.getMapAnnotationSample()), false); 
				printList("Sample",list);
				sampleUI.saveData();
				model.setChangesSample(list);
				sampleUI.afterSavingData();
			}
		}
		if(objectiveUI!=null){
			if( objectiveUI.hasDataToSave()){
				List<TagData> list=objectiveUI.getChangedTags();
				printList("Objective",list);
				objectiveUI.saveData();
				model.setChangesObject(list);
				model.setMapAnnotationObjective(objectiveUI.getMapValuesOfChanges(model.getMapAnnotationObjective()), false);
				objectiveUI.afterSavingData();
			}
		}
		if(imgEnvViewer!=null){
			if( imgEnvViewer.hasDataToSave()){
				List<TagData> list=imgEnvViewer.getChangedTags();
				model.setMapAnnotationImgEnv(imgEnvViewer.getMapValuesOfChanges(model.getMapAnnotationImgEnv()), false);
				printList("ImgEnv",list);
				imgEnvViewer.saveData();
				model.setChangesImageEnv(list);
				imgEnvViewer.afterSavingData();
			}
		}
		

		//save current selected channel
		if(channelTab!=null && 
				channelTab.getSelectedComponent()!=null && 
				channelTab.getSelectedComponent() instanceof ChannelViewer)
		{
			ChannelViewer chViewer=(ChannelViewer) channelTab.getSelectedComponent();
			chName=chViewer.getName();
			if(chViewer.hasDataToSave())
			{
				List<TagData> list=chViewer.getChangedTags();
				printList("Channel "+chViewer.getIndex(),list);
				chViewer.saveData();
				model.setChangesChannel(list, chViewer.getIndex());
				model.setMapAnnotationChannel(chViewer.getMapValuesOfChanges(model.getMapAnnotationChannel(chViewer.getIndex())),chViewer.getIndex(), false);
//				chViewer.resetInputEvent();
				chViewer.afterSavingData();
			}
		}
		// save selected component, other saved by deselect the channel
		if(detectorViewer!=null && detectorViewer.hasDataToSave()){
			int thisIndex=detectorViewer.getIndex();
			List<TagData> list=detectorViewer.getChangedTags();
			HashMap<String,String> map=model.getMapAnnotationDetector(thisIndex);
			
			printList("Detector "+thisIndex,list);
			detectorViewer.saveData();
			model.setChangesDetector(list,thisIndex);
			model.setMapAnnotationDetector(detectorViewer.getMapValuesOfChanges(map,chName),thisIndex, false);
//			detectorViewer.resetInputEvent();
			detectorViewer.afterSavingData();
		}
		if(lightPathViewer!=null && lightPathViewer.hasDataToSave()){
			int index=lightPathViewer.getIndex();
			lightPathViewer.saveData();
			try {
				model.setChangesLightPath(model.getLightPath(index),lightPathViewer.getIndex());
				model.setMapAnnotationLightPath(lightPathViewer.getMapValuesOfChanges(model.getMapAnnotationLightPath(lightPathViewer.getIndex()),chName),lightPathViewer.getIndex(), false);
//				lightPathViewer.resetInputEvent();
				lightPathViewer.afterSavingData();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(lightSrcViewer!=null && lightSrcViewer.hasDataToSave()){
			List<TagData> list=lightSrcViewer.getChangedTags();
			printList("LightSrc "+lightSrcViewer.getIndex(),list);
			lightSrcViewer.saveData();
			model.setChangesLightSrc(list,lightSrcViewer.getIndex());
			model.setMapAnnotationLightSrc(lightSrcViewer.getMapValuesOfChanges(model.getMapAnnotationLightSrc(lightSrcViewer.getIndex()),chName),lightSrcViewer.getIndex(), false); 
//			lightSrcViewer.resetInputEvent();
			lightSrcViewer.afterSavingData();
		}
		
		detectorInput=false;
		lightPathInput=false;
		lightSrcInput=false;
	}


	
	private HashMap<String, String> wrapListToMap(List<TagData> list, HashMap<String, String> map,
			String id) 
	{
		if(map==null)
			map=new HashMap<String, String>();
		for(TagData t:list){
			map.put(id+t.getTagName(), t.getTagValue());
		}
		return map;
	}

	public static void printList(String string, List<TagData> list) 
	{
		MonitorAndDebug.printConsole("\t Changes in "+string);
		for(TagData t:list){
			MonitorAndDebug.printConsole("\t\t "+t.getTagName()+" = "+t.getTagValue());
		}
	}

	public boolean experimentUIInput()
	{
		return experimentUI.hasDataToSave();
	}
	public boolean sampleUIInput()
	{
		return sampleUI.hasDataToSave();
	}


	
	
	class DemoCustomTab extends JPanel {
		 
	    JTabbedPane customJTabbedPane;
	    CustomButton removeButton;
	 
	    /** JPanel contain a JLabel and a JButton to close */
	    public DemoCustomTab(JTabbedPane customJTabbedPane) {
	        this.customJTabbedPane = customJTabbedPane;
	        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
	        setBorder(new EmptyBorder(5, 2, 2, 2));
	        setOpaque(false);
	        addLabel();
	        removeButton=new CustomButton("x");
	        add(removeButton);
	    }
	 
	   

		public void hideRemove() 
		{
			removeButton.setVisible(false);
		}
		
		public void showRemove() 
		{
			removeButton.setVisible(true);
		}



		private void addLabel() {
	        JLabel label = new JLabel() {
	            /** set text for JLabel, it will title of tab */
	            public String getText() {
	                int index = customJTabbedPane.indexOfTabComponent(DemoCustomTab.this);
	                if (index != -1) {
	                    return customJTabbedPane.getTitleAt(index);
	                }
	                return null;
	            }
	        };
	        /** add more space between the label and the button */
	        label.setBorder(new EmptyBorder(0, 0, 0, 10));
	        add(label);
	    }
	 
	    class CustomButton extends JButton implements MouseListener {
	        public CustomButton(String text) {
	            int size = 15;
	            setText(text);
	            /** set size for button close */
	            setPreferredSize(new Dimension(size, size));
	 
	            setToolTipText("close the Tab");
	 
	            /** set transparent */
	            setContentAreaFilled(false);
	 
	            /** set border for button */
	            setBorder(new EtchedBorder());
	            /** don't show border */
	            setBorderPainted(false);
	 
	            setFocusable(false);
	 
	            /** add event with mouse */
	            addMouseListener(this);
	 
	        }
	 
	        /** when click button, tab will close */
	        @Override
	        public void mouseClicked(MouseEvent e) {
	            int index = customJTabbedPane.indexOfTabComponent(DemoCustomTab.this);
	            if (index != -1) {
	            	// remove channel and his linked detector, lightSrc, lightPath
	            	MetaDataUI.this.model.getChannelModel().remove(index); 
	            	MetaDataUI.this.model.getDetectorModel().remove(index);
	            	MetaDataUI.this.model.getLightSourceModel().remove(index);
	            	MetaDataUI.this.model.getLightPathModel().remove(index);
	                customJTabbedPane.remove(index);
	                numChannelTabs--;
	            
	                MonitorAndDebug.printConsole("\t...Remove tab- index: "+index+", numTabs: "+numChannelTabs);
//	                 set last channel as removable
					if(directoryPane && numChannelTabs>2){
						DemoCustomTab lastTab=(DemoCustomTab) channelTab.getComponentAt(index-1);
						if(lastTab!=null) 
							lastTab.showRemove();
						else
							channelTab.setTabComponentAt(index-1, new DemoCustomTab(channelTab));
					}
					
	                if (index == numChannelTabs - 1 && index > 0) {
	                	customJTabbedPane.setSelectedIndex(numChannelTabs - 2);
	                } else {
	                	customJTabbedPane.setSelectedIndex(index);
	                }
	         
	                if (numChannelTabs == 1) {
	                    addNewChannelTab();
	                }
	              
	            }
	        }
	 
	        @Override
	        public void mousePressed(MouseEvent e) {
	        }
	 
	        @Override
	        public void mouseReleased(MouseEvent e) {
	        }
	 
	        /** show border button when mouse hover */
	        @Override
	        public void mouseEntered(MouseEvent e) {
	            setBorderPainted(true);
	            setForeground(Color.RED);
	        }
	 
	        /** hide border when mouse not hover */
	        @Override
	        public void mouseExited(MouseEvent e) {
	            setBorderPainted(false);
	            setForeground(Color.BLACK);
	        }
	    }
	}
	
	public boolean predefinitionsAreLoaded()
	{
		if(customSett==null )
			return false;
		
		boolean result=false;
		if(imageUI!=null){
			result=result || imageUI.predefinitionValAreLoaded();
			MonitorAndDebug.printConsole("-- Image : predata loaded - "+imageUI.predefinitionValAreLoaded());
		}
		
		if(imgEnvViewer!=null){
			result=result|| imgEnvViewer.predefinitionValAreLoaded();
			MonitorAndDebug.printConsole("-- ImgEnv : predata loaded - "+imgEnvViewer.predefinitionValAreLoaded());
		}
		if(objectiveUI!=null){
			result=result|| objectiveUI.predefinitionValAreLoaded();
			MonitorAndDebug.printConsole("-- Objective : predata loaded - "+objectiveUI.predefinitionValAreLoaded());
		}
		if(detectorViewer!=null){
			result=result|| detectorViewer.predefinitionValAreLoaded();
			MonitorAndDebug.printConsole("-- Detector : predata loaded - "+detectorViewer.predefinitionValAreLoaded());
		}
		if(lightSrcViewer!=null){
			result=result|| lightSrcViewer.predefinitionValAreLoaded();
			MonitorAndDebug.printConsole("-- LightSrc : predata loaded - "+lightSrcViewer.predefinitionValAreLoaded());
		}
		if(lightPathViewer!=null){
			result=result|| lightPathViewer.predefinitionValAreLoaded();
			MonitorAndDebug.printConsole("-- LightPath : predata loaded - "+lightPathViewer.predefinitionValAreLoaded());
		}
		if(sampleUI!=null){
			result=result|| sampleUI.predefinitionValAreLoaded();
			MonitorAndDebug.printConsole("-- Sample : predata loaded - "+sampleUI.predefinitionValAreLoaded());
		}
		if(experimentUI!=null){
			result=result|| experimentUI.predefinitionValAreLoaded();
			MonitorAndDebug.printConsole("-- Experiment : predata loaded - "+experimentUI.predefinitionValAreLoaded());
		}
		if(initChannelUI  && model.getNumberOfChannels()>0){
			if(channelTab!=null){
				for(int i=0; i< channelTab.getTabCount(); i++){
					if(channelTab.getComponentAt(i)!=null){
						result=result || ((ChannelViewer) channelTab.getComponentAt(i)).predefinitionValAreLoaded();
						MonitorAndDebug.printConsole("-- Image : predata loaded - "+((ChannelViewer) channelTab.getComponentAt(i)).predefinitionValAreLoaded());
					}
				}
			}
		}else{
			if(initChannelUI && channelTab!=null && channelTab.getTabCount()>0){
				if(channelTab.getComponentAt(0)!=null){
					result=result || ((ChannelViewer) channelTab.getComponentAt(0)).predefinitionValAreLoaded();
					MonitorAndDebug.printConsole("-- Image : predata loaded - "+((ChannelViewer) channelTab.getComponentAt(0)).predefinitionValAreLoaded());
				}
			}
		}
		//TODO channel
		return result;
	}
	
	public void savePreValues()
	{
		if(imageUI!=null && imageUI.predefinitionValAreLoaded()){
			imageUI.saveData();
		}
		if(imgEnvViewer!=null && imgEnvViewer.predefinitionValAreLoaded()){
			imgEnvViewer.saveData();
		}
		if(objectiveUI!=null && objectiveUI.predefinitionValAreLoaded()){
			objectiveUI.saveData();
		}
		if(detectorViewer!=null && detectorViewer.predefinitionValAreLoaded()){
			detectorViewer.saveData();
		}
		if(lightSrcViewer!=null && lightSrcViewer.predefinitionValAreLoaded()){
			lightSrcViewer.saveData();
		}
		if(lightPathViewer!=null && lightPathViewer.predefinitionValAreLoaded()){
			lightPathViewer.saveData();
		}
		if(sampleUI!=null && sampleUI.predefinitionValAreLoaded()){
			sampleUI.saveData();
		}
		if(experimentUI!=null && experimentUI.predefinitionValAreLoaded()){
			experimentUI.saveData();
		}
	}

	public MapAnnotationData getMapAnnotation() 
	{
		MapAnnotationData ma=model.getAnnotation();
		return ma;
	}
	
	public void addMapAnnotations(MetaDataModel m)
	{
		MonitorAndDebug.printConsole("# MetaDataUI::addMapAnnotations()");
		model.setMapAnnotationImage(m.getMapAnnotationImage(), true);
		model.setMapAnnotationImgEnv(m.getMapAnnotationImgEnv(), true);
		model.setMapAnnotationExperiment(m.getMapAnnotationExperiment(), true);
		model.setMapAnnotationSample(m.getMapAnnotationSample(), true);
		model.setMapAnnotationObjective(m.getMapAnnotationObjective(), true);
		
		for(int i=0; i<m.getNumberOfChannels();i++){
			model.setMapAnnotationChannel(m.getMapAnnotationChannel(i), i, true);
		}
		for(int i=0; i<m.getNumberOfDetectors();i++){
			model.setMapAnnotationDetector(m.getMapAnnotationDetector(i), i, true);
		}
		for(int i=0; i<m.getNumberOfLightPath();i++){
			model.setMapAnnotationLightPath(m.getMapAnnotationLightPath(i), i, true);
		}
		for(int i=0; i<m.getNumberOfLightSrc();i++){
			model.setMapAnnotationLightSrc(m.getMapAnnotationLightSrc(i), i, true);
		}
	}
	

	
}
