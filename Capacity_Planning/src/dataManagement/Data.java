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
	
	private double parameter_capacityDemand;							//q_d (LocationPlanningModel)
	
	private int [] testResults;										// Delta_t
	private int [] countSuccessfulTests;								// Gamma_t
	private int [] countFailedTests;									// Zeta_t
	private int [] remainingYearsToBuildPrimaryFacility;				// s_p_t
	private double [] testProbability;								// p
	
	private int [] investmentDecisionPrimaryFacility;					// a_p_t
	private int investmentDecisionSecondaryFacility;					// a_s_T+1 - secondary facility is built in period T+1 if clinical trails are successful
	
	private int countPeriods;										// t
	
	private ArrayList<ArrayList<Event>> scenarioTree;				// needed to calculate E[V]
								
	//Location Planning Model

    // Indices
    private int i; // number of material types
    private int f; // number of internal facilities
    private int g; // number of external facilities
    private int s; // number of suppliers //TODO: stimmt das?
    private int c; // number of customers //TODO: stimmt das?
    private int t; //number of fiscal years in planning horizon
    private int tau; ////TODO:??
    private int n; //number of nations 
    
    //Sets 
    
    
    // Daten
    private double [] capitalBudget;//capitalBudget[t]								CB_t
    private double [] capitalExpenditure ; // capitalExpenditure[t]					CE_t
	private double [][][][]costInsuranceFreight; //costInsuranceFreight[i][s][f][t]	CIF_isft
    private double [][][]demand;//demand[i][c][t]										D_ict
    private double [][][][]importDuty[][][][]; //importDuty[i][s][f][t]				ID_isft
    private double []projectLife;//projectLife[t]										L_f
    private double variableProductionCostsPrimaryFacility;//							MC_p_f
    private double variableProductionCostsSecondaryFacility;//						MC_s_f
    private double [][][][]unitSellingPrice;//unitSellingPrice[i][f][g][t]			P_ifgt
    private double []lowerLimitExpansionSize;//lowerLimitExpansionSize[f]				g_L_f
    private double []initialCapacity;//initialCapacity[f] at time zero				Q_f0
    private double []upperLimitCapacity;//upperLimitCapacity[f]						Q_U_f
    private double [][][]supply;//supply[i][s][t]										S_ist
    private double [][]corporateTax;//corporateTax[n][t]								TR_nt
    private double []lowerLimitProductionAPI;//lowerLimitProductionAPI[f]				X_L_f
    private int API; //TODO: one material from set i									pi
    private double [][]materialCoefficient; //materialCoeeficient[i][f]				sigma_if
    private double [][]capacityExpansionAmount;//capacityExpansionAmount[f][t]		q_ft
    
    //Transfer parameter
    private int remainingTimeOfClinicalTrials;//T*-t*									delta_t*
    
    

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
		
		// Initialize ArrayList
		
		this.scenarioTree = new ArrayList<ArrayList<Event>>();
				
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
	 * @return the scenarioTree
	 */
	public ArrayList<ArrayList<Event>> getScenarioTree() {
		return scenarioTree;
	}


	/**
	 * @param scenarioTree the scenarioTree to set
	 */
	public void setScenarioTree(ArrayList<ArrayList<Event>> scenarioTree) {
		this.scenarioTree = scenarioTree;
	}


	/**
	 * @return the parameter_capacityDemand
	 */
	public double getParameter_capacityDemand() {
		return parameter_capacityDemand;
	}


	/**
	 * @param parameter_capacityDemand the parameter_capacityDemand to set
	 */
	public void setParameter_capacityDemand(double parameter_capacityDemand) {
		this.parameter_capacityDemand = parameter_capacityDemand;
	}


	/**
	 * @return the i
	 */
	public int getI() {
		return i;
	}


	/**
	 * @param i the i to set
	 */
	public void setI(int i) {
		this.i = i;
	}


	/**
	 * @return the f
	 */
	public int getF() {
		return f;
	}


	/**
	 * @param f the f to set
	 */
	public void setF(int f) {
		this.f = f;
	}


	/**
	 * @return the g
	 */
	public int getG() {
		return g;
	}


	/**
	 * @param g the g to set
	 */
	public void setG(int g) {
		this.g = g;
	}


	/**
	 * @return the s
	 */
	public int getS() {
		return s;
	}


	/**
	 * @param s the s to set
	 */
	public void setS(int s) {
		this.s = s;
	}


	/**
	 * @return the c
	 */
	public int getC() {
		return c;
	}


	/**
	 * @param c the c to set
	 */
	public void setC(int c) {
		this.c = c;
	}


	/**
	 * @return the t
	 */
	public int getT() {
		return t;
	}


	/**
	 * @param t the t to set
	 */
	public void setT(int t) {
		this.t = t;
	}


	/**
	 * @return the tau
	 */
	public int getTau() {
		return tau;
	}


	/**
	 * @param tau the tau to set
	 */
	public void setTau(int tau) {
		this.tau = tau;
	}


	/**
	 * @return the n
	 */
	public int getN() {
		return n;
	}


	/**
	 * @param n the n to set
	 */
	public void setN(int n) {
		this.n = n;
	}


	/**
	 * @return the capitalBudget
	 */
	public double[] getCapitalBudget() {
		return capitalBudget;
	}


	/**
	 * @param capitalBudget the capitalBudget to set
	 */
	public void setCapitalBudget(double[] capitalBudget) {
		this.capitalBudget = capitalBudget;
	}


	/**
	 * @return the capitalExpenditure
	 */
	public double[] getCapitalExpenditure() {
		return capitalExpenditure;
	}


	/**
	 * @param capitalExpenditure the capitalExpenditure to set
	 */
	public void setCapitalExpenditure(double[] capitalExpenditure) {
		this.capitalExpenditure = capitalExpenditure;
	}


	/**
	 * @return the costInsuranceFreight
	 */
	public double[][][][] getCostInsuranceFreight() {
		return costInsuranceFreight;
	}


	/**
	 * @param costInsuranceFreight the costInsuranceFreight to set
	 */
	public void setCostInsuranceFreight(double[][][][] costInsuranceFreight) {
		this.costInsuranceFreight = costInsuranceFreight;
	}


	/**
	 * @return the demand
	 */
	public double[][][] getDemand() {
		return demand;
	}


	/**
	 * @param demand the demand to set
	 */
	public void setDemand(double[][][] demand) {
		this.demand = demand;
	}


	/**
	 * @return the importDuty
	 */
	public double[][][][][][][][] getImportDuty() {
		return importDuty;
	}


	/**
	 * @param importDuty the importDuty to set
	 */
	public void setImportDuty(double[][][][][][][][] importDuty) {
		this.importDuty = importDuty;
	}


	/**
	 * @return the projectLife
	 */
	public double[] getProjectLife() {
		return projectLife;
	}


	/**
	 * @param projectLife the projectLife to set
	 */
	public void setProjectLife(double[] projectLife) {
		this.projectLife = projectLife;
	}


	/**
	 * @return the variableProductionCostsPrimaryFacility
	 */
	public double getVariableProductionCostsPrimaryFacility() {
		return variableProductionCostsPrimaryFacility;
	}


	/**
	 * @param variableProductionCostsPrimaryFacility the variableProductionCostsPrimaryFacility to set
	 */
	public void setVariableProductionCostsPrimaryFacility(double variableProductionCostsPrimaryFacility) {
		this.variableProductionCostsPrimaryFacility = variableProductionCostsPrimaryFacility;
	}


	/**
	 * @return the variableProductionCostsSecondaryFacility
	 */
	public double getVariableProductionCostsSecondaryFacility() {
		return variableProductionCostsSecondaryFacility;
	}


	/**
	 * @param variableProductionCostsSecondaryFacility the variableProductionCostsSecondaryFacility to set
	 */
	public void setVariableProductionCostsSecondaryFacility(double variableProductionCostsSecondaryFacility) {
		this.variableProductionCostsSecondaryFacility = variableProductionCostsSecondaryFacility;
	}


	/**
	 * @return the unitSellingPrice
	 */
	public double[][][][] getUnitSellingPrice() {
		return unitSellingPrice;
	}


	/**
	 * @param unitSellingPrice the unitSellingPrice to set
	 */
	public void setUnitSellingPrice(double[][][][] unitSellingPrice) {
		this.unitSellingPrice = unitSellingPrice;
	}


	/**
	 * @return the lowerLimitExpansionSize
	 */
	public double[] getLowerLimitExpansionSize() {
		return lowerLimitExpansionSize;
	}


	/**
	 * @param lowerLimitExpansionSize the lowerLimitExpansionSize to set
	 */
	public void setLowerLimitExpansionSize(double[] lowerLimitExpansionSize) {
		this.lowerLimitExpansionSize = lowerLimitExpansionSize;
	}


	/**
	 * @return the initialCapacity
	 */
	public double[] getInitialCapacity() {
		return initialCapacity;
	}


	/**
	 * @param initialCapacity the initialCapacity to set
	 */
	public void setInitialCapacity(double[] initialCapacity) {
		this.initialCapacity = initialCapacity;
	}


	/**
	 * @return the upperLimitCapacity
	 */
	public double[] getUpperLimitCapacity() {
		return upperLimitCapacity;
	}


	/**
	 * @param upperLimitCapacity the upperLimitCapacity to set
	 */
	public void setUpperLimitCapacity(double[] upperLimitCapacity) {
		this.upperLimitCapacity = upperLimitCapacity;
	}


	/**
	 * @return the supply
	 */
	public double[][][] getSupply() {
		return supply;
	}


	/**
	 * @param supply the supply to set
	 */
	public void setSupply(double[][][] supply) {
		this.supply = supply;
	}


	/**
	 * @return the corporateTax
	 */
	public double[][] getCorporateTax() {
		return corporateTax;
	}


	/**
	 * @param corporateTax the corporateTax to set
	 */
	public void setCorporateTax(double[][] corporateTax) {
		this.corporateTax = corporateTax;
	}


	/**
	 * @return the lowerLimitProductionAPI
	 */
	public double[] getLowerLimitProductionAPI() {
		return lowerLimitProductionAPI;
	}


	/**
	 * @param lowerLimitProductionAPI the lowerLimitProductionAPI to set
	 */
	public void setLowerLimitProductionAPI(double[] lowerLimitProductionAPI) {
		this.lowerLimitProductionAPI = lowerLimitProductionAPI;
	}


	/**
	 * @return the aPI
	 */
	public int getAPI() {
		return API;
	}


	/**
	 * @param aPI the aPI to set
	 */
	public void setAPI(int aPI) {
		API = aPI;
	}


	/**
	 * @return the materialCoefficient
	 */
	public double[][] getMaterialCoefficient() {
		return materialCoefficient;
	}


	/**
	 * @param materialCoefficient the materialCoefficient to set
	 */
	public void setMaterialCoefficient(double[][] materialCoefficient) {
		this.materialCoefficient = materialCoefficient;
	}


	/**
	 * @return the capacityExpansionAmount
	 */
	public double[][] getCapacityExpansionAmount() {
		return capacityExpansionAmount;
	}


	/**
	 * @param capacityExpansionAmount the capacityExpansionAmount to set
	 */
	public void setCapacityExpansionAmount(double[][] capacityExpansionAmount) {
		this.capacityExpansionAmount = capacityExpansionAmount;
	}


	/**
	 * @return the remainingTimeOfClinicalTrials
	 */
	public int getRemainingTimeOfClinicalTrials() {
		return remainingTimeOfClinicalTrials;
	}


	/**
	 * @param remainingTimeOfClinicalTrials the remainingTimeOfClinicalTrials to set
	 */
	public void setRemainingTimeOfClinicalTrials(int remainingTimeOfClinicalTrials) {
		this.remainingTimeOfClinicalTrials = remainingTimeOfClinicalTrials;
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
	public ArrayList<ArrayList<Event>> generateScenarioTree () {
		
		ArrayList<ArrayList<Event>> scenarioTree = new ArrayList<ArrayList<Event>>();
		
		// Create an ArrayList for period t element of {1,...,T} which is added to the scenario tree
		
		for (int t = 1; t <= this.parameter_planningHorizon; t++) {
			
			ArrayList<Event> period_t = new ArrayList<Event>();
			scenarioTree.add(period_t);
			
			int numberOfEvents = (int) Math.pow(2.0, t);
			
			// For each period the events are created and put into the ArrayList period_t
			
			for (int index = 0; index < numberOfEvents; index++) {
				
				Event tmp_event = new Event ();
				
				tmp_event.setPeriod(t);
				tmp_event.setIndex(index);
				
				// Is it a final event for which cost can be calculated?
				
				if (t == this.parameter_planningHorizon) {
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
		
		// Setting all countSuccessfulTestResults and countFailedTestResults
		
		for (int t = 0; t < scenarioTree.size(); t++) {
			
			for (int index = 0; index < scenarioTree.get(t).size(); index++) {
				
				
			}
			
		}
				
				
				
				
				
				
				/**
				 * 
				 * 
	private double probability;
	
	private int countSuccessfulTestResults;
	private int countFailedTestResults;
	
	private double nextProbabilitySuccessful_left;
	private double nextProbabilityFailed_right;
	

				 */
				
		return scenarioTree;
		
	}
	
	
	//TODO: final cost with each scenario
	
	
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
	
	
	/**
	 * 
	 * @param gamma
	 * @param zeta
	 * @return
	 */
	public double calculateTestProbability (int gamma, int zeta) {
		
		return gamma / (gamma + zeta);
		
	}
	
	
	// TODO RAMONA: create several toString Method for console output
	

	public static void main(String[] args) {
		
		System.out.println (0 % 2);
		
		
	}
	
	
	
}
