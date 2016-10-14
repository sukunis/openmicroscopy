package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model;

import java.util.List;

import ome.units.quantity.Length;
import ome.units.quantity.Time;
import ome.units.unit.Unit;
import ome.xml.model.Detector;
import ome.xml.model.Image;
import ome.xml.model.Pixels;
import ome.xml.model.StageLabel;
import ome.xml.model.enums.EnumerationException;
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

	/**
	 * Update data model for given modified tags.
	 * @param changesImage list of modified tags
	 * @throws Exception
	 */
	public void update(List<TagData> changesImage) throws Exception 
	{
		if(changesImage==null)
			return;
		for(TagData t: changesImage){
			updateTag(t.getTagName(),t.getTagValue(),t.getTagUnit());
		}
	}

	/**
	 * Update tag of this model with given value!="" and unit.
	 * @param tagName
	 * @param value
	 * @param tagUnit
	 * @throws Exception
	 */
	private void updateTag(String tagName, String value, Unit tagUnit) throws Exception 
	{
		// no delete of value possible?
		if(value.equals(""))
			return;
		
		
		switch (tagName) {
		case TagNames.IMG_NAME:
			element.setName(value);
			break;
		case TagNames.ACQTIME:
			element.setAcquisitionDate(Timestamp.valueOf(value));
			break;
		case TagNames.DIMXY:
//			TODO:
//			setDimXY(new String[2], prop);
			break;
		case TagNames.PIXELTYPE:
			element.getPixels().setType(PixelType.fromString(value));
			break;
		case TagNames.PIXELSIZE:
//			TODO:
//			setPixelSizeXY(null, null, prop);
			break;
		case TagNames.DIMZTC:
//			TODO:
//			setDimZTC(new String[3], prop);
			break;
		case TagNames.STAGEPOS:
			//TODO:
//			setStagePos(null,null, prop);
			break;
		case TagNames.STEPSIZE:
			//TODO:
//			setStepSize(null, prop);
			break;
		case TagNames.TIMEINC:
			element.getPixels().setTimeIncrement(new Time(Double.valueOf(value),tagUnit));
			break;
		case TagNames.WELLNR:
			//TODO:
//			setWellNr(null, prop);
			break;
		default:
			LOGGER.warn("[CONF] unknown tag: "+tagName );break;
		}
		
	}



	

}
