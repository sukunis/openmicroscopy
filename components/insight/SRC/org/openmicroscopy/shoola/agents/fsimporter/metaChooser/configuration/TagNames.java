package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration;

import org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration.util.PreTagData;

import ome.units.UNITS;
import ome.units.quantity.Angle;
import ome.units.quantity.ElectricPotential;
import ome.units.quantity.Frequency;
import ome.units.quantity.Length;
import ome.units.quantity.Power;
import ome.units.quantity.Pressure;
import ome.units.quantity.Temperature;
import ome.units.quantity.Time;
import ome.units.unit.Unit;
import ome.xml.model.primitives.PercentFraction;

public class TagNames 
{
	public static final String[] BOOLEAN_COMBO = {"true","false"};
	
	public static final String MODEL="Model";
	public static final String MANUFAC="Manufacturer";
	public static final String TYPE="Type";
	public static final String DESC="Description";
	
	//OBJECTIVE
//	private final String L_MODEL="Model";
//	private final String L_MANUFAC="Manufacturer";
	public static final String NOMMAGN="Nominal Magnification";
	public static final String CALMAGN="Calibration Magnification";
	public static final String LENSNA="Lens NA";
	public static final String IMMERSION="Immersion";
	public static final String CORRECTION="Correction";
	public static final String WORKDIST="Working Distance";
	
	public static final Unit<Length> WORKDIST_UNIT=UNITS.MICROM;
	
	//Objective Settings
	public static final String CORCOLLAR="Correction Collar";
	public static final String OBJ_MEDIUM="Medium";
	public static final String REFINDEX="Refraction Index";
	
	private static PreTagData[] objectiveTags={new PreTagData(MODEL,null, ""),new PreTagData(MANUFAC,null, ""),new PreTagData(NOMMAGN,null, ""),
			new PreTagData(CALMAGN,null, ""),new PreTagData(LENSNA,null, ""),new PreTagData(IMMERSION,null, ""),new PreTagData(CORRECTION,null, ""),
			new PreTagData(WORKDIST,WORKDIST_UNIT, ""),new PreTagData(CORCOLLAR,null, "S"),new PreTagData(OBJ_MEDIUM,null, "S"),
			new PreTagData(REFINDEX,null, "S")};
	
	
	//CHANNEL
	public static final String CH_NAME="Name";
	public static final String COLOR="Color";
	public static final String FLUOROPHORE="Fluorophore";
	public static final String ILLUMTYPE="Illumination Type";
	public static final String EXPOSURETIME="Exposure Time";
	public static final String EXCITWAVELENGTH="Excitation Wavelength";
	public static final String EMISSIONWAVELENGTH="Emission Wavelength";
	public static final String IMAGINGMODE="Imaging Mode";
	public static final String ILLUMINATIONMODE="Illumination Mode";
	public static final String CONTRASTMETHOD="Contrast Method";
	public static final String NDFILTER="ND Filter";
	public static final String PINHOLESIZE="Pinhole Size";
	
	public static final Unit<Length> PINHOLESIZE_UNIT=UNITS.MICROM;
	public static final Unit<Length> EXCITATIONWL_UNIT=UNITS.NM;
	public static final Unit<Length> EMISSIONWL_UNIT=UNITS.NM;
	public static final Unit<Time> EXPOSURETIME_UNIT=UNITS.S;
	
	private static PreTagData[] channelTags={
		new PreTagData(CH_NAME,null, ""),new PreTagData(COLOR,null, ""),new PreTagData(FLUOROPHORE,null, ""),
		new PreTagData(ILLUMTYPE,null, ""),new PreTagData(EXPOSURETIME,EXPOSURETIME_UNIT, ""),new PreTagData(EXCITWAVELENGTH,null, ""),
		new PreTagData(EMISSIONWAVELENGTH,EXCITATIONWL_UNIT, ""),
		new PreTagData(IMAGINGMODE,null, ""),new PreTagData(ILLUMINATIONMODE,null, ""),new PreTagData(CONTRASTMETHOD,null, ""),
		new PreTagData(NDFILTER,null, ""),new PreTagData(PINHOLESIZE,PINHOLESIZE_UNIT, "")};
	
	//Detector
//	private final String L_MODEL="Model";
//	private final String L_MANUFAC="Manufacturer";
//	private final String L_TYPE="Type";
	public static final String ZOOM="Zoom";
	public static final String AMPLGAIN="AmplificationGain";
	
