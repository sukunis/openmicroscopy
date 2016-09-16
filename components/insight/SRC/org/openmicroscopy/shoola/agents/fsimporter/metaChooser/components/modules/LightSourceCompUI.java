package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.function.ObjLongConsumer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ome.units.quantity.Frequency;
import ome.units.quantity.Length;
import ome.units.quantity.Power;
import ome.units.unit.Unit;
import ome.xml.model.Arc;
import ome.xml.model.Filament;
import ome.xml.model.GenericExcitationSource;
import ome.xml.model.Laser;
import ome.xml.model.LightEmittingDiode;
import ome.xml.model.LightSource;
import ome.xml.model.LightSourceSettings;
import ome.xml.model.enums.ArcType;
import ome.xml.model.enums.EnumerationException;
import ome.xml.model.enums.FilamentType;
import ome.xml.model.enums.LaserMedium;
import ome.xml.model.enums.LaserType;
import ome.xml.model.enums.Pulse;
import ome.xml.model.primitives.PercentFraction;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.ModuleConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.TagNames;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.ExceptionDialog;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagData;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.WarningDialog;

import edu.emory.mathcs.backport.java.util.Arrays;

public class LightSourceCompUI extends ElementsCompUI 
{
	private CardLayout lightSrcCard; 
	private Box box;
	private JPanel settingsBox;
	private LightSourceSettings lightSrcSett;
	
	private List<LightSource> availableLightSrcList;
	
	public final static String LASER="Laser";
	public final static String ARC="Arc";
	public final static String FILAMENT="Filament";
	public final static String GENERIC_EXCITATION="GenericExcitationSource";
	public final static String LIGHT_EMITTING_DIODE="LightEmittingDiode";
	
	private final String[] sourceTypeList={LASER,ARC,FILAMENT,GENERIC_EXCITATION,LIGHT_EMITTING_DIODE};
	private JComboBox<String> sourceType;
	
	
	private TagData settWaveLength;
	/**==Absorptionskoefizient a fraction, as a value from 0.0 to 1.0*/
	private TagData settAttenuation;
	//??
//	private TagData intensity;
	
	public LightSourceCompUI(ModuleConfiguration objConf) 
	{
		System.out.println("# LightSrcCompUI::new Instance "+this);
		
		initGUI();
		if(objConf==null){
			createDummyPane(false);
			createSettDummyPane(false);
		}
		else{
			createDummyPane(objConf,false);
			createSettDummyPane(objConf.getSettingList(), false);
		}
	}
	
	public void addToList(List<LightSource> list)
	{
		if(list==null || list.size()==0)
			return;
		
		if(availableLightSrcList==null){

			availableLightSrcList=new ArrayList<LightSource>();
		}
		for(int i=0; i<list.size(); i++){
			availableLightSrcList.add(list.get(i));
		}

	}

