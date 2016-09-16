package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JLayer;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.plaf.LayerUI;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules.ChannelCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules.DetectorCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules.ElementsCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules.ExperimentCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules.ImageCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules.LightPathCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules.LightSourceCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules.ObjectiveCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules.SampleCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.ModuleConfiguration;

public class MetaDataControl implements PropertyChangeListener
{
	private MetaDataModel model;
	
	public MetaDataControl(MetaDataModel model) 
	{
		if (model == null) throw new NullPointerException("No model.");
		this.model = model;
	}

	public JPanel showData()
	{
		JPanel pane=new JPanel();
		
		try {
//			if(model.initObjective())showObjectiveData();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return pane;
	}
	
//	private void showObjectiveData() throws Exception
//	{
//		ObjectiveCompUI o=model.getObjectiveModul();
//		if(o.getData()==null){
//			o.showOptionPane();
//		}else{
//			o.buildComponents();
//		}
//	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) 
	{
		 String name = evt.getPropertyName();
//		 if(ElementsCompUI.ADD_NEW_LIGHTSRC.equals(name)){
//			 model.createNewLightSrc((String)evt.getNewValue());
//		 }
	}

	public JPanel activateExperimentModulView() 
	{
		ExperimentCompUI e=model.getExpModul();
		e.buildComponents();
		// TODO Auto-generated method stub
		return e;
	}
	
	public SampleCompUI activateSampleModuleView() 
	{
		SampleCompUI s=model.getSampleModul();
		s.buildComponents();
		return s;
	}
	
	public Component activateImageModul() 
	{
		ImageCompUI i=model.getImageModul();
		i.buildComponents();
		return i;
	}
	
	public Component activateObjectiveModulView(String name) 
	{
		
		ObjectiveCompUI o=model.getObjectiveModul();
		o.buildComponents();
		
//		name=name.equals("image") ? "":name;
		
		return createPropPane(o, "Objective", "for image "+name);
	}

	public JPanel activateLightPathModul(int i, String name) 
	{
		LightPathCompUI d=null;
		if(i< model.getNumberOfLightPath()){
			d=model.getLightPathModul(i); 
			
		}else{
			d=new LightPathCompUI();
			model.addLightPath(d);
//			d.showOptionPane();
		}
		d.buildComponents();
		name=name.equals("Channel") ? "":name;
		return createPropPane(d, "LightPath", "for Channel "+name);
	}
	
	public JPanel activateDetectorModulView(int i,String name,ModuleConfiguration conf) 
	{
		DetectorCompUI d=null;
		if(i< model.getNumberOfDetectors()){
			d=model.getDetectorModul(i);
			
		}else{
			d=new DetectorCompUI(conf);
			model.addDetectorData(d);
//			d.showOptionPane();
		}
		d.buildComponents();
		name=name.equals("Channel") ? "":name;
		return createPropPane(d, "Detector", "for Channel "+name);
	}
	
	public Component activateLightSrcModulView(int i, String name,ModuleConfiguration conf) 
	{
		LightSourceCompUI l=null;
		if(i<model.getNumberOfLightSrc()){
			l=model.getLightSourceModul(i);
			
		}else{
			System.out.println("# MetaDataControl::activateLightSrcModul() \n"
					+ "Channel: "+i+" : no lightSrc available, create new");
			l=new LightSourceCompUI(conf);
			model.addLightSrcModul(l);
		}
		l.buildComponents();
		name=name.equals("Channel") ? "":name;
		return createPropPane(l, "LightSource", "for Channel "+name);
	}
	
	public Component activateChannelModulView(int i) 
	{
		ChannelCompUI c=model.getChannelModul(i);
		c.buildComponents();
		return c;
	}
	
	
	
	
	/**
	 * Create pane labeled tabbedPane with component main on top and sub on bottom
	 * @param main element on top
	 * @param sub element bottom
	 * @param name tabName
	 * @param labelText label beside to tabName
	 * @return
	 */
	protected JPanel createPropPane(ElementsCompUI main, ElementsCompUI sub, String name,String labelText)
	{
		JTabbedPane lTab=new JTabbedPane();
		JPanel lPanel=new JPanel();
		lPanel.setLayout(new BoxLayout(lPanel, BoxLayout.PAGE_AXIS));
		
		lPanel.add(main);
		lPanel.add(sub);
		lTab.add(name,lPanel);
		return buildTabbedPaneWithLabel(lTab,labelText);
	}
	
	protected JPanel createPropPane(ElementsCompUI main, String name,String labelText)
	{
		JTabbedPane lTab=new JTabbedPane();
		JPanel lPanel=new JPanel();
		lPanel.setLayout(new BoxLayout(lPanel, BoxLayout.PAGE_AXIS));
		
		lPanel.add(main);
		lTab.add(name,lPanel);
		return buildTabbedPaneWithLabel(lTab,labelText);
	}
	
	protected JPanel buildTabbedPaneWithLabel(JTabbedPane tab, String label)
	{
		JPanel panel = new JPanel(new BorderLayout());
		TopRightCornerLabelLayerUI labelUI=new TopRightCornerLabelLayerUI(label);
	    panel.add(new JLayer<JComponent>(tab,labelUI ));
		
		return panel;
	}
	
	class TopRightCornerLabelLayerUI extends LayerUI<JComponent> {
		  private JLabel l = new JLabel("A Label at right corner");
		
		  private JPanel rubberStamp = new JPanel();
		  
		  public TopRightCornerLabelLayerUI(String labelText) 
		  {
			  l.setText(labelText);
		  }
		  @Override public void paint(Graphics g, JComponent c) {
		    super.paint(g, c);
		    Dimension d = l.getPreferredSize();
		    int x = c.getWidth() - d.width - 5;
		    SwingUtilities.paintComponent(g, l, rubberStamp, x, 2, d.width, d.height);
		  }
	}

	

	
	

	

	

	

}
