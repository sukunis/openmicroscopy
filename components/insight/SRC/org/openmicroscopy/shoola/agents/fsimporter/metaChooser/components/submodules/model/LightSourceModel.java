package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model;

import java.util.ArrayList;
import java.util.List;

import ome.units.quantity.Frequency;
import ome.units.quantity.Length;
import ome.units.quantity.Power;
import ome.units.unit.Unit;
import ome.xml.model.Arc;
import ome.xml.model.Detector;
import ome.xml.model.DetectorSettings;
import ome.xml.model.Filament;
import ome.xml.model.GenericExcitationSource;
import ome.xml.model.Laser;
import ome.xml.model.LightEmittingDiode;
import ome.xml.model.LightSource;
import ome.xml.model.LightSourceSettings;
import ome.xml.model.enums.ArcType;
import ome.xml.model.enums.FilamentType;
import ome.xml.model.enums.LaserMedium;
import ome.xml.model.enums.LaserType;
import ome.xml.model.enums.Pulse;
import ome.xml.model.primitives.PercentFraction;
import ome.xml.model.primitives.PositiveInteger;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagData;
import org.slf4j.LoggerFactory;

public class LightSourceModel
{
	private static final org.slf4j.Logger LOGGER =
			LoggerFactory.getLogger(LightSourceModel.class);

	public final static String LASER="Laser";
	public final static String ARC="Arc";
	public final static String FILAMENT="Filament";
	public final static String GENERIC_EXCITATION="GenericExcitationSource";
	public final static String LIGHT_EMITTING_DIODE="LightEmittingDiode";

	private List<LightSource> element;

	// settings
	private List<LightSourceSettings> settings;

	// list of available lightSrc (set by hardware definition)
	private List<LightSource> availableElem;

	public LightSourceModel()
	{
		element=new ArrayList<LightSource>();
		settings=new ArrayList<LightSourceSettings>();
	}

	//copy constructor
	public LightSourceModel(LightSourceModel orig)
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
	public void addData(LightSource newElem,boolean overwrite,int i) throws Exception
	{
		System.out.println("# LightSrcModel::addData() listSize: "+element.size());
		if(element.size()<=i){
			switch(newElem.getClass().getSimpleName()){
			case LASER: expandList(element.size(),i,new Laser());break;
			case ARC: expandList(element.size(),i,new Arc());break;
			case FILAMENT: expandList(element.size(),i,new Filament());break;
			case GENERIC_EXCITATION:expandList(element.size(),i,new GenericExcitationSource());break;
			case LIGHT_EMITTING_DIODE: expandList(element.size(),i,new LightEmittingDiode());break;
			default: System.out.println("\t...unknown type");break;
			}
			
		}
		if(overwrite || (newElem.getClass()!= element.getClass())){
			replaceData(newElem,i);
			LOGGER.info("[DATA] -- replace LightSource data");
		}else{
			completeData(newElem,i);
			LOGGER.info("[DATA] -- complete LightSource data");
		}
	}

	/**
	 * Overwrite or complete settings data. Caller class has to handle notification about the changes.
	 * @param newElem
	 * @param overwrite
	 * @throws Exception
	 */
	public void addData(LightSourceSettings newElem,boolean overwrite,int i) throws Exception
	{
		if(settings.size()<=i){
			for(int j=settings.size();j<i+1;j++){
				settings.add(new LightSourceSettings());
			}
		}
		if(overwrite){
			replaceData(newElem,i);
			LOGGER.info("[DATA] -- replace LightSource data");
		}else{
			completeData(newElem,i);
			LOGGER.info("[DATA] -- complete LightSource data");
		}
	}

	/**
	 * Overwrite data with given data
	 * @param newElem
	 */
	private void replaceData(LightSource newElem,int i)
	{
		if(newElem!=null){
			element.set(i,newElem);
		}
	}

	/**
	 * Overwrite data with given data
	 * @param newElem
	 */
	private void replaceData(LightSourceSettings newElem,int i)
	{
		if(newElem!=null){
			settings.set(i, newElem);
		}
	}

