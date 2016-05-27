package org.openmicroscopy.shoola.agents.fsimporter.metaChooser;

import com.sun.xml.internal.fastinfoset.util.StringArray;

import ome.xml.model.Experimenter;
import omero.gateway.model.ExperimenterData;
import omero.gateway.model.GroupData;
import omero.model.Project;

public class ImportUserData 
{
	private GroupData group;
	private Project project;
	private ExperimenterData experimenter;
	
	public ImportUserData(GroupData group,Project project,ExperimenterData experimenter)
	{
		this.group=group;
		this.project=project;
		this.experimenter=experimenter;
		
	}
	
	public String getGroup()
	{
		return group.getName();
	}
	
	public String getProject()
	{
		return project.getName().getValue();
		
	}
	

	public Experimenter getUser()
	{
		Experimenter e= new Experimenter();
		e.setFirstName(experimenter.getFirstName());
		e.setLastName(experimenter.getLastName());
		return e;
	}
	public String getUserName()
	{
		return experimenter.getLastName();
	}
	
	public String[] getUserFullName()
	{
		String[] name={experimenter.getFirstName(),experimenter.getLastName()};
		return name;
	}
}
