package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.border.TitledBorder;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.ElementsCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.TagData;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.ModuleConfiguration;

import loci.formats.meta.IMetadata;
import ome.units.UNITS;
import ome.units.quantity.Pressure;
import ome.units.quantity.Temperature;
import ome.units.unit.Unit;
import ome.xml.model.ImagingEnvironment;
import ome.xml.model.ObjectiveSettings;
import ome.xml.model.primitives.PercentFraction;

public class ImagingEnvironmentCompUI extends ElementsCompUI 
{
	private final String L_TEMP="Temperature";
	private final String L_AIRPRESS="Air Pressure";
	private final String L_HUMIDITY="Humidity";
	private final String L_CO2="CO2 Percent";
	
	private TagData temperature;
	private TagData airPressure;
	private TagData humidity;
	private TagData co2Percent;
	private List<TagData> tagList;
	
	private Unit<Temperature> temperatureUnit;
	private Unit<Pressure> airPressureUnit;
	
	private ImagingEnvironment env;
	
	private void initTagList()
	{
		tagList=new ArrayList<TagData>();
		tagList.add(temperature);
		tagList.add(airPressure);
		tagList.add(humidity);
		tagList.add(co2Percent);
	}
	
	public boolean userInput()
	{
		boolean result=false;
		if(tagList!=null)
		for(int i=0; i<tagList.size();i++) result= result || tagList.get(i).valueChanged();
		return result;
	}
	
	public ImagingEnvironmentCompUI(ImagingEnvironment _env,int i)
	{
		env=_env;
		temperatureUnit=UNITS.DEGREEC;
		airPressureUnit=UNITS.MBAR;
		initGUI();
		if(env!=null){
			setGUIData();
		}else{
			env=new ImagingEnvironment();
			createDummyPane(false);
		}
	}

	
	public ImagingEnvironmentCompUI(ModuleConfiguration objConf) 
	{
		temperatureUnit=UNITS.DEGREEC;
		airPressureUnit=UNITS.MBAR;
		initGUI();
		if(objConf==null)
			createDummyPane(false);
		else
			createDummyPane(objConf.getList(),false);
	}

	private void initGUI()
	{
		buildComp=false;
		labels= new ArrayList<JLabel>();
		comp = new ArrayList<JComponent>();
		
		gridbag = new GridBagLayout();
		c = new GridBagConstraints();
		setLayout(gridbag);
		TitledBorder tb=new TitledBorder("Imaging Environment");
		setBorder(
//				BorderFactory.createCompoundBorder(	tb,
						BorderFactory.createEmptyBorder(5,5,5,5));
	}
	
	private void setGUIData()
	{
		if(env!=null){
			try {if(temperature!=null) setTemperature(env.getTemperature(), ElementsCompUI.REQUIRED);	} 
			catch (NullPointerException e) {}
			try {if(airPressure!=null) setAirPressure(env.getAirPressure(), ElementsCompUI.REQUIRED);	} 
			catch (NullPointerException e) {}
			try {if(humidity!=null) setHumidity(env.getHumidity(), ElementsCompUI.REQUIRED);	} 
			catch (NullPointerException e) {}
			try {if(co2Percent!=null) setCo2Percent(env.getCO2Percent(), ElementsCompUI.REQUIRED);	} 
			catch (NullPointerException e) {}
		}
	}
	
	private void readGUIInput() throws Exception
	{
		if(env==null)
			createNewElement();
		env.setTemperature(temperature.getTagValue().equals("") ?
				null : new Temperature(Double.valueOf(temperature.getTagValue()), temperatureUnit));
		env.setAirPressure(airPressure.getTagValue().equals("") ? 
				null : new Pressure(Double.valueOf(airPressure.getTagValue()),airPressureUnit));
		//TODO input format hint: percentvalue elem of [0,100] or [0,1]
		env.setHumidity(humidity.getTagValue().equals("")? 
				null : new PercentFraction(Float.valueOf(humidity.getTagValue())/100));
		env.setCO2Percent(co2Percent.getTagValue().equals("")?
				null : new PercentFraction(Float.valueOf(co2Percent.getTagValue())/100));
	}
	
	private void createNewElement() {
		env=new ImagingEnvironment();
	}

	public ImagingEnvironment getData() throws Exception
	{
		if(userInput())
			readGUIInput();
		return env;
	}
	
