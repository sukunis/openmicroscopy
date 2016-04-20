package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.format;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import loci.formats.MetadataTools;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.UOSMetadataLogger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ome.xml.model.primitives.Timestamp;

/**
 * Object for sample preparation metadata like
 * <Sample namespace="">
 * 		<Preparation date="" description="" >{0,1}
 * 		<Raw material code="" description="">{0,1}
 * 		<GridBox ID="" Nr="" Type="">{0,..,n}
 * 		<GridBox ID="" Nr="" Type="">
 * 		<Observed Sample>{0,...,n}
 * 		</Observed Sample>
 * 		<Observed Sample>
 * 		</Observed Sample>
 * </Sample>
 * @author kunis
 *
 */
public class Sample 
{
	/** Logger for this class. */
    private static Logger LOGGER = Logger.getLogger(UOSMetadataLogger.class.getName());
	
	/** preparation data **/
	private Timestamp preparationDate;
	private String preparationDescription;
	
	/** raw material data **/
	private String rawMaterialDesc;
	private String rawMaterialCode;
	
	/** sample support **/
	private GridBox gridBox;
	
	/** observed sample data**/
	private List<ObservedSample> obSampleList;

	
	//-- elements labels
	public static final String SAMPLE="Sample";
	public static final String RAW="RawMaterial";
	public static final String RAW_CODE="Code";
	public static final String RAW_DESC="Description";
	public static final String PREP="Preparation";
	public static final String PREP_DATE="Date";
	public static final String PREP_DESCRIPTION="Description";
	
	
	
	public Sample()
	{
		preparationDate=null;
		preparationDescription=null;
		rawMaterialDesc=null;
		rawMaterialCode=null;
		gridBox=null;
		obSampleList=null;
	}
	
	//copy constructor
	public Sample(Sample orig)
	{
		preparationDate=orig.preparationDate;
		preparationDescription=orig.preparationDescription;
		rawMaterialDesc=orig.rawMaterialDesc;
		rawMaterialCode=orig.rawMaterialCode;
		gridBox=orig.gridBox;
		obSampleList=orig.obSampleList;
	}
	
	//parse string of xmlannotation object to sample object
	public Sample(Element element)
	{
		String tagName=element.getTagName();
		if(!"Sample".equals(tagName)){
			LOGGER.warning("Expecting node name of Sample - but this is "+tagName);
		}
		if(element.hasAttribute("namespace")){
			//TODO test right namespace
		}
		
		readPreparationDataFromXML(element);
		readRawMaterialDataFromXML(element);
		readGridBoxDataFromXML(element);
		readObservedSampleDataFromXML(element);
	}

	private void readObservedSampleDataFromXML(Element element) {
		NodeList list;
		// parse observed sample infos
		list=element.getElementsByTagName(ObservedSample.getTagName());
		for(int i=0; i<list.getLength(); i++){
			Node node=list.item(i);
			if(node.getNodeType()==Node.ELEMENT_NODE){
				addObservedSample(new ObservedSample((Element) node));
			}
		}
	}

	private void readGridBoxDataFromXML(Element element) {
		// parse gridbox infos
		NodeList list=element.getElementsByTagName(GridBox.getGridBoxTagName());
		for (int i = 0; i < list.getLength(); i++) {
			Node node=list.item(i);
			if(node.getNodeType()==Node.ELEMENT_NODE){
				addGridBox(new GridBox((Element) node));
			}
		}
	}

	private void readRawMaterialDataFromXML(Element element)
	{
		NodeList list = element.getElementsByTagName(RAW);

		// parse preparation infos
		if(list!=null && list.getLength()>0){
			Node node=list.item(0);
			if(node.getNodeType()==Node.ELEMENT_NODE){
				if(((Element) node).hasAttribute(RAW_CODE)){
					setRawMaterialCode(String.valueOf(((Element) node).getAttribute(RAW_CODE)));
				}
				if(((Element)node).hasAttribute(RAW_DESC)){
					setRawMaterialDesc(String.valueOf(((Element)node).getAttribute(RAW_DESC)));
				}
			}
		}
	}
	
