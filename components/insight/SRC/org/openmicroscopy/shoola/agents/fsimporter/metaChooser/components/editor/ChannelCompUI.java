package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.CheckBoxNodeEditor;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.CheckBoxNodeRendererTagData;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.CheckBoxNodeTagData;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.ElementsCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.LightPathCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.TagData;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.ModuleConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.TagNames;

import ome.units.UNITS;
import ome.units.quantity.Length;
import ome.units.unit.Unit;
import ome.xml.model.enums.AcquisitionMode;
import ome.xml.model.enums.ContrastMethod;
import ome.xml.model.enums.EnumerationException;
import ome.xml.model.enums.IlluminationType;
import ome.xml.model.primitives.Color;
import ome.xml.model.Channel;


public class ChannelCompUI extends ElementsCompUI 
{
	
	
	private TagData name;
	/** color used to render this channel*/
	private TagData color;
	/** name of fluorophore to produce this channel (only fluorescence images)*/
	private TagData fluorophore;
	/** type of illumination to capture the channel*/
	private TagData illumType;
	private TagData exposureTime;
	/** wavelength of excitation for a particular channel*/
	private TagData excitWavelength;
	/** wavelength of emission for a particular channel*/
	private TagData emissionWavelength;
	/**type of microscopy performed */
//	private TagData acquisitionMode;
	private TagData imagingMode;
	private TagData illuminationMode;
	/** method to achieve contrast for this channel*/
	private TagData contrastMethod;
	/**specify the combined effect of any neutral density filter used*/
	private TagData ndFilter;
	
	private List<TagData> tagList;
	
	//TODO
	private String stagePosRef;
	
	private Unit<Length> excitWavelengthUnit;
	private Unit<Length> emissionWavelengthUnit;
	
	//lightPath: array of filter and dichroic refs(ordered: first excitation{0,..}, dichroic{0,1}, emission{0,..}) 
	//or filterset (not ordered, not all used/active)
	private boolean lightPathOrdered;
	private LightPathCompUI lightPath;
	
	
	private Channel channel;
	private boolean setFields;
	
	
	private void initTagList()
	{
		tagList=new ArrayList<TagData>();
		tagList.add(name);
		tagList.add(color);
		tagList.add(fluorophore);
		tagList.add(illuminationMode);
		tagList.add(illumType);
		tagList.add(exposureTime);
		tagList.add(excitWavelength);
		tagList.add(emissionWavelength);
		tagList.add(imagingMode);
		tagList.add(contrastMethod);
		tagList.add(ndFilter);
		
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
		return (result || setFields);
	}
	
	/** 
	 * 
	 * @param _channel
	 * @param conf
	 */
	public ChannelCompUI(Channel _channel, ModuleConfiguration objConf)
	{
		
		this(objConf);
		
		if(channel!=null){
			channel=_channel;
			setGUIData();
		}
	}

	
	public ChannelCompUI(ModuleConfiguration objConf) 
	{
		excitWavelengthUnit=UNITS.NM;
		emissionWavelengthUnit=UNITS.NM;
		initGUI();
		if(objConf==null)
			createDummyPane(false);
		else
			createDummyPane(objConf.getTagList(),false);
	}
	
	private void createNewElement()
	{
		LOGGER.info("[DATA] create new CHANNEL");
		channel=new Channel();
	}

	private void initGUI()
	{
		setLayout(new BorderLayout(5,5));
//		setPreferredSize(new Dimension(100,50));

		buildComp=false;
		labels= new ArrayList<JLabel>();
		comp = new ArrayList<JComponent>();

		gridbag = new GridBagLayout();
		c = new GridBagConstraints();

		globalPane=new JPanel();
		globalPane.setLayout(gridbag);

		//		add(new TitledSeparator("Channel", 3, TitledBorder.DEFAULT_POSITION),BorderLayout.NORTH);
		add(globalPane,BorderLayout.NORTH);

		setBorder(
				//				BorderFactory.createCompoundBorder(	new MyTitledBorder("Objective"),
				BorderFactory.createEmptyBorder(10,10,10,10));
	}
	
