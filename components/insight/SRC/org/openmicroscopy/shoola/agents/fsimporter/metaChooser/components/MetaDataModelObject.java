package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components;

import java.util.ArrayList;
import java.util.List;

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
}
