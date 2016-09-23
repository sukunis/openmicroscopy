package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model;

import org.slf4j.LoggerFactory;

import ome.units.quantity.Pressure;
import ome.units.quantity.Temperature;
import ome.xml.model.ImagingEnvironment;
import ome.xml.model.primitives.PercentFraction;

public class ImageEnvModel 
{
	 private static final org.slf4j.Logger LOGGER =
	    	    LoggerFactory.getLogger(ImageEnvModel.class);
	 
	ImagingEnvironment element;
	
	public ImageEnvModel()
	{
		element=new ImagingEnvironment();
	}
	
	public ImageEnvModel(ImageEnvModel orig)
	{
		element=orig.element;
	}
	
	public boolean addData(ImagingEnvironment img, boolean overwrite)
	{
		boolean conflicts=false;
		if(overwrite){
			replaceData(img);
			LOGGER.info("[DATA] -- replace IMAGE_ENV data");
		}else
			try {
				completeData(img);
				LOGGER.info("[DATA] -- complete IMAGE_ENV data");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return conflicts;
	}

	private void replaceData(ImagingEnvironment i)
	{
		if(i!=null){
			element=i;
		}
	}

	private void completeData(ImagingEnvironment i) throws Exception
	{
		//copy input fields
		ImagingEnvironment copyIn=null;
		if(element!=null){
			copyIn=new ImagingEnvironment(element);
		}

		replaceData(i);

		// set input field values again
		if(copyIn!=null){
			Temperature t=copyIn.getTemperature();
			Pressure p=copyIn.getAirPressure();
			PercentFraction h=copyIn.getHumidity();
			PercentFraction co=copyIn.getCO2Percent();
				
			if(t!=null) element.setTemperature(t);
			if(p!=null) element.setAirPressure(p);
			if(h!=null) element.setHumidity(h);
			if(co!=null) element.setCO2Percent(co);
		}
	}

	
	public ImagingEnvironment getImgEnv()
	{
		return element;
	}
}
