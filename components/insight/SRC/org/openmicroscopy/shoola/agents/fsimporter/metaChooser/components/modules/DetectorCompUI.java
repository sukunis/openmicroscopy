package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;


























import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules.ElementsCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.ModuleConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.TagNames;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagData;

import ome.units.UNITS;
import ome.units.quantity.ElectricPotential;
import ome.units.unit.Unit;
import ome.xml.model.Detector;
import ome.xml.model.DetectorSettings;
import ome.xml.model.Image;
import ome.xml.model.LightSource;
import ome.xml.model.Objective;
import ome.xml.model.enums.DetectorType;
import ome.xml.model.enums.EnumerationException;
import ome.xml.model.enums.LaserMedium;
import loci.formats.MetadataTools;
import loci.formats.meta.IMetadata;

public class DetectorCompUI extends ElementsCompUI
{
	
	private TagData model;
	private TagData manufact;
	private TagData type;
	/** fixed zoom */
	private TagData zoom;
	private TagData amplGain;//==emGain
	
	
	
	private String detectorId;
	
	private Detector detector;
	private DetectorSettingsCompUI detectorSettUI;
	private List<Detector> availableDetectors;
	
	private int linkChannelIdx;
	
	private Box box;
	
	private void initTagList()
	{
		tagList=new ArrayList<TagData>();
		tagList.add(model);
		tagList.add(manufact);
		tagList.add(type);
		tagList.add(zoom);
		tagList.add(amplGain);
		
		
	}
	
	public boolean userInput()
	{
		boolean result=false;
		if(tagList!=null){
			for(int i=0; i<tagList.size();i++){
				boolean val=tagList.get(i)!=null ? tagList.get(i).valueChanged() : false;
				result= result || val;
			}
		}
		return (result || detectorSettUI.userInput()|| dataChanged);
	}


	
	public DetectorCompUI(ModuleConfiguration objConf) 
	{	
		detectorSettUI=new DetectorSettingsCompUI(objConf);
		initGUI();
		if(objConf==null)
			createDummyPane(false);
		else
			createDummyPane(objConf.getTagList(),false);
	}


	
	public void clearList()
	{
		availableDetectors=null;
		LOGGER.info("[LIST] delete detector elements");
	}
	
	public void addToList(List<Detector> list)
	{
		if(list==null || list.size()==0)
			return;
		
		if(availableDetectors==null){
			availableDetectors=new ArrayList<Detector>();
		}
		
		for(int i=0; i<list.size(); i++){
			availableDetectors.add(list.get(i));
		}
	}
	
