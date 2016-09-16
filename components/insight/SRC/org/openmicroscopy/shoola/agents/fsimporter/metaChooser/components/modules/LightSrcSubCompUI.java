package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules;

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
import ome.xml.model.Arc;
import ome.xml.model.Filament;
import ome.xml.model.GenericExcitationSource;
import ome.xml.model.Laser;
import ome.xml.model.LightEmittingDiode;
import ome.xml.model.LightSource;
import ome.xml.model.LightSourceSettings;
import ome.xml.model.MapPair;
import ome.xml.model.enums.ArcType;
import ome.xml.model.enums.EnumerationException;
import ome.xml.model.enums.FilamentType;
import ome.xml.model.enums.LaserMedium;
import ome.xml.model.enums.LaserType;
import ome.xml.model.enums.Pulse;
import ome.xml.model.primitives.PositiveInteger;

import org.apache.commons.lang.BooleanUtils;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.TagNames;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagData;

public abstract class LightSrcSubCompUI extends ElementsCompUI 
{
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
	protected LightSource lightSrc;


	abstract protected void setGUIData();
	abstract protected void initTagList();
	abstract protected void readGUIInput() throws Exception;
	abstract protected void completeData(LightSource lSrc) throws Exception;
	abstract protected void addTags();
	protected abstract void createNewElement(); 

	protected void replaceData(LightSource l)
	{
		if(l!=null){
			lightSrc=l;
		}
	}
	
	protected void initGUI()
	{
		setLayout(new BorderLayout(0,0));
		buildComp=false;
		labels= new ArrayList<JLabel>();
		comp = new ArrayList<JComponent>();
		
		gridbag = new GridBagLayout();
		
		c = new GridBagConstraints();
		globalPane=new JPanel();
		
		globalPane.setAlignmentX(Component.LEFT_ALIGNMENT);
		globalPane.setAlignmentY(Component.TOP_ALIGNMENT);
		
		globalPane.setLayout(gridbag);
		
		add(globalPane,BorderLayout.NORTH);
		setBorder(
//				BorderFactory.createCompoundBorder(	new MyTitledBorder("Objective"),
						BorderFactory.createEmptyBorder(10,10,10,10));
		
	}
	
	protected void createDummyPane(List<TagConfiguration> list,boolean inactive) 
	{
		if(list==null)
			createDummyPane(inactive);
		else{
			clearDataValues();
//			if(lightSrc==null && list!=null && list.size()>0)
//				createNewElement();
			for(int i=0; i<list.size();i++){
				TagConfiguration t=list.get(i);
				String name=t.getName();
				String val=t.getValue();
				boolean prop=t.getProperty();
				if(name!=null && t.isVisible()){
					setTag(t);
				}
			}
		}
	}
	
