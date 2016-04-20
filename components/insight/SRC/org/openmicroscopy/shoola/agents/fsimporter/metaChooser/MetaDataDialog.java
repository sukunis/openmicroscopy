package org.openmicroscopy.shoola.agents.fsimporter.metaChooser;

import info.clearthought.layout.TableLayout;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import loci.common.services.ServiceFactory;
import loci.formats.FormatException;
import loci.formats.ImageReader;
import loci.formats.UnknownFormatException;
import loci.formats.meta.IMetadata;
import loci.formats.services.OMEXMLService;
import ome.xml.meta.OMEXMLMetadataRoot;
import ome.xml.model.Experiment;
import ome.xml.model.Experimenter;
import ome.xml.model.Project;
import omero.gateway.model.ExperimenterData;

import org.openmicroscopy.shoola.agents.fsimporter.ImporterAgent;
import org.openmicroscopy.shoola.agents.fsimporter.actions.ImporterAction;
import org.openmicroscopy.shoola.agents.fsimporter.chooser.ImportDialog;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.MetaDataModel;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.ExperimentCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.UOSProfileReader;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.microscope.CustomViewProperties;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.microscope.MetaDataUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.microscope.MicroscopeDataView;
import org.openmicroscopy.shoola.agents.fsimporter.view.Importer;
import org.openmicroscopy.shoola.env.LookupNames;
import org.openmicroscopy.shoola.env.data.model.FileObject;
import org.openmicroscopy.shoola.env.data.model.ImportableFile;
import org.openmicroscopy.shoola.util.ui.ClosableTabbedPaneComponent;
import org.openmicroscopy.shoola.util.ui.UIUtilities;


/**
 * Dialog used to control and specify metadata for files to import
 * @author kunis
 * @version 1.0
 */
