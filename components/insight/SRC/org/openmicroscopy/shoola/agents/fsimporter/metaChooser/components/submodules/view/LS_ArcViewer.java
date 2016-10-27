package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.view;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;

import ome.xml.model.Arc;
import ome.xml.model.enums.ArcType;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules.ElementsCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules.LightSourceCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model.LightSourceModel;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.ModuleConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagData;

public class LS_ArcViewer extends LightSourceSubViewer 
{
	
	public LS_ArcViewer(LightSourceModel model,ModuleConfiguration conf,int index,boolean showPreValues)
	{
		classification=LightSourceModel.ARC;
		this.data=model;
		this.index=index;
		initComponents(conf);
		buildGUI();
		initTagList();
		showPredefinitions(conf.getTagList(), showPreValues);
	}
	@Override
	protected void initTagList() {
		tagList=new ArrayList<TagData>();

		tagList.add(model);
		tagList.add(manufact);
		tagList.add(power);
		tagList.add(type);		
	}

	@Override
	protected void setGUIData() {
		if(data==null)
			return;
		Arc lightSrc=(Arc) data.getLightSource(index);
		try{ setManufact(((Arc)lightSrc).getManufacturer(), ElementsCompUI.REQUIRED);
		} catch (NullPointerException e) { }
		try{ setModel(((Arc)lightSrc).getModel(), ElementsCompUI.REQUIRED);
		} catch (NullPointerException e) { }
		try{ setPower(((Arc)lightSrc).getPower(), ElementsCompUI.REQUIRED);
		} catch (NullPointerException e) { }
		try{ setType(((Arc)lightSrc).getType(), ElementsCompUI.REQUIRED);
		} catch (NullPointerException e) { }		
	}

	@Override
	protected void addTags(List<JLabel> labels, List<JComponent> comp) {
		addTagToGUI(model,labels,comp);
		addTagToGUI(manufact,labels,comp);

		addTagToGUI(type,labels,comp);
		addTagToGUI(power,labels,comp); 		
	}

	@Override
	public void saveData() 
	{
		if(data==null)
			data=new LightSourceModel();
		
		Arc lightSrc=null;
		try{
			lightSrc=(Arc) data.getLightSource(index);
		}catch(ClassCastException e){
			System.out.println("\t...overwrite lightSrc with another type of lightSrc.");
		}
		if(lightSrc==null){
			lightSrc=new Arc();
			try {
				data.addData(lightSrc, true, index);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

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
			((Arc)lightSrc).setPower(LightSourceSubViewer.parsePower(power.getTagValue(),power.getTagUnit()));
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
	
	

}
