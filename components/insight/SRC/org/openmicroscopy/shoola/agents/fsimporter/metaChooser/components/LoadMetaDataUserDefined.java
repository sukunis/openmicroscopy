package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components;

import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;

import java.awt.BorderLayout;

import javax.swing.JList;
import javax.xml.transform.Templates;
import javax.xml.transform.stream.StreamSource;

import ome.xml.model.OME;

import java.awt.CardLayout;
import java.io.File;
import java.io.InputStream;

import loci.common.services.DependencyException;
import loci.common.services.ServiceFactory;
import loci.common.xml.XMLTools;
import loci.formats.IFormatReader;
import loci.formats.in.OMEXMLReader;
import loci.formats.meta.IMetadata;
import loci.formats.meta.MetadataStore;
import loci.formats.services.OMEXMLService;
import loci.formats.services.OMEXMLServiceImpl;

public class LoadMetaDataUserDefined extends JPanel {

	
	private static final String XSLT_PATH = "/transforms/";
	private static final String XSLT_201306 =
		    XSLT_PATH + "2013-06-to-2015-01.xsl";
	
	/**
	 * Create the panel.
	 */
	public LoadMetaDataUserDefined() {
		setLayout(new BorderLayout(0, 0));
		
		JSplitPane splitPane = new JSplitPane();
		add(splitPane, BorderLayout.CENTER);
		
		JList list = new JList();
		splitPane.setLeftComponent(list);
		
		JPanel panel = new JPanel();
		splitPane.setRightComponent(panel);
		panel.setLayout(new CardLayout(0, 0));


	}
	
	private OME readXMLFileToOme(File file) throws Exception
	{
		InputStream source = this.getClass().getResourceAsStream(file.getAbsolutePath());
        ServiceFactory sf = new ServiceFactory();
        OMEXMLService service = sf.getInstance(OMEXMLService.class);
        
        Templates update201306 =
                XMLTools.getStylesheet(XSLT_201306, OMEXMLServiceImpl.class);
        
        String xml = XMLTools.transformXML(
                new StreamSource(source), update201306);
        OME ome = (OME) service.createOMEXMLRoot(xml);
        return ome;
	}
	
//        oder
	private IMetadata readXMLFileToStore(File file) throws Exception	
	{
		ServiceFactory factory = new ServiceFactory();
		OMEXMLService service = factory.getInstance(OMEXMLService.class);
	    MetadataStore omexmlMeta = service.createOMEXMLMetadata();
	    IFormatReader reader = new OMEXMLReader();
	    reader.setMetadataStore(omexmlMeta);
	    reader.setId(file.getAbsolutePath());
	    
	    return (IMetadata) omexmlMeta;
	}

}
