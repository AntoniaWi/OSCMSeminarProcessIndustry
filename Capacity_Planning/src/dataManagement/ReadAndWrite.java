package dataManagement;

import ilog.concert.IloException;
import ilog.cplex.IloCplex.UnknownObjectException;

import java.io.File;

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

import jxl.*;

import java.io.FileOutputStream;

public class ReadAndWrite {

	public static int user = 1;

	// Paths Antonia #1
	public static String pathExcelAntonia = "/Users/antoniawiggert/Documents/GitHub/OSCMSeminarProcessIndustry/Capacity_Planning/lib/CaseDataBasic.xls";
	// Paths Sarah #2
	public static String pathExcelSarah = "/Users/antoniawiggert/Documents/GitHub/OSCMSeminarProcessIndustry/Capacity_Planning/lib/CaseData-Basic.xlsx";// TODO:
																																						// Sarah
																																						// Pfad
																																						// eingeben
	// Paths Ramona #3
	public static String pathExcelRamona = "/Users/antoniawiggert/Documents/GitHub/OSCMSeminarProcessIndustry/Capacity_Planning/lib/CaseData-Basic.xlsx";// TODO:
																																							// Sarah
																																							// Pfad
																																							// eingeben

	public static String path = "";

	public static void choosePaths() {

		if (user == 1) {

			path = pathExcelAntonia;
		}

		else if (user == 2) {

			path = pathExcelSarah;
		}

		else if (user == 3) {

			path = pathExcelRamona;
		}

	}

	/*
	 * Excel-File:
	 * 
	 * - Sheet 0: DataTiming - Sheet 1: F - Sheet 2: F in N - Sheet 3: IMf - Sheet
	 * 4: OMf - Sheet 5: Const. - Sheet 6: TRn - Sheet 7: MassBalance - Sheet 8:
	 * DataF - Sheet 9: Dict - Sheet 10: Sis - Sheet 11: Pif - Sheet 12: CIF1sf -
	 * Sheet 13: CIF2sf - Sheet 13: CIF3sf - Sheet 14: CIF4sf - Sheet 15: CIF5sf -
	 * Sheet 18: IDisf
	 */

	// ____________________________________________________________________________________________

	// Methoden, um aus der Excel-Datei einzulesen

	// ____________________________________________________________________________________________

	// ____________________________________________________________________________________________

	// Sheet 0: DataTiming

	// ____________________________________________________________________________________________
	public static void readDataTiming(Data instanz) throws BiffException, IOException {

		File file;
		Workbook workbook;
		choosePaths();
		file = new File(path);

		workbook = Workbook.getWorkbook(file);
		Sheet sheet = workbook.getSheet("DataTiming");

		// read planninghorizon T

		Cell cell1 = sheet.getCell(1, 0);
		NumberCell cell2 = (NumberCell) cell1;
		double cell3 = cell2.getValue();
		instanz.setParameter_planningHorizon((int) cell3);

		// read discount factor
		Cell cell4 = sheet.getCell(1, 1);

		NumberCell cell5 = (NumberCell) cell4;
		double cell6 = cell5.getValue();
		instanz.setParameter_discountFactor(cell6);

		// read construction time sp0 und ss0
		Cell cell7 = sheet.getCell(1, 2);

		NumberCell cell8 = (NumberCell) cell7;
		double cell9 = cell8.getValue();
		instanz.setParameter_monthsToBuildPrimaryFacilities((int) cell9);

		Cell cell10 = sheet.getCell(1, 3);

		NumberCell cell11 = (NumberCell) cell10;
		double cell12 = cell11.getValue();
		instanz.setParameter_monthsToBuildSecondaryFacilities((int) cell12);

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
		
		//gamma_c
		
		Cell cell31 = sheet.getCell(1, 11);

		NumberCell cell32 = (NumberCell) cell31;
		double cell33 = cell32.getValue();
		instanz.setParameter_thresholdSuccessfulTests((int) cell33);
		
		//penaltyCost
		
		Cell cell34 = sheet.getCell(1, 8);

		NumberCell cell35 = (NumberCell) cell34;
		double cell36 = cell35.getValue();
		instanz.setParameter_penaltyCost((int) cell36);
		
		
		

	}

	// ____________________________________________________________________________________________

	// Sheet 1: F

