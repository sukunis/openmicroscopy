package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.table.AbstractTableModel;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.TagConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.microscope.CustomViewProperties;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.microscope.MetaDataUI.GUIPlaceholder;
import org.slf4j.LoggerFactory;

/**
 * Save Customize View with default data as profileFile
 * Two parts: customize view | default data
 * @author Kunis
 *
 */
public class UOSProfileEditorUI extends JDialog implements ActionListener
{
	private static final org.slf4j.Logger LOGGER =
			LoggerFactory.getLogger(UOSProfileReader.class);


	private File file;
	
	private CustomViewProperties prop;
	
	private JButton openFileBtn;
	private JButton saveFileBtn;
	private JButton okBtn;
	private JButton cancelBtn;
	
	private JTextField fileName;
	
	private JPanel main;
	private JPanel dataPane;
	private JPanel buttonPane;
	private Box headerPane;
	
	
	private JFileChooser fc;

	public UOSProfileEditorUI(CustomViewProperties cView)
	{
		prop=cView;
		setTitle("Customize View");
		setModal(true);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		buildGUI();
		add(new JScrollPane(main));

		pack();

	}
	
	public CustomViewProperties getProperties()
	{
		return prop;
	}

	private void buildGUI()
	{
		fc=new JFileChooser();
		
		main=new JPanel();
		main.setLayout(new BorderLayout(5,5));

		// profile file location
		JLabel fLabel = new JLabel("Profile file: ");
		file=prop.getFile();
		fileName = new JTextField(file!=null ?file.getAbsolutePath():"");
		fLabel.setLabelFor(fileName);
		openFileBtn=new JButton("Open File...");
		openFileBtn.addActionListener(this);
		Box filePane = Box.createHorizontalBox();
		filePane.add(fLabel);
		filePane.add(Box.createHorizontalStrut(5));
		filePane.add(fileName);
		filePane.add(Box.createHorizontalStrut(15));
		filePane.add(openFileBtn);

		//microscope name
		JLabel micLabel=new JLabel("Microscope: ");
		JTextField micName=new JTextField(prop.getMicName());
		Box micPane =Box.createHorizontalBox();
		micPane.add(micLabel);
		micPane.add(Box.createHorizontalStrut(5));
		micPane.add(micName);
		
		headerPane=Box.createVerticalBox();
		headerPane.add(filePane);
		headerPane.add(Box.createVerticalStrut(10));
		headerPane.add(micPane);

		// profile settings
		dataPane=custumViewEditor();
		buttonPane=initButtonPane();

		main.add(headerPane,BorderLayout.NORTH);
		main.add(dataPane,BorderLayout.CENTER);
		main.add(buttonPane,BorderLayout.SOUTH);


	}

	private JPanel initButtonPane() 
	{
		JPanel bar=new JPanel(new FlowLayout(FlowLayout.RIGHT));
		bar.add(Box.createHorizontalStrut(5));

		okBtn=new JButton("OK");
		okBtn.setToolTipText("Use configuration for current view and close editor");
		okBtn.addActionListener(this);
		
		
		cancelBtn=new JButton("Cancel");
		cancelBtn.addActionListener(this);
		
		saveFileBtn=new JButton("Save To File...");
		saveFileBtn.setToolTipText("Save configuration to file");
		saveFileBtn.addActionListener(this);
		

		bar.add(saveFileBtn);
		bar.add(Box.createHorizontalStrut(10));
		bar.add(okBtn);
		bar.add(Box.createHorizontalStrut(5));
		bar.add(cancelBtn);
		bar.add(Box.createHorizontalStrut(10));

		return bar;
	}

	public JPanel custumViewEditor()
	{
		JPanel pane = new JPanel();
		pane.setLayout(new GridLayout(0,4,5,5));

		pane.add(new ConfigurationPanel(prop.getImageConf(),"Image Modul"));
		pane.add(new ConfigurationPanel(prop.getObjConf(),"Objective Modul"));
		pane.add(new ConfigurationPanel(prop.getDetectorConf(),"Detector Modul"));
		pane.add(new ConfigurationPanel(prop.getLightSrcConf(),"LightSource Modul"));

		pane.add(new ConfigurationPanel(prop.getChannelConf(),"Channel Modul"));
		pane.add(new ConfigurationPanel(prop.getLightPathConf(),"LightPath Modul"));
		pane.add(new ConfigurationPanel(prop.getSampleConf(),"Sample Modul"));
		pane.add(new ConfigurationPanel(prop.getExpConf(),"Experiment Modul"));
		
		
		
		return pane;

	}
	
	
	
