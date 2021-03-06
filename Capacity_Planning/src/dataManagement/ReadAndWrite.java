package dataManagement;

import ilog.concert.IloException;
import ilog.cplex.IloCplex.UnknownObjectException;
import java.io.*;
import jxl.*;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import java.io.IOException;
import java.io.FileWriter;
import java.util.*;
import helper.Event;
import jxl.*;
import java.io.FileOutputStream;


/**
 * Used for Excel input, output and console output
 * @author AntoniaWiggert
 *
 */
public class ReadAndWrite {

	public static int user = 2;

	// Paths Antonia #1
	public static String pathExcelAntonia = "/Users/antoniawiggert/Documents/GitHub/OSCMSeminarProcessIndustry/Capacity_Planning/lib/CaseDataBasic.xls";
	public static String pathExcelAntoniaR = "/Users/antoniawiggert/Documents/GitHub/OSCMSeminarProcessIndustry/Capacity_Planning/lib/Result.xls";
	public static String pathExcelAntoniaOutput = "/Users/antoniawiggert/Documents/GitHub/OSCMSeminarProcessIndustry/Capacity_Planning/lib/Computational_Study.xls";

	// Paths Sarah #2
	public static String pathExcelSarah = "C:\\Users\\Sarah\\Documents\\GitHub\\OSCMSeminarProcessIndustry\\Capacity_Planning\\lib\\CaseDataBasic.xls";
	public static String pathExcelSarahR = "C:\\Users\\Sarah\\Documents\\GitHub\\OSCMSeminarProcessIndustry\\Capacity_Planning\\lib\\Result.xls";
	public static String pathExcelSarahOutput = "C:\\Users\\Sarah\\Documents\\GitHub\\OSCMSeminarProcessIndustry\\Capacity_Planning\\lib\\Computational_Study.xls";

	// Paths Ramona #3
	public static String pathExcelRamona = "/Users/RamonaZauner/Documents/GitHub/OSCMSeminarProcessIndustry/Capacity_Planning/lib/CaseDataBasic.xls";
	public static String pathExcelRamonaR = "/Users/RamonaZauner/Documents/GitHub/OSCMSeminarProcessIndustry/Capacity_Planning/lib/Result.xls";
	public static String pathExcelRamonaOutput = "/Users/RamonaZauner/Documents/GitHub/OSCMSeminarProcessIndustry/Capacity_Planning/lib/Computational_Study.xls";

	// Paths Antonia Windows #4
	public static String pathExcelAntonia1 = "C:/Users/Antonia Wi/Documents/GitHub/OSCMSeminarProcessIndustry/Capacity_Planning/lib/CaseDataBasic.xls";//
	public static String pathExcelAntoniaR1 = "C:/Users/Antonia Wi/Documents/GitHub/OSCMSeminarProcessIndustry/Capacity_Planning/lib/Result.xls";
	public static String pathExcelAntoniaOutput1 = "C:/Users/Antonia Wi/Documents/GitHub/OSCMSeminarProcessIndustry/Capacity_Planning/lib/Computational_Study.xls";

	public static String path = "";
	public static String pathR = "";
	public static String pathOutput = "";

	
	/**
	 * Chooses paths for different user 
	 */
	public static void choosePaths() {

		if (user == 1) {

			path = pathExcelAntonia;
			pathR = pathExcelAntoniaR;
			pathOutput = pathExcelAntoniaOutput;
		}

		else if (user == 2) {

			path = pathExcelSarah;
			pathR = pathExcelSarahR;
			pathOutput = pathExcelSarahOutput;

		}

		else if (user == 3) {

			path = pathExcelRamona;
			pathR = pathExcelRamonaR;
			pathOutput = pathExcelRamonaOutput;

		} else if (user == 4) {

			path = pathExcelAntonia1;
			pathR = pathExcelAntoniaR1;
			pathOutput = pathExcelAntoniaOutput1;
		}

	}

	
	/*
	 * Excel-File:
	 * 
	 * - Sheet 0: DataTiming - Sheet 1: F - Sheet 2: F in N - Sheet 3: IMf - Sheet
	 * 4: OMf - Sheet 5: Const. - Sheet 6: TRn - Sheet 7: MassBalance - Sheet 8:
	 * DataF - Sheet 9: Dict_Basics - Sheet 10: Dict - Sheet 11: Dict_final - Sheet
	 * 12: Sis - Sheet 13: Pif_M -- Sheet 14: Pif_R - Sheet 15: CIF1sf - Sheet 16:
	 * CIF2sf - Sheet 17: CIF3sf - Sheet 18: CIF4sf - Sheet 19: CIF5sf - Sheet 20:
	 * IDisf
	 */

	// ____________________________________________________________________________________________

	// Methods to read from Excel file

	// ____________________________________________________________________________________________

	
	/**
	 * Reads data for the Decision Review Model 
	 * 
	 * @param instanz
	 * @throws BiffException
	 * @throws IOException
	 */
	public static void readDataTiming(Data instanz) throws BiffException, IOException {

		File file;
		Workbook workbook;
		choosePaths();
		file = new File(path);

		workbook = Workbook.getWorkbook(file);
		Sheet sheet = workbook.getSheet("DataTiming");

		// Planninghorizon T

		Cell cell1 = sheet.getCell(1, 0);
		NumberCell cell2 = (NumberCell) cell1;
		double cell3 = cell2.getValue();
		instanz.setParameter_planningHorizon((int) cell3);

		// Discount factor
		Cell cell4 = sheet.getCell(1, 1);

		NumberCell cell5 = (NumberCell) cell4;
		double cell6 = cell5.getValue();
		instanz.setParameter_discountFactor_timing(cell6);

		// Construction time sp0 und ss0
		Cell cell7 = sheet.getCell(1, 2);

		NumberCell cell8 = (NumberCell) cell7;
		double cell9 = cell8.getValue();
		instanz.setParameter_periodsToBuildPrimaryFacilities((int) cell9);

		Cell cell10 = sheet.getCell(1, 3);

		NumberCell cell11 = (NumberCell) cell10;
		double cell12 = cell11.getValue();
		instanz.setParameter_periodsToBuildSecondaryFacilities((int) cell12);

		// Construction costs cp and cs

		Cell cell13 = sheet.getCell(1, 4);

		NumberCell cell14 = (NumberCell) cell13;
		double cell15 = cell14.getValue();
		instanz.setParameter_constructionCostPrimaryFacility((int) cell15);

		Cell cell16 = sheet.getCell(1, 5);

		NumberCell cell17 = (NumberCell) cell16;
		double cell18 = cell17.getValue();
		instanz.setParameter_constructionCostSecondaryFacility((int) cell18);

		// Setup costs Kp and Ks

		Cell cell19 = sheet.getCell(1, 6);

		NumberCell cell20 = (NumberCell) cell19;
		double cell21 = cell20.getValue();
		instanz.setParameter_setupCostPrimaryFacility((int) cell21);

		Cell cell22 = sheet.getCell(1, 7);

		NumberCell cell23 = (NumberCell) cell22;
		double cell24 = cell23.getValue();
		instanz.setParameter_setupCostSecondaryFacility((int) cell24);

		// gamma0

		Cell cell25 = sheet.getCell(1, 9);

		NumberCell cell26 = (NumberCell) cell25;
		double cell27 = cell26.getValue();
		instanz.setParameter_preliminaryKnowledgeAboutSuccessfulTests((int) cell27);

		// zeta0

		Cell cell28 = sheet.getCell(1, 10);

		NumberCell cell29 = (NumberCell) cell28;
		double cell30 = cell29.getValue();
		instanz.setParameter_preliminaryKnowledgeAboutFailedTests((int) cell30);

		// gamma_c

		Cell cell31 = sheet.getCell(1, 11);

		NumberCell cell32 = (NumberCell) cell31;
		double cell33 = cell32.getValue();
		instanz.setParameter_thresholdSuccessfulTests((int) cell33);

		// penaltyCost

		Cell cell34 = sheet.getCell(1, 8);

		NumberCell cell35 = (NumberCell) cell34;
		double cell36 = cell35.getValue();
		instanz.setParameter_penaltyCost((int) cell36);

	}

