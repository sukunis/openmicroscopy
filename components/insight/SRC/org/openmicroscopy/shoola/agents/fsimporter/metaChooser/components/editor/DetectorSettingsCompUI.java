package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.ElementsCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.TagData;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.ModuleConfiguration;

import loci.formats.MetadataTools;
import loci.formats.meta.IMetadata;
import ome.units.UNITS;
import ome.units.quantity.ElectricPotential;
import ome.units.unit.Unit;
import ome.xml.model.DetectorSettings;
import ome.xml.model.ObjectiveSettings;
import ome.xml.model.enums.Binning;
import ome.xml.model.enums.EnumerationException;
import omero.model.enums.DetectorTypeUnknown;


public class DetectorSettingsCompUI extends ElementsCompUI
{
	private final String L_GAIN="Set Gain";
	private final String L_VOLTAGE="Set Voltage";
	private final String L_OFFSET="Set Offset";
	private final String L_CONFZOOM="Confocal Zoom";
	private final String L_BINNING="Binning";
	private final String L_SUBARRAY="Subarray";
	
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
	
	private List<TagData> tagList;
	
	private Unit<ElectricPotential> voltageUnit;
	
	private TitledBorder tb;
	
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
	
	public DetectorSettingsCompUI(DetectorSettings _detectorSett, String id)
	{
		detectorSett=_detectorSett;
		
		voltageUnit=UNITS.V;
		 
		initGUI();
		
		if(detectorSett!=null)
			setGUIData();
		else{//TODO
//			if(id==null)
//				id=MetadataTools.createLSID("Detector", 0,0);
			detectorSett=new DetectorSettings();
			detectorSett.setID("");
			createDummyPane(false);
		}
	}
	
//	public DetectorSettingsCompUI() 
//	{
//		voltageUnit=UNITS.V;
//		initGUI();
//		createDummyPane(false);
//	}
	
	public DetectorSettingsCompUI(ModuleConfiguration objConf) 
	{
		voltageUnit=UNITS.V;
		initGUI();
		if(objConf==null)
			createDummyPane(false);
		else
			createDummyPane(objConf.getSettingList(),false);
	}

	public boolean addData(DetectorSettings ds,boolean overwrite) 
	{
		boolean conflicts=false;
		if(detectorSett!=null){
			if(ds!=null){
				Double g=ds.getGain();
				ElectricPotential v=ds.getVoltage();
				Double o=ds.getOffset();
				Double z=ds.getZoom();
				Binning b=ds.getBinning();
				if(overwrite){
					if(ds.getID()!=null && !ds.getID().equals(""))
						detectorSett.setID(ds.getID());
					if(g!=null) detectorSett.setGain(g);
					if(v!=null) detectorSett.setVoltage(v);
					if(o!=null) detectorSett.setOffset(o);
					if(z!=null) detectorSett.setZoom(z);
					if(b!=null) detectorSett.setBinning(b);
					LOGGER.info("[DATA] overwrite DETECTOR_SETTINGS data");
				}else{
					if(detectorSett.getID()==null || detectorSett.getID().equals(""))
						detectorSett.setID(ds.getID());
					if(detectorSett.getGain()==null)
						detectorSett.setGain(g);
					if(detectorSett.getVoltage()==null)
						detectorSett.setVoltage(v);
					if(detectorSett.getOffset()==null)
						 detectorSett.setOffset(o);
					if(detectorSett.getZoom()==null)
						detectorSett.setZoom(z);
					if(detectorSett.getBinning()==null)
						detectorSett.setBinning(b);
					LOGGER.info("[DATA] complete DETECTOR_SETTINGS data");
				}
			}
			
		}else if(ds!=null){
			detectorSett=ds;
			LOGGER.info("[DATA] add DETECTOR_SETTINGS data");
			
		}
		setGUIData();
		return conflicts;
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
		tb=new TitledBorder("");
//		setBorder(BorderFactory.createCompoundBorder(tb,
//						BorderFactory.createEmptyBorder(5,0,5,0)));
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
			LOGGER.severe("[DATA] can't read DETECTOR SETT gain input");
		}
		try{
		detectorSett.setVoltage(voltage.getTagValue().equals("") ? 
				null : new ElectricPotential(Double.valueOf(voltage.getTagValue()), voltageUnit) );
		}catch(Exception e){
			LOGGER.severe("[DATA] can't read DETECTOR SETT voltage input");
		}
		try{
		detectorSett.setOffset(parseToDouble(offset.getTagValue()));
		}catch(Exception e){
			LOGGER.severe("[DATA] can't read DETECTOR SETT offset input");
		}
		try{
		detectorSett.setZoom(parseToDouble(confocalZoom.getTagValue()));
		}catch(Exception e){
			LOGGER.severe("[DATA] can't read DETECTOR SETT zoom input");
		}
		try{
		detectorSett.setBinning(parseBinning(binning.getTagValue()));
		}catch(Exception e){
			LOGGER.severe("[DATA] can't read DETECTOR SETT binning input");
		}
		//TODO set subarray
//		detectorSett.setSubarray
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
	

