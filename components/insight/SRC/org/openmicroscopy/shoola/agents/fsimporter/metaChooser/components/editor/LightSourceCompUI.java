package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.TitledBorder;

import org.apache.commons.lang.BooleanUtils;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.UOSMetadataLogger;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.ElementsCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.LightSourceEditor;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.TagData;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.ModuleConfiguration;

import loci.formats.MetadataTools;
import loci.formats.meta.IMetadata;
import ome.units.UNITS;
import ome.units.quantity.Frequency;
import ome.units.quantity.Length;
import ome.units.quantity.Power;
import ome.units.unit.Unit;
import ome.xml.model.Arc;
import ome.xml.model.Channel;
import ome.xml.model.Filament;
import ome.xml.model.GenericExcitationSource;
import ome.xml.model.Laser;
import ome.xml.model.LightEmittingDiode;
import ome.xml.model.LightSource;
import ome.xml.model.LightSourceSettings;
import ome.xml.model.Map;
import ome.xml.model.MapPair;
import ome.xml.model.Objective;
import ome.xml.model.enums.ArcType;
import ome.xml.model.enums.EnumerationException;
import ome.xml.model.enums.FilamentType;
import ome.xml.model.enums.LaserMedium;
import ome.xml.model.enums.LaserType;
import ome.xml.model.enums.Medium;
import ome.xml.model.enums.Pulse;
import ome.xml.model.primitives.PositiveInteger;
import sun.net.www.content.audio.wav;

public class LightSourceCompUI extends ElementsCompUI 
{
	private final String L_MODEL ="Model";
	private final String L_MANUFAC="Manufacturer";
	private final String L_POWER="Power";
	
	private final String L_L_TYPE="L Type";
	private final String L_A_TYPE="A Type";
	private final String L_F_TYPE="F Type";
	
	private final String L_MEDIUM="Medium";
	private final String L_FREQMUL="Frequency Multiplication";
	private final String L_TUNABLE="Tunable";
	private final String L_PULSE="Pulse";
	private final String L_POCKELCELL="Pockel Cell";
	private final String L_REPRATE="Repititation Rate";
	private final String L_PUMP="Pump";
	private final String L_WAVELENGTH="Wavelength";
	
	private final String L_DESC="Description";
	private final String L_MAP="Map";
	
	//TODO private List<LaserCompUI> laserList ??;
	private TagData manufact;
	private TagData power;
	private TagData model;
	private TagData type;
	
	private TagData description;//lightEmittingDiode
	private TagData map;//genericExcitationSource
	
	//laser
	private TagData medium;
	private TagData freqMul;
	private TagData tunable;
	private TagData pulse;
	private TagData pockelCell;
	private TagData repRate;
	private TagData pump;
	private TagData waveLength;
	
	private List<TagData> tagList;
	
	private String classification;
	
	private Unit<Power> powerUnit;
	private Unit<Length> waveLengthUnit;
	private Unit<Frequency> repRateUnit;
	
//	private JPanel globalPane;
	private Box box;
	
	private TitledBorder tb;
	
	private LightSource lightSrc;
	private LightSourceSettings lightSrcSett;
	private LightSourceSettingsCompUI lightSrcSettUI;
	
	
	private List<LightSource> availableLightSrcList;
	
	private final String LASER="Laser";
	private final String ARC="Arc";
	private final String FILAMENT="Filament";
	private final String GENERIC_EXCITATION="GenericExcitationSource";
	private final String LIGHT_EMITTING_DIODE="LightEmittingDiode";
	
	private final String[] sourceTypeList={LASER,ARC,FILAMENT,GENERIC_EXCITATION,LIGHT_EMITTING_DIODE};
	private final JComboBox<String> sourceType=new JComboBox<String>(sourceTypeList);
	private ActionListener aListener;
	
	private int linkChannelIdx;
	private boolean setFields;
	
	
	
	public boolean userInput()
	{
		boolean result=false;
		if(tagList!=null){
			for(int i=0; i<tagList.size();i++){
				boolean val=tagList.get(i)!=null ? tagList.get(i).valueChanged() : false;
				result= result || val;
			}
		}
		return (result || lightSrcSettUI.userInput()|| setFields);
	}
	public LightSourceCompUI(LightSource _ls, int _linkChannelIdx)
	{
		lightSrc=_ls;
		linkChannelIdx=_linkChannelIdx;
		lightSrcSettUI=null;
		powerUnit=UNITS.MW;
		waveLengthUnit=UNITS.NM;
		repRateUnit=UNITS.HZ;
		initGUI();
		if(lightSrc!=null)
			setGUIData();
		else{
			//TODO init
			//createDummyPane(false);
//			createNewLightSrcPane();
		}
	}
	

	
	public LightSourceCompUI(ModuleConfiguration objConf) 
	{
		powerUnit=UNITS.MW;
		waveLengthUnit=UNITS.NM;
		repRateUnit=UNITS.HZ;
		lightSrcSettUI=new LightSourceSettingsCompUI(objConf);
		lightSrc=null;
		initGUI();
		if(objConf==null)
			createDummyPane(false);
		else
			createDummyPane(objConf.getTagList(),false);
	}
	


	public void setList(List<LightSource> _list)
	{
		availableLightSrcList=_list;
	}

	private void initGUI()
	{
		setLayout(new BorderLayout(5,5));
		buildComp=false;
		labels= new ArrayList<JLabel>();
		comp = new ArrayList<JComponent>();
		
		gridbag = new GridBagLayout();
		c = new GridBagConstraints();
		
		globalPane=new JPanel();
		globalPane.setLayout(gridbag);
		
		aListener=new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				try {
					showSourceTypeView((String) ((JComboBox)e.getSource()).getSelectedItem());
				} catch (Exception e1) {
					LOGGER.severe("[GUI] can't display selected source type");
				}
			}
		};
		
		sourceType.addActionListener(aListener);
		
		box=Box.createVerticalBox();
		box.add(sourceType);
		box.add(Box.createVerticalStrut(5));
		box.add(globalPane);
		
		JButton editBtn=new JButton("Select");
		editBtn.setAlignmentX(Component.RIGHT_ALIGNMENT);
		editBtn.setEnabled(true);
		editBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				LightSourceEditor editor = new LightSourceEditor(new JFrame(),
						"Select From Available LightSource", availableLightSrcList); 
				LightSource l=editor.getSelectedLightSource();
				if(l!=null){
					lightSrc=l;
					setGUIData();
					revalidate();
					repaint();
				}
				
				
			}
		});
		
		tb=new TitledBorder("<SourceType>");
