package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor;

import java.awt.Color;
import java.awt.Component;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.logging.Logger;

import javafx.embed.swing.JFXPanel;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;

import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.JTextComponent;

import loci.common.DateTools;
import ome.units.unit.Unit;
import ome.xml.model.primitives.Timestamp;

import org.apache.commons.lang.BooleanUtils;
import org.joda.time.format.DateTimeFormat;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.UOSMetadataLogger;
import org.openmicroscopy.shoola.util.ui.UIUtilities;

public class TagData 
{
	/** Logger for this class. */
	private static Logger LOGGER = Logger.getLogger(UOSMetadataLogger.class.getName());

	Color fillInfo=new Color(240,240,240);//Color.LIGHT_GRAY;
	Color noInfo=new Color(217,229,220);//Color.blue;
	Color resetInfo=Color.white;
	
	private final String datePattern = DateTools.TIMESTAMP_FORMAT;

	//status of inputfield
	static final int INACTIVE=0;
	static final int EMPTY =1;
	static final int SET=2;
	static final int OVERWRITE=3;

	// kind of inputfields
	static final int TEXTFIELD=0;
	static final int COMBOBOX=1;
	static final int TEXTPANE=2;
	static final int CHECKBOX=3;
	static final int ARRAYFIELDS=4;
	static final int TIMESTAMP=5;
	static final int LIST=6;

	private JLabel label;
	private JComponent inputField;
	private Unit unit;
	private int status=INACTIVE;
	private boolean prop;
	private int type;
	private boolean visible;

	private boolean valChanged;

	private String name;
	private Boolean markedToStore;

	private KeyListener fieldKeyListener;
	private ActionListener fieldActionListener;


	public TagData(String name, String[] val, boolean prop,int type) 
	{
		if(val==null)
			val=new String[1];
		initListener();
		this.markedToStore=false;
		this.type=type;
		this.name=name;
		label = new JLabel(name);
		int size=val!=null ? val.length : 1;
		switch (type) {
		case ARRAYFIELDS:
			initArrayTextField(size);
			break;
		default:
			initTextField();
			break;
		}

		label.setLabelFor(inputField);
		setTagValue(val);
		setTagProp(prop);
		visible=false;
	}

	public TagData(String name, String val, boolean prop,int type) 
	{
		this(name,val,prop,type,null);
	}
	
	public TagData(String name, String val,Unit unit, boolean prop,int type) 
	{
		this(name,val,prop,type,null);
		this.unit=unit;
		label= new JLabel(name+" ["+unit.getSymbol()+"]:");
		label.setLabelFor(inputField);
	}

	public TagData(String name, String val, boolean prop,int type, String[] defaultVal) 
	{
		initListener();
		this.markedToStore=false;
		this.type=type;
		this.name=name;
		label = new JLabel(name);
		switch (type) {
		case TEXTFIELD:
			initTextField();
			break;
		case COMBOBOX:
			initComboBox(defaultVal);
			break;
		case TEXTPANE:
			initTextPane();
			break;
		case CHECKBOX:
			initCheckBox(val);
			break;
		case ARRAYFIELDS:
			initArrayTextField(1);
			break;
		case TIMESTAMP:
			initTimeStampField();
			break;
		default:
			initTextField();
			break;
		}

		label.setLabelFor(inputField);
		setTagValue(val);
		setTagProp(prop);
		visible=false;
	}

	private void initTimeStampField() 
	{
//		inputField = Box.createHorizontalBox();
//		
////		GridPane dateGrid=new GridPane();
//		DatePicker dPicker=new DatePicker();
//		MyStringConverter converter = new MyStringConverter();
//		dPicker.setConverter(converter);
//		dPicker.setPromptText(datePattern.toLowerCase());
//		JFXPanel fxPane=new JFXPanel();
////		dateGrid.add(dPicker,0,0);
//		fxPane.add(dPicker);
//		inputField.add(fxPane);
		
		
		inputField = new JTextField(10);
		inputField.setToolTipText("Format: "+datePattern);
		inputField.addKeyListener(fieldKeyListener);

	}

	private void initTextField()
	{
		inputField = new JTextField(10);
		inputField.addKeyListener(fieldKeyListener);
	}

	private void initTextPane()
	{
		inputField = new JTextPane();
		inputField.addKeyListener(fieldKeyListener);
	}

	private void initCheckBox(String val)
	{
		inputField = new JCheckBox("",Boolean.parseBoolean(val));
		((JCheckBox) inputField).addActionListener(fieldActionListener);
	}

	private void initArrayTextField(int size)
	{
		inputField = Box.createHorizontalBox();
		for(int i=0; i<size; i++){
			JTextField txtF=new JTextField();
			txtF.addKeyListener(fieldKeyListener);
			((Box)inputField).add(txtF);
		}
	}

