package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.border.TitledBorder;


import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.ElementsCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.TagData;

import loci.formats.meta.IMetadata;
import ome.units.quantity.Length;
import ome.units.quantity.Time;
import ome.xml.model.Plane;
import ome.xml.model.primitives.NonNegativeInteger;

/**
 * The Plane object holds microscope stage and image timing data
for a given channel/z-section/timepoint.
 * @author kunis
 *
 */
public class PlaneCompUI extends ElementsCompUI
{
	// z-section this plane is for (numbered from 0)
	private TagData planePosZ;
	// timepoint this plane is for (numbered from 0)
	private TagData planePosT;
	// channel this plane is for (numbered from 0)
	private TagData planePosC;
	// time since the beginning of the experiment
	private TagData deltaT;
	// length of the exposure
	private TagData exposureTime;
	// x position of the stage
	private TagData posX;
	// y position of the stage
	private TagData posY;
	// z position of the stage
	private TagData posZ;
	private List<TagData> tagList;
	
	
	private Plane plane;
	
	private void initTagList()
	{
		tagList=new ArrayList<TagData>();
		tagList.add(planePosZ);
		tagList.add(planePosT);
		tagList.add(planePosC);
		tagList.add(deltaT);
		tagList.add(exposureTime);
		tagList.add(posX);
		tagList.add(posY);
		tagList.add(posZ);
		
	}
	
	public boolean userInput()
	{
		boolean result=false;
		if(tagList!=null){
			for(int i=0; i<tagList.size();i++) 
				result= result || tagList.get(i).valueChanged();
		}
		return result;
	}
	
	public PlaneCompUI() 
	{
		initGUI();
		createDummyPane(false);
	}
	
	public PlaneCompUI(Plane _plane)
	{
		plane=_plane;
		initGUI();
		if(plane!=null){
			setGUIData();
		
		}else{
			createDummyPane(false);
		}
	}
	private void createNewElement()
	{
		plane=new Plane();
	}
	
	public Plane getData()
	{
		if(userInput())
			readGUIInput();
		return plane;
	}
	
	//TODO
	private void readGUIInput() {
		if(plane==null)
			createNewElement();
	}

	private void initGUI()
	{
		buildComp=false;
		labels= new ArrayList<JLabel>();
		comp = new ArrayList<JComponent>();
		
		gridbag = new GridBagLayout();
		c = new GridBagConstraints();
		setLayout(gridbag);
		setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
	}
	
	private void setGUIData()
	{
		if(plane!=null){
			try{setPosX(plane.getPositionX(), ElementsCompUI.REQUIRED);}
			catch(NullPointerException e){}
			try{setPosY(plane.getPositionY(), ElementsCompUI.REQUIRED);}
			catch(NullPointerException e){}
			try{setPosZ(plane.getPositionZ(), ElementsCompUI.REQUIRED);}
			catch(NullPointerException e){}

			try{setDeltaT(plane.getDeltaT(), ElementsCompUI.REQUIRED);}
			catch(NullPointerException e){}
			try{setExposureTime(plane.getExposureTime(), ElementsCompUI.REQUIRED);}
			catch(NullPointerException e){}

			try{setZ(plane.getTheZ(), ElementsCompUI.REQUIRED);}
			catch(NullPointerException e){}
			try{setT(plane.getTheT(), ElementsCompUI.REQUIRED);}
			catch(NullPointerException e){}
			try{setC(plane.getTheC(), ElementsCompUI.REQUIRED);}
			catch(NullPointerException e){}
		}
	}
	
	public JDialog getWindow()
	{
		JDialog d=new JDialog();
		d.setTitle("Plane/Stage Position");
		d.setSize(400,250);
		d.setModal(true);
		//TODO init position
		d.add(this);
		return d;
		
	}
	
