package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules.ElementsCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.ModuleConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.TagNames;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagData;

import loci.formats.MetadataTools;
import loci.formats.meta.IMetadata;
import ome.units.UNITS;
import ome.units.quantity.ElectricPotential;
import ome.units.unit.Unit;
import ome.xml.model.DetectorSettings;
import ome.xml.model.Image;
import ome.xml.model.ObjectiveSettings;
import ome.xml.model.enums.Binning;
import ome.xml.model.enums.EnumerationException;
import omero.model.enums.DetectorTypeUnknown;


public class DetectorSettingsCompUI extends ElementsCompUI
{
	
	/** variable value, that also can change during the acq.
	 * See also DetectorSettings*/
	private TagData gain;
	/** voltage of detector*/
	private TagData voltage;
	/** offset of detector*/
	private TagData offset;
	/** confocal or scanning zoom*/
	private TagData confocalZoom;
	/**represents the number of pixels that are combined to form larger pixels*/
	private TagData binning;
	private TagData subarray;
	
	private DetectorSettings detectorSett; 
	
	private void initTagList()
	{
		tagList=new ArrayList<TagData>();
		tagList.add(gain);
		tagList.add(voltage);
		tagList.add(offset);
		tagList.add(confocalZoom);
		tagList.add(binning);
		tagList.add(subarray);
		
	}
	
	public boolean userInput()
	{
		boolean result=false;
		if(tagList!=null){
			for(int i=0; i<tagList.size();i++){
				boolean val=tagList.get(i)!=null ? tagList.get(i).valueChanged() : false;
				result= result || val;
			}
		}
		return result;
	}
	
	
	public DetectorSettingsCompUI(ModuleConfiguration objConf) 
	{
		initGUI();
		if(objConf==null)
			createDummyPane(false);
		else
			createDummyPane(objConf.getSettingList(),false);
	}


