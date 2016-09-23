package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model;

import ome.units.quantity.Length;
import ome.xml.model.Channel;
import ome.xml.model.enums.AcquisitionMode;
import ome.xml.model.enums.ContrastMethod;
import ome.xml.model.primitives.Color;

import org.slf4j.LoggerFactory;

public class ChannelModel 
{
	private static final org.slf4j.Logger LOGGER =
    	    LoggerFactory.getLogger(ChannelModel.class);
	
	private Channel element;
	
	public ChannelModel()
	{
	element=new Channel();
	}
	
	public ChannelModel(ChannelModel orig)
	{
		element=orig.element;
	}
	
	public Channel getChannel()
	{
		return element;
	}
	
	public boolean addData(Channel c, boolean overwrite) 
	{
		boolean conflicts=false;
		if(overwrite){
			replaceData(c);
			LOGGER.info("[DATA] -- replace CHANNEL data");
		}else
			try {
				completeData(c);
				LOGGER.info("[DATA] -- complete CHANNEL data");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		return conflicts;
	}

	private void completeData(Channel c) throws Exception
	{
		//copy input fields
		Channel copyIn=null;
		if(element!=null){
			copyIn=new Channel(element);
		}
		
		replaceData(c);
		
		// set input field values again
		if(copyIn!=null){
			//read data
			String name=copyIn.getName();
			Color color=copyIn.getColor();
			String fluor=copyIn.getFluor();
			Length exW=copyIn.getExcitationWavelength();
			Length emW=copyIn.getEmissionWavelength();
			AcquisitionMode aMode=copyIn.getAcquisitionMode();
			ContrastMethod cMethod=copyIn.getContrastMethod();
			Double ndf=copyIn.getNDFilter();
			
			if(c.getID()!=null && !c.getID().equals(""))
				element.setID(c.getID());
			if(name!=null && !name.equals("")) element.setName(name);
			if(color!=null) element.setColor(color);
			if(fluor!=null && !fluor.equals("")) element.setFluor(fluor);
			if(exW!=null) element.setExcitationWavelength(exW);
			if(emW!=null) element.setEmissionWavelength(emW);
			if(aMode!=null) element.setAcquisitionMode(aMode);
			if(cMethod!=null) element.setContrastMethod(cMethod);
			if(ndf!=null) element.setNDFilter(ndf);
		
		}
	}

	private void replaceData(Channel c)
	{
		if(c!=null){
			element=c;
			
		}
	}
	public static void mergeData(Channel in, Channel channelOME)
	{
		if(channelOME==null ){
			if(in==null){
				LOGGER.error("failed to merge CHANNEL data");
			}else{
				channelOME=in;
			}
			return;
		}else if(in==null){
			LOGGER.info("nothing to merge CHANNEL data");
			return;
		}
		
		channelOME.setName(in.getName());
		channelOME.setColor(in.getColor());
		channelOME.setFluor(in.getFluor());
		channelOME.setIlluminationType(in.getIlluminationType());
		channelOME.setExcitationWavelength(in.getExcitationWavelength());
		channelOME.setEmissionWavelength(in.getEmissionWavelength());
		channelOME.setAcquisitionMode(in.getAcquisitionMode());
		channelOME.setContrastMethod(in.getContrastMethod());
		channelOME.setNDFilter(in.getNDFilter());
		
		channelOME.setDetectorSettings(in.getDetectorSettings());
		channelOME.setLightSourceSettings(in.getLightSourceSettings());
		channelOME.setLightPath(in.getLightPath());
	}
}
