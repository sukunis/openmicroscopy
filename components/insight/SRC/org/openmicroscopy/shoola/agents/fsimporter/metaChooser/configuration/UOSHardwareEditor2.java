package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import ome.xml.model.Detector;
import ome.xml.model.Filter;
import ome.xml.model.LightSource;
import ome.xml.model.Objective;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.microscope.CustomViewProperties;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.SwingUtils;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagConfiguration;
import org.slf4j.LoggerFactory;

public class UOSHardwareEditor2 extends JDialog implements ActionListener
{
	
	private static final org.slf4j.Logger LOGGER =
			LoggerFactory.getLogger(UOSProfileReader.class);


	private File file;
	
	private JButton openFileBtn;
	private JButton saveFileBtn;
	private JButton okBtn;
	private JButton cancelBtn;
	
	private JPanel main;
	private JPanel dataPane;
	private JPanel buttonPane;
	private Box headerPane;
	
	private JTextField fileName;
	private JTextField micName;
	
	private JFileChooser fc;
	
	private UOSHardwareReader reader;
	
	private static final int OBJECTIVE=0;
	private static final int DETECTOR=1;
	private static final int LIGHTPATH=2;
	private static final int LIGHTSRC=3;
	
	public UOSHardwareEditor2(UOSHardwareReader reader)
	{
		this.reader=reader;
		setModal(true);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		initGUI();
		add(new JScrollPane(main));
		pack();
	}
	
	private void initGUI()
	{
		setLayout(new BorderLayout());
		fc=new JFileChooser();
		
		main=new JPanel();
		main.setLayout(new BorderLayout(10,10));
		main.setBorder(new EmptyBorder(8, 8, 8, 8));

		// profile file location
		JLabel fLabel = new JLabel("Hardware file: ");
		Font font = fLabel.getFont();
		Font boldFont = new Font(font.getFontName(), Font.BOLD, font.getSize());
		fLabel.setFont(boldFont);
		
		file=reader.getFile();
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
		
		micName=new JTextField(reader.getMicName());
		Box micPane =Box.createHorizontalBox();
		micPane.add(micLabel);
		micPane.add(Box.createHorizontalStrut(5));
		micPane.add(micName);
		
		headerPane=Box.createVerticalBox();
		headerPane.add(filePane);
		headerPane.add(Box.createVerticalStrut(30));
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
		okBtn.setEnabled(false);
		
		cancelBtn=new JButton("Cancel");
		cancelBtn.addActionListener(this);
		
		saveFileBtn=new JButton("Save To File...");
		saveFileBtn.setToolTipText("Save configuration to file");
		saveFileBtn.addActionListener(this);
		saveFileBtn.setEnabled(true);
		

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
		pane.setLayout(new GridLayout(0,2,5,5));

//		pane.add(new SpecificationPanel(reader,"Image Modul"));
		pane.add(new SpecificationPanel(OBJECTIVE,reader.getObjectives(),"Objective Modul"));
		pane.add(new SpecificationPanel(DETECTOR,reader.getDetectors(),"Detector Modul"));
		pane.add(new SpecificationPanel(LIGHTSRC,reader.getLightSources(),"LightSource Modul"));

//		pane.add(new SpecificationPanel(prop.getChannelConf(),"Channel Modul"));
		pane.add(new SpecificationPanel(LIGHTPATH,reader.getLightPathFilters(),"LightPath Modul"));
//		pane.add(new SpecificationPanel(prop.getSampleConf(),"Sample Modul"));
//		pane.add(new SpecificationPanel(prop.getExpConf(),"Experiment Modul"));
		return pane;
	}

