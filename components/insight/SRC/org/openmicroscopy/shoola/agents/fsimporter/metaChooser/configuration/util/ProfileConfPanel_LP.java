package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import ome.units.unit.Unit;

import org.jdesktop.swingx.JXTreeTable;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules.ElementsCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.view.ModuleViewer;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.ModuleConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.TagNames;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.util.ProfileConfPanel.JTableX;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.util.ProfileConfPanel.TagTableModel;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.microscope.MetaDataUI.GUIPlaceholder;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.DisabledPanel;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.WarningDialog;

public class ProfileConfPanel_LP extends JPanel
{
	/** activated if a module should'nt be displayed*/
	private DisabledPanel tableGlassPane;
	/** table of tags */
	private JTableX_LP myTable;
	/** checkbox to select module visible */
	private JCheckBox visibleCB;
	/** combobox to select module position*/
	private JComboBox position;
	
	/** container for tag configuration*/
	private ModuleConfiguration configuration;
	/** intern assigned index of the module */
	private int index;
	/** module name */
	private String name;
	/** list all possible tags for this module */
	private PreTagData[] availableTags;
//	private JButton loadFieldValBtn;
	/** possible positions for modules for the metadata editor*/
	private String[] positions=ElementsCompUI.getNames(GUIPlaceholder.class);
	/** data for unit comboboxfield for a tag. If the tag hasn't a n unit, value at tag position is null*/
	private List<String[]> unitCBData;
	
	private List<String[]> enumerateCBData;
	/** data for fixed field value*/
	private List<String[]> fieldCBData;
	/** */
	private String[] selectedPos;


	/**
	 * Default Constructor
	 * @param conf container for visible tags and their configuration
	 * @param name of module
	 * @param availableTags all possible tags for this module. @see TagNames definitions.
	 * @param editableTags flag if the data of this module should be editable inside the Customize View.
	 * @param i assigned index
	 */
	public ProfileConfPanel_LP(ModuleConfiguration conf,
			String name, PreTagData[] availableTags, boolean editableTags, int i,String[] positions) {
		
		System.out.println("# ProfileConfPanel_LP::new Instance()");
		System.out.println("\t... conf = "+(conf==null?"null":"not null"));
		this.configuration=conf;
		this.index=i;
		this.name=name;
		this.availableTags=availableTags;
		this.selectedPos=positions;
		
		unitCBData=new ArrayList<String[]>();
		enumerateCBData=new ArrayList<String[]>();
		
		buildGUI();
		setConfigurationData(conf, editableTags);
	}
	
	/**
	 * Build gui.
	 */
	private void buildGUI()
	{
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setPreferredSize(new Dimension(300,300));

		JPanel titlePane=new JPanel(new FlowLayout(FlowLayout.LEFT));
		titlePane.setPreferredSize(new Dimension(300,30));
		JLabel label=new JLabel(name);
		Font font = label.getFont();
		Font boldFont = new Font(font.getFontName(), Font.BOLD, font.getSize());
		label.setFont(boldFont);

		
		JScrollPane tablePane=new JScrollPane();
		tableGlassPane=new DisabledPanel(tablePane);
		
		visibleCB=new JCheckBox();
		visibleCB.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED){
					tableGlassPane.setEnabled(true);
					position.setEnabled(true);
					selectedPos[index]=(String) position.getSelectedItem();
				}else{
					tableGlassPane.setEnabled(false);
					position.setEnabled(false);
					selectedPos[index]="";
				}
			}
		});
		
	
		position=new JComboBox(positions);
		position.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				 JComboBox selectedChoice = (JComboBox) e.getSource();
				 if(selectedChoice.isEnabled()){
					 testPosition((String) selectedChoice.getSelectedItem(),index);
				 }
			}
		});
//		
//		loadFieldValBtn = new JButton("Load GUI Input");
//		loadFieldValBtn.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				// TODO Auto-generated method stub
//				
//			}
//		});
//		loadFieldValBtn.setEnabled(false);

		titlePane.add(visibleCB);
		titlePane.add(label);
		titlePane.add(Box.createHorizontalStrut(5));
		titlePane.add(position);
//		titlePane.add(Box.createHorizontalStrut(10));
//		titlePane.add(loadFieldValBtn);
		
//		List<Object> lightPath =loadTestData();
		
		
		// table of tags and their data
		myTable=new JTableX_LP(null);
		myTable.setFillsViewportHeight(true);
