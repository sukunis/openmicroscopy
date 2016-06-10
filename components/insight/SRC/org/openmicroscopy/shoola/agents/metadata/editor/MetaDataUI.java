package org.openmicroscopy.shoola.agents.metadata.editor;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.jdesktop.swingx.JXTaskPane;
import org.openmicroscopy.shoola.agents.metadata.MetadataViewerAgent;
import org.openmicroscopy.shoola.agents.util.DataComponent;
import org.openmicroscopy.shoola.agents.util.EditorUtil;
import org.openmicroscopy.shoola.util.ui.UIUtilities;

/**
 * Component displaying the metadata editor.
 * 
 * @author Kunis
 *
 */
public class MetaDataUI extends JPanel implements PropertyChangeListener 
{
	private EditorControl controller;
	private EditorModel model;
	private EditorUI view;
	
	
	/** The default text. */
	private static final String			DETAILS = " Details";
	
//	private SampleComponent sampleComp;
	private JXTaskPane sampleTaskPane;
	
	private SampleEditUI sampleUI;
	
//	private ExperimentComponent experimentComp;
	private JXTaskPane experimentPane;
	
	/** The UI component hosting the <code>JXTaskPane</code>s. */
	private JPanel container;
	/** The constraints used to lay out the components in the container. */
	private GridBagConstraints constraints;
	/** Flag inidicating to build the UI once. */
	private boolean init;
	
	
	MetaDataUI(EditorUI view, EditorModel model,EditorControl controller)
	{
		if (model == null)
			throw new IllegalArgumentException("No model.");
		if (controller == null)
			throw new IllegalArgumentException("No control.");
		if (view == null)
			throw new IllegalArgumentException("No view.");
		this.model = model;
		this.controller = controller;
		this.view = view;
		initComponents();
		init = false;
	
	}
	
	/** Initializes the UI components.*/
	private void initComponents()
	{
		container = new JPanel();
		sampleUI = new SampleEditUI(model, controller);
		sampleTaskPane = EditorUtil.createTaskPane("Sample");
		sampleTaskPane.add(sampleUI);
		sampleTaskPane.setCollapsed(true);
		
		
		
		
//		sampleComp = new SampleComponent(this, model);
//		sampleComp.addPropertyChangeListener(this);
//		samplePane = EditorUtil.createTaskPane("Sample");
//		samplePane.add(sampleComp);
//		samplePane.addPropertyChangeListener(
//				UIUtilities.COLLAPSED_PROPERTY_JXTASKPANE,this);
//		
//		experimentComp = new ExperimentComponent(this, model);
//		experimentComp.addPropertyChangeListener(this);
//		experimentPane = EditorUtil.createTaskPane("Experiment");
//		experimentPane.add(experimentComp);
//		experimentPane.addPropertyChangeListener(
//				UIUtilities.COLLAPSED_PROPERTY_JXTASKPANE,this);
	}
	
	/** lays out the UI when data are loaded (siehe generalPaneUI)*/
	void layoutUI()
	{
		 if (!init) {
             buildGUI();
             init = true;
         }
         
         boolean multi = model.isMultiSelection();
		 sampleUI.buildUI();
		 sampleTaskPane.setTitle(sampleUI.getText() + DETAILS);
		 sampleTaskPane.setVisible(!multi);
	}
	
	/** Builds and lays out the components.*/
	private void buildGUI()
	{
		setBackground(UIUtilities.BACKGROUND_COLOR);
		setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTH;
		
		add(sampleTaskPane,c);
		c.gridy++;
	}
	
	void setRootObject(Object oldObject)
	{
		if (!init) {
			buildGUI();
			init = true;
		}	
		clearData(oldObject);
		sampleUI.clearDisplay();
		revalidate();
		repaint();
	}
	
	/** 
	 * Clears data to save.
	 * 
	 * @param oldObject The previously selected object.
	 */
	void clearData(Object oldObject)
	{
		setCursor(Cursor.getDefaultCursor());
	}
	
