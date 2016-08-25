package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components;

import java.util.ArrayList;
import java.util.List;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.ElementsCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.ExperimentCompUI;

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

	public void update(MetaDataModelObject parentModel) 
	{
		List<ElementsCompUI> updateList=parentModel.getList().get(0).getComponentsForUpdate();
		for(ElementsCompUI o : updateList){
				list.get(0).updateComponentsOfDirModel(o);
		}
	}

	

	public void isUpToDate(boolean b) 
	{
		if(list==null || list.isEmpty())
			return;
		for(MetaDataModel m:list){
			m.isUpToDate(b);
		}
	}
}
