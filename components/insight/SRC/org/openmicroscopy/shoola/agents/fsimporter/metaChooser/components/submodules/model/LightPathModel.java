package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model;

import java.util.ArrayList;
import java.util.List;

import ome.xml.model.Channel;
import ome.xml.model.Detector;
import ome.xml.model.Dichroic;
import ome.xml.model.ExcitationFilterRef;
import ome.xml.model.Filter;
import ome.xml.model.LightPath;
import ome.xml.model.enums.FilterType;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.MetaDataModel;
import org.slf4j.LoggerFactory;

public class LightPathModel 
{
	private static final org.slf4j.Logger LOGGER =
			LoggerFactory.getLogger(LightPathModel.class);

	private List<LightPath> element;

	private List<Object> availableElem;

	public LightPathModel()
	{
		element=new ArrayList<LightPath>();
	}

	public LightPathModel(LightPathModel orig)
	{
		element=orig.element;
		availableElem=orig.availableElem;
	}

	/**
	 * Overwrite or complete data. Caller class has to handle notification about the changes.
	 * @param newElem
	 * @param overwrite
	 * @throws Exception
	 */
	public void addData(LightPath newElem,boolean overwrite,int i) throws Exception
	{
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
			element.set(i,newElem);
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

	public List<Object> getList()
	{
		return availableElem;
	}

	public LightPath getLightPath(int i) {
		return element.get(i);
	}

	public void addFilterToList(List<Filter> list)
	{
		if(list==null || list.size()==0)
			return;

		if(availableElem==null){

			availableElem=new ArrayList<Object>();
		}
		for(int i=0; i<list.size(); i++){
			availableElem.add(list.get(i));
		}
	}

	public void addDichroicToList(List<Dichroic> list)
	{
		if(list==null || list.size()==0)
			return;

		if(availableElem==null){

			availableElem=new ArrayList<Object>();
		}
		for(int i=0; i<list.size(); i++){
			availableElem.add(list.get(i));
		}
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
		}
	}
	

	
	public void clearList()
	{
		availableElem=null;
	}

	public int getNumberOfLightPaths() 
	{
		if(element==null)
			return 0;
		
		return element.size();
	}

}
