package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

import ome.units.unit.Unit;
import ome.xml.model.Experiment;
import ome.xml.model.Experimenter;
import ome.xml.model.MapAnnotation;
import ome.xml.model.MapPair;
import ome.xml.model.MapPairs;
import ome.xml.model.enums.EnumerationException;
import ome.xml.model.enums.ExperimentType;
import ome.xml.model.enums.handlers.ExperimentTypeEnumHandler;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.UOSMetadataLogger;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.OMEStore;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules.ElementsCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model.ExperimentModel;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.ModuleConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.TagNames;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.ExperimenterListModel;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagData;
import org.openmicroscopy.shoola.util.ui.search.ExperimenterContext;

import loci.formats.FormatException;
import loci.formats.MetadataTools;

public class ExperimentCompUI extends ElementsCompUI 
{

	
	private TagData type;
	private TagData description;
	//last name of experimenter
	private TagData expName;
	
	// Project
	private TagData projectName;
	private TagData group;
	private TagData projectPartner;
	
	
//	private Experiment experiment;
	
	private ExperimentModel expContainer;
	
	
	

	public static final String PROJPARTNER_MAPLABEL="Project Partner";
	public static final String EXPERIMENT_DESC_MAPLABEL="Experiment Desc";
	public static final String EXPERIMENT_TYPE_MAPLABEL="Experiment Type";
	
	public ExperimentCompUI(ModuleConfiguration objConf)
	{
		
		initGUI();
		if(objConf==null)
			createDummyPane(false);
		else
			createDummyPane(objConf.getTagList(),false);
		
	}
	
//	public ExperimentCompUI(ExperimentContainer _experiment, String idxExp, String idxExper)
//	{
//		expContainer=_experiment;
//		
//		initGUI();
//		if(expContainer!=null){
//			setGUIData();
//		}
//		else{
//			if(idxExp==null )
//				idxExp=MetadataTools.createLSID("Experiment", 0);
//			
//			if(idxExper==null)
//				idxExper=MetadataTools.createLSID("Experimenter", 0);
//			
//			createNewExperiment(idxExp, idxExper);
//			createDummyPane(false);
//		}
//	}
	
	
	
	private void createNewExperiment(String idxExp,String idxExper)
	{
		//create new one
		expContainer=new ExperimentModel();
		Experiment experiment=new Experiment();
		experiment.setID(idxExp);
		Experimenter experimenter=new Experimenter();
		
		experimenter.setID(idxExper);
		experiment.linkExperimenter(experimenter);
		expContainer.setExperiment(experiment);
		expContainer.setExperimenter(experimenter);
	}
	
//	private void createNewElement() {
//		experiment=new Experiment();
//		Experimenter experimenter=new Experimenter();
//		experiment.linkExperimenter(experimenter);
//	}
	
	private void initTagList()
	{
		tagList=new ArrayList<TagData>();
		tagList.add(type);
		tagList.add(expName);
		tagList.add(description);
		tagList.add(projectName);
		tagList.add(group);
		tagList.add(projectPartner);
	}
	
	
	private void setGUIData() 
	{
		if(expContainer!=null) {
			try{ 
				setDescription(expContainer.getExperiment().getDescription(), ElementsCompUI.REQUIRED);}
			catch(NullPointerException e){}
			try{ 
				setType(expContainer.getExperiment().getType(), ElementsCompUI.REQUIRED);
			}catch(NullPointerException e){}
			try{ 
				setProjectPartner(expContainer.getProjectPartnerName(), ElementsCompUI.REQUIRED);
			}catch(NullPointerException e){}
			try{
//				setName(expContainer.getExperimenter(),ElementsCompUI.REQUIRED);
				setName(expContainer.getExperimenter(),ElementsCompUI.REQUIRED);
			}
			catch(NullPointerException e){}
		}

	}

	private void initGUI()
	{
		setLayout(new BorderLayout(5,5));
		buildComp=false;
		labels= new ArrayList<JLabel>();
		comp = new ArrayList<JComponent>();
		
		gridbag = new GridBagLayout();
		
		c = new GridBagConstraints();
		globalPane=new JPanel();
		
		globalPane.setAlignmentX(Component.LEFT_ALIGNMENT);
		globalPane.setAlignmentY(Component.TOP_ALIGNMENT);
		
		globalPane.setLayout(gridbag);
		
		add(globalPane,BorderLayout.NORTH);
		setBorder(
//				BorderFactory.createCompoundBorder(	new MyTitledBorder("Objective"),
						BorderFactory.createEmptyBorder(10,10,10,10));
		
	}