	private void applyConfigurations(CustomViewProperties cView)
	{
		Component[] comp=dataPane.getComponents();
		cView.setImageConf(((ConfigurationPanel)comp[0]).getConfiguration());
		cView.setObjConf(((ConfigurationPanel) comp[1]).getConfiguration());
		cView.setDetectorConf(((ConfigurationPanel) comp[2]).getConfiguration());
		cView.setLightSrcConf(((ConfigurationPanel) comp[3]).getConfiguration());
		
		cView.setChannelConf(((ConfigurationPanel) comp[4]).getConfiguration());
		cView.setLightPathConf(((ConfigurationPanel) comp[5]).getConfiguration());
		cView.setSampleConf(((ConfigurationPanel) comp[6]).getConfiguration());
		cView.setExperimenterConf(((ConfigurationPanel) comp[7]).getConfiguration());
		
	}


	private class CheckListItem
	{
		private Object item;
		private boolean selected;

		public CheckListItem(Object item)
		{
			this.item = item;
		}

		public CheckListItem(Object item, boolean selected)
		{
			this.item = item;
			this.selected=selected;
		}



		@SuppressWarnings("unused")
		public Object getItem()
		{
			return item;
		}

		public boolean isSelected()
		{
			return selected;
		}

		public void setSelected(boolean isSelected)
		{
			this.selected = isSelected;
		}

		@Override
		public String toString()
		{
			return item.toString();
		}
	}

	private class CheckBoxListRenderer extends JCheckBox
	implements ListCellRenderer
	{
		public Component getListCellRendererComponent(JList comp, Object value,
				int index, boolean isSelected, boolean hasFocus)
		{
			setEnabled(comp.isEnabled());
			setSelected(((CheckListItem) value).isSelected());
			setFont(comp.getFont());
			setText(value.toString());

			if (isSelected)
			{
				setBackground(comp.getSelectionBackground());
				setForeground(comp.getSelectionForeground());
			}
			else
			{
				setBackground(comp.getBackground());
				setForeground(comp.getForeground());
			}

			return this;
		}
	}
	
	

	class ConfigurationPanel extends JPanel
	{
		private JList list;
		private JCheckBox visibleCB;
		private JTextField position;
		private ModuleConfiguration configuration;

		public ConfigurationPanel(ModuleConfiguration conf, String name) 
		{
			configuration=conf;
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

			JPanel titlePane=new JPanel(new FlowLayout(FlowLayout.LEFT));
			JLabel label=new JLabel(name);
			Font font = label.getFont();
			Font boldFont = new Font(font.getFontName(), Font.BOLD, font.getSize());
			label.setFont(boldFont);
			
			
			visibleCB=new JCheckBox();
			visibleCB.setSelected(conf.isVisible());
			visibleCB.addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent e) {
					if(e.getStateChange() == ItemEvent.SELECTED)
						list.setEnabled(true);
					else
						list.setEnabled(false);
				}
			});
			position = new JTextField(conf.getPosition().name()); 

			titlePane.add(visibleCB);
			titlePane.add(Box.createHorizontalStrut(2));
			titlePane.add(label);
			titlePane.add(Box.createHorizontalStrut(10));
			titlePane.add(position);

			list = new JList(getTagListAsItems(conf));
			list.setCellRenderer((ListCellRenderer) new CheckBoxListRenderer());
			list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			list.setVisibleRowCount(10);
			list.addMouseListener(new MouseAdapter()
			{
				@Override
				public void mouseClicked(MouseEvent event)
				{
					selectItem( (JList) event.getSource(),event.getPoint());
				}
			});

			KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0);
			Object mapKey = keyStroke.toString();
			list.getInputMap().put(keyStroke, mapKey);
			list.getActionMap().put(mapKey, new AbstractAction()
			{
				public void actionPerformed(ActionEvent event)
				{
					toggleSelectedItem( (JList) event.getSource());
				}
			});

			if(!conf.isVisible())
				list.setEnabled(false);

			add(titlePane);
			add(Box.createVerticalStrut(2));
			add(new JScrollPane(list));
		}
		
		
		private void selectItem(JList jlist,Point point)
		{
			int index = jlist.locationToIndex(point);

			if (index >= 0)
			{
				CheckListItem item = (CheckListItem)jlist.getModel().getElementAt(index);
				item.setSelected(!item.isSelected());
				jlist.repaint(jlist.getCellBounds(index, index));
			}
		}

		private void toggleSelectedItem(JList jlist)
		{
			int index = jlist.getSelectedIndex();

			if (index >= 0)
			{
				CheckListItem item = (CheckListItem)jlist.getModel().getElementAt(index);
				item.setSelected(!item.isSelected());
				jlist.repaint(jlist.getCellBounds(index, index));
			}
		}
		private Object[] getTagListAsItems(ModuleConfiguration conf) 
		{
			List<TagConfiguration> list=conf.getTagList();
			Object[] items = new Object[list.size()];

			int i=0;
			for(TagConfiguration tag:list){
				items[i]=new CheckListItem(tag.getName(),tag.isVisible());
				i++;
			}

			return items;
		}

		public ModuleConfiguration getConfiguration()
		{
			configuration.setVisible(visibleCB.isSelected());
			configuration.setPosition(GUIPlaceholder.valueOf(position.getText()));
			ListModel model=list.getModel();
			for(int i=0; i<model.getSize(); i++){
				configuration.getTagList().get(i).setVisible(((CheckListItem)model.getElementAt(i)).isSelected());
			}

			return configuration;
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) 
	{
		if(e.getSource() == okBtn){
			//TODO: aenderungen?
			applyConfigurations(prop);
			setVisible(false);
			dispose();
		}else if(e.getSource() == openFileBtn){
			fc.setSelectedFile(new File(fileName.getText()));
			int returnVal = fc.showOpenDialog(this);
			 if (returnVal == JFileChooser.APPROVE_OPTION) {
	                File newFile = fc.getSelectedFile();
	                reloadGUIData(newFile);
	                fileName.setText(file.getAbsolutePath());
			 }
		}else if(e.getSource()==saveFileBtn){
			//TODO: aenderungen?
			applyConfigurations(prop);
			
			fc.setSelectedFile(new File(fileName.getText()));
			int returnVal = fc.showSaveDialog(this);
			
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				UOSProfileWriter writer=new UOSProfileWriter();
				
				writer.save(file, prop);
				
			}
		}else if(e.getSource()==cancelBtn){
			setVisible(false);
			dispose();
		}
	}

	private void reloadGUIData(File newFile) 
	{
		UOSProfileReader propReader=new UOSProfileReader(newFile);
		CustomViewProperties newProp=propReader.getViewProperties();
		
		if(newProp==null){
			return;
		}
		prop=newProp;
		file=newFile;
		dataPane=custumViewEditor();
		
		main.removeAll();
		main.add(headerPane,BorderLayout.NORTH);
		main.add(dataPane,BorderLayout.CENTER);
		main.add(buttonPane,BorderLayout.SOUTH);
		
		revalidate();
		repaint();
	}
	
	
