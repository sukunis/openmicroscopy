package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules;

import java.util.ArrayList;

import ome.units.quantity.Power;
import ome.xml.model.Filament;
import ome.xml.model.Laser;
import ome.xml.model.LightSource;
import ome.xml.model.enums.FilamentType;
import ome.xml.model.enums.LaserType;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.ModuleConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagData;

public class FilamentCompUI extends LightSrcSubCompUI
{

	public FilamentCompUI(ModuleConfiguration objConf) {
		System.out.println("# FilamentCompUI::new Instance 1");
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
			copyIn=new Filament((Filament)lightSrc);
		}

		replaceData(l);

		// set input field values again
		if(copyIn!=null){
			String mo=copyIn.getModel();
			String ma=copyIn.getManufacturer();
			Power p=copyIn.getPower();
			FilamentType t=((Filament)copyIn).getType();
			
			if(mo!=null && !mo.equals("")) lightSrc.setModel(mo);
			if(ma!=null && !ma.equals("")) lightSrc.setManufacturer(ma);
			if(p!=null) lightSrc.setPower(p);
			if(t!=null) ((Filament) lightSrc).setType(t);
		}
	}
	
	protected void setGUIData() 
	{
		
		try{ setManufact(((Filament)lightSrc).getManufacturer(), ElementsCompUI.REQUIRED);
		} catch (NullPointerException e) { }
		try{ setModel(((Filament)lightSrc).getModel(), ElementsCompUI.REQUIRED);
		} catch (NullPointerException e) { }
		try{ setPower(((Filament)lightSrc).getPower(), ElementsCompUI.REQUIRED);
		} catch (NullPointerException e) { }
		try{ setType(((Filament)lightSrc).getType(), ElementsCompUI.REQUIRED);
		} catch (NullPointerException e) { }
		
	}
	protected void readGUIInput() throws Exception 
	{
		System.out.println("# FilamentCompUI::setGUIData()");
		if(lightSrc==null)
			createNewElement();
		try{
			((Filament)lightSrc).setManufacturer(manufact.getTagValue().equals("")? 
					null : manufact.getTagValue());
		}catch(Exception e){
			LOGGER.error("[DATA] can't read LIGHTSRC fila manufacturer input");
		}
		try{
			((Filament)lightSrc).setModel(model.getTagValue().equals("")? 
					null : model.getTagValue());
		}catch(Exception e){
			LOGGER.error("[DATA] can't read LIGHTSRC fila model input");
		}
		try{
			((Filament)lightSrc).setPower(LightSourceCompUI.parsePower(power.getTagValue(),power.getTagUnit()));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read LIGHTSRC fila power input");
		}
		try{
			((Filament)lightSrc).setType(type.getTagValue().equals("") ? 
					null : FilamentType.fromString(type.getTagValue()));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read LIGHTSRC fila type input");
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
		System.out.println("# FilamentCompUI::addTags()");
		addTagToGUI(model);
		addTagToGUI(manufact);
		

			addTagToGUI(type);
			addTagToGUI(power); 
	
	}
	protected void createNewElement() 
	{System.out.println("# FilamentCompUI::createNewElement()");
		lightSrc=new Filament();
	}
	protected void createDummyPane(boolean inactive) 
	{
		System.out.println("# FilamentCompUI::createDummyPane(boolean)");
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
