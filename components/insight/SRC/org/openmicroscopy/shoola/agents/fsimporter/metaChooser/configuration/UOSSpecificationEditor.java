package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;

import ome.xml.model.Detector;
import ome.xml.model.Filter;
import ome.xml.model.LightSource;
import ome.xml.model.Objective;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules.DetectorCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules.FilterCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules.LightSourceCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules.ObjectiveCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.microscope.CustomViewProperties;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagData;
import org.slf4j.LoggerFactory;

import edu.emory.mathcs.backport.java.util.Arrays;

public class UOSSpecificationEditor extends JDialog implements ActionListener
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
	
	public UOSSpecificationEditor(UOSHardwareReader reader)
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
		main.setLayout(new BorderLayout(5,5));
		main.setBorder(new EmptyBorder(5, 5, 5, 5));

		// profile file location
		JLabel fLabel = new JLabel("Hardware file: ");
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
		micName=new JTextField(reader.getMicName());
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
		okBtn.setEnabled(false);
		
		cancelBtn=new JButton("Cancel");
		cancelBtn.addActionListener(this);
		
		saveFileBtn=new JButton("Save To File...");
		saveFileBtn.setToolTipText("Save configuration to file");
		saveFileBtn.addActionListener(this);
		saveFileBtn.setEnabled(false);
		

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
		pane.add(new SpecificationPanel(1,reader.getObjectives(),"Objective Modul"));
		pane.add(new SpecificationPanel(0,reader.getDetectors(),"Detector Modul"));
		pane.add(new SpecificationPanel(2,reader.getLightSources(),"LightSource Modul"));

//		pane.add(new SpecificationPanel(prop.getChannelConf(),"Channel Modul"));
		pane.add(new SpecificationPanel(3,reader.getLightPathFilters(),"LightPath Modul"));
//		pane.add(new SpecificationPanel(prop.getSampleConf(),"Sample Modul"));
//		pane.add(new SpecificationPanel(prop.getExpConf(),"Experiment Modul"));
		
		
		
		return pane;

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
			//TODO: aenderungen?
//			applyConfigurations(prop);
			
			fc.setSelectedFile(new File(fileName.getText()));
			int returnVal = fc.showSaveDialog(this);
			
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
//				UOSSpecificationWriter writer=new UOSSpecificationWriter();
//				writer.save(file, reader);
				
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
		
		public SpecificationPanel(int type,Object list,String name)
		{
			setLayout(new BorderLayout());
			JLabel label=new JLabel(name);
			Font font = label.getFont();
			Font boldFont = new Font(font.getFontName(), Font.BOLD, font.getSize());
			label.setFont(boldFont);
			
			tablePane=new JTabbedPane();
			if(list!=null){
				switch (type) {
				case 0:
					buildDetectorPane((List<Detector>) list);
					break;
				case 1:
					buildObjectivePane((List<Objective>) list);
					break;
				case 2:
					buildLightSrcPane((List<LightSource>) list);
					break;
				case 3:
					buildFilterPane((List<Filter>) list);
					break;
				default:
					break;
				}
				
			}
			
			add(label,BorderLayout.NORTH);
			add(tablePane,BorderLayout.CENTER);
		}

		private void buildObjectivePane(List<Objective> list) {
			int i=0;
			for(Objective f:list){
				SpecificationTable table=new SpecificationTable();
				table.setFillsViewportHeight(true);
				
				ObjectiveCompUI ui=new ObjectiveCompUI(null);
				ui.addData(f,true);
				ui.buildComponents();
				
				List<TagData> tagList=ui.getActiveTags();
				for(TagData tagData:tagList){
					table.appendElem(tagData);
				}
				
				JScrollPane pane = new JScrollPane(table);
				tablePane.add("Objective"+i,	pane);
				i++;
			}			
		}
		
		private void buildFilterPane(List<Filter> list) {
			int i=0;
			for(Filter f:list){
				SpecificationTable table=new SpecificationTable();
				table.setFillsViewportHeight(true);
				
				FilterCompUI ui=new FilterCompUI(null);
//				ui.addData(f,true);
				ui.buildComponents();
				
				List<TagData> tagList=ui.getActiveTags();
				for(TagData tagData:tagList){
					table.appendElem(tagData);
				}
				
				JScrollPane pane = new JScrollPane(table);
				tablePane.add("Filter"+i,	pane);
				i++;
			}			
		}
		
		private void buildLightSrcPane(List<LightSource> list) {
			int i=0;
			for(LightSource f:list){
				SpecificationTable table=new SpecificationTable();
				table.setFillsViewportHeight(true);
				
				LightSourceCompUI ui=new LightSourceCompUI(null);
				ui.addData(f,true);
				ui.buildComponents();
				
				List<TagData> tagList=ui.getActiveTags();
				for(TagData tagData:tagList){
					table.appendElem(tagData);
				}
				
				JScrollPane pane = new JScrollPane(table);
				tablePane.add("LightSource"+i,	pane);
				i++;
			}			
		}

		private void buildDetectorPane(List<Detector> list)
		{
			int i=0;
			for(Detector f:list){
				SpecificationTable table=new SpecificationTable();
				table.setFillsViewportHeight(true);
				
				DetectorCompUI detectorUI=new DetectorCompUI(null);
				detectorUI.addData(f,true);
				detectorUI.buildComponents();
				
				List<TagData> tagList=detectorUI.getActiveTags();
				for(TagData tagData:tagList){
					table.appendElem(tagData);
				}
				
				JScrollPane pane = new JScrollPane(table);
				tablePane.add("Detector"+i,	pane);
				i++;
			}
		}
		
		
		
	}
	
	
	class SpecificationTable extends JTable
	{
		public SpecificationTable()
		{
			setModel(new SpecificationTableModel());
		}
		
		public boolean isCellEditable(int row,int column){  
	        return false;  
	      }
		
		public void appendElem(TagData o)
		{
			((SpecificationTableModel) getModel()).addRow(o);
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

//		public void removeRow(int i) {
//			// TODO Auto-generated method stub
//			
//		}

		public void addRow(TagData t) {
//			List list=Arrays.asList(t.isVisible(),t.getTagLabel().getText(),t.getTagValue(),t.getTagUnit().getSymbol());
			List<Object> list=new ArrayList<Object>(columnCount);
			list.add(t.getTagLabel().getText());
			list.add(t.getTagValue());
			list.add(t.getTagUnit()!=null ? t.getTagUnit().getSymbol():"");
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
		
	}

	
}