//		myTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		myTable.setFillsViewportHeight(true);
		((JTableX_LP) myTable).setEditorEnumerateData(enumerateCBData);
		
		add(titlePane);
		add(Box.createVerticalStrut(2));
		add(tableGlassPane);
		tablePane.setViewportView(myTable);
	}



	private List<LightPathElement> loadTestData() 
	{
		List<LightPathElement> result = new ArrayList<LightPathElement>();
		
		List<TagConfiguration> confList=new ArrayList<TagConfiguration>();
		confList.add(new TagConfiguration("Model", "bla", null, ModuleViewer.OPTIONAL, true, null, null));
		confList.add(new TagConfiguration("Manufacturer", "bla", null, ModuleViewer.OPTIONAL, true, null, null));
		confList.add(new TagConfiguration("Type", "bla", null, ModuleViewer.OPTIONAL, true, null, null));
		confList.add(new TagConfiguration("Filterwheel", "bla", null, ModuleViewer.OPTIONAL, true, null, null));
		
		LightPathElement elem=new LightPathElement("Filter", confList);
		result.add(elem);
		
		return result;
	}

	/**
	 * Set tags according to their predefinitions declared in the profile file.
	 * Nothing to do if no file available.
	 * 
	 * @param conf predefinition of view,tags and their data from file.
	 * @param editableTags flag if the data of this module should be editable inside the Customize View.
	 */
	private void setConfigurationData(ModuleConfiguration conf,
			boolean editableTags) 
	{
		if(conf==null)
			return;
					
		position.setSelectedItem(conf.getPosition().name());
		visibleCB.setSelected(conf.isVisible());

		// show tags, their value and unit
		List<LightPathElement> list=conf.getElementList();
		
		
		if(list!=null){
			System.out.println("# ProfileConfPanel_LP::setConfiguration() : "+list.size());
			for(LightPathElement e:list){
				addTags(e);
			}
		}
		
	
		
		if(!conf.isVisible()){
			tableGlassPane.setEnabled(false);
		}
	}

	public void addTags(LightPathElement e)
	{
		((LP_TagTableModel) myTable.getModel()).add(e);
	}
	

	/** 
	 * Test given modul position if this position is empty and available.
	 * 
	 * @param selectedItem
	 * @param index
	 */
	protected void testPosition(String selectedItem, int index) 
	{
		String oldval=selectedPos[index];
		selectedPos[index]="";
		boolean stillExists=false;
		for(int i=0; i<8;i++){
			if(!selectedItem.equals("") && selectedPos[i].equals(selectedItem)){
				stillExists=true;
			}
		}
		
		if(stillExists){
        	WarningDialog ld = new WarningDialog("Please check selected Position!", 
					"Position is used by another component",
					this.getClass().getSimpleName());
			ld.setVisible(true);
        }else{
        	selectedPos[index]=selectedItem;
        }			
	}


	/**
	 * Read from gui predefinitions and configurations for this module.
	 * @return
	 */
	public ModuleConfiguration getConfiguration()
	{
		ModuleConfiguration conf=new ModuleConfiguration(visibleCB.isSelected(),
				GUIPlaceholder.valueOf((String) position.getSelectedItem()), "1");

		LP_TagTableModel dataModel=(LP_TagTableModel) myTable.getModel();
		List<LightPathElement> elementList=new ArrayList<LightPathElement>();
		
		
		// deselect selection in table for data upgrade
		if (myTable.isEditing()) myTable.getCellEditor().stopCellEditing();
		
		for(int i=0; i<dataModel.getRowCount(); i++){
			elementList.add((LightPathElement) dataModel.getRow(i));
		}
		conf.setElementList(elementList);
		return conf;
	}
	
	public class LP_TagTableModel extends AbstractTableModel
	{
		String[] columns = {"Class","Model","Manufacturer","Type","Filterwheel"};
		private List<List> data = new ArrayList<>();

		public static final String NOEDITABLE="--";
		
		public static final int COL_CLASS=0;
		public static final int COL_MODEL=1;
		public static final int COL_MANU=2;
		public static final int COL_TYPE=3;
		public static final int COL_FW=4;
		

		@Override
		public String getColumnName(int column) 
		{
			return columns[column];
		}

		
		/**
		 * Add given tag predefinitions to table.
		 * @param t tag information and configurations
		 * @param isSettingsTag marker for setting tags
		 * @param editable
		 */
		public void add(LightPathElement t) 
		{
			List<Object> list=new ArrayList<Object>(columns.length);
			
			//col: class
			list.add(t.getClazz());
			// col: model
			list.add(t.getProperty(TagNames.MODEL));
			
			// col: manufacturer
			list.add(t.getProperty(TagNames.MANUFAC));
			
			//col: type
			list.add(t.getProperty(TagNames.TYPE));
			
			//col: filterwheel
			list.add(t.getProperty(TagNames.FILTERWHEEL));
			
			data.add(list);

			fireTableRowsInserted(data.size() - 1, data.size() - 1);
		}


		@Override
		public int getRowCount() 
		{
			return data.size();
		}

		@Override
		public int getColumnCount() 
		{
			return columns.length;
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) 
		{
			Class classOfCol = String.class;

			return classOfCol;
		}



		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) 
		{
			boolean editable=true;
			
			return editable;
		}

		/**
		 * Insert given tag at given rowIndex.
		 * @param rowIndex
		 * @param t
		 * @param settings
		 */
		public void insertRow(int rowIndex, LightPathElement t)
		{
			List<Object> list=new ArrayList<Object>(columns.length);
			
			//col: class
			list.add(t.getClazz());
			// col: model
			list.add(t.getProperty(TagNames.MODEL));
			
			// col: manufacturer
			list.add(t.getProperty(TagNames.MANUFAC));
			
			//col: type
			list.add(t.getProperty(TagNames.TYPE));
			
			//col: filterwheel
			list.add(t.getProperty(TagNames.FILTERWHEEL));
			
			data.add(rowIndex, list);

				fireTableRowsInserted(rowIndex,rowIndex);
		}
		
		
		/**
		 * Update data after :cell editing + press enter (or select over cell)
		 */
		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			
			if(aValue instanceof String){
				List temp=data.get(rowIndex);
				temp.set(columnIndex, aValue.toString());
				data.set(rowIndex, temp);

			}else{
				data.get(rowIndex).set(columnIndex, aValue);
			}
			fireTableRowsUpdated(rowIndex, rowIndex);
			
