package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.microscope.hardware;

import java.util.ArrayList;
import java.util.List;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.ModuleConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.microscope.MetaDataUI;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.microscope.MicroscopeProperties;
import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.microscope.MetaDataUI.GUIPlaceholder;

import ome.model.units.UNITS;
import ome.units.quantity.Frequency;
import ome.units.quantity.Length;
import ome.units.quantity.Power;
import ome.xml.model.Arc;
import ome.xml.model.Detector;
import ome.xml.model.Dichroic;
import ome.xml.model.Filament;
import ome.xml.model.Filter;
import ome.xml.model.FilterSet;
import ome.xml.model.Laser;
import ome.xml.model.LightSource;
import ome.xml.model.Objective;
import ome.xml.model.TransmittanceRange;
import ome.xml.model.enums.ArcType;
import ome.xml.model.enums.Correction;
import ome.xml.model.enums.DetectorType;
import ome.xml.model.enums.FilamentType;
import ome.xml.model.enums.FilterType;
import ome.xml.model.enums.Immersion;
import ome.xml.model.enums.LaserMedium;
import ome.xml.model.enums.LaserType;
import ome.xml.model.enums.Pulse;
import ome.xml.model.enums.UnitsFrequency;
import ome.xml.model.enums.UnitsLength;
import ome.xml.model.enums.UnitsPower;
import ome.xml.model.enums.handlers.UnitsFrequencyEnumHandler;
import ome.xml.model.enums.handlers.UnitsLengthEnumHandler;
import ome.xml.model.enums.handlers.UnitsPowerEnumHandler;

public class OlympusTIRF4Line_SMT extends MicroscopeProperties{
	public OlympusTIRF4Line_SMT()
	{
		detectors=this.getMicDetectorList();
		objectives=this.getMicObjectiveList();
		lightSources=this.getMicLightSrcList();
		lightPathObjects=this.getMicLightPathFilterList();
	}
	
	

	@Override
	public List<LightSource> getMicLightSrcList() {
		List<LightSource> list=new ArrayList<>();
		
		Laser l=new Laser();
//		l.setModel("");
//		l.setManufacturer("");
//		l.setType(LaserType.SOLIDSTATE);
//		l.setLaserMedium(LaserMedium.OTHER);
		l.setWavelength(new Length(405, UnitsLengthEnumHandler.getBaseUnit(UnitsLength.NM)));
//		l.setPulse(Pulse.CW);
		l.setPower(new Power(100, UnitsPowerEnumHandler.getBaseUnit(UnitsPower.MW)));
		list.add(l);
		
		l=new Laser();
//		l.setModel("LuxX 488-200");
//		l.setManufacturer("Omicron");
		l.setType(LaserType.SOLIDSTATE);
//		l.setLaserMedium(LaserMedium.OTHER);//??
		l.setWavelength(new Length(488, UnitsLengthEnumHandler.getBaseUnit(UnitsLength.NM)));
//		l.setPulse(Pulse.CW);//??
		l.setPower(new Power(150, UnitsPowerEnumHandler.getBaseUnit(UnitsPower.MW)));
		list.add(l);
		
		l=new Laser();
//		l.setModel("Cobolt Samba 532");
//		l.setManufacturer("Cobolt");
		l.setType(LaserType.SOLIDSTATE);
//		l.setLaserMedium(LaserMedium.OTHER);//??
		l.setWavelength(new Length(561, UnitsLengthEnumHandler.getBaseUnit(UnitsLength.NM)));
//		l.setPulse(Pulse.CW);//??
		l.setPower(new Power(150, UnitsPowerEnumHandler.getBaseUnit(UnitsPower.MW)));
		list.add(l);
		
		l=new Laser();
//		l.setModel("Cobolt Jive 561");
//		l.setManufacturer("Cobolt");
//		l.setType(LaserType.SOLIDSTATE);
//		l.setLaserMedium(LaserMedium.OTHER);//??
		l.setWavelength(new Length(640, UnitsLengthEnumHandler.getBaseUnit(UnitsLength.NM)));
//		l.setPulse(Pulse.CW);//??
		l.setPower(new Power(140, UnitsPowerEnumHandler.getBaseUnit(UnitsPower.MW)));
		list.add(l);
		
		
		
		return list;
	}