	//DetectorSettings
	public static final String GAIN="Set Gain";
	public static final String VOLTAGE="Set Voltage";
	public static final String OFFSET="Set Offset";
	public static final String CONFZOOM="Confocal Zoom";
	public static final String BINNING="Binning";
	public static final String SUBARRAY="Subarray";
	
	public static final Unit<ElectricPotential> VOLTAGE_UNIT=UNITS.V;
	
	private static PreTagData[] detectorTags={new PreTagData(MODEL,null, ""),new PreTagData(MANUFAC,null, ""),new PreTagData(TYPE,null, ""),
		new PreTagData(ZOOM,null, ""),new PreTagData(AMPLGAIN,null, ""),new PreTagData(GAIN,null, "S"),new PreTagData(VOLTAGE,VOLTAGE_UNIT, "S"),
		new PreTagData(OFFSET,null, "S"),new PreTagData(CONFZOOM,null, "S"),new PreTagData(BINNING,null, "S"),
		new PreTagData(SUBARRAY,null, "S")};
	
	//Experiment
//	private final String TYPE="Type";
//	private final String DESC="Description";
	public static final String EXPNAME="Experimenter Name";
	
	public static final String PROJECTNAME="Project Name";
	public static final String GROUP="Group";
	public static final String PROJECTPARTNER="Project Partner";
	
	private static PreTagData[] experimentTags={new PreTagData(TYPE,null, ""),new PreTagData(DESC,null, ""),new PreTagData(EXPNAME,null, ""),
		new PreTagData(PROJECTNAME,null, ""),new PreTagData(GROUP,null, ""),new PreTagData(PROJECTPARTNER,null, "")};
	
	//Image
	public static final String IMG_NAME="Name";
	public static final String ACQTIME="Acquisition Time";
	public static final String DIMXY="Dim X x Y";
	public static final String PIXELTYPE="Pixel Depth";
	public static final String PIXELSIZE="Pixel Size (XY)";
	public static final String DIMZTC="Dim Z x T x C";
	public static final String STAGEPOS="Stage Position (XY)";
	public static final String STEPSIZE="Step Size";
	public static final String TIMEINC="Time Increment";
	public static final String WELLNR="Well #";
	
	public static final Unit<Length> STAGEPOS_UNIT=UNITS.REFERENCEFRAME;
	public static final Unit<Length> PIXELSIZE_UNIT=UNITS.MICROM;
	public static final Unit<Time> TIMEINC_UNIT=UNITS.S;
	
	private static PreTagData[] imageTags={new PreTagData(IMG_NAME,null, ""),new PreTagData(ACQTIME,null, ""),new PreTagData(DIMXY,null, ""),
		new PreTagData(PIXELTYPE,null, ""),new PreTagData(PIXELSIZE,PIXELSIZE_UNIT, ""),new PreTagData(DIMZTC,null, ""),new PreTagData(STAGEPOS,STAGEPOS_UNIT, ""),
		new PreTagData(STEPSIZE,null, ""),new PreTagData(TIMEINC,TIMEINC_UNIT, ""),new PreTagData(WELLNR,null, "")};
	
	//ImageEnv
	public static final String TEMP="Temperature";
	public static final String AIRPRESS="Air Pressure";
	public static final String HUMIDITY="Humidity";
	public static final String CO2="CO2 Percent";
	
	public static final Unit<Temperature> TEMPERATURE_UNIT=UNITS.DEGREEC;
	public static final Unit<Pressure> PRESSURE_UNIT=UNITS.MBAR;
	public static final Unit PERCENT_UNIT=ome.units.unit.Unit.CreateBaseUnit("SI.PERCENT", "%");
	
	
	
	//LightSrc
//	private final String L_MODEL ="Model";
//	private final String L_MANUFAC="Manufacturer";
	public static final String POWER="Power";
	
	public static final String L_TYPE="L_Type";
	public static final String A_TYPE="A_Type";
	public static final String F_TYPE="F_Type";
	public static final String GES_TYPE="GES_Type";
	public static final String LED_TYPE="LED_Type";
	
	public static final String MEDIUM="Medium";
	public static final String FREQMUL="Frequency Multiplication";
	public static final String TUNABLE="Tunable";
	public static final String PULSE="Pulse";
	public static final String POCKELCELL="Pockel Cell";
	public static final String REPRATE="Repititation Rate";
	public static final String PUMP="Pump";
	public static final String WAVELENGTH="Wavelength";
	
//	private final String L_DESC="Description";
	public static final String MAP="Map";
	
