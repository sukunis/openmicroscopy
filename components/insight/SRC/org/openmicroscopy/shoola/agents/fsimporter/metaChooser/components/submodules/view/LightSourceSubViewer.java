package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ome.units.quantity.Frequency;
import ome.units.quantity.Length;
import ome.units.quantity.Power;
import ome.units.unit.Unit;
import ome.xml.model.LightSource;
import ome.xml.model.MapPair;
import ome.xml.model.enums.ArcType;
import ome.xml.model.enums.Enumeration;
import ome.xml.model.enums.FilamentType;
import ome.xml.model.enums.LaserMedium;
import ome.xml.model.enums.LaserType;
import ome.xml.model.enums.Pulse;
import ome.xml.model.primitives.PositiveInteger;

import org.apache.commons.lang.BooleanUtils;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules.LightSourceCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model.LightSourceModel;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.ModuleConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.TagNames;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagData;
import org.slf4j.LoggerFactory;

public abstract class LightSourceSubViewer extends ModuleViewer 
{
	protected static final org.slf4j.Logger LOGGER =
			LoggerFactory.getLogger(LightSourceSubViewer.class);
	protected TagData manufact;
	protected TagData power;
	protected TagData model;
	protected TagData type;
	
	protected TagData description;//lightEmittingDiode
	protected TagData map;//genericExcitationSource
	
	//laser
	protected TagData medium;
	protected TagData freqMul;
	protected TagData tunable;
	protected TagData pulse;
	protected TagData pockelCell;
	protected TagData repRate;
	protected TagData pump;
	protected TagData waveLength;
	

	protected String classification;
	protected LightSourceModel data;
	
	abstract protected void initTagList();
	abstract protected void setGUIData();
	abstract protected void addTags(List<JLabel> labels, List<JComponent> comp);
	
	protected void initComponents(ModuleConfiguration conf)
	{
		setLayout(new BorderLayout(0,0));
		
		gridbag = new GridBagLayout();
		
		c = new GridBagConstraints();
		globalPane=new JPanel();
		
		globalPane.setAlignmentX(Component.LEFT_ALIGNMENT);
		globalPane.setAlignmentY(Component.TOP_ALIGNMENT);
		
		globalPane.setLayout(gridbag);
		
		add(globalPane,BorderLayout.NORTH);
		setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		
		// init tag layout
		List<TagConfiguration> list=conf.getTagList();
		initTags(list);
		
	}
	
	public void buildGUI() {
		List<JLabel> labels= new ArrayList<JLabel>();
		List<JComponent> comp=new ArrayList<JComponent>();
		addTags(labels,comp);
		
		addLabelTextRows(labels, comp, gridbag, globalPane);
		
		c.gridwidth = GridBagConstraints.REMAINDER; //last
		c.anchor = GridBagConstraints.WEST;
		c.weightx = 1.0;
		initTagList();
	}
	
	public void setManufact(String value, boolean prop)
	{
		if(manufact == null) 
			manufact = new TagData(TagNames.MANUFAC,value,prop,TagData.TEXTFIELD);
		else 
			manufact.setTagValue(value,prop);
	}
	public void setType(Enumeration value,boolean prop)
	{
		String val;
		System.out.println("\n ...classification lightSrc: "+classification);
		
		switch(classification){
		case "Laser":
			val= (value != null)? ((LaserType) value).getValue() : "";
			if(type == null) 
				type = new TagData(TagNames.TYPE,val,prop,TagData.COMBOBOX,getNames(LaserType.class));
			else 
				type.setTagValue(val,prop);
			break;
		case "Arc":
			val= (value != null)? ((ArcType) value).getValue() : "";
			if(type == null) 
				type = new TagData(TagNames.TYPE,val,prop,TagData.COMBOBOX,getNames(ArcType.class));
			else 
				type.setTagValue(val,prop);
			break;
		case "Filament":
			val= (value != null)? ((FilamentType) value).getValue() : "";
			if(type == null) 
				type = new TagData(TagNames.TYPE,val,prop,TagData.COMBOBOX,getNames(FilamentType.class));
			else 
				type.setTagValue(val,prop);
			break;
			default:
				val= (value != null)? ((LaserType) value).getValue() : "";
				if(type == null) 
					type = new TagData(TagNames.TYPE,val,prop,TagData.COMBOBOX,getNames(LaserType.class));
				else 
					type.setTagValue(val,prop);
				break;
		}
		
	}
	
	public void setPower(Power value, boolean prop)
	{
		String val= (value != null)? String.valueOf(value.value()) : "";
		Unit unit=(value!=null) ? value.unit() :TagNames.POWER_UNIT;
		if(power == null) 
//			power = new TagData(TagNames.POWER+" ["+powerUnit.getSymbol()+"]: ",val,prop,TagData.TEXTFIELD);
			power = new TagData(TagNames.POWER,val,unit,prop,TagData.TEXTFIELD);
		else 
			power.setTagValue(val,unit,prop);
	}
	public void setModel(String value, boolean prop)
	{
		if(model == null) 
			model = new TagData(TagNames.MODEL,value,prop,TagData.TEXTFIELD);
		else 
			model.setTagValue(value,prop);
	}
	
	
	
	
	public void setMedium(LaserMedium value, boolean prop)
	{
		String val= (value != null)? value.getValue() : "";
		if(medium == null) 
			medium = new TagData(TagNames.MEDIUM,val,prop,TagData.COMBOBOX,getNames(LaserMedium.class));
		else 
			medium.setTagValue(val,prop);
	}
	public void setFreqMultiplication(PositiveInteger value, boolean prop)
	{
		String val= (value != null)? String.valueOf(value.getNumberValue()) : "";
		if(freqMul == null) 
			freqMul = new TagData(TagNames.FREQMUL,val,prop,TagData.TEXTFIELD);
		else 
			freqMul.setTagValue(val,prop);
	}
	
	
	public void setTunable(Boolean value, boolean prop)
	{
		String val=(value!=null) ? String.valueOf(value): "";
		if(tunable == null) 
			tunable = new TagData(TagNames.TUNABLE,val,prop,TagData.COMBOBOX,TagNames.BOOLEAN_COMBO);
		else 
			tunable.setTagValue(val,prop);
	}
	
