package helper;


/**
 * Implements a node in the scenario tree with probability, test result, cost and all other relevant values
 * @author RamonaZauner
 *
 */
public class Event {
	
	private int period;
	private int index;
	
	private boolean firstEvent;
	private boolean finalEvent;
	
	private double finalCost;
	private double expectedCost;
	private double discountedExpectedCost;
	private double periodCost;
	private double totalCost;
	
	private double probability;
	private int testResult;
	
	private int countSuccessfulTestResults;
	private int countFailedTestResults;
	
	private double nextProbabilitySuccessful_left;
	private double nextProbabilityFailed_right;
	
	private Event left_nextSuccessfulTestResult;
	private Event right_nextFailedTestResult;
	private Event previousEvent;
	
	private int [] strategy;
	private int a_T;
	private int s_T;

	
	/**
	 * Default constructor to initialize an event with -1 and null values
	 */
	public Event () {
		
		this.period = -1;
		this.index = -1;
		
		this.finalEvent = false;
		this.firstEvent = false;
		
		this.finalCost = -1;
		this.expectedCost = -1;
		this.discountedExpectedCost = -1;
		this.periodCost = -1;
		this.totalCost = -1; 
		
		this.probability = -1;
		this.testResult = -1;
		
		this.countSuccessfulTestResults = -1;
		this.countFailedTestResults = -1;
		
		nextProbabilitySuccessful_left = -1;
		nextProbabilityFailed_right = -1;
		
		this.left_nextSuccessfulTestResult = null;
		this.right_nextFailedTestResult = null;
		this.previousEvent = null;
		
		this.strategy = null;
		this.a_T = -1;
		this.s_T = -1;
	}

	
	/**
	 * @return the period
	 */
	public int getPeriod() {
		return period;
	}


