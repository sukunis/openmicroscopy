package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.microscope;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import jdk.internal.org.xml.sax.SAXParseException;
import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.formats.FormatException;
import loci.formats.ImageReader;
import loci.formats.meta.MetadataRetrieve;
import loci.formats.meta.MetadataStore;
import loci.formats.services.OMEXMLService;
import ome.xml.meta.IMetadata;
import ome.xml.model.OME;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.ImportUserData;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.MetaDataDialog;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.UOSMetadataLogger;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.MetaDataModel;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.MetaDataModelObject;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.SaveMetadata;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules.ExperimentCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.ExceptionDialog;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.WarningDialog;
import org.slf4j.LoggerFactory;

/**
 * Gui for metadata for selected node of filetree.
 * Series image file: holds one metadataui - tab for every series image of the selected file. 
 * If not a series file, than view is  an instance of jpanel.
 * 
 * @author Kunis
 *
 */
public class MetaDataView extends JPanel
{
	 /** Logger for this class. */
//    private static Logger LOGGER = Logger.getLogger(UOSMetadataLogger.class.getName());
	 private static final org.slf4j.Logger LOGGER =
	    	    LoggerFactory.getLogger(MetaDataUI.class);
    
    private OME ome;
    private File srcFile;
    private MetaDataUI singleView;
    
    private DefaultListModel seriesListModel;
    private JPanel cardPane;
    private CardLayout seriesCard; 
    private List<String> cardNames;
    private int currentCardIndex;
    
    private boolean seriesData;
    
    private boolean readFile;
    
    
    /**
     * Constructor for place holder / dummy component
     */
    public MetaDataView() 
    {
    	super(new BorderLayout());
		this.setBorder(BorderFactory.createEmptyBorder());
	}
    
    /**
     * Metadata GUI for file.
     * @param sett GUI properties
     * @param fName file name
     * @param importData given import information for this data.
	 * @param parentData given parent information for this data.
	 * @param parentPanel parent JPanel of this component.
     */
	public MetaDataView(CustomViewProperties sett,String fName,
			ImportUserData importData, MetaDataModel parentData, JPanel parentPanel) throws Exception
	{
		super(new BorderLayout());
		this.setBorder(BorderFactory.createEmptyBorder());

		LOGGER.info("### build view and load data for "+ fName+" ###");

		ImageReader reader = new ImageReader();
		IMetadata data=null;
		
		Cursor cursor=parentPanel.getCursor();
		parentPanel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		readFile=true;
		data = readMetadataFromFile(fName, reader);

		parentPanel.setCursor(cursor);
		if(data==null){ 
			readFile=false;
			singleView=new MetaDataUI(sett);
			return ;
		}

		LOGGER.info("### read "+ fName+" ###");

		srcFile=new File(fName);

		if(reader.getSeriesCount()<2){
			LOGGER.info("[DATA] -- no series data");
			seriesData=false;
			// create with profile and hardware configurations
			singleView=new MetaDataUI(sett);

			// load importData
//			singleView.readData(importData);
//
//			//load parent data
//			loadParentData(parentData, singleView);
//
//			//load data from file
//			try{
//				loadFileData(fName, ome, 0, singleView);
//			}catch(Exception e){
//				LOGGER.error("[DATA] CAN'T read METADATA of "+fName);
//				ExceptionDialog ld = new ExceptionDialog("Metadata Error!", 
//						"Can't read given metadata  from "+fName,e);
//				ld.setVisible(true);
//			}
			add(singleView,BorderLayout.CENTER);
			
		}else{
			seriesData=true;
			currentCardIndex=-1;
			
			seriesListModel=new DefaultListModel();
			seriesCard = new CardLayout();
			cardPane=new JPanel(seriesCard);
			for(int j=0; j< reader.getSeriesCount(); j++){
				LOGGER.info("[SERIE] ------------ read SERIE "+j+" of "+reader.getSeriesCount()+
						": "+data.getImageName(j)+"---------------------" );
				reader.setSeries(j);
				//new metaUI tab
				MetaDataUI metaUI=new MetaDataUI(sett);

				//load importData
				metaUI.readData(importData);

				//load parent data
				loadParentData(parentData,metaUI);

				//load data from file
				loadFileData(fName, ome, j, metaUI);

				// add series to cardPane
				seriesListModel.addElement(data.getImageName(j));
				
				
				cardPane.add(metaUI,data.getImageName(j));
			}
			add(cardPane,BorderLayout.CENTER);
		}
		
	}
	