	public static final Unit<Frequency> REPRATE_UNIT_HZ= UNITS.HZ;
	public static final Unit<Frequency> REPRATE_UNIT_MHZ= UNITS.MHZ;
	public static final Unit<Length> WAVELENGTH_UNIT=UNITS.NM;
	public static final Unit<Power> POWER_UNIT=UNITS.MW;
	
	
	//LightSrcSettings
	public static final String SET_WAVELENGTH="Set Wavelength";
	public static final String ATTENUATION="Attenuation";
	
	private static PreTagData[] lightSrcTags={new PreTagData(MODEL,null, ""),new PreTagData(MANUFAC,null, ""),new PreTagData(POWER,POWER_UNIT, ""),
			new PreTagData(L_TYPE,null, ""),new PreTagData(A_TYPE,null, ""),new PreTagData(F_TYPE,null, ""),new PreTagData(MEDIUM,null, ""),
			new PreTagData(FREQMUL,null, ""),new PreTagData(TUNABLE,null, ""),new PreTagData(PULSE,null, ""),new PreTagData(POCKELCELL,null, ""),
			new PreTagData(REPRATE,REPRATE_UNIT_HZ, ""),new PreTagData(PUMP,null, ""),new PreTagData(WAVELENGTH,WAVELENGTH_UNIT, ""),new PreTagData(MAP,null, ""),
			new PreTagData(SET_WAVELENGTH,WAVELENGTH_UNIT, "S"),new PreTagData(ATTENUATION,null, "S")};
	
	//Sample
	public static final String PREPDATE="Prep Date";
	public static final String PREPDESC="Prep Description";
	public static final String RAWCODE="Code";
	public static final String RAWDESC="Description";
	public static final String GRIDBOXNR="Gridbox Nr";
	public static final String GRIDBOXTYPE="Gridbox Type";
	public static final String EXPGRID="Grid (XY)";
	public static final String EXPOBJNR="Observed Object Nr";
	public static final String EXPOBJTYPE="Observed Object Type";
	
	private static PreTagData[] sampleTags={new PreTagData(PREPDATE,null, ""),new PreTagData(PREPDESC,null, ""),new PreTagData(RAWCODE,null, ""),
		new PreTagData(RAWDESC,null, ""),new PreTagData(GRIDBOXNR,null, ""),new PreTagData(GRIDBOXTYPE,null, ""),new PreTagData(EXPGRID,null, ""),
		new PreTagData(EXPOBJNR,null, ""),new PreTagData(EXPOBJTYPE,null, "")};
	
	//LightPath
	public static final String FILTER = "Filter";
	public static final String FILTER_CLASS="Class";
	public static final String FILTER_CLASS_EM="Emission Filter";
	public static final String FILTER_CLASS_EX="Exitation Filter";
	public static final String FILTER_CLASS_D="Dichroic";
	
//	public static final String MODEL="Model";
//	public static final String MANUFAC="Manufacturer";
//	public static final String TYPE="Type";
	public static final String FILTERWHEEL="Filterwheel";
	
	//Plane
	public static final String DELTA_T="Delta T";
	public static final String STAGE_POS_X="Stage Pos X";
	public static final String STAGE_POS_Y="Stage Pos Y";
	public static final String STAGE_POS_Z="Stage Pos Z";
	
	public static final Unit<Time> DELTA_T_UNIT=UNITS.S;
	public static final Unit<Length> STAGE_POS_X_UNIT=UNITS.REFERENCEFRAME;
	public static final Unit<Length> STAGE_POS_Y_UNIT=UNITS.REFERENCEFRAME;
	public static final Unit<Length> STAGE_POS_Z_UNIT=UNITS.REFERENCEFRAME;
	
	
	public static PreTagData[] getDetectorTags()
	{
		return detectorTags;
	}
	
	public static PreTagData[] getSampleTags() {
		return sampleTags;
	}

	

	public static PreTagData[] getLightSrcTags()
	{
		return lightSrcTags;
	}


	public static PreTagData[] getObjectiveTags() {
		return objectiveTags;
	}

	public static PreTagData[] getImageTags() {
		return imageTags;
	}


	public static PreTagData[] getExperimentTags() {
		return experimentTags;
	}


	public static PreTagData[] getChannelTags() {
		return channelTags;
	}

	
}
