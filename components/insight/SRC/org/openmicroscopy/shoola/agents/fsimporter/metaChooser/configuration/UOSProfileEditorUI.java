package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
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
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import ome.model.units.UNITS;
import ome.units.unit.Unit;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.microscope.CustomViewProperties;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.microscope.MetaDataUI.GUIPlaceholder;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagData;
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
	private JTextField micName;

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
		main.setBorder(new EmptyBorder(5, 5, 5, 5));

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
		micName=new JTextField(prop.getMicName());
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

		pane.add(new ConfigurationPanel2(prop.getImageConf(),"Image Modul",false));
		pane.add(new ConfigurationPanel2(prop.getObjConf(),"Objective Modul",true));
		pane.add(new ConfigurationPanel2(prop.getDetectorConf(),"Detector Modul",true));
		pane.add(new ConfigurationPanel2(prop.getLightSrcConf(),"LightSource Modul",true));

		pane.add(new ConfigurationPanel2(prop.getChannelConf(),"Channel Modul",false));
		pane.add(new ConfigurationPanel2(prop.getLightPathConf(),"LightPath Modul",false));
		pane.add(new ConfigurationPanel2(prop.getSampleConf(),"Sample Modul",false));
		pane.add(new ConfigurationPanel2(prop.getExpConf(),"Experiment Modul",false));

		return pane;

	}



	//	private void applyConfigurations(CustomViewProperties cView)
	//	{
	//		Component[] comp=dataPane.getComponents();
	//		cView.setImageConf(((ConfigurationPanel)comp[0]).getConfiguration());
	//		cView.setObjConf(((ConfigurationPanel) comp[1]).getConfiguration());
	//		cView.setDetectorConf(((ConfigurationPanel) comp[2]).getConfiguration());
	//		cView.setLightSrcConf(((ConfigurationPanel) comp[3]).getConfiguration());
	//		
	//		cView.setChannelConf(((ConfigurationPanel) comp[4]).getConfiguration());
	//		cView.setLightPathConf(((ConfigurationPanel) comp[5]).getConfiguration());
	//		cView.setSampleConf(((ConfigurationPanel) comp[6]).getConfiguration());
	//		cView.setExperimenterConf(((ConfigurationPanel) comp[7]).getConfiguration());
	//		
	//	}

	private void applyConfigurations(CustomViewProperties cView)
	{
		cView.setMicName(micName.getText());
		Component[] comp=dataPane.getComponents();
		cView.setImageConf(((ConfigurationPanel2)comp[0]).getConfiguration());
		cView.setObjConf(((ConfigurationPanel2) comp[1]).getConfiguration());
		cView.setDetectorConf(((ConfigurationPanel2) comp[2]).getConfiguration());
		cView.setLightSrcConf(((ConfigurationPanel2) comp[3]).getConfiguration());

		cView.setChannelConf(((ConfigurationPanel2) comp[4]).getConfiguration());
		cView.setLightPathConf(((ConfigurationPanel2) comp[5]).getConfiguration());
		cView.setSampleConf(((ConfigurationPanel2) comp[6]).getConfiguration());
		cView.setExperimenterConf(((ConfigurationPanel2) comp[7]).getConfiguration());
	}

	//	private class CheckListItem
	//	{
	//		private Object item;
	//		private boolean selected;
	//
	//		public CheckListItem(Object item)
	//		{
	//			this.item = item;
	//		}
	//
	//		public CheckListItem(Object item, boolean selected)
	//		{
	//			this.item = item;
	//			this.selected=selected;
	//		}
	//
	//
	//
	//		@SuppressWarnings("unused")
	//		public Object getItem()
	//		{
	//			return item;
	//		}
	//
	//		public boolean isSelected()
	//		{
	//			return selected;
	//		}
	//
	//		public void setSelected(boolean isSelected)
	//		{
	//			this.selected = isSelected;
	//		}
	//
	//		@Override
	//		public String toString()
	//		{
	//			return item.toString();
	//		}
	//	}
	//
	//	private class CheckBoxListRenderer extends JCheckBox
	//	implements ListCellRenderer
	//	{
	//		public Component getListCellRendererComponent(JList comp, Object value,
	//				int index, boolean isSelected, boolean hasFocus)
	//		{
	//			setEnabled(comp.isEnabled());
	//			setSelected(((CheckListItem) value).isSelected());
	//			setFont(comp.getFont());
	//			setText(value.toString());
	//
	//			if (isSelected)
	//			{
	//				setBackground(comp.getSelectionBackground());
	//				setForeground(comp.getSelectionForeground());
	//			}
	//			else
	//			{
	//				setBackground(comp.getBackground());
	//				setForeground(comp.getForeground());
	//			}
	//
	//			return this;
	//		}
	//	}



	//	class ConfigurationPanel extends JPanel
	//	{
	//		private JList list;
	//		private JCheckBox visibleCB;
	//		private JTextField position;
	//		private ModuleConfiguration configuration;
	//
	//		public ConfigurationPanel(ModuleConfiguration conf, String name) 
	//		{
	//			configuration=conf;
	//			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	//
	//			JPanel titlePane=new JPanel(new FlowLayout(FlowLayout.LEFT));
	//			JLabel label=new JLabel(name);
	//			Font font = label.getFont();
	//			Font boldFont = new Font(font.getFontName(), Font.BOLD, font.getSize());
	//			label.setFont(boldFont);
	//			
	//			
	//			visibleCB=new JCheckBox();
	//			visibleCB.setSelected(conf.isVisible());
	//			visibleCB.addItemListener(new ItemListener() {
	//
	//				@Override
	//				public void itemStateChanged(ItemEvent e) {
	//					if(e.getStateChange() == ItemEvent.SELECTED)
	//						list.setEnabled(true);
	//					else
	//						list.setEnabled(false);
	//				}
	//			});
	//			position = new JTextField(conf.getPosition().name()); 
	//
	//			titlePane.add(visibleCB);
	//			titlePane.add(Box.createHorizontalStrut(2));
	//			titlePane.add(label);
	//			titlePane.add(Box.createHorizontalStrut(10));
	//			titlePane.add(position);
	//
	//			list = new JList(getTagListAsItems(conf));
	//			list.setCellRenderer((ListCellRenderer) new CheckBoxListRenderer());
	//			list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	//			list.setVisibleRowCount(10);
	//			list.addMouseListener(new MouseAdapter()
	//			{
	//				@Override
	//				public void mouseClicked(MouseEvent event)
	//				{
	//					selectItem( (JList) event.getSource(),event.getPoint());
	//				}
	//			});
	//
	//			KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0);
	//			Object mapKey = keyStroke.toString();
	//			list.getInputMap().put(keyStroke, mapKey);
	//			list.getActionMap().put(mapKey, new AbstractAction()
	//			{
	//				public void actionPerformed(ActionEvent event)
	//				{
	//					toggleSelectedItem( (JList) event.getSource());
	//				}
	//			});
	//
	//			if(!conf.isVisible())
	//				list.setEnabled(false);
	//
	//			add(titlePane);
	//			add(Box.createVerticalStrut(2));
	//			add(new JScrollPane(list));
	//		}
	//		
	//		
	//		private void selectItem(JList jlist,Point point)
	//		{
	//			int index = jlist.locationToIndex(point);
	//
	//			if (index >= 0)
	//			{
	//				CheckListItem item = (CheckListItem)jlist.getModel().getElementAt(index);
	//				item.setSelected(!item.isSelected());
	//				jlist.repaint(jlist.getCellBounds(index, index));
	//			}
	//		}
	//
	//		private void toggleSelectedItem(JList jlist)
	//		{
	//			int index = jlist.getSelectedIndex();
	//
	//			if (index >= 0)
	//			{
	//				CheckListItem item = (CheckListItem)jlist.getModel().getElementAt(index);
	//				item.setSelected(!item.isSelected());
	//				jlist.repaint(jlist.getCellBounds(index, index));
	//			}
	//		}
	//		private Object[] getTagListAsItems(ModuleConfiguration conf) 
	//		{
	//			List<TagConfiguration> list=conf.getTagList();
	//			Object[] items = new Object[list.size()];
	//
	//			int i=0;
	//			for(TagConfiguration tag:list){
	//				items[i]=new CheckListItem(tag.getName(),tag.isVisible());
	//				i++;
	//			}
	//
	//			return items;
	//		}
	//
	//		public ModuleConfiguration getConfiguration()
	//		{
	//			configuration.setVisible(visibleCB.isSelected());
	//			configuration.setPosition(GUIPlaceholder.valueOf(position.getText()));
	//			ListModel model=list.getModel();
	//			for(int i=0; i<model.getSize(); i++){
	//				configuration.getTagList().get(i).setVisible(((CheckListItem)model.getElementAt(i)).isSelected());
	//			}
	//
	//			return configuration;
	//		}
	//	}


	class ConfigurationPanel2 extends JPanel
	{
		private JTable myTable;
		private JCheckBox visibleCB;
		private JTextField position;
		private ModuleConfiguration configuration;

		public ConfigurationPanel2(ModuleConfiguration conf, String name,boolean full) 
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
						myTable.setEnabled(true);
					else
						myTable.setEnabled(false);
				}
			});
			position = new JTextField(conf.getPosition().name()); 

			titlePane.add(visibleCB);
			titlePane.add(Box.createHorizontalStrut(2));
			titlePane.add(label);
			titlePane.add(Box.createHorizontalStrut(10));
			titlePane.add(position);

			myTable=new JTable(new TagTableModel());
			myTable.setShowGrid(false);
			//hide settings col
			myTable.getColumnModel().getColumn(5).setMinWidth(0);
			myTable.getColumnModel().getColumn(5).setMaxWidth(0);
			myTable.getColumnModel().getColumn(5).setPreferredWidth(0);
			
			myTable.requestFocusInWindow();
			
			List<TagConfiguration> tagList=conf.getTagList();
			List<TagConfiguration> settList=conf.getSettingList();
			if(tagList!=null){
				if(full){
					for(TagConfiguration t:tagList){
						((TagTableModel) myTable.getModel()).addFull(t,"");
					}
					for(TagConfiguration t:settList){
						((TagTableModel) myTable.getModel()).addFull(t,"S");
					}
				}else{
					for(TagConfiguration t:tagList){
						((TagTableModel) myTable.getModel()).add(t,"");
					}
					for(TagConfiguration t:settList){
						((TagTableModel) myTable.getModel()).add(t,"S");
					}
				}
			}
			//		

			if(!conf.isVisible())
				myTable.setEnabled(false);

			add(titlePane);
			add(Box.createVerticalStrut(2));
			add(new JScrollPane(myTable));
		}

		public ModuleConfiguration getConfiguration()
		{
			configuration.setVisible(visibleCB.isSelected());
			configuration.setPosition(GUIPlaceholder.valueOf(position.getText()));
			TagTableModel dataModel=(TagTableModel) myTable.getModel();
			int j=0;
			for(int i=0; i<dataModel.getRowCount(); i++){
				if(dataModel.getValueAt(i, 5).equals("S")){
					//visible
					configuration.getSettingList().get(j).setVisible((boolean) dataModel.getValueAt(i, 0));
					//value
					if(dataModel.saveable(i,2))
						configuration.getSettingList().get(j).setValue( (String) dataModel.getValueAt(i, 2));
					//unit
					if(dataModel.saveable(i,3)){
						try {
							configuration.getSettingList().get(j).setUnit(
									UOSHardwareReader.parseUnit((String)dataModel.getValueAt(i, 3), (String)dataModel.getValueAt(i, 1)));
						} catch (Exception e) {
							configuration.getSettingList().get(j).setUnit(null);
						}
					}
					configuration.getSettingList().get(j).setProperty((boolean)dataModel.getValueAt(i, 4));
					j++;
				}else{
					//visible
					configuration.getTagList().get(i).setVisible((boolean) dataModel.getValueAt(i, 0));
					//value
					if(dataModel.saveable(i,2))
						configuration.getTagList().get(i).setValue( (String) dataModel.getValueAt(i, 2));
					//unit
					if(dataModel.saveable(i,3)){
						try {
							configuration.getTagList().get(i).setUnit(
									UOSHardwareReader.parseUnit((String)dataModel.getValueAt(i, 3), (String)dataModel.getValueAt(i, 1)));
						} catch (Exception e) {
							configuration.getTagList().get(i).setUnit(null);
						}
					}
					configuration.getTagList().get(i).setProperty((boolean)dataModel.getValueAt(i, 4));
				}
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
				File newFile = fc.getSelectedFile();
				UOSProfileWriter writer=new UOSProfileWriter();

				writer.save(newFile, prop);
				reloadGUIData(newFile);
				fileName.setText(file.getAbsolutePath());
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
		micName.setText(prop.getMicName());
		dataPane=custumViewEditor();

		main.removeAll();
		main.add(headerPane,BorderLayout.NORTH);
		main.add(dataPane,BorderLayout.CENTER);
		main.add(buttonPane,BorderLayout.SOUTH);

		revalidate();
		repaint();
	}


	//TODO: navigation by tab-key
	public class MyTable extends JTable
	{
		public MyTable()
        {
            super(new TagTableModel());
//            putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
            setDefaultRenderer(String.class, new Renderer());
            DefaultCellEditor dce = new DefaultCellEditor(new JTextField());
            dce.setClickCountToStart(1);
//            setDefaultEditor(String.class, dce);
            setOpaque(false);
            setShowGrid(false);
            getColumnModel().getColumn(5).setMinWidth(0);
			getColumnModel().getColumn(5).setMaxWidth(0);
			getColumnModel().getColumn(5).setPreferredWidth(0);
            configure();
        }
  
        private void configure()
        {
            setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            setCellSelectionEnabled(true);
            // Add SelectionListeners to track selection changes
            // across columns.
            getColumnModel().getSelectionModel().addListSelectionListener(
                        new ExploreSelectionListener());
        }
  
        private class ExploreSelectionListener implements ListSelectionListener
        {
            public void valueChanged(ListSelectionEvent e)
            {
                if(!e.getValueIsAdjusting())
                {
                    int row = getSelectedRow();
                    int col = getSelectedColumn();
                    // Make sure we start with legal values.
                    while(col < 0) col++;
                    while(row < 0) row++;
                    // Find the next editable cell.
                    while(!isCellEditable(row, col))
                    {
                        col++;
                        if(col > getColumnCount()-1)
                        {
                            col = 1;
                            row = (row == getRowCount()-1) ? 1 : row+1;
                        }
                    }
                    // Select the cell in the table.
                    final int r = row, c = col;
                    EventQueue.invokeLater(new Runnable()
                    {
                        public void run()
                        {
                            changeSelection(r, c, false, false);
                        }
                    });
                    // Edit.
                    if(isCellEditable(row, col))
                    {
                        editCellAt(row, col);
                        ((JTextField)editorComp).selectAll();
                        editorComp.requestFocusInWindow();
                    }
                }
            }
        }
  
        private class Renderer implements TableCellRenderer
        {
            DefaultTableCellRenderer renderer;
            JTextField textField;
            protected Border border = new EmptyBorder(1, 1, 1, 1);
  
            public Renderer()
            {
                renderer = new DefaultTableCellRenderer();
                textField = new JTextField();
                textField.setHorizontalAlignment(SwingConstants.RIGHT);
            }
  
            public Component getTableCellRendererComponent(JTable table,
                                                           Object value,
                                                           boolean isSelected,
                                                           boolean hasFocus,
                                                           int row, int column)
            {
                if (!isCellEditable(row, column))
                {
                    renderer.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus, row, column);
                    renderer.setHorizontalAlignment(column == 0
                        ? SwingConstants.LEFT
                        : SwingConstants.RIGHT);
                    renderer.setBackground(Color.GRAY.brighter());
                    renderer.setOpaque(false);
                    renderer.setFont(
                        table.getFont().deriveFont(9f).deriveFont(Font.BOLD));
                    renderer.setForeground(Color.BLACK);
                    renderer.setBorder(border);
                    return renderer;
                }
                textField.setText(value.toString());
                return textField;
            }
        }
    }
    
	
	
	public class TagTableModel extends AbstractTableModel 
	{
		String[] columns = {"Visible","Field Name","Field Value","Unit","Optional","Settings"};
		private List<List> data = new ArrayList<>();

		private String NOEDITABLE="--";

		@Override
		public String getColumnName(int column) {

			return columns[column];
		}
		
		public boolean saveable(int rowIdx, int colIdx) 
		{
			boolean saveVal=false;
			if(getValueAt(rowIdx, colIdx)!=null && !getValueAt(rowIdx,colIdx).equals(NOEDITABLE))
				saveVal=true;
			return saveVal;
		}
		
		public void addFull(TagConfiguration t,String settings) {
			List<Object> list=new ArrayList<Object>(columns.length);
			boolean hideUnit=false;
			if(UOSHardwareReader.getUnitList(t.getName())==null){
				hideUnit=true;
			}

			list.add(t.isVisible());
			list.add(t.getName());
			list.add(t.getValue());
			if(hideUnit)
				list.add(NOEDITABLE);
			else
				list.add(t.getUnitSymbol());
			list.add(t.getProperty());
			list.add(settings);
			data.add(list);

			fireTableRowsInserted(data.size() - 1, data.size() - 1);

		}
		
		public void add(TagConfiguration t,String settings) {
			List<Object> list=new ArrayList<Object>(columns.length);
			

			list.add(t.isVisible());
			list.add(t.getName());
			list.add(NOEDITABLE);
			list.add(NOEDITABLE);
			list.add(t.getProperty());
			list.add(settings);
			data.add(list);

			fireTableRowsInserted(data.size() - 1, data.size() - 1);

		}

		public void remove(TagConfiguration t) {
			if (data.contains(t)) {
				int index = data.indexOf(t);
				remove(index);
			}
		}

		public void remove(int index) {
			data.remove(index);
			fireTableRowsDeleted(index, index);
		}

		@Override
		public int getRowCount() {
			return data.size();
		}

		@Override
		public int getColumnCount() {
			return columns.length;
		}

		@Override
		//	        public Class<?> getColumnClass(int columnIndex) {
			//	        	return data.get(0).get(columnIndex).getClass();
			//	        }
		public Class<?> getColumnClass(int columnIndex) {
			Class classOfCol = String.class;
			if(columnIndex==0 || columnIndex==4)
				classOfCol=Boolean.class;

			return classOfCol;
		}



		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) 
		{
			boolean editable=true;
			
			if((columnIndex!=0 && columnIndex!= 4) &&
					(getValueAt(rowIndex, columnIndex)!=null && getValueAt(rowIndex, columnIndex).equals(NOEDITABLE))
					|| columnIndex==1 || columnIndex==5)
				editable=false;
			return editable;
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			data.get(rowIndex).set(columnIndex, aValue);
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			return data.get(rowIndex).get(columnIndex);
		}
	}
}
