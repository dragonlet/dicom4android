/** DataElement.java - definition des marques DICOM existentes (DICOM 3.0)
 *
 * @author
 * @version $Revision: 0.2 $ $Date: 2010/03/19 23:04:18 $ $Author: andleg $
 *
 * Modification History
 * ---------------------
 * 01b,19Mar2010,andleg   change type of vrAT : Integer to Long
 * 01a,24Dec2009,andleg   written
 *
 */
/*This file is part of dicom4android.

    dicom4android is free software: you can redistribute it and/or modify
    it under the terms of the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.medcare.Dicom;

public class DataElement {

	private int indElementInGroup;
	private int tabGroup[];
	private String tabGroupString[];
	private String denominationGroup;

	// 0x10?? pour String
	// 0x20?? pour Integer
	// 0x50?? pour Long
	// 0x30?? pour Float
	// 0x40?? pour Sequence

	static final int retired_element = -1;
	static final int vrAE = 0x1000;
	static final int vrAS = 0x1001;
	static final int vrAT = 0x5001;
	static final int vrCS = 0x1002;
	static final int vrDA = 0x1003;
	static final int vrDS = 0x3000;
	static final int vrDT = 0x1004;
	static final int vrFL = 0x3001;
	static final int vrFD = 0x3002;
	static final int vrIS = 0x2001;
	static final int vrLO = 0x1005;
	static final int vrLT = 0x1006;
	static final int vrOB = 0x2002;
	static final int vrOW = 0x2003;
	static final int vrPN = 0x1007;
	static final int vrSH = 0x1008;
	static final int vrSL = 0x2004;
	static final int vrSQ = 0x4000;
	static final int vrSS = 0x2005;
	static final int vrST = 0x1009;
	static final int vrTM = 0x100a;
	static final int vrUI = 0x100b;
	static final int vrUL = 0x5000;
	static final int vrUS = 0x2006;
	static final int vrUSorSS = 0x2008;
	static final int vrOWorOB = 0x2009;

	// pour la troisieme case :
	// 0 >> VM multiple indefini.
	// 1..n >> Seule.
	// -1 pour implicite.

	static int[] gr0002 = { 0x0000, vrUL, 1, // Group Length
			0x0001, vrOB, 1, // Information Version
			0x0002, vrUI, 1, // SOP Class UID
			0x0003, vrUI, 1, // SOP Instance UID
			0x0010, vrUI, 1, // Transfer Syntax UID
			0x0012, vrUI, 1, // Implementation Class UID
			0x0013, vrSH, 1 // Implementation Version Name
	};

	static String[] gr0002String = { "Group Length", "Information Version",
			"SOP Class UID", "SOP Instance UID", "Transfer Syntax UID",
			"Implementation Class UID", "Implementation Version Name" };

	static int[] gr0008 = { 0x0000, vrUL, 1, // Group Length
			0x0001, retired_element, -1, // Length to End
			0x0005, vrCS, 1, // Specific character Set
			0x0008, vrCS, 0, // Image Type
			0x0010, retired_element, -1, // Recognition Code
			0x0012, vrDA, 1, // Instance Creation Date
			0x0013, vrTM, 1, // Instance Creation Time
			0x0014, vrUI, 1, // Instance Creator UID
			0x0016, vrUI, 1, // SOP Class UID
			0x0018, vrUI, 1, // SOP Instance UID
			0x0020, vrDA, 1, // Study date
			0x0021, vrDA, 1, // Series Date
			0x0022, vrDA, 1, // Acquisition Date
			0x0023, vrDA, 1, // Image Date
			0x0024, vrDA, 1, // Overlay Date
			0x0025, vrDA, 1, // Curve Date
			0x0030, vrTM, 1, // Study Time
			0x0031, vrTM, 1, // Series Time
			0x0032, vrTM, 1, // Acquisition Time
			0x0033, vrTM, 1, // Image Time
			0x0034, vrTM, 1, // Overlay Time
			0x0035, vrTM, 1, // Curve Time
			0x0040, retired_element, -1, // Data Set Type
			0x0041, retired_element, -1, // Data Set Subtype
			0x0042, vrCS, 1, // Nuclear Medecine Series Type
			0x0050, vrSH, 1, // Accession Number
			0x0052, vrCS, 1, // Query/retrieve Level
			0x0054, vrAE, 0, // Retrieve AE Title
			0x0058, vrUI, 0, // Failed SOP Instance UID List
			0x0060, vrCS, 1, // Modality
			0x0064, vrCS, 1, // Conversion Type
			0x0070, vrLO, 1, // Manufacturer
			0x0080, vrLO, 1, // Institution Name
			0x0081, vrST, 1, // Institution Address
			0x0082, vrSQ, 1, // Institution Code Sequence
			0x0090, vrPN, 1, // Reffering Physician's Name
			0x0092, vrST, 1, // Reffering Physician's Address
			0x0094, vrSH, 0, // Reffering Physician's Telephone Numbers
			0x0100, vrSH, 1, // Code Value
			0x0102, vrSH, 1, // Coding Scheme Designator
			0x0104, vrLO, 1, // Code meaning
			0x1010, vrSH, 1, // Station Name
			0x1030, vrLO, 1, // Study Description
			0x1032, vrSQ, 1, // Procedure Code Sequence
			0x103E, vrLO, 1, // Series Description
			0x1040, vrLO, 1, // Institutional Departement Name
			0x1050, vrPN, 0, // Performing Physician's Name
			0x1060, vrPN, 0, // Name of Physician(s) Reading Study
			0x1070, vrPN, 0, // Operators'Name
			0x1080, vrLO, 0, // Admitting Diagnoses Description
			0x1084, vrSQ, 1, // Admitting Diagnosis Code Sequence
			0x1090, vrLO, 1, // Manufacturer's Model Name
			0x1100, vrSQ, 1, // Referenced Results Sequence
			0x1110, vrSQ, 1, // Referenced Study Sequence
			0x1111, vrSQ, 1, // Referenced Study Component Sequence
			0x1115, vrSQ, 1, // Referenced Series Sequence
			0x1120, vrSQ, 1, // Referenced Patient Sequence
			0x1125, vrSQ, 1, // Referenced Visit Sequence
			0x1130, vrSQ, 1, // Referenced Overlay Sequence
			0x1140, vrSQ, 1, // Referenced Image Sequence
			0x1145, vrSQ, 1, // Referenced Curve Sequence
			0x1150, vrUI, 1, // Referenced SOP Class UID
			0x1155, vrUI, 1, // Referenced SOP Instance UID
			0x1160, vrIS, 1, // Referenced Frame Number
			0x2111, vrST, 1, // Detivation Description
			0x2112, vrSQ, 1, // Source Image Sequence
			0x2120, vrSH, 1, // Stage Name
			0x2122, vrIS, 1, // Stage Number
			0x2124, vrIS, 1, // Number of Stages
			0x2128, vrIS, 1, // View Number
			0x2129, vrIS, 1, // Number of Event Timers
			0x212A, vrIS, 1, // Number of Views in Stage
			0x2130, vrDS, 0, // Event Elapsed Time(s)
			0x2132, vrLO, 0, // Event Timer Name(s)
			0x2142, vrIS, 1, // Start Trim
			0x2143, vrIS, 1, // Stop Trim
			0x2144, vrIS, 1, // Recommended Display Frame Rate
			0x2200, vrCS, 1, // Transducer Position
			0x2204, vrCS, 1, // Transducer Orientation
			0x2208, vrCS, 1, // Anatomic Structure
			0x4000, retired_element, -1 // Comments
	};

	static String[] gr0008String = { "Group Length", "Length to End",
			"Specific character Set", "Image Type", "Recognition Code",
			"Instance Creation Date", "Instance Creation Time",
			"Instance Creator UID", "SOP Class UID", "SOP Instance UID",
			"Study date", "Series Date", "Acquisition Date", "Image Date",
			"Overlay Date", "Curve Date", "Study Time", "Series Time",
			"Acquisition Time", "Image Time", "Overlay Time", "Curve Time",
			"Data Set Type", "Data Set Subtype",
			"Nuclear Medecine Series Type", "Accession Number",
			"Query/retrieve Level", "Retrieve AE Title",
			"Failed SOP Instance UID List", "Modality", "Conversion Type",
			"Manufacturer", "Institution Name", "Institution Address",
			"Institution Code Sequence", "Reffering Physician's Name",
			"Reffering Physician's Address",
			"Reffering Physician's Telephone Numbers", "Code Value",
			"Coding Scheme Designator", "Code meaning", "Station Name",
			"Study Description", "Procedure Code Sequence",
			"Series Description", "Institutional Departement Name",
			"Performing Physician's Name",
			"Name of Physician(s) Reading Study", "Operators'Name",
			"Admitting Diagnoses Description",
			"Admitting Diagnosis Code Sequence", "Manufacturer's Model Name",
			"Referenced Results Sequence", "Referenced Study Sequence",
			"Referenced Study Component Sequence",
			"Referenced Series Sequence", "Referenced Patient Sequence",
			"Referenced Visit Sequence", "Referenced Overlay Sequence",
			"Referenced Image Sequence", "Referenced Curve Sequence",
			"Referenced SOP Class UID", "Referenced SOP Instance UID",
			"Referenced Frame Number", "Detivation Description",
			"Source Image Sequence", "Stage Name", "Stage Number",
			"Number of Stages", "View Number", "Number of Event Timers",
			"Number of Views in Stage", "Event Elapsed Time(s)",
			"Event Timer Name(s)", "Start Trim", "Stop Trim",
			"Recommended Display Frame Rate", "Transducer Position",
			"Transducer Orientation", "Anatomic Structure", "Comments" };

	static int[] gr0010 = { 0x0000, vrUL, 1, // Group Length
			0x0010, vrPN, 1, // Patient's Name
			0x0020, vrLO, 1, // Patient ID
			0x0021, vrLO, 1, // Issuer of Patient ID
			0x0030, vrDA, 1, // Patient's Birth Date
			0x0032, vrTM, 1, // Patient's Birth Time
			0x0040, vrCS, 1, // Patient's Sex
			0x0050, vrSQ, 1, // Patient's Insurance Plan Code Sequence
			0x1000, vrLO, 0, // Other Patient IDs
			0x1001, vrPN, 0, // Other Patient Names
			0x1005, vrPN, 1, // Patient's Birth Name
			0x1010, vrAS, 1, // Patient's Age
			0x1020, vrDS, 1, // Patient's Size
			0x1030, vrDS, 1, // Patient's Weight
			0x1040, vrLO, 1, // Patient's Address
			0x1050, retired_element, -1,// Insurance Plan Identification
			0x1060, vrPN, 1, // Patient's Mother's Birth Name
			0x1080, vrLO, 1, // Military Rank
			0x1081, vrLO, 1, // Branch of Service
			0x1090, vrLO, 1, // Medical Record Locator
			0x2000, vrLO, 0, // Medical Alerts
			0x2110, vrLO, 0, // Contrast Allergies
			0x2150, vrLO, 1, // Country of Residence
			0x2152, vrLO, 1, // Region of Residence
			0x2154, vrSH, 0, // Patient's Telephone Numbers
			0x2160, vrSH, 1, // Ethnic Group
			0x2180, vrSH, 1, // Occupation
			0x21A0, vrCS, 1, // Smoking Status
			0x21B0, vrLT, 1, // Additional Patient History
			0x21C0, vrUS, 1, // Pregnancy Status
			0x21D0, vrDA, 1, // Last Menstrual Date
			0x21F0, vrLO, 1, // Patient's Religious Preference
			0x4000, vrLT, 1 // Patient Comments
	};

	static String[] gr0010String = { "Group Length", "Patient's Name",
			"Patient ID", "Issuer of Patient ID", "Patient's Birth Date",
			"Patient's Birth Time", "Patient's Sex",
			"Patient's Insurance Plan Code Sequence", "Other Patient IDs",
			"Other Patient Names", "Patient's Birth Name", "Patient's Age",
			"Patient's Size", "Patient's Weight", "Patient's Address",
			"Insurance Plan Identification", "Patient's Mother's Birth Name",
			"Military Rank", "Branch of Service", "Medical Record Locator",
			"Medical Alerts", "Contrast Allergies", "Country of Residence",
			"Region of Residence", "Patient's Telephone Numbers",
			"Ethnic Group", "Occupation", "Smoking Status",
			"Additional Patient History", "Pregnancy Status",
			"Last Menstrual Date", "Patient's Religious Preference",
			"Patient Comments" };

	static int[] gr0018 = { 0x0000, vrUL, 1, // Group Length
			0x0010, vrLO, 1, // Contrast/Bolus Agent
			0x0015, vrCS, 1, // Body Part Examinated
			0x0020, vrCS, 0, // Scanning Sequence
			0x0021, vrCS, 0, // Sequence Variant
			0x0022, vrCS, 0, // Scan Options
			0x0023, vrCS, 1, // MR Acquisition Type
			0x0024, vrSH, 1, // Sequence Name
			0x0025, vrCS, 1, // Angio Flag
			0x0030, vrLO, 0, // Radionuclide
			0x0031, vrLO, 0, // Radiopharmaceutical
			0x0032, vrDS, 1, // Energy Window Centerline
			0x0033, vrDS, 0, // Energy Window Total Width
			0x0034, vrLO, 1, // Intervention Drug Name
			0x0035, vrTM, 1, // Intervention Drug Start Time
			0x0040, vrIS, 1, // Cine Rate
			0x0050, vrDS, 1, // Slice Thickness
			0x0060, vrDS, 1, // KVP
			0x0070, vrIS, 1, // Counts Accumulated
			0x0071, vrCS, 1, // Acquisition Termination Condition
			0x0072, vrDS, 1, // Effective Series Duration
			0x0080, vrDS, 1, // Repetition Time
			0x0081, vrDS, 1, // Echo Time
			0x0082, vrDS, 1, // Inversion Time
			0x0083, vrDS, 1, // Number of Averages
			0x0084, vrDS, 1, // Imaging Frequency
			0x0085, vrSH, 1, // Imaged Nucleus
			0x0086, vrIS, 0, // Echo Number(s)
			0x0087, vrDS, 1, // Magnetic Field Strength
			0x0088, vrDS, 1, // Spacing Between Slices
			0x0089, vrIS, 1, // Number of Phase Encoding Steps
			0x0090, vrDS, 1, // Data Collection Diameter
			0x0091, vrIS, 1, // Echo Train Length
			0x0093, vrDS, 1, // Percent Sampling
			0x0094, vrDS, 1, // Percent Phase Field of View
			0x0095, vrDS, 1, // Pixel Bandwidth
			0x1000, vrLO, 1, // Device Serial Number
			0x1004, vrLO, 1, // Plate ID
			0x1010, vrLO, 1, // Secondary Capture Device ID
			0x1012, vrDA, 1, // Date of Secondary Capture
			0x1014, vrTM, 1, // Time of Secondary Capture
			0x1016, vrLO, 1, // Secondary Capture Device Manufacturer
			0x1018, vrLO, 1, // Secondary Capture Device Manufacturer's Model
			// Name
			0x1019, vrLO, 0, // Secondary Capture Device Software Version(s)
			0x1020, vrLO, 0, // Software Version(s)
			0x1022, vrSH, 1, // Video Image Format Acquired
			0x1023, vrLO, 1, // Digital Image Format Acquired
			0x1030, vrLO, 1, // Protocol Name
			0x1040, vrLO, 1, // Contrast/Bolus Route
			0x1041, vrDS, 1, // Contrast/Bolus Volume
			0x1042, vrTM, 1, // Contrast/Bolus Start Time
			0x1043, vrTM, 1, // Contrast/Bolus Stop Time
			0x1044, vrDS, 1, // Contrast/Bolus Total Dose
			0x1045, vrIS, 0, // Syringe counts
			0x1050, vrDS, 1, // Spatial Resolution
			0x1060, vrDS, 1, // Trigger Time
			0x1061, vrLO, 1, // Trigger Source or Type
			0x1062, vrIS, 1, // Normal Interval
			0x1063, vrDS, 1, // Frame Time
			0x1064, vrLO, 1, // Framing Type
			0x1065, vrDS, 0, // Frame Time Vector
			0x1066, vrDS, 1, // Frame Delay
			0x1070, vrLO, 0, // Radionuclide Route
			0x1071, vrDS, 0, // Radionuclide Volume
			0x1072, vrTM, 0, // Radionuclide Start Time
			0x1073, vrTM, 0, // Radionuclide Stop Time
			0x1074, vrDS, 0, // Radionuclide Total Dose
			0x1080, vrCS, 1, // Beat Rejection Flag
			0x1081, vrIS, 1, // Low R-R Value
			0x1082, vrIS, 1, // High R-R Value
			0x1083, vrIS, 1, // Intervals Acquired
			0x1084, vrIS, 1, // Intervals Rejected
			0x1085, vrLO, 1, // PVC Rejection
			0x1086, vrIS, 1, // Skip Beats
			0x1088, vrIS, 1, // Heart Rate
			0x1090, vrIS, 1, // Cardiac Number of Images
			0x1094, vrIS, 1, // Trigger Window
			0x1100, vrDS, 1, // Reconstruction Diameter
			0x1110, vrDS, 1, // Distance Source to Detector
			0x1111, vrDS, 1, // Distance Source to Patient
			0x1120, vrDS, 1, // Gantry/Detector Tilt
			0x1130, vrDS, 1, // Table Height
			0x1131, vrDS, 1, // Table Traverse
			0x1140, vrCS, 1, // Rotation Direction
			0x1141, vrDS, 1, // Angular Position
			0x1142, vrDS, 0, // Radial Position
			0x1143, vrDS, 1, // Scan Arc
			0x1144, vrDS, 1, // Angular Step
			0x1145, vrDS, 1, // Center of Rotation Offset
			0x1146, vrDS, 0, // Rotation Offset
			0x1147, vrCS, 1, // Field of View Shape
			0x1149, vrIS, 0, // Field of View Dimension(s)
			0x1150, vrIS, 1, // Exposure Time
			0x1151, vrIS, 1, // X-ray Tube Current
			0x1152, vrIS, 1, // Exposure
			0x1160, vrSH, 1, // Filter Type
			0x1170, vrIS, 1, // Generator Power
			0x1180, vrSH, 1, // Collimator/grid Name
			0x1181, vrCS, 1, // Collimator Type
			0x1182, vrIS, 1, // Focal Distance
			0x1183, vrDS, 1, // X Focus Center
			0x1184, vrDS, 1, // Y Focus Center
			0x1190, vrDS, 0, // Focal Spot(s)
			0x1200, vrDA, 0, // Date of Last Calibration
			0x1201, vrTM, 0, // Time of Last Calibration
			0x1210, vrSH, 0, // Convolution Kernel
			0x1240, retired_element, -1,// Upper/Lower Pixel Values
			0x1242, vrIS, 1, // Actual Frame Duration
			0x1243, vrIS, 1, // Count Rate
			0x1250, vrSH, 1, // Receiving Coil
			0x1251, vrSH, 1, // Transmitting Coil
			0x1260, vrSH, 1, // Plate Type
			0x1261, vrLO, 1, // Phosphor Type
			0x1300, vrIS, 1, // Scan Velocity
			0x1301, vrCS, 0, // Whole Body Technique
			0x1302, vrIS, 1, // Scan Length
			0x1310, vrUS, 4, // Acquisition Matrix
			0x1312, vrUS, 1, // Phase Encoding Direction
			0x1315, vrCS, 1, // Variable Filp Angle Flag
			0x1316, vrDS, 1, // SAR
			0x1318, vrDS, 1, // dB/dt
			0x1400, vrLO, 1, // Acquisition Device Processing Descripton
			0x1401, vrLO, 1, // Acquisition Device Processing Code
			0x1402, vrCS, 1, // Cassette Orientation
			0x1403, vrCS, 1, // Cassette Size
			0x1404, vrUS, 1, // Exposures on Plate
			0x1405, vrIS, 1, // Relative X-ray Exposure
			0x4000, retired_element, -1,// Comments
			0x5000, vrSH, 0, // Output Power
			0x5010, vrLO, 3, // Transducer Data
			0x5012, vrDS, 1, // Focus Depth
			0x5020, vrLO, 1, // Preprocessing Function
			0x5021, vrLO, 1, // Postprocessing Function
			0x5022, vrDS, 1, // Mechanical Index
			0x5024, vrDS, 1, // Thermal Index
			0x5026, vrDS, 1, // Cranial Thermal Index
			0x5027, vrDS, 1, // Soft Tissue Thermal Index
			0x5028, vrDS, 1, // Soft Tissue-focus Thermal Index
			0x5029, vrDS, 1, // Soft Tissue-surface Thermal Index
			0x5050, vrIS, 1, // Depth of Scan Field
			0x5100, vrCS, 1, // Patient Position
			0x5101, vrCS, 1, // View position
			0x5210, vrDS, 6, // Image Transformation Matrix
			0x5212, vrDS, 3, // Image Translation Vector
			0x6000, vrDS, 1, // Sensitivity
			0x6011, vrSQ, 1, // Sequence of Ultrasound Regions
			0x6012, vrUS, 1, // Region Spatial Format
			0x6014, vrUS, 1, // Region Data Type
			0x6016, vrUL, 1, // Region Flags
			0x6018, vrUL, 1, // Region Location Min X0
			0x601A, vrUL, 1, // Region Location Min Y0
			0x601C, vrUL, 1, // Region locationMax X1
			0x601E, vrUL, 1, // Region locatiMax Y1
			0x6020, vrSL, 1, // Reference Pixel X0
			0x6022, vrSL, 1, // Reference Pixel Y0
			0x6024, vrUS, 1, // Physical Units X Direction
			0x6026, vrUS, 1, // Physical Units Y Direction
			0x6028, vrFD, 1, // Reference Pixel Physical Value X
			0x602A, vrFD, 1, // Reference Pixel Physical Valueb Y
			0x602C, vrFD, 1, // Physical Delta X
			0x602E, vrFD, 1, // Physical Delta Y
			0x6030, vrUL, 1, // Transducer Frequency
			0x6031, vrCS, 1, // Transducer Type
			0x6032, vrUL, 1, // Pulse Repetition Frequency
			0x6034, vrFD, 1, // Doppler Correction Angle
			0x6036, vrFD, 1, // Sterring Angle
			0x6038, vrUL, 1, // Doppler Sample Volume X Position
			0x603A, vrUL, 1, // Doppler Sample Volume Y Position
			0x603C, vrUL, 1, // TM-Line Position X0
			0x603E, vrUL, 1, // TM-Line Position Y0
			0x6040, vrUL, 1, // TM-Line Position X1
			0x6042, vrUL, 1, // TM-Line Position Y1
			0x6044, vrUS, 1, // Pixel Component Organization
			0x6046, vrUL, 1, // Pixel Component Mask
			0x6048, vrUL, 1, // Pixel Component Range Start
			0x604A, vrUL, 1, // Pixel Component Range Stop
			0x604C, vrUS, 1, // Pixel Component Physical Units
			0x604E, vrUS, 1, // Pixel Component Data Type
			0x6050, vrUL, 1, // Number of Table Break Points
			0x6052, vrUL, 0, // Table of X Break Points
			0x6054, vrFD, 0 // Table of Y Break Points
	};

	static String[] gr0018String = { "Group Length", "Contrast/Bolus Agent",
			"Body Part Examinated", "Scanning Sequence", "Sequence Variant",
			"Scan Options", "MR Acquisition Type", "Sequence Name",
			"Angio Flag", "Radionuclide", "Radiopharmaceutical",
			"Energy Window Centerline", "Energy Window Total Width",
			"Intervention Drug Name", "Intervention Drug Start Time",
			"Cine Rate", "Slice Thickness", "KVP", "Counts Accumulated",
			"Acquisition Termination Condition", "Effective Series Duration",
			"Repetition Time", "Echo Time", "Inversion Time",
			"Number of Averages", "Imaging Frequency", "Imaged Nucleus",
			"Echo Number(s)", "Magnetic Field Strength",
			"Spacing Between Slices", "Number of Phase Encoding Steps",
			"Data Collection Diameter", "Echo Train Length",
			"Percent Sampling", "Percent Phase Field of View",
			"Pixel Bandwidth", "Device Serial Number", "Plate ID",
			"Secondary Capture Device ID", "Date of Secondary Capture",
			"Time of Secondary Capture",
			"Secondary Capture Device Manufacturer",
			"Secondary Capture Device Manufacturer's Model Name",
			"Secondary Capture Device Software Version(s)",
			"Software Version(s)", "Video Image Format Acquired",
			"Digital Image Format Acquired", "Protocol Name",
			"Contrast/Bolus Route", "Contrast/Bolus Volume",
			"Contrast/Bolus Start Time", "Contrast/Bolus Stop Time",
			"Contrast/Bolus Total Dose", "Syringe counts",
			"Spatial Resolution", "Trigger Time", "Trigger Source or Type",
			"Normal Interval", "Frame Time", "Framing Type",
			"Frame Time Vector", "Frame Delay", "Radionuclide Route",
			"Radionuclide Volume", "Radionuclide Start Time",
			"Radionuclide Stop Time", "Radionuclide Total Dose",
			"Beat Rejection Flag", "Low R-R Value", "High R-R Value",
			"Intervals Acquired", "Intervals Rejected", "PVC Rejection",
			"Skip Beats", "Heart Rate", "Cardiac Number of Images",
			"Trigger Window", "Reconstruction Diameter",
			"Distance Source to Detector", "Distance Source to Patient",
			"Gantry/Detector Tilt", "Table Height", "Table Traverse",
			"Rotation Direction", "Angular Position", "Radial Position",
			"Scan Arc", "Angular Step", "Center of Rotation Offset",
			"Rotation Offset", "Field of View Shape",
			"Field of View Dimension(s)", "Exposure Time",
			"X-ray Tube Current", "Exposure", "Filter Type", "Generator Power",
			"Collimator/grid Name", "Collimator Type", "Focal Distance",
			"X Focus Center", "Y Focus Center", "Focal Spot(s)",
			"Date of Last Calibration", "Time of Last Calibration",
			"Convolution Kernel", "Upper/Lower Pixel Values",
			"Actual Frame Duration", "Count Rate", "Receiving Coil",
			"Transmitting Coil", "Plate Type", "Phosphor Type",
			"Scan Velocity", "Whole Body Technique", "Scan Length",
			"Acquisition Matrix", "Phase Encoding Direction",
			"Variable Filp Angle Flag", "SAR", "dB/dt",
			"Acquisition Device Processing Descripton",
			"Acquisition Device Processing Code", "Cassette Orientation",
			"Cassette Size", "Exposures on Plate", "Relative X-ray Exposure",
			"Comments", "Output Power", "Transducer Data", "Focus Depth",
			"Preprocessing Function", "Postprocessing Function",
			"Mechanical Index", "Thermal Index", "Cranial Thermal Index",
			"Soft Tissue Thermal Index", "Soft Tissue-focus Thermal Index",
			"Soft Tissue-surface Thermal Index", "Depth of Scan Field",
			"Patient Position", "View position", "Image Transformation Matrix",
			"Image Translation Vector", "Sensitivity",
			"Sequence of Ultrasound Regions", "Region Spatial Format",
			"Region Data Type", "Region Flags", "Region Location Min X0",
			"Region Location Min Y0", "Region locationMax X1",
			"Region locatiMax Y1", "Reference Pixel X0", "Reference Pixel Y0",
			"Physical Units X Direction", "Physical Units Y Direction",
			"Reference Pixel Physical Value X",
			"Reference Pixel Physical Value Y", "Physical Delta X",
			"Physical Delta Y", "Transducer Frequency", "Transducer Type",
			"Pulse Repetition Frequency", "Doppler Correction Angle",
			"Sterring Angle", "Doppler Sample Volume X Position",
			"Doppler Sample Volume Y Position", "TM-Line Position X0",
			"TM-Line Position Y0", "TM-Line Position X1",
			"TM-Line Position Y1", "Pixel Component Organization",
			"Pixel Component Mask", "Pixel Component Range Start",
			"Pixel Component Range Stop", "Pixel Component Physical Units",
			"Pixel Component Data Type", "Number of Table Break Points",
			"Table of X Break Points", "Table of Y Break Points" };

	static int[] gr0020 = {

	0x0000, vrUL, 1, // Group Length
			0x000D, vrUI, 1, // Study Instance UID
			0x000E, vrUI, 1, // Series Instance UID
			0x0010, vrSH, 1, // Study ID
			0x0011, vrIS, 1, // Series Number
			0x0012, vrIS, 1, // Acquisition Number
			0x0013, vrIS, 1, // Image Number
			0x0014, vrIS, 1, // Isotope Number
			0x0015, vrIS, 1, // Phase Number
			0x0016, vrIS, 1, // Interval Number
			0x0017, vrIS, 1, // Time Slot Number
			0x0018, vrIS, 1, // Angle Number
			0x0020, vrCS, 2, // Patient Orientation
			0x0022, vrIS, 1, // Overlay Number
			0x0024, vrIS, 1, // Curve Number
			0x0026, vrIS, 1, // LUT number
			0x0030, retired_element, -1,// Image Position
			0x0032, vrDS, 3, // Image Position(Patient)
			0x0035, retired_element, -1,// Image Orientation
			0x0037, vrDS, 6, // Image Orientation(Patient)
			0x0050, retired_element, -1,// Location
			0x0052, vrUI, 1, // Frame of Reference UID
			0x0060, vrCS, 1, // Laterality
			0x0070, retired_element, -1,// Image Geometry Type
			0x0080, retired_element, -1,// Masking Image
			0x0100, vrIS, 1, // Temporal Position Identifier
			0x0105, vrIS, 1, // Number of Temporal Positions
			0x0110, vrDS, 1, // Temporal Resolution
			0x1000, vrIS, 1, // Series in Study
			0x1001, retired_element, -1,// Acquisitions in Series
			0x1002, vrIS, 1, // Images in Acquisitions
			0x1003, retired_element, -1,// Images in Series
			0x1004, vrIS, 1, // Acquisition in Study
			0x1005, retired_element, -1,// Images in Study
			0x1020, retired_element, -1,// Reference
			0x1040, vrLO, 1, // Position Reference Indicator
			0x1041, vrDS, 1, // Slice Location
			0x1070, vrIS, 0, // Other Study Numbers
			0x1200, vrIS, 1, // Number of Patient Ralated Studies
			0x1202, vrIS, 1, // Number of Patient Related Series
			0x1204, vrIS, 1, // Number of patient Related Images
			0x1206, vrIS, 1, // Number of Study Related Series
			0x1208, vrIS, 1, // Number of Study Related Images
			// ATTENTION IL MANQUE SOURCE IMAGE IDs
			0x3401, retired_element, -1,// Modifying Device ID
			0x3402, retired_element, -1,// Modified Image ID
			0x3403, retired_element, -1,// Modified Image Date
			0x3404, retired_element, -1,// Modifying Device Manufacturer
			0x3405, retired_element, -1,// Modified Image Time
			0x3406, retired_element, -1,// Modified Image Description
			0x4000, vrLT, 1, // Image Comments
			0x5000, retired_element, -1,// Original Image Identification
			0x5002, retired_element, -1 // Original Image Identification
	// Nomenclature
	};

	static String[] gr0020String = {

	"Group Length", "Study Instance UID", "Series Instance UID", "Study ID",
			"Series Number", "Acquisition Number", "Image Number",
			"Isotope Number", "Phase Number", "Interval Number",
			"Time Slot Number", "Angle Number", "Patient Orientation",
			"Overlay Number", "Curve Number", "LUT number", "Image Position",
			"Image Position(Patient)", "Image Orientation",
			"Image Orientation(Patient)", "Location", "Frame of Reference UID",
			"Laterality", "Image Geometry Type", "Masking Image",
			"Temporal Position Identifier", "Number of Temporal Positions",
			"Temporal Resolution", "Series in Study", "Acquisitions in Series",
			"Images in Acquisitions", "Images in Series",
			"Acquisition in Study", "Images in Study",
			"Reference",
			"Position Reference Indicator",
			"Slice Location",
			"Other Study Numbers",
			"Number of Patient Related Studies",
			"Number of Patient Related Series",
			"Number of patient Related Images",
			"Number of Study Related Series",
			"Number of Study Related Images"
			// ATTENTION IL MANQUE SOURCE IMAGE IDs
			, "Modifying Device ID", "Modified Image ID",
			"Modified Image Date", "Modifying Device Manufacturer",
			"Modified Image Time", "Modified Image Description",
			"Image Comments", "Original Image Identification",
			"Original Image Identification Nomenclature" };

	static int[] gr0028 = {

	0x0000, vrUL, 1, // Group Length
			0x0002, vrUS, 1, // Samples per Pixel
			0x0004, vrCS, 1, // Photometric Interpretation
			0x0005, retired_element, -1,// Image Dimensions
			0x0006, vrUS, 1, // Planar Configuration
			0x0008, vrIS, 1, // Number of Frames
			0x0009, vrAT, 1, // Frame Increment Pointer
			0x0010, vrUS, 1, // Rows
			0x0011, vrUS, 1, // Columns
			0x0030, vrDS, 2, // Pixel Spacing
			0x0031, vrDS, 2, // Zoom Factor
			0x0032, vrDS, 2, // Zoom Center
			0x0034, vrIS, 2, // Pixel Aspect Ratio
			0x0040, retired_element, -1,// Image Format
			0x0050, retired_element, -1,// Manipulated Image
			0x0051, vrCS, 1, // Corrected Image
			0x0060, retired_element, -1,// Compression Code
			0x0100, vrUS, 1, // Bits Allocated
			0x0101, vrUS, 1, // Bits Stored
			0x0102, vrUS, 1, // High Bit
			0x0103, vrUS, 1, // Pixel Representation
			0x0104, retired_element, 1, // Smallest Valid Pixel Value
			0x0105, retired_element, 1, // Largest Valid Pixel Value
			0x0106, vrUSorSS, 1, // Smallest Image Pixel Value
			0x0107, vrUSorSS, 1, // Largest Image Pixel Value
			0x0108, vrUSorSS, 1, // Smallest Pixel Value in Series
			0x0109, vrUSorSS, 1, // Largest Pixel Value in Series
			0x0120, vrUSorSS, 1, // Pixel Padding Value
			0x0200, retired_element, -1,// Image Location
			0x1050, vrDS, 0, // Window Center
			0x1051, vrDS, 0, // Window Width
			0x1052, vrDS, 1, // Rescale Intercept
			0x1053, vrDS, 1, // Rescale Slope
			0x1054, vrLO, 1, // Rescale type
			0x1055, vrLO, 0, // Window Center & Width Explanation
			0x1080, retired_element, -1,// Gray Scale
			0x1100, retired_element, -1,// Gray Lookup Table Descriptor.
			0x1101, vrUSorSS, 3, // Red Palette Color Lookup Table Descriptor
			0x1102, vrUSorSS, 3, // Green Palette Color Lookup Table Descriptor
			0x1103, vrUSorSS, 3, // Blue Palette Color Lookup TableDescriptor
			0x1200, retired_element, -1,// Gray Lookup Table Data
			0x1201, vrUSorSS, 0, // Red Palette Color Lookup Table Data
			0x1202, vrUSorSS, 0, // Green palette Color Lookup Table Data
			0x1203, vrUSorSS, 0, // Blue palette Color Lookup Table Data
			0x3000, vrSQ, 1, // Modality LUT Sequence
			0x3002, vrUSorSS, 3, // LUT Descriptor
			0x3003, vrLO, 1, // LUT Explanation
			0x3004, vrLO, 1, // Modality LUT Type
			0x3006, vrUSorSS, 0, // LUT Data
			0x3010, vrSQ, 1, // VOI LUT Sequence
			0x4000, retired_element, -1 // Comments
	};

	static String[] gr0028String = {

	"Group Length", "Samples per Pixel", "Photometric Interpretation",
			"Image Dimensions", "Planar Configuration", "Number of Frames",
			"Frame Increment Pointer", "Rows", "Columns", "Pixel Spacing",
			"Zoom Factor", "Zoom Center", "Pixel Aspect Ratio", "Image Format",
			"Manipulated Image", "Corrected Image", "Compression Code",
			"Bits Allocated", "Bits Stored", "High Bit",
			"Pixel Representation", "Smallest Valid Pixel Value",
			"Largest Valid Pixel Value", "Smallest Image Pixel Value",
			"Largest Image Pixel Value", "Smallest Pixel Value in Series",
			"Largest Pixel Value in Series", "Pixel Padding Value",
			"Image Location", "Window Center", "Window Width",
			"Rescale Intercept", "Rescale Slope", "Rescale type",
			"Window Center & Width Explanation", "Gray Scale",
			"Gray Lookup Table Descriptor",
			"Red Palette Color Lookup Table Descriptor",
			"Green Palette Color Lookup Table Descriptor",
			"Blue Palette Color Lookup TableDescriptor",
			"Gray Lookup Table Data", "Red Palette Color Lookup Table Data",
			"Green palette Color Lookup Table Data",
			"Blue palette Color Lookup Table Data", "Modality LUT Sequence",
			"LUT Descriptor", "LUT Explanation", "Modality LUT Type",
			"LUT Data", "VOI LUT Sequence", "Comments" };

	static int[] gr0029 = { 0x0010, vrLT, 1, // Window Style (Siemens)
			0x1013, vrUL, 1, // Siemens ??
			0x1014, vrUL, 1, // Siemens ??
			0x1017, vrOB, 1, // Siemens ??
			0x1018, vrOB, 1, // Siemens ??
			0x1019, vrUL, 1, // Siemens ??
			0x101A, vrOB, 1, // Siemens ??
			0x101B, vrUL, 1, // Siemens ??
			0x101C, vrUL, 1, // Siemens ??
			0x101D, vrOB, 1, // Siemens ??
			0x101E, vrOB, 1, // Siemens ??
			0x1021, vrOB, 1, // Siemens ??
			0x1022, vrOB, 1, // Siemens ??
			0x1023, vrOB, 1 // Siemens ??
	};

	static int[] gr0032 = { 0x0000, vrUL, 1, // Group Length
			0x000A, vrCS, 1, // Study Satus ID
			0x000C, vrCS, 1, // Study Priority ID
			0x0012, vrLO, 1, // Study ID Issuer
			0x0032, vrDA, 1, // Study Verified Date
			0x0033, vrTM, 1, // Study Verified Time
			0x0034, vrDA, 1, // Study Read Date
			0x0035, vrTM, 1, // Study Read Time
			0x1000, vrDA, 1, // Scheduled Study Start Date
			0x1001, vrTM, 1, // Scheduled Study Start Time
			0x1010, vrDA, 1, // Scheduled Study Stop Date
			0x1011, vrTM, 1, // Scheduled Study Stop Time
			0x1020, vrLO, 1, // Scheduled Study Location
			0x1021, vrAE, 0, // Scheduled Study Location AE Title(s)
			0x1030, vrLO, 1, // Reason for Study
			0x1032, vrPN, 1, // Requesting Physician
			0x1033, vrLO, 1, // Requesting Service
			0x1040, vrDA, 1, // Study Arrival Date
			0x1041, vrTM, 1, // Study Arrival Time
			0x1050, vrDA, 1, // Study Completion Date
			0x1051, vrTM, 1, // Study Completion Time
			0x1055, vrCS, 1, // Study Component Status ID
			0x1060, vrLO, 1, // Requested Procedure Description

			0x1064, vrSQ, 1, // Requested Procedure Code Sequence
			0x1070, vrLO, 1, // Requested Contrast Agent
			0x4000, vrLT, 1 // Study Comments
	};

	static String[] gr0032String = { "Group Length", "Study Satus ID",
			"Study Priority ID", "Study ID Issuer", "Study Verified Date",
			"Study Verified Time", "Study Read Date", "Study Read Time",
			"Scheduled Study Start Date", "Scheduled Study Start Time",
			"Scheduled Study Stop Date", "cheduled Study Stop Time",
			"Scheduled Study Location", "Scheduled Study Location AE Title(s)",
			"Reason for Study", "Requesting Physician", "Requesting Service",
			"Study Arrival Date", "Study Arrival Time",
			"Study Completion Date", "Study Completion Time",
			"Study Component Status ID", "Requested Procedure Description",
			"Requested Procedure Code Sequence", "Requested Contrast Agent",
			"Study Comments" };

	static int[] gr0038 = {

	0x0000, vrUL, 1, // Group Length
			0x0004, vrSQ, 1, // Referenced Patient Alias Sequence
			0x0008, vrCS, 1, // Visit Status ID
			0x0010, vrLO, 1, // Admission ID
			0x0011, vrLO, 1, // Issuer of Admission ID
			0x0016, vrLO, 1, // Route of Admissions
			0x001A, vrDA, 1, // Scheduled Admission Date
			0x001B, vrTM, 1, // Scheduled Admission Time
			0x001C, vrDA, 1, // Scheduled Discharge Date
			0x001D, vrTM, 1, // Scheduled Discharge Time
			0x001E, vrLO, 1, // Scheduled Patient Institution Residence
			0x0020, vrDA, 1, // Admitting Date
			0x0021, vrTM, 1, // Admitting Time
			0x0030, vrDA, 1, // Discharge Date
			0x0032, vrTM, 1, // Discharge Time
			0x0040, vrLO, 1, // Discharge Diagnosis Description
			0x0044, vrSQ, 1, // Discharge Diagnosis Code Sequence
			0x0050, vrLO, 1, // Special Needs
			0x0300, vrLO, 1, // Current Patient Location
			0x0400, vrLO, 1, // Patient's Institution Residence
			0x0500, vrLO, 1, // Patient State
			0x4000, vrLT, 1 // Visit Comments
	};

	static int[] gr0088 = { 0x0000, vrUL, 1, // Group Length
			0x0130, vrSH, 1, // Storage Media File-set ID
			0x0140, vrUI, 1 // Storage Media File Set UID
	};

	static int[] gr2000 = { 0x0000, vrUL, 1, // Group Length
			0x0010, vrIS, 1, // Number of Copies
			0x0020, vrCS, 1, // Print Priority
			0x0030, vrCS, 1, // Medium Type
			0x0040, vrCS, 1, // Film Destination
			0x0050, vrLO, 1, // Film Session Label
			0x0060, vrIS, 1, // Memory Allocation
			0x0500, vrSQ, 1 // Referenced Film Box Sequence
	};

	static int[] gr2010 = { 0x0000, vrUL, 1, // Group Length
			0x0010, vrST, 1, // Image Display Format
			0x0030, vrCS, 1, // Annotation Diosplay Format ID
			0x0040, vrCS, 1, // Film Orientation
			0x0050, vrCS, 1, // Film Size ID
			0x0060, vrCS, 1, // Magnification Type
			0x0080, vrCS, 1, // Smoothing Type
			0x0100, vrCS, 1, // Border Density
			0x0110, vrCS, 1, // Empty Image Density
			0x0120, vrUS, 1, // Min Density
			0x0130, vrUS, 1, // Max Density
			0x0140, vrCS, 1, // Trim
			0x0150, vrST, 1, // Configuration Information
			0x0500, vrSQ, 1, // Referenced Film Session Sequence
			0x0510, vrSQ, 1, // Referenced Image Box Sequence
			0x0520, vrSQ, 1 // Referenced Basic Annotation BoxSequence
	};

	static int[] gr2020 = {

	0x0000, vrUL, 1, // Group Length
			0x0010, vrUS, 1, // Image Position
			0x0020, vrCS, 1, // Polarity
			0x0030, vrDS, 1, // Requested Image Size
			0x0110, vrSQ, 1, // Perfomatted Grayscale Image Sequence
			0x0111, vrSQ, 1, // Performated Color Image Sequence
			0x0130, vrSQ, 1, // Referenced Image Overlay Box Sequence
			0x0140, vrSQ, 1 // Referenced VOI LUT Box Sequence

	};

	static int[] gr2030 = {

	0x0000, vrUL, 1, // Group Length
			0x0010, vrUS, 1, // Annotation Position
			0x0020, vrLO, 1 // Text String
	};

	static int[] gr2040 = {

	0x0000, vrUL, 1, // Group Length
			0x0010, vrSQ, 1, // Referenced Overlay Plane Sequence
			0x0011, vrUS, 0, // referenced Overlay Plane Groups
			0x0060, vrCS, 1, // Overlay Magnification Type
			0x0070, vrCS, 1, // Overlay Smoothing Type
			0x0080, vrCS, 1, // Overlay Foreground Density
			0x0090, vrCS, 1, // Overlay Mode
			0x0100, vrCS, 1 // Threshold Density
	};

	static int[] gr2100 = {

	0x0000, vrUL, 1, // Group Length
			0x0020, vrCS, 1, // Execution Status
			0x0030, vrCS, 1, // Execution Status Info
			0x0040, vrDA, 1, // Creation Date
			0x0050, vrTM, 1, // Creation Time
			0x0070, vrAE, 1, // Originator
			0x0500, vrSQ, 1 // referenced Print Job Sequence

	};

	static int[] gr2110 = {

	0x0000, vrUL, 1, // Group Length
			0x0010, vrCS, 1, // Printer Status
			0x0020, vrCS, 1, // Printer Status Info
			0x0030, vrLO, 1 // Printer Name
	};

	static int[] gr4008 = {

	0x0000, vrUL, 1, // Group Length
			0x0040, vrSH, 1, // Result ID
			0x0042, vrLO, 1, // Result ID Issuer
			0x0050, vrSQ, 1, // Referenced Interpretation Sequence
			0x0100, vrDA, 1, // Interpretation Recorded Date
			0x0101, vrTM, 1, // Interpretation Recorded Time
			0x0102, vrPN, 1, // Interpretation Recorder
			0x0103, vrLO, 1, // Reference to Recorded Sound
			0x0108, vrDA, 1, // Interpretation Transcription Date
			0x0109, vrTM, 1, // Interpretation Transcription Time
			0x010A, vrPN, 1, // Interpretation Transcriber
			0x010B, vrST, 1, // Interpretation Text
			0x010C, vrPN, 1, // Interpretation Author
			0x0111, vrSQ, 1, // Interpretation Approver Sequence
			0x0112, vrDA, 1, // Interpretation Approval Date
			0x0113, vrTM, 1, // Interpretation Approval Time
			0x0114, vrPN, 1, // Physician Approving Interpretation
			0x0115, vrLT, 1, // Interpretation Diagnosis Description
			0x0117, vrSQ, 1, // Diagnosis Code Sequence
			0x0118, vrSQ, 1, // Results Distribution List Sequence
			0x0119, vrPN, 1, // Distribution Name
			0x011A, vrLO, 1, // Distribution Address
			0x0200, vrSH, 1, // Interpretation ID
			0x0202, vrLO, 1, // Interpretation ID Issuer
			0x0210, vrCS, 1, // Interpretation Type ID
			0x0212, vrCS, 1, // Interpretation Status ID
			0x0300, vrST, 1, // Impressions
			0x4000, vrST, 1 // Results Comments
	};

	static int[] gr50xx = {

	0x0000, vrUL, 1, // Group Length
			0x0005, vrUS, 1, // Curve Dimensions
			0x0010, vrUS, 1, // Number of points
			0x0020, vrCS, 1, // Type of Data
			0x0022, vrLO, 1, // Curve Description
			0x0030, vrSH, 0, // Axis Units
			0x0040, vrSH, 0, // Axis Labels
			0x0103, vrUS, 1, // Data Value Representation
			0x0104, vrUS, 0, // Minimum Coordinate Value
			0x0105, vrUS, 0, // Maximum Coordinate Value
			0x0106, vrSH, 0, // Curve Range
			0x0110, vrUS, 1, // Curve Data Descriptor
			0x0112, vrUS, 1, // Coordinate Start Value
			0x0114, vrUS, 1, // Coordinate Step Value
			0x2000, vrUS, 1, // Audio Type
			0x2002, vrUS, 1, // Audio Sample Format
			0x2004, vrUS, 1, // Number of Channels
			0x2006, vrUL, 1, // Number of Samples
			0x2008, vrUL, 1, // Sample Rate
			0x200A, vrUL, 1, // Total Time
			0x200C, vrOWorOB, 1, // Audio Sample Data
			0x200E, vrLT, 1, // Audio Comments
			0x3000, vrOWorOB, 1 // Curve Data
	};

	static int[] gr60xx = {

	0x0000, vrUL, 1, // Group Length
			0x0010, vrUS, 1, // Rows
			0x0011, vrUS, 1, // Columns
			0x0015, vrIS, 1, // Number of Frames in Overlay
			0x0040, vrCS, 1, // Overlay Type
			0x0050, vrSS, 2, // Origin
			0x0100, vrUS, 1, // Bits Allocated
			0x0102, vrUS, 1, // Bit Position
			0x0110, retired_element, -1,// Overlay Format
			0x0200, retired_element, -1,// Overlay Location
			0x1100, vrUS, 1, // Overlay Descriptor Gray
			0x1101, vrUS, 1, // Overlay Descriptor Red
			0x1102, vrUS, 1, // Overlay Descriptor Blue
			0x1200, vrUS, 0, // Overlays Gray
			0x1201, vrUS, 0, // Overlays Red
			0x1202, vrUS, 0, // Overlays Green
			0x1203, vrUS, 0, // Overlays Blue
			0x1301, vrIS, 1, // ROI Area
			0x1302, vrDS, 1, // ROI Mean
			0x1303, vrDS, 1, // ROI Standard deviation
			0x3000, vrOW, 1, // Overlay Data
			0x4000, retired_element, -1 // Comments
	};

	static int[] gr7FE0 = {

	0x0000, vrUL, 1, // Group Length
			0x0010, vrOWorOB, 1 // Pixel Data

	};

	static int[] grFFFE = {

	0xE000, 0, 1, // Item
			0xE00D, 0, 1, // Item Delimitation Item
			0xE0DD, 0, 1 // sequence Delimitation Item
	};

	public DataElement() {
	};

	public int findTag(TagDicom t) {
		int group = t.getGroup();
		int element = t.getElement();
		boolean find = false;

		indElementInGroup = -1;
		tabGroup = null;
		tabGroupString = null;
		denominationGroup = "??";

		switch (group) {
		case 0x0002:
			tabGroup = gr0002;
			tabGroupString = gr0002String;
			denominationGroup = "Gr0002";
			break;
		case 0x0008:
			tabGroup = gr0008;
			tabGroupString = gr0008String;
			denominationGroup = "Gr0008: Contexte Etude medicale";
			break;
		case 0x0010:
			tabGroup = gr0010;
			tabGroupString = gr0010String;
			denominationGroup = "Gr0010: Informations Patient";
			break;
		case 0x0018:
			tabGroup = gr0018;
			tabGroupString = gr0018String;
			denominationGroup = "Gr0018: Contexte Acquisition";
			break;
		case 0x0020:
			tabGroup = gr0020;
			tabGroupString = gr0020String;
			denominationGroup = "Gr0020: Informations Image";
			break;
		case 0x0028:
			tabGroup = gr0028;
			tabGroupString = gr0028String;
			denominationGroup = "Gr0028: Informations Stockage";
			break;
		case 0x0029:
			tabGroup = gr0029;
			break;
		case 0x0032:
			tabGroup = gr0032;
			tabGroupString = gr0032String;
			denominationGroup = "Gr0032: Complement Etude medicale";
			break;
		case 0x0038:
			tabGroup = gr0038;
			break;
		case 0x0088:
			tabGroup = gr0088;
			break;
		case 0x2000:
			tabGroup = gr2000;
			break;
		case 0x2010:
			tabGroup = gr2010;
			break;
		case 0x2020:
			tabGroup = gr2020;
			break;
		case 0x2030:
			tabGroup = gr2030;
			break;
		case 0x2040:
			tabGroup = gr2040;
			break;
		case 0x2100:
			tabGroup = gr2100;
			break;
		case 0x2110:
			tabGroup = gr2110;
			break;
		case 0x4008:
			tabGroup = gr4008;
			break;
		case 0x7FE0:
			tabGroup = gr7FE0;
			break;
		case 0xFFFE:
			tabGroup = grFFFE;
			break;
		default:
			tabGroup = null;
		}

		if (tabGroup != null) {
			for (indElementInGroup = 0; indElementInGroup < tabGroup.length; indElementInGroup += 3) {
				if (tabGroup[indElementInGroup] == element) {
					find = true;
					break;
				}
			}
			if (!find)
				indElementInGroup = -1;
		}

		return indElementInGroup;
	}

	public int getTypeVr() {
		if (indElementInGroup != -1)
			return tabGroup[indElementInGroup + 1];
		else
			return -1;
	}

	public int getVM() {
		if (indElementInGroup != -1)
			return tabGroup[indElementInGroup + 2];
		else
			return -1;
	}

	public String getDenomination() {
		if ((indElementInGroup != -1) && (tabGroupString != null))
			return tabGroupString[indElementInGroup / 3];
		else
			return "";
	}

	public String getDenominationGroup() {
		if (indElementInGroup != -1)
			return denominationGroup;
		else
			return "";
	}
}
