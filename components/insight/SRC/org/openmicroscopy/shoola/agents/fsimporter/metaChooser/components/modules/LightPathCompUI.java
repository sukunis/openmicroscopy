package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import ome.xml.model.Dichroic;
import ome.xml.model.Filter;
import ome.xml.model.LightPath;
import ome.xml.model.enums.FilterType;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.MetaDataModel;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagData;
import org.slf4j.LoggerFactory;

public class LightPathCompUI extends ElementsCompUI 
{
	/** Logger for this class. */
//    private static Logger LOGGER = Logger.getLogger(UOSMetadataLogger.class.getName());
	private static final org.slf4j.Logger LOGGER =
    	    LoggerFactory.getLogger(LightPathCompUI.class);
	
	private LightPath lightPath;
	private int chIdx;
	private JButton editBtn;
	
	private LightPathTableSmall lightPathTable;
	private List<Object> availableFilterList;
	
	private boolean useEditor;
	
	public LightPathCompUI(LightPath _lightPath, int _chIdx, MetaDataModel _model) 
	{
		lightPath=_lightPath;
		chIdx=_chIdx;
		
		initGUI();
		if(lightPath!=null)
			setGUIData();
		
	}
	
	public LightPathCompUI() 
	{
		initGUI();
		createDummyPane(false);
	}
	public boolean userInput(){
		return useEditor;
	}
	
	
	private void initGUI()
	{
		lightPathTable=new LightPathTableSmall();
		setLayout(new BorderLayout(5,5));
		buildComp=false;
		
//		GridLayout grid=new GridLayout(0,2);
//		globalPane=new JPanel(new BorderLayout(5,5));
//		globalPane.setLayout(grid);
		
		editBtn=new JButton("Edit");
		editBtn.setAlignmentX(Component.RIGHT_ALIGNMENT);
		
		editBtn.setEnabled(true);
		editBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				LightPathEditor creator = new LightPathEditor(new JFrame(),"Edit LightPath",
						availableFilterList,lightPath);
				useEditor=true;
				List<Object> newList=creator.getLightPathList(); 
				if(newList!=null && !newList.isEmpty()){
					createLightPath(newList);
//					createLightPath(model.updateLightPathElems(newList,chIdx));
					setGUIData();
					buildComponents();   
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
		setBorder(
//				BorderFactory.createCompoundBorder(	new MyTitledBorder("Objective"),
						BorderFactory.createEmptyBorder(10,10,10,10));
		revalidate();
		
		
	}
	
	public void addData(LightPath lp,boolean overwrite)
	{
		if(lightPath!=null){
			if(lp!=null){
				if(overwrite){
					LOGGER.info("[DATA] overwrite LIGHTPATH data");
				}else{
					LOGGER.info("[DATA] complete LIGHTPATH data");
				}
			}
		}else if(lp!=null){
			lightPath=lp;
			LOGGER.info("[DATA] add LIGHTPATH data");
		}
		setGUIData();
	}
	
	public void setGUIData()
	{
		if(lightPath!=null){
			editBtn.setEnabled(true);
			lightPathTable.clearData();
			//load primary dichroic of instrument
			Dichroic d=lightPath.getLinkedDichroic();
			List<Filter> emList=lightPath.copyLinkedEmissionFilterList();
			List<Filter> exList=lightPath.copyLinkedExcitationFilterList();

			if(exList!=null){
				for(Filter f:exList){
					lightPathTable.appendElem(f,LightPathElem.EXITATION);
				}
			}else{
				LOGGER.info("can't load EX Filter element");
			}

			if(d!=null){
				lightPathTable.appendElem(d,LightPathElem.DICHROIC);
			}else{
				LOGGER.info("::ATTENTION:: can't load Dichroic element");
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
					String elemType=LightPathElem.EMISSION;
					if(type.equals(FilterType.DICHROIC.toString()))					{
						elemType=LightPathElem.DICHROIC;
					}
					lightPathTable.appendElem(f,elemType);
				}
			}else{
				LOGGER.info("::ATTENTION:: can't load EM Filter element ");
			}
		}
	}
	
	private void readGUIInput() throws Exception
	{
		List<Object> lightPathList=lightPathTable.getLightPathList();
		
		createLightPath(lightPathList);
	}
	
