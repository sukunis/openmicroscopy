package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Paint;
import java.awt.geom.Point2D;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;



public class TitledSeparator extends JLabel{
	private Color color;
	private final String title;
	private final Color target;
	private final int height;
	private final int titlePosition;

	public TitledSeparator(String title, int height, int titlePosition, boolean setBold) {
		this(title, null, height, titlePosition, setBold);
	}

	public TitledSeparator(
			String _title, Color _target, int _height, int _titlePosition, boolean setBold) {
		super();
		this.title = _title;
		this.target = _target;
		this.height = _height;
		this.titlePosition = _titlePosition;
		Icon icon = new Icon() {
			private int width = -1;
			private Paint painter1, painter2;

			@Override public void paintIcon(Component c, Graphics g, int x, int y) {
				int w = c.getWidth();
				if(w!=width || painter1==null || painter2==null || color==null) {
					width = w;
					Point2D start = new Point2D.Float(0f, 0f);
					Point2D end   = new Point2D.Float((float)width, 0f);
					float[] dist  = {0.0f, 1.0f};
					color = getBackground();
					color = color==null ? UIManager.getColor("Panel.background"):color;
					Color tc = target==null ? color : target;
					painter1 = new LinearGradientPaint(
							start, end, dist, new Color[] {tc.darker(),   color});
					painter2 = new LinearGradientPaint(
							start, end, dist, new Color[] {tc.brighter(), color});
				}
				int h = getIconHeight()/2;
				Graphics2D g2  = (Graphics2D)g.create();
				g2.setPaint(painter1);
				g2.fillRect(x, y,   width, getIconHeight());
				g2.setPaint(painter2);
				g2.fillRect(x, y+h, width, getIconHeight()-h);
				g2.dispose();
			}
			@Override public int getIconWidth()  { return 2000; } //dummy width
			@Override public int getIconHeight() { return height; }
		};


		//		    this.setBorder(BorderFactory.createTitledBorder(
		//		      BorderFactory.createMatteBorder(height, 0, 0, 0, icon), title,
		//		      TitledBorder.DEFAULT_JUSTIFICATION, titlePosition));
		this.setBorder(new MyTitledBorder(title,height,titlePosition,icon, setBold));

		//System.out.println(getInsets());
	}
	@Override public Dimension getMaximumSize() {
		Dimension d = super.getPreferredSize();
		d.width = Short.MAX_VALUE;
		return d;
	}
	@Override public void updateUI() {
		super.updateUI();
		color = null;
	}
	
	class MyTitledBorder extends TitledBorder{

		public MyTitledBorder(String title, boolean setBold) {
			super(title);
			setBorder(BorderFactory.createMatteBorder(2, 0,0, 0, Color.BLACK));
			if(setBold)
				setTitleFont(new Font(this.getTitleFont().getFontName(),Font.BOLD,12));
			else
				setTitleFont(new Font(this.getTitleFont().getFontName(),Font.PLAIN,12));
		}
		
		public MyTitledBorder(String title, int height,int pos,Icon icon, boolean setBold) {
			super(title);
			setBorder(BorderFactory.createMatteBorder(height, 0,0, 0, icon));
			if(setBold)
				setTitleFont(new Font(this.getTitleFont().getFontName(),Font.BOLD,12));
			else
				setTitleFont(new Font(this.getTitleFont().getFontName(),Font.PLAIN,12));
			setTitleJustification(TitledBorder.DEFAULT_JUSTIFICATION);
			setTitlePosition(pos);
		}
	}
}

