package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.ElementsCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.TagData;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.TitledSeparator;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.ModuleConfiguration;

import ome.units.UNITS;
import ome.units.quantity.Length;
import ome.units.quantity.Time;
import ome.units.unit.Unit;
import ome.xml.model.Image;
import ome.xml.model.Pixels;
import ome.xml.model.enums.PixelType;
import ome.xml.model.primitives.PositiveInteger;
import ome.xml.model.primitives.Timestamp;

public class ImageCompUI extends ElementsCompUI
{
	private final String L_NAME="Name";
	private final String L_ACQTIME="Acquisition Time";
	private final String L_DIM="Dimension (XY)";
	private final String L_PIXELTYPE="Pixel Depth";
	private final String L_PIXELSIZE="Pixel Size (XY)";
	private final String L_DIMZTC="Dim Z x T x C";
	private final String L_STAGEPOS="Stage Position (XY)";
	private final String L_STEPSIZE="Step Size";
	private final String L_TIMEINC="Time Increment";
	private final String L_WELLNR="Well #";
	
	private TagData name;
	private TagData acqTime;
	private TagData dimXY;
	private TagData pixelType;
	private TagData pixelSize;
	private TagData dimZTC;
	private TagData stagePos;
	private TagData stepSize;
	private TagData timeIncrement;
	private TagData wellNr;
	private List<TagData> tagList;
	
	private Unit<Length> sizeUnit;
	private Unit<Time> timeUnit;
	
	
	
	private Image image;
	
	private void initTagList()
	{
		tagList=new ArrayList<TagData>();
		tagList.add(name);
		tagList.add(acqTime);
		tagList.add(dimXY);
		tagList.add(pixelSize);
		tagList.add(pixelType);
		tagList.add(dimZTC);
		tagList.add(stagePos);
		tagList.add(stepSize);
		tagList.add(timeIncrement);
		tagList.add(wellNr);
		
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
		return (result );
	}
	

	
	public ImageCompUI(ModuleConfiguration objConf)
	{
		sizeUnit=UNITS.MICROM;
		timeUnit=UNITS.SECOND;
		initGUI();
		if(objConf==null)
			createDummyPane(false);
		else
			createDummyPane(objConf.getList(),false);
	}
	
	public ImageCompUI(Image _image,int i)
	{
		image=_image;
		
		sizeUnit=UNITS.MICROM;
		timeUnit=UNITS.SECOND;
		
		initGUI();
		if(image!=null)
			setGUIData();
		else{
			createNewElement();
			createDummyPane(false);
		}
	}
	
	private void createNewElement()
	{
		image=new Image();
		image.setPixels(new Pixels());
	}
	
