package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor;

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

















import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.ElementsCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.TagData;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.ModuleConfiguration;

import ome.units.UNITS;
import ome.units.quantity.ElectricPotential;
import ome.units.unit.Unit;
import ome.xml.model.Detector;
import ome.xml.model.DetectorSettings;
import ome.xml.model.LightSource;
import ome.xml.model.Objective;
import ome.xml.model.enums.DetectorType;
import loci.formats.MetadataTools;
import loci.formats.meta.IMetadata;

public class DetectorCompUI extends ElementsCompUI
{
	private final String L_MODEL="Model";
	private final String L_MANUFAC="Manufacturer";
	private final String L_TYPE="Type";
	private final String L_ZOOM="Zoom";
	private final String L_AMPLGAIN="AmplificationGain";
//	private final String L_GAIN="Gain";
//	private final String L_VOLTAGE="Voltage";
//	private final String L_OFFSET="Offset";
	
	
	
	private TagData model;
	private TagData manufact;
	private TagData type;
	/** fixed zoom */
	private TagData zoom;
	private TagData amplGain;//==emGain
//	private TagData gain;
//	private TagData offset;
//	private TagData voltage;
	
	private List<TagData> tagList;
	
	private Unit<ElectricPotential> voltageUnit;
	private String detectorId;
	
	private Detector detector;
	private DetectorSettingsCompUI detectorSettUI;
	private List<Detector> availableDetectors;
	
	private int linkChannelIdx;
	
	private Box box;
	private boolean setFields;
	
	private void initTagList()
	{
		tagList=new ArrayList<TagData>();
		tagList.add(model);
		tagList.add(manufact);
		tagList.add(type);
		tagList.add(zoom);
		tagList.add(amplGain);
//		tagList.add(gain);
//		tagList.add(offset);
//		tagList.add(voltage);
		
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
		return (result || detectorSettUI.userInput()|| setFields);
	}


	
	public DetectorCompUI(ModuleConfiguration objConf) 
	{	
		voltageUnit=UNITS.V;
		detectorSettUI=new DetectorSettingsCompUI(objConf);
		initGUI();
		if(objConf==null)
			createDummyPane(false);
		else
			createDummyPane(objConf.getTagList(),false);
	}

	public void setList(List<Detector> _list)
	{
		availableDetectors=_list;
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
		
		JButton editBtn=new JButton("Select");
		editBtn.setAlignmentX(Component.RIGHT_ALIGNMENT);
		editBtn.setEnabled(true);
		editBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				DetectorEditor creator = new DetectorEditor(new JFrame(),"Select Detector",
						availableDetectors);
				Detector selected=creator.getDetector();  
				if(selected!=null ){
					detector=selected;
					setGUIData();
//					buildComponents();   
					revalidate();
					repaint();
				}
			}
		});
		