	private void readPreparationDataFromXML(Element element) 
	{
		NodeList list = element.getElementsByTagName(PREP);

		// parse preparation infos
		if(list!=null && list.getLength()>0){
			Node node=list.item(0);
			if(node.getNodeType()==Node.ELEMENT_NODE){
				if(((Element) node).hasAttribute(PREP_DATE)){
					setPrepDate(Timestamp.valueOf(String.valueOf(
							((Element) node).getAttribute(PREP_DATE))));
				}
				if(((Element)node).hasAttribute(PREP_DESCRIPTION)){
					setPrepDescription(String.valueOf(((Element)node).getAttribute(PREP_DESCRIPTION)));
				}
			}
		}
	}
	
	//-- set and getter
	public void setPrepDate(Timestamp date)
	{
		this.preparationDate=date;
	}
	
	public void setPrepDate(String date)
	{
		//TODO parse
//		this.preparationDate=date;
	}
	
	public Timestamp getPrepDate()
	{
		return preparationDate;
	}
	
	public String getDateAsString()
	{
		java.sql.Timestamp timestamp=null;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		
		if(preparationDate!=null){
			try{
				
				Date parsedDate = dateFormat.parse(preparationDate.toString());
				timestamp = new java.sql.Timestamp(parsedDate.getTime());

			}catch(Exception e){//this generic but you can control another types of exception
				LOGGER.severe("Wrong date format for SAMPLE preparation date");
				return "";
			}
			return dateFormat.format(timestamp);
		}else
			return "";
	}
	
	/**
	 * @return the description of probe preparation
	 */
	public String getPrepDescription() {
		return preparationDescription;
	}

	/**
	 * @param description of the probe preparation
	 */
	public void setPrepDescription(String description) {
		this.preparationDescription = description;
	}
	
	public void setRawMaterialDesc(String tagValue) {
		this.rawMaterialDesc=tagValue;
	}
	
	public String getRawMaterialDesc()
	{
		return rawMaterialDesc;
	}
	
	public String getRawMaterialCode()
	{
		return rawMaterialCode;
	}

	public void setRawMaterialCode(String tagValue) {
		this.rawMaterialCode=tagValue;		
	}

	/**
	 * @return the gridSizeX
	 */
	public GridBox getGridBox() {
			return gridBox;
	}
	
	

//	public void addGridBoxData(String nr, String type)
//	{
//		int size=0;
//		if(gridBoxList!=null){
//			size=gridBoxList.size();
//		}else{
//			gridBoxList=new ArrayList<GridBox>();
//		}
//		int gridBoxNr = (nr!=null && !nr.equals("")) ? Integer.valueOf(nr) : -1;
//		
//		GridBox box=new GridBox(String.valueOf(size),gridBoxNr,type);
//		gridBoxList.add(box);
//		
//	}
	
	public void setGridBoxData(Integer nr, String type) 
	{
		if(gridBox==null)
			gridBox=new GridBox("0",nr,type);
		else{
			gridBox.setNr(nr);
			gridBox.setType(type);
		}		
	}
	
	public void setGridBoxData(String nr, String type)
	{
		
		Integer gridBoxNr = (nr!=null && !nr.equals("")) ? Integer.valueOf(nr) : -1;
		setGridBoxData(gridBoxNr,type);
	}
	
	/**
	 * @param gridSizeX the gridSizeX to set
	 */
	public void addGridBox(GridBox g) 
	{
		gridBox=g;
	}
	
	public void setGridBoxNr(String nr)
	{
		Integer gridBoxNr = (nr!=null && !nr.equals("")) ? Integer.valueOf(nr) : -1;
		if(gridBox==null){
			gridBox=new GridBox("0",gridBoxNr,"");
		}else{
			gridBox.setNr(gridBoxNr);
		}
	}
	
	public void setGridBoxType(String t)
	{
		if(gridBox==null){
			gridBox=new GridBox("0",null,t);
		}else{
			gridBox.setType(t);
		}
	}
	

	public void addObservedSample(ObservedSample sample)
	{
		if(obSampleList==null){
			obSampleList=new ArrayList<ObservedSample>();
		}
		sample.setSampleID(MetadataTools.createLSID("ObservedSample", obSampleList.size()+1));
		obSampleList.add(sample);
	}
	
	public void setObservedSample(ObservedSample sample)
	{
		obSampleList=new ArrayList<ObservedSample>();
//		sample.setSampleID("0");
		obSampleList.add(sample);
	}
	
	
	
	public int getObservedSampleCount()
	{
		return (obSampleList!=null) ? obSampleList.size() : 0;
	}
	
	public List<ObservedSample> getObservedSampleList()
	{
		return obSampleList;
	}
	
