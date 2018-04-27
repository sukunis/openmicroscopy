package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ome.xml.model.Dichroic;
import ome.xml.model.Filter;
import ome.xml.model.FilterSet;
import ome.xml.model.LightPath;

import org.openmicroscopy.shoola.util.MonitorAndDebug;
import org.slf4j.LoggerFactory;


/**
 * Works for xsi:schemaLocation="http://www.openmicroscopy.org/Schemas/OME/2015-01 
 * @author Kunis
 *
 */
public class LightPathModel 
{
	private static final org.slf4j.Logger LOGGER =
			LoggerFactory.getLogger(LightPathModel.class);

	private List<LightPath> element;


	/** Map of key-value pairs like [FilterType]:[Nr]:TagName, TagVal **/
	private List<HashMap<String,String>> maps;

	private List<FilterSet> filtersets;

	public LightPathModel()
	{
		element=new ArrayList<LightPath>();
		filtersets = new ArrayList<FilterSet>();
		maps=new ArrayList<HashMap<String,String>>();
	}

	public LightPathModel(LightPathModel orig)
	{
		element=orig.element;
		filtersets=orig.filtersets;
		maps=orig.maps;
	}
	
	/**
	 * Return map of changes
	 * @param i
	 * @return
	 */
	public HashMap<String,String> getMap(int i)
	{
		if(i>=maps.size())
			return null;
		return maps.get(i);
	}
	
	/**
	 * Set map of changes.
	 * @param map
	 * @param i
	 */
	public void setMap(HashMap<String,String> map,int i)
	{
		
		if(i>=maps.size())
			expandList(maps.size(),i);

		maps.set(i, map);
	}

	/**
	 * Add filterset data (only replaced functionality)
	 * @param filterSet
	 * @param overwrite should be true
	 * @param i
	 */
	public void addData(FilterSet filterSet, boolean overwrite, int i) 
	{
		if(!overwrite) {
			LOGGER.info("[DATA] -- Attention: filterset data could only be replaced");
		}
		replaceData(filterSet,i);
		LOGGER.info("[DATA] -- replace FilterSet data");
	}

	/**
	 * @param filterSet
	 * @param i
	 */
	private void replaceData(FilterSet filterSet, int i) 
	{
		if(filterSet==null)
			return;
		if(filtersets.size()<=i){
			expandList(filtersets.size(),i);
		}
		
		filtersets.set(i, new FilterSet(filterSet));
		
	}

	/**
	 * Overwrite lightpath data. Caller class has to handle notification about the changes.
	 * @param newElem
	 * @param overwrite should be true
	 * @throws Exception
	 */
	public void addData(LightPath newElem,boolean overwrite,int i) throws Exception
	{
		
		if(!overwrite) {
			LOGGER.info("[DATA] -- Attention: LightPath data could only be replaced");
		}
		
		replaceData(newElem,i);
		LOGGER.info("[DATA] -- replace LightPath data");
		
	}

	/**
	 * Overwrite data with given data
	 * @param newElem
	 */
	private void replaceData(LightPath newElem,int i)
	{
		if(newElem==null)
			return;
		
		if(element.size()<=i){
			expandList(element.size(),i);
		}
		element.set(i,new LightPath(newElem));
	}

	

	
	/** 
	 * @param i index of lightpath
	 * @return lightpath at index i in element list
	 */
	public LightPath getLightPath(int i) 
	{
		if(i>=element.size())
			return null;
		return element.get(i);
	}

	

	
	/**
	 * If index exits size, expand elements and settings list
	 * @param size
	 * @param index
	 */
	private void expandList(int size,int index) 
	{
		for(int i=size;i<index+1;i++){
			element.add(new LightPath());
			filtersets.add(null);
			maps.add(new HashMap<String,String>());
		}
	}

	/**
	 * @return size of element list
	 */
	public int getNumberOfLightPaths() 
	{
		if(element==null)
			return 0;
		
		return element.size();
	}

	/**
	 * Remove lightpath at index from element list
	 * @param index
	 */
	public void remove(int index) {
		if(element!=null && !element.isEmpty())
			element.remove(index);		
	}

	/**
	 *  replace lightPaths with changed lightPaths
	 * @param changesLightPath
	 */
	public void update(List<LightPath> changesLightPath) 
	{
		if(changesLightPath==null){
			MonitorAndDebug.printConsole("\t no changes for lightPath");
			return;
		}
		int index=0;
		for(LightPath lp:changesLightPath)
		{
			if(maps.size()>=index)
				maps.add(new HashMap<String,String>());
			if(lp!=null && element.size()>index){
				element.set(index, lp);
			}
			
			//update map annotation
			int i=1;
			for(Filter f: lp.copyLinkedExcitationFilterList()){
				String id="[Excitation Filter]:["+i+"]:";
				maps.get(index).put(id+"Model", f.getModel());
				maps.get(index).put(id+"Manufactur", f.getManufacturer());
				maps.get(index).put(id+"Type", (f.getType()==null?"": f.getType().getValue()));
				maps.get(index).put(id+"FilterWheel", f.getFilterWheel());
				
				i++;
			}
			
			Dichroic d= lp.getLinkedDichroic();
			if(d!=null){
				maps.get(index).put("[Dichroic]:["+i+"]:", d.getModel());
				i++;
			}
			
			for(Filter f: lp.copyLinkedEmissionFilterList()){
				String id="[Emmission Filter]:["+i+"]:";
				maps.get(index).put(id+"Model", f.getModel());
				maps.get(index).put(id+"Manufactur", f.getManufacturer());
				maps.get(index).put(id+"Type",(f.getType()==null?"": f.getType().getValue()));
				maps.get(index).put(id+"FilterWheel", f.getFilterWheel());
				
				i++;
			}
			
			
			index++;
		}
	}

	/**
	 * 
	 * @param i
	 * @return filterset at index i
	 */
	public FilterSet getFilterSet(int i) {
		if(i>filtersets.size())
			return null;
	
		return filtersets.get(i);
	}

}
