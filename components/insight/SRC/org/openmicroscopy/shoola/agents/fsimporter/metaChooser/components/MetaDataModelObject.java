package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components;

import java.util.ArrayList;
import java.util.List;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules.ElementsCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules.ExperimentCompUI;

/**
 * Model for a image could be a single model or (for series data) a list of models
 * @author Kunis
 *
 */
public class MetaDataModelObject 
{
	private boolean seriesData;
	private List<MetaDataModel> list;
	
	public MetaDataModelObject()
	{
		seriesData=false;
		list=new ArrayList<MetaDataModel>();
	}
	
	public MetaDataModelObject(boolean series, List<MetaDataModel> mList)
	{
		seriesData=series;
		list=mList;
	}

	public boolean isSeriesData() {
		return seriesData;
	}

	public void setSeriesData(boolean seriesData) {
		this.seriesData = seriesData;
	}

	public List<MetaDataModel> getList() {
		return list;
	}

	public void setList(List<MetaDataModel> list) {
		this.list = list;
	}
	
	/**
	 * 
	 * @return true, if some changes of datas available
	 */
	public boolean hasToUpdate()
	{
		boolean res=false;
		if(list==null || list.isEmpty()){
			return res;
		}
		for(MetaDataModel m: list){
			res= res || m.noticUserInput();
		}
		return res;
	}

	// TODO: at the moment not for series data
	public void updateData(MetaDataModelObject newData) 
	{
		List<MetaDataModel> modelList_new=newData.getList();
//		list.get(0).updateData(modelList_new.get(0));
	}

	

	public void isUpToDate(boolean b) 
	{
//		System.out.println("# MetaDataModel::isUpToDate("+b+")");
//		if(list==null || list.isEmpty())
//			return;
//		for(MetaDataModel m:list){
//			m.isUpToDate(b);
//		}
	}
}
