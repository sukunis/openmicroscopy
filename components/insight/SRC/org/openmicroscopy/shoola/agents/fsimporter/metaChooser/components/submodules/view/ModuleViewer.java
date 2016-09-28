package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.view;

import java.awt.Container;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Arrays;
import java.util.List;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ome.units.quantity.Length;
import ome.units.unit.Unit;
import ome.xml.model.primitives.PositiveInteger;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagData;

public abstract class ModuleViewer extends JPanel 
{
	public static final boolean REQUIRED=true;
	public static final boolean OPTIONAL =false;
	
	
	protected GridBagConstraints c;
	protected GridBagLayout gridbag;
	
	protected JPanel globalPane;
	
	protected List<TagData> tagList;
	protected boolean dataChanged;
	
	
	public abstract void saveData();
	protected abstract void initTag(TagConfiguration t); 
	
	protected boolean inputAt(TagData tag)
	{
		if(tag != null && tag.valueChanged()){
			return true;
		}
		return false;
	}
	
	public boolean hasDataToSave() 
	{
		boolean result=false;
		if(tagList!=null){
			for(int i=0; i<tagList.size();i++){
				boolean val=tagList.get(i)!=null ? tagList.get(i).valueChanged() : false;
				result= result || val || dataChanged;
			}
		}
		return (result);
	}
	
	/**
	 * Get enum values as string[]
	 * @param e Enum.class
	 * @return 
	 */
	public static String[] getNames(Class<? extends Enum<?>> e) {
		 return Arrays.toString(e.getEnumConstants()).replaceAll("^.|.$", "").split(", ");
//	    return Arrays.stream(e.getEnumConstants()).map(Enum::name).toArray(String[]::new);
	}
	
	public static PositiveInteger parseToPositiveInt(String c)
	{
		if(c==null || c.equals(""))
			return null;
		
		return new PositiveInteger(Integer.parseInt(c));
	}
	
	public static Length parseToLength(String c, Unit<Length> unit) throws Exception
	{
		if(c==null || c.equals(""))
			return null;
		
		return new Length(Double.valueOf(c), unit);
	}
	
	public static Double parseToDouble(String c) throws NumberFormatException
	{
		if(c==null || c.equals(""))
			return null;
		
		return Double.parseDouble(c);
	}
	
	/**
	 * Init given tags and mark it as visible.
	 * @param list
	 */
	protected void initTags(List<TagConfiguration> list) 
	{
		if(list==null)
			return;
		
		for(int i=0; i<list.size();i++){
			TagConfiguration t=list.get(i);
			if(t.getName()!=null){
				initTag(t);
			}
		}
	}
	
	
	protected void addTagToGUI(TagData tag,List<JLabel> labels,List<JComponent> comp)
	{
		if(tag != null && tag.isVisible()){
			labels.add(tag.getTagLabel());
			comp.add(tag.getInputField());
		}
	}
	
	protected void addLabelToGUI(JLabel l,List<JLabel> labels,List<JComponent> comp)
	{
		if(l!=null){
			Font font = l.getFont();
			// same font but bold
			Font boldFont = new Font(font.getFontName(), Font.BOLD, font.getSize());
			l.setFont(boldFont);
			labels.add(l);
			comp.add((JComponent) Box.createVerticalStrut(5));
		}
	}
	protected void addVSpaceToGui(int height,List<JLabel> labels,List<JComponent> comp)
	{
		labels.add(new JLabel(""));
		comp.add((JComponent) Box.createVerticalStrut(height));
	}
	
	//TODO: equal vertical space between components
		protected void addLabelTextRows(List<JLabel> labels,List<JComponent> fields,GridBagLayout gridbag,Container container) {
			GridBagConstraints c = new GridBagConstraints();
			c.anchor = GridBagConstraints.NORTHWEST;
			c.insets = new Insets( 0, 0, 1, 0);
			int numLabels = labels.size();

			for (int i = 0; i < numLabels; i++) {
				c.gridwidth = GridBagConstraints.RELATIVE; //next-to-last
				c.fill = GridBagConstraints.NONE;      //reset to default
				c.weightx = 0.0;                       //reset to default
				container.add(labels.get(i), c);

				c.gridwidth = GridBagConstraints.REMAINDER;     //end row
				c.fill = GridBagConstraints.HORIZONTAL;
				c.weightx = 1.0;
				container.add(fields.get(i), c);
				
			}
		}
}