//			System.out.println("Edit value at "+rowIndex+", "+columnIndex+" input: "+((String)getValueAt(rowIndex,columnIndex)));
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			return data.get(rowIndex).get(columnIndex);
		}
		
		
		
		/**
		 * Return row as TagConfiguration
		 * @param rowIndex
		 * @return row as TagConfiguration object
		 */
		public LightPathElement getRow(int rowIndex)
		{
			LightPathElement elem=new LightPathElement((String)getValueAt(rowIndex,COL_CLASS), null);
			elem.setTag(TagNames.MODEL, (String) getValueAt(rowIndex,COL_MODEL));
			elem.setTag(TagNames.MANUFAC, (String) getValueAt(rowIndex,COL_MANU));
			elem.setTag(TagNames.TYPE, (String) getValueAt(rowIndex,COL_TYPE));
			elem.setTag(TagNames.FILTERWHEEL, (String) getValueAt(rowIndex,COL_FW));
			
			return elem;
		}
	}//end class LP_TagTableModel
	
	
	public class JTableX_LP extends JTable
	{
		
		/** possible enumeration data */
		private List<String[]> enumerateComboBoxData=null;
		
		/** all supported tags for current module*/
		private PreTagData[] availableTags;
		
		/** Create new instance */
		public JTableX_LP(PreTagData[] availableTags)
		{
			super(new LP_TagTableModel());
			setShowGrid(false);
			
			this.availableTags=availableTags;
			
			getTableHeader().setBackground(Color.lightGray);
			
			requestFocusInWindow();
		}
		
		/**
		 * 
		 * @param data list of enumerate data for all enumerate tags in the table. 
		 * 
		 */
		public void setEditorEnumerateData(List<String[]> data)
		{
			enumerateComboBoxData=data;
		}
		
		/**
		 * Set unit field as combobox if units available for this tag
		 */
		public TableCellEditor getCellEditor(int row, int column)
		{
			int modelColumn = convertColumnIndexToModel( column );

            if(modelColumn == LP_TagTableModel.COL_TYPE && 
            		enumerateComboBoxData!=null && enumerateComboBoxData.size()>row && enumerateComboBoxData.get(row)!=null ){
            	 JComboBox<String> comboBox1 = new JComboBox<String>( enumerateComboBoxData.get(row));
                 return new DefaultCellEditor( comboBox1 );
            }else{
                return super.getCellEditor(row, column);
            }
		}
		
		/**
		 * Highlight non editable field value and field unit cells
		 */
		@Override
	    public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
	        Component comp = super.prepareRenderer(renderer, row, col);
	        if(!getModel().isCellEditable(row, col) && (col==LP_TagTableModel.COL_TYPE )){
	        	comp.setBackground(UIManager.getColor("TextField.inactiveBackground"));
	        	comp.setFont(getFont());
	        } 
	        else {
	        	comp.setBackground(getBackground());
	        	comp.setForeground(getForeground());
	        	comp.setFont(getFont());
	        }
	        return comp;
	    }
	}//end class JTableX_LP

}
