package org.openmicroscopy.shoola.agents.fsimporter.metaChooser;

import info.clearthought.layout.TableLayout;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
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

import ome.xml.meta.OMEXMLMetadataRoot;
import ome.xml.model.Experimenter;
import ome.xml.model.Project;
import omero.gateway.model.ExperimenterData;
import omero.gateway.model.ProjectData;
import omero.gateway.model.ScreenData;

import org.apache.commons.io.FilenameUtils;
import org.openmicroscopy.shoola.agents.events.treeviewer.ShowProperties;
import org.openmicroscopy.shoola.agents.fsimporter.ImporterAgent;
import org.openmicroscopy.shoola.agents.fsimporter.actions.ImporterAction;
import org.openmicroscopy.shoola.agents.fsimporter.chooser.ImportDialog;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.MetaDataModel;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.UOSHardwareReader;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.UOSProfileReader;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.UOSProfileEditorUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.UOSSpecificationEditor;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.microscope.CustomViewProperties;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.microscope.MetaDataView;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.ExceptionDialog;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.WarningDialog;
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
    implements ActionListener, PropertyChangeListener, TreeSelectionListener, TreeExpansionListener, ListSelectionListener, ItemListener
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
    
    /** The type associated to the import.The type of dialog e.g. screen view. */
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
    
    
    private JCheckBox showFileData;
    private JCheckBox showDirData;
    private JCheckBox showCustomData;
    private boolean enabledPredefinedData;
//    private JCheckBox showHardwareData;
    
    /** Test text area*/
    public JTextArea textArea; 
    
    private JTree fileTree;
    private JList seriesList;
    
    private JPanel metaPanel;
//	private MetaDataUI dataView;
    private CustomViewProperties customSettings;
    private UOSHardwareReader hardwareDef;
    
    
    /** debug pane*/
    private JTextPane debugTextPane;
    private JTextPane bioFormatPane;
    
    /** Identifies the style of the document.*/
    private static final String STYLE = "StyleName";
    /** The maximum number of characters in the debug text.*/
    private static final int 	MAX_CHAR = 200000;
    
    /** lastSelection types*/
    public static final int DIR=0;
    public static final int FILE=1;
    
    /** type of last selection in tree */
    private int lastSelectionType;
    
    private FNode lastNode;
    
    private String micName;
    
    private String[] channelList;


    private boolean holdData;


    private FileFilter fileFilter;
    
private List<String> unreadableFileList;