	private void initGUI()
	{
		setLayout(new BorderLayout(5,5));
		setBorder(BorderFactory.createCompoundBorder(new TitledBorder(""),
						BorderFactory.createEmptyBorder(10,10,10,10)));
		buildComp=false;
		labels= new ArrayList<JLabel>();
		comp = new ArrayList<JComponent>();
		
		gridbag = new GridBagLayout();
		c = new GridBagConstraints();
		
		globalPane=new JPanel();
		globalPane.setLayout(gridbag);
		
		box=Box.createVerticalBox();
		box.add(globalPane);
		
		JButton editBtn=new JButton("Selection");
		editBtn.setAlignmentX(Component.RIGHT_ALIGNMENT);
		editBtn.setEnabled(true);
		editBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				DetectorEditor creator = new DetectorEditor(new JFrame(),"Select Detector",
						availableDetectors);
				Detector selected=creator.getDetector();  
				if(selected!=null ){
					dataChanged=true;
					detector=selected;
					setGUIData();
					revalidate();
					repaint();
				}
			}
		});
		
		add(box,BorderLayout.NORTH);
		add(editBtn,BorderLayout.SOUTH);
	}
	
	
	public boolean addData(Detector d, boolean overwrite)
	{
		boolean conflicts=false;
		if(overwrite){
			replaceData(d);
			LOGGER.info("[DATA] -- replace DETECTOR data");
		}else
			try {
				completeData(d);
				LOGGER.info("[DATA] -- complete DETECTOR data");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		setGUIData();
		return conflicts;
	}
	
	private void replaceData(Detector d)
	{
		if(d!=null){
			detector=d;
		}
	}
	
	private void completeData(Detector d) throws Exception
	{
		//copy input fields
		Detector copyIn=null;
		if(detector!=null){
			getData();
			copyIn=new Detector(detector);
		}

		replaceData(d);

		// set input field values again
		if(copyIn!=null){
			String mo=copyIn.getModel();
			String ma=copyIn.getManufacturer();
			DetectorType t=copyIn.getType();
			ElectricPotential v=copyIn.getVoltage();
			Double o = copyIn.getOffset();
			Double z=copyIn.getZoom();
			Double a=copyIn.getAmplificationGain();
			Double g=copyIn.getGain();

			if(mo!=null && !mo.equals("")) detector.setModel(mo);
			if(ma!=null && !ma.equals("")) detector.setManufacturer(ma);
			if(t!=null) detector.setType(t);
			if(v!=null) detector.setVoltage(v);
			if(o!=null) detector.setOffset(o);
			if(z!=null) detector.setZoom(z);
			if(a!=null) detector.setAmplificationGain(a);
			if(g!=null) detector.setGain(g);
		}
	}
	
	//TODO: is this the right place for implementation
	public void addData(DetectorSettings ds,boolean overwrite)
	{
		if(detectorSettUI!=null){
			detectorSettUI.addData(ds,overwrite);
		}
	}
	
	
	private void setGUIData()
	{
		if(detector!=null){
			try{setModel(detector.getModel(), ElementsCompUI.REQUIRED);
			} catch (NullPointerException e) { }
			try{setManufact(detector.getManufacturer(),  ElementsCompUI.REQUIRED);
			} catch (NullPointerException e) { }
			try{setType(detector.getType(),  ElementsCompUI.REQUIRED);
			} catch (NullPointerException e) { }
			
			try{setZoom(detector.getZoom(), ElementsCompUI.REQUIRED);
			} catch (NullPointerException e) { }
			try{setAmplGain(detector.getAmplificationGain(),  ElementsCompUI.REQUIRED);
			} catch (NullPointerException e) { }
		}
	}
	
	private void readGUIInput() throws Exception
	{
		LOGGER.info("[DATA] -- read gui input detector");
		if(detector==null)
			createNewElement();
		
		try{
		detector.setModel(model.getTagValue().equals("")?
				null : model.getTagValue());
		}catch(Exception e){
			LOGGER.error("[DATA] can't read DETECTOR model input");
		}
		try{
	
		detector.setManufacturer(manufact.getTagValue().equals("")?
				null : manufact.getTagValue());
		}catch(Exception e){
			LOGGER.error("[DATA] can't read DETECTOR manufacturer input");
		}
		try{
		detector.setType(parseDetectorType(type.getTagValue()));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read DETECTOR type input");
		}
		try{
		detector.setZoom(zoom.getTagValue().equals("")?
				null : Double.valueOf(zoom.getTagValue()));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read DETECTOR zoom input");
		}
		try{
		detector.setAmplificationGain(amplGain.getTagValue().equals("")?
				null : Double.valueOf(amplGain.getTagValue()));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read DETECTOR amplification gain input");
		}
		
	}
	
	private DetectorType parseDetectorType(String c) 
	{
		if(c==null || c.equals(""))
			return null;
		
		DetectorType m=null;
		try{
			m=DetectorType.fromString(c);
		}catch(EnumerationException e){
			LOGGER.warn("DetectorType: "+c+" is not supported");
			m=DetectorType.OTHER;
		}
		return m;
	}
	
	private void createNewElement() {
		detector=new Detector();		
	}

	/**
	 * Read gui input if necessary.
	 * @return detector object
	 * @throws Exception
	 */
	public Detector getData() throws Exception
	{
		if(userInput())
			readGUIInput();
		return detector;
	}
	
	public DetectorSettingsCompUI getSettings()
	{
		return detectorSettUI;
	}
	
	public void buildComponents()
	{
		labels.clear();
		comp.clear();
		
		addTagToGUI(model);
		addTagToGUI(manufact);
		addTagToGUI(type);
		addTagToGUI(zoom);
		addTagToGUI(amplGain);
		
		
		addLabelTextRows(labels, comp, gridbag, globalPane);
		
		c.gridwidth = GridBagConstraints.REMAINDER; //last
		c.anchor = GridBagConstraints.WEST;
		c.weightx = 1.0;
		
		detectorSettUI.buildComponents();
		box.removeAll();
		box.add(globalPane);
		box.add(Box.createVerticalStrut(10));
		box.add(detectorSettUI);
		
		buildComp=true;
		initTagList();
		dataChanged=false;
	}

	
	
	
	public void createDummyPane(boolean inactive)
	{
		clearCompData();
		
		setID(null);
		setModel(null, OPTIONAL);
		setManufact(null, OPTIONAL);
		setType(null, OPTIONAL);
	
		setZoom(null,OPTIONAL);
		setAmplGain(null,  OPTIONAL);
		
		if(inactive){
			model.setEnable(false);
			manufact.setEnable(false);
			type.setEnable(false);
			amplGain.setEnable(false);
			zoom.setEnable(false);
		}
	}
	
	public void createDummyPane(List<TagConfiguration> list,boolean inactive) 
	{
		if(list==null)
			createDummyPane(inactive);
		else{
			clearCompData();
			for(int i=0; i<list.size();i++){
				TagConfiguration t=list.get(i);
				String name=t.getName();
				String val=t.getValue();
				boolean prop=t.getProperty();
				if(name!=null  && t.isVisible()){
					setTag(name,val,prop,t.getUnit());
				}
			}
		}
	}

	

	public void clearDataValues()
	{
		clearCompData();
		detectorSettUI.clearDataValues();
	}
	
	private void clearCompData()
	{
		clearTagValue(model);
		clearTagValue(manufact);
		clearTagValue(type);
		clearTagValue(amplGain);
		
		clearTagValue(zoom);
	}
	
	public List<TagData> getActiveTags()
	{
		List<TagData> list = new ArrayList<TagData>();
		if(isActive(model)) list.add(model);
		if(isActive(manufact)) list.add(manufact);
		if(isActive(type)) list.add(type);
		if(isActive(zoom)) list.add(zoom);
		if(isActive(amplGain)) list.add(amplGain);
		
		
		return list;
	}
	
	
	
	
	public void setID(String value)
	{
		String val= (value != null) ? String.valueOf(value):"";
		detectorId=val;
	}
	
	public String getID()
	{
		return detectorId;
	}
	
		
	public void setModel(String value, boolean prop)
	{
		if(model == null) 
			model = new TagData(TagNames.MODEL,value,prop,TagData.TEXTFIELD);
		else 
			model.setTagValue(value,prop);
	}
	
	public void setManufact(String value, boolean prop)
	{
		if(manufact == null) 
			manufact = new TagData(TagNames.MANUFAC,value,prop,TagData.TEXTFIELD);
		else 
			manufact.setTagValue(value,prop);
	}
	
	public void setType(DetectorType value, boolean prop)
	{
		String val= (value != null)? value.getValue() : "";
		if(type == null) 
			type = new TagData(TagNames.TYPE,val,prop,TagData.COMBOBOX,getNames(DetectorType.class));
		else 
			type.setTagValue(val,prop);
	}
	
	//==em gain if type==PMT
	public void setAmplGain(Double value, boolean prop)
	{
		String val= (value != null) ? String.valueOf(value):"";
		if(amplGain == null) 
			amplGain = new TagData(TagNames.AMPLGAIN,val,prop,TagData.TEXTFIELD);
		else 
			amplGain.setTagValue(val,prop);
	}
	
	
	public void setZoom(Double value, boolean prop)
	{
		String val= (value != null) ? String.valueOf(value):"";
		if(zoom == null) 
			zoom = new TagData(TagNames.ZOOM,val,prop,TagData.TEXTFIELD);
		else 
			zoom.setTagValue(val,prop);
	}

	public void setFieldsExtern(boolean b) {
		dataChanged= dataChanged || b;		
	}
	
	/**
	 * Update tags with val from list
	 */
	public void update(List<TagData> list) 
	{
		for(TagData t: list){
			if(t.valueChanged()){
				setTag(t);
			}
		}
	}

	private void setTag(TagData t)
	{
		setTag(t.getTagName(),t.getTagValue(),t.getTagProp(),t.getTagUnit());
	}
	
	private void setTag(String name,String val,boolean prop,Unit unit)
	{
		switch (name) {
		case TagNames.MODEL: 
			try{
				if(val!=null){
					setModel(val,prop);
					dataChanged=true;
				}else{
					setModel(null,OPTIONAL);
				}
			}
			catch(Exception e){
				setModel(null,OPTIONAL);
			}
			model.setVisible(true);
		
			break;
		case TagNames.MANUFAC: 
			try{
				if(val!=null){
					setManufact(val, prop);
					dataChanged=true;
				}else{
					setManufact(null, OPTIONAL);
				}
			}
			catch(Exception e){
				setManufact(null, OPTIONAL);
			}
			manufact.setVisible(true);
			
			break;
		case TagNames.TYPE:
			try{
				DetectorType value= DetectorType.fromString(val);
				setType(value, prop);
				dataChanged=true;
			}
			catch(Exception e){
				setType(null,OPTIONAL);
			}
			type.setVisible(true);
			break;
		case TagNames.ZOOM:
			try{
				setZoom(Double.valueOf(val), prop);
				dataChanged=true;
			}
			catch(Exception e){
				setZoom(null, OPTIONAL);
			}
			zoom.setVisible(true);
			break;
		case TagNames.AMPLGAIN:
			try{
				setAmplGain(Double.valueOf(val), prop);
				dataChanged=true;
			}
			catch(Exception e){
				setAmplGain(null, OPTIONAL);
			}
			amplGain.setVisible(true);
			break;
		default: LOGGER.warn("[CONF] unknown tag: "+name );break;
		}
	}

	


	
	
}