	private void setGUIData()
	{
		if(channel!=null){
			//Channel data
			try{ setName(channel.getName(),ElementsCompUI.REQUIRED);
			} catch (NullPointerException e) { }
			try{ setColor(channel.getColor(), ElementsCompUI.REQUIRED);
			} catch (NullPointerException e) { }
			try{ setFluorophore(channel.getFluor(), ElementsCompUI.REQUIRED);
			} catch (NullPointerException e) { }
			try{ setIllumType(channel.getIlluminationType(), ElementsCompUI.REQUIRED);
			} catch (NullPointerException e) { }
			//TODO exposure time
			try{ setExposureTime(null, ElementsCompUI.REQUIRED);
			} catch (NullPointerException e) { }
			try{ setExcitWavelength(channel.getExcitationWavelength(), ElementsCompUI.REQUIRED);
			} catch (NullPointerException e) { }
			try{ setEmissionWavelength(channel.getEmissionWavelength(), ElementsCompUI.REQUIRED);
			} catch (NullPointerException e) { }
			try{ setImagingMode(channel.getAcquisitionMode(), ElementsCompUI.REQUIRED);
			setIlluminationMode(channel.getAcquisitionMode(), ElementsCompUI.REQUIRED);
			} catch (NullPointerException e) { }
			try{ setContrastMethod(channel.getContrastMethod(), ElementsCompUI.REQUIRED);
			} catch (NullPointerException e) { }
			try{ setNDFilter(channel.getNDFilter(), ElementsCompUI.REQUIRED);
			} catch (NullPointerException e) { }
		}
	}
	
	public boolean addData(Channel c, boolean overwrite) 
	{
		boolean conflicts=false;
		if(channel!=null){
			if(c!=null){
				//read data
				String name=c.getName();
				Color color=c.getColor();
				String fluor=c.getFluor();
				Length exW=c.getExcitationWavelength();
				Length emW=c.getEmissionWavelength();
				AcquisitionMode aMode=c.getAcquisitionMode();
				ContrastMethod cMethod=c.getContrastMethod();
				Double ndf=c.getNDFilter();
				
				
				if(overwrite){
					if(c.getID()!=null && !c.getID().equals(""))
						channel.setID(c.getID());
					if(name!=null && !name.equals("")) channel.setName(name);
					if(color!=null) channel.setColor(color);
					if(fluor!=null && !fluor.equals("")) channel.setFluor(fluor);
					if(exW!=null) channel.setExcitationWavelength(exW);
					if(emW!=null) channel.setEmissionWavelength(emW);
					if(aMode!=null) channel.setAcquisitionMode(aMode);
					if(cMethod!=null) channel.setContrastMethod(cMethod);
					if(ndf!=null) channel.setNDFilter(ndf);
					
					LOGGER.info("[DATA] overwrite CHANNEL data");
				}else{
					if(channel.getID()==null || channel.getID().equals(""))
						channel.setID(c.getID());
					if(channel.getName()==null || channel.getName().equals(""))
						channel.setName(name);
					if(channel.getColor()==null)
						channel.setColor(color);
					if(channel.getFluor()==null || channel.getFluor().equals(""))
						channel.setFluor(fluor);
					if(channel.getExcitationWavelength()==null)
						channel.setExcitationWavelength(exW);
					if(channel.getEmissionWavelength()==null)
						channel.setEmissionWavelength(emW);
					if(channel.getAcquisitionMode()==null)
						channel.setAcquisitionMode(aMode);
					if(channel.getContrastMethod()==null)
						channel.setContrastMethod(cMethod);
					if(channel.getNDFilter()==null)
						channel.setNDFilter(ndf);
					
					LOGGER.info("[DATA] complete CHANNEL data");
				}
			}
		}else if(c!=null){
			channel=c;
			LOGGER.info("[DATA] add CHANNEL data");
		}
		
		setGUIData();
		
		return conflicts;
	}
	