	public boolean addData(Image img, boolean overwrite)
	{
		boolean conflicts=false;
		if(image!=null){
			if(img!=null){
				String name=img.getName();
				PositiveInteger dimX=img.getPixels().getSizeX();
				PositiveInteger dimY=img.getPixels().getSizeY();
				PositiveInteger dimZ=img.getPixels().getSizeZ();
				PositiveInteger dimT=img.getPixels().getSizeT();
				PositiveInteger dimC=img.getPixels().getSizeC();
				PixelType type=img.getPixels().getType();
				Time timeInc=img.getPixels().getTimeIncrement();
				Timestamp stamp=img.getAcquisitionDate();
				Length pixelSizeX=img.getPixels().getPhysicalSizeX();
				Length pixelSizeY=img.getPixels().getPhysicalSizeY();
//				//TODO stagePos,wellNr,expRef
				Pixels p=image.getPixels();
				
				if(overwrite){
					if(!name.equals("")) image.setName(name);
					if(!dimX.toString().equals("")) p.setSizeX(dimX);
					if(!dimY.toString().equals("")) p.setSizeX(dimY);
					if(!dimZ.toString().equals("")) p.setSizeX(dimZ);
					if(!dimT.toString().equals("")) p.setSizeX(dimT);
					if(!dimC.toString().equals("")) p.setSizeX(dimC);
					if(!type.toString().equals("")) p.setType(type);
					//TODO test ifEmpty
					if(timeInc!=null) p.setTimeIncrement(timeInc);
					if(stamp!=null) image.setAcquisitionDate(stamp);
					if(pixelSizeX!=null) p.setPhysicalSizeX(pixelSizeX);
					if(pixelSizeY!=null) p.setPhysicalSizeY(pixelSizeY);
					
					LOGGER.info("[DATA] overwrite IMAGE data");
				}else{
					
					if(image.getName()==null || image.getName().equals(""))
						image.setName(name);
					if(p.getSizeX() ==null || p.getSizeX().equals(""))
						p.setSizeX(dimX);
					if(p.getSizeY() ==null || p.getSizeY().equals(""))
						p.setSizeY(dimY);
					if(p.getSizeZ() ==null || p.getSizeZ().equals(""))
						p.setSizeZ(dimZ);
					if(p.getSizeT() ==null || p.getSizeT().equals(""))
						p.setSizeT(dimT);
					if(p.getSizeC() ==null || p.getSizeC().equals(""))
						p.setSizeC(dimC);
					if(p.getType()==null ) p.setType(type);
					if(image.getAcquisitionDate()==null) image.setAcquisitionDate(stamp);
					if(p.getTimeIncrement()==null) p.setTimeIncrement(timeInc);
					if(p.getPhysicalSizeX()==null) p.setPhysicalSizeX(pixelSizeX);
					if(p.getPhysicalSizeY()==null) p.setPhysicalSizeY(pixelSizeY);
					LOGGER.info("[DATA] complete IMAGE data");
				}
			}
		}else if(img!=null){
			LOGGER.info("[DATA] add IMAGE data");
			image=img;
		}
			
		setGUIData();
		return conflicts;
	}
	
	
	private void setGUIData() 
	{
		if(image!=null){ 
			try{setName(image.getName(),ElementsCompUI.REQUIRED);
			} catch (NullPointerException e) { }

			try{
				String[] dimXY={image.getPixels().getSizeX().toString(),
						image.getPixels().getSizeY().toString()};
				setDimXY(dimXY,ElementsCompUI.REQUIRED);
			} catch (NullPointerException e) { }

			try{setPixelType(image.getPixels().getType(),ElementsCompUI.REQUIRED);
			} catch (NullPointerException e) { }

			try{ 
				String[] dimZTC={image.getPixels().getSizeZ().toString(),
					image.getPixels().getSizeT().toString(),
					image.getPixels().getSizeC().toString()};
				setDimZTC(dimZTC,ElementsCompUI.REQUIRED);
			} catch (NullPointerException e) { }
			//TODO

			try{setStagePos(new String[2], ElementsCompUI.REQUIRED);
			} catch (NullPointerException e) { }

			try{setTimeIncrement(image.getPixels().getTimeIncrement(), ElementsCompUI.REQUIRED);
			} catch (NullPointerException e) { }
			//TODO wellsample

			try{setWellNr(null, ElementsCompUI.REQUIRED);
			} catch (NullPointerException e) { }

			try{ setAcqTime(image.getAcquisitionDate(),ElementsCompUI.REQUIRED);
			} catch (NullPointerException e) { }

			try{ setPixelSizeXY(image.getPixels().getPhysicalSizeX(),image.getPixels().getPhysicalSizeY(),
					ElementsCompUI.REQUIRED);
			} catch (NullPointerException e) { }
		}
	}

	private void initGUI()
	{
		setLayout(new BorderLayout(5,5));
		buildComp=false;
		labels= new ArrayList<JLabel>();
		comp = new ArrayList<JComponent>();
		
		gridbag = new GridBagLayout();
		c = new GridBagConstraints();
		globalPane=new JPanel();
		globalPane.setLayout(gridbag);
		
		add(new TitledSeparator("Image", 3, TitledBorder.DEFAULT_POSITION, true),BorderLayout.NORTH);
		add(globalPane,BorderLayout.NORTH);
		
		setBorder(
//				BorderFactory.createCompoundBorder(	new MyTitledBorder("Objective"),
						BorderFactory.createEmptyBorder(10,10,10,10));
	}
	
	private void readGUIInput() throws Exception
	{
		if(image==null){
			createNewElement();
		}
		
		image.setName(name.getTagValue());
		image.setAcquisitionDate(acqTime.getTagValue().equals("") ? 
				null : Timestamp.valueOf(acqTime.getTagValue()));
		
		image.getPixels().setSizeX(dimXY.getTagValue(0).equals("") ?
				null : PositiveInteger.valueOf(dimXY.getTagValue(0)));
		image.getPixels().setSizeY(dimXY.getTagValue(1).equals("") ?
				null : PositiveInteger.valueOf(dimXY.getTagValue(1)));
		
		image.getPixels().setType(pixelType.getTagValue().equals("") ?
				null : PixelType.fromString(pixelType.getTagValue()));
		
		image.getPixels().setPhysicalSizeX(pixelSize.getTagValue(0).equals("") ?
				null : new Length(Double.valueOf(pixelSize.getTagValue(0)),sizeUnit));
		image.getPixels().setPhysicalSizeY(pixelSize.getTagValue(1).equals("") ?
				null : new Length(Double.valueOf(pixelSize.getTagValue(1)),sizeUnit));
		
		image.getPixels().setSizeZ(dimZTC.getTagValue(0).equals("")?
				null : PositiveInteger.valueOf(dimZTC.getTagValue(0)));
		image.getPixels().setSizeT(dimZTC.getTagValue(1).equals("")?
				null : PositiveInteger.valueOf(dimZTC.getTagValue(1)));
		image.getPixels().setSizeC(dimZTC.getTagValue(2).equals("")?
				null : PositiveInteger.valueOf(dimZTC.getTagValue(2)));
		
		
		image.getPixels().setTimeIncrement(timeIncrement.getTagValue().equals("")?
				null : new Time(Double.valueOf(timeIncrement.getTagValue()),timeUnit));
		
		//TODO: stagePos,wellNr,stepSize

//		image.setObjectiveSettings(objectiveSett.getData());
		
	}
	
