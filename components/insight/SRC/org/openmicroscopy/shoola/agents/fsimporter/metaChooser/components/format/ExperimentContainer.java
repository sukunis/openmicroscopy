package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.format;

import ome.xml.model.Experiment;
import ome.xml.model.Experimenter;

public class ExperimentContainer {

	Experiment experiment;
	Experimenter projectPartner;
	Experimenter experimenter;
	
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
		experiment=exp;
		experimenter=exper;
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

	public boolean testExperimenter(Experimenter exp) 
	{ 
		if(experimenter==null && exp==null){
			experimenter=new Experimenter();
			return true;
		}
		if(experimenter==null){
			experimenter=exp;
			return false;
		}else if(!experimenter.getID().equals(exp.getID())){
			experimenter=exp;
			return false;
		}
		return true;
	}
}