	public void setTitledBorder(String s)
	{
		if(s== null || s.equals(null)) return;
		tb.setTitle(s);
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

	@Override
	public void buildExtendedComponents() {
		// TODO Auto-generated method stub
		
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
		if(detectorSett==null && list!=null && list.size()>0)
			createNewElement();
		for(int i=0; i<list.size();i++){
			TagConfiguration t=list.get(i);
			String name=t.getName();
			String val=t.getValue();
			boolean prop=t.getProperty()!= null ? Boolean.parseBoolean(t.getProperty()):
				OPTIONAL;
			if(name!=null){
				switch (name) {
				case L_GAIN:
					try{
						Double value=parseToDouble(val);
						setGain(value, prop);
						detectorSett.setGain(value);
					}
					catch(Exception e){
						setGain(null,OPTIONAL);
					}
					gain.setVisible(true);
					break;
				case L_VOLTAGE:
					try{
						ElectricPotential value=new ElectricPotential(Double.valueOf(val), voltageUnit);
						detectorSett.setVoltage(value);
					}
					catch(Exception e){
						setVoltage(null, OPTIONAL);
					}
					voltage.setVisible(true);
					break;
				case L_OFFSET:
					try{
						setOffset(Double.valueOf(val), prop);
						detectorSett.setOffset(Double.valueOf(val));
					}
					catch(Exception e){
						setOffset(null, OPTIONAL);
					}
					offset.setVisible(true);
					break;
				case L_CONFZOOM:
					try{
						Double value=parseToDouble(val);
					setConfocalZoom(value, prop);
					detectorSett.setZoom(value);
					}catch(Exception e){
						setConfocalZoom(null, prop);
					}
					confocalZoom.setVisible(true);
					break;
				case L_BINNING:
					try{
						Binning value=parseBinning(val);
						setBinning(value, prop);
						detectorSett.setBinning(value);
					}catch(Exception e){
						setBinning(null, prop);
					}
					binning.setVisible(true);
					break;
				case L_SUBARRAY:
					//TODO
					setSubarray(null, prop);
					subarray.setVisible(true);
					break;
				default:
					LOGGER.warning("[CONF] DETECTOR SETT unknown tag: "+name );break;
				}
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
			gain = new TagData("Gain: ",val,prop,TagData.TEXTFIELD);
		else 
			gain.setTagValue(val,prop);
	}
	//TODO
		public void setBinning(Binning value, boolean prop)
		{
			String val= (value != null)? value.getValue() : "";
			if(binning == null) 
				binning = new TagData("Binning: ",val,prop,TagData.COMBOBOX,getNames(Binning.class));
			else 
				binning.setTagValue(val,prop);
		}
		public void setVoltage(ElectricPotential value, boolean prop)
		{
			String val= (value != null)? String.valueOf(value.value()) : "";
			voltageUnit=(value!=null) ? value.unit() :voltageUnit;
			if(voltage == null) 
				voltage = new TagData("Voltage ["+voltageUnit.getSymbol()+"]: ",val,prop,TagData.TEXTFIELD);
			else 
				voltage.setTagValue(val,prop);
		}
		public void setOffset(Double value, boolean prop)
		{
			String val= (value != null) ? String.valueOf(value):"";
			if(offset == null) 
				offset = new TagData("Offset: ",val,prop,TagData.TEXTFIELD);
			else 
				offset.setTagValue(val,prop);
		}
		public void setConfocalZoom(Double value, boolean prop)
		{
			String val= (value != null) ? String.valueOf(value):"";
			if(confocalZoom == null) 
				confocalZoom = new TagData("Confocal Zoom: ",val,prop,TagData.TEXTFIELD);
			else 
				confocalZoom.setTagValue(val,prop);
		}
		public void setSubarray(String value, boolean prop)
		{
			if(subarray == null) 
				subarray = new TagData("Subarray: ",value,prop,TagData.TEXTFIELD);
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
			// TODO Auto-generated method stub
			return null;
		}

		



}