	// ____________________________________________________________________________________________
	public static void readF(Data instanz) throws BiffException, IOException {

		File file;
		Workbook workbook;
		choosePaths();
		file = new File(path);

		workbook = Workbook.getWorkbook(file);
		Sheet sheet = workbook.getSheet("F");

		// read IF[]

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

		// read EF
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

	// ____________________________________________________________________________________________

	// Sheet 2: F in N

	// ____________________________________________________________________________________________
	public static void readFinN(Data instanz) throws BiffException, IOException {

		File file;
		Workbook workbook;
		choosePaths();
		file = new File(path);

		workbook = Workbook.getWorkbook(file);
		Sheet sheet = workbook.getSheet("F in N");

		// read Fn[f][n]

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
				// System.out.println(help1);
				// System.out.println("Fn["+i+"]["+j+"]");
				Fn[i][j] = help1;

			}

		}
		instanz.setFn(Fn);
	}
	// ____________________________________________________________________________________________

	// Sheet 3: IMf

	// ____________________________________________________________________________________________
	public static void readIMf(Data instanz) throws BiffException, IOException {

		File file;
		Workbook workbook;
		choosePaths();
		file = new File(path);

		workbook = Workbook.getWorkbook(file);
		Sheet sheet = workbook.getSheet("IMf");

		// read IM[f][i]

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

	// ____________________________________________________________________________________________

	// Sheet 4: OMf

	// ____________________________________________________________________________________________
	public static void readOMf(Data instanz) throws BiffException, IOException {

		File file;
		Workbook workbook;
		choosePaths();
		file = new File(path);

		workbook = Workbook.getWorkbook(file);
		Sheet sheet = workbook.getSheet("OMf");

		// read OM[f][i]

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

	// ____________________________________________________________________________________________

	// Sheet 5: Const.

	// ____________________________________________________________________________________________
	public static void readConst(Data instanz) throws BiffException, IOException {

		File file;
		Workbook workbook;
		choosePaths();
		file = new File(path);

		workbook = Workbook.getWorkbook(file);
		Sheet sheet = workbook.getSheet("Const.");

		// read projectLife

		Cell cell1 = sheet.getCell(1, 1);
		NumberCell cell2 = (NumberCell) cell1;
		double cell3 = cell2.getValue();

		instanz.setProjectLife((int) cell3);

		// read numberFacilities F
		Cell cell7 = sheet.getCell(1, 6);

		NumberCell cell8 = (NumberCell) cell7;
		double cell9 = cell8.getValue();
		instanz.setF((int) cell9);

		// read numberMaterials I
		Cell cell10 = sheet.getCell(1, 7);
		NumberCell cell11 = (NumberCell) cell10;
		double cell12 = cell11.getValue();
		instanz.setI((int) cell12);

		// read numberMonths T

		Cell cell13 = sheet.getCell(1, 8);
		NumberCell cell14 = (NumberCell) cell13;
		double cell15 = cell14.getValue();
		instanz.setT((int) cell15);

		// read numberOfNations N
		Cell cell16 = sheet.getCell(1, 9);
		NumberCell cell17 = (NumberCell) cell16;
		double cell18 = cell17.getValue();
		instanz.setN((int) cell18);

		// read budget
		Cell cell4 = sheet.getCell(1, 2);
		NumberCell cell5 = (NumberCell) cell4;
		double cell6 = cell5.getValue();

		double budget[] = new double[instanz.getT()];
		budget[0] = cell6;
		for (int i = 1; i < instanz.getT(); i++) {
			budget[i] = 0;
		}
		instanz.setCapitalBudget(budget);

		// read initial capacity Q0

		Cell cell19 = sheet.getCell(1, 3);

		NumberCell cell20 = (NumberCell) cell19;
		double cell21 = cell20.getValue();
		instanz.setInitialCapacity((int) cell21);

		// read API
		Cell cell22 = sheet.getCell(1, 4);
		NumberCell cell23 = (NumberCell) cell22;
		double cell24 = cell23.getValue();
		instanz.setAPI((int) cell24);

	}

	// ____________________________________________________________________________________________

	// Sheet 6: TRn

	// ____________________________________________________________________________________________
	public static void readTRn(Data instanz) throws BiffException, IOException {

		File file;
		Workbook workbook;
		choosePaths();
		file = new File(path);

		workbook = Workbook.getWorkbook(file);
		Sheet sheet = workbook.getSheet("TRn");

		// read TRn[n]

		double[] TRn = new double[instanz.getN()];

		for (int i = 0; i < instanz.getN(); i++) {

			Cell cell1 = sheet.getCell(1, i + 1);
			NumberCell cell2 = (NumberCell) cell1;
			double cell3 = cell2.getValue();

			TRn[i] = cell3;

		}

		instanz.setCorporateTax(TRn);
	}

	// ____________________________________________________________________________________________

	// Sheet 7: Mass-balance

	// ____________________________________________________________________________________________
	public static void readMassbalance(Data instanz) throws BiffException, IOException {

		File file;
		Workbook workbook;
		choosePaths();
		file = new File(path);

		workbook = Workbook.getWorkbook(file);
		Sheet sheet = workbook.getSheet("MassBalance");

		// read sigma[i][f]

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

	// ____________________________________________________________________________________________

	// Sheet 8: DataF

	// ____________________________________________________________________________________________
	public static void readDataF(Data instanz) throws BiffException, IOException {

		File file;
		Workbook workbook;
		choosePaths();
		file = new File(path);

		workbook = Workbook.getWorkbook(file);
		Sheet sheet = workbook.getSheet("DataF");

		// read DataF[f]: Q_U_f, q_L_f, X_L_f, MC_f

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

		double[] XLf = new double[instanz.getF()];

		for (int i = 0; i < instanz.getF(); i++) {
			Cell cell1 = sheet.getCell(3, i + 1);
			NumberCell cell2 = (NumberCell) cell1;
			double cell3 = cell2.getValue();

			XLf[i] = cell3;

		}

		instanz.setLowerLimitProductionAPI(XLf);

		double[] Mcf = new double[instanz.getF()];

		for (int i = 0; i < instanz.getF(); i++) {
			Cell cell1 = sheet.getCell(4, i + 1);
			NumberCell cell2 = (NumberCell) cell1;
			double cell3 = cell2.getValue();

			Mcf[i] = cell3;

		}

		instanz.setVariableProductionCosts(Mcf);

	}

	// ____________________________________________________________________________________________

	// Sheet 9: D_ict//TODO: fehlt

	// ____________________________________________________________________________________________

	// ____________________________________________________________________________________________

	// Sheet 10:Sis

	// ____________________________________________________________________________________________
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
	// ____________________________________________________________________________________________

	// Sheet 11:

	// ____________________________________________________________________________________________
	// ____________________________________________________________________________________________

	public static void readPif(Data instanz) throws BiffException, IOException {

		File file;
		Workbook workbook;
		choosePaths();
		file = new File(path);

		workbook = Workbook.getWorkbook(file);
		Sheet sheet = workbook.getSheet("Pif");

		// read P[i][f]

		double[][] P = new double[instanz.getI()][instanz.getF()];

		for (int i = 0; i < instanz.getI(); i++) {
			for (int j = 0; j < instanz.getF(); j++) {

				Cell cell1 = sheet.getCell(i + 1, j + 1);
				NumberCell cell2 = (NumberCell) cell1;
				double cell3 = cell2.getValue();

				P[i][j] = cell3;

			}

		}
		instanz.setUnitSellingPrice(P);
	}

	// Sheet 12: CIF1sf

	// ____________________________________________________________________________________________
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
		Sheet sheet5 = workbook.getSheet("CIF5sf");

		// read CIF[i][s][f]

		double[][][] CIF = new double[instanz.getI()][instanz.getF()][instanz.getF()];

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
					} else if (i == 4) {
						Cell cell1 = sheet5.getCell(k + 1, j + 1);
						NumberCell cell2 = (NumberCell) cell1;
						double cell3 = cell2.getValue();
						CIF[i][j][k] = cell3;
					}

				}

			}
		}
		instanz.setCostInsuranceFreight(CIF);
	}


	// ____________________________________________________________________________________________

	// Sheet 18:

	// ____________________________________________________________________________________________

	public static void readIDsf(Data instanz) throws BiffException, IOException {

		File file;
		Workbook workbook;
		choosePaths();
		file = new File(path);

		workbook = Workbook.getWorkbook(file);
		Sheet sheet = workbook.getSheet("IDisf");

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
	 * Should go into ReadAndWrite-Class later
	 * 
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
	 * 
	 * @param array
	 * @param title
	 */
	public static void printArraySimple(int[] array) {

		System.out.print("\n\n| ");

		for (int i = 0; i < array.length; i++) {

			System.out.print(array[i] + "\t" + "|");
		}
	}

}
