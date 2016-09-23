package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model;

import java.util.ArrayList;
import java.util.List;

import ome.units.quantity.ElectricPotential;
import ome.units.quantity.Length;
import ome.xml.model.Detector;
import ome.xml.model.DetectorSettings;
import ome.xml.model.LightSource;
import ome.xml.model.enums.Binning;
import ome.xml.model.enums.Correction;
import ome.xml.model.enums.DetectorType;
import ome.xml.model.enums.Immersion;
import ome.xml.model.enums.Medium;

import org.slf4j.LoggerFactory;

public class DetectorModel 
{
	 private static final org.slf4j.Logger LOGGER =
	    	    LoggerFactory.getLogger(DetectorModel.class);
	
	private Detector element;
	
	// settings
	private DetectorSettings settings;
	
	// list of available detector (set by hardware definition)
	private List<Detector> availableElem;
	
	public DetectorModel()
	{
		element=new Detector();
	}
	
	//copy constructor
	public DetectorModel(DetectorModel orig)
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
	public void addData(Detector newElem,boolean overwrite) throws Exception
	{
		if(overwrite){
			replaceData(newElem);
			LOGGER.info("[DATA] -- replace Detector data");
		}else{
			completeData(newElem);
			LOGGER.info("[DATA] -- complete Detector data");
		}
	}
	
	/**
	 * Overwrite or complete settings data. Caller class has to handle notification about the changes.
	 * @param newElem
	 * @param overwrite
	 * @throws Exception
	 */
	public void addData(DetectorSettings newElem,boolean overwrite) throws Exception
	{
		if(overwrite){
			replaceData(newElem);
			LOGGER.info("[DATA] -- replace Detector data");
		}else{
			completeData(newElem);
			LOGGER.info("[DATA] -- complete Detector data");
		}
	}
	
	/**
	 * Overwrite data with given data
	 * @param newElem
	 */
	private void replaceData(Detector newElem)
	{
		if(newElem!=null){
			element=newElem;
		}
	}
	
	/**
	 * Overwrite data with given data
	 * @param newElem
	 */
	private void replaceData(DetectorSettings newElem)
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
	private void completeData(Detector newElem) throws Exception
	{
		//copy input fields
		Detector copyIn=null;
		if(element!=null){
			copyIn=new Detector(element);
		}

		replaceData(newElem);
		// set input field values again
		if(copyIn!=null){
			String mo=copyIn.getModel();
			String ma=copyIn.getManufacturer();
			DetectorType t=copyIn.getType();
			ElectricPotential v=copyIn.getVoltage();
			Double o = copyIn.getOffset();
			Double z=copyIn.getZoom();
			Double a=copyIn.getAmplificationGain();
			Double g=copyIn.getGain();
			
			if(mo!=null && !mo.equals("")) element.setModel(mo);
			if(ma!=null && !ma.equals("")) element.setManufacturer(ma);
			if(t!=null) element.setType(t);
			if(v!=null) element.setVoltage(v);
			if(o!=null) element.setOffset(o);
			if(z!=null) element.setZoom(z);
			if(a!=null) element.setAmplificationGain(a);
			if(g!=null) element.setGain(g);
		}
	}
	
	/**
	 * Complete existing data with data from newElem
	 * @param newElem
	 * @throws Exception
	 */
	private void completeData(DetectorSettings newElem) throws Exception
	{
		//copy input fields
		DetectorSettings copyIn=null;
		if(settings!=null){
			copyIn=new DetectorSettings(settings);
		}

		replaceData(newElem);

		// set input field values again
		if(copyIn!=null){
			Double g=copyIn.getGain();
			ElectricPotential v=copyIn.getVoltage();
			Double o=copyIn.getOffset();
			Double z=copyIn.getZoom();
			Binning b=copyIn.getBinning();

			if(g!=null) settings.setGain(g);
			if(v!=null) settings.setVoltage(v);
			if(o!=null) settings.setOffset(o);
			if(z!=null) settings.setZoom(z);
			if(b!=null) settings.setBinning(b);
		}
	}
	
	public List<Detector> getList()
	{
		return availableElem;
	}

	/**
	 * 
	 * @return
	 */
	public Detector getDetector() {
		return element;
	}
	
	public DetectorSettings getSettings()
	{
		return settings;
	}
	
	/**
	 * Copy elements from given list to local list
	 * @param list
	 */
	public void addToList(List<Detector> list)
	{
		if(list==null || list.size()==0)
			return;
		
		if(availableElem==null){
			availableElem=new ArrayList<Detector>();
		}
		for(int i=0; i<list.size(); i++){
			availableElem.add(list.get(i));
		}

	}
}
