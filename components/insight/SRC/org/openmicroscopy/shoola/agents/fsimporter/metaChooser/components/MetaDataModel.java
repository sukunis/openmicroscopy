package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import loci.formats.MetadataTools;
import ome.xml.model.Channel;
import ome.xml.model.Detector;
import ome.xml.model.DetectorSettings;
import ome.xml.model.Dichroic;
import ome.xml.model.Experiment;
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
import ome.xml.model.enums.FilterType;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.UOSMetadataLogger;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.ChannelCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.DetectorCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.ElementsCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.ExperimentCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.ImageCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.ImagingEnvironmentCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.LightPathCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.LightSourceCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.ObjectiveCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.ObjectiveSettingsCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.PlaneCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.SampleCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.format.Sample;

//see also ome.xml.model.OME
//TODO use OME
public class MetaDataModel 
{
	
	/** Logger for this class. */
    private static Logger LOGGER = Logger.getLogger(UOSMetadataLogger.class.getName());
    
	private ElementsCompUI image;
	private List<ElementsCompUI> detectorList;
	private List<ElementsCompUI> lightSrcList;
	
	// list of all filter for current image
	private List<Filter> filterList;
	/** linked Channel for single filter, because there are more than one filter per channel
	 and maybe two channel have the same kind of filter with differents settings**/
	private List<Integer> linkedChannelForFilter;
	// list of all dichroics for current image
	private List<Dichroic> dichroicList;
	private List<Integer> linkedChannelForDichroic;
	
	

	private List<ElementsCompUI> planeList;
	//TODO
//	private ElementsCompUI stage;
	
	//-----------------------------------
	private ElementsCompUI experimentUI;
	private ElementsCompUI objectiveUI;
	private ElementsCompUI imgEnvUI;
	private ElementsCompUI sampleUI;
	
	// list of lightPaths for channel of current image. List index== channelIndex
	private List<ElementsCompUI> lightPathList;
	
	private List<ElementsCompUI> channelList;
	
	private Image imageOME;
	private int imageIndex;
	private int numOfChannels;
	
	private OME ome;

	private List<LightSource> lightSrcOrigList;
	
	
	
	public MetaDataModel()
	{
		imageIndex=-1;
		numOfChannels=-1;
		
		channelList=new ArrayList<ElementsCompUI>();
		planeList=new ArrayList<ElementsCompUI>();
		lightSrcList=new ArrayList<ElementsCompUI>();
		detectorList=new ArrayList<ElementsCompUI>();
		
		lightPathList=new ArrayList<ElementsCompUI>();
	}
	
	public MetaDataModel(int imgIdx, int _numOfChannels)
	{
		LOGGER.info("[DEBUG] set image index "+imgIdx);
		imageIndex=imgIdx;
		numOfChannels=_numOfChannels;
		
		
		channelList=new ArrayList<ElementsCompUI>(numOfChannels);
		planeList=new ArrayList<ElementsCompUI>();
		lightSrcList=new ArrayList<ElementsCompUI>(numOfChannels);
		detectorList=new ArrayList<ElementsCompUI>(numOfChannels);
		
		lightPathList=new ArrayList<ElementsCompUI>(numOfChannels);
	}
	
	public void clearData()
	{
		imageIndex=-1;
		numOfChannels=-1;
		
		imageOME=null;
		ome=null;
		image=null;
		
		experimentUI=null;
		sampleUI=null;
		objectiveUI=null;
		imgEnvUI=null;
		
		filterList=null;
		dichroicList=null;
		linkedChannelForDichroic=null;
		linkedChannelForFilter=null;
		
		channelList=new ArrayList<ElementsCompUI>();
		planeList=new ArrayList<ElementsCompUI>();
		lightSrcList=new ArrayList<ElementsCompUI>();
		detectorList=new ArrayList<ElementsCompUI>();
		
		lightPathList=new ArrayList<ElementsCompUI>();
	}
	
