package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules;

import java.util.ArrayList;

import ome.units.quantity.Power;
import ome.xml.model.Arc;
import ome.xml.model.Laser;
import ome.xml.model.LightSource;
import ome.xml.model.enums.ArcType;
import ome.xml.model.enums.LaserType;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.ModuleConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagData;

public class ArcCompUI extends LightSrcSubCompUI
{

	public ArcCompUI(ModuleConfiguration objConf) {
		System.out.println("# ArcCompUI::new Instance 1");
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
			copyIn=new Arc((Arc)lightSrc);
		}

		replaceData(l);

		// set input field values again
		if(copyIn!=null){
			String mo=copyIn.getModel();
			String ma=copyIn.getManufacturer();
			Power p=copyIn.getPower();
			ArcType t=((Arc)copyIn).getType();
			
			if(mo!=null && !mo.equals("")) lightSrc.setModel(mo);
			if(ma!=null && !ma.equals("")) lightSrc.setManufacturer(ma);
			if(p!=null) lightSrc.setPower(p);
			if(t!=null) ((Arc) lightSrc).setType(t);
		}
	}
	
	protected void setGUIData() 
	{
		try{ setManufact(((Arc)lightSrc).getManufacturer(), ElementsCompUI.REQUIRED);
		} catch (NullPointerException e) { }
		try{ setModel(((Arc)lightSrc).getModel(), ElementsCompUI.REQUIRED);
		} catch (NullPointerException e) { }
		try{ setPower(((Arc)lightSrc).getPower(), ElementsCompUI.REQUIRED);
		} catch (NullPointerException e) { }
		try{ setType(((Arc)lightSrc).getType(), ElementsCompUI.REQUIRED);
		} catch (NullPointerException e) { }
	}
	
	protected void readGUIInput() throws Exception 
	{
		if(lightSrc==null)
			createNewElement();
		try{
			((Arc)lightSrc).setManufacturer(manufact.getTagValue().equals("")? 
					null : manufact.getTagValue());
		}catch(Exception e){
			LOGGER.error("[DATA] can't read LIGHTSRC arc manufacturer input");
		}
		try{
			((Arc)lightSrc).setModel(model.getTagValue().equals("")? 
					null : model.getTagValue());
		}catch(Exception e){
			LOGGER.error("[DATA] can't read LIGHTSRC arc model input");
		}
		try{
			((Arc)lightSrc).setPower(LightSourceCompUI.parsePower(power.getTagValue(),power.getTagUnit()));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read LIGHTSRC arc power input. "+e.getMessage());
		}
		try{
			((Arc)lightSrc).setType(type.getTagValue().equals("") ? 
					null : ArcType.fromString(type.getTagValue()));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read LIGHTSRC arc type input");
		}
	}
	protected void initTagList()
	{
		tagList=new ArrayList<TagData>();
		
		tagList.add(model);
		tagList.add(manufact);
		tagList.add(power);
		tagList.add(type);
	}
	
	protected void addTags()
	{
		addTagToGUI(model);
		addTagToGUI(manufact);

			addTagToGUI(type);
			addTagToGUI(power); 
	
	}
	
	protected void createNewElement() 
	{
		lightSrc=new Arc();
	}
	protected void createDummyPane(boolean inactive) 
	{
		setManufact(null, OPTIONAL);
		setType((LaserType)null, OPTIONAL);
		setPower(null, OPTIONAL);
		setModel(null, OPTIONAL);
		if(inactive){
			manufact.setEnable(false);
			type.setEnable(false);
			power.setEnable(false);
			model.setEnable(false);
		}
	}
	
}
