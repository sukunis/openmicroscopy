package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.view;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;

import ome.xml.model.LightEmittingDiode;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules.ElementsCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model.LightSourceModel;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.ModuleConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagData;

public class LS_LEDViewer extends LightSourceSubViewer 
{
	public LS_LEDViewer(LightSourceModel model,ModuleConfiguration conf)
	{
		classification=LightSourceModel.LIGHT_EMITTING_DIODE;
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
		tagList.add(description);		
	}

	@Override
	protected void setGUIData() {
		LightEmittingDiode lightSrc=(LightEmittingDiode) data.getLightSource();
		try{ setManufact(((LightEmittingDiode)lightSrc).getManufacturer(), ElementsCompUI.REQUIRED);
		} catch (NullPointerException e) { }
		try{ setModel(((LightEmittingDiode)lightSrc).getModel(), ElementsCompUI.REQUIRED);
		} catch (NullPointerException e) { }
		try{ setDescription(null, ElementsCompUI.REQUIRED);
		} catch (NullPointerException e) { }		
	}

	@Override
	protected void addTags(List<JLabel> labels, List<JComponent> comp) {
		addTagToGUI(model,labels,comp);
		addTagToGUI(manufact,labels,comp);


		addTagToGUI(description,labels,comp);
	}

	@Override
	public void saveData() {
		LightEmittingDiode lightSrc=(LightEmittingDiode) data.getLightSource();
		if(lightSrc==null)
			lightSrc=new LightEmittingDiode();
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

	}

}
