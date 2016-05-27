package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.ElementsCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.LightPathElem;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.TagData;

import ome.xml.model.Detector;
import ome.xml.model.Dichroic;
import ome.xml.model.Filter;
import ome.xml.model.enums.EnumerationException;
import ome.xml.model.enums.FilterType;


import loci.formats.meta.IMetadata;

public class FilterCompUI extends LightPathElem 
{
	private TagData model;
	private TagData manufact;
	private TagData type;
	private TagData filterwheel;
	private TagData classification; // main Filter, second, tertiary or quanternary
	
	final static String[] classificList={"","Main Filter","Secondary Filter","Tertiary Filter","Quaternary Filter"};
	final static String[] typeList=getNames(FilterType.class); 
	
	//??
	private TagData width;
	
	private TitledBorder tb;
	
	private Object filter;
	
	
	
	public FilterCompUI(Object _filter)
	{
		filter=_filter;
		initGUI();
		if(filter!=null)
			setGUIData();
	}
	
	private void setGUIData() 
	{
		if(filter instanceof Filter){
			try{setID(((Filter) filter).getID());
			}catch (NullPointerException e) { }
			try{setManufact(((Filter) filter).getManufacturer(), ElementsCompUI.REQUIRED);
			}catch (NullPointerException e) { }
			try{setModel(((Filter) filter).getModel(), ElementsCompUI.REQUIRED);
			}catch (NullPointerException e) { }
			try{setFilterWheel(((Filter) filter).getFilterWheel(), ElementsCompUI.REQUIRED);
			}catch (NullPointerException e) { }
			try{setType(((Filter) filter).getType(), ElementsCompUI.REQUIRED);
			}catch (NullPointerException e) { }
		}else{
			try{setID(((Dichroic) filter).getID());
			}catch (NullPointerException e) { }
			try{setManufact(((Dichroic) filter).getManufacturer(), ElementsCompUI.REQUIRED);
			}catch (NullPointerException e) { }
			try{setModel(((Dichroic) filter).getModel(), ElementsCompUI.REQUIRED);
			}catch (NullPointerException e) { }
			try{setType(FilterType.DICHROIC, ElementsCompUI.REQUIRED);
			}catch (NullPointerException e) { }
			try{setFilterWheel("", ElementsCompUI.OPTIONAL);filterwheel.setEnable(false);}
			catch(NullPointerException e){}
		}
	}

	private void initGUI()
	{
		setLayout(new BorderLayout(5,5));
		buildComp=false;
		labels= new ArrayList<JLabel>();
		comp = new ArrayList<JComponent>();
		
		gridbag = new GridBagLayout();
		c = new GridBagConstraints();
		globalPane=new JPanel();
		globalPane.setLayout(gridbag);
		
//		add(new TitledSeparator("Filter", 3, TitledBorder.DEFAULT_POSITION),BorderLayout.NORTH);
		add(globalPane,BorderLayout.NORTH);
		tb=new TitledBorder("");
		setBorder(BorderFactory.createCompoundBorder(tb,
						BorderFactory.createEmptyBorder(5,5,5,5)));
	}

	private void readGUIInput() throws Exception
	{
		if(filter==null){
			filter=new Filter();
		}
		if(filter instanceof Filter){
			try{
				((Filter) filter).setManufacturer(manufact.getTagValue().equals("")?
						null : manufact.getTagValue());
			}catch(Exception e){
				LOGGER.error("[DATA] can't read FILTER manufacturer input");
			}
			try{
				((Filter) filter).setModel(model.getTagValue().equals("")?
						null : model.getTagValue());
			}catch(Exception e){
				LOGGER.error("[DATA] can't read FILTER model input");
			}
			try{
				((Filter) filter).setFilterWheel(filterwheel.getTagValue().equals("")?
						null : filterwheel.getTagValue());
			}catch(Exception e){
				LOGGER.error("[DATA] can't read FILTER filter wheel input");
			}
			try{
				((Filter) filter).setType(type.getTagValue().equals("")?
						null : FilterType.fromString(type.getTagValue()));
			}catch(Exception e){
				LOGGER.error("[DATA] can't read FILTER type input");
			}
		}else{
			try{
				((Dichroic) filter).setManufacturer(manufact.getTagValue().equals("")?
						null : manufact.getTagValue());
			}catch(Exception e){
				LOGGER.error("[DATA] can't read DICHROIC manufacturer input");
			}
			try{
				((Dichroic) filter).setModel(model.getTagValue().equals("")?
						null : model.getTagValue());
			}catch(Exception e){
				LOGGER.error("[DATA] can't read DICHROIC model input");
			}
		}
		
		
	}
	
	

