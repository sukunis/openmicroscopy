package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model;

import java.util.ArrayList;
import java.util.List;

import ome.units.quantity.ElectricPotential;
import ome.units.quantity.Length;
import ome.units.unit.Unit;
import ome.xml.model.Detector;
import ome.xml.model.DetectorSettings;
import ome.xml.model.LightSource;
import ome.xml.model.enums.Binning;
import ome.xml.model.enums.Correction;
import ome.xml.model.enums.DetectorType;
import ome.xml.model.enums.Immersion;
import ome.xml.model.enums.Medium;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.view.ModuleViewer;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.view.DetectorViewer;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.TagNames;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagData;
import org.slf4j.LoggerFactory;

public class DetectorModel 
{
	 private static final org.slf4j.Logger LOGGER =
	    	    LoggerFactory.getLogger(DetectorModel.class);
	
	private List<Detector> element;
	
	// settings
	private List<DetectorSettings> settings;
	
	// list of available detector (set by hardware definition)
	private List<Detector> availableElem;
	
	public DetectorModel()
	{
		element=new ArrayList<Detector>();
		settings=new ArrayList<DetectorSettings>();
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
	public void addData(Detector newElem,boolean overwrite,int index) throws Exception
	{
		if(element.size()<=index){
			expandList(element.size(),index);
		}
		
		if(overwrite){
			replaceData(newElem,index);
			LOGGER.info("[DATA] -- replace Detector data");
		}else{
			completeData(newElem,index);
			LOGGER.info("[DATA] -- complete Detector data");
		}
	}
	
	/**
	 * Overwrite or complete settings data. Caller class has to handle notification about the changes.
	 * @param newElem
	 * @param overwrite
	 * @throws Exception
	 */
	public void addData(DetectorSettings newElem,boolean overwrite,int index) throws Exception
	{
		if(overwrite){
			replaceData(newElem,index);
			LOGGER.info("[DATA] -- replace Detector data");
		}else{
			completeData(newElem,index);
			LOGGER.info("[DATA] -- complete Detector data");
		}
	}
	
	/**
	 * Overwrite data with given data
	 * @param newElem
	 */
	private void replaceData(Detector newElem,int index)
	{
			
		if(newElem!=null){
			element.set(index, newElem);
		}
	}
	
	/**
	 * If index exits size, expand elements and settings list
	 * @param size
	 * @param index
	 */
	private void expandList(int size,int index) 
	{
		for(int i=size;i<index+1;i++){
			element.add(new Detector());
			settings.add(new DetectorSettings());
		}
	}

	/**
	 * Overwrite data with given data
	 * @param newElem
	 */
	private void replaceData(DetectorSettings newElem,int index)
	{
		if(settings.size()<=index){
			expandList(settings.size(),index);
		}
		if(newElem!=null){
			settings.set(index,newElem);
		}
	}
	
	/**
	 * Complete existing data with data from newElem
	 * @param newElem
	 * @throws Exception
	 */
	private void completeData(Detector newElem,int index) throws Exception
	{
		
		//copy input fields
		Detector copyIn=null;
		if(element!=null){
			copyIn=new Detector(element.get(index));
		}

		replaceData(newElem,index);
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
			
			Detector d=element.get(index);
			if(mo!=null && !mo.equals("")) d.setModel(mo);
			if(ma!=null && !ma.equals("")) d.setManufacturer(ma);
			if(t!=null) d.setType(t);
			if(v!=null) d.setVoltage(v);
			if(o!=null) d.setOffset(o);
			if(z!=null) d.setZoom(z);
			if(a!=null) d.setAmplificationGain(a);
			if(g!=null) d.setGain(g);
		}
	}
	