	public boolean noticUserInput()
	{
		boolean hasUserInput=false;
		boolean result=false;
		
		if(experimentUI!=null){
			LOGGER.info("[DEBUG] -- changes in EXPERIMENT: "+
				((ExperimentCompUI) experimentUI).userInput());
			hasUserInput=hasUserInput || ((ExperimentCompUI) experimentUI).userInput();
		}
		
		if(image!=null){ LOGGER.info("[DEBUG] -- changes in IMAGE: "+
				((ImageCompUI) image).userInput());
		hasUserInput=hasUserInput ||((ImageCompUI) image).userInput();
		}
		
		if(objectiveUI!=null){ LOGGER.info("[DEBUG] -- changes in OBJECT: "+
				objectiveUI.userInput());
		hasUserInput=hasUserInput ||objectiveUI.userInput();
		}

		
		for(int i=0; i<detectorList.size();i++){
			if(detectorList.get(i)!=null) 
					result=result ||( detectorList.get(i)).userInput();
		}
		hasUserInput=hasUserInput ||result;
		LOGGER.info("[DEBUG] -- changes in DETECTOR: "+result);

		result=false;
		for(int i=0; i<lightSrcList.size();i++){
			if(lightSrcList.get(i)!=null)
					result=result || (lightSrcList.get(i)).userInput();
		}
		hasUserInput=hasUserInput ||result;
		 LOGGER.info("[DEBUG] -- changes in LIGHTSRC: "+result);
		 
		 result=false;
		for(int i=0; i<channelList.size();i++){
			if(channelList.get(i)!=null) 
					result=result || ( channelList.get(i)).userInput();
		}
		hasUserInput=hasUserInput ||result;
		LOGGER.info("[DEBUG] -- changes in CHANNEL: "+result);
		
		result=false;
		for(int i=0; i<lightPathList.size();i++){
			if(lightPathList.get(i)!=null) 
					result=result || ( lightPathList.get(i)).userInput();
		}
		hasUserInput=hasUserInput ||result;
		LOGGER.info("[DEBUG] -- changes in LIGHTPATH: "+result);
		
		if(sampleUI!=null){ LOGGER.info("[DEBUG] -- changes in SAMPLE: "+
				( sampleUI).userInput());
		hasUserInput=hasUserInput ||( sampleUI).userInput();
		}
		
		if(imgEnvUI!=null){ LOGGER.info("[DEBUG] -- changes in IMGENV: "+
				( imgEnvUI).userInput());
		hasUserInput=hasUserInput || ( imgEnvUI).userInput();
		}

		result=false;
		for(int i=0; i<planeList.size();i++){
			if(planeList.get(i)!=null) 
					result=result ||( planeList.get(i)).userInput();
		}
		hasUserInput=hasUserInput ||result;
		LOGGER.info("[DEBUG] -- changes in PLANE: "+result);
		
		
		return hasUserInput;
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
			ImageCompUI.mergeData(in,imageOME); 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return imageOME;
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
	public void setExpData(ElementsCompUI e)
	{
		experimentUI=e;
//		if(e!=null)
//			LOGGER.info("[MODEL] link experiment module");
	}
	
	public ExperimentCompUI getExpModul()
	{
		return (ExperimentCompUI) experimentUI;
	}
	
	
	public Experiment getExperiment() throws Exception
	{
		if(experimentUI==null)
			return null;
		else
			return ((ExperimentCompUI) experimentUI).getData();
	}
	
	public void setSampleData(ElementsCompUI e)
	{
		sampleUI=e;
		if(e==null){
			LOGGER.warning("[DEBUG] Add empty sample obj ");
		}
	}
	
	public Sample getSample() throws Exception
	{
		if(sampleUI==null){
			return null;
		}else{
			return ((SampleCompUI) sampleUI).getData();
		}
	}
	
	//image
	public void setImageModul(ElementsCompUI elem)
	{
		image=elem;
	}
	
	public ImageCompUI getImageModul() 
	{
		return (ImageCompUI) image;
	}

	/**
	 * Get image modul gui input data
	 * @return
	 * @throws Exception
	 */
	public Image getImageData() throws Exception
	{
		if(image==null)
			return null;
		return ((ImageCompUI)image).getData();
	}

	
	
	public ObjectiveSettings getObjectiveSettings() throws Exception
	{
		if(objectiveUI==null)
			return null;
		return ((ObjectiveCompUI)objectiveUI).getSettings().getData();
	}

	
	
	public void addChannelData(ChannelCompUI c)
	{
		if(channelList==null)
			channelList=new ArrayList<ElementsCompUI>();
		
		int size=channelList.size();
		channelList.add(c);
		try {
			c.getData();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public int getNumberOfChannels()
	{
		return channelList!=null ? channelList.size() : 0;
	}
	
	public Channel getChannel(int index) throws Exception
	{
		if(channelList==null || channelList.get(index)==null)
			return null;
		else
			return  ((ChannelCompUI)channelList.get(index)).getData(); 
	}
	
	public ChannelCompUI getChannelModul(int i) 
	{
		return (ChannelCompUI) channelList.get(i);
	}
	
	
	public void setDetectorData(DetectorCompUI d, int chIdx)
	{
		if(detectorList!=null)
			detectorList.add(chIdx,d);
		else
			LOGGER.warning("[Model] detector list not available");
	}
	
	public void addDetectorData(DetectorCompUI d)
	{
		if(detectorList!=null){
			detectorList.add(d);
			int size=lightSrcList.size();
			try {
				Detector l=((DetectorCompUI) d).getData();
			} catch (Exception err) {
				// TODO Auto-generated catch block
				err.printStackTrace();
			}
		}
		else
			LOGGER.warning("[Model] detector list not available");
	}
	
	public DetectorCompUI getDetectorModul(int i) 
	{
		return (detectorList!=null && !detectorList.isEmpty()) ?
				(DetectorCompUI) detectorList.get(i) : null;
	}

	
	
	public int getNumberOfDetectors()
	{
		return detectorList!=null ? detectorList.size() : 0;
	}
	
	public Detector getDetector(int index) throws Exception
	{
		Detector res=null;
		if(detectorList!=null){
			DetectorCompUI dUI=((DetectorCompUI)detectorList.get(index));
			if(dUI!=null){
				res=dUI.getData();
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
		if(detectorList==null || index>=detectorList.size())
			return null;
		
		return ((DetectorCompUI)detectorList.get(index)).getSettings().getData();
	}
	
	public void setLightPath(LightPathCompUI lpUI, int chIdx) 
	{
		if(lightPathList!=null)
			lightPathList.add(chIdx, lpUI);
		else
			LOGGER.warning("[Model] light path not available");
		
		// set linked Channel for Filter and dichroic list
		LightPath lP=lpUI.getLightPath();
		if(lP!=null){
			setLinkedChannel(lP,chIdx);
		}
	}
	public void addLightPath(LightPathCompUI lpUI) 
	{
		if(lightPathList!=null){
			lightPathList.add(lpUI);
			// set linked Channel for Filter and dichroic list
			LightPath lP=lpUI.getLightPath();
			if(lP!=null){
				setLinkedChannel(lP,lightPathList.size());
			}
		}else{
			LOGGER.warning("[Model] light path not available");
		}
	}

	public LightPath getLightPath(int index) throws Exception
	{
		return lightPathList!=null ?((LightPathCompUI)lightPathList.get(index)).getData() : null;
	}
	
	public LightPathCompUI getLightPathModul(int index)
	{
		return lightPathList!=null ?((LightPathCompUI)lightPathList.get(index)) : null;
	}
	
	public int getNumberOfLightPath() {
		return lightPathList!=null ? lightPathList.size() : 0;
	}
	
	
	private void setLinkedChannel(LightPath lP, int chIdx) 
	{
		List<Filter> list=lP.copyLinkedExcitationFilterList();
		int counter=0;
		if(filterList !=null){
		for(Filter f:filterList){
			for(Filter fC:list)	{
				if(f.equals(fC)){
					if(linkedChannelForFilter.get(counter)==-1){
						linkedChannelForFilter.set(counter, chIdx);
					}else{
						LOGGER.warning("DEBUG: filter schon von anderem Channel besetzt "+
								fC.getID());
					}
				}
			}
			counter++;
		}	
		list=lP.copyLinkedEmissionFilterList();
		counter=0;
		for(Filter f:filterList){
			for(Filter fC:list)	{
				//TODO : better filterList.indexOf(fC)
				if(f.equals(fC)){
					if(linkedChannelForFilter.get(counter)==-1){
						linkedChannelForFilter.set(counter, chIdx);
					}else{
						LOGGER.warning("filter is used by another channel"+
								fC.getID());
					}
				}
			}
			counter++;
		}	
		}
		
		Dichroic thisD=lP.getLinkedDichroic();
		counter=0;
		if(dichroicList!=null){
		for(Dichroic d:dichroicList){
			if(d.equals(thisD)){
				if(linkedChannelForDichroic.get(counter)==-1){
					linkedChannelForDichroic.set(counter,chIdx);
				}else{
					LOGGER.warning("dichroic is used by another channel "+d.getID());
				}
			}
			counter++;
		}
		}
	}
	
	
	//originalList from xml
	public void setLightSrcList(List<LightSource> list)
	{
		lightSrcOrigList=list;
	}
	//lightSrc for channels (maybe same new created)
	public void setLightSrcModul(ElementsCompUI e, int chIdx)
	{
		if(lightSrcList!=null)
			lightSrcList.add(chIdx,e);
		else
			LOGGER.warning("[MODEL] light source list not available");
	}
	
	public void addLightSrcModul(ElementsCompUI e)
	{
		if(lightSrcList!=null){
			lightSrcList.add(e);
			int size=lightSrcList.size();
			try {
				LightSource l=((LightSourceCompUI) e).getData();
			} catch (Exception err) {
				// TODO Auto-generated catch block
				err.printStackTrace();
			}
		}
		else
			LOGGER.warning("[MODEL] light source list not available");
	}
	
	
	public int getNumberOfLightSrc()
	{
		return lightSrcList!=null ? lightSrcList.size() : 0;
	}
	
	public LightSource getLightSourceData(int index) throws Exception
	{
		LightSource res=null;
		if(lightSrcList!=null){
			LightSourceCompUI lUI=((LightSourceCompUI)lightSrcList.get(index));
			if(lUI!=null){
				res=lUI.getData();
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
		if(lightSrcList==null || index >=lightSrcList.size() ){
			return null;
		}
		return ((LightSourceCompUI)lightSrcList.get(index)).getSettings().getData(); 
	}
	
	public LightSourceCompUI getLightSourceModul(int index)
	{
		
		return (lightSrcList!=null && !lightSrcList.isEmpty()) ? ((LightSourceCompUI)lightSrcList.get(index)) : null;
	}
	
	
	
	public void setImagingEnv(ImagingEnvironmentCompUI i)
	{
		imgEnvUI=i;
	}
	

	public ImagingEnvironmentCompUI getImgEnvModel() 
	{
		return (ImagingEnvironmentCompUI)imgEnvUI;
	}
	
	public ImagingEnvironment getImgagingEnv() throws Exception
	{
		if(imgEnvUI==null)
			return null;
		else
			return ((ImagingEnvironmentCompUI) imgEnvUI).getData();
	}
	
	public void createAndLinkNewInstrument(OME o)
	{
		 Instrument i=new Instrument();
		 i.setID(MetadataTools.createLSID("Instrument", o.sizeOfInstrumentList()));
		 imageOME.linkInstrument(i);
		 o.addInstrument(i);
		 LOGGER.info("[DEBUG] create new Instrument : "+i.getID());
	}
	
	public void setObjectiveData(ObjectiveCompUI o)
	{
		objectiveUI=o;
	}
	
	public Objective getObjective() throws Exception
	{
		Objective res=null;
		if(objectiveUI!=null){
			res=((ObjectiveCompUI) objectiveUI).getData();
			ObjectiveSettings oSett=getObjectiveSettings();
			// check if this objective is linked to image
			if(res!=null && !objectiveIsLinkedToImage(oSett,res.getID())){
				LOGGER.info("[DEBUG] objective is not linked");
				if(imageOME!=null){
					 if(imageOME.getLinkedInstrument()==null ){
						createAndLinkNewInstrument(ome);
					 }
					linkObjective(oSett,res,imageIndex,imageOME.getLinkedInstrument().sizeOfObjectiveList());
//					((ObjectiveCompUI)objectiveUI).addData(oSett,true);
				}else{
					LOGGER.info("[DEBUG] can't link objective. ");
				}
			}
		}
		return res;
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
		if(sett!=null && sett.getObjective()!=null && sett.getObjective().getID().equals(id))
			res=true;
		
		return res;
	}

	public ObjectiveCompUI getObjectiveModul() 
	{
		return (ObjectiveCompUI) objectiveUI;
	}
	
	public void setFilterList(List<Filter> list)
	{
		filterList=list;
		linkedChannelForFilter=new ArrayList<Integer>(Collections.nCopies(filterList.size(),-1));
	}
	
	public void setDichroicList(List<Dichroic> list)
	{
		dichroicList=list;
		linkedChannelForDichroic=new ArrayList<Integer>(Collections.nCopies(dichroicList.size(), -1));
	}
	
	/** TODO: if no primD exists convert first emF of type dichroic to primD
	 *  Add elements of list to the list of filter and dichroics of this image.
	 Note: set ID for new elements and return list completed by ID
	 @return input list completed by id's
	 **/
	public List<Object> updateLightPathElems(List<Object> list,int channelIdx)
	{
		if(imageIndex!=-1){
			int id1=imageIndex;
			int id2=channelIdx;
			boolean primDExists=true;

			int idxList=0;
			for(int i=0; i<list.size(); i++){
				Object o=list.get(i);
				if(o instanceof Dichroic){
					Dichroic f= (Dichroic) o;
					// new Dichroic elem
					if(f.getID()==null || f.getID().equals("")){
						appendNewDichroic(f, id1, dichroicList.size(), channelIdx);
					}else{
						int listIndex=identifyDichroic(f, channelIdx);
						if(listIndex!=-1){
							dichroicList.set(listIndex, f);
						}else{
							appendNewDichroic(f,id1,dichroicList.size(),channelIdx);
						}
					}
				}// end dichroic

				if(o instanceof Filter){
					Filter f=(Filter) o;
					if(f.getID()==null || f.getID().equals("")){
						appendNewFilter(f,id1,filterList.size(),channelIdx);
					}else{
						int listIndex=identifyFilter(f, channelIdx);
						if(listIndex!=-1){
							filterList.set(listIndex, f);
						}else{
							appendNewFilter(f,id1,filterList.size(),channelIdx);
						}
					}
				}
			}
		}
		return list;
	}
	

	
	
	
	
	private void appendNewFilter(Filter f, int id1, int id2, int chIdx)
	{
		f.setID(MetadataTools.createLSID("Filter", id1,	id2));
		filterList.add(f);
		linkedChannelForFilter.add(chIdx);
	}
	
	private void appendNewDichroic(Dichroic f, int id1, int id2, int chIdx)
	{
		f.setID(MetadataTools.createLSID("Dichroic", id1,	id2));
		dichroicList.add(f);
		linkedChannelForDichroic.add(chIdx);
	}
	
	private int identifyFilter(Filter f, int chIdx)
	{
		int listIndex=-1;
		for(int i=0; i<filterList.size(); i++){
			if(filterList.get(i).getID().equals(f.getID())){
				if(linkedChannelForFilter.get(i)==chIdx){
					return i;
				}
			}
		}
		
		return listIndex;
	}
	
	private int identifyDichroic(Dichroic f, int chIdx)
	{
		int listIndex=-1;
		for(int i=0; i<dichroicList.size(); i++){
			if(dichroicList.get(i).getID().equals(f.getID())){
				if(linkedChannelForDichroic.get(i)==chIdx){
					return i;
				}else{
					return -1;
				}
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
//		for(Filter f: filterList){
//			printFilter("DEBUG return", f);
//		}
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
	
//	public void clearData()
//	{
//		if(lightPath!=null)
//			lightPath.clear();
//		if(lightSrcList!=null)
//			lightSrcList.clear();
//		if(detectorList!=null)
//			detectorList.clear();
//		if(linkedChannelForDichroic!=null)
//			linkedChannelForDichroic.clear();
//		if(linkedChannelForFilter!=null)
//			linkedChannelForFilter.clear();
//		if(dichroicList!=null)
//			dichroicList.clear();
//		if(filterList!=null)
//			filterList.clear();
//		if(channelList!=null)
//			channelList.clear();
//		
//		image.clearDataValues();
//		image=null;
//	}

	
	
	public static final Dichroic convertFilterToDichroic(Filter f)
	{
		Dichroic d=new Dichroic();
		d.setID("");
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
		f.setID("");
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


	//TODO necessary??
	public void save() 
	{
		LOGGER.info("[MODEL] -- Save model data");
		try {
			if(experimentUI!=null && ((ExperimentCompUI) experimentUI).userInput()){
				((ExperimentCompUI) experimentUI).getData();
			}
			
			getObjective();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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

	public SampleCompUI getSampleModul() {
		// TODO Auto-generated method stub
		return (SampleCompUI) sampleUI;
	}



	

	

	

	


	


	

	

	






	
	
	
}
