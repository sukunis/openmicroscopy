package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.io.FilenameUtils;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.UOSMetadataLogger;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.format.Sample;

import loci.formats.meta.IMetadata;
import ome.specification.XMLWriter;
import ome.xml.model.Channel;
import ome.xml.model.Detector;
import ome.xml.model.DetectorSettings;
import ome.xml.model.Dichroic;
import ome.xml.model.Experiment;
import ome.xml.model.FileAnnotation;
import ome.xml.model.Filter;
import ome.xml.model.Image;
import ome.xml.model.ImagingEnvironment;
import ome.xml.model.LightPath;
import ome.xml.model.LightSource;
import ome.xml.model.LightSourceSettings;
import ome.xml.model.OME;
import ome.xml.model.Objective;
import ome.xml.model.ObjectiveSettings;

public class SaveMetadata 
{
	/** Logger for this class. */
    private static Logger LOGGER = Logger.getLogger(UOSMetadataLogger.class.getName());
	
	private MetaDataModel model;
	private IMetadata omexmlMeta;
	private OME ome;
	private OMEStore omeStore;
	private String srcImageFName;
	private String linkFile;
	
	public SaveMetadata(OME _ome,MetaDataModel _model, IMetadata _store, File srcImage)
	{
		ome=_ome;
		model=_model;
		omexmlMeta=_store;
		
		omeStore=new OMEStore(ome);
		
		FileAnnotation annotation = new FileAnnotation();
//		annotation.setName(srcImage.getName());
//		annotation.setFile(srcImage);
		
		// save xml in *.ome under same name and path like srcImage
		srcImageFName=FilenameUtils.removeExtension(srcImage.getAbsolutePath());
		linkFile=srcImage.getName();
		
	}
	
