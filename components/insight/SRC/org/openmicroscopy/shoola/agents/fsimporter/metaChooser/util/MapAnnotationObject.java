package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util;

import java.util.List;

import omero.gateway.model.MapAnnotationData;
import omero.model.NamedValue;

public class MapAnnotationObject {

	private String fileName;
	private MapAnnotationData mapAnnotation;

	public MapAnnotationObject(String fileName, MapAnnotationData map)
	{
		this.fileName=fileName;
		this.mapAnnotation=map;
	}
	public String getFileName() {
		return fileName;
	}

	public MapAnnotationData getMapAnnotation() {
		return mapAnnotation;
	}

	static public void printMapAnnotation(MapAnnotationData map)
	{
		System.out.println("\t mapAnnotation: ");
		
		List<NamedValue> values=(List<NamedValue>) map.getContent();
		for(NamedValue val:values){
			System.out.println("\t"+ val.name+": "+val.value);
		}
	}
	
	static public void printObject(MapAnnotationObject o)
	{
		System.out.println("\t file : "+o.getFileName());
		printMapAnnotation(o.getMapAnnotation());
	}
	
}
