package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model;

import java.util.ArrayList;
import java.util.List;

import ome.units.quantity.Length;
import ome.units.unit.Unit;
import ome.xml.model.Channel;
import ome.xml.model.Detector;
import ome.xml.model.DetectorSettings;
import ome.xml.model.enums.AcquisitionMode;
import ome.xml.model.enums.ContrastMethod;
import ome.xml.model.enums.EnumerationException;
import ome.xml.model.enums.IlluminationType;
import ome.xml.model.primitives.Color;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.view.ChannelViewer;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.view.ModuleViewer;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.TagNames;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagData;
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
		if(index>=element.size())
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
			element.set(i, new Channel(c));
			
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

	public void remove(int index) 
	{
		if(element!=null && !element.isEmpty())
			element.remove(index);
	}

	/**
	 * Update list of channels with given modified tags.
	 * Do nothing if channel at index doesn't exist. 
	 * @param changesChannel
	 * @throws Exception
	 */
	public void update(List<List<TagData>> changesChannel) throws Exception
	{
		if(changesChannel==null)
			return;
		int index=0;
		for(List<TagData> list : changesChannel){
			if(list!=null && element.size()>index && element.get(index)!=null){
				Channel ch=element.get(index);
				for(TagData t: list){
					updateTag(ch, t.getTagName(),t.getTagValue(),t.getTagUnit());
				}
			}
			index++;
			
		}
	}

	/**
	 * Update tag of given channel if value!="". 
	 * @param channel
	 * @param tagName
	 * @param tagValue
	 * @param tagUnit
	 * @throws Exception 
	 */
	private void updateTag(Channel channel, String tagName, String tagValue,
			Unit tagUnit) throws Exception 
	{
		if(tagValue.equals(""))
			return;
		
		switch (tagName) 
		{
		case TagNames.CH_NAME:
			channel.setName(tagValue);
			break;
		case TagNames.COLOR:
			channel.setColor(ChannelViewer.parseColor(tagValue));
			break;
		case TagNames.FLUOROPHORE:
			channel.setFluor(tagValue);
			break;
		case TagNames.ILLUMTYPE:
			channel.setIlluminationType(ChannelViewer.parseIllumType(tagValue));
			break;
		case TagNames.EXPOSURETIME:
//TODO			channel.setE
			break;
		case TagNames.EXCITWAVELENGTH:
			channel.setExcitationWavelength(ModuleViewer.parseToLength(tagValue,tagUnit, true));
			break;
		case TagNames.EMISSIONWAVELENGTH:
			channel.setEmissionWavelength(ModuleViewer.parseToLength(tagValue,tagUnit, true));
			break;
		case TagNames.IMAGINGMODE:
			//TODO
			break;
		case TagNames.ILLUMINATIONMODE:
			//TODO
			break;
		case TagNames.CONTRASTMETHOD:
			channel.setContrastMethod(ChannelViewer.parseContrastMethod(tagValue));
			break;
		case TagNames.NDFILTER:
			channel.setNDFilter(ModuleViewer.parseToDouble(tagValue));
			break;
		case TagNames.PINHOLESIZE:
			channel.setPinholeSize(ModuleViewer.parseToLength(tagValue,tagUnit, false));
			break;
		default:
			LOGGER.warn("[CONF] unknown tag: "+tagName );break;
		}
	}
}
