package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.ElementsCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.TagData;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.TitledSeparator;


public class MetaDataPreferences extends JPanel
{
	private JRadioButton defMeta;
	private JPanel pane1;
	private List<Boolean> tagSelectionList;
	private List<TagData> tagList;
	
	private ElementsCompUI comp;
	
	private String name;
	
	public MetaDataPreferences(String objName, ElementsCompUI elem)
	{
		name=objName;
		comp=elem;
		tagList=null;
		if(elem!=null){
			tagList=elem.getActiveTags();
//			if(tagList!=null && tagList.size()>0){
//				System.out.println("[MetadataPreferences::MetadataPreferences()] TagListSize: "+objName+": "+tagList.size());
//			}else{
//				System.out.println("[MetadataPreferences::MetadataPreferences()] TagListSize: "+objName+": null");
//			}
		}
		
		
		JRadioButton noneObj=new JRadioButton("None");
		JRadioButton allObj=new JRadioButton("All ");
		allObj.setSelected(true);
		JRadioButton defObj=new JRadioButton(objName+": ");
		JTextField defObjSelectionFrom=new JTextField(10);
		JTextField defObjSelectionTo=new JTextField(10);
		
		ButtonGroup g1=new ButtonGroup();
		g1.add(allObj);
		g1.add(defObj);
		g1.add(noneObj);
		
		pane1=new JPanel();
		pane1.setLayout(new GridBagLayout());
		pane1.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		GridBagConstraints c= new GridBagConstraints();
//		c.anchor = GridBagConstraints.WEST;
		c.anchor = GridBagConstraints.LINE_START;
		c.gridwidth=GridBagConstraints.RELATIVE;
		c.fill=GridBagConstraints.NONE;
		c.weightx=0.0;
		
		int x=0; int y=0;
		c.gridwidth=2;
		c.weightx=1.0;
		c.gridx=x; c.gridy=y++;
		c.insets = new Insets(10,0,10,0); 
		pane1.add(new JLabel("Select "+objName),c);
		c.insets = new Insets(0,0,0,0); 
		c.gridwidth=1;
		c.weightx=0.0;
		c.gridx=x; c.gridy=y++;
		pane1.add(noneObj, c);
		
		c.gridx=0; c.gridy=y++;
		pane1.add(allObj,c);
		
		Box b1=Box.createHorizontalBox();
		b1.add(defObj);
		b1.add(defObjSelectionFrom);
		b1.add(new JLabel(" to "));
		b1.add(defObjSelectionTo);
		c.gridx=0; c.gridy=y++;
		pane1.add(b1,c);
		
//		c.gridwidth=GridBagConstraints.REMAINDER;
//		c.fill=GridBagConstraints.HORIZONTAL;
//		c.weightx=1.0;
//		c.insets = new Insets(0,0,0,20);  //end padding
//		c.gridx=x+1; c.gridy=y;
//		pane1.add(defObjSelectionFrom,c);
//		pane1.add(new JLabel(" to "),c);
//		
		
		JRadioButton allMeta=new JRadioButton("All tags");
//		allMeta.setSelected(true);
		defMeta=new JRadioButton("Selection");
		
		ActionListener changeListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!defMeta.isSelected()){
					System.out.println("[MetadataPrefs] selection disable");
					disableCheckBoxes(pane1);
				}else{
					enableCheckBoxes(pane1);
				}
			}
		};
		
		allMeta.addActionListener(changeListener);
		defMeta.addActionListener(changeListener);

		c.gridwidth=GridBagConstraints.RELATIVE;
		c.fill=GridBagConstraints.NONE;
		c.weightx=0.0;
		c.insets=new Insets(20, 0, 10, 0);
		c.gridx=0; c.gridy=y++;
		pane1.add(new JLabel("Select Tags"),c);
		c.insets=new Insets(0, 0, 0, 0);
		c.gridx=0; c.gridy=y++;
		pane1.add(allMeta, c);
		c.gridx=0; c.gridy=y++;
		pane1.add(defMeta, c);
		
		ButtonGroup g2=new ButtonGroup();
		g2.add(allMeta);
		g2.add(defMeta);
		allMeta.setSelected(true);
		
		tagSelectionList=new ArrayList<Boolean>();
		
		// TagList
		if(tagList!=null){
			for(TagData tag : tagList)
			{
				c.gridx=x+1; c.gridy=y++;
				JCheckBox ch=new JCheckBox(tag.getTagLabel().getText());
				ch.setName(tag.getTagLabel().getText());
				pane1.add(ch,c);
				tagSelectionList.add(false);
			}
		}		
		
		setLayout(new GridBagLayout());
		GridBagConstraints gbc=new GridBagConstraints();
         
		
		
		JPanel labelPane=new JPanel();
		labelPane.setBackground(Color.gray);
		labelPane.add(new JLabel(objName), JLabel.CENTER);
		
		//TODO: inhalt springt
		JScrollPane sPane=new JScrollPane(pane1);
		
		gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 0;
        add(labelPane, gbc);

        gbc.gridy++;
        gbc.weighty = 1;
        add(sPane, gbc);
//		add(labelPane);
//		add(sPane);
		
		
	}
	
	public String getName(){
		return name;
	}
	
	public List<Boolean> selectionList()
	{
		return tagSelectionList;
	}
	
	public List<TagData> getTagList()
	{
		return tagList;
	}
	
	public ElementsCompUI getCompUI()
	{
		return comp;
	}
	
	private void disableCheckBoxes(JPanel parent)
	{
		Component[] component = parent.getComponents();

	    // Reset user interface
	    for(int i=0; i<component.length; i++)
	    {
	    	if(component[i] instanceof JCheckBox){
	    		component[i].setEnabled(false);
	    	}
	    }
	}
	private void enableCheckBoxes(JPanel parent)
	{
		Component[] component = parent.getComponents();

	    // Reset user interface
	    for(int i=0; i<component.length; i++)
	    {
	    	if(component[i] instanceof JCheckBox){
	    		component[i].setEnabled(true);
	    	}
	    }
	}
}
