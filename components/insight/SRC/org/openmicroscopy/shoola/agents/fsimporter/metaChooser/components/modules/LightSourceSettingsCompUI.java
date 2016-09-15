package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.border.TitledBorder;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules.ElementsCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.ModuleConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.TagNames;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagData;

import loci.formats.MetadataTools;
import loci.formats.meta.IMetadata;
import ome.units.UNITS;
import ome.units.quantity.Length;
import ome.units.unit.Unit;
import ome.xml.model.DetectorSettings;
import ome.xml.model.Image;
import ome.xml.model.Laser;
import ome.xml.model.LightSourceSettings;
import ome.xml.model.primitives.PercentFraction;


public class LightSourceSettingsCompUI extends ElementsCompUI 
{

	private TagData waveLength;
	/**==Absorptionskoefizient a fraction, as a value from 0.0 to 1.0*/
	private TagData attenuation;
	//??
//	private TagData intensity;

	
	private LightSourceSettings lightSrc;
	
	private void initTagList()
	{
		tagList=new ArrayList<TagData>();
		tagList.add(waveLength);
		tagList.add(attenuation);
		
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
		System.out.println("# LightSrcSettCompUI::userInput()= "+result);
		return result;
	}

	
	public LightSourceSettingsCompUI(ModuleConfiguration objConf)
	{
		System.out.println("# LightSrcSettCompUI::new Instance 1");
		initGUI();
		if(objConf==null)
			createDummyPane(false);
		else
			createDummyPane(objConf.getSettingList(),false);
	}

	
	private void initGUI()
	{
		buildComp=false;
		labels= new ArrayList<JLabel>();
		comp = new ArrayList<JComponent>();
		
		gridbag = new GridBagLayout();
		c = new GridBagConstraints();
		setLayout(gridbag);
	}
	

