package org.openmicroscopy.shoola.agents.fsimporter.metaChooser;

import info.clearthought.layout.TableLayout;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
//import javax.swing.filechooser.FileFilter;

import javax.swing.filechooser.FileFilter;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.formats.FormatException;
import loci.formats.ImageReader;
import loci.formats.UnknownFormatException;
import loci.formats.meta.IMetadata;
import loci.formats.services.OMEXMLService;
import ome.xml.meta.OMEXMLMetadataRoot;
import ome.xml.model.Experimenter;
import ome.xml.model.Project;
import omero.gateway.model.ExperimenterData;
import omero.gateway.model.ProjectData;
import omero.gateway.model.ScreenData;

import org.apache.commons.io.FilenameUtils;
import org.openmicroscopy.shoola.agents.fsimporter.ImporterAgent;
import org.openmicroscopy.shoola.agents.fsimporter.actions.ImporterAction;
import org.openmicroscopy.shoola.agents.fsimporter.chooser.ImportDialog;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.MetaDataModel;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.UOSHardwareReader;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.UOSProfileReader;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.microscope.CustomViewProperties;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.microscope.MetaDataUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.microscope.MetaDataView;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.ExceptionDialog;
import org.openmicroscopy.shoola.agents.fsimporter.view.Importer;
import org.openmicroscopy.shoola.env.LookupNames;
import org.openmicroscopy.shoola.env.data.model.FileObject;
import org.openmicroscopy.shoola.env.data.model.ImportableFile;
import org.openmicroscopy.shoola.util.ui.ClosableTabbedPaneComponent;
import org.openmicroscopy.shoola.util.ui.UIUtilities;
import org.slf4j.LoggerFactory;


/**
 * Dialog used to control and specify metadata for files to import
 * @author kunis
 * @version 1.0
 */
