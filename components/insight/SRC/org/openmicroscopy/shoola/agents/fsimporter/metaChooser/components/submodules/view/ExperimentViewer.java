package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import loci.formats.FormatException;
import ome.xml.model.Experiment;
import ome.xml.model.Experimenter;
import ome.xml.model.Objective;
import ome.xml.model.ObjectiveSettings;
import ome.xml.model.enums.EnumerationException;
import ome.xml.model.enums.ExperimentType;
import ome.xml.model.enums.handlers.ExperimentTypeEnumHandler;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules.ElementsCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules.ObjectiveEditor;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model.ExperimentModel;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model.ObjectiveModel;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.ModuleConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.TagNames;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagData;
import org.slf4j.LoggerFactory;

public class ExperimentViewer extends ModuleViewer{

	private static final org.slf4j.Logger LOGGER =
			LoggerFactory.getLogger(ExperimentViewer.class);

	private ExperimentModel data;
	private Box box;

	// available element tags
	private TagData type;
	private TagData description;
	//last name of experimenter
	private TagData expName;

	// Project
	private TagData projectName;
	private TagData group;
	private TagData projectPartner;

	public static final String PROJPARTNER_MAPLABEL="Project Partner";
	public static final String EXPERIMENT_DESC_MAPLABEL="Experiment Desc";
	public static final String EXPERIMENT_TYPE_MAPLABEL="Experiment Type";

	/**
	 * Creates a new instance.
	 * @param model Reference to model.
	 */
	public ExperimentViewer(ExperimentModel model,ModuleConfiguration conf)
	{
		System.out.println("# ExperimentModel::new Instance("+(model!=null?"model":"null")+")");
		this.data=model;
		initComponents(conf);
		initTagList();
		buildGUI();
	}

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

	/**
	 * Builds and lay out GUI.
	 */
	private void buildGUI() 
	{
		List<JLabel> labels= new ArrayList<JLabel>();
		List<JComponent> comp=new ArrayList<JComponent>();
		addTagToGUI(projectName,labels,comp);
		addTagToGUI(group,labels,comp);
		addTagToGUI(projectPartner,labels,comp);
		addVSpaceToGui(10,labels,comp);
		addTagToGUI(expName,labels,comp);
		addTagToGUI(type,labels,comp);
		addTagToGUI(description,labels,comp);

		addLabelTextRows(labels, comp, gridbag, globalPane);

		c.gridwidth = GridBagConstraints.REMAINDER; //last
		c.anchor = GridBagConstraints.WEST;
		c.weightx = 1.0;

		if(group!=null)group.setEnable(false);
		if(projectName!=null)projectName.setEnable(false);
		// set data
		setGUIData();
	}

	/**
	 * Initialize components.
	 */
	private void initComponents(ModuleConfiguration conf) 
	{
		setLayout(new BorderLayout(5,5));

		gridbag = new GridBagLayout();
		c = new GridBagConstraints();

		globalPane=new JPanel();
		globalPane.setLayout(gridbag);

		//		add(new TitledSeparator("Channel", 3, TitledBorder.DEFAULT_POSITION),BorderLayout.NORTH);
		add(globalPane,BorderLayout.NORTH);

		setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

		// init tag layout
		List<TagConfiguration> list=conf.getTagList();
		initTags(list);
	}



	/**
	 * Init given tag and mark it as visible.
	 * @param t
	 */
	protected void initTag(TagConfiguration t) 
	{
		predefinitionValLoaded=predefinitionValLoaded || (t.getValue()!=null && !t.getValue().equals(""));
		String name=t.getName();
		Boolean prop=t.getProperty();
		switch (name) {
		case TagNames.TYPE:

			setType(null, prop);
			type.setVisible(true);
			break;
		case TagNames.DESC:
			setDescription(null, prop);
			description.setVisible(true);
			break;
			//Set by system
		case TagNames.GROUP:
			setGroupName(null, prop);
			group.setVisible(true);
			break;
		case TagNames.EXPNAME:
			setName("", prop);
			this.expName.setVisible(true);
			break;
			//set by system
		case TagNames.PROJECTNAME:
			setProjectName(null, prop);
			projectName.setVisible(true);
			break;
		case TagNames.PROJECTPARTNER:
			setProjectPartner(null, prop);
			projectPartner.setVisible(true);
			break;
		default:
			LOGGER.warn("[CONF] unknown tag: "+name );break;
		}
	}

	/**
	 * Show data of objective
	 */
	private void setGUIData() 
	{
		if(data!=null) {
			try{ 
				setDescription(data.getExperiment().getDescription(), ElementsCompUI.REQUIRED);}
			catch(NullPointerException e){}
			try{ 
				setType(data.getExperiment().getType(), ElementsCompUI.REQUIRED);
			}catch(NullPointerException e){}
			try{ 
				setProjectPartner(data.getProjectPartnerName(), ElementsCompUI.REQUIRED);
			}catch(NullPointerException e){}
			try{
				//			setName(expContainer.getExperimenter(),ElementsCompUI.REQUIRED);
				setName(data.getExperimenter(),ElementsCompUI.REQUIRED);
			}catch(NullPointerException e){}
			try{
				setGroupName(data.getGroupName(), OPTIONAL);
			}catch(NullPointerException e){}
			try{setProjectName(data.getProjectName(), OPTIONAL);

			}catch(NullPointerException e){}
		}

	}




	/*------------------------------------------------------
	 * Set methods data Values
	 * -----------------------------------------------------*/
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


	//public void setNameString(String value, boolean prop)
	//{
	//	if(value!=null && !value.equals("")){
	//		Experimenter e=new Experimenter();
	//		e.setLastName(value);
	//		setName(e,prop);
	//	}
	//}
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



	@Override
	public void saveData() 
	{
		if(data==null){
			data=new ExperimentModel(new Experiment(),new Experimenter(),"");
		}
		if(data.getExperiment()==null)
			data.setExperiment(new Experiment());
		
		if(data.getExperimenter()==null)
			data.setExperimenter(new Experimenter());
		
		//TODO input checker
		try{
			data.getExperiment().setDescription(description.getTagValue());
			description.dataSaved(true);
		}catch(Exception e){
			LOGGER.error("[DATA] can't read EXPERIMENT description input");
		}
		try{
			data.getExperiment().setType(getExperimentType(type.getTagValue()));
			type.dataSaved(true);
		}catch(Exception e){
			LOGGER.error("[DATA] can't read EXPERIMENT type input");
		}
		try{
			data.setProjectPartner(projectPartner.getTagValue());
			projectPartner.dataSaved(true);
		}catch(Exception e){
			LOGGER.error("[DATA] can't read EXPERIMENT project partner input");
		}
		try{
			data.setProjectName(projectName.getTagValue());
			projectName.dataSaved(true);
		}catch(Exception e){
			LOGGER.error("[DATA] can't read EXPERIMENT project name input");
		}
		try{
			data.setGroupName(group.getTagValue());
			group.dataSaved(true);
		}catch(Exception e){
			LOGGER.error("[DATA] can't read EXPERIMENT group input");
		}
		try{
			data.setExperimenter(parseExperimenter(expName.getTagValue()));
			expName.dataSaved(true);
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
	public List<TagData> getChangedTags() 
	{
		List<TagData> list = new ArrayList<TagData>();
		if(inputAt(projectName)) list.add(projectName);
		if(inputAt(projectPartner))list.add(projectPartner);
		if(inputAt(group))list.add(group);
		if(inputAt(type)) list.add(type);
		if(inputAt(description)) list.add(description);
		if(inputAt(expName)) list.add(expName);

		return list;
	}


}