	private void saveMetaData(File file)
	{
		XMLWriter writer=new XMLWriter();

		try {
			writeInputToOME();
			writer.writeFile(file, omeStore.getOME(), false);
			LOGGER.info("[SAVE] save to "+file.getAbsolutePath());
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	//write data to omexmlMeta
	private void writeInputToOME() throws Exception
	{
		//experiment
		try {
			saveExperiment();
			
			saveObjective();
			
			saveImageEnv();	
			
			//TODO save stagelabel
			
			//TODO save planes, moechte man ueberhaupt hier aenderungen zulassen???
			
			//TODO extra store of detector and lightSrc necessary?? see bottum
			saveDetector();
			
			//--- LightSources
			//TODO update two lists
			saveLightSource();
			
			saveChannel();
			
			saveImage();
			
			saveFilter(); 
			
			saveDichroic(); 
			
			saveSample();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	private void saveSample() throws Exception 
	{
		Sample s = model.getSample(); 
		if(s!=null)
			omeStore.storeSample(s,model.getImageIndex());
		else
			LOGGER.info("SAMPLE is empty");
		
	}
	
	
	private void saveDichroic() {
		//--Dichroics
		List<Dichroic> dList=model.getDichroicList();
		if(dList!=null)
			omeStore.storeDichroicList(dList,model.getImageIndex());
	}

	private void saveFilter() {
		List<Filter> fList=model.getFilterList();
		if(fList!=null)
			omeStore.storeFilterList(fList,model.getImageIndex());
	}

	private void saveImage() throws Exception {
		//--- Image
		Image image=model.getImageData();
		if(image!=null)
			omeStore.storeImage(image,model.getImageIndex());
	}

	private void saveChannel() throws Exception {
		//--- Channels
		int numChannels=model.getNumberOfChannels();
		for(int cNr=0; cNr<numChannels; cNr++){
			Channel thisChannel=model.getChannel(cNr);
			
			LightSource thisLightSrc=model.getLightSourceData(cNr);
			
			if(thisLightSrc==null){
				LOGGER.warning("[SAVE] could not save LIGHTSOURCE - not specified for CHANNEL "+thisChannel.getName());
			}else{
				System.out.println("DEBUG: save: thisLightSrc: "+thisLightSrc.getID());
				LightSourceSettings lSett=thisChannel.getLightSourceSettings();
				System.out.println("DEBUG: save: chLightSrc: "+lSett.getID());
				if(lSett.getID().equals("")){
					// lightSrcSettings not yet linked to created lightSrc
					lSett.setID(thisLightSrc.getID());
				}else if(!thisChannel.getLightSourceSettings().getID().equals(thisLightSrc.getID())){
					LOGGER.severe("[SAVE] wrong LIGHTSOURCE reference at CHANNEL "+thisChannel.getName());
				}
			}
			
			Detector thisDetector=model.getDetector(cNr);
			if(thisDetector==null){
				LOGGER.warning("could not save DETECTOR - not specified for CHANNEL "+thisChannel.getName());
			}else{
				DetectorSettings dSett = thisChannel.getDetectorSettings();
				if(dSett.getID().equals("")){
					//detectorSettings not yet linked to new created detector
					dSett.setID(thisDetector.getID());
				}else if(!thisChannel.getDetectorSettings().getID().equals(thisDetector.getID())){
					LOGGER.severe("[SAVE] wrong DETECTOR reference at CHANNEL "+thisChannel.getName());
				}
			}
			
			LightPath thisLightPath=thisChannel.getLightPath();
			if(thisLightPath==null){
				LOGGER.warning("[SAVE] could not save LIGHTPATH- not specified for CHANNEL "+thisChannel.getName());
			}
			
			if(thisChannel!=null)
				omeStore.storeChannel(thisChannel,model.getImageIndex());
			// store reference objects
			if(thisLightSrc!=null)
				omeStore.storeLightSrc(thisLightSrc, model.getImageIndex());
			if(thisDetector!=null)
				omeStore.storeDetector(thisDetector, model.getImageIndex());
			
//				if(thisLightPath!=null){
//					omeStore.storeLightPath(thisLightPath,model.getIm)
//				}
		}
	}

	private void saveLightSource() throws Exception {
		int numLightSrc=model.getNumberOfLightSrc();
		for(int lNr=0; lNr<numLightSrc; lNr++)
		{
			LightSource l=model.getLightSourceData(lNr);
			if(l!=null)
				omeStore.storeLightSrc(l,model.getImageIndex());
		}
	}

	//TODO id test
	private void saveDetector() throws Exception {
		//--- Detectors
		int numDetectors=model.getNumberOfDetectors();
		for(int dNr=0; dNr<numDetectors; dNr++)
		{
			Detector d = model.getDetector(dNr);
			if(d!=null)
				omeStore.storeDetector(d,model.getImageIndex());
		}
	}

	private void saveImageEnv() throws Exception {
		ImagingEnvironment iEnv=model.getImgagingEnv();
		if(iEnv!=null)
			omeStore.storeImagingEnv(iEnv,model.getImageIndex());
	}

	private void saveObjective() throws Exception {
		//--- save ObjectiveSettings and Objective
		ObjectiveSettings os=model.getObjectiveSettings();
		if(os!=null)
			omeStore.storeObjectiveSettings(os,model.getImageIndex());
		
		Objective o=model.getObjective();
		if(o!=null)
			omeStore.storeObjective(o,model.getImageIndex());
	}

	private void saveExperiment() throws Exception {
		//--- save Experiment and Experimenter data
		Experiment e = model.getExperiment();
		
		Image i=model.getImageOMEData();
		if(e!=null){
			omeStore.storeExperiment(e);
			// update refs
			i.linkExperiment(e);
			i.linkExperimenter(e.getLinkedExperimenter());
		}
		//TODO: refs update
		//Refs to experimenter: Dataset, ExperimenterGroup, Image, MicrobeamManipulation, Project
	}

	public void save() 
	{
		//save to existing file-> overwrite or add?
		String fname = srcImageFName+".ome";
		File file=new File(fname);//fchooser.getSelectedFile();
		// first solution: overwrite metadata
		saveMetaData(file);
		
	}
	
}
