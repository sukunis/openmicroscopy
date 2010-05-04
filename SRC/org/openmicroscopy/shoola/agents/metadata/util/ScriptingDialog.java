/*
 * org.openmicroscopy.shoola.agents.metadata.util.ScriptingDialog 
 *
 *------------------------------------------------------------------------------
 *  Copyright (C) 2006-2010 University of Dundee. All rights reserved.
 *
 *
 * 	This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *------------------------------------------------------------------------------
 */
package org.openmicroscopy.shoola.agents.metadata.util;


//Java imports
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

//Third-party libraries
import info.clearthought.layout.TableLayout;
import org.jdesktop.swingx.JXTaskPane;


//Application-internal dependencies
import org.openmicroscopy.shoola.agents.util.ViewerSorter;
import org.openmicroscopy.shoola.env.data.model.ParamData;
import org.openmicroscopy.shoola.env.data.model.ScriptObject;
import org.openmicroscopy.shoola.util.ui.NumericalTextField;
import org.openmicroscopy.shoola.util.ui.NumericalTextFieldLabelled;
import org.openmicroscopy.shoola.util.ui.TitlePanel;
import org.openmicroscopy.shoola.util.ui.UIUtilities;
import pojos.ExperimenterData;

/** 
 * Dialog to run the selected script. The UI is created on the fly.
 *
 * @author  Jean-Marie Burel &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:j.burel@dundee.ac.uk">j.burel@dundee.ac.uk</a>
 * @author Donald MacDonald &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:donald@lifesci.dundee.ac.uk">donald@lifesci.dundee.ac.uk</a>
 * @version 3.0
 * <small>
 * (<b>Internal version:</b> $Revision: $Date: $)
 * </small>
 * @since 3.0-Beta4
 */
