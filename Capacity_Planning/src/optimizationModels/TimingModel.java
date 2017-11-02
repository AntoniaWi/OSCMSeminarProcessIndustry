package optimizationModels;

import java.util.ArrayList;

import dataManagement.Data;
import dataManagement.ReadAndWrite;
import dataManagement.StdRandom;
import helper.Event;

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
		
		TimingModel.generateScenarioTree();
		TimingModel.printScenarioTree();
		
		
		
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
	
	
	/**
	 * 
	 * @return
	 */
	public static ArrayList<ArrayList<Event>> generateScenarioTree () {
		
		ArrayList<ArrayList<Event>> scenarioTree = new ArrayList<ArrayList<Event>>();
		
		// Create an ArrayList for period t element of {1,...,T} which is added to the scenario tree
		
		for (int t = 1; t <= dataInstance.getParameter_planningHorizon(); t++) {
			
			ArrayList<Event> period_t = new ArrayList<Event>();
			scenarioTree.add(period_t);
			
			int numberOfEvents = (int) Math.pow(2.0, t);
			
			// For each period the events are created and put into the ArrayList period_t
			
			for (int index = 0; index < numberOfEvents; index++) {
				
				Event tmp_event = new Event ();
				
				tmp_event.setPeriod(t);
				tmp_event.setIndex(index);
				
				// Is it a final event for which cost can be calculated?
				
				if (t == dataInstance.getParameter_planningHorizon()) {
					tmp_event.setFinalEvent(true);
				}
				else {
					tmp_event.setFinalEvent(false);
				}
				
				// Is it the left (successful) or the right (failed) event 
				
				if ((index % 2) == 0) {
					tmp_event.setTestResult(1);
				}
				else {
					tmp_event.setTestResult(0);
				}
				
				period_t.add(tmp_event);	
			}
		}
		
		// Setting all children - final events in T do not have children, "-1" in the for-loop
		
		for (int t = 0; t < scenarioTree.size()-1; t++) {
			
			for (int index = 0; index < scenarioTree.get(t).size(); index++) {
			
				Event tmp_event = scenarioTree.get(t).get(index);
				
				ArrayList<Event> period_tplus1 = scenarioTree.get(t+1);
				
				tmp_event.setLeft_nextSuccessfulTestResult(period_tplus1.get(index*2));
				tmp_event.setRight_nextFailedTestResult(period_tplus1.get(index*2 +1));
			}
		}
		
		// Setting all parents - first events in t = 1 do not have any parents
		
		for (int t = scenarioTree.size()-1; t > 0; t--) {
			
			for (int index = 0; index < scenarioTree.get(t).size(); index++) {
			
				Event tmp_event = scenarioTree.get(t).get(index);
				
				int index_parent = -1;
				
				if ((index % 2) == 0) {
					index_parent = index / 2;
				}
				else {
					index_parent = (index-1)/2;
				}
				
				ArrayList<Event> period_tminus1 = scenarioTree.get(t-1);
				tmp_event.setPreviousEvent(period_tminus1.get(index_parent));
			}
		}
		
		// Setting all countSuccessfulTestResults, countFailedTestResults, nextProbabilitySuccessful_left, nextProbabilityFailed_right
		
		for (int t = 0; t < scenarioTree.size(); t++) {
			
			for (int index = 0; index < scenarioTree.get(t).size(); index++) {
				
				Event event_tmp = scenarioTree.get(t).get(index);
				
				if (t == 0) {
					
					event_tmp.setCountSuccessfulTestResults(dataInstance.getParameter_preliminaryKnowledgeAboutSuccessfulTests() + event_tmp.getTestResult());
					event_tmp.setCountFailedTestResults(dataInstance.getParameter_preliminaryKnowledgeAboutFailedTests() + (1 - event_tmp.getTestResult()));
				}
				
				else {
					
					event_tmp.setCountSuccessfulTestResults(event_tmp.getPreviousEvent().getCountSuccessfulTestResults() + event_tmp.getTestResult());
					event_tmp.setCountFailedTestResults(event_tmp.getPreviousEvent().getCountFailedTestResults() + (1 - event_tmp.getTestResult()));
				}
				
				int gamma = event_tmp.getCountSuccessfulTestResults();
				int zeta = event_tmp.getCountFailedTestResults();
				
				double p = TimingModel.calculateTestProbability(gamma, zeta);
				double p_counter = TimingModel.calculateTestProbability(zeta, gamma);
				
				event_tmp.setNextProbabilitySuccessful_left(p);
				event_tmp.setNextProbabilityFailed_right(p_counter);
			}
			
		}
				
		// Setting probability
		
		for (int t = 0; t < scenarioTree.size(); t++) {
			
			for (int index = 0; index < scenarioTree.get(t).size(); index++) {
				
				Event event_tmp = scenarioTree.get(t).get(index);
				
				if (t == 0) {
					
					int gamma = dataInstance.getParameter_preliminaryKnowledgeAboutSuccessfulTests();
					int zeta = dataInstance.getParameter_preliminaryKnowledgeAboutFailedTests();
					
					double p = TimingModel.calculateTestProbability(gamma, zeta);
					
					if (event_tmp.getTestResult() == 1) {
						
						event_tmp.setProbability(p);
					}
					
					else {
						
						event_tmp.setProbability(1-p);
					}
				}
				
				else {
					
					Event event_parent = event_tmp.getPreviousEvent();
					
				if (event_tmp.getTestResult() == 1) {
						
						event_tmp.setProbability(event_parent.getNextProbabilitySuccessful_left());
					}
					
					else {
						
						event_tmp.setProbability(event_parent.getNextProbabilityFailed_right());
					}
				}
			}
		}		
		
		dataInstance.setScenarioTree(scenarioTree);
		
		return scenarioTree;
		
	}
	
	
	/**
	 * 
	 * @param gamma
	 * @param zeta
	 * @return
	 */
	public static double calculateTestProbability (double gamma, double zeta) {
		
		double p = gamma / (gamma + zeta);
		
		return p;
	}
	
	
	/**
	 * 
	 * @param s_T
	 * @param a_T
	 * @return
	 */
	public double calculateF (int s_T, int a_T, int gamma_T) {
		
		double result = s_T * dataInstance.getParameter_constructionCostPrimaryFacility() + dataInstance.getParameter_penaltyCost() * s_T + dataInstance.getParameter_setupCostPrimaryFacility() * Math.max((1-a_T),0);
		
		return result;
	}
	
	
	/**
	 * 
	 */
	public static void printScenarioTree () {
		
		for (int t = 0; t < dataInstance.getScenarioTree().size(); t++) {
			
			for (int index = 0; index < dataInstance.getScenarioTree().get(t).size(); index++) {
				
				System.out.println(dataInstance.getScenarioTree().get(t).get(index).toString());
			}
		}
	}
	
	
	/**
	 * 
	 * @return
	 */
	//public static int[][] generateAllPossibleStrategiesInPeriod_t() {
		
		
		
		
		
	//}
	
	

}


