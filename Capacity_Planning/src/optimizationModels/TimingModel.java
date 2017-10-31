package optimizationModels;

import dataManagement.Data;
import dataManagement.ReadAndWrite;
import dataManagement.StdRandom;
import jsc.distributions.Beta;

public class TimingModel {
	
	private static Data dataInstance;
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		// Get all the parameters
		
		dataInstance = new Data();
		
		// Generate console output
		
		System.out.println("Timing Model starts with following parameters:");
		
		System.out.println("");
		
		System.out.println("Planning horizon (T): " + dataInstance.getParameter_planningHorizon());
		System.out.println("Discount factor (alpha): " + dataInstance.getParameter_discountFactor());
		
		System.out.println("Number of periods (year) to build a primary facility (s_p_0): " + dataInstance.getParameter_yearsToBuildPrimaryFacilities());
		System.out.println("Number of periods (year) to build a secondary facility (s_s_0): " + dataInstance.getParameter_yearsToBuildSecondaryFacilities());
		
		System.out.println("Construction cost of a primary facility (c_p): " + dataInstance.getParameter_constructionCostPrimaryFacility());
		System.out.println("Construction cost of a secondary facility (c_s): " + dataInstance.getParameter_constructionCostSecondaryFacility());
		
		System.out.println("Setup cost for a primary facility (K_p): " + dataInstance.getParameter_setupCostPrimaryFacility());
		System.out.println("Setup cost for a secondary facility (K_s): " + dataInstance.getParameter_setupCostSecondaryFacility());
		
		System.out.println("Penalty cost (Phi): " + dataInstance.getParameter_penaltyCost());
		
		System.out.println("Preliminary knowledge of successful tests (gamma): " + dataInstance.getParameter_preliminaryKnowledgeAboutSuccessfulTests());
		System.out.println("Preliminary knowledge of failed tests (zeta): " + dataInstance.getParameter_preliminaryKnowledgeAboutFailedTests());

		System.out.println("");
		
		System.out.println("Model 'Planning under Uncertainty' starts.");
		
		int index = 1;
		
		while (index <= dataInstance.getParameter_planningHorizon()) {
			
			newPeriod();
			index++;
			
		}
		
		System.out.println("");
		
		System.out.println("************************************************************");
		
		System.out.println("\nEnd of experiment run:");
		
		ReadAndWrite.printArrayWithPeriodsInt(dataInstance.getCountSuccessfulTests(), "Successful Tests (gamma)");
		ReadAndWrite.printArrayWithPeriodsInt(dataInstance.getCountFailedTests(), "Failed Tests (zeta)");
		ReadAndWrite.printArrayWithPeriodsDouble(dataInstance.getTestProbability(), "Test Probability (p)");
		ReadAndWrite.printArrayWithPeriodsInt(dataInstance.getTestResults(), "Test Results (delta)");
		
		
		
	}
	
	
	/**
	 * 
	 */
	public static void newPeriod () {
		
		// New period
		
		dataInstance.incrementCountPeriods();
		
		// Update most-up-to-date knowledge to gamma_t-1 and zeta_t-1
		// Most-up-to-date knowledge for period 1 is the preliminary knowledge gamma_0 and zeta_0
		
		if (dataInstance.getCountPeriods() > 1) {
			
			dataInstance.updateCountSuccessfulTests(dataInstance.getCountPeriods() );
			dataInstance.updateCountFailedTests(dataInstance.getCountPeriods() );
		}
		
		// TODO:
		
		// Check threshold values with regards to a_t-1 (former investment decision)
		
		// If threshold value is met, set a_t = 1 and s_t = -1 and go to network model
		
		
		
		
		// Create new test result
		
		double p = dataInstance.calculateTestProbability();
		
		boolean newTestResult = StdRandom.bernoulli(p); 
		
		if (newTestResult == true) {
			
			dataInstance.getTestResults()[dataInstance.getCountPeriods() ] = 1;
		}
		
		else {
			
			dataInstance.getTestResults()[dataInstance.getCountPeriods() ] = 0;
		}
		
		// Print all information regarding the new period
		
		System.out.println("");
		
		System.out.println("************************************************************");
		System.out.println("Period # " + dataInstance.getCountPeriods() );
		
		System.out.println("");
		System.out.println("Gamma " + (dataInstance.getCountPeriods() - 1) + ": " + dataInstance.getCountSuccessfulTests()[dataInstance.getCountPeriods() -1]);
		
		System.out.println("");
		
		System.out.println("Zeta " + (dataInstance.getCountPeriods() - 1) + ": " + dataInstance.getCountFailedTests()[dataInstance.getCountPeriods() -1]);
		
		System.out.println("");
	
		System.out.println("Probability p = " + dataInstance.getTestProbability()[dataInstance.getCountPeriods()]);
	
		System.out.println("");
		
		System.out.println("New test result: " + dataInstance.getTestResults()[dataInstance.getCountPeriods() ]);
		

	}
	


	
	
	
	

}


