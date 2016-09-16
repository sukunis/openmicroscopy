package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.util.ProfileConfPanel;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.microscope.CustomViewProperties;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.ExceptionDialog;
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

	/** profile src file as xml */
	private File file;
	/** container for customize view and values*/
	private CustomViewProperties prop;
	/** array of module positions */
	protected String[] selectedPos;
	
	// GUI
	private JButton openFileBtn;
	private JButton saveFileBtn;
	private JButton okBtn;
	private JButton cancelBtn;

	/** textfield: name of source file of displayed values*/
	private JTextField fileName;
	/** textfield: specify mic name */
	private JTextField micName;

	private JPanel main;
	private JPanel dataPane;
	private JPanel buttonPane;
	private Box headerPane;
	private JFileChooser fc;

	private final int xDim=1300;
	private final int yDim=900;

	/**
	 * 
	 * @param cView view properties read from file
	 */
	public UOSProfileEditorUI(CustomViewProperties cView)
	{
		prop=cView;
		setTitle("Customize View");
		setModal(true);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setPreferredSize(new Dimension(xDim,yDim));
		buildGUI();
		add(main);
		pack();
	}

	/**
	 * 
	 * @return properties container of defined positions and values inside the GUI
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
		
		// set module positions empty
		selectedPos= new String[8];
		for(int i=0; i<8; i++)
			selectedPos[i]="";
		
		main=new JPanel();
		main.setLayout(new BorderLayout(10,10));
		main.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

		//get and display profile file location
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

		//get and display microscope name
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
		dataPane=showData(prop);
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
	 * Display configuration from given file
	 * @return panel with configurations
	 */
	private JPanel showData(CustomViewProperties cProp)
	{
		JPanel pane = new JPanel();
		pane.setBorder(BorderFactory.createEtchedBorder());
		pane.setLayout(new GridLayout(0,4,5,5));

		if(prop.getImageConf()==null)
			prop.loadImageConf(false);
		pane.add(new ProfileConfPanel(prop.getImageConf(),"Image Modul",TagNames.getImageTags(), false, 0,selectedPos));

		if(prop.getObjConf()==null)
			prop.loadObjectiveConf(false);
		pane.add(new ProfileConfPanel(prop.getObjConf(),"Objective Modul",TagNames.getObjectiveTags(),true,1,selectedPos));
		
		if(prop.getDetectorConf()==null)
			prop.loadDetectorConf(false);
		pane.add(new ProfileConfPanel(prop.getDetectorConf(),"Detector Modul",TagNames.getDetectorTags(),true,2,selectedPos));
		
		if(prop.getLightSrcConf()==null)
			prop.loadLightSrcConf(false);
		pane.add(new ProfileConfPanel(prop.getLightSrcConf(),"LightSource Modul",TagNames.getLightSrcTags(),true,3,selectedPos));

		if(prop.getChannelConf()==null)
			prop.loadChannelConf(false);
		pane.add(new ProfileConfPanel(prop.getChannelConf(),"Channel Modul",TagNames.getChannelTags(),false,4,selectedPos));
		
//		if(prop.getLightPathConf()==null)
//			prop.loadLightPathConf(false);
		pane.add(new ProfileConfPanel(prop.getLightPathConf(),"LightPath Modul",null,false,5,selectedPos));
		
		if(prop.getSampleConf()==null)
			prop.loadSampleConf(false);
		pane.add(new ProfileConfPanel(prop.getSampleConf(),"Sample Modul",TagNames.getSampleTags(),true,6,selectedPos));
		
		if(prop.getExpConf()==null)
			prop.loadExperimentConf(false);
		pane.add(new ProfileConfPanel(prop.getExpConf(),"Experiment Modul",TagNames.getExperimentTags(),false,7,selectedPos));

		return pane;

	}

	
	/**
	 * Write configuration to given CustomViewProperties cView
	 * @param cView
	 */
	private CustomViewProperties applyConfigurations(CustomViewProperties cView)
	{
		cView.setMicName(micName.getText());
		Component[] comp=dataPane.getComponents();
		
		//read from gui
		ModuleConfiguration imageConf=((ProfileConfPanel)comp[0]).getConfiguration();
		ModuleConfiguration objConf=((ProfileConfPanel)comp[1]).getConfiguration();
		ModuleConfiguration detectorConf=((ProfileConfPanel)comp[2]).getConfiguration();
		ModuleConfiguration lightSrcConf=((ProfileConfPanel)comp[3]).getConfiguration();
		ModuleConfiguration channelConf=((ProfileConfPanel)comp[4]).getConfiguration();
		ModuleConfiguration lightPathConf=((ProfileConfPanel)comp[5]).getConfiguration();
		ModuleConfiguration sampleConf=((ProfileConfPanel)comp[6]).getConfiguration();
		ModuleConfiguration experimenterConf=((ProfileConfPanel)comp[7]).getConfiguration();
		
		
		// write it to container
		System.out.println("##########APPLY CONF");
		cView.setImageConf(imageConf);
		cView.setObjConf(objConf);
		cView.setDetectorConf(detectorConf);
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


	@Override
	public void actionPerformed(ActionEvent e) 
	{
		// Press OK Button
		if(e.getSource() == okBtn){
			System.out.println("######OK BTN");
			//TODO: sind ueberhaupt aenderungen da?
			prop=applyConfigurations(prop);
			if(prop!=null){
				prop.getObjConf().printConfig();
				prop.getDetectorConf().printConfig();

				UOSProfileWriter writer=new UOSProfileWriter();
				File output=null;
				//if no file available save to standard file in the directory where the application was started
				if(fileName.getText()==null || fileName.getText().equals("")){
					output=new File("profileUOSImporter.xml");
					try {
						output.createNewFile();
					} catch (IOException e1) {
						ExceptionDialog ld = new ExceptionDialog("Configuration error!", 
					            "Can't create new file "+output.getAbsolutePath()+"!",e1,
					            this.getClass().getSimpleName());
					    ld.setVisible(true);
					}
				}else{
					output=new File(fileName.getText());
				}
				// save changes to file
				writer.save(output, prop);
			}else{
				ExceptionDialog ld = new ExceptionDialog("Configuration error!", 
			            "Can't read given configuration!",new Exception("Configuration container is null"),
			            this.getClass().getSimpleName());
			    ld.setVisible(true);
			}
			//close editor
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
		dataPane=showData(prop);

		main.removeAll();
		main.add(headerPane,BorderLayout.NORTH);
		main.add(dataPane,BorderLayout.CENTER);
		main.add(buttonPane,BorderLayout.SOUTH);

		revalidate();
		repaint();
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