	@Override
	public void buildComponents() {
		labels.clear();
		comp.clear();
		
		addTagToGUI(planePosZ);
		addTagToGUI(planePosT);
		addTagToGUI(planePosC);
		addTagToGUI(deltaT);
		addTagToGUI(exposureTime);
		addTagToGUI(posX);
		addTagToGUI(posY);
		addTagToGUI(posZ);
				
		addLabelTextRows(labels, comp, gridbag, this);
		
		c.gridwidth = GridBagConstraints.REMAINDER; //last
		c.anchor = GridBagConstraints.WEST;
		c.weightx = 1.0;
		
		if(planePosZ!=null)planePosZ.setEnable(false);
		if(planePosT!=null)planePosT.setEnable(false);
		if(planePosC!=null)planePosC.setEnable(false);
		
		buildComp=true;		
		initTagList();
	}
	@Override
	public void buildExtendedComponents() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void createDummyPane(boolean inactive) {
		setZ(null,OPTIONAL);
		setT(null,OPTIONAL);
		setC(null,OPTIONAL);
		setDeltaT(null,OPTIONAL);
		setExposureTime(null, OPTIONAL);
		setPosX(null,OPTIONAL);
		setPosY(null,OPTIONAL);
		setPosZ(null,OPTIONAL);
		
		planePosZ.setEnable(false);
		planePosT.setEnable(false);
		planePosC.setEnable(false);
		deltaT.setEnable(false);
		exposureTime.setEnable(false);
		posX.setEnable(false);
		posY.setEnable(false);
		posZ.setEnable(false);
	}
	@Override
	public void clearDataValues() {
		clearTagValue(planePosZ);
		clearTagValue(planePosT);
		clearTagValue(planePosC);
		clearTagValue(deltaT);
		clearTagValue(exposureTime);
		clearTagValue(posX);
		clearTagValue(posY);
		clearTagValue(posZ);
	}
	
	public void setZ(NonNegativeInteger value, boolean prop)
	{
		String val = (value != null) ? String.valueOf(value.getValue()) : "";
		if(planePosZ == null) 
			planePosZ = new TagData("Z: ",val,prop,TagData.TEXTFIELD);
		else 
			planePosZ.setTagValue(val,prop);
	}
	public void setT(NonNegativeInteger value, boolean prop)
	{
		String val = (value != null) ? String.valueOf(value.getValue()) : "";
		if(planePosT == null) 
			planePosT = new TagData("T: ",val,prop,TagData.TEXTFIELD);
		else 
			planePosT.setTagValue(val,prop);
	}
	public void setC(NonNegativeInteger value, boolean prop)
	{
		String val = (value != null) ? String.valueOf(value.getValue()) : "";
		if(planePosC == null) 
			planePosC = new TagData("C: ",val,prop,TagData.TEXTFIELD);
		else 
			planePosC.setTagValue(val,prop);
	}
	public void setDeltaT(Time value, boolean prop)
	{
		String val = (value != null) ? String.valueOf(value.value()) : "";
		String unit=(value!=null) ? value.unit().getSymbol() :"?";
		if(deltaT == null) 
			deltaT = new TagData("Delta T ["+unit+"]: ",val,prop,TagData.TEXTFIELD);
		else 
			deltaT.setTagValue(val,prop);
	}
	public void setExposureTime(Time value, boolean prop)
	{
		String val = (value != null) ? String.valueOf(value.value()) : "";
		String unit=(value!=null) ? value.unit().getSymbol() :"?";
		if(exposureTime == null) 
			exposureTime = new TagData("Exposure Time ["+unit+"]: ",val,prop,TagData.TEXTFIELD);
		else 
			exposureTime.setTagValue(val,prop);
	}
	public void setPosX(Length value, boolean prop)
	{
		String val = (value != null) ? String.valueOf(value.value()) : "";
		String unit=(value!=null) ? value.unit().getSymbol() :"?";
		if(posX == null) 
			posX = new TagData("Position X ["+unit+"]: ",val,prop,TagData.TEXTFIELD);
		else 
			posX.setTagValue(val,prop);
	}
	public void setPosY(Length value, boolean prop)
	{
		String val = (value != null) ? String.valueOf(value.value()) : "";
		String unit=(value!=null) ? value.unit().getSymbol() :"?";
		if(posY == null) 
			posY = new TagData("Position Y ["+unit+"]: ",val,prop,TagData.TEXTFIELD);
		else 
			posY.setTagValue(val,prop);
	}
	public void setPosZ(Length value, boolean prop)
	{
		String val = (value != null) ? String.valueOf(value.value()) : "";
		String unit=(value!=null) ? value.unit().getSymbol() :"?";
		if(posZ == null) 
			posZ = new TagData("Position Z ["+unit+"]: ",val,prop,TagData.TEXTFIELD);
		else 
			posZ.setTagValue(val,prop);
	}

	@Override
	public List<TagData> getActiveTags() {
		// TODO Auto-generated method stub
		return null;
	}

	public void addData(Plane p) 
	{
		if(plane!=null){
			
		}else if(p!=null){
			plane=p;
		}
		setGUIData();
	}

	
}
