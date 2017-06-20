package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.view;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import ome.units.quantity.Length;
import ome.units.unit.Unit;
import ome.xml.model.Channel;
import ome.xml.model.enums.AcquisitionMode;
import ome.xml.model.enums.ContrastMethod;
import ome.xml.model.enums.EnumerationException;
import ome.xml.model.enums.IlluminationType;
import ome.xml.model.primitives.Color;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model.ChannelModel;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.ModuleConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.TagNames;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagData;
import org.slf4j.LoggerFactory;

/**
 * Works for xsi:schemaLocation="http://www.openmicroscopy.org/Schemas/OME/2015-01 
 * @author Kunis
 *
 */
public class ChannelViewer extends ModuleViewer
{

	private static final org.slf4j.Logger LOGGER =
    	    LoggerFactory.getLogger(ChannelViewer.class);
 
private ChannelModel data;
private int index;

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
public ChannelViewer(ChannelModel model,ModuleConfiguration conf,int index)
{
	System.out.println("# ChannelViewer::newInstance("+(model!=null?"model":"null")+") "+index);
	this.data=model;
	this.index=index;
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
	
	gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER; //last
	gridBagConstraints.anchor = GridBagConstraints.WEST;
	gridBagConstraints.weightx = 1.0;
	
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
	gridBagConstraints = new GridBagConstraints();

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

@Override
protected void setPredefinedTag(TagConfiguration t) 
{
	//no predefinitions posiible for channel
}

/**
 * Show data of objective
 */
private void setGUIData() 
{
	if(data==null || data.getNumberOfChannels()==0)
		return;
	Channel channel=data.getChannel(index);
	
	if(channel!=null){
		//Channel data
		try{ setName(channel.getName(),REQUIRED);
		} catch (NullPointerException e) { }
		try{ setColor(channel.getColor(), REQUIRED);
		} catch (NullPointerException e) { }
		try{ setFluorophore(channel.getFluor(), REQUIRED);
		} catch (NullPointerException e) { }
		try{ setIllumType(channel.getIlluminationType(), REQUIRED);
		} catch (NullPointerException e) { }
		//TODO exposure time
		try{ setExposureTime(null, REQUIRED);
		} catch (NullPointerException e) { }
		try{ setExcitWavelength(channel.getExcitationWavelength(), REQUIRED);
		} catch (NullPointerException e) { }
		try{ setEmissionWavelength(channel.getEmissionWavelength(), REQUIRED);
		} catch (NullPointerException e) { }
		try{ setImagingMode(channel.getAcquisitionMode(), REQUIRED);
		setIlluminationMode(channel.getAcquisitionMode(), REQUIRED);
		} catch (NullPointerException e) { }
		try{ setContrastMethod(channel.getContrastMethod(), REQUIRED);
		} catch (NullPointerException e) { }
		try{ setNDFilter(channel.getNDFilter(), REQUIRED);
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
	if(excitWavelength == null) {
		excitWavelength = new TagData(TagNames.EXCITWAVELENGTH,val,unit,prop,TagData.TEXTFIELD);
		excitWavelength.addDocumentListener(createDocumentListenerPosFloat(excitWavelength,"Invalid input. Use float >0!"));
	}else 
		excitWavelength.setTagValue(val,unit,prop);
}
private void setEmissionWavelength(Length value, boolean prop)
{
	String val=(value!=null) ? String.valueOf(value.value()) :"";
	Unit unit=(value!=null)? value.unit(): TagNames.EMISSIONWL_UNIT;
	if(emissionWavelength == null) {
		emissionWavelength = new TagData(TagNames.EMISSIONWAVELENGTH,val,unit,prop,TagData.TEXTFIELD);
		emissionWavelength.addDocumentListener(createDocumentListenerPosFloat(emissionWavelength,"Invalid input. Use float >0!"));
	}else 
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
	
	System.out.println("# ChannelViewer::saveData()");
	if(data==null)
		data=new ChannelModel();
	
	if(data.getChannel(index)==null)
		data.addData(new Channel(), true, index);
	
	Channel channel=data.getChannel(index);
	
		
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
		channel.setExcitationWavelength(parseToLength(excitWavelength.getTagValue(), excitWavelength.getTagUnit(), true));
	}catch(Exception e){
		LOGGER.error("[DATA] can't read CHANNEL excitation wavelength input");
	}
	try{
		channel.setEmissionWavelength(parseToLength(emissionWavelength.getTagValue(),emissionWavelength.getTagUnit(), true));
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
		channel.setPinholeSize(parseToLength(pinholeSize.getTagValue(),pinholeSize.getTagUnit(), false));
	}catch(Exception e){
		LOGGER.error("[DATA] can't read CHANNEL pinhole size input");
	}
	
	

}
public static ContrastMethod parseContrastMethod(String c) throws EnumerationException
{
	if(c.equals(""))
		return null;
	
	return ContrastMethod.fromString(c);
}
public static Color parseColor(String c)
{
	if(c.equals(""))
		return null;
	
	return new Color(Integer.valueOf(c, 16).intValue());//Integer.valueOf(c));
}
public static IlluminationType parseIllumType(String c) throws EnumerationException 
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

public List<TagData> getChangedTags() 
{
	List<TagData> list = new ArrayList<TagData>();
	if(inputAt(name)) list.add(name);
	if(inputAt(color)) list.add(color);
	if(inputAt(fluorophore)) list.add(fluorophore);
	if(inputAt(illumType)) list.add(illumType);
	if(inputAt(exposureTime)) list.add(exposureTime);
	if(inputAt(excitWavelength)) list.add(excitWavelength);
	if(inputAt(emissionWavelength)) list.add(emissionWavelength);
	if(inputAt(imagingMode)) list.add(imagingMode);
	if(inputAt(illuminationMode)) list.add(illuminationMode);
	if(inputAt(contrastMethod)) list.add(contrastMethod);
	if(inputAt(ndFilter)) list.add(ndFilter);
	if(inputAt(pinholeSize)) list.add(pinholeSize);
	
	return list;
}

public int getIndex() {
	return index;
}


}


