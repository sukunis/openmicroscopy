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

import loci.formats.MetadataTools;
import ome.xml.model.Objective;
import ome.xml.model.ObjectiveSettings;
import ome.xml.model.primitives.Timestamp;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.format.ObservedSample;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.format.Sample;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.format.Sample.GridBox;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules.ObjectiveEditor;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model.ObjectiveModel;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model.SampleModel;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.ModuleConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.TagNames;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagData;
import org.slf4j.LoggerFactory;

public class SampleViewer extends ModuleViewer{

	private static final org.slf4j.Logger LOGGER =
    	    LoggerFactory.getLogger(SampleViewer.class);
 
private SampleModel data;

// available element tags
private TagData preparationDate;
private TagData preparationDescription;

private TagData gridBoxNumber;
private TagData gridBoxType;

private TagData expGrid;
private TagData expObjectNr;
private TagData expObjectType;

private TagData rawMaterialDesc;
private TagData rawMaterialCode;



/**
 * Creates a new instance.
 * @param model Reference to model.
 */
public SampleViewer(SampleModel model,ModuleConfiguration conf,boolean showPreValues)
{
	this.data=model;
	initComponents(conf);
	initTagList();
	buildGUI();
	showPredefinitions(conf.getTagList(), showPreValues);
}

private void initTagList()
{
	tagList=new ArrayList<TagData>();
	tagList.add(preparationDate);
	tagList.add(preparationDescription);
	tagList.add(gridBoxNumber);
	tagList.add(gridBoxType);
	tagList.add(expGrid);
	tagList.add(expObjectNr);
	tagList.add(expObjectType);
	tagList.add(rawMaterialDesc);
	tagList.add(rawMaterialCode);
	
}

/**
 * Builds and lay out GUI.
 */
private void buildGUI() 
{
	List<JLabel> labels= new ArrayList<JLabel>();
	List<JComponent> comp=new ArrayList<JComponent>();
	addLabelToGUI(new JLabel("Raw Material:"),labels,comp);
	addTagToGUI(rawMaterialCode,labels,comp);
	addTagToGUI(rawMaterialDesc,labels,comp);
	addVSpaceToGui(10,labels,comp);
	
	
	addLabelToGUI(new JLabel("Preparation:"),labels,comp);
	addTagToGUI(preparationDate,labels,comp);
	addTagToGUI(preparationDescription,labels,comp);
	addTagToGUI(gridBoxNumber,labels,comp);
	addTagToGUI(gridBoxType,labels,comp);
	
	addVSpaceToGui(10,labels,comp);
	
	addLabelToGUI(new JLabel("Observed Sample:"),labels,comp);
	addTagToGUI(expGrid,labels,comp);
	addTagToGUI(expObjectType,labels,comp);
	addTagToGUI(expObjectNr,labels,comp);
	
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
	case TagNames.PREPDATE:// no pre value possible
		setPreparationDate(null, prop);
		preparationDate.setVisible(true);
		break;
	case TagNames.PREPDESC:// no pre value possible
		setPreparationDescription(null, prop);
		preparationDescription.setVisible(true);
		break;
	case TagNames.RAWCODE:// no pre value possible
		setRawMaterialCode(null, prop); 
		rawMaterialCode.setVisible(true);
		break;
	case TagNames.RAWDESC:// no pre value possible
		setRawMaterialDesc(null, prop);
		rawMaterialDesc.setVisible(true);
		break;
	case TagNames.GRIDBOXNR:// no pre value possible
		setGridBoxNumber(null, prop);
		gridBoxNumber.setVisible(true);
		break;
	case TagNames.GRIDBOXTYPE:// no pre value possible
		setGridType(null, prop);
		gridBoxType.setVisible(true);
		break;
	case TagNames.EXPGRID:// no pre value possible
		setExpGridNumber(new String[2], prop);
		expGrid.setVisible(true);
		break;
	case TagNames.EXPOBJNR:// no pre value possible
		setExpObjectNr(null, prop);
		expObjectNr.setVisible(true);
		break;
	case TagNames.EXPOBJTYPE: // no pre value possible
		setExpObjectType(null, prop);
		expObjectType.setVisible(true);
		break;
	default:
		LOGGER.warn("[CONF] unknown tag: "+name );break;
	}
}