//	 public class TagTableModel extends AbstractTableModel {
//
//	        private List<TagConfiguration> lstRecords;
//
//	        public TagTableModel() {
//	            lstRecords = new ArrayList<>(24);
//	        }
//
//	        public void add(TagConfiguration record) {
//	            lstRecords.add(record);
//	            fireTableRowsInserted(lstRecords.size() - 1, lstRecords.size() - 1);
//	        }
//
//	        public void remove(TagConfiguration record) {
//	            if (lstRecords.contains(record)) {
//	                int index = lstRecords.indexOf(record);
//	                remove(index);
//	            }
//	        }
//
//	        public void remove(int index) {
//	            lstRecords.remove(index);
//	            fireTableRowsDeleted(index, index);
//	        }
//
//	        @Override
//	        public int getRowCount() {
//	            return lstRecords.size();
//	        }
//
//	        @Override
//	        public int getColumnCount() {
//	            return 2;
//	        }
//
//	        @Override
//	        public Class<?> getColumnClass(int columnIndex) {
//	            Class clazz = String.class;
//	            switch (columnIndex) {
//	                case 0:
//	                    clazz = Integer.class;
//	                    break;
//	            }
//	            return clazz;
//	        }
//
//	        @Override
//	        public String getColumnName(int column) {
//	            String name = null;
//	            switch (column) {
//	                case 0:
//	                    name = "ID";
//	                    break;
//	                case 1:
//	                    name = "Value";
//	                    break;
//	                case 2:
//	                	name= "Default Val";
//	            }
//	            return name;
//	        }
//
//	        @Override
//	        public Object getValueAt(int rowIndex, int columnIndex) {
//	        	TagConfiguration record = lstRecords.get(rowIndex);
//	            Object value = null;
//	            switch (columnIndex) {
//	                case 0:
//	                    value = record.getID();
//	                    break;
//	                case 1:
//	                    value = record.getValue();
//	                    break;
//	                case 2:
//	                	value = record.getValue();
//	                	break;
//	            }
//	            return value;
//	        }
//
//	        @Override
//	        public boolean isCellEditable(int rowIndex, int columnIndex) {
//	            return columnIndex !=1;
//	        }
//
//	        @Override
//	        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
//	        	TagConfiguration record = lstRecords.get(rowIndex);
//	            switch (columnIndex) {
//	                case 1:
//	                    record.setValue(aValue == null ? null : aValue.toString());
//	                    fireTableCellUpdated(rowIndex, columnIndex);
//	                    break;
//	            }
//	        }
//	    }
}