	/**
	 * 
	 * @return listModel with names of series of given ome model
	 */
	public DefaultListModel getSeries()
	{
		return seriesListModel;
	}

	/**
	 * Read data from OME o into given MetaDataUI component metaUI and link given metaUI to the OME own file.
	 * @param fName given file
	 * @param o OME of the given file
	 * @param j series index
	 * @param metaUI gui for filedata
	 * @throws Exception 
	 */
	private void loadFileData(String fName, OME o, int j,
			MetaDataUI metaUI) throws Exception 
	{
		metaUI.linkToFile(new File(fName));
		metaUI.readData(o, j);
	}
	
	
	/**
	 * Metadata GUI for directory.
	 * @param sett GUI properties
	 * @param name directory name
	 * @param importData given import information for this dataset.
	 * @param parentData given parent information for this dataset.
	 * @param dirData metadata model for current directory
	 */
	public MetaDataView(CustomViewProperties sett, String name, 
			ImportUserData importData, MetaDataModel parentData, MetaDataModel dirData)
	{
		super(new BorderLayout());
		LOGGER.info("[GUI] -- select directory");
		this.setBorder(BorderFactory.createEmptyBorder());
		
		srcFile=null;
		seriesData=false;
		singleView= new MetaDataUI(sett);
		
		//set importData
		singleView.readData(importData);
		
		//set parentData
		loadParentData(parentData,singleView);
		
		// set saved data for this directory
		if(dirData!=null){
			try {
				singleView.addData(dirData);
			} catch (Exception e) {
				LOGGER.warn("[DATA] -- Can't add metadata from dir model "+name);
				e.printStackTrace();
			}
		}

		add(singleView,BorderLayout.CENTER);
		revalidate();
		repaint();
	}

	

	/**
	 * Add parent metadata to given metaData GUI.
	 * @param parentData parent data model
	 * @param pane metaData GUI
	 */
	private void loadParentData(MetaDataModel parentData,MetaDataUI pane) 
	{
		if(parentData!=null){
			try {
				pane.addData(parentData);
			} catch (Exception e) {
				LOGGER.warn("[DATA] -- Can't add metadata from parent model");
				e.printStackTrace();
			}
		}else{
			LOGGER.info("[DATA]--- No parent data available ");
		}
	}
	
	
	/**
	 * Read meta data from given file into OMEXMLMetadata format and set it as the MetadataStore 
	 * for given reader. Set global ome as MetadataRetrieve OMEXMLRoot. 
	 * @param file source file
	 * @param reader of the source file
	 * @return metadata as OMEXMLMetadata format
	 * @throws DependencyException
	 * @throws ServiceException
	 */
	private IMetadata readMetadataFromFile(String file, 
			ImageReader reader) throws DependencyException, ServiceException 
	{
		//record metadata to ome-xml format
		ServiceFactory factory=new ServiceFactory();
		OMEXMLService service = factory.getInstance(OMEXMLService.class);
		IMetadata metadata =  service.createOMEXMLMetadata();
		reader.setMetadataStore((MetadataStore) metadata);
		
		try{
			reader.setId(file);
		}catch(FormatException | IOException e){
//			WarningDialog ld=new WarningDialog("Not supported file format for MetaData Editor!", 
//					"Can't read metadata of "+file+"! Format is not supported.");
//			ld.setVisible(true);
			return null;
		}
		
		LOGGER.info("[DATA] -- use READER: "+reader.getReader().getClass().getName());
		System.out.println("Use Reader: "+reader.getReader().getClass().getSimpleName());
		
		String xml = service.getOMEXML((MetadataRetrieve) metadata);
		ome = (OME) service.createOMEXMLRoot(xml);
		
		return metadata;
	}
	
