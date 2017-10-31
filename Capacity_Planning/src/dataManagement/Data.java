package dataManagement;

import helper.Event;
import java.util.*;

public class Data {
	
	// Timing Model
	
	private int parameter_planningHorizon;  							// T
	
	private double parameter_discountFactor;							// alpha
	
	private int parameter_yearsToBuildPrimaryFacilities;				// s_p_0 - in whole years
	private double parameter_yearsToBuildSecondaryFacilities;			// s_s_0 - in fraction of years - if construction takes place, it starts in T+1
	
	private int parameter_constructionCostPrimaryFacility;			// c_p
	private int parameter_constructionCostSecondaryFacility;			// c_s
	
	private int parameter_setupCostPrimaryFacility;					// K_p
	private int parameter_setupCostSecondaryFacility;					// K_s
	
	private int parameter_penaltyCost;								// Phi(s_t) = Phi*s_t
	
	private int parameter_preliminaryKnowledgeAboutSuccessfulTests;	// Gamma_0
	private int parameter_preliminaryKnowledgeAboutFailedTests;		// Zeta_0
	private int parameter_thresholdSuccessfulTests;					// Gamma_c
	
	private int [] testResults;										// Delta_t
	private int [] countSuccessfulTests;								// Gamma_t
	private int [] countFailedTests;									// Zeta_t
	private int [] remainingYearsToBuildPrimaryFacility;				// s_p_t
	private double [] testProbability;								// p
	
	private int [] investmentDecisionPrimaryFacility;					// a_p_t
	private int investmentDecisionSecondaryFacility;					// a_s_T+1 - secondary facility is built in period T+1 if clinical trails are successful
	
	private int countPeriods;										// t
	
