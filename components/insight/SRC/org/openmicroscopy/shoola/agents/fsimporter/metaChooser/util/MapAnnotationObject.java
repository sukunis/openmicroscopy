package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util;

import java.util.ArrayList;
import java.util.List;

import omero.gateway.model.MapAnnotationData;
import omero.model.NamedValue;

public class MapAnnotationObject {

	private String fileName;
	private List<MapAnnotationData> mapAnnotation;

	public MapAnnotationObject(String fileName, MapAnnotationData map)
	{
		this.fileName=fileName;
		this.mapAnnotation=new ArrayList<>();
		this.mapAnnotation.add(map);
	}
	
	public MapAnnotationObject(String fileName, List<MapAnnotationData> maps)
	{
		this.fileName=fileName;
		this.mapAnnotation=maps;
	}
	
	
	public String getFileName() {
		return fileName;
	}

	public MapAnnotationData getMapAnnotation(int i) {
		return mapAnnotation.get(i);
	}
	
	public List<MapAnnotationData> getMapAnnotationList() {
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
		if(o==null)
			return;
		System.out.println("\t file : "+o.getFileName());
		List<MapAnnotationData> list=o.getMapAnnotationList();
		for(MapAnnotationData m:list){
			printMapAnnotation(m);
		}
	}
	
}
