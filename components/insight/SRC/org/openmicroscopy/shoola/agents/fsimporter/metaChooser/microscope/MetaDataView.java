package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.microscope;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
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
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.UOSMetadataLogger;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.MetaDataModel;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.MetaDataModelObject;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.SaveMetadata;
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
//    private JComponent view;
    private MetaDataUI singleView;
    
    private JTabbedPane seriesView;
    
    private boolean seriesData;
    
    
    
    public MetaDataView() 
    {
    	super(new BorderLayout());
		this.setBorder(BorderFactory.createEmptyBorder());
	}
    
    /**
     * Metadataview for file
     * @param sett
     * @param data
     * @param fName
     */
	public MetaDataView(CustomViewProperties sett,String fName,
			ImportUserData importData, MetaDataModel parentData, JPanel view) throws Exception
	{
		super(new BorderLayout());
		this.setBorder(BorderFactory.createEmptyBorder());

		LOGGER.info("### build view and load data for "+ fName+" ###");

		ImageReader reader = new ImageReader();
		IMetadata data=null;

		
		Cursor cursor=view.getCursor();
		view.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		
		data = readMetadataFromFile(fName, reader);
		
//		ServiceFactory factory = new ServiceFactory();
//		OMEXMLService service = factory.getInstance(OMEXMLService.class);
//		IMetadata metadata = service.createOMEXMLMetadata();
//		reader = new ImageReader();
//		reader.setMetadataStore(metadata);
//		reader.setId(file);
		
		

		view.setCursor(cursor);
		if(data==null){ 
			singleView=new MetaDataUI(sett);
			return ;
		}

		LOGGER.info("### read "+ fName+" ###");

		ServiceFactory factory = new ServiceFactory();
		OMEXMLService service = factory.getInstance(OMEXMLService.class);
		
		String xml = service.getOMEXML((MetadataRetrieve) data);
		ome = (OME) service.createOMEXMLRoot(xml);

System.out.println("Use Reader: "+reader.getReader().getClass().getSimpleName());
		srcFile=new File(fName);

		if(reader.getSeriesCount()<2){
			LOGGER.info("[DATA] -- no series data");
			seriesData=false;
			singleView=new MetaDataUI(sett);

			// load importData
			singleView.readData(importData);

			//load parent data
			loadParentData(parentData, singleView);

			//load data from file
			loadFileData(fName, ome, 0, singleView);
			add(singleView,BorderLayout.CENTER);
			
		}else{
			seriesData=true;
			seriesView=new JTabbedPane();
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

				seriesView.addTab("#"+j+": "+data.getImageName(j),(Component) metaUI);
			}
			add(seriesView,BorderLayout.CENTER);
		}
		
	}
	
	

	/**
	 * @param fName
	 * @param data
	 * @param j
	 * @param metaUI
	 */
	private void loadFileData(String fName, OME o, int j,
			MetaDataUI metaUI) 
	{
		metaUI.linkToFile(new File(fName));
		metaUI.readData(o, j);
	}
	
	/**
	 * Metadataview for directory
	 * @param sett
	 * @param name
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
				LOGGER.warn("[DATA] -- Can't add metadata from dir model");
				e.printStackTrace();
			}
		}

		add(singleView,BorderLayout.CENTER);
		revalidate();
		repaint();
	}

	

	/**
	 * @param parentData
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
//			LOGGER.error("Error read file");
//			ExceptionDialog ld = new ExceptionDialog("Metadata Error!", 
//					"Can't read metadata of "+file,e);
//			ld.setVisible(true);
			WarningDialog ld=new WarningDialog("Not supported file format for MetaData Editor!", 
					"Can't read metadata of "+file+"! Format is not supported.");
			ld.setVisible(true);
			return null;
		}
		
		LOGGER.info("[DATA] -- use READER: "+reader.getReader().getClass().getName());
		return metadata;
	}
	
	public MetaDataModelObject getModelObject()
	{
		
		List<MetaDataModel> list=new ArrayList<MetaDataModel>();
		if(seriesData){
			int series=seriesView.getTabCount();
			for(int i=0; i<series; i++){
				list.add(((MetaDataUI) seriesView.getComponentAt(i)).getModel());
			}
		}else{
			list.add(singleView.getModel());
		}
		MetaDataModelObject obj=new MetaDataModelObject(seriesData,list);
		
		return obj;
	}
	
	public void save()
	{
		if(ome!=null){
			List<MetaDataModel> list=new ArrayList<MetaDataModel>();
			//file
			if(seriesData){
				JTabbedPane view=(JTabbedPane) getComponent(0);
				int series=((JTabbedPane)view).getTabCount();
				for(int i=0; i<series; i++){
					list.add(((MetaDataUI) ((JTabbedPane)view).getComponentAt(i)).getModel());
				}
				MetaDataModelObject obj=new MetaDataModelObject(seriesData,list);
				LOGGER.info("[SAVE] -- save model for series data");
				LOGGER.info("[SAVE] -- save to "+srcFile.getAbsolutePath());
				SaveMetadata saver=new SaveMetadata(ome, getModelObject(), null, srcFile);
				saver.save();
				
			}else{
				if(singleView!=null){
					LOGGER.info("[SAVE] -- save model for single data");
					LOGGER.info("[SAVE] -- save to "+srcFile.getAbsolutePath());
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
	
	public void setVisible()
	{
		
		if(seriesData){
			JTabbedPane view=(JTabbedPane) getComponent(0);
			int series=((JTabbedPane)view).getTabCount();
			for(int i=0; i<series; i++){
				try {
					((MetaDataUI) ((JTabbedPane)view).getComponentAt(i)).showData();
				} catch (Exception e) {
					LOGGER.error("[DATA] CAN'T load METADATA gui");
					ExceptionDialog ld = new ExceptionDialog("Metadata GUI Error!", 
							"Can't load metadata gui",e);
					ld.setVisible(true);
				}
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
}
