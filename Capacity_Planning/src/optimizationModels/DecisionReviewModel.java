package optimizationModels;

import java.util.ArrayList;
import dataManagement.*;
import helper.*;


/**
 * Implements all methods required by the decision review model: calculating all strategies, generating scenario trees and finding the strategy that minimizes cost for each period
 * @author RamonaZauner
 *
 */

public class DecisionReviewModel {
	
	
	private Data dataInstance;
	
	
	/**
	 * Creates a timing model with a data instance 
	 * @param dataInstance containing all relevant information
	 */
	public DecisionReviewModel (Data dataInstance) {	
		this.dataInstance = dataInstance;
	}
	
	
	/**
	 * Runs the decision review model with regards to its data instance and its period t: 
	 * generates a scenario tree, creates all possible strategies, calculate cost, and choose cost minimizing strategy.
	 */
	public void run () {
				
		// Generate scenario tree
		
		ArrayList<ArrayList<Event>> scenarioTree = generateScenarioTree();
		
		// Generate all possible strategies 
		
		ArrayList<int[]> strategies = generateAllPossibleStrategies();
		
		// Calculate cost with regards to the scenario tree
		
		ArrayList<Double> cost = calculateV(scenarioTree, strategies);
		
		// Set the new investment decision
		
		this.setNewInvesmentDecision(cost, strategies);
	}
	

