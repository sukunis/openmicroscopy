package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;





















import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.format.ObservedSample;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.format.Sample;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.format.Sample.GridBox;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.ModuleConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.TagNames;

import ome.xml.model.XMLAnnotation;
import ome.xml.model.primitives.Timestamp;
import loci.formats.MetadataTools;
import loci.formats.meta.IMetadata;

public class SampleCompUI extends ElementsCompUI
{
		
	private TagData preparationDate;
	private TagData preparationDescription;
	
	private TagData gridBoxNumber;
	private TagData gridBoxType;
	
	private TagData expGrid;
	private TagData expObjectNr;
	private TagData expObjectType;
	
	private TagData rawMaterialDesc;
	private TagData rawMaterialCode;
	
	private List<TagData> tagList;
	
	
	private Sample sample;
	private boolean setFields;
	
	private void initTagList()
	{
		tagList=new ArrayList<TagData>();
		tagList.add(preparationDate);
		tagList.add(preparationDescription);
		tagList.add(gridBoxNumber);
		tagList.add(gridBoxType);
		tagList.add(expGrid);
		tagList.add(expObjectNr);
		tagList.add(expObjectType);
		tagList.add(rawMaterialDesc);
		tagList.add(rawMaterialCode);
		
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
		return result || setFields;
	}
	
	public SampleCompUI(Sample _sample,int i)
	{
		sample=_sample;
		initGUI();
		if(sample!=null){
			setGUIData();
		}else{
			sample=new Sample();
			createDummyPane(false);
		}
	}
	
	public SampleCompUI(ModuleConfiguration objConf) 
	{
		initGUI();
		if(objConf==null)
			createDummyPane(false);
		else
			createDummyPane(objConf.getTagList(),false);
	}


