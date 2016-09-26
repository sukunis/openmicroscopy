package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model;

import java.util.List;

import ome.units.quantity.Length;
import ome.units.quantity.Time;
import ome.units.unit.Unit;
import ome.xml.model.Detector;
import ome.xml.model.Image;
import ome.xml.model.Pixels;
import ome.xml.model.StageLabel;
import ome.xml.model.enums.PixelType;
import ome.xml.model.primitives.PositiveInteger;
import ome.xml.model.primitives.Timestamp;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.TagNames;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagData;
import org.slf4j.LoggerFactory;

public class ImageModel 
{
	 private static final org.slf4j.Logger LOGGER =
	    	    LoggerFactory.getLogger(ImageModel.class);
	
	private Image element;
	
	public ImageModel()
	{
		element=new Image();
		element.setPixels(new Pixels());
	}
	
	public ImageModel(ImageModel orig)
	{
		element=orig.element;
	}
	
	public boolean addData(Image img, boolean overwrite)
	{
		System.out.println("# ImageModel::addData()");
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
		return conflicts;
	}

	private void replaceData(Image i)
	{
		if(i!=null){
			element=i;

		}
	}

	private void completeData(Image i) throws Exception
	{
		//copy input fields
		Image copyIn=null;
		if(element!=null){
			copyIn=new Image(element);
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
			//		//TODO wellNr,expRef
			StageLabel stageLabel=copyIn.getStageLabel();
			Pixels p=element.getPixels();

			if(name!=null && !name.equals("")) element.setName(name);
			if(dimX!=null && !dimX.toString().equals("")) p.setSizeX(dimX);
			if(dimY!=null && !dimY.toString().equals("")) p.setSizeX(dimY);
			if(dimZ!=null && !dimZ.toString().equals("")) p.setSizeX(dimZ);
			if(dimT!=null && !dimT.toString().equals("")) p.setSizeX(dimT);
			if(dimC!=null && !dimC.toString().equals("")) p.setSizeX(dimC);
			if(type!=null && !type.toString().equals("")) p.setType(type);
			//TODO test ifEmpty
			if(timeInc!=null) p.setTimeIncrement(timeInc);
			if(stamp!=null) element.setAcquisitionDate(stamp);
			if(pixelSizeX!=null) p.setPhysicalSizeX(pixelSizeX);
			if(pixelSizeY!=null) p.setPhysicalSizeY(pixelSizeY);
			if(stageLabel!=null) element.setStageLabel(stageLabel);


		}
	}

	public Image getImage() {
		return element;
	}

//	public void update(List<TagData> changesImage) 
//	{
//		for(TagData t: changesImage){
//			updateTag(t.getTagName(),t.getTagValue(),t.getTagUnit());
//		}
//	}

//	private void updateTag(String tagName, Object value, Unit tagUnit) 
//	{
//		switch (tagName) {
//		case TagNames.IMG_NAME:
//			element.setName(value);
//			break;
//		case TagNames.ACQTIME:
//			element.setAcquisitionDate(Timestamp.valueOf(value));
//			break;
//		case TagNames.DIMXY:
//			setDimXY(new String[2], prop);
//			dimXY.setVisible(true);
//			break;
//		case TagNames.PIXELTYPE:
//			setPixelType(null, prop);
//			pixelType.setVisible(true);
//			break;
//		case TagNames.PIXELSIZE:
//			setPixelSizeXY(null, null, prop);
//			pixelSize.setVisible(true);
//			break;
//		case TagNames.DIMZTC:
//			setDimZTC(new String[3], prop);
//			dimZTC.setVisible(true);
//			break;
//		case TagNames.STAGEPOS:
//			setStagePos(null,null, prop);
//			stagePos.setVisible(true);
//			break;
//		case TagNames.STEPSIZE:
//			setStepSize(null, prop);
//			stepSize.setVisible(true);
//			break;
//		case TagNames.TIMEINC:
//			setTimeIncrement(null, prop);
//			timeIncrement.setVisible(true);
//			break;
//		case TagNames.WELLNR:
//			setWellNr(null, prop);
//			wellNr.setVisible(true);
//			break;
//		default:
//			LOGGER.warn("[CONF] unknown tag: "+name );break;
//		}
//		
//	}



	

}
