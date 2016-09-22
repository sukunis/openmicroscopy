package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.view;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;

import ome.xml.model.GenericExcitationSource;
import ome.xml.model.LightEmittingDiode;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules.ElementsCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules.LightSourceCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model.LightSourceModel;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.ModuleConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagData;

public class LS_GESViewer extends LightSourceSubViewer
{
	public LS_GESViewer(LightSourceModel model,ModuleConfiguration conf)
	{
		classification=LightSourceModel.GENERIC_EXCITATION;
		this.data=model;
		initComponents(conf);
		buildGUI();
		initTagList();
	}

	@Override
	protected void initTagList() {
		tagList=new ArrayList<TagData>();

		tagList.add(model);
		tagList.add(manufact);
		tagList.add(power);
		tagList.add(map);		
	}

	@Override
	protected void setGUIData() {
		GenericExcitationSource lightSrc=(GenericExcitationSource) data.getLightSource();
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
	protected void addTags(List<JLabel> labels, List<JComponent> comp) {
		addTagToGUI(model,labels,comp);
		addTagToGUI(manufact,labels,comp);
		addTagToGUI(map,labels,comp);
		addTagToGUI(power,labels,comp);		
	}

	@Override
	public void saveData() {
		GenericExcitationSource lightSrc=(GenericExcitationSource) data.getLightSource();
		if(lightSrc==null)
			lightSrc=new GenericExcitationSource();
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

}
