package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components;

import java.awt.BorderLayout;
import java.util.logging.Logger;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.util.List;

import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.metal.MetalFileChooserUI;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;























import javax.xml.transform.Templates;
import javax.xml.transform.stream.StreamSource;

import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.common.xml.XMLTools;
import loci.formats.FormatException;
import loci.formats.IFormatReader;
import loci.formats.IFormatWriter;
import loci.formats.ImageReader;
import loci.formats.ImageWriter;
import loci.formats.MetadataTools;
import loci.formats.in.OMEXMLReader;
import loci.formats.meta.IMetadata;
import loci.formats.services.OMEXMLService;
import loci.formats.services.OMEXMLServiceImpl;
import ome.specification.XMLWriter;
import ome.xml.model.Experiment;
import ome.xml.model.Image;
import ome.xml.model.ImagingEnvironment;
import ome.xml.model.OME;
import ome.xml.model.Objective;
import ome.xml.model.ObjectiveSettings;

import org.apache.commons.io.FilenameUtils;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.FNode;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.UOSMetadataLogger;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.DetectorCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.DichroicCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.ElementsCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.ExperimentCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.FilterCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.ImageCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.LightPathElem;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.ObjectiveCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.TagData;

import sun.swing.plaf.GTKKeybindings;

import com.sun.java.swing.plaf.windows.WindowsFileChooserUI;

public class SaveMetadataUserDefinedUI extends JPanel implements  ListSelectionListener, ActionListener
{
	/** Logger for this class. */
    private static Logger LOGGER = Logger.getLogger(UOSMetadataLogger.class.getName());
	
	private JList<String> objList;
//	private JPanel cardPane;
	private JButton saveBtn;
	private JButton chancelBtn;
	
	private JFileChooser fchooser;
	private MetaDataModel model;
	private IMetadata omexmlMeta;
	
	private final String OBJECTIVE="Objective";
	private final String DETECTOR="Detector";
	private final String IMAGE="Image";
	private final String LIGHTSRC="LightSource";
	private final String CHANNEL="Channel";
	private final String EXPERIMENT="Experiment";
	private final String IMGENV="ImagingEnvironment";
	private final String FILTER="Filter";
	private final String DICHROIC="Dichroic";
	
	private static final int SAVE=1;
	private static final int CHANCEL=2;
	
	private OME ome;
	private OMEStore omeStore;
	private String srcImageFName;
//	private String srcImagePath;
	
//	private List<>
	
	
//	 private static final Logger LOGGER =
//			    LoggerFactory.getLogger(SaveMetadataUserDefinedUI.class);
	
	public SaveMetadataUserDefinedUI(OME _ome,MetaDataModel _model, IMetadata _store, File srcImage)
	{
		ome=_ome;
		model=_model;
		omexmlMeta=_store;
		
		omeStore=new OMEStore(ome);
		
		// save xml in *.ome under same name and path like srcImage
		srcImageFName=FilenameUtils.removeExtension(srcImage.getAbsolutePath());
//		srcImagePath=FilenameUtils.getFullPath(srcImage.getAbsolutePath());
//		System.out.println("::FILEPATH "+srcImagePath);
//		System.out.println("::FILENAME "+FilenameUtils.getBaseName(srcImage.getAbsolutePath()));
//		System.out.println("::FILEABSPATH "+srcImageFName);
//		try{
//		System.out.println("::FILEURL "+fileToURL(srcImage));
//		}catch (Exception e){}
		
		// layout tag preferences
//		CardLayout cl=new CardLayout();
//		cardPane=new JPanel(cl);	
//		
//		buildCardPane();
//		
//		JSplitPane splitPane=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
//				buildList(),cardPane);
//		splitPane.setResizeWeight(0.3);
//		splitPane.setDividerLocation(100);
		
		// Save, Chancel Button for filechooser
		JPanel buttonPane=new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		saveBtn=new JButton("Save");
		saveBtn.setPreferredSize(new Dimension(70, 23));
		saveBtn.addActionListener(this);
		
		chancelBtn=new JButton("Chancel");
		chancelBtn.setPreferredSize(new Dimension(70,23));
		chancelBtn.addActionListener(this);
		
		buttonPane.add(chancelBtn);
		buttonPane.add(Box.createHorizontalStrut(5));
		buttonPane.add(saveBtn);
		
		// filechooser
		Box box=Box.createVerticalBox();
		fchooser=new JFileChooser();
		fchooser.setDialogType(JFileChooser.SAVE_DIALOG);
		fchooser.setControlButtonsAreShown(false);
		fchooser.setFileSelectionMode(javax.swing.JFileChooser.FILES_ONLY);
		MetalFileChooserUI wui=new MetalFileChooserUI(fchooser);
		
		wui.installUI(fchooser);
		box.add(fchooser);
		box.add(Box.createVerticalStrut(10));
		box.add(buttonPane);
		
		setLayout(new BorderLayout());
		add(box, BorderLayout.NORTH);
//		add(splitPane,BorderLayout.CENTER);

	}
	
	