public class MetaDataDialog extends ClosableTabbedPaneComponent
	implements ActionListener, PropertyChangeListener, TreeSelectionListener, TreeExpansionListener, ListSelectionListener
{
	
	 /** Logger for this class. */
//    private static Logger LOGGER = Logger.getLogger(UOSMetadataLogger.class.getName());
	 private static final org.slf4j.Logger LOGGER =
	    	    LoggerFactory.getLogger(MetaDataDialog.class);
    
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
	
	/** load another profile xml */
	private JButton loadProfileButton;

	/** load another hardware specification */
	private JButton loadHardwareSpecButton;

	/** reset metadata to data from image file*/
	private JButton resetFileDataButton;

	/** save data for current image*/
	private JButton saveDataButton;

	/** save data for all image files in selected directory*/
	private JButton saveAllDataButton;
	
	
	private JToggleButton viewFileDataButton;
	private JToggleButton viewDirDataButton;
	
	/** Test text area*/
	public JTextArea textArea; 
	
	private JTree fileTree;
	private JList seriesList;
	
	private JPanel metaPanel;
//	private MetaDataUI dataView;
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
	
	private String micName;
	
	private String[] channelList;


	private boolean holdData;


	private FileFilter fileFilter;

	private static final String VIEWFILE ="View File Data";
	private static final String VIEWDIR ="View File and Dir Data";

	
	
	/** Bound property indicating that the cancel button is pressed. */
	public static final String CANCEL_SELECTION_PROPERTY = "cancelSelection";
	/** Action id indicating to import the selected files. */
	private static final int CMD_IMPORT = 1;
	/** Action id indicating to close the dialog. */
	private static final int CMD_CLOSE = 2;
	/** Action id indicating to reset the names. */
	private static final int CMD_REFRESH = 3;
	private static final int LOAD_MIC_SETTINGS=4;

	private static final int CMD_SAVEALL = 5;

	private static final int CMD_RESET = 6;

	private static final int CMD_SAVE = 7;

	private static final int CMD_SPECIFICATION = 8;

	private static final int CMD_PROFILE = 9;
	
	private static final int CMD_VIEWFILE=10;
	private static final int CMD_VIEWDIR=11;
	
	
	
	
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
//		UOSMetadataLogger.init();
		
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
//			prop="[Group: "+fileObj.getGroup().getName()+", Project: "+
//			fileObj.getParent().asProject().getName().getValue()+"]";
			data = new ImportUserData(fileObj.getGroup(), fileObj.getParent(), fileObj.getUser());
		}else{
//			copyParentOME(f.getParentName());
		}
		
		if(f.isDirectory()){

			dir=new FNode(new File(f.getAbsolutePath()),data,null);
			parent.add(dir);
			LOGGER.info("[TREE] Append Dir "+f.getAbsolutePath());
			File[] files=(new File(f.getAbsolutePath()).listFiles((java.io.FileFilter)fileFilter));

			if(files != null && files.length>0){
				for(int i=0; i<files.length;i++){
					addNode(dir,new FileObject(files[i]),null);
				}
			}

			//add files, attention: only image files?
		}else{
			try {
				file=new FNode(new File(f.getAbsolutePath()),data,fileObj);
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
		if(fileObj.isFolderAsContainer() && fileObj.getParent() instanceof ProjectData){
			Project p=new Project();//==import project
			p.setName(fileObj.getParent().asProject().getName().getValue());
			p.setDescription(fileObj.getParent().asProject().getDescription().toString());
			ome.addProject(p);
		}else{
			//screen import object
		}
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
		
		if(!holdData){
			root.removeAllChildren();

			ImportableFile f;
			Iterator<ImportableFile> j=files.iterator();

			while (j.hasNext()) {
				f = j.next();
				addNode(root,f.getFile(),f);
				
				String name="";
				if(f.getParent() instanceof ProjectData)
					name=f.getParent().asProject().getName().getValue();
				else if(f.getParent() instanceof ScreenData)
					name=f.getParent().asScreen().getName().getValue();
				
				LOGGER.info("BUILD FILE TREE: add "+f.getFile().getAbsolutePath()+
						"[group: "+f.getGroup().getName()+", project: "+
						name+"]");
			}
			treeModel.reload();
		}else{
			TreePath path=fileTree.getSelectionPath();
			
			FNode node = (FNode)fileTree.getLastSelectedPathComponent();
			
			String dirName=node.getFile().getName();
			FNode dirNode=node;
			if(node!=null && node.isLeaf()){
				LOGGER.info("[DEBUG] node is Leaf");
				dirName=node.getFile().getParentFile().getName();
				dirNode=(FNode) node.getParent();
			}
			
			insertNodes(files,dirName,dirNode);
			fileTree.updateUI();
			fileTree.expandPath(path);
			
		}
	}



	/**
	 * Reload node. If node is a directory, load all files of the directory, if is importable file. 
	 * If node==null or node.getFile()==null -> The import queue holds single files. Import only files from list.
	 * @param files
	 * @param dirName
	 * @param dir
	 */
	private void insertNodes(List<ImportableFile> files,String dirName,FNode node) 
	{
		if(node==null || node.getFile()==null){
			node=(FNode)fileTree.getModel().getRoot();
			LOGGER.info("[DEBUG] select root as dir");
			ImportableFile f;
			Iterator<ImportableFile> j=files.iterator();
			node.removeAllChildren();
			while (j.hasNext()) {
				f = j.next();
				LOGGER.info("[DEBUG] insert only file");
				// single file in the importQueue, only insert this and their ome file
				addNode(node,f.getFile(),f);
			}
		}else{
			LOGGER.info("[DEBUG] update node "+dirName);
			node.removeAllChildren();
			File[] fileList=(new File(node.getFile().getAbsolutePath())).listFiles((java.io.FileFilter)fileFilter);

			if(fileList != null && fileList.length>0){
				for(int i=0; i<fileList.length;i++){
					addNode(node,new FileObject(fileList[i]),null);
				}
			}
		
		}
	}



	private void initComponents(FileFilter[] filters,
	        ImporterAction importerAction)
	{
		holdData=false;
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
	    
	    loadProfileButton=new JButton("Load Profile");
	    loadProfileButton.setBackground(UIUtilities.BACKGROUND);
	    loadProfileButton.setToolTipText("Load another profile file ");
	    loadProfileButton.setActionCommand("" + CMD_PROFILE);
	    loadProfileButton.addActionListener(this);
	    loadProfileButton.setEnabled(false);
	    
	    loadHardwareSpecButton=new JButton("Load Specification");
	    loadHardwareSpecButton.setBackground(UIUtilities.BACKGROUND);
	    loadHardwareSpecButton.setToolTipText("Load another microscope hardware specification");
	    loadHardwareSpecButton.setActionCommand("" + CMD_SPECIFICATION);
	    loadHardwareSpecButton.addActionListener(this);
	    loadHardwareSpecButton.setEnabled(false);
	    
	    resetFileDataButton=new JButton("Reset/Clear");
	    resetFileDataButton.setBackground(UIUtilities.BACKGROUND);
	    resetFileDataButton.setToolTipText("Reset metadata. Show only metadata of selected image file.");
	    resetFileDataButton.setActionCommand("" + CMD_RESET);
	    resetFileDataButton.addActionListener(this);
	    
	    saveDataButton=new JButton("Save To File");
	    saveDataButton.setBackground(UIUtilities.BACKGROUND);
	    saveDataButton.setToolTipText("Save selected image metadata to separate *.ome file with image name");
	    saveDataButton.setActionCommand("" + CMD_SAVE);
	    saveDataButton.addActionListener(this);
	    
	    saveAllDataButton=new JButton("Save All");
	    saveAllDataButton.setBackground(UIUtilities.BACKGROUND);
	    saveAllDataButton.setToolTipText("Save metadata of all images of selected directory.");
	    saveAllDataButton.setActionCommand("" + CMD_SAVEALL);
	    saveAllDataButton.addActionListener(this);
	    
	    viewFileDataButton=new JToggleButton("File Data",false);
	    viewFileDataButton.setBackground(UIUtilities.BACKGROUND);
	    viewFileDataButton.setName(VIEWFILE);
	    viewFileDataButton.setActionCommand("" + CMD_VIEWFILE);
	    viewFileDataButton.addActionListener(this);
	    viewFileDataButton.setEnabled(false);
	    
	    viewDirDataButton=new JToggleButton("File + Directory Data",true);
	    viewDirDataButton.setBackground(UIUtilities.BACKGROUND);
	    viewDirDataButton.setName(VIEWDIR);
	    viewDirDataButton.setActionCommand("" + CMD_VIEWFILE);
	    viewDirDataButton.addActionListener(this);
	    viewDirDataButton.setEnabled(false);
	   
	    
	    ButtonGroup bg = new ButtonGroup();
	    bg.add(viewDirDataButton);
	    bg.add(viewFileDataButton);
	    
	    UOSProfileReader propReader=new UOSProfileReader(new File("profileUOSImporter.xml"));
	    UOSHardwareReader hardwareDef=new UOSHardwareReader(new File("hardwareUOSImporter.xml"));
//	    dataView=new MicroscopeDataView(propReader.getViewProperties());
	    customSettings=propReader.getViewProperties();
	    
	    customSettings.setMicObjList(hardwareDef.getObjectives());
	    customSettings.setMicDetectorList(hardwareDef.getDetectors());
	    customSettings.setMicLightSrcList(hardwareDef.getLightSources());
	    customSettings.setMicLightPathFilterList(hardwareDef.getLightPathFilters());
	    
	    
	    micName=customSettings.getMicName();
//	    dataView=new MetaDataUI(customSettings);
	    MetaDataView view=new MetaDataView();
		
		metaPanel=new JPanel(new BorderLayout());
		metaPanel.add(view,BorderLayout.CENTER);
		
//		File root=new File(System.getProperty("user.home"));
//		FNode rootNode=new FNode(root);
		FNode rootNode=new FNode("ImportQueue");
		//Create a tree that allows one selection at a time
		fileTree = new JTree(rootNode);
		fileTree.setRootVisible(true);
		fileTree.setShowsRootHandles(true);
		fileTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

		//Listen for when the selection changes.
		fileTree.addTreeSelectionListener(this);
		fileTree.addTreeExpansionListener(this);
		
		seriesList = new JList();
        seriesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        seriesList.setSelectedIndex(0);
        seriesList.addListSelectionListener(this);
        seriesList.setVisibleRowCount(5);
		
		lastSelectionType=DIR;
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
	/**
	 * Builds and lays out file view right side and the seriesbrowser
	 * @return JPanel
	 */
	private JPanel buildFileViewExtended()
	{
		 //Create the list and put it in a scroll pane.
        JPanel pane=new JPanel();
        pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
        
        
		JScrollPane listScrollPane=new JScrollPane(seriesList);
		pane.add(new JLabel("Series:"));
		pane.add(listScrollPane);
		
		JSplitPane splitPane;		
		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,buildFileView(),pane);
		splitPane.setResizeWeight(0.5);
		splitPane.setDividerLocation(200);
		JPanel fileView=new JPanel();
		fileView.setLayout(new BorderLayout(0,0));
		
		fileView.add(splitPane);
		
		return fileView;
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
	
	private JPanel buildToolBarMetaUI()
	{
		JPanel bar = new JPanel();
		bar.setLayout(new BoxLayout(bar, BoxLayout.X_AXIS));
		
		JPanel barL=new JPanel(new FlowLayout(FlowLayout.LEFT));
		barL.add(Box.createHorizontalStrut(5));
		//refresh
		int plugin = ImporterAgent.runAsPlugin();
		if (!(plugin == LookupNames.IMAGE_J_IMPORT ||
				plugin == LookupNames.IMAGE_J)) {

			barL.add(refreshFilesButton);
		}
		//load profile
		barL.add(Box.createHorizontalStrut(10));
		barL.add(loadProfileButton);
		barL.add(Box.createHorizontalStrut(5));
		//load Hardware specification
		barL.add(loadHardwareSpecButton);
		barL.add(Box.createHorizontalStrut(10));
		
		JPanel barM=new JPanel(new FlowLayout(FlowLayout.LEFT));
		barM.add(new JLabel("View: "));
		barM.add(Box.createHorizontalStrut(5));
		barM.add(viewFileDataButton);
		barM.add(Box.createHorizontalStrut(5));
		barM.add(viewDirDataButton);
		barM.add(Box.createHorizontalStrut(10));
		
		JPanel barR = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		//reset
		barR.add(resetFileDataButton);
		barR.add(Box.createHorizontalStrut(5));
		//save
		barR.add(saveDataButton);
		barR.add(Box.createHorizontalStrut(5));
		//save all
		barR.add(saveAllDataButton);
		barR.add(Box.createHorizontalStrut(10));
		
		bar.add(barL);
		bar.add(barM);
		bar.add(barR);
		return bar;
	}
	

	private void buildGUI()
	{
		setLayout(new BorderLayout(0,0));
		JSplitPane splitPane;		
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,buildFileViewExtended(),metaPanel);
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
//		bar.add(buildToolBarLeft());
//		bar.add(buildToolBarRight());
		bar.add(buildToolBarMetaUI());
		controls.add(new JSeparator());
		controls.add(bar);
		add(controls, BorderLayout.SOUTH);
		
//		micList.setSelectedIndex(0);
	}
	public String getMicName() {
		return micName;
	}


	public void setMicName(String micName) {
		this.micName = micName;
	}


	private void loadAndShowDataForSelection()
	{
		if(fileTree.getLastSelectedPathComponent()==null || getSelectedFile()==null)
			return;
		
		
		LOGGER.info("[TREE] -- Node: "+((FNode)fileTree.getLastSelectedPathComponent()).toString()+" ##############################################");
		
		saveModel();

		JComponent panel=null;
		
		String file = getSelectedFile(); 
		
		//import user data
		ImportUserData importData = getImportData();
		
		//get parent dir model data
		MetaDataModel parentModel=getParentMetaDataModel();
		if(parentModel!=null)
			viewDirDataButton.setSelected(true);
		else
			viewFileDataButton.setSelected(true);
		
		MetaDataView view=null;
		// is selection a file or directory
		if(file.equals("")){
			lastSelectionType=DIR;
			
			MetaDataModel dirModel=getCurrentSelectionMetaDataModel();
			try {
				view = new MetaDataView(customSettings, file, importData, parentModel, dirModel);
				view.setVisible();
			} catch (Exception e) {
//				catch (DependencyException | ServiceException e) {
				LOGGER.error("[DATA] CAN'T read METADATA");
				ExceptionDialog ld = new ExceptionDialog("Metadata Error!", 
						"Can't read given metadata of "+file,e);
				ld.setVisible(true);
			}
		}else{
			lastSelectionType=FILE;
			
			try {
				view = new MetaDataView(customSettings, file, importData, parentModel,this);
				view.setVisible();
			} catch (Exception e) {
//				catch (DependencyException | ServiceException e) {
				LOGGER.error("[DATA] CAN'T read METADATA");
				ExceptionDialog ld = new ExceptionDialog("Metadata Error!", 
						"Can't read given metadata of "+file,e);
				ld.setVisible(true);
			}
		}
		panel=view;
		// notice last selection for save user input as model
		lastNode=(FNode)fileTree.getLastSelectedPathComponent();
		
		metaPanel.removeAll();
		if(panel!=null){
			metaPanel.add(panel,BorderLayout.CENTER);
			DefaultListModel list=view.getSeries();
			if(list!=null){
				seriesList.setModel(list);
				seriesList.setSelectedIndex(0);
			}else{
				seriesList.setModel(new DefaultListModel());
			}
		}
		revalidate();
		repaint();
	}
	
	
	/**
	 * TODO: no series image are saved
	 * Load data for given node: importData, parentData,fileData
	 * @param node
	 */
	private MetaDataView loadData(FNode node)
	{
		//import user data
		ImportUserData importData = getImportData();
		
		//set parent dir data
		MetaDataModel parentModel=lastNode.getModel(0);
		if(lastNode!=null && parentModel!=null){
			LOGGER.info("[DEBUG] -- READ MODEL OF "+lastNode.getAbsolutePath());
			try {
				parentModel.noticUserInput();
				
			} catch (Exception e) {
				LOGGER.error("can't read model of "+lastNode.getAbsolutePath());
				ExceptionDialog ld = new ExceptionDialog("Metadata Error!", 
						"Can't read model of "+lastNode.getAbsolutePath(),e);
				ld.setVisible(true);
			}
		}
		//set current dir data
		MetaDataModel dirModel=getCurrentSelectionMetaDataModel();
//
//		dataView=new MetaDataUI(customSettings);
//		dataView.readData(importData);
//
//		addParentModel(parentModel,dataView);
//
//		LOGGER.info("[DEBUG]--- ADD METADATA FROM FILE");
//
//		try {
//			loadFileMetaData(node.getAbsolutePath(), dataView,parentModel,importData);
//		} catch (FormatException e) {
//			LOGGER.warning("MY Unknown file format "+node.getAbsolutePath());
//		}catch(IOException e){
//			LOGGER.warning("Can't read/access "+node.getAbsolutePath());
//		}
		MetaDataView dataView=null;
		try {
			dataView=new MetaDataView(customSettings, node.getAbsolutePath(), importData, parentModel,this);
		} catch (Exception e) {
//			catch (DependencyException | ServiceException e) {
			LOGGER.error("[DATA] CAN'T read METADATA");
			ExceptionDialog ld = new ExceptionDialog("Metadata Error!", 
					"Can't read given metadata of "+node.getAbsolutePath(),e);
			ld.setVisible(true);
			return null;
		}
		return dataView;
	}


	private void saveModel() 
	{
		if(lastNode!=null){
			LOGGER.info("[DEBUG] -- SAVE MODEL FOR: "+lastNode.getAbsolutePath());
			LOGGER.info("[DEBUG] components metaPanel: "+metaPanel.getComponentCount());
			if(metaPanel.getComponentCount()>0){
				Component c=metaPanel.getComponent(0);
				if(c instanceof MetaDataView)
					lastNode.setModelObject(((MetaDataView) c).getModelObject());
				else
					LOGGER.info("[DEBUG] metaPanel Component class: "+metaPanel.getComponent(0).getClass());
				//TODO: save to file if there are some changes
				//			if(dataView.getModel().noticUserInput())
				//			{
				//				
				//			}
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
		}
	}


	private void addParentModel(MetaDataModel myModel,MetaDataUI view) 
	{
		
		try {
			if(myModel!=null && lastNode!=null){ 
				LOGGER.info("[DEBUG]--- Add data of parent model "+lastNode.toString());
				if(myModel.noticUserInput()){
					view.addData(myModel);
				}else{
					LOGGER.info("[DEBUG]--- No parent model: No user input");
				}
			}else{
				LOGGER.info("[DEBUG]--- No parent model ");
			}
		} catch (Exception e) {
			LOGGER.warn("[DATA] -- Can't add metadata from parent model");
			e.printStackTrace();
		}
	}


	private MetaDataModel getParentMetaDataModel() 
	{
		FNode node = (FNode)fileTree.getLastSelectedPathComponent();
		if(node!=null){
			FNode parent=(FNode) node.getParent();
			if(parent!=null){
				return parent.getModel(0);
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @return model of current selected directory, if exist's. If selection is a file, return null.
	 */
	private MetaDataModel getCurrentSelectionMetaDataModel()
	{
		FNode node = (FNode)fileTree.getLastSelectedPathComponent();
		if(node!=null && !node.isLeaf()){
			LOGGER.info("[GUI] -- Load current selection model");
			return node.getModel(0);
		}else{
			LOGGER.info("[GUI] -- No model for current selection");
		}
			
		return null;
	}
	
//	private JComponent loadFileMetaData(String file, MetaDataUI metaUI, MetaDataModel parent,ImportUserData userdata) 
//			throws UnknownFormatException, FormatException,IOException
//	{
//		JComponent panel=null;
//		
//		ImageReader reader = new ImageReader();
//		LOGGER.info("### read "+ file+" ###");
//		try {
//			IMetadata metadata = readMetadataFromFile(file, reader);
//			if(metadata==null) return null;
//			
//			LOGGER.info("[DEBUG] -- Link data to file "+file);
//			metaUI.linkToFile(new File(file));
//			
//			if(reader.getSeriesCount()<2){
//				LOGGER.info("no serie ");
//				metaUI.readData(metadata, 0);
//				metaUI.showData();
//				panel=metaUI;
//			}else{
//				JTabbedPane panelObj=new JTabbedPane();
//				for(int j=0; j< reader.getSeriesCount(); j++){
//					LOGGER.info("[SERIE] ------------ read SERIE "+j+" of "+reader.getSeriesCount()+
//							": "+metadata.getImageName(j)+"---------------------" );
//					reader.setSeries(j);
//					//new metaUI tab
//					metaUI=new MetaDataUI(customSettings);
//					metaUI.linkToFile(new File(file));
//					metaUI.readData(userdata);
//					addParentModel(parent, metaUI);
//					metaUI.readData(metadata, j);
//					metaUI.showData();
//					((JTabbedPane) panelObj).addTab("#"+j+": "+metadata.getImageName(j),(Component) metaUI);
//
//				}
//				panel=panelObj;
//			}
//		} catch (Exception e){
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return panel;
//	}
//	
//	private JComponent loadFileMetaData(String file, MetaDataUI metaUI) 
//			throws UnknownFormatException, FormatException,IOException
//	{
//		
//		JComponent panel=null;
//		ImageReader reader = new ImageReader();
//		LOGGER.info("### read "+ file+" ###");
//		try {
//			IMetadata metadata = readMetadataFromFile(file, reader);
//			if(metadata==null) return null;
//			
//			LOGGER.info("[DEBUG] -- Link data to file "+file);
//			metaUI.linkToFile(new File(file));
//			
//			if(reader.getSeriesCount()<2){
//				LOGGER.info("no serie ");
//				metaUI.readData(metadata, 0);
//				metaUI.showData();
//				panel= metaUI;
//			}else{
//				JTabbedPane panelObj=new JTabbedPane();
//				for(int j=0; j< reader.getSeriesCount(); j++){
//					LOGGER.info("[SERIE] ------------ read SERIE "+j+" of "+reader.getSeriesCount()+
//							": "+metadata.getImageName(j)+"---------------------" );
//					reader.setSeries(j);
//					//new metaUI tab
//					metaUI=new MetaDataUI(customSettings);
//					metaUI.linkToFile(new File(file));
//					metaUI.readData(metadata, j);
//					metaUI.showData();
//					((JTabbedPane) panelObj).addTab("#"+j+": "+metadata.getImageName(j),(Component) metaUI);
//				}
//				panel=panelObj;
//			}
//		} catch (Exception e){
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return panel;
//	}


//	private IMetadata readMetadataFromFile(String file, 
//			ImageReader reader) throws DependencyException, ServiceException 
//	{
//		Cursor cursor=getCursor();
//		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
//		
//		//record metadata to ome-xml format
//		ServiceFactory factory=new ServiceFactory();
//		OMEXMLService service = factory.getInstance(OMEXMLService.class);
//		IMetadata metadata =  service.createOMEXMLMetadata();
//		reader.setMetadataStore(metadata);
//		
//		try{
//		reader.setId(file);
//		}catch(Exception e){
//			LOGGER.severe("Error read file");
//			ExceptionDialog ld = new ExceptionDialog("Metadata Error!", 
//					"Can't read metadata of "+file,e);
//			ld.setVisible(true);
//			setCursor(cursor);
//			return null;
//		}
//		setCursor(cursor);
//		LOGGER.info("use READER: "+reader.getReader().getClass().getName());
//		return metadata;
//	}
//	
//	

	
	private String getSelectedFile()
	{
		String fname="";
		FNode node = (FNode)fileTree.getLastSelectedPathComponent();

		if (node!=null && node.isLeaf()) {
			fname=node.getAbsolutePath();
		} 
		return fname;
	}
	
	private ImportableFile getSelectedImportableFile()
	{
		ImportableFile f=null;
		FNode node = (FNode)fileTree.getLastSelectedPathComponent();

		if (node!=null && node.isLeaf()) {
			f=node.getImportableFile();
		} 
		return f;
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
			LOGGER.warn("No import data available");
			return null;
		}
		return data;
	}

	
	
	public void refreshFileView(List<ImportableFile> files, FileFilter fileFilter)
	{
		this.fileFilter=fileFilter;
		createNodes(files);
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
		LOGGER.info("[DEBUG] MetaDataDialog notice propertyChange "+name);
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
			LOGGER.info("[GUI-ACTION] -- close");
			firePropertyChange(CANCEL_SELECTION_PROPERTY,
					Boolean.valueOf(false), Boolean.valueOf(true));
			break;
		case CMD_IMPORT: // call importFiles function of chooser
			LOGGER.info("[GUI-ACTION] -- import");
			Component c=owner.getComponent(0);
			if(c instanceof ImportDialog)
				((ImportDialog)c).importFiles();
			break;
		case CMD_REFRESH:
			LOGGER.info("[GUI-ACTION] -- refresh");
			//Schleife durch fileTree
			//dir-> refresh dir
			//file without a parent dir -> add *.ome
			
//			DefaultMutableTreeNode root=(DefaultMutableTreeNode) fileTree.getModel().getRoot();
//			Enumeration e = root.preorderEnumeration();
//		    while(e.hasMoreElements()){
//		    	 DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
//		         while (node.getParent() == null) {
//		        	
//		         }
//		    }
			firePropertyChange(ImportDialog.ADD_AND_REFRESH_FILE_LIST,null, null);
			
			break;
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
		case CMD_SAVE:
			LOGGER.info("[GUI-ACTION] -- save");
			TreePath path=fileTree.getSelectionPath();
			String fileName=getSelectedFile();
			if(fileName!=null){
				String srcFile=fileName.equals("") ? "" : fileName;
				saveCurrentNodeAndUpdate(path,srcFile);
			}
			break;
		case CMD_SAVEALL:
			LOGGER.info("[GUI-ACTION] -- save all");
			//only for directory
			FNode parentNode = (FNode)fileTree.getLastSelectedPathComponent();
			lastNode=parentNode;
			saveModel();
			Enumeration children =parentNode.children();
			while(children.hasMoreElements()){
				FNode node=(FNode)children.nextElement();
				//load all data and save
				if(node!=null && node.isLeaf()){
					MetaDataView view=loadData(node);
					saveMetadataForNode(node.getAbsolutePath(),view);
				}
			}
			insertNodes(null, parentNode.getFile().getName(), parentNode);
			fileTree.updateUI();
			break;
		case CMD_RESET:
			LOGGER.info("[GUI-ACTION] -- reset");
			
			JComponent panel=null;
			//TODO: profile default data eliminate
//			dataView=new MetaDataUI(customSettings);
//			String file = getSelectedFile(); 
//			if(!file.equals("")){
//				try {
//					panel=loadFileMetaData(file, dataView);
//				} catch (FormatException | IOException e) {
//					LOGGER.severe("[RESET] Can't load metadata from file.");
//					ExceptionDialog ld = new ExceptionDialog("Metadata Error!", 
//							"Can't load metadata of "+file,e);
//					ld.setVisible(true);
//				}
//			}else{
//				try {
//					dataView.showData();
//				} catch (Exception e) {
//					LOGGER.severe("[RESET] Can't reload view.");
//					ExceptionDialog ld = new ExceptionDialog("Metadata GUI Error!", 
//							"Can't show metadata of "+file,e);
//					ld.setVisible(true);
//				}
//				panel=dataView;
//			}
			//file
			String file = getSelectedFile(); 
			if(!file.equals("")){
				viewFileDataButton.setSelected(true);
				MetaDataView view=null;
				try {
					view = new MetaDataView(customSettings, file, null, null,this);
					
					view.setVisible();
				} catch (Exception e) {
//					catch (DependencyException | ServiceException e) {
					LOGGER.error("[DATA] CAN'T read METADATA");
					ExceptionDialog ld = new ExceptionDialog("Metadata Error!", 
							"Can't read given metadata of "+file,e);
					ld.setVisible(true);
				}
				
				metaPanel.removeAll();
				if(view!=null){
					metaPanel.add(view,BorderLayout.CENTER);
					viewFileDataButton.setSelected(true);	
					DefaultListModel list=view.getSeries();
					if(list!=null){
						seriesList.setModel(list);
						seriesList.setSelectedIndex(0);
					}else{
						seriesList.setModel(new DefaultListModel());
					}
				}
				
				revalidate();
				repaint();
			}
			
			
			break;
		case CMD_PROFILE:
			LOGGER.info("[GUI-ACTION] -- load profile file");
			break;
		case CMD_SPECIFICATION:
			LOGGER.info("[GUI-ACTION] -- load specification file");
			break;
		case CMD_VIEWFILE:
			Border redline = BorderFactory.createLineBorder(Color.red);
			Border compound= BorderFactory.createRaisedBevelBorder();
			if(((JToggleButton)evt.getSource()).getName().equals(VIEWFILE)){
//				viewFileDataButton.setBorder(BorderFactory.createCompoundBorder(redline, compound));
//				viewDirDataButton.setBorder(compound);
				viewFileDataButton.setBackground(Color.GREEN);
				viewDirDataButton.setBackground(Color.gray);
			}else{
				viewFileDataButton.setBackground(Color.gray);
				viewDirDataButton.setBackground(Color.GREEN);
				
//				viewFileDataButton.setBorder(compound);
//				viewDirDataButton.setBorder(BorderFactory.createCompoundBorder(redline, compound));
			}
				
			break;
		
		}
		
		

	}


	/**
	 * 
	 */
	private void saveCurrentNodeAndUpdate(TreePath path, String srcFile) 
	{
		saveCurrentNode(path, srcFile);
		//freeze status fileTree
		holdData=true;
		
		
		String fileName="";
		if(!srcFile.equals(""))
			fileName=FilenameUtils.removeExtension(srcFile)+".ome";
		
		File[] fileList={new File(srcFile),new File(fileName)};
		firePropertyChange(ImportDialog.ADD_AND_REFRESH_FILE_LIST,null, fileList);
		fileTree.expandPath(path);
		holdData=false;
	}
	
	
	private void saveCurrentNode(TreePath path, String srcFile)
	{
		LOGGER.info("[DEBUG] -- save node "+srcFile);
		
		((MetaDataView)metaPanel.getComponent(0)).save();
	}
	
	private void saveMetadataForNode(String srcFile,MetaDataView view)
	{
		LOGGER.info("[DEBUG] -- save node "+srcFile);
		view.save();
	}
	
	

	@Override
	public void valueChanged(TreeSelectionEvent e) 
	{
		FNode node = (FNode)fileTree.getLastSelectedPathComponent();
		
		if(node!=null ){
			if(node.isLeaf()){
				saveDataButton.setEnabled(true);
				saveAllDataButton.setEnabled(false);
			}else{
				saveDataButton.setEnabled(false);
				saveAllDataButton.setEnabled(true);
			}
			loadAndShowDataForSelection();
			
			revalidate();
			repaint();
		}
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


	@Override
	public void valueChanged(ListSelectionEvent e) 
	{
		 if (e.getValueIsAdjusting() == false) {
			 
	            if (seriesList.getSelectedIndex() != -1) {
	            	if(metaPanel.getComponentCount()>0){
	            		Component c=metaPanel.getComponent(0);
	            		if(c instanceof MetaDataView){
	            			((MetaDataView) c).showSeries((String)seriesList.getSelectedValue());
	            		}
	            	}
	            }
		 }
	} 


	
	

}
