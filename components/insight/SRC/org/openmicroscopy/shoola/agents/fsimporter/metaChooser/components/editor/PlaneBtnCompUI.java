package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.PlaneCompUI;

public class PlaneBtnCompUI extends JPanel
{
	private JPanel cardPaneAll;
	private JPanel cardPaneZ;
	private JPanel cardPaneC;
	
	private List<PlaneCompUI> list;
	
	private JComponent btnT;
	private JComponent btnZ;
	private JComponent btnC;
	
	private int numT;
	private int numZ;
	private int numC;
	
	//dummy pane
	public PlaneBtnCompUI()
	{
		setLayout(new BorderLayout());
		List<PlaneCompUI> list=new ArrayList<PlaneCompUI>();
		PlaneCompUI plane=new PlaneCompUI();
		plane.createDummyPane(true);
		list.add(plane);
		createGUI(list,1,1,1);
	}
	
	public PlaneBtnCompUI(List<PlaneCompUI> list, int t, int z, int c)
	{
		setLayout(new BorderLayout());
		createGUI(list, t, z, c);
		
	}
	
	private void createGUI(List<PlaneCompUI> list, int t, int z, int c)
	{
		CardLayout clAll = new CardLayout();
		cardPaneAll=new JPanel(clAll);
		
		CardLayout clC = new CardLayout();
		cardPaneC=new JPanel(clC);
		cardPaneC.setBorder(new TitledBorder("Channels:"));
		
		CardLayout clZ = new CardLayout();
		cardPaneZ=new JPanel(clZ);
		cardPaneZ.setBorder(new TitledBorder("Slices:"));
		
		List<String> idListAll=new ArrayList<String>();
		List<String> idListT=new ArrayList<String>();
		List<String> idListZ=new ArrayList<String>();
		List<String> idListC=new ArrayList<String>();
		
		List<String> titleListT=new ArrayList<String>();
		List<String> titleListZ=new ArrayList<String>();
		List<String> titleListC=new ArrayList<String>();
		int id=1;
		
		for(int s=1; s<=t; s++){
			titleListT.add(String.valueOf(s-1));
			for(int i=1; i<=z; i++){
				titleListZ.add(String.valueOf(i-1));
				for(int j=1; j<=c; j++){
					titleListC.add(String.valueOf(j-1));
					//id=(z*c)*(s-1)+ c*(i-1)+ j
					idListC.add(String.valueOf(id));
					idListAll.add(String.valueOf(id));
					id++;
				}
				btnC=makeBreadcrumbList(titleListC, idListC, cardPaneAll);
				titleListC.clear();
				idListC.clear();
				
				cardPaneC.add(btnC,String.valueOf(i));
				idListZ.add(String.valueOf(i));
			}
			btnZ=makeBreadcrumbList(titleListZ,idListZ,cardPaneC);
			titleListZ.clear();
			
			cardPaneZ.add(btnZ, String.valueOf(s));
			idListT.add(String.valueOf(s));
		}
		btnT=makeBreadcrumbList(titleListT, idListT,cardPaneZ);
		
		addToCardPane(cardPaneAll,list, idListAll);
		
		JPanel btnPane=new JPanel();
		btnPane.setLayout(new GridLayout(3,0,0,7));
		
		btnPane.add(btnT);
		btnPane.add(cardPaneZ);
		btnPane.add(cardPaneC);
		this.add(btnPane,BorderLayout.NORTH);
		this.add(cardPaneAll,BorderLayout.CENTER);
	}
	
	private void addToCardPane(JPanel p,List<PlaneCompUI> list,List<String> idList)
	{
		if(list==null) return;
		for(int i=0; i< list.size(); i++)
    		p.add(list.get(i),idList.get(i));
	}
	

	private JPanel makePanel(int overlap) {
		 JPanel p=new JPanel(new FlowLayout(FlowLayout.LEADING, (-1)*overlap, 0)){
			public boolean isOptimizedDrawingEnabled(){
				return false;
			}
		 };

//	        p.setBorder(BorderFactory.createEmptyBorder(4, overlap + 4, 4, 4));
	        p.setBorder(BorderFactory.createEmptyBorder(4, overlap + 1, 4, 1));
	        p.setOpaque(false);
	        return p;
	    }
	 