	/**
	 * Reads facility classification
	 * 
	 * @param instanz
	 * @throws BiffException
	 * @throws IOException
	 */
	public static void readF(Data instanz) throws BiffException, IOException {

		File file;
		Workbook workbook;
		choosePaths();
		file = new File(path);

		workbook = Workbook.getWorkbook(file);
		Sheet sheet = workbook.getSheet("F");

		// IF[f]

		boolean[] IF = new boolean[instanz.getF()];

		for (int i = 0; i < instanz.getF(); i++) {
			Cell cell1 = sheet.getCell(1, i + 1);
			NumberCell cell2 = (NumberCell) cell1;
			double cell3 = cell2.getValue();

			int help = (int) cell3;
			boolean help1;
			if (help == 1) {
				help1 = true;
			} else {
				help1 = false;
			}

			IF[i] = help1;

		}
		instanz.setIF(IF);

		// EF[f]
		boolean[] EF = new boolean[instanz.getF()];

		for (int i = 0; i < instanz.getF(); i++) {
			Cell cell1 = sheet.getCell(2, i + 1);
			NumberCell cell2 = (NumberCell) cell1;
			double cell3 = cell2.getValue();

			int help = (int) cell3;
			boolean help1;
			if (help == 1) {
				help1 = true;
			} else {
				help1 = false;
			}

			EF[i] = help1;

		}
		instanz.setEF(EF);

		boolean[] PIF = new boolean[instanz.getF()];

		for (int i = 0; i < instanz.getF(); i++) {
			Cell cell1 = sheet.getCell(3, i + 1);
			NumberCell cell2 = (NumberCell) cell1;
			double cell3 = cell2.getValue();

			int help = (int) cell3;
			boolean help1;
			if (help == 1) {
				help1 = true;
			} else {
				help1 = false;
			}

			PIF[i] = help1;

		}
		instanz.setPIF(PIF);

		boolean[] SIF = new boolean[instanz.getF()];

		for (int i = 0; i < instanz.getF(); i++) {
			Cell cell1 = sheet.getCell(4, i + 1);
			NumberCell cell2 = (NumberCell) cell1;
			double cell3 = cell2.getValue();

			int help = (int) cell3;
			boolean help1;
			if (help == 1) {
				help1 = true;
			} else {
				help1 = false;
			}

			SIF[i] = help1;

		}
		instanz.setSIF(SIF);

	}

	/**
	 * Reads location for facilities
	 * 
	 * @param instanz
	 * @throws BiffException
	 * @throws IOException
	 */
	public static void readFinN(Data instanz) throws BiffException, IOException {

		File file;
		Workbook workbook;
		choosePaths();
		file = new File(path);

		workbook = Workbook.getWorkbook(file);
		Sheet sheet = workbook.getSheet("F in N");

		// Fn[f][n]

		boolean[][] Fn = new boolean[instanz.getF()][instanz.getN()];

		for (int i = 0; i < instanz.getF(); i++) {
			for (int j = 0; j < instanz.getN(); j++) {

				Cell cell1 = sheet.getCell(j + 1, i + 1);
				NumberCell cell2 = (NumberCell) cell1;
				double cell3 = cell2.getValue();

				int help = (int) cell3;
				boolean help1;
				if (help == 1) {
					help1 = true;
				} else {
					help1 = false;
				}

				Fn[i][j] = help1;

			}

		}
		instanz.setFn(Fn);
	}

	/**
	 * Reads input materials for facilities
	 * 
	 * @param instanz
	 * @throws BiffException
	 * @throws IOException
	 */
	public static void readIMf(Data instanz) throws BiffException, IOException {

		File file;
		Workbook workbook;
		choosePaths();
		file = new File(path);

		workbook = Workbook.getWorkbook(file);
		Sheet sheet = workbook.getSheet("IMf");

		// IM[f][i]

		boolean[][] IMf = new boolean[instanz.getF()][instanz.getI()];

		for (int i = 0; i < instanz.getF(); i++) {
			for (int j = 0; j < instanz.getI(); j++) {

				Cell cell1 = sheet.getCell(j + 1, i + 1);
				NumberCell cell2 = (NumberCell) cell1;
				double cell3 = cell2.getValue();

				int help = (int) cell3;
				boolean help1;
				if (help == 1) {
					help1 = true;
				} else {
					help1 = false;
				}

				IMf[i][j] = help1;

			}

		}
		instanz.setIM(IMf);
	}

	
	/**
	 * Reads output materials for facilities
	 * 
	 * @param instanz
	 * @throws BiffException
	 * @throws IOException
	 */
	public static void readOMf(Data instanz) throws BiffException, IOException {

		File file;
		Workbook workbook;
		choosePaths();
		file = new File(path);

		workbook = Workbook.getWorkbook(file);
		Sheet sheet = workbook.getSheet("OMf");

		// OM[f][i]

		boolean[][] OMf = new boolean[instanz.getF()][instanz.getI()];

		for (int i = 0; i < instanz.getF(); i++) {
			for (int j = 0; j < instanz.getI(); j++) {

				Cell cell1 = sheet.getCell(j + 1, i + 1);
				NumberCell cell2 = (NumberCell) cell1;
				double cell3 = cell2.getValue();

				int help = (int) cell3;
				boolean help1;
				if (help == 1) {
					help1 = true;
				} else {
					help1 = false;
				}

				OMf[i][j] = help1;

			}

		}
		instanz.setOM(OMf);
	}

	
	 /**
	  * Reads general information 
	  * @param instanz
	  * @throws BiffException
	  * @throws IOException
	  */
	public static void readConst(Data instanz) throws BiffException, IOException {

		File file;
		Workbook workbook;
		choosePaths();
		file = new File(path);

		workbook = Workbook.getWorkbook(file);
		Sheet sheet = workbook.getSheet("Const.");

		// numberFacilities F
		Cell cell7 = sheet.getCell(1, 8);

		NumberCell cell8 = (NumberCell) cell7;
		double cell9 = cell8.getValue();
		instanz.setF((int) cell9);

		// numberMaterials I
		Cell cell10 = sheet.getCell(1, 9);
		NumberCell cell11 = (NumberCell) cell10;
		double cell12 = cell11.getValue();
		instanz.setI((int) cell12);

		// numberMonths T will be calculated when Location Planning Model is called

		// numberOfNations N
		Cell cell16 = sheet.getCell(1, 11);
		NumberCell cell17 = (NumberCell) cell16;
		double cell18 = cell17.getValue();
		instanz.setN((int) cell18);

		// initial capacity Q0

		Cell cell19 = sheet.getCell(1, 3);

		NumberCell cell20 = (NumberCell) cell19;
		double cell21 = cell20.getValue();
		instanz.setInitialCapacity((int) cell21);

		// API
		Cell cell22 = sheet.getCell(1, 4);
		NumberCell cell23 = (NumberCell) cell22;
		double cell24 = cell23.getValue();
		instanz.setAPI((int) cell24);

		// timeMonopoly
		Cell cell25 = sheet.getCell(1, 14);
		NumberCell cell26 = (NumberCell) cell25;
		double cell27 = cell26.getValue();
		instanz.setTimeM((int) cell27);

		// timeRegularMarket
		Cell cell28 = sheet.getCell(1, 15);
		NumberCell cell29 = (NumberCell) cell28;
		double cell30 = cell29.getValue();
		instanz.setTimeR((int) cell30);

		// monthlyDiscountFactor
		Cell cell31 = sheet.getCell(1, 5);
		NumberCell cell32 = (NumberCell) cell31;
		double cell33 = cell32.getValue();
		instanz.setParameter_discountFactor_location(cell33);

		// constructionTimePrimary
		Cell cell34 = sheet.getCell(1, 19);
		NumberCell cell35 = (NumberCell) cell34;
		double cell36 = cell35.getValue();
		int cell37 = (int) cell36;
		instanz.setMonthsToBuildPrimaryFacilities_location(cell37);

		// constructionTimeSecondary
		Cell cell38 = sheet.getCell(1, 20);
		NumberCell cell39 = (NumberCell) cell38;
		double cell40 = cell39.getValue();
		int cell41 = (int) cell40;
		instanz.setMonthsToBuildSecondaryFacilities_location(cell41);

		// constructionCostPrimary
		Cell cell42 = sheet.getCell(1, 21);
		NumberCell cell43 = (NumberCell) cell42;
		double cell44 = cell43.getValue();
		int cell45 = (int) cell44;
		instanz.setConstructionCostPrimaryFacility_location(cell45);

		// constructionCostSecondary
		Cell cell46 = sheet.getCell(1, 22);
		NumberCell cell47 = (NumberCell) cell46;
		double cell48 = cell47.getValue();
		int cell49 = (int) cell48;
		instanz.setConstructionCostSecondaryFacility_location(cell49);

	}
	
	
	/**
	 * Writes tranfer parameters in sheet Const.
	 * @param instanz
	 * @param index
	 * @throws BiffException
	 * @throws IOException
	 * @throws WriteException
	 */