	@Override
	public void buildComponents() {
		labels.clear();
		comp.clear();
		
		addTagToGUI(temperature);
		addTagToGUI(airPressure);
		addTagToGUI(humidity);
		addTagToGUI(co2Percent);
				
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
	@Override
	public void createDummyPane(boolean inactive) {
		setTemperature(null, OPTIONAL);
		setAirPressure(null, OPTIONAL);
		setHumidity(null, OPTIONAL);
		setCo2Percent(null, OPTIONAL);
	}
	
	public void createDummyPane(List<TagConfiguration> list,boolean inactive) 
	{
		if(list==null)
			createDummyPane(inactive);
		else{
		clearDataValues();
		if(env==null && list!=null && list.size()>0)
			createNewElement();
		for(int i=0; i<list.size();i++){
			TagConfiguration t=list.get(i);
			String name=t.getName();
			String val=t.getValue();
			boolean prop=t.getProperty()!= null ? Boolean.parseBoolean(t.getProperty()):
				OPTIONAL;
			if(name!=null){
				switch (name) {
				case L_TEMP:
					setTemperature(null, prop);
					temperature.setVisible(true);
					break;
				case L_AIRPRESS:
					setAirPressure(null, prop);
					airPressure.setVisible(true);
					break;
				case L_HUMIDITY:
					setHumidity(null, prop);
					humidity.setVisible(true);
					break;
				case L_CO2:
					setCo2Percent(null, prop);
					co2Percent.setVisible(true);
					break;
				default:
					LOGGER.warning("[CONF] unknown tag: "+name );break;
				}
			}
		}
		}
	}
	
	
	@Override
	public void clearDataValues() {
		clearTagValue(temperature);
		clearTagValue(airPressure);
		clearTagValue(humidity);
		clearTagValue(co2Percent);
	}
	
	public void setTemperature(Temperature value, boolean prop)
	{
		String val=(value!=null) ? String.valueOf(value.value()) :"";
		temperatureUnit=(value!=null) ? value.unit():temperatureUnit;
		if(temperature == null) 
			temperature = new TagData("Temperature ["+temperatureUnit.getSymbol()+"]: ",val,prop,TagData.TEXTFIELD);
		else 
			temperature.setTagValue(val,prop);
	}
	
	public void setAirPressure(Pressure value, boolean prop)
	{
		String val=(value!=null) ? String.valueOf(value.value()) :"";
		airPressureUnit=(value!=null) ? value.unit() :airPressureUnit;
		if(airPressure == null) 
			airPressure = new TagData("Air Pressure ["+airPressureUnit.getSymbol()+"]: ",val,prop,TagData.TEXTFIELD);
		else 
			airPressure.setTagValue(val,prop);
	}
	
	public void setHumidity(PercentFraction value, boolean prop)
	{
		String val=(value!=null) ? String.valueOf(value.getValue()*100) :"";
		if(humidity == null) 
			humidity = new TagData("Humidity [%]: ",val,prop,TagData.TEXTFIELD);
		else 
			humidity.setTagValue(val,prop);
	}
	
	public void setCo2Percent(PercentFraction value, boolean prop)
	{
		String val=(value!=null) ? String.valueOf(value.getValue()*100) :"";
		
		if(co2Percent == null) 
			co2Percent = new TagData("CO2 Percent [%]: ",val,prop,TagData.TEXTFIELD);
		else 
			co2Percent.setTagValue(val,prop);
	}


	@Override
	public List<TagData> getActiveTags() {
		List<TagData> list = new ArrayList<TagData>();
		if(isActive(temperature)) list.add(temperature);
		if(isActive(airPressure)) list.add(airPressure);
		if(isActive(humidity)) list.add(humidity);
		if(isActive(co2Percent)) list.add(co2Percent);
		
		return list;
		
		
	}

	public void addData(ImagingEnvironment i,boolean overwrite) 
	{
		if(env!=null){
			Temperature t=i.getTemperature();
			Pressure p=i.getAirPressure();
			PercentFraction h=i.getHumidity();
			PercentFraction co=i.getCO2Percent();
			if(i!=null){
				if(overwrite){
					if(t!=null) env.setTemperature(t);
					if(p!=null) env.setAirPressure(p);
					if(h!=null) env.setHumidity(h);
					if(co!=null) env.setCO2Percent(co);
					LOGGER.info("[DATA] overwrite IMG_ENV data");
				}else{
					if(env.getTemperature()==null)
						env.setTemperature(t);
					if(env.getAirPressure()==null)
						env.setAirPressure(p);
					if(env.getHumidity()==null)
						env.setHumidity(h);
					if(env.getCO2Percent()==null)
						 env.setCO2Percent(co);
					LOGGER.info("[DATA] complete IMG_ENV data");
				}
			}
		}else if(i!=null){
			env=i;
			LOGGER.info("[DATA] add IMG_ENV data");
		}
		
		setGUIData();
	}


	
	
}
