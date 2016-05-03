package org.openmicroscopy.shoola.agents.fsimporter.metaChooser;

import java.io.File;

import javax.swing.tree.DefaultMutableTreeNode;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.components.MetaDataModel;
import org.openmicroscopy.shoola.env.data.model.ImportableFile;

public class FNode extends DefaultMutableTreeNode
{
	private ImportUserData importData;
	private MetaDataModel model;
	private ImportableFile iFile;
	
	public FNode(File file){
		this.importData=null;
		iFile=null;
		setUserObject(file);
	}
	
	public FNode(File file,ImportUserData importData,ImportableFile iFile ){
		this.importData=importData;
		this.iFile=iFile;
		setUserObject(file);
	}
	
	public File getFile(){
		return (File) getUserObject();
	}
	
	public ImportableFile getImportableFile()
	{
		return iFile;
	}
	
	public boolean isLeaf(){
		return !getFile().isDirectory();
	}
	
	public String toString() {
		String ad="";
		if(importData!=null){
			ad=" [Group: "+importData.getGroup()+", Project: "+
					importData.getProject()+"]";
		}
        return getFile().getName()+ad;
    } 
	
	public String getAbsolutePath(){
		return getFile().getAbsolutePath();
	}
	public boolean getAllowsChildren() {
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
	
	public void setModel(MetaDataModel model)
	{
		this.model=model;
	}
	
	public MetaDataModel getModel()
	{
		return model;
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