	public static void writeTransferParameter(Data instanz, int index)
			throws BiffException, IOException, WriteException {

		File file;
		WritableWorkbook writableWorkbook;
		Workbook workbook;
		choosePaths();

		file = new File(path);

		workbook = Workbook.getWorkbook(file);
		writableWorkbook = Workbook.createWorkbook(file, workbook);

		WritableSheet sheet = writableWorkbook.getSheet("Const.");

		// remaining Time

		if (index == 1) {
			Number label3 = new Number(1, 16, instanz.getRemainingTimeofClinicalTrials());
			sheet.addCell(label3);
		} else if (index == 2) {
			Number label5 = new Number(3, 16, instanz.getRemainingTimeofClinicalTrials());
			sheet.addCell(label5);
		}

		// planning horizon T
		if (index == 1) {
			Number label4 = new Number(1, 10, instanz.getT());
			sheet.addCell(label4);
		} else if (index == 2) {
			Number label6 = new Number(3, 10, instanz.getT());
			sheet.addCell(label6);
		}
		
		
		writableWorkbook.write();
		writableWorkbook.close();
		workbook.close();
	}

	
	/**
	 * Reads corporate tax rates
	 * @param instanz
	 * @throws BiffException
	 * @throws IOException
	 */
	public static void readTRn(Data instanz) throws BiffException, IOException {

		File file;
		Workbook workbook;
		choosePaths();
		file = new File(path);

		workbook = Workbook.getWorkbook(file);
		Sheet sheet = workbook.getSheet("TRn");

		// TRn[n]

		double[] TRn = new double[instanz.getN()];

		for (int i = 0; i < instanz.getN(); i++) {

			Cell cell1 = sheet.getCell(1, i + 1);
			NumberCell cell2 = (NumberCell) cell1;
			double cell3 = cell2.getValue();

			TRn[i] = cell3;

		}

		instanz.setCorporateTax(TRn);
	}

	
	/**
	 * Reads massbalance information
	 * @param instanz
	 * @throws BiffException
	 * @throws IOException
	 */
	public static void readMassbalance(Data instanz) throws BiffException, IOException {

		File file;
		Workbook workbook;
		choosePaths();
		file = new File(path);

		workbook = Workbook.getWorkbook(file);
		Sheet sheet = workbook.getSheet("MassBalance");

		// sigma[i][f]

		double[][] sigma = new double[instanz.getI()][instanz.getF()];

		for (int i = 0; i < instanz.getI(); i++) {
			for (int j = 0; j < instanz.getF(); j++) {

				Cell cell1 = sheet.getCell(i + 1, j + 1);
				NumberCell cell2 = (NumberCell) cell1;
				double cell3 = cell2.getValue();

				sigma[i][j] = cell3;

			}

		}
		instanz.setMaterialCoefficient(sigma);
	}
	

