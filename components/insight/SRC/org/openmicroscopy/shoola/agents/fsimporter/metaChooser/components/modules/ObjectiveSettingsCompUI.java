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
import ome.units.unit.Unit;
import ome.xml.model.Image;
import ome.xml.model.ObjectiveSettings;
import ome.xml.model.enums.EnumerationException;
import ome.xml.model.enums.LaserType;
import ome.xml.model.enums.Medium;


public class ObjectiveSettingsCompUI extends ElementsCompUI
{
	
	
	private TagData corCollar;
	private TagData medium;
	private TagData refractIndex;
	
	private TitledBorder tb;
	
	private ObjectiveSettings oSett;
	
//	public ObjectiveSettingsCompUI()
//	{
//		initGUI();
//		createDummyPane(false);
//	}
	public ObjectiveSettingsCompUI(ModuleConfiguration objConf)
	{
		initGUI();
		if(objConf==null){
			createDummyPane(false);
		}else{
			createDummyPane(objConf.getSettingList(),false);
		}
	}
	

	
	private void initTagList()
	{
		tagList=new ArrayList<TagData>();
		tagList.add(corCollar);
		tagList.add(medium);
		tagList.add(refractIndex);
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
		return (result);
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
	
	private void setGUIData()
	{
		try{setRefractIndex(oSett.getRefractiveIndex(), ElementsCompUI.REQUIRED);
		} catch (NullPointerException e) { }
		try{ setMedium(oSett.getMedium(), ElementsCompUI.REQUIRED);
		} catch (NullPointerException e) { }
		try{ setCorCollar(oSett.getCorrectionCollar(), ElementsCompUI.REQUIRED);
		} catch (NullPointerException e) { }
	
	}
	
	

	
	public boolean addData(ObjectiveSettings objS, boolean overwrite)
	{
		boolean conflicts=false;
		if(overwrite){
			replaceData(objS);
			LOGGER.info("[DATA] -- replace OBJECTIVE_SETTINGS data");
		}else
			try {
				completeData(objS);
				LOGGER.info("[DATA] -- complete OBJECTIVE_SETTINGS data");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		setGUIData();
		return conflicts;
	}
	
	private void replaceData(ObjectiveSettings o)
	{
		if(o!=null){
			oSett=o;
		}
	}
	
	private void completeData(ObjectiveSettings o) throws Exception
	{
		//copy input fields
		ObjectiveSettings copyIn=null;
		if(oSett!=null){
			getData();
			copyIn=new ObjectiveSettings(oSett);
		}

		replaceData(o);

		// set input field values again
		if(copyIn!=null){
			Double rI=copyIn.getRefractiveIndex();
			Medium m=copyIn.getMedium();
			Double cc=copyIn.getCorrectionCollar();

			if(rI!=null)oSett.setRefractiveIndex(rI);
			if(m!=null)oSett.setMedium(m);
			if(cc!=null)oSett.setCorrectionCollar(cc);
		}
	}
	
	private void readGUIInput() throws Exception
	{
		if(oSett==null)
			createNewElement();
		//TODO input checker
		try{
			oSett.setRefractiveIndex(parseToDouble(refractIndex.getTagValue()));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read OBJECTIVE SETT refraction index input");
		}
		try{
			oSett.setMedium(parseMedium(medium.getTagValue()));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read OBJECTIVE SETT medium input");
		}
		try{
			oSett.setCorrectionCollar(parseToDouble(corCollar.getTagValue()));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read OBJECTIVE SETT correction collar input");
		}
	}
	
	private Medium parseMedium(String c) 
	{
		if(c==null || c.equals(""))
			return null;
		
		Medium a=null;
		try{
			a=Medium.fromString(c);
		}catch(EnumerationException e){
			LOGGER.warn("Medium: "+c+" is not supported");
			a=Medium.OTHER;
		}
		return a;
	}
	
	private void createNewElement() {
		oSett=new ObjectiveSettings();
	}
	public ObjectiveSettings getData() throws Exception
	{
		if(userInput()){
			readGUIInput();
		}
		return oSett;
	}
	
	
//	public void setTitledBorder(String s)
//	{
//		if(s== null || s.equals(null)) return;
//		tb.setTitle(s);
//	}
	public void buildComponents() 
	{
		labels.clear();
		comp.clear();
	
		addLabelToGUI(new JLabel("Settings:"));
		addTagToGUI(corCollar);
		addTagToGUI(medium);
		addTagToGUI(refractIndex);
		
		addLabelTextRows(labels, comp, gridbag, this);
		
		c.gridwidth = GridBagConstraints.REMAINDER; //last
		c.anchor = GridBagConstraints.WEST;
		c.weightx = 1.0;
		
		buildComp=true;
		initTagList();
	}

	
	
	public void createDummyPane(boolean inactive)
	{
		setCorCollar(null,OPTIONAL);
		setMedium(null,OPTIONAL);
		setRefractIndex(null, OPTIONAL);
		
		if(inactive){
		corCollar.setEnable(false);
		medium.setEnable(false);
		refractIndex.setEnable(false);
		}
	}
	
	private void createDummyPane(List<TagConfiguration> list,boolean inactive) 
	{
		if(list==null)
			createDummyPane(inactive);
		else{
			clearDataValues();
			//		if(oSett==null && list!=null && list.size()>0)
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
		clearTagValue(corCollar);
		clearTagValue(medium);
		clearTagValue(refractIndex);		
	}
	
	
	private void setCorCollar(Double value,boolean prop)
	{
		String val= (value != null) ? String.valueOf(value):"";
		if(corCollar == null) 
			corCollar = new TagData(TagNames.CORCOLLAR,val,prop,TagData.TEXTFIELD);
		else 
			corCollar.setTagValue(val,prop);
	}
	private void setMedium(Medium value,boolean prop)
	{
		String val= (value != null) ? String.valueOf(value):"";
		if(medium == null) 
			medium = new TagData(TagNames.OBJ_MEDIUM,val,prop,TagData.COMBOBOX,getNames(Medium.class));
		else 
			medium.setTagValue(val,prop);
	}
	private void setRefractIndex(Double value,boolean prop)
	{
		String val= (value != null) ? String.valueOf(value):"";
		if(refractIndex == null) 
			refractIndex = new TagData(TagNames.REFINDEX,val,prop,TagData.TEXTFIELD);
		else 
			refractIndex.setTagValue(val,prop);
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
		setTag(t.getName(),t.getValue(),t.getProperty(),t.getUnit());
	}
	
	private void setTag(String name,String val,boolean prop,Unit unit)
	{
		switch (name) {
		case TagNames.CORCOLLAR: 
			try{
				if(val!=null){
					Double value=parseToDouble(val);
					setCorCollar(value, prop);
					
				}else{
					setCorCollar(null, prop);
				}
//				oSett.setCorrectionCollar(value);
			}
			catch(Exception e){
				setCorCollar(null, prop);
			}
			corCollar.setVisible(true);
			break;
		case TagNames.OBJ_MEDIUM: 
			try{
				if(val!=null){
					Medium value=parseMedium(val);
					setMedium(value, prop);
				}else{
					setMedium(null, prop);	
				}
				//						oSett.setMedium(value);
			}catch(Exception e){
				setMedium(null, prop);	
			}
			medium.setVisible(true);
			break;
		case TagNames.REFINDEX:
			try{
				if(val!=null){
					Double value=parseToDouble(val);
					setRefractIndex(value, prop);
				}else{
					setRefractIndex(null, prop);		
				}
				//						oSett.setRefractiveIndex(value);
			}catch(Exception e){
				setRefractIndex(null, prop);	
			}
			refractIndex.setVisible(true);
			break;
		default:LOGGER.warn("[CONF] OBJECTIVE SETT unknown tag: "+name );break;
		}
		dataChanged=true;
	}

	
	
}
