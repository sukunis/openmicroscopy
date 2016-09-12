package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import ome.units.unit.Unit;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules.ElementsCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.microscope.CustomViewProperties;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.microscope.MetaDataUI.GUIPlaceholder;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.DisabledPanel;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.WarningDialog;
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
	
	protected String[] selectedPos;

	public UOSProfileEditorUI(CustomViewProperties cView)
	{
		prop=cView;
		setTitle("Customize View");
		setModal(true);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setPreferredSize(new Dimension(1300,900));
		buildGUI();
		add(main);

		pack();

	}

	/**
	 * 
	 * @return properties from GUI
	 */
	public CustomViewProperties getProperties()
	{
		return prop;
	}

	
	/**
	 * Build Profile Editor GUI
	 */
	private void buildGUI()
	{
		fc=new JFileChooser();
		selectedPos= new String[8];
		for(int i=0; i<8; i++)
			selectedPos[i]="";
		
		main=new JPanel();
		main.setLayout(new BorderLayout(10,10));
		main.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

		// profile file location
		JLabel fLabel = new JLabel("Profile file: ");
		Font font = fLabel.getFont();
		Font boldFont = new Font(font.getFontName(), Font.BOLD, font.getSize());
		fLabel.setFont(boldFont);
		
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
		font = micLabel.getFont();
		boldFont = new Font(font.getFontName(), Font.BOLD, font.getSize());
		micLabel.setFont(boldFont);
		
		micName=new JTextField(prop.getMicName());
		Box micPane =Box.createHorizontalBox();
		micPane.add(micLabel);
		micPane.add(Box.createHorizontalStrut(5));
		micPane.add(micName);

		headerPane=Box.createVerticalBox();
		headerPane.add(filePane);
		headerPane.add(Box.createVerticalStrut(30));
		headerPane.add(micPane);

		// profile settings
		dataPane=custumViewEditor(prop);
		buttonPane=initButtonPane();

		main.add(headerPane,BorderLayout.NORTH);
		main.add(new JScrollPane(dataPane),BorderLayout.CENTER);
		main.add(buttonPane,BorderLayout.SOUTH);


	}

	/**
	 * 
	 * @return panel with save, ok, cancel buttons
	 */
	private JPanel initButtonPane() 
	{
		JPanel bar=new JPanel(new FlowLayout(FlowLayout.RIGHT));
		bar.add(Box.createHorizontalStrut(5));

		okBtn=new JButton("OK");
		okBtn.setToolTipText("Apply configuration for current view, save it and close editor");
		okBtn.addActionListener(this);


		cancelBtn=new JButton("Cancel");
		cancelBtn.addActionListener(this);

		saveFileBtn=new JButton("Save To File...");
		saveFileBtn.setToolTipText("Save configuration to selected file");
		saveFileBtn.addActionListener(this);


		bar.add(saveFileBtn);
		bar.add(Box.createHorizontalStrut(10));
		bar.add(okBtn);
		bar.add(Box.createHorizontalStrut(5));
		bar.add(cancelBtn);
		bar.add(Box.createHorizontalStrut(10));

		return bar;
	}

	/**
	 * Load configuration from given file
	 * @return panel with configurations
	 */
	public JPanel custumViewEditor(CustomViewProperties cProp)
	{
		JPanel pane = new JPanel();
		pane.setBorder(BorderFactory.createEtchedBorder());
		pane.setLayout(new GridLayout(0,4,5,5));

		if(prop.getImageConf()==null)
			prop.loadImageConf(false);
		pane.add(new ConfigurationPanel(prop.getImageConf(),"Image Modul",TagNames.getImageTags(), false, 0));

		if(prop.getObjConf()==null)
			prop.loadObjectiveConf(false);
		pane.add(new ConfigurationPanel(prop.getObjConf(),"Objective Modul",TagNames.getObjectiveTags(),true,1));
		
		if(prop.getDetectorConf()==null)
			prop.loadDetectorConf(false);
		pane.add(new ConfigurationPanel(prop.getDetectorConf(),"Detector Modul",TagNames.getDetectorTags(),true,2));
		
		if(prop.getLightSrcConf()==null)
			prop.loadLightSrcConf(false);
		pane.add(new ConfigurationPanel(prop.getLightSrcConf(),"LightSource Modul",TagNames.getLightSrcTags(),true,3));

		
		if(prop.getChannelConf()==null)
			prop.loadChannelConf(false);
		pane.add(new ConfigurationPanel(prop.getChannelConf(),"Channel Modul",TagNames.getChannelTags(),false,4));
		
//		if(prop.getLightPathConf()==null)
//			prop.loadLightPathConf(false);
		pane.add(new ConfigurationPanel(prop.getLightPathConf(),"LightPath Modul",null,false,5));
		
		if(prop.getSampleConf()==null)
			prop.loadSampleConf(false);
		pane.add(new ConfigurationPanel(prop.getSampleConf(),"Sample Modul",TagNames.getSampleTags(),false,6));
		
		if(prop.getExpConf()==null)
			prop.loadExperimentConf(false);
		pane.add(new ConfigurationPanel(prop.getExpConf(),"Experiment Modul",TagNames.getExperimentTags(),false,7));

		return pane;

	}

	
	/**
	 * write configuration to given CustomViewProperties cView
	 * @param cView
	 */
	private CustomViewProperties applyConfigurations(CustomViewProperties cView)
	{
		cView.setMicName(micName.getText());
		Component[] comp=dataPane.getComponents();
		
		ModuleConfiguration imageConf=((ConfigurationPanel)comp[0]).getConfiguration();
		ModuleConfiguration objConf=((ConfigurationPanel)comp[1]).getConfiguration();
		ModuleConfiguration detectorConf=((ConfigurationPanel)comp[2]).getConfiguration();
		ModuleConfiguration lightSrcConf=((ConfigurationPanel)comp[3]).getConfiguration();
		ModuleConfiguration channelConf=((ConfigurationPanel)comp[4]).getConfiguration();
		ModuleConfiguration lightPathConf=((ConfigurationPanel)comp[5]).getConfiguration();
		ModuleConfiguration sampleConf=((ConfigurationPanel)comp[6]).getConfiguration();
		ModuleConfiguration experimenterConf=((ConfigurationPanel)comp[7]).getConfiguration();
		
		
		
		cView.setImageConf(imageConf);
		cView.setObjConf(objConf);
		cView.setDetectorConf(detectorConf);
		System.out.println("##########APPLY CONF");
		detectorConf.printConfig();
		cView.setLightSrcConf(lightSrcConf);

		cView.setChannelConf(channelConf);
		cView.setLightPathConf(lightPathConf);
		cView.setSampleConf(sampleConf);
		cView.setExperimenterConf(experimenterConf);
		
		return cView;
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



	

	class ConfigurationPanel extends JPanel
	{
		private DisabledPanel tableGlassPane;
		private JTable myTable;
		private JCheckBox visibleCB;
		private JComboBox position;
		private ModuleConfiguration configuration;
		private int index;
		private String name;
		private PreTagData[] availableTags;
//		private JButton loadFieldValBtn;
		
		private String[] positions=ElementsCompUI.getNames(GUIPlaceholder.class);
		private List<String[]> comboBoxData;

	
		
		public ConfigurationPanel(ModuleConfiguration conf,
				String name, PreTagData[] availableTags, boolean editableTags, int i) {
			
			configuration=conf;
			this.index=i;
			this.name=name;
			this.availableTags=availableTags;
			comboBoxData=new ArrayList<String[]>();
			
			
			buildGUI();
			setConfigurationData(conf, editableTags);
			pack();
		}
		
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
//			loadFieldValBtn = new JButton("Load GUI Input");
//			loadFieldValBtn.addActionListener(new ActionListener() {
//				@Override
//				public void actionPerformed(ActionEvent e) {
//					// TODO Auto-generated method stub
//					
//				}
//			});
//			loadFieldValBtn.setEnabled(false);

			titlePane.add(visibleCB);
			titlePane.add(label);
			titlePane.add(Box.createHorizontalStrut(5));
			titlePane.add(position);
//			titlePane.add(Box.createHorizontalStrut(10));
//			titlePane.add(loadFieldValBtn);
			
			myTable=new JTableX(availableTags);
			myTable.setFillsViewportHeight(true);
			((JTableX) myTable).setEditorData(comboBoxData);
			
			add(titlePane);
			add(Box.createVerticalStrut(2));
			add(tableGlassPane);
			tablePane.setViewportView(myTable);
		}



		/**
		 * @param conf
		 * @param editableTags
		 */
		private void setConfigurationData(ModuleConfiguration conf,
				boolean editableTags) 
		{
			if(conf==null)
				return;
						
			position.setSelectedItem(conf.getPosition().name());
			
			visibleCB.setSelected(conf.isVisible());
			
			List<TagConfiguration> tagList=conf.getTagList();
			List<TagConfiguration> settList=conf.getSettingList();

			if(editableTags){
				if(tagList!=null){
					for(TagConfiguration t:tagList){
						addEditableTag(t,"");
					}
				}
				if(settList!=null){
					for(TagConfiguration t:settList){
						addEditableTag(t,"S");
					}
				}
			}else{
				if(tagList!=null){
					for(TagConfiguration t:tagList){
						((TagTableModel) myTable.getModel()).add(t,"");
					}
				}
				if(settList!=null){
					for(TagConfiguration t:settList){
						((TagTableModel) myTable.getModel()).add(t,"S");
					}
				}
			}
			
			if(!conf.isVisible()){
				tableGlassPane.setEnabled(false);
//				myTable.setEnabled(false);
			}
		}

		
		
		



		/**
		 * @param t
		 */
		public void addEditableTag(TagConfiguration t,String clazz) {
			comboBoxData.add(t.getPossibleUnits());
			((TagTableModel) myTable.getModel()).addFull(t,clazz);
		}
		
		 /*
	     * This method picks good column sizes.
	     * If all column heads are wider than the column's cells'
	     * contents, then you can just use column.sizeWidthToFit().
	     */
//	    private void initColumnSizes(JTable table) {
//	        TagTableModel model = (TagTableModel)table.getModel();
//	        TableColumn column = null;
//	        Component comp = null;
//	        int headerWidth = 0;
//	        int cellWidth = 0;
//	        Object[] longValues = model.longValues;
//	        TableCellRenderer headerRenderer =
//	            table.getTableHeader().getDefaultRenderer();
//	 
//	        for (int i = 0; i < 5; i++) {
//	            column = table.getColumnModel().getColumn(i);
//	 
//	            comp = headerRenderer.getTableCellRendererComponent(
//	                                 null, column.getHeaderValue(),
//	                                 false, false, 0, 0);
//	            headerWidth = comp.getPreferredSize().width;
//	 
//	            comp = table.getDefaultRenderer(model.getColumnClass(i)).
//	                             getTableCellRendererComponent(
//	                                 table, longValues[i],
//	                                 false, false, 0, i);
//	            cellWidth = comp.getPreferredSize().width;
//	 
//	            if (DEBUG) {
//	                System.out.println("Initializing width of column "
//	                                   + i + ". "
//	                                   + "headerWidth = " + headerWidth
//	                                   + "; cellWidth = " + cellWidth);
//	            }
//	 
//	            column.setPreferredWidth(Math.max(headerWidth, cellWidth));
//	        }
//	    }

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
						"Position is used by another component");
				ld.setVisible(true);
            }else{
            	selectedPos[index]=selectedItem;
            }			
		}


		public ModuleConfiguration getConfiguration()
		{
			ModuleConfiguration conf=new ModuleConfiguration(visibleCB.isSelected(),
					GUIPlaceholder.valueOf((String) position.getSelectedItem()), "1");
			
			TagTableModel dataModel=(TagTableModel) myTable.getModel();
			int j=0;
			List<TagConfiguration> settList=new ArrayList<TagConfiguration>();
			List<TagConfiguration> tagList=new ArrayList<TagConfiguration>();
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
			}


	@Override
	public void actionPerformed(ActionEvent e) 
	{
		if(e.getSource() == okBtn){
			//TODO: aenderungen?
			prop=applyConfigurations(prop);
			
			System.out.println("######OK BTN");
			prop.getDetectorConf().printConfig();
			
			UOSProfileWriter writer=new UOSProfileWriter();
			writer.save(new File(fileName.getText()), prop);
			
			LOGGER.info("[PROFILE EDITOR]: Reload MetaDataUI");
			
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
			prop=applyConfigurations(prop);

			fc.setSelectedFile(new File(fileName.getText()));
			int returnVal = fc.showSaveDialog(this);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File newFile = fc.getSelectedFile();
				UOSProfileWriter writer=new UOSProfileWriter();

				writer.save(newFile, prop);
				file=newFile;
				reloadGUIData(newFile);
				fileName.setText(file.getAbsolutePath());
			}
			
		
		}else if(e.getSource()==cancelBtn){
			LOGGER.info("[PROFILE EDITOR]: Cancel");
			setVisible(false);
			dispose();
		}
	}

	/**
	 * Update editor data
	 * @param newFile input file for editor data
	 */
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
		dataPane=custumViewEditor(prop);

		main.removeAll();
		main.add(headerPane,BorderLayout.NORTH);
		main.add(dataPane,BorderLayout.CENTER);
		main.add(buttonPane,BorderLayout.SOUTH);

		revalidate();
		repaint();
	}


	
	public class JTableX extends JTable
	{
		private List<String[]> comboBoxData=null;
		private JPopupMenu popupMenu;
		private PreTagData[] availableTags;
		
		public JTableX(PreTagData[] availableTags){
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
		
		private void insertTagAtTable(int index) 
		{
			System.out.println("Select index: "+index);
			TagConfiguration t=new TagConfiguration(availableTags[index].name, "", 
					availableTags[index].defaultUnit, false, false, availableTags[index].unitsList);
			TagTableModel model=(TagTableModel) getModel();
			int row=getSelectedRow();
			model.insertRow(row, t, availableTags[index].settings);
		}
		
		/**
		 * 
		 * @param data list of unit combobox default data for all tags in the table. If no units specified for a tag, element=null
		 */
		public void setEditorData(List<String[]> data)
		{
			comboBoxData=data;
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
            }
            else
                return super.getCellEditor(row, column);
		}
		
		
		@Override
		/**
		 * Highlight non editable field value and field unit cells
		 */
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

		public static final String NOEDITABLE="--";
		
		public static final int COL_VISIBLE=0;
		public static final int COL_NAME=1;
		public static final int COL_VALUE=2;
		public static final int COL_UNIT=3;
		public static final int COL_PROP=4;
		public static final int COL_SETT=5;
		

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
			data.add(list);

			fireTableRowsInserted(data.size() - 1, data.size() - 1);

		}
		/**
		 * Add only tag, predefinitions for this tag are not possible.
		 * @param t
		 * @param isSettingsTag
		 */
		public void add(TagConfiguration t,String isSettingsTag) {
			List<Object> list=new ArrayList<Object>(columns.length);
			

			list.add(t.isVisible());
			list.add(t.getName());
			list.add(NOEDITABLE);
			list.add(NOEDITABLE);
			list.add(t.getProperty());
			list.add(isSettingsTag);
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
			
			if((columnIndex!=COL_VISIBLE && columnIndex!= COL_PROP) &&
					(getValueAt(rowIndex, columnIndex)!=null && getValueAt(rowIndex, columnIndex).equals(NOEDITABLE))
					|| columnIndex==COL_NAME || columnIndex==COL_SETT)
				editable=false;
			return editable;
		}

		public void insertRow(int rowIndex, TagConfiguration t,String settings)
		{
			List<Object> list=new ArrayList<Object>(columns.length);
			boolean hideUnit=false;
			if(UOSHardwareReader.getUnitList(t.getName())==null){
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
		
		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			System.out.println("Set value at "+rowIndex+", "+columnIndex);
			if(aValue instanceof String){
				List temp=data.get(rowIndex);
				temp.set(columnIndex, aValue.toString());
				data.set(rowIndex, temp);
				
			}else{
			data.get(rowIndex).set(columnIndex, aValue);
		}
			fireTableRowsUpdated(rowIndex, rowIndex);
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			return data.get(rowIndex).get(columnIndex);
		}
		
		/**
		 * 
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
			if(!((String) getValueAt(rowIndex,COL_UNIT)).equals(NOEDITABLE)){
				try {
					unit=UOSHardwareReader.parseUnit((String) getValueAt(rowIndex,COL_UNIT),name);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
	}
				pU= UOSHardwareReader.getUnits(name);
			}
			tag=new TagConfiguration(name, value, unit, (Boolean)getValueAt(rowIndex,COL_PROP),(Boolean) getValueAt(rowIndex,0), pU);
			return tag;
		}
	}

//	public class TagTableModel extends DefaultTableModel
//	{
//		public void insertRow(int row, TagConfiguration tag,String sett) 
//		{
//			Object[] rowData={tag.isVisible(),tag.getName(),};
//			super.insertRow(row, rowData);
//		}
//	}
}
