package optimizationModels;

import java.io.IOException;
import dataManagement.*;
import helper.StdRandom;
import ilog.concert.IloException;
import jxl.read.biff.BiffException;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

/**
 * Runs the whole algorithm with the Decision Review and Location Planning Model
 * 
 * @author RamonaZauner
 *
 */

public class Algorithm {

	// ---------- Can be modified
	// ------------------------------------------------------------------------------//

	public static int numberOfTestRuns = 2;

	// ---------------------------------------------------------------------------------------------------------------//

	// ---------- Cannot be modified
	// ------------------------------------------------------------------------------//

	public static Data[] dataInstances = new Data[numberOfTestRuns];
	public static Data[] dataInstances_copy = new Data[numberOfTestRuns];

	public static DecisionReviewModel[] decisionReviewModels = new DecisionReviewModel[numberOfTestRuns];
	public static LocationPlanningModel[] locationPlanningModels = new LocationPlanningModel[numberOfTestRuns];

	public static boolean[] firstInvestment = new boolean[numberOfTestRuns];

	// ---------------------------------------------------------------------------------------------------------------//

	/**
	 * Let the algorithm run
	 * 
	 * @param args
	 * @throws BiffException
	 * @throws IOException
	 * @throws WriteException
	 * @throws IloException
	 * @throws BiffException
	 * @throws RowsExceededException
	 */
	public static void main(String[] args)
			throws BiffException, IOException, WriteException, IloException, BiffException, RowsExceededException {

		// Initialize firstInvestment array

		for (int i = 0; i < firstInvestment.length; i++) {
			firstInvestment[i] = true;
		}

		// Start test runs

		for (int i = 0; i < numberOfTestRuns; i++) {

			Data dataInstance = new Data(1);
			dataInstances[i] = dataInstance;

			DecisionReviewModel decisionReviewModel = new DecisionReviewModel(dataInstances[i]);
			decisionReviewModels[i] = decisionReviewModel;

			printModelInformation_Start(i); // TODO: rework needed

			int period = 1;

			while (period <= dataInstances[i].getParameter_planningHorizon()) {

				nextPeriod(i);
				period++;
			}

			endOfModel(i);

			// TODO: Call Location Planning Model again, redefine
			// remainingTimeOfClinicalTrials and fix primary facility, überschreiben?

			int primaryFacility = 0;

			for (int k = 0; k < dataInstances[i].getF(); k++) {

				for (int j = 0; j < dataInstances[i].getT(); j++) {

					if (dataInstances[i].getResult_constructionStartPrimaryFacility()[k][j] == 1) {

						primaryFacility = k;
					}
				}
			}

			Data dataInstance_copy = dataInstances[i].clone();
			dataInstances_copy[i] = dataInstance_copy;

			if (dataInstances[i].isSuccessOfClinicalTrials()) {
				LocationPlanningModel locationPlanningModel = new LocationPlanningModel(dataInstances[i],
						primaryFacility);
				locationPlanningModels[i] = locationPlanningModel;
				locationPlanningModels[i].run(primaryFacility);

			}
			
			else {
				LocationPlanningModel locationPlanningModel = new LocationPlanningModel(dataInstances[i],
						primaryFacility);
				locationPlanningModels[i] = locationPlanningModel;
				locationPlanningModels[i].run(primaryFacility);//TODO:new run methode!
			}

			printModelInformation_End(i); // TODO: rework needed

			String tab = "Run " + (i + 1);

			// TODO: Welche dataInstance soll am Ende in die Excel geschrieben werden?
			// dataInstances[i] oder dataInstances_copy[i] ?

			ReadAndWrite.writeSolutionLocationModel(dataInstances[i], dataInstances_copy[i], tab);
			ReadAndWrite.writeSolutionDecisionPlanningModel(dataInstances[i], tab);

		}

	}

	/**
	 * Creates next period, updates former knowledge, calls the Timing Model and if
	 * needed the Location Planning Model
	 */
	public static void nextPeriod(int testRun)
			throws BiffException, IOException, WriteException, IloException, BiffException, RowsExceededException {

		dataInstances[testRun].incrementCountPeriods();

		updateFormerKnowledge(testRun);

		decisionReviewModels[testRun].run();

		if (dataInstances[testRun].getInvestmentDecisionPrimaryFacility()[dataInstances[testRun].getCountPeriods()] == 1
				&& firstInvestment[testRun] == true) {

			LocationPlanningModel locationPlanningModel = new LocationPlanningModel(dataInstances[testRun]);
			locationPlanningModels[testRun] = locationPlanningModel;

			locationPlanningModels[testRun].run();
			firstInvestment[testRun] = false;
		}

		newTestResult(testRun);

		printModelInformation_Period(testRun); // TODO: rework needed
	}

	/**
	 * Sets period to T+1, updates knowledge about former successful and failed test
	 * results, and calculates final expansion cost
	 */
	public static void endOfModel(int testRun) {

		dataInstances[testRun].incrementCountPeriods();
		updateFormerKnowledge(testRun);
		dataInstances[testRun].calculateTotalExpansionCost();
		dataInstances[testRun].updateClinicalTrialOutcome();
	}

	/**
	 * Updates knowledge about former successful and failed test results
	 */
	public static void updateFormerKnowledge(int testRun) {

		if (dataInstances[testRun].getCountPeriods() > 1) {

			dataInstances[testRun].updateCountSuccessfulTests(dataInstances[testRun].getCountPeriods());
			dataInstances[testRun].updateCountFailedTests(dataInstances[testRun].getCountPeriods());
		}
	}

