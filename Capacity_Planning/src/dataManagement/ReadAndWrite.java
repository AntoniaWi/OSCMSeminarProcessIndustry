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
	
	//Paths Antonia #1
	public static String pathExcelAntonia = "/Users/antoniawiggert/Documents/GitHub/OSCMSeminarProcessIndustry/Capacity_Planning/lib/CaseData-Basic.xlsx";
	//Paths Sarah #2
	public static String pathExcelSarah ="/Users/antoniawiggert/Documents/GitHub/OSCMSeminarProcessIndustry/Capacity_Planning/lib/CaseData-Basic.xlsx";//TODO: Sarah Pfad eingeben
	//Paths Ramona #3
	public static String pathExcelRamona ="/Users/antoniawiggert/Documents/GitHub/OSCMSeminarProcessIndustry/Capacity_Planning/lib/CaseData-Basic.xlsx";//TODO: Sarah Pfad eingeben
	
	public static String path = "";
	
	
	
	public static void choosePaths () {	
	
		if (user ==1) {
			
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
		 *		- Sheet 0: DataTiming
		 *		- Sheet 1: F
		 *		- Sheet 2: F in N
		 *		- Sheet 3: IMf
		 *		- Sheet 4: OMf
		 *		- Sheet 5: Const.
		 *		- Sheet 6: TRn
		 *		- Sheet 7: MassBalance
		 *		- Sheet 8: DataF
		 *		- Sheet 9: Dict
		 *		- Sheet 10: Sis
		 *		- Sheet 11: Pif
		 *		- Sheet 12: CIF1sf
		 *		- Sheet 13: CIF2sf
		 *		- Sheet 13: CIF3sf
		 *		- Sheet 14: CIF4sf
		 *		- Sheet 15: CIF5sf
		 *		- Sheet 16: CIF6sf
		 *		- Sheet 17: CIF7sf
		 *		- Sheet 18: IDisf
		 */

		
		//____________________________________________________________________________________________
		
		//Methoden, um aus der Excel-Datei einzulesen
				
		//____________________________________________________________________________________________
		
		
		
		
		//____________________________________________________________________________________________
		
		//Sheet 0: DataTiming
					
		//____________________________________________________________________________________________
	public static void readDataTiming (Data instanz) throws BiffException, IOException {
		
		File file;
		Workbook workbook;
		choosePaths();
			file = new File (path);
			
			
			workbook = Workbook.getWorkbook(file);
			Sheet sheet = workbook.getSheet("DataTiming");
				
				
			//read planninghorizon T 
			
				Cell cell1 = sheet.getCell (0,1);
				NumberCell cell2 = (NumberCell) cell1;
				double cell3 = cell2.getValue();
				instanz.setParameter_planningHorizon((int)cell3);
					
					
			//read discount factor
				Cell cell4 = sheet.getCell (1,1);
					
				    	NumberCell cell5 = (NumberCell) cell4;
					double cell6 = cell5.getValue();
					instanz.setParameter_discountFactor(cell6);
					
					
									
			//read construction time sp0
				Cell cell7 = sheet.getCell (2,1);
						
					 NumberCell cell8 = (NumberCell) cell7;
					 double cell9 = cell8.getValue();
					 instanz.setParameter_monthsToBuildPrimaryFacilities((int)cell9);
					
						
			
			
			
					// return instanz;
			
		
		
	}	
		
		
	
	
	/**
	 * @param array
	 * @param title
	 */
	public static void printArrayWithPeriodsInt (int [] array, String title) {
		
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
	 * @param array
	 * @param title
	 */
	public static void printArrayWithPeriodsDouble (double [] array, String title) {
		
		System.out.println("\n\n" + title);
		
		System.out.println("--------------------------------------------------------------------------");
		
		System.out.print("Period ");
		
		for (int i = 0; i < array.length; i++) {
			
			System.out.print("| " + i + "\t");
		}
		
		System.out.println("");
		
		System.out.print("Result ");
		
		for (int i = 0; i < array.length; i++) {
			
			double tmp = Math.round(array[i]*100.00);
			tmp = tmp / 100;
			
			System.out.print("| " + tmp + "\t");
		}
	}
	
	
	/**
	 * 
	 * @param array
	 * @param title
	 */
	public static void printArraySimple (int [] array) {
		
		System.out.print("\n\n| ");
		
		for (int i = 0; i < array.length; i++) {
			
			System.out.print(array[i] + "\t" + "|");
		}
	}
	

}
