package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.io.FilenameUtils;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.UOSMetadataLogger;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.ExperimentCompUI;
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
		try{
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
		}catch (Exception err){
			LOGGER.severe("[SAVE] corrupted save data: ");
			err.printStackTrace();
		}
		
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
			Image i=model.getImageOMEData();
			
			if(i!=null){
				saveExperiment(i);


				saveObjective(i);

				saveImageEnv(i); 	

				//TODO save stagelabel

				//TODO save planes, moechte man ueberhaupt hier aenderungen zulassen???

				//TODO extra store of detector and lightSrc necessary?? see bottum
							saveDetector(i);

				//--- LightSources
				//TODO update two lists
							saveLightSource(i);

				saveChannel(i);
				saveSample(i);

				saveImage(i);

				//			saveFilter(i); 
				//			
				//			saveDichroic(i); 
			}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	private void saveSample(Image i) throws Exception 
	{
		Sample s = model.getSample(); 
		if(s!=null)
			omeStore.storeSample(s,i);
		else
			LOGGER.info("SAMPLE is empty");
		
	}
	
	
	private void saveDichroic(Image img) {
		//--Dichroics
		List<Dichroic> dList=model.getDichroicList();
		if(dList!=null)
			omeStore.storeDichroicList(dList,img,model.getImageIndex());
	}

	private void saveFilter(Image img) {
		List<Filter> fList=model.getFilterList();
		if(fList!=null)
			omeStore.storeFilterList(fList,img,model.getImageIndex());
	}

	private void saveImage(Image image) throws Exception {
		//--- Image
		if(image!=null)
			omeStore.storeImage(image,model.getImageIndex());
	}

	private void saveChannel(Image i) throws Exception 
	{
		//--- Channels
		int numChannels=model.getNumberOfChannels();
		for(int cNr=0; cNr<numChannels; cNr++){
			Channel thisChannel=model.getChannel(cNr);
			LightSource thisLightSrc=model.getLightSourceData(cNr);
			Detector thisDetector=model.getDetector(cNr);
			
			if(thisChannel!=null){
				//link
				if(thisLightSrc==null){
					LOGGER.warning("[SAVE] could not save LIGHTSOURCE - not specified for CHANNEL "+thisChannel.getName());
				}else{
					LightSourceSettings lSett=model.getLightSourceSettings(cNr);
					if(lSett!=null){
						if(lSett.getID().equals("")){
							// lightSrcSettings not yet linked to created lightSrc
							LOGGER.info("[SAVE] link LIGHTSRC to channel");
							lSett.setID(thisLightSrc.getID());
						}else if(!lSett.getID().equals(thisLightSrc.getID())){
							LOGGER.severe("[SAVE] wrong LIGHTSOURCE reference at CHANNEL "+thisChannel.getName()+": "+
									lSett.getID()+" - "+thisLightSrc.getID());
						}else{
							omeStore.storeLightSrcSettings(lSett,thisChannel);
						}
					}
				}

				if(thisDetector==null ){
					LOGGER.warning("[SAVE] could not save DETECTOR - not specified for CHANNEL "+thisChannel.getName());
				}else{
					DetectorSettings dSett = model.getDetectorSettings(cNr);
					if(dSett!=null){
						if(dSett.getID().equals("")){
							//detectorSettings not yet linked to new created detector
							LOGGER.info("[SAVE] link DETECTOR to channel");
							dSett.setID(thisDetector.getID());
						}else if(!dSett.getID().equals(thisDetector.getID())){
							LOGGER.severe("[SAVE] wrong DETECTOR reference at CHANNEL "+thisChannel.getName()+": "+
									dSett.getID()+" - "+thisDetector.getID());
						}else{
							omeStore.storeDetectorSettings(dSett,thisChannel);
						}
					}
				}

				LightPath thisLightPath=thisChannel.getLightPath();
				if(thisLightPath==null){
					LOGGER.warning("[SAVE] could not save LIGHTPATH- not specified for CHANNEL "+thisChannel.getName());
				}
			}

			if(thisChannel!=null){
				omeStore.storeChannel(thisChannel,i);
			}
			// store reference objects
//			if(thisLightSrc!=null)
//				omeStore.storeLightSrc(thisLightSrc, i,model.getImageIndex());
//			if(thisDetector!=null)
//				omeStore.storeDetector(thisDetector, i,model.getImageIndex());

			//				if(thisLightPath!=null){
			//					omeStore.storeLightPath(thisLightPath,model.getIm)
			//				}
		}
	}

	private void saveLightSource(Image i) throws Exception 
	{
		int numLightSrc=model.getNumberOfLightSrc();
		for(int lNr=0; lNr<numLightSrc; lNr++)
		{
			LightSource l=model.getLightSourceData(lNr);
			if(l!=null)
				omeStore.storeLightSrc(l,i,model.getImageIndex());
		}
	}

	//TODO id test
	private void saveDetector(Image i) throws Exception 
	{
		int numDetectors=model.getNumberOfDetectors();
		for(int dNr=0; dNr<numDetectors; dNr++)
		{
			Detector d = model.getDetector(dNr);
			if(d!=null)
				omeStore.storeDetector(d,i,model.getImageIndex());
		}
	}

	private void saveImageEnv(Image i) throws Exception {
		ImagingEnvironment iEnv=model.getImgagingEnv();
		if(iEnv!=null)
			omeStore.storeImagingEnv(iEnv,i);
	}

	/**
	 * Write objective to ome::instrument.
	 * Set objective settings in model::image
	 * @throws Exception
	 */
	private void saveObjective(Image i) throws Exception {
		//--- save ObjectiveSettings and Objective
		ObjectiveSettings os=model.getObjectiveSettings();
		if(os!=null)
			omeStore.storeObjectiveSettings(os,i);
		
		Objective o=model.getObjective();
		if(o!=null)
			omeStore.storeObjective(o,i,model.getImageIndex());
	}

	/**
	 * Write Experiment and Experimenter to ome file . 
	 * Set link to experiment in model::image. 
	 * @throws Exception
	 */
	private void saveExperiment(Image i) throws Exception {
		//--- save Experiment and Experimenter data
		Experiment e = model.getExperiment();
		
		if(e!=null){
			omeStore.storeExperiment(e);
			omeStore.storeProjectPartner(((ExperimentCompUI)model.getExpModul()).getProjectPartnerAsExp()); 
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
