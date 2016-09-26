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

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ome.units.quantity.Frequency;
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

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.ModuleConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagData;

public class LightSourceCompUI extends ElementsCompUI 
{
	private CardLayout lightSrcCard; 
	private Box box;
	private LightSourceSettingsCompUI lightSrcSettUI;
	
	private List<LightSource> availableLightSrcList;
	
	public final static String LASER="Laser";
	public final static String ARC="Arc";
	public final static String FILAMENT="Filament";
	public final static String GENERIC_EXCITATION="GenericExcitationSource";
	public final static String LIGHT_EMITTING_DIODE="LightEmittingDiode";
	
	private final String[] sourceTypeList={LASER,ARC,FILAMENT,GENERIC_EXCITATION,LIGHT_EMITTING_DIODE};
	private JComboBox<String> sourceType;
	
	
	
	
	public LightSourceCompUI(ModuleConfiguration objConf) 
	{
//		System.out.println("# LightSrcCompUI::new Instance ");
		lightSrcSettUI=new LightSourceSettingsCompUI(objConf);
		initGUI();
		if(objConf==null){
			createDummyPane(false);
			lightSrcSettUI.createDummyPane(false);
		}
		else{
			createDummyPane(objConf,false);
			lightSrcSettUI.createDummyPane(objConf.getSettingList(), false);
		}
	}
	
	/**
	 * Copy elements from given list to local list
	 * @param list
	 */
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
					dataChanged=true;
//					System.out.println("\tsetfields="+dataChanged);
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
	
	/** LightSource was choose from a table -> set sourceType, instance subCompUI, mark values as changed*/
	protected void showSelectedLightSrc(LightSource l) 
	{ 
//		System.out.println("#LightSrcCompUI::showSelectedLightSrc("+l.getClass().getSimpleName()+")");
		// notice sourceType cb
		// adapt intance and data of lightSrc 
		//TODO: what happens with still existing data
//		checkInstance(l);
		switch (l.getClass().getSimpleName()) {
		case LASER:
			lightSrcCard.show(globalPane, LASER);
			sourceType.setSelectedIndex(0);
			((LightSrcSubCompUI) globalPane.getComponent(0)).addData(l, true);
			((LightSrcSubCompUI) globalPane.getComponent(0)).setAllValueChanged();
			break;
		case ARC:
			lightSrcCard.show(globalPane, ARC);
			sourceType.setSelectedIndex(1);
			((LightSrcSubCompUI) globalPane.getComponent(1)).addData(l, true);
			((LightSrcSubCompUI) globalPane.getComponent(1)).setAllValueChanged();
			break;
		case FILAMENT:
			lightSrcCard.show(globalPane, FILAMENT);
			sourceType.setSelectedIndex(2);
			((LightSrcSubCompUI) globalPane.getComponent(2)).addData(l, true);
			((LightSrcSubCompUI) globalPane.getComponent(2)).setAllValueChanged();
			break;
		case GENERIC_EXCITATION:
			lightSrcCard.show(globalPane, GENERIC_EXCITATION);
			sourceType.setSelectedIndex(3);
			((LightSrcSubCompUI) globalPane.getComponent(3)).addData(l, true);
			((LightSrcSubCompUI) globalPane.getComponent(3)).setAllValueChanged();
			break;
		case LIGHT_EMITTING_DIODE:
			lightSrcCard.show(globalPane, LIGHT_EMITTING_DIODE);
			sourceType.setSelectedIndex(4);
			((LightSrcSubCompUI) globalPane.getComponent(4)).addData(l, true);
			((LightSrcSubCompUI) globalPane.getComponent(4)).setAllValueChanged();
			break;
		default:
			break;
		}
		
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
//		System.out.println("# LightSrcCompUI::showSubCompForSelection("+sType+"]");
		lightSrcCard.show(globalPane,sType);
	}
	
	
	

	
	
