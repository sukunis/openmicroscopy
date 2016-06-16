package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import ome.xml.model.Dichroic;
import ome.xml.model.Filter;
import ome.xml.model.Objective;
import ome.xml.model.enums.Correction;
import ome.xml.model.enums.FilterType;
import ome.xml.model.enums.Immersion;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.UOSMetadataLogger;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.LightPathTable.LightPathTableModel;

public class ObjectiveEditor extends JDialog implements ActionListener 
{
	/** Logger for this class. */
    private static Logger LOGGER = Logger.getLogger(UOSMetadataLogger.class.getName());
    
    private List<Objective> availableObjectives;
    private ObjectiveTable objectivTable;
    private Objective selectObjective;
    
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public ObjectiveEditor(JFrame parent, String title, List<Objective> availableObjectives)
	{
		super(parent,title);
		this.availableObjectives=availableObjectives;
		selectObjective=null;
		initGUI();
	}
	
	
	
	private void initGUI() 
	{

		setBounds(100, 100, 500, 600);
		getContentPane().setLayout(new BorderLayout());
		setModal(true);

		getContentPane().add(availableObjectivePane(), BorderLayout.CENTER);

		//Bottom
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		
//		JButton loadBtn= new JButton("Load Workstation List");
//		loadBtn.setActionCommand("Load");
//		loadBtn.setEnabled(false);
//		buttonPane.add(loadBtn);
//		buttonPane.add(Box.createHorizontalGlue());

		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
					try {
						int row=objectivTable.getSelectedRow();
						if(row!=-1)
							selectObjective=availableObjectives.get(row);
					} catch (Exception e1) {
						LOGGER.severe("can't read objective from table");
						e1.printStackTrace();
					}
				
				setVisible(false);
				dispose();
			}

			
		});
		okButton.setActionCommand("OK");
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);

		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				selectObjective=null;
				setVisible(false);
				dispose();
			}
		});
		cancelButton.setActionCommand("Cancel");
		buttonPane.add(cancelButton);


		getContentPane().add(buttonPane, BorderLayout.SOUTH);

		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		pack();
		setVisible(true);
	}

	



	private Component availableObjectivePane() 
	{
		JPanel pane=new JPanel();
		pane.setLayout(new BorderLayout(5, 5));
		pane.setBorder(new EmptyBorder(5,5,5,5));

		JLabel label = new JLabel("Available Elements:");
		label.setFont(new Font("Tahoma", Font.BOLD, 11));
		
		JScrollPane scrollPane = new JScrollPane();
		

		objectivTable = new ObjectiveTable();
		scrollPane.setViewportView(objectivTable);
//		objectivTable.setPreferredScrollableViewportSize(objectivTable.getPreferredSize());
//		objectivTable.setFillsViewportHeight(true);
		objectivTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		if(availableObjectives!=null){
			for(Objective o: availableObjectives){
				objectivTable.appendElem(o);
			}
		}
		pane.add(scrollPane, BorderLayout.CENTER);
		return pane;
	}



	class ObjectiveTable extends JTable
	{
		public ObjectiveTable()
		{
			setModel(new ObjectiveTableModel());
		}
		public boolean isCellEditable(int row,int column){  
	        return false;  
	      }
		
		public void appendElem(Objective o)
		{
			((ObjectiveTableModel) getModel()).addRow(o);
		}
		
//		/**
//	     * Return a row from the table as a array of strings
//	     * @param rowIndex The index of the row you would like
//	     * @return Returns the row from the table as an array of strings or null if
//	     * the index is invalid
//	     */
//		public String[] getRowData(int rowIndex)
//	    {
//	        //test the index
//	        if ( (rowIndex  >  getRowCount()) || rowIndex  <  0)
//	            return null;
//
//	        ArrayList<String> data = new ArrayList<String>();
//	        for (int c = 0; c  <  getColumnCount(); c++)
//	        {
//	            data.add((String) getValueAt(rowIndex, c));
//	        }
//	        String[] retVal = new String[data.size()];
//	        for (int i = 0; i  <  retVal.length; i++)
//	        {
//	            retVal[i] = data.get(i);
//	        }
//	        return retVal;
//	    }
//		
//		public void clearData()
//		{
//			ObjectiveTableModel model = (ObjectiveTableModel)getModel();
//			for(int i=0; i<model.getRowCount(); i++){
//				model.removeRow(i);
//			}
//		}
	}
	
	class ObjectiveTableModel extends DefaultTableModel
	{
		Class[] columnTypes = new Class[] {
				String.class,String.class, String.class, String.class, String.class, 
				String.class,String.class,String.class,String.class
		};

		private ArrayList<TableColumn> tableColumns;

		public ObjectiveTableModel()
		{
			super(new Object[][] {},
					new String[] {"ID","Model", "Manufactur", "Nominal Magn.",
					"Calibration Magn.", "Lens NA","Immersion","Correction",
					"Working Distance"});

		}

		public Class getColumnClass(int columnIndex) {
			return columnTypes[columnIndex];
		}

		public void addRow(Objective o)
		{
			super.addRow(parseFromObjectiveLong(o));
		}

		public void insertRow(int index, Objective o)
		{
			super.insertRow(index, parseFromObjectiveLong(o));
		}
		
		private Object[] parseFromObjectiveLong(Objective e)
		{
			Object[] o=new Object[9];
			if(e!=null){
				try{o[0]=e.getID()!=null ? e.getID() : "";
				}catch(Exception err){
					LOGGER.severe("[EDITOR] Can't parse objective::id");
				}
				try{o[1]=e.getModel()!=null ? e.getModel() : "";
				}catch(Exception err){
					LOGGER.severe("[EDITOR] Can't parse objective::model");
				}
				try{o[2]=e.getManufacturer()!=null ? e.getManufacturer() : "";
				}catch(Exception err){
					LOGGER.severe("[EDITOR] Can't parse objective::manufac");
				}
				try{o[3]=e.getNominalMagnification()!=null ? e.getNominalMagnification().toString() : "";
				}catch(Exception err){
					LOGGER.severe("[EDITOR] Can't parse objective::nominalMagn");
				}
				try{o[4]=e.getCalibratedMagnification()!=null ? e.getCalibratedMagnification().toString() : "";
				}catch(Exception err){
					LOGGER.severe("[EDITOR] Can't parse objective::calibrationMagn");
				}
				try{o[5]=e.getLensNA()!=null ? e.getLensNA().toString():"";
				}catch(Exception err){
					LOGGER.severe("[EDITOR] Can't parse objective::lensNA");
				}
				try{o[6]= e.getImmersion()!=null ? e.getImmersion().getValue():"";
				}catch(Exception err){
					LOGGER.severe("[EDITOR] Can't parse objective::immersion");
				}
				try{o[7]=e.getCorrection()!=null ? e.getCorrection().getValue():"";
				}catch(Exception err){
					LOGGER.severe("[EDITOR] Can't parse objective::correction");
				}
				try{
					String val="";
					if(e.getWorkingDistance()!=null)
						val=e.getWorkingDistance().value().toString()+e.getWorkingDistance().unit().getSymbol();
					o[8]=val;
				}catch(Exception err){
					LOGGER.severe("[EDITOR] Can't parse objective::workingDist");
					System.out.println("[EDITOR] Can't parse objective::workingDist");
				}
			}
			return o;
		}
		
	}

	public Objective getObjective() 
	{ 
		if(selectObjective==null)
			return null;
	
		return new Objective(selectObjective);
	}

}