	/**
	 * Reads facility information
	 * @param instanz
	 * @throws BiffException
	 * @throws IOException
	 */
	public static void readDataF(Data instanz) throws BiffException, IOException {

		File file;
		Workbook workbook;
		choosePaths();
		file = new File(path);

		workbook = Workbook.getWorkbook(file);
		Sheet sheet = workbook.getSheet("DataF");

		// DataF[f]: Q_U_f, q_L_f, X_L_f, MC_f

		double[] QU = new double[instanz.getF()];

		for (int i = 0; i < instanz.getF(); i++) {
			Cell cell1 = sheet.getCell(1, i + 1);
			NumberCell cell2 = (NumberCell) cell1;
			double cell3 = cell2.getValue();

			QU[i] = cell3;

		}

		instanz.setUpperLimitCapacity(QU);

		double[] qL = new double[instanz.getF()];

		for (int i = 0; i < instanz.getF(); i++) {
			Cell cell1 = sheet.getCell(2, i + 1);
			NumberCell cell2 = (NumberCell) cell1;
			double cell3 = cell2.getValue();

			qL[i] = cell3;

		}

		instanz.setLowerLimitExpansionSize(qL);

		double[] Mcf = new double[instanz.getF()];

		for (int i = 0; i < instanz.getF(); i++) {
			Cell cell1 = sheet.getCell(3, i + 1);
			NumberCell cell2 = (NumberCell) cell1;
			double cell3 = cell2.getValue();

			Mcf[i] = cell3;

		}

		instanz.setVariableProductionCosts(Mcf);

	}

	
	/**
	 * Reads basic information for demand 
	 * @param instanz
	 * @throws BiffException
	 * @throws IOException
	 */
	public static void readDictBasics(Data instanz) throws BiffException, IOException {

		File file;
		Workbook workbook;
		choosePaths();
		file = new File(path);

		workbook = Workbook.getWorkbook(file);
		Sheet sheet = workbook.getSheet("Dict_Basics");

		// DictBasics[c]:

		double[] DemandM = new double[instanz.getF()];

		for (int i = 0; i < instanz.getF(); i++) {

			Cell cell1 = sheet.getCell(2, i + 2);
			NumberCell cell2 = (NumberCell) cell1;
			double cell3 = cell2.getValue();

			DemandM[i] = cell3;

		}

		instanz.setDemandM(DemandM);

		double[] DemandR = new double[instanz.getF()];

		for (int i = 0; i < instanz.getF(); i++) {

			Cell cell1 = sheet.getCell(3, i + 2);
			NumberCell cell2 = (NumberCell) cell1;
			double cell3 = cell2.getValue();

			DemandR[i] = cell3;

		}

		instanz.setDemandR(DemandR);

	}
	
	
	/**
	 * Calculates demand, price, budget and write results in Excel-file
	 * @param instanz
	 * @param index
	 * @throws BiffException
	 * @throws IOException
	 * @throws WriteException
	 */
	public static void calculateParameters(Data instanz, int index) throws BiffException, IOException, WriteException {

		File file;
		Workbook workbook;
		choosePaths();
		file = new File(path);

		workbook = Workbook.getWorkbook(file);
		Sheet sheet2 = workbook.getSheet("Pif_M");
		Sheet sheet3 = workbook.getSheet("Pif_R");

		// P[i][f][t]

		double[][][] P = new double[instanz.getI()][instanz.getF()][instanz.getT()];

		for (int i = 0; i < instanz.getI(); i++) {
			for (int j = 0; j < instanz.getF(); j++) {
				for (int k = 0; k < instanz.getT(); k++) {
					// Pift Monopoly
					Cell cell1 = sheet2.getCell(i + 1, j + 1);
					NumberCell cell2 = (NumberCell) cell1;
					double cell3 = cell2.getValue();
					// Pift Regular Market
					Cell cell4 = sheet3.getCell(i + 1, j + 1);
					NumberCell cell5 = (NumberCell) cell4;
					double cell6 = cell5.getValue();

					if (k < instanz.getRemainingTimeofClinicalTrials()) {
						P[i][j][k] = 0;

					} else if (k < (instanz.getRemainingTimeofClinicalTrials() + instanz.getTimeM())) {
						P[i][j][k] = cell3;

					} else if (k <= instanz.getT()) {
						P[i][j][k] = cell6;

					}
					
				}

			}
		}
		
		instanz.setUnitSellingPrice(P);

		File file2;
		Workbook workbook2;
		choosePaths();
		file2 = new File(path);

		workbook2 = Workbook.getWorkbook(file2);
		Sheet sheet = workbook2.getSheet("Const.");

		// budget
		Cell cell4 = sheet.getCell(1, 2);
		NumberCell cell5 = (NumberCell) cell4;
		double cell6 = cell5.getValue();

		double budget[] = new double[instanz.getT()];
		budget[0] = cell6;
		for (int i = 1; i < instanz.getT(); i++) {
			budget[i] = 0;
		}
		instanz.setCapitalBudget(budget);

		File file1;
		WritableWorkbook writableWorkbook;
		Workbook workbook1;
		choosePaths();
		file1 = new File(path);

		workbook1 = Workbook.getWorkbook(file1);
		writableWorkbook = Workbook.createWorkbook(file1, workbook1);

		WritableSheet sheet0 = writableWorkbook.getSheet("Dict");
		WritableSheet sheet1 = writableWorkbook.getSheet("Dict_final");

		// headings

		if (index == 1) {
			for (int i = 0; i < instanz.getT(); i++) {
				Number label3 = new Number(i + 1, 1, i + 1);
				sheet0.addCell(label3);

			}
		}

		else if (index == 2) {
			for (int i = 0; i < instanz.getT(); i++) {
				Number label3 = new Number(i + 1, 1, i + 1);
				sheet1.addCell(label3);

			}
		}

		// final product
		if (index == 1) {
			Number label30 = new Number(1, 0, instanz.getI());
			sheet0.addCell(label30);
		} else if (index == 2) {
			Number label30 = new Number(1, 0, instanz.getI());
			sheet1.addCell(label30);
		}

		// Dict or Dict_final
		if (index == 1) {
			double[][][] Dict = new double[instanz.getI()][instanz.getF()][instanz.getT()];
			for (int i = 0; i < instanz.getI(); i++) {
				for (int j = 0; j < instanz.getF(); j++) {
					for (int k = 0; k < instanz.getT(); k++) {
						if (i == instanz.getI() - 1 && k < instanz.getRemainingTimeofClinicalTrials()) {
							Dict[i][j][k] = 0;
							Number label3 = new Number(k + 1, j + 2, Dict[i][j][k]);
							sheet0.addCell(label3);

						} else if (i == instanz.getI() - 1
								&& k < (instanz.getRemainingTimeofClinicalTrials() + instanz.getTimeM())) {
							Dict[i][j][k] = instanz.getDemandM()[j];
							Number label3 = new Number(k + 1, j + 2, Dict[i][j][k]);
							sheet0.addCell(label3);

						} else if (i == instanz.getI() - 1 && k <= instanz.getT()) {
							Dict[i][j][k] = instanz.getDemandR()[j];
							Number label3 = new Number(k + 1, j + 2, Dict[i][j][k]);
							sheet0.addCell(label3);

						}

						else {
							Dict[i][j][k] = 0;

						}
					}
				}
			}
			instanz.setDemand(Dict);
		} else if (index == 2) {
			double[][][] Dict = new double[instanz.getI()][instanz.getF()][instanz.getT()];
			for (int i = 0; i < instanz.getI(); i++) {
				for (int j = 0; j < instanz.getF(); j++) {
					for (int k = 0; k < instanz.getT(); k++) {
						if (i == instanz.getI() - 1 && k < instanz.getRemainingTimeofClinicalTrials()) {
							Dict[i][j][k] = 0;
							Number label3 = new Number(k + 1, j + 2, Dict[i][j][k]);
							sheet1.addCell(label3);

						} else if (i == instanz.getI() - 1
								&& k < (instanz.getRemainingTimeofClinicalTrials() + instanz.getTimeM())) {
							Dict[i][j][k] = instanz.getDemandM()[j];
							Number label3 = new Number(k + 1, j + 2, Dict[i][j][k]);
							sheet1.addCell(label3);

						} else if (i == instanz.getI() - 1 && k <= instanz.getT()) {
							Dict[i][j][k] = instanz.getDemandR()[j];
							Number label3 = new Number(k + 1, j + 2, Dict[i][j][k]);
							sheet1.addCell(label3);

						}

						else {
							Dict[i][j][k] = 0;

						}
					}
				}
			}
			instanz.setDemand(Dict);
		}

		writableWorkbook.write();
		writableWorkbook.close();
		workbook1.close();

	}
	
	
	/**
	 *Reads supplier information
	 * @param instanz
	 * @throws BiffException
	 * @throws IOException
	 */
	public static void readSis(Data instanz) throws BiffException, IOException {

		File file;
		Workbook workbook;
		choosePaths();
		file = new File(path);

		workbook = Workbook.getWorkbook(file);
		Sheet sheet = workbook.getSheet("Sis");

		// read S[i][s]

		double[][] supply = new double[instanz.getI()][instanz.getF()];

		for (int i = 0; i < instanz.getF(); i++) {
			for (int j = 0; j < instanz.getI(); j++) {

				Cell cell1 = sheet.getCell(i + 1, j + 1);
				NumberCell cell2 = (NumberCell) cell1;
				double cell3 = cell2.getValue();

				supply[j][i] = cell3;

			}

		}
		instanz.setSupply(supply);
	}