//		add(new TitledSeparator("Detector", 3, TitledBorder.DEFAULT_POSITION),BorderLayout.NORTH);
		add(box,BorderLayout.NORTH);
		add(editBtn,BorderLayout.SOUTH);
	}
	
	public boolean addData(Detector d, boolean overwrite) 
	{
		boolean conflicts=false;
		if(detector!=null)
		{
			if(d!=null){
				String mo=d.getModel();
				String ma=d.getManufacturer();
				DetectorType t=d.getType();
				ElectricPotential v=d.getVoltage();
				Double o = d.getOffset();
				Double z=d.getZoom();
				Double a=d.getAmplificationGain();
				Double g=d.getGain();
				if(overwrite){
					if(d.getID()!=null && !d.getID().equals(""))
						detector.setID(d.getID());
					if(!mo.equals("")) detector.setModel(mo);
					if(!ma.equals("")) detector.setManufacturer(ma);
					if(t!=null) detector.setType(t);
					if(v!=null) detector.setVoltage(v);
					if(o!=null) detector.setOffset(o);
					if(z!=null) detector.setZoom(z);
					if(a!=null) detector.setAmplificationGain(a);
					if(g!=null) detector.setGain(g);
					LOGGER.info("[DATA] overwrite DETECTOR data");
				}else{
					if(detector.getID()==null || detector.getID().equals(""))
						detector.setID(d.getID());
					if(detector.getModel()==null || detector.getModel().equals("") )
						detector.setModel(mo);
					if(detector.getManufacturer()==null || detector.getManufacturer().equals(""))
						detector.setManufacturer(ma);
					if(detector.getType()==null)detector.setType(t);
					if(detector.getVoltage()==null) detector.setVoltage(v);
					if(detector.getOffset()==null) detector.setOffset(o);
					if(detector.getZoom()==null) detector.setZoom(z);
					if(detector.getAmplificationGain()==null) detector.setAmplificationGain(a);
					if(detector.getGain()==null) detector.setGain(g);
					LOGGER.info("[DATA] complete DETECTOR data");
				}
			}
		}else if(d!=null){
			detector=d;
			LOGGER.info("[DATA] add DETECTOR data");
		}
		setGUIData();
		return conflicts;
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
//			try{setVoltage(detector.getVoltage(),  ElementsCompUI.REQUIRED);
//			} catch (NullPointerException e) { }
//			try{setOffset(detector.getOffset(),  ElementsCompUI.REQUIRED);
//			} catch (NullPointerException e) { }
			try{setZoom(detector.getZoom(), ElementsCompUI.REQUIRED);
			} catch (NullPointerException e) { }
			try{setAmplGain(detector.getAmplificationGain(),  ElementsCompUI.REQUIRED);
			//TODO: richtiger Gain????
			} catch (NullPointerException e) { }
//			try{ setGain(detector.getGain(), ElementsCompUI.REQUIRED);
//			} catch (NullPointerException e) { }
		}
	}
	
	private void readGUIInput() throws Exception
	{
		
		if(detector==null)
			createNewElement();
		try{
		detector.setModel(model.getTagValue().equals("")?
				null : model.getTagValue());
		}catch(Exception e){
			LOGGER.severe("[DATA] can't read DETECTOR model input");
		}
		try{
	
		detector.setManufacturer(manufact.getTagValue().equals("")?
				null : manufact.getTagValue());
		}catch(Exception e){
			LOGGER.severe("[DATA] can't read DETECTOR manufacturer input");
		}
		try{
		detector.setType(type.getTagValue().equals("")?
				null : DetectorType.fromString(type.getTagValue()));
		}catch(Exception e){
			LOGGER.severe("[DATA] can't read DETECTOR type input");
		}
		try{
		detector.setZoom(zoom.getTagValue().equals("")?
				null : Double.valueOf(zoom.getTagValue()));
		}catch(Exception e){
			LOGGER.severe("[DATA] can't read DETECTOR zoom input");
		}
		try{
		detector.setAmplificationGain(amplGain.getTagValue().equals("")?
				null : Double.valueOf(amplGain.getTagValue()));
		}catch(Exception e){
			LOGGER.severe("[DATA] can't read DETECTOR amplification gain input");
		}
//		detector.setGain(gain.getTagValue().equals("")?
//				null : Double.valueOf(gain.getTagValue()));
//	
//		detector.setVoltage(voltage.getTagValue().equals("")?
//				null : new ElectricPotential(Double.valueOf(voltage.getTagValue()), voltageUnit));
//	
//		detector.setOffset(offset.getTagValue().equals("")?
//				null : Double.valueOf(offset.getTagValue()));
		
	}
	
	private void createNewElement() {
		detector=new Detector();		
	}

	
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
//		addTagToGUI(gain);
//		addTagToGUI(voltage);
//		addTagToGUI(offset);
		
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
		setFields=false;
	}

	//TODO: advanced properties shows by touch a button
	public void buildExtendedComponents(){

	}
	