	/**
	 * Attaches listener to each item of the map.
	 * 
	 * @param map The map to handle.
	 */
	void attachListener(Map<String, DataComponent> map)
	{
		Iterator i = map.entrySet().iterator();
		Entry entry;
		while (i.hasNext()) {
			entry = (Entry) i.next();
			((DataComponent) entry.getValue()).attachListener(controller);
		}
	}
	
	/**
	 * Returns <code>true</code> if one of the components has been modified,
	 * <code>false</code> otherwise.
	 * 
	 * @param map The map to handle.
	 * @return See above.
	 */
	boolean hasDataToSave(Map<String, DataComponent> map)
	{
		if (map == null) return false;
		Iterator i = map.entrySet().iterator();
		DataComponent comp;
		Entry entry;
		while (i.hasNext()) {
			entry = (Entry) i.next();
			comp = (DataComponent) entry.getValue();
			if (comp.isDirty()) return true;
		}
		return false;
	}
	
	/**
	 * Formats the component.
	 * 
	 * @param comp  The component to format.
	 * @param title The title to add to the border.
	 */
	void format(JComponent comp, String title)
	{
		if (comp == null) return;
		if (title == null) title = "";
		comp.setBorder(BorderFactory.createTitledBorder(title));
		comp.setBackground(UIUtilities.BACKGROUND_COLOR);
		comp.setLayout(new GridBagLayout());
	}

	/** 
	 * Lays out the passed component.
	 * 
	 * @param pane 		The main component.
	 * @param button	The button to show or hide the unset fields.
	 * @param fields	The fields to lay out.
	 * @param shown		Pass <code>true</code> to show the unset fields,
	 * 					<code>false</code> to hide them.
	 */
	void layoutFields(JPanel pane, JComponent button, 
			Map<String, DataComponent> fields, boolean shown)
	{
		pane.removeAll();
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(0, 2, 2, 0);
		DataComponent comp;
        Set set = fields.entrySet();
        Entry entry;
        
		Iterator i = set.iterator();
		c.gridy = 0;
        while (i.hasNext()) {
            c.gridx = 0;
            entry = (Entry) i.next();
            comp = (DataComponent) entry.getValue();
            if (comp.isSetField() || shown) {
            	 ++c.gridy;
            	 c.gridwidth = GridBagConstraints.RELATIVE; //next-to-last
                 c.fill = GridBagConstraints.NONE;      //reset to default
                 c.weightx = 0.0;  
                 pane.add(comp.getLabel(), c);
                 c.gridx++;
                 pane.add(Box.createHorizontalStrut(5), c); 
                 c.gridx++;
                 c.gridwidth = GridBagConstraints.REMAINDER;     //end row
                 c.fill = GridBagConstraints.HORIZONTAL;
                 c.weightx = 1.0;
                 pane.add(comp.getArea(), c);  
            } 
        }
        if (c.gridy != 0) ++c.gridy;
        c.gridx = 0;
        c.gridwidth = GridBagConstraints.REMAINDER;     //end row
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        if (button != null) pane.add(button, c);
	}
	
	
	void setSampleData()
	{
//		Map sample =model.getSampleData();
//		sampleAcquisitionPanes.clear();
//		sampleComp.clear();
//		if(sample !=null){
			
//		}
//		revalidate();
	}
	
	void setExperimentData()
	{
//		experimentComp.setExperimentData();
		revalidate();
	}
	
	/** 
	 * Updates display when the new root node is set. 
	 * Loads the acquisition data if the passed parameter is <code>true</code>
	 * and the {@link #imagePane} is expanded.
	 * 
	 * @param load 	Pass <code>true</code> to load the image data,
	 * 				<code>false</code> otherwise.
	 */
	void setRootObject(boolean load)
	{
//		sampleComp.setRootObject();
//		experimentComp.setRootObject();
//		layoutUI();
//		repaint();
//		if (load){
//			controller.loadSampleData();
//			controller.loadExperimentData();
//		}
	}
	
	
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) 
	{
//		String name = evt.getPropertyName();
//		if (!UIUtilities.COLLAPSED_PROPERTY_JXTASKPANE.equals(name)) return;
//		Object src = evt.getSource();
//		if (src == samplePane) {
//			controller.loadSampleData();
//		}else if(src == experimentPane){
//			controller.loadExperimentData();
//		}
	}
	

}