	//TODO: optimize
	/**
	 * LightPath order is Exitation Filter -> Dichroic -> Dichroic/Emission filter
	 * @param list
	 */
	private void createLightPath(List<Object> list)
	{
		if(list!=null && !list.isEmpty()){
			lightPath=new LightPath();
			int linkType=1;
			for(Object f : list)
			{
				Dichroic pD=lightPath.getLinkedDichroic();
				boolean primDNotExists= pD==null ? true : false ;

				// Dichroic
				if(f instanceof Dichroic){
					linkType=2;
					// primary dichroic exists?
					if(primDNotExists){
						lightPath.linkDichroic((Dichroic) f);
					}else{
						LOGGER.warn("primary Dichroic still exists! [LightPathCompUI::createLightPath]");
						lightPath.linkEmissionFilter(MetaDataModel.convertDichroicToFilter((Dichroic)f));
					}

				}else{

					String	type= ((Filter) f).getType()!=null ? ((Filter) f).getType().toString() : "";
					//filters that comes before and dichroic are exitation filters by definition
					if(	!type.equals(FilterType.DICHROIC.getValue()) && 
							linkType==1){
						lightPath.linkExcitationFilter((Filter) f);
					}else{// link additional dichroic as emission filter
						linkType=2;

						if( primDNotExists){
							lightPath.linkDichroic(MetaDataModel.convertFilterToDichroic((Filter) f));
						}else{
							lightPath.linkEmissionFilter((Filter) f);
						}
					}
				}
			}
		}
	}

	@Override
	public void buildComponents() 
	{
//		if(lightPathTable!=null){
//		JScrollPane scrollPane = new JScrollPane(lightPathTable,
//				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
//				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
//		// to avoid jump when resize the window
////		scrollPane.setPreferredSize(new Dimension(1,1));
////		scrollPane.setPreferredSize(lightPathTable.getSize());
//		scrollPane.setViewportBorder(new EmptyBorder(5, 5, 5, 5));
//		
//		globalPane.add(lightPathTable,BorderLayout.NORTH);
//		buildComp=true;
//		}
		useEditor=false;
		revalidate();
		repaint();
	}

	

	@Override
	public void createDummyPane(boolean inactive) 
	{
//		JScrollPane scrollPane = new JScrollPane(lightPathTable,
//				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
//				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
//		// to avoid jump when resize the window
////		scrollPane.setPreferredSize(new Dimension(1,1));
////		scrollPane.setPreferredSize(lightPathTable.getSize());
//		scrollPane.setViewportBorder(new EmptyBorder(5, 5, 5, 5));
//		globalPane.add(lightPathTable,BorderLayout.NORTH);
	}

	@Override
	public void clearDataValues() 
	{
		lightPathTable.clearData();
		availableFilterList=null;
	}

	@Override
	public List<TagData> getActiveTags() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	/**
	 * 
	 * @return ome.xml.model::LightPath
	 */
	public LightPath getLightPath()
	{
		return lightPath;
	}
	
	public void setLightPath(LightPath l)
	{
		lightPath=l;
	}
	
	/**
	 * Update data from input and return ome.xml.model::LightPath
	 * @return
	 * @throws Exception
	 */
	public LightPath getData() throws Exception
	{
		if(userInput())
			readGUIInput();
		
		
		return lightPath;
	}

	public void clearList() {
		availableFilterList=null;
	}

	
	public void addFilterToList(List<Filter> list)
	{
		if(list==null || list.size()==0)
			return;
		
		if(availableFilterList==null){

			availableFilterList=new ArrayList<Object>();
		}
		for(int i=0; i<list.size(); i++){
			availableFilterList.add(list.get(i));
		}
	}
	
	public void addDichroicToList(List<Dichroic> list)
	{
		if(list==null || list.size()==0)
			return;
		
		if(availableFilterList==null){

			availableFilterList=new ArrayList<Object>();
		}
		for(int i=0; i<list.size(); i++){
			availableFilterList.add(list.get(i));
		}
	}

	public void setFieldsExtern(boolean b) 
	{
		useEditor = useEditor || b;
	}

	@Override
	public void update(List<TagData> list) {
		// TODO Auto-generated method stub
		
	}
	
	
}
