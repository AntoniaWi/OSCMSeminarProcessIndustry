package helper;


public class Event {
	
	private int period;
	private int index;
	
	private boolean finalEvent;
	private double finalCost;
	
	private double probability;
	private int testResult;
	
	private int countSuccessfulTestResults;
	private int countFailedTestResults;
	
	private double nextProbabilitySuccessful_left;
	private double nextProbabilityFailed_right;
	
	private Event left_nextSuccessfulTestResult;
	private Event right_nextFailedTestResult;
	private Event previousEvent;
	
	
	/**
	 * @param period
	 * @param index
	 * @param finalEvent
	 * @param finalCost
	 * @param probability
	 * @param testResult
	 * @param countSuccessfulTestResults
	 * @param countFailedTestResults
	 * @param nextProbability_Successful
	 * @param nextProbability_Failed
	 * @param left_nextSuccessfulTestResult
	 * @param right_nextFailedTestResult
	 * @param previousEvent
	 */
	public Event(int period, int index, boolean finalEvent, double finalCost, double probability, int testResult,
			int countSuccessfulTestResults, int countFailedTestResults, double nextProbability_Successful,
			double nextProbability_Failed, Event left_nextSuccessfulTestResult, Event right_nextFailedTestResult,
			Event previousEvent) {
		
		this.period = period;
		this.index = index;
		this.finalEvent = finalEvent;
		this.finalCost = finalCost;
		this.probability = probability;
		this.testResult = testResult;
		this.countSuccessfulTestResults = countSuccessfulTestResults;
		this.countFailedTestResults = countFailedTestResults;
		this.nextProbabilitySuccessful_left = nextProbability_Successful;
		this.nextProbabilityFailed_right = nextProbability_Failed;
		this.left_nextSuccessfulTestResult = left_nextSuccessfulTestResult;
		this.right_nextFailedTestResult = right_nextFailedTestResult;
		this.previousEvent = previousEvent;
	}
	
	
	/**
	 * 
	 */
	public Event () {
		
		this.period = -1;
		this.index = -1;
		
		this.finalEvent = false;
		this.finalCost = -1;
		
		this.probability = -1;
		this.testResult = -1;
		
		this.countSuccessfulTestResults = -1;
		this.countFailedTestResults = -1;
		
		nextProbabilitySuccessful_left = -1;
		nextProbabilityFailed_right = -1;
		
		this.left_nextSuccessfulTestResult = null;
		this.right_nextFailedTestResult = null;
		this.previousEvent = null;
		
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
	 * @return the nextProbability_Successful
	 */
	public double getNextProbability_Successful() {
		return nextProbabilitySuccessful_left;
	}


	/**
	 * @param nextProbability_Successful the nextProbability_Successful to set
	 */
	public void setNextProbability_Successful(double nextProbability_Successful) {
		this.nextProbabilitySuccessful_left = nextProbability_Successful;
	}


	/**
	 * @return the nextProbability_Failed
	 */
	public double getNextProbability_Failed() {
		return nextProbabilityFailed_right;
	}


	/**
	 * @param nextProbability_Failed the nextProbability_Failed to set
	 */
	public void setNextProbability_Failed(double nextProbability_Failed) {
		this.nextProbabilityFailed_right = nextProbability_Failed;
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
	 * Adjusts the links and the corresponding probabilities
	 * @param parent
	 */
	public void addPreviousEvent (Event parent) {
		
		this.previousEvent = parent;
		
		if (this.testResult == 1) {
			
			parent.left_nextSuccessfulTestResult = this;
			this.probability = parent.nextProbabilitySuccessful_left;
		}
		
		else if (this.testResult == 0) {
			
			parent.right_nextFailedTestResult = this;
			this.probability = parent.nextProbabilityFailed_right;
		}
	}
	
	
	/**
	 * Adjusts the links and the corresponding probabilities
	 * @param child
	 */
	public void addNextEvent (Event child) {
		
		if (child.testResult == 1) {
			
			this.left_nextSuccessfulTestResult = child;
			
		}
	}
	
	
	/**
	 * 
	 * @param s_T 	Remaining periods (years) to build primary facility in period T
	 * @param a_T 	Investment decision about primary facility in period T
	 * @param c		Construction cost of primary facility		
	 * @param K		Setup cost of primary facility
	 * @param phi	Penalty cost when construction of primary facility is not finished 
	 * @return		Final cost in T+1 -> F(s_T, a_T)
	 */
	public double calculateF (int s_T, int a_T, int c, int K, int phi, int gamma_c) {
	
		double F = -1;
		
		if (this.finalEvent == true) {
			
			if (this.countSuccessfulTestResults >= gamma_c) {
				
				F = s_T * c + s_T * phi + K * Math.max((1 - a_T), 0);
				
			}
			
			else if (this.countSuccessfulTestResults < gamma_c) {
				
				F = 0;
			}			
		}
		
		else {
			
			System.out.println ("ERROR - this event is not a final event and cost cannot be calculated!");
			
		}
		
		return F;
	}
	
	
	/**
	 * 
	 */
	public String toString () {
		
		String string = "";
		
		string += "\n\n**********      EVENT      **********\n\n";
		
		if (this.previousEvent != null) {
			
			string += "Previous Event - Period: " + this.previousEvent.period + ", index: " + this.previousEvent.index + "\n";
		}
		
		string += "Period: " + this.period + "\n";
		string += "Index: " + this.index + "\n";
		string += "Probability: " + this.probability + "\n";
		string += "Test result: " + this.testResult + "\n";
		string += "Gamma: " + this.countSuccessfulTestResults + "\n";
		string += "Zeta: " + this.countFailedTestResults + "\n";
		string += "Next probability (success): " + this.nextProbabilitySuccessful_left + "\n";
		string += "Next probability (fail): " + this.nextProbabilityFailed_right + "\n";
		
		return string;
		
	}
	


	
}




























