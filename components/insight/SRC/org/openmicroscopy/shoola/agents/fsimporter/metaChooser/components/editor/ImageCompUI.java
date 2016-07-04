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
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.TagNames;

import ome.units.UNITS;
import ome.units.quantity.Length;
import ome.units.quantity.Time;
import ome.units.unit.Unit;
import ome.xml.model.Channel;
import ome.xml.model.Image;
import ome.xml.model.Pixels;
import ome.xml.model.StageLabel;
import ome.xml.model.enums.PixelType;
import ome.xml.model.primitives.PositiveInteger;
import ome.xml.model.primitives.Timestamp;

public class ImageCompUI extends ElementsCompUI
{
	
	
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
	
	
	private Image image;
	private boolean setFields;
	
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
		return (result || setFields);
	}
	

	
	public ImageCompUI(ModuleConfiguration objConf)
	{
		initGUI();
		if(objConf==null)
			createDummyPane(false);
		else
			createDummyPane(objConf.getTagList(),false);
	}
	
//	public ImageCompUI(Image _image,int i)
//	{
//		image=_image;
//		
//		sizeUnit=UNITS.MICROM;
//		timeUnit=UNITS.SECOND;
//		
//		initGUI();
//		if(image!=null)
//			setGUIData();
//		else{
//			createNewElement();
//			createDummyPane(false);
//		}
//	}
	
	private void createNewElement()
	{
		image=new Image();
		image.setPixels(new Pixels());
	}
	
