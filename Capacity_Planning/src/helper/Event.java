package helper;


public class Event {
	
	private int index;
	
	private boolean finalEvent;
	
	private int period;
	private int testResult;
	private int countSuccessfulTestResults;
	private int countFailedTestResults;
	private double nextProbability;
	
	private Event nextSuccessfulTestResult;
	private Event nextFailedTestResult;
	
	private Event previousEvent;
	

	/**
	 * @param finalEvent
	 * @param threshold
	 * @param period
	 * @param testResult
	 * @param countSuccessfulTestResults
	 * @param countFailedTestResults
	 * @param nextProbability
	 * @param nextSuccessfulTestResult
	 * @param nextFailedTestResult
	 * @param previousEvent
	 */
	public Event(int index, boolean finalEvent, int period, int testResult, int countSuccessfulTestResults,
			int countFailedTestResults, double nextProbability, Event nextSuccessfulTestResult,
			Event nextFailedTestResult, Event previousEvent) {
		
		this.index = index;
		this.finalEvent = finalEvent;
		this.period = period;
		this.testResult = testResult;
		this.countSuccessfulTestResults = countSuccessfulTestResults;
		this.countFailedTestResults = countFailedTestResults;
		this.nextProbability = nextProbability;
		this.nextSuccessfulTestResult = nextSuccessfulTestResult;
		this.nextFailedTestResult = nextFailedTestResult;
		this.previousEvent = previousEvent;
	}

	
	/**
	 * 
	 */
	public Event () {
		
		this.index = -1;
		this.finalEvent = false;
		this.period = -1;
		this.testResult = -1;
		this.countSuccessfulTestResults = -1;
		this.countFailedTestResults = -1;
		this.nextProbability = -1;
		this.nextSuccessfulTestResult = null;
		this.nextFailedTestResult = null;
		this.previousEvent = null;
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
	 * @return the nextProbability
	 */
	public double getNextProbability() {
		return nextProbability;
	}


	/**
	 * @param nextProbability the nextProbability to set
	 */
	public void setNextProbability(double nextProbability) {
		this.nextProbability = nextProbability;
	}


	/**
	 * @return the nextSuccessfulTestResult
	 */
	public Event getNextSuccessfulTestResult() {
		return nextSuccessfulTestResult;
	}


	/**
	 * @param nextSuccessfulTestResult the nextSuccessfulTestResult to set
	 */
	public void setNextSuccessfulTestResult(Event nextSuccessfulTestResult) {
		this.nextSuccessfulTestResult = nextSuccessfulTestResult;
	}


	/**
	 * @return the nextFailedTestResult
	 */
	public Event getNextFailedTestResult() {
		return nextFailedTestResult;
	}


	/**
	 * @param nextFailedTestResult the nextFailedTestResult to set
	 */
	public void setNextFailedTestResult(Event nextFailedTestResult) {
		this.nextFailedTestResult = nextFailedTestResult;
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
	
	
	
	

}
