package dataManagement;
//Sarahtest

public class Data {
	
	// Model under uncertainty data
	
	private int parameter_planningHorizon;
	private double parameter_discountFactor;
	private int parameter_periodsToBuild;
	private int parameter_constructionCost;
	private int parameter_setupCost;
	private int parameter_penaltyCost;
	private int parameter_preliminaryKnowledgeAboutSuccessfulTests;
	private int parameter_preliminaryKnowledgeAboutFailedTests;
	
	private int [] testResults;		
	private int [] countSuccessfulTests;
	private int [] countFailedTests;
	private int [] remainingPeriodsToBuild;
	
	private int [] investmentDecisionIF;
	
	private int countPeriods;
	
	private double finalCost;
	
	
	
	/**
	 * Constructor for a data instance for the uncertainty model 
	 */
	public Data () {

		parameter_planningHorizon = 10;
		parameter_discountFactor = 1.0;
		parameter_periodsToBuild = 4;
		parameter_constructionCost = 100;
		parameter_setupCost = 10;
		parameter_penaltyCost = 30;
		parameter_preliminaryKnowledgeAboutSuccessfulTests = 1;
		parameter_preliminaryKnowledgeAboutFailedTests = 1;
		
		// Currently no test results are available
		
		testResults = new int [parameter_planningHorizon + 1];	
		for (int i = 0; i < testResults.length; i++) {	
			testResults[i] = -1;
		}
		
		// Currently no knowledge available except from preliminary knowledge
		
		countSuccessfulTests = new int [parameter_planningHorizon + 1];
		countSuccessfulTests[0] = parameter_preliminaryKnowledgeAboutSuccessfulTests;
	
		for (int i = 1; i < countSuccessfulTests.length; i++) {	
			countSuccessfulTests[i] = -1;
		}
		
		// Currently no knowledge available except from preliminary knowledge
		
		countFailedTests = new int [parameter_planningHorizon + 1];
		countFailedTests[0] = parameter_preliminaryKnowledgeAboutFailedTests;
		
		for (int i = 1; i < countFailedTests.length; i++) {	
			countFailedTests[i] = -1;
		}		
		
		this.remainingPeriodsToBuild = new int [this.parameter_planningHorizon + 1];
		this.remainingPeriodsToBuild[0] = this.parameter_periodsToBuild;
		
		for (int i = 1; i < remainingPeriodsToBuild.length; i++) {	
			remainingPeriodsToBuild[i] = -1;
		}	
		
		// Currently no investment decision made
		
		this.investmentDecisionIF = new int [this.parameter_planningHorizon + 1];
		for (int i = 0; i < this.investmentDecisionIF.length; i++) {
			this.investmentDecisionIF[i] = -1;
		}
		
		this.countPeriods = 0;

		this.finalCost = 0;
				
	}



	/**
	 * @return the parameter_planningHorizon
	 */
	public int getParameter_planningHorizon() {
		return parameter_planningHorizon;
	}



	/**
	 * @param parameter_planningHorizon the parameter_planningHorizon to set
	 */
	public void setParameter_planningHorizon(int parameter_planningHorizon) {
		this.parameter_planningHorizon = parameter_planningHorizon;
	}


	/**
	 * @return the parameter_discountFactor
	 */
	public double getParameter_discountFactor() {
		return parameter_discountFactor;
	}



	/**
	 * @param parameter_discountFactor the parameter_discountFactor to set
	 */
	public void setParameter_discountFactor(double parameter_discountFactor) {
		this.parameter_discountFactor = parameter_discountFactor;
	}



	/**
	 * @return the parameter_periodsToBuild
	 */
	public int getParameter_periodsToBuild() {
		return parameter_periodsToBuild;
	}



	/**
	 * @param parameter_periodsToBuild the parameter_periodsToBuild to set
	 */
	public void setParameter_periodsToBuild(int parameter_periodsToBuild) {
		this.parameter_periodsToBuild = parameter_periodsToBuild;
	}



	/**
	 * @return the parameter_constructionCost
	 */
	public int getParameter_constructionCost() {
		return parameter_constructionCost;
	}



	/**
	 * @param parameter_constructionCost the parameter_constructionCost to set
	 */
	public void setParameter_constructionCost(int parameter_constructionCost) {
		this.parameter_constructionCost = parameter_constructionCost;
	}



	/**
	 * @return the parameter_setupCost
	 */
	public int getParameter_setupCost() {
		return parameter_setupCost;
	}



	/**
	 * @param parameter_setupCost the parameter_setupCost to set
	 */
	public void setParameter_setupCost(int parameter_setupCost) {
		this.parameter_setupCost = parameter_setupCost;
	}



	/**
	 * @return the parameter_penaltyCost
	 */
	public int getParameter_penaltyCost() {
		return parameter_penaltyCost;
	}



	/**
	 * @param parameter_penaltyCost the parameter_penaltyCost to set
	 */
	public void setParameter_penaltyCost(int parameter_penaltyCost) {
		this.parameter_penaltyCost = parameter_penaltyCost;
	}



	/**
	 * @return the parameter_preliminaryKnowledgeAboutSuccessfulTests
	 */
	public int getParameter_preliminaryKnowledgeAboutSuccessfulTests() {
		return parameter_preliminaryKnowledgeAboutSuccessfulTests;
	}



