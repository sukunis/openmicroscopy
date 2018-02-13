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

public class OlympusTIRF4Line_STORM extends MicroscopeProperties
{
	public OlympusTIRF4Line_STORM()
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
		l.setModel("BCL-100-405");
		l.setManufacturer("CrystaLaser");
		l.setType(LaserType.SOLIDSTATE);
		l.setLaserMedium(LaserMedium.OTHER);
		l.setWavelength(new Length(405, UnitsLengthEnumHandler.getBaseUnit(UnitsLength.NM)));
		l.setPulse(Pulse.CW);
		l.setPower(new Power(100, UnitsPowerEnumHandler.getBaseUnit(UnitsPower.MW)));
		list.add(l);
		
		l=new Laser();
		l.setModel("LuxX 488-200");
		l.setManufacturer("Omicron");
		l.setType(LaserType.SEMICONDUCTOR);//??
		l.setLaserMedium(LaserMedium.OTHER);//??
		l.setWavelength(new Length(488, UnitsLengthEnumHandler.getBaseUnit(UnitsLength.NM)));
		l.setPulse(Pulse.CW);//??
		l.setPower(new Power(200, UnitsPowerEnumHandler.getBaseUnit(UnitsPower.MW)));
		list.add(l);
		
		l=new Laser();
		l.setModel("Cobolt Samba 532");
		l.setManufacturer("Cobolt");
		l.setType(LaserType.SOLIDSTATE);
		l.setLaserMedium(LaserMedium.OTHER);//??
		l.setWavelength(new Length(532, UnitsLengthEnumHandler.getBaseUnit(UnitsLength.NM)));
		l.setPulse(Pulse.CW);//??
		l.setPower(new Power(150, UnitsPowerEnumHandler.getBaseUnit(UnitsPower.MW)));
		list.add(l);
		
		l=new Laser();
		l.setModel("Cobolt Jive 561");
		l.setManufacturer("Cobolt");
		l.setType(LaserType.SOLIDSTATE);
		l.setLaserMedium(LaserMedium.OTHER);//??
		l.setWavelength(new Length(561, UnitsLengthEnumHandler.getBaseUnit(UnitsLength.NM)));
		l.setPulse(Pulse.CW);//??
		l.setPower(new Power(200, UnitsPowerEnumHandler.getBaseUnit(UnitsPower.MW)));
		list.add(l);
		
		l=new Laser();
		l.setModel("Luxx 642-140");
		l.setManufacturer("Omicron");
		l.setType(LaserType.OTHER);//??
		l.setLaserMedium(LaserMedium.OTHER);//??
		l.setWavelength(new Length(642, UnitsLengthEnumHandler.getBaseUnit(UnitsLength.NM)));
		l.setPulse(Pulse.CW);//??
		l.setPower(new Power(140, UnitsPowerEnumHandler.getBaseUnit(UnitsPower.MW)));
		list.add(l);
		
		l=new Laser();
		l.setModel("iFLEX2000");
		l.setManufacturer("Qiotiq");
		l.setType(LaserType.OTHER);//??
		l.setLaserMedium(LaserMedium.OTHER);//??
		l.setWavelength(new Length(730, UnitsLengthEnumHandler.getBaseUnit(UnitsLength.NM)));
		l.setPulse(Pulse.CW);//??
		l.setPower(new Power(40, UnitsPowerEnumHandler.getBaseUnit(UnitsPower.MW)));
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
		d1.setModel("488nm");
		
		FilterSet fs1= new FilterSet();
		fs1.setModel("Pos.1");
		//TODO
//		fs1.linkExcitationFilter(
//				getFilter("No Filter",FilterType.OTHER,488,-1,UnitsLength.NM));
		fs1.linkDichroic(d1);
		fs1.linkEmissionFilter(
				getFilter("HQ 570/30",FilterType.BANDPASS,555,585,UnitsLength.NM));
		list.add(fs1);
		
		
		
		d1= new Dichroic();
		d1.setModel("zt405/488/561/640/730rpc");
		d1.setManufacturer("Semrock");
		
		fs1= new FilterSet();
		fs1.setModel("Pos.3: pentacolor(bkue, green, yellow/orange,red, dark red");
		//TODO
//		fs1.linkExcitationFilter(
//				getFilter("No Filter",FilterType.OTHER,488,-1,UnitsLength.NM));
		fs1.linkDichroic(d1);
		fs1.linkEmissionFilter(
				getFilter("BrightLine HC 440/521/607/694/809",FilterType.BANDPASS,440,809,UnitsLength.NM));
		list.add(fs1);
		
		
		
