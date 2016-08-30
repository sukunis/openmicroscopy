package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.format;

import java.util.ArrayList;
import java.util.List;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.ExperimenterListModel;

import ome.xml.model.Experiment;
import ome.xml.model.Experimenter;

public class ExperimentContainer {

	Experiment experiment;
	Experimenter projectPartner;
	String projectName;
	String group;
	Experimenter experimenter;
//	ExperimenterListModel experimenterListModel;
//	List<Experimenter> expList;
	
	
	public ExperimentContainer()
	{
		experiment=null;
		projectPartner=null;
		experimenter=null;
	}
	
	public ExperimentContainer(ExperimentContainer orig)
	{
		experiment=orig.experiment;
		experimenter=orig.experimenter;
		projectPartner=orig.projectPartner;
	}
	
	public ExperimentContainer(Experiment exp, Experimenter exper, Experimenter projPartner)
	{
		experiment=exp;
		experimenter=exper;
		projectPartner=projPartner;
	}
	
	public ExperimentContainer(Experiment exp, Experimenter exper, String projPartner)
	{
		if(exp!=null)
			setExperiment(exp);
		if(exper!=null)
			setExperimenter(exper);
		if(projPartner!=null)
			setProjectPartner(projPartner);
	}

	public Experiment getExperiment() {
		return experiment;
	}

	public void setExperiment(Experiment experiment) {
		this.experiment = experiment;
	}
	
	public Experiment getContainerAsExp()
	{
		//TODO ids
		experiment.linkExperimenter(experimenter);
		experiment.linkExperimenter(projectPartner);
		return experiment;
	}

	public Experimenter getProjectPartner() {
		return projectPartner;
	}
	public String getProjectPartnerName()
	{
		if(projectPartner==null)
			return "";
		
		return projectPartner.getLastName();
	}

	public void setProjectPartner(String name)
	{
		if(projectPartner==null)
			projectPartner=new Experimenter();
		projectPartner.setLastName(name);
	}
	public void setProjectPartner(Experimenter projectPartner) {
		this.projectPartner = projectPartner;
	}

	public Experimenter getExperimenter() {
		return experimenter;
	}

	public void setExperimenter(Experimenter experimenter) {
		this.experimenter = experimenter;
	}
	

	public boolean testExperiment(Experiment e) 
	{
		if(experiment==null && e==null){
			experiment=new Experiment();
			return true;
		}
		if(experiment==null){
			experiment=e;
			return false;
		}else if(!experiment.getID().equals(e.getID())){
			experiment=e;
			return false;
		}
		return true;
	}

	public void setGroupName(String group) {
		this.group=group;		
	}

	public void setProjectName(String project) {
		this.projectName=project;		
	}

	public String getProjectName() {
		return projectName;
	}

	public String getGroupName() {
		return group;
	}



	
}