	private void applyTags(UOSHardwareWriter writer){
		writer.setMicName(micName.getText());
		Component[] comp=dataPane.getComponents();
		System.out.println("Number comp: "+comp.length);
		
		List<List<TagConfiguration>> listObj=(List<List<TagConfiguration>>) ((SpecificationPanel)comp[OBJECTIVE]).getConfigurations();
		List<List<TagConfiguration>> listDet=(List<List<TagConfiguration>>) ((SpecificationPanel)comp[DETECTOR]).getConfigurations();
		List<List<TagConfiguration>> listLS= (List<List<TagConfiguration>>) ((SpecificationPanel)comp[LIGHTSRC]).getConfigurations();
		List<List<TagConfiguration>> listLP=(List<List<TagConfiguration>>) ((SpecificationPanel)comp[LIGHTPATH]).getConfigurations();
		
		System.out.println("Objectives: "+listObj.size());
		System.out.println("Detectors: "+listDet.size());
		System.out.println("LightSrc: "+listLS.size());
//		System.out.println("LightPath: "+listLP.size());
		
		writer.setObjectives(listObj);
		writer.setDetectors(listDet); 
		writer.setLightSource(listLS);
		writer.setLightPath(listLP);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		if(e.getSource() == okBtn){
			//TODO: aenderungen?
//			applyConfigurations(prop);
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
		
			fc.setSelectedFile(new File(fileName.getText()));
			int returnVal = fc.showSaveDialog(this);
			
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File newFile = fc.getSelectedFile();
					UOSHardwareWriter writer = new UOSHardwareWriter();
				applyTags(writer);
				writer.save(newFile);
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
		UOSHardwareReader specReader=new UOSHardwareReader(newFile);
		
		
		if(!specReader.readSpecification()){
			return;
		}
		reader=specReader;
		file=newFile;
		micName.setText(reader.getMicName());
		dataPane=custumViewEditor();
		
		main.removeAll();
		main.add(headerPane,BorderLayout.NORTH);
		main.add(dataPane,BorderLayout.CENTER);
		main.add(buttonPane,BorderLayout.SOUTH);
		
		revalidate();
		repaint();
	}

	
	class SpecificationPanel extends JPanel
	{
		private Object spec;
		private JTabbedPane tablePane;
		private int type;
		private CustomViewProperties prop;
		
		private List<List<TagConfiguration>> componentList;
		
		public SpecificationPanel(int type,Object list,String name)
		{
			prop=new CustomViewProperties();
			prop.init();
			
			this.type=type;
			setLayout(new BorderLayout());
			setPreferredSize(new Dimension(300,300));
			
			Box titleBox=Box.createHorizontalBox();
			
			JLabel label=new JLabel(name);
			Font font = label.getFont();
			Font boldFont = new Font(font.getFontName(), Font.BOLD, font.getSize());
			label.setFont(boldFont);
			
			JButton newBtn=new JButton("new");
			newBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					
				}
			});
			newBtn.setEnabled(false);
			
			componentList = new ArrayList<List<TagConfiguration>>();
			
			tablePane=new JTabbedPane(JTabbedPane.TOP,JTabbedPane.SCROLL_TAB_LAYOUT);
			tablePane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			if(list!=null){
				switch (type) {
				case DETECTOR:
					buildDetectorPane((List<Detector>) list);
					break;
				case OBJECTIVE:
					buildObjectivePane((List<Objective>) list);
					break;
				case LIGHTSRC:
					buildLightSrcPane((List<LightSource>) list);
					break;
				case LIGHTPATH:
					buildFilterPane((List<Filter>) list);
					break;
				default:
					break;
				}
			}
			
			titleBox.add(label);
			titleBox.add(Box.createHorizontalGlue());
			titleBox.add(newBtn);
			
