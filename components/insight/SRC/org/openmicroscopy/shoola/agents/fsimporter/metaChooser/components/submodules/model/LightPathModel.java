package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ome.xml.model.Dichroic;
import ome.xml.model.Filter;
import ome.xml.model.LightPath;
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

	private List<Object> availableElem;

	private List<HashMap<String,String>> maps;

	public LightPathModel()
	{
		element=new ArrayList<LightPath>();
		maps=new ArrayList<HashMap<String,String>>();
	}

	public LightPathModel(LightPathModel orig)
	{
		element=orig.element;
		availableElem=orig.availableElem;
		maps=orig.maps;
	}
	
	
	public HashMap<String,String> getMap(int i)
	{
		if(i>=maps.size())
			return null;
		return maps.get(i);
	}
	
	public void setMap(HashMap<String,String> map,int i)
	{
		
		if(i>=maps.size())
			expandList(maps.size(),i);

		maps.set(i, map);
	}

	/**
	 * Overwrite or complete data. Caller class has to handle notification about the changes.
	 * @param newElem
	 * @param overwrite
	 * @throws Exception
	 */
	public void addData(LightPath newElem,boolean overwrite,int i) throws Exception
	{
		System.out.println("# LightPathModel::addData()");
		if(newElem==null)
			return;
		
		if(element.size()<=i){
			expandList(element.size(),i);
		}
		
		if(overwrite){
			replaceData(newElem,i);
			LOGGER.info("[DATA] -- replace LightPath data");
		}else{
			completeData(newElem,i);
			LOGGER.info("[DATA] -- complete LightPath data");
		}
	}

	/**
	 * Overwrite data with given data
	 * @param newElem
	 */
	private void replaceData(LightPath newElem,int i)
	{
		if(newElem!=null){
			element.set(i,new LightPath(newElem));
		}
	}

	/**
	 * Complete existing data with data from newElem
	 * @param newElem
	 * @throws Exception
	 */
	private void completeData(LightPath newElem,int i) throws Exception
	{
		//copy existing data
		LightPath copyIn=null;
		if(element!=null){
			copyIn=new LightPath(element.get(i));
		}
		replaceData(newElem,i);

		List<Filter> excitRef =copyIn.copyLinkedExcitationFilterList();
		List<Filter> emisRef = copyIn.copyLinkedEmissionFilterList();
		Dichroic dichroic=copyIn.getLinkedDichroic();

		if(excitRef!=null){
			if(element.get(i).sizeOfLinkedExcitationFilterList()>0){
				// search by id
				for(int j=0; j<excitRef.size(); j++){
					if(!element.get(i).copyLinkedExcitationFilterList().contains(excitRef.get(j))){
						element.get(i).linkExcitationFilter(excitRef.get(j));
					}
				}
			}else{
				for(int j=0; j<excitRef.size(); j++){
					element.get(i).linkExcitationFilter(excitRef.get(j));
				}
			}
		}

		if(emisRef!=null){
			if(element.get(i).sizeOfLinkedEmissionFilterList()>0){
				// search by id
				for(int j=0;j<emisRef.size(); j++){
					if(!element.get(i).copyLinkedEmissionFilterList().contains(emisRef.get(j))){
						element.get(i).linkEmissionFilter(emisRef.get(j));
					}
				}
			}else{
				for(int j=0; j<emisRef.size(); j++){
					element.get(i).linkEmissionFilter(emisRef.get(j));
				}
			}
		}

		if(dichroic!=null) element.get(i).linkDichroic(dichroic);
	}

	

	public LightPath getLightPath(int i) 
	{
		if(i>=element.size())
			return null;
		return element.get(i);
	}

	
//	public List<Object> getList()
//	{
//		return availableElem;
//	}
//	public void addFilterToList(List<Filter> list)
//	{
//		if(list==null || list.size()==0)
//			return;
//
//		if(availableElem==null){
//
//			availableElem=new ArrayList<Object>();
//		}
//		for(int i=0; i<list.size(); i++){
//			availableElem.add(list.get(i));
//		}
//	}
//
//	public void addDichroicToList(List<Dichroic> list)
//	{
//		if(list==null || list.size()==0)
//			return;
//
//		if(availableElem==null){
//
//			availableElem=new ArrayList<Object>();
//		}
//		for(int i=0; i<list.size(); i++){
//			availableElem.add(list.get(i));
//		}
//	}
//	public void clearList()
//	{
//		availableElem=null;
//	}
	
	/**
	 * If index exits size, expand elements and settings list
	 * @param size
	 * @param index
	 */
	private void expandList(int size,int index) 
	{
		for(int i=size;i<index+1;i++){
			element.add(new LightPath());
			maps.add(new HashMap<String,String>());
		}
	}
	

	
	

	public int getNumberOfLightPaths() 
	{
		if(element==null)
			return 0;
		
		return element.size();
	}

	public void remove(int index) {
		if(element!=null && !element.isEmpty())
			element.remove(index);		
	}

	// replace lightPaths with changed lightPaths
	public void update(List<LightPath> changesLightPath) 
	{
		if(changesLightPath==null){
			System.out.println("\t no changes for lightPath");
			return;
		}
		int index=0;
		for(LightPath lp:changesLightPath)
		{
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
				maps.get(index).put("["+d.getID()+"]:["+i+"]:", d.getModel());
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

	

}
