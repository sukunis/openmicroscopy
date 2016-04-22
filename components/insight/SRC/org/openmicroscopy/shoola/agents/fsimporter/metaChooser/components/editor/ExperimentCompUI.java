package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor;

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

import ome.xml.model.Experiment;
import ome.xml.model.Experimenter;
import ome.xml.model.enums.EnumerationException;
import ome.xml.model.enums.ExperimentType;
import ome.xml.model.enums.handlers.ExperimentTypeEnumHandler;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.UOSMetadataLogger;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.TagData;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.ElementsCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.ModuleConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.TagNames;

import loci.formats.FormatException;
import loci.formats.MetadataTools;

public class ExperimentCompUI extends ElementsCompUI 
{

	
	private TagData type;
	private TagData description;
	//last name of experimenter
	private TagData name;
	
	// Project
	private TagData projectName;
	private TagData group;
	private TagData projectPartner;
	
	private List<TagData> tagList;
	
	private Experiment experiment;
	
	private boolean setFields;
	

	
	public ExperimentCompUI(ModuleConfiguration objConf)
	{
		
		initGUI();
		if(objConf==null)
			createDummyPane(false);
		else
			createDummyPane(objConf.getTagList(),false);
		
	}
	
	public ExperimentCompUI(Experiment _experiment, String idxExp, String idxExper)
	{
		experiment=_experiment;
		
		initGUI();
		if(experiment!=null){
			setGUIData();
		}
		else{
			if(idxExp==null )
				idxExp=MetadataTools.createLSID("Experiment", 0);
			
			if(idxExper==null)
				idxExper=MetadataTools.createLSID("Experimenter", 0);
			
			createNewExperiment(idxExp, idxExper);
			createDummyPane(false);
		}
	}
	
	public void createNewExperiment(String idxExp,String idxExper)
	{
		//create new one
		experiment=new Experiment();
		experiment.setID(idxExp);
		Experimenter experimenter=new Experimenter();
		
		experimenter.setID(idxExper);
		experiment.linkExperimenter(experimenter);
	}
	
	private void createNewElement() {
		experiment=new Experiment();
		Experimenter experimenter=new Experimenter();
		experiment.linkExperimenter(experimenter);
	}
	
