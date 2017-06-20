package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

import ome.xml.model.Dichroic;
import ome.xml.model.Filter;
import ome.xml.model.LightPath;
import ome.xml.model.enums.EnumerationException;
import ome.xml.model.enums.FilterType;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.MetaDataModel;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model.LightPathModel;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.ModuleConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.TagNames;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.util.LightPathElement;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagConfiguration;
import org.slf4j.LoggerFactory;

/**
 * Works for xsi:schemaLocation="http://www.openmicroscopy.org/Schemas/OME/2015-01 
 * @author Kunis
 *
 */
public class LightPathViewer extends ModuleViewer{

	private static final org.slf4j.Logger LOGGER =
			LoggerFactory.getLogger(LightPathViewer.class);

	private LightPathModel data;
	private LightPathTableSmall lightPathTable;
	private boolean useEditor;

	private int index;

	static final String EXITATION="Excitation Filter";
	static final String EMISSION="Emission Filter";
	static final String DICHROIC="Dichroic";
	
	private List<Object> availableElems;

	/**
	 * Creates a new instance.
	 * @param model Reference to model.
	 */
	public LightPathViewer(LightPathModel model,ModuleConfiguration conf,int index,
			List<Object> availableElems)
	{
		System.out.println("# LightPathViewer::newInstance("+(model!=null?"model":"null")+") "+index);
		this.data=model;
		this.index=index;
		this.availableElems=availableElems;
		initComponents(conf);
		buildGUI();
		showPreDefinitions(conf);
	}
	
	public void showPreDefinitions(ModuleConfiguration conf) 
	{
		List<LightPathElement> list=conf.getElementList();
		if(list==null)
			return;
		
		if(lightPathTable.getRowCount()==0){
			for(LightPathElement t:list){
				lightPathTable.appendElem(parseObject(t), t.getClazz());
			}
		}
		
	}

	private Object parseObject(LightPathElement t) 
	{
		if(t.getClazz().equals(DICHROIC)){
			Dichroic d=new Dichroic();
			d.setModel(t.getProperty(TagNames.MODEL));
			d.setManufacturer(t.getProperty(TagNames.MANUFAC));
			return d;
		}else{
			Filter f= new Filter();
			f.setModel(t.getProperty(TagNames.MODEL));
			f.setManufacturer(t.getProperty(TagNames.MANUFAC));
			f.setType(parseFilterType(t.getProperty(TagNames.LP_TYPE)));
			f.setFilterWheel(t.getProperty(TagNames.FILTERWHEEL));
			return f;
		}
	}
	
	private FilterType parseFilterType(String c)
	{
		if(c==null || c.equals(""))
			return null;

		FilterType m=null;
		try{
			m=FilterType.fromString(c);
		}catch(EnumerationException e){
			LOGGER.warn("FilterType: "+c+" is not supported");
		}
		return m;
	}


	/**
	 * Builds and lay out GUI.
	 */
	private void buildGUI() 
	{
		// set data
		setGUIData();
		dataChanged=false;
	}