	/**
	 * If overwrite==true overwrite data, else only complete data
	 * @param exp
	 * @param overwrite
	 * @return
	 */
	public boolean addData(ExperimentModel exp, boolean overwrite,int nodeType)
	{
		boolean conflicts=false;
		if(exp==null)
			return false;
		
		if(overwrite){
			replaceData(exp);
			LOGGER.info("[DATA] -- replace EXPERIMENT data");
		}else
			try {
				completeData(exp);
				LOGGER.info("[DATA] -- complete EXPERIMENT data");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		setGUIData();
		return conflicts;
	}
	
	
	/**
	 * Overwrites only tags that are not set
	 * @param exper
	 * @throws Exception 
	 */
	private List<TagData> completeData(ExperimentModel exper) throws Exception
	{
		List<TagData> conflictTags=new ArrayList<TagData>();
		
		// copy input fields
		ExperimentModel copyIn=null;
		if(expContainer!=null ){
			getData();
			copyIn=new ExperimentModel(expContainer);
		}
		replaceData(exper);
		
		// set input field values again
		if(copyIn!=null){
			String projP=copyIn.getProjectPartnerName();;
			String projN=copyIn.getProjectName();
			String groupN=copyIn.getGroupName();
			Experimenter experimenter=copyIn.getExperimenter();

			ExperimentType type=null;
			String desc="";
			
			Experiment e=copyIn.getExperiment();
			if(e!=null){
				desc = copyIn.getExperiment().getDescription();
				type=copyIn.getExperiment().getType();
			}

			if(expContainer.testExperiment(e))
			{
				if(desc!=null && !desc.equals("")){
					if(expContainer.getExperiment().getDescription()!=null && !expContainer.getExperiment().getDescription().equals(""))
						conflictTags.add(description);
					
					expContainer.getExperiment().setDescription(desc);
				}
				if(type!=null) expContainer.getExperiment().setType(type);
			}

			if(experimenter!=null && !experimenter.getLastName().equals("")) {
				expContainer.setExperimenter(experimenter);
			}
			if(projP!=null && !projP.equals("")) expContainer.setProjectPartner(projP);
			if(projN!=null && !projN.equals("")) expContainer.setProjectName(projN);
			if(groupN!=null && !groupN.equals("")) expContainer.setGroupName(groupN);
			
		}
		
		return conflictTags;
	}
	
	/**
	 * Replace intern experimenter object by given experimenter. All manuell input data are lost. 
	 * @param exper
	 */
	private void replaceData(ExperimentModel exper)
	{
		if(exper!=null){
			expContainer=exper;
		}
	}
	

	
	
	private void readGUIInput() throws Exception
	{
		if(expContainer==null){
			createNewExperiment("", "");
		}
		//TODO input checker
		try{
			expContainer.getExperiment().setDescription(description.getTagValue());
		}catch(Exception e){
			LOGGER.error("[DATA] can't read EXPERIMENT description input");
		}
		try{
			expContainer.getExperiment().setType(getExperimentType(type.getTagValue()));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read EXPERIMENT type input");
		}
		try{
			expContainer.setProjectPartner(projectPartner.getTagValue());
		}catch(Exception e){
			LOGGER.error("[DATA] can't read EXPERIMENT project partner input");
		}
		try{
			expContainer.setProjectName(projectName.getTagValue());
		}catch(Exception e){
			LOGGER.error("[DATA] can't read EXPERIMENT project name input");
		}
		try{
			expContainer.setGroupName(group.getTagValue());
		}catch(Exception e){
			LOGGER.error("[DATA] can't read EXPERIMENT group input");
		}
		try{
			expContainer.setExperimenter(parseExperimenter(expName.getTagValue()));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read EXPERIMENT experimenter input");
		}
	}
	
	private Experimenter parseExperimenter(String str)
	{
		Experimenter ex= null;
		
		if(str!=null && str.length()>0){
			String[] split=str.split("\\s+");
			if(split.length >1){
				ex=new Experimenter();
				ex.setFirstName(split[0]);
				ex.setLastName(split[1]);
			}else{
				return null;
			}
		}
		return ex;
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
		
		
		return (result || dataChanged);
	}
	
	public void setFieldsExtern(boolean val)
	{
		dataChanged= dataChanged || val;
	}
	
//	private void updateDataFromXML(int idxExperiment, int idxExperimenter,	IMetadata data) throws Exception 
//	{
//		ServiceFactory factory = new ServiceFactory();
//		OMEXMLService service = factory.getInstance(OMEXMLService.class);
//		OME ome= (OME)service.createOMEXMLRoot(service.getOMEXML(data));
//		experiment=ome.getExperiment(idxExperiment);
//	}
	
	
	public void buildComponents() {
		labels.clear();
		comp.clear();
		addTagToGUI(projectName);
		addTagToGUI(group);
		addTagToGUI(projectPartner);
		addVSpaceToGui(10);
		addTagToGUI(expName);
		addTagToGUI(type);
		addTagToGUI(description);
		
		addLabelTextRows(labels, comp, gridbag, globalPane);
		
		c.gridwidth = GridBagConstraints.REMAINDER; //last
		c.anchor = GridBagConstraints.WEST;
		c.weightx = 1.0;
		
		if(group!=null)group.setEnable(false);
		if(projectName!=null)projectName.setEnable(false);
		
		
		buildComp=true;	
		initTagList();
		dataChanged=false;
		
//		globalPane.setMinimumSize(globalPane.getMinimumSize());
//		setMinimumSize(getMinimumSize());
//		
//		revalidate();
	}

	

	public void clearDataValues() 
	{
		clearTagValue(type);
		clearTagValue(description);
		clearTagValue(expName);
		clearTagValue(projectName);
		clearTagValue(projectPartner);
		clearTagValue(group);
		
	}
	
	
	public ExperimentModel getData() throws Exception
	{
//		if(userInput()){
//			LOGGER.info("[DEBUG] read GUI input (EXPERIMENT)");
			readGUIInput();
//		}
		return expContainer;
	}
	
//	public List<Experimenter> getExperimenterList()
//	{
//		return expName.getListValues();
//	}

	private void setType(ExperimentType value, boolean prop)
	{
		String val= (value != null) ? value.getValue():"";
		if(type == null) 
			type = new TagData(TagNames.TYPE,val,prop,TagData.COMBOBOX,getNames(ExperimentType.class));
		else 
			type.setTagValue(val,prop);	
	}
	
	private ExperimentType getExperimentType(String value)  throws FormatException
	  {
		if(value==null)
			return null;
		
	    ExperimentTypeEnumHandler handler = new ExperimentTypeEnumHandler();
	    try {
	      return (ExperimentType) handler.getEnumeration(value);
	    }
	    catch (EnumerationException e) {
	      throw new FormatException("ExperimentType creation failed", e);
	    }
	  }
	
	private void setDescription(String value, boolean prop)
	{
		if(description == null) 
			description = new TagData(TagNames.DESC,value,prop,TagData.TEXTAREA);
		else 
			description.setTagValue(value,prop);	
	}
	

//	public void setNameString(String value, boolean prop)
//	{
//		if(value!=null && !value.equals("")){
//			Experimenter e=new Experimenter();
//			e.setLastName(value);
//			setName(e,prop);
//		}
//	}
	private void setName(Experimenter value, boolean prop)
	{
		if(expName == null){ 
			expName = new TagData(TagNames.EXPNAME,getExperimenterName(value),prop,TagData.TEXTFIELD);
		}
		else{ 
			expName.setTagValue(getExperimenterName(value));
		}
	}
	
	private String getExperimenterName(Experimenter e)
	{
		
		String res=null;
		if(e!=null){
			String fName= (e.getFirstName()!=null && !e.getFirstName().equals("")) ? e.getFirstName():"";
			String lName=(e.getLastName()!=null && !e.getLastName().equals("")) ? e.getLastName() : "";

			if(fName.equals(""))
				res=lName;
			else
				res=fName+" "+lName;
		}
		return res;
	}
	private void setName(String val, boolean prop) 
	{
		setName(parseExperimenter(val),prop);
	}
	
	
	private void setProjectName(String value, boolean prop)
	{
		if(projectName == null) 
			projectName = new TagData(TagNames.PROJECTNAME,value,prop,TagData.TEXTFIELD);
		else 
			projectName.setTagValue(value,prop);
	}
	
	private void setGroupName(String value, boolean prop)
	{
		if(group == null) 
			group = new TagData(TagNames.GROUP,value,prop,TagData.TEXTFIELD);
		else 
			group.setTagValue(value,prop);
	}
	
	private void setProjectPartner(String value, boolean prop)
	{
		if(projectPartner == null) 
			projectPartner = new TagData(TagNames.PROJECTPARTNER,value,prop,TagData.TEXTFIELD);
		else 
			projectPartner.setTagValue(value,prop);
	}
	
//	public Experimenter getProjectPartnerAsExp()
//	{
//		Experimenter e=new Experimenter();
//		e.setLastName(projectPartner.getTagValue());
//		return e;
//	}
	

	@Override
	public void createDummyPane(boolean inactive) 
	{
		setProjectName(null, OPTIONAL);
		setGroupName(null, OPTIONAL);
		setProjectPartner(null, OPTIONAL);
		
		setType(null, ElementsCompUI.OPTIONAL);
		setDescription(null, ElementsCompUI.OPTIONAL);
//		setName(null,OPTIONAL);
		setName("",OPTIONAL);
		
		projectName.setEnable(false);
		group.setEnable(false);
		
		if(inactive){
			
			projectPartner.setEnable(false);
			expName.setEnable(false);
			type.setEnable(false);
			description.setEnable(false);
		}
	
	}
	
	public void createDummyPane(List<TagConfiguration> list,boolean inactive) 
	{
		if(list==null)
			createDummyPane(inactive);
		else{
			clearDataValues();
			//		if(experiment==null && list!=null && list.size()>0)
			//			createNewElement();
			for(int i=0; i<list.size();i++){
				TagConfiguration t=list.get(i);
				String name=t.getName();
				String val=t.getValue();
				boolean prop=t.getProperty();
				if(name!=null && t.isVisible()){
					setTag(name,val,prop,t.getUnit());
				}
			}
		}
	}
	

	@Override
	public List<TagData> getActiveTags() 
	{
		List<TagData> list = new ArrayList<TagData>();
		if(isActive(projectName)) list.add(projectName);
		if(isActive(projectPartner))list.add(projectPartner);
		if(isActive(group))list.add(group);
		if(isActive(type)) list.add(type);
		if(isActive(description)) list.add(description);
		if(isActive(expName)) list.add(expName);

		return list;
	}

	public class ScrollablePanel extends JPanel implements Scrollable {
	    public Dimension getPreferredScrollableViewportSize() {
	        return getPreferredSize();
	    }

	    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
	       return 10;
	    }

	    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
	        return ((orientation == SwingConstants.VERTICAL) ? visibleRect.height : visibleRect.width) - 10;
	    }

