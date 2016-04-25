package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.Box;
import javax.swing.JButton;
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






import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.UOSMetadataLogger;

import ome.xml.model.Laser;
import ome.xml.model.LightSource;

public class LightSourceEditor extends JDialog
{
	/** Logger for this class. */
    private static Logger LOGGER = Logger.getLogger(UOSMetadataLogger.class.getName());
	
	private LightSource lightSrc;
	
	private List<LightSource> availableLightSrc;
	
	private JTable lightSrcTable;
	
	public LightSourceEditor(JFrame parent,String title,
			List<LightSource> _availableLightSrc)
	{
		super(parent,title);
		availableLightSrc=_availableLightSrc;
		
		JPanel panel=createGUIEditor();
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
		
//		JButton loadBtn= new JButton("Load Workstation List");
//		loadBtn.setActionCommand("Load");
//		loadBtn.setEnabled(false);
//		buttonPane.add(loadBtn);
//		buttonPane.add(Box.createHorizontalGlue());

		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
					try {
						int idx=lightSrcTable.getSelectedRow();
						if(idx!=-1)
							lightSrc=availableLightSrc.get(idx);
						else
							lightSrc=null;
					} catch (Exception e1) {
						LOGGER.severe("can't read LIGHTPATH from table");
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
				lightSrc=null;
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

	private JPanel createGUIEditor() 
	{
		JLabel label = new JLabel("Available Elements:");
		label.setFont(new Font("Tahoma", Font.BOLD, 11));
		
		JScrollPane scrollPane = new JScrollPane();
		

		lightSrcTable = new JTable(){  
		       public boolean isCellEditable(int row,int column){  
		           return false;  
		         }  };
		         
		scrollPane.setViewportView(lightSrcTable);
//		lightSrcTable.setPreferredScrollableViewportSize(lightSrcTable.getPreferredSize());
//		lightSrcTable.setFillsViewportHeight(true);
		lightSrcTable.setModel(new LightSrcTableModel());
		lightSrcTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		//fill table
		if(availableLightSrc!=null)
		{
			for(LightSource o: availableLightSrc){
				((LightSrcTableModel)lightSrcTable.getModel()).addRow(o);
			}
		}
		
		JPanel panel=new JPanel();
		panel.setLayout(new BorderLayout(0, 0));
		panel.setBorder(new EmptyBorder(5,5,5,5));
		panel.add(label,BorderLayout.NORTH);
		panel.add(scrollPane,BorderLayout.CENTER);
		return panel;
	}
	
	public LightSource getSelectedLightSource()
	{
		return lightSrc;
	}
	
	
	class LightSrcTableModel extends DefaultTableModel
	{
		Class[] columnTypes = new Class[] {
				String.class,String.class, String.class, String.class, String.class, String.class};
		
		public LightSrcTableModel()
		{
			super(new Object[][] {},
					new String[] {"ID","Model", "Type","Wavelength","Power","Repititation Rate"});
		}
		
		public Class getColumnClass(int columnIndex) {
			return columnTypes[columnIndex];
		}
		
		public void addRow(LightSource o)
		{
			super.addRow(parseFromLightSrc(o));
		}

		private Object[] parseFromLightSrc(LightSource l) 
		{
			Object[] o=new Object[6];
			if(l!=null){
				o[0]=l.getID()!=null ? l.getID():"";
				o[1]=l.getModel()!=null ? l.getModel() : "";
				o[2]=l.getClass().getName();
				if(l instanceof Laser){
					o[3]=((Laser)l).getWavelength()!=null ? 
							((Laser)l).getWavelength().value()+((Laser)l).getWavelength().unit().getSymbol() : "";
					o[4]=((Laser)l).getPower()!=null ? 
							((Laser)l).getPower().value()+((Laser)l).getPower().unit().getSymbol() : ""; 
					o[5]=((Laser)l).getRepetitionRate()!=null ? 
							((Laser)l).getRepetitionRate().value()+((Laser)l).getRepetitionRate().unit().getSymbol() : ""; 
				}else{
					o[3]="";
					o[4]="";
					o[5]="";
				}
			}
			return o;
		}
	}
}