	private void readGUIInput() 
	{
		if(channel==null)
			createNewElement();
		//TODO format check
		try{
			channel.setName(name.getTagValue());
		}catch(Exception e){
			LOGGER.severe("[DATA] can't read CHANNEL name input");
		}
		try{
			channel.setColor(parseColor(color.getTagValue()));
		}catch(Exception e){
			LOGGER.severe("[DATA] can't read CHANNEL color input");
		}
		try{
			channel.setFluor(fluorophore.getTagValue());
		}catch(Exception e){
			LOGGER.severe("[DATA] can't read CHANNEL fluorophore input");
		}
		try{
			channel.setIlluminationType(parseIllumType(illumType.getTagValue()));
		}catch(Exception e){
			LOGGER.severe("[DATA] can't read CHANNEL illumination type input");
		}
		//		TODO channel.setExpTime;
		try{
			channel.setExcitationWavelength(parseToLength(excitWavelength.getTagValue(), excitWavelengthUnit));
		}catch(Exception e){
			LOGGER.severe("[DATA] can't read CHANNEL excitation wavelength input");
		}
		try{
			channel.setEmissionWavelength(parseToLength(emissionWavelength.getTagValue(),emissionWavelengthUnit));
		}catch(Exception e){
			LOGGER.severe("[DATA] can't read CHANNEL emission wavelength input");
		}
		try{
			channel.setAcquisitionMode(parseAcqMode(imagingMode.getTagValue()));
		}catch(Exception e){
			LOGGER.severe("[DATA] can't read CHANNEL acquisition mode input");
		}

		//TODO: imagingmode und illuminationmode abspeichern

		try{
			channel.setContrastMethod(parseContrastMethod(contrastMethod.getTagValue()));
		}catch(Exception e){
			LOGGER.severe("[DATA] can't read CHANNEL contrast method input");
		}
		try{
			channel.setNDFilter(parseToDouble(ndFilter.getTagValue()));
		}catch(Exception e){
			LOGGER.severe("[DATA] can't read CHANNEL ndfilter input");
		}

		//TODO: new one?
		try{
		if(lightPath!=null) channel.setLightPath(lightPath.getData());
		}catch(Exception e){
			LOGGER.severe("[DATA] can't read CHANNEL lightPath input");
		}
	}

	public Channel getData() throws Exception
	{
		if(userInput())
			readGUIInput();
		return channel;
	}
	
	public static void mergeData(Channel in, Channel channelOME)
	{
		if(channelOME==null ){
			if(in==null){
				LOGGER.severe("failed to merge CHANNEL data");
			}else{
				channelOME=in;
			}
			return;
		}else if(in==null){
			LOGGER.info("nothing to merge CHANNEL data");
			return;
		}
		
		channelOME.setName(in.getName());
		channelOME.setColor(in.getColor());
		channelOME.setFluor(in.getFluor());
		channelOME.setIlluminationType(in.getIlluminationType());
		channelOME.setExcitationWavelength(in.getExcitationWavelength());
		channelOME.setEmissionWavelength(in.getEmissionWavelength());
		channelOME.setAcquisitionMode(in.getAcquisitionMode());
		channelOME.setContrastMethod(in.getContrastMethod());
		channelOME.setNDFilter(in.getNDFilter());
		
		channelOME.setDetectorSettings(in.getDetectorSettings());
		channelOME.setLightSourceSettings(in.getLightSourceSettings());
		channelOME.setLightPath(in.getLightPath());
	}
	
	
	private ContrastMethod parseContrastMethod(String c) throws EnumerationException
	{
		if(c.equals(""))
			return null;
		
		return ContrastMethod.fromString(c);
	}
	private Color parseColor(String c)
	{
		if(c.equals(""))
			return null;
		
		return new Color(Integer.valueOf(c));
	}
	private IlluminationType parseIllumType(String c) throws EnumerationException 
	{
		if(c.equals(""))
			return null;
		
		return IlluminationType.fromString(c);
	}
	
	private AcquisitionMode parseAcqMode(String c) throws EnumerationException {
		if(c.equals(""))
			return null;
		
		return AcquisitionMode.fromString(c);
	}
	
	
	public void buildComponents() 
	{
		labels.clear();
		comp.clear();
		
		addTagToGUI(name);
		addTagToGUI(color);
		addTagToGUI(fluorophore);
		addTagToGUI(illumType);
		addTagToGUI(exposureTime);
		if(exposureTime!=null)exposureTime.setEnable(false);
		addTagToGUI(excitWavelength);
		addTagToGUI(emissionWavelength);
//		addTag(acquisitionMode);
		addTagToGUI(imagingMode);
		addTagToGUI(illuminationMode);
		if(illuminationMode!=null)illuminationMode.setEnable(false);
		
		addTagToGUI(contrastMethod);
		addTagToGUI(ndFilter);
		
		addLabelTextRows(labels, comp, gridbag, globalPane);
		
		c.gridwidth = GridBagConstraints.REMAINDER; //last
		c.anchor = GridBagConstraints.WEST;
		c.weightx = 1.0;
		
		buildComp=true;
		initTagList();
		setFields=false;
	}

