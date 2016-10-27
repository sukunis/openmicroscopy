package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.view;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;

import ome.xml.model.Arc;
import ome.xml.model.Filament;
import ome.xml.model.Laser;

import org.apache.commons.lang.BooleanUtils;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules.ElementsCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules.LightSourceCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model.LightSourceModel;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.ModuleConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagData;

public class LS_LaserViewer extends LightSourceSubViewer
{
	/**
	 * Creates a new instance.
	 * @param model Reference to model.
	 */
	public LS_LaserViewer(LightSourceModel model,ModuleConfiguration conf,int index,boolean showPreValues)
	{
		classification=LightSourceModel.LASER;
		this.data=model;
		this.index=index;
		initComponents(conf);
		buildGUI();
		initTagList();
		showPredefinitions(conf.getTagList(), showPreValues);
	}
	
	@Override
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

	@Override
	protected void setGUIData() {
		if(data==null)
			return;
		Laser lightSrc=(Laser) data.getLightSource(index);
		
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

	@Override
	protected void addTags(List<JLabel> labels, List<JComponent> comp) {
		addTagToGUI(model,labels,comp);
		addTagToGUI(manufact,labels,comp);

		addTagToGUI(type,labels,comp);
		addTagToGUI(power,labels,comp);
		addTagToGUI(medium,labels,comp);
		addTagToGUI(freqMul,labels,comp);
		addTagToGUI(tunable,labels,comp);
		addTagToGUI(pulse,labels,comp);
		addTagToGUI(pockelCell,labels,comp);
		addTagToGUI(repRate,labels,comp);
		addTagToGUI(pump,labels,comp);
		if(pump!=null)pump.setEnable(false);
		addTagToGUI(waveLength,labels,comp);		
	}

	@Override
	public void saveData() 
	{
		System.out.println("# LS_LaserViewer::saveData()");
		if(data==null)
			data=new LightSourceModel();
		
		Laser lightSrc=null;
		try{
			lightSrc=(Laser) data.getLightSource(index);
		}catch(ClassCastException e){
			System.out.println("\t...overwrite lightSrc with another type of lightSrc.");
		}
		if(lightSrc==null){
			lightSrc=new Laser();
			try {
				data.addData(lightSrc, true, index);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
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
				((Laser)lightSrc).setPower(LightSourceSubViewer.parsePower(power.getTagValue(),power.getTagUnit()));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read LIGHTSRC power input");
		}
		try{
			((Laser)lightSrc).setType(LightSourceSubViewer.parseLaserType(type.getTagValue()));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read LIGHTSRC type input");
		}
		try{
			((Laser)lightSrc).setFrequencyMultiplication(parseToPositiveInt(freqMul.getTagValue()));
			freqMul.setTagInfo("");
		}catch(Exception e){
			LOGGER.error("[DATA] can't read LIGHTSRC freq multiplication input");
			freqMul.setTagInfo("Can't parse input value"+freqMul.getTagValue());
		}
		try{
			((Laser)lightSrc).setLaserMedium(LightSourceSubViewer.parseMedium(medium.getTagValue()));
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

			((Laser)lightSrc).setPulse(LightSourceSubViewer.parsePulse(pulse.getTagValue()));
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
			((Laser)lightSrc).setRepetitionRate(LightSourceSubViewer.parseFrequency(repRate.getTagValue(), repRate.getTagUnit()));
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

}