	/**
	 * Complete existing data with data from newElem
	 * @param newElem
	 * @throws Exception
	 */
	private void completeData(DetectorSettings newElem,int index) throws Exception
	{
		if(settings.size()<=index){
			expandList(settings.size(),index);
		}
		//copy input fields
		DetectorSettings copyIn=null;
		if(settings!=null){
			copyIn=new DetectorSettings(settings.get(index));
		}

		replaceData(newElem,index);

		// set input field values again
		if(copyIn!=null){
			Double g=copyIn.getGain();
			ElectricPotential v=copyIn.getVoltage();
			Double o=copyIn.getOffset();
			Double z=copyIn.getZoom();
			Binning b=copyIn.getBinning();

			DetectorSettings sett=settings.get(index);
			if(g!=null) sett.setGain(g);
			if(v!=null) sett.setVoltage(v);
			if(o!=null) sett.setOffset(o);
			if(z!=null) sett.setZoom(z);
			if(b!=null) sett.setBinning(b);
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
	public Detector getDetector(int index) {
		if(index>=element.size())
			return null;
		return element.get(index);
	}
	
	public DetectorSettings getSettings(int index)
	{
		if(index>=settings.size())
			return null;
		return settings.get(index);
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
	
	public void clearList() 
	{
		availableElem=null;
	}

	public int getNumberOfElements() {
		return element.size();
	}

	public void remove(int index) {
		if(element!=null && !element.isEmpty())
			element.remove(index);		
	}

	/**
	 * Update list of detectors with given modified tags.
	 * Do nothing if detector at index doesn't exists.
	 * @param changesDetector
	 * @throws Exception
	 */
	public void update(List<List<TagData>> changesDetector) throws Exception 
	{
		if(changesDetector==null)
			return;
		int index=0;
		for(List<TagData> list :changesDetector){
			if(list!=null && element.size()>index 
					&& element.get(index)!=null){
				Detector detector=element.get(index);
				DetectorSettings sett=settings.get(index);
				for(TagData t: list){
					updateTag(detector,sett,t.getTagName(),t.getTagValue(),t.getTagUnit());
				}
			}
			index++;
		}
	}

	/**
	 * Update tag of given channel if value!=""
	 * @param detector
	 * @param tagName
	 * @param tagValue
	 * @param tagUnit
	 * @throws Exception 
	 */
	private void updateTag(Detector detector, DetectorSettings sett,String tagName, String tagValue,
			Unit tagUnit) throws Exception 
	{
		if(tagValue.equals(""))
			return;
		
		switch (tagName) 
		{
		case TagNames.MODEL: 
			detector.setModel(tagValue);		
			break;
		case TagNames.MANUFAC: 
			detector.setManufacturer(tagValue);			
			break;
		case TagNames.TYPE:
			detector.setType(DetectorViewer.parseDetectorType(tagValue));
			break;
		case TagNames.ZOOM:
			detector.setZoom(ModuleViewer.parseToDouble(tagValue));
			break;
		case TagNames.AMPLGAIN:
			detector.setAmplificationGain(ModuleViewer.parseToDouble(tagValue));
			break;
			case TagNames.GAIN:
				if(sett!=null){
					sett.setGain(ModuleViewer.parseToDouble(tagValue));
				}
				break;
			case TagNames.VOLTAGE:
				if(sett!=null){
					sett.setVoltage(DetectorViewer.parseElectricPotential(tagValue,tagUnit));
				}
				break;
			case TagNames.OFFSET:
				if(sett!=null){
					sett.setOffset(ModuleViewer.parseToDouble(tagValue));
				}
				break;
			case TagNames.CONFZOOM:
				if(sett!=null){
					sett.setZoom(ModuleViewer.parseToDouble(tagValue));
				}
				break;
			case TagNames.BINNING:
				if(sett!=null){
					sett.setBinning(DetectorViewer.parseBinning(tagValue));
				}
				break;
			case TagNames.SUBARRAY:
				//TODO
				break;
		default: LOGGER.warn("[CONF] unknown tag: "+tagName );break;
		}
	}
}