private boolean disableItemListener;



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
    private static final int CMD_LOADPREVAL=12;

	public static final String CHANGE_CUSTOMSETT = "changesCustomSettings";
    
    
    
    
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
     *            
     * @param type TODO: necessary?
     *            The type of dialog e.g. screen view.
     * @param importerAction
     *            The cancel-all-imports action.
     */
    public MetaDataDialog(JFrame owner, FileFilter[] filters, int type,
            ImporterAction importerAction, Importer importer)
    {
        super(1, TITLE, TITLE);
        
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
        
        root.setModelObject(null);
        
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
        

        loadProfileButton=new JButton("Customize...");
        loadProfileButton.setBackground(UIUtilities.BACKGROUND);
        loadProfileButton.setToolTipText("Load/Save/Edit profile file to customize view.");
        loadProfileButton.setActionCommand("" + CMD_PROFILE);
        loadProfileButton.addActionListener(this);
//	    loadProfileButton.setEnabled(false);
        

        loadHardwareSpecButton=new JButton("Hardware...");
        loadHardwareSpecButton.setBackground(UIUtilities.BACKGROUND);
        loadHardwareSpecButton.setToolTipText("Load another microscope hardware specification");
        loadHardwareSpecButton.setActionCommand("" + CMD_SPECIFICATION);
        loadHardwareSpecButton.addActionListener(this);
      
//	    loadHardwareSpecButton.setEnabled(false);
        
        resetFileDataButton=new JButton("Clear Input");
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
        
     
        
        initFilterViewBar();
       
        
        UOSProfileReader propReader=new UOSProfileReader(new File("profileUOSImporter.xml"));

         hardwareDef=new UOSHardwareReader(new File("hardwareUOSImporter.xml"));
//	    dataView=new MicroscopeDataView(propReader.getViewProperties());
        customSettings=propReader.getViewProperties();
        if(customSettings==null)
            customSettings=propReader.getDefaultProperties();
        
        customSettings.setMicObjList(hardwareDef.getObjectives());
        customSettings.setMicDetectorList(hardwareDef.getDetectors());
        customSettings.setMicLightSrcList(hardwareDef.getLightSources());
        customSettings.setMicLightPathFilterList(hardwareDef.getLightPathFilters());
        
        
        micName=customSettings.getMicName();
//	    dataView=new MetaDataUI(customSettings);
        MetaDataView view=new MetaDataView();
        
        metaPanel=new JPanel(new BorderLayout());
        metaPanel.add(view,BorderLayout.CENTER);
        
      
        initFileTree();
        
        initSeriesList();
        
        lastSelectionType=DIR;
    }


	/**
	 * 
	 */
	public void initSeriesList() {
		seriesList = new JList();
        seriesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        seriesList.setSelectedIndex(0);
        seriesList.addListSelectionListener(this);
        seriesList.setVisibleRowCount(5);
	}


    /**
     * Create a tree that allows one selection at a time
     */
    public void initFileTree() 
    {
    	FNode rootNode=new FNode("ImportQueue");

    	fileTree = new JTree(rootNode);
    	fileTree.setRootVisible(true);
    	fileTree.setShowsRootHandles(true);
    	fileTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

    	//Listen for when the selection changes.
    	fileTree.addTreeSelectionListener(this);
    	fileTree.addTreeExpansionListener(this);
    }
    
    /**
     * Init GUI of filter view panel to filter which data should displayed
     */
    private void initFilterViewBar() 
    {
    	
    	showFileData=new JCheckBox("File Data");
    	showFileData.addItemListener(this);
    	 showDirData=new JCheckBox("Parent Data");
         showDirData.addItemListener(this);
         showCustomData=new JCheckBox("Predefined Data");
         showCustomData.addItemListener(this);
         enabledPredefinedData=true;
         
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
        loadHardwareSpecButton.setEnabled(false);
        barL.add(Box.createHorizontalStrut(10));
        
        JPanel barM = buildFilterViewBar();
        
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


	/**
	 * @return
	 */
	private JPanel buildFilterViewBar() 
	{
		JPanel barM=new JPanel(new FlowLayout(FlowLayout.LEFT));
        barM.add(new JLabel("View: "));
        barM.add(Box.createHorizontalStrut(5));
        barM.add(showFileData);
        barM.add(Box.createHorizontalStrut(5));
        barM.add(showDirData);
        barM.add(Box.createHorizontalStrut(5));
        barM.add(showCustomData);
//        showCustomData.setEnabled(false);
        barM.add(Box.createHorizontalStrut(10));
		return barM;
		
		
	}
    

	/**
	 * Init and layout gui components 
	 */
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
        bar.add(buildToolBarMetaUI());
        controls.add(new JSeparator());
        controls.add(bar);
        
        this.add(controls, BorderLayout.SOUTH);
    }
    
    /**
     * 
     * @return name of microscope of loaded hardware or predefined values.
     */
    public String getMicName() {
        return micName;
    }


    public void setMicName(String micName) {
        this.micName = micName;
    }

    /**
     * Generate MetaDataView for selected node and load predefined value, parent, import and model data
     * if there was selected in the bottom filter bar.
     * @param node selected node in the filetree
     * @param parentVisible =true shows also parent data
     * @param fileVisible = true shows also file data
     * @param predefinedVisible = true shows also predefine values
     */
    private void loadAndShowFilteredDataForSelection(FNode node,
    		boolean parentVisible,boolean fileVisible,boolean predefinedVisible)
    {
    	System.out.println("#MetaDataDialog::loadAndShowFilteredDataForSelection(): "
    			+ "parent="+parentVisible+", file="+fileVisible+", predefined="+predefinedVisible);
    	String file=null;
    	if(node==null || (file=getSelectedFilePath(node))==null)
    		return;

    	LOGGER.debug("[TREE] -- FILTER VIEW Node: "+node.toString()+" ##############################################");

    	//import user data
    	ImportUserData importData = getImportData();

    	//get parent dir model data 
    	MetaDataModel parentModel=null;
    	if(parentVisible)
    		parentModel=getParentMetaDataModel(node);

    	MetaDataView view=null;

    	// is selection a file or directory
    	if(file.equals("")){
    			view = loadAndShowDataForDirectory(node, file, importData,
    					parentModel, view, true, predefinedVisible);
    	}else{
    		try{
   				view = loadAndShowDataForFile(file, importData, parentModel, view, fileVisible, predefinedVisible);
    		}catch(Exception e){
    			LOGGER.error("[DATA] CAN'T read METADATA");
    			ExceptionDialog ld = new ExceptionDialog("Metadata Error!", 
    					"Can't read given metadata of "+file,e,
    					this.getClass().getSimpleName());
    			ld.setVisible(true);
    			fileTree.setSelectionPath(fileTree.getSelectionPath().getParentPath());
    			this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    			return;
    		}
    	}
    	showMetaDataView(view);
    	
    	setFilterView(view);
    	
    	revalidate();
    	repaint();
    }
    
    /**
     * Generate MetaDataView for selected node and load predefined value, parent, import and model data.
     * @param node selected node in the filetree
     */
    private void loadAndShowDataForSelection(FNode node)
    {
    	String file=null;
    	if(node==null || (file=getSelectedFilePath(node))==null)
    		return;

    	LOGGER.debug("[TREE] -- Node: "+node.toString()+" ##############################################");

    	//import user data
    	ImportUserData importData = getImportData();

    	//get parent dir model data 
    	MetaDataModel parentModel=getParentMetaDataModel(node);

    	// TODO: new View with available model
    	// if a view still available, load it 
    	MetaDataView view=node.getView();
    	// an update of parent data was happend?
    	boolean loadParentDataAtUpdate=false;
    	if(view!=null){
    		loadParentDataAtUpdate=view.parentDataAreLoaded();
    	}
    	// is selection a file or directory
    	if(file.equals("")){
    			view = loadAndShowDataForDirectory(node, file, importData,
    					parentModel, view, true, true && enabledPredefinedData);
    		
    			boolean pLoad=loadParentDataAtUpdate || view.parentDataAreLoaded();
    			System.out.println("# MetaDataView::setParentDataLoaded(): "+pLoad+" - of node "+node.getAbsolutePath());
    			view.setParentDataLoaded(pLoad);
    	}else{
    		try{
    			//if model still exists, it was still updated by parent data at deselectedNodeAction()
    			if(node.hasModelObject())
    				view = node.getView();
    			}else{
    				view = loadAndShowDataForFile(file, importData, parentModel, view, true, true && enabledPredefinedData);
    			}
    		}catch(Exception e){
    			LOGGER.error("[DATA] CAN'T read METADATA");
    			ExceptionDialog ld = new ExceptionDialog("Metadata Error!", 
    					"Can't read given metadata of "+file,e,
    					this.getClass().getSimpleName());
    			ld.setVisible(true);
    			fileTree.setSelectionPath(fileTree.getSelectionPath().getParentPath());
    			this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    			return;
    		}
    	}
