package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.view;

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
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import ome.units.quantity.ElectricPotential;
import ome.units.unit.Unit;
import ome.xml.model.Arc;
import ome.xml.model.Detector;
import ome.xml.model.DetectorSettings;
import ome.xml.model.enums.Binning;
import ome.xml.model.enums.DetectorType;
import ome.xml.model.enums.EnumerationException;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules.DetectorEditor;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules.ElementsCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model.DetectorModel;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.ModuleConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.TagNames;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagData;
import org.slf4j.LoggerFactory;

public class DetectorViewer extends ModuleViewer{

	private static final org.slf4j.Logger LOGGER =
			LoggerFactory.getLogger(DetectorViewer.class);

	private DetectorModel data;
	private Box box;
	private int index;

	// available element tags
	private TagData model;
	private TagData manufact;
	private TagData type;
	/** fixed zoom */
	private TagData zoom;
	private TagData amplGain;//==emGain

	//available element setting tags
	/** variable value, that also can change during the acq.
	 * See also DetectorSettings*/
	private TagData gain;
	/** voltage of detector*/
	private TagData voltage;
	/** offset of detector*/
	private TagData offset;
	/** confocal or scanning zoom*/
	private TagData confocalZoom;
	/**represents the number of pixels that are combined to form larger pixels*/
	private TagData binning;
	private TagData subarray;

	

	/**
	 * Creates a new instance.
	 * @param model Reference to model.
	 */
	public DetectorViewer(DetectorModel model,ModuleConfiguration conf,int index)
	{
		System.out.println("# DetectorViewer::newInstance("+(model!=null?"model":"null")+") "+index);
		this.data=model;
		this.index=index;
		initComponents(conf);
		initTagList();
		buildGUI();
	}

	private void initTagList()
	{
		tagList=new ArrayList<TagData>();
		tagList.add(model);
		tagList.add(manufact);
		tagList.add(type);
		tagList.add(zoom);
		tagList.add(amplGain);


		tagList.add(gain);
		tagList.add(voltage);
		tagList.add(offset);
		tagList.add(confocalZoom);
		tagList.add(binning);
		tagList.add(subarray);
	}

	/**
	 * Builds and lay out GUI.
	 */
	private void buildGUI() 
	{
		List<JLabel> labels= new ArrayList<JLabel>();
		List<JComponent> comp=new ArrayList<JComponent>();
		addTagToGUI(model,labels,comp);
		addTagToGUI(manufact,labels,comp);
		addTagToGUI(type,labels,comp);
		addTagToGUI(zoom,labels,comp);
		addTagToGUI(amplGain,labels,comp);

		addLabelTextRows(labels, comp, gridbag, globalPane);

		c.gridwidth = GridBagConstraints.REMAINDER; //last
		c.anchor = GridBagConstraints.WEST;
		c.weightx = 1.0;

		//Settings
		GridBagConstraints cSett=new GridBagConstraints();
		GridBagLayout gridbagSett = new GridBagLayout();
		List<JLabel> labelsSett= new ArrayList<JLabel>();
		List<JComponent> compSett=new ArrayList<JComponent>();
		JPanel settingsPane=new JPanel(gridbagSett);
		addLabelToGUI(new JLabel("Settings:"),labelsSett,compSett);
		addTagToGUI(gain,labelsSett,compSett);
		addTagToGUI(voltage,labelsSett,compSett);
		addTagToGUI(offset,labelsSett,compSett);
		addTagToGUI(confocalZoom,labelsSett,compSett);
		addTagToGUI(binning,labelsSett,compSett);
		addTagToGUI(subarray,labelsSett,compSett);

		addLabelTextRows(labelsSett, compSett, gridbag, settingsPane);

		c.gridwidth = GridBagConstraints.REMAINDER; //last
		c.anchor = GridBagConstraints.WEST;
		c.weightx = 1.0;

		box.add(Box.createVerticalStrut(20));
		box.add(settingsPane);

		// set data
		setGUIData();
		setSettingsGUIData();
		dataChanged=false;
	}

