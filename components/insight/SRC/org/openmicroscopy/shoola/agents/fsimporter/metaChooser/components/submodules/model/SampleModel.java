package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model;

import ome.xml.model.primitives.Timestamp;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.format.ObservedSample;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.format.Sample;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.format.Sample.GridBox;
import org.slf4j.LoggerFactory;

public class SampleModel 
{
	private static final org.slf4j.Logger LOGGER =
			LoggerFactory.getLogger(SampleModel.class);
	
	private Sample element;
	
	public SampleModel()
	{
		element=new Sample();
	}
	public SampleModel(SampleModel orig)
	{
		element=orig.element;
	}
	
	
	public boolean addData(Sample s, boolean overwrite)
	{
		boolean conflicts=false;
		if(overwrite){
			replaceData(s);
			LOGGER.info("[DATA] -- replace SAMPLE data");
		}else
			try {
				completeData(s);
				LOGGER.info("[DATA] -- complete SAMPLE data");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return conflicts;
	}

	private void replaceData(Sample s)
	{
		if(s!=null){
			element=s;
		}
	}

	private void completeData(Sample s) throws Exception
	{
		//copy input fields
		Sample copyIn=null;
		if(element!=null){
			copyIn=new Sample(element);
		}

		replaceData(s);

		// set input field values again
		if(copyIn!=null){
			String pdesc=copyIn.getPrepDescription();
			Timestamp pdate=copyIn.getPrepDate();
			String rc=copyIn.getRawMaterialCode();
			String rdesc=copyIn.getRawMaterialDesc();
			
			GridBox g=copyIn.getGridBox();
			String gNr=null;
			String gT=null;
			if(g!=null){
				gNr=g.getNr();
				gT=g.getType();
			}
			ObservedSample os=copyIn.getObservedSample(0);
			String osgx=null;
			String osgy=null;
			String ost=null;
			String osNr=null;
			if(os!=null){
				osgx=os.getGridNumberX();
				osgy=os.getGridNumberY();
				ost=os.getObjectType();
				osNr=os.getObjectNumber();
			}

			if(pdesc!=null && !pdesc.equals("")) element.setPrepDescription(pdesc);
			if(pdate!=null) element.setPrepDate(pdate);
			if(rc!=null && !rc.equals("")) element.setRawMaterialCode(rc);
			if(rdesc!=null && !rdesc.equals("")) element.setRawMaterialDesc(rdesc);
			if(gNr!=null) element.getGridBox().setNr(gNr);
			if(gT!=null && !gT.equals("")) element.getGridBox().setType(gT);
			if(osgx!=null && !osgx.equals("")) element.getObservedSample(0).setGridNumberY(osgx);
			if(osgy!=null && !osgy.equals("")) element.getObservedSample(0).setGridNumberY(osgy);
			if(ost!=null && !ost.equals("")) element.getObservedSample(0).setObjectType(ost);
			if(osNr!=null && !osNr.equals("")) element.getObservedSample(0).setObjectNumber(osNr);
		}
	}
	public Sample getSample()
	{
		return element;
	}
}
