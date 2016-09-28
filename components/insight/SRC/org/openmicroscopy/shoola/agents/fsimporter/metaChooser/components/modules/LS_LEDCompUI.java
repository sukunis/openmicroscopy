package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules;

import java.util.ArrayList;

import ome.xml.model.GenericExcitationSource;
import ome.xml.model.LightEmittingDiode;
import ome.xml.model.LightSource;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.ModuleConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagData;

public class LS_LEDCompUI extends LightSrcSubCompUI
{

	public LS_LEDCompUI(ModuleConfiguration objConf) {
		classification="LightEmittingDiode";
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
			copyIn=new LightEmittingDiode((LightEmittingDiode)lightSrc);
		}

		replaceData(l);

		// set input field values again
		if(copyIn!=null){
			String mo=copyIn.getModel();
			String ma=copyIn.getManufacturer();
			
			if(mo!=null && !mo.equals("")) lightSrc.setModel(mo);
			if(ma!=null && !ma.equals("")) lightSrc.setManufacturer(ma);
		}
	}
	
	protected void setGUIData() 
	{
		
		try{ setManufact(((LightEmittingDiode)lightSrc).getManufacturer(), ElementsCompUI.REQUIRED);
		} catch (NullPointerException e) { }
		try{ setModel(((LightEmittingDiode)lightSrc).getModel(), ElementsCompUI.REQUIRED);
		} catch (NullPointerException e) { }
		try{ setDescription(null, ElementsCompUI.REQUIRED);
		} catch (NullPointerException e) { }
	}
	
	protected void readGUIInput() throws Exception 
	{
		System.out.println("# LS_LEDCompUI::readGUIInput()");
		if(lightSrc==null)
			createNewElement();
		try{
			((LightEmittingDiode)lightSrc).setManufacturer(manufact.getTagValue().equals("")? null : manufact.getTagValue());
		}catch(Exception e){
			LOGGER.error("[DATA] can't read LIGHTSRC led manufacturer input");
		}
		try{
			((LightEmittingDiode)lightSrc).setModel(model.getTagValue().equals("")? null : model.getTagValue());
		}catch(Exception e){
			LOGGER.error("[DATA] can't read LIGHTSRC led model input");
		}
		//TODO ((LightEmittingDiode)lightSrc).setLinkedAnnotation(index, o)
	}
	
	protected void initTagList()
	{
		tagList=new ArrayList<TagData>();
		
		tagList.add(model);
		tagList.add(manufact);
		tagList.add(description);
	}
	
	protected void addTags()
	{
		addTagToGUI(model);
		addTagToGUI(manufact);
		

			addTagToGUI(description);
	}
	
	protected void createNewElement() 
	{
		lightSrc=new LightEmittingDiode();
	}
	
	
	protected void createDummyPane(boolean inactive) 
	{
		setManufact(null, OPTIONAL);
		setModel(null, OPTIONAL);
		setDescription(null, OPTIONAL);
		if(inactive){
			manufact.setEnable(false);
			model.setEnable(false);
			description.setEnable(false);
		}
	}
	@Override
	protected void setAllValueChanged() {
		System.out.println("# LS_LEDCompUI::setAllTagsChanged()");
		if(manufact!=null) manufact.dataSaved(false);
		if(description!=null)description.dataSaved(false);
		if(model!=null)model.dataSaved(false);
	}
}