	private void initGUI()
	{
		setLayout(new BorderLayout(5,5));
		buildComp=false;
		
		labels= new ArrayList<JLabel>();
		comp = new ArrayList<JComponent>();
		
		gridbag = new GridBagLayout();
		c = new GridBagConstraints();
		
		settingsBox=new JPanel();
		settingsBox.setLayout(gridbag);
		
		lightSrcCard=new CardLayout();
		globalPane=new JPanel(lightSrcCard);
//		globalPane.setLayout(gridbag);
		
		ActionListener aListener=new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				try {
					showSubCompForSelection( ((JComboBox)e.getSource()).getSelectedItem().toString());
					
				} catch (Exception e1) {
					LOGGER.error("[GUI] can't display selected source type");
				}
			}
		};
		
		sourceType=new JComboBox<String>(sourceTypeList);
		sourceType.addActionListener(aListener);
		
		box=Box.createVerticalBox();
		
		JButton editBtn=new JButton("Selection");
		editBtn.setAlignmentX(Component.RIGHT_ALIGNMENT);
		editBtn.setEnabled(true);
		editBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				LightSourceEditor editor = new LightSourceEditor(new JFrame(),
						"Select From Available LightSource", availableLightSrcList); 
				LightSource l=editor.getSelectedLightSource();
				if(l!=null){
					showSelectedLightSrc(l);
				}
			}
		});
		
		add(box,BorderLayout.NORTH);
		setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		add(editBtn,BorderLayout.SOUTH);
	}
	
	private void showLightSrcSubPane(String name)
	{
		lightSrcCard.show(globalPane,name);
	}
	
	/** LightSource was choose from a table -> set sourceType, instance subCompUI*/
	protected void showSelectedLightSrc(LightSource l) 
	{ 
		// notice sourceType cb
		// adapt intance and data of lightSrc 
		//TODO: what happens with still existing data
//		checkInstance(l);
		switch (l.getClass().getSimpleName()) {
		case LASER:
			lightSrcCard.show(globalPane, LASER);
			sourceType.setSelectedIndex(0);
			((LightSrcSubCompUI) globalPane.getComponent(0)).addData(l, true);
			break;
		case ARC:
			lightSrcCard.show(globalPane, ARC);
			sourceType.setSelectedIndex(1);
			((LightSrcSubCompUI) globalPane.getComponent(1)).addData(l, true);
			break;
		case FILAMENT:
			lightSrcCard.show(globalPane, FILAMENT);
			sourceType.setSelectedIndex(2);
			((LightSrcSubCompUI) globalPane.getComponent(2)).addData(l, true);
			break;
		case GENERIC_EXCITATION:
			lightSrcCard.show(globalPane, GENERIC_EXCITATION);
			sourceType.setSelectedIndex(3);
			((LightSrcSubCompUI) globalPane.getComponent(3)).addData(l, true);
			break;
		case LIGHT_EMITTING_DIODE:
			lightSrcCard.show(globalPane, LIGHT_EMITTING_DIODE);
			sourceType.setSelectedIndex(4);
			((LightSrcSubCompUI) globalPane.getComponent(4)).addData(l, true);
			break;
		default:
			break;
		}
		setFields=true;
		revalidate();
		repaint();
	}
	//TODO doesn't work correctly
	/**
	 * Show tags for selected lightSrc element. Doesn't save previously define values.
	 * @param sType
	 * @throws Exception
	 */
	protected void showSubCompForSelection(String sType) throws Exception 
	{
		lightSrcCard.show(globalPane,sType);
	}
	
	
	

	
	
	public void addData(LightSource l,boolean overwrite)
	{
		System.out.println("# LightSrcCompUI::addData()");
		if(l instanceof Laser){
			sourceType.setSelectedIndex(0);
			((LightSrcSubCompUI) globalPane.getComponent(0)).addData(l, overwrite);
		}else if(l instanceof Arc){
			sourceType.setSelectedIndex(1);
			((LightSrcSubCompUI) globalPane.getComponent(1)).addData(l, overwrite);
		}else if(l instanceof Filament){
			sourceType.setSelectedIndex(2);
			((LightSrcSubCompUI) globalPane.getComponent(2)).addData(l, overwrite);
		}else if(l instanceof GenericExcitationSource){
			sourceType.setSelectedIndex(3);
			((LightSrcSubCompUI) globalPane.getComponent(3)).addData(l, overwrite);
		}else if(l instanceof LightEmittingDiode){
			sourceType.setSelectedIndex(4);
			((LightSrcSubCompUI) globalPane.getComponent(4)).addData(l, overwrite);
		}else{
			LOGGER.error(" unknown LIGHTSOURCE or element is null ");
		}
	}
	
	public static LightSource copyLightSource(LightSource l) 
	{
		System.out.println("# LightSrcCompUI::copyLightSrc()");
		LightSource result=null;
		if(l!=null){
			switch (l.getClass().getSimpleName()) {
			case LASER:
				result=new Laser((Laser) l);
				break;
			case ARC:
				result=new Arc((Arc) l);
				break;
			case FILAMENT:
				result=new Filament((Filament) l);
				break;
			case GENERIC_EXCITATION:
				result=new GenericExcitationSource((GenericExcitationSource) l);
				break;
			case LIGHT_EMITTING_DIODE:
				result=new LightEmittingDiode((LightEmittingDiode) l);
				break;
			default:
				break;
			}
		}
		return result;
	}
	
	private void showLightSrcPane(LightSource l)
	{
		System.out.println("# LightSrcCompUI::showLightSrcPane");
		if(l instanceof Laser){
			sourceType.setSelectedIndex(0);
		}else if(l instanceof Arc){
			sourceType.setSelectedIndex(1);
		}else if(l instanceof Filament){
			sourceType.setSelectedIndex(2);
		}else if(l instanceof GenericExcitationSource){
			sourceType.setSelectedIndex(3);
		}else if(l instanceof LightEmittingDiode){
			sourceType.setSelectedIndex(4);
		}else{
			LOGGER.error(" unknown LIGHTSOURCE or element is null ");
		}
	}
	
	public LightSource getData() throws Exception
	{
		return ((LightSrcSubCompUI) globalPane.getComponent(sourceType.getSelectedIndex())).getData();
	}
	
	

	
	
	public void buildComponents()
	{
		System.out.println("# LightSrcCompUI::buildComponents()");
		for (Component comp : globalPane.getComponents() ) {
			((LightSrcSubCompUI) comp).buildComponents();
		}
		labels.clear();
		comp.clear();

		//		addLabelToGUI(new JLabel("Settings:"));
		//		addTagToGUI(intensity);
		addTagToGUI(settWaveLength);
		addTagToGUI(settAttenuation);

		System.out.println("Setting waveLenght= "+settWaveLength.getTagValue());

		addLabelTextRows(labels, comp, gridbag, settingsBox);

		c.gridwidth = GridBagConstraints.REMAINDER; //last
		c.anchor = GridBagConstraints.WEST;
		c.weightx = 1.0;

		buildComp=true;
		initTagList();

		box.removeAll();
		box.add(sourceType);
		box.add(Box.createVerticalStrut(5));
		box.add(globalPane);
		box.add(Box.createVerticalStrut(10));
		//			box.add(new JLabel("Settings:"));
		box.add(settingsBox);

		buildComp=true;
		setFields=false;
	}
	
	
	

	
	private void createDummyPane(ModuleConfiguration objConf,boolean inactive) 
	{
		System.out.println("# LightSrcCompUI::createDummyPane(conf,boolean)");
		globalPane.add(new LaserCompUI(objConf),LASER);
		globalPane.add(new ArcCompUI(objConf),ARC);
		globalPane.add(new FilamentCompUI(objConf),FILAMENT);
		globalPane.add(new GESCompUI(objConf),GENERIC_EXCITATION);
		globalPane.add(new LEDCompUI(objConf),LIGHT_EMITTING_DIODE);
		
		showLightSrcSubPane(LASER);
		sourceType.setSelectedItem(0);
	}
	
	

	//create dummy laser
	protected void createDummyPane(boolean inactive)
	{
		System.out.println("# LightSrcCompUI::createDummyPane(boolean)");
		globalPane.add(new LaserCompUI(null),LASER);
		globalPane.add(new ArcCompUI(null),ARC);
		globalPane.add(new FilamentCompUI(null),FILAMENT);
		globalPane.add(new GESCompUI(null),GENERIC_EXCITATION);
		globalPane.add(new LEDCompUI(null),LIGHT_EMITTING_DIODE);
		
		
		showLightSrcSubPane(LASER);
		sourceType.setSelectedItem(0);
	}
	

	public void clearDataValues()
	{
		clearCompData();
//		lightSrcSettUI.clearDataValues();
		clearSettDataValues();
		
	}
	
	private void clearCompData()
	{
		System.out.println("# LightSrcCompUI::clearCompData");
		for (Component comp : globalPane.getComponents() ) {
			((LightSrcSubCompUI) comp).clearDataValues();
		}
		
	}

	
	public void setFieldsExtern(boolean b) {
		setFields= setFields || b;		
	}


	
	public void clearList() {
		availableLightSrcList=null;
	}

	/**
	 * Update tags with val from list
	 */
	public void update(List<TagData> list) 
	{
		for (Component comp : globalPane.getComponents() ) {
			((LightSrcSubCompUI) comp).update(list);
		}
		updateSettings(list);
	}


	@Override
	public List<TagData> getActiveTags() {
		// TODO Auto-generated method stub
		return ((LightSrcSubCompUI) globalPane.getComponent(sourceType.getSelectedIndex())).getActiveTags();
	}


	
	
	@Override
	public boolean userInput() {
		return userInputSettings()
				|| setFields || ((LightSrcSubCompUI) globalPane.getComponent(sourceType.getSelectedIndex())).userInput();
	}
	
	
	

	public static Frequency parseFrequency(String c,Unit<Frequency> unit) throws Exception
	{
		if(c==null || c.equals(""))
			return null;
//		
//		LOGGER.info("[DEBUG] parse: unit= "+unit.getSymbol());
//		
//		if(unit==null || unit.equals("")){
//			LOGGER.warning("unknown unit for frequency of lightSrc, set to "+repRateUnit);
//			return new Frequency(parseToDouble(c), repRateUnit);
//		}else{
			return new Frequency(parseToDouble(c),unit);
			
//		}
	}
	
	public static Pulse parsePulse(String c) 
	{
		if(c==null || c.equals(""))
			return null;
		Pulse m=null;
		try{
			m=Pulse.fromString(c);
		}catch(EnumerationException e){
			LOGGER.warn("Pulse: "+c+" is not supported");
			m=Pulse.OTHER;
		}
		return m;
	}
	
	public static LaserMedium parseMedium(String c) 
	{
		if(c==null || c.equals(""))
			return null;
		LaserMedium m=null;
		try{
			m=LaserMedium.fromString(c);
		}catch(EnumerationException e){
			LOGGER.warn("LaserMedium: "+c+" is not supported");
			m=LaserMedium.OTHER;
		}
		return m;
	}
	public static LaserType parseLaserType(String c) 
	{
		if(c==null || c.equals(""))
			return null;
		
		LaserType a=null;
		try{
			a=LaserType.fromString(c);
		}catch(EnumerationException e){
			LOGGER.warn("LaserType: "+c+" is not supported");
			a=LaserType.OTHER;
		}
		return a;
	}
	
	public static FilamentType parseFilamentType(String c) {
		if(c==null || c.equals(""))
			return null;
		
		FilamentType a=null;
		try{
			a=FilamentType.fromString(c);
		}catch(EnumerationException e){
			LOGGER.warn("FilamentType: "+c+" is not supported");
			a=FilamentType.OTHER;
		}
		return a;
	}
	public static ArcType parseArcType(String c)  {
		if(c==null || c.equals(""))
			return null;
		
		ArcType a=null;
		try{
			a=ArcType.fromString(c);
		}catch(EnumerationException e){
			LOGGER.warn("ArcType: "+c+" is not supported");
			a=ArcType.OTHER;
		}
		return a;
	}
	
	public static Power parsePower(String c,Unit<Power> unit) throws Exception
	{
		if(c==null || c.equals(""))
			return null;
//		if(unit==null)
//			unit=powerUnit;
		
		return new Power(Double.valueOf(c),unit);
		
	}
	
	
	/////////////////////////////////////////////////////////////
	//			Settings
	/////////////////////////////////////////////////////////////
	
	
	public void createSettDummyPane(List<TagConfiguration> list,boolean inactive) 
	{
		System.out.println("# LightSrcCompUI::createSettDummyPane(List,boolean)");
		if(list==null)
			createSettDummyPane(inactive);
		else{
			clearSettDataValues();
			for(int i=0; i<list.size();i++){
				TagConfiguration t=list.get(i);
				String name=t.getName();
				String val=t.getValue();
				boolean prop=t.getProperty();
				if(name!=null){
					t.printf();
					setTag(t);
				}
			}
		}
	}
	
	
	private void clearSettDataValues() {
		clearTagValue(settWaveLength);
		clearTagValue(settAttenuation);
		
	}

	private void createSettDummyPane(boolean inactive) 
	{
		// setting values
		setSettWavelength(null, ElementsCompUI.OPTIONAL);
		setSettAttenuation(null, ElementsCompUI.OPTIONAL);
//		setIntensity(null, ElementsCompUI.OPTIONAL);
		
		if(inactive){
//			intensity.setInactiv();
			settWaveLength.setEnable(false);
			settAttenuation.setEnable(false);
		}
		
	}
	
	public void setSettWavelength(Length value, boolean prop)
	{
		String val=(value!=null) ? String.valueOf(value.value()) :"";
		Unit unit=(value!=null) ? value.unit():TagNames.WAVELENGTH_UNIT;
		if(settWaveLength == null) 
			settWaveLength = new TagData(TagNames.SET_WAVELENGTH,val,unit,prop,TagData.TEXTFIELD);
		else 
			settWaveLength.setTagValue(val,unit,prop);
		
	}
	public void setSettAttenuation(PercentFraction value, boolean prop)
	{
		String val= (value != null) ? String.valueOf(value.getNumberValue()):"";
		if(settAttenuation == null) 
			settAttenuation = new TagData(TagNames.ATTENUATION,val,prop,TagData.TEXTFIELD);
		else 
			settAttenuation.setTagValue(val,prop);
	}
	
	//settings tag list
		private void initTagList()
		{
			tagList=new ArrayList<TagData>();
			tagList.add(settWaveLength);
			tagList.add(settAttenuation);
			
		}
		private boolean userInputSettings()
		{
			boolean result=false;
			if(tagList!=null){
				for(int i=0; i<tagList.size();i++){
					boolean val=tagList.get(i)!=null ? tagList.get(i).valueChanged() : false;
					result= result || val;
				}
			}
			return result;
		}
		
		public void addData(LightSourceSettings ls,boolean overwrite)
		{
			System.out.println("# LightSrcSettCompUI::addData("+overwrite+")");
			boolean conflicts=false;
			if(overwrite){
				replaceData(ls);
				LOGGER.info("[DATA] -- replace LIGHTSRC_SETTINGS data");
			}else
				try {
					completeData(ls);
					LOGGER.info("[DATA] -- complete LIGHTSRC_SETTINGS data");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			setGUIData();
		}
		
		private void replaceData(LightSourceSettings l)
		{
			if(l!=null){
				lightSrcSett=l;
			}
		}
		
		private void completeData(LightSourceSettings l) throws Exception
		{
			//copy input fields
			LightSourceSettings copyIn=null;
			if(lightSrcSett!=null){
				getData();
				copyIn=new LightSourceSettings(lightSrcSett);
			}

			replaceData(l);

			// set input field values again
			if(copyIn!=null){
				Length w=copyIn.getWavelength();
				PercentFraction p=copyIn.getAttenuation();
				if(w!=null) lightSrcSett.setWavelength(w);
				if(p!=null) lightSrcSett.setAttenuation(p);
			}
		}
		
		private void setGUIData()
		{
			if(lightSrcSett!=null){
				try{setSettWavelength(lightSrcSett.getWavelength(), ElementsCompUI.REQUIRED);
				} catch (NullPointerException e) { }
				try{setSettAttenuation(lightSrcSett.getAttenuation(), ElementsCompUI.REQUIRED);
				}catch (NullPointerException e){}
			}
		}
		
		private void readGUIInput() throws Exception
		{
			System.out.println("# LightSrcSettCompUI::readGuiInput");
			if(lightSrcSett==null){
				createNewElement();
			}
			try{
				lightSrcSett.setWavelength(parseToLength(settWaveLength.getTagValue(),settWaveLength.getTagUnit()));
			}catch(Exception e){
				LOGGER.error("[DATA] can't read LIGHTSRC SETT wavelength input");
			}
			try{
				//TODO input format hint: percentvalue elem of [0,100] or [0,1]
				lightSrcSett.setAttenuation(parseAttenuation(settAttenuation.getTagValue()));
			}catch(Exception e){
				LOGGER.error("[DATA] can't read LIGHTSRC SETT attenuation input");
			}
		}
		
		private PercentFraction parseAttenuation(String c)
		{
			if(c==null || c.equals(""))
				return null;
			return new PercentFraction(Float.valueOf(c));
		}
		
		private void createNewElement() {
			lightSrcSett=new LightSourceSettings();
		}
		
		public LightSourceSettings getSettingsData() throws Exception
		{
			System.out.println("# LightSrcSettCompUI::getData()");
			if(userInput())
				readGUIInput();
			return lightSrcSett;
		}

		public void updateSettings(List<TagData> list) 
		{
			System.out.println("# LightSrcSettCompUI::update()");
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
		
		private void setTag(TagConfiguration t)
		{
			t.printf();
			setTag(t.getName(),t.getValue(),t.getProperty(),t.getUnit());
		}
		
		private void setTag(String name,String val,boolean prop,Unit unit) 
		{
			Exception exception=null;
			switch (name) {
			case TagNames.SET_WAVELENGTH:
				try {
					if(unit==null){
						unit=TagNames.WAVELENGTH_UNIT;
					}
					setSettWavelength(parseToLength(val, unit), prop);
					System.out.println("wavelength set");
				} catch (Exception e) {
					setSettWavelength(null, prop);
					exception=e;
				}
				settWaveLength.setVisible(true);
				
				break;
			case TagNames.ATTENUATION:
				try{
				setSettAttenuation(parseAttenuation(val), prop);
				System.out.println("attenuation set");
				}catch(Exception e){
					setSettAttenuation(null, prop);
					exception=e;
				}
				settAttenuation.setVisible(true);
				
				break;
			default: 
				LOGGER.warn("[CONF] LIGHTSRC SETT unknown tag: "+name );
				break;
			}
			
			if(exception!=null){
				WarningDialog ld = new WarningDialog(
    					"Can't read tag value : "+name+" = "+val,exception.toString(),
    					this.getClass().getSimpleName());
    			ld.setVisible(true);
			}
		}
}
