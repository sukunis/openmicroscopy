package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules;

import java.util.ArrayList;

import ome.units.quantity.Power;
import ome.xml.model.GenericExcitationSource;
import ome.xml.model.Laser;
import ome.xml.model.LightSource;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.ModuleConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagData;

public class LS_GESCompUI extends LightSrcSubCompUI
{

	public LS_GESCompUI(ModuleConfiguration objConf) {
		classification="GenericExcitationSource";
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
			copyIn=new GenericExcitationSource((GenericExcitationSource)lightSrc);
		}

		replaceData(l);

		// set input field values again
		if(copyIn!=null){
			String mo=copyIn.getModel();
			String ma=copyIn.getManufacturer();
			Power p=copyIn.getPower();
			
			if(mo!=null && !mo.equals("")) lightSrc.setModel(mo);
			if(ma!=null && !ma.equals("")) lightSrc.setManufacturer(ma);
			if(p!=null) lightSrc.setPower(p);
		}
	}
	
	protected void setGUIData() 
	{
	
		try{ setManufact(((GenericExcitationSource)lightSrc).getManufacturer(), ElementsCompUI.REQUIRED);
		} catch (NullPointerException e) { }
		try{ setModel(((GenericExcitationSource)lightSrc).getModel(), ElementsCompUI.REQUIRED);
		} catch (NullPointerException e) { }
		try{ setPower(((GenericExcitationSource)lightSrc).getPower(), ElementsCompUI.REQUIRED);
		} catch (NullPointerException e) { }
		try{ setMap(((GenericExcitationSource)lightSrc).getMap().getPairs(), ElementsCompUI.REQUIRED);
		} catch (NullPointerException e) { }
	}
	
	@Override
	protected void readGUIInput() throws Exception 
	{
		System.out.println("# LS_GESCompUI::readGUIInput()");
		if(lightSrc==null)
			createNewElement();
		try{
			((GenericExcitationSource)lightSrc).setManufacturer(manufact.getTagValue().equals("")? 
					null : manufact.getTagValue());
		}catch(Exception e){
			LOGGER.error("[DATA] can't read LIGHTSRC ges manufacturer input");
		}
		try{
			((GenericExcitationSource)lightSrc).setModel(model.getTagValue().equals("")? 
					null : model.getTagValue());
		}catch(Exception e){
			LOGGER.error("[DATA] can't read LIGHTSRC ges model input");
		}
		try{
			((GenericExcitationSource)lightSrc).setPower(LightSourceCompUI.parsePower(power.getTagValue(),power.getTagUnit()));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read LIGHTSRC ges power input");
		}
		//TODO set Map
//		((GenericExcitationSource)lightSrc).setMap(map.getTagValue().equals("") ? 
//				null : );
	}
	
	protected void initTagList()
	{
		tagList=new ArrayList<TagData>();
		
		tagList.add(model);
		tagList.add(manufact);
		tagList.add(power);
		tagList.add(map);
	}
	
	protected void addTags()
	{
		addTagToGUI(model);
		addTagToGUI(manufact);
		addTagToGUI(map);
		addTagToGUI(power); 

	}
	
	protected void createNewElement() 
	{
		lightSrc=new GenericExcitationSource();
	}
	
	protected void createDummyPane(boolean inactive) 
	{
		setManufact(null, OPTIONAL);
		setPower(null, OPTIONAL);
		setModel(null, OPTIONAL);
		setMap(null, OPTIONAL);
		if(inactive){
			manufact.setEnable(false);
			map.setEnable(false);
			power.setEnable(false);
			model.setEnable(false);
		}
	}
	@Override
	protected void setAllValueChanged() {
		System.out.println("# LS_GESCompUI::setAllTagsChanged()");
		if(manufact!=null)manufact.changeIsUpdated(false);
		if(map!=null)map.changeIsUpdated(false);
		if(power!=null)power.changeIsUpdated(false);
		if(model!=null)model.changeIsUpdated(false);
	}

}