	public boolean addData(LightSource lSrc, boolean overwrite)
	{
		boolean conflicts=false;
		
		//TODO
		if(lightSrc!=null && !lSrc.getClass().equals(lightSrc.getClass())){
			LOGGER.info("[DEBUG] different lightSrc types "+lightSrc.getClass().getSimpleName()+" - "+
					lSrc.getClass().getSimpleName());
			if(!overwrite){
				LOGGER.warn("[DATA] add LIGHTSOURCE data: different lightSrc types - do nothing");
				return conflicts;
			}
		}
		
		if(overwrite){
			replaceData(lSrc);
			LOGGER.info("[DATA] -- replace LIGHTSOURCE data");
		}else
			try {
				completeData(lSrc);
				LOGGER.info("[DATA] -- complete LIGHTSOURCE data");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		setGUIData();
		return conflicts;
	}

	
	public void buildComponents() {
		labels.clear();
		comp.clear();
		addTags();
		
		addLabelTextRows(labels, comp, gridbag, globalPane);
		
		c.gridwidth = GridBagConstraints.REMAINDER; //last
		c.anchor = GridBagConstraints.WEST;
		c.weightx = 1.0;
		
		
		buildComp=true;	
		initTagList();
		setFields=false;
		
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
	
	public LightSource getData() throws Exception
	{
		if(userInput()) 
			readGUIInput();
		return lightSrc;
	}
	
	private void setTag(TagData t)
	{
		setTag(t.getTagName(),t.getTagValue(),t.getTagProp(),t.getTagUnit());
	} 
	
	private void setTag(TagConfiguration t)
	{
		setTag(t.getName(),t.getValue(),t.getProperty(),t.getUnit());
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
		return (result ||  setFields);
	}
	
	
	public void clearDataValues()
	{
		clearTagValue(manufact);
		clearTagValue(type);
		clearTagValue(power);
		clearTagValue(model);
		
		clearTagValue(description);
		clearTagValue(map);
		
		clearTagValue(medium);
		clearTagValue(freqMul);
		clearTagValue(tunable);
		clearTagValue(pulse);
		clearTagValue(pockelCell);
		clearTagValue(repRate);
		clearTagValue(pump);
		clearTagValue(waveLength);
		
	}
	
	public void setManufact(String value, boolean prop)
	{
		if(manufact == null) 
			manufact = new TagData(TagNames.MANUFAC,value,prop,TagData.TEXTFIELD);
		else 
			manufact.setTagValue(value,prop);
	}
	
	public void setType(LaserType value, boolean prop)
	{
		String val= (value != null)? value.getValue() : "";
		if(type == null) 
			type = new TagData(TagNames.TYPE,val,prop,TagData.COMBOBOX,getNames(LaserType.class));
		else 
			type.setTagValue(val,prop);
	}
	public void setType(ArcType value, boolean prop)
	{
		String val= (value != null)? value.getValue() : "";
		if(type == null) 
			type = new TagData(TagNames.TYPE,val,prop,TagData.COMBOBOX,getNames(ArcType.class));
		else 
			type.setTagValue(val,prop);
	}
	public void setType(FilamentType value, boolean prop)
	{
		String val= (value != null)? value.getValue() : "";
		if(type == null) 
			type = new TagData(TagNames.TYPE,val,prop,TagData.COMBOBOX,getNames(FilamentType.class));
		else 
			type.setTagValue(val,prop);
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

	@Override
	public List<TagData> getActiveTags() {
		List<TagData> list = new ArrayList<TagData>();
		if(isActive(model)) list.add(model);
		if(isActive(manufact)) list.add(manufact);
		if(isActive(type)) list.add(type);
		if(isActive(power)) list.add(power);
		if(isActive(description)) list.add(description);
		if(isActive(map)) list.add(map);
		if(isActive(medium)) list.add(medium);
		if(isActive(freqMul)) list.add(freqMul);
		if(isActive(tunable)) list.add(tunable);
		if(isActive(pulse)) list.add(pulse);
		if(isActive(pockelCell)) list.add(pockelCell);
		if(isActive(repRate)) list.add(repRate);
		if(isActive(pump)) list.add(pump);
		if(isActive(waveLength)) list.add(waveLength);
		
		return list;
		
		
	}
	protected void setTag(String name,String val,boolean prop,Unit unit)
	{
		switch (name) {
		case TagNames.MODEL:
			setModel(val, prop);
//			lightSrc.setModel(val);
			model.setVisible(true);
			break;
		case TagNames.MANUFAC:
			setManufact(val, prop);
//			lightSrc.setManufacturer(val);
			manufact.setVisible(true);
			break;
		case TagNames.L_TYPE:
			try{
				if(val!=null){
				LaserType value=LightSourceCompUI.parseLaserType(val);
				setType(value, prop);
				}else{
					setType((LaserType)null,prop);
				}
//				((Laser)lightSrc).setType(value);
			}catch(Exception e){
				setType((LaserType)null,prop);
			}
			type.setVisible(true);
			break;
		case TagNames.A_TYPE:
			try{
				if(val!=null){							
				ArcType value=LightSourceCompUI.parseArcType(val);
				setType(value, prop);
				}else{
					setType((ArcType)null,prop);
				}
//				((Arc)lightSrc).setType(value);
			}catch(Exception e){
				setType((ArcType)null,prop);
			}
			type.setVisible(true);
			break;
		case TagNames.F_TYPE:
			try{
				if(val!=null){
				FilamentType value=LightSourceCompUI.parseFilamentType(val);
				setType(value, prop);
				}else{
					setType((FilamentType)null,prop);
				}
//				((Filament)lightSrc).setType(value);
			}catch(Exception e){
				setType((FilamentType)null,prop);
			}
			type.setVisible(true);
			break;
		case TagNames.POWER:
			try{
				
				if(val!=null){Power value = LightSourceCompUI.parsePower(val, unit);
			setPower(value, prop);
				}else{
					setPower(null, prop);
				}
//			lightSrc.setPower(value);
			}catch(Exception e){
				setPower(null, prop);
			}
			power.setVisible(true);
			break;
		case TagNames.MEDIUM:
			try {
				if(val!=null){
				LaserMedium value=LightSourceCompUI.parseMedium(val);
				setMedium(value,prop);
				}else{
					setMedium(null, prop);
				}
//				((Laser)lightSrc).setLaserMedium(value);
			} catch (Exception e) {
				setMedium(null, prop);
			}
			medium.setVisible(true);
			break;
		case TagNames.FREQMUL:
			try {
				if(val!=null){
				PositiveInteger value=parseToPositiveInt(val);
				setFreqMultiplication(value, prop);
				}else{
					setFreqMultiplication(null, prop);
				}
//				((Laser)lightSrc).setFrequencyMultiplication(value);
			} catch (Exception e) {
				setFreqMultiplication(null, prop);
			}
			freqMul.setVisible(true);
			break;
		case TagNames.TUNABLE:
			try {
				if(val!=null){
				setTunable(val, prop);
				}else{
					setTunable((String)null, prop);
				}
//				((Laser)lightSrc).setTuneable(BooleanUtils.toBoolean(val));
			} catch (Exception e) {
				setTunable((String)null, prop);
			}
			tunable.setVisible(true);
			break;
		case TagNames.PULSE:
			try {
				if(val!=null){
				Pulse value=LightSourceCompUI.parsePulse(val);
				setPulse(value, prop);
				}else{
					setPulse(null,prop);
				}
//				((Laser)lightSrc).setPulse(value);
			} catch (Exception e) {
				setPulse(null,prop);
			}
			pulse.setVisible(true);
			break;
		case TagNames.POCKELCELL:
			try {
				if(val!=null){
				setPocketCell(BooleanUtils.toBoolean(val), prop);
				}else{
					setPocketCell(null, prop);
				}
//				((Laser)lightSrc).setPockelCell(BooleanUtils.toBoolean(val));
			} catch (Exception e) {
				setPocketCell(null, prop);
			}
			pockelCell.setVisible(true);
			break;
		case TagNames.REPRATE:
			try {
				if(val!=null){
				Frequency value=LightSourceCompUI.parseFrequency(val, unit);
				setRepititationRate(value, prop);
				}else{
					setRepititationRate(null, prop);
				}
//				((Laser)lightSrc).setRepetitionRate(value);
			} catch (Exception e) {
				setRepititationRate(null, prop);
			}
			repRate.setVisible(true);
			break;
		case TagNames.PUMP:
			try {
				if(val!=null){
				setPump(val, prop);
				}else{
					setPump(null, prop);
				}
//				TODO: ((Laser)lightSrc).linkPump(o);
			} catch (Exception e) {
				setPump(null, prop);
			}
			pump.setVisible(true);
			break;
		case TagNames.WAVELENGTH:
			try {
				if(val!=null){
				Length value = parseToLength(val, unit);
				setWavelength(value, prop);
				}else{
					setWavelength(null, prop);
				}
//				((Laser)lightSrc).setWavelength(value);
			} catch (Exception e) {
				setWavelength(null, prop);
			}
			waveLength.setVisible(true);
			break;
		case TagNames.MAP:
			try {
				setMap(null, prop);
//				TODO:((GenericExcitationSource)lightSrc).setMap(value);
			} catch (Exception e) {
				setMap(null, prop);
			}
			map.setVisible(true);
			break;
		case TagNames.DESC:
			setDescription(val, prop);
//			TODO: ((LightEmittingDiode)lightSrc).set
			description.setVisible(true);
			break;
		default:
			LOGGER.warn("[CONF] unknown tag: "+name );break;
		}
	}
	
	

	
}
