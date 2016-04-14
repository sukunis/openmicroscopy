package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components;

import java.util.List;
import java.util.logging.Logger;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.UOSMetadataLogger;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.format.Sample;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.xml.SampleAnnotation;

import com.drew.metadata.Metadata;

import ome.xml.model.Channel;
import ome.xml.model.Detector;
import ome.xml.model.Dichroic;
import ome.xml.model.Experiment;
import ome.xml.model.Experimenter;
import ome.xml.model.Filter;
import ome.xml.model.Image;
import ome.xml.model.ImagingEnvironment;
import ome.xml.model.Instrument;
import ome.xml.model.LightSource;
import ome.xml.model.OME;
import ome.xml.model.Objective;
import ome.xml.model.ObjectiveSettings;
import ome.xml.model.Pixels;
import ome.xml.model.StructuredAnnotations;
import ome.xml.model.XMLAnnotation;
import loci.formats.MetadataTools;
import loci.formats.meta.IMetadata;

public class OMEStore 
{
	/** Logger for this class. */
    private static Logger LOGGER = Logger.getLogger(UOSMetadataLogger.class.getName());
	
	private OME ome;
//	private IMetadata data;
	
	public OMEStore(OME _ome)
	{
		ome=_ome;
//		data=_data;
	}
	
	
	
	// search for experiment with id in data, return entry index
	private int getExperimentIndexByID(String id)
	{
		int result=-1;
		int count = ome.sizeOfExperimentList();
		List<Experiment> list = ome.copyExperimentList();
		for(int i=0; i<count; i++){
			if(list.get(i).getID().equals(id)){
				return i;
			}
		}
		
		return result;
	}
	
	// search for experimenter with id in data, return entry index
	private int getExperimenterIndexByID(String id)
	{
		int result=-1;
		if(id==null || id.equals("")) return result;
		int count =ome.sizeOfExperimenterList();
		List<Experimenter> list = ome.copyExperimenterList();
		for(int i=0; i<count; i++){
			if(list.get(i).equals(id)){
				return i;
			}
		}
		return result;
	}
	
	// search for experimenter by lastName, return entry index
	private int getExperimenterIndexByLName(String lastName)
	{
		int result=-1;
		int count =ome.sizeOfExperimenterList();
		List<Experimenter> list = ome.copyExperimenterList();
		String name="";
		for(int i=0; i<count; i++){
			try{
				name=list.get(i).getLastName();
				if(name.equals(lastName)){
					return i;
				}
			}catch(Exception e){
				LOGGER.warning("empty EXPERIMENTER");
			}

		}
		return result;
	}
	
	
	public void storeExperiment(Experiment e)
	{
		LOGGER.info("save EXPERIMENT data");
		int idxExperiment=getExperimentIndexByID(e.getID());
			
		//store Experimenter
		Experimenter exp=e.getLinkedExperimenter();
		
		int idx2=getExperimenterIndexByID(exp.getID());
		int idx3=getExperimenterIndexByLName(exp.getLastName());
		
		if(idx3==-1){
			if(idx2==-1){
				//new one
				if(exp.getID()==null || exp.getID().equals(""))
					exp.setID(MetadataTools.createLSID("Experimenter",ome.sizeOfExperimenterList()));

				LOGGER.info("* create new EXPERIMENTER "+exp.getID());
				ome.addExperimenter(exp);
			}else{
				//save by id
				ome.setExperimenter(idx2, exp);	
			}
		}else{
			// save by name, perhaps ref has changed
			exp.setID(ome.copyExperimenterList().get(idx3).getID());
			ome.setExperimenter(idx3, exp);
		}
			
		e.linkExperimenter(exp);

		// store Experiment
		if(idxExperiment==-1){
			LOGGER.info("* create new EXPERIMENT "+e.getID());
			ome.addExperiment(e);
		}else{
			//rewrite
			ome.setExperiment(idxExperiment, e);
		}
	}
	

