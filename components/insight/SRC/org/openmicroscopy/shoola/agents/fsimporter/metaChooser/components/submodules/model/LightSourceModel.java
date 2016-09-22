package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model;

import java.util.List;

import ome.units.quantity.Frequency;
import ome.units.quantity.Length;
import ome.units.quantity.Power;
import ome.xml.model.Arc;
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

	private LightSource element;

	// settings
	private LightSourceSettings settings;

	// list of available lightSrc (set by hardware definition)
	private List<LightSource> availableElem;

	public LightSourceModel()
	{

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
	public void addData(LightSource newElem,boolean overwrite) throws Exception
	{
		if(overwrite || (newElem.getClass()!= element.getClass())){
			replaceData(newElem);
			LOGGER.info("[DATA] -- replace LightSource data");
		}else{
			completeData(newElem);
			LOGGER.info("[DATA] -- complete LightSource data");
		}
	}

	/**
	 * Overwrite or complete settings data. Caller class has to handle notification about the changes.
	 * @param newElem
	 * @param overwrite
	 * @throws Exception
	 */
	public void addData(LightSourceSettings newElem,boolean overwrite) throws Exception
	{
		if(overwrite){
			replaceData(newElem);
			LOGGER.info("[DATA] -- replace LightSource data");
		}else{
			completeData(newElem);
			LOGGER.info("[DATA] -- complete LightSource data");
		}
	}

	/**
	 * Overwrite data with given data
	 * @param newElem
	 */
	private void replaceData(LightSource newElem)
	{
		if(newElem!=null){
			element=newElem;
		}
	}

	/**
	 * Overwrite data with given data
	 * @param newElem
	 */
	private void replaceData(LightSourceSettings newElem)
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
	private void completeData(LightSource newElem) throws Exception
	{
		switch(newElem.getClass().getSimpleName())
		{
		case LASER: completeLaserData(newElem);break;
		case ARC: completeArcData(newElem);break;
		case FILAMENT: completeFilamentData(newElem);break;
		case GENERIC_EXCITATION: completeGESData(newElem);break;
		case LIGHT_EMITTING_DIODE: completeLEDData(newElem);break;
		default: break;
		}

	}

	private void completeLEDData(LightSource newElem) 
	{
		//copy input fields
		LightSource copyIn=null;
		if(element!=null){
			copyIn=new LightEmittingDiode((LightEmittingDiode)element);
		}

		replaceData(newElem);

		// set input field values again
		if(copyIn!=null){
			String mo=copyIn.getModel();
			String ma=copyIn.getManufacturer();

			if(mo!=null && !mo.equals("")) element.setModel(mo);
			if(ma!=null && !ma.equals("")) element.setManufacturer(ma);
		}

	}

	private void completeGESData(LightSource newElem) 
	{
		//copy input fields
		LightSource copyIn=null;
		if(element!=null){
			copyIn=new GenericExcitationSource((GenericExcitationSource)element);
		}

		replaceData(newElem);

		// set input field values again
		if(copyIn!=null){
			String mo=copyIn.getModel();
			String ma=copyIn.getManufacturer();
			Power p=copyIn.getPower();

			if(mo!=null && !mo.equals("")) element.setModel(mo);
			if(ma!=null && !ma.equals("")) element.setManufacturer(ma);
			if(p!=null) element.setPower(p);
		}		
	}

	private void completeFilamentData(LightSource newElem) 
	{
		//copy input fields
		LightSource copyIn=null;
		if(element!=null){
			copyIn=new Filament((Filament)element);
		}

		replaceData(newElem);

		// set input field values again
		if(copyIn!=null){
			String mo=copyIn.getModel();
			String ma=copyIn.getManufacturer();
			Power p=copyIn.getPower();
			FilamentType t=((Filament)copyIn).getType();

			if(mo!=null && !mo.equals("")) element.setModel(mo);
			if(ma!=null && !ma.equals("")) element.setManufacturer(ma);
			if(p!=null) element.setPower(p);
			if(t!=null) ((Filament) element).setType(t);
		}

	}

	private void completeArcData(LightSource newElem) {
		//copy input fields
		LightSource copyIn=null;
		if(element!=null){
			copyIn=new Arc((Arc)element);
		}

		replaceData(newElem);

		// set input field values again
		if(copyIn!=null){
			String mo=copyIn.getModel();
			String ma=copyIn.getManufacturer();
			Power p=copyIn.getPower();
			ArcType t=((Arc)copyIn).getType();

			if(mo!=null && !mo.equals("")) element.setModel(mo);
			if(ma!=null && !ma.equals("")) element.setManufacturer(ma);
			if(p!=null) element.setPower(p);
			if(t!=null) ((Arc) element).setType(t);
		}		
	}

	private void completeLaserData(LightSource newElem) {
		//copy input fields
		LightSource copyIn=null;
		if(element!=null){
			copyIn=new Laser((Laser)element);
		}

		replaceData(newElem);

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

			if(mo!=null && !mo.equals("")) element.setModel(mo);
			if(ma!=null && !ma.equals("")) element.setManufacturer(ma);
			if(p!=null) element.setPower(p);
			if(t!=null) ((Laser) element).setType(t);
			if(m!=null) ((Laser)element).setLaserMedium(m);
			if(fM!=null) ((Laser)element).setFrequencyMultiplication(fM);
			if(tu!=null){
				((Laser)element).setTuneable(tu);
			}
			if(po!=null) ((Laser)element).setPockelCell(po);
			if(rr!=null) ((Laser)element).setRepetitionRate(rr);
			if(w!=null) ((Laser)element).setWavelength(w);
			if(pu!=null) ((Laser)element).setPulse(pu);
			if(((Laser)newElem).getLinkedPump()!=null) ((Laser)element).linkPump(((Laser)newElem).getLinkedPump());
		}		
	}

	/**
	 * Complete existing data with data from newElem
	 * @param newElem
	 * @throws Exception
	 */
	private void completeData(LightSourceSettings newElem) throws Exception
	{
		//copy input fields
		LightSourceSettings copyIn=null;
		if(settings!=null){
			copyIn=new LightSourceSettings(settings);
		}

		replaceData(newElem);

		// set input field values again
		if(copyIn!=null){
			Length w=copyIn.getWavelength();
			PercentFraction p=copyIn.getAttenuation();
			if(w!=null) settings.setWavelength(w);
			if(p!=null) settings.setAttenuation(p);
		}
	}

	public List<LightSource> getList()
	{
		return availableElem;
	}

	public LightSource getLightSource() {
		return element;
	}

	public LightSourceSettings getSettings()
	{
		return settings;
	}

}