	private ArrayList<Event> scenarioTree;							// needed to calculate
								
	
	/**
	 * 
	 */
	public Data () {

		this.parameter_planningHorizon = 10;
		
		this.parameter_discountFactor = 1.0;
		
		this.parameter_yearsToBuildPrimaryFacilities = 4;
		this.parameter_yearsToBuildSecondaryFacilities = 0.25;
		
		this.parameter_constructionCostPrimaryFacility = 100;
		this.parameter_constructionCostSecondaryFacility = 25;
		
		this.parameter_setupCostPrimaryFacility = 10;
		this.parameter_setupCostSecondaryFacility = 2;
		
		this.parameter_penaltyCost = 30;
		
		this.parameter_preliminaryKnowledgeAboutSuccessfulTests = 1;
		this.parameter_preliminaryKnowledgeAboutFailedTests = 1;
		this.parameter_thresholdSuccessfulTests = 5;
		
		// Currently no test results are available
		
		this.testResults = new int [this.parameter_planningHorizon + 1];	
		
		for (int i = 0; i < this.testResults.length; i++) {	
			this.testResults[i] = -1;
		}
		
		// Currently no knowledge available except from preliminary knowledge
		
		this.countSuccessfulTests = new int [this.parameter_planningHorizon + 1];
		this.countSuccessfulTests[0] = this.parameter_preliminaryKnowledgeAboutSuccessfulTests;
	
		for (int i = 1; i < this.countSuccessfulTests.length; i++) {	
			this.countSuccessfulTests[i] = -1;
		}
		
		// Currently no knowledge available except from preliminary knowledge
		
		this.countFailedTests = new int [this.parameter_planningHorizon + 1];
		this.countFailedTests[0] = this.parameter_preliminaryKnowledgeAboutFailedTests;
		
		for (int i = 1; i < this.countFailedTests.length; i++) {	
			this.countFailedTests[i] = -1;
		}		
		
		// Currently only available for period 0
		
		this.remainingYearsToBuildPrimaryFacility = new int [this.parameter_planningHorizon + 1];
		this.remainingYearsToBuildPrimaryFacility[0] = this.parameter_yearsToBuildPrimaryFacilities;
		
		for (int i = 1; i < this.remainingYearsToBuildPrimaryFacility.length; i++) {	
			this.remainingYearsToBuildPrimaryFacility[i] = -1;
		}	
		
		// Currently no investment decision about primary facility made
		
		this.investmentDecisionPrimaryFacility = new int [this.parameter_planningHorizon + 1];
		for (int i = 0; i < this.investmentDecisionPrimaryFacility.length; i++) {
			this.investmentDecisionPrimaryFacility[i] = -1;
		}
		
		// Currently no test probability is calculated
		
		this.testProbability = new double [this.parameter_planningHorizon + 1];
		for (int i = 0; i < this.testProbability.length; i++) {
			this.testProbability[i] = -1;
		}
		
		// Currently no investment decision about secondary facility made
		
		this.investmentDecisionSecondaryFacility = -1;
		
		// Start in period t = 0
		
		this.countPeriods = 0;
				
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
	 * @return the parameter_yearsToBuildPrimaryFacilities
	 */
	public int getParameter_yearsToBuildPrimaryFacilities() {
		return parameter_yearsToBuildPrimaryFacilities;
	}


	/**
	 * @param parameter_yearsToBuildPrimaryFacilities the parameter_yearsToBuildPrimaryFacilities to set
	 */
	public void setParameter_yearsToBuildPrimaryFacilities(int parameter_yearsToBuildPrimaryFacilities) {
		this.parameter_yearsToBuildPrimaryFacilities = parameter_yearsToBuildPrimaryFacilities;
	}


	/**
	 * @return the parameter_yearsToBuildSecondaryFacilities
	 */
	public double getParameter_yearsToBuildSecondaryFacilities() {
		return parameter_yearsToBuildSecondaryFacilities;
	}


	/**
	 * @param parameter_yearsToBuildSecondaryFacilities the parameter_yearsToBuildSecondaryFacilities to set
	 */
	public void setParameter_yearsToBuildSecondaryFacilities(double parameter_yearsToBuildSecondaryFacilities) {
		this.parameter_yearsToBuildSecondaryFacilities = parameter_yearsToBuildSecondaryFacilities;
	}


	/**
	 * @return the parameter_constructionCostPrimaryFacility
	 */
	public int getParameter_constructionCostPrimaryFacility() {
		return parameter_constructionCostPrimaryFacility;
	}


	/**
	 * @param parameter_constructionCostPrimaryFacility the parameter_constructionCostPrimaryFacility to set
	 */
	public void setParameter_constructionCostPrimaryFacility(int parameter_constructionCostPrimaryFacility) {
		this.parameter_constructionCostPrimaryFacility = parameter_constructionCostPrimaryFacility;
	}


	/**
	 * @return the parameter_constructionCostSecondaryFacility
	 */
	public int getParameter_constructionCostSecondaryFacility() {
		return parameter_constructionCostSecondaryFacility;
	}


	/**
	 * @param parameter_constructionCostSecondaryFacility the parameter_constructionCostSecondaryFacility to set
	 */
	public void setParameter_constructionCostSecondaryFacility(int parameter_constructionCostSecondaryFacility) {
		this.parameter_constructionCostSecondaryFacility = parameter_constructionCostSecondaryFacility;
	}


	/**
	 * @return the parameter_setupCostPrimaryFacility
	 */
	public int getParameter_setupCostPrimaryFacility() {
		return parameter_setupCostPrimaryFacility;
	}


	/**
	 * @param parameter_setupCostPrimaryFacility the parameter_setupCostPrimaryFacility to set
	 */
	public void setParameter_setupCostPrimaryFacility(int parameter_setupCostPrimaryFacility) {
		this.parameter_setupCostPrimaryFacility = parameter_setupCostPrimaryFacility;
	}


	/**
	 * @return the parameter_setupCostSecondaryFacility
	 */
	public int getParameter_setupCostSecondaryFacility() {
		return parameter_setupCostSecondaryFacility;
	}


	/**
	 * @param parameter_setupCostSecondaryFacility the parameter_setupCostSecondaryFacility to set
	 */
	public void setParameter_setupCostSecondaryFacility(int parameter_setupCostSecondaryFacility) {
		this.parameter_setupCostSecondaryFacility = parameter_setupCostSecondaryFacility;
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
	public void setParameter_preliminaryKnowledgeAboutFailedTests(int parameter_preliminaryKnowledgeAboutFailedTests) {
		this.parameter_preliminaryKnowledgeAboutFailedTests = parameter_preliminaryKnowledgeAboutFailedTests;
	}


	/**
	 * @return the parameter_thresholdSuccessfulTests
	 */
	public int getParameter_thresholdSuccessfulTests() {
		return parameter_thresholdSuccessfulTests;
	}


	/**
	 * @param parameter_thresholdSuccessfulTests the parameter_thresholdSuccessfulTests to set
	 */
	public void setParameter_thresholdSuccessfulTests(int parameter_thresholdSuccessfulTests) {
		this.parameter_thresholdSuccessfulTests = parameter_thresholdSuccessfulTests;
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
	 * @return the remainingYearsToBuildPrimaryFacility
	 */
	public int[] getRemainingYearsToBuildPrimaryFacility() {
		return remainingYearsToBuildPrimaryFacility;
	}


	/**
	 * @param remainingYearsToBuildPrimaryFacility the remainingYearsToBuildPrimaryFacility to set
	 */
	public void setRemainingYearsToBuildPrimaryFacility(int[] remainingYearsToBuildPrimaryFacility) {
		this.remainingYearsToBuildPrimaryFacility = remainingYearsToBuildPrimaryFacility;
	}


	/**
	 * @return the testProbability
	 */
	public double[] getTestProbability() {
		return testProbability;
	}


	/**
	 * @param testProbability the testProbability to set
	 */
	public void setTestProbability(double[] testProbability) {
		this.testProbability = testProbability;
	}


	/**
	 * @return the investmentDecisionPrimaryFacility
	 */
	public int[] getInvestmentDecisionPrimaryFacility() {
		return investmentDecisionPrimaryFacility;
	}


	/**
	 * @param investmentDecisionPrimaryFacility the investmentDecisionPrimaryFacility to set
	 */
	public void setInvestmentDecisionPrimaryFacility(int[] investmentDecisionPrimaryFacility) {
		this.investmentDecisionPrimaryFacility = investmentDecisionPrimaryFacility;
	}


	/**
	 * @return the investmentDecisionSecondaryFacility
	 */
	public int getInvestmentDecisionSecondaryFacility() {
		return investmentDecisionSecondaryFacility;
	}


	/**
	 * @param investmentDecisionSecondaryFacility the investmentDecisionSecondaryFacility to set
	 */
	public void setInvestmentDecisionSecondaryFacility(int investmentDecisionSecondaryFacility) {
		this.investmentDecisionSecondaryFacility = investmentDecisionSecondaryFacility;
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
	 * @return
	 */
	public ArrayList<Event> generateScenarioTree () {
		
		ArrayList<Event> list = new ArrayList<Event> ();
		
		
		return list;
	}
	
	
	/**
	 * 
	 * @param s_T
	 * @param a_T
	 * @return
	 */
	public double calculateF (int s_T, int a_T, int gamma_T) {
		
		double result = s_T * this.parameter_constructionCostPrimaryFacility + this.parameter_penaltyCost * s_T + this.parameter_setupCostPrimaryFacility * Math.max((1-a_T),0);
		
		return result;
	}
	

	/**
	 * 
	 * @return
	 */
	public double calculateTestProbability () {
		
		double gamma = this.countSuccessfulTests[this.countPeriods-1];
		double zeta = this.countFailedTests[this.countPeriods-1];
		
		double p = gamma / (gamma + zeta);
		
		this.testProbability[this.countPeriods] = p;
		
		return p;		
	}
	
	
	
	// TODO RAMONA: create several toString Method for console output
	

}