//		add(new TitledSeparator("LightSource", 3, TitledBorder.DEFAULT_POSITION),BorderLayout.NORTH);
		add(box,BorderLayout.NORTH);
		setBorder(
//				BorderFactory.createCompoundBorder(	tb,
						BorderFactory.createEmptyBorder(10,10,10,10));
		add(editBtn,BorderLayout.SOUTH);
	}
	
	//TODO doesn't work correctly
	protected void showSourceTypeView(String sType) throws Exception 
	{
		//read available data from pane 
		getData();
		if(lightSrcSettUI!=null) lightSrcSettUI.getData();
		
		globalPane.removeAll();
    	globalPane.setLayout(gridbag);
    	
    	//set data again
		createCustomDummyPane(false,sType);
		
		rebuildComponents();
		revalidate();
		repaint();
		
	}
	
	public LightSourceSettingsCompUI getSettings()
	{
		return lightSrcSettUI;
	}
	
	public void addData(LightSource l,boolean overwrite) 
	{
		if(l==null)
			return;
		
		//TODO
		if(lightSrc!=null && !l.getClass().equals(lightSrc.getClass())){
			LOGGER.severe("[DATA] add LIGHTSOURCE data: different lightSrc types");
			return;
		}
		
		if(lightSrc!=null){
			if(overwrite){
				if(l.getID()!=null && !l.getID().equals(""))
					lightSrc.setID(l.getID());
			}else{
				if(lightSrc.getID()==null || lightSrc.getID().equals(""))
					lightSrc.setID(l.getID());
			}
		}

		if(l instanceof Laser){
			addDataLaser(l,overwrite);
		}else if(l instanceof Arc){
			addDataArc(l,overwrite);
		}else if(l instanceof Filament){
			addDataFilament(l,overwrite);
		}else if(l instanceof GenericExcitationSource){
			addDataGES(l,overwrite);
		}else if(l instanceof LightEmittingDiode){
			addDataLED(l,overwrite);
		}
		
		setGUIData();
		
	}
	
	private void addDataLED(LightSource l,boolean overwrite) 
	{
		String mo=l.getModel();
		String ma=l.getManufacturer();
		if(lightSrc!=null){
			if(overwrite){
				if(mo!=null && !mo.equals("")) lightSrc.setModel(mo);
				if(ma!=null && !ma.equals("")) lightSrc.setManufacturer(ma);

				LOGGER.info("[DATA] overwrite LIGHTSOURCE data");
			}else{
				if(lightSrc.getManufacturer()==null)
					lightSrc.setManufacturer(ma);
				if(lightSrc.getModel()==null)
					lightSrc.setModel(mo);
				LOGGER.info("[DATA] complete LIGHTSOURCE data");
			}
		}else{
			lightSrc=l;
			LOGGER.info("[DATA] add LIGHTSOURCE data");
		}
		
	}
	private void addDataGES(LightSource l,boolean overwrite) 
	{
		String mo=l.getModel();
		String ma=l.getManufacturer();
		Power p=l.getPower();
		if(lightSrc!=null){
			if(overwrite){
				if(mo!=null && !mo.equals("")) lightSrc.setModel(mo);
				if(ma!=null && !ma.equals("")) lightSrc.setManufacturer(ma);
				if(p!=null) lightSrc.setPower(p);
				LOGGER.info("[DATA] overwrite LIGHTSOURCE data");
			}else{
				if(lightSrc.getManufacturer()==null)
					lightSrc.setManufacturer(ma);
				if(lightSrc.getModel()==null)
					lightSrc.setModel(mo);
				if(lightSrc.getPower()==null)
					lightSrc.setPower(p);
				LOGGER.info("[DATA] complete LIGHTSOURCE data");
			}
		}else {
			lightSrc=l;
			LOGGER.info("[DATA] add LIGHTSOURCE data");
		}
		
		
	}
	private void addDataFilament(LightSource l,boolean overwrite) 
	{
		String mo=l.getModel();
		String ma=l.getManufacturer();
		Power p=l.getPower();
		FilamentType t=((Filament)l).getType();
		if(lightSrc!=null){
			if(overwrite){
				if(mo!=null && !mo.equals("")) lightSrc.setModel(mo);
				if(ma!=null && !ma.equals("")) lightSrc.setManufacturer(ma);
				if(p!=null) lightSrc.setPower(p);
				if(t!=null) ((Filament) lightSrc).setType(t);
				LOGGER.info("[DATA] overwrite LIGHTSOURCE data");
			}else{
				if(lightSrc.getManufacturer()==null)
					lightSrc.setManufacturer(ma);
				if(lightSrc.getModel()==null)
					lightSrc.setModel(mo);
				if(lightSrc.getPower()==null)
					lightSrc.setPower(p);
				if(((Filament) lightSrc).getType()==null)
					if(t!=null) ((Filament) lightSrc).setType(t);
				LOGGER.info("[DATA] complete LIGHTSOURCE data");
			}
		}else {
			lightSrc=l;
			LOGGER.info("[DATA] add LIGHTSOURCE data");
		}

	}
	private void addDataArc(LightSource l,boolean overwrite) 
	{
		String mo=l.getModel();
		String ma=l.getManufacturer();
		Power p=l.getPower();
		ArcType t=((Arc)l).getType();
		if(lightSrc!=null){
			if(overwrite){
				
				if(mo!=null && !mo.equals("")) lightSrc.setModel(mo);
				if(ma!=null && !ma.equals("")) lightSrc.setManufacturer(ma);
				if(p!=null) lightSrc.setPower(p);
				if(t!=null) ((Arc) lightSrc).setType(t);
				LOGGER.info("[DATA] overwrite LIGHTSOURCE data");
			}else{
				if(lightSrc.getManufacturer()==null)
					lightSrc.setManufacturer(ma);
				if(lightSrc.getModel()==null)
					lightSrc.setModel(mo);
				if(lightSrc.getPower()==null)
					lightSrc.setPower(p);
				if(((Arc) lightSrc).getType()==null)
					if(t!=null) ((Arc) lightSrc).setType(t);
				LOGGER.info("[DATA] complete LIGHTSOURCE data");
			}
		}else {
			lightSrc=l;
			LOGGER.info("[DATA] add LIGHTSOURCE data");
		}
	}
	private void addDataLaser(LightSource l,boolean overwrite) 
	{
		String mo=l.getModel();
		String ma=l.getManufacturer();
		Power p=l.getPower();
		LaserType t=((Laser)l).getType();
		LaserMedium m=((Laser)l).getLaserMedium();
		PositiveInteger fM=((Laser)l).getFrequencyMultiplication();
		Boolean tu=((Laser)l).getTuneable();
		Boolean po=((Laser)l).getPockelCell();
		Frequency rr=((Laser)l).getRepetitionRate();
		Length w=((Laser)l).getWavelength();
		Pulse pu=((Laser)l).getPulse();
				

		if(lightSrc!=null){	
			if(overwrite){
				
				if(mo!=null && !mo.equals("")) lightSrc.setModel(mo);
				if(ma!=null && !ma.equals("")) lightSrc.setManufacturer(ma);
				if(p!=null) lightSrc.setPower(p);
				if(t!=null) ((Laser) lightSrc).setType(t);

				if(m!=null) ((Laser)lightSrc).setLaserMedium(m);
				if(fM!=null) ((Laser)lightSrc).setFrequencyMultiplication(fM);
				if(tu!=null){
					((Laser)lightSrc).setTuneable(tu);
				}
				if(po!=null) ((Laser)lightSrc).setPockelCell(po);
				if(rr!=null) ((Laser)lightSrc).setRepetitionRate(rr);
				if(w!=null) ((Laser)lightSrc).setWavelength(w);
				if(pu!=null) ((Laser)lightSrc).setPulse(pu);
				if(((Laser)l).getLinkedPump()!=null) ((Laser)lightSrc).linkPump(((Laser)l).getLinkedPump());
				LOGGER.info("[DATA] overwrite LIGHTSOURCE data");
			}else{
				
				if(lightSrc.getManufacturer()==null)
					lightSrc.setManufacturer(ma);
				if(lightSrc.getModel()==null)
					lightSrc.setModel(mo);
				if(lightSrc.getPower()==null)
					lightSrc.setPower(p);
				if(((Laser) lightSrc).getType()==null)
					((Laser) lightSrc).setType(t);
				if(((Laser)lightSrc).getLaserMedium()==null)
					((Laser)lightSrc).setLaserMedium(m);
				if(((Laser)lightSrc).getFrequencyMultiplication()==null)
					((Laser)lightSrc).setFrequencyMultiplication(fM);
				try{
//				if(((Laser)lightSrc).getTuneable()==null){
					((Laser)lightSrc).setTuneable(tu);
//				}
//				if(((Laser)lightSrc).getPockelCell()==null)
					((Laser)lightSrc).setPockelCell(po);
				}catch(Exception e){
					LOGGER.warning("LIGHTSRC Can't set checkbox values");
				}
				if(((Laser)lightSrc).getRepetitionRate()==null)
					((Laser)lightSrc).setRepetitionRate(rr);
				if(((Laser)lightSrc).getWavelength()==null)
					((Laser)lightSrc).setWavelength(w);
				if(((Laser)lightSrc).getPulse()==null)
					((Laser)lightSrc).setPulse(pu);
				if(((Laser)lightSrc).getLinkedPump()==null)
					((Laser)lightSrc).linkPump(((Laser)l).getLinkedPump());
				LOGGER.info("[DATA] complete LIGHTSOURCE data");
			}
		}else {
			lightSrc=l;
			LOGGER.info("[DATA] add LIGHTSOURCE data");
		}
		

	}
	public void addData(LightSourceSettings ls,boolean overwrite)
	{
		if(lightSrcSettUI!=null)
			lightSrcSettUI.addData(ls,overwrite);
	}

	
	
	private void setGUIData()
	{
		if(lightSrc instanceof Laser){
			sourceType.setSelectedItem(0);
			setTitledBorder("Laser");
			setGUIDataLaser();
		}else if(lightSrc instanceof Arc){
			setTitledBorder("Arc");
			sourceType.setSelectedItem(1);
			setGUIDataArc();
		}else if(lightSrc instanceof Filament){
			setTitledBorder("Filament");
			sourceType.setSelectedItem(3);
			setGUIDataFilament();
		}else if(lightSrc instanceof GenericExcitationSource){
			sourceType.setSelectedItem(4);
			setTitledBorder("GenericExcitationSource");
			setGUIDataGenericExcitationSource();
		}else if(lightSrc instanceof LightEmittingDiode){
			sourceType.setSelectedItem(5);
			setTitledBorder("LightEmittingDiode");
			setGUIDataLightEmittingDiode();
		}else{
			LOGGER.severe(" unknown LIGHTSOURCE or element is null ");
		}
		
		
			
	}
	
	public LightSource getData() throws Exception
	{
		if(userInput()) 
			readGUIInput();
		return lightSrc;
	}
	
	private void readGUIInput() throws Exception
	{
		if(lightSrc==null)
			createNewElement();
		
		if(lightSrc instanceof Laser){
			readGUIInputLaserData();
		}else if(lightSrc instanceof Arc){
			readGUIInputArcData();
		}else if(lightSrc instanceof Filament){
			readGUIInputFilamentData();
		}else if(lightSrc instanceof GenericExcitationSource){
			greadGUIInputGenericExcitationSourceData();
		}else if(lightSrc instanceof LightEmittingDiode){
			readGUIInputLightEmittingDiodeData();
		}else{
			LOGGER.severe("unknown LIGHTSOURCE or element is null");
		}
	}
	
	private void createNewElement() 
	{
		String kindOfLightSrc=sourceType.getSelectedItem().toString();
		switch(kindOfLightSrc){
		case LASER: 
			lightSrc=new Laser();
			break;
		case ARC: 
			lightSrc=new Arc(); 
			break;
		case FILAMENT: 
			lightSrc=new Filament(); 
			break;
		case GENERIC_EXCITATION: 
			lightSrc=new GenericExcitationSource(); 
			break;
		case LIGHT_EMITTING_DIODE:
			lightSrc=new LightEmittingDiode();
			break;
		default: LOGGER.severe("unknown LightSource");
		break;
		}
		
	}
	private void readGUIInputLightEmittingDiodeData() throws Exception 
	{
		try{
			((LightEmittingDiode)lightSrc).setManufacturer(manufact.getTagValue().equals("")? null : manufact.getTagValue());
		}catch(Exception e){
			LOGGER.severe("[DATA] can't read LIGHTSRC led manufacturer input");
		}
		try{
			((LightEmittingDiode)lightSrc).setModel(model.getTagValue().equals("")? null : model.getTagValue());
		}catch(Exception e){
			LOGGER.severe("[DATA] can't read LIGHTSRC led model input");
		}
		//TODO ((LightEmittingDiode)lightSrc).setLinkedAnnotation(index, o)
	}

	private void greadGUIInputGenericExcitationSourceData() throws Exception 
	{
		try{
			((GenericExcitationSource)lightSrc).setManufacturer(manufact.getTagValue().equals("")? 
					null : manufact.getTagValue());
		}catch(Exception e){
			LOGGER.severe("[DATA] can't read LIGHTSRC ges manufacturer input");
		}
		try{
			((GenericExcitationSource)lightSrc).setModel(model.getTagValue().equals("")? 
					null : model.getTagValue());
		}catch(Exception e){
			LOGGER.severe("[DATA] can't read LIGHTSRC ges model input");
		}
		try{
			((GenericExcitationSource)lightSrc).setPower(parsePower(power.getTagValue(),powerUnit));
		}catch(Exception e){
			LOGGER.severe("[DATA] can't read LIGHTSRC ges power input");
		}
		//TODO set Map
//		((GenericExcitationSource)lightSrc).setMap(map.getTagValue().equals("") ? 
//				null : );
	}

	private void readGUIInputFilamentData() throws Exception 
	{
		try{
			((Filament)lightSrc).setManufacturer(manufact.getTagValue().equals("")? 
					null : manufact.getTagValue());
		}catch(Exception e){
			LOGGER.severe("[DATA] can't read LIGHTSRC fila manufacturer input");
		}
		try{
			((Filament)lightSrc).setModel(model.getTagValue().equals("")? 
					null : model.getTagValue());
		}catch(Exception e){
			LOGGER.severe("[DATA] can't read LIGHTSRC fila model input");
		}
		try{
			((Filament)lightSrc).setPower(parsePower(power.getTagValue(),powerUnit));
		}catch(Exception e){
			LOGGER.severe("[DATA] can't read LIGHTSRC fila power input");
		}
		try{
			((Filament)lightSrc).setType(type.getTagValue().equals("") ? 
					null : FilamentType.fromString(type.getTagValue()));
		}catch(Exception e){
			LOGGER.severe("[DATA] can't read LIGHTSRC fila type input");
		}
		
	}

	private void readGUIInputArcData() throws Exception 
	{
		try{
			((Arc)lightSrc).setManufacturer(manufact.getTagValue().equals("")? 
					null : manufact.getTagValue());
		}catch(Exception e){
			LOGGER.severe("[DATA] can't read LIGHTSRC arc manufacturer input");
		}
		try{
			((Arc)lightSrc).setModel(model.getTagValue().equals("")? 
					null : model.getTagValue());
		}catch(Exception e){
			LOGGER.severe("[DATA] can't read LIGHTSRC arc model input");
		}
		try{
			((Arc)lightSrc).setPower(parsePower(power.getTagValue(),powerUnit));
		}catch(Exception e){
			LOGGER.severe("[DATA] can't read LIGHTSRC arc power input");
		}
		try{
			((Arc)lightSrc).setType(type.getTagValue().equals("") ? 
					null : ArcType.fromString(type.getTagValue()));
		}catch(Exception e){
			LOGGER.severe("[DATA] can't read LIGHTSRC arc type input");
		}
	}

	private void readGUIInputLaserData() throws Exception 
	{
		try{
			((Laser)lightSrc).setManufacturer(manufact.getTagValue().equals("")? 
					null : manufact.getTagValue());
		}catch(Exception e){
			LOGGER.severe("[DATA] can't read LIGHTSRC maunfacturer input");
		}
		try{
			((Laser)lightSrc).setModel(model.getTagValue().equals("")? 
					null : model.getTagValue());
		}catch(Exception e){
			LOGGER.severe("[DATA] can't read LIGHTSRC model input");
		}
		try{
			((Laser)lightSrc).setPower(parsePower(power.getTagValue(),powerUnit));
		}catch(Exception e){
			LOGGER.severe("[DATA] can't read LIGHTSRC power input");
		}
		try{
			((Laser)lightSrc).setType(parseLaserType(type.getTagValue()));
		}catch(Exception e){
			LOGGER.severe("[DATA] can't read LIGHTSRC type input");
		}
		try{
			((Laser)lightSrc).setFrequencyMultiplication(parseToPositiveInt(freqMul.getTagValue()));
		}catch(Exception e){
			LOGGER.severe("[DATA] can't read LIGHTSRC freq multiplication input");
		}
		try{
			((Laser)lightSrc).setLaserMedium(parseMedium(medium.getTagValue()));
		}catch(Exception e){
			LOGGER.severe("[DATA] can't read LIGHTSRC medium input");
		}
		try{

			((Laser)lightSrc).setTuneable(BooleanUtils.toBoolean(tunable.getTagValue()));
		}catch(Exception e){
			LOGGER.severe("[DATA] can't read LIGHTSRC tunable input");
		}
		try{

			((Laser)lightSrc).setPulse(parsePulse(pulse.getTagValue()));
		}catch(Exception e){
			LOGGER.severe("[DATA] can't read LIGHTSRC pulse input");
		}
		try{

			((Laser)lightSrc).setPockelCell(Boolean.valueOf(pockelCell.getTagValue()));
		}catch(Exception e){
			LOGGER.severe("[DATA] can't read LIGHTSRC pockell cell input");
		}
		try{
			((Laser)lightSrc).setRepetitionRate(parseFrequency(repRate.getTagValue(), repRateUnit));
		}catch(Exception e){
			LOGGER.severe("[DATA] can't read LIGHTSRC repetition rate input");
		}

		//TODO: link pump object
		//		((Laser)lightSrc).linkPump(pump.getTagValue().equals("") ? 
		//				null : pump.getTagValue());
		try{
			((Laser)lightSrc).setWavelength(parseToLength(waveLength.getTagValue(),waveLengthUnit));
		}catch(Exception e){
			LOGGER.severe("[DATA] can't read LIGHTSRC wavelength input");
		}
		
	}
	private Frequency parseFrequency(String c,Unit<Frequency> unit)
	{
		if(c==null || c.equals(""))
			return null;
		
		return new Frequency(parseToDouble(c), unit);
	}
	
	private Pulse parsePulse(String c) throws EnumerationException
	{
		if(c==null || c.equals(""))
			return null;
		
		return Pulse.fromString(c);
	}
	
	private LaserMedium parseMedium(String c) throws EnumerationException
	{
		if(c==null || c.equals(""))
			return null;
		
		return LaserMedium.fromString(c);
	}
	private LaserType parseLaserType(String c) throws EnumerationException
	{
		if(c==null || c.equals(""))
			return null;
		
		return LaserType.fromString(c);
	}
	private FilamentType parseFilamentType(String c) throws EnumerationException {
		if(c==null || c.equals(""))
			return null;
		
		return FilamentType.fromString(c);
	}
	private ArcType parseArcType(String c) throws EnumerationException {
		if(c==null || c.equals(""))
			return null;
		
		return ArcType.fromString(c);
	}
	
	private Power parsePower(String c,Unit<Power> unit)
	{
		if(c==null || c.equals(""))
			return null;
		
		return new Power(Double.valueOf(c),unit);
		
	}

	private void setGUIDataLightEmittingDiode() 
	{
		
		try{ setManufact(((LightEmittingDiode)lightSrc).getManufacturer(), ElementsCompUI.REQUIRED);
		} catch (NullPointerException e) { }
		try{ setModel(((LightEmittingDiode)lightSrc).getModel(), ElementsCompUI.REQUIRED);
		} catch (NullPointerException e) { }
		try{ setDescription(null, ElementsCompUI.REQUIRED);
		} catch (NullPointerException e) { }
	}
	private void initTagListLED()
	{
		tagList=new ArrayList<TagData>();
		
		tagList.add(model);
		tagList.add(manufact);
		tagList.add(description);
	}

	private void setGUIDataGenericExcitationSource() 
	{
	
		try{ setManufact(((GenericExcitationSource)lightSrc).getManufacturer(), ElementsCompUI.REQUIRED);
		} catch (NullPointerException e) { }
		try{ setModel(((GenericExcitationSource)lightSrc).getModel(), ElementsCompUI.REQUIRED);
		} catch (NullPointerException e) { }
		try{ setPower(((GenericExcitationSource)lightSrc).getPower(), ElementsCompUI.REQUIRED);
		} catch (NullPointerException e) { }
		try{ setMap(((GenericExcitationSource)lightSrc).getMap().getPairs(), ElementsCompUI.REQUIRED);
		} catch (NullPointerException e) { }
	}
	private void initTagListGenericExc()
	{
		tagList=new ArrayList<TagData>();
		
		tagList.add(model);
		tagList.add(manufact);
		tagList.add(power);
		tagList.add(map);
	}

	private void setGUIDataFilament() 
	{
		
		try{ setManufact(((Filament)lightSrc).getManufacturer(), ElementsCompUI.REQUIRED);
		} catch (NullPointerException e) { }
		try{ setModel(((Filament)lightSrc).getModel(), ElementsCompUI.REQUIRED);
		} catch (NullPointerException e) { }
		try{ setPower(((Filament)lightSrc).getPower(), ElementsCompUI.REQUIRED);
		} catch (NullPointerException e) { }
		try{ setType(((Filament)lightSrc).getType(), ElementsCompUI.REQUIRED);
		} catch (NullPointerException e) { }
		
	}
	private void initTagListFilament()
	{
		tagList=new ArrayList<TagData>();
		
		tagList.add(model);
		tagList.add(manufact);
		tagList.add(power);
		tagList.add(type);
	}

	private void setGUIDataArc() 
	{
		
		try{ setManufact(((Arc)lightSrc).getManufacturer(), ElementsCompUI.REQUIRED);
		} catch (NullPointerException e) { }
		try{ setModel(((Arc)lightSrc).getModel(), ElementsCompUI.REQUIRED);
		} catch (NullPointerException e) { }
		try{ setPower(((Arc)lightSrc).getPower(), ElementsCompUI.REQUIRED);
		} catch (NullPointerException e) { }
		try{ setType(((Arc)lightSrc).getType(), ElementsCompUI.REQUIRED);
		} catch (NullPointerException e) { }
	}
	
	private void initTagListArc()
	{
		tagList=new ArrayList<TagData>();
		
		tagList.add(model);
		tagList.add(manufact);
		tagList.add(power);
		tagList.add(type);
	}

	private void setGUIDataLaser()
	{
		
		try{setManufact(((Laser)lightSrc).getManufacturer(), ElementsCompUI.REQUIRED);
		} catch (NullPointerException e) { }
		try{setModel(((Laser)lightSrc).getModel(), ElementsCompUI.REQUIRED);
		} catch (NullPointerException e) { }
		try{ setPower(((Laser)lightSrc).getPower(), ElementsCompUI.REQUIRED);
		} catch (NullPointerException e) { }
		try{ setType(((Laser)lightSrc).getType(), ElementsCompUI.REQUIRED);
		} catch (NullPointerException e) { }
		try{ setMedium(((Laser)lightSrc).getLaserMedium(), ElementsCompUI.REQUIRED);
		} catch (NullPointerException e) { }
		try{ setFreqMultiplication(((Laser)lightSrc).getFrequencyMultiplication(), ElementsCompUI.REQUIRED);
		} catch (NullPointerException e) { }
		try{ setTunable(((Laser)lightSrc).getTuneable(), ElementsCompUI.REQUIRED);
		} catch (NullPointerException e) { }
		try{ setPulse(((Laser)lightSrc).getPulse(), ElementsCompUI.REQUIRED);
		} catch (NullPointerException e) { }
		try{ setPocketCell(((Laser)lightSrc).getPockelCell(), ElementsCompUI.REQUIRED);
		} catch (NullPointerException e) { }
		try{setRepititationRate(((Laser)lightSrc).getRepetitionRate(), ElementsCompUI.REQUIRED);
		} catch (NullPointerException e) { }
		try{setPump(((Laser)lightSrc).getLinkedPump().getID(), ElementsCompUI.REQUIRED);
		} catch (NullPointerException e) { }
		try{ setWavelength(((Laser)lightSrc).getWavelength(), ElementsCompUI.REQUIRED);
		} catch (NullPointerException e) { }
	}
	private void initTagListLaser()
	{
		tagList=new ArrayList<TagData>();
		
		tagList.add(model);
		tagList.add(manufact);
		tagList.add(power);
		tagList.add(type);
		
		tagList.add(medium);
		tagList.add(freqMul);
		tagList.add(tunable);
		tagList.add(pulse);
		tagList.add(pockelCell);
		tagList.add(repRate);
		tagList.add(pump);
		tagList.add(waveLength);
	}
	
	public void setTitledBorder(String s)
	{
		if(s== null || s.equals(null)) return;
		tb.setTitle(s);
		classification=s;
	}
	
	
	
	public void buildComponents()
	{
			labels.clear();
			comp.clear();

			addTags(sourceType.getSelectedItem().toString());
			addLabelTextRows(labels, comp, gridbag, globalPane);

			c.gridwidth = GridBagConstraints.REMAINDER; //last
			c.anchor = GridBagConstraints.WEST;
			c.weightx = 1.0;

			lightSrcSettUI.buildComponents();
			box.add(Box.createVerticalStrut(20));
//			box.add(new JLabel("Settings:"));
//			box.add(Box.createVerticalStrut(10));
			box.add(lightSrcSettUI);
			buildComp=true;
			
			initTagList(sourceType.getSelectedItem().toString());
			setFields=false;
	}
	
	private void addTags(String kindOfLightSrc)
	{
		addTagToGUI(model);
		addTagToGUI(manufact);
		
		switch(kindOfLightSrc){
		case LASER: 
			addTagToGUI(type);
			addTagToGUI(power);
			addTagToGUI(medium);
			addTagToGUI(freqMul);
			addTagToGUI(tunable);
			addTagToGUI(pulse);
			addTagToGUI(pockelCell);
			addTagToGUI(repRate);
			addTagToGUI(pump);
			if(pump!=null)pump.setEnable(false);
			addTagToGUI(waveLength);
			break;
		case ARC: 
			addTagToGUI(type);
			addTagToGUI(power); 
			break;
		case FILAMENT: 
			addTagToGUI(type);
			addTagToGUI(power); 
			break;
		case GENERIC_EXCITATION: 
			addTagToGUI(map);
			addTagToGUI(power); 
			break;
		case LIGHT_EMITTING_DIODE:
			addTagToGUI(description);
			break;
		default: LOGGER.severe("unknown LightSource");
		break;
		}
	}
	
	private void rebuildComponents()
	{
		labels.clear();
		comp.clear();

		addTags(sourceType.getSelectedItem().toString());
		addLabelTextRows(labels, comp, gridbag, globalPane);

		c.gridwidth = GridBagConstraints.REMAINDER; //last
		c.anchor = GridBagConstraints.WEST;
		c.weightx = 1.0;
		initTagList(sourceType.getSelectedItem().toString());
	}
	
	//TODO: advanced properties shows by touch a button
	public void buildExtendedComponents(){

	}
	