	/**
	 * Complete existing data with data from newElem
	 * @param newElem
	 * @throws Exception
	 */
	private void completeData(LightSource newElem,int i) throws Exception
	{
		switch(newElem.getClass().getSimpleName())
		{
		case LASER: completeLaserData(newElem,i);break;
		case ARC: completeArcData(newElem,i);break;
		case FILAMENT: completeFilamentData(newElem,i);break;
		case GENERIC_EXCITATION: completeGESData(newElem,i);break;
		case LIGHT_EMITTING_DIODE: completeLEDData(newElem,i);break;
		default: break;
		}

	}

	private void completeLEDData(LightSource newElem,int i) 
	{
		//copy input fields
		LightSource copyIn=null;
		if(element!=null){
			copyIn=new LightEmittingDiode((LightEmittingDiode)element.get(i));
		}

		replaceData(newElem,i);

		// set input field values again
		if(copyIn!=null){
			String mo=copyIn.getModel();
			String ma=copyIn.getManufacturer();

			if(mo!=null && !mo.equals("")) element.get(i).setModel(mo);
			if(ma!=null && !ma.equals("")) element.get(i).setManufacturer(ma);
		}

	}

	private void completeGESData(LightSource newElem,int i) 
	{
		//copy input fields
		LightSource copyIn=null;
		if(element!=null){
			copyIn=new GenericExcitationSource((GenericExcitationSource)element.get(i));
		}

		replaceData(newElem,i);

		// set input field values again
		if(copyIn!=null){
			String mo=copyIn.getModel();
			String ma=copyIn.getManufacturer();
			Power p=copyIn.getPower();

			if(mo!=null && !mo.equals("")) element.get(i).setModel(mo);
			if(ma!=null && !ma.equals("")) element.get(i).setManufacturer(ma);
			if(p!=null) element.get(i).setPower(p);
		}		
	}

	private void completeFilamentData(LightSource newElem,int i) 
	{
		//copy input fields
		LightSource copyIn=null;
		if(element!=null){
			copyIn=new Filament((Filament)element.get(i));
		}

		replaceData(newElem,i);

		// set input field values again
		if(copyIn!=null){
			String mo=copyIn.getModel();
			String ma=copyIn.getManufacturer();
			Power p=copyIn.getPower();
			FilamentType t=((Filament)copyIn).getType();

			if(mo!=null && !mo.equals("")) element.get(i).setModel(mo);
			if(ma!=null && !ma.equals("")) element.get(i).setManufacturer(ma);
			if(p!=null) element.get(i).setPower(p);
			if(t!=null) ((Filament) element.get(i)).setType(t);
		}

	}

	private void completeArcData(LightSource newElem,int i) {
		//copy input fields
		LightSource copyIn=null;
		if(element!=null){
			copyIn=new Arc((Arc)element.get(i));
		}

		replaceData(newElem,i);

		// set input field values again
		if(copyIn!=null){
			String mo=copyIn.getModel();
			String ma=copyIn.getManufacturer();
			Power p=copyIn.getPower();
			ArcType t=((Arc)copyIn).getType();

			if(mo!=null && !mo.equals("")) element.get(i).setModel(mo);
			if(ma!=null && !ma.equals("")) element.get(i).setManufacturer(ma);
			if(p!=null) element.get(i).setPower(p);
			if(t!=null) ((Arc) element.get(i)).setType(t);
		}		
	}

