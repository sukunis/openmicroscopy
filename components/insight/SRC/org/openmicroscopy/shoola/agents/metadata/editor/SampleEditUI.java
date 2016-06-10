package org.openmicroscopy.shoola.agents.metadata.editor;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.editor.SampleCompUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.format.Sample;
import org.openmicroscopy.shoola.agents.util.EditorUtil;
import org.openmicroscopy.shoola.util.ui.MultilineLabel;
import org.openmicroscopy.shoola.util.ui.TextFieldLimit;
import org.openmicroscopy.shoola.util.ui.UIUtilities;

import omero.gateway.model.AnnotationData;
import omero.gateway.model.ChannelData;
import omero.gateway.model.DatasetData;
import omero.gateway.model.PlateData;

public class SampleEditUI extends AnnotationUI 
						implements DocumentListener, FocusListener, PropertyChangeListener,ActionListener
{
	/** The text associated to the data object. */
	private String				text="Sample ";
	
	private static final String SAVE_TEXT = "Save";
	  /** Bound property indicating to save the renaming for the image.*/
    static final String SAVE_PROPERTY = "Save";
	private static final String CANCEL_TEXT = "Cancel";
	private static final String SAVE_TIP = "Save Sample Data";
	private static final int SAVE = 0;
	private static final String CANCEL_TIP = "Cancel.";
	private static final int CANCEL = 1;
	 /** Bound property indicating to cancel the renaming.*/
    static final String CANCEL_PROPERTY = "Cancel";
	/** Reference to the control. */
	private EditorControl		controller;
	
	/** Map hosting the fields used to edit the corresponding sample.*/
	private LinkedHashMap<JLabel,JTextField> fields;
	
	private JButton saveButton;
	private JButton cancelButton;
	
	/**Component used to display a warning befor saving.*/
	private JTextArea messageLabel;

	 /** The data object hosting all the images to update.*/
    private Object parent;
    
    private Sample sampleObj;
	
	SampleEditUI(EditorModel model, EditorControl controller) {
		super(model);
		if (controller == null)
			throw new IllegalArgumentException("No control.");
		this.controller = controller;
		sampleObj=model.getSampleObj();
		initComponents();
		
	}

	String getText() { return text; }
	
	private void initComponents() 
	{
		if(sampleObj==null)
			sampleObj=new Sample();
		
		
		System.out.println("init sampleEditUI");
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(UIUtilities.BACKGROUND_COLOR);
        
        
        setSampleParameters();
        
        saveButton = new JButton(SAVE_TEXT);
        saveButton.setToolTipText(SAVE_TIP);
        saveButton.addActionListener(this);
        saveButton.setActionCommand(""+SAVE);
        saveButton.setEnabled(false);

        cancelButton = new JButton(CANCEL_TEXT);
        cancelButton.setToolTipText(CANCEL_TIP);
        cancelButton.addActionListener(this);
        cancelButton.setActionCommand(""+CANCEL);
        
//        messageLabel = new MultilineLabel();
//        messageLabel.setBackground(UIUtilities.BACKGROUND_COLOR);
        
        saveButton.setVisible(!(parent instanceof PlateData));
	}

	/**
	 * 
	 */
	private void setSampleParameters() 
	{
		fields = new LinkedHashMap<JLabel,JTextField>();
        
        JTextField field;
        JLabel l = new JLabel();
    	Font font = l.getFont();
    	int size = font.getSize()-2;
    	
        field = new TextFieldLimit(EditorUtil.MAX_CHAR-1);
        field.setBackground(UIUtilities.BACKGROUND_COLOR);
        field.setText("");//sampleObj.getDateAsString());
        field.getDocument().addDocumentListener(this);
        fields.put(UIUtilities.setTextFont(Sample.PREP_DATE_MAPLABEL,Font.BOLD,size),field);
        
        field = new TextFieldLimit(EditorUtil.MAX_CHAR-1);
        field.setBackground(UIUtilities.BACKGROUND_COLOR);
        field.setText("");//sampleObj.getDateAsString());
        field.getDocument().addDocumentListener(this);
        fields.put(UIUtilities.setTextFont(Sample.PREP_DESCRIPTION,Font.BOLD,size),field);
        
        System.out.println("sampleUI elements: "+fields.size());
	}
	
	 /** Builds and lays out the UI.*/
    private void buildGUI()
    {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(UIUtilities.BACKGROUND_COLOR);
        
        JPanel content = new JPanel();
    	content.setBackground(UIUtilities.BACKGROUND_COLOR);
    	content.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
    	content.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.NONE;
		c.weightx = 1;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets = new Insets(0, 0, 2, 2);
		c.gridy = 0;
		c.gridx = 0;
		
		JLabel label;
		JTextField value=new JTextField();
        
        Iterator<Entry<JLabel,JTextField>> 
        i = fields.entrySet().iterator();
        while (i.hasNext()) {
        	
        	label= i.next().getKey();
        	System.out.println("Add Sample key: "+label.getText());
//        	value = i.next().getValue();
        	content.add(label,c);
        	c.gridx++;
	    	content.add(value, c);
	    	c.gridy++;
	    	c.gridx = 0;
//            add(i.next().getKey());
        }
//        add(messageLabel);
        add(content);
        add(buildControls());
    }
    
    /**
     * Builds and lays out the controls.
     *
     * @return See above.
     */
    private JPanel buildControls()
    {
        JPanel bar = new JPanel();
        bar.setLayout(new FlowLayout(FlowLayout.LEFT));
        bar.setBackground(UIUtilities.BACKGROUND_COLOR);
        bar.add(saveButton);
//        if (parent instanceof DatasetData || parent instanceof PlateData)
//            bar.add(applyToAll);
        bar.add(cancelButton);
        return bar;
    }
    
    /** Saves the changes.*/
    private void save()
    {
        Entry<JLabel,JTextField> e;
        Iterator<Entry<JLabel,JTextField>>
        i = fields.entrySet().iterator();
        Sample sample=new Sample();
        while (i.hasNext()) {
        	e = i.next();
        	switch (e.getKey().getText()) {
			case Sample.PREP_DATE_MAPLABEL:
				sample.setPrepDate(e.getValue().getText());
				break;

			default:
				break;
			}
            
            
        }
        //Apply the
//        if (!applyToAll.isVisible())
//            firePropertyChange(APPLY_TO_ALL_PROPERTY, null, channels);
//        else 
        	firePropertyChange(SAVE_PROPERTY, null, sample);
        resetControls();
    }

    /** Resets the controls.*/
    private void resetControls()
    {
        saveButton.setEnabled(false);
        saveButton.setVisible(!(parent instanceof PlateData));
        saveButton.setText(SAVE_TEXT);
        messageLabel.setText("");
//        applyToAll.setVisible(true);
    }

    /** Cancel the saving.*/
    private void cancel()
    {
        resetControls();
        //Reset the fields' values.
        Entry<JLabel,JTextField> e;
        Iterator<Entry<JLabel,JTextField>>
        i = fields.entrySet().iterator();
        while (i.hasNext()) {
            e = i.next();
            switch (e.getKey().getText()) {
			case Sample.PREP_DATE_MAPLABEL:
				e.getValue().setText(sampleObj.getPrepDate().toString());
				break;

			default:
				break;
			}
        }
        firePropertyChange(CANCEL_PROPERTY, Boolean.valueOf(false),
                Boolean.valueOf(true));
    }





	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void focusGained(FocusEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void focusLost(FocusEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void buildUI() 
	{
		removeAll();
		Object refObject = model.getRefObject();
		if (model.isMultiSelection())
			return;
		
		 boolean b = model.canEdit();
		 buildGUI();
	}

	@Override
	protected String getComponentTitle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean hasDataToSave() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected List<Object> getAnnotationToRemove() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<AnnotationData> getAnnotationToSave() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void clearDisplay() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void clearData(Object oldObject) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void setComponentTitle() {
		// TODO Auto-generated method stub
		
	}







	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}

}
