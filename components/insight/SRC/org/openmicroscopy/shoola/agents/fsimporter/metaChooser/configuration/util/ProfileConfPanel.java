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

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules.ElementsCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.ModuleConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.TagNames;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.microscope.MetaDataUI.GUIPlaceholder;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.DisabledPanel;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.WarningDialog;

/**
 * ConfigurationPanel shows predefinitions for viewing a module and tags and their predefine values.
 * @author Kunis
 *
 */
public class ProfileConfPanel extends JPanel
{
	/** activated if a module should'nt be displayed*/
	private DisabledPanel tableGlassPane;
	/** table of tags */
	private JTable myTable;
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
	public ProfileConfPanel(ModuleConfiguration conf,
			String name, PreTagData[] availableTags, boolean editableTags, int i,String[] positions) {
		
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
		
		// table of tags and their data
		myTable=new JTableX(availableTags);
		myTable.setFillsViewportHeight(true);
		((JTableX) myTable).setEditorUnitData(unitCBData);
		((JTableX) myTable).setEditorEnumerateData(enumerateCBData);
		
		add(titlePane);
		add(Box.createVerticalStrut(2));
		add(tableGlassPane);
		tablePane.setViewportView(myTable);
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
		List<TagConfiguration> tagList=conf.getTagList();
		List<TagConfiguration> settList=conf.getSettingList();

		if(tagList!=null){
			for(TagConfiguration t:tagList){
				addEditableTag(t,"",editableTags);
			}
		}
		if(settList!=null){
			for(TagConfiguration t:settList){
				addEditableTag(t,"S",editableTags);
			}
		}
		
		if(!conf.isVisible()){
			tableGlassPane.setEnabled(false);
		}
	}

	
	

	/**
	 * @param t
	 */
	public void addEditableTag(TagConfiguration t,String clazz,boolean editable) 
	{
		unitCBData.add(t.getPossibleUnits());
		enumerateCBData.add(t.getPossibleValues());
		((TagTableModel) myTable.getModel()).add(t,clazz,editable);
	}
	
	 /*
     * This method picks good column sizes.
     * If all column heads are wider than the column's cells'
     * contents, then you can just use column.sizeWidthToFit().
     */
//    private void initColumnSizes(JTable table) {
//        TagTableModel model = (TagTableModel)table.getModel();
//        TableColumn column = null;
//        Component comp = null;
//        int headerWidth = 0;
//        int cellWidth = 0;
//        Object[] longValues = model.longValues;
//        TableCellRenderer headerRenderer =
//            table.getTableHeader().getDefaultRenderer();
// 
//        for (int i = 0; i < 5; i++) {
//            column = table.getColumnModel().getColumn(i);
// 
//            comp = headerRenderer.getTableCellRendererComponent(
//                                 null, column.getHeaderValue(),
//                                 false, false, 0, 0);
//            headerWidth = comp.getPreferredSize().width;
// 
//            comp = table.getDefaultRenderer(model.getColumnClass(i)).
//                             getTableCellRendererComponent(
//                                 table, longValues[i],
//                                 false, false, 0, i);
//            cellWidth = comp.getPreferredSize().width;
// 
//            if (DEBUG) {
//                System.out.println("Initializing width of column "
//                                   + i + ". "
//                                   + "headerWidth = " + headerWidth
//                                   + "; cellWidth = " + cellWidth);
//            }
// 
//            column.setPreferredWidth(Math.max(headerWidth, cellWidth));
//        }
//    }

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

		TagTableModel dataModel=(TagTableModel) myTable.getModel();
		List<TagConfiguration> settList=new ArrayList<TagConfiguration>();
		List<TagConfiguration> tagList=new ArrayList<TagConfiguration>();
		
		
		// deselct selection in table for data upgrade
		if (myTable.isEditing()) myTable.getCellEditor().stopCellEditing();
		