protected void setPredefinedTag(TagConfiguration t) 
{
	if(t.getValue()==null)
		return;
	
	predefinitionValLoaded=predefinitionValLoaded || (!t.getValue().equals(""));
	String name=t.getName();
	Boolean prop=t.getProperty();
	switch (name) {
	case TagNames.PREPDATE:
		if(preparationDate!=null && !preparationDate.getTagValue().equals(""))
			return;
		setPreparationDate(t.getValue().equals("")? 
				null : Timestamp.valueOf(t.getValue()), prop);
		break;
	case TagNames.PREPDESC:
		if(preparationDescription!=null && !preparationDescription.getTagValue().equals(""))
			return;
		setPreparationDescription(t.getValue(), prop);
		break;
	case TagNames.RAWCODE:
		if(rawMaterialCode!=null && !rawMaterialCode.getTagValue().equals(""))
			return;
		setRawMaterialCode(t.getValue(), prop); 
		break;
	case TagNames.RAWDESC:
		if(rawMaterialDesc!=null && !rawMaterialDesc.getTagValue().equals(""))
			return;
		System.out.println("Raw desc = "+t.getValue());
		setRawMaterialDesc(t.getValue(), prop);
		break;
	case TagNames.GRIDBOXNR:
		if(gridBoxNumber!=null && !gridBoxNumber.getTagValue().equals(""))
			return;
		setGridBoxNumber(t.getValue(), prop);
		break;
	case TagNames.GRIDBOXTYPE:
		if(gridBoxType!=null && !gridBoxType.getTagValue().equals(""))
			return;
		setGridType(t.getValue(), prop);
		break;
	case TagNames.EXPGRID:
		if(expGrid!=null && !expGrid.getTagValue().equals(""))
			return;
		setExpGridNumber(parseExpGrid(t.getValue()), prop);
		break;
	case TagNames.EXPOBJNR:
		if(expObjectNr!=null && !expObjectNr.getTagValue().equals(""))
			return;
		setExpObjectNr(t.getValue(), prop);
		break;
	case TagNames.EXPOBJTYPE: 
		if(expObjectType!=null && !expObjectType.getTagValue().equals(""))
			return;
		setExpObjectType(t.getValue(), prop);
		break;
	default:
		LOGGER.warn("[CONF] unknown tag: "+name );break;
	}
}

/**
 * TODO: parse x,y to stringarray={x,y}
 * @param value
 * @return
 */
public static String[] parseExpGrid(String value) 
{
	String delims="[,]";
	String[] splitting =value.split(delims);	

	String[] result=new String[2];
	result[0]=splitting.length==0 ? "":splitting[0];
	result[1]=splitting.length<2 ? "":splitting[1];
	
	return result;
}

/**
 * Show data of objective
 */
private void setGUIData() 
{
	if(data==null)
		return;
	
	Sample sample=data.getSample();
	
	if(sample!=null){
		try{ setPreparationDescription(sample.getPrepDescription(), REQUIRED);}
		catch(NullPointerException e){}

		try{ setPreparationDate(sample.getPrepDate(), REQUIRED);}
		catch(NullPointerException e){}

		try{ setRawMaterialCode(sample.getRawMaterialCode(), OPTIONAL);}
		catch(NullPointerException e){}

		try{ setRawMaterialDesc(sample.getRawMaterialDesc(), REQUIRED);}
		catch(NullPointerException e){}

		try{ setGridBoxNumber(sample.getGridBox().getNr(), REQUIRED);}
		catch(NullPointerException e){}

		try{ setGridType(sample.getGridBox().getType(), REQUIRED);}
		catch(NullPointerException e){}

		try{
			String[] n={sample.getObservedSample().getGridNumberX(),
				sample.getObservedSample().getGridNumberY()};
		
			setExpGridNumber(n, REQUIRED);
		
		}catch(NullPointerException e){}
		

		try{ setExpObjectType(sample.getObservedSample().getObjectType(), REQUIRED);}
		catch(NullPointerException e){}

		try{ setExpObjectNr(sample.getObservedSample().getObjectNumber(), REQUIRED);}
		catch(NullPointerException e){}
	}
	
	
}




/*------------------------------------------------------
 * Set methods data Values
 * -----------------------------------------------------*/
private void setRawMaterialDesc(String value, boolean prop) 
{
	if(rawMaterialDesc == null) 
		rawMaterialDesc = new TagData(TagNames.RAWDESC,value,prop,TagData.TEXTAREA);
	else 
		rawMaterialDesc.setTagValue(value,prop);
}

private void setRawMaterialCode(String value, boolean prop) 
{
	if(rawMaterialCode == null) 
		rawMaterialCode = new TagData(TagNames.RAWCODE,value,prop,TagData.TEXTFIELD);
	else 
		rawMaterialCode.setTagValue(value,prop);
}

private void setPreparationDate(Timestamp value, boolean prop)
{
	
	String val= (value != null) ? value.getValue():"";
	if(preparationDate == null) 
		preparationDate = new TagData(TagNames.PREPDATE,val,prop,TagData.TIMESTAMP);
	else 
		preparationDate.setTagValue(val,prop);
}

private void setPreparationDescription(String value, boolean prop)
{
	if(preparationDescription == null) 
		preparationDescription = new TagData(TagNames.PREPDESC,value,prop,TagData.TEXTAREA);
	else 
		preparationDescription.setTagValue(value,prop);	
}

private void setGridBoxNumber(String string, boolean prop)
{
	String val=(string!=null) ? String.valueOf(string):"";
	if(gridBoxNumber == null) 
		gridBoxNumber = new TagData(TagNames.GRIDBOXNR,val,prop,TagData.TEXTFIELD);
	else {
		gridBoxNumber.setTagValue(val,0,prop);
	}
}

