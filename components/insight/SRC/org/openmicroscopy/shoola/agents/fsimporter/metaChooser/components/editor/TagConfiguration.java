package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor;

public class TagConfiguration 
{
	private String name;
	private String value;
	private String property;
	
	public TagConfiguration(String name, String value, String property)
	{
		this.name=name;
		this.value=value;
		this.property=property;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}
	
}
