package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor;

import ome.units.unit.Unit;

public class TagConfiguration 
{
	private String name;
	private String value;
	private Unit unit;
	private String property;
	
	public TagConfiguration(String name, String value, Unit unit,String property)
	{
		this.name=name;
		this.value=value;
		this.unit=unit;
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

	public String getUnitSymbol() {
		return unit.getSymbol();
	}
	
	public Unit getUnit() {
		return unit;
	}

	public void setUnit(Unit unit) {
		this.unit = unit;
	}
	
}
