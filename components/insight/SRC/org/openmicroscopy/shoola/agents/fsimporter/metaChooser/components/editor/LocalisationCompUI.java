package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor;

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

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.ElementsCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.TagData;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.TitledSeparator;

import loci.formats.meta.IMetadata;

public class LocalisationCompUI extends ElementsCompUI
{
	private TagData xpos;
	private TagData ypos;
	private TagData zpos;
	private TagData pinholeSize;
	private TagData pinholePos;
	private TagData sdSpeed;
	
	public LocalisationCompUI()
	{
		setLayout(new BorderLayout(5,5));
		buildComp=false;
		labels= new ArrayList<JLabel>();
		comp = new ArrayList<JComponent>();
		
		gridbag = new GridBagLayout();
		c = new GridBagConstraints();
		globalPane=new JPanel();
		globalPane.setLayout(gridbag);
		
		add(new TitledSeparator("Localisation", 3, TitledBorder.DEFAULT_POSITION, true),BorderLayout.NORTH);
		add(globalPane,BorderLayout.CENTER);
		setBorder(
//				BorderFactory.createCompoundBorder(	new MyTitledBorder("Objective"),
						BorderFactory.createEmptyBorder(10,10,10,10));
	}
	
	public LocalisationCompUI(IMetadata data)
	{
		buildComp=false;
		labels= new ArrayList<JLabel>();
		comp = new ArrayList<JComponent>();
		
		gridbag = new GridBagLayout();
		c = new GridBagConstraints();
		setLayout(gridbag);
		setBorder(
//				BorderFactory.createCompoundBorder(	new MyTitledBorder("Objective"),
						BorderFactory.createEmptyBorder(10,10,10,10));
	}
	
	public void buildComponents() 
	{
		labels.clear();
		comp.clear();
		addTagToGUI(xpos);
		addTagToGUI(ypos);
		addTagToGUI(zpos);
		addTagToGUI(pinholeSize);
		addTagToGUI(pinholePos);
		addTagToGUI(sdSpeed);
		
		
		addLabelTextRows(labels, comp, gridbag, globalPane);
		
		c.gridwidth = GridBagConstraints.REMAINDER; //last
		c.anchor = GridBagConstraints.WEST;
		c.weightx = 1.0;
		
		buildComp=true;
	}

	public void buildExtendedComponents() 
	{
		
	}

	public void clearDataValues() 
	{
		clearTagValue(xpos);
		clearTagValue(ypos);
		clearTagValue(zpos);
		clearTagValue(pinholeSize);
		clearTagValue(pinholePos);
		clearTagValue(sdSpeed);
		
	}

	@Override
	public void createDummyPane(boolean inactive) 
	{
		
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