//    	view.getModelObject().isUpToDate(true);
    	
    	showMetaDataView(view);
    	setFilterView(view);
    	
    	revalidate();
    	repaint();
    }


	/**
	 * Add MetaDataView to main panel and show list of series if exists.
	 * @param panel
	 * @param view
	 */
	public void showMetaDataView(MetaDataView view) {
		JComponent panel=null;
		panel=view;
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
	}

    /**
     * Set values of filter view bar according of data that will be set inside the given view.
     * While the values are set, the listener is disabled.
     * @param view
     */
	private void setFilterView(MetaDataView view) 
	{
		disableItemListener=true;
		showDirData.setSelected(view.parentDataAreLoaded());
		showFileData.setSelected(view.fileDataAreLoaded());
		showCustomData.setSelected(view.predefineDataLoaded());
		disableItemListener=false;
	}


	/**
	 * Generate MetaDataView for selected file and load predefined value, parent, import and model data.
	 * @param file
	 * @param importData
	 * @param parentModel
	 * @param view
	 * @param showFileData TODO
	 * @param showPreValues TODO
	 * @return
	 */
	public MetaDataView loadAndShowDataForFile(String file,
			ImportUserData importData, MetaDataModel parentModel,
			MetaDataView view, boolean showFileData, boolean showPreValues) throws Exception
	{
		lastSelectionType=FILE;
	
		System.out.println("# MetaDataDialog::loadAndShowDataForFile() : showFileData = "+showFileData+
				", showPreVal = "+showPreValues+", enablePreVal= "+enabledPredefinedData);
		
		String hasParentModel=parentModel==null ? "null" : "available";
		LOGGER.debug("load and show data for file: parentModel="+hasParentModel);
		
		    view = new MetaDataView(file, importData, parentModel, this, showFileData, showPreValues);
		    view.setVisible();
		return view;
	}


	/**
	 * Generate MetaDataView for selected directory and load predefined value, parent, import and model data.
	 * @param node
	 * @param file
	 * @param importData
	 * @param parentModel
	 * @param view
	 * @param useCurrentModel TODO
	 * @param showPreValues TODO
	 * @return
	 */
	public MetaDataView loadAndShowDataForDirectory(FNode node, String file,
			ImportUserData importData, MetaDataModel parentModel,
			MetaDataView view, boolean useCurrentModel, boolean showPreValues) 
	{
		lastSelectionType=DIR;
		
		System.out.println("# MetaDataDialog::loadAndShowDataForDir() : "+
				", showPreVal = "+showPreValues+", enablePreVal= "+enabledPredefinedData);
		
		MetaDataModel currentDirModel=null;
		if(useCurrentModel)
			currentDirModel=getCurrentSelectionMetaDataModel(node);
		
		String hasParentModel=parentModel==null ? "null" : "available";
		String hasCurrentModel=currentDirModel==null ? "null" : "available";
		LOGGER.debug("load and show data for directory: parentModel="+hasParentModel
				+", current model = "+hasCurrentModel);
		
		try {
		    view = new MetaDataView(file, importData, parentModel, currentDirModel,this, showPreValues);
		    view.setVisible();
		} catch (Exception e) {
//				catch (DependencyException | ServiceException e) {
		    LOGGER.error("[DATA] CAN'T read METADATA");
		    ExceptionDialog ld = new ExceptionDialog("Metadata Error!", 
		            "Can't read given metadata of "+file,e,this.getClass().getSimpleName());
		    ld.setVisible(true);
		}
		return view;
	}
	
	
	

	/**
	 * Call routines after deselect a node.
	 * This routines are: save view, save input to model and update childs of the node, notice deselected node.
	 */
	public void deselectNodeAction(FNode node) {
		
		if(node!=null){
			System.out.println("# MetaDataDialog::deselectNodeAction("+node.getAbsolutePath()+")");
			LOGGER.debug("Deselect node action for "+node.getAbsolutePath());
			node.setView(getMetaDataView(metaPanel));
        	saveInputToModel(node);
        	lastNode=node;
        	seriesList.setModel(new DefaultListModel());
        }
	}
    
	/**
	 * 
	 * @param panel
	 * @return active MetaDataView component from metapanel
	 */
    private MetaDataView getMetaDataView(JPanel panel)
    {
    	 if(panel.getComponentCount()>0){
             Component c=panel.getComponent(0);
             if(c instanceof MetaDataView){
            	 return (MetaDataView) c;
             }
    	 }
    	 return null;
    }
    
    /**
     * TODO: no series image are saved
     * Load data for given node: importData, parentData,fileData
     * @param node
     */
    private MetaDataView loadData(FNode node,FNode parentNode)
    {
        //import user data
        ImportUserData importData = getImportData();
        
        //set parent dir data
//        MetaDataModel parentModel=parentNode.getModelOfSeries(0);
        MetaDataModel parentModel = getParentMetaDataModel(node);
        
        if(parentNode!=null && parentModel!=null){
            LOGGER.info("[DEBUG] -- READ MODEL OF "+parentNode.getAbsolutePath());
           
            boolean parentDataChange=parentModel.noticUserInput();
        }
        //set current dir data
        MetaDataModel dirModel=getCurrentSelectionMetaDataModel(parentNode);

        MetaDataView dataView=null;
        try {
            dataView=new MetaDataView(node.getAbsolutePath(), importData, parentModel, this, true, true);
        } catch (Exception e) {
            return null;
        }
        return dataView;
    }

    /**
     * save data model of  node, if any user input available and update child tags
     */
    private void saveInputToModel(FNode node) 
    {
    	if(node!=null){
    		// save input
    		node.saveModel();
    		// has data saved?
    		if(node.getModelObject()!=null && node.getModelObject().hasBeenModified()){ 
    			LOGGER.debug("Update childs of "+node.getAbsolutePath());
    			System.out.println("# MeatDataDialog:: saveInputToModel("+node.getAbsolutePath()+")-- update childs");
    			updateChildsOfDirectory(node);
    		}
    	}
    }

    /**
     * Update all childs of type directory with existing model with tags changes
     * @param node
     */
    private void updateChildsOfDirectory(FNode node) 
    {
    	System.out.println("# MetaDataDialog::updateChildDirectories of "+node.getAbsolutePath());
		int numChilds=node.getChildCount();
		for(int i=0; i<numChilds;i++){
			FNode child = (FNode) node.getChildAt(i);
			if(node.hasModelObject()){
				if(child.hasModelObject() ){
					System.out.println("\t ...update "+child.getAbsolutePath());
					LOGGER.debug("Update "+child.getAbsolutePath());
					try {
						child.getModelObject().updateData(node.getModelObject());
						child.getView().setParentDataLoaded(true);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(!child.isLeaf()){
						updateChildsOfDirectory(child);
					}
				}//else load data at loadParentData
			}
		}
		node.getModelObject().clearListOfModifications();
	}


    


	private void updateModel(FNode node)
    {
    	
		if(node.getView()!=null){
			LOGGER.debug("[Save] -- save gui model for: "+node.getAbsolutePath());
			node.setModelObject(((MetaDataView) node.getView()).getSavedModelObject());
		}else{
			// save model of parent
			FNode parent=(FNode) node.getParent();
			LOGGER.debug("[Save] -- No gui -save model of parent node: "+parent.getAbsolutePath());
			node.setModelObject(parent.getModelObject());
		}
    }


//    private void addParentModel(MetaDataModel myModel,MetaDataUI view) 
//    {
//        
//        try {
//            if(myModel!=null && lastNode!=null){ 
//                LOGGER.info("[DEBUG]--- Add data of parent model "+lastNode.toString());
//                if(myModel.noticUserInput()){
//                    view.addData(myModel);
//                }else{
//                    LOGGER.info("[DEBUG]--- No parent model: No user input");
//                }
//            }else{
//                LOGGER.info("[DEBUG]--- No parent model ");
//            }
//        } catch (Exception e) {
//            LOGGER.warn("[DATA] -- Can't add metadata from parent model");
//            e.printStackTrace();
//        }
//    }


    private MetaDataModel getParentMetaDataModel(FNode node) 
    {
        if(node!=null){
            FNode parent=(FNode) node.getParent();
           
            if(parent!=null){
            	if(parent.hasModelObject()){
            		// parent is a directory with only one metadatamodel
            		return parent.getModelOfSeries(0);
            	}else{
            		return getParentMetaDataModel(parent);
            	}
            }
        }
        return null;
    }
    
    /**
     * 
     * @return model of current selected directory, if exist's. If selection is a file, return null.
     */
    private MetaDataModel getCurrentSelectionMetaDataModel(FNode node)
    {
        if(node!=null && !node.isLeaf()){
            LOGGER.info("[GUI] -- Load current selection model");
            return node.getModelOfSeries(0);
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

    /**
     * 
     * @return absolute path of selected file. If selected component is a dir return empty string 
     */
    private String getSelectedFilePath(FNode node)
    {
        String fname="";

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
    	System.out.println("# MetaDataDialog::refreshFileView()");
        this.fileFilter=fileFilter;
        metaPanel.removeAll();
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
            firePropertyChange(ImportDialog.ADD_AND_REFRESH_FILE_LIST,null, null);
            
            break;
//        case LOAD_MIC_SETTINGS: 
//            JComboBox cb = (JComboBox)evt.getSource();
//            String petName = (String)cb.getSelectedItem();
//            LOGGER.info("\n Load mic settings for "+petName);
////	        customSettings=new CustomViewProperties(petName);
////	        dataView=new MicroscopeDataView(sett);
//            loadAndShowDataForSelection();
//            revalidate();
//            repaint();
//            
//            break;
        case CMD_SAVE:
            LOGGER.info("[GUI-ACTION] -- save");
            System.out.println("\n+++ EVENT: SAVE ++++\n");
            TreePath path=fileTree.getSelectionPath();
            String fileName=getSelectedFilePath((FNode)fileTree.getLastSelectedPathComponent());
            if(fileName!=null){
                String srcFile=fileName.equals("") ? "" : fileName;
                try {
					saveCurrentNodeAndUpdate(path,srcFile);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
            revalidate();
            repaint();
            break;
        case CMD_SAVEALL:
            LOGGER.info("[GUI-ACTION] -- save all");
            System.out.println("\n+++ EVENT: SAVE ALL ++++\n");
            //only for directory
            FNode parentNode = (FNode)fileTree.getLastSelectedPathComponent();
            unreadableFileList=new ArrayList<String>();
            deselectNodeAction(parentNode);
            saveAllChilds(parentNode);
            insertNodes(null, parentNode.getFile().getName(), parentNode);
            fileTree.updateUI();
            if(unreadableFileList.size()> 0){
            	String files="";
            	for(int i=0; i<unreadableFileList.size(); i++){
            		files=files+"\n"+unreadableFileList.get(i);
            	}
            	WarningDialog ld=new WarningDialog("Not supported file format!", 
    					"Can't read metadata of following files! Format is not supported.\n "+files,this.getClass().getSimpleName());
    			ld.setVisible(true);
            }
            break;
        case CMD_RESET:
        	LOGGER.info("[GUI-ACTION] -- reset");
        	System.out.println("\n +++ EVENT RESET +++\n");
        	FNode selection=(FNode)fileTree.getLastSelectedPathComponent();
        	//TODO: profile default data eliminate
        	//file
        	String file = getSelectedFilePath(selection);
        	//clear node model data
        	selection.setModelObject(null);
        	MetaDataView view=null;
        	try {
        		
        		if(!selection.isLeaf()){
        			view = loadAndShowDataForDirectory(selection, file, null,
        					null, view, true, true && enabledPredefinedData);
        		}else{
        			view = loadAndShowDataForFile(file, null, null, view, true, true && enabledPredefinedData);
        		}
//        		((MetaDataView)metaPanel.getComponent(0)).reset();
        	} catch (Exception e) {
        		// TODO Auto-generated catch block
        		e.printStackTrace();
        	}
        	showMetaDataView(view);
        	setFilterView(view);
        	revalidate();
        	repaint();
            
            break;
        case CMD_PROFILE:
        	//TODO: reload all available views
            LOGGER.info("[GUI-ACTION] -- load profile file");
            
            UOSProfileEditorUI profileWriter=new UOSProfileEditorUI(customSettings, enabledPredefinedData);
            profileWriter.setVisible(true);
            customSettings=profileWriter.getProperties();
			enabledPredefinedData =profileWriter.shouldPredefinedValLoaded();
            //TODO reload current view if changes
            loadAndShowDataForSelection((FNode)fileTree.getLastSelectedPathComponent());
//            firePropertyChange(CHANGE_CUSTOMSETT, null, customSettings); MetaDataControl
            break;
        case CMD_SPECIFICATION:
            LOGGER.info("[GUI-ACTION] -- load specification file");
            UOSSpecificationEditor specEditor=new UOSSpecificationEditor(hardwareDef);
            specEditor.setVisible(true);
            
            break;
        case CMD_VIEWFILE:
            Border redline = BorderFactory.createLineBorder(Color.red);
            Border compound= BorderFactory.createRaisedBevelBorder();
            break;
        case CMD_LOADPREVAL:
        	break;
        
        }
        
        

    }


	/**
	 * Save recursive all childs.
	 * @param parentNode
	 */
    public void saveAllChilds(FNode parentNode) 
    {
    	updateModel(parentNode);
    	Enumeration children =parentNode.children();
    	while(children.hasMoreElements()){
    		FNode node=(FNode)children.nextElement();
    		//load all data and save
    		if(node !=null){
    			if(node.isLeaf()){
    				MetaDataView view=loadData(node,parentNode);
    				if(view.isLoaded())
    					saveMetadataForNode(node.getAbsolutePath(),view);
    				else
    					unreadableFileList.add(node.getAbsolutePath());
    			}else{
    				saveAllChilds(node);
    			}
    		}
    	}
    }


    /**
     * @throws Exception 
     * 
     */
    private void saveCurrentNodeAndUpdate(TreePath path, String srcFile) throws Exception 
    {
        saveCurrentNode(path, srcFile);
        //freeze status fileTree
        holdData=true;
        
        
        String fileName="";
        if(!srcFile.equals(""))
            fileName=FilenameUtils.removeExtension(srcFile)+".ome";
        
        File[] fileList={new File(srcFile),new File(fileName)};
        firePropertyChange(ImportDialog.ADD_AND_REFRESH_FILE_LIST,null, fileList);
        fileTree.setSelectionPath(path);//TODO: 
        holdData=false;
    }
    
    
    private void saveCurrentNode(TreePath path, String srcFile) throws Exception
    {
        LOGGER.info("[DEBUG] -- save node "+srcFile);
        
        ((MetaDataView)metaPanel.getComponent(0)).saveToFile();
    }
    
    private void saveMetadataForNode(String srcFile,MetaDataView view)
    {
        LOGGER.debug("[SAVE] -- save node "+srcFile);
        try {
			view.saveToFile();
		} catch (Exception e) {
			LOGGER.error("Can't save metadata for "+srcFile);
			LOGGER.debug(e.getStackTrace().toString());
		}
    }
    
    

    @Override
    public void valueChanged(TreeSelectionEvent e) 
    {
        FNode selectedNode=null;
        FNode lastSelectedNode=null;
        
        TreePath[] paths = e.getPaths();
        
        // maximum 2 paths in the list -> last and current
        for (int i = 0; i < paths.length; i++) {
          if (e.isAddedPath(i)) {
            selectedNode=(FNode)paths[i].getLastPathComponent();
          } else {
            lastSelectedNode = (FNode)paths[i].getLastPathComponent();
          }
        }
        System.out.println("\n+++ EVENT TREE +++\n");
        deselectNodeAction(lastSelectedNode);
        selectNodeAction(selectedNode);
    }

    /**
     * Call methods for selected node in the tree.
     * @param selectedNode
     */
    private void selectNodeAction(FNode selectedNode) 
   {
	   if(selectedNode!=null ){
		   System.out.println("\n# MetaDataDialog::selectNodeAction("+selectedNode.getAbsolutePath()+")");
		   LOGGER.debug("Select node action for "+selectedNode.getAbsolutePath());
           
		   enableSaveButtons(selectedNode.isLeaf());
           loadAndShowDataForSelection(selectedNode);
           
           revalidate();
           repaint();
       }		
	}


	/**
	 * If selected node is a leaf (file or dir without childs) set save enabled and disabled save all.
	 * @param isLeaf
	 */
	public void enableSaveButtons(boolean isLeaf) 
	{
		if(isLeaf){
               saveDataButton.setEnabled(true);
               saveAllDataButton.setEnabled(false);
           }else{
               saveDataButton.setEnabled(false);
               saveAllDataButton.setEnabled(true);
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


    /**
     * Show selected series
     */
    @Override
    public void valueChanged(ListSelectionEvent e) 
    {
         if (e.getValueIsAdjusting() == false) {
                if (seriesList.getSelectedIndex() != -1) {
                    if(metaPanel.getComponentCount()>0){
                        Component c=metaPanel.getComponent(0);
                        if(c instanceof MetaDataView){
                        	((MetaDataView) c).updateGlobalSeriesData();
                            ((MetaDataView) c).showSeries((String)seriesList.getSelectedValue());
                        }
                    }
                }
         }
    }


	@Override
	public void itemStateChanged(ItemEvent e) 
	{
//		Object source=e.getItemSelectable();
//		if(source==showCustomData )
//		{
//			if(e.getStateChange() == ItemEvent.SELECTED){
//				enabledPredefinedData=true;
//			}else{
//				enabledPredefinedData=false;
//			}
//		}
		System.out.println("\n+++EVENT : FILTERVIEW +++\n");
		if(!disableItemListener)
			showFilteredData(showDirData.isSelected(),showFileData.isSelected(),
					showCustomData.isSelected());
		

	}


	private void showFilteredData(boolean dirData, boolean fileData,
			boolean customData) 
	{
		System.out.println("# MetaDataDialog::showFilteredData(): "+dirData+", "+fileData+", "+customData+", enablePreVal= "+enabledPredefinedData);
		
		loadAndShowFilteredDataForSelection((FNode)fileTree.getLastSelectedPathComponent(),
				dirData,fileData,customData);
	} 

	public CustomViewProperties getCustomViewProperties()
	{
		return customSettings;
	}
    


}
