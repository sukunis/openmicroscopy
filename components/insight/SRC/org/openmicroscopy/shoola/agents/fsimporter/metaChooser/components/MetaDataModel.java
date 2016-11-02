package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components;

import java.util.ArrayList;
import java.util.List;

import loci.formats.MetadataTools;
import ome.xml.model.Channel;
import ome.xml.model.Detector;
import ome.xml.model.DetectorSettings;
import ome.xml.model.Dichroic;
import ome.xml.model.Filter;
import ome.xml.model.Image;
import ome.xml.model.ImagingEnvironment;
import ome.xml.model.Instrument;
import ome.xml.model.LightPath;
import ome.xml.model.LightSource;
import ome.xml.model.LightSourceSettings;
import ome.xml.model.OME;
import ome.xml.model.Objective;
import ome.xml.model.ObjectiveSettings;
import ome.xml.model.Pixels;
import ome.xml.model.enums.FilterType;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.format.Sample;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules.ElementsCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules.ImageCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules.PlaneCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules.PlaneSliderCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model.ChannelModel;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model.DetectorModel;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model.ExperimentModel;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model.ImageEnvModel;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model.ImageModel;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model.LightPathModel;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model.LightSourceModel;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model.ObjectiveModel;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model.SampleModel;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagData;
import org.slf4j.LoggerFactory;

//see also ome.xml.model.OME
//TODO use OME
public class MetaDataModel 
{
	
	/** Logger for this class. */
	 private static final org.slf4j.Logger LOGGER =
	    	    LoggerFactory.getLogger(MetaDataModel.class);
	
	/** TODO: module for planeSlider */
	private ElementsCompUI planeSliderUI;
	
	
	/** list of filter data  of current image*/
	private List<Filter> filterList;
	/** list of all dichroics for current image*/
	private List<Dichroic> dichroicList;
	/** list of all lightSrc for current image */
	private List<LightSource> lightSrcOrigList;
	
	
	
	//-----------------------------------------
	/** module for image data */ 
	private ImageModel imgModel;
	/** module for experiment data */
	private ExperimentModel expModel;
	/** module for objective data */
	private ObjectiveModel objModel;
	/** module for imgEnv data */
	private ImageEnvModel imgEnvModel;
	/** module for sample data */
	private SampleModel sampleModel;
	/** module for lightPath data link to channel of current image. List index of element== channelIndex*/
	private LightPathModel lightPathModel;
	/** module for channel data*/
	private ChannelModel channelModel;
	/** module for detector data link to channel of current image */
	private DetectorModel detectorModel;
	/** module for lightsource data link to channel of current image */
	private LightSourceModel lightSrcModel;
	
	private List<TagData> changesImg;
	//------------------------------------------------
	
	/** linked Channel for single filter, because there are more than one filter per channel
	 and maybe two channel have the same kind of filter with differents settings**/
//	private List<Integer> linkedChannelForFilter;

//	private List<Integer> linkedChannelForDichroic;
	
	
	/** list of all planes of selected image*/
	private List<ElementsCompUI> planeList;
	
	//TODO
//	private ElementsCompUI stage;
	private Image imageOME;
	private int imageIndex;
	
	private OME ome;

	private List<TagData> changesImgEnv;

	private List<TagData> changesObj;

	private List<List<TagData>> changesDetector;

	private List<List<TagData>> changesLightSrc;

	private List<TagData> changesSample;
	private List<TagData> changesExperiment;

	private List<List<TagData>> changesChannel;

	private List<LightPath> changesLightPath;

	
	
	
	
	public MetaDataModel()
	{
		imageIndex=-1;
		
		channelModel=null;
		planeList=new ArrayList<ElementsCompUI>();
		lightSrcModel=null;
		
		lightPathModel=null;
		
		
		imgModel=new ImageModel();
		
	}

	
	
	public void resetData()
	{
		System.out.println("#MetaDataModel::resetData()");
		expModel=null;
		sampleModel=null;
		objModel=null;
		imgEnvModel=null;
//		planeSliderUI.clearDataValues();
		imgModel=null;
		
		lightSrcModel=null;
		detectorModel=null;
		lightPathModel=null;
		
	}
	
	
	
	
	
	
	
//	public void setImageProp(int imgIdx, int _numOfChannels)
//	{
//		imageIndex=imgIdx;
//		numOfChannels=_numOfChannels;
//		
//		
//		channelList=new ArrayList<ElementsCompUI>(numOfChannels);
//		planeList=new ArrayList<ElementsCompUI>();
//		lightSrcList=new ArrayList<ElementsCompUI>(numOfChannels);
//		detectorList=new ArrayList<ElementsCompUI>(numOfChannels);
//		
//		lightPathList=new ArrayList<ElementsCompUI>(numOfChannels);
//	}
	
	public void setOME(OME o)
	{
		LOGGER.info("[DEBUG] set ome");
		ome=o;
	}
	
	public OME getOME()
	{
		return ome;
	}
	public void setImageOMEData(Image i)
	{
		LOGGER.info("[DEBUG] set image ome");
		imageOME=i;
	}
	
