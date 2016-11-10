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

/**
 * Works for xsi:schemaLocation="http://www.openmicroscopy.org/Schemas/OME/2015-01 
 * @author Kunis
 *
 */
public class LS_LEDViewer extends LightSourceSubViewer 
{
	public LS_LEDViewer(LightSourceModel model,ModuleConfiguration conf,int index,boolean showPreValues)
	{
		classification=LightSourceModel.LIGHT_EMITTING_DIODE;
		this.data=model;
		this.index=index;
		initComponents(conf);
		buildGUI();
		initTagList();
//		showPredefinitions(conf.getTagList(), showPreValues);
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
		if(data==null)
			return;
		LightEmittingDiode lightSrc=(LightEmittingDiode) data.getLightSource(index);
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
	public void saveData() 
	{
		if(data==null)
			data=new LightSourceModel();
		
		
		if( data.getLightSource(index)==null){
			try {
				data.addData(new LightEmittingDiode(), true, index);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		LightEmittingDiode lightSrc=null;
		try{
			lightSrc=(LightEmittingDiode) data.getLightSource(index);
		}catch(ClassCastException e){
			System.out.println("\tATTENTION...overwrite lightSrc with another type of lightSrc.");
			String oldClass=data.getLightSource(index).getClass().getSimpleName();
			try{
				data.addData(new LightEmittingDiode(), true, index);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			lightSrc=(LightEmittingDiode) data.getLightSource(index);
			System.out.println("\t...replace "+oldClass+" by "+data.getLightSource(index).getClass().getSimpleName());
		}
		
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
