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
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.ModuleConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.TagNames;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagData;
import org.slf4j.LoggerFactory;

public class SampleViewer extends ModuleViewer{

	private static final org.slf4j.Logger LOGGER =
    	    LoggerFactory.getLogger(SampleViewer.class);
 
private Sample sample;

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
public SampleViewer(Sample model,ModuleConfiguration conf)
{
	this.sample=model;
	initComponents(conf);
	initTagList();
	buildGUI();
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

/**
 * Show data of objective
 */
private void setGUIData() 
{
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
			String[] n={sample.getObservedSample(0).getGridNumberX(),
				sample.getObservedSample(0).getGridNumberY()};
		
			setExpGridNumber(n, REQUIRED);
		
		}catch(NullPointerException e){}
		

		try{ setExpObjectType(sample.getObservedSample(0).getObjectType(), REQUIRED);}
		catch(NullPointerException e){}

		try{ setExpObjectNr(sample.getObservedSample(0).getObjectNumber(), REQUIRED);}
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
	if(sample==null)
		sample=new Sample();
	//TODO input checker
	try{sample.setPrepDate(preparationDate.getTagValue().equals("")? 
			null : Timestamp.valueOf(preparationDate.getTagValue()));}
	catch(Exception e){}
	try{
		sample.setPrepDescription(preparationDescription.getTagValue());
	}catch(Exception e){
		LOGGER.error("[DATA] can't read SAMPLE preparation description input");
	}
	try{
		String g1=gridBoxNumber!=null ? gridBoxNumber.getTagValue() : null;
		String g2=gridBoxType!=null ? gridBoxType.getTagValue(): null;
		sample.setGridBoxData(g1, g2);
	}catch(Exception e){
		LOGGER.error("[DATA] can't read SAMPLE grid box data input");
		e.printStackTrace();
	}
	
	ObservedSample observedSample=new ObservedSample();
	observedSample.setSampleID(MetadataTools.createLSID("ObservedSample", 0));
	try{
		observedSample.setObjectNumber(expObjectNr!=null ? expObjectNr.getTagValue(): null);
	}catch(Exception e){
		LOGGER.error("[DATA] can't read SAMPLE observed sample object nr input");
	}
	try{
		observedSample.setObjectType(expObjectType!=null ? expObjectType.getTagValue():null);
	}catch(Exception e){
		LOGGER.error("[DATA] can't read SAMPLE observed sample object type input");
	}
	try{
		observedSample.setGridNumberX(expGrid!=null ? expGrid.getTagValue(0):null);
	}catch(Exception e){
		LOGGER.error("[DATA] can't read SAMPLE observed sample grid number x input");
	}
	try{
		observedSample.setGridNumberY(expGrid!=null ?expGrid.getTagValue(1):null);
	}catch(Exception e){
		LOGGER.error("[DATA] can't read SAMPLE observed sample grid number y input");
	}
	sample.setObservedSample(observedSample);
	
	try{
		sample.setRawMaterialDesc(rawMaterialDesc!=null ? rawMaterialDesc.getTagValue():null); 
	}catch(Exception e){
		LOGGER.error("[DATA] can't read SAMPLE raw material description input");
	}
	try{
		sample.setRawMaterialCode(rawMaterialCode!=null ? rawMaterialCode.getTagValue():null); 
	}catch(Exception e){
		LOGGER.error("[DATA] can't read SAMPLE raw material code input");
	}
	
	
	
}
/*----------------------------------------
 * MODEL functionality
 -----------------------------------------*/
public boolean addData(Sample s, boolean overwrite)
{
	boolean conflicts=false;
	if(overwrite){
		replaceData(s);
		LOGGER.info("[DATA] -- replace SAMPLE data");
	}else
		try {
			completeData(s);
			LOGGER.info("[DATA] -- complete SAMPLE data");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	setGUIData();
	return conflicts;
}

private void replaceData(Sample s)
{
	if(s!=null){
		sample=s;
	}
}

private void completeData(Sample s) throws Exception
{
	//copy input fields
	Sample copyIn=null;
	if(sample!=null){
		if(hasDataToSave()) saveData();
		copyIn=new Sample(sample);
	}

	replaceData(s);

	// set input field values again
	if(copyIn!=null){
		String pdesc=copyIn.getPrepDescription();
		Timestamp pdate=copyIn.getPrepDate();
		String rc=copyIn.getRawMaterialCode();
		String rdesc=copyIn.getRawMaterialDesc();
		
		GridBox g=copyIn.getGridBox();
		String gNr=null;
		String gT=null;
		if(g!=null){
			gNr=g.getNr();
			gT=g.getType();
		}
		ObservedSample os=copyIn.getObservedSample(0);
		String osgx=null;
		String osgy=null;
		String ost=null;
		String osNr=null;
		if(os!=null){
			osgx=os.getGridNumberX();
			osgy=os.getGridNumberY();
			ost=os.getObjectType();
			osNr=os.getObjectNumber();
		}

		if(pdesc!=null && !pdesc.equals("")) sample.setPrepDescription(pdesc);
		if(pdate!=null) sample.setPrepDate(pdate);
		if(rc!=null && !rc.equals("")) sample.setRawMaterialCode(rc);
		if(rdesc!=null && !rdesc.equals("")) sample.setRawMaterialDesc(rdesc);
		if(gNr!=null) sample.getGridBox().setNr(gNr);
		if(gT!=null && !gT.equals("")) sample.getGridBox().setType(gT);
		if(osgx!=null && !osgx.equals("")) sample.getObservedSample(0).setGridNumberY(osgx);
		if(osgy!=null && !osgy.equals("")) sample.getObservedSample(0).setGridNumberY(osgy);
		if(ost!=null && !ost.equals("")) sample.getObservedSample(0).setObjectType(ost);
		if(osNr!=null && !osNr.equals("")) sample.getObservedSample(0).setObjectNumber(osNr);
	}
}

}


