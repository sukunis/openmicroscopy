package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;

import ome.units.quantity.Length;
import ome.xml.model.Objective;
import ome.xml.model.ObjectiveSettings;
import ome.xml.model.enums.Correction;
import ome.xml.model.enums.Immersion;
import ome.xml.model.enums.Medium;

public class ObjectiveModel 
{
	 private static final org.slf4j.Logger LOGGER =
	    	    LoggerFactory.getLogger(ObjectiveModel.class);
	
	private Objective element;
	
	// settings
	private ObjectiveSettings settings;
	
	// list of available objective (set by hardware definition)
	private List<Objective> availableElem;
	
	public ObjectiveModel()
	{
		
	}
	
	//copy constructor
	public ObjectiveModel(ObjectiveModel orig)
	{
		element=orig.element;
		settings=orig.settings;
		availableElem=orig.availableElem;
	}
	
	/**
	 * Overwrite or complete data. Caller class has to handle notification about the changes.
	 * @param newElem
	 * @param overwrite
	 * @throws Exception
	 */
	public void addData(Objective newElem,boolean overwrite) throws Exception
	{
		if(overwrite){
			replaceData(newElem);
			LOGGER.info("[DATA] -- replace OBJECTIVE data");
		}else{
			completeData(newElem);
			LOGGER.info("[DATA] -- complete OBJECTIVE data");
		}
	}
	
	/**
	 * Overwrite or complete settings data. Caller class has to handle notification about the changes.
	 * @param newElem
	 * @param overwrite
	 * @throws Exception
	 */
	public void addData(ObjectiveSettings newElem,boolean overwrite) throws Exception
	{
		if(overwrite){
			replaceData(newElem);
			LOGGER.info("[DATA] -- replace OBJECTIVE data");
		}else{
			completeData(newElem);
			LOGGER.info("[DATA] -- complete OBJECTIVE data");
		}
	}
	
	/**
	 * Overwrite data with given data
	 * @param newElem
	 */
	private void replaceData(Objective newElem)
	{
		if(newElem!=null){
			element=newElem;
		}
	}
	
	/**
	 * Overwrite data with given data
	 * @param newElem
	 */
	private void replaceData(ObjectiveSettings newElem)
	{
		if(newElem!=null){
			settings=newElem;
		}
	}
	
	/**
	 * Complete existing data with data from newElem
	 * @param newElem
	 * @throws Exception
	 */
	private void completeData(Objective newElem) throws Exception
	{
		//copy input fields
		Objective copyIn=null;
		if(element!=null){
			copyIn=new Objective(element);
		}

		replaceData(newElem);
		// set input field values again
		if(copyIn!=null){
			String mo=copyIn.getModel();
			String ma=copyIn.getManufacturer();
			Double nm=copyIn.getNominalMagnification();
			Double cM=copyIn.getCalibratedMagnification();
			Double l=copyIn.getLensNA();
			Immersion i=copyIn.getImmersion();
			Correction c=copyIn.getCorrection();
			Length wD=copyIn.getWorkingDistance();
			Boolean ir=copyIn.getIris();
			
			if(mo!=null && !mo.equals("")) element.setModel(mo);
			if(ma!=null && !ma.equals("")) element.setManufacturer(ma);
			if(nm!=null) element.setNominalMagnification(nm);
			if(cM!=null) element.setCalibratedMagnification(cM);
			if(l!=null) element.setLensNA(l);
			if(i!=null) element.setImmersion(i);
			if(c!=null) element.setCorrection(c);
			if(wD!=null) element.setWorkingDistance(wD);
			if(ir!=null) element.setIris(ir);
		}
	}
	
	/**
	 * Complete existing data with data from newElem
	 * @param newElem
	 * @throws Exception
	 */
	private void completeData(ObjectiveSettings newElem) throws Exception
	{
		//copy input fields
		ObjectiveSettings copyIn=null;
		if(settings!=null){
			copyIn=new ObjectiveSettings(settings);
		}

		replaceData(newElem);

		// set input field values again
		if(copyIn!=null){
			Double rI=copyIn.getRefractiveIndex();
			Medium m=copyIn.getMedium();
			Double cc=copyIn.getCorrectionCollar();

			if(rI!=null)settings.setRefractiveIndex(rI);
			if(m!=null)settings.setMedium(m);
			if(cc!=null)settings.setCorrectionCollar(cc);
		}
	}
	
	public List<Objective> getList()
	{
		return availableElem;
	}

	public Objective getObjective() {
		return element;
	}
	
	public ObjectiveSettings getSettings()
	{
		return settings;
	}
	
	public void addToList(List<Objective> list)
	{
		if(list==null || list.size()==0)
			return;

		if(availableElem==null){
			availableElem=new ArrayList<Objective>();
		}
		for(int i=0; i<list.size(); i++){
			availableElem.add(list.get(i));
		}
	}
	public void clearList() 
	{
		availableElem=null;
	}
			
}
