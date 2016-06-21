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


import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.ElementsCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.TagData;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.ModuleConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.TagNames;

import ome.units.UNITS;
import ome.units.quantity.Length;
import ome.units.unit.Unit;
import ome.xml.model.Image;
import ome.xml.model.Objective;
import ome.xml.model.ObjectiveSettings;
import ome.xml.model.enums.Correction;
import ome.xml.model.enums.EnumerationException;
import ome.xml.model.enums.Immersion;
import ome.xml.model.enums.LaserMedium;

/**
 * Implements the objectiv data panel. Define here default values for input.
 * TODO: visuell marker for necessary fields
 * @author kunis
 *
 */
public class ObjectiveCompUI extends ElementsCompUI
{
	
	private TagData model;
	private TagData manufact;
	private TagData nomMagn;
	private TagData calMagn;
	private TagData lensNA;
	private TagData immersion;
	private TagData correction;
	private TagData workDist;
	private TagData iris;
	
	private List<TagData> tagList;
	
	private static Unit<Length> workDistUnit=UNITS.MICROM;
	
	private JPanel globalPane;
	
	private Objective objective;
	private ObjectiveSettingsCompUI objectiveSettUI;
	private Box box;
	
	private List<Objective> availableObj;
	private boolean setFields;
	

	
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
		return (result || objectiveSettUI.userInput() || setFields);
	}
	

	
//	public ObjectiveCompUI(Objective _objective, int _linkImageIdx)
//	{
//		workDistUnit=UNITS.MICROM;
//		objectiveSettUI=new ObjectiveSettingsCompUI(null);
//		objective=_objective;
//		
//		initGUI();
//		if(objective!=null)
//			setGUIData();
//		
//	}
	
	public ObjectiveCompUI(ModuleConfiguration objConf) 
	{
		objectiveSettUI=new ObjectiveSettingsCompUI(objConf);
		
		initGUI();
		if(objConf==null)
			createDummyPane(false);
		else
			createDummyPane(objConf.getTagList(),false);
	}

	
