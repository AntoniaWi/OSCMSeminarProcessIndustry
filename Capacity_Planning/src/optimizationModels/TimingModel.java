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
		
		System.out.println("Model 'Planning under Uncertainty' starts with following parameters:");
		
		System.out.println("");
		
		// TODO Ramona: neue Parameter ausgeben
		
		/*System.out.println("Planning horizon (T): " + dataInstance.getParameter_planningHorizon());
		System.out.println("Discount factor (alpha): " + dataInstance.getParameter_discountFactor());
		System.out.println("Number of periods to build (s): " + dataInstance.getParameter_periodsToBuild());
		System.out.println("Construction cost (c): " + dataInstance.getParameter_constructionCost());
		System.out.println("Setup cost (K): " + dataInstance.getParameter_setupCost());
		System.out.println("Penalty cost (Phi): " + dataInstance.getParameter_penaltyCost());
		System.out.println("Preliminary knowledge of successful tests (gamma): " + dataInstance.getParameter_preliminaryKnowledgeAboutSuccessfulTests());
		System.out.println("Preliminary knowledge of failed tests (zeta): " + dataInstance.getParameter_preliminaryKnowledgeAboutFailedTests());

		System.out.println("");*/
		
		System.out.println("Model 'Planning under Uncertainty' starts.");
		
		int index = 1;
		
		while (index <= dataInstance.getParameter_planningHorizon()) {
			
			newPeriod();
			index++;
			
		}
		
		
		
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
		
		Beta b = new Beta (dataInstance.getCountSuccessfulTests()[dataInstance.getCountPeriods() -1], dataInstance.getCountFailedTests()[dataInstance.getCountPeriods() -1]);
		
		double p = b.mean(); // TODO: Ramona in Data Preliminary (8) letzte Formel
		
		boolean newTestResult = StdRandom.bernoulli(p); //TODO: ŸberprŸfen, ob das wirklich die richtige Bernoulli-Formel ist
		
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
		//printArrayWithPeriods(countSuccessfulTests, "Count of successful test results");
		
		System.out.println("");
		
		System.out.println("Zeta " + (dataInstance.getCountPeriods() - 1) + ": " + dataInstance.getCountFailedTests()[dataInstance.getCountPeriods() -1]);
		//printArrayWithPeriods(countFailedTests, "Count of failed test results");
	
		System.out.println("");
	
		System.out.println("Probability p = " + p);
	
		System.out.println("");
		
		System.out.println("New test result: " + dataInstance.getTestResults()[dataInstance.getCountPeriods() ]);
		//printArrayWithPeriods(testResults, "Test Results");
		

	}
	


	
	
	
	

}


