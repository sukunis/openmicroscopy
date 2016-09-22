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
import ome.xml.model.enums.FilterType;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules.LightPathEditor;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules.LightPathTableSmall;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.model.LightPathModel;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.ModuleConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagConfiguration;
import org.slf4j.LoggerFactory;

public class LightPathViewer extends ModuleViewer{

	private static final org.slf4j.Logger LOGGER =
			LoggerFactory.getLogger(LightPathViewer.class);

	private LightPathModel data;
	private LightPathTableSmall lightPathTable;
	private boolean useEditor;

	// available element tags



	//available element setting tags


	/**
	 * Creates a new instance.
	 * @param model Reference to model.
	 */
	public LightPathViewer(LightPathModel model,ModuleConfiguration conf)
	{
		this.data=model;
		initComponents(conf);
		buildGUI();
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

		JButton editBtn=new JButton("Edit");
		editBtn.setAlignmentX(Component.RIGHT_ALIGNMENT);

		editBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				LightPathEditor creator = new LightPathEditor(new JFrame(),"Edit LightPath",
						data.getList(),data.getLightPath());
				useEditor=true;
				List<Object> newList=creator.getLightPathList(); 
				if(newList!=null && !newList.isEmpty()){
					data.createLightPath(newList);
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
		if(data==null)
			return;

		LightPath lightPath=data.getLightPath();
		if(lightPath!=null){
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



	@Override
	public void saveData() 
	{
		List<Object> lightPathList=lightPathTable.getLightPathList();

		if(data==null)
			data=new LightPathModel();
		data.createLightPath(lightPathList);
		dataChanged=false;
	}



	@Override
	protected void initTag(TagConfiguration t) {
		// TODO Auto-generated method stub

	}




}