	/**
	 * Initialize components.
	 */
	private void initComponents(ModuleConfiguration conf) 
	{
		// init view layout
		setLayout(new BorderLayout(5,5));
		setBorder(BorderFactory.createCompoundBorder(new TitledBorder(""),
				BorderFactory.createEmptyBorder(5,10,5,10)));

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
						data.getList());
				Detector selected=creator.getDetector();  
				if(selected!=null ){
					try {
						data.addData(selected, true,index);
					} catch (Exception e1) {
						LOGGER.warn("Can't set data of selected detector! "+e1);
					}
					setGUIData();
					dataChanged=true;
					revalidate();
					repaint();
				}
			}
		});
		add(box,BorderLayout.NORTH);
		add(editBtn,BorderLayout.SOUTH);

		// init tag layout
		List<TagConfiguration> list=conf.getTagList();
		List<TagConfiguration> settList=conf.getSettingList();
		initTags(list);
		initTags(settList);
	}



	/**
	 * Init given tag and mark it as visible.
	 * @param t
	 */
	protected void initTag(TagConfiguration t) 
	{
		predefinitionValLoaded=predefinitionValLoaded || (t.getValue()!=null && !t.getValue().equals(""));
		String name=t.getName();
		Boolean prop=t.getProperty();
		switch (name) {
		case TagNames.MODEL: 
			setModel(null,OPTIONAL);
			model.setVisible(true);
			break;
		case TagNames.MANUFAC: 
			setManufact(null, OPTIONAL);
			manufact.setVisible(true);
			break;
		case TagNames.TYPE:
			setType(null,OPTIONAL);
			type.setVisible(true);
			break;
		case TagNames.ZOOM:
			setZoom(null, OPTIONAL);
			zoom.setVisible(true);
			break;
		case TagNames.AMPLGAIN:
			setAmplGain(null, OPTIONAL);
			amplGain.setVisible(true);
			break;
		case TagNames.GAIN:
			setGain(null,OPTIONAL);
			gain.setVisible(true);
			break;
		case TagNames.VOLTAGE:
			setVoltage(null, OPTIONAL);
			voltage.setVisible(true);
			break;
		case TagNames.OFFSET:
			setOffset(null, OPTIONAL);
			offset.setVisible(true);
			break;
		case TagNames.CONFZOOM:
			setConfocalZoom(null, prop);
			confocalZoom.setVisible(true);
			break;
		case TagNames.BINNING:
			setBinning(null, prop);
			binning.setVisible(true);
			break;
		case TagNames.SUBARRAY:
			//TODO
			setSubarray(null, prop);
			subarray.setVisible(true);
			break;
		default: LOGGER.warn("[CONF] unknown tag: "+name );break;
		}
	}

	/**
	 * Show data of detector
	 */
	private void setGUIData() 
	{
		if(data==null || data.getNumberOfElements()==0)
			return;
		Detector detector=data.getDetector(index);
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

	private void setSettingsGUIData()
	{
		if(data==null)
			return;
		DetectorSettings settings = data.getSettings(index);
		if(settings!=null){
			try{setGain(settings.getGain(), ElementsCompUI.REQUIRED);
			} catch (NullPointerException e) { }
			try{setVoltage(settings.getVoltage(), ElementsCompUI.REQUIRED);
			} catch (NullPointerException e) { }
			try{ setOffset(settings.getOffset(), ElementsCompUI.REQUIRED);
			} catch (NullPointerException e) { }
			try{ setConfocalZoom(settings.getZoom(), ElementsCompUI.REQUIRED);
			} catch (NullPointerException e) { }
			try{ setBinning(settings.getBinning(), ElementsCompUI.REQUIRED);
			} catch (NullPointerException e) { }
			//TODO
			try{ setSubarray(null, ElementsCompUI.REQUIRED);
			} catch (NullPointerException e) { }
		}

	}



	/*------------------------------------------------------
	 * Set methods data Values
	 * -----------------------------------------------------*/

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


	/*------------------------------------------------------
	 * Set methods settings Values
	 * -----------------------------------------------------*/
	public void setGain(Double value, boolean prop)
	{
		String val= (value != null) ? String.valueOf(value):"";
		if(gain == null) 
			gain = new TagData(TagNames.GAIN,val,prop,TagData.TEXTFIELD);
		else 
			gain.setTagValue(val,prop);
	}
	//TODO
	public void setBinning(Binning value, boolean prop)
	{
		String val= (value != null)? value.getValue() : "";
		if(binning == null) 
			binning = new TagData(TagNames.BINNING,val,prop,TagData.COMBOBOX,getNames(Binning.class));
		else 
			binning.setTagValue(val,prop);
	}
	public void setVoltage(ElectricPotential value, boolean prop)
	{
		String val= (value != null)? String.valueOf(value.value()) : "";
		Unit unit=(value!=null) ? value.unit() :TagNames.VOLTAGE_UNIT;
		if(voltage == null) 
			voltage = new TagData(TagNames.VOLTAGE,val,unit,prop,TagData.TEXTFIELD);
		else 
			voltage.setTagValue(val,unit,prop);
	}
	public void setOffset(Double value, boolean prop)
	{
		String val= (value != null) ? String.valueOf(value):"";
		if(offset == null) 
			offset = new TagData(TagNames.OFFSET,val,prop,TagData.TEXTFIELD);
		else 
			offset.setTagValue(val,prop);
	}
	public void setConfocalZoom(Double value, boolean prop)
	{
		String val= (value != null) ? String.valueOf(value):"";
		if(confocalZoom == null) 
			confocalZoom = new TagData(TagNames.CONFZOOM,val,prop,TagData.TEXTFIELD);
		else 
			confocalZoom.setTagValue(val,prop);
	}
	public void setSubarray(String value, boolean prop)
	{
		if(subarray == null) 
			subarray = new TagData(TagNames.SUBARRAY,value,prop,TagData.TEXTFIELD);
		else 
			subarray.setTagValue(value,prop);
	}

	public String getGain()
	{
		return gain.getTagValue();
	}

	public String getBinning()
	{
		return binning.getTagValue();
	}

	@Override
	public void saveData() 
	{
		if(data==null)
			data=new DetectorModel();
		
		Detector detector =data.getDetector(index);
		if(detector==null){
			detector = new Detector();
			try {
				data.addData(detector, true, index);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		System.out.println("# DetectorViewer::saveData() "+index);
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
		// --- Settings --------------------
		DetectorSettings settings=data.getSettings(index);
		if(settings==null)
			settings = new DetectorSettings();

		try{
			settings.setGain(parseToDouble(gain.getTagValue()));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read DETECTOR SETT  gain input");
		}
		try{
			settings.setVoltage(parseElectricPotential(voltage.getTagValue(), voltage.getTagUnit()));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read DETECTOR SETT voltage input");
		}
		try{
			settings.setOffset(parseToDouble(offset.getTagValue()));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read DETECTOR SETT offset input");
		}


		try{
			settings.setZoom(parseToDouble(confocalZoom.getTagValue()));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read DETECTOR SETT zoom input");
		}
		try{
			settings.setBinning(parseBinning(binning.getTagValue()));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read DETECTOR SETT binning input");
		}
		
		dataChanged=false;
	}
	public static DetectorType parseDetectorType(String c) 
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
	public static ElectricPotential parseElectricPotential(String c, Unit unit) 
	{
		if(c==null || c.equals(""))
			return null;

		ElectricPotential p=null;

		return new ElectricPotential(Double.valueOf(c), unit);
	}
	public static Binning parseBinning(String c) throws EnumerationException
	{
		if(c==null || c.equals(""))
			return null;

		return Binning.fromString(c);
	}
	
	public List<TagData> getChangedTags()
	{
		List<TagData> list = new ArrayList<TagData>();
		if(inputAt(model)) list.add(model);
		if(inputAt(manufact)) list.add(manufact);
		if(inputAt(type)) list.add(type);
		if(inputAt(zoom)) list.add(zoom);
		if(inputAt(amplGain)) list.add(amplGain);
		
		// settings
		if(inputAt(gain))list.add(gain);
		if(inputAt(voltage))list.add(voltage);
		if(inputAt(offset))list.add(offset);
		if(inputAt(confocalZoom))list.add(confocalZoom);
		if(inputAt(binning))list.add(binning);
		if(inputAt(subarray))list.add(subarray);
		return list;
	}

	public int getIndex()
	{
		return index;
	}
	
}