	@Override
	public List<Object> getMicLightPathFilterList() {
List<Object> list = new ArrayList<Object>();
		
		// TODO:
		//- cutIn, CutOut richtig?
		//-polarizer?		
		
		Dichroic d1= new Dichroic();
		d1.setModel("zt405/488/561/640rpc");
		
		FilterSet fs1= new FilterSet();
		fs1.setModel("Pos.1: quadcolor(blue, green, orange, red)");
		fs1.linkDichroic(d1);
		fs1.linkEmissionFilter(
				getFilter("BrightLine HC 446/523/500/677",FilterType.BANDPASS,446,677,UnitsLength.NM));
		list.add(fs1);
		
		
		
		fs1= new FilterSet();
		fs1.setModel("Pos.6: IX2-MDICT");
		list.add(fs1);
		
		
		
		fs1= new FilterSet();
		fs1.setModel("Emmision Filter Wheel: Pos.1: blue (DAPI,BFP)");		
		fs1.linkEmissionFilter(
				getFilter("BrightLine HC 444/45",FilterType.BANDPASS,423,468,UnitsLength.NM));
		list.add(fs1);
		
		
		fs1= new FilterSet();
		fs1.setModel("Emmision Filter Wheel: Pos.2: green (GFP)");		
		fs1.linkEmissionFilter(
				getFilter("BrightLine HC 525/50",FilterType.BANDPASS,500,550,UnitsLength.NM));
		list.add(fs1);
		
		
		fs1= new FilterSet();
		fs1.setModel("Emmision Filter Wheel: Pos.3: orange (TMR, mCherry)");
		fs1.linkEmissionFilter(
				getFilter("BrightLine HC 600/37",FilterType.BANDPASS,582,619,UnitsLength.NM));
		list.add(fs1);
		
		fs1= new FilterSet();
		fs1.setModel("Emmision Filter Wheel: Pos.4: red (Cy5,Atto655)");
		fs1.linkEmissionFilter(
				getFilter("BrightLine HC 697/58",FilterType.BANDPASS,668,726,UnitsLength.NM));
		list.add(fs1);
		
		
		
		
		
		fs1= new FilterSet();
		fs1.setModel("Cube :blue, green, yellow/orange, red");
		//TODO
		fs1.linkExcitationFilter(
				getFilter("Beamsplitter 480dcxr",FilterType.LONGPASS,480,-1,UnitsLength.NM));
		fs1.linkExcitationFilter(
				getFilter("Beamsplitter 565dcxr",FilterType.LONGPASS,565,-1,UnitsLength.NM));
		fs1.linkExcitationFilter(
				getFilter("Beamsplitter 640dcxr",FilterType.LONGPASS,640,-1,UnitsLength.NM));
		
		fs1.linkEmissionFilter(
				getFilter("BrightLine HC 438/24 (blue:DAPI,BFP)",FilterType.BANDPASS,426,450,UnitsLength.NM));
		fs1.linkEmissionFilter(
				getFilter("BrightLine HC 520/35 (green: GFP)",FilterType.BANDPASS,502,538,UnitsLength.NM));
		fs1.linkEmissionFilter(
				getFilter("BrightLine HC 600/37 (orange: TMR,mCherry)",FilterType.BANDPASS,582,619,UnitsLength.NM));
		fs1.linkEmissionFilter(
				getFilter("BrightLine HC 685/40 (red: Cy5,Atto 655)",FilterType.BANDPASS,665,705,UnitsLength.NM));
		list.add(fs1);
		
		
		
		return list;
	}



	@Override
	protected List<Detector> getMicDetectorList() {
		
		List<Detector> list=new ArrayList<Detector>();
		
		Detector d=new Detector();
		d.setModel("Hamamatsu ORCA-Flash4.0");
		d.setManufacturer("Hamamatsu");
		d.setType(DetectorType.CCD);
		list.add(d);
		
		
		
		return list;
	}



	@Override
	protected List<Objective> getMicObjectiveList() {
		List<Objective> list=new ArrayList<>();
		
		Objective o=new Objective();
		o.setModel("UPLAFLN 20x");
		o.setManufacturer("Olympus");
		o.setNominalMagnification(20.0);
		o.setCalibratedMagnification(20.0);
		o.setLensNA(0.75);
		o.setImmersion(Immersion.AIR);
//		o.setCorrection(Correction.PLANAPO);
//		o.setWorkingDistance(new Length(600, UnitsLengthEnumHandler.getBaseUnit(UnitsLength.MICROM)));
		list.add(o);

		o=new Objective();
		o.setModel("PLAPON 60x");
		o.setManufacturer("Olympus");
		o.setNominalMagnification(60.0);
		o.setCalibratedMagnification(60.0);
		o.setLensNA(1.49);
		o.setImmersion(Immersion.OIL);
//		o.setCorrection(Correction.PLANFLUOR);
		o.setWorkingDistance(new Length(100, UnitsLengthEnumHandler.getBaseUnit(UnitsLength.MICROM)));
		list.add(o);
		
		o=new Objective();
		o.setModel("UAPON OTIRF 100x / 1,49");
		o.setManufacturer("Olympus");
		o.setNominalMagnification(100.0);
		o.setCalibratedMagnification(100.0);
		o.setLensNA(1.49);
		o.setImmersion(Immersion.OIL);
//		o.setCorrection(Correction.PLANAPO);
		o.setWorkingDistance(new Length(100, UnitsLengthEnumHandler.getBaseUnit(UnitsLength.MICROM)));
		list.add(o);

		o=new Objective();
		o.setModel("UAPO OTIRFM 150x / 1,45");
		o.setManufacturer("Olympus");
		o.setNominalMagnification(150.0);
		o.setCalibratedMagnification(150.0);
		o.setLensNA(1.45);
		o.setImmersion(Immersion.OIL);
//		o.setCorrection(Correction.PLANAPO);
		o.setWorkingDistance(new Length(80, UnitsLengthEnumHandler.getBaseUnit(UnitsLength.MICROM)));
		list.add(o);
		
		
		return list;
	}
	
	@Override
	public ModuleConfiguration loadSampleConf(boolean active, GUIPlaceholder pos, String width) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ModuleConfiguration loadLightSrcConf(boolean active, GUIPlaceholder pos, String width) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ModuleConfiguration loadImageEnvConf(boolean active, GUIPlaceholder pos, String width) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ModuleConfiguration loadExperimentConf(boolean active, GUIPlaceholder pos, String width) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ModuleConfiguration loadDetectorConf(boolean active, GUIPlaceholder pos, String width) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ModuleConfiguration loadObjectiveConf(boolean active, GUIPlaceholder pos, String width) {
		// TODO Auto-generated method stub
		return null;
	}

}