	public Object getData() throws Exception
	{
		if(userInput())
			readGUIInput();
		return filter;
	}
	
//	public Filter copyData() throws Exception
//	{
//		Filter f=new Filter();
//		if(filter!=null){
//			readGUIInput();
//			f.setManufacturer(filter.getManufacturer());
//			f.setModel(filter.getModel());
//			f.setID(filter.getID());
//			f.setFilterWheel(filter.getFilterWheel());
//			f.setType(filter.getType());
//		}
//		
//		return f;
//	}
	
	
	public void buildComponents() 
	{
		labels.clear();
		comp.clear();
		
		tb.setTitle(id);
		addTagToGUI(model);
		addTagToGUI(manufact);
		addTagToGUI(type);
		
		addTagToGUI(filterwheel);
		addTagToGUI(classification);
		addTagToGUI(width);
		
		addLabelTextRows(labels, comp, gridbag, globalPane);
		
		c.gridwidth = GridBagConstraints.REMAINDER; //last
		c.anchor = GridBagConstraints.WEST;
		c.weightx = 1.0;
		
		buildComp=true;
	}

	public void buildExtendedComponents() 
	{
		
	}

	@Override
	public void createDummyPane(boolean inactive) 
	{
		filter=new Filter();
		setName(null);
		setModel(null, ElementsCompUI.OPTIONAL);
		setManufact(null, ElementsCompUI.OPTIONAL);
		setType(null, ElementsCompUI.OPTIONAL);
		setFilterWheel(null, ElementsCompUI.OPTIONAL);
		setClassification(null, ElementsCompUI.OPTIONAL);
		setWidth(null,  ElementsCompUI.OPTIONAL);
		
		if(inactive){
		model.setEnable(false);
		manufact.setEnable(false);
		type.setEnable(false);
		filterwheel.setEnable(false);
		classification.setEnable(false);
		width.setEnable(false);
		}
	}
	
	public void clearDataValues() 
	{
		clearTagValue(model);
		clearTagValue(manufact);
		clearTagValue(type);
		clearTagValue(filterwheel);
		clearTagValue(classification);
		clearTagValue(width);
	}
	
	
	public void setModel(String value, boolean prop)
	{
		if(model == null) 
			model = new TagData("Model: ",value,prop,TagData.TEXTFIELD);
		else 
			model.setTagValue(value,prop);
		model.setVisible(true);
	}
	public void setManufact(String value, boolean prop)
	{
		if(manufact == null) 
			manufact = new TagData("Manufacturer: ",value,prop,TagData.TEXTFIELD);
		else 
			manufact.setTagValue(value,prop);
		manufact.setVisible(true);
	}
	public void setType(FilterType value, boolean prop)
	{
		String val= (value != null)? value.getValue() : "";
		if(type == null) 
			type = new TagData("Type: ",val,prop,TagData.COMBOBOX,typeList);
		else 
			type.setTagValue(val,prop);
		
		if(val.equals(FilterType.DICHROIC.toString())){
			filterwheel.setEnable(false);
		}
		type.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(type.getTagValue().equals(FilterType.DICHROIC.toString())){
					filterwheel.setEnable(false);
				}else{
					filterwheel.setEnable(true);
				}
			}
		});
		type.setVisible(true);
		
	}
//	public void setName(String value)
//	{
//		if(name == null) 
//			name = new TagData("Name: ",value,ElementsCompUI.OPTIONAL,TEXTFIELD);
//		else 
//			name.setTagValue(value,ElementsCompUI.OPTIONAL);
//	}
	public void setFilterWheel(String value, boolean prop)
	{
		if(filterwheel == null) 
			filterwheel = new TagData("Filterwheel: ",value,prop,TagData.TEXTFIELD);
		else 
			filterwheel.setTagValue(value,prop);
		
		filterwheel.setVisible(true);
	}
	public void setClassification(String value, boolean prop)
	{
	
		if(classification == null) 
			classification = new TagData("Classification: ",value,prop,TagData.COMBOBOX,classificList);
		else 
			classification.setTagValue(value,prop);
		
		classification.setVisible(true);
	}
	public void setWidth(String value, boolean prop)
	{
		if(width == null) 
			width = new TagData("Width: ",value,prop,TagData.TEXTFIELD);
		else 
			width.setTagValue(value,prop);
		
		width.setVisible(true);
	}

	@Override
	public List<TagData> getActiveTags() {
		// TODO Auto-generated method stub
		return null;
	}

	

	@Override
	public Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean userInput() {
		// TODO Auto-generated method stub
		return false;
	}

	

	

}
