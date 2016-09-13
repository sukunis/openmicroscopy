package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.ModuleConfiguration;

import ome.units.quantity.Power;
import ome.xml.model.Arc;
import ome.xml.model.LightSource;
import ome.xml.model.enums.ArcType;

public class LS_ArcCompUI //extends LightSourceCompUI
{

//	public LS_ArcCompUI(LightSource _ls, int _linkChannelIdx) 
//	{
//		super(_ls, _linkChannelIdx);
//	}
//	
//	public LS_ArcCompUI(ModuleConfiguration objConf)
//	{
//		super(objConf);
//	}
//
//	private void addDataArc(LightSource l,boolean overwrite) 
//	{
//		String mo=l.getModel();
//		String ma=l.getManufacturer();
//		Power p=l.getPower();
//		ArcType t=((Arc)l).getType();
//		if(lightSrc!=null){
//			if(overwrite){
//				
//				if(mo!=null && !mo.equals("")) lightSrc.setModel(mo);
//				if(ma!=null && !ma.equals("")) lightSrc.setManufacturer(ma);
//				if(p!=null) lightSrc.setPower(p);
//				if(t!=null) ((Arc) lightSrc).setType(t);
//				LOGGER.info("[DATA] overwrite LIGHTSOURCE data");
//			}else{
//				if(lightSrc.getManufacturer()==null)
//					lightSrc.setManufacturer(ma);
//				if(lightSrc.getModel()==null)
//					lightSrc.setModel(mo);
//				if(lightSrc.getPower()==null)
//					lightSrc.setPower(p);
//				if(((Arc) lightSrc).getType()==null)
//					if(t!=null) ((Arc) lightSrc).setType(t);
//				LOGGER.info("[DATA] complete LIGHTSOURCE data");
//			}
//		}else {
//			lightSrc=l;
//			LOGGER.info("[DATA] add LIGHTSOURCE data");
//		}
//	}
}