		d1= new Dichroic();
		d1.setModel("zt405/488/561/640rpc");
		
		fs1= new FilterSet();
		fs1.setModel("Pos.4: quadcolor (blue,green,yellow/orange,red)");
		//TODO
		fs1.linkExcitationFilter(
				getFilter("BrightLine HC 390/482/563/640",FilterType.BANDPASS,390,640,UnitsLength.NM));
		fs1.linkDichroic(d1);
		fs1.linkEmissionFilter(
				getFilter("BrightLine HC 446/523/500/677",FilterType.BANDPASS,446,677,UnitsLength.NM));
		list.add(fs1);
		
		
		
		d1= new Dichroic();
		d1.setModel("HC BS R488/561");
		
		fs1= new FilterSet();
		fs1.setModel("Pos.5: dualcolor (green, orange)");
		//TODO
		fs1.linkExcitationFilter(
				getFilter("BrightLine HC 482/563",FilterType.BANDPASS,482,563,UnitsLength.NM));
		fs1.linkDichroic(d1);
		fs1.linkEmissionFilter(
				getFilter("BrightLine HC 523/610",FilterType.BANDPASS,523,610,UnitsLength.NM));
		list.add(fs1);
		
		
		d1= new Dichroic();
		d1.setModel("zt532/642rpc");
		
		 fs1= new FilterSet();
		fs1.setModel("Pos.6: Cy3 + Cy5");
		//TODO
		fs1.linkExcitationFilter(
				getFilter("HC 527/645",FilterType.BANDPASS,527,645,UnitsLength.NM));
		fs1.linkDichroic(d1);
		fs1.linkEmissionFilter(
				getFilter("EdgeBasic LP 532",FilterType.LONGPASS,532,-1,UnitsLength.NM));
		fs1.linkEmissionFilter(
				getFilter("ZET647NF",FilterType.OTHER,647,-1,UnitsLength.NM));
		list.add(fs1);
		
		
		
		fs1= new FilterSet();
		fs1.setModel("Cube 1:blue, green, yellow/orange, red");
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
		
		
		
		fs1= new FilterSet();
		fs1.setModel("Cube 2:green, green-orange, red, dark red");
		//TODO
		fs1.linkExcitationFilter(
				getFilter("Beamsplitter T565LPXR",FilterType.LONGPASS,565,-1,UnitsLength.NM));
		fs1.linkExcitationFilter(
				getFilter("Beamsplitter 630DCXR",FilterType.LONGPASS,630,-1,UnitsLength.NM));
		fs1.linkExcitationFilter(
				getFilter("Beamsplitter 735DCXR",FilterType.LONGPASS,735,-1,UnitsLength.NM));
		
		
		fs1.linkEmissionFilter(
				getFilter("BrightLine HC 520/35 (green: GFP, Cy2)",FilterType.BANDPASS,502,538,UnitsLength.NM));
		fs1.linkEmissionFilter(
				getFilter("ET 600/50 (green-orange: TMR,Cy3)",FilterType.BANDPASS,544,620,UnitsLength.NM));
		fs1.linkEmissionFilter(
				getFilter("ET 685/50 (red: Cy5,Atto 655)",FilterType.BANDPASS,660,710,UnitsLength.NM));
		fs1.linkEmissionFilter(
				getFilter("BrightLine HC 809/81 (dark-red: Cy7)",FilterType.BANDPASS,769,850,UnitsLength.NM));
		list.add(fs1);
		
		
		
		return list;
	}



	@Override
	protected List<Detector> getMicDetectorList() {
		
		List<Detector> list=new ArrayList<Detector>();
		
		Detector d=new Detector();
		d.setModel("Andor iXon Ultra 897");
		d.setManufacturer("Andor");
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
		o.setLensNA(0.5);
		o.setImmersion(Immersion.AIR);
//		o.setCorrection(Correction.PLANAPO);
		o.setWorkingDistance(new Length(2100, UnitsLengthEnumHandler.getBaseUnit(UnitsLength.MICROM)));
		list.add(o);

		o=new Objective();
		o.setModel("PLAPON 60x");
		o.setManufacturer("Olympus");
		o.setNominalMagnification(60.0);
		o.setCalibratedMagnification(60.0);
		o.setLensNA(1.42);
		o.setImmersion(Immersion.OIL);
//		o.setCorrection(Correction.PLANFLUOR);
		o.setWorkingDistance(new Length(150, UnitsLengthEnumHandler.getBaseUnit(UnitsLength.MICROM)));
		list.add(o);

		o=new Objective();
		o.setModel("UAPON 150x TIRF");
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
	
	
}