//	public boolean addData(Image img, boolean overwrite)
//	{
//		boolean conflicts=false;
//		if(image!=null){
//			if(img!=null){
//				String name=img.getName();
//				PositiveInteger dimX=img.getPixels().getSizeX();
//				PositiveInteger dimY=img.getPixels().getSizeY();
//				PositiveInteger dimZ=img.getPixels().getSizeZ();
//				PositiveInteger dimT=img.getPixels().getSizeT();
//				PositiveInteger dimC=img.getPixels().getSizeC();
//				PixelType type=img.getPixels().getType();
//				Time timeInc=img.getPixels().getTimeIncrement();
//				Timestamp stamp=img.getAcquisitionDate();
//				Length pixelSizeX=img.getPixels().getPhysicalSizeX();
//				Length pixelSizeY=img.getPixels().getPhysicalSizeY();
////				//TODO stagePos,wellNr,expRef
//				Pixels p=image.getPixels();
//				
//				if(overwrite){
//					if(img.getID()!=null && !img.getID().equals(""))
//						image.setID(img.getID());
//					if(name!=null && !name.equals("")) image.setName(name);
//					if(dimX!=null && !dimX.toString().equals("")) p.setSizeX(dimX);
//					if(dimY!=null && !dimY.toString().equals("")) p.setSizeX(dimY);
//					if(dimZ!=null && !dimZ.toString().equals("")) p.setSizeX(dimZ);
//					if(dimT!=null && !dimT.toString().equals("")) p.setSizeX(dimT);
//					if(dimC!=null && !dimC.toString().equals("")) p.setSizeX(dimC);
//					if(type!=null && !type.toString().equals("")) p.setType(type);
//					//TODO test ifEmpty
//					if(timeInc!=null) p.setTimeIncrement(timeInc);
//					if(stamp!=null) image.setAcquisitionDate(stamp);
//					if(pixelSizeX!=null) p.setPhysicalSizeX(pixelSizeX);
//					if(pixelSizeY!=null) p.setPhysicalSizeY(pixelSizeY);
//					
//					LOGGER.info("[DATA] overwrite IMAGE data");
//				}else{
//					if(image.getID()==null || image.getID().equals(""))
//						image.setID(img.getID());
//					if(image.getName()==null || image.getName().equals(""))
//						image.setName(name);
//					if(p.getSizeX() ==null || p.getSizeX().equals(""))
//						p.setSizeX(dimX);
//					if(p.getSizeY() ==null || p.getSizeY().equals(""))
//						p.setSizeY(dimY);
//					if(p.getSizeZ() ==null || p.getSizeZ().equals(""))
//						p.setSizeZ(dimZ);
//					if(p.getSizeT() ==null || p.getSizeT().equals(""))
//						p.setSizeT(dimT);
//					if(p.getSizeC() ==null || p.getSizeC().equals(""))
//						p.setSizeC(dimC);
//					if(p.getType()==null ) p.setType(type);
//					if(image.getAcquisitionDate()==null) image.setAcquisitionDate(stamp);
//					if(p.getTimeIncrement()==null) p.setTimeIncrement(timeInc);
//					if(p.getPhysicalSizeX()==null) p.setPhysicalSizeX(pixelSizeX);
//					if(p.getPhysicalSizeY()==null) p.setPhysicalSizeY(pixelSizeY);
//					LOGGER.info("[DATA] complete IMAGE data");
//				}
//			}
//		}else if(img!=null){
//			LOGGER.info("[DATA] add IMAGE data");
//			image=img;
//		}
//			
//		setGUIData();
//		return conflicts;
//	}
	

	public boolean addData(Image img, boolean overwrite)
	{
		boolean conflicts=false;
		if(overwrite){
			replaceData(img);
			LOGGER.info("[DATA] -- replace IMAGE data");
		}else
			try {
				completeData(img);
				LOGGER.info("[DATA] -- complete IMAGE data");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		setGUIData();
		return conflicts;
	}
	
	private void replaceData(Image i)
	{
		if(i!=null){
			image=i;
			
		}
	}
	
	private void completeData(Image i) throws Exception
	{
		//copy input fields
		Image copyIn=null;
		if(image!=null){
			getData();
			copyIn=new Image(image);
		}

		replaceData(i);

		// set input field values again
		if(copyIn!=null){
			String name=copyIn.getName();
			PositiveInteger dimX=copyIn.getPixels().getSizeX();
			PositiveInteger dimY=copyIn.getPixels().getSizeY();
			PositiveInteger dimZ=copyIn.getPixels().getSizeZ();
			PositiveInteger dimT=copyIn.getPixels().getSizeT();
			PositiveInteger dimC=copyIn.getPixels().getSizeC();
			PixelType type=copyIn.getPixels().getType();
			Time timeInc=copyIn.getPixels().getTimeIncrement();
			Timestamp stamp=copyIn.getAcquisitionDate();
			Length pixelSizeX=copyIn.getPixels().getPhysicalSizeX();
			Length pixelSizeY=copyIn.getPixels().getPhysicalSizeY();
//			//TODO wellNr,expRef
			StageLabel stageLabel=copyIn.getStageLabel();
			Pixels p=image.getPixels();
			
			if(name!=null && !name.equals("")) image.setName(name);
			if(dimX!=null && !dimX.toString().equals("")) p.setSizeX(dimX);
			if(dimY!=null && !dimY.toString().equals("")) p.setSizeX(dimY);
			if(dimZ!=null && !dimZ.toString().equals("")) p.setSizeX(dimZ);
			if(dimT!=null && !dimT.toString().equals("")) p.setSizeX(dimT);
			if(dimC!=null && !dimC.toString().equals("")) p.setSizeX(dimC);
			if(type!=null && !type.toString().equals("")) p.setType(type);
			//TODO test ifEmpty
			if(timeInc!=null) p.setTimeIncrement(timeInc);
			if(stamp!=null) image.setAcquisitionDate(stamp);
			if(pixelSizeX!=null) p.setPhysicalSizeX(pixelSizeX);
			if(pixelSizeY!=null) p.setPhysicalSizeY(pixelSizeY);
			if(stageLabel!=null) image.setStageLabel(stageLabel);
			
			
		}
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

			try{
				StageLabel stage=image.getStageLabel();
				setStagePos(stage.getX(),stage.getY(), ElementsCompUI.REQUIRED);
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
		
		try{
			image.setName(name.getTagValue());
		}catch(Exception e){
			LOGGER.error("[DATA] can't read IMAGE name input");
		}
		try{
			image.setAcquisitionDate(acqTime.getTagValue().equals("") ? 
					null : Timestamp.valueOf(acqTime.getTagValue()));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read IMAGE acquisition date input");
		}
		try{
			image.getPixels().setSizeX(dimXY.getTagValue(0).equals("") ?
					null : PositiveInteger.valueOf(dimXY.getTagValue(0)));
		
			image.getPixels().setSizeY(dimXY.getTagValue(1).equals("") ?
					null : PositiveInteger.valueOf(dimXY.getTagValue(1)));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read IMAGE dimension x,y input");
		}
		try{
			image.getPixels().setType(pixelType.getTagValue().equals("") ?
					null : PixelType.fromString(pixelType.getTagValue()));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read IMAGE pixel type input");
		}
		try{
			image.getPixels().setPhysicalSizeX(parseToLength(pixelSize.getTagValue(0),pixelSize.getTagUnit()));
			image.getPixels().setPhysicalSizeY(parseToLength(pixelSize.getTagValue(1),pixelSize.getTagUnit()));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read IMAGE pixel size input");
		}
		try{

			image.getPixels().setSizeZ(dimZTC.getTagValue(0).equals("")?
					null : PositiveInteger.valueOf(dimZTC.getTagValue(0)));
			image.getPixels().setSizeT(dimZTC.getTagValue(1).equals("")?
					null : PositiveInteger.valueOf(dimZTC.getTagValue(1)));
			image.getPixels().setSizeC(dimZTC.getTagValue(2).equals("")?
					null : PositiveInteger.valueOf(dimZTC.getTagValue(2)));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read IMAGE dimension z,t,c input");
		}
		try{
			image.getPixels().setTimeIncrement(timeIncrement.getTagValue().equals("")?
					null : new Time(Double.valueOf(timeIncrement.getTagValue()),timeIncrement.getTagUnit()));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read IMAGE time increment input");
		}
		
		//TODO: wellNr,stepSize
		try {
			image.getStageLabel().setX(parseToLength(stagePos.getTagValue(0),stagePos.getTagUnit()));
			image.getStageLabel().setY(parseToLength(stagePos.getTagValue(1),stagePos.getTagUnit()));
		} catch (Exception e) {
			LOGGER.error("[DATA] can't read IMAGE stage position input");
		}
	}
	
	
	public static void mergeData(Image in, Image imageOME) 
	{
		if(imageOME==null ){
			if(in==null){
				LOGGER.error("failed to merge IMAGE data");
			}else{
				imageOME=in;
			}
			return;
		}else if(in==null){
			return;
		}
		
		if(in.getName()!=null && !in.getName().equals(""))
			imageOME.setName(in.getName());
		imageOME.setAcquisitionDate(in.getAcquisitionDate());
		
		Pixels pOME=imageOME.getPixels();
		Pixels pIN=in.getPixels();
		
		pOME.setSizeX(pIN.getSizeX());
		pOME.setSizeY(pIN.getSizeY());
		pOME.setType(pIN.getType());
		pOME.setPhysicalSizeX(pIN.getPhysicalSizeX());
		pOME.setPhysicalSizeY(pIN.getPhysicalSizeY());
		pOME.setSizeZ(pIN.getSizeZ());
		pOME.setSizeT(pIN.getSizeT());
		pOME.setSizeC(pIN.getSizeC());
		pOME.setTimeIncrement(pIN.getTimeIncrement());
		
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
		if(stepSize!=null) stepSize.setEnable(false);
		addTagToGUI(timeIncrement);
		addTagToGUI(stagePos);
//		if(stagePos!=null) stagePos.setEnable(false);
		addTagToGUI(wellNr);
		if(wellNr!=null) wellNr.setEnable(false);
		
		addLabelTextRows(labels, comp, gridbag, globalPane);
		
		c.gridwidth = GridBagConstraints.REMAINDER; //last
		c.anchor = GridBagConstraints.WEST;
		c.weightx = 1.0;
		
		buildComp=true;
		initTagList();
		setFields=false;
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
		setStagePos(null,null, ElementsCompUI.OPTIONAL);
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
//			if(image==null && list!=null && list.size()>0)
//				createNewElement();
			for(int i=0; i<list.size();i++){
				TagConfiguration t=list.get(i);
				String name=t.getName();
				String val=t.getValue();
				boolean prop=t.getProperty()!= null ? Boolean.parseBoolean(t.getProperty()):
					OPTIONAL;
				if(name!=null && t.isVisible()){
					switch (name) {
					case TagNames.IMG_NAME:
						setName(null,prop);
						this.name.setVisible(true);
						break;
					case TagNames.ACQTIME:
						setAcqTime(null, prop);
						acqTime.setVisible(true);
						break;
					case TagNames.DIMXY:
						setDimXY(new String[2], prop);
						dimXY.setVisible(true);
						break;
					case TagNames.PIXELTYPE:
						setPixelType(null, prop);
						pixelType.setVisible(true);
						break;
					case TagNames.PIXELSIZE:
						setPixelSizeXY(null, null, prop);
						pixelSize.setVisible(true);
						break;
					case TagNames.DIMZTC:
						setDimZTC(new String[3], prop);
						dimZTC.setVisible(true);
						break;
					case TagNames.STAGEPOS:
						setStagePos(null,null, prop);
						stagePos.setVisible(true);
						break;
					case TagNames.STEPSIZE:
						setStepSize(null, prop);
						stepSize.setVisible(true);
						break;
					case TagNames.TIMEINC:
						setTimeIncrement(null, prop);
						timeIncrement.setVisible(true);
						break;
					case TagNames.WELLNR:
						setWellNr(null, prop);
						wellNr.setVisible(true);
						break;
					default:
						LOGGER.warn("[CONF] unknown tag: "+name );break;
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
			name = new TagData(TagNames.IMG_NAME+": ",value,prop,TagData.TEXTFIELD);
		else 
			name.setTagValue(value,prop);
	}
	//Datums- und Zeitfeld
	public void setAcqTime(Timestamp value, boolean prop)
	{
		String val= (value != null) ? value.getValue():"";
		if(acqTime == null) 
			acqTime = new TagData(TagNames.ACQTIME+": ",val,prop,TagData.TIMESTAMP);
		else 
			acqTime.setTagValue(val,prop);
	}
	public void setDimXY(String[] value, boolean prop)
	{
		if(dimXY == null) 
			dimXY = new TagData(TagNames.DIMXY+": ",value,prop,TagData.ARRAYFIELDS);
		else{ 
			dimXY.setTagValue(value[0],0,prop);
			dimXY.setTagValue(value[1],1,prop);
		}
	}
	public void setPixelType(PixelType value, boolean prop)
	{
		String val= (value != null) ? value.getValue():"";
		if(pixelType == null) 
			pixelType = new TagData(TagNames.PIXELTYPE+": ",val,prop,TagData.TEXTFIELD);
		else 
			pixelType.setTagValue(val,prop);
	}
	public void setPixelSizeXY(Length valueX, Length valueY, boolean prop)
	{
		String valX = (valueX != null) ? String.valueOf(valueX.value()) : "";
		String valY = (valueY != null) ? String.valueOf(valueY.value()) : "";
		Unit unit=(valueX!=null) ? valueX.unit() : TagNames.PIXELSIZE_UNIT;
		String[] val= {valX,valY};
		if(pixelSize == null) 
			pixelSize = new TagData(TagNames.PIXELSIZE+"["+unit.getSymbol()+"]: ",val,prop,TagData.ARRAYFIELDS);
		else {
			pixelSize.setTagValue(valX,0,prop);
			pixelSize.setTagValue(valY,1,prop);
		}
		pixelSize.setTagUnit(unit);
	}
	public void setDimZTC(String[] value, boolean prop)
	{
		if(dimZTC == null) 
			dimZTC = new TagData(TagNames.DIMZTC+": ",value,prop,TagData.ARRAYFIELDS);
		else{ 
			dimZTC.setTagValue(value[0],0,prop);
			dimZTC.setTagValue(value[1],1,prop);
			dimZTC.setTagValue(value[2],2,prop);
		}
	}
	
	//TODO
	public void setStagePos(Length valueX, Length valueY, boolean prop)
	{
		String valX = (valueX != null) ? String.valueOf(valueX.value()) : "";
		String valY = (valueY != null) ? String.valueOf(valueY.value()) : "";
		Unit unit=(valueX!=null) ? valueX.unit() : TagNames.STAGEPOS_UNIT;
		String symbol = unit==UNITS.REFERENCEFRAME ? "rf" : unit.getSymbol();
		String[] val= {valX,valY};
		if(stagePos == null){ 
			stagePos = new TagData(TagNames.STAGEPOS+"["+symbol+"]: ",val,prop,TagData.ARRAYFIELDS);
		}else {
			stagePos.setTagValue(valX,0,prop);
			stagePos.setTagValue(valY,1,prop);
		}
		stagePos.setTagUnit(unit);
	}
	

	//unit field
		public void setStepSize(String value, boolean prop)
		{
			if(stepSize == null) 
				stepSize = new TagData(TagNames.STEPSIZE+": ",value,prop,TagData.TEXTFIELD);
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
		Unit unit=(value!=null) ? value.unit() : TagNames.TIMEINC_UNIT;
		if(timeIncrement == null) 
			timeIncrement = new TagData(TagNames.TIMEINC,val,unit,prop,TagData.TEXTFIELD);
		else 
			timeIncrement.setTagValue(val,unit,prop);
	}
	public void setWellNr(String value, boolean prop)
	{
		if(wellNr == null) 
			wellNr = new TagData(TagNames.WELLNR+": ",value,prop,TagData.TEXTFIELD);
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

	public void setFieldsExtern(boolean b) {
		setFields= setFields || b;		
	}

	

	

}
