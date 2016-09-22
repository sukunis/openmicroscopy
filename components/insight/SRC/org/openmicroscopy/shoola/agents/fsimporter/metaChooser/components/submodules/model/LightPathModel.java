package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model;

import java.util.ArrayList;
import java.util.List;

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

	private LightPath element;

	private List<Object> availableElem;

	public LightPathModel(){}

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
	public void addData(LightPath newElem,boolean overwrite) throws Exception
	{
		if(overwrite){
			replaceData(newElem);
			LOGGER.info("[DATA] -- replace LightPath data");
		}else{
			completeData(newElem);
			LOGGER.info("[DATA] -- complete LightPath data");
		}
	}

	/**
	 * Overwrite data with given data
	 * @param newElem
	 */
	private void replaceData(LightPath newElem)
	{
		if(newElem!=null){
			element=newElem;
		}
	}

	/**
	 * Complete existing data with data from newElem
	 * @param newElem
	 * @throws Exception
	 */
	private void completeData(LightPath newElem) throws Exception
	{
		//copy existing data
		LightPath copyIn=null;
		if(element!=null){
			copyIn=new LightPath(element);
		}
		replaceData(newElem);

		List<Filter> excitRef =copyIn.copyLinkedExcitationFilterList();
		List<Filter> emisRef = copyIn.copyLinkedEmissionFilterList();
		Dichroic dichroic=copyIn.getLinkedDichroic();

		if(excitRef!=null){
			if(element.sizeOfLinkedExcitationFilterList()>0){
				// search by id
				for(int i=0; i<excitRef.size(); i++){
					if(!element.copyLinkedExcitationFilterList().contains(excitRef.get(i))){
						element.linkExcitationFilter(excitRef.get(i));
					}
				}
			}else{
				for(int i=0; i<excitRef.size(); i++){
					element.linkExcitationFilter(excitRef.get(i));
				}
			}
		}

		if(emisRef!=null){
			if(element.sizeOfLinkedEmissionFilterList()>0){
				// search by id
				for(int i=0; i<emisRef.size(); i++){
					if(!element.copyLinkedEmissionFilterList().contains(emisRef.get(i))){
						element.linkEmissionFilter(emisRef.get(i));
					}
				}
			}else{
				for(int i=0; i<emisRef.size(); i++){
					element.linkEmissionFilter(emisRef.get(i));
				}
			}
		}

		if(dichroic!=null) element.linkDichroic(dichroic);
	}

	public List<Object> getList()
	{
		return availableElem;
	}

	public LightPath getLightPath() {
		return element;
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
	 * LightPath order is Exitation Filter -> Dichroic -> Dichroic/Emission filter
	 * @param list
	 */
	public void createLightPath(List<Object> list)
	{
		if(list!=null && !list.isEmpty()){
			element=new LightPath();
			int linkType=1;
			for(Object f : list)
			{
				Dichroic pD=element.getLinkedDichroic();
				boolean primDNotExists= pD==null ? true : false ;

				// Dichroic
				if(f instanceof Dichroic){
					linkType=2;
					// primary dichroic exists?
					if(primDNotExists){
						element.linkDichroic((Dichroic) f);
					}else{
						LOGGER.warn("primary Dichroic still exists! [LightPathCompUI::createLightPath]");
						element.linkEmissionFilter(MetaDataModel.convertDichroicToFilter((Dichroic)f));
					}

				}else{

					String	type= ((Filter) f).getType()!=null ? ((Filter) f).getType().toString() : "";
					//filters that comes before and dichroic are exitation filters by definition
					if(	!type.equals(FilterType.DICHROIC.getValue()) && 
							linkType==1){
						element.linkExcitationFilter((Filter) f);
					}else{// link additional dichroic as emission filter
						linkType=2;

						if( primDNotExists){
							element.linkDichroic(MetaDataModel.convertFilterToDichroic((Filter) f));
						}else{
							element.linkEmissionFilter((Filter) f);
						}
					}
				}
			}
		}
	}

}
