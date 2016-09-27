package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.view;

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
import javax.swing.border.TitledBorder;

import ome.units.quantity.Length;
import ome.units.unit.Unit;
import ome.xml.model.Arc;
import ome.xml.model.LightSource;
import ome.xml.model.LightSourceSettings;
import ome.xml.model.Objective;
import ome.xml.model.ObjectiveSettings;
import ome.xml.model.primitives.PercentFraction;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules.ElementsCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules.LightSourceEditor;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules.LightSrcSubCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules.ObjectiveEditor;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model.LightSourceModel;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model.ObjectiveModel;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.ModuleConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.TagNames;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagData;
import org.slf4j.LoggerFactory;

public class LightSourceViewer extends ModuleViewer{

	private static final org.slf4j.Logger LOGGER =
			LoggerFactory.getLogger(LightSourceViewer.class);

	private LightSourceModel data;
	private CardLayout lightSrcCard; 
	private Box box;
	private final String[] sourceTypeList={LightSourceModel.LASER,
			LightSourceModel.ARC,LightSourceModel.FILAMENT,
			LightSourceModel.GENERIC_EXCITATION,LightSourceModel.LIGHT_EMITTING_DIODE};
	private JComboBox<String> sourceType;
	
	private int index;

	//available element setting tags
	private TagData waveLengthSett;
	/**==Absorptionskoefizient a fraction, as a value from 0.0 to 1.0*/
	private TagData attenuation;
	//??
	//private TagData intensity;

	
	/**
	 * Creates a new instance.
	 * @param model Reference to model.
	 */
	public LightSourceViewer(LightSourceModel model,ModuleConfiguration conf,int index)
	{
		System.out.println("# LightSrcViewer::newInstance("+(model!=null?"model":"null")+") "+index);
		this.index=index;
		this.data=model;
		initComponents(conf);
		buildGUI();
		initTagList();
	}

	private void initTagList()
	{
		tagList=new ArrayList<TagData>();
		tagList.add(waveLengthSett);
		tagList.add(attenuation);
	}

	/**
	 * Builds and lay out GUI.
	 */
	private void buildGUI() 
	{
		for (Component comp : globalPane.getComponents() ) {
			((LightSourceSubViewer) comp).buildGUI();
		}

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
		addTagToGUI(waveLengthSett,labelsSett,compSett);
		addTagToGUI(attenuation,labelsSett,compSett);
		//	addTagToGUI(intensity,labelsSett,compSett);

		addLabelTextRows(labelsSett, compSett, gridbag, settingsPane);

		c.gridwidth = GridBagConstraints.REMAINDER; //last
		c.anchor = GridBagConstraints.WEST;
		c.weightx = 1.0;

		box.add(sourceType);
		box.add(Box.createVerticalStrut(5));
		box.add(globalPane);
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

		lightSrcCard=new CardLayout();
		globalPane=new JPanel(lightSrcCard);

		box=Box.createVerticalBox();
		box.add(globalPane);

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


		JButton editBtn=new JButton("Selection");
		editBtn.setAlignmentX(Component.RIGHT_ALIGNMENT);
		editBtn.setEnabled(true);
		editBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				LightSourceEditor editor = new LightSourceEditor(new JFrame(),
						"Select From Available LightSource", data.getList()); 
				LightSource l=editor.getSelectedLightSource();
				if(l!=null ){
					dataChanged=true;
					try {
						data.addData(l, true,index);
					} catch (Exception e1) {
						LOGGER.warn("Can't set data of selected lightSrc! "+e1);
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
		((LightSourceSubViewer) globalPane.getComponent(sourceType.getSelectedIndex())).initTags(list);
		initTags(settList);
	}

	/**
	 * Show tags for selected lightSrc element. Doesn't save previously define values.
	 * @param sType
	 * @throws Exception
	 */
	protected void showSubCompForSelection(String sType) throws Exception 
	{
		System.out.println("# LightSrcCompUI::showSubCompForSelection("+sType+"]");
		lightSrcCard.show(globalPane,sType);
		dataChanged=true;
	}

	/**
	 * Init given tag and mark it as visible.
	 * @param t
	 */
	protected void initTag(TagConfiguration t) 
	{
		String name=t.getName();
		Boolean prop=t.getProperty();
		switch (name) {
		case TagNames.SET_WAVELENGTH:
			setWavelength(null, prop);
			waveLengthSett.setVisible(true);

			break;
		case TagNames.ATTENUATION:
			setAttenuation(null, prop);
			attenuation.setVisible(true);

			break;
		default: 
			LOGGER.warn("[CONF] LIGHTSRC SETT unknown tag: "+name );
			break;
		}
	}

	/**
	 * Show data of objective
	 */
	private void setGUIData() 
	{
		((LightSourceSubViewer) globalPane.getComponent(sourceType.getSelectedIndex())).setGUIData();

	}

	private void setSettingsGUIData()
	{
		if(data==null)
			return;
		LightSourceSettings settings = data.getSettings(index);

		if(settings!=null){
			try{setWavelength(settings.getWavelength(), ElementsCompUI.REQUIRED);
			} catch (NullPointerException e) { }
			try{setAttenuation(settings.getAttenuation(), ElementsCompUI.REQUIRED);
			}catch (NullPointerException e){}
		}
	}



	/*------------------------------------------------------
	 * Set methods data Values
	 * -----------------------------------------------------*/



	/*------------------------------------------------------
	 * Set methods settings Values
	 * -----------------------------------------------------*/
	public void setWavelength(Length value, boolean prop)
	{
		String val=(value!=null) ? String.valueOf(value.value()) :"";
		Unit unit=(value!=null) ? value.unit():TagNames.WAVELENGTH_UNIT;
		if(waveLengthSett == null) 
			waveLengthSett = new TagData(TagNames.SET_WAVELENGTH,val,unit,prop,TagData.TEXTFIELD);
		else 
			waveLengthSett.setTagValue(val,unit,prop);

	}
	public void setAttenuation(PercentFraction value, boolean prop)
	{
		String val= (value != null) ? String.valueOf(value.getNumberValue()):"";
		if(attenuation == null) 
			attenuation = new TagData(TagNames.ATTENUATION,val,prop,TagData.TEXTFIELD);
		else 
			attenuation.setTagValue(val,prop);
	}

	@Override
	public void saveData() 
	{
		((LightSourceSubViewer) globalPane.getComponent(sourceType.getSelectedIndex())).saveData();

		// --- Settings --------------------
		LightSourceSettings settings=data.getSettings(index);
		if(settings==null)
			settings = new LightSourceSettings();

		try{
			settings.setWavelength(parseToLength(waveLengthSett.getTagValue(),waveLengthSett.getTagUnit()));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read LIGHTSRC SETT wavelength input");
		}
		try{
			//TODO input format hint: percentvalue elem of [0,100] or [0,1]
			settings.setAttenuation(parseAttenuation(attenuation.getTagValue()));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read LIGHTSRC SETT attenuation input");
		}
		dataChanged=false;
	}
	private PercentFraction parseAttenuation(String c)
	{
		if(c==null || c.equals(""))
			return null;

		return new PercentFraction(Float.valueOf(c));
	}

	@Override
	public boolean hasDataToSave() 
	{
		boolean result=false;
		if(tagList!=null){
			for(int i=0; i<tagList.size();i++){
				boolean val=tagList.get(i)!=null ? tagList.get(i).valueChanged() : false;
				result= result || dataChanged || 
						val || ((LightSourceSubViewer) globalPane.getComponent(sourceType.getSelectedIndex())).hasDataToSave();
			}
		}
		return (result);
	}

}