			add(titleBox,BorderLayout.NORTH);
			add(tablePane,BorderLayout.CENTER);
		}
		
		public void removeTab(int index) {
			tablePane.remove(index);
	 
			int numTabs=tablePane.getTabCount();
			
	        if (index == numTabs - 1 && index > 0) {
	        	tablePane.setSelectedIndex(numTabs - 2);
	        } else {
	        	tablePane.setSelectedIndex(index);
	        }
	 
	        if (numTabs == 1) {
	            addNewTab();
	        }
	    }
		
		 private void addNewTab() 
		 {
		        int index = tablePane.getTabCount() - 1;
		        if (tablePane.getSelectedIndex() == index) { /* if click new tab */
		            /*TODO  add new tab */
		        	tablePane.add( getTabName()+String.valueOf(index), new JScrollPane());
		            /* set tab is custom tab */
//		        	tabbedPane.removeChangeListener(changeListener);
		        	tablePane.setSelectedIndex(index);
//		        	tablePane.addChangeListener(changeListener);
		        }
		    }

		private String getTabName() {
			switch (type) {
			case DETECTOR:
				return "Detector";
			case OBJECTIVE:
				return "Objective";
			case LIGHTSRC:
				return "LightSource";
			case LIGHTPATH:
				return "LightPath";
			default:
				return "";
			}
		}

		public List<List<TagConfiguration>> getConfigurations() {
			
			if(componentList==null || componentList.size()==0)
				return null;
			
			List<SpecificationTable> components =
					SwingUtils.getDescendantsOfType(SpecificationTable.class, tablePane, true);
			

			int indexObj=0;
			for(SpecificationTable table : components){
				List<TagConfiguration> tagList = componentList.get(indexObj);
				SpecificationTableModel dataModel = (SpecificationTableModel) table.getModel();
				for(int i=0; i<dataModel.getRowCount();i++){
					TagConfiguration tag=tagList.get(i);
					//value
					tag.setValue((String)dataModel.getValueAt(i,1));
					//unit
					if(dataModel.saveable(i,2)){
						try {
							tag.setUnit(TagNames.parseUnit(
									(String)dataModel.getValueAt(i, 2),(String)dataModel.getValueAt(i, 0)));
						} catch (Exception e) {
							tag.setUnit(null);
						}
					}
				}
				indexObj++;
			}
			return componentList;
		}

		private List<Detector> getDetectorPaneData() {
			// TODO Auto-generated method stub
			return null;
		}

		private void buildObjectivePane(List<Objective> list) {
			int i=0;
			List<String[]> comboBoxData =new ArrayList<String[]>();
			for(Objective f:list){
				SpecificationTable table=new SpecificationTable();
				table.setFillsViewportHeight(true);
				
				List<TagConfiguration> tagList=prop.getObjConf().getTagList();
				componentList.add(tagList);
				for(TagConfiguration  tagData:tagList){
					table.appendElem(tagData);
					comboBoxData.add(TagNames.getUnits(tagData.getName()));
				}
				table.setEditorData(comboBoxData);
				
				JScrollPane pane = new JScrollPane(table);
				tablePane.add("Objective"+i,	pane);
				i++;
			}			
		}
		
		private void buildFilterPane(List<Filter> list) {
			int i=0;
			List<String[]> comboBoxData =new ArrayList<String[]>();
			for(Filter f:list){
				SpecificationTable table=new SpecificationTable();
				table.setFillsViewportHeight(true);
				
				List<TagConfiguration> tagList=prop.getLightPathConf().getTagList();
				componentList.add(tagList);
				for(TagConfiguration tagData:tagList){
					table.appendElem(tagData);
					comboBoxData.add(TagNames.getUnits(tagData.getName()));
				}
				table.setEditorData(comboBoxData);
				
				JScrollPane pane = new JScrollPane(table);
				tablePane.add("Filter"+i,	pane);
				i++;
			}			
		}
		
		private void buildLightSrcPane(List<LightSource> list) {
			int i=0;
			List<String[]> comboBoxData =new ArrayList<String[]>();
			for(LightSource f:list){
				SpecificationTable table=new SpecificationTable();
				table.setFillsViewportHeight(true);
				
				List<TagConfiguration> tagList=prop.getLightSrcConf().getTagList();
				componentList.add(tagList);
				for(TagConfiguration tagData:tagList){
					table.appendElem(tagData);
					comboBoxData.add(TagNames.getUnits(tagData.getName()));
				}
				table.setEditorData(comboBoxData);
				
				JScrollPane pane = new JScrollPane(table);
				tablePane.add("LightSource"+i,	pane);
				i++;
			}			
		}

		private void buildDetectorPane(List<Detector> list)
		{
			int i=0;
			List<String[]> comboBoxData =new ArrayList<String[]>();
			for(Detector f:list){
				SpecificationTable table=new SpecificationTable();
				table.setFillsViewportHeight(true);
				
				List<TagConfiguration> tagList=prop.getDetectorConf().getTagList();
				componentList.add(tagList);
				for(TagConfiguration tagData:tagList){
					table.appendElem(tagData);
					comboBoxData.add(TagNames.getUnits(tagData.getName()));
				}
				table.setEditorData(comboBoxData);
				
				JScrollPane pane = new JScrollPane(table);
				tablePane.add("Detector"+i,	pane);
				i++;
			}
		}
		
		
		
	}
	
	
	class SpecificationTable extends JTable
	{
		private List<String[]> comboBoxData=null;
		
		public SpecificationTable()
		{
			setModel(new SpecificationTableModel());
			setShowGrid(false);
			//fit size of col unit
			getColumnModel().getColumn(2).setMinWidth(15);
			getColumnModel().getColumn(2).setPreferredWidth(15);
		}
		
		/**
		 * 
		 * @param data list of unit combobox default data for all tags in the table. If no units specified for a tag, element=null
		 */
		public void setEditorData(List<String[]> data) {
			comboBoxData=data;
		}

		public boolean isCellEditable(int row,int column){  
			boolean editable=true;
			if(column==0){
				editable= false;
			}else{
				if(column==2 && comboBoxData.get(row)==null )
					editable= false;
			}
	        return editable;  
	      }
		
		public void appendElem(TagConfiguration o)
		{
			((SpecificationTableModel) getModel()).addRow(o);
		}
		
		/**
		 * Set unit field as combobox if units available for this tag
		 */
		public TableCellEditor getCellEditor(int row, int column)
		{
			int modelColumn = convertColumnIndexToModel( column );
            if (modelColumn == 2 && comboBoxData.get(row)!=null)
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
//	        if(!getModel().isCellEditable(row, col) && col==2 ){
	        if(!getModel().isCellEditable(row, col) && col==2 ){
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
		
//		public void clearData()
//		{
//			SpecificationTableModel model = (SpecificationTableModel)getModel();
//			for(int i=0; i<model.getRowCount(); i++){
//				model.removeRow(i);
//			}
//		}
		
	}
	
	class SpecificationTableModel extends AbstractTableModel
	{
		public static final String NOEDITABLE="--";
		int columnCount=3;
//		String[] columns = {"","Field Name","Field Value","Unit"};
//		Class[] columnTypes = new Class[] {
//				JCheckBox.class,String.class, String.class, String.class
//		};

		//TODO: right init?
		private List<List> data=new ArrayList<>();
		
		@Override
        public String getColumnName(int column) {
            String name = null;
            switch (column) {
                case 0:
                    name = "Field Name";
                    break;
                case 1:
                	name= "Field Val";
                	break;
                case 2:
                	name="Unit";
                	break;
            }
            return name;
        }
		
		@Override
		public int getColumnCount() {
			return columnCount;
		}
		public boolean saveable(int rowIdx, int colIdx) 
		{
			boolean saveVal=false;
			if(getValueAt(rowIdx, colIdx)!=null && !getValueAt(rowIdx,colIdx).equals(NOEDITABLE))
				saveVal=true;
			return saveVal;
		}
		
//		public void removeRow(int i) {
//			// TODO Auto-generated method stub
//			
//		}

		public void addRow(TagConfiguration t) {
//			List list=Arrays.asList(t.isVisible(),t.getTagLabel().getText(),t.getTagValue(),t.getTagUnit().getSymbol());
			List<Object> list=new ArrayList<Object>(columnCount);
			list.add(t.getName());
			list.add(t.getValue());
			list.add(t.getUnitSymbol());
			data.add(list);
			fireTableRowsInserted(data.size()-1, data.size()-1);
		}

		@Override
		public int getRowCount() {
			return data.size();
		}

		@Override
		public Object getValueAt(int rowIdx, int colIdx) {
			return data.get(rowIdx).get(colIdx);
		}
		
		//
        // This method is used by the JTable to define the default
        // renderer or editor for each cell. For example if you have
        // a boolean data it will be rendered as a check box. A
        // number value is right aligned.
        //
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return data.get(0).get(columnIndex).getClass();
        }
        
        
        //##############################################################################
        
        
        /**
         * --------------------- @author nguyenvanquan7826 ---------------------
         * ------------------ website: nguyenvanquan7826.com -------------------
         * ---------- date: Jul 24, 2014 - filename: DemoCustomJTabPane.java ----------
         */
        public class DemoCustomJTabbedPane extends JPanel {
            JTabbedPane tabbedPane;
            int numTabs;
         
            public DemoCustomJTabbedPane() {
                createGUI();
                setDisplay();
            }
         
            /** set diplay for JFrame */
            private void setDisplay() {
                setSize(450, 300);
                setLocationRelativeTo(null);
                setVisible(true);
            }
         
            /** set title and add JTabbedPane into JFrame */
            private void createGUI() {
                setTitle("Demo custum JTabbedPane");
                createJTabbedPane();
                add(tabbedPane);
            }
         
            /** create JTabbedPane contain 2 tab */
            private void createJTabbedPane() {
                /* create JTabbedPane */
                tabbedPane = new JTabbedPane(JTabbedPane.TOP,
                        JTabbedPane.SCROLL_TAB_LAYOUT);
         
                /* add first tab */
                tabbedPane.add(createJPanel(), "Tab " + String.valueOf(numTabs),
                        numTabs++);
                tabbedPane.setTabComponentAt(0, new DemoCustomTab(this));
         
                /* add tab to add new tab when click */
                tabbedPane.add(new JPanel(), "+", numTabs++);
         
                tabbedPane.addChangeListener(changeListener);
            }
         
            /** create JPanel contain a JLabel */
            private JPanel createJPanel() {
                JPanel panel = new JPanel(new GridLayout(1, 1));
                panel.add(new JScrollPane(createTextArea(10, 40)));
                return panel;
            }
         
            private JTextArea createTextArea(int row, int col) {
                JTextArea ta = new JTextArea(row, col);
                ta.setWrapStyleWord(true);
                ta.setLineWrap(true);
                ta.setForeground(Color.BLUE);
                return ta;
            }
         
            ChangeListener changeListener = new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    addNewTab();
                }
            };
         
            private void addNewTab() {
                int index = numTabs - 1;
                if (tabbedPane.getSelectedIndex() == index) { /* if click new tab */
                    /* add new tab */
                    tabbedPane.add(createJPanel(), "Tab " + String.valueOf(index),
                            index);
                    /* set tab is custom tab */
                    tabbedPane.setTabComponentAt(index, new DemoCustomTab(this));
                    tabbedPane.removeChangeListener(changeListener);
                    tabbedPane.setSelectedIndex(index);
                    tabbedPane.addChangeListener(changeListener);
                    numTabs++;
                }
            }
         
            public void removeTab(int index) {
                tabbedPane.remove(index);
                numTabs--;
         
                if (index == numTabs - 1 && index > 0) {
                    tabbedPane.setSelectedIndex(numTabs - 2);
                } else {
                    tabbedPane.setSelectedIndex(index);
                }
         
                if (numTabs == 1) {
                    addNewTab();
                }
            }
         
         
        }
        
        /**
         * --------------------- @author nguyenvanquan7826 ---------------------
         * ------------------ website: nguyenvanquan7826.com -------------------
         * ---------- date: Jul 24, 2014 - filename: DemoButtonTab.java ----------
         */
        public class DemoCustomTab extends JPanel {
         
            DemoCustomJTabbedPane customJTabbedPane;
         
            /** JPanel contain a JLabel and a JButton to close */
            public DemoCustomTab(DemoCustomJTabbedPane customJTabbedPane) {
                this.customJTabbedPane = customJTabbedPane;
                setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
                setBorder(new EmptyBorder(5, 2, 2, 2));
                setOpaque(false);
                addLabel();
                add(new CustomButton("x"));
            }
         
            private void addLabel() {
                JLabel label = new JLabel() {
                    /** set text for JLabel, it will title of tab */
                    public String getText() {
                        int index = customJTabbedPane.tabbedPane
                                .indexOfTabComponent(DemoCustomTab.this);
                        if (index != -1) {
                            return customJTabbedPane.tabbedPane.getTitleAt(index);
                        }
                        return null;
                    }
                };
                /** add more space between the label and the button */
                label.setBorder(new EmptyBorder(0, 0, 0, 10));
                add(label);
            }
         
            class CustomButton extends JButton implements MouseListener {
                public CustomButton(String text) {
                    int size = 15;
                    setText(text);
                    /** set size for button close */
                    setPreferredSize(new Dimension(size, size));
         
                    setToolTipText("close the Tab");
         
                    /** set transparent */
                    setContentAreaFilled(false);
         
                    /** set border for button */
                    setBorder(new EtchedBorder());
                    /** don't show border */
                    setBorderPainted(false);
         
                    setFocusable(false);
         
                    /** add event with mouse */
                    addMouseListener(this);
         
                }
         
                /** when click button, tab will close */
                @Override
                public void mouseClicked(MouseEvent e) {
                    int index = customJTabbedPane.tabbedPane
                            .indexOfTabComponent(DemoCustomTab.this);
                    if (index != -1) {
                        customJTabbedPane.removeTab(index);
                    }
                }
         
                @Override
                public void mousePressed(MouseEvent e) {
                }
         
                @Override
                public void mouseReleased(MouseEvent e) {
                }
         
                /** show border button when mouse hover */
                @Override
                public void mouseEntered(MouseEvent e) {
                    setBorderPainted(true);
                    setForeground(Color.RED);
                }
         
                /** hide border when mouse not hover */
                @Override
                public void mouseExited(MouseEvent e) {
                    setBorderPainted(false);
                    setForeground(Color.BLACK);
                }
            }
        }
		
	}

	
}
