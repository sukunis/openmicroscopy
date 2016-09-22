package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import ome.units.quantity.Pressure;
import ome.units.quantity.Temperature;
import ome.units.unit.Unit;
import ome.xml.model.ImagingEnvironment;
import ome.xml.model.Objective;
import ome.xml.model.ObjectiveSettings;
import ome.xml.model.primitives.PercentFraction;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules.ElementsCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules.ObjectiveEditor;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model.ObjectiveModel;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.ModuleConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.TagNames;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagData;
import org.slf4j.LoggerFactory;

public class ImageEnvViewer extends ModuleViewer{

	private static final org.slf4j.Logger LOGGER =
    	    LoggerFactory.getLogger(ImageEnvViewer.class);
 
private ImagingEnvironment env;
private Box box;

// available element tags
private TagData temperature;
private TagData airPressure;
private TagData humidity;
private TagData co2Percent;


/**
 * Creates a new instance.
 * @param model Reference to model.
 */
public ImageEnvViewer(ImagingEnvironment model,ModuleConfiguration conf)
{
	this.env=model;
	initComponents(conf);
	initTagList();
	buildGUI();
}

private void initTagList()
{
	tagList=new ArrayList<TagData>();
	tagList.add(temperature);
	tagList.add(airPressure);
	tagList.add(humidity);
	tagList.add(co2Percent);
	
}

/**
 * Builds and lay out GUI.
 */
private void buildGUI() 
{
	List<JLabel> labels= new ArrayList<JLabel>();
	List<JComponent> comp=new ArrayList<JComponent>();
	addTagToGUI(temperature,labels,comp);
	addTagToGUI(airPressure,labels,comp);
	addTagToGUI(humidity,labels,comp);
	addTagToGUI(co2Percent,labels,comp);
	addLabelTextRows(labels, comp, gridbag, globalPane);
	
	c.gridwidth = GridBagConstraints.REMAINDER; //last
	c.anchor = GridBagConstraints.WEST;
	c.weightx = 1.0;
	
	
	
	// set data
	setGUIData();
}

/**
 * Initialize components.
 */
private void initComponents(ModuleConfiguration conf) 
{
	setLayout(new BorderLayout(5,5));

	gridbag = new GridBagLayout();
	c = new GridBagConstraints();

	globalPane=new JPanel();
	globalPane.setLayout(gridbag);

	//		add(new TitledSeparator("Channel", 3, TitledBorder.DEFAULT_POSITION),BorderLayout.NORTH);
	add(globalPane,BorderLayout.NORTH);

	setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

	// init tag layout
	List<TagConfiguration> list=conf.getTagList();
	initTags(list);
}



/**
 * Init given tag and mark it as visible.
 * @param t
 */
protected void initTag(TagConfiguration t) 
{
	String name=t.getName();
	Boolean prop=t.getProperty();
	switch (name) {
	case TagNames.TEMP:
		setTemperature(null, prop);
		temperature.setVisible(true);
		break;
	case TagNames.AIRPRESS:
		setAirPressure(null, prop);
		airPressure.setVisible(true);
		break;
	case TagNames.HUMIDITY:
		setHumidity(null, prop);
		humidity.setVisible(true);
		break;
	case TagNames.CO2:
		setCo2Percent(null, prop);
		co2Percent.setVisible(true);
		break;
	default:
		LOGGER.warn("[CONF] unknown tag: "+name );break;
	}
}

/**
 * Show data of objective
 */
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




/*------------------------------------------------------
 * Set methods data Values
 * -----------------------------------------------------*/

private void setTemperature(Temperature value, boolean prop)
{
	String val=(value!=null) ? String.valueOf(value.value()) :"";
//	temperatureUnit=(value!=null) ? value.unit():temperatureUnit;
	Unit unit=(value!=null)?value.unit() : TagNames.TEMPERATURE_UNIT;
	if(temperature == null) 
		temperature = new TagData(TagNames.TEMP,val,unit,prop,TagData.TEXTFIELD);
	else 
		temperature.setTagValue(val,unit,prop);
}

private void setAirPressure(Pressure value, boolean prop)
{
	String val=(value!=null) ? String.valueOf(value.value()) :"";
//	airPressureUnit=(value!=null) ? value.unit() :airPressureUnit;
	Unit unit= value!=null ? value.unit() : TagNames.PRESSURE_UNIT;
	if(airPressure == null) 
		airPressure = new TagData(TagNames.AIRPRESS,val,unit,prop,TagData.TEXTFIELD);
	else 
		airPressure.setTagValue(val,unit,prop);
}

private void setHumidity(PercentFraction value, boolean prop)
{
	String val=(value!=null) ? String.valueOf(value.getValue()*100) :"";
	Unit unit=TagNames.PERCENT_UNIT;
	if(humidity == null) 
		humidity = new TagData(TagNames.HUMIDITY,val,unit,prop,TagData.TEXTFIELD); 
	else 
		humidity.setTagValue(val,unit,prop);
}

private void setCo2Percent(PercentFraction value, boolean prop)
{
	String val=(value!=null) ? String.valueOf(value.getValue()*100) :"";
	Unit unit=TagNames.PERCENT_UNIT;
	if(co2Percent == null) 
		co2Percent = new TagData(TagNames.CO2,val,unit,prop,TagData.TEXTFIELD);
	else 
		co2Percent.setTagValue(val,unit,prop);
}



@Override
public void saveData() 
{
	if(env==null)
		env=new ImagingEnvironment();
	
	try{
	env.setTemperature(temperature.getTagValue().equals("") ?
			null : new Temperature(Double.valueOf(temperature.getTagValue()), temperature.getTagUnit()));
	}catch(Exception e){
		LOGGER.error("[DATA] can't read IMAGE ENV temperature input");
	}
	try{
	env.setAirPressure(airPressure.getTagValue().equals("") ? 
			null : new Pressure(Double.valueOf(airPressure.getTagValue()),airPressure.getTagUnit()));
	}catch(Exception e){
		LOGGER.error("[DATA] can't read IMAGE ENV air pressure input");
	}
	try{
	//TODO input format hint: percentvalue elem of [0,100] or [0,1]
	env.setHumidity(humidity.getTagValue().equals("")? 
			null : new PercentFraction(Float.valueOf(humidity.getTagValue())/100));
	}catch(Exception e){
		LOGGER.error("[DATA] can't read IMAGE ENV humidity input");
	}
	try{
	env.setCO2Percent(co2Percent.getTagValue().equals("")?
			null : new PercentFraction(Float.valueOf(co2Percent.getTagValue())/100));
	}catch(Exception e){
		LOGGER.error("[DATA] can't read IMAGE ENV co2 percent input");
	}
}

/*--------------------------------------
 * Model
 ---------------------------------------*/


public boolean addData(ImagingEnvironment img, boolean overwrite)
{
	boolean conflicts=false;
	if(overwrite){
		replaceData(img);
		LOGGER.info("[DATA] -- replace IMAGE_ENV data");
	}else
		try {
			completeData(img);
			LOGGER.info("[DATA] -- complete IMAGE_ENV data");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	setGUIData();
	return conflicts;
}

private void replaceData(ImagingEnvironment i)
{
	if(i!=null){
		env=i;
	}
}

private void completeData(ImagingEnvironment i) throws Exception
{
	//copy input fields
	ImagingEnvironment copyIn=null;
	if(env!=null){
		if(hasDataToSave()) saveData();
		copyIn=new ImagingEnvironment(env);
	}

	replaceData(i);

	// set input field values again
	if(copyIn!=null){
		Temperature t=copyIn.getTemperature();
		Pressure p=copyIn.getAirPressure();
		PercentFraction h=copyIn.getHumidity();
		PercentFraction co=copyIn.getCO2Percent();
			
		if(t!=null) env.setTemperature(t);
		if(p!=null) env.setAirPressure(p);
		if(h!=null) env.setHumidity(h);
		if(co!=null) env.setCO2Percent(co);
	}
}

}


