package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.border.TitledBorder;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.ElementsCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.TagData;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.ModuleConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.TagNames;

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
	private Unit<Length> waveLengthUnit=UNITS.NM;
	/**==Absorptionskoefizient a fraction, as a value from 0.0 to 1.0*/
	private TagData attenuation;
	//??
//	private TagData intensity;

	private List<TagData> tagList;
	
	//reference to lightSrc
	private String lightSrcId;
	
	private TitledBorder tb;
	
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
		return result;
	}

	
	public LightSourceSettingsCompUI(ModuleConfiguration objConf)
	{
	
		initGUI();
		if(objConf==null)
			createDummyPane(false);
		else
			createDummyPane(objConf.getSettingList(),false);
	}
	
	public LightSourceSettingsCompUI(LightSourceSettings _ls, String id)
	{
		lightSrc=_ls;
		initGUI();
		if(lightSrc!=null)
			setGUIData();
		else{
			//TODO
//			if(id==null){
//				id=MetadataTools.createLSID("LightSource", 0,0);
//			}
			lightSrc=new LightSourceSettings();
			lightSrc.setID("");
			createDummyPane(false);
		}
	}
	
	private void initGUI()
	{
		buildComp=false;
		labels= new ArrayList<JLabel>();
		comp = new ArrayList<JComponent>();
		
		gridbag = new GridBagLayout();
		c = new GridBagConstraints();
		setLayout(gridbag);
		tb=new TitledBorder("");
//		setBorder(
//				BorderFactory.createCompoundBorder(	tb,
//						BorderFactory.createEmptyBorder(5,5,5,5)));
	}
	
//	public void addData(LightSourceSettings ls,boolean overwrite)
//	{
//		if(lightSrc!=null){
//			if(ls!=null){
//				Length w=ls.getWavelength();
//				PercentFraction p=ls.getAttenuation();
//				if(overwrite){
//					if(ls.getID()!=null && !ls.getID().equals(""))
//						lightSrc.setID(ls.getID());
//					if(w!=null) lightSrc.setWavelength(w);
//					if(p!=null) lightSrc.setAttenuation(p);
//					LOGGER.info("[DATA] overwrite LIGHTSRC_SETTINGS data");
//				}else{
//					if(lightSrc.getID()==null || lightSrc.getID().equals(""))
//						lightSrc.setID(ls.getID());
//					if(lightSrc.getWavelength()==null)
//						lightSrc.setWavelength(w);
//					if(lightSrc.getAttenuation()==null)
//						lightSrc.setAttenuation(p);
//					LOGGER.info("[DATA] complete LIGHTSRC_SETTINGS data");
//				}
//			}
//			
//		}else if(ls!=null){
//			lightSrc=ls;
//			LOGGER.info("[DATA] add LIGHTSRC_SETTINGS data");
//		}
//		
//		setGUIData();
//	}
//	
	public boolean addData(LightSourceSettings l, boolean overwrite)
	{
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
		if(userInput())
			readGUIInput();
		return lightSrc;
	}
	
	public void setTitledBorder(String s)
	{
		if(s== null || s.equals(null)) return;
		tb.setTitle(s);
	}
	
	public void buildComponents() 
	{
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

	public void buildExtendedComponents() 
	{

	}
	
	@Override
	public void createDummyPane(boolean inactive) {
		
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
				switch (name) {
				case TagNames.SET_WAVELENGTH:
					try {
						if(val!=null){
						Length value = parseToLength(val, t.getUnit());
						setWavelength(value, prop);
						}else{
							setWavelength(null, prop);
						}
//						lightSrc.setWavelength(value);
					} catch (Exception e) {
						setWavelength(null, prop);
					}
					waveLength.setVisible(true);
					break;
				case TagNames.ATTENUATION:
					try{
						if(val!=null){
						PercentFraction value=parseAttenuation(val);
					setAttenuation(value, prop);
						}else{
							setAttenuation(null, prop);
						}
//					lightSrc.setAttenuation(value);
					}catch(Exception e){
						setAttenuation(null, prop);
					}
					attenuation.setVisible(true);
					break;
				default: LOGGER.warn("[CONF] LIGHTSRC SETT unknown tag: "+name );break;
				}
			}
		}
		}
	}

	public void clearDataValues() 
	{
//		clearTagValue(intensity);
		clearTagValue(waveLength);
		clearTagValue(attenuation);
		lightSrcId=null;
	}
	
	public void setID(String value)
	{
		String val= (value != null) ? String.valueOf(value):"";
		lightSrcId=val;
	}
	
	public String getID()
	{
		return lightSrc.getID();
	}
	

	public void setWavelength(Length value, boolean prop)
	{
		String val=(value!=null) ? String.valueOf(value.value()) :"";
		Unit unit=(value!=null) ? value.unit():waveLengthUnit;
		if(waveLength == null) 
			waveLength = new TagData(TagNames.SET_WAVELENGTH,val,unit,prop,TagData.TEXTFIELD);
		else 
			waveLength.setTagValue(val,unit,prop);
		
	}
	public void setAttenuation(PercentFraction value, boolean prop)
	{
		String val= (value != null) ? String.valueOf(value.getNumberValue()):"";
		if(attenuation == null) 
			attenuation = new TagData(TagNames.ATTENUATION+": ",val,prop,TagData.TEXTFIELD);
		else 
			attenuation.setTagValue(val,prop);
	}

	@Override
	public List<TagData> getActiveTags() {
		// TODO Auto-generated method stub
		return null;
	}

	

}
