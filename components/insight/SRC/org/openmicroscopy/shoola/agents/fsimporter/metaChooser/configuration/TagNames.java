package org.openmicroscopy.shoola.agents.fsimporter.metaChooser.configuration;

import ome.units.UNITS;
import ome.units.quantity.Frequency;
import ome.units.quantity.Length;
import ome.units.quantity.Power;
import ome.units.unit.Unit;

public class TagNames 
{
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
	
	//CHANNEL
	public static final String CH_NAME="Name";
	public static final String COLOR="Color";
	public static final String FLUOROPHORE="Fluorophore";
	public static final String ILLUMTYPE="Illumination Type";
	public static final String EXPOSURETIME="Exposure Time";
	public static final String EXCITWAVELENGTH="Excitation Wavelength";
	public static final String EMMISIONWAVELENGTH="Emission Wavelength";
	public static final String IMAGINGMODE="Imaging Mode";
	public static final String ILLUMINATIONMODE="Illumination Mode";
	public static final String CONTRASTMETHOD="Contrast Method";
	public static final String NDFILTER="ND Filter";
	
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
	
	//Experiment
//	private final String TYPE="Type";
//	private final String DESC="Description";
	public static final String EXPNAME="Experimenter Name";
	
	public static final String PROJECTNAME="Project Name";
	public static final String GROUP="Group";
	public static final String PROJECTPARTNER="Project Partner";
	
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
	
	//ImageEnv
	public static final String TEMP="Temperature";
	public static final String AIRPRESS="Air Pressure";
	public static final String HUMIDITY="Humidity";
	public static final String CO2="CO2 Percent";
	
	//LightSrc
//	private final String L_MODEL ="Model";
//	private final String L_MANUFAC="Manufacturer";
	public static final String POWER="Power";
	
	public static final String L_TYPE="L_Type";
	public static final String A_TYPE="A_Type";
	public static final String F_TYPE="F_Type";
	
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
	
}