//	public void showOptionPane() 
//	{
//		globalPane.removeAll();
//		JButton newBtn=new JButton("New...");
//		newBtn.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				createNewElem();
//			}
//		});
//		
//		JButton selectBtn=new JButton("Select from data...");
//		if(availableLightSrcList==null || availableLightSrcList.isEmpty())
//			selectBtn.setEnabled(false);
//		selectBtn.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) 
//			{
//				LightSourceEditor editor = new LightSourceEditor(new JFrame(),
//						"Select From Available LightSource", availableLightSrcList); 
//				LightSource l=editor.getSelectedLightSource();
//				if(l!=null){
//					l.setID("");
//					lightSrc=l;
//					createNewElemGUI();
//				}
//			}
//		});
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
	
	

//	private void createNewElem() 
//	{
////		ImageIcon icon = createImageIcon("images/middle.gif");
//		Object[] options={LASER,ARC,FILAMENT,GENERIC_EXCITATION,LIGHT_EMITTING_DIODE};
//		String s=(String) JOptionPane.showInputDialog(
//				globalPane, 
//				"Choose the class of LightSource",
//				"Create new LightSource",
//				JOptionPane.PLAIN_MESSAGE,
//				null, options,LASER);
//		
//		//If a string was returned, say so.
//        if ((s != null) && (s.length() > 0)) {
//        	globalPane.removeAll();
//        	globalPane.setLayout(gridbag);
//        	createCustomDummyPane(false,s);
//        	buildComponents();       
//        	
//        	revalidate();
//        	repaint();
//            return;
//        }
//	}
	
	private void createNewElemGUI()
	{
		globalPane.removeAll();
    	globalPane.setLayout(gridbag);
    	setGUIData();
    	buildComponents();       
    	
    	revalidate();
    	repaint();
	}
	
	public void createDummyPane(List<TagConfiguration> list,boolean inactive) 
	{
		if(list==null)
			createDummyPane(inactive);
		else{
			clearDataValues();
			if(lightSrc==null && list!=null && list.size()>0)
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
						setModel(val, prop);
						lightSrc.setModel(val);
						model.setVisible(true);
						break;
					case L_MANUFAC:
						setManufact(val, prop);
						lightSrc.setManufacturer(val);
						manufact.setVisible(true);
						break;
					case L_L_TYPE:
						try{
							LaserType value=parseLaserType(val);
							setType(value, prop);
							((Laser)lightSrc).setType(value);
						}catch(Exception e){
							setType((LaserType)null,prop);
						}
						type.setVisible(true);
						break;
					case L_A_TYPE:
						try{
							ArcType value=parseArcType(val);
							setType(value, prop);
							((Arc)lightSrc).setType(value);
						}catch(Exception e){
							setType((ArcType)null,prop);
						}
						type.setVisible(true);
						break;
					case L_F_TYPE:
						try{
							FilamentType value=parseFilamentType(val);
							setType(value, prop);
							((Filament)lightSrc).setType(value);
						}catch(Exception e){
							setType((FilamentType)null,prop);
						}
						type.setVisible(true);
						break;
					case L_POWER:
						try{
							Power value = parsePower(val, powerUnit);
						setPower(value, prop);
						lightSrc.setPower(value);
						}catch(Exception e){
							setPower(null, prop);
						}
						power.setVisible(true);
						break;
					case L_MEDIUM:
						try {
							LaserMedium value=parseMedium(val);
							setMedium(value,prop);
							((Laser)lightSrc).setLaserMedium(value);
						} catch (Exception e) {
							setMedium(null, prop);
						}
						medium.setVisible(true);
						break;
					case L_FREQMUL:
						try {
							PositiveInteger value=parseToPositiveInt(val);
							setFreqMultiplication(value, prop);
							((Laser)lightSrc).setFrequencyMultiplication(value);
						} catch (Exception e) {
							setFreqMultiplication(null, prop);
						}
						freqMul.setVisible(true);
						break;
					case L_TUNABLE:
						try {
//							Boolean value=BooleanUtils.toBoolean(val);
							setTunable(val, prop);
							((Laser)lightSrc).setTuneable(BooleanUtils.toBoolean(val));
						} catch (Exception e) {
							setTunable((String)null, prop);
						}
						tunable.setVisible(true);
						break;
					case L_PULSE:
						try {
							Pulse value=parsePulse(val);
							setPulse(value, prop);
							((Laser)lightSrc).setPulse(value);
						} catch (Exception e) {
							setPulse(null,prop);
						}
						pulse.setVisible(true);
						break;
					case L_POCKELCELL:
						try {
							Boolean value=Boolean.valueOf(val);
							setPocketCell(value, prop);
							((Laser)lightSrc).setPockelCell(value);
						} catch (Exception e) {
							setPocketCell(null, prop);
						}
						pockelCell.setVisible(true);
						break;
					case L_REPRATE:
						try {
							Frequency value=parseFrequency(val, repRateUnit);
							setRepititationRate(value, prop);
							((Laser)lightSrc).setRepetitionRate(value);
						} catch (Exception e) {
							setRepititationRate(null, prop);
						}
						repRate.setVisible(true);
						break;
					case L_PUMP:
						try {
							
							setPump(val, prop);
//							TODO: ((Laser)lightSrc).linkPump(o);
						} catch (Exception e) {
							setPump(null, prop);
						}
						pump.setVisible(true);
						break;
					case L_WAVELENGTH:
						try {
							Length value = parseToLength(val, waveLengthUnit);
							setWavelength(value, prop);
							((Laser)lightSrc).setWavelength(value);
						} catch (Exception e) {
							setWavelength(null, prop);
						}
						waveLength.setVisible(true);
						break;
					case L_MAP:
						try {
							setMap(null, prop);
//							TODO:((GenericExcitationSource)lightSrc).setMap(value);
						} catch (Exception e) {
							setMap(null, prop);
						}
						map.setVisible(true);
						break;
					case L_DESC:
						setDescription(val, prop);
//						TODO: ((LightEmittingDiode)lightSrc).set
						description.setVisible(true);
						break;
					default:
						LOGGER.warning("[CONF] unknown tag: "+name );break;
					}
				}
			}
		}
	}
	
	
	//create dummy laser
	public void createDummyPane(boolean inactive)
	{
		clearDataValues();
		createLaserDummy(inactive);
		sourceType.setSelectedItem(0);
	}
	
	public void createCustomDummyPane(boolean inactive,String kindOfLightSrc)
	{
		clearDataValues();
		switch(kindOfLightSrc){
		case LASER: createLaserDummy(inactive);  break;
		case ARC: createArcDummy(inactive); break;
		case FILAMENT: createFilamentDummy(inactive); break;
		case GENERIC_EXCITATION: createGenericExcDummy(inactive); break;
		case LIGHT_EMITTING_DIODE: createLightEmDummy(inactive); break;
		default: LOGGER.severe("unknown LightSource");
		break;
		}
		setGUIData();
		if(lightSrcSettUI!=null) lightSrcSettUI.addData(lightSrcSett,true);
	}
	
	private void initTagList(String kindOfLightSrc)
	{
		switch(kindOfLightSrc){
		case LASER: initTagListLaser();  break;
		case ARC: initTagListArc(); break;
		case FILAMENT: initTagListFilament(); break;
		case GENERIC_EXCITATION: initTagListGenericExc(); break;
		case LIGHT_EMITTING_DIODE: initTagListLED(); break;
		default: LOGGER.severe("unknown LightSource");
		break;
		}
	}
	
	private void createLightEmDummy(boolean inactive) 
	{
		setManufact(null, OPTIONAL);
		setModel(null, OPTIONAL);
		setDescription(null, OPTIONAL);
		setTitledBorder(LIGHT_EMITTING_DIODE);
		if(inactive){
			manufact.setEnable(false);
			model.setEnable(false);
			description.setEnable(false);
		}
	}

	private void createGenericExcDummy(boolean inactive) 
	{
		setManufact(null, OPTIONAL);
		setPower(null, OPTIONAL);
		setModel(null, OPTIONAL);
		setMap(null, OPTIONAL);
		setTitledBorder(GENERIC_EXCITATION);
		if(inactive){
			manufact.setEnable(false);
			map.setEnable(false);
			power.setEnable(false);
			model.setEnable(false);
		}
	}


	
	private void createFilamentDummy(boolean inactive) 
	{
		setManufact(null, OPTIONAL);
		setType((LaserType)null, OPTIONAL);
		setPower(null, OPTIONAL);
		setModel(null, OPTIONAL);
		setTitledBorder(FILAMENT);
		if(inactive){
			manufact.setEnable(false);
			type.setEnable(false);
			power.setEnable(false);
			model.setEnable(false);
		}
	}

	private void createArcDummy(boolean inactive) 
	{
		setManufact(null, OPTIONAL);
		setType((LaserType)null, OPTIONAL);
		setPower(null, OPTIONAL);
		setModel(null, OPTIONAL);
		setTitledBorder(ARC);
		if(inactive){
			manufact.setEnable(false);
			type.setEnable(false);
			power.setEnable(false);
			model.setEnable(false);
		}
	}

	private void createLaserDummy(boolean inactive) 
	{
		setManufact(null, OPTIONAL);
		setType((LaserType)null, OPTIONAL);
		setPower(null, OPTIONAL);
		setModel(null, OPTIONAL);
		
		setMedium(null, OPTIONAL);
		setFreqMultiplication(null, OPTIONAL);
		setTunable((String)null, OPTIONAL);
		setPulse(null, OPTIONAL);
		setPocketCell(null, OPTIONAL);
		setRepititationRate(null, OPTIONAL);
		setPump(null, OPTIONAL);
		setWavelength(null, OPTIONAL);
		
		setTitledBorder(LASER);
		
		if(inactive){
			manufact.setEnable(false);
			type.setEnable(false);
			power.setEnable(false);
			model.setEnable(false);

			medium.setEnable(false);
			freqMul.setEnable(false);
			tunable.setEnable(false);
			pulse.setEnable(false);
			pockelCell.setEnable(false);
			repRate.setEnable(false);
			pump.setEnable(false);
			waveLength.setEnable(false);
		}
	}

	public void clearDataValues()
	{
		clearTagValue(manufact);
		clearTagValue(type);
		clearTagValue(power);
		clearTagValue(model);
		
		clearTagValue(description);
		clearTagValue(map);
		
		clearTagValue(medium);
		clearTagValue(freqMul);
		clearTagValue(tunable);
		clearTagValue(pulse);
		clearTagValue(pockelCell);
		clearTagValue(repRate);
		clearTagValue(pump);
		clearTagValue(waveLength);
		
		if(lightSrcSettUI!=null) lightSrcSettUI.clearDataValues();
	}
	
	public void clear()
	{
		clearDataValues();
		lightSrc=null;
		lightSrcSettUI=null;
	}
	
	

	
	
	public void setManufact(String value, boolean prop)
	{
		if(manufact == null) 
			manufact = new TagData("Manufacturer: ",value,prop,TagData.TEXTFIELD);
		else 
			manufact.setTagValue(value,prop);
	}
	
	public void setType(LaserType value, boolean prop)
	{
		String val= (value != null)? value.getValue() : "";
		if(type == null) 
			type = new TagData("Type: ",val,prop,TagData.COMBOBOX,getNames(LaserType.class));
		else 
			type.setTagValue(val,prop);
	}
	public void setType(ArcType value, boolean prop)
	{
		String val= (value != null)? value.getValue() : "";
		if(type == null) 
			type = new TagData("Type: ",val,prop,TagData.COMBOBOX,getNames(ArcType.class));
		else 
			type.setTagValue(val,prop);
	}
	public void setType(FilamentType value, boolean prop)
	{
		String val= (value != null)? value.getValue() : "";
		if(type == null) 
			type = new TagData("Type: ",val,prop,TagData.COMBOBOX,getNames(FilamentType.class));
		else 
			type.setTagValue(val,prop);
	}
	
	
	public void setPower(Power value, boolean prop)
	{
		String val= (value != null)? String.valueOf(value.value()) : "";
		powerUnit=(value!=null) ? value.unit() :powerUnit;
		if(power == null) 
			power = new TagData("Power ["+powerUnit.getSymbol()+"]: ",val,prop,TagData.TEXTFIELD);
		else 
			power.setTagValue(val,prop);
	}
	public void setModel(String value, boolean prop)
	{
		if(model == null) 
			model = new TagData("Model: ",value,prop,TagData.TEXTFIELD);
		else 
			model.setTagValue(value,prop);
	}
	
	
	
	
	public void setMedium(LaserMedium value, boolean prop)
	{
		String val= (value != null)? value.getValue() : "";
		if(medium == null) 
			medium = new TagData("Medium: ",val,prop,TagData.COMBOBOX,getNames(LaserMedium.class));
		else 
			medium.setTagValue(val,prop);
	}
	public void setFreqMultiplication(PositiveInteger value, boolean prop)
	{
		String val= (value != null)? String.valueOf(value.getNumberValue()) : "";
		if(freqMul == null) 
			freqMul = new TagData("Frequency Multiplication: ",val,prop,TagData.TEXTFIELD);
		else 
			freqMul.setTagValue(val,prop);
	}
	
	
	public void setTunable(Boolean value, boolean prop)
	{
		String val=(value!=null) ? String.valueOf(value): "false";
		if(tunable == null) 
			tunable = new TagData("Tunable: ",val,prop,TagData.CHECKBOX);
		else 
			tunable.setTagValue(val,prop);
	}
	
	public void setTunable(String value, boolean prop)
	{
		String val=(value!=null) ? value: "false";
		if(tunable == null) 
			tunable = new TagData("Tunable: ",val,prop,TagData.CHECKBOX);
		else 
			tunable.setTagValue(val,prop);
	}
	
	public void setPulse(Pulse value, boolean prop)
	{
		String val= (value != null)? value.getValue() : "";
		if(pulse == null) 
			pulse = new TagData("Pulse: ",val,prop,TagData.COMBOBOX,getNames(Pulse.class));
		else 
			pulse.setTagValue(val,prop);
	}

	public void setPocketCell(Boolean value, boolean prop)
	{
		String val=(value!=null) ? String.valueOf(value): "false";
		if(pockelCell == null) 
			pockelCell = new TagData("Pockel Cell: ",val,prop,TagData.CHECKBOX);
		else 
			pockelCell.setTagValue(val,prop);
	}
	public void setRepititationRate(Frequency value, boolean prop)
	{
		String val=(value!=null) ? String.valueOf(value.value()) :"";
		repRateUnit=(value!=null) ? value.unit() :repRateUnit;
		if(repRate == null) 
			repRate = new TagData("Repititation Rate ["+repRateUnit.getSymbol()+"]: ",val,prop,TagData.TEXTFIELD);
		else 
			repRate.setTagValue(val,prop);
	}
	
	public void setPump(String value, boolean prop)
	{
		if(pump == null) 
			pump = new TagData("Pump: ",value,prop,TagData.TEXTFIELD);
		else 
			pump.setTagValue(value,prop);
	}
	public void setWavelength(Length value, boolean prop)
	{
		String val=(value!=null) ? String.valueOf(value.value()) :"";
		waveLengthUnit=(value!=null) ? value.unit() :waveLengthUnit;
		if(waveLength == null) 
			waveLength = new TagData("Wavelenght ["+waveLengthUnit.getSymbol()+"]: ",val,prop,TagData.TEXTFIELD);
		else 
			waveLength.setTagValue(val,prop);
	}
	
	
	
	
	public void setDescription(String value, boolean prop)
	{
		if(description == null) 
			description = new TagData("Description: ",value,prop,TagData.TEXTPANE);
		else 
			description.setTagValue(value,prop);
	}
	
	//TODO
	public void setMap(List<MapPair> value, boolean prop)
	{
		String val="";
		if(map == null) 
			map = new TagData("Map: ",val,prop,TagData.TEXTFIELD);
		else 
			map.setTagValue(val,prop);
	}

	@Override
	public List<TagData> getActiveTags() {
		List<TagData> list = new ArrayList<TagData>();
		if(isActive(model)) list.add(model);
		if(isActive(manufact)) list.add(manufact);
		if(isActive(type)) list.add(type);
		if(isActive(power)) list.add(power);
		if(isActive(description)) list.add(description);
		if(isActive(map)) list.add(map);
		if(isActive(medium)) list.add(medium);
		if(isActive(freqMul)) list.add(freqMul);
		if(isActive(tunable)) list.add(tunable);
		if(isActive(pulse)) list.add(pulse);
		if(isActive(pockelCell)) list.add(pockelCell);
		if(isActive(repRate)) list.add(repRate);
		if(isActive(pump)) list.add(pump);
		if(isActive(waveLength)) list.add(waveLength);
		
		return list;
		
		
	}
	public void setFieldsExtern(boolean b) {
		setFields= setFields || b;		
	}

	


}