	public ObservedSample getObservedSample(int i)
	{
		if(obSampleList!=null)
			return obSampleList.get(i);
		else
			return null;
	}
	
	//-- xml
	
	public String toXMLAnnotation()
	{
		StringBuffer xml=new StringBuffer("<Sample namespace=\"uos/Schemas/Additions/2016-02\">");
		
		xml=appendPreparation(xml);
		appendRawMaterial(xml);
		
		xml.append(gridBox.toXMLAnnotation());
		
		for(int i=0; i<obSampleList.size(); i++){
			xml.append(obSampleList.get(i).toXMLAnnotation());
		}
		
		xml.append("</Sample>");
		
		return xml.toString();
	}
	
	private StringBuffer appendPreparation(StringBuffer xml)
	{
		boolean writePrep=false;
		if(preparationDate!=null || preparationDescription!=null){
			xml.append("<"+PREP);
			writePrep=true;
		}
			
		if(preparationDate!=null){
			xml.append(" "+PREP_DATE+"=\"");
			xml.append(preparationDate.getValue());
			xml.append("\"");
		}
		
		if(preparationDescription!=null){
			xml.append(" "+PREP_DESCRIPTION+"=\"");
			xml.append(preparationDescription);
			xml.append("\"");
		}
		
		if(writePrep)
			xml.append("/>");
		return xml;
	}
	
	private StringBuffer appendRawMaterial(StringBuffer xml)
	{
		boolean writePrep=false;
		if(rawMaterialCode!=null || rawMaterialDesc!=null){
			xml.append("<"+PREP);
			writePrep=true;
		}
			
		if(rawMaterialCode!=null){
			xml.append(" "+RAW_CODE+"=\"");
			xml.append(rawMaterialCode);
			xml.append("\"");
		}
		
		if(rawMaterialDesc!=null){
			xml.append(" "+RAW_DESC+"=\"");
			xml.append(rawMaterialDesc);
			xml.append("\"");
		}
		
		if(writePrep)
			xml.append("/>");
		return xml;
	}
	
	
	
	public static class GridBox
	{
		private String id;
		private Integer nr;
		private String type;
		
		public static String GRID="GridBox";
		public static String GRID_ID="ID";
		public static String GRID_NR="NR";
		public static String GRID_TYPE="Type";
		
		public GridBox(String id,Integer nr, String type)
		{
			this.id=id;
			this.nr=nr;
			this.type=type;
		}
		
		public GridBox(Element node)
		{
			id=null;
			nr=null;
			type=null;
			
			if(((Element)node).hasAttribute(GRID_ID)){
				setId(String.valueOf(((Element)node).getAttribute(GRID_ID)));
			}
			
			if(((Element) node).hasAttribute(GRID_NR)){
				setNr(Integer.valueOf(
						((Element) node).getAttribute(GRID_NR)));
			}
			if(((Element)node).hasAttribute(GRID_TYPE)){
				setType(String.valueOf(((Element)node).getAttribute(GRID_TYPE)));
			}
		}

		
		
		/**
		 * @return the id
		 */
		public String getId() {
			return id!=null ? id : "";
		}



		/**
		 * @param id the id to set
		 */
		public void setId(String id) {
			this.id = id;
		}


		/**
		 * @return the sizeY
		 */
		public Integer getNr() {
			return nr;
		}

		/**
		 * @param sizeY the sizeY to set
		 */
		public void setNr(Integer nr) {
			this.nr = nr;
		}

		/**
		 * @return the type
		 */
		public String getType() {
			return type!=null ? type : "";
		}

		/**
		 * @param type the type to set
		 */
		public void setType(String type) {
			this.type = type;
		}
		
		public final static String getGridBoxTagName()
		{
			return GRID;
		}
		
		private String toXMLAnnotation()
		{
			StringBuffer xml=new StringBuffer("<"+GRID);
			
			
			if(id!=null){
				xml.append(" "+GRID_ID+"=\"");
				xml.append(id);
				xml.append("\"");
			}
			
			if(nr>0){
				xml.append(" "+GRID_NR+"=\"");
				xml.append(String.valueOf(nr));
				xml.append("\"");
			}
			if(type!=null){
				xml.append(" "+GRID_TYPE+"=\"");
				xml.append(type);
				xml.append("\"");
			}
			
			xml.append("/>");
			
			return xml.toString();
		}
		
	}



	



	



	



	
}