	private JPanel buildList()
	{
		JPanel view=new JPanel();
		view.setLayout(new BorderLayout(10,0));
		
		DefaultListModel<String> listModel = new DefaultListModel<String>();
		listModel.addElement(IMAGE);//+settings
		listModel.addElement(OBJECTIVE);
		listModel.addElement(DETECTOR);
		listModel.addElement(LIGHTSRC);
		listModel.addElement(CHANNEL);//+settings
		listModel.addElement(EXPERIMENT);
		listModel.addElement(FILTER); // belongs to channel
		listModel.addElement(DICHROIC);
		listModel.addElement(IMGENV);

		objList = new JList<String>(listModel);
		objList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		objList.setSelectedIndex(0);
		objList.addListSelectionListener(this);
		objList.setVisibleRowCount(3);
        JScrollPane listScrollPane = new JScrollPane(objList);

		JPanel labelPane=new JPanel();
//		labelPane.setSize(width,10);
		labelPane.setBackground(Color.gray);
		labelPane.add(new JLabel("Category"), JLabel.CENTER);
		
		view.add(labelPane, BorderLayout.NORTH);
		view.add(listScrollPane,BorderLayout.CENTER);
		
		return view;
	}
	
//	private void buildCardPane()
//	{
//		addCard(model.getImageData(),IMAGE);
////		addCard(model.getDetectorData(),DETECTOR);
////		addCard(model.getObjectiveData(), OBJECTIVE);//TODO: tagList==0
////		addCard(model.getLightSrcData(),LIGHTSRC);
////		addCard(model.getChannelData(),CHANNEL);
//		addCard(model.getExpData(),EXPERIMENT);
////		addCard(model.getImgEnvData(),IMGENV);
//		
//		if(model.getFilterData()!=null && !model.getFilterData().isEmpty()){
//			FilterCompUI f=new FilterCompUI();
//			f.createDummyPane(false);
//			addCard(f,FILTER);
//		}
//		if(model.getDichroicData()!=null && !model.getDichroicData().isEmpty()){
//			DichroicCompUI d=new DichroicCompUI();
//			d.createDummyPane(false);
//			addCard(d,DICHROIC);
//		}
//	}
//	
//	private void addCard(List<ElementsCompUI> list, String name)
//	{
//		if(list!=null){
//			addCard(list.get(0),name);
//		}
//	}
//	
//	private void addCard(ElementsCompUI elem, String name)
//	{
//		MetadataPreferences prefs = new MetadataPreferences(name, elem);
//		System.out.println("[SaveMetadata::addCard()] "+name);
//		cardPane.add(prefs,name);
//	}
	