	/**
	 * @param parameter_preliminaryKnowledgeAboutSuccessfulTests the parameter_preliminaryKnowledgeAboutSuccessfulTests to set
	 */
	public void setParameter_preliminaryKnowledgeAboutSuccessfulTests(
			int parameter_preliminaryKnowledgeAboutSuccessfulTests) {
		this.parameter_preliminaryKnowledgeAboutSuccessfulTests = parameter_preliminaryKnowledgeAboutSuccessfulTests;
	}



	/**
	 * @return the parameter_preliminaryKnowledgeAboutFailedTests
	 */
	public int getParameter_preliminaryKnowledgeAboutFailedTests() {
		return parameter_preliminaryKnowledgeAboutFailedTests;
	}



	/**
	 * @param parameter_preliminaryKnowledgeAboutFailedTests the parameter_preliminaryKnowledgeAboutFailedTests to set
	 */
	public void setParameter_preliminaryKnowledgeAboutFailedTests(
			int parameter_preliminaryKnowledgeAboutFailedTests) {
		this.parameter_preliminaryKnowledgeAboutFailedTests = parameter_preliminaryKnowledgeAboutFailedTests;
	}



	/**
	 * @return the testResults
	 */
	public int[] getTestResults() {
		return testResults;
	}



	/**
	 * @param testResults the testResults to set
	 */
	public void setTestResults(int[] testResults) {
		this.testResults = testResults;
	}



	/**
	 * @return the countSuccessfulTests
	 */
	public int[] getCountSuccessfulTests() {
		return countSuccessfulTests;
	}



	/**
	 * @param countSuccessfulTests the countSuccessfulTests to set
	 */
	public void setCountSuccessfulTests(int[] countSuccessfulTests) {
		this.countSuccessfulTests = countSuccessfulTests;
	}



	/**
	 * @return the countFailedTests
	 */
	public int[] getCountFailedTests() {
		return countFailedTests;
	}



	/**
	 * @param countFailedTests the countFailedTests to set
	 */
	public void setCountFailedTests(int[] countFailedTests) {
		this.countFailedTests = countFailedTests;
	}



	/**
	 * @return the remainingPeriodsToBuild
	 */
	public int[] getRemainingPeriodsToBuild() {
		return remainingPeriodsToBuild;
	}



	/**
	 * @param remainingPeriodsToBuild the remainingPeriodsToBuild to set
	 */
	public void setRemainingPeriodsToBuild(int[] remainingPeriodsToBuild) {
		this.remainingPeriodsToBuild = remainingPeriodsToBuild;
	}



	/**
	 * @return the investmentDecisionIF
	 */
	public int[] getInvestmentDecisionIF() {
		return investmentDecisionIF;
	}



	/**
	 * @param investmentDecisionIF the investmentDecisionIF to set
	 */
	public void setInvestmentDecisionIF(int[] investmentDecisionIF) {
		this.investmentDecisionIF = investmentDecisionIF;
	}



	/**
	 * @return the countPeriods
	 */
	public int getCountPeriods() {
		return countPeriods;
	}



	/**
	 * @param countPeriods the countPeriods to set
	 */
	public void setCountPeriods(int countPeriods) {
		this.countPeriods = countPeriods;
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
	 * Increments period count by one
	 */
	public void incrementCountPeriods() {
		this.countPeriods++;
	}
	
	
	

	/**
	 * 
	 * @param period
	 */
	public void updateCountSuccessfulTests (int period) {
		
		// E.g. we are in period t = 3, then we compute gamma for t = 3-1 = 2 out of gamma for t = 3-2 = 1 and the test result on t = 3-1 = 2
		
		if (this.testResults[period-1] == -1) {
			
			System.out.println("ERROR - update count successful tests");
		}
		
		else {
			
			this.countSuccessfulTests[period-1] = this.countSuccessfulTests[period-2] + this.testResults[period-1];
		}
		
	}
	
	
	/**
	 * 
	 * @param period
	 */
	public void updateCountFailedTests (int period) {
		
		// E.g. we are in period t = 3, then we compute gamma for t = 3-1 = 2 out of gamma for t = 3-2 = 1 and the test result on t = 3-1 = 2
		
		if (this.testResults[period-1] == -1) {
			
			System.out.println("ERROR - update count failed tests");
		}
		
		else {
			
			this.countFailedTests[period-1] = this.countFailedTests[period-2] + (1-this.testResults[period-1]);
		}
		
	}
	
	
	/**
	 * 
	 * @param period
	 * @param former_gamma
	 * @param former_s
	 * @param former_investmentDecision
	 * @return
	 */
	/*public double calculateV (int period, double result_for_a_0, double result_for_a_1, int former_gamma, int former_s, int former_investmentDecision) {
		
		double finalResult = 0.0;
		
		// a_t = 0
		
		int tmp_period = period + 1;
		
		if (period < this.parameter_planningHorizon+1) {
			
			result_for_a_0 += this.parameter_discountFactor * this.calculateV(tmp_period + 1, former_gamma + 1, former_s, 0);
		}
		
		else {
			
			result_for_a_0 += finalCost
			
		}
		
		
		
		
		
		// a_t = 1
		
		
		return finalResult;
		
	}*/
	
	
	/**
	 * 
	 * @param s_T
	 * @param a_T
	 * @return
	 */
	public double calculateF (int s_T, int a_T, int gamma_T) {
		
		double result = s_T * this.parameter_constructionCost + this.parameter_penaltyCost * s_T + this.parameter_setupCost * Math.max((1-a_T),0);
		
		return result;
	}
	
	

}
