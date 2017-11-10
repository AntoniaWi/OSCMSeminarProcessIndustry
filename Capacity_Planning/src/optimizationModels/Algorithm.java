package optimizationModels;

import java.io.IOException;

import dataManagement.*;
import ilog.concert.IloException;
import jxl.read.biff.BiffException;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import ilog.concert.*;
import ilog.cplex.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import dataManagement.Data;
import dataManagement.ReadAndWrite;
import jxl.read.biff.BiffException;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class Algorithm {
	
	public static Data dataInstance;
	public static TimingModel timingModel;
	public static LocationPlanningModel locationPlanningModel;
	public static boolean firstInvestment = true;
	
	public static void main (String [] args) throws BiffException, IOException, WriteException, IloException, BiffException, RowsExceededException {
		
		dataInstance = new Data (1); 
		
		timingModel = new TimingModel(dataInstance);
		
		printModelInformation_Start();
		
		int period = 1;
		
		while (period <= dataInstance.getParameter_planningHorizon()) {
			
			nextPeriod();
			period++;
			
		}
		
		endOfModel();
		
		printModelInformation_End();
	}
	

	/**
	 * 
	 */
	public static void nextPeriod () throws BiffException, IOException, WriteException, IloException, BiffException, RowsExceededException {
		
		dataInstance.incrementCountPeriods();
		
		updateFormerKnowledge();
		
		timingModel.run();
		
		// TODO:
		
		if (dataInstance.getInvestmentDecisionPrimaryFacility()[dataInstance.getCountPeriods()] == 1 && firstInvestment == true ) {
			
			int tmp_remainingTime = (dataInstance.getParameter_planningHorizon() - dataInstance.getCountPeriods());
			dataInstance.setRemainingTimeofClinicalTrials(tmp_remainingTime+1);
			
			
			
			locationPlanningModel = new LocationPlanningModel(dataInstance);
			locationPlanningModel.run();
			firstInvestment = false;
		}
		
		newTestResult();
		
		printModelInformation_Period();
	}
	
	
	/**
	 * 
	 */
	public static void endOfModel () {
		
		dataInstance.incrementCountPeriods();
		
		updateFormerKnowledge();
		
	}
	
	/**
	 * 
	 */
	public static void updateFormerKnowledge () {
		
		if (dataInstance.getCountPeriods() > 1) {
			
			dataInstance.updateCountSuccessfulTests(dataInstance.getCountPeriods());
			dataInstance.updateCountFailedTests(dataInstance.getCountPeriods());
		}
	}
	
	
	/**
	 * 
	 */
	public static void newTestResult () {
		
		double p = dataInstance.calculateTestProbability();
		
		boolean newTestResult = StdRandom.bernoulli(p); 
		
		if (newTestResult == true) {
			
			dataInstance.getTestResults()[dataInstance.getCountPeriods() ] = 1;
		}
		
		else {
			
			dataInstance.getTestResults()[dataInstance.getCountPeriods() ] = 0;
		}	
	}
	
	
	/**
	 * 
	 */
	public static void printModelInformation_Start () {
		
		System.out.println("Timing Model starts with following parameters:");
		
		System.out.println("");
		
		System.out.println("Planning horizon (T): " + dataInstance.getParameter_planningHorizon());
		System.out.println("Discount factor (alpha): " + dataInstance.getParameter_discountFactor());
		
		System.out.println("Number of periods (year) to build a primary facility (s_p_0): " + dataInstance.getParameter_monthsToBuildPrimaryFacilities());
		System.out.println("Number of periods (year) to build a secondary facility (s_s_0): " + dataInstance.getParameter_monthsToBuildSecondaryFacilities());
		
		System.out.println("Construction cost of a primary facility (c_p): " + dataInstance.getParameter_constructionCostPrimaryFacility());
		System.out.println("Construction cost of a secondary facility (c_s): " + dataInstance.getParameter_constructionCostSecondaryFacility());
		
		System.out.println("Setup cost for a primary facility (K_p): " + dataInstance.getParameter_setupCostPrimaryFacility());
		System.out.println("Setup cost for a secondary facility (K_s): " + dataInstance.getParameter_setupCostSecondaryFacility());
		
		System.out.println("Penalty cost (Phi): " + dataInstance.getParameter_penaltyCost());
		
		System.out.println("Preliminary knowledge of successful tests (gamma): " + dataInstance.getParameter_preliminaryKnowledgeAboutSuccessfulTests());
		System.out.println("Preliminary knowledge of failed tests (zeta): " + dataInstance.getParameter_preliminaryKnowledgeAboutFailedTests());

		System.out.println("");
		
		System.out.println("Model 'Planning under Uncertainty' starts.");	
	}
	
	
	/**
	 * 
	 */
	public static void printModelInformation_Period () {
		
		System.out.println("");
		
		System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		
		System.out.println("");
		
		System.out.println("Period # " + dataInstance.getCountPeriods() );
		
		System.out.println("");
		
		System.out.println("1. Update knowledge about former test results:");
		
		System.out.println("");
		
		System.out.println("  - Gamma_" + (dataInstance.getCountPeriods() - 1) + " = " + dataInstance.getCountSuccessfulTests()[dataInstance.getCountPeriods() -1]);
		System.out.println("  - Zeta_" + (dataInstance.getCountPeriods() - 1) + " = " + dataInstance.getCountFailedTests()[dataInstance.getCountPeriods() -1]);
		
		System.out.println("");
		
		System.out.println("2. Decide whether to invest or not; if so, where:");
		
		System.out.println("");
		
		System.out.println("  - Investment (yes or no): " + dataInstance.getInvestmentDecisionPrimaryFacility()[dataInstance.getCountPeriods()]);
		System.out.println("  - Location: " + "empty" );
		
		System.out.println("");
		
		System.out.println("3. Obtain a new test result in the end of the period:");
		
		System.out.println("");
		
		System.out.println("  - Probability p = " + dataInstance.getTestProbability()[dataInstance.getCountPeriods()]);		
		System.out.println("  - New test result: " + dataInstance.getTestResults()[dataInstance.getCountPeriods() ]);
	}
	
	
	/**
	 * 
	 */
	public static void printModelInformation_End () {
		
		System.out.println("");
		
		System.out.println("********************************************************************************");
		
		System.out.println("\nEnd of experiment run:");
		
		ReadAndWrite.printArrayWithPeriodsInt(dataInstance.getCountSuccessfulTests(), "Successful Tests (gamma)");
		ReadAndWrite.printArrayWithPeriodsInt(dataInstance.getCountFailedTests(), "Failed Tests (zeta)");
		ReadAndWrite.printArrayWithPeriodsDouble(dataInstance.getTestProbability(), "Test Probability (p)");
		
		ReadAndWrite.printArrayWithPeriodsInt(dataInstance.getTestResults(), "Test Results (delta)");
		
		ReadAndWrite.printArrayWithPeriodsInt(dataInstance.getInvestmentDecisionPrimaryFacility(), "Investment decision (a)");
		
		for (int i = 1; i < dataInstance.getInvestmentStrategies().length; i++) {
			
			ReadAndWrite.printArrayWithPeriodsInt(dataInstance.getInvestmentStrategies()[i],"Investment strategy in period " + i + ":");
		}
	}

}