	/**
	 * 
	 * @return metaData model that holds a list of all series data models of current img data.
	 */
	public MetaDataModelObject getModelObject() 
	{
		try{
		List<MetaDataModel> list=new ArrayList<MetaDataModel>();
		if(seriesData){
			for(Component comp:cardPane.getComponents()){
				list.add(((MetaDataUI) comp).getModel());
			}
		}else{
			list.add(singleView.getModel());
		}
		MetaDataModelObject obj=new MetaDataModelObject(seriesData,list);
		
		return obj;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Save data from GUI to file.
	 */
	public void save() throws Exception
	{
		if(ome!=null){
			List<MetaDataModel> list=new ArrayList<MetaDataModel>();
			//file
			if(seriesData){
				updateGlobalSeriesData();
				for(Component comp:cardPane.getComponents()){
					list.add(((MetaDataUI) comp).getModel());
				}
				LOGGER.info("[SAVE] -- save series data to "+srcFile.getAbsolutePath());
				System.out.println("Save series data");
				SaveMetadata saver=new SaveMetadata(ome, getModelObject(), null, srcFile);
				saver.save();
				
			}else{
				if(singleView!=null){
					LOGGER.info("[SAVE] -- save single data to "+srcFile.getAbsolutePath());
					SaveMetadata saver=new SaveMetadata(ome, singleView.getModel(), null, srcFile);

					saver.save();
				}
			}
			
		}else{
			//dir
			if(singleView!=null){
				LOGGER.info("[SAVE] -- save model for directory");
				singleView.getModel();
			}
		}
	}
	
	/**
	 * Shows all GUI data.
	 */
	public void setVisible()
	{
		if(seriesData){
			int i=0;
			for(Component comp : cardPane.getComponents()){
				try {
					((MetaDataUI) comp).showData();
				} catch (Exception e) {
					LOGGER.error("[DATA] CAN'T load METADATA gui");
					ExceptionDialog ld = new ExceptionDialog("Metadata GUI Error!", 
							"Can't load metadata gui for series "+i,e);
					ld.setVisible(true);
				}
				i++;
			}
		}else{
			try {
				singleView.showData();
			} catch (Exception e) {
				LOGGER.error("[DATA] CAN'T load METADATA gui");
				ExceptionDialog ld = new ExceptionDialog("Metadata GUI Error!", 
						"Can't load metadata gui",e);
				ld.setVisible(true);
			}
		}
		revalidate();
		repaint();
	}
	
	/**
	 * Shows card pane of given name on the top. Holds Sample and ExperimentCompUI
	 * as global informnations.
	 * @param name
	 */
	public void showSeries(String name)
	{
		seriesCard.show(cardPane,name);
		currentCardIndex = seriesListModel.indexOf(name);
	}
	
	/**
	 * Update global data for all series, if data has changed in current selection. 
	 * Global data are sample, experiment data.
	 */
	public void updateGlobalSeriesData() 
	{
		try {
			if(currentCardIndex==-1)
				currentCardIndex=getCurrentCardIndex();
			if(currentCardIndex!=-1){
				MetaDataModel currentModel=((MetaDataUI) cardPane.getComponent(currentCardIndex)).getModel();
				if(currentModel.getExpModul().userInput() || currentModel.getSampleModul().userInput()){
					//					MetaDataUI newModel=((MetaDataUI) cardPane.getComponent(seriesListModel.indexOf(name)));
					for(Component comp:cardPane.getComponents()){
						if(currentModel.getExpModul().userInput()){
							((MetaDataUI) comp).addExperimentData(currentModel.getExperiment(),true);
						}
						if(currentModel.getSampleModul().userInput()){
							((MetaDataUI) comp).addSampleData(currentModel.getSample(),true);
						}
					}
				}
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	public int getCurrentCardIndex()
	{
		int index = -1;

		int i=0;
	    for (Component comp : cardPane.getComponents() ) {
	        if (comp.isVisible() == true) {
	            try {
					index=i;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
	        i++;
	    }
	    return index;
	}

	public boolean isLoaded() {
		return readFile;
	}
}
