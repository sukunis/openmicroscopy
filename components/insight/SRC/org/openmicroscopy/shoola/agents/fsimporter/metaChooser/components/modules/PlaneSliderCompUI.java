package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules.ElementsCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.modules.PlaneCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagData;

import loci.formats.meta.IMetadata;

public class PlaneSliderCompUI extends ElementsCompUI
{
	private JSlider sliderT;
	private JSlider sliderZ;
	private JSlider sliderC;
	private JSlider sliderDeltaT;
	
	private CardLayout cl;
	private JPanel cardP;
	
	private int numT,numZ,numC;
	
	private JTextField posT;
	
	
	public PlaneSliderCompUI(List<ElementsCompUI> plist,int t,int z,int c)
	{
		setLayout(new BorderLayout());
		if(plist==null){
			List<ElementsCompUI> list=new ArrayList<ElementsCompUI>();
			PlaneCompUI plane=new PlaneCompUI(null);
			plane.createDummyPane(true);
			list.add(plane);
			createGUI(list,1,1,1);
		}else{
			createGUI(plist,t,z,c);
		}
		revalidate();
		repaint();
	}
	
	private void createGUI(List<ElementsCompUI> list,int t,int z,int c)
	{
		numT=t;
		numZ=z;
		numC=c;
		
		
		JPanel editT=new JPanel();
		editT.setLayout(new BoxLayout(editT,BoxLayout.X_AXIS));
		posT = new JTextField("");
		JButton minusTBtn= new JButton("<");
		JButton plusTBtn= new JButton(">");
		
		
		JLabel labelT=new JLabel("T: ");
		int maxTickSpace= t < 10 ? 2 : (t < 20 ? 5 : 10);
		if(t>0){
			sliderT = new JSlider( 0, t-1, 0 );
		}else{
			sliderT = new JSlider( 0, 0, 0 );
		}
		sliderT.setPaintTicks( true );
		sliderT.setMajorTickSpacing( 10 );
		sliderT.setMinorTickSpacing( 1 );
		//		sliderT.setPaintTrack( false );
		sliderT.createStandardLabels(1);
		sliderT.setPaintLabels(true);
		sliderT.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int cardID=(numZ*numC)*(sliderT.getValue())+numC*(sliderZ.getValue())+sliderC.getValue();
				cl.show(cardP,String.valueOf(cardID));
				posT.setText(String.valueOf(numT));
			}
		});
		labelT.setLabelFor(sliderT);
		
		editT.add(sliderT);
		editT.add(minusTBtn);
		editT.add(posT);
		editT.add(plusTBtn);
		
		
		
		JLabel labelZ=new JLabel("Z: ");
		maxTickSpace= z < 10 ? 2 : (z < 20 ? 5 : 10);
		if(z>0){
			sliderZ = new JSlider( 0, z-1, 0 );
		}else{
			sliderZ = new JSlider( 0, 0, 0 );
		}
		sliderZ.setPaintTicks( true );
		sliderZ.setMajorTickSpacing( 10 );
		sliderZ.setMinorTickSpacing( 1 );
		//		sliderZ.setPaintTrack( false );
		sliderZ.createStandardLabels(1);
		sliderZ.setPaintLabels(true);
		sliderZ.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int cardID=(numZ*numC)*(sliderT.getValue())+numC*(sliderZ.getValue())+sliderC.getValue();
				cl.show(cardP,String.valueOf(cardID));
			}
		});
		labelZ.setLabelFor(sliderZ);

		JLabel labelC = new JLabel("C: ");
		maxTickSpace= c < 10 ? 2 : (c < 20 ? 5 : 10);
		if(c>0){
			sliderC = new JSlider( 0, c-1, 0 );
		}else{
			sliderC = new JSlider( 0, 0, 0 );
		}

		sliderC.setPaintTicks( true );
		sliderC.setMajorTickSpacing( maxTickSpace );
		sliderC.setMinorTickSpacing( 1 );
		//		sliderC.setPaintTrack( false );
		sliderC.createStandardLabels(1);
		sliderC.setPaintLabels(true);
		//	
		sliderC.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int cardID=(numZ*numC)*(sliderT.getValue())+numC*(sliderZ.getValue())+sliderC.getValue();
				cl.show(cardP,String.valueOf(cardID));
			}
		});
		labelC.setLabelFor(sliderC);
		List<String> idList=new ArrayList<String>();
		for(int i=0; i<t*z*c; i++)
			idList.add(String.valueOf(i));
		
		cl = new CardLayout();
		cardP=new JPanel(cl);
//		addToCardPane(cardP,list, idList);
		if(list==null) return;
		for(int i=0; i< list.size(); i++){
			PlaneCompUI pUI=(PlaneCompUI) list.get(i);
			pUI.buildComponents();
    		cardP.add(pUI,idList.get(i));
		}
		
		List<JLabel> myLabels=new ArrayList<JLabel>();
		myLabels.add(labelT);
		myLabels.add(labelZ);
		myLabels.add(labelC);
		
		List<JComponent> myFields=new ArrayList<JComponent>();
		myFields.add(sliderT);
//		myFields.add(editT);
		myFields.add(sliderZ);
		myFields.add(sliderC);
		
		GridBagLayout myGridbag=new GridBagLayout();
		JPanel btnPane=new JPanel();
		btnPane.setLayout(myGridbag);
		addLabelTextRows(myLabels,myFields,myGridbag,btnPane);
		
		
		this.add(btnPane,BorderLayout.NORTH);
		this.add(cardP,BorderLayout.CENTER);

	}
	
	private void addToCardPane(JPanel p,List<ElementsCompUI> list,List<String> idList)
	{
		if(list==null) return;
		for(int i=0; i< list.size(); i++){
			PlaneCompUI pUI=(PlaneCompUI) list.get(i);
			pUI.buildComponents();
    		p.add(pUI,idList.get(i));
		}
	}

	@Override
	public void buildComponents() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void buildExtendedComponents() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createDummyPane(boolean inactive) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clearDataValues() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<TagData> getActiveTags() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean userInput() 
	{
		boolean result=false;
		Component[] cList=cardP.getComponents();
		for(int i=0; i<cList.length; i++){
			if(cList[i] instanceof PlaneCompUI){
				result=result || ((PlaneCompUI)cList[i]).userInput();
			}
		}
		return result;
	}

	@Override
	public void update(List<TagData> list) {
		// TODO Auto-generated method stub
		
	}

	
}