//	public void showOptionPane() 
//	{
//		JButton newBtn=new JButton("New...");
//		newBtn.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				createNewElem();
//				
//			}
//		});
//		
//		JButton selectBtn=new JButton("Select from data...");
//		if(availableDetectors==null || availableDetectors.isEmpty())
//			selectBtn.setEnabled(false);
//		JButton addBtn=new JButton("Select from system...");
//		addBtn.setEnabled(false);
//		
//		GridBagConstraints c = new GridBagConstraints();
//		c.anchor = GridBagConstraints.WEST;
//		c.gridwidth = GridBagConstraints.REMAINDER;     //end row
//		c.fill = GridBagConstraints.HORIZONTAL;
//		c.weightx = 1.0;
//		
//		globalPane.add(newBtn,c);
//		globalPane.add(selectBtn,c);
//		globalPane.add(addBtn,c);
//		
//	}
	
	private void createNewElem()
	{
		globalPane.removeAll();
    	globalPane.setLayout(gridbag);
    	createDummyPane(false);
    	buildComponents();   
    	revalidate();
    	repaint();
	}
	
	
	
	public void createDummyPane(boolean inactive)
	{
		clearDataValues();
		
		setID(null);
		setModel(null, OPTIONAL);
		setManufact(null, OPTIONAL);
		setType(null, OPTIONAL);
//		setVoltage(null,  OPTIONAL);
//		setOffset(null, OPTIONAL);
		setZoom(null,OPTIONAL);
		setAmplGain(null,  OPTIONAL);
//		setGain(null, OPTIONAL);
		
		if(inactive){
			model.setEnable(false);
			manufact.setEnable(false);
			type.setEnable(false);
			amplGain.setEnable(false);
//			gain.setEnable(false);
//			voltage.setEnable(false);
//			offset.setEnable(false);
			zoom.setEnable(false);
		}
	}
	
	public void createDummyPane(List<TagConfiguration> list,boolean inactive) 
	{
		if(list==null)
			createDummyPane(inactive);
		else{
			clearDataValues();
			if(detector==null && list!=null && list.size()>0)
				createNewElement();
			for(int i=0; i<list.size();i++){
				TagConfiguration t=list.get(i);
				String name=t.getName();
				String val=t.getValue();
				boolean prop=t.getProperty()!= null ? Boolean.parseBoolean(t.getProperty()):
					OPTIONAL;
				if(name!=null){
					switch (name) {
					case L_MODEL: 
						try{
							setModel(val,prop);
							detector.setModel(val);
						}
						catch(Exception e){
							setModel(null,OPTIONAL);
						}
						model.setVisible(true);
						break;
					case L_MANUFAC: 
						try{
							setManufact(val, prop);
							detector.setManufacturer(val);
						}
						catch(Exception e){
							setManufact(null, OPTIONAL);
						}
						manufact.setVisible(true);
						break;
					case L_TYPE:
						try{
							DetectorType value= DetectorType.fromString(val);
							setType(value, prop);
							detector.setType(value);
						}
						catch(Exception e){
							setType(null,OPTIONAL);
						}
						type.setVisible(true);
						break;
					case L_ZOOM:
						try{
							setZoom(Double.valueOf(val), prop);
							detector.setZoom(Double.valueOf(val));
						}
						catch(Exception e){
							setZoom(null, OPTIONAL);
						}
						zoom.setVisible(true);
						break;
					case L_AMPLGAIN:
						try{
							setAmplGain(Double.valueOf(val), prop);
							detector.setZoom(Double.valueOf(val));
						}
						catch(Exception e){
							setAmplGain(null, OPTIONAL);
						}
						amplGain.setVisible(true);
						break;
//					case L_GAIN:
//						try{
//							setGain(Double.valueOf(val), prop);
//							detector.setGain(Double.valueOf(val));
//						}
//						catch(Exception e){
//							setGain(null,OPTIONAL);
//						}
//						gain.setVisible(true);
//						break;
//					case L_VOLTAGE:
//						try{
//							ElectricPotential value=new ElectricPotential(Double.valueOf(val), voltageUnit);
//							detector.setVoltage(value);
//						}
//						catch(Exception e){
//							setVoltage(null, OPTIONAL);
//						}
//						voltage.setVisible(true);
//						break;
//					case L_OFFSET:
//						try{
//							setOffset(Double.valueOf(val), prop);
//							detector.setOffset(Double.valueOf(val));
//						}
//						catch(Exception e){
//							setOffset(null, OPTIONAL);
//						}
//						offset.setVisible(true);
//						break;
					default: LOGGER.warning("[CONF] unknown tag: "+name );break;
					}
				}
			}
		}
	}

	public void clearDataValues()
	{
		clearTagValue(model);
		clearTagValue(manufact);
		clearTagValue(type);
		clearTagValue(amplGain);
//		clearTagValue(gain);
//		clearTagValue(voltage);
//		clearTagValue(offset);
		clearTagValue(zoom);
		if(detectorSettUI!=null) detectorSettUI.clearDataValues();
	}
	
	public List<TagData> getActiveTags()
	{
		List<TagData> list = new ArrayList<TagData>();
		if(isActive(model)) list.add(model);
		if(isActive(manufact)) list.add(manufact);
		if(isActive(type)) list.add(type);
		if(isActive(zoom)) list.add(zoom);
		if(isActive(amplGain)) list.add(amplGain);
//		if(isActive(gain)) list.add(gain);
//		if(isActive(offset)) list.add(offset);
//		if(isActive(voltage)) list.add(voltage);
		
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
			model = new TagData("Model: ",value,prop,TagData.TEXTFIELD);
		else 
			model.setTagValue(value,prop);
	}
	
	public void setManufact(String value, boolean prop)
	{
		if(manufact == null) 
			manufact = new TagData("Manufacturer: ",value,prop,TagData.TEXTFIELD);
		else 
			manufact.setTagValue(value,prop);
	}
	
	public void setType(DetectorType value, boolean prop)
	{
		String val= (value != null)? value.getValue() : "";
		if(type == null) 
			type = new TagData("Type: ",val,prop,TagData.COMBOBOX,getNames(DetectorType.class));
		else 
			type.setTagValue(val,prop);
	}
	
	
	public void setAmplGain(Double value, boolean prop)
	{
		String val= (value != null) ? String.valueOf(value):"";
		if(amplGain == null) 
			amplGain = new TagData("Amplification Gain: ",val,prop,TagData.TEXTFIELD);
		else 
			amplGain.setTagValue(val,prop);
	}
	
	//TODO