	public boolean addData(DetectorSettings sett, boolean overwrite)
	{
		boolean conflicts=false;
		if(overwrite){
			replaceData(sett);
			LOGGER.info("[DATA] -- replace DETECTOR_SETTINGS data");
		}else
			try {
				completeData(sett);
				LOGGER.info("[DATA] -- complete DETECTOR_SETTINGS data");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		setGUIData();
		return conflicts;
	}
	
	private void replaceData(DetectorSettings d)
	{
		if(d!=null){
			detectorSett=d;
		}
	}
	
	private void completeData(DetectorSettings d) throws Exception
	{
		//copy gui input fields
		DetectorSettings copyIn=null;
		if(detectorSett!=null){
			getData();
			copyIn=new DetectorSettings(detectorSett);
		}
		replaceData(d);

		// set input field values again
		if(copyIn!=null){
			Double g=copyIn.getGain();
			ElectricPotential v=copyIn.getVoltage();
			Double o=copyIn.getOffset();
			Double z=copyIn.getZoom();
			Binning b=copyIn.getBinning();

			if(g!=null) detectorSett.setGain(g);
			if(v!=null) detectorSett.setVoltage(v);
			if(o!=null) detectorSett.setOffset(o);
			if(z!=null) detectorSett.setZoom(z);
			if(b!=null) detectorSett.setBinning(b);
		}
		
	}
	
	
	private void setGUIData() 
	{
		if(detectorSett!=null){
			try{setGain(detectorSett.getGain(), ElementsCompUI.REQUIRED);
			} catch (NullPointerException e) { }
			try{setVoltage(detectorSett.getVoltage(), ElementsCompUI.REQUIRED);
			} catch (NullPointerException e) { }
			try{ setOffset(detectorSett.getOffset(), ElementsCompUI.REQUIRED);
			} catch (NullPointerException e) { }
			try{ setConfocalZoom(detectorSett.getZoom(), ElementsCompUI.REQUIRED);
			} catch (NullPointerException e) { }
			try{ setBinning(detectorSett.getBinning(), ElementsCompUI.REQUIRED);
			} catch (NullPointerException e) { }
			//TODO
			try{ setSubarray(null, ElementsCompUI.REQUIRED);
			} catch (NullPointerException e) { }
		}
		
	}

	private void initGUI()
	{
		buildComp=false;
		labels= new ArrayList<JLabel>();
		comp = new ArrayList<JComponent>();
		
		gridbag = new GridBagLayout();
		c = new GridBagConstraints();
		setLayout(gridbag);
	}
	
	private void readGUIInput() throws Exception
	{
		if(detectorSett==null){
			createNewElement();
		}
		//TODO input checker
		try{
			detectorSett.setGain(parseToDouble(gain.getTagValue()));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read DETECTOR SETT  gain input");
		}
		try{
			detectorSett.setVoltage(parseElectricPotential(voltage.getTagValue(), voltage.getTagUnit()));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read DETECTOR SETT voltage input");
		}
		try{
			detectorSett.setOffset(parseToDouble(offset.getTagValue()));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read DETECTOR SETT offset input");
		}


		try{
		detectorSett.setZoom(parseToDouble(confocalZoom.getTagValue()));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read DETECTOR SETT zoom input");
		}
		try{
		detectorSett.setBinning(parseBinning(binning.getTagValue()));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read DETECTOR SETT binning input");
		}
		//TODO set subarray
//		detectorSett.setSubarray
	}

	private ElectricPotential parseElectricPotential(String c, Unit unit) 
	{
		if(c==null || c.equals(""))
			return null;
		
		ElectricPotential p=null;

		return new ElectricPotential(Double.valueOf(c), unit);
	}
	private Binning parseBinning(String c) throws EnumerationException
	{
		if(c==null || c.equals(""))
			return null;
		
		return Binning.fromString(c);
	}
	
	private void createNewElement() {
		detectorSett=new DetectorSettings();		
	}

	public DetectorSettings getData() throws Exception
	{
		if(userInput())
			readGUIInput();
		return detectorSett;
	}
	
	@Override
	public void buildComponents() 
	{
		labels.clear();
		comp.clear();
		addLabelToGUI(new JLabel("Settings:"));
		addTagToGUI(gain);
		addTagToGUI(voltage);
		addTagToGUI(offset);
		addTagToGUI(confocalZoom);
		addTagToGUI(binning);
		addTagToGUI(subarray);
		if(subarray!=null) subarray.setEnable(false);
		addLabelTextRows(labels, comp, gridbag, this);

		c.gridwidth = GridBagConstraints.REMAINDER; //last
		c.anchor = GridBagConstraints.WEST;
		c.weightx = 1.0;
		
		buildComp=true;
		initTagList();

	}

	
	public void createDummyPane(boolean inactive)
	{
		setGain(null,OPTIONAL);
		setVoltage(null, OPTIONAL);
		setOffset(null,OPTIONAL);
		setConfocalZoom(null, OPTIONAL);
		setBinning(null, OPTIONAL);
		setSubarray(null, OPTIONAL);
		
		if(inactive){
			gain.setEnable(false);
			voltage.setEnable(false);
			offset.setEnable(false);
			confocalZoom.setEnable(false);
			binning.setEnable(false);
			subarray.setEnable(false);
		}
	}
	
	public void createDummyPane(List<TagConfiguration> list,boolean inactive) 
	{
		if(list==null)
			createDummyPane(inactive);
		else{
		clearDataValues();
//		if(detectorSett==null && list!=null && list.size()>0)
//			createNewElement();
		for(int i=0; i<list.size();i++){
			TagConfiguration t=list.get(i);
			String name=t.getName();
			String val=t.getValue();
			boolean prop=t.getProperty();
			if(name!=null){
				setTag(name,val,prop,t.getUnit());
			}
		}
		}
	}

	@Override
	public void clearDataValues() 
	{
		clearTagValue(gain);
		clearTagValue(voltage);
		clearTagValue(offset);
		clearTagValue(confocalZoom);
		clearTagValue(binning);
		clearTagValue(subarray);
		
	}
	
	
	
	public void setGain(Double value, boolean prop)
	{
		String val= (value != null) ? String.valueOf(value):"";
		if(gain == null) 
			gain = new TagData(TagNames.GAIN,val,prop,TagData.TEXTFIELD);
		else 
			gain.setTagValue(val,prop);
	}
	//TODO
		public void setBinning(Binning value, boolean prop)
		{
			String val= (value != null)? value.getValue() : "";
			if(binning == null) 
				binning = new TagData(TagNames.BINNING,val,prop,TagData.COMBOBOX,getNames(Binning.class));
			else 
				binning.setTagValue(val,prop);
		}
		public void setVoltage(ElectricPotential value, boolean prop)
		{
			String val= (value != null)? String.valueOf(value.value()) : "";
			Unit unit=(value!=null) ? value.unit() :TagNames.VOLTAGE_UNIT;
			if(voltage == null) 
				voltage = new TagData(TagNames.VOLTAGE,val,unit,prop,TagData.TEXTFIELD);
			else 
				voltage.setTagValue(val,unit,prop);
		}
		public void setOffset(Double value, boolean prop)
		{
			String val= (value != null) ? String.valueOf(value):"";
			if(offset == null) 
				offset = new TagData(TagNames.OFFSET,val,prop,TagData.TEXTFIELD);
			else 
				offset.setTagValue(val,prop);
		}
		public void setConfocalZoom(Double value, boolean prop)
		{
			String val= (value != null) ? String.valueOf(value):"";
			if(confocalZoom == null) 
				confocalZoom = new TagData(TagNames.CONFZOOM,val,prop,TagData.TEXTFIELD);
			else 
				confocalZoom.setTagValue(val,prop);
		}
		public void setSubarray(String value, boolean prop)
		{
			if(subarray == null) 
				subarray = new TagData(TagNames.SUBARRAY,value,prop,TagData.TEXTFIELD);
			else 
				subarray.setTagValue(value,prop);
		}
		
		public String getGain()
		{
			return gain.getTagValue();
		}
		
		public String getBinning()
		{
			return binning.getTagValue();
		}


		@Override
		public List<TagData> getActiveTags() {
//			if(isActive(gain)) list.add(gain);
//			if(isActive(offset)) list.add(offset);
//			if(isActive(voltage)) list.add(voltage);
			return null;
		}

		/**
		 * Update tags with val from list
		 */
		public void update(List<TagData> list) 
		{
			for(TagData t: list){
				if(t.valueChanged()){
					setTag(t);
				}
			}
		}

		private void setTag(TagData t)
		{
			setTag(t.getTagName(),t.getTagValue(),t.getTagProp(),t.getTagUnit());
		}
		
		private void setTag(String name,String val,boolean prop,Unit unit)
		{
			switch (name) {
			case TagNames.GAIN:
				try{
					setGain(parseToDouble(val), prop);
				}
				catch(Exception e){
					setGain(null,OPTIONAL);
				}
				gain.setVisible(true);
				break;
			case TagNames.VOLTAGE:
				try{
						setVoltage(parseElectricPotential(val,unit), prop);
				}
				catch(Exception e){
					setVoltage(null, OPTIONAL);
				}
				voltage.setVisible(true);
				break;
			case TagNames.OFFSET:
				try{
					setOffset(Double.valueOf(val), prop);
				}
				catch(Exception e){
					setOffset(null, OPTIONAL);
				}
				offset.setVisible(true);
				break;
			case TagNames.CONFZOOM:
				try{
					if(val!=null){
					Double value=parseToDouble(val);
				setConfocalZoom(value, prop);
					}else{
						setConfocalZoom(null, prop);
					}
//				detectorSett.setZoom(value);
				}catch(Exception e){
					setConfocalZoom(null, prop);
				}
				confocalZoom.setVisible(true);
				break;
			case TagNames.BINNING:
				try{
					if(val!=null){
					Binning value=parseBinning(val);
					setBinning(value, prop);
					}else{
						setBinning(null, prop);
					}
//					detectorSett.setBinning(value);
				}catch(Exception e){
					setBinning(null, prop);
				}
				binning.setVisible(true);
				break;
			case TagNames.SUBARRAY:
				//TODO
				setSubarray(null, prop);
				subarray.setVisible(true);
				break;
			default:
				LOGGER.warn("[CONF] DETECTOR SETT unknown tag: "+name );break;
			}
		}



}