	public void buildExtendedComponents() 
	{
		
	}

	public void createDummyPane(boolean inactive)
	{
		setName(null,OPTIONAL);
		setColor(null, OPTIONAL);
		setFluorophore(null, OPTIONAL);
		setIllumType(null, OPTIONAL);
		setExposureTime(null, OPTIONAL);
		setExcitWavelength(null, OPTIONAL);
		setEmissionWavelength(null, OPTIONAL);
//		setAcquisitionMode(null, OPTIONAL);
		setImagingMode(null, OPTIONAL);
		setIlluminationMode(null, OPTIONAL);
		setContrastMethod(null, OPTIONAL);
		setNDFilter(null, OPTIONAL);
		
		if(inactive){
			name.setEnable(false);
			color.setEnable(false);
			fluorophore.setEnable(false);
			illumType.setEnable(false);
			exposureTime.setEnable(false);
			excitWavelength.setEnable(false);
			emissionWavelength.setEnable(false);
			//		acquisitionMode.setInactiv();
			imagingMode.setEnable(false);
			illuminationMode.setEnable(false);
			contrastMethod.setEnable(false);
			ndFilter.setEnable(false);
		}
	}
	
	public void createDummyPane(List<TagConfiguration> list,boolean inactive) 
	{
		if(list==null)
			createDummyPane(inactive);
		else{
		clearDataValues();
//		if(channel==null && list!=null && list.size()>0)
//			createNewElement();
		for(int i=0; i<list.size();i++){
			TagConfiguration t=list.get(i);
			String name=t.getName();
			String val=t.getValue();
			boolean prop=t.getProperty()!= null ? Boolean.parseBoolean(t.getProperty()):
				OPTIONAL;
			if(name!=null){
				switch (name) {
				case TagNames.CH_NAME:
					try{
						setName(val,prop);
//						channel.setName(val);
					}catch(Exception e){
						setName(null,prop);
					}
					this.name.setVisible(true);
					break;
				case TagNames.COLOR:
					try{
						Color value=parseColor(val);
						setColor(value, prop);
//						channel.setColor(value);
					}catch(Exception e){
						setColor(null,prop);
					}
					color.setVisible(true);
					break;
				case TagNames.FLUOROPHORE:
					try{
						setFluorophore(val, prop);
//						channel.setFluor(val);
					}catch(Exception e){
						setFluorophore(null, prop);
					}
					fluorophore.setVisible(true);
					break;
				case TagNames.IILUMTYPE:
					try{
						IlluminationType value=parseIllumType(val);
						setIllumType(value, prop);
//						channel.setIlluminationType(value);
					}catch(Exception e){
						setIllumType(null, prop);
					}
					illumType.setVisible(true);
					break;
				case TagNames.EXPOSURETIME:
					try{
						setExposureTime(val, prop);
						//TODO: channel.set;
					}catch(Exception e){
						setExposureTime(null, prop);
					}
					exposureTime.setVisible(true);
					break;
				case TagNames.EXCITWAVELENGTH:
					try{
						Length value=parseToLength(val, excitWavelengthUnit);
						setExcitWavelength(value, prop);
//						channel.setExcitationWavelength(value);
					}catch(Exception e){
						setExcitWavelength(null, prop);
					}
					excitWavelength.setVisible(true);
					break;
				case TagNames.EMMISIONWAVELENGTH:
					try{
						Length value=parseToLength(val, emissionWavelengthUnit);
						setEmissionWavelength(value, prop);
//						channel.setEmissionWavelength(value);
					}catch(Exception e){
						setEmissionWavelength(null, prop);
					}
					emissionWavelength.setVisible(true);
					break;
				case TagNames.IMAGINGMODE:
					try{
						AcquisitionMode value=parseAcqMode(val);
						setImagingMode(value, prop);
//						TODO: channel.set
					}catch(Exception e){
						setImagingMode(null, prop);
					}
					imagingMode.setVisible(true);
					break;
				case TagNames.ILLUMINATIONMODE:
					try{
						AcquisitionMode value=parseAcqMode(val);
						setIlluminationMode(value, prop);
//						TODO: channel.set
					}catch(Exception e){
						setIlluminationMode(null, prop);
					}
					illuminationMode.setVisible(true);
					break;
				case TagNames.CONTRASTMETHOD:
					try{
						ContrastMethod value=parseContrastMethod(val);
						setContrastMethod(value, prop);
//						channel.setContrastMethod(value);
					}catch(Exception e){
						setContrastMethod(null, prop);
					}
					contrastMethod.setVisible(true);
					break;
				case TagNames.NDFILTER:
					try{
						Double value=parseToDouble(val);
						setNDFilter(value, prop);
//						channel.setNDFilter(value);
					}catch(Exception e){
						setNDFilter(null, prop);
					}
					ndFilter.setVisible(true);
					break;
				default:
					LOGGER.warning("[CONF] unknown tag: "+name );break;
				}
			}
		}
		}
	}
	
	