	/**
	 * Generates a scenario tree of instances of the class Event for period t
	 * @return scenario tree of instances of the class Event
	 */
	private ArrayList<ArrayList<Event>> generateScenarioTree () {
		
		ArrayList<ArrayList<Event>> scenarioTree = new ArrayList<ArrayList<Event>>();
		
		int count = 0; // period counter to set the indices right
		
		// Creates the scenario tree with its events and thereby sets the first attributes: period, index, firstEvent, finalEvent, testResult
		
		for (int t = dataInstance.getCountPeriods(); t <= this.dataInstance.getParameter_planningHorizon()+1; t++) {
			
			ArrayList<Event> period_t = new ArrayList<Event>();
			scenarioTree.add(period_t);
			
			int numberOfEvents = (int) Math.pow(2.0, count);
			
			for (int index = 0; index < numberOfEvents; index++) {
				
				Event tmp_event = new Event ();
				
				tmp_event.setPeriod(t);
				tmp_event.setIndex(index);
			
				// Final event?
				
				if (t == this.dataInstance.getParameter_planningHorizon()+1) {
					tmp_event.setFinalEvent(true);
				}
				else {
					tmp_event.setFinalEvent(false);
				}
				
				// First event?
				
				if (t == dataInstance.getCountPeriods()) {
					tmp_event.setFirstEvent(true);
					tmp_event.setTestResult(this.dataInstance.getTestResults()[dataInstance.getCountPeriods()-1]);
				}
				else {
					
					tmp_event.setFirstEvent(false);
					
					if ((index % 2) == 0) {
						tmp_event.setTestResult(1);
					}
					else {
						tmp_event.setTestResult(0);
					}
				}
				period_t.add(tmp_event);	
			}
			count++;
		}
		
		// Sets all children, except for the last events which do not have any children
		
		for (int t = 0; t < scenarioTree.size()-1; t++) {
			
			for (int index = 0; index < scenarioTree.get(t).size(); index++) {
			
				Event tmp_event = scenarioTree.get(t).get(index);
				
				ArrayList<Event> period_tplus1 = scenarioTree.get(t+1);
				
				tmp_event.setLeft_nextSuccessfulTestResult(period_tplus1.get(index*2));
				tmp_event.setRight_nextFailedTestResult(period_tplus1.get(index*2 +1));
			}
		}
		
		// Sets all parents, except for the first event which does not have any parents
		
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
		
		// Sets all counts and next probabilities, except for final events which do not need counts and next probabilities
		
		for (int t = 0; t < scenarioTree.size(); t++) {
			
			for (int index = 0; index < scenarioTree.get(t).size(); index++) {
				
				Event event_tmp = scenarioTree.get(t).get(index);
				
				if (t == 0) {
					
					event_tmp.setCountSuccessfulTestResults(this.dataInstance.getCountSuccessfulTests()[dataInstance.getCountPeriods()-1]);
					event_tmp.setCountFailedTestResults(this.dataInstance.getCountFailedTests()[dataInstance.getCountPeriods()-1]);
				}
				
				else {
					
					event_tmp.setCountSuccessfulTestResults(event_tmp.getPreviousEvent().getCountSuccessfulTestResults() + event_tmp.getTestResult());
					event_tmp.setCountFailedTestResults(event_tmp.getPreviousEvent().getCountFailedTestResults() + (1 - event_tmp.getTestResult()));
				}
				
				int gamma = event_tmp.getCountSuccessfulTestResults();
				int zeta = event_tmp.getCountFailedTestResults();
				
				double p = DecisionReviewModel.calculateTestProbability(gamma, zeta);
				double p_counter = DecisionReviewModel.calculateTestProbability(zeta, gamma);
				
				event_tmp.setNextProbabilitySuccessful_left(p);
				event_tmp.setNextProbabilityFailed_right(p_counter);
			}
			
		}
				
		// Sets the (own) probability of events
		
		for (int t = 0; t < scenarioTree.size(); t++) {
			
			for (int index = 0; index < scenarioTree.get(t).size(); index++) {
				
				Event event_tmp = scenarioTree.get(t).get(index);
				
				if (t == 0) {
					event_tmp.setProbability(1.0);
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
	 * Generates all possible strategies for period t
	 * @return an array list of strategies that contain holistic investment decisions (all former investment decision and all future strategies)
	 */
	private ArrayList<int[]> generateAllPossibleStrategies() {
		
		ArrayList<int[]> futureStrategies = new ArrayList<int[]> ();
			
		// Get all relevant parameters
		
		int doneInvestments = DecisionReviewModel.countTrueValuesInArray(this.dataInstance.getInvestmentDecisionPrimaryFacility());
		int remainingPossibleInvestment = this.dataInstance.getParameter_periodsToBuildPrimaryFacilities() - doneInvestments;
		int remainingPeriods = (this.dataInstance.getParameter_planningHorizon() - this.dataInstance.getCountPeriods()) + 1;
		int max_length = Math.min(remainingPeriods, remainingPossibleInvestment);
		
		// Create all possible future strategies
		
		for (int i = 0; i <= max_length; i++) {
			
			int [] array_tmp = new int[remainingPeriods]; 
			
			int index_1 = 0;
			
			while (index_1 < i) {
				
				array_tmp[index_1] = 1;
			
				index_1++;
			}
			
			Permuter.permute(array_tmp, futureStrategies);
		}
		
		// Add all former investment decisions to all future strategies to obtain holistic strategies over the whole planning horizon
		
		ArrayList<int[]> strategies = this.addAllFormerInvestmentStrategies(futureStrategies);
		
		return strategies;
	}
	
	
	/**
	 * Add all former investment decisions to all future strategies to obtain holistic strategies over the whole planning horizon
	 * @param futureStrategies all possible future strategies in period t
	 * @return array list of holistic strategies over the whole planning horizon
	 */
	private ArrayList<int[]> addAllFormerInvestmentStrategies (ArrayList<int[]> futureStrategies) {
		
		// New array list
		
		ArrayList<int[]> strategies = new ArrayList<int[]>();
		
		// Create new strategy for each future strategy
		
		for (int i = 0; i < futureStrategies.size(); i++) {
			
			int [] strategy = new int [this.dataInstance.getParameter_planningHorizon()+1];
			
			int index_1 = 0; // index for former investment decisions and whole strategy array
			int index_2 = 0; // index for future investment decisions
			
			// Add former investment decisions to the array
			
			while (index_1 < this.dataInstance.getCountPeriods()) {
				
				strategy[index_1] = this.dataInstance.getInvestmentDecisionPrimaryFacility()[index_1];	
				index_1++;
			}
			
			// Add future strategy i to the array
			
			while (index_1 < strategy.length) {
				
				strategy[index_1] = futureStrategies.get(i)[index_2];
				index_1++;
				index_2++;
			}
			
			// Add created holistic strategy to strategies
			
			strategies.add(strategy);
		}
		
		return strategies;
	}
	

	/**
	 * Calculates total cost (period cost + expected cost) in period t for each possible strategy
	 * @param scenarioTree related to the period t
	 * @param strategies all strategies that are possible in period t
	 * @return a cost array with total cost related to all strategies in period t
	 */
	private ArrayList<Double> calculateV (ArrayList<ArrayList<Event>> scenarioTree, ArrayList<int[]> strategies) {
		
		// Create new cost array
		
		ArrayList<Double> cost = new ArrayList<Double>();
		
		// Get all relevant parameters
		
		double c = this.dataInstance.getParameter_constructionCostPrimaryFacility();
		double K = this.dataInstance.getParameter_setupCostPrimaryFacility();
		double phi = this.dataInstance.getParameter_penaltyCost();
		int gamma_c = this.dataInstance.getParameter_thresholdSuccessfulTests();
		double alpha = this.dataInstance.getParameter_discountFactor_timing();
		
		// Cost calculation for every strategy
		
		for (int i = 0; i < strategies.size(); i++) {
			
			// Delete former cost calculation from all Event instances in the scenario tree
			
			DecisionReviewModel.deleteCostCalculation(scenarioTree);
			
			// Cost calculation for each event in the scenario tree (bottom-up)
			
			for (int j = scenarioTree.size()-1; j >= 0; j--) {
				
				for (int k = 0; k < scenarioTree.get(j).size(); k++) {
					
					scenarioTree.get(j).get(k).addStrategy(strategies.get(i), dataInstance.getParameter_periodsToBuildPrimaryFacilities());
					scenarioTree.get(j).get(k).calculateTotalCost(c, K, phi, gamma_c, alpha);
				}	
			}
						
			// Add the calculated cost to the cost array
			
			cost.add(scenarioTree.get(0).get(0).getTotalCost());
			
			// Print scenario tree with cost related to strategy with index i in strategies
			// ReadAndWrite.printScenarioTree(scenarioTree);
		}
		return cost;
	}
	
	
	/**
	 * Deletes cost calculation and all related parameter values in the scenario tree for further cost calculation
	 * @param scenarioTree of instances of the class Event
	 */
	private static void deleteCostCalculation (ArrayList<ArrayList<Event>> scenarioTree) {
		
		for (int i = 0; i < scenarioTree.size(); i++) {
			
			for (int j = 0; j < scenarioTree.get(i).size(); j++) {
				
				scenarioTree.get(i).get(j).deleteCostCalculation();
			}
		}
	}
		
	
	/**
	 * Sets optimal investment decision for period t, calculates the remaining periods to build and saves cost minimizing strategy for later comparison
	 * @param cost array with total cost for each strategy
	 * @param strategies 
	 */
	private void setNewInvesmentDecision (ArrayList<Double> cost, ArrayList<int[]> strategies) {
		
		// Index of the minimum cost in the cost array
		
		int min_cost_index = searchForMin (cost);
		
		// Recent period
		
		int period = this.dataInstance.getCountPeriods();
		
		// Set investment decision for period t
		
		this.dataInstance.setInvestmentDecisionPrimaryFacility(period, strategies.get(min_cost_index)[period]);
		
		// Save optimal strategy in period t for later comparison
		
		this.dataInstance.addNewStrategyDecision(period, strategies.get(min_cost_index));
		
		// Calculate remaining periods to build primary facility
		
		this.dataInstance.calculateRemainingPeriodsToBuildPrimaryFacility(dataInstance.getCountPeriods() + 1);
	}
	
	
	/**
	 * Returns the probability for a successful test result based on the knowledge about former test results
	 * @param gamma knowledge about former successful test results
	 * @param zeta knowledge about former failed test results
	 * @return probability for a successful test result
	 */
	private static double calculateTestProbability (double gamma, double zeta) {
		
		double p = gamma / (gamma + zeta);
		return p;
	}
	
	
	/**
	 * Searches for the minimum cost in a cost array and returns the index
	 * @param cost array with total cost for each strategy
	 * @return index of minimum cost
	 */
	private static int searchForMin (ArrayList<Double> cost) {
		
		double min = Double.MAX_VALUE;
		int index = -1;
		
		for (int i = 0; i < cost.size(); i++) {
			
			if (cost.get(i) <= min) {
				
				min = cost.get(i);
				index = i;
			}
		}
		return index;
	}
	
	
	/**
	 * Counts values equal to 1 in an integer array
	 * @param array
	 * @return number of values equal to 1 
	 */
	public static int countTrueValuesInArray (int [] array) {
		
		int count = 0;
		
		for (int i = 0; i < array.length; i++) {
			
			if (array[i] == 1) {
				count++;
			}
		}
		return count;
	}
}