//
//	public boolean addData(Objective obj, boolean overwrite)
//	{
//		boolean conflicts=false;
//		if(objective!=null){
//			if(obj!=null){
//				String mo=obj.getModel();
//				String ma=obj.getManufacturer();
//				Double nm=obj.getNominalMagnification();
//				Double cM=obj.getCalibratedMagnification();
//				Double l=obj.getLensNA();
//				Immersion i=obj.getImmersion();
//				Correction c=obj.getCorrection();
//				Length wD=obj.getWorkingDistance();
//				Boolean ir=obj.getIris();
//				if(overwrite){
//					if(obj.getID()!=null && !obj.getID().equals(""))
//						objective.setID(obj.getID());
//					if(mo!=null && !mo.equals("")) objective.setModel(mo);
//					if(ma!=null && !ma.equals("")) objective.setManufacturer(ma);
//					if(nm!=null) objective.setNominalMagnification(nm);
//					if(cM!=null) objective.setCalibratedMagnification(cM);
//					if(l!=null) objective.setLensNA(l);
//					if(i!=null) objective.setImmersion(i);
//					if(c!=null) objective.setCorrection(c);
//					if(wD!=null) objective.setWorkingDistance(wD);
//					if(ir!=null) objective.setIris(ir);
//					LOGGER.info("[DATA] overwrite OBJECTIVE data");
//				}else{
//					if(objective.getID()==null || objective.getID().equals(""))
//						objective.setID(obj.getID());
//					if(objective.getModel()==null || objective.getModel().equals("") )
//						objective.setModel(mo);
//					if(objective.getManufacturer()==null || objective.getManufacturer().equals("") )
//						objective.setManufacturer(ma);
//					if(objective.getNominalMagnification()==null)
//						objective.setNominalMagnification(nm);
//					if(objective.getCalibratedMagnification()==null)
//						objective.setCalibratedMagnification(cM);
//					if(objective.getLensNA()==null)
//						objective.setLensNA(l);
//					if(objective.getImmersion()==null)
//						objective.setImmersion(i);
//					if(objective.getCorrection()==null)
//						objective.setCorrection(c);
//					if(objective.getWorkingDistance()==null)
//						objective.setWorkingDistance(wD);
//					if(objective.getIris()==null)
//						objective.setIris(ir);
//					LOGGER.info("[DATA] complete OBJECTIVE data");
//				}
//			}
//			
//		}else if(obj!=null){
//			objective=obj;
//			LOGGER.info("[DATA] add OBJECTIVE data");
//		}
//		setGUIData();
//		
//		return conflicts;
//		
//	}
	
	public boolean addData(Objective obj, boolean overwrite)
	{
		boolean conflicts=false;
		if(overwrite){
			replaceData(obj);
			LOGGER.info("[DATA] -- replace OBJECTIVE data");
		}else
			try {
				completeData(obj);
				LOGGER.info("[DATA] -- complete OBJECTIVE data");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		setGUIData();
		return conflicts;
	}
	
	private void completeData(Objective o) throws Exception
	{
		//copy input fields
		Objective copyIn=null;
		if(objective!=null){
			getData();
			copyIn=new Objective(objective);
		}

		replaceData(o);
		// set input field values again
		if(copyIn!=null){
			String mo=copyIn.getModel();
			String ma=copyIn.getManufacturer();
			Double nm=copyIn.getNominalMagnification();
			Double cM=copyIn.getCalibratedMagnification();
			Double l=copyIn.getLensNA();
			Immersion i=copyIn.getImmersion();
			Correction c=copyIn.getCorrection();
			Length wD=copyIn.getWorkingDistance();
			Boolean ir=copyIn.getIris();
			
			if(mo!=null && !mo.equals("")) objective.setModel(mo);
			if(ma!=null && !ma.equals("")) objective.setManufacturer(ma);
			if(nm!=null) objective.setNominalMagnification(nm);
			if(cM!=null) objective.setCalibratedMagnification(cM);
			if(l!=null) objective.setLensNA(l);
			if(i!=null) objective.setImmersion(i);
			if(c!=null) objective.setCorrection(c);
			if(wD!=null) objective.setWorkingDistance(wD);
			if(ir!=null) objective.setIris(ir);
		}
	}
	
	private void replaceData(Objective o)
	{
		if(o!=null){
			objective=o;
		}
	}
	
	public void addData(ObjectiveSettings os, boolean overwrite)
	{
		if(objectiveSettUI!=null)
			objectiveSettUI.addData(os,overwrite);
	}


	
	public void addToList(List<Objective> list)
	{
		if(list==null || list.size()==0)
			return;

		if(availableObj==null){
			availableObj=new ArrayList<Objective>();
		}
		for(int i=0; i<list.size(); i++){
			availableObj.add(list.get(i));
		}

	}
	
	/**
	 * Show data of objective
	 */
	private void setGUIData() 
	{
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

	private void initGUI()
	{
		setLayout(new BorderLayout(5,5));
		setBorder(BorderFactory.createCompoundBorder(new TitledBorder(""),
				BorderFactory.createEmptyBorder(5,10,5,10)));
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
				ObjectiveEditor creator = new ObjectiveEditor(new JFrame(),"Select Objective",
						availableObj);
				Objective selectedObj=creator.getObjective();  
				if(selectedObj!=null ){
					setFields=true;
					objective=selectedObj;
					setGUIData();
					revalidate();
					repaint();
				}
			}
		});
		
