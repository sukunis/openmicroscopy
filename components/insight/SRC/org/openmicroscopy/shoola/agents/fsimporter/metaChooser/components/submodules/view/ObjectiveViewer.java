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

import ome.units.quantity.Length;
import ome.units.unit.Unit;
import ome.xml.model.Objective;
import ome.xml.model.ObjectiveSettings;
import ome.xml.model.enums.Correction;
import ome.xml.model.enums.EnumerationException;
import ome.xml.model.enums.Immersion;
import ome.xml.model.enums.Medium;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules.ElementsCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules.ObjectiveEditor;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model.ObjectiveModel;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.ModuleConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.TagNames;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagData;
import org.slf4j.LoggerFactory;

public class ObjectiveViewer extends ModuleViewer 
{
	 private static final org.slf4j.Logger LOGGER =
	    	    LoggerFactory.getLogger(ObjectiveViewer.class);
	 
	private ObjectiveModel data;
	private Box box;
	
	// available objective tags
	private TagData model;
	private TagData manufact;
	private TagData nomMagn;
	private TagData calMagn;
	private TagData lensNA;
	private TagData immersion;
	private TagData correction;
	private TagData workDist;
	private TagData iris;
	//available objective setting tags
	private TagData corCollar;
	private TagData medium;
	private TagData refractIndex;
	
	/**
	 * Creates a new instance.
	 * @param model Reference to model.
	 */
	public ObjectiveViewer(ObjectiveModel model,ModuleConfiguration conf)
	{
		this.data=model;
		initComponents(conf);
		initTagList();
		buildGUI();
	}
	
