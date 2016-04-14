package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.microscope;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.microscope.CustomViewProperties.MicSubmodule;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.microscope.MetaDataUI.GUIPlaceholder;

public class Submodule 
{
	private MicSubmodule module;
	private GUIPlaceholder position;
	private int width;
	
	public Submodule(MicSubmodule module, GUIPlaceholder position, int width)
	{
		this.module=module;
		this.position=position;
		this.width=width;
	}
	
	public MicSubmodule getModule() {
		return module;
	}
	public GUIPlaceholder getPosition() {
		return position;
	}
	public int getWidth() {
		return width;
	}
	
}