public class MetaDataDialog extends ClosableTabbedPaneComponent
	implements ActionListener, PropertyChangeListener, TreeSelectionListener, TreeExpansionListener
{
	
	 /** Logger for this class. */
    private static Logger LOGGER = Logger.getLogger(UOSMetadataLogger.class.getName());
    
    
	private boolean DEBUG=false;
	
	/** The title of the dialog. */
	private static final String TITLE = "Specify Metadata";

	
	/** The owner related to the component. */
	private JFrame owner;
	/** Reference to the model.*/
	private Importer importer;
	
	/** The type associated to the import. */
	private int type;
	
	/** The approval option the user chose. */
	//private int option;
	JTabbedPane tp;
	
	/** Button to cancel all imports. */
	private JButton cancelImportButton;
	
	/** Button to import the files. */
	private JButton importButton;
	
	/** Button to refresh the file chooser. */
	private JButton refreshFilesButton;
	
	/** Test text area*/
	public JTextArea textArea; 
	
	private JTree fileTree;
//	private JComboBox<String> micList;
	private JLabel micName;
	
//	private JPanel metaPanelAcq;
	private JPanel metaPanelMic;
	private JTabbedPane metaPanel;
//	private MicroscopeDataView dataView;
	private MetaDataUI dataView;
	private CustomViewProperties customSettings;
	
	
	/** debug pane*/
	private JTextPane debugTextPane;
	private JTextPane bioFormatPane;
	
	/** Identifies the style of the document.*/
	private static final String STYLE = "StyleName";
	/** The maximum number of characters in the debug text.*/
	private static final int 	MAX_CHAR = 200000;
	
	/** lastSelection types*/
	private static final int DIR=0;
	private static final int FILE=1;
	
	/** type of last selection in tree */
	private int lastSelectionType;
	
	private FNode lastNode;
	
	private String defaultMic="Unspecified";
	
	private String[] channelList;
	
	/** Bound property indicating that the cancel button is pressed. */
	public static final String CANCEL_SELECTION_PROPERTY = "cancelSelection";
	/** Action id indicating to import the selected files. */
	private static final int CMD_IMPORT = 1;
	/** Action id indicating to close the dialog. */
	private static final int CMD_CLOSE = 2;
	/** Action id indicating to reset the names. */
	private static final int CMD_REFRESH = 3;
	private static final int LOAD_MIC_SETTINGS=4;
	
	
	
	
	/**
	 * Creates a new instance.
	 * 
	 * @param owner
	 *            The owner of the dialog.
	 * @param filters
	 *            The list of filters.
	 * @param selectedContainer
	 *            The selected container if any.
	 * @param objects
	 *            The possible objects.
	 * @param type
	 *            One of the type constants.
	 * @param importerAction
	 *            The cancel-all-imports action.
	 */
	public MetaDataDialog(JFrame owner, FileFilter[] filters, int type,
			ImporterAction importerAction, Importer importer)
	{
		super(1, TITLE, TITLE);
		
		// init logger
		UOSMetadataLogger.init();
		
		this.owner = owner;
		this.type = type;
		this.importer = importer;
		setClosable(false);
		setCloseVisible(false);
		initComponents(filters, importerAction);
		buildGUI();
	}
	
	
	private void addNode(FNode parent, FileObject f,ImportableFile fileObj)
	{
		
		FNode dir = null;
		FNode file = null;
		String prop=null;
		ImportUserData data=null;
		
		if(fileObj!=null){
			createParentOME(fileObj);
			prop="[Group: "+fileObj.getGroup().getName()+", Project: "+
			fileObj.getParent().asProject().getName().getValue()+"]";
			data = new ImportUserData(fileObj.getGroup(), fileObj.getParent().asProject(), fileObj.getUser());
		}else{
//			copyParentOME(f.getParentName());
		}
		
		if(f.isDirectory()){

			dir=new FNode(new File(f.getAbsolutePath()),data);
			parent.add(dir);
			LOGGER.info("[TREE] Append Dir "+f.getAbsolutePath());
			File[] files=(new File(f.getAbsolutePath()).listFiles());

			if(files != null && files.length>0){
				for(int i=0; i<files.length;i++){
					addNode(dir,new FileObject(files[i]),null);
				}
			}

			//add files, attention: only image files?
		}else{
			try {
				file=new FNode(new File(f.getAbsolutePath()),data);
				LOGGER.info("[TREE] Append File "+f.getAbsolutePath());
				parent.add(file);
			} catch (Exception e) {
				LOGGER.info("[TREE] Wrong import format "+f.getAbsolutePath());
			}
		}
	}
	
	private void createParentOME(ImportableFile fileObj) 
	{
		OMEXMLMetadataRoot ome=new OMEXMLMetadataRoot();
		
		//Experimenter/User/Importer
		ome.addExperimenter(convertExperimenter(fileObj.getUser()));
		// TODO input Partner as ExperimenterRef
		// TODO input partner working group as ExperimenterGroupRef
		
		
		//import project
		Project p=new Project();//==import project
		p.setName(fileObj.getParent().asProject().getName().getValue());
		p.setDescription(fileObj.getParent().asProject().getDescription().toString());
		ome.addProject(p);
		
		//TODO: input experiment data
		
		//TODO: input probe data as XMLAnnotation link by project
		
	}


	private Experimenter convertExperimenter(ExperimenterData expData) 
	{
		Experimenter exp=new Experimenter();
		try{exp.setFirstName(expData.getFirstName());}catch(Exception e){};
		try{exp.setLastName(expData.getLastName());}catch(Exception e){};
//		try{exp.setID((expData.getId());}catch(Exception e){};
		
		return exp;
	}


	private void createNodes(List<ImportableFile> files){
		
		DefaultTreeModel treeModel=(DefaultTreeModel)fileTree.getModel();
		FNode root =(FNode)treeModel.getRoot();
		
		root.removeAllChildren();
		
		 ImportableFile f;
		 Iterator<ImportableFile> j=files.iterator();

		    while (j.hasNext()) {
		    	f = j.next();
		    	addNode(root,f.getFile(),f);
		    	LOGGER.info("BUILD FILE TREE: add "+f.getFile().getAbsolutePath()+
		    			"[group: "+f.getGroup().getName()+", project: "+
		    			f.getParent().asProject().getName().getValue()+"]");
		    }
		    treeModel.reload();
	}
	
	
	
	
	private void initComponents(FileFilter[] filters,
	        ImporterAction importerAction)
	{
		cancelImportButton = new JButton(importerAction);
//		importerAction.setEnabled(false);
				
		importButton = new JButton("Import");
		importButton.setToolTipText("Import the selected data");
		importButton.setActionCommand("" + CMD_IMPORT);
		importButton.addActionListener(this);
		importButton.setEnabled(false);	
		
		refreshFilesButton= new JButton("Refresh");
		refreshFilesButton.setBackground(UIUtilities.BACKGROUND);
		refreshFilesButton.setToolTipText("Refresh the selected files and metaData");
	    refreshFilesButton.setActionCommand("" + CMD_REFRESH);
	    refreshFilesButton.addActionListener(this);
	    
	    UOSProfileReader propReader=new UOSProfileReader(new File("profileUOSImporter.xml"));
//	    dataView=new MicroscopeDataView(propReader.getViewProperties());
	    customSettings=propReader.getViewProperties();
	    micName=new JLabel(customSettings.getMicName());
	    dataView=new MetaDataUI(customSettings);
	    
	    metaPanelMic= new JPanel(new BorderLayout(5,5));
	    metaPanelMic.add(dataView);
		
		metaPanel=new JTabbedPane();
	  
//	    micList= new JComboBox<String>(CustomViewProperties.MICLIST);
//		micList.addActionListener(this);
//		micList.setActionCommand("" + LOAD_MIC_SETTINGS);
//		micList.setVisible(false);
	
		
		File root=new File(System.getProperty("user.home"));
		FNode rootNode=new FNode(root);
		
		//Create a tree that allows one selection at a time
		fileTree = new JTree(rootNode);
		fileTree.setRootVisible(false);
		fileTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

		//Listen for when the selection changes.
		fileTree.addTreeSelectionListener(this);
		fileTree.addTreeExpansionListener(this);
		
		lastSelectionType=DIR;
	}
	
	/**
	 * Creates the component hosting the debug text.
	 * 
	 * @return See above.
	 */
	private JComponent createDebugTab()
	{
		debugTextPane = new JTextPane();
		debugTextPane.setEditable(false);
		StyledDocument doc = (StyledDocument) debugTextPane.getDocument();

		Style style = doc.addStyle(STYLE, null);
		StyleConstants.setForeground(style, Color.black);
		StyleConstants.setFontFamily(style, "SansSerif");
		StyleConstants.setFontSize(style, 12);
		StyleConstants.setBold(style, false);

		JScrollPane sp = new JScrollPane(debugTextPane);
		sp.setPreferredSize(new Dimension(250, 250));
		sp.getVerticalScrollBar().addAdjustmentListener(
				new AdjustmentListener()
				{
					public void adjustmentValueChanged(AdjustmentEvent e)
					{
						try {
							debugTextPane.setCaretPosition(
									debugTextPane.getDocument().getLength());
						} catch (IllegalArgumentException ex) {
							//
						}
					}
				}
		);
		
		
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(sp, BorderLayout.CENTER);
		return panel;
	}
	
	private JComponent createBioFormatTab()
	{
		bioFormatPane = new JTextPane();
		bioFormatPane.setEditable(false);
		StyledDocument doc = (StyledDocument) bioFormatPane.getDocument();

		Style style = doc.addStyle(STYLE, null);
		StyleConstants.setForeground(style, Color.black);
		StyleConstants.setFontFamily(style, "SansSerif");
		StyleConstants.setFontSize(style, 12);
		StyleConstants.setBold(style, false);

		JScrollPane sp = new JScrollPane(bioFormatPane);
//		sp.setPreferredSize(new Dimension(250, 250));
		sp.getVerticalScrollBar().addAdjustmentListener(
				new AdjustmentListener()
				{
					public void adjustmentValueChanged(AdjustmentEvent e)
					{
						try {
							bioFormatPane.setCaretPosition(
									bioFormatPane.getDocument().getLength());
						} catch (IllegalArgumentException ex) {
							//
						}
					}
				}
		);
		
		
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(sp, BorderLayout.CENTER);
		return panel;
	}
	
	/**
	 * Builds and lays out file view right side
	 * @return JPanel
	 */
	private JPanel buildFileView(){
		JPanel fileView=new JPanel();
		fileView.setLayout(new BorderLayout(0,0));
		
		//Create the scroll pane and add the tree to it
		JScrollPane treeView = new JScrollPane(fileTree);
		
		fileView.add(treeView);
		
		return fileView;
	}

	
	private Component buildDataView()
	{
		JPanel tab2 =buildDataViewMicroscope();
		
		JPanel micP = new JPanel(new BorderLayout());
		JLabel miclabel=new JLabel("Microscope:");
		miclabel.setLabelFor(micName);
		micP.add(miclabel,BorderLayout.WEST);
		micP.add(micName,BorderLayout.CENTER);
		micP.setBorder(BorderFactory.createEmptyBorder(10,5,10,5));
		
		JPanel mainPanel= new JPanel(new BorderLayout());
		mainPanel.add(micP,BorderLayout.NORTH);
		metaPanel.add(tab2);
		mainPanel.add(metaPanel,BorderLayout.CENTER);
		
		JPanel tab3=(JPanel) createDebugTab();
		tab3.setVisible(DEBUG);
		JPanel tab4=(JPanel) createBioFormatTab();
		tab4.setVisible(DEBUG);

		if(DEBUG){
			tp = new JTabbedPane();
			tp.addTab("Acquisition and Instrument Infos", mainPanel);
			tp.setMnemonicAt(0, KeyEvent.VK_1);
			tp.addTab("Debug",tab3 );
			tp.setMnemonicAt(1, KeyEvent.VK_2);
			tp.addTab("Bio-Format Info",tab4);
			tp.setMnemonicAt(2, KeyEvent.VK_3);

			return tp;
		}
		else{
			return mainPanel;
		}
	}
	
	
	
	private JPanel buildDataViewMicroscope() 
	{
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(metaPanelMic,BorderLayout.CENTER);
		return mainPanel;
	}
	
	/**
	 * Builds and lays out the tool bar.
	 * 
	 * @return See above.
	 */
	private JPanel buildToolBarLeft() {
	    JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT));
	    int plugin = ImporterAgent.runAsPlugin();
	    if (!(plugin == LookupNames.IMAGE_J_IMPORT ||
	            plugin == LookupNames.IMAGE_J)) {
	        bar.add(Box.createHorizontalStrut(5));
	        bar.add(refreshFilesButton);
	    }

	    return bar;
	}
	
	/**
	 * Builds and lays out the tool bar.
	 * @return See above.
	 */
	private JPanel buildToolBarRight() {
		JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		bar.add(cancelImportButton);
		bar.add(Box.createHorizontalStrut(5));
		bar.add(importButton);
		bar.add(Box.createHorizontalStrut(10));
		return bar;
	}
	

	private void buildGUI()
	{
		setLayout(new BorderLayout(0,0));
		JSplitPane splitPane;		
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,buildFileView(),buildDataView());
		splitPane.setResizeWeight(0.5);
		splitPane.setDividerLocation(150);
		
		JPanel mainPanel=new JPanel();
		double[][] mainPanelDesign = { { TableLayout.FILL },
				{ TableLayout.PREFERRED, TableLayout.FILL } };
		mainPanel.setLayout(new TableLayout(mainPanelDesign));
		mainPanel.setBackground(UIUtilities.BACKGROUND);
		mainPanel.add(splitPane, "0, 1");
		
		this.add(mainPanel, BorderLayout.CENTER);
		
		JPanel controls = new JPanel();
		controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));
		
		// Lays out the buttons.
		JPanel bar = new JPanel();
		bar.setLayout(new BoxLayout(bar, BoxLayout.X_AXIS));
		bar.add(buildToolBarLeft());
		bar.add(buildToolBarRight());
		
		controls.add(new JSeparator());
		controls.add(bar);
		add(controls, BorderLayout.SOUTH);
		