	/**
	 * Reads cost insurance and freight
	 * @param instanz
	 * @throws BiffException
	 * @throws IOException
	 */
	public static void readCIFsf(Data instanz) throws BiffException, IOException {

		File file;
		Workbook workbook;
		choosePaths();
		file = new File(path);

		workbook = Workbook.getWorkbook(file);
		Sheet sheet1 = workbook.getSheet("CIF1sf");
		Sheet sheet2 = workbook.getSheet("CIF2sf");
		Sheet sheet3 = workbook.getSheet("CIF3sf");
		Sheet sheet4 = workbook.getSheet("CIF4sf");

		// read CIF[i][s][f]

		double[][][] CIF = new double[instanz.getI() - 1][instanz.getF()][instanz.getF()];

		for (int i = 0; i < instanz.getI(); i++) {// i
			for (int j = 0; j < instanz.getF(); j++) {// s
				for (int k = 0; k < instanz.getF(); k++) {// f

					if (i == 0) {
						Cell cell1 = sheet1.getCell(k + 1, j + 1);
						NumberCell cell2 = (NumberCell) cell1;
						double cell3 = cell2.getValue();
						CIF[i][j][k] = cell3;
					} else if (i == 1) {
						Cell cell1 = sheet2.getCell(k + 1, j + 1);
						NumberCell cell2 = (NumberCell) cell1;
						double cell3 = cell2.getValue();
						CIF[i][j][k] = cell3;
					} else if (i == 2) {
						Cell cell1 = sheet3.getCell(k + 1, j + 1);
						NumberCell cell2 = (NumberCell) cell1;
						double cell3 = cell2.getValue();
						CIF[i][j][k] = cell3;
					} else if (i == 3) {
						Cell cell1 = sheet4.getCell(k + 1, j + 1);
						NumberCell cell2 = (NumberCell) cell1;
						double cell3 = cell2.getValue();
						CIF[i][j][k] = cell3;
					}

				}

			}
		}
		instanz.setCostInsuranceFreight(CIF);
	}
	

	/**
	 * Reads import duty rates
	 * @param instanz
	 * @throws BiffException
	 * @throws IOException
	 */
	public static void readIDsf(Data instanz) throws BiffException, IOException {

		File file;
		Workbook workbook;
		choosePaths();
		file = new File(path);

		workbook = Workbook.getWorkbook(file);
		Sheet sheet = workbook.getSheet("IDsf");

		// read IDsf[s][f]

		double[][] ID = new double[instanz.getF()][instanz.getF()];

		for (int i = 0; i < instanz.getF(); i++) {// s
			for (int j = 0; j < instanz.getF(); j++) {// f

				Cell cell1 = sheet.getCell(j + 1, i + 1);
				NumberCell cell2 = (NumberCell) cell1;
				double cell3 = cell2.getValue();

				ID[i][j] = cell3;

			}

		}
		instanz.setImportDuty(ID);
	}

	
	/**
	 * Writes solution of Location Planning Modelin Result Excel
	 * @param instanz
	 * @throws BiffException
	 * @throws IOException
	 * @throws WriteException
	 */
	public static void writeSolutionInResultFile(Data instanz) throws BiffException, IOException, WriteException {

		File file;
		WritableWorkbook writableWorkbook;
		Workbook workbook;
		choosePaths();

		file = new File(pathR);

		workbook = Workbook.getWorkbook(file);
		writableWorkbook = Workbook.createWorkbook(file, workbook);

		WritableSheet sheet = writableWorkbook.getSheet("yft");
		WritableSheet sheet1 = writableWorkbook.getSheet("zft");
		WritableSheet sheet2 = writableWorkbook.getSheet("TInt");
		WritableSheet sheet3 = writableWorkbook.getSheet("GIft");
		WritableSheet sheet4 = writableWorkbook.getSheet("CEt");
		WritableSheet sheet5 = writableWorkbook.getSheet("Qft");
		WritableSheet sheet6 = writableWorkbook.getSheet("delta_qft");
		WritableSheet sheet7 = writableWorkbook.getSheet("Xft");

		// clear sheets
		int rows = sheet.getRows();
		int r = 0;

		while (r <= rows) {

			sheet.removeRow(0);
			r++;
		}
		int rows1 = sheet1.getRows();
		int r1 = 0;

		while (r1 <= rows1) {

			sheet1.removeRow(0);
			r1++;
		}
		int rows2 = sheet2.getRows();
		int r2 = 0;

		while (r2 <= rows2) {

			sheet2.removeRow(0);
			r2++;
		}
		int rows3 = sheet3.getRows();
		int r3 = 0;

		while (r3 <= rows3) {

			sheet3.removeRow(0);
			r3++;
		}
		int rows4 = sheet4.getRows();
		int r4 = 0;

		while (r4 <= rows4) {

			sheet4.removeRow(0);
			r4++;
		}

		int rows5 = sheet5.getRows();
		int r5 = 0;
		while (r5 <= rows5) {

			sheet5.removeRow(0);
			r5++;
		}

		int rows6 = sheet6.getRows();
		int r6 = 0;
		while (r6 <= rows6) {

			sheet6.removeRow(0);
			r6++;
		}
		int rows7 = sheet7.getRows();
		int r7 = 0;
		while (r7 <= rows7) {

			sheet7.removeRow(0);
			r7++;
		}

		// yft
		// Headings
		Label label4 = new Label(0, 0, "f/t");
		sheet.addCell(label4);

		for (int i = 0; i < instanz.getF(); i++) {
			Number label3 = new Number(0, i + 1, i + 1);
			sheet.addCell(label3);

		}
		for (int i = 0; i < instanz.getT(); i++) {
			Number label3 = new Number(i + 1, 0, i + 1);
			sheet.addCell(label3);

		}
		// Results
		for (int i = 0; i < instanz.getF(); i++) {
			for (int j = 0; j < instanz.getT(); j++) {
				if (instanz.getIF()[i] && instanz.getPIF()[i]) {

					Number label3 = new Number(j + 1, i + 1,
							instanz.getResult_constructionStartPrimaryFacility()[i][j]);
					sheet.addCell(label3);
				} else {
					Number label3 = new Number(j + 1, i + 1, 0);
					sheet.addCell(label3);

				}

			}
		}

		// zft
		// Headings
		Label label5 = new Label(0, 0, "f/t");
		sheet1.addCell(label5);

		for (int i = 0; i < instanz.getF(); i++) {
			Number label3 = new Number(0, i + 1, i + 1);
			sheet1.addCell(label3);

		}
		for (int i = 0; i < instanz.getT(); i++) {
			Number label3 = new Number(i + 1, 0, i + 1);
			sheet1.addCell(label3);

		}

		// Result
		for (int i = 0; i < instanz.getF(); i++) {
			for (int j = 0; j < instanz.getT(); j++) {
				if (instanz.getIF()[i] && instanz.getSIF()[i]) {

					Number label3 = new Number(j + 1, i + 1,
							instanz.getResult_constructionStartSecondaryFacility()[i][j]);
					sheet1.addCell(label3);
				} else {
					Number label3 = new Number(j + 1, i + 1, 0);
					sheet1.addCell(label3);

				}

			}
		}

		// TInt

		// Headings
		Label label6 = new Label(0, 0, "n/t");
		sheet2.addCell(label6);

		for (int i = 0; i < instanz.getN(); i++) {
			Number label3 = new Number(0, i + 1, i + 1);
			sheet2.addCell(label3);

		}
		for (int i = 0; i < instanz.getT(); i++) {
			Number label3 = new Number(i + 1, 0, i + 1);
			sheet2.addCell(label3);

		}
		// Result
		for (int i = 0; i < instanz.getN(); i++) {
			for (int j = 0; j < instanz.getT(); j++) {

				Number label3 = new Number(j + 1, i + 1, instanz.getResult_taxableIncome()[i][j]);
				sheet2.addCell(label3);

			}
		}

		// GIft

		// Headings
		Label label7 = new Label(0, 0, "f/t");
		sheet3.addCell(label7);

		for (int i = 0; i < instanz.getF(); i++) {
			Number label3 = new Number(0, i + 1, i + 1);
			sheet3.addCell(label3);

		}
		for (int i = 0; i < instanz.getT(); i++) {
			Number label3 = new Number(i + 1, 0, i + 1);
			sheet3.addCell(label3);

		}

		// Result
		for (int i = 0; i < instanz.getF(); i++) {
			for (int j = 0; j < instanz.getT(); j++) {
				if (instanz.getIF()[i]) {
					Number label3 = new Number(j + 1, i + 1, instanz.getResult_grossIncome()[i][j]);
					sheet3.addCell(label3);

				} else {
					Number label3 = new Number(j + 1, i + 1, 0);
					sheet3.addCell(label3);
				}

			}
		}

		// CEt

		// Headings
		Label label8 = new Label(0, 0, "t");
		sheet4.addCell(label8);

		for (int i = 0; i < instanz.getT(); i++) {
			Number label90 = new Number(i + 1, 0, i + 1);
			sheet4.addCell(label90);

		}

		// Result

		for (int j = 0; j < instanz.getT(); j++) {

			Number label3 = new Number(j + 1, 1, instanz.getResult_capitalExpenditure()[j]);
			sheet4.addCell(label3);

		}

		// Qft, delta_qft, Xft

		// Headings
		Label label9 = new Label(0, 0, "f/t");
		sheet5.addCell(label9);
		Label label80 = new Label(0, 0, "f/t");
		sheet6.addCell(label80);
		Label label800 = new Label(0, 0, "f/t");
		sheet7.addCell(label800);

		for (int i = 0; i < instanz.getF(); i++) {
			Number label90 = new Number(0, i + 1, i + 1);
			sheet5.addCell(label90);
			Number label900 = new Number(0, i + 1, i + 1);
			sheet6.addCell(label900);
			Number label9000 = new Number(0, i + 1, i + 1);
			sheet7.addCell(label9000);

		}
		for (int i = 0; i < instanz.getT(); i++) {
			Number label90 = new Number(i + 1, 0, i + 1);
			sheet5.addCell(label90);
			Number label900 = new Number(i + 1, 0, i + 1);
			sheet6.addCell(label900);
			Number label9000 = new Number(i + 1, 0, i + 1);
			sheet7.addCell(label9000);

		}

		// Result
		for (int i = 0; i < instanz.getF(); i++) {
			for (int j = 0; j < instanz.getT(); j++) {

				Number label3 = new Number(j + 1, i + 1, instanz.getResult_availableProductionCapacity()[i][j]);
				sheet5.addCell(label3);

				Number label30 = new Number(j + 1, i + 1, instanz.getResult_deltaCapacityExpansion()[i][j]);
				sheet6.addCell(label30);

				Number label300 = new Number(j + 1, i + 1, instanz.getResult_consumedOrProducedAPI()[i][j]);
				sheet7.addCell(label300);

			}
		}

		
		writableWorkbook.write();
		writableWorkbook.close();
		workbook.close();

	}
	