		for(int i=0; i<dataModel.getRowCount(); i++){
			if(dataModel.getValueAt(i, 5).equals("S")){
				settList.add(dataModel.getRow(i));
			}else{
				tagList.add(dataModel.getRow(i));
			}
		}
		conf.setSettingList(settList);
		conf.setTagList(tagList);
		return conf;
	}
	
	
	/**
	 * Table for tag data.
	 * @author Kunis
	 *
	 */
	public class JTableX extends JTable
	{
		/** possible unit data */
		private List<String[]> comboBoxData=null;
		/** possible enumeration data */
		private List<String[]> enumerateComboBoxData=null;
		/** popupmenu for insert a missing supported tag */
		private JPopupMenu popupMenu;
		/** all supported tags for current module*/
		private PreTagData[] availableTags;
		
		/** Create new instance */
		public JTableX(PreTagData[] availableTags)
		{
			super(new TagTableModel());
			setShowGrid(false);
			
			this.availableTags=availableTags;
			// fit size of column: visible and unit
			getColumnModel().getColumn(0).setMinWidth(15);
			getColumnModel().getColumn(0).setPreferredWidth(15);
			getColumnModel().getColumn(3).setMinWidth(15);
			getColumnModel().getColumn(3).setPreferredWidth(15);
			
			//hide col for optional (4)
			getColumnModel().getColumn(4).setMinWidth(0);
			getColumnModel().getColumn(4).setMaxWidth(0);
			getColumnModel().getColumn(4).setPreferredWidth(0);
			//hide col for settings notize (5)
			getColumnModel().getColumn(5).setMinWidth(0);
			getColumnModel().getColumn(5).setMaxWidth(0);
			getColumnModel().getColumn(5).setPreferredWidth(0);
			
			getTableHeader().setBackground(Color.lightGray);
			
			popupMenu = new JPopupMenu();
			JMenu insertMenu=new JMenu("Insert Tag");
			
			if(availableTags!=null){
				for(int i=0; i<availableTags.length; i++){
					JMenuItem item = new JMenuItem(availableTags[i].name);
					item.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							JMenuItem item= (JMenuItem) e.getSource();
							JPopupMenu menu= (JPopupMenu) item.getParent();
							insertTagAtTable(menu.getComponentZOrder(item));
						}
					});
					insertMenu.add(item);
				}
			}	
			popupMenu.add(insertMenu);
			addMouseListener(new MouseAdapter() {
		         public void mouseClicked(MouseEvent me) {
		            if (SwingUtilities.isRightMouseButton(me))
		               popupMenu.show(getParent(), me.getX(), me.getY());
		         }
		      });
			
			requestFocusInWindow();
		}
		
		 
		/**
		 * Insert selected tag from popup menu to the table after selected position
		 * @param index
		 */
		private void insertTagAtTable(int index) 
		{
			TagConfiguration t=new TagConfiguration(availableTags[index].name, "", 
					availableTags[index].defaultUnit, false, false, 
					availableTags[index].unitsList,TagNames.getEnumerationVal(availableTags[index].name));
			TagTableModel model=(TagTableModel) getModel();
			int row=getSelectedRow();
			model.insertRow(row, t, availableTags[index].settings);
		}
		
		/**
		 * 
		 * @param data list of unit combobox default data for all tags in the table. 
		 * If no units specified for a tag, element=null
		 */
		public void setEditorUnitData(List<String[]> data)
		{
			comboBoxData=data;
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

            if (modelColumn == 3 && comboBoxData.get(row)!=null)
            {
                JComboBox<String> comboBox1 = new JComboBox<String>( comboBoxData.get(row));
                return new DefaultCellEditor( comboBox1 );
            }else if(modelColumn == 2 && enumerateComboBoxData.get(row)!=null ){
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
	        if(!getModel().isCellEditable(row, col) && (col==2 || col==3)){
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
	}// JTableX
	
	
	public class TagTableModel extends AbstractTableModel 
	{
		String[] columns = {"Visible","Field Name","Field Value","Unit","Optional","Settings"};
		private List<List> data = new ArrayList<>();

		public static final String NOEDITABLE="--";
		
		public static final int COL_VISIBLE=0;
		public static final int COL_NAME=1;
		public static final int COL_VALUE=2;
		public static final int COL_UNIT=3;
		public static final int COL_PROP=4;
		public static final int COL_SETT=5;
		

		@Override
		public String getColumnName(int column) 
		{
			return columns[column];
		}
		
//		public boolean saveable(int rowIdx, int colIdx) 
//		{
//			boolean saveVal=false;
//			if(getValueAt(rowIdx, colIdx)!=null && !getValueAt(rowIdx,colIdx).equals(NOEDITABLE))
//				saveVal=true;
//			return saveVal;
//		}
		
		/**
		 * Add given tag predefinitions to table.
		 * @param t tag information and configurations
		 * @param isSettingsTag marker for setting tags
		 * @param editable
		 */
		public void add(TagConfiguration t,String isSettingsTag,boolean editable) 
		{
			List<Object> list=new ArrayList<Object>(columns.length);
			
			//unit available for this tag?
			boolean hideUnit=false;
			if(TagNames.getUnitList(t.getName())==null){
				hideUnit=true;
			}
			
			//col: visible
			list.add(t.isVisible());
			// col: field name
			list.add(t.getName());
			
			// col: field value
			if(!editable)
				list.add(NOEDITABLE);
			else{
				list.add(t.getValue());
			}
			//col: unit
			if(!editable || hideUnit){
				list.add(NOEDITABLE);
			}else{
				list.add(t.getUnitSymbol());
			}
			
			//invisible col: property
			list.add(t.getProperty());
			//invisible col: is setting tag
			list.add(isSettingsTag);
			
			data.add(list);

			fireTableRowsInserted(data.size() - 1, data.size() - 1);
		}

//		public void remove(TagConfiguration t) 
//		{
//			if (data.contains(t)) {
//				int index = data.indexOf(t);
//				remove(index);
//			}
//		}
//
//		public void remove(int index) 
//		{
//			data.remove(index);
//			fireTableRowsDeleted(index, index);
//		}

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
			if(columnIndex==0 || columnIndex==4)
				classOfCol=Boolean.class;

			return classOfCol;
		}



		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) 
		{
			boolean editable=true;
			
			if((columnIndex!=COL_VISIBLE && columnIndex!= COL_PROP) &&
					(getValueAt(rowIndex, columnIndex)!=null && getValueAt(rowIndex, columnIndex).equals(NOEDITABLE))
					|| columnIndex==COL_NAME || columnIndex==COL_SETT)
				editable=false;
			return editable;
		}

		/**
		 * Insert given tag at given rowIndex.
		 * @param rowIndex
		 * @param t
		 * @param settings
		 */
		public void insertRow(int rowIndex, TagConfiguration t,String settings)
		{
			List<Object> list=new ArrayList<Object>(columns.length);
			boolean hideUnit=false;
			if(TagNames.getUnitList(t.getName())==null){
				hideUnit=true;
			}
			//visible
			list.add(t.isVisible());
			// field name
			list.add(t.getName());
			// field value
			list.add(t.getValue());
			//unit
			if(hideUnit)
				list.add(NOEDITABLE);
			else
				list.add(t.getUnitSymbol());
			//optional
			list.add(t.getProperty());
			list.add(settings);
			
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
		public TagConfiguration getRow(int rowIndex)
		{
			TagConfiguration tag=null;
			String name=(String) getValueAt(rowIndex,COL_NAME);
			String value=(String) getValueAt(rowIndex,COL_VALUE);
			value=(value==null ||value.equals(NOEDITABLE))? "":value;
			Unit unit=null;
			String[] pU=null;
			String[] eVal=null;
			if(!((String) getValueAt(rowIndex,COL_UNIT)).equals(NOEDITABLE)){
				try {
					unit=TagNames.parseUnit((String) getValueAt(rowIndex,COL_UNIT),name);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
	}
				pU= TagNames.getUnits(name);
				eVal=TagNames.getEnumerationVal(name);
				
			}
			tag=new TagConfiguration(name, value, unit, (Boolean)getValueAt(rowIndex,COL_PROP),(Boolean) getValueAt(rowIndex,0), pU,eVal);
			return tag;
		}
	}
	
	//TODO: navigation by tab-key
//	public class MyTable extends JTable
//	{
//		public MyTable()
//        {
//            super(new TagTableModel());
////            putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
//            setDefaultRenderer(String.class, new Renderer());
//            DefaultCellEditor dce = new DefaultCellEditor(new JTextField());
//            dce.setClickCountToStart(1);
////            setDefaultEditor(String.class, dce);
//            setOpaque(false);
//            setShowGrid(false);
//            getColumnModel().getColumn(5).setMinWidth(0);
//			getColumnModel().getColumn(5).setMaxWidth(0);
//			getColumnModel().getColumn(5).setPreferredWidth(0);
//            configure();
//        }
//  
//        private void configure()
//        {
//            setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//            setCellSelectionEnabled(true);
//            // Add SelectionListeners to track selection changes
//            // across columns.
//            getColumnModel().getSelectionModel().addListSelectionListener(
//                        new ExploreSelectionListener());
//        }
//  
//        private class ExploreSelectionListener implements ListSelectionListener
//        {
//            public void valueChanged(ListSelectionEvent e)
//            {
//            	// wann aufgerufen?????
//            	System.out.println("Edit cell at: "+getSelectedRow()+", "+getSelectedColumn());
//                if(!e.getValueIsAdjusting())
//                {
//                    int row = getSelectedRow();
//                    int col = getSelectedColumn();
//                    // Make sure we start with legal values.
//                    while(col < 0) col++;
//                    while(row < 0) row++;
//                    // Find the next editable cell.
//                    while(!isCellEditable(row, col))
//                    {
//                        col++;
//                        if(col > getColumnCount()-1)
//                        {
//                            col = 1;
//                            row = (row == getRowCount()-1) ? 1 : row+1;
//                        }
//                    }
//                    // Select the cell in the table.
//                    final int r = row, c = col;
//                    EventQueue.invokeLater(new Runnable()
//                    {
//                        public void run()
//                        {
//                            changeSelection(r, c, false, false);
//                        }
//                    });
//                    // Edit.
//                    if(isCellEditable(row, col))
//                    {
//                    	
//                        editCellAt(row, col);
//                        ((JTextField)editorComp).selectAll();
//                        editorComp.requestFocusInWindow();
//                    }
//                }
//            }
//        }
//  
//        private class Renderer implements TableCellRenderer
//        {
//            DefaultTableCellRenderer renderer;
//            JTextField textField;
//            protected Border border = new EmptyBorder(1, 1, 1, 1);
//  
//            public Renderer()
//            {
//                renderer = new DefaultTableCellRenderer();
//                textField = new JTextField();
//                textField.setHorizontalAlignment(SwingConstants.RIGHT);
//            }
//  
//            public Component getTableCellRendererComponent(JTable table,
//                                                           Object value,
//                                                           boolean isSelected,
//                                                           boolean hasFocus,
//                                                           int row, int column)
//            {
//                if (!isCellEditable(row, column))
//                {
//                    renderer.getTableCellRendererComponent(table, value,
//                        isSelected, hasFocus, row, column);
//                    renderer.setHorizontalAlignment(column == 0
//                        ? SwingConstants.LEFT
//                        : SwingConstants.RIGHT);
//                    renderer.setBackground(Color.GRAY.brighter());
//                    renderer.setOpaque(false);
//                    renderer.setFont(
//                        table.getFont().deriveFont(9f).deriveFont(Font.BOLD));
//                    renderer.setForeground(Color.BLACK);
//                    renderer.setBorder(border);
//                    return renderer;
//                }
//                textField.setText(value.toString());
//                return textField;
//            }
//        }
//    }
//    
	
	
	

}