	/**
	 * Get image ome data merged with image modul input
	 * @return
	 */
	public Image getImageOMEData()
	{
		//merge GUI and ome data
		try {
			Image in=getImageData();
			mergeData(in,imageOME); 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return imageOME;
	}
	
	private void mergeData(Image in, Image imageOME) 
	{
		if(imageOME==null ){
			if(in==null){
				LOGGER.error("failed to merge IMAGE data");
			}else{
				imageOME=in;
			}
			return;
		}else if(in==null){
			return;
		}
		
		if(in.getName()!=null && !in.getName().equals(""))
			imageOME.setName(in.getName());
		
		imageOME.setAcquisitionDate(in.getAcquisitionDate());
		
		Pixels pOME=imageOME.getPixels();
		Pixels pIN=in.getPixels();
		
		pOME.setSizeX(pIN.getSizeX());
		pOME.setSizeY(pIN.getSizeY());
		pOME.setType(pIN.getType());
		pOME.setPhysicalSizeX(pIN.getPhysicalSizeX());
		pOME.setPhysicalSizeY(pIN.getPhysicalSizeY());
		pOME.setSizeZ(pIN.getSizeZ());
		pOME.setSizeT(pIN.getSizeT());
		pOME.setSizeC(pIN.getSizeC());
		pOME.setTimeIncrement(pIN.getTimeIncrement());
		
		imageOME.setStageLabel(in.getStageLabel());
		
		//TODO: step size, well#
		
	}
	
	
	
	public int getPixelsDimZ()
	{
		return imageOME!=null ? imageOME.getPixels().getSizeZ().getValue() : 0;
	}
	
	public int getPixelsDimC()
	{
		return imageOME!=null ? imageOME.getPixels().getSizeC().getValue() : 0;
	}
	public int getPixelsDimT()
	{
		return imageOME!=null ? imageOME.getPixels().getSizeT().getValue() : 0;
	}
	
	//----------------------------------------------

	
	public SampleModel getSampleModel() 
	{
		if(sampleModel==null)
			sampleModel=new SampleModel();
		return sampleModel;
	}
	
	
	public Sample getSample() throws Exception
	{
		if(sampleModel==null)
			return null;
		return sampleModel.getSample();
	}
	
	public void addData(Sample s, boolean overwrite) 
	{
		System.out.println("# MetaDataModel::addData() - Sample ");
		if(sampleModel==null)
			sampleModel=new SampleModel();
		
		sampleModel.addData(s, overwrite);
	}
	

	
	//-------------------------------------
	public ExperimentModel getExperimentModel()
	{
		if(expModel==null)
			expModel=new ExperimentModel();
		return expModel;
	}
	
	public void addData(ExperimentModel e, boolean overwrite) throws Exception 
	{
		System.out.println("# MetaDataModel::addData() - Experiment ");	
		if(expModel==null)
			expModel=new ExperimentModel();
		
		expModel.addData(e.getExperiment(), overwrite);
		expModel.addData(e.getExperimenter(), overwrite);	
		
		if(overwrite){
			expModel.setProjectPartner(e.getProjectPartnerName());
		}else{
					
//			if(expModel.getGroupName()==null || expModel.getGroupName().equals("")){
//					expModel.setGroupName(e.getGroupName());
//					System.out.println("\t...group name = "+e.getGroupName());
//			}else{
//				System.out.println("\t...group name doesn't set");
//			}
//			if(expModel.getProjectName()==null || expModel.getProjectName().equals(""))
//				expModel.setProjectName(e.getProjectName());
			if(expModel.getProjectPartnerName()==null || expModel.getProjectPartnerName().equals(""))
				expModel.setProjectPartner(e.getProjectPartnerName());
			
		}
	}
	/**
	 * Set group, project name and experimenter list data
	 * @param expCont
	 */
	public void setExtendedData(ExperimentModel expCont) 
	{
		System.out.println("# MetaDataModel::setExtendedData - Experiment");
		if(expModel==null)
			expModel=new ExperimentModel();
		
		expModel.setGroupName(expCont.getGroupName());
		expModel.setProjectName(expCont.getProjectName());
		expModel.setExperimenter(expCont.getExperimenter());
	}
	
	/*-------------------------------------------
	 * Image
	 -------------------------------------------*/
	
	
	public ImageModel getImageModel() 
	{
		if(imgModel==null)
			imgModel=new ImageModel();
		return imgModel;
	}
	
	public void addData(Image i,boolean overwrite)
	{
		System.out.println("# MetaDataModel::addData - Image");
		if(imgModel==null)
			imgModel=new ImageModel();
		imgModel.addData(i, overwrite);
	}

	/**
	 * Get image modul data
	 * @return
	 * @throws Exception
	 */
	public Image getImageData() throws Exception
	{
		if(imgModel==null)
			return null;
		return imgModel.getImage();
	}

	
	


	/*-------------------------------------------
	 * Channel
	 -------------------------------------------*/
	
	
	public void addData(Channel c,boolean overwrite,int index)
	{
		System.out.println("# MetaDataModel::addData - Channel "+index);
		if(channelModel==null)
			channelModel=new ChannelModel();
		
		channelModel.addData(c, overwrite, index);
	}
	
	public int getNumberOfChannels()
	{
		if(channelModel==null)
			return 0;
		return channelModel.getNumberOfChannels();
	}
	
	public Channel getChannelData(int index) throws Exception
	{
		if(channelModel==null || getNumberOfChannels()==0 )
			return null;

		return  channelModel.getChannel(index); 
	}
	
	public ChannelModel getChannelModel() 
	{
		if(channelModel==null )
			channelModel=new ChannelModel();
		
		return channelModel;
	}
	
	
	/*------------------------------------------------------
	 * Detector
	 -------------------------------------------------------*/
	public void addData(Detector d, boolean overwrite, int index) throws Exception 
	{
		System.out.println("# MetaDataModel::addData - Detector "+index);
		if(detectorModel==null)
			detectorModel=new DetectorModel();
		detectorModel.addData(d, overwrite,index);
	}



	public void addData(DetectorSettings ds, boolean overwrite, int index) throws Exception 
	{
		System.out.println("# MetaDataModel::addData - DetectorSettings "+index);
		if(detectorModel==null)
			detectorModel=new DetectorModel();
		detectorModel.addData(ds, overwrite,index);
	}



	public void addToDetectorList(List<Detector> micDetectorList, boolean append) 
	{
		if(!append)
			detectorModel.clearList();
		
		detectorModel.addToList(micDetectorList);
			
	}
	
	public DetectorModel getDetectorModel() 
	{
		if(detectorModel==null ){
			detectorModel=new DetectorModel();
		}
		return 	detectorModel;
	}
	
	
	public int getNumberOfDetectors()
	{
		if(detectorModel==null)
			return 0;
		return detectorModel.getNumberOfElements();
	}
	
	public Detector getDetector(int index) throws Exception
	{
		Detector res=null;


		if(detectorModel==null || getNumberOfDetectors()==0 ){
			return res;
		}
		res=detectorModel.getDetector(index);
		DetectorSettings dSett=getDetectorSettings(index);
		// check if this detector is linked to channel
		if(res!=null && !detectorIsLinkedToChannel(dSett, res.getID())){
			LOGGER.info("[DEBUG] detector is not linked");
			if(imageOME!=null){
				if(imageOME.getLinkedInstrument()==null ){
					createAndLinkNewInstrument(ome);
				}
				linkDetector(dSett,res,imageIndex,imageOME.getLinkedInstrument().sizeOfDetectorList());
				//					((ObjectiveCompUI)objectiveUI).addData(oSett,true);
			}else{
				LOGGER.info("[DEBUG] can't link detector. ");
			}
		}
		return res;
	}
	
	
	private void linkDetector(DetectorSettings dSett, Detector d,
			int imgIdx, int sizeOfDetectorList) 
	{
		if(d==null)
			return;
		else{
			if(d.getID()==null || d.getID().equals("")){
				d.setID(MetadataTools.createLSID("Detector", imgIdx,sizeOfDetectorList));
			}
			
			// new objectiveSettings for this image
			if(dSett==null){
				LOGGER.info("[DEBUG] no detector settings available for current channel");
				dSett=new DetectorSettings();
			}
			dSett.setID(d.getID());
			// link to given object
			LOGGER.info("[DEBUG] link to detector id = "+d.getID());
			dSett.setDetector(d);
		}
	}

	private boolean detectorIsLinkedToChannel(DetectorSettings sett,String id) 
	{
		boolean res=false;
		if(sett!=null && sett.getDetector()!=null && sett.getDetector().getID().equals(id))
			res=true;
		
		return res;
	}
	
	public DetectorSettings getDetectorSettings(int index) throws Exception
	{
		if(detectorModel==null)
			return null;
		
		return detectorModel.getSettings(index);
	}
	

	
	/*--------------------------------------------
	 * LightPath
	 *--------------------------------------------*/
	public void addData(LightPath lp, boolean overwrite, int i) throws Exception 
	{
		System.out.println("# MetaDataModel::addData - LightPath "+i);
		if(lightPathModel==null)
			lightPathModel=new LightPathModel();
		
		lightPathModel.addData(lp, overwrite, i);
	}

	public void addFilterToList(List<Filter> list, boolean append) 
	{
		if(!append)
			lightPathModel.addFilterToList(list);
	}

	public void addDichroicToList(List<Dichroic> list, boolean append) {
		if(!append)
			lightPathModel.addDichroicToList(list);
	}
	
	

	public LightPath getLightPath(int index) throws Exception
	{
		if(lightPathModel==null || getNumberOfLightPath()==0){
			LOGGER.info("No lightPath available for channel "+index);
			return null;
		}
//		updateLightPathElems( lightPathObjectList, chIdx);
		
		return lightPathModel.getLightPath(index);
	}
	

	public LightPathModel getLightPathModel()
	{
		if(lightPathModel==null )
			lightPathModel=new LightPathModel();
		return lightPathModel;
	}
	
	public int getNumberOfLightPath() {
		if(lightPathModel==null)
			return 0;
		return lightPathModel.getNumberOfLightPaths();
	}
	
	
	/*--------------------------------------------
	 * LightSource
	 *--------------------------------------------*/
	public void addData(LightSource l, boolean overwrite, int i) throws Exception 
	{
		System.out.println("# MetaDataModel::addData - LightSource "+i);
		if(lightSrcModel==null)
			lightSrcModel=new LightSourceModel();
		
		lightSrcModel.addData(l, overwrite, i);
	}

	public void addData(LightSourceSettings ls, boolean overwrite, int i) throws Exception {
		if(lightSrcModel==null)
			lightSrcModel=new LightSourceModel();
		
		lightSrcModel.addData(ls, overwrite, i);		
	}

	public void addToLightSrcList(List<LightSource> micLightSrcList, boolean append) 
	{
		if(!append)
			lightSrcModel.clearList();
		
		lightSrcModel.addToList(micLightSrcList);		
	}
	
	public LightSourceModel getLightSourceModel()
	{
		if(lightSrcModel==null)
			lightSrcModel=new LightSourceModel();
		
		return lightSrcModel;
	}
	
	
	
	public int getNumberOfLightSrc()
	{
		if(lightSrcModel==null)
			return 0;
		return lightSrcModel.getNumberOfLightSrc();
	}

	public LightSource getLightSourceData(int index) throws Exception
	{
		LightSource res=null;
		if(lightSrcModel==null || getNumberOfLightSrc()==0){
			return res;
		}
		res=lightSrcModel.getLightSource(index);
		LightSourceSettings lSett=getLightSourceSettings(index);
		// check if this detector is linked to channel
		if(res!=null && !lightSrcIsLinkedToChannel(lSett, res.getID())){
			LOGGER.info("[DEBUG] lightSrc is not linked");
			if(imageOME!=null){
				if(imageOME.getLinkedInstrument()==null ){
					createAndLinkNewInstrument(ome);
				}
				linkLightSrc(lSett,res,imageIndex,imageOME.getLinkedInstrument().sizeOfLightSourceList());
			}else{
				LOGGER.info("[DEBUG] can't link lightSrc. ");
			}
		}
		return res;
	}
	
	private void linkLightSrc(LightSourceSettings dSett, LightSource d,
			int imgIdx, int sizeOfList) 
	{
		if(d==null)
			return;
		else{
			if(d.getID()==null || d.getID().equals("")){
				d.setID(MetadataTools.createLSID("LightSource", imgIdx,sizeOfList));
			}
			
			// new objectiveSettings for this image
			if(dSett==null){
				LOGGER.info("[DEBUG] no lightSrc settings available for current channel");
				dSett=new LightSourceSettings();
			}
			dSett.setID(d.getID());
			// link to given object
			LOGGER.info("[DEBUG] link to lightSrc id = "+d.getID());
			dSett.setLightSource(d);
		}
	}

	private boolean lightSrcIsLinkedToChannel(LightSourceSettings sett,String id) 
	{
		boolean res=false;
		if(sett!=null && sett.getLightSource()!=null && sett.getLightSource().getID().equals(id))
			res=true;
		
		return res;
	}
	
	public LightSourceSettings getLightSourceSettings(int index) throws Exception
	{
		if(lightSrcModel==null ||getNumberOfLightSrc()==0){
			return null;
		}
		return lightSrcModel.getSettings(index);
	}
	
	
	
	
	

	public PlaneSliderCompUI getPlaneModel()
	{
		return (PlaneSliderCompUI)planeSliderUI;
	}
	
	public void setPlaneModel(PlaneSliderCompUI p)
	{
		planeSliderUI=p;
	}
	
	public ImageEnvModel getImgEnvModel() 
	{
		if(imgEnvModel==null)
			imgEnvModel=new ImageEnvModel();
		return imgEnvModel;
	}
	
	public void addData(ImagingEnvironment i,boolean overwrite)
	{
		if(imgEnvModel==null)
			imgEnvModel=new ImageEnvModel();
		
		imgEnvModel.addData(i, overwrite);
	}
	
	public ImagingEnvironment getImgagingEnv() throws Exception
	{
		if(imgEnvModel==null)
			return null;

		return imgEnvModel.getImgEnv();
	}
	
	
	
	public void createAndLinkNewInstrument(OME o)
	{
		 Instrument i=new Instrument();
		 i.setID(MetadataTools.createLSID("Instrument", o.sizeOfInstrumentList()));
		 imageOME.linkInstrument(i);
		 o.addInstrument(i);
		 LOGGER.info("[DEBUG] create new Instrument : "+i.getID());
	}
	
	
	/*--------------------------------------------
	 * Objective
	 *--------------------------------------------*/
	
	public void addData(Objective o,boolean overwrite) throws Exception
	{
		System.out.println("# MetaDataModel::addData() - Objective");
		if(objModel==null)
			objModel=new ObjectiveModel();
		
		objModel.addData(o, overwrite);
	}

	public void addData(ObjectiveSettings os, boolean overwrite) throws Exception 
	{
		System.out.println("# MetaDataModel::addData() - ObjectiveSettings");
		if(objModel==null)
			objModel=new ObjectiveModel();
		objModel.addData(os, overwrite);		
	}
	
	public ObjectiveModel getObjectiveModel()
	{
		if(objModel==null)
			objModel=new ObjectiveModel();
		return objModel;
	}
	
	public ObjectiveSettings getObjectiveSettings() throws Exception
	{
		if(objModel==null)
			return null;
		return objModel.getSettings();
	}
	
	public void addToObjList(List<Objective> list, boolean append) 
	{
		if(!append)
			objModel.clearList();
		objModel.addToList(list);
	}
	/**
	 * Get objective modul data
	 * @return
	 * @throws Exception
	 */
	public Objective getObjectiveData() throws Exception
	{
		if(objModel==null)
			return null;
		
		Objective obj=objModel.getObjective();
		ObjectiveSettings oSett=getObjectiveSettings();
		
		// check if this objective is linked to image
		if(obj!=null && !objectiveIsLinkedToImage(oSett,obj.getID())){
			LOGGER.info("[DEBUG] objective is not linked");
			if(imageOME!=null){
				if(imageOME.getLinkedInstrument()==null ){
					createAndLinkNewInstrument(ome);
				}
				linkObjective(oSett,obj,imageIndex,imageOME.getLinkedInstrument().sizeOfObjectiveList());
				//					((ObjectiveCompUI)objectiveUI).addData(oSett,true);
			}else{
				LOGGER.info("[DEBUG] can't link objective. ");
			}
		}
		
		return obj;
	}

	private void linkObjective(ObjectiveSettings oSett,
			Objective o, int imgIdx, int sizeOfObjectiveList) 
	{
		if(o==null)
			return;
		else{
			if(o.getID()==null || o.getID().equals("")){
				o.setID(MetadataTools.createLSID("Objective", imgIdx,sizeOfObjectiveList));
			}
			
			// new objectiveSettings for this image
			if(oSett==null){
				LOGGER.info("[DEBUG] no objective settings available for current image");
				oSett=new ObjectiveSettings();
			}
			oSett.setID(o.getID());
			// link to given object
			LOGGER.info("[DEBUG] link to objective id = "+o.getID());
			oSett.setObjective(o);
		}
	}

	private boolean objectiveIsLinkedToImage(ObjectiveSettings sett,String id) 
	{
		boolean res=false;
		if(sett!=null && sett.getObjective()!=null &&  sett.getObjective().getID()!=null && sett.getObjective().getID().equals(id))
			res=true;
		
		return res;
	}

	
	
	public void setFilterList(List<Filter> list)
	{
		filterList=list;
//		linkedChannelForFilter=new ArrayList<Integer>(Collections.nCopies(filterList.size(),-1));
	}
	
	public void setDichroicList(List<Dichroic> list)
	{
		dichroicList=list;
//		linkedChannelForDichroic=new ArrayList<Integer>(Collections.nCopies(dichroicList.size(), -1));
	}
	

	
	/** TODO: if no primD exists convert first emF of type dichroic to primD
	 *  Add elements of list to the list of filter and dichroics of this image.
	 Note: set ID for new elements and return list completed by ID
	 @return input list completed by id's
	 **/
	public LightPath updateLightPathElems(LightPath lP,int channelIdx)
	{
		if(filterList==null)
			filterList=new ArrayList<Filter>();
			
		if(imageIndex!=-1){
			int id1=imageIndex;
			int id2=channelIdx;
			boolean primDExists=true;

			int idxList=0;
			Dichroic d=lP.getLinkedDichroic();
			
			if(d!=null){
				if(d.getID()==null || d.getID().equals("")){
					String id=appendNewDichroic(d, id1, dichroicList.size(), channelIdx);
					d.setID(id);
				}else{
					int listIndex=identifyDichroic(d, channelIdx);
					if(listIndex!=-1){
						dichroicList.set(listIndex, d);
					}else{
						String id=appendNewDichroic(d,id1,dichroicList.size(),channelIdx);
						d.setID(id);
					}
				}
				lP.linkDichroic(d);
			}
			
			List<Filter> emF=lP.copyLinkedEmissionFilterList();
			
			for(int i=0; i<emF.size(); i++){
				Filter f=emF.get(i);
				if(f.getID()==null || f.getID().equals("")){
					String id=appendNewFilter(f,id1,filterList.size(),channelIdx);
					f.setID(id);
				}else{
					int listIndex=identifyFilter(f, channelIdx);
					if(listIndex!=-1){
						filterList.set(listIndex, f);
					}else{
						String id=appendNewFilter(f,id1,filterList.size(),channelIdx);
						f.setID(id);
					}
				}
				lP.setLinkedEmissionFilter(i, f);
			}
			
			List<Filter> exF=lP.copyLinkedExcitationFilterList();
			
			for(int i=0; i<exF.size(); i++){
				Filter f=exF.get(i);
				if(f.getID()==null || f.getID().equals("")){
					String id=appendNewFilter(f,id1,filterList.size(),channelIdx);
					f.setID(id);
				}else{
					int listIndex=identifyFilter(f, channelIdx);
					if(listIndex!=-1){
						filterList.set(listIndex, f);
					}else{
						String id=appendNewFilter(f,id1,filterList.size(),channelIdx);
						f.setID(id);
					}
				}
				lP.setLinkedExcitationFilter(i, f);
			}
			
		}
		return lP;
	}

	
	
	
	
	private String appendNewFilter(Filter f, int id1, int id2, int chIdx)
	{
		f.setID(MetadataTools.createLSID("Filter", id1,	id2));
		filterList.add(f);
//		linkedChannelForFilter.add(chIdx);
		
		return f.getID();
	}
	
	private String appendNewDichroic(Dichroic f, int id1, int id2, int chIdx)
	{
		f.setID(MetadataTools.createLSID("Dichroic", id1,	id2));
		dichroicList.add(f);
//		linkedChannelForDichroic.add(chIdx);
		
		return f.getID();
	}
	
	private int identifyFilter(Filter f, int chIdx)
	{
		int listIndex=-1;
		for(int i=0; i<filterList.size(); i++){
			if(filterList.get(i).getID().equals(f.getID())){
//				if(linkedChannelForFilter.get(i)==chIdx){
					return i;
//				}
			}
		}
		
		return listIndex;
	}
	
	private int identifyDichroic(Dichroic f, int chIdx)
	{
		int listIndex=-1;
		for(int i=0; i<dichroicList.size(); i++){
			if(dichroicList.get(i).getID().equals(f.getID())){
//				System.out.println("identifyDich "+f.getID()+"("+chIdx+", "+linkedChannelForDichroic.get(i)+")");
				return i;
//				if(linkedChannelForDichroic.get(i)==chIdx){
//					return i;
//				}else{
//					return -1;
//				}
			}
		}
		
		return listIndex;
	}
	
	private void printFilter(String s,Filter f)
	{
		try{
		System.out.println(s+" Filter "+f.getID()!=null ? f.getID() : "");
		System.out.println("Model "+f.getModel()!=null ? f.getModel() : "");
		System.out.println("Type "+f.getType()!=null ? f.getType().toString() : "");
		}catch(Exception e){}
	}
	
	
	public List<Filter> getFilterList()
	{
		return filterList;
	}
	
	public List<Dichroic> getDichroicList()
	{
		return dichroicList;
	}
	
	public List<Object> getFilterAndDichroics()
	{
		List<Object> o=new ArrayList<Object>();
		if(filterList!=null){
			for(Filter f:filterList){
				o.add(f);
			}
		}
		if(dichroicList!=null){
			for(Dichroic d: dichroicList){
				o.add(d);
			}
		}
		return o;
	}
	
	
	

	public int getImageIndex() {
		return imageIndex;
	}
	
	public void setImageIndex(int idx){
		imageIndex=idx;
	}

	public void setPrimDichroic(int chIdx) 
	{
		
	}
	


	
	
	public static final Dichroic convertFilterToDichroic(Filter f)
	{
		Dichroic d=new Dichroic();
		d.setID(f.getID());
		d.setModel(f.getModel());
		d.setManufacturer(f.getManufacturer());
		d.setLotNumber(f.getLotNumber());
		d.setSerialNumber(f.getSerialNumber());
		d.setInstrument(f.getInstrument());
		//TODO
//		d.setLinkedAnnotation(index, o)
//		d.setLinkedFilterSet(index, o)
//		d.setLinkedLightPath(index, o)
		return d;
	}
	
	public static final Filter convertDichroicToFilter(Dichroic d)
	{
		Filter f=new Filter();
		f.setID(d.getID());
		f.setModel(d.getModel());
		f.setManufacturer(d.getManufacturer());
		f.setLotNumber(d.getLotNumber());
		f.setSerialNumber(d.getSerialNumber());
		f.setInstrument(d.getInstrument());
		f.setType(FilterType.DICHROIC);
//		f.setLinkedAnnotation(index, o)
		
		return f;
	}
	

	public class Pair<L,R> {
	    private L l;
	    private R r;
	    public Pair(L l, R r){
	        this.l = l;
	        this.r = r;
	    }
	    public L getL(){ return l; }
	    public R getR(){ return r; }
	    public void setL(L l){ this.l = l; }
	    public void setR(R r){ this.r = r; }
	}


	

	public PlaneCompUI getPlaneModul(int i) {
		// TODO Auto-generated method stub
		return planeList!=null ? (PlaneCompUI) planeList.get(i):null;
	}
	public void addPlaneModul(PlaneCompUI pUI) 
	{
		if(planeList!=null)
			planeList.add(pUI);
	}
	public List<ElementsCompUI> getPlaneModulList() {
		// TODO Auto-generated method stub
		return planeList;
	}
	
	public int getNumberOfPlanes() 
	{
		return planeList!=null ? planeList.size():0;
	}

	/**
	 * Set list of modified tags for image view
	 * @param list
	 */
	public void setChangesImage(List<TagData> list)
	{
		changesImg=list;
	}
	
	public List<TagData> getChangesImage()
	{
		return changesImg;
	}


/**
 * Adapt number of channel, detector, lightSrc and lightPath, check lightSrc types 
 * and update data
 * @param metaDataModel
 * @throws Exception
 */
	public void updateData(MetaDataModel metaDataModel) throws Exception 
	{
		if(metaDataModel==null)
			return;
		
		if(imageOME==null){
			//update all modules
			System.out.println("# MetaDataModel::updateData() -- DIR");
			// adapt number of channels, detectors, lightPath and LightSrc
			
			//channels
			int childNumberOfObjects=this.getNumberOfChannels();
			int parentNumberOfObjects=metaDataModel.getNumberOfChannels();
			if(childNumberOfObjects < parentNumberOfObjects && 
					metaDataModel.getChangesChannel()!=null){
				for(int i=childNumberOfObjects;i<parentNumberOfObjects;i++){
					this.addData(metaDataModel.getChannelData(i), true, i);
					//this channel doesn't have to update, because it was added directly
					metaDataModel.getChangesChannel().set(i, null);
				}
			}
			else if(childNumberOfObjects > parentNumberOfObjects){
				//something deleted
				for(int i=parentNumberOfObjects; i<childNumberOfObjects;i++){
					this.getChannelModel().remove(i);
				}
			}
			
			//detector
			childNumberOfObjects=this.getNumberOfDetectors();
			parentNumberOfObjects=metaDataModel.getNumberOfDetectors();
			if(childNumberOfObjects < parentNumberOfObjects && 
					metaDataModel.getChangesDetector()!=null){
				for(int i=childNumberOfObjects;i<parentNumberOfObjects;i++){
					this.addData(metaDataModel.getDetector(i), true, i);
					this.addData(metaDataModel.getDetectorSettings(i),true,i);
					//this detector doesn't have to update, because it was added directly
					metaDataModel.getChangesDetector().set(i, null);
				}
			}else if(childNumberOfObjects > parentNumberOfObjects){
				//something deleted
				for(int i=parentNumberOfObjects; i<childNumberOfObjects;i++){
					this.getDetectorModel().remove(i);
				}
			}
			
			//lightSrc
			childNumberOfObjects=this.getNumberOfLightSrc();
			parentNumberOfObjects=metaDataModel.getNumberOfLightSrc();
			if(metaDataModel.getChangesLightSrc()!=null){
				//check first if available lightSrc of child the type has changed
				for(int i=0; i<childNumberOfObjects;i++){
					List<TagData> changes=metaDataModel.getChangesLightSrc().get(i);
					String type=changes.get(changes.size()-1).getTagValue();
					if(!this.getLightSourceModel().getLightSource(i).getClass().getSimpleName().equals(type)){
						this.addData(metaDataModel.getLightSourceData(i), true, i);
					}
				}
			}
			
			if(childNumberOfObjects < parentNumberOfObjects && 
					metaDataModel.getChangesLightSrc()!=null){
				for(int i=childNumberOfObjects;i<parentNumberOfObjects;i++){
					this.addData(metaDataModel.getLightSourceData(i), true, i);
					this.addData(metaDataModel.getLightSourceSettings(i), true, i);
					//this lightsrc doesn't have to update, because it was added directly
					metaDataModel.getChangesLightSrc().set(i, null);
				}
			}else if(childNumberOfObjects > parentNumberOfObjects){
				//something deleted
				for(int i=parentNumberOfObjects; i<childNumberOfObjects;i++){
					this.getLightSourceModel().remove(i);
				}
			}
			
			//lightPath
			childNumberOfObjects=this.getNumberOfLightPath();
			parentNumberOfObjects=metaDataModel.getNumberOfLightPath();
			if(childNumberOfObjects < parentNumberOfObjects && 
					metaDataModel.getChangesLightPath()!=null){
				for(int i=childNumberOfObjects;i<parentNumberOfObjects;i++){
					this.addData(metaDataModel.getLightPath(i), true, i);
					//this lightpath doesn't have to update, because it was added directly
						metaDataModel.getChangesLightPath().set(i, null);
				}
			}else if(childNumberOfObjects > parentNumberOfObjects){
				//something deleted
				for(int i=parentNumberOfObjects; i<childNumberOfObjects;i++){
					this.getLightPathModel().remove(i);
				}
			}
			
			
			update(metaDataModel,0);
		}else{
			System.out.println("# MetaDataModel::updateData() -- FILE");
			if(getNumberOfChannels()>1){
				
			}else{
				update(metaDataModel,0);
			}
		}
		
		
	}
	
	private void update(MetaDataModel metaDataModel,int index) throws Exception
	{
		if(imgModel!=null)imgModel.update(metaDataModel.getChangesImage());
		if(imgEnvModel!=null)imgEnvModel.update(metaDataModel.getChangesImgEnv());
		if(channelModel!=null)channelModel.update(metaDataModel.getChangesChannel()); 
		if(objModel!=null)objModel.update(metaDataModel.getChangesObject()); 
		if(detectorModel!=null) detectorModel.update(metaDataModel.getChangesDetector());
//		lightSrcModel.update(metaDataModel.getChangesLightSrc());
		if(lightPathModel!=null) lightPathModel.update(metaDataModel.getChangesLightPath());
		if(sampleModel!=null)sampleModel.update(metaDataModel.getChangesSample()); 
		if(expModel!=null)expModel.update(metaDataModel.getChangesExperiment());
	}

	
	/**
	 * Clear list of changes if status=false
	 * @param changes
	 */
	public void setDataChange(boolean status)
	{
		if(!status){
			// reset state
			changesImg=null;
			changesImgEnv=null;
			changesObj=null;
			changesDetector=null;
			changesLightSrc=null;
			changesLightPath=null;
			changesChannel=null;
			changesSample=null;
			changesExperiment=null;
		}
	}
	
	public boolean noticUserInput()
	{
		System.out.println("# MetaDataModel::noticeUserInput()");
		boolean res=false;
		res=res || (changesImg!=null && !changesImg.isEmpty())
				|| (changesImgEnv!=null && !changesImgEnv.isEmpty())
				|| (changesObj!=null && !changesObj.isEmpty())
				|| (changesDetector!=null && !changesDetector.isEmpty())
				|| (changesLightSrc!=null && !changesLightSrc.isEmpty())
				|| (changesLightPath!=null && !changesLightPath.isEmpty())
				|| (changesChannel!=null && !changesChannel.isEmpty())
				|| (changesSample!=null && !changesSample.isEmpty())
				|| (changesExperiment!=null && !changesExperiment.isEmpty());
		
		System.out.println("\t...Changes Image: "+(changesImg!=null && !changesImg.isEmpty()));
		System.out.println("\t...Changes Objective: "+(changesObj!=null && !changesObj.isEmpty()));
		System.out.println("\t...Changes Detector: "+(changesDetector!=null && !changesDetector.isEmpty()));
		System.out.println("\t...Changes LightSrc: "+(changesLightSrc!=null && !changesLightSrc.isEmpty()));
		System.out.println("\t...Changes LightPath: "+(changesLightPath!=null && !changesLightPath.isEmpty()));
		System.out.println("\t...Changes Channel: "+(changesChannel!=null && !changesChannel.isEmpty()));
		System.out.println("\t...Changes Sample: "+(changesSample!=null && !changesSample.isEmpty()));
		System.out.println("\t...Changes Experiment: "+(changesExperiment!=null && !changesExperiment.isEmpty()));
		System.out.println("\t...Changes ImgEnv: "+(changesImgEnv!=null && !changesImgEnv.isEmpty()));
				
		return res;
	}

	public void setChangesImageEnv(List<TagData> newValue) {
		changesImgEnv=newValue;
	}
	public List<TagData> getChangesImgEnv()
	{
		return changesImgEnv;
	}

	public void setChangesObject(List<TagData> newValue) {
		changesObj=newValue;
	}
	public List<TagData> getChangesObject()
	{
		return changesObj;
	}

	public List<List<TagData>> getChangesChannel()
	{
		return changesChannel;
	}
	
	public void setChangesChannel(List<TagData> newValue,int index)
	{
		if(changesChannel==null)
			changesChannel=new ArrayList<List<TagData>>();
		
		if(index>=changesChannel.size()){
			//expand list
			while(changesChannel.size()<=index)
				changesChannel.add(null);
		}
		changesChannel.set(index, newValue);
	}
	
	public void setChangesDetector(List<TagData> newValue,int index) 
	{
		if(changesDetector==null)
			changesDetector=new ArrayList<List<TagData>>();
		if(index>=changesDetector.size()){
			while(changesDetector.size()<=index)
				changesDetector.add(null);
		}
		changesDetector.set(index, newValue);		
	}
	public List<List<TagData>> getChangesDetector()
	{
		return changesDetector;
	}

	public void setChangesLightSrc(List<TagData> newValue,int index) {
		if(changesLightSrc==null)
			changesLightSrc=new ArrayList<List<TagData>>();
		
		if(index>=changesLightSrc.size()){
			//expand list
			while(changesLightSrc.size()<=index)
				changesLightSrc.add(null);
		}
		changesLightSrc.set(index, newValue);	
	}
	public List<List<TagData>> getChangesLightSrc()
	{
		return changesLightSrc;
	}
	
	public void setChangesLightPath(LightPath lightPath,int index) 
	{
		if(changesLightPath==null)
			changesLightPath=new ArrayList<LightPath>();
		
		if(index>=changesLightPath.size()){
			//expand list
			while(changesLightPath.size()<=index)
				changesLightPath.add(null);
		}
		changesLightPath.set(index, lightPath);
	}
	public List<LightPath> getChangesLightPath()
	{
		return changesLightPath;
	}

	public void setChangesSample(List<TagData> newValue) {
		changesSample=newValue;
	}
	public List<TagData> getChangesSample()
	{
		return changesSample;
	}

	public void setChangesExperiment(List<TagData> newValue) {
		changesExperiment=newValue;
	}
	public List<TagData> getChangesExperiment()
	{
		return changesExperiment;
	}
	

	



	



	






	



	

	



	

	

	

	


	


	

	

	






	
	
	
}
