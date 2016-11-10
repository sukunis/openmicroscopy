package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.util;

import java.io.File;

import javax.swing.tree.DefaultMutableTreeNode;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.MetaDataModel;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.MetaDataModelObject;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.microscope.MetaDataView;
import org.openmicroscopy.shoola.env.data.model.ImportableFile;

public class FNode extends DefaultMutableTreeNode
{
	private ImportUserData importData;
	private MetaDataModelObject modelObj;
	private ImportableFile iFile;
	private MetaDataView view;
	
	private Boolean saved;
	
	public FNode(File file){
		this.importData=null;
		iFile=null;
		setUserObject(file);
		saved=false;
	}
	public FNode(Object object)
	{
		this.importData=null;
		iFile=null;
		setUserObject(object);
		saved=false;
	}
	
	public FNode(File file,ImportUserData importData,ImportableFile iFile ){
		this.importData=importData;
		this.iFile=iFile;
		setUserObject(file);
	}
	
	public File getFile()
	{
		Object o=getUserObject();
		if(o instanceof File)
			return (File) o;
		else
			return null;
	}
	
	public ImportableFile getImportableFile()
	{
		return iFile;
	}
	
//	/**
//	 * Returns true if the node is a leaf or the root node.
//	 */
//	public boolean isLeaf()
//	{
//		File f=getFile();
//		boolean result=f!=null? !f.isDirectory() : true;
//		return result;
//	}
	
	public String toString() {
		String ad="";
		if(importData!=null){
			ad=" [Group: "+importData.getGroup()+", Project: "+
					importData.getProject()+"]";
		}
		if(getFile()==null){
			return (String)getUserObject();
		}
        return getFile().getName()+ad;
    } 
	
	public String getAbsolutePath()
	{
		if(getFile()==null)
			return null;
		
		return getFile().getAbsolutePath();
	}
	
	/**
	 * true if node is a directory or the root
	 */
	public boolean getAllowsChildren() {
		if(getFile()==null)
			return true;
		
		return getFile().isDirectory();
	} 
	
	public boolean hasImportData()
	{
		return (importData!=null);
	}
	
	public ImportUserData getImportData()
	{
		return importData;
	}
	
	public void setModelObject(MetaDataModelObject m)
	{
		this.modelObj=m;
	}
	
	public MetaDataModelObject getModelObject()
	{
		return modelObj;
	}
	
	public MetaDataModel getModelOfSeries(int index)
	{
		if(modelObj==null)
			return null;
		
		return modelObj.getList().get(index);
	}
	
	public boolean hasModelObject() {
		return modelObj!=null;
	}
	public MetaDataView getView() {
		return view;
	}
	public void setView(MetaDataView view) {
		this.view = view;
	}
	
	/**
	 * Save model if view exists.
	 */
	public void saveModel() 
	{
		if(view==null)
			return;

		modelObj = view.getSavedModelObject();
	}
	
//	public void addFiles(int showHidden) {
//		File[] files = getFile().listFiles();
//		for (File f : files) {
//			if (showHidden == ImportFileTree.SHOW_HIDDEN) {
//				if (f.isHidden())
//					this.add(new FNode(f));
//			} else if (showHidden == ImportFileTree.SHOW_VISIBLE) {
//				if (!f.isHidden())
//					this.add(new FNode(f));
//			} else {
//				this.add(new FNode(f));
//			}
//		}
//	} 

}
