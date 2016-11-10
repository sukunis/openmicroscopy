package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.submodules.view;

import java.awt.Container;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import ome.units.quantity.Length;
import ome.units.unit.Unit;
import ome.xml.model.primitives.PercentFraction;
import ome.xml.model.primitives.PositiveFloat;
import ome.xml.model.primitives.PositiveInteger;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util.TagData;

/**
 * Works for xsi:schemaLocation="http://www.openmicroscopy.org/Schemas/OME/2015-01 
 * @author Kunis
 *
 */
public abstract class ModuleViewer extends JPanel 
{
	public static final boolean REQUIRED=true;
	public static final boolean OPTIONAL =false;
	
	protected boolean predefinitionValLoaded;
	
	protected GridBagConstraints gridBagConstraints;
	protected GridBagLayout gridbag;
	
	protected JPanel globalPane;
	
	protected List<TagData> tagList;
	protected boolean dataChanged;
	
	
	public abstract void saveData();
	protected abstract void initTag(TagConfiguration t); 
	protected abstract void setPredefinedTag(TagConfiguration t);
	
	private static String pattern_double = "\\s|[0-9]+.*[0-9]*";//"\\d*+\\.\\d{1,}";
	private static String pattern_posDouble="\\s|[1-9]+.*[0-9]*";
	
//	private static String pattern_number="\d";
	/* http://stackoverflow.com/questions/6400955/how-to-get-1-100-using-regex
	 * match 0 oder 0.0-0.99 oder 1 oder 1.0*/
	private static String pattern_percentFraction="[0]{1}.[0-9]{1,2}|1|1.0|0";
	
	public final static String ERROR_PREVALUE="Invalid predefined value: ";
	
	
	
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
	
	/**
	 * Parse String to a simple type PercentFraction that restricts the value to a float between 0 and 1 (inclusive)
	 * @param c
	 * @return
	 * @throws Exception
	 */
	public static PercentFraction parseToPercentFraction(String c) throws Exception
	{
		if(c==null || c.equals(""))
			return null;

		
		return new PercentFraction(Float.valueOf(c));
	}
	
	protected Boolean parseToBoolean(String val) 
	{
		if(val==null || val.equals("")){
			System.out.println("# ModuleViewer::parseBoolean(): return null");
			return null;
		}
		
		return Boolean.valueOf(val);
	}
	
	public static PositiveInteger parseToPositiveInt(String c) throws Exception
	{
		if(c==null || c.equals(""))
			return null;
		PositiveInteger result=null;
		Integer t=Integer.parseInt(c);
//		if(t!=null && t>0){
//			System.out.println("\t...parseToPositiveInt() "+t);
			result=new PositiveInteger(t);
//		}else{
//			System.out.println("ERROR: parseToPositiveInt() "+c);
//		}
		return result;
	}
	
	/**
	 * If positiveVal==true, c has to be a positive float >0. Test by parse PositiveFloat
	 * @param c
	 * @param unit
	 * @param positiveVal
	 * @return
	 * @throws Exception
	 */
	public static Length parseToLength(String c, Unit<Length> unit, boolean positiveVal) throws Exception
	{
		if(c==null || c.equals(""))
			return null;
		
		Double value=Double.valueOf(c);
		Length result=null;
		if(positiveVal){
			// if value isn't a positive number-> throws error
			PositiveFloat pF=new PositiveFloat(value);
			result=new Length(value,unit);
		}else{
			result=new Length(value,unit);
		}
		
		return result;
	}
	
	public static Double parseToDouble(String c) throws NumberFormatException
	{
		if(c==null || c.equals(""))
			return null;
		
		return Double.parseDouble(c);
	}
	
	/**
	 * Init given tags and mark it as visible.(Predefinition of gui and values)
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
	
	/**
	 * Show predefined values if showPreValues==true and the field is init but empty
	 * @param list
	 * @param showPreValues
	 */
	public void showPredefinitions(List<TagConfiguration> list,boolean showPreValues)
	{
		if(showPreValues){
			for(int i=0; i<list.size();i++){
				TagConfiguration t=list.get(i);
				if(t.getName()!=null){
					setPredefinedTag(t);
				}
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
		
//		protected void addLabelTextRows(List<TagData> tags,GridBagLayout gridbag,Container container) {
//			GridBagConstraints c = new GridBagConstraints();
//			c.anchor = GridBagConstraints.NORTHWEST;
//			c.insets = new Insets( 0, 0, 1, 0);
//			int numLabels = tags.size();
//
//			for (int i = 0; i < numLabels; i++) {
//				c.gridwidth = GridBagConstraints.RELATIVE; //next-to-last
//				c.fill = GridBagConstraints.NONE;      //reset to default
//				c.weightx = 0.0;                       //reset to default
//				container.add(tags.get(i).getTagLabel(), c);
//
//				c.gridwidth = GridBagConstraints.REMAINDER;     //end row
//				c.fill = GridBagConstraints.HORIZONTAL;
//				c.weightx = 1.0;
//				container.add(tags.get(i).getInputField(), c);
//				
//			}
//		}
		
		public boolean predefinitionValAreLoaded()
		{
			return predefinitionValLoaded;
		}
		
		protected void validateInput(TagData tag,String error,String pattern) 
		{
			String text = tag.getTagValue();
			Pattern r= Pattern.compile(pattern);
			Matcher m= r.matcher(text);
			if(m.matches()){
				tag.setTagInfo("");
			}else{
				tag.setTagInfo(error);
			}
		}
		
		
		
		/**
		 * @return
		 */
		public DocumentListener createDocumentListenerDouble(TagData tag, String error) {
			return new DocumentListenerForDouble(tag,error, false); 
				
		}
		
		public DocumentListener createDocumentListenerPosFloat(TagData tag,String error){
			return new DocumentListenerForDouble(tag,error, true); 
		}
		
		class DocumentListenerForDouble implements DocumentListener
		{
			private TagData tag;
			private String error;
			private boolean posVal;
			
			public DocumentListenerForDouble(TagData tag,String error, boolean positiveVal)
			{
				this.tag=tag;
				this.error=error;
				this.posVal=positiveVal;
			}
			@Override
			public void removeUpdate(DocumentEvent e) {}
			@Override
			public void insertUpdate(DocumentEvent e) {
				if(!posVal)
					validateInput(tag,error,pattern_double);
				else
					validateInput(tag,error,pattern_posDouble);
			}
			@Override
			public void changedUpdate(DocumentEvent e) {}
		}
		
		
		/**
		 * @return
		 */
		public DocumentListener createDocumentListenerPercentFraction(TagData tag, String error) {
			return new DocumentListenerForPercentFraction(tag,error); 
				
		}
		
		class DocumentListenerForPercentFraction implements DocumentListener
		{
			private TagData tag;
			private String error;
			public DocumentListenerForPercentFraction(TagData tag,String error)
			{
				this.tag=tag;
				this.error=error;
			}
			@Override
			public void removeUpdate(DocumentEvent e) {}
			@Override
			public void insertUpdate(DocumentEvent e) {
				validateInput(tag,error,pattern_percentFraction);
			}
			@Override
			public void changedUpdate(DocumentEvent e) {}
		}
}