	private void completeLaserData(LightSource newElem,int i) {
		//copy input fields
		LightSource copyIn=null;
		if(element!=null){
			copyIn=new Laser((Laser)element.get(i));
		}

		replaceData(newElem,i);

		// set input field values again
		if(copyIn!=null){
			String mo=copyIn.getModel();
			String ma=copyIn.getManufacturer();
			Power p=copyIn.getPower();
			LaserType t=((Laser)copyIn).getType();
			LaserMedium m=((Laser)copyIn).getLaserMedium();
			PositiveInteger fM=((Laser)copyIn).getFrequencyMultiplication();
			Boolean tu=((Laser)copyIn).getTuneable();
			Boolean po=((Laser)copyIn).getPockelCell();
			Frequency rr=((Laser)copyIn).getRepetitionRate();
			Length w=((Laser)copyIn).getWavelength();
			Pulse pu=((Laser)copyIn).getPulse();

			if(mo!=null && !mo.equals("")) element.get(i).setModel(mo);
			if(ma!=null && !ma.equals("")) element.get(i).setManufacturer(ma);
			if(p!=null) element.get(i).setPower(p);
			if(t!=null) ((Laser) element.get(i)).setType(t);
			if(m!=null) ((Laser)element.get(i)).setLaserMedium(m);
			if(fM!=null) ((Laser)element.get(i)).setFrequencyMultiplication(fM);
			if(tu!=null){
				((Laser)element.get(i)).setTuneable(tu);
			}
			if(po!=null) ((Laser)element.get(i)).setPockelCell(po);
			if(rr!=null) ((Laser)element.get(i)).setRepetitionRate(rr);
			if(w!=null) ((Laser)element.get(i)).setWavelength(w);
			if(pu!=null) ((Laser)element.get(i)).setPulse(pu);
			if(((Laser)newElem).getLinkedPump()!=null) ((Laser)element.get(i)).linkPump(((Laser)newElem).getLinkedPump());
		}		
	}

	/**
	 * Complete existing data with data from newElem
	 * @param newElem
	 * @throws Exception
	 */
	private void completeData(LightSourceSettings newElem,int i) throws Exception
	{
		//copy input fields
		LightSourceSettings copyIn=null;
		if(settings!=null){
			copyIn=new LightSourceSettings(settings.get(i));
		}

		replaceData(newElem,i);

		// set input field values again
		if(copyIn!=null){
			Length w=copyIn.getWavelength();
			PercentFraction p=copyIn.getAttenuation();
			if(w!=null) settings.get(i).setWavelength(w);
			if(p!=null) settings.get(i).setAttenuation(p);
		}
	}

	public List<LightSource> getList()
	{
		return availableElem;
	}

	public LightSource getLightSource(int i) 
	{
		if(i>=element.size())
			return null;
		return element.get(i);
	}

	public LightSourceSettings getSettings(int i)
	{
		if(i>=settings.size())
			return null;
		return settings.get(i);
	}
	
	/**
	 * If index exits size, expand elements and settings list
	 * @param size
	 * @param index
	 */
	private void expandList(int size,int index,Object newElem) 
	{
		for(int i=size;i<index+1;i++){
			element.add((LightSource) newElem);
//			settings.add(new LightSourceSettings());
		}
	}
	
	/**
	 * Copy elements from given list to local list
	 * @param list
	 */
	public void addToList(List<LightSource> list)
	{
		if(list==null || list.size()==0)
			return;
		
		if(availableElem==null){

			availableElem=new ArrayList<LightSource>();
		}
		for(int i=0; i<list.size(); i++){
			availableElem.add(list.get(i));
		}

	}

	public void clearList() {
		availableElem=null;
	}
	public int getNumberOfLightSrc()
	{
		if(element==null)
			return 0;
		return element.size();
	}

	public void remove(int index) {
		if(element!=null && !element.isEmpty())
			element.remove(index);		
	}

	/**
	 * Update list of lightSrc with given modified tags.
	 * Do nothing if lightSrc at index doesn't exists.
	 * @param changesLightSrc
	 * @throws Exception
	 */
	public void update(List<List<TagData>> changesLightSrc) throws Exception 
	{
		if(changesLightSrc==null)
			return;
		int index=0;
		for(List<TagData> list :changesLightSrc){
			if(list!=null && element.size()>index 
					&& element.get(index)!=null){
				LightSource lightSrc=element.get(index);
				LightSourceSettings sett=settings.get(index);
				if(lightSrc.getClass().getSimpleName().equals(list.get(list.size()-1))){
					//				TODO: switch(lightSrcclass)
					for(TagData t: list){
						updateTag(lightSrc,sett,t.getTagName(),t.getTagValue(),t.getTagUnit());
					}
				}
			}
			index++;
		}		
	}

	
	private void updateTag(LightSource lightSrc, LightSourceSettings sett,
			String tagName, String tagValue, Unit tagUnit) throws Exception 
	{
//		if(tagValue.equals(""))
//			return;
//		
//		switch (tagName) 
//		{
	}

}