	public void clearDataValues() 
	{
		clearTagValue(name);
		clearTagValue(color);
		clearTagValue(fluorophore);
		clearTagValue(illumType);
		clearTagValue(exposureTime);
		clearTagValue(excitWavelength);
		clearTagValue(emissionWavelength);
//		clearTagValue(acquisitionMode);
		clearTagValue(imagingMode);
		clearTagValue(illuminationMode);
		clearTagValue(contrastMethod);
		clearTagValue(ndFilter);
		
		if(lightPath!=null) lightPath.clearDataValues();
		lightPathOrdered=false;
	}
	
	public JTree getTagDataCheckBoxes()
	{
		DefaultMutableTreeNode root=new DefaultMutableTreeNode("Channel");
		JTree chTree = new JTree();
		chTree.setEditable(true);
		chTree.setRootVisible(false);
		chTree.setCellRenderer(new CheckBoxNodeRendererTagData());
		chTree.setCellEditor(new CheckBoxNodeEditor());
		// add elem
		root.add(new DefaultMutableTreeNode( 
				new CheckBoxNodeTagData(name, true)));
		root.add(new DefaultMutableTreeNode(
				new CheckBoxNodeTagData(color, true)));
		root.add(new DefaultMutableTreeNode(
				new CheckBoxNodeTagData(fluorophore, true)));
		 
		chTree.setModel(new DefaultTreeModel(root));
		return null;
	}
	
	public void setName(String value, boolean prop)
	{
		if(name == null) 
			name = new TagData(TagNames.CH_NAME+": ",value,prop,TagData.TEXTFIELD);
		else 
			name.setTagValue(value,prop);
	}
	public String getName()
	{
		return name!=null ? name.getTagValue():null;
	}
	
