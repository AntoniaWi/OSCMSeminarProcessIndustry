package optimizationModels;

import java.io.IOException;
import java.util.ArrayList;

import dataManagement.Data;
import dataManagement.ReadAndWrite;
import dataManagement.StdRandom;
import helper.Event;
import helper.Permuter;
import ilog.concert.IloException;
import jxl.read.biff.BiffException;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class TimingModel {
	
	private Data dataInstance;
	
	
	public TimingModel (Data dataInstance) {
		
		this.dataInstance = dataInstance;
		
	}
	
	public static void main(String[] args) throws BiffException, IOException, WriteException {
		
		// Get all the parameters
		
		Data dataInstance = new Data();
		
		TimingModel tm = new TimingModel (dataInstance);
		
		// Generate console output
		
		// TODO Check if all important parameters are printed out
		
		tm.printTimingModelInformationStart();
				
		int period = 1;
		
		while (period <= dataInstance.getParameter_planningHorizon()) {
			
			tm.newPeriod();
			period++;
			
		}
		
		// Generate console output
		
		tm.printTimingModelInformationEnd();
		
		
	}
	
	
	
	
	
	public void run () throws IloException, BiffException, IOException, RowsExceededException, WriteException {
		
		// Get all the parameters
		
		Data dataInstance = new Data();
		
		// Data dataInstance = new Data(x);
		
		TimingModel tm = new TimingModel (dataInstance);
		
		// Generate console output
		
		// TODO Check if all important parameters are printed out
		
		tm.printTimingModelInformationStart();
				
		int period = 1;
		
		while (period <= dataInstance.getParameter_planningHorizon()) {
			
			tm.newPeriod();
			period++;
			
		}
		
		// Generate console output
		
		tm.printTimingModelInformationEnd();
		
		
	}
	
	
	
	// -------------------------------------------------------------------------------------------------------------------//
	
	// Main Methods
	
	// -------------------------------------------------------------------------------------------------------------------//
	

	/**
	 * 
	 */
	public void newPeriod () {
		
		// New period
		
		this.dataInstance.incrementCountPeriods();
		
		// Update most-up-to-date knowledge to gamma_t-1 and zeta_t-1
		
		this.updateFormerKnowledge();
		
		// Build scenario tree
		
		ArrayList<ArrayList<Event>> scenarioTree = generateScenarioTree(dataInstance.getCountPeriods());
		
		// Generate all strategies
		
		ArrayList<int[]> strategies = generateAllPossibleStrategies();
		
		// Calculate cost
		
		ArrayList<Double> cost = calculateV(dataInstance.getCountPeriods(), scenarioTree, strategies);
		
		// Set investment decision and fill a_t and s_t
		
		this.setNewInvesmentDecision(cost, strategies);
		
		if (dataInstance.getInvestmentDecisionPrimaryFacility()[dataInstance.getCountPeriods()] == 1) {
			
			// TODO: call Location Planning Model
			
			// 
		}
		
		// New test result
		
		this.newTestResult();
		
		// Print all information regarding the new period
		
		this.printPeriodInformation();
	}
	
	
	/**
	 * 
	 * @param period
	 * @return
	 */
	public ArrayList<ArrayList<Event>> generateScenarioTree (int period) {
		
		ArrayList<ArrayList<Event>> scenarioTree = new ArrayList<ArrayList<Event>>();
		
		// Create an ArrayList for each period t element of {t,...,T} which is added to the scenario tree
		// Attention: index goes from t-1 to T-1
		
		int count = 0;
		
		for (int t = period; t <= this.dataInstance.getParameter_planningHorizon()+1; t++) {
			
			ArrayList<Event> period_t = new ArrayList<Event>();
			scenarioTree.add(period_t);
			
			int numberOfEvents = (int) Math.pow(2.0, count);
			
			// For each period the events are created and put into the ArrayList period_t
			
			for (int index = 0; index < numberOfEvents; index++) {
				
				Event tmp_event = new Event ();
				
				tmp_event.setPeriod(t);
				tmp_event.setIndex(index);
				
				// Is it a final event for which cost can be calculated?
				
				if (t == this.dataInstance.getParameter_planningHorizon()+1) {
					tmp_event.setFinalEvent(true);
				}
				else {
					tmp_event.setFinalEvent(false);
				}
				
				// Is it a first event for which no test result is calculated?
				
				if (t == period) {
					tmp_event.setFirstEvent(true);
					tmp_event.setTestResult(this.dataInstance.getTestResults()[period-1]);
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
				
				// Is it the left (successful) or the right (failed) event 
				
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
		
		// Setting all parents - first events in t = 0 do not have any parents
		
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
		
		// Setting all counts and next probabilities except from final events
		
		for (int t = 0; t < scenarioTree.size(); t++) {
			
			for (int index = 0; index < scenarioTree.get(t).size(); index++) {
				
				Event event_tmp = scenarioTree.get(t).get(index);
				
				if (t == 0) {
					
					event_tmp.setCountSuccessfulTestResults(this.dataInstance.getCountSuccessfulTests()[period-1]);
					event_tmp.setCountFailedTestResults(this.dataInstance.getCountFailedTests()[period-1]);
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
	 * 
	 * @return
	 */
	public ArrayList<int[]> generateAllPossibleStrategies() {
		
		ArrayList<int[]> futureStrategies = new ArrayList<int[]> ();
			
		int doneInvestments = TimingModel.countTrueValuesInArray(this.dataInstance.getInvestmentDecisionPrimaryFacility());
		
		int remainingPossibleInvestment = this.dataInstance.getParameter_monthsToBuildPrimaryFacilities() - doneInvestments;
		
		int remainingPeriods = (this.dataInstance.getParameter_planningHorizon() - this.dataInstance.getCountPeriods()) + 1;
		
		int max_length = Math.min(remainingPeriods, remainingPossibleInvestment);
		
		for (int i = 0; i <= max_length; i++) {
			
			int [] array_tmp = new int[remainingPeriods]; 
			
			int index_1 = 0;
			
			while (index_1 < i) {
				
				array_tmp[index_1] = 1;
			
				index_1++;
			}
			
			Permuter.permute(array_tmp, futureStrategies);
		
		}
		
		ArrayList<int[]> strategies = this.addAllFormerInvestmentStrategies(futureStrategies);
		return strategies;
	}
	
	
	/**
	 * 
	 * @param futureStrategies
	 */
	public ArrayList<int[]> addAllFormerInvestmentStrategies (ArrayList<int[]> futureStrategies) {
		
		ArrayList<int[]> strategies = new ArrayList<int[]>();
		
		for (int i = 0; i < futureStrategies.size(); i++) {
			
			int [] strategy = new int [this.dataInstance.getParameter_planningHorizon()+1];
			
			int index_1 = 0;
			int index_2 = 0;
			
			while (index_1 < this.dataInstance.getCountPeriods()) {
				
				strategy[index_1] = this.dataInstance.getInvestmentDecisionPrimaryFacility()[index_1];	
				index_1++;
			}
			
			while (index_1 < strategy.length) {
				
				strategy[index_1] = futureStrategies.get(i)[index_2];
				index_1++;
				index_2++;
			}
			
			strategies.add(strategy);
			
		}
		
		return strategies;
	}
	

	/**
	 * 
	 * @param period
	 * @param strategy
	 * @param periodsToBuild
	 * @return
	 */
	public int calculateRemainingPeriodsToBuildPrimaryFacility(int period, int [] strategy, int periodsToBuild) {

		int count = 0;
		
		int [] array_tmp = new int[this.dataInstance.getParameter_planningHorizon()+1];
		
		int index = 0;
		
		// All former investment decisions that were decided before
		
		while (index <= period-1) {
			
			array_tmp[index] = this.dataInstance.getInvestmentDecisionPrimaryFacility()[index];
			index++;
		}
		
		// Add all future strategies
		
		index = period;
		int index_2 = 0;
		
		while (index_2 < strategy.length) {
			
			array_tmp[index] = strategy[index_2];
			index++;
			index_2++;	
		}
		
		for (int i = 0; i < array_tmp.length; i++) {
			
			if (array_tmp[i]==1) {
				
				count++;	
			}
		}
		
		int result = periodsToBuild - count;
		
		return result;
		
	}
		


	/**
	 * 
	 * @param period
	 * @param scenarioTree
	 * @param strategies
	 * @return
	 */
	public ArrayList<Double> calculateV (int period, ArrayList<ArrayList<Event>> scenarioTree, ArrayList<int[]> strategies) {
		
		ArrayList<Double> cost = new ArrayList<Double>();
		
		double c = this.dataInstance.getParameter_constructionCostPrimaryFacility();
		double K = this.dataInstance.getParameter_setupCostPrimaryFacility();
		double phi = this.dataInstance.getParameter_penaltyCost();
		int gamma_c = this.dataInstance.getParameter_thresholdSuccessfulTests();
		double alpha = this.dataInstance.getParameter_discountFactor();
		
		// Do the cost calculation for every possible strategy
		
		for (int i = 0; i < strategies.size(); i++) {
			
			TimingModel.deleteCostCalculation(scenarioTree);
			
			// Calculate the final cost for all final events in the scenario tree
			
			for (int j = scenarioTree.size()-1; j >= 0; j--) {
				
				for (int k = 0; k < scenarioTree.get(j).size(); k++) {
					
					scenarioTree.get(j).get(k).addStrategy(strategies.get(i), dataInstance.getParameter_monthsToBuildPrimaryFacilities());
					scenarioTree.get(j).get(k).calculateTotalCost(c, K, phi, gamma_c, alpha);
				}	
			}
						
			cost.add(scenarioTree.get(0).get(0).getTotalCost());
			
			// TimingModel.printScenarioTree(scenarioTree);

		}
		
		return cost;
	}
	
	
	/**
	 * 
	 */
	public void newTestResult () {
		
		double p = this.dataInstance.calculateTestProbability();
		
		boolean newTestResult = StdRandom.bernoulli(p); 
		
		if (newTestResult == true) {
			
			this.dataInstance.getTestResults()[dataInstance.getCountPeriods() ] = 1;
		}
		
		else {
			
			this.dataInstance.getTestResults()[dataInstance.getCountPeriods() ] = 0;
		}	
	}
	
	
	/**
	 * 
	 * @throws IloException
	 * @throws BiffException
	 * @throws IOException
	 * @throws RowsExceededException
	 * @throws WriteException
	 */
	public static void runLocationModel (Data dataInstance) throws IloException, BiffException, IOException, RowsExceededException, WriteException {

		LocationPlanningModel lpm = new LocationPlanningModel(dataInstance);

		lpm.build();
		lpm.solve();
		lpm.writeSolution(new int[] { 1, 2, 3 }, dataInstance);
		ReadAndWrite.writeSolution(dataInstance);
	
	}
	
	
	
	
	// -------------------------------------------------------------------------------------------------------------------//
	
	// Helper Methods
	
	// -------------------------------------------------------------------------------------------------------------------//
	
	
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
	 * @param scenarioTree
	 */
	public static void deleteCostCalculation (ArrayList<ArrayList<Event>> scenarioTree) {
		
		for (int i = 0; i < scenarioTree.size(); i++) {
			
			for (int j = 0; j < scenarioTree.get(i).size(); j++) {
				
				scenarioTree.get(i).get(j).deleteCostCalculation();
			}
		}
	}
	
	
	/**
	 * 
	 * @param cost
	 * @return
	 */
	public static int searchForMin (ArrayList<Double> cost) {
		
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
	 * 
	 * @param cost
	 * @param strategies
	 */
	public void setNewInvesmentDecision (ArrayList<Double> cost, ArrayList<int[]> strategies) {
		
		int min_cost_index = searchForMin (cost);
		
		int period = this.dataInstance.getCountPeriods();
		
		this.dataInstance.setInvestmentDecisionPrimaryFacility(period, strategies.get(min_cost_index)[period]);
		this.dataInstance.addNewStrategyDecision(period, strategies.get(min_cost_index));
		
		this.dataInstance.calculateRemainingPeriodsToBuildPrimaryFacility(dataInstance.getCountPeriods() + 1);
	}
	
	
	/**
	 * 
	 */
	public void updateFormerKnowledge () {
		
		if (this.dataInstance.getCountPeriods() > 1) {
			
			this.dataInstance.updateCountSuccessfulTests(this.dataInstance.getCountPeriods());
			this.dataInstance.updateCountFailedTests(this.dataInstance.getCountPeriods());
		}
	}
	
	
	/**
	 * 
	 * @param array
	 * @return
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
	
	
	
	
	// -------------------------------------------------------------------------------------------------------------------//
	
	// Print Methods
	
	// -------------------------------------------------------------------------------------------------------------------//
	
	
	/**
	 * 
	 */
	public void printPeriodInformation () {
		
		System.out.println("");
		
		System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		
		System.out.println("");
		
		System.out.println("Period # " + this.dataInstance.getCountPeriods() );
		
		System.out.println("");
		
		System.out.println("1. Update knowledge about former test results:");
		
		System.out.println("");
		
		System.out.println("  - Gamma_" + (this.dataInstance.getCountPeriods() - 1) + " = " + this.dataInstance.getCountSuccessfulTests()[this.dataInstance.getCountPeriods() -1]);
		System.out.println("  - Zeta_" + (this.dataInstance.getCountPeriods() - 1) + " = " + this.dataInstance.getCountFailedTests()[this.dataInstance.getCountPeriods() -1]);
		
		System.out.println("");
		
		System.out.println("2. Decide whether to invest or not; if so, where:");
		
		System.out.println("");
		
		System.out.println("  - Investment (yes or no): " + this.dataInstance.getInvestmentDecisionPrimaryFacility()[this.dataInstance.getCountPeriods()]);
		System.out.println("  - Location: " + "empty" );
		
		System.out.println("");
		
		System.out.println("3. Obtain a new test result in the end of the period:");
		
		System.out.println("");
		
		System.out.println("  - Probability p = " + this.dataInstance.getTestProbability()[this.dataInstance.getCountPeriods()]);		
		System.out.println("  - New test result: " + this.dataInstance.getTestResults()[this.dataInstance.getCountPeriods() ]);
		
	}
	
	
	/**
	 * 
	 */
	public void printTimingModelInformationStart () {
		
		System.out.println("Timing Model starts with following parameters:");
		
		System.out.println("");
		
		System.out.println("Planning horizon (T): " + this.dataInstance.getParameter_planningHorizon());
		System.out.println("Discount factor (alpha): " + this.dataInstance.getParameter_discountFactor());
		
		System.out.println("Number of periods (year) to build a primary facility (s_p_0): " + this.dataInstance.getParameter_monthsToBuildPrimaryFacilities());
		System.out.println("Number of periods (year) to build a secondary facility (s_s_0): " + this.dataInstance.getParameter_monthsToBuildSecondaryFacilities());
		
		System.out.println("Construction cost of a primary facility (c_p): " + this.dataInstance.getParameter_constructionCostPrimaryFacility());
		System.out.println("Construction cost of a secondary facility (c_s): " + this.dataInstance.getParameter_constructionCostSecondaryFacility());
		
		System.out.println("Setup cost for a primary facility (K_p): " + this.dataInstance.getParameter_setupCostPrimaryFacility());
		System.out.println("Setup cost for a secondary facility (K_s): " + this.dataInstance.getParameter_setupCostSecondaryFacility());
		
		System.out.println("Penalty cost (Phi): " + this.dataInstance.getParameter_penaltyCost());
		
		System.out.println("Preliminary knowledge of successful tests (gamma): " + this.dataInstance.getParameter_preliminaryKnowledgeAboutSuccessfulTests());
		System.out.println("Preliminary knowledge of failed tests (zeta): " + this.dataInstance.getParameter_preliminaryKnowledgeAboutFailedTests());

		System.out.println("");
		
		System.out.println("Model 'Planning under Uncertainty' starts.");	
	}
	
	
	/**
	 * 
	 */
	public void printTimingModelInformationEnd () {
		
		System.out.println("");
		
		System.out.println("********************************************************************************");
		
		System.out.println("\nEnd of experiment run:");
		
		//ReadAndWrite.printArrayWithPeriodsInt(dataInstance.getCountSuccessfulTests(), "Successful Tests (gamma)");
		//ReadAndWrite.printArrayWithPeriodsInt(dataInstance.getCountFailedTests(), "Failed Tests (zeta)");
		//ReadAndWrite.printArrayWithPeriodsDouble(dataInstance.getTestProbability(), "Test Probability (p)");
		
		ReadAndWrite.printArrayWithPeriodsInt(this.dataInstance.getTestResults(), "Test Results (delta)");
		
		ReadAndWrite.printArrayWithPeriodsInt(this.dataInstance.getInvestmentDecisionPrimaryFacility(), "Investment decision (a)");
		
		for (int i = 1; i < this.dataInstance.getInvestmentStrategies().length; i++) {
			
			ReadAndWrite.printArrayWithPeriodsInt(this.dataInstance.getInvestmentStrategies()[i],"Investment strategy in period " + i + ":");
		}
	}
	
	
	/**
	 * 
	 * @param strategies
	 */
	public static void printStrategies (ArrayList<int[]> strategies, int period) {
		
		System.out.println("\n\n--------------------------------------------------------------------------------");
		
		System.out.println ("\nStrategies for period: " + period);
		
		for (int i = 0; i < strategies.size(); i++) {
			
			ReadAndWrite.printArraySimple(strategies.get(i));
		}
		
		System.out.println("\n--------------------------------------------------------------------------------\n\n");
	}
	
	
	
}


























