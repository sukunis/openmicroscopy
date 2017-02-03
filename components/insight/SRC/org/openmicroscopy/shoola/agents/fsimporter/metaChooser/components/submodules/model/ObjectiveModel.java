package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model;

import java.util.HashMap;
import java.util.List;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.view.ModuleViewer;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.view.ObjectiveViewer;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.TagNames;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagData;
import org.slf4j.LoggerFactory;

import ome.units.quantity.Length;
import ome.units.unit.Unit;
import ome.xml.model.Objective;
import ome.xml.model.ObjectiveSettings;
import ome.xml.model.enums.Correction;
import ome.xml.model.enums.Immersion;
import ome.xml.model.enums.Medium;


/**
 * Works for xsi:schemaLocation="http://www.openmicroscopy.org/Schemas/OME/2015-01 
 * @author Kunis
 *
 */
public class ObjectiveModel 
{
	 private static final org.slf4j.Logger LOGGER =
	    	    LoggerFactory.getLogger(ObjectiveModel.class);
	
	private Objective element;
	
	// settings
	private ObjectiveSettings settings;
	
	private HashMap<String,String> map;
	
	// list of available objective (set by hardware definition)
//	private List<Objective> availableElem;
	
	public ObjectiveModel()
	{
		
	}
	
	//copy constructor
	public ObjectiveModel(ObjectiveModel orig)
	{
		element=orig.element;
		settings=orig.settings;
//		availableElem=orig.availableElem;
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
			System.out.println("# ObjectiveModel::replaceData()");
			element=new Objective(newElem);
		}
	}
	
	/**
	 * Overwrite data with given data
	 * @param newElem
	 */
	private void replaceData(ObjectiveSettings newElem)
	{
		if(newElem!=null){
			settings=new ObjectiveSettings(newElem);
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
	
	

	public Objective getObjective() {
		return element;
	}
	
	public ObjectiveSettings getSettings()
	{
		if(settings!=null && settings.getObjective()==null)
			settings.setObjective(element);
		
		return settings;
	}
	
	
	public HashMap<String,String> getMap()
	{
		return map;
	}
	
	public void setMap(HashMap<String,String> newMap)
	{
		map=newMap;
	}
	
//	public List<Objective> getList()
//	{
//		return availableElem;
//	}
//	public void addToList(List<Objective> list)
//	{
//		if(list==null || list.size()==0)
//			return;
//
//		if(availableElem==null){
//			availableElem=new ArrayList<Objective>();
//		}
//		for(int i=0; i<list.size(); i++){
//			availableElem.add(list.get(i));
//		}
//		System.out.println("# ObjectiveModel::addToList() - "+availableElem.size());
//	}
//	public void clearList() 
//	{
//		availableElem=null;
//	}

	public void update(List<TagData> changesObjective) throws Exception 
	{
		if(changesObjective==null){
			return;
		}
		
		for(TagData t: changesObjective){
			setTag(t.getTagName(),t.getTagValue(),t.getTagUnit());
		}
	}

	private void setTag(String tagName, String tagValue, Unit tagUnit) throws Exception 
	{
		if(tagValue.equals("") )
			return;
		

		switch (tagName) {
		case TagNames.MODEL:
			if(element!=null)
				element.setModel(tagValue);
			break;
		case TagNames.MANUFAC:
			if(element!=null)
			element.setManufacturer(tagValue);
			break;
		case TagNames.NOMMAGN:
			if(element!=null)
			element.setNominalMagnification(ModuleViewer.parseToDouble(tagValue));
			break;
		case TagNames.CALMAGN:
			if(element!=null)
			element.setCalibratedMagnification(ModuleViewer.parseToDouble(tagValue));
			break;
		case TagNames.LENSNA:
			if(element!=null)
			element.setLensNA(ModuleViewer.parseToDouble(tagValue));
			break;
		case TagNames.IMMERSION:
			if(element!=null)
			element.setImmersion(ObjectiveViewer.parseImmersion(tagValue));
			break;
		case TagNames.CORRECTION:
			if(element!=null)
			element.setCorrection(ObjectiveViewer.parseCorrection(tagValue));
			break;
		case TagNames.WORKDIST:
			if(element!=null)
			element.setWorkingDistance(ModuleViewer.parseToLength(tagValue, tagUnit, false));
			break;
		case TagNames.CORCOLLAR:
			if(settings!=null)
			settings.setCorrectionCollar(ModuleViewer.parseToDouble(tagValue));
			break;
		case TagNames.OBJ_MEDIUM:
			if(settings!=null)
			settings.setMedium(ObjectiveViewer.parseMedium(tagValue));
			break;
		case TagNames.REFINDEX:
			if(settings!=null)
			settings.setRefractiveIndex(ModuleViewer.parseToDouble(tagValue));
			break;
		default:
			LOGGER.warn("[CONF] unknown tag: "+tagName );break;
		}
		
	}
			
}
