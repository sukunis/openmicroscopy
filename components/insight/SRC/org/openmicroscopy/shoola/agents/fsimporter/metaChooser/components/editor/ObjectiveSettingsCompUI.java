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

import loci.formats.MetadataTools;
import loci.formats.meta.IMetadata;
import ome.xml.model.ObjectiveSettings;
import ome.xml.model.enums.EnumerationException;
import ome.xml.model.enums.Medium;


public class ObjectiveSettingsCompUI extends ElementsCompUI
{
	private final String L_CORCOLLAR="Correction Collar";
	private final String L_MEDIUM="Medium";
	private final String L_REFINDEX="Refraction Index";
	
	private TagData corCollar;
	private TagData medium;
	private TagData refractIndex;
	private List<TagData> tagList;
	
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
		if(objConf==null)
			createDummyPane(false);
		else
			createDummyPane(objConf.getSettingList(),false);
	}
	
//	public ObjectiveSettingsCompUI(ObjectiveSettings _oSett, String id)
//	{
//		oSett=_oSett;
//		initGUI();
//		if(oSett!=null)
//			setGUIData();
//		else{
//			if(id==null)
//				id=MetadataTools.createLSID("Objective", 0,0);
//			oSett=new ObjectiveSettings();
//			oSett.setID(id);
//			createDummyPane(false);
//		}
//	}
	
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
	
	
	public boolean addData(ObjectiveSettings os, boolean overwrite)
	{
		boolean conflicts=false;
		if(oSett!=null){
			if(os!=null){
				Double rI=os.getRefractiveIndex();
				Medium m=os.getMedium();
				Double cc=os.getCorrectionCollar();
				if(overwrite){
					if(os.getID()!=null && !os.getID().equals(""))
						oSett.setID(os.getID());
					if(rI!=null)oSett.setRefractiveIndex(rI);
					if(m!=null)oSett.setMedium(m);
					if(cc!=null)oSett.setCorrectionCollar(cc);
					LOGGER.info("[DATA] overwrite OBJECTIVE_SETTINGS data");
				}else{
					if(oSett.getID()==null || oSett.getID().equals(""))
						oSett.setID(os.getID());
					if(oSett.getRefractiveIndex()==null)
						oSett.setRefractiveIndex(rI);
					if(oSett.getMedium()==null)
						oSett.setMedium(m);
					if(oSett.getCorrectionCollar()==null)
						oSett.setCorrectionCollar(cc);
					LOGGER.info("[DATA] complete OBJECTIVE_SETTINGS data");
				}
			}
		}else if(os!=null){
			oSett=os;
			LOGGER.info("[DATA] add OBJECTIVE_SETTINGS data");
		}
		
		setGUIData();
		return conflicts;
	}
	
	private void readGUIInput() throws Exception
	{
		if(oSett==null)
			createNewElement();
		//TODO input checker
		try{
			oSett.setRefractiveIndex(parseToDouble(refractIndex.getTagValue()));
		}catch(Exception e){
			LOGGER.severe("[DATA] can't read OBJECTIVE SETT refraction index input");
		}
		try{
			oSett.setMedium(parseMedium(medium.getTagValue()));
		}catch(Exception e){
			LOGGER.severe("[DATA] can't read OBJECTIVE SETT medium input");
		}
		try{
			oSett.setCorrectionCollar(parseToDouble(corCollar.getTagValue()));
		}catch(Exception e){
			LOGGER.severe("[DATA] can't read OBJECTIVE SETT correction collar input");
		}
	}
	
	private Medium parseMedium(String c) throws EnumerationException
	{
		if(c==null || c.equals(""))
			return null;
		
		return Medium.fromString(c);
	}
	
	private void createNewElement() {
		oSett=new ObjectiveSettings();
	}
	public ObjectiveSettings getData() throws Exception
	{
		if(userInput())
			readGUIInput();
		return oSett;
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

	public void buildExtendedComponents() 
	{
				
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
	
	public void createDummyPane(List<TagConfiguration> list,boolean inactive) 
	{
		if(list==null)
			createDummyPane(inactive);
		else{
		clearDataValues();
		if(oSett==null && list!=null && list.size()>0)
			createNewElement();
		for(int i=0; i<list.size();i++){
			TagConfiguration t=list.get(i);
			String name=t.getName();
			String val=t.getValue();
			boolean prop=t.getProperty()!= null ? Boolean.parseBoolean(t.getProperty()):
				OPTIONAL;
			if(name!=null){
				switch (name) {
				case L_CORCOLLAR: 
					try{
						Double value=parseToDouble(val);
						setCorCollar(value, prop);
						oSett.setCorrectionCollar(value);
					}
					catch(Exception e){
						setCorCollar(null, prop);
					}
					corCollar.setVisible(true);
					break;
				case L_MEDIUM: 
					try{
						Medium value=parseMedium(val);
						setMedium(value, prop);
						oSett.setMedium(value);
					}catch(Exception e){
						setMedium(null, prop);	
					}
					medium.setVisible(true);
					break;
				case L_REFINDEX:
					try{
						Double value=parseToDouble(val);
						setRefractIndex(value, prop);
						oSett.setRefractiveIndex(value);
					}catch(Exception e){
						setRefractIndex(null, prop);	
					}
					refractIndex.setVisible(true);
					break;
					default:LOGGER.warning("[CONF] OBJECTIVE SETT unknown tag: "+name );break;
				}
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
	
	
	public void setCorCollar(Double value,boolean prop)
	{
		String val= (value != null) ? String.valueOf(value):"";
		if(corCollar == null) 
			corCollar = new TagData("Correction Collar: ",val,prop,TagData.TEXTFIELD);
		else 
			corCollar.setTagValue(val,prop);
	}
	public void setMedium(Medium value,boolean prop)
	{
		String val= (value != null) ? String.valueOf(value):"";
		if(medium == null) 
			medium = new TagData("Medium: ",val,prop,TagData.COMBOBOX,getNames(Medium.class));
		else 
			medium.setTagValue(val,prop);
	}
	public void setRefractIndex(Double value,boolean prop)
	{
		String val= (value != null) ? String.valueOf(value):"";
		if(refractIndex == null) 
			refractIndex = new TagData("Refraction Index: ",val,prop,TagData.TEXTFIELD);
		else 
			refractIndex.setTagValue(val,prop);
	}

	@Override
	public List<TagData> getActiveTags() {
		// TODO Auto-generated method stub
		return null;
	}
	

	

	
	
}
