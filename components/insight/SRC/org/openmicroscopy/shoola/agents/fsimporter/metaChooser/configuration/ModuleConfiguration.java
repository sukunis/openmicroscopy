package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.BooleanUtils;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.TagConfiguration;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class ModuleConfiguration 
{
	public static final String TAG_NAME="Name";
	public static final String TAG_VALUE="Value";
	public static final String TAG_PROP="Optional";
	public static final String TAG_VISIBLE="Visible";
	// for lightSrc types
	private String lightSrcType;
	
	private List<TagConfiguration> list; 
	
	public ModuleConfiguration() 
	{
		list=new ArrayList<TagConfiguration>();
	}
	
	public List<TagConfiguration> getList()
	{
		return list;
	}
	
	public void setTag(String name, String val, String prop) 
	{
		list.add(new TagConfiguration(name, val, prop));
	}
	
	public void loadTags(Element node)
	{
//		NodeList tagList=node.getChildNodes();
		NodeList tagList=node.getElementsByTagName("Tag");
		
		if(tagList!=null && tagList.getLength()>0){
			for(int i=0; i<tagList.getLength(); i++){
				
				NamedNodeMap attr=tagList.item(i).getAttributes();
				String name=null;String value=null; String prop=null;
				boolean visible=false;
				
				if(attr!=null && attr.getLength()>0)
				{
					if(attr.getNamedItem(TAG_NAME)!=null){
						name=attr.getNamedItem(TAG_NAME).getNodeValue();
					}
					if(attr.getNamedItem(TAG_VALUE)!=null){
						value=attr.getNamedItem(TAG_VALUE).getNodeValue();
					}
					if(attr.getNamedItem(TAG_PROP)!=null){
						prop=attr.getNamedItem(TAG_PROP).getNodeValue();
					}
					if(attr.getNamedItem(TAG_VISIBLE)!=null){
						visible=BooleanUtils.toBoolean(attr.getNamedItem(TAG_VISIBLE).getNodeValue());
					}
				}
				System.out.println("[DEBUG] load tag "+name+
						": "+value+", "+prop+", "+visible);
				
				if(visible)
					setTag(name,value,prop);
			}
		}	
	}

	public String getLightSrcType() {
		return lightSrcType;
	}

	public void setLightSrcType(String lightSrcType) {
		this.lightSrcType = lightSrcType;
	}

	
}