	/**
	 * Creates a new test result based on the former knowledge about successful and
	 * failed test results
	 */
	public static void newTestResult(int testRun) {

		double p = dataInstances[testRun].calculateTestProbability();

		boolean newTestResult = StdRandom.bernoulli(p);

		if (newTestResult == true) {

			dataInstances[testRun].getTestResults()[dataInstances[testRun].getCountPeriods()] = 1;
		}

		else {

			dataInstances[testRun].getTestResults()[dataInstances[testRun].getCountPeriods()] = 0;
		}
	}

	/**
	 * Prints out the model information at the start of one run
	 */
	public static void printModelInformation_Start(int testRun) {

		System.out.println("Timing Model starts with following parameters:");

		System.out.println("");

		System.out.println("Planning horizon (T): " + dataInstances[testRun].getParameter_planningHorizon());
		System.out.println("Discount factor (alpha): " + dataInstances[testRun].getParameter_discountFactor());

		System.out.println("Number of periods (year) to build a primary facility (s_p_0): "
				+ dataInstances[testRun].getParameter_periodsToBuildPrimaryFacilities());
		System.out.println("Number of periods (year) to build a secondary facility (s_s_0): "
				+ dataInstances[testRun].getParameter_periodsToBuildSecondaryFacilities());

		System.out.println("Construction cost of a primary facility (c_p): "
				+ dataInstances[testRun].getParameter_constructionCostPrimaryFacility());
		System.out.println("Construction cost of a secondary facility (c_s): "
				+ dataInstances[testRun].getParameter_constructionCostSecondaryFacility());

		System.out.println("Setup cost for a primary facility (K_p): "
				+ dataInstances[testRun].getParameter_setupCostPrimaryFacility());
		System.out.println("Setup cost for a secondary facility (K_s): "
				+ dataInstances[testRun].getParameter_setupCostSecondaryFacility());

		System.out.println("Penalty cost (Phi): " + dataInstances[testRun].getParameter_penaltyCost());

		System.out.println("Preliminary knowledge of successful tests (gamma): "
				+ dataInstances[testRun].getParameter_preliminaryKnowledgeAboutSuccessfulTests());
		System.out.println("Preliminary knowledge of failed tests (zeta): "
				+ dataInstances[testRun].getParameter_preliminaryKnowledgeAboutFailedTests());

		System.out.println("");

		System.out.println("Model 'Planning under Uncertainty' starts.");
	}

	/**
	 * Prints out the period information in the end of one period
	 */
	public static void printModelInformation_Period(int testRun) {

		System.out.println("");

		System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

		System.out.println("");

		System.out.println("Period # " + dataInstances[testRun].getCountPeriods());

		System.out.println("");

		System.out.println("1. Update knowledge about former test results:");

		System.out.println("");

		System.out.println("  - Gamma_" + (dataInstances[testRun].getCountPeriods() - 1) + " = "
				+ dataInstances[testRun].getCountSuccessfulTests()[dataInstances[testRun].getCountPeriods() - 1]);
		System.out.println("  - Zeta_" + (dataInstances[testRun].getCountPeriods() - 1) + " = "
				+ dataInstances[testRun].getCountFailedTests()[dataInstances[testRun].getCountPeriods() - 1]);

		System.out.println("");

		System.out.println("2. Decide whether to invest or not; if so, where:");

		System.out.println("");

		System.out.println("  - Investment (yes or no): " + dataInstances[testRun]
				.getInvestmentDecisionPrimaryFacility()[dataInstances[testRun].getCountPeriods()]);
		System.out.println("  - Location: " + "empty");

		System.out.println("");

		System.out.println("3. Obtain a new test result in the end of the period:");

		System.out.println("");

		System.out.println("  - Probability p = "
				+ dataInstances[testRun].getTestProbability()[dataInstances[testRun].getCountPeriods()]);
		System.out.println("  - New test result: "
				+ dataInstances[testRun].getTestResults()[dataInstances[testRun].getCountPeriods()]);
	}

	/**
	 * Prints out the model information in the end of one run
	 */
	public static void printModelInformation_End(int testRun) {

		System.out.println("");

		System.out.println("********************************************************************************");

		System.out.println("\nEnd of experiment run:");

		ReadAndWrite.printArrayWithPeriodsInt(dataInstances[testRun].getCountSuccessfulTests(),
				"Successful Tests (gamma)");
		ReadAndWrite.printArrayWithPeriodsInt(dataInstances[testRun].getCountFailedTests(), "Failed Tests (zeta)");
		ReadAndWrite.printArrayWithPeriodsDouble(dataInstances[testRun].getTestProbability(), "Test Probability (p)");

		ReadAndWrite.printArrayWithPeriodsInt(dataInstances[testRun].getTestResults(), "Test Results (delta)");

		ReadAndWrite.printArrayWithPeriodsInt(dataInstances[testRun].getInvestmentDecisionPrimaryFacility(),
				"Investment decision (a)");

		for (int i = 1; i < dataInstances[testRun].getInvestmentStrategies().length; i++) {

			ReadAndWrite.printArrayWithPeriodsInt(dataInstances[testRun].getInvestmentStrategies()[i],
					"Investment strategy in period " + i + ":");
		}
	}
}