	    public boolean getScrollableTracksViewportWidth() {
	        return true;
	    }

	    public boolean getScrollableTracksViewportHeight() {
	        return false;
	    }
	}

	public String parseProjectPartner(MapAnnotation map) 
	{
		MapPairs mp=map.getValue();
		List<MapPair> listMP=mp.getPairs();
		String result=null;
		switch (map.getNamespace()) {
		case OMEStore.NS_2016_06_07:
			result=parseFromMapAnnotation2016_06_07(listMP);
			break;

		default:
			LOGGER.warn("[DATA] Namespace is not supported for parsing sample data");
			break;
		}
		return result;
	}

	private String parseFromMapAnnotation2016_06_07(List<MapPair> listMP) 
	{
		for(MapPair obj:listMP){
			switch (obj.getName()) {
			case PROJPARTNER_MAPLABEL:
				return obj.getValue();
			default:
				LOGGER.info("[DATA] unknown Label for Project Partner MapAnnotation: "+obj.getName());
				break;
			}
		}
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
		case TagNames.TYPE:
			try{
				if(val!=null){
					ExperimentType value= getExperimentType(val);
					setType(value, prop);
				}else{
					setType(null, prop);
				}
			}catch(Exception e){
				setType(null, prop);
			}
			type.setVisible(true);
			break;
		case TagNames.DESC:
			try{
				if(val!=null){
					setDescription(val, prop);
				}else{
					setDescription(null, prop);
				}
			}catch(Exception e){
				setDescription(null, prop);
			}
			description.setVisible(true);
			break;
			//Set by system
		case TagNames.GROUP:
			setGroupName(null, prop);
			group.setVisible(true);
			break;
		case TagNames.EXPNAME:
			if(val!=null)
				setName(val,prop);
			else
				setName("", prop);
			this.expName.setVisible(true);
			break;
			//set by system
		case TagNames.PROJECTNAME:
			setProjectName(null, prop);
			projectName.setVisible(true);
			break;
		case TagNames.PROJECTPARTNER:
			if(val!=null)
				setProjectPartner(val, prop);
			else
				setProjectPartner(null, prop);
			projectPartner.setVisible(true);
			break;
		default:
			LOGGER.warn("[CONF] unknown tag: "+name );break;
		}
	}

	

	/**
	 * Set group, project name and experimenter list data
	 * @param expCont
	 */
	public void setExtendedData(ExperimentModel expCont) 
	{
		setGroupName(expCont.getGroupName(), OPTIONAL);
		setProjectName(expCont.getProjectName(), OPTIONAL);
		setName(expCont.getExperimenter(), OPTIONAL);
		setFieldsExtern(true);
	}



}


