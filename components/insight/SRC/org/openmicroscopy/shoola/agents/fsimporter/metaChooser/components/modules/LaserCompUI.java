package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules;

import java.util.ArrayList;

import ome.units.quantity.Frequency;
import ome.units.quantity.Length;
import ome.units.quantity.Power;
import ome.xml.model.Laser;
import ome.xml.model.LightSource;
import ome.xml.model.enums.LaserMedium;
import ome.xml.model.enums.LaserType;
import ome.xml.model.enums.Pulse;
import ome.xml.model.primitives.PositiveInteger;

import org.apache.commons.lang.BooleanUtils;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.ModuleConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagData;

public class LaserCompUI extends LightSrcSubCompUI 
{

	public LaserCompUI(ModuleConfiguration objConf) 
	{
		System.out.println("# LaserCompUI::new Instance 1");
		lightSrc=null;
		initGUI();
		if(objConf==null)
			createDummyPane(false);
		else
			createDummyPane(objConf.getTagList(),false);
	}

	@Override
	protected void completeData(LightSource l) throws Exception 
	{
		//copy input fields
		LightSource copyIn=null;
		if(lightSrc!=null){
			getData();
			copyIn=new Laser((Laser)lightSrc);
		}

		replaceData(l);

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
			
			if(mo!=null && !mo.equals("")) lightSrc.setModel(mo);
			if(ma!=null && !ma.equals("")) lightSrc.setManufacturer(ma);
			if(p!=null) lightSrc.setPower(p);
			if(t!=null) ((Laser) lightSrc).setType(t);
			if(m!=null) ((Laser)lightSrc).setLaserMedium(m);
			if(fM!=null) ((Laser)lightSrc).setFrequencyMultiplication(fM);
			if(tu!=null){
				((Laser)lightSrc).setTuneable(tu);
			}
			if(po!=null) ((Laser)lightSrc).setPockelCell(po);
			if(rr!=null) ((Laser)lightSrc).setRepetitionRate(rr);
			if(w!=null) ((Laser)lightSrc).setWavelength(w);
			if(pu!=null) ((Laser)lightSrc).setPulse(pu);
			if(((Laser)l).getLinkedPump()!=null) ((Laser)lightSrc).linkPump(((Laser)l).getLinkedPump());
		}
	}
	
	protected void setGUIData()
	{
		try{setManufact(((Laser)lightSrc).getManufacturer(), ElementsCompUI.REQUIRED);
		} catch (NullPointerException e) { }
		try{setModel(((Laser)lightSrc).getModel(), ElementsCompUI.REQUIRED);
		} catch (NullPointerException e) { }
		try{ setPower(((Laser)lightSrc).getPower(), ElementsCompUI.REQUIRED);
		} catch (NullPointerException e) { }
		try{ setType(((Laser)lightSrc).getType(), ElementsCompUI.REQUIRED);
		} catch (NullPointerException e) { }
		try{ setMedium(((Laser)lightSrc).getLaserMedium(), ElementsCompUI.REQUIRED);
		} catch (NullPointerException e) { }
		try{ setFreqMultiplication(((Laser)lightSrc).getFrequencyMultiplication(), ElementsCompUI.REQUIRED);
		} catch (NullPointerException e) { }
		try{ setTunable(((Laser)lightSrc).getTuneable(), ElementsCompUI.REQUIRED);
		} catch (NullPointerException e) { }
		try{ setPulse(((Laser)lightSrc).getPulse(), ElementsCompUI.REQUIRED);
		} catch (NullPointerException e) { }
		try{ setPocketCell(((Laser)lightSrc).getPockelCell(), ElementsCompUI.REQUIRED);
		} catch (NullPointerException e) { }
		try{setRepititationRate(((Laser)lightSrc).getRepetitionRate(), ElementsCompUI.REQUIRED);
		} catch (NullPointerException e) { }
		try{setPump(((Laser)lightSrc).getLinkedPump().getID(), ElementsCompUI.REQUIRED);
		} catch (NullPointerException e) { }
		try{ setWavelength(((Laser)lightSrc).getWavelength(), ElementsCompUI.REQUIRED);
		} catch (NullPointerException e) { }
	}
	
	protected void readGUIInput() throws Exception 
	{
		if(lightSrc==null)
			createNewElement();
		try{
			((Laser)lightSrc).setManufacturer(manufact.getTagValue().equals("")? 
					null : manufact.getTagValue());
		}catch(Exception e){
			LOGGER.error("[DATA] can't read LIGHTSRC maunfacturer input");
		}
		try{
			((Laser)lightSrc).setModel(model.getTagValue().equals("")? 
					null : model.getTagValue());
		}catch(Exception e){
			LOGGER.error("[DATA] can't read LIGHTSRC model input");
		}
		try{
			if(power.getTagValue()!=null)
				((Laser)lightSrc).setPower(LightSourceCompUI.parsePower(power.getTagValue(),power.getTagUnit()));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read LIGHTSRC power input");
		}
		try{
			((Laser)lightSrc).setType(LightSourceCompUI.parseLaserType(type.getTagValue()));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read LIGHTSRC type input");
		}
		try{
			((Laser)lightSrc).setFrequencyMultiplication(parseToPositiveInt(freqMul.getTagValue()));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read LIGHTSRC freq multiplication input");
		}
		try{
			((Laser)lightSrc).setLaserMedium(LightSourceCompUI.parseMedium(medium.getTagValue()));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read LIGHTSRC medium input");
		}
		try{
			if(!tunable.getTagValue().equals(""))
			((Laser)lightSrc).setTuneable(BooleanUtils.toBoolean(tunable.getTagValue()));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read LIGHTSRC tunable input");
		}
		try{

			((Laser)lightSrc).setPulse(LightSourceCompUI.parsePulse(pulse.getTagValue()));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read LIGHTSRC pulse input");
		}
		try{
			if(!pockelCell.getTagValue().equals(""))
			((Laser)lightSrc).setPockelCell(Boolean.valueOf(pockelCell.getTagValue()));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read LIGHTSRC pockell cell input");
		}
		try{
			((Laser)lightSrc).setRepetitionRate(LightSourceCompUI.parseFrequency(repRate.getTagValue(), repRate.getTagUnit()));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read LIGHTSRC repetition rate input");
		}

		//TODO: link pump object
		//		((Laser)lightSrc).linkPump(pump.getTagValue().equals("") ? 
		//				null : pump.getTagValue());
		try{
			((Laser)lightSrc).setWavelength(parseToLength(waveLength.getTagValue(),waveLength.getTagUnit()));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read LIGHTSRC wavelength input");
		}
		
	}
	protected void createNewElement() 
	{
		lightSrc=new Laser();
	}

	protected void initTagList()
	{
		tagList=new ArrayList<TagData>();
		
		tagList.add(model);
		tagList.add(manufact);
		tagList.add(power);
		tagList.add(type);
		
		tagList.add(medium);
		tagList.add(freqMul);
		tagList.add(tunable);
		tagList.add(pulse);
		tagList.add(pockelCell);
		tagList.add(repRate);
		tagList.add(pump);
		tagList.add(waveLength);
	}
	
	protected void addTags()
	{
		addTagToGUI(model);
		addTagToGUI(manufact);
		 
			addTagToGUI(type);
			addTagToGUI(power);
			addTagToGUI(medium);
			addTagToGUI(freqMul);
			addTagToGUI(tunable);
			addTagToGUI(pulse);
			addTagToGUI(pockelCell);
			addTagToGUI(repRate);
			addTagToGUI(pump);
			if(pump!=null)pump.setEnable(false);
			addTagToGUI(waveLength);
	
	}
	protected void createDummyPane(boolean inactive) 
	{
		setManufact(null, OPTIONAL);
		setType((LaserType)null, OPTIONAL);
		setPower(null, OPTIONAL);
		setModel(null, OPTIONAL);
		
		setMedium(null, OPTIONAL);
		setFreqMultiplication(null, OPTIONAL);
		setTunable((String)null, OPTIONAL);
		setPulse(null, OPTIONAL);
		setPocketCell(null, OPTIONAL);
		setRepititationRate(null, OPTIONAL);
		setPump(null, OPTIONAL);
		setWavelength(null, OPTIONAL);
		
		
		if(inactive){
			manufact.setEnable(false);
			type.setEnable(false);
			power.setEnable(false);
			model.setEnable(false);

			medium.setEnable(false);
			freqMul.setEnable(false);
			tunable.setEnable(false);
			pulse.setEnable(false);
			pockelCell.setEnable(false);
			repRate.setEnable(false);
			pump.setEnable(false);
			waveLength.setEnable(false);
		}
	}
	
}
