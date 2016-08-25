package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import java.awt.Component;

import javax.swing.BoxLayout;
import javax.swing.JLabel;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.Box;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.UOSMetadataLogger;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.MetaDataModel;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules.ObjectiveEditor.ObjectiveTableModel;

import ome.xml.model.Dichroic;
import ome.xml.model.Filter;
import ome.xml.model.LightPath;
import ome.xml.model.Objective;
import ome.xml.model.enums.FilterType;

import java.awt.Font;

import loci.formats.MetadataTools;

public class LightPathEditor extends JDialog implements ActionListener 
{

	/** Logger for this class. */
    private static Logger LOGGER = Logger.getLogger(UOSMetadataLogger.class.getName());
    
    /**output: selected filter for lightPath */
	private List<Object> lightPathList;
	
	private LightPath lightPath;
	
	private FilterCompUI filterUI;

	/** input: list for selection*/
	private List<Object> availableFilter;
	
	private LightPathTable lightPathTable;
	private AvailableFilterTable avFilterTable;
	

	
	public LightPathEditor(JFrame parent,String title, List<Object> _availableFilter,
			LightPath l)
	{
		super(parent,title);
		this.availableFilter=_availableFilter;
		lightPath=l;
		lightPathList=new ArrayList<Object>();
		
		JPanel	panel=createGUIEditor();
		initGUI(panel);
	}
	

	
	private void initGUI(JPanel panel)
	{
		setBounds(100, 100, 500, 600);
		getContentPane().setLayout(new BorderLayout());
		setModal(true);

		getContentPane().add(panel, BorderLayout.CENTER);

		//Bottom
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));

		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
					try {
						createLightPathListFromJTable();
					} catch (Exception e1) {
						LOGGER.severe("CAN'T READ LIGHTPATH FROM TABLE");
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
				lightPathList.clear();
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

	private JPanel newFilterElemPane()
	{
		JButton addBtn = new JButton("Add To LightPath");
		addBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				lightPathTable.appendElem(filterUI, "");
			}
		});
		
		JPanel btnPane=new JPanel();
		btnPane.setLayout(new BoxLayout(btnPane, BoxLayout.X_AXIS));
		btnPane.add(Box.createHorizontalGlue());
		btnPane.add(addBtn);
		
		filterUI=new FilterCompUI(null);
		filterUI.createDummyPane(false);
		filterUI.buildComponents();

		JLabel label = new JLabel("New Element:");
		label.setFont(new Font("Tahoma", Font.BOLD, 11));
		
		
		JPanel topPane=new JPanel();
		topPane.setLayout(new BorderLayout(0, 0));
		topPane.add(label, BorderLayout.NORTH);
		topPane.add(filterUI,BorderLayout.CENTER);
		topPane.add(btnPane,BorderLayout.SOUTH);
		
		return topPane;
	}
	
	private JPanel availableFilterPane()
	{
		JPanel pane=new JPanel();
		pane.setLayout(new BorderLayout(0, 0));

		JLabel label = new JLabel("Available Elements:");
		label.setFont(new Font("Tahoma", Font.BOLD, 11));
		
		JScrollPane scrollPane = new JScrollPane();

		avFilterTable = new AvailableFilterTable();  
		scrollPane.setViewportView(avFilterTable);
//		avFTable.setPreferredScrollableViewportSize(avFTable.getPreferredSize());
//		avFTable.setFillsViewportHeight(true);
		
		//load data
		if(availableFilter!=null)
		{
			for(Object f: availableFilter){
				avFilterTable.appendElem(f);
			}
		}
		
		JButton addBtn = new JButton("Add To LightPath");
		addBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				int[] rows = avFilterTable.getSelectedRows();
				for(int i=0; i<rows.length; i++){
					String cat="";
					Object o;
					//test if prim dichroic still exists:
					Object selectedObj=availableFilter.get(rows[i]);
					o=selectedObj;
					if( selectedObj instanceof Dichroic){
						if(lightPath!=null && lightPath.getLinkedDichroic()!=null){
							o=MetaDataModel.convertDichroicToFilter((Dichroic) selectedObj);
						}
						cat="Dichroic";
					}else{
						if(lightPath!=null && lightPath.getLinkedDichroic()==null){
							o=MetaDataModel.convertFilterToDichroic((Filter) selectedObj);
						}
					}
					
					lightPathTable.appendElem(o, cat);
				}
			}
			
		});
		JButton loadBtn = new JButton("Load Filterset");
		loadBtn.setEnabled(false);
		loadBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				
			}
		});
		
		JPanel btnPane=new JPanel();
		btnPane.setLayout(new BoxLayout(btnPane, BoxLayout.X_AXIS));
		btnPane.add(loadBtn);
		btnPane.add(Box.createHorizontalGlue());
		btnPane.add(addBtn);
		
		pane.add(label,BorderLayout.NORTH);
		pane.add(scrollPane,BorderLayout.CENTER);
		pane.add(btnPane,BorderLayout.SOUTH);
		
		return pane;
	}
	
	private JPanel createGUIEditor()
	{
		
		JPanel top=new JPanel();
		top.setLayout(new BoxLayout(top,BoxLayout.X_AXIS));
		top.setBorder(new EmptyBorder(5,5,5,5));
		top.add(newFilterElemPane());
		top.add(Box.createHorizontalStrut(5));
		top.add(availableFilterPane());
		
		JPanel panel=new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBorder(new EmptyBorder(5, 5, 5, 5));
		panel.add(top);
		
		
		panel.add(Box.createVerticalStrut(10));

		//bottom
		JPanel panel_2 = new JPanel();
		panel.add(panel_2);
		panel_2.setLayout(new BorderLayout(0, 0));
		panel_2.setBorder(new EmptyBorder(5, 5, 5, 5));

		JLabel label = new JLabel("LightPath:");
		label.setFont(new Font("Tahoma", Font.BOLD, 11));
		panel_2.add(label,BorderLayout.NORTH);
		
		JScrollPane scrollPane = new JScrollPane();
		panel_2.add(scrollPane,BorderLayout.CENTER);

		lightPathTable = new LightPathTable();
		scrollPane.setViewportView(lightPathTable);

		
		
		if(lightPath!=null){
			
			List<Filter> exL=lightPath.copyLinkedExcitationFilterList();
			for(Filter f:exL){
				try{lightPathTable.appendElem(f, "Exitation");}catch(Exception e){e.printStackTrace();};
			}

			if(lightPath.getLinkedDichroic()!=null)
				lightPathTable.appendElem(lightPath.getLinkedDichroic(), "Dichroic");
			
			List<Filter> emL=lightPath.copyLinkedEmissionFilterList();
			for(Filter f:emL){
				try{lightPathTable.appendElem(f, "Emission");}catch(Exception e){e.printStackTrace();};
			}
		}
	 
	      JMenu insert= new JMenu("Insert Filter before");
	    createFilterListMenu(insert,availableFilter);
	    lightPathTable.notifyAvFilter(insert);  

		
		return panel;

	}



	private void createFilterListMenu(JMenu insert,	List<Object> list) 
	{
		if(list==null || list.isEmpty()){
			JMenuItem item=new JMenuItem("Empty Filter list");
			insert.add(item);
			return;
		}
		
		int i=0;
		for(Object f: list){
			if(f!=null){
				JMenuItem item;

				if(f instanceof Dichroic){
					String id=((Dichroic) f).getID()!=null ? ((Dichroic) f).getID() : "#"+i;
					item=new JMenuItem(
							id+"["+((Dichroic) f).getModel()+", Dichroic]");
				}else{ //if(f instanceof Filter){


					String id=((Filter) f).getID()!=null ? ((Filter) f).getID() : "#"+i;
					item=new JMenuItem(
							id+"["+((Filter) f).getModel()+", "+
									((Filter) f).getType()+"]");
				}
				item.addActionListener(this);
				insert.add(item);
				i++;
			}
		}
		
	}

	
	public static final Object parseToLightPathObject(String[] s) throws Exception
	{
		String type="Filter";
		if(s[0]!=null && s[0].contains("Dichroic"))
			type="Dichroic";
		
		switch (type) {
		case "Dichroic":
			Dichroic d= new Dichroic();
			d.setID(s[0]);
			d.setModel(s[1]);
			d.setManufacturer(s[2]);
			return d;
//			break;

		default:
			Filter o=new Filter();
			if(s[3].equals(FilterType.DICHROIC.toString())){
				((Filter) o).setID(s[0]);
				((Filter) o).setModel(s[1]);
				((Filter) o).setManufacturer(s[2]);
				((Filter) o).setType(FilterType.DICHROIC);
			}else{
				((Filter) o).setID(s[0]);
				((Filter) o).setModel(s[1]);
				((Filter) o).setManufacturer(s[2]);
				((Filter) o).setType(s[3].equals("")? null : FilterType.fromString(s[3]));
				((Filter) o).setFilterWheel(s[4]);
			}
			return o;
		}
		
		
	}
	
	
	
	
	private void createLightPathListFromJTable() throws Exception
	{
		lightPathList=new ArrayList<Object>();
		for(int i=0; i<lightPathTable.getRowCount();i++){
			lightPathList.add(parseToLightPathObject(lightPathTable.getRowData(i)));
		}
	}
	
	
	
	
	

	public List<Object> getLightPathList()
	{
		return lightPathList;
	}

	@Override
	public void actionPerformed(ActionEvent e) 
	{
		 JMenuItem source = (JMenuItem)(e.getSource());
		    try{
		        JMenuItem menuItem = (JMenuItem) e.getSource(); 
		        JPopupMenu popupMenu = (JPopupMenu) menuItem.getParent(); 
		        Component invoker = popupMenu.getInvoker();  
		        // Print MenuItem index against the total number of items
//		        System.out.println(popupMenu.getComponentZOrder(menuItem)+"/"+popupMenu.getComponentCount());
		        lightPathTable.insertElemAtSelection(availableFilter.get(popupMenu.getComponentZOrder(menuItem)));
		    }catch(Exception ex){
		        ex.printStackTrace();
		    }
	}

	
	class AvailableFilterTable extends JTable
	{
		public AvailableFilterTable()
		{
			setModel(new AvailableFilterTableModel());
		}
		public boolean isCellEditable(int row,int column){  
	        return false;  
	      }
		
		public void appendElem(Object f)
		{
			((AvailableFilterTableModel) getModel()).addRow(f);
		}
		
	}
	
	class AvailableFilterTableModel extends DefaultTableModel
	{
		Class[] columnTypes = new Class[] {
				String.class, String.class, String.class
		};
		
		private ArrayList<TableColumn> tableColumns;
		
		public AvailableFilterTableModel()
		{
			super(new Object[][] {},
					new String[] {"ID","Model", "Type"}	); 
		}
		
		public Class getColumnClass(int columnIndex) {
			return columnTypes[columnIndex];
		}
		
		public void addRow(Object f)
		{
			Object[] o=parseFromFilterShort(f);
			super.addRow(o);
		}
		
		public void insertRow(int index, Object f)
		{
			Object[] o=parseFromFilterShort(f);
			super.insertRow(index, o);
		}
		
		private Object[] parseFromFilterShort(Object e)
		{
			Object[] o=new Object[3];
			if(e!=null){
				if(e instanceof Filter){
					Filter f=(Filter) e;
					o[0]=f.getID()!=null ? f.getID() : "";
					o[1]=f.getModel()!=null ? f.getModel() : "";
					o[2]=f.getType()!=null ? f.getType().toString() : "";
				}else if (e instanceof Dichroic){
					Dichroic f=(Dichroic) e;
					o[0]=f.getID()!=null ? f.getID() : "";
					o[1]=f.getModel()!=null ? f.getModel() : "";
					o[2]="Dichroic";
				}
			}
			
			//empty element?
			if(o[0].equals("") && o[1].equals("") && o[2].equals(""))
				return null;
			
			return o;
		}
		
	}
	
}