	private void initComboBox(String[] defaultVal)
	{
		if(defaultVal!=null){
			inputField = new JComboBox<String>(defaultVal);
			((JComboBox<String>) inputField).insertItemAt("",0);
			((JComboBox<String>) inputField).setSelectedItem(0);
		}else{
			inputField = new JComboBox<String>();
		}

		((JComboBox<String>) inputField).addActionListener(fieldActionListener);
		//			((JComboBox<String>) inputField).addActionListener(new ActionListener(){
		//				public void actionPerformed(ActionEvent evt) 
		//				{
		////					int commandId = Integer.parseInt(evt.getActionCommand());
		////					switch (commandId) {
		////					case SET_CB_VAL:
		////						JComboBox<String> cb = (JComboBox<String>)evt.getSource();
		////				        String chName = (String)cb.getSelectedItem();
		////				         int ch=cb.getSelectedIndex();
		////					}
		//				}
		//			});
	}

	private void initListener()
	{
		fieldKeyListener=new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
			}
			@Override
			public void keyReleased(KeyEvent e) {
				valChanged=true;	
				if(inputField instanceof JTextField){
					if(inputField.getForeground()==Color.gray && 
							!((JTextField) inputField).getText().equals(DateTools.TIMESTAMP_FORMAT))
						inputField.setForeground(Color.black);
				}
			}
			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
			}
		};

		fieldActionListener=new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				valChanged=true;
			}
		};


	}

	/** Action Listener for tagData*/
	public void addActionListener(ActionListener l)
	{
		switch (type) {
		case TEXTFIELD:
			((JTextField)inputField).addActionListener(l);
			break;
		case COMBOBOX:
			((JComboBox)inputField).addActionListener(l); 
			break;
		case TEXTPANE:

			break;
		case CHECKBOX:
			((JCheckBox)inputField).addActionListener(l);
			break;
		case ARRAYFIELDS:

			break;
		case TIMESTAMP:
			break;
		default:
			((JTextField)inputField).addActionListener(l);
			break;
		}
	}

	public void setDefaultValues(String[] list)
	{
		switch (type) {
		case COMBOBOX:
			DefaultComboBoxModel<String>model=(DefaultComboBoxModel<String>) ((JComboBox<String>) inputField).getModel();
			model.removeAllElements();
			model.addElement("");
			for(int c=0; c<list.length; c++){
				model.addElement(list[c]);
			}
			((JComboBox<String>) inputField).setModel(model);
			break;

		default:
			break;
		}

	}

	public JComponent getInputField()
	{
		return inputField;
	}
	public JLabel getTagLabel(){
		return label;
	}
	public String getTagValue() 
	{
		String val="";
		switch (type) {
		case TIMESTAMP:
			val = readTimestamp(val);
			break;
		case TEXTPANE:
			val=((JTextPane) inputField).getText();
			break;
		case TEXTFIELD:
			val=((JTextField) inputField).getText();
			break;
		case COMBOBOX:
			val=(String) ((JComboBox<String>) inputField).getSelectedItem();
			break;
		case CHECKBOX:
			val= ((JCheckBox)inputField).isSelected()? "true" : "false";
			break;
		default:
			break;
		}
		return val!=null? val : "";
	}

	

	public String getTagValue(int index) 
	{
		String val="";
		switch (type) {
		case ARRAYFIELDS:
			Component comp=inputField.getComponent(index);
			if(comp instanceof JTextField)
				val=((JTextField) comp).getText();
			else
				LOGGER.severe("can't get value at "+index+" ");

		default:
			getTagValue();
			break;
		}
		return val!=null? val : "";
	}

	public Unit getTagUnit()
	{
		return unit;
	}

	public void setTagValue(String val,Unit unit, boolean property)
	{
		if(!this.unit.equals(unit)){
			label=new JLabel(this.name+" ["+unit.getSymbol()+"]:");
			label.setLabelFor(inputField);
		}
		setTagValue(val);
		setTagProp(property);
		valChanged=false;
	}

	public void setTagValue(String val, boolean property)
	{
		setTagValue(val);
		setTagProp(property);
		valChanged=false;
	}

	public void setTagValue(String val, int index, boolean property)
	{
		setTagValue(val,index);
		setTagProp(property);
		valChanged=false;
	}

	public void setTagValue(String[] val, boolean property)
	{
		if(val== null)
			val=new String[1];
		setTagValue(val);
		setTagProp(property);
		valChanged=false;
	}

	private void setTagValue(String val, int index)
	{
		switch (type) {
		case ARRAYFIELDS:
			// split string
			setValArrayField(val, index);
			break;
		default:
			setTagValue(val);
			break;
		}
		setTagStatus( val.equals("") ? EMPTY : (status==EMPTY ? SET : OVERWRITE));
	}

	private void setTagValue(String[] val) 
	{

		switch (type) {
		case ARRAYFIELDS:
			// split string 
			for(int i=0; i<val.length; i++){
				setValArrayField(val[i], i);
			}
		default:
			break;
		}
		setTagStatus( (val.length==0) ? EMPTY : (status==EMPTY ? SET : OVERWRITE));
	}

	public void setTagValue(String val) 
	{
		if(val==null || val.equals("")){
			val="";
			inputField.setBackground(noInfo);
		}else{
			inputField.setBackground(fillInfo);
		}
		switch (type) {
		case TEXTFIELD:
			setValTextField(val);
			break;
		case COMBOBOX:
			setValComboBox(val);
			break;
		case TEXTPANE:
			setValTextPane(val);
			break;
		case CHECKBOX:
			setValCheckbox(val);
			break;
		case ARRAYFIELDS:
			setValArrayField(val);
			break;
		case TIMESTAMP:
			setValTimestamp(val);
			break;
		default:
			break;
		}
		setTagStatus( val.equals("") ? EMPTY : (status==EMPTY ? SET : OVERWRITE));
		valChanged=false;
	}
	
	private String readTimestamp(String val) 
	{
		//TODO: format test
		try{
			val=DateTools.formatDate(((JTextField)inputField).getText(), DateTools.TIMESTAMP_FORMAT);
		}catch(Exception e){
			LOGGER.severe("Wrong string input format timestamp: "+val);
			e.printStackTrace();
		}
		return val;
	}
	

	private void setValTimestamp(String val) 
	{
		if(val==null || val.equals("")){
			((JTextField) inputField).setText(DateTools.TIMESTAMP_FORMAT.toLowerCase());
			((JTextField) inputField).setForeground(Color.gray);
		}else{
			((JTextField) inputField).setForeground(Color.black);
			String dateformat= DateTools.ISO8601_FORMAT_MS;
			String s=DateTools.formatDate(val,dateformat);
			if(s==null){
				dateformat=DateTools.ISO8601_FORMAT;
				s=DateTools.formatDate(val, dateformat);
				
			}
			DateTimeFormatter formatter=DateTimeFormatter.ofPattern(dateformat);
			DateFormat df=new SimpleDateFormat(dateformat);
			
			Date d=null;
			try {
				d=df.parse(s);
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				LOGGER.severe("Parse error date for format "+dateformat);
			}
			
			try {
				//			((JTextField) inputField).setText( d.toString());
				SimpleDateFormat f=new SimpleDateFormat(DateTools.TIMESTAMP_FORMAT);
				((JTextField) inputField).setText( f.format(d));
			} catch (Exception e) {
				LOGGER.severe("Parse error for timestamp");
			}
			
		}
				
	}

	private void setValArrayField(String val) 
	{
		if(val.equals("")){
			for(Component c :inputField.getComponents()){
				((JTextField) c).setText("");
				c.setBackground(noInfo);
			}
		}else{
			// split string 
			String[] components=val.split("x");
			if(components.length!= inputField.getComponentCount()){
				LOGGER.warning("Wrong input for "+getTagLabel());
			}
			for(int i=0; i<components.length;i++)
			{
				setValArrayField(components[i], i);
			}
		}
	}
	private void setValArrayField(String s,int i)
	{
		JTextField txtF=(JTextField) inputField.getComponent(i);
		txtF.setText(s);
		if(s==null || s.equals("")){
			s="";
			txtF.setBackground(noInfo);
		}else{
			txtF.setBackground(fillInfo);
		}
	}

	private void setValCheckbox(String val) 
	{
		boolean bVal=BooleanUtils.toBoolean(val);
		((JCheckBox) inputField).setSelected(bVal);
	}

	private void setValTextPane(String val) {
		((JTextPane) inputField).setText(val);
	}

	private void setValComboBox(String val) {
		for(int c=0; c< ((JComboBox<String>) inputField).getItemCount(); c++)
		{
			if(((JComboBox<String>) inputField).getItemAt(c).equals(val)){
				((JComboBox<String>) inputField).setSelectedIndex(c);
			}
		}
	}

	private void setValTextField(String val) {
		((JTextField) inputField).setText(val);
	}




	public int getTagStatus() {
		return status;
	}
	private void setTagStatus(int status) {
		this.status = status;
	}
	public boolean getTagProp() {
		return prop;
	}
	public void setTagProp(boolean prop) {
		this.prop = prop;
	}

	public void setEnable(boolean val)
	{
		label.setEnabled(val);
		inputField.setEnabled(val);
		if(!val)
			status=INACTIVE;
	}

	public void setMarkedToStore(boolean b)
	{
		markedToStore=b;
	}

	public boolean isMarkedToStore()
	{
		return markedToStore;
	}

	public boolean valueChanged()
	{
		return valChanged;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	class MyStringConverter extends StringConverter<LocalDate>{
		
		DateTimeFormatter dateFormatter = 
                DateTimeFormatter.ofPattern(datePattern);
            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }
            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }	
	}

}