	private JComponent makeBreadcrumbList(List<String> tList,List<String>idList, JComponent refPane) {
       JPanel p = makePanel(3);
       ButtonGroup bg = new ButtonGroup();
       for(int i=0; i<tList.size(); i++){
       	String title=tList.get(i);
       	String id=idList.get(i);
           AbstractButton b = makeButton(title,id,refPane);
           if(i==0)b.setSelected(true);
           p.add(b);
           bg.add(b);
       }
       
       return p;
   }
	
	private AbstractButton makeButton(final String title,String id, JComponent refPane) {
		final ToggleButtonBarCellIcon icon = new ToggleButtonBarCellIcon();
		
		AbstractButton b = new ExtendedRadioButton(title,id) {
			@Override public boolean contains(int x, int y) {
				if (icon == null || icon.area == null) {
					return super.contains(x, y);
				} else {
					return icon.area.contains(x, y);
				}
			}
		};
		((ExtendedRadioButton) b).setRefPane(refPane);
		b.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
			
				ExtendedRadioButton r = (ExtendedRadioButton) e.getSource();
				//                    tree.setSelectionPath(path);
				r.setSelected(true);
				JComponent refPane = r.getRefPane();
				CardLayout cl=(CardLayout)refPane.getLayout();
				cl.show(refPane,r.getCardID());
				//TODO: touch cardPaneC to update data
			}
		});
		b.setIcon(icon);
		b.setContentAreaFilled(false);
		b.setBorder(BorderFactory.createEmptyBorder());
		b.setVerticalAlignment(SwingConstants.CENTER);
		b.setVerticalTextPosition(SwingConstants.CENTER);
		b.setHorizontalAlignment(SwingConstants.CENTER);
		b.setHorizontalTextPosition(SwingConstants.CENTER);
		b.setFocusPainted(false);
		b.setOpaque(false);
//		b.setBackground(color);
		
	   
		return b;
    }
	
	
	//http://terai.xrea.jp/Swing/ToggleButtonBar.html
		class ToggleButtonBarCellIcon implements Icon, Serializable {
		    private static final long serialVersionUID = 1L;
		    private static final int W = 10;//Lenght of arrowhead
		    private static final int H = 21;
		    public Shape area;
		    public Shape getShape(Container parent, Component c, int x, int y) {
		        int w = c.getWidth()  - 1;
		        int h = c.getHeight() - 1;
		        int h2 = (int) (h * .5 + .5);
		        int w2 = W;
		        Path2D.Float p = new Path2D.Float();
		        p.moveTo(0,      0);
		        p.lineTo(w - w2, 0);
		        p.lineTo(w,      h2);
		        p.lineTo(w - w2, h);
		        p.lineTo(0,      h);
		        if (c != parent.getComponent(0)) {
		            p.lineTo(w2, h2);
		        }
		        p.closePath();
		        return AffineTransform.getTranslateInstance(x, y).createTransformedShape(p);
		    }
		    @Override public void paintIcon(Component c, Graphics g, int x, int y) {
		    
		        Container parent = c.getParent();
		        if (parent == null) {
		            return;
		        }
		        area = getShape(parent, c, x, y);

		        Color bgc = parent.getBackground();
		        Color borderColor = Color.GRAY.brighter();
		        if (c instanceof AbstractButton) {
		            ButtonModel m = ((AbstractButton) c).getModel();
		            
		            if (m.isSelected() /*|| m.isRollover()*/) {
		                bgc = Color.PINK;//c.getBackground();
		                borderColor = Color.GRAY;
		            }
		        }

		        Graphics2D g2 = (Graphics2D) g.create();
		        //g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		        g2.setPaint(bgc);
		        g2.fill(area);
		        g2.setPaint(borderColor);
		        g2.draw(area);
		        g2.dispose();
		    }
		    @Override public int getIconWidth()  {
		        return 60;
		    }
		    @Override public int getIconHeight() {
		        return H;
		    }
		}
		
		class ExtendedRadioButton extends JRadioButton{
			private String cardID;
			private JComponent refPane;
			
			public ExtendedRadioButton(String title,String id){
				super(title);
				cardID=id;
			}
			public void setCardID(String id){
				cardID=id;
			}
			public String getCardID(){
				return cardID;
			}
			public void setRefPane(JComponent ref){
				refPane=ref;
			}
			public JComponent getRefPane()
			{
				return refPane;
			}
			
		}

}
