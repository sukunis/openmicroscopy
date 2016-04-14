package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.microscope;
//
//import java.awt.BorderLayout;
//import java.awt.CardLayout;
//import java.awt.Component;
//import java.awt.Container;
//import java.awt.Dimension;
//import java.awt.Graphics;
//import java.awt.GridBagConstraints;
//import java.awt.GridBagLayout;
//import java.awt.Point;
//import java.awt.Toolkit;
//import java.awt.Window;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.io.File;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
//import javax.swing.Box;
//import javax.swing.BoxLayout;
//import javax.swing.ImageIcon;
//import javax.swing.JButton;
//import javax.swing.JComponent;
//import javax.swing.JDialog;
//import javax.swing.JFrame;
//import javax.swing.JLabel;
//import javax.swing.JLayer;
import javax.swing.JPanel;
//import javax.swing.JScrollPane;
//import javax.swing.JTabbedPane;
//import javax.swing.ScrollPaneConstants;
//import javax.swing.SwingUtilities;
//import javax.swing.WindowConstants;
//import javax.swing.border.EmptyBorder;
//import javax.swing.event.ChangeEvent;
//import javax.swing.event.ChangeListener;
//import javax.swing.plaf.LayerUI;
//
//import loci.common.services.ServiceFactory;
//import loci.formats.MetadataTools;
//import loci.formats.meta.IMetadata;
//import loci.formats.services.OMEXMLService;
//import ome.formats.enums.IQueryEnumProvider;
//import ome.xml.model.Channel;
//import ome.xml.model.Detector;
//import ome.xml.model.DetectorSettings;
//import ome.xml.model.Dichroic;
//import ome.xml.model.Experiment;
//import ome.xml.model.Experimenter;
//import ome.xml.model.Filter;
//import ome.xml.model.Image;
//import ome.xml.model.ImagingEnvironment;
//import ome.xml.model.Instrument;
//import ome.xml.model.LightPath;
//import ome.xml.model.LightSource;
//import ome.xml.model.LightSourceSettings;
//import ome.xml.model.OME;
//import ome.xml.model.Objective;
//import ome.xml.model.ObjectiveSettings;
//import ome.xml.model.Pixels;
//import ome.xml.model.Plane;
//import ome.xml.model.Project;
//
//import org.openmicroscopy.shoola.agents.fsimporter.IconManager;
//import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.ImportUserData;
//import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.ScrollablePanel;
//import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.UOSMetadataLogger;
//import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.MetaDataControl;
//import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.MetaDataModel;
//import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.SaveMetadata;
////import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.MicDummyCompUI.TopRightCornerLabelLayerUI;
//import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.ChannelCompUI;
//import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.DetectorCompUI;
//import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.DetectorSettingsCompUI;
//import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.ElementsCompUI;
//import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.ExperimentCompUI;
//import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.ImageCompUI;
//import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.ImagingEnvironmentCompUI;
//import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.LightPathCompUI;
//import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.LightSourceCompUI;
//import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.LightSourceSettingsCompUI;
//import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.ObjectiveCompUI;
//import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.ObjectiveSettingsCompUI;
//import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.PlaneCompUI;
//import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.PlaneSliderCompUI;
//import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.SampleCompUI;
//import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.TitledSeparator;
//import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.UOSProfileReader;
//
public class MicroscopeDataView extends JPanel
implements IMicroscopeDataView
{
	
}
//	 /** Logger for this class. */
//    private static Logger LOGGER = Logger.getLogger(UOSMetadataLogger.class.getName());
////    
////	protected enum MicSubmodules
////	{
////		IMAGE_DATA,
////		OBJECTIVE_DATA,
////		DETECTOR_DATA,
////		LIGHTSOURCE_DATA,
////		CHANNEL_DATA,
////		LIGHTPATH_DATA,
////		EXPERIMENT_DATA,
////		SAMPLE_DATA
////	}
//	
//	/**
//	 * Metadata panel:   A | B | C | D ...
//	 * 					 E | F | G | H
//	 * 						bottom
//	 * @author kunis
//	 *
//	 */
//	public static enum GUIPlaceholder
//	{
//		Pos_A,
//		Pos_B,
//		Pos_C,
//		Pos_D,
//		Pos_E,
//		Pos_F,
//		Pos_G,
//		Pos_H,
//		Pos_Bottom
//	}
//	
//	protected Image image;
//	protected Project project;
//	protected OME ome;
//	protected MetaDataModel model;
//	protected MetaDataControl controller;
//	private IMetadata metadata;
//	
//	protected JButton loadBtn;
//	protected JButton saveBtn;
//	protected JButton resetBtn;
//	
//	GridBagLayout gbl;
//	
//	protected JDialog imgEnvDialog;
//	protected JDialog planeDialog;
//	protected JTabbedPane planeTab;
//	protected JTabbedPane channelTab;
//	protected JPanel lightPathCardPane;
//	private JTabbedPane lightPathPane;
//	
//	protected JPanel lightSrcPane;
//	protected JPanel detectorPane;
//	protected JPanel objectivePane;
//	
//	//Gui data objects
//	protected List<PlaneCompUI> planesUI;
//	protected List<ChannelCompUI> channelsUI;
////	protected ImageCompUI imageUI;
//	protected ObjectiveSettingsCompUI oSUI;
//	private JTabbedPane experimentPane;
//	protected ImagingEnvironmentCompUI imgEnvUI;
//	private JTabbedPane samplePane;
//	private JTabbedPane imagePane;
//	
//	private boolean initChannelUI;
//	private boolean initImageUI;
//	private boolean initLightPathUI;
//	private boolean initLightSrcUI;
//	private boolean initDetectorUI;
//	private boolean initObjectiveUI;
//	private boolean initPlanesUI;
//	private boolean initImageEnvUI;
//	private boolean initSampleUI;
//	private boolean initExperimentUI;
//
//	//ome xml data objects
//	private List<Plane> planes;
//	private List<Channel> channels;
//	private List<Objective> objectives;
//	private List<Detector> detectors;
//	private List<LightSource> lightSources;
//	private List<Filter> filterList;
//	private List<Dichroic> dichroicList;
//	private int numT,numZ,numC;
//	
//	private int imageIndex;
//	private File file;
//	
//	private CustomViewProperties viewProperties;
//	private ImportUserData importUserData;
//	
//    /**
//     * Dummy Konstructor
//     */
//	public MicroscopeDataView(CustomViewProperties viewProp)
//	{
//		LOGGER.info("[GUI] Create Empty View");
//		System.out.println("##########################################");
//		System.out.println("CREATE EMPTY VIEW ");
//		System.out.println("##########################################");
//		
//		// data
//		initChannelUI=false;
//		initImageUI=false;
//		initLightPathUI=false;
//		initLightSrcUI=false;
//		initDetectorUI=false;
//		initObjectiveUI=false;
//		initPlanesUI=false;
//		initImageEnvUI=false;
//		initSampleUI=false;
//		initExperimentUI=false;
//		
//		viewProperties=viewProp;
//		initView();
//	}
//	
//	/**
//	 * _file image file
//	 * metadata 
//	 */
////	public MicroscopeDataView(String _file, IMetadata metadata, ImportUserData importData)
////	{
////		file=new File(_file);
////		importUserData=importData;
////		System.out.println("##########################################");
////		System.out.println("CREATE NEW VIEW for "+_file);
////		System.out.println("##########################################");
////		LOGGER.info("Create Empty View for "+_file);
////		
////		initChannelUI=false;
////		initImageUI=false;
////		initLightPathUI=false;
////		initLightSrcUI=false;
////		initDetectorUI=false;
////		initObjectiveUI=false;
////		initPlanesUI=false;
////		initImageEnvUI=false;
////		initSampleUI=false;
////		initExperimentUI=false;
////		
////		initLayout();
////
////		// global buttons
////		initBtnElements();
////
////		setFileData(metadata,importData);
////		
////		// build custom gui
////		UOSProfileReader propReader=new UOSProfileReader(new File("profileUOSImporter.xml"));
////		viewProperties=propReader.getViewProperties();
////		loadCustomView(viewProperties);
////	}
//	
//	public void linkToFile(File file)
//	{
//		this.file=file;
//	}
//	
//	public void setFileData(IMetadata metadata,ImportUserData importData)
//	{
//		LOGGER.info("[DATA] set file data");
//		importUserData=importData;
//		this.metadata=metadata;
//		readMetaDataFromImage(metadata);
//		loadModuleDataFromImage(viewProperties.getModules());
//	}
//	
//	private void initLayout()
//	{
//		//layout
//		gbl = new GridBagLayout();
//		setLayout( gbl );
//
//		gbl.columnWidths = new int[] {50, 50,50,50};
//	}
//	
//	protected void readMetaDataFromImage(IMetadata data)
//	{
//		if(data !=null)
//		{		
//			try{
//				ServiceFactory factory = new ServiceFactory();
//				OMEXMLService service = factory.getInstance(OMEXMLService.class);
//				String xml = service.getOMEXML(data);
//				ome = (OME) service.createOMEXMLRoot(xml);
//
//				//TODO eigentlich imageList!!!!
//				imageIndex=0;
//				image=ome.getImage(imageIndex);
//				//TODO richtiges project!!
//				if(ome.sizeOfProjectList()>0){
//					project=ome.getProject(0);
//				}
//				
//				if(image!=null){
//					//TODO: jedes image hat sein eigenes Model
//					model.setImageProp(imageIndex, image.getPixels().sizeOfChannelList());
//					model.setImageOMEData(image);
//
//					controller=new MetaDataControl(model);
//
//					//TODO: no referenced instrument? -> createDummy
//					Instrument instrument=image.getLinkedInstrument();
//					if(instrument==null){
//						System.out.println("::ATTENTION:: no instruments available [MicroscopeCompUI2]");
//					}else{
//						objectives=instrument.copyObjectiveList();
//						detectors=instrument.copyDetectorList();
//						lightSources=instrument.copyLightSourceList();
//						filterList=instrument.copyFilterList();
//						model.setFilterList(filterList);
//						dichroicList=instrument.copyDichroicList();
//						model.setDichroicList(dichroicList);
//					}
//
//					//TODO: no Pixel data -> dummies ??
//					Pixels pixels=image.getPixels();
//					if(pixels==null){
//						System.out.println("::ATTENTION:: no pixel object available [MicroscopeCompUI2]");
//					}else{
//						channels=pixels.copyChannelList();
//						planes=pixels.copyPlaneList();
//					}
//				}
//
//			}catch(Exception e){
//				ome=null;
//				LOGGER.severe("Can't read metadata");
//				e.printStackTrace();
//			}
//		}else{
//			LOGGER.warning("METADATA not available");
//			ome=null;
//			image=null;
//			project=null;
//			imageIndex=-1;
//			
//			model= new MetaDataModel(0, 0);
//			model.setImageOMEData(null);
//
//			controller=new MetaDataControl(model);
//			
//			objectives=null;
//			detectors=null;
//			lightSources=null;
//			filterList=null;
//			dichroicList=null;
//			channels=null;
//			planes=null;
//		}
//	}
//	
////	protected abstract void createGUI(); 
////	protected abstract void initComponents();
////	protected abstract void loadData();
//
//
//	private void loadCustomView(CustomViewProperties prop)
//	{
//		List<Submodule> list=prop.getModules();
//		
//		//init
//		initModules(list);
//		
//		//load
//		loadModuleDataFromImage(list);
//		
//		//GUI
//		addModuleToGUI(list);
//	}
//
//	private void addModuleToGUI(List<Submodule> list) 
//	{
//		LOGGER.info("[GUI] add modules to GUI");
//		for(Submodule subm:list){
//			switch (subm.getModule()) {
//			case IMAGE_DATA:
//				JComponent img=null;
//				if(initPlanesUI || initImageEnvUI){
////					JScrollPane spImage =new GBScrollPane(imageUI);
//					img=new JPanel();
//					GridBagLayout myGBL=new GridBagLayout();
//					img.setLayout(myGBL);
//					addComponent(img,myGBL,imagePane,0,0,1,3,1.0,1.0,GridBagConstraints.BOTH);
//					if(initImageEnvUI)
//						addComponent(img,myGBL,initImgEnvironmentBtn(),0,3,1,1,1.0,0,GridBagConstraints.HORIZONTAL);
//					if(initPlanesUI)
//						addComponent(img,myGBL,initPlaneBtn(),0,4,1,1,1.0,0,GridBagConstraints.HORIZONTAL);
//				}else{
//					img =imagePane;
//				}
//				addToPlaceholder(img, subm.getPosition(), subm.getWidth());
//				break;
//			case CHANNEL_DATA:
//				addToPlaceholder(channelTab,subm.getPosition(), subm.getWidth());
//				break;
//			case EXPERIMENT_DATA:
//				addToPlaceholder(experimentPane,subm.getPosition(), subm.getWidth());
//				break;
//			case SAMPLE_DATA:
//				addToPlaceholder(samplePane, subm.getPosition(), subm.getWidth());
//				break;
//			case OBJECTIVE_DATA:
//				addToPlaceholder(objectivePane, subm.getPosition(), subm.getWidth());
//				break;
//			case DETECTOR_DATA:
//				addToPlaceholder(detectorPane, subm.getPosition(), subm.getWidth());
//				break;
//			case LIGHTSOURCE_DATA:
//				addToPlaceholder(lightSrcPane, subm.getPosition(), subm.getWidth());
//				break;
//			case LIGHTPATH_DATA:
//				addToPlaceholder(lightPathPane, subm.getPosition(), subm.getWidth());
//				break;
//
//			default:
//				break;
//			}
//		}
//		addToPlaceholder(getButtonPane(), GUIPlaceholder.Pos_Bottom, 2);
//		revalidate();
//		repaint();
//	}
//
//	private void loadModuleDataFromImage(List<Submodule> list) 
//	{
//		for(Submodule subm:list){
//			switch (subm.getModule()) {
//			case IMAGE_DATA:
//				loadImageModule();
//				break;
//			case CHANNEL_DATA:
//				loadChannelModule();
//				break;
//			case IMAGEENVIRONMENT_DATA:
//				loadImageEnvModule();
//				break;
//			case EXPERIMENT_DATA:
//				Experiment exp=null; Experimenter exper=null;
//				if(image!=null){
//					exp=image.getLinkedExperiment();
//					exper=image.getLinkedExperimenter();
//				}
//				loadExperimentModule(exp,exper);
//				break;
//			case PLANE_DATA:
//				loadPlaneModule();
//				break;
//			case SAMPLE_DATA:
//				loadSampleModule();
//				break;
//
//			default:
//				break;
//			}
//		}
//		revalidate();
//		repaint();
//	}
//
//	private void initModules(List<Submodule> list) 
//	{
//		LOGGER.info("[GUI] init modules "+list.size()+"####");
//		for(Submodule subm:list){
//			switch (subm.getModule()) {
//			case OBJECTIVE_DATA:
//				initObjectiveModule();
//				break;
//			case DETECTOR_DATA:
//				initDetectorModule();
//				break;
//			case LIGHTSOURCE_DATA:
//				initLightSrcModule();
//				break;
//			case CHANNEL_DATA:
//				initChannelModule();
//				break;
//			case LIGHTPATH_DATA:
//				initLightPathModule();
//				break;
//			case IMAGEENVIRONMENT_DATA:
//				initImageEnvUI=true;
//			case PLANE_DATA:
//				initPlanesModule();
//			case EXPERIMENT_DATA:
//				initExperimentModul();
//			case SAMPLE_DATA:
//				initSampleModul();
//			case IMAGE_DATA:
//				initImageModul();
//			default:
//				break;
//			}
//		}
//		LOGGER.info("[GUI] ### FINISHED init modules ");
//		revalidate();
//		repaint();
//	}
//	
//	
//	
//	private void reloadMetaData()
//	{
//		LOGGER.info("*************** Reload Metadata *********************");
//		this.removeAll();
//		
//		initChannelUI=false;
//		initImageUI=false;
//		initLightPathUI=false;
//		initLightSrcUI=false;
//		initDetectorUI=false;
//		initObjectiveUI=false;
//		initPlanesUI=false;
//		initImageEnvUI=false;
//		initSampleUI=false;
//		initExperimentUI=false;
//		
//		initLayout();
//
//		// global buttons
//		initBtnElements();
//
//		readMetaDataFromImage(metadata);
//		
//		// build custom gui
//		viewProperties=new CustomViewProperties();
//		loadCustomView(viewProperties);
//		
//		revalidate();
//		repaint();
//	}
//	
//	
//	protected void loadPlaneModule() 
//	{
//		if(image!=null && planes!=null){
//			try{
//				numT=image.getPixels().getSizeT().getValue();
//				numZ=image.getPixels().getSizeZ().getValue();
//				numC=image.getPixels().getSizeC().getValue();
//
//				int num=1;
//				for(Plane p:planes){
//					PlaneCompUI pUI=new PlaneCompUI(p);
//					pUI.buildComponents();
//					planesUI.add(pUI);
//					planeTab.add("#"+num,pUI);
//					num++;
//				}
//				LOGGER.info("PLANE data loaded");
//			}catch(Exception e){
//				planes=null;numT=1; numZ=1;numC=1;
//				LOGGER.severe("PLANE data load failed");
//			}
//		}else{
//			LOGGER.info("IMAGE or PLANE data not available");
//			PlaneCompUI pUI=new PlaneCompUI(null);
//			pUI.buildComponents();
//		}
//		
//	}
//	
//
//	protected void loadImageEnvModule() 
//	{
//		ImagingEnvironment iEnv=null;
//		if(image!=null){
//			iEnv=image.getImagingEnvironment();
//			if(iEnv==null){
//				LOGGER.info("IMAGE_ENVIRONMENT data not available");
//			}else{
//				LOGGER.info("IMAGE_ENVIRONMENT data loaded");
//			}
//		}
//		imgEnvUI=new ImagingEnvironmentCompUI(iEnv);
//		model.setImagingEnv(imgEnvUI);
//	}
//
//	/**
//	 * Init channel ui
//	 */
//	protected void initChannelModule() 
//	{
//		LOGGER.info("[GUI] init CHANNEL UI");
//		channelsUI=new ArrayList<ChannelCompUI>();
//		channelTab=new JTabbedPane();
//		channelTab.addChangeListener(new ChangeListener() {
//			public void stateChanged(ChangeEvent changeEvent) {
//				if(!channelsUI.isEmpty()){
//					JTabbedPane sourceTabbedPane = (JTabbedPane) changeEvent.getSource();
//					int index = sourceTabbedPane.getSelectedIndex();
//					if(index >=0){
//						selectChannel(index);
//					}
//				}
//			}
//		});
//		initChannelUI=true;
//	}
//	
//	protected void loadChannelModuleEmpty()
//	{
//		//Channels
//		ChannelCompUI cUI=new ChannelCompUI(null);
//		if(initLightPathUI){
//			// add lightPath
//			cUI.setLightPath(loadLightPathModule(null, 0, ""));
//		}
//		if(initDetectorUI){
//			//add detectorSett
////			cUI.setDetectorSettings(loadDetectorModule(null, 0, ""));
//		}
//		if(initLightSrcUI){
//			//add lightSrcSett
////			cUI.setLightSrcSettings(loadLightSrcModule(null,0,""));
//		}
//		cUI.buildComponents();
//		channelTab.add("Channel",cUI);
//	}
//	
//	protected void loadChannelModule() 
//	{
//		if(channels!=null){
//			int channelIdx=0;
//			for(Channel c:channels){
//				ChannelCompUI cUI=new ChannelCompUI(c);
//				
//				if(initLightPathUI){
//					//add lightPath
//					cUI.setLightPath(loadLightPathModule(c, channelIdx, cUI.getName()));
//				}
//
//				if(initDetectorUI){
//					/////---add detector and detectorSett
////					cUI.setDetectorSettings(loadDetectorModule(c,channelIdx,cUI.getName()));
//				}
//
//				if(initLightSrcUI){
//					/////---add lightSrc and lightSrcSett
////					cUI.setLightSrcSettings(loadLightSrcModule(c,channelIdx,cUI.getName()));
//				}
//				cUI.buildComponents();
//				channelsUI.add(cUI);
////				GBScrollPane scrollPane=new GBScrollPane(cUI);
//				channelTab.add(c.getName()!=null? c.getName(): "Channel",cUI);
//				model.addChannelData(cUI);
//				
//				channelIdx++;
//				LOGGER.info("CHANNEL data "+cUI.getName()+" loaded");
//			}
//			
//		}else{
//			LOGGER.info("CHANNEL data not available");
//			loadChannelModuleEmpty();
//		}
//	}
//	
//	
//	private LightPathCompUI loadLightPathModule(Channel c, int channelIdx,String channelName)
//	{
//		LightPathCompUI lpUI = null;
//		LightPath lp=null;
//		boolean openChooser=true;
//		
//		if(c!=null){
//			lp=c.getLightPath();
//			if(lp!=null &&(
//					lp.sizeOfLinkedEmissionFilterList()!=0 || 
//					lp.sizeOfLinkedExcitationFilterList()!=0 ||
//					lp.getLinkedDichroic()!=null)){
//				openChooser=false;
//
//				LOGGER.info("LIGHTPATH data loaded ");
//			}
//		}
//		
//		lpUI=new LightPathCompUI(lp,channelIdx,model);
//		if(openChooser){
//			LOGGER.info("LIGHTPATH data not available");
//		}else{
//			lpUI.buildComponents();
//		}
//		
////		model.setLightPath(lpUI, channelIdx);
////		lightPathCardPane.add(new GBScrollPane(lpUI),channelName);
//		
//		return lpUI;
//	}
//	private LightSourceSettingsCompUI loadLightSrcModule(Channel c, int channelIdx, String channelName)
//	{
//		LightSourceSettings ls= null;
//		LightSource l=null;
//		LightSourceCompUI lUI=null;
//		LightSourceSettingsCompUI lsUI=null;
//		String refID=null;
//		boolean openChooser=true;
//
//		if(c!=null){
//			ls= c.getLightSourceSettings();
//			if(ls!=null){
//				//no lightSrc reference
//				// add chooser
//				openChooser=false;
//				refID=ls.getID();
//				int idx=getLightSrcByID(lightSources, refID);
//				if(lightSources!=null && !lightSources.isEmpty()){
//					l=lightSources.get(idx);
//				}else{
//					l=null;
//					openChooser=true;
//				}
//			}
//		}
//		lUI=new LightSourceCompUI(l, channelIdx);
//		if(openChooser){
//			lUI.setList(lightSources);
//			lUI.showOptionPane();
//			LOGGER.info("LIGHTSOURCE  data not available");
//		}else{
//			lUI.buildComponents();
//			LOGGER.info("LIGHTSOURCE  data loaded");
//		}
//		lsUI=new LightSourceSettingsCompUI(ls, refID);	
//		lsUI.buildComponents();
//		
//		model.setLightSrcModul(lUI, channelIdx);
//		lightSrcPane.add(createPropPane(lUI,lsUI,"LightSource",
//				"for Channel "+channelName),channelName);
//		
//		return lsUI;
//	}
//	
//	private DetectorSettingsCompUI loadDetectorModule(Channel c,int channelIdx, String channelName)
//	{
//		
//		DetectorSettings ds=null;
//		Detector d=null;
//		DetectorCompUI dUI=null;
//		DetectorSettingsCompUI dsUI=null;
//		String refID=null;
//		boolean openChooser=true;
//
//		if(c!=null){
//			ds=c.getDetectorSettings();
//			if(ds!=null){
//				openChooser=false;
//				refID=ds.getID();
//				int idx=getDetectorByID(detectors, refID);
//				if(detectors!=null && !detectors.isEmpty()){
//					d=detectors.get(idx);
//				}else{
//					d=null;
//					openChooser=true;
//				}
//			}
//		}
//
//		dUI=new DetectorCompUI(d, channelIdx);
//		if(openChooser){
//			dUI.setList(detectors);
//			dUI.showOptionPane();
//			LOGGER.info("DETECTOR data not available");
//		}else{
//			dUI.buildComponents();
//			LOGGER.info("DETECTOR data loaded");
//		}
//		dsUI=new DetectorSettingsCompUI(ds, refID);
//		dsUI.buildComponents();
//	
//		model.setDetectorData(dUI, channelIdx);
//		detectorPane.add(createPropPane(dUI,dsUI,"Detector",
//				"for Channel "+channelName),channelName);
//		
//		return dsUI;
//	}
//
//
//	private void initExperimentModul()
//	{
//		LOGGER.info("[GUI] init EXPERIMENT modul");
//		experimentPane=new JTabbedPane();
//		
//		ExperimentCompUI expUI=new ExperimentCompUI(); 
//		expUI.buildComponents();
//		model.setExpData(expUI);
//		
//		experimentPane.add("Experiment",expUI);
//		
//		initExperimentUI=true;
//	}
//	
//	private void loadExperimentModule(Experiment exp, Experimenter exper)
//	{
//		LOGGER.info("[GUI] load EXPERIMENT modul");
//		String idxExp="",idxExper="";
//		if(image!=null){
//			//TODO bei neuanlage update refs
//			if(exp==null){
//				idxExp=MetadataTools.createLSID("Experiment",ome.sizeOfExperimentList());
//			}else{
//				idxExp=exp.getID();
//			}
//			if(exper==null){
//				if(exp!=null && exp.getLinkedExperimenter()!=null)
//					idxExper=exp.getLinkedExperimenter().getID();
//				else
//					idxExper=MetadataTools.createLSID("Experimenter",ome.sizeOfExperimenterList());
//			}else{
//				idxExper=exper.getID();
//			}
//			LOGGER.info("EXPERIMENT "+idxExp+" loaded");
//			LOGGER.info("EXPERIMENTER "+idxExper+" loaded");
//
//		}else{
//			idxExp=null;
//			idxExper=null;
//			LOGGER.info("EXPERIMENT data not available");
//			LOGGER.info("EXPERIMENTER data not available");
//		}
//		ExperimentCompUI experimentUI=model.getExpModul();
//		if(experimentUI==null)
//			LOGGER.warning("[GUI] EXPERIMENT modul empty");
//		experimentUI.addData(exp, false);
//		
//		if(importUserData!=null){
//			experimentUI.setName(importUserData.getUser(), ElementsCompUI.OPTIONAL);
//			experimentUI.setGroupName(importUserData.getGroup(), ElementsCompUI.OPTIONAL);
//			experimentUI.setProjectName(importUserData.getProject(), ElementsCompUI.OPTIONAL);
//		}
//	}
//	
//	private void initObjectiveModule() 
//	{
//		LOGGER.info("[GUI] init OBJECTIVE UI");
//		CardLayout cl;
//		cl=new CardLayout();
//		objectivePane=new JPanel(cl);
//		
////		ObjectiveCompUI oUI=new ObjectiveCompUI(); 
//		oUI.buildComponents();
//		
//		ObjectiveSettingsCompUI osUI=new ObjectiveSettingsCompUI();
//		osUI.buildComponents();
//		
//		objectivePane.add(createPropPane(oUI,osUI,"Objective",""),"");
//		model.setObjectiveData(oUI);
////		model.setObjectiveSettingData(osUI);
//		initObjectiveUI=true;
//	}
//	
//	private void loadObjectModule() 
//	{
//		if(initObjectiveUI){
//			ObjectiveCompUI oUI=model.getObjectiveModul();
//			if(image!=null){
//				ObjectiveSettings os=image.getObjectiveSettings();
//				Objective o=null;
//				
//				//TODO addData for objectiveSettings
//				ObjectiveSettingsCompUI osUI=null;//model.getObjectiveSettingsModul();
//				
//				String refID=null;
//				Boolean openChooser=true;
//
//				if(os!=null){
//					openChooser=false;
//					refID=os.getID();
//					int idx=getObjectiveByID(objectives, refID);
//					if(objectives!=null && !objectives.isEmpty()){
//						o=objectives.get(idx);
//					}else{
//						o=null;
//						openChooser=true;
//					}
//				}
//
//				
//				if(openChooser){
//					oUI.setList(objectives);
//					oUI.showOptionPane();
//					LOGGER.info("OBJECTIVE data not available");
//				}else{
//					oUI.addData(o, false);
//					LOGGER.info("OBJECTIVE data loaded");
//				}
//				osUI.addData(os, false);
////				model.getImageModul().setObjectiveSettings(osUI);
//				
////				objectivePane.removeAll();
////				objectivePane.add(createPropPane(oUI,osUI,"Objective",
////						"for image "+image.getName()),image.getName());
//				objectivePane.revalidate();
//				objectivePane.repaint();
//			}else{
//				LOGGER.info("OBJECTIVE data not available");
////				ObjectiveCompUI oUI=new ObjectiveCompUI(null,0);
//				
//			}
//		}
//	}
//
//	
//	private void initImageModul()
//	{
//		LOGGER.info("[GUI] init IMAGE modul");
//		imagePane=new JTabbedPane();
//		initImageUI=true;
//		
//		ImageCompUI imageUI = new ImageCompUI();
//		imageUI.buildComponents();
//		
//		model.setImageModul(imageUI);
//		
//		imagePane.add("Image",imageUI);
//		initImageUI=true;
//		
//	}
//	
//	private void loadImageModule() 
//	{
//		if(image==null){
//			LOGGER.info("[DATA] IMAGE data not available");
//		}else{
//			ImageCompUI ui=model.getImageModul();
//			ui.addData(image, false); 
//
//			// load object data for this image
//			loadObjectModule();
//			LOGGER.info("[DATA] IMAGE data loaded");
//		}
//	}
//	
//	
//
//	protected void initPlanesModule() 
//	{
//		LOGGER.info("[GUI] init PLANE UI");
//		planesUI=new ArrayList<PlaneCompUI>();
//		planeTab=new JTabbedPane();
//		initPlanesUI=true;
//	}
//
//	protected void initDetectorModule() {
//		LOGGER.info("[GUI] init DETECTOR UI");
//		CardLayout cl;
//		cl=new CardLayout();
//		detectorPane=new JPanel(cl);
//		initDetectorUI=true;
//	}
//
//	protected void initLightSrcModule() {
//		LOGGER.info("[GUI] init LIGHTSOURCE UI");
//		CardLayout cl;
//		cl=new CardLayout();
//		lightSrcPane=new JPanel(cl);
//		initLightSrcUI=true;
//	}
//
//	
//	
//	
//	
//	private void initSampleModul()
//	{
//		LOGGER.info("[GUI] init SAMPLE UI");
//		samplePane=new JTabbedPane();
//		SampleCompUI sampleUI=new SampleCompUI(null);
//		sampleUI.buildComponents();
//		model.setSampleData(sampleUI);
//		samplePane.add("Sample",new GBScrollPane(sampleUI));
//		initSampleUI=true;
//	}
//	
//	protected void loadSampleModule()
//	{
//		
//		
//	}
//	
//	
//
//	protected void initLightPathModule() 
//	{
//		LOGGER.info("[GUI] init LIGHTPATH UI");
//		CardLayout cl=new CardLayout();
//		lightPathCardPane=new JPanel(cl);
//		lightPathPane=new JTabbedPane();
//		lightPathPane.add(lightPathCardPane,"LightPath");
//		initLightPathUI=true;
//	}
//
//	protected void initBtnElements() 
//	{
//		// Button on bottom pane for load/save metadata
//		loadBtn=new JButton("Load settings"); // -> Datachooser dialog
//		loadBtn.setSize(30, 7);
//		loadBtn.setEnabled(false);
//		saveBtn=new JButton("Save settings"); // -> Datachooser
//		saveBtn.setSize(30, 7);
//
//		saveBtn.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				if(ome!=null && model!=null){
////					SaveMetadataUserDefinedUI pane = new SaveMetadataUserDefinedUI(ome,model,null,file);
////					JDialog diag=createSaveDialog(pane,"Save MetaData", 600,600);
//					SaveMetadata saver=new SaveMetadata(ome, model, null, file);
//					saver.save();
//				}
//			}
//		});
//
//		resetBtn=new JButton("Reset"); 
//		resetBtn.setSize(30, 7);
//		
//		resetBtn.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				reloadMetaData();
//			}
//		});
//	}
//
//	
//	
//	/**
//	 * @return
//	 */
//	protected JButton initPlaneBtn() 
//	{
//		LOGGER.info("[GUI] init PLANE Button");
//		JButton btnPlanePos=new JButton("Plane/Stage Positions");
//		btnPlanePos.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				
//				PlaneSliderCompUI plane;
//				
//				//create new Dialog with dummy pane?
//				if(planeDialog==null){
//					//add dummy if no data available
////					if(planes==null){
////						plane=new PlaneSliderCompUI();//new PlaneBtnCompUI();
////					}else{
////						plane=new PlaneSliderCompUI(planesUI,numT,numZ,numC);//new PlaneBtnCompUI(planeList,numT,numZ,numC);
////					}
//						
////					planeDialog=createPlaneDialog(plane,"Plane/Stage Positions",400,800);
//				}
//				planeDialog.setVisible(true);
//				
//			}
//		});
//		return btnPlanePos;
//	}
//	
//	/**
//	 * @return
//	 */
//	protected JButton initImgEnvironmentBtn() 
//	{
//		LOGGER.info("[GUI] init IMAGE_ENV Button");
//		JButton btnImgEnv=new JButton("Imaging Environment");
//		btnImgEnv.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				// create new Dialog with dummy pane
//				if(imgEnvDialog==null){
//					if(imgEnvUI!=null){
//						imgEnvDialog=createImgEnvDialog(imgEnvUI, "Imaging Environment",350,150);	
//					}else{
//						LOGGER.warning("IMAGE ENVIRONMENT not available");
//					}
//				}
//				imgEnvDialog.setVisible(true);
////				JDialog diag=createDialog(imgEnvUI);
////				diag.setVisible(true);
//			}
//		});
//		return btnImgEnv;
//	}
//	
//	/**
//	 * Create pane labeled tabbedPane with component main on top and sub on bottom
//	 * @param main element on top
//	 * @param sub element bottom
//	 * @param name tabName
//	 * @param labelText label beside to tabName
//	 * @return
//	 */
//	protected JPanel createPropPane(ElementsCompUI main, ElementsCompUI sub, String name,String labelText)
//	{
//		JTabbedPane lTab=new JTabbedPane();
//		JPanel lPanel=new JPanel();
//		lPanel.setLayout(new BoxLayout(lPanel, BoxLayout.PAGE_AXIS));
//		
//		lPanel.add(main);
//		lPanel.add(sub);
//		lTab.add(name,lPanel);
//		return buildTabbedPaneWithLabel(lTab,labelText);
//	}
//	
//	/**
//	 * Create pane labeled tabbedPane with component main on top and sub on bottom
//	 * @param main element on top
//	 * @param name tabName
//	 * @param labelText label beside to tabName
//	 * @return
//	 */
//	protected JPanel createPropPane(ElementsCompUI main, String name,String labelText)
//	{
//		JTabbedPane lTab=new JTabbedPane();
//		JPanel lPanel=new JPanel();
//		lPanel.setLayout(new BoxLayout(lPanel, BoxLayout.PAGE_AXIS));
//		
//		lPanel.add(main);
//		lTab.add(name,lPanel);
//		return buildTabbedPaneWithLabel(lTab,labelText);
//	}
//	
//	
//	private JDialog createDialog(ImagingEnvironmentCompUI comp)
//	{
////		org.openmicroscopy.shoola.util.ui.ScrollablePanelS
//		JDialog d=new JDialog();
//		d.setTitle("Scrollpane");
//		d.setSize(350,150);
//		d.setModal(true);
//		d.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
////		centerOnParent(d, true);
//		ScrollablePanel panel=new ScrollablePanel();
//		panel.setScrollableWidth( ScrollablePanel.ScrollableSizeHint.FIT );
//		panel.setScrollableBlockIncrement(
//		    ScrollablePanel.VERTICAL, ScrollablePanel.IncrementType.PERCENT, 200);
//		comp.buildComponents();
//		panel.add(comp);
//		JScrollPane scrollPane = new JScrollPane( panel );
//		d.add(scrollPane);
////		d.setVisible(true);
//		return d;
//		
//	}
//	
//	protected JDialog createPlaneDialog(JComponent p,String title, int width, int height)
//	{
//		JDialog d=new JDialog();
//		d.setTitle(title);
//		d.setSize(width,height);
//		d.setModal(true);
//		d.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
////		centerOnParent(d, true);
//		d.add(p);
//		d.pack();
////		d.setVisible(true);
//		return d;
//	}
//	
//	protected JDialog createSaveDialog(JComponent p,String title, int width, int height)
//	{
//		JDialog d=new JDialog();
//		d.setTitle(title);
//		d.setSize(width,height);
//		d.setModal(true);
//		d.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
////		centerOnParent(d, true);
//		d.add(p);
//		d.setVisible(true);
//		d.setResizable(false);
//		return d;
//	}
//	protected JDialog createImgEnvDialog(ImagingEnvironmentCompUI p,String title, int width, int height)
//	{
//		JDialog d=new JDialog();
//		d.setTitle(title);
//		d.setSize(width,height);
//		d.setModal(true);
//		d.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
//		p.buildComponents();
////		centerOnParent(d, true);
//		d.add(p);
//		d.pack();
////		d.setVisible(true);
//		return d;
//	}
//	
//	
//	protected int getLightSrcByID(List<LightSource> list,String id)
//	{
//		int result=-1;
//		if(id==null || id.equals("") || list==null)
//			return result;
//		for(int i=0; i<list.size(); i++){
//			if(list.get(i).getID().equals(id)){
//				return i;
//			}
//		}
//		return result;
//	}
//	protected int getDetectorByID(List<Detector> list,String id)
//	{
//		int result=-1;
//		if(id==null || id.equals("") || list==null)
//			return result;
//		for(int i=0; i<list.size(); i++){
//			if(list.get(i).getID().equals(id)){
//				return i;
//			}
//		}
//		return result;
//	}
//	
//	protected int getObjectiveByID(List<Objective> list,String id)
//	{
//		int result=-1;
//		if(id==null || id.equals("") || list==null)
//			return result;
//		for(int i=0; i<list.size(); i++){
//			if(list.get(i).getID().equals(id)){
//				return i;
//			}
//		}
//		return result;
//	}
//	
//	protected void selectChannel(int chNr) 
//	{	
//		if(channelsUI.isEmpty() || channelsUI.size() <=chNr )
//			return;
//		
//		channelTab.setSelectedIndex(chNr);
//
//		ChannelCompUI channel=(ChannelCompUI)channelsUI.get(chNr);
//		LOGGER.info("select Channel "+channel.getName());
//		
//		String chName=channel.getName();
//		CardLayout cl;
//		
//		// update submodules
//		if(initLightSrcUI){
//			// show referenced lightSrc + settings
//			cl=(CardLayout) lightSrcPane.getLayout();
//			cl.show(lightSrcPane, chName);
//		}
//
//		if(initDetectorUI){
//			// show referenced detector + settings
//			cl=(CardLayout) detectorPane.getLayout();
//			cl.show(detectorPane,chName);
//		}
//
//		if(initLightPathUI){
//			//update lightPath
//			chName=channelTab.getTitleAt(chNr);
//			cl=(CardLayout) lightPathCardPane.getLayout();
//			cl.show(lightPathCardPane, chName);
//		}
//		revalidate();
//		repaint();
//
//	}
//	
//	// Placeholder functions
//	protected void addToPlaceholder(JComponent comp,GUIPlaceholder place, int width )
//	{
//		switch (place) {
//		case Pos_A:
//			addComponent(this,gbl,comp,0,0,width,5,0.25,1.0,GridBagConstraints.BOTH);
//			break;
//		case Pos_B:
//			addComponent(this,gbl,comp,1,0,width,5,0.25,1.0,GridBagConstraints.BOTH);
//			break;
//		case Pos_C:
//			addComponent(this,gbl,comp,2,0,width,5,0.25,1.0,GridBagConstraints.BOTH);
//			break;
//		case Pos_D:
//			addComponent(this,gbl,comp,3,0,width,5,0.25,1.0,GridBagConstraints.BOTH);
//			break;
//		case Pos_E:
//			addComponent(this,gbl,comp,0,5,width,5,0.25,1.0,GridBagConstraints.BOTH);
//			break;
//		case Pos_F:
//			addComponent(this,gbl,comp,1,5,width,5,0.25,1.0,GridBagConstraints.BOTH);
//			break;
//		case Pos_G:
//			addComponent(this,gbl,comp,2,5,width,5,0.25,1.0,GridBagConstraints.BOTH);
//			break;
//		case Pos_H:
//			addComponent(this,gbl,comp,3,5,width,5,0.25,1.0,GridBagConstraints.BOTH);
//			break;
//		case Pos_Bottom:
//			addComponent(this,gbl,comp,2,10,width,1,1.0,0,GridBagConstraints.HORIZONTAL);
//			break;
//		default:
//			LOGGER.severe("Unknown position for element");
//			break;
//		}
//	}
//	
//	
//	
//	public void clearView()
//	{
//		LOGGER.info("******* Clear data view *********");
//		this.removeAll();
//		
//		initChannelUI=false;
//		initImageUI=false;
//		initLightPathUI=false;
//		initLightSrcUI=false;
//		initDetectorUI=false;
//		initObjectiveUI=false;
//		initPlanesUI=false;
//		initImageEnvUI=false;
//		initSampleUI=false;
//		initExperimentUI=false;
//		
//		metadata=null;
//		
//		revalidate();
//		repaint();
//	}
//	
//	public void initView()
//	{
//		initLayout();
//
//		// global buttons
//		initBtnElements();
//
//		readMetaDataFromImage(metadata);
//		
//		// build custom gui
//		initModules(viewProperties.getModules());
//		addModuleToGUI(viewProperties.getModules());
//		
//		revalidate();
//		repaint();
//	}
//	
//	public void saveViewData()
//	{
//		if(model!=null)
//			model.save(); 
//	}
//
//	
//	
//
//
//	//http://stackoverflow.com/questions/213266/how-do-i-center-a-jdialog-on-screen
//	// Center on parent ( absolute true/false (exact center or 25% upper left) )
//	public void centerOnParent(final Window child, final boolean absolute) {
//		child.pack();
//		boolean useChildsOwner = child.getOwner() != null ? 
//				((child.getOwner() instanceof JFrame) || (child.getOwner() instanceof JDialog)) : false;
//				final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
//				final Dimension parentSize = useChildsOwner ? child.getOwner().getSize() : screenSize ;
//				final Point parentLocationOnScreen = useChildsOwner ? child.getOwner().getLocationOnScreen() : new Point(0,0) ;
//				final Dimension childSize = child.getSize();
//				//		    childSize.width = Math.min(childSize.width, screenSize.width);
//				//		    childSize.height = Math.min(childSize.height, screenSize.height);
//				//		    child.setSize(childSize);        
//				int x;
//				int y;
//				if ((child.getOwner() != null) && child.getOwner().isShowing()) {
//					x = (parentSize.width - childSize.width) / 2;
//					y = (parentSize.height - childSize.height) / 2;
//					x += parentLocationOnScreen.x;
//					y += parentLocationOnScreen.y;
//				} else {
//					x = (screenSize.width - childSize.width) / 2;
//					y = (screenSize.height - childSize.height) / 2;
//				}
//				if (!absolute) {
//					x /= 2;
//					y /= 2;
//				}
//				child.setLocation(x, y);
//	}
//
//	static void addComponent( Container cont,
//			GridBagLayout layout,
//			JComponent c,
//			int x, int y,
//			int width, int height,
//			double weightx, double weighty,int fill )
//	{
//		GridBagConstraints gbc = new GridBagConstraints();
//		gbc.anchor=GridBagConstraints.NORTHWEST;
//		gbc.fill = fill;
//		gbc.gridx = x; gbc.gridy = y;
//		gbc.gridwidth = width; gbc.gridheight = height;
//		gbc.weightx = weightx; gbc.weighty = weighty;
//		layout.setConstraints( c, gbc );
//		cont.add( c );
//	}
//
//	protected JPanel getButtonPane()
//	{
//		JPanel buttonPane = new JPanel();
//		GridBagLayout layout=new GridBagLayout();
//		buttonPane.setLayout(layout);
//		Box buttonBox = Box.createHorizontalBox();
//		buttonBox.add(resetBtn);
//		buttonBox.add(Box.createHorizontalStrut(20));
//		buttonBox.add(loadBtn);
//		buttonBox.add(Box.createHorizontalStrut(20));
//		buttonBox.add(saveBtn);
//		addComponent(buttonPane, layout, buttonBox, 2, 4, 1, 1, 0.0, 0.0, GridBagConstraints.NONE);
//
//		return buttonPane;
//	}
//
//	protected int getIDFromLSID(String str)
//	{
//		int index=-1;
//		if(str ==null || str.equals(""))return index;
//		String delims = "[:]";
//		try {
//		String[] tokens = str.split(delims);
//		
//			if(tokens.length > 0){
//				index=Integer.parseInt(tokens[tokens.length-1]);
//			}else{
//				index=Integer.parseInt(str);
//			}
//		} catch (NumberFormatException e) {
//			LOGGER.severe("wrong format for id ("+str+")");
//		}
//		return index;
//	}
//	
//	protected Component[] getComponents(Component container) {
//		ArrayList<Component> list = null;
//
//		try {
//			list = new ArrayList<Component>(Arrays.asList(
//					((Container) container).getComponents()));
//			for (int index = 0; index < list.size(); index++) {
//				for (Component currentComponent : getComponents(list.get(index))) {
//					if(!(currentComponent instanceof TitledSeparator))
//						list.add(currentComponent);
//				}
//			}
//		} catch (ClassCastException e) {
//			list = new ArrayList<Component>();
//		}
//
//		return list.toArray(new Component[list.size()]);
//	}
//
//
//	protected JPanel buildTabbedPaneWithBtn(JTabbedPane tabs)
//	{
//		JPanel panel = new JPanel(new BorderLayout());
//	    panel.add(new JLayer<JComponent>(tabs, new TopRightCornerBtnLayerUI()));
//		
//		return panel;
//	}
//	
//	protected JPanel buildTabbedPaneWithLabel(JTabbedPane tab, String label)
//	{
//		JPanel panel = new JPanel(new BorderLayout());
//		TopRightCornerLabelLayerUI labelUI=new TopRightCornerLabelLayerUI(label);
//	    panel.add(new JLayer<JComponent>(tab,labelUI ));
//		
//		return panel;
//	}
//	
//	class TopRightCornerBtnLayerUI extends LayerUI<JComponent> {
////		  private JLabel l = new JLabel("A Label at right corner");
//		 ImageIcon icon = IconManager.getInstance().getImageIcon(
//	        		IconManager.PLUS_12);
////	        if (icon != null) setIconImage(icon.getImage());
//		 private JButton l = new JButton(icon);
//		
//		 
//		  private JPanel rubberStamp = new JPanel();
//		  @Override public void paint(Graphics g, JComponent c) {
//			  l.addActionListener(new ActionListener() {
//				
//				@Override
//				public void actionPerformed(ActionEvent e) {
//					//show list
//				}
//			});
//		    super.paint(g, c);
//		    Dimension d = l.getPreferredSize();
//		    int x = c.getWidth() - d.width - 5;
//		    SwingUtilities.paintComponent(g, l, rubberStamp, x, 2, d.width, d.height);
//		  }
//	}
//	class TopRightCornerLabelLayerUI extends LayerUI<JComponent> {
//		  private JLabel l = new JLabel("A Label at right corner");
//		
//		  private JPanel rubberStamp = new JPanel();
//		  
//		  public TopRightCornerLabelLayerUI(String labelText) 
//		  {
//			  l.setText(labelText);
//		  }
//		  @Override public void paint(Graphics g, JComponent c) {
//		    super.paint(g, c);
//		    Dimension d = l.getPreferredSize();
//		    int x = c.getWidth() - d.width - 5;
//		    SwingUtilities.paintComponent(g, l, rubberStamp, x, 2, d.width, d.height);
//		  }
//	}
//	
//	class GBScrollPane extends JScrollPane
//	{
//		public GBScrollPane(JComponent comp)
//		{
//			super(comp, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
//					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
//			// to avoid jump when resize the window
////			setPreferredSize(new Dimension(1,1));
//			setPreferredSize(comp.getSize());
//			setViewportBorder(new EmptyBorder(5, 5, 5, 5));
//		}
//	}
//
//	public boolean hasUserInput() 
//	{
//		if(model!=null){
//			return model.userInput();
//		}
//		return false;
//	}
//
//	public MetaDataModel getModel() 
//	{
//		return model;
//	}
//}
