package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor;

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
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.FilterCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.LightPathTable;

import ome.xml.model.Dichroic;
import ome.xml.model.Filter;
import ome.xml.model.LightPath;
import ome.xml.model.enums.FilterType;

import java.awt.Font;

import loci.formats.MetadataTools;

public class LightPathEditor extends JDialog implements ActionListener 
{

	/** Logger for this class. */
    private static Logger LOGGER = Logger.getLogger(UOSMetadataLogger.class.getName());
    
	//	private final JPanel contentPanel = new JPanel();
	private List<Object> lightPathList;
	private LightPath lightPath;
	
	private FilterCompUI filterUI;


	private List<Object> availableFilter;
	private LightPathTable lightPathTable;
	


	
	public LightPathEditor(JFrame parent,String title, List<Object> _availableFilter,LightPath l)
	{
		super(parent,title);
		availableFilter=_availableFilter;
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
		

		final JTable avFTable = new JTable(){  
		       public boolean isCellEditable(int row,int column){  
		           return false;  
		         }  };
		scrollPane.setViewportView(avFTable);
		avFTable.setPreferredScrollableViewportSize(avFTable.getPreferredSize());
		avFTable.setFillsViewportHeight(true);
		avFTable.setModel(new DefaultTableModel(
				new Object[][] {
				},
				new String[] {
						"ID","Model", "Type",
				}
				) {
			Class[] columnTypes = new Class[] {
					String.class, String.class, String.class
			};
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
		});
		
		//load data
		if(availableFilter!=null)
		{
			for(Object f: availableFilter){
				((DefaultTableModel)avFTable.getModel()).addRow(parseFromFilterShort(f));
			}
		}
		
		JButton addBtn = new JButton("Add To LightPath");
		addBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				int[] rows = avFTable.getSelectedRows();
				for(int i=0; i<rows.length; i++){
					String cat="";
					Object o;
					//test if prim dichroic still exists:
					if(availableFilter.get(rows[i]) instanceof Dichroic){
						if(lightPath!=null && lightPath.getLinkedDichroic()!=null){
							o=MetaDataModel.convertDichroicToFilter((Dichroic) availableFilter.get(rows[i]));
						}else{
							o=availableFilter.get(rows[i]);
						}
						cat="Dichroic";
					}else{
						if(lightPath!=null && lightPath.getLinkedDichroic()==null){
							o=MetaDataModel.convertFilterToDichroic((Filter) availableFilter.get(rows[i]));
						}else{
							o=availableFilter.get(rows[i]);
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
		if(list!=null && !list.isEmpty()){
			int i=0;
		for(Object f: list){
			JMenuItem item;
			if(f instanceof Filter){
				item=new JMenuItem(
						((Filter) f).getID()+"["+((Filter) f).getModel()+", "+
								((Filter) f).getType()+"]");
			}else{
				item=new JMenuItem(
						((Dichroic) f).getID()+"["+((Dichroic) f).getModel()+", Dichroic]");
			}
			item.addActionListener(this);
			insert.add(item);
			i++;
		}
		}else{
			JMenuItem item=new JMenuItem("Empty Filter list");
			insert.add(item);
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
		return o;
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

	
}