	public void storeSample(Sample s,int imageIndex) 
	{
		LOGGER.info("save SAMPLE data");
		StructuredAnnotations annot=ome.getStructuredAnnotations();
		if(annot==null){
			LOGGER.info("Structured Annotation are empty");
			annot=new StructuredAnnotations();
		}
		int annotationIndex=annot.sizeOfXMLAnnotationList();
		
		SampleAnnotation sampleAnnot=new SampleAnnotation();
		if(sampleAnnot.getID()==null)
			sampleAnnot.setID(MetadataTools.createLSID(SampleAnnotation.SAMPLE_ANNOT_ID, annotationIndex));
		sampleAnnot.setSample(s);
		annot.addXMLAnnotation(sampleAnnot);
		
		ome.setStructuredAnnotations(annot);
		LOGGER.info("[DEBUG] size structuredAnnot "+ome.getStructuredAnnotations().sizeOfXMLAnnotationList());
		ome.getImage(imageIndex).linkAnnotation(sampleAnnot);
		
	}
	
	
	public void storeObjectiveSettings(ObjectiveSettings o,int imageIndex)
	{
		
		ome.getImage(imageIndex).setObjectiveSettings(o);
	}
	
	
	private int getObjectiveIndexByID(List<Objective> list,String id) 
	{
		int result=-1;
		for(int i=0; i<list.size(); i++){
			if(list.get(i).getID().equals(id)){
				return i;
			}
		}
		return result;
	}
	
	private int getObjectiveIndexByModel(List<Objective> list,String model) 
	{
		int result=-1;
		if(model.equals(""))
			return result;
		
		for(int i=0; i<list.size(); i++){
			try{
				if(model.equals(list.get(i).getModel())){
					return i;
				}
			}catch(Exception e){
				LOGGER.warning("empty OBJECTIVE");
			}

		}
		return result;
	}
	
	public void storeObjective(Objective o, int imageIndex) 
	{
		LOGGER.info("save OBJECTIVE data");
		Instrument instrument = ome.getImage(imageIndex).getLinkedInstrument();
		if(instrument==null){
			instrument=new Instrument();
			instrument.setID(MetadataTools.createLSID("Instrument", imageIndex));
			LOGGER.info("* create new INSTRUMENT "+instrument.getID());
			ome.addInstrument(instrument);
			ome.getImage(imageIndex).linkInstrument(instrument);
		}
		
		int idxModel=getObjectiveIndexByModel(instrument.copyObjectiveList(),o.getModel());
		int idxID=getObjectiveIndexByID(instrument.copyObjectiveList(),o.getID());
		
		if(idxModel==-1){
			if(idxID==-1){
				//new one
				if(o.getID()==null || o.getID().equals(""))
					o.setID(MetadataTools.createLSID("Objective",imageIndex,instrument.sizeOfObjectiveList()));
				
				LOGGER.info("* create new OBJECTIVE "+o.getID());
				instrument.addObjective(o);
			}else{
				//save by id
				instrument.setObjective(idxID, o);
			}
		}else{
			// save by model, perhaps ref has changed
			o.setID(instrument.copyObjectiveList().get(idxModel).getID());
			instrument.setObjective(idxModel, o);
		}
	}
	
	