	private void initTagList()
	{
		tagList=new ArrayList<TagData>();
		tagList.add(model);
		tagList.add(manufact);
		tagList.add(nomMagn);
		tagList.add(calMagn);
		tagList.add(lensNA);
		tagList.add(immersion);
		tagList.add(correction);
		tagList.add(workDist);
		tagList.add(iris);
		tagList.add(corCollar);
		tagList.add(medium);
		tagList.add(refractIndex);
		
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
		addTagToGUI(nomMagn,labels,comp);
		addTagToGUI(calMagn,labels,comp);
		addTagToGUI(lensNA,labels,comp);
		addTagToGUI(immersion,labels,comp);
		addTagToGUI(correction,labels,comp);
		addTagToGUI(workDist,labels,comp);
		
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
		addTagToGUI(corCollar,labelsSett,compSett);
		addTagToGUI(medium,labelsSett,compSett);
		addTagToGUI(refractIndex,labelsSett,compSett);
		
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
				ObjectiveEditor creator = new ObjectiveEditor(new JFrame(),"Select Objective",
						data.getList());
				Objective selectedObj=creator.getObjective();  
				if(selectedObj!=null ){
					try {
						data.addData(selectedObj, true);
					} catch (Exception e1) {
						LOGGER.warn("Can't set data of selected objective! "+e1);
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
		String name=t.getName();
		Boolean prop=t.getProperty();
		switch (name) {
		case TagNames.CORCOLLAR: 
			setCorCollar(null, prop);
			corCollar.setVisible(true);
			break;
		case TagNames.OBJ_MEDIUM: 
			setMedium(null, prop);
			medium.setVisible(true);
			break;
		case TagNames.REFINDEX:
			setRefractIndex(null, prop);
			refractIndex.setVisible(true);
			break;
		case TagNames.MODEL:
			setModel(null,prop);
			model.setVisible(true);
			break;
		case TagNames.MANUFAC:
			setManufact(null,prop);
			manufact.setVisible(true);
			break;
		case TagNames.NOMMAGN:
			setNomMagnification(null, prop);
			nomMagn.setVisible(true);
			break;
		case TagNames.CALMAGN:
			setCalMagnification(null,prop);
			calMagn.setVisible(true);
			break;
		case TagNames.LENSNA:
			setLensNA(null,prop);
			lensNA.setVisible(true);
			break;
		case TagNames.IMMERSION:
			setImmersion(null, prop);
			immersion.setVisible(true);
			break;
		case TagNames.CORRECTION:
			setCorrection(null, prop);
			correction.setVisible(true);
			break;
		case TagNames.WORKDIST:
			setWorkingDist(null,prop);
			workDist.setVisible(true);
			break;
		default:LOGGER.warn("[CONF] OBJECTIVE  unknown tag: "+name );break;
		}
	}

	/**
	 * Show data of objective
	 */
	private void setGUIData() 
	{
		Objective objective=data.getObjective();
		try{setModel(objective.getModel());
		} catch (NullPointerException e) { }
		try{setManufact(objective.getManufacturer());
		} catch (NullPointerException e) { }
		try{setNomMagnification(objective.getNominalMagnification());
		} catch (NullPointerException e) { }
		try{setCalMagnification(objective.getCalibratedMagnification());
		} catch (NullPointerException e) { }
		try{setLensNA(objective.getLensNA());
		} catch (NullPointerException e) { }
		try{setImmersion(objective.getImmersion());
		} catch (NullPointerException e) { }
		try{setCorrection(objective.getCorrection());
		} catch (NullPointerException e) { }
		try{setWorkingDist(objective.getWorkingDistance());
		} catch (NullPointerException e) { }
		try{setIris(objective.getIris());
		} catch (NullPointerException e) { }
		
	}
	
	private void setSettingsGUIData()
	{
		ObjectiveSettings settings = data.getSettings();
		try{setRefractIndex(settings.getRefractiveIndex(), ElementsCompUI.REQUIRED);
		} catch (NullPointerException e) { }
		try{ setMedium(settings.getMedium(), ElementsCompUI.REQUIRED);
		} catch (NullPointerException e) { }
		try{ setCorCollar(settings.getCorrectionCollar(), ElementsCompUI.REQUIRED);
		} catch (NullPointerException e) { }
	
	}


	

	
	
	private void setModel(String value,boolean prop)
	{
		if(model == null) 
			model = new TagData(TagNames.MODEL,value,prop,TagData.TEXTFIELD);
		else 
			model.setTagValue(value,prop);
	}
	private void setModel(String value)
	{
		setModel(value, REQUIRED);
	}
	
	private void setManufact(String value,boolean prop)
	{
		if(manufact == null) 
			manufact = new TagData(TagNames.MANUFAC,value,prop,TagData.TEXTFIELD);
		else 
			manufact.setTagValue(value,prop);
	}
	private void setManufact(String value)
	{
		setManufact(value, REQUIRED);
	}
	
	private void setNomMagnification(Double value,boolean prop)
	{
		String val= (value != null) ? String.valueOf(value):"";
		if(nomMagn == null) 
			nomMagn = new TagData(TagNames.NOMMAGN,val,prop,TagData.TEXTFIELD);
		else 
			nomMagn.setTagValue(val,prop);
	}
	
	
	
	private void setNomMagnification(Double value)
	{
		setNomMagnification(value, REQUIRED);
	}
	
	private void setCalMagnification(Double value,boolean prop)
	{
		String val= (value != null) ? String.valueOf(value):"";
		if(calMagn == null) 
			calMagn = new TagData(TagNames.CALMAGN,val,prop,TagData.TEXTFIELD);
		else 
			calMagn.setTagValue(val,prop);
	}
	private void setCalMagnification(Double value)
	{
		setCalMagnification(value, REQUIRED);
	}
	
	private void setLensNA(Double value,boolean prop)
	{
		String val= (value != null) ? String.valueOf(value):"";
		if(lensNA == null) 
			lensNA = new TagData(TagNames.LENSNA,val,prop,TagData.TEXTFIELD);
		else 
			lensNA.setTagValue(val,prop);
	}
	private void setLensNA(Double value)
	{
		setLensNA(value, REQUIRED);
	}
	
	private void setImmersion(Immersion value,boolean prop)
	{
		String val= (value != null) ? String.valueOf(value):"";
		if(immersion == null) 
			immersion = new TagData(TagNames.IMMERSION,val,prop,TagData.COMBOBOX,getNames(Immersion.class));
		else 
			immersion.setTagValue(val,prop);
	}
	private void setImmersion(Immersion value)
	{
		setImmersion(value, REQUIRED);
	}
	
	private void setCorrection(Correction value,boolean prop)
	{
		String val= (value != null) ? String.valueOf(value):"";
		if(correction == null) 
			correction = new TagData(TagNames.CORRECTION,val,prop,TagData.COMBOBOX,getNames(Correction.class));
		else 
			correction.setTagValue(val,prop);
	}
	private void setCorrection(Correction value)
	{
		setCorrection(value, REQUIRED);
	}
	
	private void setWorkingDist(Length value,boolean prop)
	{
		String val=(value!=null) ? String.valueOf(value.value()) :"";
		Unit unit=(value!=null) ? value.unit() : TagNames.WORKDIST_UNIT;
		if(workDist == null) 
			workDist = new TagData(TagNames.WORKDIST,val,unit,prop,TagData.TEXTFIELD);
		else 
			workDist.setTagValue(val,unit,prop);
	}
	private void setWorkingDist(Length value)
	{
		setWorkingDist(value, REQUIRED);
	}
	
	private void setIris(Boolean value,boolean prop)
	{
		String val= (value != null) ? String.valueOf(value):"";
		if(iris == null) 
			iris = new TagData("Iris",val,prop,TagData.TEXTFIELD);
		else 
			iris.setTagValue(val,prop);
	}
	
	private void setIris(Boolean value)
	{
		setIris(value, REQUIRED);
	}
	
	/*------------------------------------------------------
	 * Settings Values
	 * -----------------------------------------------------*/
	private void setCorCollar(Double value,boolean prop)
	{
		String val= (value != null) ? String.valueOf(value):"";
		if(corCollar == null) 
			corCollar = new TagData(TagNames.CORCOLLAR,val,prop,TagData.TEXTFIELD);
		else 
			corCollar.setTagValue(val,prop);
	}
	private void setMedium(Medium value,boolean prop)
	{
		String val= (value != null) ? String.valueOf(value):"";
		if(medium == null) 
			medium = new TagData(TagNames.OBJ_MEDIUM,val,prop,TagData.COMBOBOX,getNames(Medium.class));
		else 
			medium.setTagValue(val,prop);
	}
	private void setRefractIndex(Double value,boolean prop)
	{
		String val= (value != null) ? String.valueOf(value):"";
		if(refractIndex == null) 
			refractIndex = new TagData(TagNames.REFINDEX,val,prop,TagData.TEXTFIELD);
		else 
			refractIndex.setTagValue(val,prop);
	}

	@Override
	public void saveData() 
	{
		Objective objective =data.getObjective();
		if(objective==null)
			objective = new Objective();
		try{
			objective.setModel(model.getTagValue());
		}catch(Exception e){
			LOGGER.error("[DATA] can't read OBJECTIVE model input");
		}
		try{
			objective.setManufacturer(manufact.getTagValue());
		}catch(Exception e){
			LOGGER.error("[DATA] can't read OBJECTIVE manufacturer input");
		}
		try{objective.setNominalMagnification(nomMagn.getTagValue().equals("")? 
				null : Double.valueOf(nomMagn.getTagValue()));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read OBJECTIVE nominal magnification input");
		}
		try{
			objective.setCalibratedMagnification(calMagn.getTagValue().equals("")? 
					null : Double.valueOf(calMagn.getTagValue()));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read OBJECTIVE calibrated magnification input");
		}
		try{
			objective.setLensNA(lensNA.getTagValue().equals("")? 
					null : Double.valueOf(lensNA.getTagValue()));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read OBJECTIVE lensNa input");
		}
		try{
			objective.setImmersion(parseImmersion(immersion.getTagValue()));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read OBJECTIVE immersion input");
		}
		try{

			objective.setCorrection(parseCorrection(correction.getTagValue()));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read OBJECTIVE correction input");
		}
		try{
			objective.setWorkingDistance(workDist.getTagValue().equals("")?
					null : new Length(new Double(workDist.getTagValue()), workDist.getTagUnit()));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read OBJECTIVE working distance input");
		}
		// --- Settings --------------------
		ObjectiveSettings settings=data.getSettings();
		if(settings==null)
			settings = new ObjectiveSettings();
		
		//TODO input checker
		try{
			settings.setRefractiveIndex(parseToDouble(refractIndex.getTagValue()));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read OBJECTIVE SETT refraction index input");
		}
		try{
			settings.setMedium(parseMedium(medium.getTagValue()));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read OBJECTIVE SETT medium input");
		}
		try{
			settings.setCorrectionCollar(parseToDouble(corCollar.getTagValue()));
		}catch(Exception e){
			LOGGER.error("[DATA] can't read OBJECTIVE SETT correction collar input");
		}
		dataChanged=false;
	}
	
	private Medium parseMedium(String c) 
	{
		if(c==null || c.equals(""))
			return null;
		
		Medium a=null;
		try{
			a=Medium.fromString(c);
		}catch(EnumerationException e){
			LOGGER.warn("Medium: "+c+" is not supported");
			a=Medium.OTHER;
		}
		return a;
	}
	
	private Immersion parseImmersion(String c) 
	{
		if(c==null || c.equals(""))
			return null;
		Immersion m=null;
		try{
			m=Immersion.fromString(c);
		}catch(EnumerationException e){
			LOGGER.warn("Immersion: "+c+" is not supported");
			m=Immersion.OTHER;
		}
		return m;
	}
	
	private Correction parseCorrection(String c) 
	{
		if(c==null || c.equals(""))
			return null;
		Correction m=null;
		try{
			m=Correction.fromString(c);
		}catch(EnumerationException e){
			LOGGER.warn("Correction: "+c+" is not supported");
			m=Correction.OTHER;
		}
		return m;
	}
	
}