	private void setGUIData() 
	{
		if(sample!=null){
			try{ setPreparationDescription(sample.getPrepDescription(), REQUIRED);}
			catch(NullPointerException e){}

			try{ setPreparationDate(sample.getPrepDate(), REQUIRED);}
			catch(NullPointerException e){}

			try{ setRawMaterialCode(sample.getRawMaterialCode(), OPTIONAL);}
			catch(NullPointerException e){}

			try{ setRawMaterialDesc(sample.getRawMaterialDesc(), REQUIRED);}
			catch(NullPointerException e){}

			try{ setGridBoxNumber(sample.getGridBox().getNr(), REQUIRED);}
			catch(NullPointerException e){}

			try{ setGridType(sample.getGridBox().getType(), REQUIRED);}
			catch(NullPointerException e){}

			try{
				String[] n={sample.getObservedSample(0).getGridNumberX(),
					sample.getObservedSample(0).getGridNumberY()};
			
				setExpGridNumber(n, REQUIRED);
			
			}catch(NullPointerException e){}
			

			try{ setExpObjectType(sample.getObservedSample(0).getObjectType(), REQUIRED);}
			catch(NullPointerException e){}

			try{ setExpObjectNr(sample.getObservedSample(0).getObjectNumber(), REQUIRED);}
			catch(NullPointerException e){}
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
		globalPane=new JPanel();
		globalPane.setLayout(gridbag);
		
		add(new TitledSeparator("Sample", 3, TitledBorder.DEFAULT_POSITION, true),BorderLayout.NORTH);
		add(globalPane,BorderLayout.NORTH);
		setBorder(
//				BorderFactory.createCompoundBorder(	new MyTitledBorder("Objective"),
						BorderFactory.createEmptyBorder(10,10,10,10));
	}
	
	@Override
	public void buildComponents() {
		labels.clear();
		comp.clear();
		
		
		
		addLabelToGUI(new JLabel("Raw Material:"));
		addTagToGUI(rawMaterialCode);
		addTagToGUI(rawMaterialDesc);
		addVSpaceToGui(10);
		
		
		addLabelToGUI(new JLabel("Preparation:"));
		addTagToGUI(preparationDate);
		addTagToGUI(preparationDescription);
		addTagToGUI(gridBoxNumber);
		addTagToGUI(gridBoxType);
		
		addVSpaceToGui(10);
		
		addLabelToGUI(new JLabel("Observed Sample:"));
		addTagToGUI(expGrid);
		addTagToGUI(expObjectType);
		addTagToGUI(expObjectNr);
		
		addLabelTextRows(labels, comp, gridbag, globalPane);
		
		c.gridwidth = GridBagConstraints.REMAINDER; //last
		c.anchor = GridBagConstraints.WEST;
		c.weightx = 1.0;
		
		buildComp=true;		
		initTagList();
		setFields=false;
	}
	@Override
	public void buildExtendedComponents() 
	{
					
	}
	@Override
	public void createDummyPane(boolean inactive) 
	{
		setRawMaterialCode(null,OPTIONAL);
		setRawMaterialDesc(null,OPTIONAL);
		setPreparationDate(null, OPTIONAL);
		setPreparationDescription(null, OPTIONAL);
		setGridBoxNumber(null, OPTIONAL);
		setGridType(null, OPTIONAL);
		setExpGridNumber(new String[2], OPTIONAL);
		setExpObjectNr(null, OPTIONAL);
		setExpObjectType(null, OPTIONAL);
	}
	
	public void createDummyPane(List<TagConfiguration> list,boolean inactive) 
	{
		if(list==null)
			createDummyPane(inactive);
		else{
			clearDataValues();
			if(sample==null && list!=null && list.size()>0)
				createNewElement();
			for(int i=0; i<list.size();i++){
				TagConfiguration t=list.get(i);
				String name=t.getName();
				String val=t.getValue();
				boolean prop=t.getProperty()!= null ? Boolean.parseBoolean(t.getProperty()):
					OPTIONAL;
				if(name!=null){
					switch (name) {
					case TagNames.PREPDATE:// no pre value possible
						setPreparationDate(null, prop);
						preparationDate.setVisible(true);
						break;
					case TagNames.PREPDESC:// no pre value possible
						setPreparationDescription(null, prop);
						preparationDescription.setVisible(true);
						break;
					case TagNames.RAWCODE:// no pre value possible
						setRawMaterialCode(null, prop); 
						rawMaterialCode.setVisible(true);
						break;
					case TagNames.RAWDESC:// no pre value possible
						setRawMaterialDesc(val, prop);
						rawMaterialDesc.setVisible(true);
						break;
					case TagNames.GRIDBOXNR:// no pre value possible
						setGridBoxNumber(null, prop);
						gridBoxNumber.setVisible(true);
						break;
					case TagNames.GRIDBOXTYPE:// no pre value possible
						setGridType(null, prop);
						gridBoxType.setVisible(true);
						break;
					case TagNames.EXPGRID:// no pre value possible
						setExpGridNumber(new String[2], prop);
						expGrid.setVisible(true);
						break;
					case TagNames.EXPOBJNR:// no pre value possible
						setExpObjectNr(null, prop);
						expObjectNr.setVisible(true);
						break;
					case TagNames.EXPOBJTYPE: // no pre value possible
						setExpObjectType(null, prop);
						expObjectType.setVisible(true);
						break;
					default:
						LOGGER.warning("[CONF] unknown tag: "+name );break;
					}
				}
			}
		}
	}
	

	@Override
	public void clearDataValues() 
	{
		if(tagList!=null)
			for(int i=0; i<tagList.size();i++) 
				clearTagValue(tagList.get(i));
	}
	
	public Sample getData() throws Exception
	{
		if(userInput())
			readGUIInput();
		return sample;
	}
	
	public boolean addData(Sample s,boolean overwrite)
	{
		boolean conflict=false;
		if(s==null)
			return false;
		if(sample!=null){
				String pdesc=s.getPrepDescription();
				Timestamp pdate=s.getPrepDate();
				String rc=s.getRawMaterialCode();
				String rdesc=s.getRawMaterialDesc();
				
				GridBox g=s.getGridBox();
				Integer gNr=null;
				String gT=null;
				if(g!=null){
					gNr=g.getNr();
					gT=g.getType();
				}
				ObservedSample os=s.getObservedSample(0);
				String osgx=null;
				String osgy=null;
				String ost=null;
				String osNr=null;
				if(os!=null){
					osgx=os.getGridNumberX();
					osgy=os.getGridNumberY();
					ost=os.getObjectType();
					osNr=os.getObjectNumber();
				}
				
				if(overwrite){
					if(pdesc!=null && !pdesc.equals("")) sample.setPrepDescription(pdesc);
					if(pdate!=null) sample.setPrepDate(pdate);
					if(rc!=null && !rc.equals("")) sample.setRawMaterialCode(rc);
					if(rdesc!=null && !rdesc.equals("")) sample.setRawMaterialDesc(rdesc);
					if(gNr!=null) sample.getGridBox().setNr(gNr);
					if(gT!=null && !gT.equals("")) sample.getGridBox().setType(gT);
					if(osgx!=null && !osgx.equals("")) sample.getObservedSample(0).setGridNumberY(osgx);
					if(osgy!=null && !osgy.equals("")) sample.getObservedSample(0).setGridNumberY(osgy);
					if(ost!=null && !ost.equals("")) sample.getObservedSample(0).setObjectType(ost);
					if(osNr!=null && !osNr.equals("")) sample.getObservedSample(0).setObjectNumber(osNr);
					LOGGER.info("[DATA] overwrite SAMPLE data");
				}else{
					if(sample.getPrepDescription()==null || sample.getPrepDescription().equals(""))
						sample.setPrepDescription(pdesc);
					if(sample.getPrepDate()==null )
						sample.setPrepDate(pdate);
					if(sample.getRawMaterialCode()==null || sample.getRawMaterialCode().equals(""))
						sample.setRawMaterialCode(rc);
					if(sample.getRawMaterialDesc()==null || sample.getRawMaterialDesc().equals(""))
						sample.setRawMaterialDesc(rdesc);
					
					if(sample.getGridBox()==null){
						sample.setGridBoxData(gNr, gT); 
					}else{
						if(sample.getGridBox().getNr()==null )
							sample.getGridBox().setNr(gNr);
						if(sample.getGridBox().getType()==null || sample.getGridBox().getType().equals(""))
							sample.getGridBox().setType(gT);
					}
					if(sample.getObservedSample(0)==null){
						sample.setObservedSample(os);
					}else{
						if(sample.getObservedSample(0).getGridNumberX()==null || sample.getObservedSample(0).getGridNumberX().equals(""))
							sample.getObservedSample(0).setGridNumberX(osgx);
						if(sample.getObservedSample(0).getGridNumberY()==null || sample.getObservedSample(0).getGridNumberY().equals(""))
							sample.getObservedSample(0).setGridNumberY(osgy);
						if(sample.getObservedSample(0).getObjectType()==null || sample.getObservedSample(0).getObjectType().equals(""))
							sample.getObservedSample(0).setObjectType(ost);
						if(sample.getObservedSample(0).getObjectNumber()==null || sample.getObservedSample(0).getObjectNumber().equals(""))
							sample.getObservedSample(0).setObjectNumber(osNr);
					}
					LOGGER.info("[DATA] complete SAMPLE data");
				}
		}else{
			sample=s;
			
			LOGGER.info("[DATA] add SAMPLE data");
		}
		
		setGUIData();
		return conflict;
	}
	
	private void readGUIInput() 
	{
		if(sample==null)
			createNewElement();
		//TODO input checker
		try{sample.setPrepDate(preparationDate.getTagValue().equals("")? 
				null : Timestamp.valueOf(preparationDate.getTagValue()));}
		catch(Exception e){}
		try{
			sample.setPrepDescription(preparationDescription.getTagValue());
		}catch(Exception e){
			LOGGER.severe("[DATA] can't read SAMPLE preparation description input");
		}
		try{
			String g1=gridBoxNumber!=null ? gridBoxNumber.getTagValue() : null;
			String g2=gridBoxType!=null ? gridBoxType.getTagValue(): null;
			sample.setGridBoxData(g1, g2);
		}catch(Exception e){
			LOGGER.severe("[DATA] can't read SAMPLE grid box data input");
			e.printStackTrace();
		}
		
		ObservedSample observedSample=new ObservedSample();
		observedSample.setSampleID(MetadataTools.createLSID("ObservedSample", 0));
		try{
			observedSample.setObjectNumber(expObjectNr!=null ? expObjectNr.getTagValue(): null);
		}catch(Exception e){
			LOGGER.severe("[DATA] can't read SAMPLE observed sample object nr input");
		}
		try{
			observedSample.setObjectType(expObjectType!=null ? expObjectType.getTagValue():null);
		}catch(Exception e){
			LOGGER.severe("[DATA] can't read SAMPLE observed sample object type input");
		}
		try{
			observedSample.setGridNumberX(expGrid!=null ? expGrid.getTagValue(0):null);
		}catch(Exception e){
			LOGGER.severe("[DATA] can't read SAMPLE observed sample grid number x input");
		}
		try{
			observedSample.setGridNumberY(expGrid!=null ?expGrid.getTagValue(1):null);
		}catch(Exception e){
			LOGGER.severe("[DATA] can't read SAMPLE observed sample grid number y input");
		}
		sample.setObservedSample(observedSample);
		
		try{
			sample.setRawMaterialDesc(rawMaterialDesc!=null ? rawMaterialDesc.getTagValue():null); 
		}catch(Exception e){
			LOGGER.severe("[DATA] can't read SAMPLE raw material description input");
		}
		try{
			sample.setRawMaterialCode(rawMaterialCode!=null ? rawMaterialCode.getTagValue():null); 
		}catch(Exception e){
			LOGGER.severe("[DATA] can't read SAMPLE raw material code input");
		}
		
		
	}

	private void createNewElement()
	{
		sample=new Sample();
	}

	@Override
	public List<TagData> getActiveTags() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	private void setRawMaterialDesc(String value, boolean prop) 
	{
		if(rawMaterialDesc == null) 
			rawMaterialDesc = new TagData(TagNames.RAWDESC+": ",value,prop,TagData.TEXTPANE);
		else 
			rawMaterialDesc.setTagValue(value,prop);
	}

	private void setRawMaterialCode(String value, boolean prop) 
	{
		if(rawMaterialCode == null) 
			rawMaterialCode = new TagData(TagNames.RAWCODE+": ",value,prop,TagData.TEXTFIELD);
		else 
			rawMaterialCode.setTagValue(value,prop);
	}
	
	public void setPreparationDate(Timestamp value, boolean prop)
	{
		
		String val= (value != null) ? value.getValue():"";
		if(preparationDate == null) 
			preparationDate = new TagData(TagNames.PREPDATE+": ",val,prop,TagData.TIMESTAMP);
		else 
			preparationDate.setTagValue(val,prop);
	}
	
	public void setPreparationDescription(String value, boolean prop)
	{
		if(preparationDescription == null) 
			preparationDescription = new TagData(TagNames.PREPDESC+": ",value,prop,TagData.TEXTPANE);
		else 
			preparationDescription.setTagValue(value,prop);	
	}
	
	public void setGridBoxNumber(Integer value, boolean prop)
	{
		String val=(value!=null) ? String.valueOf(value):"";
		if(gridBoxNumber == null) 
			gridBoxNumber = new TagData(TagNames.GRIDBOXNR+": ",val,prop,TagData.TEXTFIELD);
		else {
			gridBoxNumber.setTagValue(val,0,prop);
		}
	}
	
	public void setGridType(String value, boolean prop)
	{
		if(gridBoxType == null) 
			gridBoxType = new TagData(TagNames.GRIDBOXTYPE+": ",value,prop,TagData.TEXTPANE);
		else 
			gridBoxType.setTagValue(value,prop);	
	}
	
	public void setExpGridNumber(String[] value, boolean prop)
	{
		if(expGrid == null) 
			expGrid = new TagData(TagNames.EXPGRID+": ",value,prop,TagData.ARRAYFIELDS);
		else{ 
			expGrid.setTagValue(value[0],0,prop);
			expGrid.setTagValue(value[1],1,prop);
		}
	}
	
	public void setExpObjectNr(String value, boolean prop)
	{
		if(expObjectNr == null) 
			expObjectNr = new TagData(TagNames.EXPOBJNR+": ",value,prop,TagData.TEXTPANE);
		else 
			expObjectNr.setTagValue(value,prop);	
	}
	
	public void setExpObjectType(String value, boolean prop)
	{
		if(expObjectType == null) 
			expObjectType = new TagData(TagNames.EXPOBJTYPE+": ",value,prop,TagData.TEXTPANE);
		else 
			expObjectType.setTagValue(value,prop);	
	}

	public void setFieldsExtern(boolean b) {
		setFields= setFields || b;		
	}
	
}
