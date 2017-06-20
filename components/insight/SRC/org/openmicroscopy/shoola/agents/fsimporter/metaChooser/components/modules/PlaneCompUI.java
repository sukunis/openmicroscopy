package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.border.TitledBorder;






import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules.ElementsCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.TagNames;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagData;

import loci.formats.meta.IMetadata;
import ome.units.quantity.Length;
import ome.units.quantity.Time;
import ome.units.unit.Unit;
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
	
	
	public PlaneCompUI(Plane _plane)
	{
		plane=_plane;
		initGUI();
		if(plane!=null){
			setGUIData();
		}else{
			plane=new Plane();
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
	
	
	
	@Override
	public void buildComponents() {
		labels.clear();
		comp.clear();
		
		planePosC.setVisible(true);
		planePosT.setVisible(true);
		planePosZ.setVisible(true);
		deltaT.setVisible(true);
		exposureTime.setVisible(true);
		posX.setVisible(true);
		posY.setVisible(true);
		posZ.setVisible(true);
		
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
		
		deltaT.setEnable(false);
		exposureTime.setEnable(false);
		posX.setEnable(false);
		posY.setEnable(false);
		posZ.setEnable(false);
		
		buildComp=true;		
		initTagList();
		
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
		
		
		
//		planePosZ.setEnable(false);
//		planePosT.setEnable(false);
//		planePosC.setEnable(false);
//		deltaT.setEnable(false);
//		exposureTime.setEnable(false);
//		posX.setEnable(false);
//		posY.setEnable(false);
//		posZ.setEnable(false);
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
		Unit unit=(value!=null) ? value.unit() : TagNames.DELTA_T_UNIT;
		if(deltaT == null) 
			deltaT = new TagData(TagNames.DELTA_T,val,unit,prop,TagData.TEXTFIELD);
		else 
			deltaT.setTagValue(val,unit,prop);
	}
	public void setExposureTime(Time value, boolean prop)
	{
		String val = (value != null) ? String.valueOf(value.value()) : "";
		Unit unit=(value!=null) ? value.unit() :TagNames.EXPOSURETIME_UNIT;
		if(exposureTime == null) 
			exposureTime = new TagData(TagNames.EXPOSURETIME,val,unit,prop,TagData.TEXTFIELD);
		else 
			exposureTime.setTagValue(val,unit,prop);
	}
	public void setPosX(Length value, boolean prop)
	{
		String val = (value != null) ? String.valueOf(value.value()) : "";
		Unit unit=(value!=null) ? value.unit() :TagNames.STAGE_POS_X_UNIT;
		if(posX == null) 
			posX = new TagData(TagNames.STAGE_POS_X,val,unit,prop,TagData.TEXTFIELD);
		else 
			posX.setTagValue(val,unit,prop);
	}
	public void setPosY(Length value, boolean prop)
	{
		String val = (value != null) ? String.valueOf(value.value()) : "";
		Unit unit=(value!=null) ? value.unit():TagNames.STAGE_POS_Y_UNIT;
		if(posY == null) 
			posY = new TagData(TagNames.STAGE_POS_Y,val,unit,prop,TagData.TEXTFIELD);
		else 
			posY.setTagValue(val,unit,prop);
	}
	public void setPosZ(Length value, boolean prop)
	{
		String val = (value != null) ? String.valueOf(value.value()) : "";
		Unit unit=(value!=null) ? value.unit():TagNames.STAGE_POS_Z_UNIT;
		if(posZ == null) 
			posZ = new TagData(TagNames.STAGE_POS_Z,val,unit,prop,TagData.TEXTFIELD);
		else 
			posZ.setTagValue(val,unit,prop);
	}

	@Override
	public List<TagData> getActiveTags() {
		// TODO Auto-generated method stub
		return null;
	}

	public void addData(Plane p) 
	{
		if(p!=null){
			plane=p;
		}
		setGUIData();
	}

	@Override
	public void update(List<TagData> list) {
		// TODO Auto-generated method stub
		
	}

	
}