	/**
	 * Writes solution of Location Planning Model: replanning at the end of the clinical trials
	 * @param instanz
	 * @param tab
	 * @throws BiffException
	 * @throws IOException
	 * @throws WriteException
	 */
	public static void writeSolutionLocationModelReplanning(Data instanz, String tab)
			throws BiffException, IOException, WriteException {

		File file;
		WritableWorkbook writableWorkbook;
		Workbook workbook;
		choosePaths();

		file = new File(pathOutput);

		workbook = Workbook.getWorkbook(file);
		writableWorkbook = Workbook.createWorkbook(file, workbook);

		WritableSheet sheet = writableWorkbook.getSheet(tab);

		// Time horizon
		Number label3 = new Number(1, 5, instanz.getT());
		sheet.addCell(label3);

		// Revenue
		double revenue = 0;
		for (int i = 0; i < instanz.getI(); i++) {
			for (int j = 0; j < instanz.getF(); j++) {
				for (int k = 0; k < instanz.getF(); k++) {
					for (int l = 0; l < instanz.getT(); l++) {

						revenue += (instanz.getUnitSellingPrice()[i][j][l]
								* instanz.getResult_shippedMaterialUnits()[i][j][k][l]);

					}
				}
			}
		}
		Number label40 = new Number(1, 8, revenue);
		sheet.addCell(label40);

		// Net present value
		Number label30 = new Number(1, 9, instanz.getResult_netPresentValue());
		sheet.addCell(label30);

		// Primary
		for (int i = 0; i < instanz.getF(); i++) {
			for (int j = 0; j < instanz.getT(); j++) {

				if (instanz.getResult_constructionStartPrimaryFacility()[i][j] == 1) {
					Number label4 = new Number(1, 13, i + 1);
					sheet.addCell(label4);

					// Nation
					int nation = -1;
					for (int k = 0; k < instanz.getN(); k++) {
						if (instanz.getFn()[i][k]) {
							nation = (k + 1);
							Number label5 = new Number(2, 13, nation);
							sheet.addCell(label5);
						}
					}
					// Total Capacity
					Number label5 = new Number(3, 13,
							instanz.getResult_availableProductionCapacity()[i][instanz.getT() - 1]);
					sheet.addCell(label5);

					// Production Monopoly and Regular Market
					Number label500 = new Number(5, 13,
							instanz.getResult_consumedOrProducedAPI()[i][instanz.getRemainingTimeofClinicalTrials()+instanz.getTimeM()-1]);
					sheet.addCell(label500);

					Number label501 = new Number(6, 13,
							instanz.getResult_consumedOrProducedAPI()[i][instanz.getRemainingTimeofClinicalTrials()
									+ instanz.getTimeM()+instanz.getTimeR()-1]);
					sheet.addCell(label501);

					// Assumed GrossIncome
					double grossincome = 0;

					for (int l = 0; l < instanz.getT(); l++) {
						grossincome += instanz.getResult_grossIncome()[i][l];
					}

					Number label6 = new Number(4, 13, grossincome);
					sheet.addCell(label6);

					// Construction costs
					Number label7 = new Number(1, 20, instanz.getResult_capitalExpenditure()[j]);
					sheet.addCell(label7);

				}
			}
		}

		// Secondaries
		int counter = 0;
		for (int i = 0; i < instanz.getF(); i++) {
			for (int j = 0; j < instanz.getT(); j++) {
				if (instanz.getResult_constructionStartSecondaryFacility()[i][j] == 1) {
					Number label4 = new Number(1, 15 + counter, i + 1);
					sheet.addCell(label4);

					// Nation
					int nation = -1;
					for (int k = 0; k < instanz.getN(); k++) {
						if (instanz.getFn()[i][k]) {
							nation = (k + 1);
							Number label5 = new Number(2, 15 + counter, nation);
							sheet.addCell(label5);
						}
					}

					// Capacity
					Number label5 = new Number(3, 15 + counter,
							instanz.getResult_availableProductionCapacity()[i][instanz.getT() - 1]);
					sheet.addCell(label5);

					// Production Monopoly and Regular Market
					Number label500 = new Number(5, 15 + counter,
							instanz.getResult_consumedOrProducedAPI()[i][instanz.getRemainingTimeofClinicalTrials()+instanz.getTimeM()-1]);
					sheet.addCell(label500);

					Number label501 = new Number(6, 15 + counter,
							instanz.getResult_consumedOrProducedAPI()[i][instanz.getRemainingTimeofClinicalTrials()
									+ instanz.getTimeM()+instanz.getTimeR()-1]);
					sheet.addCell(label501);

					// GrossIncome
					double grossincome = 0;
					for (int l = 0; l < instanz.getT(); l++) {
						grossincome += instanz.getResult_grossIncome()[i][l];
					}

					Number label6 = new Number(4, 15 + counter, grossincome);
					sheet.addCell(label6);

					// Construction costs
					if (counter == 0) {
						Number label8 = new Number(1, 21, instanz.getResult_capitalExpenditure()[j]);
						sheet.addCell(label8);
					}

					counter++;

				}
			}

		}

		// Assumed Production Cost

		double productionCost = 0;
		for (int i = 0; i < instanz.getF(); i++) {
			for (int l = 0; l < instanz.getT(); l++) {
				productionCost += (instanz.getVariableProductionCosts()[i]
						* instanz.getResult_consumedOrProducedAPI()[i][l]);
			}
		}
		Number label7 = new Number(1, 22, productionCost);
		sheet.addCell(label7);

		// Assumed corporate Tax
		double corporateTax = 0;

		for (int m = 0; m < instanz.getN(); m++) {
			for (int l = 0; l < instanz.getT(); l++) {
				corporateTax += (instanz.getResult_taxableIncome()[m][l] * instanz.getCorporateTax()[m]);
			}
		}

		Number label9 = new Number(1, 23, corporateTax);
		sheet.addCell(label9);

		// Custom Duties
		double customDuties = 0;

		for (int m = 0; m < instanz.getF(); m++) {
			for (int l = 0; l < instanz.getT(); l++) {
				customDuties += (instanz.getResult_grossIncome()[m][l]);

			}
		}
		customDuties += productionCost;
		customDuties -= revenue;
		Number label10 = new Number(1, 24, -customDuties);
		sheet.addCell(label10);

		// close workbook
		writableWorkbook.write();
		writableWorkbook.close();
		workbook.close();
	}

	
	/**
	 * Writes solution of Location Planning Model: first investment decision
	 * @param instanz_preplanning
	 * @param tab
	 * @throws BiffException
	 * @throws IOException
	 * @throws WriteException
	 */
	public static void writeSolutionLocationModelFirstInvestmentDecision(Data instanz_preplanning, String tab)
			throws BiffException, IOException, WriteException {

		File file;
		WritableWorkbook writableWorkbook;
		Workbook workbook;
		choosePaths();

		file = new File(pathOutput);

		workbook = Workbook.getWorkbook(file);
		writableWorkbook = Workbook.createWorkbook(file, workbook);

		WritableSheet sheet = writableWorkbook.getSheet(tab);

		// Time horizon first investment decision

		Number label100 = new Number(9, 5, instanz_preplanning.getT());
		sheet.addCell(label100);

		// Revenue first investment decision

		double revenue_pp = 0;

		for (int i = 0; i < instanz_preplanning.getI(); i++) {
			for (int j = 0; j < instanz_preplanning.getF(); j++) {
				for (int k = 0; k < instanz_preplanning.getF(); k++) {
					for (int l = 0; l < instanz_preplanning.getT(); l++) {

						revenue_pp += (instanz_preplanning.getUnitSellingPrice()[i][j][l]
								* instanz_preplanning.getResult_shippedMaterialUnits()[i][j][k][l]);

					}
				}
			}
		}

		Number label400 = new Number(9, 8, revenue_pp);
		sheet.addCell(label400);

		// Net present value first investment decision

		Number label300 = new Number(9, 9, instanz_preplanning.getResult_netPresentValue());
		sheet.addCell(label300);

		// Primary first investment decision
		for (int i = 0; i < instanz_preplanning.getF(); i++) {
			for (int j = 0; j < instanz_preplanning.getT(); j++) {

				if (instanz_preplanning.getResult_constructionStartPrimaryFacility()[i][j] == 1) {
					Number label4 = new Number(9, 13, i + 1);
					sheet.addCell(label4);

					// Nation first investment decision
					int nation = -1;
					for (int k = 0; k < instanz_preplanning.getN(); k++) {
						if (instanz_preplanning.getFn()[i][k]) {
							nation = (k + 1);
							Number label5 = new Number(10, 13, nation);
							sheet.addCell(label5);
						}
					}
					// Total Capacity first investment decision
					Number label5 = new Number(11, 13,
							instanz_preplanning.getResult_availableProductionCapacity()[i][instanz_preplanning.getT()
									- 1]);
					sheet.addCell(label5);

					// Production Monopoly and Regular Market first investment decision
					Number label500 = new Number(13, 13,
							instanz_preplanning.getResult_consumedOrProducedAPI()[i][instanz_preplanning
									.getRemainingTimeofClinicalTrials()+instanz_preplanning.getTimeM()-1]);
					sheet.addCell(label500);

					Number label501 = new Number(14, 13,
							instanz_preplanning.getResult_consumedOrProducedAPI()[i][instanz_preplanning
									.getRemainingTimeofClinicalTrials() + instanz_preplanning.getTimeM()+instanz_preplanning.getTimeR()-1]);
					sheet.addCell(label501);

					// Assumed GrossIncome first investment decision
					double grossincome = 0;

					for (int l = 0; l < instanz_preplanning.getT(); l++) {
						grossincome += instanz_preplanning.getResult_grossIncome()[i][l];
					}

					Number label6 = new Number(12, 13, grossincome);
					sheet.addCell(label6);

					// Construction costs first investment decision
					Number label7 = new Number(9, 20, instanz_preplanning.getResult_capitalExpenditure()[j]);
					sheet.addCell(label7);

				}
			}
		}

		// Secondaries first investment decision
		int counter1 = 0;
		for (int i = 0; i < instanz_preplanning.getF(); i++) {
			for (int j = 0; j < instanz_preplanning.getT(); j++) {
				if (instanz_preplanning.getResult_constructionStartSecondaryFacility()[i][j] == 1) {
					Number label4 = new Number(9, 15 + counter1, i + 1);
					sheet.addCell(label4);

					// Nation first investment decision
					int nation = -1;
					for (int k = 0; k < instanz_preplanning.getN(); k++) {
						if (instanz_preplanning.getFn()[i][k]) {
							nation = (k + 1);
							Number label5 = new Number(10, 15 + counter1, nation);
							sheet.addCell(label5);
						}
					}

					// Capacity first investment decision
					Number label5 = new Number(11, 15 + counter1,
							instanz_preplanning.getResult_availableProductionCapacity()[i][instanz_preplanning.getT()
									- 1]);
					sheet.addCell(label5);

					// Production Monopoly and Regular Market first investment decision
					Number label500 = new Number(13, 15 + counter1,
							instanz_preplanning.getResult_consumedOrProducedAPI()[i][instanz_preplanning
									.getRemainingTimeofClinicalTrials()+instanz_preplanning.getTimeM()-1]);
					sheet.addCell(label500);

					Number label501 = new Number(14, 15 + counter1,
							instanz_preplanning.getResult_consumedOrProducedAPI()[i][instanz_preplanning
									.getRemainingTimeofClinicalTrials() + instanz_preplanning.getTimeM()+instanz_preplanning.getTimeR()-1]);
					sheet.addCell(label501);

					// GrossIncome first investment decision
					double grossincome = 0;
					for (int l = 0; l < instanz_preplanning.getT(); l++) {
						grossincome += instanz_preplanning.getResult_grossIncome()[i][l];
					}

					Number label6 = new Number(12, 15 + counter1, grossincome);
					sheet.addCell(label6);

					// Construction costs first investment decision
					if (counter1 == 0) {
						Number label8 = new Number(9, 21, instanz_preplanning.getResult_capitalExpenditure()[j]);
						sheet.addCell(label8);
					}

					counter1++;

				}
			}

		}

		// Assumed Production Cost first investment decision

		double productionCost1 = 0;
		for (int i = 0; i < instanz_preplanning.getF(); i++) {
			for (int l = 0; l < instanz_preplanning.getT(); l++) {
				productionCost1 += (instanz_preplanning.getVariableProductionCosts()[i]
						* instanz_preplanning.getResult_consumedOrProducedAPI()[i][l]);
			}
		}
		Number label70 = new Number(9, 22, productionCost1);
		sheet.addCell(label70);

		// Assumed corporate Tax
		double corporateTax1 = 0;

		for (int m = 0; m < instanz_preplanning.getN(); m++) {
			for (int l = 0; l < instanz_preplanning.getT(); l++) {
				corporateTax1 += (instanz_preplanning.getResult_taxableIncome()[m][l]
						* instanz_preplanning.getCorporateTax()[m]);
			}
		}

		Number label90 = new Number(9, 23, corporateTax1);
		sheet.addCell(label90);

		// Custom Duties first investment decision
		double customDuties1 = 0;

		for (int m = 0; m < instanz_preplanning.getF(); m++) {
			for (int l = 0; l < instanz_preplanning.getT(); l++) {
				customDuties1 += (instanz_preplanning.getResult_grossIncome()[m][l]);

			}
		}
		customDuties1 += productionCost1;
		customDuties1 -= revenue_pp;
		Number label1000 = new Number(9, 24, -customDuties1);
		sheet.addCell(label1000);

		writableWorkbook.write();
		writableWorkbook.close();
		workbook.close();

	}

	
	/**
	 * Exports Decision Planning Model data into an Excel file
	 * @param instance
	 * @param tab
	 * @throws BiffException
	 * @throws IOException
	 * @throws WriteException
	 */
	public static void writeSolutionDecisionPlanningModel(Data dataInstance, String tab)
			throws BiffException, IOException, WriteException {

		File file;
		WritableWorkbook writableWorkbook;
		Workbook workbook;
		choosePaths();

		file = new File(pathOutput);

		workbook = Workbook.getWorkbook(file);
		writableWorkbook = Workbook.createWorkbook(file, workbook);

		WritableSheet sheet = writableWorkbook.getSheet(tab);

		int column = 1;
		int row = 29;

		Number number;
		Label label;

		// Planning horizon (B30)

		number = new Number(column, row, dataInstance.getParameter_planningHorizon());
		sheet.addCell(number);

		// Total construction cost primary facility (B33)

		number = new Number(column, row + 3, dataInstance.getTotalConstructionCost_primary());
		sheet.addCell(number);

		// Total setup cost primary facility (B34)

		number = new Number(column, row + 4, dataInstance.getTotalSetUpCost_primary());
		sheet.addCell(number);

		// Total penalty cost primary facility (B35)

		number = new Number(column, row + 5, dataInstance.getTotalPenaltyCost_primary());
		sheet.addCell(number);

		// Total expansion cost primary facility (B36)

		number = new Number(column, row + 6, dataInstance.getTotalExpansionCost_primary());
		sheet.addCell(number);

		// Positive outcome (B39)

		label = new Label(column, row + 9, Boolean.toString(dataInstance.isSuccessOfClinicalTrials()));
		sheet.addCell(label);

		// Preliminary knowledge about successful test results (B40)

		number = new Number(column, row + 10, dataInstance.getParameter_preliminaryKnowledgeAboutSuccessfulTests());
		sheet.addCell(number);

		// Preliminary knowledge about failed test results (B41)

		number = new Number(column, row + 11, dataInstance.getParameter_preliminaryKnowledgeAboutFailedTests());
		sheet.addCell(number);

		// Test results (B44 - F44)

		for (int i = 1; i <= dataInstance.getParameter_planningHorizon(); i++) {

			number = new Number(column + i - 1, row + 14, dataInstance.getTestResults()[i]);
			sheet.addCell(number);
		}

		// Test probabilities (B45 - F54)

		for (int i = 1; i <= dataInstance.getParameter_planningHorizon(); i++) {

			number = new Number(column + i - 1, row + 15, dataInstance.getTestProbability()[i]);
			sheet.addCell(number);
		}

		// Investment decisions in period t (B49 - F49 -> B53 - F53)

		for (int i = 1; i <= dataInstance.getParameter_planningHorizon(); i++) {

			for (int j = 1; j <= dataInstance.getParameter_planningHorizon(); j++) {

				number = new Number(column + j - 1, row + 18 + i, dataInstance.getInvestmentStrategies()[i][j]);
				sheet.addCell(number);
			}
		}

		// Final investment decisions in period t (B55 - F55)

		for (int i = 1; i <= dataInstance.getParameter_planningHorizon(); i++) {

			sheet.getCell(column + i, row + 25).getCellFormat();

			number = new Number(column + i - 1, row + 25, dataInstance.getInvestmentDecisionPrimaryFacility()[i]);
			sheet.addCell(number);
		}

		// close workbook
		writableWorkbook.write();
		writableWorkbook.close();
		workbook.close();
	}
	

