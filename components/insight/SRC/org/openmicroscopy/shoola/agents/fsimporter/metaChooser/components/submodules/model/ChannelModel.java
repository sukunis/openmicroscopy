package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model;

import java.util.ArrayList;
import java.util.List;

import ome.units.quantity.Length;
import ome.xml.model.Channel;
import ome.xml.model.Detector;
import ome.xml.model.DetectorSettings;
import ome.xml.model.enums.AcquisitionMode;
import ome.xml.model.enums.ContrastMethod;
import ome.xml.model.primitives.Color;

import org.slf4j.LoggerFactory;

public class ChannelModel 
{
	private static final org.slf4j.Logger LOGGER =
    	    LoggerFactory.getLogger(ChannelModel.class);
	
	private List<Channel> element;
	
	public ChannelModel()
	{
		element=new ArrayList<Channel>();
	}
	
	public ChannelModel(ChannelModel orig)
	{
		element=orig.element;
	}
	
	public Channel getChannel(int index)
	{
		if(element==null)
			return null;
		
		return element.get(index);
	}
	
	public boolean addData(Channel c, boolean overwrite,int i) 
	{
		if(element.size()<=i){
			expandList(element.size(),i);
		}
		boolean conflicts=false;
		if(overwrite){
			replaceData(c,i);
			LOGGER.info("[DATA] -- replace CHANNEL data");
		}else
			try {
				completeData(c,i);
				LOGGER.info("[DATA] -- complete CHANNEL data");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		return conflicts;
	}

	private void completeData(Channel c,int i) throws Exception
	{
		//copy input fields
		Channel copyIn=null;
		if(element!=null){
			copyIn=new Channel(element.get(i));
		}
		
		replaceData(c,i);
		
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
			
			Channel channel=element.get(i);
			if(c.getID()!=null && !c.getID().equals(""))
				channel.setID(c.getID());
			if(name!=null && !name.equals("")) channel.setName(name);
			if(color!=null) channel.setColor(color);
			if(fluor!=null && !fluor.equals("")) channel.setFluor(fluor);
			if(exW!=null) channel.setExcitationWavelength(exW);
			if(emW!=null) channel.setEmissionWavelength(emW);
			if(aMode!=null) channel.setAcquisitionMode(aMode);
			if(cMethod!=null) channel.setContrastMethod(cMethod);
			if(ndf!=null) channel.setNDFilter(ndf);
		
		}
	}

	private void replaceData(Channel c,int i)
	{
		if(c!=null){
			element.set(i, c);
			
		}
	}
	
	public int getNumberOfChannels()
	{
		if(element==null)
			return 0;
		return element.size();
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
	
	/**
	 * If index exits size, expand elements and settings list
	 * @param size
	 * @param index
	 */
	private void expandList(int size,int index) 
	{
		for(int i=size;i<index+1;i++){
			element.add(new Channel());
		}
	}
}