	public Image getData() throws Exception
	{
		if(userInput())
			readGUIInput();
		return image;
	}
	
	
	public void buildComponents() 
	{
		labels.clear();
		comp.clear();
		addTagToGUI(name);
		addTagToGUI(acqTime);
		addTagToGUI(dimXY);
		addTagToGUI(pixelType);
		addTagToGUI(pixelSize);
		addTagToGUI(dimZTC);
		addTagToGUI(stepSize);
		addTagToGUI(timeIncrement);
		addTagToGUI(stagePos);
		addTagToGUI(wellNr);
		
		addLabelTextRows(labels, comp, gridbag, globalPane);
		
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
	public void createDummyPane(boolean inactive) 
	{
		setName(null,ElementsCompUI.OPTIONAL);
		setAcqTime(null, ElementsCompUI.OPTIONAL);
		setDimXY(new String[2], ElementsCompUI.OPTIONAL);
		setPixelType(null, ElementsCompUI.OPTIONAL);
		setPixelSizeXY(null, null, ElementsCompUI.OPTIONAL);
		setDimZTC(new String[3], ElementsCompUI.OPTIONAL);
		setStagePos(new String[2], ElementsCompUI.OPTIONAL);
		setStepSize(null,ElementsCompUI.OPTIONAL);
		setTimeIncrement(null, ElementsCompUI.OPTIONAL);
		setWellNr(null, ElementsCompUI.OPTIONAL);
		
		if(inactive){
			name.setEnable(false);
			acqTime.setEnable(false);
			dimXY.setEnable(false);
			pixelSize.setEnable(false);
			pixelType.setEnable(false);
			dimZTC.setEnable(false);
			stagePos.setEnable(false);
			stepSize.setEnable(false);
			timeIncrement.setEnable(false);
			wellNr.setEnable(false);
		}
	}
	
	public void createDummyPane(List<TagConfiguration> list,boolean inactive) 
	{
		if(list==null)
			createDummyPane(inactive);
		else{
			clearDataValues();
			if(image==null && list!=null && list.size()>0)
				createNewElement();
			for(int i=0; i<list.size();i++){
				TagConfiguration t=list.get(i);
				String name=t.getName();
				String val=t.getValue();
				boolean prop=t.getProperty()!= null ? Boolean.parseBoolean(t.getProperty()):
					OPTIONAL;
				if(name!=null){
					switch (name) {
					case L_NAME:
						setName(null,prop);
						this.name.setVisible(true);
						break;
					case L_ACQTIME:
						setAcqTime(null, prop);
						acqTime.setVisible(true);
						break;
					case L_DIM:
						setDimXY(new String[2], prop);
						dimXY.setVisible(true);
						break;
					case L_PIXELTYPE:
						setPixelType(null, prop);
						pixelType.setVisible(true);
						break;
					case L_PIXELSIZE:
						setPixelSizeXY(null, null, prop);
						pixelSize.setVisible(true);
						break;
					case L_DIMZTC:
						setDimZTC(new String[3], prop);
						dimZTC.setVisible(true);
						break;
					case L_STAGEPOS:
						setStagePos(new String[2], prop);
						stagePos.setVisible(true);
						break;
					case L_STEPSIZE:
						setStepSize(null, prop);
						stepSize.setVisible(true);
						break;
					case L_TIMEINC:
						setTimeIncrement(null, prop);
						timeIncrement.setVisible(true);
						break;
					case L_WELLNR:
						setWellNr(null, prop);
						wellNr.setVisible(true);
						break;
					default:
						LOGGER.warning("[CONF] unknown tag: "+name );break;
					}
				}
			}
		}
	}

	public void clearDataValues() 
	{
		clearTagValue(name);
		clearTagValue(acqTime);
		clearTagValue(dimXY);
		clearTagValue(pixelType);
		clearTagValue(pixelSize);
		clearTagValue(dimZTC);
		clearTagValue(stagePos);
		clearTagValue(stepSize);
		clearTagValue(timeIncrement);
		clearTagValue(wellNr);
		
		
	}
	
	public void setName(String value, boolean prop)
	{
		if(name == null) 
			name = new TagData("Name: ",value,prop,TagData.TEXTFIELD);
		else 
			name.setTagValue(value,prop);
	}
	//Datums- und Zeitfeld
	public void setAcqTime(Timestamp value, boolean prop)
	{
		String val= (value != null) ? value.getValue():"";
		if(acqTime == null) 
			acqTime = new TagData("Acquisition Time: ",val,prop,TagData.TIMESTAMP);
		else 
			acqTime.setTagValue(val,prop);
	}
	public void setDimXY(String[] value, boolean prop)
	{
		if(dimXY == null) 
			dimXY = new TagData("Dimension (XY): ",value,prop,TagData.ARRAYFIELDS);
		else{ 
			dimXY.setTagValue(value[0],0,prop);
			dimXY.setTagValue(value[1],1,prop);
		}
	}
	public void setPixelType(PixelType value, boolean prop)
	{
		String val= (value != null) ? value.getValue():"";
		if(pixelType == null) 
			pixelType = new TagData("Pixel Depth: ",val,prop,TagData.TEXTFIELD);
		else 
			pixelType.setTagValue(val,prop);
	}
	public void setPixelSizeXY(Length valueX, Length valueY, boolean prop)
	{
		String valX = (valueX != null) ? String.valueOf(valueX.value()) : "";
		String valY = (valueY != null) ? String.valueOf(valueY.value()) : "";
		sizeUnit=(valueX!=null) ? valueX.unit() : sizeUnit;
		String[] val= {valX,valY};
		if(pixelSize == null) 
			pixelSize = new TagData("Pixel Size (XY)["+sizeUnit.getSymbol()+"]: ",val,prop,TagData.ARRAYFIELDS);
		else {
			pixelSize.setTagValue(valX,0,prop);
			pixelSize.setTagValue(valY,1,prop);
		}
	}
	public void setDimZTC(String[] value, boolean prop)
	{
		if(dimZTC == null) 
			dimZTC = new TagData("Dim Z x T x C: ",value,prop,TagData.ARRAYFIELDS);
		else{ 
			dimZTC.setTagValue(value[0],0,prop);
			dimZTC.setTagValue(value[1],1,prop);
			dimZTC.setTagValue(value[2],2,prop);
		}
	}
	
	//TODO
	public void setStagePos(String[] value, boolean prop)
	{
		if(stagePos == null) 
			stagePos = new TagData("Stage Posistion (XY): ",value,prop,TagData.ARRAYFIELDS);
		else {
			stagePos.setTagValue(value[0],0,prop);
			stagePos.setTagValue(value[1],1,prop);
		}
	}
	

	//unit field
		public void setStepSize(String value, boolean prop)
		{
			if(stepSize == null) 
				stepSize = new TagData("Step Size: ",value,prop,TagData.TEXTFIELD);
			else 
				stepSize.setTagValue(value,prop);
		}
	/**
	 * used for time series that have a global
timing specification instead of per-timepoint timing info.
For example in a video stream.
	 * @param value
	 * @param prop
	 */
	//unit field
	public void setTimeIncrement(Time value, boolean prop)
	{
		String val= (value != null) ? String.valueOf(value.value()) :"";
		timeUnit=(value!=null) ? value.unit() : timeUnit;
		if(timeIncrement == null) 
			timeIncrement = new TagData("Time Increment ["+timeUnit.getSymbol()+"]: ",val,prop,TagData.TEXTFIELD);
		else 
			timeIncrement.setTagValue(val,prop);
	}
	public void setWellNr(String value, boolean prop)
	{
		if(wellNr == null) 
			wellNr = new TagData("Well #: ",value,prop,TagData.TEXTFIELD);
		else 
			wellNr.setTagValue(value,prop);
	}
	
	
	

	public List<TagData> getActiveTags() 
	{
		List<TagData> list = new ArrayList<TagData>();
		if(isActive(name)) list.add(name);
		if(isActive(acqTime)) list.add(acqTime);
		if(isActive(dimXY)) list.add(dimXY);
		if(isActive(pixelType)) list.add(pixelType);
		if(isActive(pixelSize)) list.add(pixelSize);
		if(isActive(dimZTC)) list.add(dimZTC);
		if(isActive(stepSize)) list.add(stepSize);
		if(isActive(timeIncrement)) list.add(timeIncrement);
		if(isActive(stagePos)) list.add(stagePos);
		if(isActive(wellNr)) list.add(wellNr);
		
		return list;
	}

	

}
