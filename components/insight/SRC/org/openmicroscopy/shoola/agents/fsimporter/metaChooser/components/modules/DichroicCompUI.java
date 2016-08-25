package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules.DichroicCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules.ElementsCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagData;

import ome.xml.model.Dichroic;
import ome.xml.model.Filter;
import ome.xml.model.ManufacturerSpec;
import ome.xml.model.enums.FilterType;
import loci.formats.meta.IMetadata;

public class DichroicCompUI extends LightPathElem 
{
	private TagData manufact;
	private TagData model;
	
	private TitledBorder tb;
	
	private Dichroic dichroic;
	
	
	
	public DichroicCompUI(Dichroic _dichroic)
	{
		dichroic=_dichroic;
		initGUI();
		if(dichroic!=null)
			setGUIData();
	}
	
	
	
	private void setGUIData()
	{
		try{
		
		setID(dichroic.getID());
		}catch (NullPointerException e) { }
		try{setManufact(dichroic.getManufacturer(), ElementsCompUI.REQUIRED);
		}catch (NullPointerException e) { }
		try{setModel(dichroic.getModel(), ElementsCompUI.REQUIRED);
		}catch (NullPointerException e) { }
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

		//		add(new TitledSeparator("Dichroic", 3, TitledBorder.DEFAULT_POSITION),BorderLayout.NORTH);
		add(globalPane,BorderLayout.CENTER);
		tb=new TitledBorder("");
		setBorder(BorderFactory.createCompoundBorder(	tb,
				BorderFactory.createEmptyBorder(5,5,5,5)));

	}
	
	private void readGUIInput() throws Exception
	{
		if(dichroic==null)
			createNewElement();
		
		dichroic.setManufacturer(manufact.getTagValue().equals("")?
				null : manufact.getTagValue());
		
		dichroic.setModel(model.getTagValue().equals("")?
				null : model.getTagValue());
		
	}
	
	private void createNewElement() {
		dichroic=new Dichroic();
	}



	public Dichroic getData() throws Exception
	{
		if(userInput())
			readGUIInput();
		return dichroic;
	}
	
	public DichroicCompUI(String myID, String spec,TagData myModel, TagData myManufact)
	{
		buildComp=false;
		labels= new ArrayList<JLabel>();
		comp = new ArrayList<JComponent>();
		
		gridbag = new GridBagLayout();
		c = new GridBagConstraints();
		setLayout(gridbag);
		tb=new TitledBorder("");
		setBorder(BorderFactory.createCompoundBorder(	tb,
				BorderFactory.createEmptyBorder(5,5,5,5)));
		this.id=myID;
		this.specification=spec;
		this.model=myModel;
		this.manufact=myManufact;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		DichroicCompUI d=new DichroicCompUI(this.id,this.specification,this.model,this.manufact);
		d.buildComponents();
		return d;
	}
	
	@Override
	public void buildComponents() {
		labels.clear();
		comp.clear();
		tb.setTitle(id);
		
		addTagToGUI(model);
		addTagToGUI(manufact);
		
		addLabelTextRows(labels, comp, gridbag, globalPane);
		
		c.gridwidth = GridBagConstraints.REMAINDER; //last
		c.anchor = GridBagConstraints.WEST;
		c.weightx = 1.0;
		
		buildComp=true;
		
	}

	@Override
	public void buildExtendedComponents() {
		// TODO Auto-generated method stub
		
	}

	public void createDummyPane(boolean inactive)
	{
		setModel(null,OPTIONAL);
		setManufact(null,OPTIONAL);
		model.setEnable(false);
		manufact.setEnable(false);
		
	}
	@Override
	public void clearDataValues() {
		clearTagValue(model);
		clearTagValue(manufact);
		
	}
	

	public void setModel(String value, boolean prop)
	{
		if(model == null) 
			model = new TagData("Model",value,prop,TagData.TEXTFIELD);
		else 
			model.setTagValue(value,prop);
	}
	public String getModel()
	{
		return model.getTagValue();
	}
	public void setManufact(String value, boolean prop)
	{
		if(manufact == null) 
			manufact = new TagData("Manufacturer",value,prop,TagData.TEXTFIELD);
		else 
			manufact.setTagValue(value,prop);
	}

	@Override
	public List<TagData> getActiveTags() {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public boolean userInput() {
		// TODO Auto-generated method stub
		return false;
	}

	
	
	
}