//		micList.setSelectedIndex(0);
	}
	private void clearDataView()
	{
//		dataView.clearView();
//		dataView.initView();
		metaPanelMic.removeAll();
		metaPanel.removeAll();
	}
	
	
	
	
	//ome version
//	private void loadAndShowDataForSelection()
//	{
//		
//		if(lastNode!=null){
//			System.out.println("[DEBUG] LAST selection: "+lastNode.getAbsolutePath());
//			lastNode.setModel(dataView.getModel());
//		}
//		if(dataView.hasUserInput() ) 
//		{
//			if(lastSelectionType==FILE)
//			{
//				dataView.saveViewData();
//			}else{
//				System.out.println("DIRECTORY USER INPUT");
//			}
//		}
//		clearDataView();
//		
//		System.out.println("");
//		System.out.println("");
//		System.out.println("");
//		
//		String file = getSelectedFile(); 
//		
//		//import user data
//		ImportUserData importData = getImportData();
//		
//		//TODO parent dir data
//		MetaDataModel parentModel=getParentMetaDataModel();
//		if(parentModel!=null){
//			System.out.println("[DEBUG] READ MODEL OF "+lastNode.getAbsolutePath());
//			try {
//				Experiment e=parentModel.getExperiment();
//				if(e!=null){
//				System.out.println("[DEBUG] Exp desc: "+e.getDescription());
//				}else{
//					System.out.println("[DEBUG] exp == null");
//				}
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		
//		// is selection a file or directory
//		if(file.equals("")){
//			LOGGER.info("[GUI] select directory");
//			//show dataview
//			metaPanel.addTab("",dataView);
//			lastSelectionType=DIR;
//			return;
//		}else{
//			lastSelectionType=FILE;
//		}
//		
//		loadFileMetaData(file, importData);
//	}
	
	private void loadAndShowDataForSelection()
	{
		if(lastNode!=null){
			System.out.println("[DEBUG] LAST selection: "+lastNode.getAbsolutePath());
			lastNode.setModel(dataView.getModel());
			//TODO: save to file if there are some changes
			if(dataView.getModel().noticUserInput())
			{
				
			}
		}
//		if(dataView..getModel().noticUserInput() ) 
//		{
//			if(lastSelectionType==FILE)
//			{
//				dataView.saveViewData();
//			}else{
//				System.out.println("DIRECTORY USER INPUT");
//			}
//		}
		clearDataView();
		
		System.out.println("");
		System.out.println("");
		System.out.println("");
		
		String file = getSelectedFile(); 
		
		//import user data
		ImportUserData importData = getImportData();
		
		//TODO parent dir data
		MetaDataModel parentModel=getParentMetaDataModel();
		if(lastNode!=null && parentModel!=null){
			System.out.println("[DEBUG] READ MODEL OF "+lastNode.getAbsolutePath());
			try {
				parentModel.noticUserInput();
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		// is selection a file or directory
		if(file.equals("")){
			LOGGER.info("[GUI] select directory");
			//show dataview
			dataView=new MetaDataUI(customSettings);
			dataView.readData(importData);
			try {
				dataView.showData();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			metaPanel.addTab("",dataView);
			lastSelectionType=DIR;
			return;
		}else{
			lastSelectionType=FILE;
			dataView=new MetaDataUI(customSettings);
			dataView.readData(importData);
			
			addParentModel(parentModel,dataView);
			System.out.println("[DEBUG]--- ADD METADATA FROM FILE");
		
			try {
				loadFileMetaData(file, dataView,parentModel,importData);
			} catch (FormatException e) {
				LOGGER.warning("MY Unknown file format "+file);
			}catch(IOException e){
				LOGGER.warning("Can't read/access "+file);
			}
		}
		
	}


	private void addParentModel(MetaDataModel parentModel,MetaDataUI view) 
	{
		try {
			if(parentModel!=null && parentModel.noticUserInput()){ 
				System.out.println("[DEBUG]--- ADD MODEL OF "+lastNode.getAbsolutePath());
				view.addData(parentModel);
			}else{
				System.out.println("[DEBUG]--- NO PARENT MODEL");
			}
		} catch (Exception e) {
			LOGGER.warning("Can't add metadata from parent");
			e.printStackTrace();
		}
	}


	private MetaDataModel getParentMetaDataModel() 
	{
		FNode node = (FNode)fileTree.getLastSelectedPathComponent();
		if(node!=null){
			FNode parent=(FNode) node.getParent();
			if(parent!=null){
				return parent.getModel();
			}
		}
		return null;
	}
	
	private void loadFileMetaData(String file, MetaDataUI metaUI, MetaDataModel parent,ImportUserData userdata) 
			throws UnknownFormatException, FormatException,IOException
	{
		Cursor cursor=getCursor();
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		
		ImageReader reader = new ImageReader();
		LOGGER.info("### read "+ file+" ###");
		try {
			//record metadata to ome-xml format
			ServiceFactory factory=new ServiceFactory();
			OMEXMLService service = factory.getInstance(OMEXMLService.class);
			IMetadata metadata =  service.createOMEXMLMetadata();
			reader.setMetadataStore(metadata);
			
			try{
			reader.setId(file);
			}catch(Exception e){
				LOGGER.severe("Error read file");
				setCursor(cursor);
				return;
			}
			
			LOGGER.info("use READER: "+reader.getReader().getClass().getName());
			LOGGER.info("[DEBUG] Link data to file "+file);
			metaUI.linkToFile(new File(file));
			
			//TODO: automatische auswahl mic bzgl format
			if(reader.getSeriesCount()<2){
				LOGGER.info("no serie ");
				metaUI.readData(metadata, 0);
				metaUI.showData();
				metaPanel.addTab(metadata.getImageName(0),(Component) metaUI);
			}else{
				for(int j=0; j< reader.getSeriesCount(); j++){
					LOGGER.info("[SERIE] ------------ read SERIE "+j+" of "+reader.getSeriesCount()+
							": "+metadata.getImageName(j)+"---------------------" );
					reader.setSeries(j);
					//new metaUI tab
					metaUI=new MetaDataUI(customSettings);
					metaUI.linkToFile(new File(file));
					metaUI.readData(userdata);
					addParentModel(parent, metaUI);
					metaUI.readData(metadata, j);
					metaUI.showData();
					metaPanel.addTab("#"+j+": "+metadata.getImageName(j),(Component) metaUI);

				}
			}
		} catch (Exception e){
			// TODO Auto-generated catch block
			e.printStackTrace();
			setCursor(cursor);
		}
		
		setCursor(cursor);
	}
	

	
	private String getSelectedFile()
	{
		String fname="";
		FNode node = (FNode)fileTree.getLastSelectedPathComponent();
		if (node == null) return fname;

		if (node.isLeaf()) {
			fname=node.getAbsolutePath();
		} 
		return fname;
	}
	
	private ImportUserData getImportData()
	{
		ImportUserData data=null;
		try{
			FNode node = (FNode)fileTree.getLastSelectedPathComponent();

			if(node == null) return null;

			while(!node.hasImportData()){
				node=(FNode) node.getParent();
			}
			data=node.getImportData();
		}catch(Exception e){
			LOGGER.warning("No import data available");
			return null;
		}
		return data;
	}

	
	
	public void refreshFileView(List<ImportableFile> files)
	{
		createNodes(files);
		System.out.println("######################################");
	}
	
	
	/**
	 * Shows the metaDataChooser dialog.
	 * 
	 * @return The option selected.
	 */
	public int showDialog() {
		UIUtilities.setLocationRelativeToAndShow(getParent(), this);
		return 0;//option;
	}

	/**
	 * Shows the metaDataChooser dialog.
	 * 
	 * @return The option selected.
	 */
	public int centerDialog() {
		UIUtilities.centerAndShow(this);
		return 0;//option;
	}
	
	/**
	 * Reacts to property fired by the table.
	 * 
	 * @see PropertyChangeListener#propertyChange(PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		String name = evt.getPropertyName();
		
	}

	/**
	 * Cancels or imports the files.
	 * 
	 * @see ActionListener#actionPerformed(ActionEvent)
	 */
	public void actionPerformed(ActionEvent evt) {
		int commandId = Integer.parseInt(evt.getActionCommand());

		switch (commandId) {
		case CMD_CLOSE:
			firePropertyChange(CANCEL_SELECTION_PROPERTY,
					Boolean.valueOf(false), Boolean.valueOf(true));
			break;
		case CMD_IMPORT: // call importFiles function of chooser
			Component c=owner.getComponent(0);
			if(c instanceof ImportDialog)
				((ImportDialog)c).importFiles();
			break;
		case CMD_REFRESH: 
		case LOAD_MIC_SETTINGS: 
			JComboBox cb = (JComboBox)evt.getSource();
	        String petName = (String)cb.getSelectedItem();
	        LOGGER.info("\n Load mic settings for "+petName);
//	        customSettings=new CustomViewProperties(petName);
//	        dataView=new MicroscopeDataView(sett);
	        loadAndShowDataForSelection();
			revalidate();
			repaint();
	        
	        break;
		}
		

	}

	@Override
	public void valueChanged(TreeSelectionEvent e) 
	{
		String fname=getSelectedFile();
		if (fname.equals("")) {
			loadAndShowDataForSelection();
		}else{
			loadAndShowDataForSelection();
		}
		// notice last selection for save user input as model
		lastNode=(FNode)fileTree.getLastSelectedPathComponent();
		revalidate();
		repaint();
		
	}


	@Override
	public void treeCollapsed(TreeExpansionEvent arg0) 
	{
//		System.out.println("[DEBUG] tree collapsed");
		
	}


	@Override
	public void treeExpanded(TreeExpansionEvent arg0) 
	{
//		System.out.println("[DEBUG] tree expand"+	
//		fileTree.getLastSelectedPathComponent());
	} 


	
	

}