	//write data to omexmlMeta
	public void writeToMetaDataStore() throws Exception
	{
//		 for (Component comp : cardPane.getComponents() ) {
//			 if(comp instanceof MetadataPreferences)
//				 saveComponent((MetadataPreferences) comp);
//		 }
		//experiment
		try {
			Image img=model.getImageOMEData();
			if(img!=null){
				//--- save Experiment and Experimenter data
				Experiment e = model.getExperiment();
				omeStore.storeExperiment(e);
				// update refs
			
				img.linkExperiment(e);
				img.linkExperimenter(e.getLinkedExperimenter());
				//TODO: refs update
				//Refs to experimenter: Dataset, ExperimenterGroup, Image, MicrobeamManipulation, Project

				//--- save ObjectiveSettings and Objective
				ObjectiveSettings os=model.getObjectiveSettings();
				omeStore.storeObjectiveSettings(os,img);

				Objective o=model.getObjective();
				omeStore.storeObjective(o,img,model.getImageIndex());

				//--- save ImagingEnv
				ImagingEnvironment iEnv=model.getImgagingEnv();
				omeStore.storeImagingEnv(iEnv,img);	

				//TODO save stagelabel

				//TODO save planes, moechte man ueberhaupt hier aenderungen zulassen???

			}
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void saveComponent(MetaDataPreferences prefs)
	{
		switch (prefs.getName()) {
		case DETECTOR:
			break;
		case OBJECTIVE:
			break;
		case IMAGE:
			break;
		case LIGHTSRC:
			break;
		case CHANNEL:
			break;
		case EXPERIMENT:
			try {
				String idx=model.getImageOMEData().getLinkedExperiment().getID();
				if(idx==null){
					idx=MetadataTools.createLSID("Experiment",model.getOME().sizeOfExperimentList());
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case IMGENV:
			break;
		case FILTER:
			break;
		case DICHROIC:
			break;

		default:
			LOGGER.warning("[SAVE] Component not found: "+prefs.getName());
			break;
		}
		
	}
	
//	private void saveExperiment(MetadataPreferences prefs)
//	{
//		List<Boolean> tagSelected=prefs.selectionList();
//		List<TagData> tagList=prefs.getTagList();
//		for(int i=0; i<tagList.size(); i++){
//			if(tagSelected.get(i)){
//				saveExperimentTag(tagList.get(i));
//			}
//		}
//		
//		
//	}
//	private void saveExperimentTag(TagData t)
//	{
//		switch (t.getTagLabel().getText()) {
//		case "Description: ":
//			omexmlMeta.setExperimentDescription(t.getTagValue(), 0);
//			break;
//		case "Type: ":
////			omexmlMeta.setExperimentType(t.getTagValue(), 0);
//			break;
//		case "Experimenter Name: ":
//			break;
//		default:
//			break;
//		}
//		
//	}
	
	private String getMetaXml() throws Exception
	{
		ServiceFactory factory = new ServiceFactory();
		OMEXMLService service = factory.getInstance(OMEXMLService.class);
//		 System.out.println("Full OME-XML dump:");
		 String xml = service.getOMEXML(omexmlMeta);
//		 System.out.println(xml);
		 return xml;
	}
	
	private OME metadataToOME(IMetadata md) throws Exception
	{
		ServiceFactory factory = new ServiceFactory();
		OMEXMLService service = factory.getInstance(OMEXMLService.class);
		return (OME)service.createOMEXMLRoot(service.getOMEXML(md));
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
//		if (e.getValueIsAdjusting() == false) {
//	        if (objList.getSelectedIndex() != -1) {
//	        	CardLayout cl=(CardLayout) cardPane.getLayout();
//	        	System.out.println(objList.getSelectedValue());
//	    		cl.show(cardPane, (String) objList.getSelectedValue());
//	    		
//	        }
//	    }
		
	}

	// omeFromFile ergaenzen mit omeFromGUI oder ueberschreiben
	private void saveMetaData(File file)
	{
		XMLWriter writer=new XMLWriter();
		OME omeFromFile=null;
		OME omeFromGUI=null;
//		if(file.exists() && overwriteOutputFile(file)){
//			// read ome from file
//			try {
//				omeFromFile=readXMLFile(file);
//				
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}else{
		
//		if(file.exists()){
//			if(!overwriteOutputFile(file)){
//				//TODO rename file
//			}
//		}
//			
//			try {
//				omeFromGUI=getOme();
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
////		}

		//ergaenzt bestehende datei
		try {
			writeToMetaDataStore();
			writer.writeFile(file, omeStore.getOME(), false);
			LOGGER.info("[SAVE] save to "+file.getAbsolutePath());
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
//		}
			
//			IFormatWriter writer = new ImageWriter();
//			writer.setMetadataRetrieve(omexmlMeta);
//			try {
//				writer.setId(FileSystems.getDefault().getPath("C:\\Users\\Kunis\\Work\\Tmp\\data.ome.tif").toString());
//			} catch (FormatException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			////			  data = new byte[SERIES_COUNT][PLANES_COUNT][SIZE];
//			////			  for (int s = 0; s < SERIES_COUNT; s++) {
//			////			    writer.setSeries(s);
//			////			    for (int p = 0; p < PLANES_COUNT; p++) {
//			////			      byte[] img = makeImg();
//			////			      writer.saveBytes(p, img);
//			////			      data[s][p] = img;
//			////			    }
//			////			  }
//			try {
//				writer.close();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			
			//oder:
			
			
			
			
			//oder:
//			PrintWriter out=null;
//			try {
//				writeToMetaDataStore();
//				out = new PrintWriter(fname);
//				out.println(getMetaXml());
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			out.close();
	}


//TODO: test fileformat, get selected file	
	public void actionPerformed(ActionEvent evt) 
	{

		//Handle save button action.
		if (evt.getSource() == saveBtn) {//val==JFileChooser.APPROVE_OPTION
			//save to existing file-> overwrite or add?
//			String fname=FileSystems.getDefault().getPath("C:\\Users\\Kunis\\Work\\Tmp\\data3.ome").toString();
			String fname = srcImageFName+".ome";
			File file=new File(fname);//fchooser.getSelectedFile();
			// first solution: overwrite metadata
			saveMetaData(file);
			
		}else if (evt.getSource() == chancelBtn){
			SwingUtilities.windowForComponent(this).dispose();
		}
	}

//	private boolean overwriteOutputFile(File file)
//	{
//		boolean overwrite=false;
//		
//			int result=JOptionPane.showConfirmDialog(saveBtn.getParent(),
//							"File exists, overwrite?","File exists",
//							JOptionPane.YES_NO_CANCEL_OPTION);
//			switch (result) {
//			case JOptionPane.YES_OPTION: 
//				overwrite=true;
//				break;
//			default: 
//				overwrite=false;
//				break;
//			}
//		
//		return overwrite;
//	}
	
	private OME readXMLFile(File file) throws Exception
	{
//		InputStream source = this.getClass().getResourceAsStream(file.getAbsolutePath());
//        ServiceFactory sf = new ServiceFactory();
//        OMEXMLService service = sf.getInstance(OMEXMLService.class);
//        
//        Templates update201306 =
//                XMLTools.getStylesheet(XSLT_201306, OMEXMLServiceImpl.class);
//        
//        String xml = XMLTools.transformXML(
//                new StreamSource(source), update201306);
//        service.convertMetadata(xml, omexmlMeta);
////        OME ome = (OME) service.createOMEXMLRoot(xml);
//        return null;
        
//        oder
		
		ServiceFactory factory = new ServiceFactory();
		OMEXMLService service = factory.getInstance(OMEXMLService.class);
	    omexmlMeta = service.createOMEXMLMetadata();
	    IFormatReader reader = new OMEXMLReader();
	    reader.setMetadataStore(omexmlMeta);
	    reader.setId(file.getAbsolutePath());
	    
	    return null;
	}
	
	static URL fileToURL(File file){
        URL url = null;
        try {
            url = new URL("file://" + file.getPath());
            // Sonderzeichen (z.B. Leerzeichen) bleiben erhalten
            System.out.println(url);
            // Sonderzeichen (z.B. Leerzeichen) werden codiert
            url = file.toURI().toURL();
            System.out.println(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    } 
	
}
