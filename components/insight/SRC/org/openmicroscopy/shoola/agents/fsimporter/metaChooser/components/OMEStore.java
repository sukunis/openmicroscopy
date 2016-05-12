package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components;

import java.util.List;
import java.util.logging.Logger;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.UOSMetadataLogger;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.ChannelCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.format.Sample;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.xml.SampleAnnotation;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.ExceptionDialog;

import com.drew.metadata.Metadata;

import ome.xml.model.Channel;
import ome.xml.model.Detector;
import ome.xml.model.DetectorSettings;
import ome.xml.model.Dichroic;
import ome.xml.model.Experiment;
import ome.xml.model.Experimenter;
import ome.xml.model.Filter;
import ome.xml.model.Image;
import ome.xml.model.ImagingEnvironment;
import ome.xml.model.Instrument;
import ome.xml.model.LightSource;
import ome.xml.model.LightSourceSettings;
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
				LOGGER.warning("[SAVE] -- empty EXPERIMENTER");
			}

		}
		return result;
	}
	
	public void storeProjectPartner(Experimenter e) 
	{
		int idx=getExperimenterIndexByLName(e.getLastName());
		if(idx==-1){
			e.setID(MetadataTools.createLSID("Experimenter",ome.sizeOfExperimenterList()));
			ome.addExperimenter(e);
		}else{
			// save by name
			e.setID(ome.copyExperimenterList().get(idx).getID());
			ome.setExperimenter(idx, e);
		}
	}

	
	public void storeExperiment(Experiment e)
	{
		LOGGER.info("[SAVE] -- save EXPERIMENT data");
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

				LOGGER.info("[SAVE] --  create new EXPERIMENTER "+exp.getID());
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
			e.setID(MetadataTools.createLSID("Experiment", 0));
			LOGGER.info("[SAVE] -- add new EXPERIMENT "+e.getID());
			ome.addExperiment(e);
		}else{
			//rewrite
			ome.setExperiment(idxExperiment, e);
		}
	}
	

	public void storeSample(Sample s,Image i) 
	{
		LOGGER.info("[SAVE] -- save SAMPLE data");
		StructuredAnnotations annot=ome.getStructuredAnnotations();
		if(annot==null){
			LOGGER.info("[SAVE] -- Structured Annotation are empty");
			annot=new StructuredAnnotations();
		}
		int annotationIndex=annot.sizeOfXMLAnnotationList();
		
		SampleAnnotation sampleAnnot=new SampleAnnotation();
		if(sampleAnnot.getID()==null)
			sampleAnnot.setID(MetadataTools.createLSID(SampleAnnotation.SAMPLE_ANNOT_ID, annotationIndex));
		sampleAnnot.setSample(s);
		annot.addXMLAnnotation(sampleAnnot);
		
		ome.setStructuredAnnotations(annot);
		i.linkAnnotation(sampleAnnot);
		
	}
	
	
	public void storeObjectiveSettings(ObjectiveSettings o,Image i)
	{
		if(o.getObjective()==null)
			return;
		
		LOGGER.info("[SAVE] -- save OBJECTIVE SETTINGS");
		if(o.getID()==null || o.getID().equals("")){
			o.setID(MetadataTools.createLSID("ObjectiveSettings", 0));
		}
		i.setObjectiveSettings(o);
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
				LOGGER.warning("[SAVE] -- empty OBJECTIVE");
			}

		}
		return result;
	}
	
	public void storeObjective(Objective o, Image i, int imageIndex) 
	{
		LOGGER.info("[SAVE] -- save OBJECTIVE data");
		Instrument instrument =i.getLinkedInstrument();
		if(instrument==null){
			instrument=new Instrument();
			instrument.setID(MetadataTools.createLSID("Instrument", imageIndex));
			LOGGER.info("[SAVE] --  create new INSTRUMENT "+instrument.getID());
			ome.addInstrument(instrument);
			i.linkInstrument(instrument);
		}
		
//		int idxModel=getObjectiveIndexByModel(instrument.copyObjectiveList(),o.getModel());
		int idxID=getObjectiveIndexByID(instrument.copyObjectiveList(),o.getID());
		
//		if(idxModel==-1){
			if(idxID==-1){
				//new one
				if(o.getID()==null || o.getID().equals("")){
					o.setID(MetadataTools.createLSID("Objective",imageIndex,instrument.sizeOfObjectiveList()));
					// link to current image
				}
				LOGGER.info("[SAVE] --  add new OBJECTIVE "+o.getID());
				instrument.addObjective(o);
			}else{
				//save by id
				instrument.setObjective(idxID, o);
			}
//		}else{
//			// save by model, perhaps ref has changed
//			o.setID(instrument.copyObjectiveList().get(idxModel).getID());
//			instrument.setObjective(idxModel, o);
//		}
	}
	
	



	public void storeImagingEnv(ImagingEnvironment iEnv,Image i) 
	{
		LOGGER.info("[SAVE] -- save IMAGE ENVIRONMENT data");
		i.setImagingEnvironment(iEnv);
	}
	
	public void storeDetectorSettings(DetectorSettings dSett,Channel c) 
	{
		if(dSett.getDetector()==null)
			return;
		LOGGER.info("[SAVE] -- save DETECTOR SETTINGS");
		if(dSett.getID()==null || dSett.getID().equals("")){
			dSett.setID(MetadataTools.createLSID("DetectorSettings", 0));
		}
		c.setDetectorSettings(dSett);
	}
	
	public void storeDetector(Detector d, Image i,int imageIndex) 
	{
		
		Instrument instrument = i.getLinkedInstrument();
		if(instrument==null){
			instrument=new Instrument();
			instrument.setID(MetadataTools.createLSID("Instrument", imageIndex));
			LOGGER.info("[SAVE] -- create new INSTRUMENT "+instrument.getID());
			ome.addInstrument(instrument);
			i.linkInstrument(instrument);
		}
		
//		int idxModel=getDetectorIndexByModel(instrument.copyDetectorList(), d.getModel());
		int idxID = getDetectorIndexByID(instrument.copyDetectorList(),d.getID());
		
//		if(idxModel==-1){
			if(idxID==-1){
				//new one
				if(d.getID()==null || d.getID().equals(""))
					d.setID(MetadataTools.createLSID("Detector",imageIndex,instrument.sizeOfDetectorList()));
				
				LOGGER.info("[SAVE] -- add new DETECTOR "+d.getID());
				instrument.addDetector(d);
			}else{
				//save by id
				instrument.setDetector(idxID, d);
				LOGGER.info("[SAVE] -- save DETECTOR data by id "+d.getID());
			}
//		}else{
//			// save by model, perhaps ref has changed
//			d.setID(instrument.copyDetectorList().get(idxModel).getID());
//			instrument.setDetector(idxModel, d);
//			LOGGER.info("[SAVE] save DETECTOR data by model "+d.getID());
//		}
		
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
				LOGGER.warning("[SAVE] -- empty DETECTOR");
			}

		}
		return result;
	}

	
	public void storeLightSrcSettings(LightSourceSettings lSett,Channel c) 
	{
		if(lSett.getLightSource()==null)
			return;
		LOGGER.info("[SAVE] -- save LIGHTSRC SETTINGS");
		if(lSett.getID()==null || lSett.getID().equals("")){
			lSett.setID(MetadataTools.createLSID("LightSourceSettings", 0));
		}
		c.setLightSourceSettings(lSett);
	}
	
	public void storeLightSrc(LightSource l, Image i,int imageIndex) 
	{
		LOGGER.info("[SAVE] -- save LIGHTSOURCE data");
		Instrument instrument = i.getLinkedInstrument();
		if(instrument==null){
			instrument=new Instrument();
			instrument.setID(MetadataTools.createLSID("Instrument", imageIndex));
			LOGGER.info("[SAVE] --  create new INSTRUMENT "+instrument.getID());
			ome.addInstrument(instrument);
			i.linkInstrument(instrument);
		}
		
		int idxModel=getLightSourceIndexByModel(instrument.copyLightSourceList(), l.getModel());
		int idxID = getLightSourceIndexByID(instrument.copyLightSourceList(),l.getID());
		
		if(idxModel==-1){
			if(idxID==-1){
				//new one
				if(l.getID()==null || l.getID().equals(""))
					l.setID(MetadataTools.createLSID("LightSource",imageIndex,instrument.sizeOfLightSourceList()));
				
				LOGGER.info("[SAVE] --  add new LIGHTSOURCE "+l.getID());
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
				LOGGER.warning("[SAVE] -- empty DETECTOR");
			}

		}
		return result;
	}

	
	public void storeFilterList(List<Filter> fList,Image img,int imageIndex) 
	{
		LOGGER.info("[SAVE] -- save FILTER data");
		Instrument instrument = img.getLinkedInstrument();
		if(instrument==null){
			instrument=new Instrument();
			instrument.setID(MetadataTools.createLSID("Instrument", imageIndex));
			LOGGER.info("[SAVE] --  create new INSTRUMENT "+instrument.getID());
			ome.addInstrument(instrument);
			img.linkInstrument(instrument);
		}
		int i=0;
		for(Filter f:fList){
			
			int idxID = getFilterIndexByID(instrument.copyFilterList(),f.getID());

			if(idxID==-1){
				//new one
				if(f.getID()==null || f.getID().equals(""))
					f.setID(MetadataTools.createLSID("Filter",imageIndex,instrument.sizeOfFilterList()));

				LOGGER.info("[SAVE] --  add new FILTER "+f.getID());
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

	

	public void storeDichroicList(List<Dichroic> dList,Image img, int imageIndex) 
	{
		LOGGER.info("[SAVE] -- save DICHROIC data");
		Instrument instrument = img.getLinkedInstrument();
		if(instrument==null){
			instrument=new Instrument();
			instrument.setID(MetadataTools.createLSID("Instrument", imageIndex));
			LOGGER.info("[SAVE] --  create new INSTRUMENT "+instrument.getID());
			ome.addInstrument(instrument);
			img.linkInstrument(instrument);
		}
		for(Dichroic f:dList){
			int idxID = getDichroicIndexByID(instrument.copyDichroicList(),f.getID());

			if(idxID==-1){
				//new one
				if(f.getID()==null || f.getID().equals(""))
					f.setID(MetadataTools.createLSID("Dichroic",imageIndex,instrument.sizeOfDichroicList()));

				LOGGER.info("[SAVE] --  add new DICHROIC "+f.getID());
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


/**
 * 
 * @param c
 * @param i
 */
	public void storeChannel(Channel c, Image i) 
	{
		
		Pixels pixels=i.getPixels();
		if(pixels==null){
			//TODO create new or corrupted data???
			LOGGER.severe("[SAVE] -- no PIXELS object available");
			ExceptionDialog ld = new ExceptionDialog("OME Format Error!", 
					"No pixel element available to save channel.");
			ld.setVisible(true);
		}else{
			int idx = getChannelIndexByID(pixels.copyChannelList(),c.getID());
			if(idx==-1){
				if(c.getID()==null || c.getID().equals("")){
					c.setID(MetadataTools.createLSID("Channel", pixels.sizeOfChannelList()));
				}
				LOGGER.info("[SAVE] -- add new CHANNEL "+c.getID());
				pixels.addChannel(c);
			}else{
				Channel omeCH=pixels.getChannel(idx);
				ChannelCompUI.mergeData(c, omeCH);
				pixels.setChannel(idx, omeCH);
					LOGGER.info("[SAVE] -- save CHANNEL "+c.getID()+" at "+idx+" for image ");
				
			}
			i.setPixels(pixels);
		}
	}
	
	public void storeImage(Image image, int imageIndex) 
	{
		LOGGER.info("[SAVE] -- save IMAGE data");
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
			LOGGER.severe("[SAVE] -- Wrong Format: "+str);
			ExceptionDialog ld = new ExceptionDialog("ID Format Error!", 
					"Wrong id format : "+str,e);
			ld.setVisible(true);
		}
		return index;
	}



	public OME getOME() {
		return ome;
	}



	


	









	




}