private void setGridType(String value, boolean prop)
{
	if(gridBoxType == null) 
		gridBoxType = new TagData(TagNames.GRIDBOXTYPE,value,prop,TagData.TEXTAREA);
	else 
		gridBoxType.setTagValue(value,prop);	
}

private void setExpGridNumber(String[] value, boolean prop)
{
	if(expGrid == null) 
		expGrid = new TagData(TagNames.EXPGRID,value,prop,TagData.ARRAYFIELDS);
	else{ 
		expGrid.setTagValue(value[0],0,prop);
		expGrid.setTagValue(value[1],1,prop);
	}
}

private void setExpObjectNr(String value, boolean prop)
{
	if(expObjectNr == null) 
		expObjectNr = new TagData(TagNames.EXPOBJNR,value,prop,TagData.TEXTFIELD);
	else 
		expObjectNr.setTagValue(value,prop);	
}

private void setExpObjectType(String value, boolean prop)
{
	if(expObjectType == null) 
		expObjectType = new TagData(TagNames.EXPOBJTYPE,value,prop,TagData.TEXTAREA);
	else 
		expObjectType.setTagValue(value,prop);	
}




@Override
public void saveData() 
{
	System.out.println("# SampleViewer::saveData()");
	if(data==null)
		data=new SampleModel();


	if(data.getSample()==null)
		data.addData(new Sample(), true);
	
	Sample sample=data.getSample();
	
	//TODO input checker
	try{sample.setPrepDate(preparationDate.getTagValue().equals("")? 
			null : Timestamp.valueOf(preparationDate.getTagValue()));
	preparationDate.dataSaved(true);
	}catch(Exception e){}
	try{
		sample.setPrepDescription(preparationDescription.getTagValue());
		preparationDescription.dataSaved(true);
	}catch(Exception e){
		LOGGER.error("[DATA] can't read SAMPLE preparation description input");
	}
	try{
		String g1=gridBoxNumber!=null ? gridBoxNumber.getTagValue() : null;
		String g2=gridBoxType!=null ? gridBoxType.getTagValue(): null;
		sample.setGridBoxData(g1, g2);
		gridBoxNumber.dataSaved(true);
		gridBoxType.dataSaved(true);
	}catch(Exception e){
		LOGGER.error("[DATA] can't read SAMPLE grid box data input");
		e.printStackTrace();
	}
	
	if(sample.getObservedSample()==null){
		sample.addObservedSample(new ObservedSample());
	}
	ObservedSample observedSample=sample.getObservedSample();
	observedSample.setSampleID(MetadataTools.createLSID("ObservedSample", 0));
	try{
		observedSample.setObjectNumber(expObjectNr!=null ? expObjectNr.getTagValue(): null);
		expObjectNr.dataSaved(true);
	}catch(Exception e){
		LOGGER.error("[DATA] can't read SAMPLE observed sample object nr input");
	}
	try{
		observedSample.setObjectType(expObjectType!=null ? expObjectType.getTagValue():null);
		expObjectType.dataSaved(true);
	}catch(Exception e){
		LOGGER.error("[DATA] can't read SAMPLE observed sample object type input");
	}
	try{
		observedSample.setGridNumberX(expGrid!=null ? expGrid.getTagValue(0):null);
		observedSample.setGridNumberY(expGrid!=null ?expGrid.getTagValue(1):null);
		expGrid.dataSaved(true);
		
		System.out.println("\t...grid(x,y)= "+observedSample.getGridNumberX()+
				", "+observedSample.getGridNumberY());
	}catch(Exception e){
		LOGGER.error("[DATA] can't read SAMPLE observed sample grid number x/y input");
	}
	
	
	try{
		sample.setRawMaterialDesc(rawMaterialDesc!=null ? rawMaterialDesc.getTagValue():null); 
		rawMaterialDesc.dataSaved(true);
	}catch(Exception e){
		LOGGER.error("[DATA] can't read SAMPLE raw material description input");
	}
	try{
		sample.setRawMaterialCode(rawMaterialCode!=null ? rawMaterialCode.getTagValue():null); 
		rawMaterialCode.dataSaved(true);
	}catch(Exception e){
		LOGGER.error("[DATA] can't read SAMPLE raw material code input");
	}
	
	
	
}
public List<TagData> getChangedTags() {
	List<TagData> list = new ArrayList<TagData>();
	if(inputAt(preparationDate)) list.add(preparationDate);
	if(inputAt(preparationDescription)) list.add(preparationDescription);
	
	if(inputAt(rawMaterialCode)) list.add(rawMaterialCode);
	if(inputAt(rawMaterialDesc)) list.add(rawMaterialDesc);
	if(inputAt(expGrid)) list.add(expGrid);
	if(inputAt(expObjectNr)) list.add(expObjectNr);
	if(inputAt(expObjectType)) list.add(expObjectType);
	if(inputAt(gridBoxNumber)) list.add(gridBoxNumber);
	if(inputAt(gridBoxType)) list.add(gridBoxType);
	return list;
}


}