	/**
	 * Initialize components.
	 */
	private void initComponents(ModuleConfiguration conf) 
	{
		lightPathTable=new LightPathTableSmall();
		setLayout(new BorderLayout(5,5));

		JButton editBtn=new JButton("Choose...");
		editBtn.setAlignmentX(Component.RIGHT_ALIGNMENT);

		editBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				if(data==null)
					data=new LightPathModel();
				
				saveData();
				LightPathEditor creator = new LightPathEditor(new JFrame(),"Edit LightPath",
						availableElems,data.getLightPath(index));
				useEditor=true;
				List<Object> newList=creator.getLightPathList(); 
				if(newList!=null && !newList.isEmpty()){
					try {
						createLightPath(newList);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					//				createLightPath(model.updateLightPathElems(newList,chIdx));
					setGUIData();
					dataChanged=true;
					revalidate();
					repaint();
				}
			}
		});

		JScrollPane scrollPane = new JScrollPane();

		lightPathTable = new LightPathTableSmall();
		scrollPane.setViewportView(lightPathTable);
		scrollPane.setPreferredSize(new Dimension(this.getSize().width-5,50));

		add(editBtn,BorderLayout.SOUTH);
		add(scrollPane,BorderLayout.CENTER);
		setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		revalidate();
	}


	/**
	 * Show data of objective
	 */
	private void setGUIData() 
	{
		if(data==null || data.getNumberOfLightPaths()==0)
			return;

		LightPath lightPath=data.getLightPath(index);
		if(lightPath!=null){
			lightPathTable.clearData();
			//load primary dichroic of instrument
			Dichroic d=lightPath.getLinkedDichroic();
			List<Filter> emList=lightPath.copyLinkedEmissionFilterList();
			List<Filter> exList=lightPath.copyLinkedExcitationFilterList();

			if(exList!=null){
				for(Filter f:exList){
					lightPathTable.appendElem(f,EXITATION);
				}
			}else{
				LOGGER.info("can't load EX Filter element");
			}

			if(d!=null){
				lightPathTable.appendElem(d,DICHROIC);
			}else{
				LOGGER.info("No dichroic element is given");
			}

			if(emList!=null)
			{
				for(Filter f:emList)
				{
					String type="";
					try {
						type=f.getType().getValue();
					} catch (Exception e) {
					}
					String elemType=EMISSION;
					if(type.equals(FilterType.DICHROIC.toString()))					{
						elemType=DICHROIC;
					}
					lightPathTable.appendElem(f,elemType);
				}
			}else{
				LOGGER.info("::ATTENTION:: can't load EM Filter element ");
			}
		}
	}



	@Override
	public void saveData()  
	{
		System.out.println("# LightPathViewer::saveData()");
		List<Object> lightPathList=lightPathTable.getLightPathList();

		if(data==null)
			data=new LightPathModel();
		try {
			createLightPath(lightPathList);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dataChanged=false;
	}



	@Override
	protected void initTag(TagConfiguration t) {
		// TODO Auto-generated method stub

	}
	@Override
	protected void setPredefinedTag(TagConfiguration t) 
	{
		//no predefinitions possible
	}

	/**
	 * LightPath order is Exitation Filter -> Dichroic -> Dichroic/Emission filter
	 * @param list
	 * @throws Exception 
	 */
	public void createLightPath(List<Object> list) throws Exception
	{
		if(list!=null && !list.isEmpty()){
			LightPath newElement=new LightPath();
			int linkType=1;
			for(Object f : list)
			{
				Dichroic pD=newElement.getLinkedDichroic();
				boolean primDNotExists= pD==null ? true : false ;

				// Dichroic
				if(f instanceof Dichroic){
					linkType=2;
					// primary dichroic exists?
					if(primDNotExists){
						newElement.linkDichroic((Dichroic) f);
					}else{
						LOGGER.warn("primary Dichroic still exists! [LightPathViewer::createLightPath]");
						newElement.linkEmissionFilter(MetaDataModel.convertDichroicToFilter((Dichroic)f));
					}

				}else{

					String	type= ((Filter) f).getType()!=null ? ((Filter) f).getType().toString() : "";
					//filters that comes before and dichroic are exitation filters by definition
					if(	!type.equals(FilterType.DICHROIC.getValue()) && 
							linkType==1){
						newElement.linkExcitationFilter((Filter) f);
					}else{// link additional dichroic as emission filter
						linkType=2;

						if( primDNotExists){
							newElement.linkDichroic(MetaDataModel.convertFilterToDichroic((Filter) f));
						}else{
							newElement.linkEmissionFilter((Filter) f);
						}
					}
				}
			}
			data.addData(newElement, true, index);
		}
	}



	public int getIndex() {
		return index;
	}


}