public class ScriptingDialog 
	extends JDialog
	implements ActionListener
{
	
	/** Bound property indicating to run the script. */
	public static final String RUN_SCRIPT_PROPERTY = "runScript";
	
	/** 
	 * The size of the invisible components used to separate buttons
	 * horizontally.
	 */
	private static final Dimension  H_SPACER_SIZE = new Dimension(5, 10);
	
	/** Title of the dialog. */
	private static final String		TITLE = "Run Script";
	
	/** The text displayed in the header. */
	private static final String		TEXT = "Set the parameters for the " +
			"script ";
	
	/** The text displayed in the header. */
	private static final String		TEXT_END = ".\n"+ScriptComponent.REQUIRED +
			" indicates the required parameter."+
			"\nIf (List) is indicated next to a parameter, use spaces " +
			"to separate values.\n"+
			"If (Map) is indicated next to a parameter, use "+
			ScriptComponent.MAP_SEPARATOR+
			"to separate (key, value) pair and spaces to separate pairs.\n";
	
	/** Indicates to close the dialog. */
	private static final int CANCEL = 0;
	
	/** Indicates to run the script. */
	private static final int APPLY = 1;
	
	/** Close the dialog. */
	private JButton cancelButton;
	
	/** Run the script. */
	private JButton applyButton;
	
	/** Component used to enter the author of the script. */
	private JTextField	author;
	
	/** Component used to enter the author's e-mail address. */
	private JTextField	eMail;
	
	/** Component used to enter the author's institution. */
	private JTextField	institution;
	
	/** Component used to enter the description of the script. */
	private JTextField	description;
	
	/** Component used to enter where the script was published if 
	 * published. */
	private JTextField	journalRef;
	
	/** The object to handle. */
	private ScriptObject script;
	
	/** The components to display. */
	private Map<String, ScriptComponent> components;
	
	/** Used to sort collections. */
	private ViewerSorter sorter;
	
	/** Closes the dialog. */
	private void close()
	{
		setVisible(false);
		dispose();
	}
	
	/** Collects the data and fires a property.*/
	private void runScript()
	{
		Entry entry;
		ScriptComponent c;
		Iterator i = components.entrySet().iterator();
		Map<String, Object> values = new HashMap<String, Object>();
		
		while (i.hasNext()) {
			entry = (Entry) i.next();
			c = (ScriptComponent) entry.getValue();
			values.put((String) entry.getKey(), c.getValue());
		}
		//script.setParameterValues(values);
		firePropertyChange(RUN_SCRIPT_PROPERTY, null, script);
		close();
	}
	
	/** 
	 * Creates a component displaying the various options.
	 * 
	 * @param values The values to display.
	 * @return See above.
	 */
	private JComboBox createValuesBox(List<Object> values)
	{
		if (values == null) return null;
		Object[] v = new Object[values.size()];
		JComboBox box = new JComboBox(v);
		
		return box;
	}
	
	/** Initializes the components. */
	private void initComponents()
	{
		sorter = new ViewerSorter();
		List<ExperimenterData> experimenters = script.getAuthors();
		Iterator<ExperimenterData> j;
		author = new JTextField();
		author.setEnabled(false);
		if (experimenters != null && experimenters.size() > 0) {
			StringBuffer buffer = new StringBuffer();
			j = experimenters.iterator();
			ExperimenterData exp;
			int index = experimenters.size();
			while (j.hasNext()) {
				exp = j.next();
				buffer.append(exp.getLastName());
				if (index > 1) buffer.append(","); 
			}
			author.setText(buffer.toString());
		}
	    eMail = new JTextField();
	    eMail.setEnabled(false);
	    eMail.setText(script.getContact());
	    institution = new JTextField();
	    institution.setEnabled(false);
		//if (exp != null) {
			//institution.setText(exp.getInstitution());
		//}
	    journalRef = new JTextField(script.getJournalRef()); 
	    journalRef.setEnabled(false);
	    description = new JTextField(script.getDescription());
	    description.setEnabled(false);
		cancelButton = new JButton("Cancel");
		cancelButton.setToolTipText("Close the dialog.");
		cancelButton.setActionCommand(""+CANCEL);
		cancelButton.addActionListener(this);
		applyButton = new JButton("Run");
		applyButton.setToolTipText("Run the script.");
		applyButton.setActionCommand(""+APPLY);
		applyButton.addActionListener(this);
		components = new LinkedHashMap<String, ScriptComponent>(); 
		Map<String, ParamData> types = script.getInputs();
		if (types == null) return;
		Map <String, ScriptComponent> 
			results = new HashMap<String, ScriptComponent>();
		Entry entry;
		ParamData param;
		JComponent comp;
		ScriptComponent c;
		String name;
		Class type;
		Object defValue ;
		Iterator i = types.entrySet().iterator();
		List<Object> values;
		Number n;
		String details = "";
		while (i.hasNext()) {
			comp = null;
			entry = (Entry) i.next();
			param = (ParamData) entry.getValue();
			name = (String) entry.getKey();
			type = param.getPrototype();
			values = param.getValues();
			defValue = param.getDefaultValue();
			if (values != null && values.size() > 0) {
				comp = createValuesBox(values);
			} else {
				if (Long.class.equals(type) || Integer.class.equals(type)) {
					type = Double.class;
					if (param.hasRangeSpecified()) {
						comp = new NumericalTextFieldLabelled(type, 
								param.getMinValue(), param.getMaxValue());
						if (defValue != null)
							((NumericalTextFieldLabelled) comp).setValue(
									""+((Number) defValue).doubleValue());
					} else {
						comp = new NumericalTextField();
						((NumericalTextField) comp).setNumberType(type);
						if (defValue != null)
							((NumericalTextField) comp).setText(
									""+defValue);
					}
				} else if (String.class.equals(type)) {
					comp = new JTextField();
					if (defValue != null)
						((JTextField) comp).setText(""+defValue);
				} else if (Boolean.class.equals(type)) {
					comp = new JCheckBox();
					if (defValue != null)
						((JCheckBox) comp).setSelected((Boolean) defValue);
				} else if (Map.class.equals(type)) {
					comp = new JTextField();
					name += " (Map)";
				} else if (List.class.equals(type)) {
					comp = new JTextField();
					name += " (List)";
				}
			}
			if (comp != null) {
				comp.setToolTipText(param.getDescription());
				c = new ScriptComponent(comp, name);
				if (!(comp instanceof JComboBox))
					c.setRequired(!param.isOptional());
				if (details != null && details.trim().length() > 0)
					c.setInfo(details);
				results.put((String) entry.getKey(), c);
			}
		}
		List<String> sortedKeys = sorter.sort(results.keySet());
		Iterator<String> k = sortedKeys.iterator();
		String key;
		while (k.hasNext()) {
			key = k.next();
			components.put(key, results.get(key));
		}
	}
	
	/**
	 * Builds and lays out the details of the script.
	 * 
	 * @return See above.
	 */
	private JPanel buildScriptDetails()
	{
		double[][] size = {{TableLayout.PREFERRED, 5, TableLayout.FILL},
				{TableLayout.PREFERRED, TableLayout.PREFERRED, 
			TableLayout.PREFERRED, TableLayout.PREFERRED, 50}};
		JPanel details = new JPanel();
		details.setLayout(new TableLayout(size));
		int row = 0;
		JLabel l = UIUtilities.setTextFont("Author (First, Last):");
		details.add(l, "0, "+row+", LEFT, CENTER");
		details.add(author, "2, "+row);
		row++;
		l = UIUtilities.setTextFont("E-mail:");
		details.add(l, "0, "+row+", LEFT, CENTER");
		details.add(eMail, "2, "+row);
		row++;
		l = UIUtilities.setTextFont("Institution:");
		details.add(l, "0, "+row+", LEFT, CENTER");
		details.add(institution, "2, "+row);
		row++;
		l = UIUtilities.setTextFont("Journal Ref:");
		details.add(l, "0, "+row+", LEFT, CENTER");
		details.add(journalRef, "2, "+row);
		row++;
		l = UIUtilities.setTextFont("Script's Description:");
		details.add(l, "0, "+row+", LEFT, TOP");
		details.add(description, "2, "+row);
		
		return details;
	}
	
	/**
	 * Builds the panel hosting the components.
	 * 
	 * @return See above.
	 */
	private JPanel buildControlPanel()
	{
		JPanel controlPanel = new JPanel();
		controlPanel.setBorder(null);
		controlPanel.add(applyButton);
		controlPanel.add(Box.createRigidArea(H_SPACER_SIZE));
		controlPanel.add(cancelButton);
		controlPanel.add(Box.createRigidArea(H_SPACER_SIZE));
		JPanel bar = new JPanel();
		bar.setLayout(new BoxLayout(bar, BoxLayout.Y_AXIS));
		bar.add(controlPanel);
		bar.add(Box.createVerticalStrut(10));
		JPanel p = UIUtilities.buildComponentPanelRight(bar);
		return p;
	}
	
	/** 
	 * Builds the component displaying the parameters.
	 * 
	 * @return See above.
	 */
	private JPanel buildBody()
	{
		JPanel p = new JPanel();
		p.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		double[] columns = {TableLayout.PREFERRED, 5, TableLayout.FILL};
		TableLayout layout = new TableLayout();
		layout.setColumn(columns);
		p.setLayout(layout);
		int row = 0;
		Entry entry;
		Iterator i = components.entrySet().iterator();
		ScriptComponent comp;
		JLabel label;
		while (i.hasNext()) {
			entry = (Entry) i.next();
			comp = (ScriptComponent) entry.getValue();
			layout.insertRow(row, TableLayout.PREFERRED);
			p.add(comp.getLabel(), "0,"+row);
			p.add(comp.getComponent(), "2, "+row);
			row++;
		}
		
		JXTaskPane pane = null;
		if (script.hasDetails()) {
			pane = new JXTaskPane();
			pane.setCollapsed(true);
			pane.setTitle("Script details");
			pane.add(buildScriptDetails());
		}
		
		JPanel controls = new JPanel();
		controls.setLayout(new BorderLayout(0, 0));
		if (pane != null) controls.add(pane, BorderLayout.NORTH);
		controls.add(new JScrollPane(p), BorderLayout.CENTER);
		controls.add(buildControlPanel(), BorderLayout.SOUTH);
		return controls;
	}
	
	/** Builds and lays out the UI. */
	private void buildGUI()
	{
		String text = TEXT+script.getName()+TEXT_END;
		TitlePanel tp = new TitlePanel(TITLE, text, script.getIconLarge());
		Container c = getContentPane();
		c.setLayout(new BorderLayout(0, 0));
		c.add(tp, BorderLayout.NORTH);
		c.add(buildBody(), BorderLayout.CENTER);
		//c.add(buildControlPanel(), BorderLayout.SOUTH);
	}
	
	/**
	 * Creates a new instance.
	 * 
	 * @param parent The parent of the frame.
	 * @param script The script to run. Mustn't be <code>null</code>.
	 */
	public ScriptingDialog(JFrame parent, ScriptObject script)
	{
		super(parent);
		if (script == null)
			throw new IllegalArgumentException("No script specified");
		this.script = script;
		initComponents();
		buildGUI();
		pack();
	}
	
	/**
	 * Closes or runs the scripts.
	 * @see ActionListener#actionPerformed(ActionEvent)
	 */
	public void actionPerformed(ActionEvent e)
	{
		int index = Integer.parseInt(e.getActionCommand());
		switch (index) {
			case CANCEL:
				close();
				break;
			case APPLY:
				runScript();
		}
	}

}