	public void setColor(Color value, boolean prop)
	{
		String val= (value != null) ? value.toString():"";
		if(color == null) 
			color = new TagData(TagNames.COLOR+": ",val,prop,TagData.TEXTFIELD);
		else 
			color.setTagValue(val,prop);
	}
	public void setFluorophore(String value, boolean prop)
	{
		if(fluorophore == null) 
			fluorophore = new TagData(TagNames.FLUOROPHORE+": ",value,prop,TagData.TEXTFIELD);
		else 
			fluorophore.setTagValue(value,prop);
	}
	public void setIllumType(IlluminationType value, boolean prop)
	{
		String val= (value != null) ? String.valueOf(value):"";
		if(illumType == null) 
			illumType = new TagData(TagNames.IILUMTYPE+": ",val,prop,TagData.COMBOBOX,getNames(IlluminationType.class));
		else 
			illumType.setTagValue(val,prop);
	}
	public void setExposureTime(String value, boolean prop)
	{
		if(exposureTime == null) 
			exposureTime = new TagData(TagNames.EXPOSURETIME+": ",value,prop,TagData.TEXTFIELD);
		else 
			exposureTime.setTagValue(value,prop);
	}
	public void setExcitWavelength(Length value, boolean prop)
	{
		String val=(value!=null) ? String.valueOf(value.value()) :"";
		excitWavelengthUnit= (value!=null) ? value.unit() : excitWavelengthUnit;
		if(excitWavelength == null) 
			excitWavelength = new TagData(TagNames.EXCITWAVELENGTH+" ["+excitWavelengthUnit.getSymbol()
					+"]: ",val,prop,TagData.TEXTFIELD);
		else 
			excitWavelength.setTagValue(val,prop);
	}
	public void setEmissionWavelength(Length value, boolean prop)
	{
		String val=(value!=null) ? String.valueOf(value.value()) :"";
		emissionWavelengthUnit=(value!=null) ? value.unit() :emissionWavelengthUnit;
		if(emissionWavelength == null) 
			emissionWavelength = new TagData(TagNames.EMMISIONWAVELENGTH+" ["+emissionWavelengthUnit.getSymbol()+"]: ",val,prop,TagData.TEXTFIELD);
		else 
			emissionWavelength.setTagValue(val,prop);
	}
//	public void setAcquisitionMode(AcquisitionMode value, boolean prop)
//	{
//		String val= (value != null) ? String.valueOf(value):"";
//		if(acquisitionMode == null) 
//			acquisitionMode = new TagData("Acquisition Mode: ",val,prop,COMBOBOX,getNames(AcquisitionMode.class));
//		else 
//			acquisitionMode.setTagValue(val,prop);
//	}
	public void setImagingMode(AcquisitionMode value, boolean prop)
	{
		String val= (value != null) ? String.valueOf(value):"";
		if(imagingMode == null) 
			imagingMode = new TagData(TagNames.IMAGINGMODE+": ",val,prop,TagData.COMBOBOX,getNames(AcquisitionMode.class));
		else 
			imagingMode.setTagValue(val,prop);
	}
	public void setIlluminationMode(AcquisitionMode value, boolean prop)
	{
		String val= (value != null) ? String.valueOf(value):"";
		if(illuminationMode == null) 
			illuminationMode = new TagData(TagNames.ILLUMINATIONMODE+": ",val,prop,TagData.COMBOBOX,getNames(AcquisitionMode.class));
		else 
			illuminationMode.setTagValue(val,prop);
	}
	public void setContrastMethod(ContrastMethod value, boolean prop)
	{
		String val= (value != null) ? String.valueOf(value):"";
		if(contrastMethod == null) 
			contrastMethod = new TagData(TagNames.CONTRASTMETHOD+": ",val,prop,TagData.COMBOBOX,getNames(ContrastMethod.class));
		else 
			contrastMethod.setTagValue(val,prop);
	}
	public void setNDFilter(Double value, boolean prop)
	{
		String val= (value != null) ? String.valueOf(value):"";
		if(ndFilter == null) 
			ndFilter = new TagData(TagNames.NDFILTER+": ",val,prop,TagData.TEXTFIELD);
		else 
			ndFilter.setTagValue(val,prop);
	}
	
	public LightPathCompUI getLightPath()
	{
		return lightPath;
	}
	
	public void setLightPath(LightPathCompUI lp)
	{
		lightPath=lp;
		
	}
	
	
	
	
	
	

	@Override
	public List<TagData> getActiveTags() 
	{
		List<TagData> list = new ArrayList<TagData>();
		if(isActive(name)) list.add(name);
		if(isActive(color)) list.add(color);
		if(isActive(fluorophore)) list.add(fluorophore);
		if(isActive(illumType)) list.add(illumType);
		if(isActive(exposureTime)) list.add(exposureTime);
		if(isActive(excitWavelength)) list.add(excitWavelength);
		if(isActive(emissionWavelength)) list.add(emissionWavelength);
		if(isActive(imagingMode)) list.add(imagingMode);
		if(isActive(illuminationMode)) list.add(illuminationMode);
		if(isActive(contrastMethod)) list.add(contrastMethod);
		if(isActive(ndFilter)) list.add(ndFilter);
		
		return list;
	}

	public void setFieldsExtern(boolean b) {
		setFields= setFields || b;		
	}

	

	

}
