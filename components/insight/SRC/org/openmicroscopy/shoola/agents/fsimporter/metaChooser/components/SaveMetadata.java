package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.io.FilenameUtils;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.UOSMetadataLogger;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.format.ExperimentModel;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.format.Sample;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules.ExperimentCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.microscope.MetaDataUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.ExceptionDialog;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.ExperimenterListModel;
import org.slf4j.LoggerFactory;

import loci.formats.meta.IMetadata;
import ome.specification.XMLWriter;
import ome.xml.model.Channel;
import ome.xml.model.Detector;
import ome.xml.model.DetectorSettings;
import ome.xml.model.Dichroic;
import ome.xml.model.Experiment;
import ome.xml.model.Experimenter;
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
//    private static Logger LOGGER = Logger.getLogger(UOSMetadataLogger.class.getName());
	 private static final org.slf4j.Logger LOGGER =
	    	    LoggerFactory.getLogger(SaveMetadata.class);
	private MetaDataModel model;
	private IMetadata omexmlMeta;
	private OME ome;
	private OMEStore omeStore;
	private String srcImageFName;
	private String linkFile;

	private MetaDataModelObject modelObj;
	
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
			String name= srcImage!=null ? srcImage.getAbsolutePath() : "";
			LOGGER.error("[SAVE] corrupted save data: ");
			ExceptionDialog ld = new ExceptionDialog("Invalid Data Error!", 
					"Corrupted data : "+name,err,this.getClass().getSimpleName());
			ld.setVisible(true);
		}
		
	}
	
	public SaveMetadata(OME _ome, MetaDataModelObject mList,
			IMetadata _store, File srcImage) 
	{
		try{
			ome=_ome;
			modelObj=mList;
			omexmlMeta=_store;

			omeStore=new OMEStore(ome);

			FileAnnotation annotation = new FileAnnotation();
			//		annotation.setName(srcImage.getName());
			//		annotation.setFile(srcImage);

			// save xml in *.ome under same name and path like srcImage
			srcImageFName=FilenameUtils.removeExtension(srcImage.getAbsolutePath());
			linkFile=srcImage.getName();
		}catch (Exception err){
			String name= srcImage!=null ? srcImage.getAbsolutePath() : "";
			LOGGER.error("[SAVE] corrupted save data: ");
			ExceptionDialog ld = new ExceptionDialog("Invalid Data Error!", 
					"Corrupted data : "+name,err,this.getClass().getSimpleName());
			ld.setVisible(true);
		}
	}

	private void saveMetaData(File file)
	{
		XMLWriter writer=new XMLWriter();

		try {
			writeDataToOME();
			writer.writeFile(file, omeStore.getOME(), false);
			LOGGER.info("[SAVE] save to "+file.getAbsolutePath());
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	//write data to omexmlMeta
	private void writeInputToOME(MetaDataModel m) throws Exception
	{
		Image i=m.getImageOMEData();

		if(i!=null){
			saveExperiment(i,m);


			saveObjective(i,m);

			saveImageEnv(i,m); 	

			//TODO save stagelabel

			//TODO save planes, moechte man ueberhaupt hier aenderungen zulassen???

			//TODO extra store of detector and lightSrc necessary?? see bottum
			saveDetector(i,m);
			
			

			//--- LightSources
			//TODO update two lists
			saveLightSource(i,m);

			saveChannel(i,m);
			saveSample(i,m);

			saveImage(i,m);

						saveFilter(i,m); 
			//			
						saveDichroic(i,m); 
		}
		
	}
	
	

	private void writeDataToOME() throws Exception
	{
		if(modelObj==null){
			//single image
			writeInputToOME(model);
		}else{
			//series data
			for(MetaDataModel m : modelObj.getList()){
				writeInputToOME(m);
			}
		}
	}

	
	private void saveSample(Image i,MetaDataModel m ) throws Exception 
	{
		Sample s = m.getSample();
		if(s!=null)
			omeStore.storeSample(s,i);
		else
			LOGGER.info("SAMPLE is empty");
		
	}
	
	
	private void saveDichroic(Image img,MetaDataModel m) {
		//--Dichroics
		List<Dichroic> dList=m.getDichroicList();
		if(dList!=null)
			omeStore.storeDichroicList(dList,img,m.getImageIndex());
	}

	private void saveFilter(Image img,MetaDataModel m) {
		List<Filter> fList=m.getFilterList();
		if(fList!=null)
			omeStore.storeFilterList(fList,img,m.getImageIndex());
	}

	private void saveImage(Image image,MetaDataModel m) throws Exception {
		//--- Image
		if(image!=null)
			omeStore.storeImage(image,m.getImageIndex());
	}

	private void saveChannel(Image i,MetaDataModel m) throws Exception 
	{
		//--- Channels
		int numChannels=m.getNumberOfChannels();
		for(int cNr=0; cNr<numChannels; cNr++){
			Channel thisChannel=m.getChannel(cNr);
			LightSource thisLightSrc=m.getLightSourceData(cNr);
			Detector thisDetector=m.getDetector(cNr);
			LightPath thisLightPath = m.getLightPath(cNr);
			
			if(thisChannel!=null){
				//link
				if(thisLightSrc==null){
					LOGGER.warn("[SAVE] could not save LIGHTSOURCE - not specified for CHANNEL "+thisChannel.getName());
				}else{
					LightSourceSettings lSett=m.getLightSourceSettings(cNr);
					if(lSett!=null){
						if(lSett.getID()==null || lSett.getID().equals("")){
							// lightSrcSettings not yet linked to created lightSrc
							LOGGER.info("[SAVE] link LIGHTSRC to channel");
							lSett.setID(thisLightSrc.getID());
						}else if(!lSett.getID().equals(thisLightSrc.getID())){
							LOGGER.error("[SAVE] wrong LIGHTSOURCE reference at CHANNEL "+thisChannel.getName()+": "+
									lSett.getID()+" - "+thisLightSrc.getID());
							ExceptionDialog ld = new ExceptionDialog("Link LightSource Error!", 
									"Wrong lightsource reference at channel "+thisChannel.getName()+": "+
										lSett.getID()+" - "+thisLightSrc.getID(),
										this.getClass().getSimpleName());
							ld.setVisible(true);
						}else{
							omeStore.storeLightSrcSettings(lSett,thisChannel);
						}
					}
				}

				if(thisDetector==null ){
					LOGGER.warn("[SAVE] could not save DETECTOR - not specified for CHANNEL "+thisChannel.getName());
				}else{
					DetectorSettings dSett = m.getDetectorSettings(cNr);
					if(dSett!=null){
						if(dSett.getID()==null || dSett.getID().equals("")){
							//detectorSettings not yet linked to new created detector
							LOGGER.info("[SAVE] link DETECTOR to channel");
							dSett.setID(thisDetector.getID());
						}else if(!dSett.getID().equals(thisDetector.getID())){
							LOGGER.error("[SAVE] wrong DETECTOR reference at CHANNEL "+thisChannel.getName()+": "+
									dSett.getID()+" - "+thisDetector.getID());
							ExceptionDialog ld = new ExceptionDialog("Link Detector Error!", 
									"Wrong detector reference at channel "+thisChannel.getName()+": "+
										dSett.getID()+" - "+thisDetector.getID(),
										this.getClass().getSimpleName());
							ld.setVisible(true);
						}else{
							omeStore.storeDetectorSettings(dSett,thisChannel);
						}
					}
				}

				if(thisLightPath==null){
					LOGGER.warn("[SAVE] could not save LIGHTPATH- not specified for CHANNEL "+thisChannel.getName());
				}else{
					m.updateLightPathElems(thisLightPath, cNr);
					omeStore.storeLightPath(thisLightPath,thisChannel);
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

	private void saveLightSource(Image i,MetaDataModel m) throws Exception 
	{
		int numLightSrc=m.getNumberOfLightSrc();
		for(int lNr=0; lNr<numLightSrc; lNr++)
		{
			LightSource l=m.getLightSourceData(lNr);
			if(l!=null)
				omeStore.storeLightSrc(l,i,m.getImageIndex());
		}
	}

	//TODO id test
	private void saveDetector(Image i,MetaDataModel m) throws Exception 
	{
		int numDetectors=m.getNumberOfDetectors();
		for(int dNr=0; dNr<numDetectors; dNr++)
		{
			Detector d = m.getDetector(dNr);
			if(d!=null)
				omeStore.storeDetector(d,i,m.getImageIndex());
		}
	}
	
	

	private void saveImageEnv(Image i,MetaDataModel m) throws Exception {
		ImagingEnvironment iEnv=m.getImgagingEnv();
		if(iEnv!=null)
			omeStore.storeImagingEnv(iEnv,i);
	}

	/**
	 * Write objective to ome::instrument.
	 * Set objective settings in model::image
	 * @throws Exception
	 */
	private void saveObjective(Image i,MetaDataModel m) throws Exception {
		//--- save ObjectiveSettings and Objective
		ObjectiveSettings os=m.getObjectiveSettings();
		if(os!=null)
			omeStore.storeObjectiveSettings(os,i);
		
		Objective o=m.getObjective();
		if(o!=null )
			omeStore.storeObjective(o,i,m.getImageIndex());
	}

	/**
	 * Write Experiment and Experimenter to ome file . 
	 * Set link to experiment in model::image. 
	 * @throws Exception
	 */
	private void saveExperiment(Image i,MetaDataModel m) throws Exception 
	{
		//--- save Experiment and Experimenter data
		ExperimentModel e = m.getExperimentModel();
		
		if(e!=null){
			omeStore.storeExperiment(e); 
//			omeStore.storeExperimenter(((ExperimentCompUI)m.getExpModul()).getProjectPartnerAsExp()); 
			// update refs
			i.linkExperiment(e.getExperiment());
			i.linkExperimenter(e.getExperimenter());
			
			//if there are more than one experimenter?
//			ExperimentCompUI eUI = m.getExpModul();
			Experimenter exper=e.getExperimenter();//eUI.getExperimenterList();
			
			omeStore.storeExperimenter(exper);
			
			// store experiment type, desc and project partner in mapAnnotations of image
			omeStore.appendExperimentToMap(e.getExperiment(),i);
			omeStore.appendProjectPartnerToMap(e.getProjectPartner(),i);
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