	public boolean addData(LightSourceSettings l, boolean overwrite)
	{
		System.out.println("# LightSrcSettCompUI::addData("+overwrite+")");
		boolean conflicts=false;
		if(overwrite){
			replaceData(l);
			LOGGER.info("[DATA] -- replace LIGHTSRC_SETTINGS data");
		}else
			try {
				completeData(l);
				LOGGER.info("[DATA] -- complete LIGHTSRC_SETTINGS data");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		setGUIData();
		return conflicts;
	}
	
	private void replaceData(LightSourceSettings l)
	{
		if(l!=null){
			lightSrc=l;
		}
	}
	
	private void completeData(LightSourceSettings l) throws Exception
	{
		//copy input fields
		LightSourceSettings copyIn=null;
		if(lightSrc!=null){
			getData();
			copyIn=new LightSourceSettings(lightSrc);
		}

		replaceData(l);

		// set input field values again
		if(copyIn!=null){
			Length w=copyIn.getWavelength();
			PercentFraction p=copyIn.getAttenuation();
			if(w!=null) lightSrc.setWavelength(w);
			if(p!=null) lightSrc.setAttenuation(p);
		}
	}
	
	private void setGUIData()
	{
		if(lightSrc!=null){
			try{setWavelength(lightSrc.getWavelength(), ElementsCompUI.REQUIRED);
			} catch (NullPointerException e) { }
			try{setAttenuation(lightSrc.getAttenuation(), ElementsCompUI.REQUIRED);
			}catch (NullPointerException e){}
		}
	}
	
	private void readGUIInput() throws Exception
	{
		System.out.println("# LightSrcSettCompUI::readGuiInput");
		if(lightSrc==null){
			createNewElement();
		}
		try{
			lightSrc.setWavelength(parseToLength(waveLength.getTagValue(),waveLength.getTagUnit()));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read LIGHTSRC SETT wavelength input");
		}
		try{
			//TODO input format hint: percentvalue elem of [0,100] or [0,1]
			lightSrc.setAttenuation(parseAttenuation(attenuation.getTagValue()));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read LIGHTSRC SETT attenuation input");
		}
	}
	
	private PercentFraction parseAttenuation(String c)
	{
		if(c==null || c.equals(""))
			return null;
		
		return new PercentFraction(Float.valueOf(c));
	}
	
	private void createNewElement() {
		lightSrc=new LightSourceSettings();
	}

	public LightSourceSettings getData() throws Exception
	{
		System.out.println("# LightSrcSettCompUI::getData()");
		if(userInput())
			readGUIInput();
		return lightSrc;
	}
	
	
	public void buildComponents() 
	{
		System.out.println("# LightSrcSettCompUI::buildComp()");
		labels.clear();
		comp.clear();
		
		addLabelToGUI(new JLabel("Settings:"));
//		addTagToGUI(intensity);
		addTagToGUI(waveLength);
		addTagToGUI(attenuation);
				
		addLabelTextRows(labels, comp, gridbag, this);
		
		c.gridwidth = GridBagConstraints.REMAINDER; //last
		c.anchor = GridBagConstraints.WEST;
		c.weightx = 1.0;
		
		buildComp=true;
		initTagList();
	}


	
	@Override
	public void createDummyPane(boolean inactive) {
		System.out.println("# LightSrcSettCompUI::createDummyPane(bool)");
//		setIntensity(null, ElementsCompUI.OPTIONAL);
		setWavelength(null, ElementsCompUI.OPTIONAL);
		setAttenuation(null, ElementsCompUI.OPTIONAL);
		
		if(inactive){
//			intensity.setInactiv();
			waveLength.setEnable(false);
			attenuation.setEnable(false);
		}
	}
	
	public void createDummyPane(List<TagConfiguration> list,boolean inactive) 
	{
		System.out.println("# LightSrcSettingsCompUI::createDummyPane(List,boolean)");
		if(list==null)
			createDummyPane(inactive);
		else{
		clearDataValues();
//		if(lightSrc==null && list!=null && list.size()>0)
//			createNewElement();
		for(int i=0; i<list.size();i++){
			TagConfiguration t=list.get(i);
			String name=t.getName();
			String val=t.getValue();
			boolean prop=t.getProperty();
			if(name!=null){
				setTag(t);
			}
		}
		}
	}

	public void clearDataValues() 
	{
		System.out.println("# LightSrcSettCompUI::clearDataValues()");
//		clearTagValue(intensity);
		clearTagValue(waveLength);
		clearTagValue(attenuation);
	}
	
	

	public void setWavelength(Length value, boolean prop)
	{
		String val=(value!=null) ? String.valueOf(value.value()) :"";
		Unit unit=(value!=null) ? value.unit():TagNames.WAVELENGTH_UNIT;
		if(waveLength == null) 
			waveLength = new TagData(TagNames.SET_WAVELENGTH,val,unit,prop,TagData.TEXTFIELD);
		else 
			waveLength.setTagValue(val,unit,prop);
		
	}
	public void setAttenuation(PercentFraction value, boolean prop)
	{
		String val= (value != null) ? String.valueOf(value.getNumberValue()):"";
		if(attenuation == null) 
			attenuation = new TagData(TagNames.ATTENUATION,val,prop,TagData.TEXTFIELD);
		else 
			attenuation.setTagValue(val,prop);
	}

	@Override
	public List<TagData> getActiveTags() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Update tags with val from list
	 */
	public void update(List<TagData> list) 
	{
		System.out.println("# LightSrcSettCompUI::update()");
		for(TagData t: list){
			if(t.valueChanged()){
				setTag(t);
			}
		}
	}

	private void setTag(TagData t)
	{
		setTag(t.getTagName(),t.getTagValue(),t.getTagProp(),t.getTagUnit());
	}
	
	private void setTag(TagConfiguration t)
	{
		t.printf();
		setTag(t.getName(),t.getValue(),t.getProperty(),t.getUnit());
	}
	
	private void setTag(String name,String val,boolean prop,Unit unit)
	{
		switch (name) {
		case TagNames.SET_WAVELENGTH:
			try {
				setWavelength(parseToLength(val, unit), prop);
			} catch (Exception e) {
				setWavelength(null, prop);
			}
			waveLength.setVisible(true);
			break;
		case TagNames.ATTENUATION:
			try{
			setAttenuation(parseAttenuation(val), prop);
			}catch(Exception e){
				setAttenuation(null, prop);
			}
			attenuation.setVisible(true);
			break;
		default: LOGGER.warn("[CONF] LIGHTSRC SETT unknown tag: "+name );break;
		}
	}

}