	/**
	 * Prints an integer array with periods to the console
	 * @param array
	 * @param title
	 */
	public static void printArrayWithPeriodsInt(int[] array, String title) {

		System.out.println("\n\n" + title);

		System.out.println("--------------------------------------------------------------------------");

		System.out.print("Period ");

		for (int i = 0; i < array.length; i++) {

			System.out.print("| " + i + "\t");
		}

		System.out.println("");

		System.out.print("Result ");

		for (int i = 0; i < array.length; i++) {

			System.out.print("| " + array[i] + "\t");
		}
	}
	
	
	/**
	 * Prints an double array with periods to the console
	 * @param array
	 * @param title
	 */
	public static void printArrayWithPeriodsDouble(double[] array, String title) {

		System.out.println("\n\n" + title);

		System.out.println("--------------------------------------------------------------------------");

		System.out.print("Period ");

		for (int i = 0; i < array.length; i++) {

			System.out.print("| " + i + "\t");
		}

		System.out.println("");

		System.out.print("Result ");

		for (int i = 0; i < array.length; i++) {

			double tmp = Math.round(array[i] * 100.00);
			tmp = tmp / 100;

			System.out.print("| " + tmp + "\t");
		}
	}

	
	/**
	 * Prints an array to the console
	 * @param array
	 * @param title
	 */
	public static void printArraySimple(int[] array) {

		System.out.print("\n| ");

		for (int i = 0; i < array.length; i++) {

			System.out.print(array[i] + "\t" + "|");
		}

		System.out.println("");
	}

	
	/**
	 * Prints a strategy list to the console with regards to a period
	 * @param strategies
	 */
	public static void printStrategies(ArrayList<int[]> strategies, int period) {

		System.out.println("\n\n--------------------------------------------------------------------------------");

		System.out.println("\nStrategies for period: " + period);

		for (int i = 0; i < strategies.size(); i++) {

			ReadAndWrite.printArraySimple(strategies.get(i));
		}

		System.out.println("\n--------------------------------------------------------------------------------\n\n");
	}

	
	/**
	 * Prints a scenario tree to the console
	 * @param scenarioTree
	 */
	public static void printScenarioTree(ArrayList<ArrayList<Event>> scenarioTree) {

		for (int t = 0; t < scenarioTree.size(); t++) {

			for (int index = 0; index < scenarioTree.get(t).size(); index++) {

				System.out.println(scenarioTree.get(t).get(index).toString());
			}
		}
	}

}