	private void initTagList()
	{
		tagList=new ArrayList<TagData>();
		tagList.add(type);
		tagList.add(name);
		tagList.add(description);
		tagList.add(projectName);
		tagList.add(group);
		tagList.add(projectPartner);
	}
	
	
	private void setGUIData() 
	{
		if(experiment!=null) {
			try{ setDescription(experiment.getDescription(), ElementsCompUI.REQUIRED);}
			catch(NullPointerException e){}
			try{ setType(experiment.getType(), ElementsCompUI.REQUIRED);}
			catch(NullPointerException e){}
			try{ setName(experiment.getLinkedExperimenter().getLastName(), ElementsCompUI.REQUIRED);}
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
	public boolean addData(Experiment exp, boolean overwrite)
	{
		boolean conflicts=false;
		
		if(experiment!=null){
			if(exp!=null){
				String desc=exp.getDescription();
				ExperimentType type=exp.getType();
				Experimenter exper=exp.getLinkedExperimenter();
				addData(exper,overwrite);
				//TODO: save Partner
				//				String partner=
				if(overwrite){
					if(exp.getID()!=null && !exp.getID().equals(""))
						experiment.setID(exp.getID());
					if(desc!=null && !desc.equals("")) experiment.setDescription(desc);
					if(type!=null && !type.equals("")) experiment.setType(type);
					
//					if(exper==null){
//						experiment.linkExperimenter(exper);
//					}
//					else if(!exper.getLastName().equals("")){
//						experiment.getLinkedExperimenter().setLastName(exper.getLastName());
//					}
					LOGGER.info("[DATA] overwrite EXPERIMENT data");
				}else{
					if(experiment.getID()==null || experiment.getID().equals(""))
						experiment.setID(exp.getID());
					if(experiment.getDescription()==null || experiment.getDescription().equals("")) 
						experiment.setDescription(desc);
					if(experiment.getType()==null ) 
						experiment.setType(type);
					
//					if(exper==null){
//						experiment.linkExperimenter(exper);
//					}else if(experiment.getLinkedExperimenter().getLastName().equals("")){
//						experiment.getLinkedExperimenter().setLastName(exper.getLastName());
//					}
					LOGGER.info("[DATA] complete EXPERIMENT data");
				}
			}
		}else if(exp!=null){
			experiment=exp;
			LOGGER.info("[DATA] add EXPERIMENT data");
		}
		
		setGUIData();
			
		return conflicts;
	}
	
	public boolean addData(Experimenter exper, boolean overwrite)
	{
		boolean conflicts=false;
		if(experiment!=null && experiment.getLinkedExperimenter()!=null){ 
			if(exper!=null){
				String name=exper.getLastName();
				if(overwrite){
					if(exper.getID()!=null && !exper.getID().equals(""))
						experiment.getLinkedExperimenter().setID(exper.getID());
					if(!name.equals("")) experiment.getLinkedExperimenter().setLastName(name);
					LOGGER.info("[DATA] overwrite EXPERIMENTER data");
				}else{
					if(experiment.getLinkedExperimenter().getID()==null || experiment.getLinkedExperimenter().getID().equals(""))
						experiment.getLinkedExperimenter().setID(exper.getID());
					if(experiment.getLinkedExperimenter().getLastName()==null || experiment.getLinkedExperimenter().getLastName().equals(""))
						experiment.getLinkedExperimenter().setLastName(name);
					LOGGER.info("[DATA] complete EXPERIMENTER data");
				}
			}
		}else if(exper!=null){
			experiment.linkExperimenter(exper);
			LOGGER.info("[DATA] add EXPERIMENTER data");
		}
		setGUIData();
		
		
		return conflicts;
	}
	
	
	private void readGUIInput() throws Exception
	{
		if(experiment==null){
			createNewExperiment("", "");
		}
		//TODO input checker
		try{
			experiment.setDescription(description.getTagValue());
		}catch(Exception e){
			LOGGER.severe("[DATA] can't read EXPERIMENT description input");
		}
		try{
			experiment.setType(getExperimentType(type.getTagValue()));
		}catch(Exception e){
			LOGGER.severe("[DATA] can't read EXPERIMENT type input");
		}
		try{
			experiment.getLinkedExperimenter().setLastName(name.getTagValue());
		}catch(Exception e){
			LOGGER.severe("[DATA] can't read EXPERIMENT experimenter input");
		}
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
		return (result || setFields);
	}
	
	public void setFieldsExtern(boolean val)
	{
		setFields= setFields || val;
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
		addTagToGUI(name);
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
		setFields=false;
		
//		globalPane.setMinimumSize(globalPane.getMinimumSize());
//		setMinimumSize(getMinimumSize());
//		
//		revalidate();
	}

	@Override
	public void buildExtendedComponents() {
		// TODO Auto-generated method stub
		
	}

	public void clearDataValues() 
	{
		clearTagValue(type);
		clearTagValue(description);
		clearTagValue(name);
		clearTagValue(projectName);
		clearTagValue(projectPartner);
		clearTagValue(group);
		
	}
	
	
	
	public Experiment getData() throws Exception
	{
//		if(userInput()){
//			LOGGER.info("[DEBUG] read GUI input (EXPERIMENT)");
			readGUIInput();
//		}
		return experiment;
	}
	
	

	public void setType(ExperimentType value, boolean prop)
	{
		String val= (value != null) ? value.getValue():"";
		if(type == null) 
			type = new TagData(TagNames.TYPE+": ",val,prop,TagData.COMBOBOX,getNames(ExperimentType.class));
		else 
			type.setTagValue(val,prop);	
	}
	
	public ExperimentType getExperimentType(String value)  throws FormatException
	  {
	    ExperimentTypeEnumHandler handler = new ExperimentTypeEnumHandler();
	    try {
	      return (ExperimentType) handler.getEnumeration(value);
	    }
	    catch (EnumerationException e) {
	      throw new FormatException("ExperimentType creation failed", e);
	    }
	  }
	
	public void setDescription(String value, boolean prop)
	{
		if(description == null) 
			description = new TagData(TagNames.DESC+": ",value,prop,TagData.TEXTPANE);
		else 
			description.setTagValue(value,prop);	
	}
	
	
	public void setName(String value, boolean prop)
	{
		if(name == null) 
			name = new TagData(TagNames.EXPNAME+": ",value,prop,TagData.TEXTFIELD);
		else 
			name.setTagValue(value,prop);
	}
	
	public void setProjectName(String value, boolean prop)
	{
		if(projectName == null) 
			projectName = new TagData(TagNames.PROJECTNAME+": ",value,prop,TagData.TEXTFIELD);
		else 
			projectName.setTagValue(value,prop);
	}
	
	public void setGroupName(String value, boolean prop)
	{
		if(group == null) 
			group = new TagData(TagNames.GROUP+": ",value,prop,TagData.TEXTFIELD);
		else 
			group.setTagValue(value,prop);
	}
	
	public void setProjectPartner(String value, boolean prop)
	{
		if(projectPartner == null) 
			projectPartner = new TagData(TagNames.PROJECTPARTNER+": ",value,prop,TagData.TEXTFIELD);
		else 
			projectPartner.setTagValue(value,prop);
	}
	
	public Experimenter getProjectPartnerAsExp()
	{
		Experimenter e=new Experimenter();
		e.setLastName(projectPartner.getTagValue());
		return e;
	}
	
	
	
	
	
	
//	public void printValues()
//	{
//		String out=getID()+":\n\tType: "+getType()+"\n\tDescription: "+getDescription()+
//				"\n\t"+getRefExperimenterID()+"\n\tLastName: "+getRefExperimenterName();
//		System.out.println(out);
//	}
	@Override
	public void createDummyPane(boolean inactive) 
	{
		setProjectName(null, OPTIONAL);
		setGroupName(null, OPTIONAL);
		setProjectPartner(null, OPTIONAL);
		
		setType(null, ElementsCompUI.OPTIONAL);
		setDescription(null, ElementsCompUI.OPTIONAL);
		setName(null,OPTIONAL);
		
		projectName.setEnable(false);
		group.setEnable(false);
		
		if(inactive){
			
			projectPartner.setEnable(false);
			name.setEnable(false);
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
		if(experiment==null && list!=null && list.size()>0)
			createNewElement();
		for(int i=0; i<list.size();i++){
			TagConfiguration t=list.get(i);
			String name=t.getName();
			String val=t.getValue();
			boolean prop=t.getProperty()!= null ? Boolean.parseBoolean(t.getProperty()):
				OPTIONAL;
			if(name!=null){
				switch (name) {
				case TagNames.TYPE:
					try{
						ExperimentType value= getExperimentType(val);
						setType(value, prop);
						experiment.setType(value);
					}catch(Exception e){
						setType(null, prop);
					}
					type.setVisible(true);
					break;
				case TagNames.DESC:
					try{
						setDescription(val, prop);
						experiment.setDescription(val);
					}catch(Exception e){
						setDescription(null, prop);
					}
					description.setVisible(true);
					break;
				case TagNames.GROUP:
					setGroupName(null, prop);
					group.setVisible(true);
					break;
				case TagNames.EXPNAME:
					setName(null, prop);
					this.name.setVisible(true);
					break;
				case TagNames.PROJECTNAME:
					setProjectName(null, prop);
					projectName.setVisible(true);
					break;
				case TagNames.PROJECTPARTNER:
					setProjectPartner(null, prop);
					projectPartner.setVisible(true);
					break;
				default:
					LOGGER.warning("[CONF] unknown tag: "+name );break;
				}
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
		if(isActive(name)) list.add(name);

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




}