	public void setTunable(String value, boolean prop)
	{
		String val=(value!=null) ? value: "";
		if(tunable == null) 
			tunable = new TagData(TagNames.TUNABLE,val,prop,TagData.COMBOBOX,TagNames.BOOLEAN_COMBO);
		else 
			tunable.setTagValue(val,prop);
	}
	
	public void setPulse(Pulse value, boolean prop)
	{
		String val= (value != null)? value.getValue() : "";
		if(pulse == null) 
			pulse = new TagData(TagNames.PULSE,val,prop,TagData.COMBOBOX,getNames(Pulse.class));
		else 
			pulse.setTagValue(val,prop);
	}

	public void setPocketCell(Boolean value, boolean prop)
	{
		String val=(value!=null) ? String.valueOf(value): "";
		if(pockelCell == null) 
			pockelCell = new TagData(TagNames.POCKELCELL,val,prop,TagData.COMBOBOX,TagNames.BOOLEAN_COMBO);
		else 
			pockelCell.setTagValue(val,prop);
	}
	public void setRepititationRate(Frequency value, boolean prop)
	{
		String val=(value!=null) ? String.valueOf(value.value()) :"";
		Unit unit=(value!=null) ? value.unit():TagNames.REPRATE_UNIT_HZ;
		if(repRate == null) {
			repRate = new TagData(TagNames.REPRATE,val,unit,prop,TagData.TEXTFIELD);
		}else {
			repRate.setTagValue(val,unit,prop);
		}
			
	}
	
	public void setPump(String value, boolean prop)
	{
		if(pump == null) 
			pump = new TagData(TagNames.PUMP,value,prop,TagData.TEXTFIELD);
		else 
			pump.setTagValue(value,prop);
	}
	public void setWavelength(Length value, boolean prop)
	{
		String val=(value!=null) ? String.valueOf(value.value()) :"";
		Unit unit=(value!=null) ? value.unit() :TagNames.WAVELENGTH_UNIT;
		if(waveLength == null) 
			waveLength = new TagData(TagNames.WAVELENGTH,val,unit,prop,TagData.TEXTFIELD);
		else 
			waveLength.setTagValue(val,unit,prop);
	}
	
	
	
	
	public void setDescription(String value, boolean prop)
	{
		if(description == null) 
			description = new TagData(TagNames.DESC,value,prop,TagData.TEXTAREA);
		else 
			description.setTagValue(value,prop);
	}
	
	//TODO
	public void setMap(List<MapPair> value, boolean prop)
	{
		String val="";
		if(map == null) 
			map = new TagData(TagNames.MAP,val,prop,TagData.TEXTFIELD);
		else 
			map.setTagValue(val,prop);
	}
	
	protected void initTag(TagConfiguration t)
	{
		String name=t.getName();
		Boolean prop=t.getProperty();
		switch(name){
		case TagNames.MODEL:
			setModel(null, prop);
			model.setVisible(true);
			break;
		case TagNames.MANUFAC:
			setManufact(null, prop);
			manufact.setVisible(true);
			break;
		case TagNames.TYPE:
		case TagNames.A_TYPE:
		case TagNames.L_TYPE:
		case TagNames.F_TYPE:
			setType(null,prop);
			type.setVisible(true);
			break;
		case TagNames.POWER:
					setPower(null, prop);
			power.setVisible(true);
			break;
		case TagNames.MEDIUM:
				setMedium(null, prop);
			medium.setVisible(true);
			break;
		case TagNames.FREQMUL:
				setFreqMultiplication(null, prop);
			freqMul.setVisible(true);
			break;
		case TagNames.TUNABLE:
				setTunable((String)null, prop);
			tunable.setVisible(true);
			break;
		case TagNames.PULSE:
				setPulse(null,prop);
			pulse.setVisible(true);
			break;
		case TagNames.POCKELCELL:
				setPocketCell(null, prop);
			pockelCell.setVisible(true);
			break;
		case TagNames.REPRATE:
				setRepititationRate(null, prop);
			repRate.setVisible(true);
			break;
		case TagNames.PUMP:
				setPump(null, prop);
			pump.setVisible(true);
			break;
		case TagNames.WAVELENGTH:
				setWavelength(null, prop);
			waveLength.setVisible(true);
			break;
		case TagNames.MAP:
				setMap(null, prop);
			map.setVisible(true);
			break;
		case TagNames.DESC:
			setDescription(null, prop);
//			TODO: ((LightEmittingDiode)lightSrc).set
			description.setVisible(true);
			break;
		default:
			System.out.println("[CONF] unknown tag: "+name );break;
		}
	}
}
