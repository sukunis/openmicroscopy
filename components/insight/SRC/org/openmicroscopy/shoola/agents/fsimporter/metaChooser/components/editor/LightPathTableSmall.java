package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import ome.xml.model.Dichroic;
import ome.xml.model.Filter;
import ome.xml.model.enums.FilterType;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.UOSMetadataLogger;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.LightPathTable.LightPathTableModel;

public class LightPathTableSmall extends JTable
{
	/** Logger for this class. */
    private static Logger LOGGER = Logger.getLogger(UOSMetadataLogger.class.getName());
    
//	private JPopupMenu popupMenu;
    private List<Object> lightPathMembers;
	
	public LightPathTableSmall()
	{
		setModel(new CustomModel());
		lightPathMembers=new ArrayList<Object>();
	}
	
	public boolean isCellEditable(int row,int column)
	{  
        return false;  
	} 
	
	public void appendElem(Object o, String category)
	{
		if(o instanceof FilterCompUI){
			Filter f;
			try {
				f = (Filter) ((FilterCompUI) o).getData();//((FilterCompUI) o).copyData();
				if(f!=null){
					((CustomModel) getModel()).addRow(f,"");
					((FilterCompUI) o).clearDataValues();
				}
			} catch (Exception e) {
				LOGGER.severe("Filter is not a valid element!!!");
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}else{
			((CustomModel) getModel()).addRow(o,category);
		}
		lightPathMembers.add(o);
	}
	
	/**
     * Return a row from the table as a array of strings
     * @param rowIndex The index of the row you would like
     * @return Returns the row from the table as an array of strings or null if
     * the index is invalid
     */
	public Object getRowData(int rowIndex)
    {
		if(lightPathMembers!=null && !lightPathMembers.isEmpty()){
			return lightPathMembers.get(rowIndex);
		}
		else
			return null;
    }
	
	
	
	public void clearData()
	{
		lightPathMembers=new ArrayList<Object>();
		setModel(new CustomModel());
	}
	public List<Object> getLightPathList() 
	{
		return lightPathMembers;
	}




	class CustomModel extends DefaultTableModel
	{
		Class[] columnTypes = new Class[] {
				String.class,String.class, String.class
		};


		public CustomModel()
		{
			super(new Object[][] {},
					new String[] {"Model", "Manufactur", "Type"});

		}

		public Class getColumnClass(int columnIndex) {
			return columnTypes[columnIndex];
		}

		public void addRow(Object o, String category)
		{
			super.addRow(parseFromFilterLong(o,category));
		}

		private Object[] parseFromFilterLong(Object e,String cat)
		{
			Object[] o=new Object[3];
			if(e!=null){
				if(e instanceof Filter){
					Filter f=(Filter) e;
					String type=f.getType()!=null ? f.getType().toString() : "";
					o[0]=f.getModel()!=null ? f.getModel() : "";
					o[1]=f.getManufacturer()!=null ? f.getManufacturer() : "";
					o[2]=f.getType()!=null ? f.getType().toString() : "";
				}else if (e instanceof Dichroic){
					Dichroic f=(Dichroic) e;
					o[0]=f.getModel()!=null ? f.getModel() : "";
					o[1]=f.getManufacturer()!=null ? f.getManufacturer() : "";
					o[2]="Dichroic";
				}
			}
			return o;
		}
		
	}




	
	
}
