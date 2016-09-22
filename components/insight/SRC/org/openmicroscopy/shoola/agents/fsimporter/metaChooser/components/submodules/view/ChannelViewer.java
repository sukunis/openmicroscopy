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

import ome.units.quantity.Length;
import ome.units.unit.Unit;
import ome.xml.model.Channel;
import ome.xml.model.Objective;
import ome.xml.model.ObjectiveSettings;
import ome.xml.model.enums.AcquisitionMode;
import ome.xml.model.enums.ContrastMethod;
import ome.xml.model.enums.EnumerationException;
import ome.xml.model.enums.IlluminationType;
import ome.xml.model.primitives.Color;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules.ElementsCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules.ObjectiveEditor;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model.ObjectiveModel;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.ModuleConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.TagNames;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagData;
import org.slf4j.LoggerFactory;

public class ChannelViewer extends ModuleViewer{

	private static final org.slf4j.Logger LOGGER =
    	    LoggerFactory.getLogger(ChannelViewer.class);
 
private Channel channel;

// available element tags
private TagData name;
/** color used to render this channel*/
private TagData color;
/** name of fluorophore to produce this channel (only fluorescence images)*/
private TagData fluorophore;
/** type of illumination to capture the channel*/
private TagData illumType;
private TagData exposureTime;
/** wavelength of excitation for a particular channel*/
private TagData excitWavelength;
/** wavelength of emission for a particular channel*/
private TagData emissionWavelength;
/**type of microscopy performed */
//private TagData acquisitionMode;
private TagData imagingMode;
private TagData illuminationMode;
/** method to achieve contrast for this channel*/
private TagData contrastMethod;
/**specify the combined effect of any neutral density filter used*/
private TagData ndFilter;
/** specifying adjustable pin hole diameter for confocal microscopes. Unit are set by PinholeSizeUnit*/
private TagData pinholeSize;


//TODO
private String stagePosRef;


/**
 * Creates a new instance.
 * @param model Reference to model.
 */
public ChannelViewer(Channel model,ModuleConfiguration conf)
{
	this.channel=model;
	initComponents(conf);
	initTagList();
	buildGUI();
}

private void initTagList()
{
	tagList=new ArrayList<TagData>();
	tagList.add(name);
	tagList.add(color);
	tagList.add(fluorophore);
	tagList.add(illuminationMode);
	tagList.add(illumType);
	tagList.add(exposureTime);
	tagList.add(excitWavelength);
	tagList.add(emissionWavelength);
	tagList.add(imagingMode);
	tagList.add(contrastMethod);
	tagList.add(ndFilter);
	tagList.add(pinholeSize);
	
}

/**
 * Builds and lay out GUI.
 */
private void buildGUI() 
{
	List<JLabel> labels= new ArrayList<JLabel>();
	List<JComponent> comp=new ArrayList<JComponent>();
	addTagToGUI(name,labels,comp);
	addTagToGUI(color,labels,comp);
	addTagToGUI(fluorophore,labels,comp);
	addTagToGUI(illumType,labels,comp);
	addTagToGUI(exposureTime,labels,comp);
	if(exposureTime!=null)exposureTime.setEnable(false);
	addTagToGUI(excitWavelength,labels,comp);
	addTagToGUI(emissionWavelength,labels,comp);
//	addTag(acquisitionMode);
	addTagToGUI(imagingMode,labels,comp);
	addTagToGUI(illuminationMode,labels,comp);
	if(illuminationMode!=null)illuminationMode.setEnable(false);
	
	addTagToGUI(contrastMethod,labels,comp);
	addTagToGUI(ndFilter,labels,comp);
	addTagToGUI(pinholeSize,labels,comp);
	
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
	case TagNames.CH_NAME:
			setName(null,prop);
		this.name.setVisible(true);
		break;
	case TagNames.COLOR:
			setColor(null,prop);
		color.setVisible(true);
		break;
	case TagNames.FLUOROPHORE:
			setFluorophore(null, prop);
		fluorophore.setVisible(true);
		break;
	case TagNames.ILLUMTYPE:
			setIllumType(null, prop);
		illumType.setVisible(true);
		break;
	case TagNames.EXPOSURETIME:
			setExposureTime(null, prop);
		exposureTime.setVisible(true);
		break;
	case TagNames.EXCITWAVELENGTH:
			setExcitWavelength(null, prop);
		excitWavelength.setVisible(true);
		break;
	case TagNames.EMISSIONWAVELENGTH:
			setEmissionWavelength(null, prop);
		emissionWavelength.setVisible(true);
		break;
	case TagNames.IMAGINGMODE:
			setImagingMode(null, prop);
		imagingMode.setVisible(true);
		break;
	case TagNames.ILLUMINATIONMODE:
			setIlluminationMode(null, prop);
		illuminationMode.setVisible(true);
		break;
	case TagNames.CONTRASTMETHOD:
			setContrastMethod(null, prop);
		contrastMethod.setVisible(true);
		break;
	case TagNames.NDFILTER:
			setNDFilter(null, prop);
		ndFilter.setVisible(true);
		break;
	case TagNames.PINHOLESIZE:
			setPinholeSize(null,prop);
		pinholeSize.setVisible(true);
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
	if(channel!=null){
		//Channel data
		try{ setName(channel.getName(),ElementsCompUI.REQUIRED);
		} catch (NullPointerException e) { }
		try{ setColor(channel.getColor(), ElementsCompUI.REQUIRED);
		} catch (NullPointerException e) { }
		try{ setFluorophore(channel.getFluor(), ElementsCompUI.REQUIRED);
		} catch (NullPointerException e) { }
		try{ setIllumType(channel.getIlluminationType(), ElementsCompUI.REQUIRED);
		} catch (NullPointerException e) { }
		//TODO exposure time
		try{ setExposureTime(null, ElementsCompUI.REQUIRED);
		} catch (NullPointerException e) { }
		try{ setExcitWavelength(channel.getExcitationWavelength(), ElementsCompUI.REQUIRED);
		} catch (NullPointerException e) { }
		try{ setEmissionWavelength(channel.getEmissionWavelength(), ElementsCompUI.REQUIRED);
		} catch (NullPointerException e) { }
		try{ setImagingMode(channel.getAcquisitionMode(), ElementsCompUI.REQUIRED);
		setIlluminationMode(channel.getAcquisitionMode(), ElementsCompUI.REQUIRED);
		} catch (NullPointerException e) { }
		try{ setContrastMethod(channel.getContrastMethod(), ElementsCompUI.REQUIRED);
		} catch (NullPointerException e) { }
		try{ setNDFilter(channel.getNDFilter(), ElementsCompUI.REQUIRED);
		} catch (NullPointerException e) { }
		try {setPinholeSize(channel.getPinholeSize(), REQUIRED);
		} catch (NullPointerException e) {}
	}
	
	
}





/*------------------------------------------------------
 * Set methods data Values
 * -----------------------------------------------------*/


private void setName(String value, boolean prop)
{
	if(name == null) 
		name = new TagData(TagNames.CH_NAME,value,prop,TagData.TEXTFIELD);
	else 
		name.setTagValue(value,prop);
}
public String getName()
{
	return name!=null ? name.getTagValue():null;
}

private void setColor(Color value, boolean prop)
{
	String val= (value != null) ? Integer.toHexString(value.getValue()):"";
	
	if(color == null) 
		color = new TagData(TagNames.COLOR,val,prop,TagData.TEXTFIELD);
	else 
		color.setTagValue(val,prop);
}
private void setFluorophore(String value, boolean prop)
{
	if(fluorophore == null) 
		fluorophore = new TagData(TagNames.FLUOROPHORE,value,prop,TagData.TEXTFIELD);
	else 
		fluorophore.setTagValue(value,prop);
}
private void setIllumType(IlluminationType value, boolean prop)
{
	String val= (value != null) ? String.valueOf(value):"";
	if(illumType == null) 
		illumType = new TagData(TagNames.ILLUMTYPE,val,prop,TagData.COMBOBOX,getNames(IlluminationType.class));
	else 
		illumType.setTagValue(val,prop);
}
private void setExposureTime(String value, boolean prop)
{
	if(exposureTime == null) 
		exposureTime = new TagData(TagNames.EXPOSURETIME,value,prop,TagData.TEXTFIELD);
	else 
		exposureTime.setTagValue(value,prop);
}
private void setExcitWavelength(Length value, boolean prop)
{
	String val=(value!=null) ? String.valueOf(value.value()) :"";
	Unit unit=(value!=null)? value.unit(): TagNames.EXCITATIONWL_UNIT;
	if(excitWavelength == null) 
		excitWavelength = new TagData(TagNames.EXCITWAVELENGTH,val,unit,prop,TagData.TEXTFIELD);
	else 
		excitWavelength.setTagValue(val,unit,prop);
}
private void setEmissionWavelength(Length value, boolean prop)
{
	String val=(value!=null) ? String.valueOf(value.value()) :"";
	Unit unit=(value!=null)? value.unit(): TagNames.EMISSIONWL_UNIT;
	if(emissionWavelength == null) 
		emissionWavelength = new TagData(TagNames.EMISSIONWAVELENGTH,val,unit,prop,TagData.TEXTFIELD);
	else 
		emissionWavelength.setTagValue(val,unit,prop);
}

private void setPinholeSize(Length value, boolean prop)
{
	String val = (value!=null) ? String.valueOf(value.value()) : "";
	Unit unit=(value!=null)? value.unit(): TagNames.PINHOLESIZE_UNIT;
	if(pinholeSize==null)
		pinholeSize=new TagData(TagNames.PINHOLESIZE,val,unit,prop,TagData.TEXTFIELD);
	else
		pinholeSize.setTagValue(val, unit, prop);
}
//public void setAcquisitionMode(AcquisitionMode value, boolean prop)
//{
//	String val= (value != null) ? String.valueOf(value):"";
//	if(acquisitionMode == null) 
//		acquisitionMode = new TagData("Acquisition Mode: ",val,prop,COMBOBOX,getNames(AcquisitionMode.class));
//	else 
//		acquisitionMode.setTagValue(val,prop);
//}
private void setImagingMode(AcquisitionMode value, boolean prop)
{
	String val= (value != null) ? String.valueOf(value):"";
	if(imagingMode == null) 
		imagingMode = new TagData(TagNames.IMAGINGMODE,val,prop,TagData.COMBOBOX,getNames(AcquisitionMode.class));
	else 
		imagingMode.setTagValue(val,prop);
}
private void setIlluminationMode(AcquisitionMode value, boolean prop)
{
	String val= (value != null) ? String.valueOf(value):"";
	if(illuminationMode == null) 
		illuminationMode = new TagData(TagNames.ILLUMINATIONMODE,val,prop,TagData.COMBOBOX,getNames(AcquisitionMode.class));
	else 
		illuminationMode.setTagValue(val,prop);
}
private void setContrastMethod(ContrastMethod value, boolean prop)
{
	String val= (value != null) ? String.valueOf(value):"";
	if(contrastMethod == null) 
		contrastMethod = new TagData(TagNames.CONTRASTMETHOD,val,prop,TagData.COMBOBOX,getNames(ContrastMethod.class));
	else 
		contrastMethod.setTagValue(val,prop);
}
private void setNDFilter(Double value, boolean prop)
{
	String val= (value != null) ? String.valueOf(value):"";
	if(ndFilter == null) 
		ndFilter = new TagData(TagNames.NDFILTER,val,prop,TagData.TEXTFIELD);
	else 
		ndFilter.setTagValue(val,prop);
}



@Override
public void saveData() 
{
	if(channel==null)
		channel=new Channel();
	//TODO format check
	try{
		channel.setName(name.getTagValue());
	}catch(Exception e){
		LOGGER.error("[DATA] can't read CHANNEL name input");
	}
	try{
		channel.setColor(parseColor(color.getTagValue()));
	}catch(Exception e){
		LOGGER.error("[DATA] can't read CHANNEL color input");
	}
	try{
		channel.setFluor(fluorophore.getTagValue());
	}catch(Exception e){
		LOGGER.error("[DATA] can't read CHANNEL fluorophore input");
	}
	try{
		channel.setIlluminationType(parseIllumType(illumType.getTagValue()));
	}catch(Exception e){
		LOGGER.error("[DATA] can't read CHANNEL illumination type input");
	}
	//		TODO channel.setExpTime;
	try{
		channel.setExcitationWavelength(parseToLength(excitWavelength.getTagValue(), excitWavelength.getTagUnit()));
	}catch(Exception e){
		LOGGER.error("[DATA] can't read CHANNEL excitation wavelength input");
	}
	try{
		channel.setEmissionWavelength(parseToLength(emissionWavelength.getTagValue(),emissionWavelength.getTagUnit()));
	}catch(Exception e){
		LOGGER.error("[DATA] can't read CHANNEL emission wavelength input");
	}
	try{
		channel.setAcquisitionMode(parseAcqMode(imagingMode.getTagValue()));
	}catch(Exception e){
		LOGGER.error("[DATA] can't read CHANNEL acquisition mode input");
	}

	//TODO: imagingmode und illuminationmode abspeichern

	try{
		channel.setContrastMethod(parseContrastMethod(contrastMethod.getTagValue()));
	}catch(Exception e){
		LOGGER.error("[DATA] can't read CHANNEL contrast method input");
	}
	try{
		channel.setNDFilter(parseToDouble(ndFilter.getTagValue()));
	}catch(Exception e){
		LOGGER.error("[DATA] can't read CHANNEL ndfilter input");
	}
	
	try{
		channel.setPinholeSize(parseToLength(pinholeSize.getTagValue(),pinholeSize.getTagUnit()));
	}catch(Exception e){
		LOGGER.error("[DATA] can't read CHANNEL pinhole size input");
	}

}
private ContrastMethod parseContrastMethod(String c) throws EnumerationException
{
	if(c.equals(""))
		return null;
	
	return ContrastMethod.fromString(c);
}
private Color parseColor(String c)
{
	if(c.equals(""))
		return null;
	
	return new Color(Integer.valueOf(c, 16).intValue());//Integer.valueOf(c));
}
private IlluminationType parseIllumType(String c) throws EnumerationException 
{
	if(c.equals(""))
		return null;
	
	return IlluminationType.fromString(c);
}

private AcquisitionMode parseAcqMode(String c) throws EnumerationException {
	if(c.equals(""))
		return null;
	
	return AcquisitionMode.fromString(c);
}


/*----------------------------------------------------
 * Model
 -------------------------------------------------------*/
public boolean addData(Channel c, boolean overwrite) 
{
	boolean conflicts=false;
	if(overwrite){
		replaceData(c);
		LOGGER.info("[DATA] -- replace CHANNEL data");
	}else
		try {
			completeData(c);
			LOGGER.info("[DATA] -- complete CHANNEL data");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	setGUIData();
	return conflicts;
}

private void completeData(Channel c) throws Exception
{
	//copy input fields
	Channel copyIn=null;
	if(channel!=null){
		if(hasDataToSave()) saveData();
		copyIn=new Channel(channel);
	}
	
	replaceData(c);
	
	// set input field values again
	if(copyIn!=null){
		//read data
		String name=copyIn.getName();
		Color color=copyIn.getColor();
		String fluor=copyIn.getFluor();
		Length exW=copyIn.getExcitationWavelength();
		Length emW=copyIn.getEmissionWavelength();
		AcquisitionMode aMode=copyIn.getAcquisitionMode();
		ContrastMethod cMethod=copyIn.getContrastMethod();
		Double ndf=copyIn.getNDFilter();
		
		if(c.getID()!=null && !c.getID().equals(""))
			channel.setID(c.getID());
		if(name!=null && !name.equals("")) channel.setName(name);
		if(color!=null) channel.setColor(color);
		if(fluor!=null && !fluor.equals("")) channel.setFluor(fluor);
		if(exW!=null) channel.setExcitationWavelength(exW);
		if(emW!=null) channel.setEmissionWavelength(emW);
		if(aMode!=null) channel.setAcquisitionMode(aMode);
		if(cMethod!=null) channel.setContrastMethod(cMethod);
		if(ndf!=null) channel.setNDFilter(ndf);
	
	}
}

private void replaceData(Channel c)
{
	if(c!=null){
		channel=c;
		
	}
}
public static void mergeData(Channel in, Channel channelOME)
{
	if(channelOME==null ){
		if(in==null){
			LOGGER.error("failed to merge CHANNEL data");
		}else{
			channelOME=in;
		}
		return;
	}else if(in==null){
		LOGGER.info("nothing to merge CHANNEL data");
		return;
	}
	
	channelOME.setName(in.getName());
	channelOME.setColor(in.getColor());
	channelOME.setFluor(in.getFluor());
	channelOME.setIlluminationType(in.getIlluminationType());
	channelOME.setExcitationWavelength(in.getExcitationWavelength());
	channelOME.setEmissionWavelength(in.getEmissionWavelength());
	channelOME.setAcquisitionMode(in.getAcquisitionMode());
	channelOME.setContrastMethod(in.getContrastMethod());
	channelOME.setNDFilter(in.getNDFilter());
	
	channelOME.setDetectorSettings(in.getDetectorSettings());
	channelOME.setLightSourceSettings(in.getLightSourceSettings());
	channelOME.setLightPath(in.getLightPath());
}


}