//	public void setGain(Double value, boolean prop)
//	{
//		String val= (value != null) ? String.valueOf(value):"";
//		if(gain == null) 
//			gain = new TagData("Gain: ",val,prop,TagData.TEXTFIELD);
//		else 
//			gain.setTagValue(val,prop);
//	}
	
	
//	public void setVoltage(ElectricPotential value, boolean prop)
//	{
//		String val= (value != null)? String.valueOf(value.value()) : "";
//		voltageUnit=(value!=null) ? value.unit() :voltageUnit;
//		if(voltage == null) 
//			voltage = new TagData("Voltage ["+voltageUnit.getSymbol()+"]: ",val,prop,TagData.TEXTFIELD);
//		else 
//			voltage.setTagValue(val,prop);
//	}
	
//	public void setOffset(Double value, boolean prop)
//	{
//		String val= (value != null) ? String.valueOf(value):"";
//		if(offset == null) 
//			offset = new TagData("Offset: ",val,prop,TagData.TEXTFIELD);
//		else 
//			offset.setTagValue(val,prop);
//	}
	
	public void setZoom(Double value, boolean prop)
	{
		String val= (value != null) ? String.valueOf(value):"";
		if(zoom == null) 
			zoom = new TagData("Zoom: ",val,prop,TagData.TEXTFIELD);
		else 
			zoom.setTagValue(val,prop);
	}

	public void setFieldsExtern(boolean b) {
		setFields= setFields || b;		
	}



	
	
}