//		add(new TitledSeparator("Objective", 3, TitledBorder.DEFAULT_POSITION),BorderLayout.NORTH);
		add(box,BorderLayout.NORTH);
		add(editBtn,BorderLayout.SOUTH);
	
	}
	
	private void readGUIInput() throws Exception
	{
		if(objective==null)
			createNewElement();

		//TODO input checker
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
	
	private void createNewElement() {
		objective=new Objective();		
	}

	
	
	public Objective getData() throws Exception
	{
		if(userInput())
			readGUIInput();
		return objective;
	}
	
	public void buildComponents()
	{
		labels.clear();
		comp.clear();
		addTagToGUI(model);
		addTagToGUI(manufact);
		addTagToGUI(nomMagn);
		addTagToGUI(calMagn);
		addTagToGUI(lensNA);
		addTagToGUI(immersion);
		addTagToGUI(correction);
		addTagToGUI(workDist);
		
		addLabelTextRows(labels, comp, gridbag, globalPane);
		
		c.gridwidth = GridBagConstraints.REMAINDER; //last
		c.anchor = GridBagConstraints.WEST;
		c.weightx = 1.0;
		
		objectiveSettUI.buildComponents();
		box.add(Box.createVerticalStrut(20));
		box.add(objectiveSettUI);
		
		
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
//			}
//		});
//		
//		JButton selectBtn=new JButton("Select from data...");
//		if(availableObj==null || availableObj.isEmpty())
//			selectBtn.setEnabled(false);
//		
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
	
	public void createNewElem()
	{
		globalPane.removeAll();
    	globalPane.setLayout(gridbag);
    	createDummyPane(false);
    	buildComponents();   
    	revalidate();
    	repaint();
	}
	
	@Override
	public void createDummyPane(boolean inactive) 
	{
		clearDataValues();
		
		setModel(null,OPTIONAL);
		setManufact(null,OPTIONAL);
		setNomMagnification(null,OPTIONAL);
		setCalMagnification(null,OPTIONAL);
		setLensNA(null,OPTIONAL);
		setImmersion(null,OPTIONAL);
		setCorrection(null,OPTIONAL);
		setWorkingDist(null,OPTIONAL);
		setIris(null,OPTIONAL);
		
		if(inactive){
			model.setEnable(false);
			manufact.setEnable(false);
			nomMagn.setEnable(false);
			calMagn.setEnable(false);
			lensNA.setEnable(false);
			immersion.setEnable(false);
			correction.setEnable(false);
			workDist.setEnable(false);
		}
		
//		objective=new Objective();
//		objective.setID(MetadataTools.createLSID("Objective", linkImageIdx,(list!=null)? list.size() : 0));
	}
	
	public void createDummyPane(List<TagConfiguration> taglist,boolean inactive) 
	{
		if(taglist==null)
			createDummyPane(inactive);
		else{
			clearDataValues();
//			if(objective==null && taglist!=null && taglist.size()>0)
//				createNewElement();
			for(int i=0; i<taglist.size();i++){
				TagConfiguration t=taglist.get(i);
				String name=t.getName();
				String val=t.getValue();
				boolean prop=t.getProperty()!= null ? Boolean.parseBoolean(t.getProperty()):
					OPTIONAL;
				if(name!=null){
					switch (name) {
					case TagNames.MODEL:
						setModel(val,prop);
						model.setVisible(true);
//						objective.setModel(val);
						break;
					case TagNames.MANUFAC:
						setManufact(val,prop);
						manufact.setVisible(true);
//						objective.setManufacturer(val);
						break;
					case TagNames.NOMMAGN:
						try{
							if(val!=null){
							setNomMagnification(Double.valueOf(val), prop);
							}else{
								setNomMagnification(null, prop);
							}
//							objective.setNominalMagnification(Double.valueOf(val));
						}catch(Exception e){
							setNomMagnification(null, prop);
						}
						nomMagn.setVisible(true);
						break;
					case TagNames.CALMAGN:
						try{
							if(val!=null){
							setCalMagnification(Double.valueOf(val), prop);
							}else{
								setCalMagnification(null,prop);
							}
//							objective.setCalibratedMagnification(Double.valueOf(val));
						}catch(Exception e){
							setCalMagnification(null,prop);
						}
						calMagn.setVisible(true);
						break;
					case TagNames.LENSNA:
						try{
							if(val!=null){
							setLensNA(Double.valueOf(val), prop);
							}else{
								setLensNA(null,prop);
							}
//							objective.setLensNA(Double.valueOf(val));
						}catch(Exception e){
							setLensNA(null,prop);
						}
						lensNA.setVisible(true);
						break;
					case TagNames.IMMERSION:
						try {
							setImmersion(parseImmersion(val), prop);
						} catch (Exception e) {
							setImmersion(null, prop);
						}
						immersion.setVisible(true);
						break;
					case TagNames.CORRECTION:
						try {
							setCorrection(parseCorrection(val), prop);
						} catch (Exception e) {
							setCorrection(null, prop);
						}
						correction.setVisible(true);
						break;
					case TagNames.WORKDIST:
						try{
							setWorkingDist(parseToLength(val, t.getUnit()), prop);
						}catch(Exception e){
							setWorkingDist(null,prop);
						}
						workDist.setVisible(true);
						break;
					default:
						LOGGER.warn("[CONF] unknown tag: "+name );break;
					}
				}
			}
		}

		if(inactive){
			model.setEnable(false);
			manufact.setEnable(false);
			nomMagn.setEnable(false);
			calMagn.setEnable(false);
			lensNA.setEnable(false);
			immersion.setEnable(false);
			correction.setEnable(false);
			workDist.setEnable(false);
		}
		
	}
	
	public void clearDataValues()
	{
		clearTagValue(model);
		clearTagValue(manufact);
		clearTagValue(nomMagn);
		clearTagValue(calMagn);
		clearTagValue(lensNA);
		clearTagValue(immersion);
		clearTagValue(correction);
		clearTagValue(workDist);
		
		if(objectiveSettUI!=null){
			objectiveSettUI.clearDataValues();
		}
	}
	
	
	public ObjectiveSettingsCompUI getSettings()
	{
		return objectiveSettUI;
	}
	
	public void setModel(String value,boolean prop)
	{
		if(model == null) 
			model = new TagData(TagNames.MODEL+": ",value,prop,TagData.TEXTFIELD);
		else 
			model.setTagValue(value,prop);
	}
	public void setModel(String value)
	{
		setModel(value, REQUIRED);
	}
	
	public void setManufact(String value,boolean prop)
	{
		if(manufact == null) 
			manufact = new TagData(TagNames.MANUFAC+": ",value,prop,TagData.TEXTFIELD);
		else 
			manufact.setTagValue(value,prop);
	}
	public void setManufact(String value)
	{
		setManufact(value, REQUIRED);
	}
	
	public void setNomMagnification(Double value,boolean prop)
	{
		String val= (value != null) ? String.valueOf(value):"";
		if(nomMagn == null) 
			nomMagn = new TagData(TagNames.NOMMAGN+": ",val,prop,TagData.TEXTFIELD);
		else 
			nomMagn.setTagValue(val,prop);
	}
	
	
	
	public void setNomMagnification(Double value)
	{
		setNomMagnification(value, REQUIRED);
	}
	
	public void setCalMagnification(Double value,boolean prop)
	{
		String val= (value != null) ? String.valueOf(value):"";
		if(calMagn == null) 
			calMagn = new TagData(TagNames.CALMAGN+": ",val,prop,TagData.TEXTFIELD);
		else 
			calMagn.setTagValue(val,prop);
	}
	public void setCalMagnification(Double value)
	{
		setCalMagnification(value, REQUIRED);
	}
	
	public void setLensNA(Double value,boolean prop)
	{
		String val= (value != null) ? String.valueOf(value):"";
		if(lensNA == null) 
			lensNA = new TagData(TagNames.LENSNA+": ",val,prop,TagData.TEXTFIELD);
		else 
			lensNA.setTagValue(val,prop);
	}
	public void setLensNA(Double value)
	{
		setLensNA(value, REQUIRED);
	}
	
	public void setImmersion(Immersion value,boolean prop)
	{
		String val= (value != null) ? String.valueOf(value):"";
		if(immersion == null) 
			immersion = new TagData(TagNames.IMMERSION+": ",val,prop,TagData.COMBOBOX,getNames(Immersion.class));
		else 
			immersion.setTagValue(val,prop);
	}
	public void setImmersion(Immersion value)
	{
		setImmersion(value, REQUIRED);
	}
	
	public void setCorrection(Correction value,boolean prop)
	{
		String val= (value != null) ? String.valueOf(value):"";
		if(correction == null) 
			correction = new TagData(TagNames.CORRECTION+": ",val,prop,TagData.COMBOBOX,getNames(Correction.class));
		else 
			correction.setTagValue(val,prop);
	}
	public void setCorrection(Correction value)
	{
		setCorrection(value, REQUIRED);
	}
	
	public void setWorkingDist(Length value,boolean prop)
	{
		String val=(value!=null) ? String.valueOf(value.value()) :"";
		Unit unit=(value!=null) ? value.unit() : workDistUnit;
		if(workDist == null) 
//			workDist = new TagData(TagNames.WORKDIST+" ["+workDistUnit.getSymbol()
//					+"]: ",val,prop,TagData.TEXTFIELD);
			workDist = new TagData(TagNames.WORKDIST,val,unit,prop,TagData.TEXTFIELD);
		else 
			workDist.setTagValue(val,unit,prop);
	}
	public void setWorkingDist(Length value)
	{
		setWorkingDist(value, REQUIRED);
	}
	
	public void setIris(Boolean value,boolean prop)
	{
		String val= (value != null) ? String.valueOf(value):"";
		if(iris == null) 
			iris = new TagData("Iris: ",val,prop,TagData.TEXTFIELD);
		else 
			iris.setTagValue(val,prop);
	}
	
	public void setIris(Boolean value)
	{
		setIris(value, REQUIRED);
	}

	@Override
	public List<TagData> getActiveTags() {
		
		List<TagData> list = new ArrayList<TagData>();
		if(isActive(model)) list.add(model);
		if(isActive(manufact)) list.add(manufact);
		if(isActive(nomMagn)) list.add(nomMagn);
		if(isActive(calMagn)) list.add(calMagn);
		if(isActive(lensNA)) list.add(lensNA);
		if(isActive(immersion)) list.add(immersion);
		if(isActive(correction)) list.add(correction);
		if(isActive(workDist)) list.add(workDist);
		//if(isActive(iris)) list.add(iris);
		
		return list;
		
		
	
	}

	public void setFieldsExtern(boolean b) {
		setFields= setFields || b;		
	}

	public void clearList() 
	{
		availableObj=null;
	}

	
	
}
	