	public void storeImagingEnv(ImagingEnvironment iEnv,int imageIndex) 
	{
		LOGGER.info("save IMAGE ENVIRONMENT data");
		ome.getImage(imageIndex).setImagingEnvironment(iEnv);
	}
	
	
	public void storeDetector(Detector d, int imageIndex) 
	{
		LOGGER.info("save DETECTOR data");
		Instrument instrument = ome.getImage(imageIndex).getLinkedInstrument();
		if(instrument==null){
			instrument=new Instrument();
			instrument.setID(MetadataTools.createLSID("Instrument", imageIndex));
			LOGGER.info("* create new INSTRUMENT "+instrument.getID());
			ome.addInstrument(instrument);
			ome.getImage(imageIndex).linkInstrument(instrument);
		}
		
		int idxModel=getDetectorIndexByModel(instrument.copyDetectorList(), d.getModel());
		int idxID = getDetectorIndexByID(instrument.copyDetectorList(),d.getID());
		
		if(idxModel==-1){
			if(idxID==-1){
				//new one
				if(d.getID()==null || d.getID().equals(""))
					d.setID(MetadataTools.createLSID("Detector",imageIndex,instrument.sizeOfDetectorList()));
				
				LOGGER.info("* create new DETECTOR "+d.getID());
				instrument.addDetector(d);
			}else{
				//save by id
				instrument.setDetector(idxID, d);
			}
		}else{
			// save by model, perhaps ref has changed
			d.setID(instrument.copyDetectorList().get(idxModel).getID());
			instrument.setDetector(idxModel, d);
		}
		
	}
	
	
	private int getDetectorIndexByID(List<Detector> list, String id) 
	{
		int result=-1;
		if(id==null ||id.equals(""))
			return result;
		for(int i=0; i<list.size(); i++){
			if(list.get(i).getID().equals(id)){
				return i;
			}
		}
		return result;
	}
	private int getDetectorIndexByModel(List<Detector> list,String model)
	{
		int result=-1;
		if(model==null || model.equals(""))
			return result;
		
		for(int i=0; i<list.size(); i++){
			try{
				if(model.equals(list.get(i).getModel())){
					return i;
				}
			}catch(Exception e){
				LOGGER.warning("empty DETECTOR");
			}

		}
		return result;
	}

	
	public void storeLightSrc(LightSource l, int imageIndex) 
	{
		LOGGER.info("save LIGHTSOURCE data");
		Instrument instrument = ome.getImage(imageIndex).getLinkedInstrument();
		if(instrument==null){
			instrument=new Instrument();
			instrument.setID(MetadataTools.createLSID("Instrument", imageIndex));
			LOGGER.info("* create new INSTRUMENT "+instrument.getID());
			ome.addInstrument(instrument);
			ome.getImage(imageIndex).linkInstrument(instrument);
		}
		
		int idxModel=getLightSourceIndexByModel(instrument.copyLightSourceList(), l.getModel());
		int idxID = getLightSourceIndexByID(instrument.copyLightSourceList(),l.getID());
		
		if(idxModel==-1){
			if(idxID==-1){
				//new one
				if(l.getID()==null || l.getID().equals(""))
					l.setID(MetadataTools.createLSID("LightSource",imageIndex,instrument.sizeOfLightSourceList()));
				
				LOGGER.info("* create new LIGHTSOURCE "+l.getID());
				instrument.addLightSource(l);
			}else{
				//save by id
				instrument.setLightSource(idxID, l);
			}
		}else{
			// save by model, perhaps ref has changed
			l.setID(instrument.copyLightSourceList().get(idxModel).getID());
			instrument.setLightSource(idxModel, l);
		}
	}
	
	private int getLightSourceIndexByID(List<LightSource> list, String id) 
	{
		int result=-1;
		if(id==null || id.equals(""))
			return result;
		for(int i=0; i<list.size(); i++){
			if(list.get(i).getID().equals(id)){
				return i;
			}
		}
		return result;
	}
	private int getLightSourceIndexByModel(List<LightSource> list,String model)
	{
		int result=-1;
		if(model==null || model.equals(""))
			return result;
		
		for(int i=0; i<list.size(); i++){
			try{
				if(model.equals(list.get(i).getModel())){
					return i;
				}
			}catch(Exception e){
				LOGGER.warning("empty DETECTOR");
			}

		}
		return result;
	}

	
	public void storeFilterList(List<Filter> fList,int imageIndex) 
	{
		LOGGER.info("save FILTER data");
		Instrument instrument = ome.getImage(imageIndex).getLinkedInstrument();
		if(instrument==null){
			instrument=new Instrument();
			instrument.setID(MetadataTools.createLSID("Instrument", imageIndex));
			LOGGER.info("* create new INSTRUMENT "+instrument.getID());
			ome.addInstrument(instrument);
			ome.getImage(imageIndex).linkInstrument(instrument);
		}
		int i=0;
		for(Filter f:fList){
			
			int idxID = getFilterIndexByID(instrument.copyFilterList(),f.getID());

			if(idxID==-1){
				//new one
				if(f.getID()==null || f.getID().equals(""))
					f.setID(MetadataTools.createLSID("Filter",imageIndex,instrument.sizeOfFilterList()));

				LOGGER.info("* create new FILTER "+f.getID());
				instrument.addFilter(f);
			}else{
				//save by id
				String type=f.getType()!=null ? f.getType().toString() : "";
				instrument.setFilter(idxID, f);
			}
			i++;
		}

	}

