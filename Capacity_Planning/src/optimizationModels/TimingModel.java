package optimizationModels;

import java.util.ArrayList;

import dataManagement.Data;
import dataManagement.ReadAndWrite;
import dataManagement.StdRandom;
import helper.Event;
import helper.Permuter;

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
		
		// Build scenario tree
		
		ArrayList<ArrayList<Event>> scenarioTree = generateScenarioTree(dataInstance.getCountPeriods());
		
		// Generate all strategies
		
		ArrayList<int[]> strategies = generateAllPossibleStrategiesInPeriod_t();
		
		// Calculate cost
		
		ArrayList<Double> cost = calculateV(dataInstance.getCountPeriods(), scenarioTree, strategies);
		
		// Search for the minimal cost
		
		double min = Double.MAX_VALUE;
		int index = -1;
		
		for (int i = 0; i < cost.size(); i++) {
			
			if (cost.get(i) <= min) {
				
				min = cost.get(i);
				index = i;
			}
		}
		
		int [] optimal_strategy_in_t = strategies.get(index);
		
		
		//TODO:  Set a_t and s_t
		
		
		//TODO: delete all cost in events for next calculation
	
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
		
		System.out.println("\n++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n");
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
	public static ArrayList<ArrayList<Event>> generateScenarioTree (int period) {
		
		ArrayList<ArrayList<Event>> scenarioTree = new ArrayList<ArrayList<Event>>();
		
		// Create an ArrayList for each period t element of {t,...,T} which is added to the scenario tree
		// Attention: index goes from t-1 to T-1
		
		int count = 1;
		
		for (int t = period; t <= dataInstance.getParameter_planningHorizon(); t++) {
			
			ArrayList<Event> period_t = new ArrayList<Event>();
			scenarioTree.add(period_t);
			
			int numberOfEvents = (int) Math.pow(2.0, count);
			
			// For each period the events are created and put into the ArrayList period_t
			
			for (int index = 0; index < numberOfEvents; index++) {
				
				Event tmp_event = new Event ();
				
				tmp_event.setPeriod(t);
				tmp_event.setIndex(index);
				
				// Is it a final event for which cost can be calculated?
				
				if (t == dataInstance.getParameter_planningHorizon()-1) {
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
			
			count++;
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
					
					event_tmp.setCountSuccessfulTestResults(dataInstance.getCountSuccessfulTests()[period-1] + event_tmp.getTestResult());
					event_tmp.setCountFailedTestResults(dataInstance.getCountFailedTests()[period-1] + (1 - event_tmp.getTestResult()));
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
					
					int gamma = dataInstance.getCountSuccessfulTests()[period-1];
					int zeta = dataInstance.getCountFailedTests()[period-1];
					
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
	public static void printScenarioTree (ArrayList<ArrayList<Event>> scenarioTree) {
		
		for (int t = 0; t < scenarioTree.size(); t++) {
			
			for (int index = 0; index < scenarioTree.get(t).size(); index++) {
				
				System.out.println(scenarioTree.get(t).get(index).toString());
			}
		}
	}
	
	
	/**
	 * 
	 * @return
	 */
	public static ArrayList<int[]> generateAllPossibleStrategiesInPeriod_t() {
		
		ArrayList<int[]> strategies = new ArrayList<int[]> ();
			
		int doneInvestments = 0;
		
		for (int i = 0; i < dataInstance.getInvestmentDecisionPrimaryFacility().length; i++) { 	
			if (dataInstance.getInvestmentDecisionPrimaryFacility()[i] == 1) {	
				doneInvestments++;
			}
		}
		
		int remainingPossibleInvestment = dataInstance.getParameter_monthsToBuildPrimaryFacilities() - doneInvestments;
		
		int remainingPeriods = (dataInstance.getParameter_planningHorizon() - dataInstance.getCountPeriods()) + 1;
		
		for (int i = 0; i <= remainingPossibleInvestment; i++) {
			
			int [] array_tmp = new int[remainingPeriods]; 
			
			int index_1 = 0;
			
			while (index_1 < i) {
				
				array_tmp[index_1] = 1;
			
				index_1++;
			}
			
			Permuter.permute(array_tmp, strategies);
		
		}
		
		for (int i = 0; i < strategies.size(); i++) {
			
			ReadAndWrite.printArraySimple(strategies.get(i));
		}
		
		return strategies;
	}
	

	/**
	 * 
	 */
	public static int calculateRemainingPeriodsToBuildPrimaryFacility(int period, int [] investmentDecision, int periodsToBuild) {
		
		int count = 0;
		
		for (int i = 1; i < (period-1); i++) {
			
			if(investmentDecision[i] == 1) {
				count++;
			}
		}
		
		int result = periodsToBuild - count;
		
		return result;
	}


	/**
	 * 
	 * @return
	 */
	public static ArrayList<Double> calculateV (int period, ArrayList<ArrayList<Event>> scenarioTree, ArrayList<int[]> strategies) {
		
		ArrayList<Double> cost = new ArrayList<Double>();
		
		double c = dataInstance.getParameter_constructionCostPrimaryFacility();
		double K = dataInstance.getParameter_setupCostPrimaryFacility();
		double phi = dataInstance.getParameter_penaltyCost();
		int gamma_c = dataInstance.getParameter_thresholdSuccessfulTests();
		
		// Do the cost calculation for every possible strategy
		
		for (int i = 0; i < strategies.size(); i++) {
			
			int a_T = strategies.get(i)[dataInstance.getParameter_planningHorizon()];
			int s_T = calculateRemainingPeriodsToBuildPrimaryFacility(period, strategies.get(i), dataInstance.getParameter_monthsToBuildPrimaryFacilities());
			
			// Calculate the final cost for all final events in the scenario tree
			
			for (int j = 0; j < scenarioTree.get(scenarioTree.size()-1).size(); j ++) {
					
				scenarioTree.get(j).get(scenarioTree.size()-1).calculateF(s_T, a_T, c, K, phi, gamma_c);
			}
			
			for (int j = scenarioTree.size()-2; j >= 0; j--) {
				
				for (int k = 0; k < scenarioTree.get(j).size(); k++) {
					
					scenarioTree.get(j).get(k).calculateExpectedCost();
				}
			}
			
			Event left = scenarioTree.get(0).get(0);
			Event right = scenarioTree.get(0).get(1);
			
			double final_cost = left.getProbability() * left.getExpectedCost() + right.getProbability() * right.getExpectedCost();
			
			cost.add(final_cost);
		}
		
		return cost;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}


























