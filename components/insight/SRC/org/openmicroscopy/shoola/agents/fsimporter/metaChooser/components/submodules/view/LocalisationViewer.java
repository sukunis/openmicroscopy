package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import ome.xml.model.Objective;
import ome.xml.model.ObjectiveSettings;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules.ObjectiveEditor;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model.ObjectiveModel;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.ModuleConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagData;
import org.slf4j.LoggerFactory;

public class LocalisationViewer extends ModuleViewer{

	@Override
	public void saveData() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void initTag(TagConfiguration t) {
		// TODO Auto-generated method stub
		
	}
	protected void setPredefinedTag(TagConfiguration t) 
	{
		
	}

//	private static final org.slf4j.Logger LOGGER =
//    	    LoggerFactory.getLogger(ObjectiveViewer.class);
// 
//private ObjectiveModel data;
//private Box box;
//
//// available element tags
//
////available element setting tags
//
//
///**
// * Creates a new instance.
// * @param model Reference to model.
// */
//public ObjectiveViewer(ObjectiveModel model,ModuleConfiguration conf)
//{
//	this.data=model;
//	initComponents(conf);
//	initTagList();
//	buildGUI();
//}
//
//private void initTagList()
//{
//	tagList=new ArrayList<TagData>();
//	tagList.add(model);
//	tagList.add(manufact);
//	tagList.add(nomMagn);
//	tagList.add(calMagn);
//	tagList.add(lensNA);
//	tagList.add(immersion);
//	tagList.add(correction);
//	tagList.add(workDist);
//	tagList.add(iris);
//	tagList.add(corCollar);
//	tagList.add(medium);
//	tagList.add(refractIndex);
//	
//}
//
///**
// * Builds and lay out GUI.
// */
//private void buildGUI() 
//{
//	List<JLabel> labels= new ArrayList<JLabel>();
//	List<JComponent> comp=new ArrayList<JComponent>();
//	addTagToGUI(model,labels,comp);
//	addTagToGUI(manufact,labels,comp);
//	addTagToGUI(nomMagn,labels,comp);
//	addTagToGUI(calMagn,labels,comp);
//	addTagToGUI(lensNA,labels,comp);
//	addTagToGUI(immersion,labels,comp);
//	addTagToGUI(correction,labels,comp);
//	addTagToGUI(workDist,labels,comp);
//	
//	addLabelTextRows(labels, comp, gridbag, globalPane);
//	
//	c.gridwidth = GridBagConstraints.REMAINDER; //last
//	c.anchor = GridBagConstraints.WEST;
//	c.weightx = 1.0;
//	
//	//Settings
//	GridBagConstraints cSett=new GridBagConstraints();
//	GridBagLayout gridbagSett = new GridBagLayout();
//	List<JLabel> labelsSett= new ArrayList<JLabel>();
//	List<JComponent> compSett=new ArrayList<JComponent>();
//	JPanel settingsPane=new JPanel(gridbagSett);
//	addLabelToGUI(new JLabel("Settings:"),labelsSett,compSett);
//	addTagToGUI(corCollar,labelsSett,compSett);
//	addTagToGUI(medium,labelsSett,compSett);
//	addTagToGUI(refractIndex,labelsSett,compSett);
//	
//	addLabelTextRows(labelsSett, compSett, gridbag, settingsPane);
//	
//	c.gridwidth = GridBagConstraints.REMAINDER; //last
//	c.anchor = GridBagConstraints.WEST;
//	c.weightx = 1.0;
//	
//	box.add(Box.createVerticalStrut(20));
//	box.add(settingsPane);
//	
//	// set data
//	setGUIData();
//	setSettingsGUIData();
//}
//
///**
// * Initialize components.
// */
//private void initComponents(ModuleConfiguration conf) 
//{
//	// init view layout
//	setLayout(new BorderLayout(5,5));
//	setBorder(BorderFactory.createCompoundBorder(new TitledBorder(""),
//			BorderFactory.createEmptyBorder(5,10,5,10)));
//	
//	gridbag = new GridBagLayout();
//	c = new GridBagConstraints();
//	
//	globalPane=new JPanel();
//	globalPane.setLayout(gridbag);
//	
//	box=Box.createVerticalBox();
//	box.add(globalPane);
//	
//	JButton editBtn=new JButton("Selection");
//	editBtn.setAlignmentX(Component.RIGHT_ALIGNMENT);
//	editBtn.setEnabled(true);
//	editBtn.addActionListener(new ActionListener() {
//		public void actionPerformed(ActionEvent e) 
//		{
//			ObjectiveEditor creator = new ObjectiveEditor(new JFrame(),"Select Objective",
//					data.getList());
//			Objective selectedObj=creator.getObjective();  
//			if(selectedObj!=null ){
//				dataChanged=true;
//				try {
//					data.addData(selectedObj, true);
//				} catch (Exception e1) {
//					LOGGER.warn("Can't set data of selected objective! "+e1);
//				}
//				setGUIData();
//				revalidate();
//				repaint();
//			}		
//		}
//	});
//	add(box,BorderLayout.NORTH);
//	add(editBtn,BorderLayout.SOUTH);
//	
//	// init tag layout
//	List<TagConfiguration> list=conf.getTagList();
//	List<TagConfiguration> settList=conf.getSettingList();
//	initTags(list);
//	initTags(settList);
//}
//
//
//
///**
// * Init given tag and mark it as visible.
// * @param t
// */
//protected void initTag(TagConfiguration t) 
//{
//	String name=t.getName();
//	Boolean prop=t.getProperty();
//	switch (name) {
//	}
//}
//
///**
// * Show data of objective
// */
//private void setGUIData() 
//{
//	Objective objective=data.getObjective();
//	
//	
//}
//
//private void setSettingsGUIData()
//{
//	ObjectiveSettings settings = data.getSettings();
//	
//
//}
//
//
//
///*------------------------------------------------------
// * Set methods data Values
// * -----------------------------------------------------*/
//
//
//
///*------------------------------------------------------
// * Set methods settings Values
// * -----------------------------------------------------*/
//
//
//@Override
//public void saveData() 
//{
//	Objective objective =data.getObjective();
//	if(objective==null)
//		objective = new Objective();
//	
//	// --- Settings --------------------
//	ObjectiveSettings settings=data.getSettings();
//	if(settings==null)
//		settings = new ObjectiveSettings();
//	
//	
//}


}