	public void addData(LightSource l,boolean overwrite)
	{
//		System.out.println("# LightSrcCompUI::addData()");
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
//		System.out.println("# LightSrcCompUI::copyLightSrc()");
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
//		System.out.println("# LightSrcCompUI::showLightSrcPane");
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
//		System.out.println("# LightSrcCompUI::buildComponents()");
		dataChanged=false;
		for (Component comp : globalPane.getComponents() ) {
			((LightSrcSubCompUI) comp).buildComponents();
		}

		//		addLabelToGUI(new JLabel("Settings:"));
		//		addTagToGUI(intensity);


		c.gridwidth = GridBagConstraints.REMAINDER; //last
		c.anchor = GridBagConstraints.WEST;
		c.weightx = 1.0;

		buildComp=true;
		
		lightSrcSettUI.buildComponents();

		box.removeAll();
		box.add(sourceType);
		box.add(Box.createVerticalStrut(5));
		box.add(globalPane);
		box.add(Box.createVerticalStrut(10));
		//			box.add(new JLabel("Settings:"));
		box.add(lightSrcSettUI);

		buildComp=true;
		
	}
	
	
	

	
	private void createDummyPane(ModuleConfiguration objConf,boolean inactive) 
	{
//		System.out.println("# LightSrcCompUI::createDummyPane(conf,boolean)");
		globalPane.add(new LS_LaserCompUI(objConf),LASER);
		globalPane.add(new LS_ArcCompUI(objConf),ARC);
		globalPane.add(new LS_FilamentCompUI(objConf),FILAMENT);
		globalPane.add(new LS_GESCompUI(objConf),GENERIC_EXCITATION);
		globalPane.add(new LS_LEDCompUI(objConf),LIGHT_EMITTING_DIODE);
		
		showLightSrcSubPane(LASER);
		sourceType.setSelectedItem(0);
	}
	
	

	//create dummy laser
	protected void createDummyPane(boolean inactive)
	{
//		System.out.println("# LightSrcCompUI::createDummyPane(boolean)");
		globalPane.add(new LS_LaserCompUI(null),LASER);
		globalPane.add(new LS_ArcCompUI(null),ARC);
		globalPane.add(new LS_FilamentCompUI(null),FILAMENT);
		globalPane.add(new LS_GESCompUI(null),GENERIC_EXCITATION);
		globalPane.add(new LS_LEDCompUI(null),LIGHT_EMITTING_DIODE);
		
		
		showLightSrcSubPane(LASER);
		sourceType.setSelectedItem(0);
	}
	

	public void clearDataValues()
	{
		clearCompData();
		lightSrcSettUI.clearDataValues();
		
	}
	
	private void clearCompData()
	{
//		System.out.println("# LightSrcCompUI::clearCompData");
		for (Component comp : globalPane.getComponents() ) {
			((LightSrcSubCompUI) comp).clearDataValues();
		}
		
	}

	
	public void setFieldsExtern(boolean b) {
		dataChanged= dataChanged || b;		
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
			((LightSrcSubCompUI) comp).clearDataValues();
		}

		lightSrcSettUI.update(list);

		TagData lastelem=list.get(list.size()-1);
		if(lastelem.getTagName().equals("SourceType")){
			try {
//				System.out.println("\t ...set SourceType: "+sourceTypeList[Integer.valueOf(lastelem.getTagValue())]);
				sourceType.setSelectedIndex(Integer.valueOf(lastelem.getTagValue()));
//				showSubCompForSelection(lastelem.getTagValue());
				((LightSrcSubCompUI) globalPane.getComponent(sourceType.getSelectedIndex())).update(list);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		revalidate();
		repaint();
	}


	@Override
	public List<TagData> getActiveTags() {
		List<TagData> result=new ArrayList<TagData>();
		for(TagData t:((LightSrcSubCompUI) globalPane.getComponent(sourceType.getSelectedIndex())).getActiveTags()){
			result.add(t);
		}
		for(TagData ts:lightSrcSettUI.getActiveTags()){
			result.add(ts);
		}
		result.add(new TagData("SourceType", String.valueOf(sourceType.getSelectedIndex()), OPTIONAL, TagData.TEXTFIELD));
//		System.out.println("\t ...selected SourceType: "+sourceTypeList[sourceType.getSelectedIndex()]);
		return result;
	}


	
	
	@Override
	public boolean userInput() {
		boolean result= lightSrcSettUI.userInput()
				|| dataChanged
				|| ((LightSrcSubCompUI) globalPane.getComponent(sourceType.getSelectedIndex())).userInput();
		return result;
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
	
	

		
		public void addData(LightSourceSettings ls,boolean overwrite)
		{
			lightSrcSettUI.addData(ls, overwrite);
		}
		
		
		public LightSourceSettings getSettingsData() throws Exception
		{
			return lightSrcSettUI.getData();
		}

		
		
		
		
		
}