	private int getFilterIndexByID(List<Filter> list, String id) 
	{
		int result=-1;
		if(id==null || id.equals(""))
			return result;
		for(int i=0; i<list.size(); i++){
			if(list.get(i).getID().equals(id)){
				return i;
			}
		}
		return result;
	}

	

	public void storeDichroicList(List<Dichroic> dList, int imageIndex) 
	{
		LOGGER.info("save DICHROIC data");
		Instrument instrument = ome.getImage(imageIndex).getLinkedInstrument();
		if(instrument==null){
			instrument=new Instrument();
			instrument.setID(MetadataTools.createLSID("Instrument", imageIndex));
			LOGGER.info("* create new INSTRUMENT "+instrument.getID());
			ome.addInstrument(instrument);
			ome.getImage(imageIndex).linkInstrument(instrument);
		}
		for(Dichroic f:dList){
			int idxID = getDichroicIndexByID(instrument.copyDichroicList(),f.getID());

			if(idxID==-1){
				//new one
				if(f.getID()==null || f.getID().equals(""))
					f.setID(MetadataTools.createLSID("Dichroic",imageIndex,instrument.sizeOfDichroicList()));

				LOGGER.info("* create new DICHROIC "+f.getID());
				instrument.addDichroic(f);
			}else{
				//save by id
				instrument.setDichroic(idxID, f);
			}
		}

	}

	private int getDichroicIndexByID(List<Dichroic> list, String id) 
	{
		int result=-1;
		if(id==null || id.equals(""))
			return result;
		for(int i=0; i<list.size(); i++){
			if(list.get(i).getID().equals(id)){
				return i;
			}
		}
		return result;
	}



	public void storeChannel(Channel c, int imageIndex) 
	{
		LOGGER.info("save CHANNEL data");
		Pixels pixels=ome.getImage(imageIndex).getPixels();
		if(pixels==null){
			//TODO create new or corrupted data???
			LOGGER.severe("no PIXELS object available");
		}else{
			int idx = getChannelIndexByID(pixels.copyChannelList(),c.getID());
			if(idx==-1){
				LOGGER.info("* create new CHANNEL "+c.getID());
				pixels.addChannel(c);
			}else{
				pixels.setChannel(idx, c);
			}
		}
	}
	
	public void storeImage(Image image, int imageIndex) 
	{
		LOGGER.info("save IMAGE data");
		ome.setImage(imageIndex, image);
	}


	private int getChannelIndexByID(List<Channel> list, String id) 
	{
		int result=-1;
		for(int i=0; i<list.size(); i++){
			if(list.get(i).getID().equals(id)){
				return i;
			}
		}
		return result;
	}



	private int getIDFromLSID(String str)
	{
		int index=-1;
		if(str ==null || str.equals(""))return index;
		String delims = "[:]";
		try {
		String[] tokens = str.split(delims);
		
			if(tokens.length > 0){
				index=Integer.parseInt(tokens[tokens.length-1]);
			}else{
				index=Integer.parseInt(str);
			}
		} catch (NumberFormatException e) {
			LOGGER.severe("Wrong Format: "+str);
		}
		return index;
	}



	public OME getOME() {
		return ome;
	}









	




}
