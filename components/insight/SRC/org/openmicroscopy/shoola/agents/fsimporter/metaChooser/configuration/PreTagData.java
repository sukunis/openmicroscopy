package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration;

import ome.units.unit.Unit;

public class PreTagData {

		String name;
		Unit defaultUnit;
		String[] unitsList;
		String settings;
		
		public PreTagData(String name, Unit defaultUnit, String isSett)
		{
			this.name=name;
			this.defaultUnit=defaultUnit;
			this.settings=isSett;
			if(defaultUnit!=null){
				unitsList=UOSHardwareReader.getUnits(name);
			}
		}
	}