	/**
	 * @param period the period to set
	 */
	public void setPeriod(int period) {
		this.period = period;
	}


	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}


	/**
	 * @param index the index to set
	 */
	public void setIndex(int index) {
		this.index = index;
	}
	

	/**
	 * @return the firstEvent
	 */
	public boolean isFirstEvent() {
		return firstEvent;
	}


	/**
	 * @param firstEvent the firstEvent to set
	 */
	public void setFirstEvent(boolean firstEvent) {
		this.firstEvent = firstEvent;
	}
	
	/**
	 * @return the finalEvent
	 */
	public boolean isFinalEvent() {
		return finalEvent;
	}


	/**
	 * @param finalEvent the finalEvent to set
	 */
	public void setFinalEvent(boolean finalEvent) {
		this.finalEvent = finalEvent;
	}


	/**
	 * @return the finalCost
	 */
	public double getFinalCost() {
		return finalCost;
	}


	/**
	 * @param finalCost the finalCost to set
	 */
	public void setFinalCost(double finalCost) {
		this.finalCost = finalCost;
	}


	/**
	 * @return the expectedCost
	 */
	public double getExpectedCost() {
		return expectedCost;
	}


	/**
	 * @param expectedCost the expectedCost to set
	 */
	public void setExpectedCost(double expectedCost) {
		this.expectedCost = expectedCost;
	}


	/**
	 * @return the discountedExpectedCost
	 */
	public double getDiscountedExpectedCost() {
		return discountedExpectedCost;
	}


	/**
	 * @param discountedExpectedCost the discountedExpectedCost to set
	 */
	public void setDiscountedExpectedCost(double discountedExpectedCost) {
		this.discountedExpectedCost = discountedExpectedCost;
	}


	/**
	 * @return the periodCost
	 */
	public double getPeriodCost() {
		return periodCost;
	}


	/**
	 * @param periodCost the periodCost to set
	 */
	public void setPeriodCost(double periodCost) {
		this.periodCost = periodCost;
	}


	/**
	 * @return the totalCost
	 */
	public double getTotalCost() {
		return totalCost;
	}


	/**
	 * @param totalCost the totalCost to set
	 */
	public void setTotalCost(double totalCost) {
		this.totalCost = totalCost;
	}


	/**
	 * @return the probability
	 */
	public double getProbability() {
		return probability;
	}


	/**
	 * @param probability the probability to set
	 */
	public void setProbability(double probability) {
		this.probability = probability;
	}


	/**
	 * @return the testResult
	 */
	public int getTestResult() {
		return testResult;
	}


	/**
	 * @param testResult the testResult to set
	 */
	public void setTestResult(int testResult) {
		this.testResult = testResult;
	}


	/**
	 * @return the countSuccessfulTestResults
	 */
	public int getCountSuccessfulTestResults() {
		return countSuccessfulTestResults;
	}


	/**
	 * @param countSuccessfulTestResults the countSuccessfulTestResults to set
	 */
	public void setCountSuccessfulTestResults(int countSuccessfulTestResults) {
		this.countSuccessfulTestResults = countSuccessfulTestResults;
	}


	/**
	 * @return the countFailedTestResults
	 */
	public int getCountFailedTestResults() {
		return countFailedTestResults;
	}


	/**
	 * @param countFailedTestResults the countFailedTestResults to set
	 */
	public void setCountFailedTestResults(int countFailedTestResults) {
		this.countFailedTestResults = countFailedTestResults;
	}


	/**
	 * @return the nextProbabilitySuccessful_left
	 */
	public double getNextProbabilitySuccessful_left() {
		return nextProbabilitySuccessful_left;
	}


	/**
	 * @param nextProbabilitySuccessful_left the nextProbabilitySuccessful_left to set
	 */
	public void setNextProbabilitySuccessful_left(double nextProbabilitySuccessful_left) {
		this.nextProbabilitySuccessful_left = nextProbabilitySuccessful_left;
	}


	/**
	 * @return the nextProbabilityFailed_right
	 */
	public double getNextProbabilityFailed_right() {
		return nextProbabilityFailed_right;
	}


	/**
	 * @param nextProbabilityFailed_right the nextProbabilityFailed_right to set
	 */
	public void setNextProbabilityFailed_right(double nextProbabilityFailed_right) {
		this.nextProbabilityFailed_right = nextProbabilityFailed_right;
	}


	/**
	 * @return the left_nextSuccessfulTestResult
	 */
	public Event getLeft_nextSuccessfulTestResult() {
		return left_nextSuccessfulTestResult;
	}


	/**
	 * @param left_nextSuccessfulTestResult the left_nextSuccessfulTestResult to set
	 */
	public void setLeft_nextSuccessfulTestResult(Event left_nextSuccessfulTestResult) {
		this.left_nextSuccessfulTestResult = left_nextSuccessfulTestResult;
	}


	/**
	 * @return the right_nextFailedTestResult
	 */
	public Event getRight_nextFailedTestResult() {
		return right_nextFailedTestResult;
	}


	/**
	 * @param right_nextFailedTestResult the right_nextFailedTestResult to set
	 */
	public void setRight_nextFailedTestResult(Event right_nextFailedTestResult) {
		this.right_nextFailedTestResult = right_nextFailedTestResult;
	}


	/**
	 * @return the previousEvent
	 */
	public Event getPreviousEvent() {
		return previousEvent;
	}


	/**
	 * @param previousEvent the previousEvent to set
	 */
	public void setPreviousEvent(Event previousEvent) {
		this.previousEvent = previousEvent;
	}


	/**
	 * @return the strategy
	 */
	public int[] getStrategy() {
		return strategy;
	}


	/**
	 * @param strategy the strategy to set
	 */
	public void setStrategy(int[] strategy) {
		this.strategy = strategy;
	}


	/**
	 * @return the a_T
	 */
	public int getA_T() {
		return a_T;
	}


	/**
	 * @param a_T the a_T to set
	 */
	public void setA_T(int a_T) {
		this.a_T = a_T;
	}


	/**
	 * @return the s_T
	 */
	public int getS_T() {
		return s_T;
	}


	/**
	 * @param s_T the s_T to set
	 */
	public void setS_T(int s_T) {
		this.s_T = s_T;
	}


	/**
	 * Adds a certain strategy to an event for cost calculation
	 * @param strategy
	 */
	public void addStrategy (int [] strategy, int periodsToBuild) {
		
		this.strategy = strategy;
		this.a_T = strategy[strategy.length-1];
		this.s_T = periodsToBuild - countTrueValuesInArray(strategy);
	}
	
	
	/**
	 * Calculates cost that occur in a period
	 * @param c construction cost
	 * @param K setup cost
	 */
	private void calculatePeriodCost (double c, double K) {
		
		this.periodCost = this.strategy[this.period] * c + K * Math.max(strategy[this.period]-strategy[this.period-1], 0);
	}
	
	
	/**
	 * Calculates expected cost with regards to the total cost of the children of the event
	 */
	private void calculateExpectedCost () {
		
		this.expectedCost = this.nextProbabilitySuccessful_left * this.left_nextSuccessfulTestResult.totalCost
							+ this.nextProbabilityFailed_right * this.right_nextFailedTestResult.totalCost;	
	}
	
	
	/**
	 * Adds a discount factor to the expected cost and calculates discounted expected cost
	 * @param alpha yearly discount factor
	 */
	private void calculateDiscountedExpectedCost (double alpha) {
		
		this.discountedExpectedCost = alpha * this.expectedCost;	
	}
	
	
	/**
	 * Calculates final cost (only relevant for final events) depending on the threshold gamma_c
	 * @param c construction cost
	 * @param K setup cost
	 * @param phi penalty cost
	 * @param gamma_c treshold for positive clinical trial outcome regarding all successful test results 
	 */
	private void calculateFinalCost (double c, double K, double phi, int gamma_c) {
			
		if (this.countSuccessfulTestResults >= gamma_c) {
				
			this.finalCost = this.s_T * c + this.s_T * phi + K * Math.max((1 - a_T), 0);
		}
		
		else {
			
			this.finalCost = 0;	
		}
	}
	
	
	/**
	 * Calculates total cost and thereby differentiates between final and not final events
	 * @param c construction cost
	 * @param K setup cost
	 * @param phi penalty cost
	 * @param gamma_c treshold for positive clinical trial outcome regarding all successful test results
	 * @param alpha yearly discount factor
	 */
	public void calculateTotalCost (double c, double K, double phi, int gamma_c, double alpha) {
		
		if (this.finalEvent) {
			
			this.calculateFinalCost(c, K, phi, gamma_c);
			this.totalCost = this.finalCost;
		}
		
		else {
			
			this.calculatePeriodCost(c, K);
			this.calculateExpectedCost();
			this.calculateDiscountedExpectedCost(alpha);
			
			this.totalCost = this.periodCost + this.discountedExpectedCost;
		}	
	}
	
	
	/**
	 * Deletes cost calculation from event for further cost calculation
	 */
	public void deleteCostCalculation () {
		
		this.periodCost = -1;
		this.expectedCost = -1;
		this.discountedExpectedCost = -1;
		this.finalCost = -1;
		this.totalCost = -1;
		
		this.strategy = null;
		this.a_T = -1;
		this.s_T = -1;
	}
	
	
	/**
	 * Count values equal to 1 in an array
	 * @param array
	 * @return
	 */
	private static int countTrueValuesInArray (int [] array) {
		
		int count = 0;
		
		for (int i = 0; i < array.length; i++) {
			
			if (array[i] == 1) {
				count++;
			}
		}
		return count;
	}
	
	
	/**
	 * Transforms all information about an event to a string
	 */
	public String toString () {
		
		String string = "";
		
		string += "\n\n**********      EVENT      **********\n\n";
		
		string += "Period: " + this.period + "\n";
		string += "Index: " + this.index + "\n";
		
		if (this.firstEvent) {
			string += "First event: " + this.firstEvent + "\n";
		}
		
		if (this.finalEvent) {
			string += "Final event: " + this.finalEvent + "\n";
		}
		
		if (this.previousEvent != null) {
			string += "Previous Event - period: " + this.previousEvent.period + ", index: " + this.previousEvent.index + "\n";
		}
		
		if (this.probability > -1) {
			string += "Probability: " + this.probability + "\n";
		}
		
		if (this.testResult > -1) {
			string += "Test result: " + this.testResult + "\n";
		}
		
		if (this.countSuccessfulTestResults > -1) {
			string += "Gamma: " + this.countSuccessfulTestResults + "\n";
		}
		
		if (this.countFailedTestResults > -1) {
			string += "Zeta: " + this.countFailedTestResults + "\n";
		}
		
		if (this.nextProbabilitySuccessful_left  > -1) {
			string += "Next probability (success): " + this.nextProbabilitySuccessful_left + "\n";
		}
		
		if (this.nextProbabilityFailed_right > -1) {
			string += "Next probability (fail): " + this.nextProbabilityFailed_right + "\n";
		}
		
		if (this.periodCost > -1) {
			string += "Period cost: " + this.periodCost + "\n";
		}
		
		if (this.expectedCost > -1) {
			string += "Expected cost: " + this.expectedCost + "\n";
		}
		
		if (this.discountedExpectedCost > -1) {
			string += "Discounted expected cost: " + this.discountedExpectedCost + "\n";
		}
		
		if (this.finalCost > -1) {
			string += "Final cost: " + this.finalCost + "\n";
		}
		
		if (this.totalCost > -1) {
			string += "Total cost: " + this.totalCost + "\n";
		}
				
		return string;
	}		
}
