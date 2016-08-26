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
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.format.ExperimentContainer;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules.ElementsCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.ModuleConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.TagNames;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.ExperimenterListModel;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagData;

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
	
	
	private Experiment experiment;
	
	private ExperimentContainer expContainer;
	
	
	

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
		tagList.add(expName);
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
//			try{ setName(experiment.getLinkedExperimenter().getLastName(), ElementsCompUI.REQUIRED);}
			try{
//				String [] name= {experiment.getLinkedExperimenter().getFirstName(),
//					experiment.getLinkedExperimenter().getLastName()};
//				setName(name, ElementsCompUI.REQUIRED);
				setName(experiment.getLinkedExperimenter(),ElementsCompUI.REQUIRED);
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
	public boolean addData(Experiment exp, boolean overwrite)
	{
		boolean conflicts=false;
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
	
	public boolean addData(Experimenter exper, boolean overwrite)
	{
		boolean conflicts=false;
		if(overwrite){
			replaceData(exper);
		LOGGER.info("[DATA] -- replace EXPERIMENTER data");
	}	else
			try {
				completeData(exper);
				LOGGER.info("[DATA] -- complete EXPERIMENTER data");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		setGUIData();
		
		
		return conflicts;
	}
	
	public void addProjectPartner(Experimenter e, boolean overwrite) 
	{
		if(overwrite){
			System.out.println("Overwrite:set projectPartner");
			setProjectPartner(e.getLastName(), OPTIONAL);
		}
	}
	
	/**
	 * Overwrites only tags that are not set
	 * @param exper
	 * @throws Exception 
	 */
	public void completeData(Experiment e) throws Exception
	{
		//copy input fields
		Experiment copyIn=null;
		if(experiment!=null){
			getData();//experiment+experimenter
			copyIn=new Experiment(experiment);
		}
		
		replaceData(e);
		
		// set input field values again
		if(copyIn!=null){
			String desc=copyIn.getDescription();
			ExperimentType type=copyIn.getType();
			
			if(desc!=null && !desc.equals("")) experiment.setDescription(desc);
			if(type!=null && !type.equals("")) experiment.setType(type);
			
			Experimenter exper=copyIn.getLinkedExperimenter();
			if(exper!=null){
				String nameL=exper.getLastName();
				String nameF=exper.getFirstName();
				if(!nameL.equals("")) experiment.getLinkedExperimenter().setLastName(nameL);
				if(!nameF.equals("")) experiment.getLinkedExperimenter().setFirstName(nameF);
				
			}
		}
	}
	
	
	/**
	 * Overwrites only tags that are not set
	 * @param exper
	 * @throws Exception 
	 */
	public void completeData(Experimenter exper) throws Exception
	{
		// copy input fields
		Experimenter copyIn=null;
		if(experiment!=null && experiment.getLinkedExperimenter()!=null){
			getData();
			copyIn=new Experimenter(experiment.getLinkedExperimenter());
		}
		replaceData(exper);
		
		// set input field values again
		if(copyIn!=null){
			String nameL=copyIn.getLastName();
			String nameF=copyIn.getFirstName();
			if(!nameL.equals("")) experiment.getLinkedExperimenter().setLastName(nameL);
			if(!nameF.equals("")) experiment.getLinkedExperimenter().setFirstName(nameF);
			
		}
	}
	
	/**
	 * Replace intern experimenter object by given experimenter. All manuell input data are lost. 
	 * @param exper
	 */
	public void replaceData(Experimenter exper)
	{
		if(exper!=null){
			experiment.linkExperimenter(exper);
			
		}
	}
	
	/**
	 * Replace intern experiment object by given experiment. All manuell input data are lost. 
	 * @param e
	 */
	public void replaceData(Experiment e)
	{
		if(e!=null){
			experiment=e;
		}
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
			LOGGER.error("[DATA] can't read EXPERIMENT description input");
		}
		try{
			experiment.setType(getExperimentType(type.getTagValue()));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read EXPERIMENT type input");
		}
		try{
//			experiment.getLinkedExperimenter().setFirstName(expName.getTagValue(0));
//			experiment.getLinkedExperimenter().setLastName(expName.getTagValue(1));
			// first element should be the import user
//			if(expName.getListValues().get(0)!=null)
//				System.out.println("Link to experimenter");
			experiment.linkExperimenter(expName.getListValues().get(0));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read EXPERIMENT experimenter input");
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
		clearTagValue(expName);
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
	
	public List<Experimenter> getExperimenterList()
	{
		return expName.getListValues();
	}

	private void setType(ExperimentType value, boolean prop)
	{
		String val= (value != null) ? value.getValue():"";
		if(type == null) 
			type = new TagData(TagNames.TYPE,val,prop,TagData.COMBOBOX,getNames(ExperimentType.class));
		else 
			type.setTagValue(val,prop);	
	}
	
	public ExperimentType getExperimentType(String value)  throws FormatException
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
	

	
	public void setName(Experimenter value, boolean prop)
	{
		if(expName == null){ 
			
			ExperimenterListModel m=new ExperimenterListModel();
			if(value!=null){
				m.addElement(value);
			}
			expName = new TagData(TagNames.EXPNAME,m,prop,TagData.LIST);
		}
		else{ 
			expName.setTagValue(value);
		}
	}
	
	public void setProjectName(String value, boolean prop)
	{
		if(projectName == null) 
			projectName = new TagData(TagNames.PROJECTNAME,value,prop,TagData.TEXTFIELD);
		else 
			projectName.setTagValue(value,prop);
	}
	
	public void setGroupName(String value, boolean prop)
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
	
	public Experimenter getProjectPartnerAsExp()
	{
		Experimenter e=new Experimenter();
		e.setLastName(projectPartner.getTagValue());
		return e;
	}
	

	@Override
	public void createDummyPane(boolean inactive) 
	{
		setProjectName(null, OPTIONAL);
		setGroupName(null, OPTIONAL);
		setProjectPartner(null, OPTIONAL);
		
		setType(null, ElementsCompUI.OPTIONAL);
		setDescription(null, ElementsCompUI.OPTIONAL);
//		setName(null,OPTIONAL);
		setName(null,OPTIONAL);
		
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

	public void parseProjectPartner(MapAnnotation map) 
	{
		MapPairs mp=map.getValue();
		List<MapPair> listMP=mp.getPairs();
		switch (map.getNamespace()) {
		case OMEStore.NS_2016_06_07:
			parseFromMapAnnotation2016_06_07(listMP);
			break;

		default:
			LOGGER.warn("[DATA] Namespace is not supported for parsing sample data");
			break;
		}
	}

	private void parseFromMapAnnotation2016_06_07(List<MapPair> listMP) 
	{
		for(MapPair obj:listMP){
			switch (obj.getName()) {
			case PROJPARTNER_MAPLABEL:
				System.out.println("MAP:set projectPartner");
				setProjectPartner(obj.getValue(), OPTIONAL);
				break;		
			default:
				LOGGER.info("[DATA] unknown Label for Project Partner MapAnnotation: "+obj.getName());
				break;
			}
		}
	}
	
	/**
	 * Update tags with val from list
	 */
	public void update(List<TagData> list) 
	{
		for(TagData t: list){
			if(t.valueChanged()){
				System.out.println("EXP: Update Tag "+t.getTagName()+" = "+t.getTagValue());
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
		case TagNames.GROUP:
			setGroupName(null, prop);
			group.setVisible(true);
			break;
		case TagNames.EXPNAME:
			setName(null, prop);
			this.expName.setVisible(true);
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
			LOGGER.warn("[CONF] unknown tag: "+name );break;
		}
	}



}


