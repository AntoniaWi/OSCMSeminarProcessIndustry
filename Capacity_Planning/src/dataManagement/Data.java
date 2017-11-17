package dataManagement;

import jxl.read.biff.BiffException;
import jxl.write.WriteException;
import java.io.IOException;

public class Data {
	
	// Timing Model
	
	private int parameter_planningHorizon;  								// T
	
	private double parameter_discountFactor_timing;						// alpha
	private double parameter_discountFactor_location;						// alpha
	
	private int parameter_periodsToBuildPrimaryFacilities;				// s_p_0 - in whole years
	private int parameter_periodsToBuildSecondaryFacilities;				// s_s_0 - in fraction of years - if construction takes place, it starts in T+1
	
	private double parameter_constructionCostPrimaryFacility;				// c_p
	private double parameter_constructionCostSecondaryFacility;			// c_s
	
	private double parameter_setupCostPrimaryFacility;					// K_p
	private double parameter_setupCostSecondaryFacility;					// K_s
	
	private int parameter_penaltyCost;									// Phi(s_t) = Phi*s_t
	
	private int parameter_preliminaryKnowledgeAboutSuccessfulTests;		// Gamma_0
	private int parameter_preliminaryKnowledgeAboutFailedTests;			// Zeta_0
	private int parameter_thresholdSuccessfulTests;						// Gamma_c
	
	private int [] testResults;											// Delta_t
	private int [] countSuccessfulTests;									// Gamma_t
	private int [] countFailedTests;										// Zeta_t
	private int [] remainingYearsToBuildPrimaryFacility;					// s_p_t
	private double [] testProbability;									// p
	private int remainingTimeofClinicalTrials;							//delta_t
	
	private int [] investmentDecisionPrimaryFacility;						// a_p_t
	private int investmentDecisionSecondaryFacility;						// a_s_T+1 - secondary facility is built in period T+1 if clinical trails are successful
	private int[][] investmentStrategies;
	
	private int countPeriods;											// t
	
	private double totalConstructionCost_primary;
	private double totalSetUpCost_primary;
	private double totalPenaltyCost_primary;
	private double totalExpansionCost_primary;
	private boolean successOfClinicalTrials;
	
	//Location Planning Model

	// Sets
	private boolean[] IF; 												// IF[f] internal facilities
	private boolean[] EF; 												// EF[f] external facilities
	private boolean[][] OM; 												// OM[f][i] outgoing material
	private boolean[][] IM; 												// IM[f][i]incoming material		
	private boolean[][] Fn; 												// Fn[f][n] nations
	private boolean[] PIF; 												// PIF[f]
	private boolean[] SIF; 												// SIF[f]

	// Parameter
	private int I; 														// number of material types
	private int F; 														// number of all facilities
	private int T; 														// number of months in planning horizon
	private int N; 														// number of nations
	private double[] capitalBudget;										// capitalBudget[t] CB_t
	private double[][][] costInsuranceFreight; 							// costInsuranceFreight[i][s][f] CIF_isf
	private double[][][] demand;											// demand[i][c][t] D_ict
	private double[][] importDuty; 										// importDuty[s][f] ID_isf
	private int projectLife;												// projectLife[t] L_f
	private double[] variableProductionCosts;							// MC_f
	private double[][][] unitSellingPrice;								// unitSellingPrice[i][f][t] P_ift
	private double[] lowerLimitExpansionSize;							// lowerLimitExpansionSize[f] g_L_f
	private double[] upperLimitCapacity;									// upperLimitCapacity[f] Q_U_f
	private double[][] supply;											// supply[i][s] S_is
	private double[] corporateTax;										// corporateTax[n]TR_n
	private double[] lowerLimitProductionAPI;							// lowerLimitProductionAPI[f] X_L_f
	private int API; //
	private double[][] materialCoefficient; 								// materialCoeeficient[i][f] sigma_if
	private int initialCapacity; 										// Q0
	private int monthsToBuildPrimaryFacilities_location;					// s_p_0 - in whole years
	private int monthsToBuildSecondaryFacilities_location;				// s_s_0 - in fraction of years - if construction takes place, it starts in T+1
	private double constructionCostPrimaryFacility_location;				// c_p
	private double constructionCostSecondaryFacility_location;
	
	//Help parameter for demand
	private double [] demandM; 											// demandM[f]
	private double [] demandR; 											// demand[f]
	private int timeM;
	private int timeR;
	
	//Result Arrays
	private double[][][][] result_shippedMaterialUnitsFacilityToCustomer; // F_ifct
	private double[][][][] result_shippedMaterialUnitsSupplierToFacility; // F_isft
	private double[][] result_availableProductionCapacity; 				// Q_ft
	private double[][] result_taxableIncome; 							// TI_nt
	private double[][] result_consumedOrProducedAPI; 						// X_ft
	private double[] result_capitalExpenditure; 							// CE_t
	private double[][] result_grossIncome; 								// GI_ft
	private double[][] result_deltaCapacityExpansion; 					// delta_q_ft
	private double[][] result_capacityExpansionAmount; 					// q_ft

	private double[][] result_constructionStartPrimaryFacility; 			// y_ft
	private double[][] result_constructionStartSecondaryFacility; 		// z_ft	

	private double result_netPresentValue;
	
	/**
	 * Constructor: empty data instance
	 */
	public Data () {
		
		
	}
	
	
	
	/**
	 * Constructor: with data from excel sheet
	 * @param x
	 * @throws BiffException
	 * @throws IOException
	 * @throws WriteException
	 */
	public Data (int x) throws BiffException, IOException, WriteException {
		
		ReadAndWrite.readDataTiming(this);
		ReadAndWrite.readConst(this);
		ReadAndWrite.readF(this);
		ReadAndWrite.readFinN(this);
		ReadAndWrite.readIMf(this);
		ReadAndWrite.readOMf(this);
		ReadAndWrite.readTRn(this);
		ReadAndWrite.readMassbalance(this);
		ReadAndWrite.readDataF(this);
		ReadAndWrite.readSis(this);
		ReadAndWrite.readIDsf(this);
		ReadAndWrite.readCIFsf(this);
		ReadAndWrite.readDictBasics(this);
		
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
		
		this.remainingYearsToBuildPrimaryFacility = new int [this.parameter_planningHorizon + 2];
		this.remainingYearsToBuildPrimaryFacility[0] = this.parameter_periodsToBuildPrimaryFacilities;
		
		for (int i = 1; i < this.remainingYearsToBuildPrimaryFacility.length; i++) {	
			this.remainingYearsToBuildPrimaryFacility[i] = -1;
		}	
		
		// Currently no investment decision about primary facility made
		
		this.investmentDecisionPrimaryFacility = new int [this.parameter_planningHorizon + 1];
		this.investmentDecisionPrimaryFacility[0] = 0;
		for (int i = 1; i < this.investmentDecisionPrimaryFacility.length; i++) {
			this.investmentDecisionPrimaryFacility[i] = -1;
		}
		
		// Currently no test probability is calculated
		
		this.testProbability = new double [this.parameter_planningHorizon + 1];
		for (int i = 0; i < this.testProbability.length; i++) {
			this.testProbability[i] = -1;
		}
		
		// Currently no investment decision about secondary facility made
		
		this.investmentDecisionSecondaryFacility = -1;
		
		// Initiate strategy list to see how strategy updates influence the overall decision making, period 0 does not have any strategy
		
		this.investmentStrategies = new int [this.parameter_planningHorizon + 1][this.parameter_planningHorizon + 1];
		
		for (int i = 0; i < this.parameter_planningHorizon; i++) {
			
			this.investmentStrategies[0][i] = -1;
		}
		
		// Start in period t = 0
		
		this.countPeriods = 0;
		
		this.totalConstructionCost_primary = 0;
		this.totalSetUpCost_primary = 0;
		this.totalPenaltyCost_primary = 0;
		this.totalExpansionCost_primary = 0; 
		
		this.successOfClinicalTrials = false;
	
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
		return parameter_discountFactor_timing;
	}


	/**
	 * @param parameter_discountFactor the parameter_discountFactor to set
	 */
	public void setParameter_discountFactor(double parameter_discountFactor) {
		this.parameter_discountFactor_timing = parameter_discountFactor;
	}


	/**
	 * @return the parameter_monthsToBuildPrimaryFacilities
	 */
	public int getParameter_periodsToBuildPrimaryFacilities() {
		return parameter_periodsToBuildPrimaryFacilities;
	}


	/**
	 * @param parameter_monthsToBuildPrimaryFacilities the parameter_yearsToBuildPrimaryFacilities to set
	 */
	public void setParameter_periodsToBuildPrimaryFacilities(int parameter_monthsToBuildPrimaryFacilities) {
		this.parameter_periodsToBuildPrimaryFacilities = parameter_monthsToBuildPrimaryFacilities;
	}


	/**
	 * @return the parameter_monthsToBuildSecondaryFacilities
	 */
	public int getParameter_periodsToBuildSecondaryFacilities() {
		return parameter_periodsToBuildSecondaryFacilities;
	}


	/**
	 * @param parameter_monthsToBuildSecondaryFacilities the parameter_yearsToBuildSecondaryFacilities to set
	 */
	public void setParameter_periodsToBuildSecondaryFacilities(int parameter_monthsToBuildSecondaryFacilities) {
		this.parameter_periodsToBuildSecondaryFacilities = parameter_monthsToBuildSecondaryFacilities;
	}


	/**
	 * @return the parameter_constructionCostPrimaryFacility
	 */
	public double getParameter_constructionCostPrimaryFacility() {
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
	public double getParameter_constructionCostSecondaryFacility() {
		return parameter_constructionCostSecondaryFacility;
	}


	/**
	 * @param parameter_constructionCostSecondaryFacility the parameter_constructionCostSecondaryFacility to set
	 */
	public void setParameter_constructionCostSecondaryFacility(double parameter_constructionCostSecondaryFacility) {
		this.parameter_constructionCostSecondaryFacility = parameter_constructionCostSecondaryFacility;
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
	 * @return the investmentStrategies
	 */
	public int[][] getInvestmentStrategies() {
		return investmentStrategies;
	}

	/**
	 * @param investmentStrategies the investmentStrategies to set
	 */
	public void setInvestmentStrategies(int[][] investmentStrategies) {
		this.investmentStrategies = investmentStrategies;
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
	 * @return the costInsuranceFreight
	 */
	public double[][][] getCostInsuranceFreight() {
		return costInsuranceFreight;
	}


	/**
	 * @param costInsuranceFreight the costInsuranceFreight to set
	 */
	public void setCostInsuranceFreight(double[][][] costInsuranceFreight) {
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
	public double[][] getImportDuty() {
		return importDuty;
	}


	/**
	 * @param importDuty the importDuty to set
	 */
	public void setImportDuty(double[][]importDuty) {
		this.importDuty = importDuty;
	}


	/**
	 * @return the projectLife
	 */
	public int getProjectLife() {
		return projectLife;
	}


	/**
	 * @param projectLife the projectLife to set
	 */
	public void setProjectLife(int projectLife) {
		this.projectLife = projectLife;
	}


	/**
	 * @return the variableProductionCostsPrimaryFacility
	 */
	public double [] getVariableProductionCosts() {
		return variableProductionCosts;
	}


	/**
	 * @param variableProductionCostsPrimaryFacility the variableProductionCostsPrimaryFacility to set
	 */
	public void setVariableProductionCosts(double [] variableProductionCosts) {
		this.variableProductionCosts = variableProductionCosts;
	}



	/**
	 * @return the unitSellingPrice
	 */
	public double[][][] getUnitSellingPrice() {
		return unitSellingPrice;
	}


	/**
	 * @param unitSellingPrice the unitSellingPrice to set
	 */
	public void setUnitSellingPrice(double[][][] unitSellingPrice) {
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
	public double[][] getSupply() {
		return supply;
	}


	/**
	 * @param supply the supply to set
	 */
	public void setSupply(double[][] supply) {
		this.supply = supply;
	}


	/**
	 * @return the corporateTax
	 */
	public double[] getCorporateTax() {
		return corporateTax;
	}


	/**
	 * @param corporateTax the corporateTax to set
	 */
	public void setCorporateTax(double[] corporateTax) {
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
	 * @param parameter_constructionCostPrimaryFacility the parameter_constructionCostPrimaryFacility to set
	 */
	public void setParameter_constructionCostPrimaryFacility(double parameter_constructionCostPrimaryFacility) {
		this.parameter_constructionCostPrimaryFacility = parameter_constructionCostPrimaryFacility;
	}


	/**
	 * @param parameter_setupCostPrimaryFacility the parameter_setupCostPrimaryFacility to set
	 */
	public void setParameter_setupCostPrimaryFacility(double parameter_setupCostPrimaryFacility) {
		this.parameter_setupCostPrimaryFacility = parameter_setupCostPrimaryFacility;
	}


	/**
	 * @param parameter_setupCostSecondaryFacility the parameter_setupCostSecondaryFacility to set
	 */
	public void setParameter_setupCostSecondaryFacility(double parameter_setupCostSecondaryFacility) {
		this.parameter_setupCostSecondaryFacility = parameter_setupCostSecondaryFacility;
	}


	/**
	 * @return the iF
	 */
	public boolean[] getIF() {
		return IF;
	}


	/**
	 * @param iF the iF to set
	 */
	public void setIF(boolean[] iF) {
		IF = iF;
	}


	/**
	 * @return the eF
	 */
	public boolean[] getEF() {
		return EF;
	}


	/**
	 * @param eF the eF to set
	 */
	public void setEF(boolean[] eF) {
		EF = eF;
	}


	/**
	 * @return the oM
	 */
	public boolean[][] getOM() {
		return OM;
	}


	/**
	 * @param oM the oM to set
	 */
	public void setOM(boolean[][] oM) {
		OM = oM;
	}


	/**
	 * @return the iM
	 */
	public boolean[][] getIM() {
		return IM;
	}


	/**
	 * @param iM the iM to set
	 */
	public void setIM(boolean[][] iM) {
		IM = iM;
	}


	/**
	 * @return the fn
	 */
	public boolean[][] getFn() {
		return Fn;
	}


	/**
	 * @param fn the fn to set
	 */
	public void setFn(boolean[][] fn) {
		Fn = fn;
	}


	/**
	 * @return the pIF
	 */
	public boolean[] getPIF() {
		return PIF;
	}


	/**
	 * @param pIF the pIF to set
	 */
	public void setPIF(boolean[] pIF) {
		PIF = pIF;
	}


	/**
	 * @return the sIF
	 */
	public boolean[] getSIF() {
		return SIF;
	}


	/**
	 * @param sIF the sIF to set
	 */
	public void setSIF(boolean[] sIF) {
		SIF = sIF;
	}


	/**
	 * @return the i
	 */
	public int getI() {
		return I;
	}


	/**
	 * @param i the i to set
	 */
	public void setI(int i) {
		I = i;
	}


	/**
	 * @return the f
	 */
	public int getF() {
		return F;
	}


	/**
	 * @param f the f to set
	 */
	public void setF(int f) {
		F = f;
	}


	/**
	 * @return the t
	 */
	public int getT() {
		return T;
	}


	/**
	 * @param t the t to set
	 */
	public void setT(int t) {
		T = t;
	}


	/**
	 * @return the n
	 */
	public int getN() {
		return N;
	}


	/**
	 * @param n the n to set
	 */
	public void setN(int n) {
		N = n;
	}


	/**
	 * @return the initialCapacity
	 */
	public int getInitialCapacity() {
		return initialCapacity;
	}


	/**
	 * @param initialCapacity the initialCapacity to set
	 */
	public void setInitialCapacity(int initialCapacity) {
		this.initialCapacity = initialCapacity;
	}


	/**
	 * @return the remainingTimeofClinicalTrials
	 */
	public int getRemainingTimeofClinicalTrials() {
		return remainingTimeofClinicalTrials;
	}


	/**
	 * @param remainingTimeofClinicalTrials the remainingTimeofClinicalTrials to set
	 */
	public void setRemainingTimeofClinicalTrials(int remainingTimeofClinicalTrials) {
		this.remainingTimeofClinicalTrials = remainingTimeofClinicalTrials;
	}


	/**
	 * @return the parameter_setupCostPrimaryFacility
	 */
	public double getParameter_setupCostPrimaryFacility() {
		return parameter_setupCostPrimaryFacility;
	}

	/**
	 * @return the parameter_setupCostSecondaryFacility
	 */
	public double getParameter_setupCostSecondaryFacility() {
		return parameter_setupCostSecondaryFacility;
	}

	/**
	 * @return the demandM
	 */
	public double[] getDemandM() {
		return demandM;
	}

	/**
	 * @param demandM the demandM to set
	 */
	public void setDemandM(double[] demandM) {
		this.demandM = demandM;
	}

	/**
	 * @return the demandR
	 */
	public double[] getDemandR() {
		return demandR;
	}

	/**
	 * @param demandR the demandR to set
	 */
	public void setDemandR(double[] demandR) {
		this.demandR = demandR;
	}

	/**
	 * @return the timeM
	 */
	public int getTimeM() {
		return timeM;
	}

	/**
	 * @param timeM the timeM to set
	 */
	public void setTimeM(int timeM) {
		this.timeM = timeM;
	}

	/**
	 * @return the timeR
	 */
	public int getTimeR() {
		return timeR;
	}

	/**
	 * @param timeR the timeR to set
	 */
	public void setTimeR(int timeR) {
		this.timeR = timeR;
	}

	
	/**
	 * @return the result_shippedMaterialUnitsFacilityToCustomer
	 */
	public double[][][][] getResult_shippedMaterialUnitsFacilityToCustomer() {
		return result_shippedMaterialUnitsFacilityToCustomer;
	}

	/**
	 * @param result_shippedMaterialUnitsFacilityToCustomer the result_shippedMaterialUnitsFacilityToCustomer to set
	 */
	public void setResult_shippedMaterialUnitsFacilityToCustomer(
			double[][][][] result_shippedMaterialUnitsFacilityToCustomer) {
		this.result_shippedMaterialUnitsFacilityToCustomer = result_shippedMaterialUnitsFacilityToCustomer;
	}

	/**
	 * @return the result_shippedMaterialUnitsSupplierToFacility
	 */
	public double[][][][] getResult_shippedMaterialUnitsSupplierToFacility() {
		return result_shippedMaterialUnitsSupplierToFacility;
	}

	/**
	 * @param result_shippedMaterialUnitsSupplierToFacility the result_shippedMaterialUnitsSupplierToFacility to set
	 */
	public void setResult_shippedMaterialUnitsSupplierToFacility(
			double[][][][] result_shippedMaterialUnitsSupplierToFacility) {
		this.result_shippedMaterialUnitsSupplierToFacility = result_shippedMaterialUnitsSupplierToFacility;
	}


	/**
	 * @return the result_availableProductionCapacity
	 */
	public double[][] getResult_availableProductionCapacity() {
		return result_availableProductionCapacity;
	}

	/**
	 * @param result_availableProductionCapacity the result_availableProductionCapacity to set
	 */
	public void setResult_availableProductionCapacity(double[][] result_availableProductionCapacity) {
		this.result_availableProductionCapacity = result_availableProductionCapacity;
	}

	/**
	 * @return the result_taxableIncome
	 */
	public double[][] getResult_taxableIncome() {
		return result_taxableIncome;
	}

	/**
	 * @param result_taxableIncome the result_taxableIncome to set
	 */
	public void setResult_taxableIncome(double[][] result_taxableIncome) {
		this.result_taxableIncome = result_taxableIncome;
	}

	

	/**
	 * @return the result_consumedOrProducedAPI
	 */
	public double[][] getResult_consumedOrProducedAPI() {
		return result_consumedOrProducedAPI;
	}

	/**
	 * @param result_consumedOrProducedAPI the result_consumedOrProducedAPI to set
	 */
	public void setResult_consumedOrProducedAPI(double[][] result_consumedOrProducedAPI) {
		this.result_consumedOrProducedAPI = result_consumedOrProducedAPI;
	}

	/**
	 * @return the result_capitalExpenditure
	 */
	public double[] getResult_capitalExpenditure() {
		return result_capitalExpenditure;
	}

	/**
	 * @param result_capitalExpenditure the result_capitalExpenditure to set
	 */
	public void setResult_capitalExpenditure(double[] result_capitalExpenditure) {
		this.result_capitalExpenditure = result_capitalExpenditure;
	}

	/**
	 * @return the result_grossIncome
	 */
	public double[][] getResult_grossIncome() {
		return result_grossIncome;
	}

	/**
	 * @param result_grossIncome the result_grossIncome to set
	 */
	public void setResult_grossIncome(double[][] result_grossIncome) {
		this.result_grossIncome = result_grossIncome;
	}

	/**
	 * @return the result_deltaCapacityExpansion
	 */
	public double[][] getResult_deltaCapacityExpansion() {
		return result_deltaCapacityExpansion;
	}

	/**
	 * @param result_deltaCapacityExpansion the result_deltaCapacityExpansion to set
	 */
	public void setResult_deltaCapacityExpansion(double[][] result_deltaCapacityExpansion) {
		this.result_deltaCapacityExpansion = result_deltaCapacityExpansion;
	}

	/**
	 * @return the result_capacityExpansionAmount
	 */
	public double[][] getResult_capacityExpansionAmount() {
		return result_capacityExpansionAmount;
	}

	/**
	 * @param result_capacityExpansionAmount the result_capacityExpansionAmount to set
	 */
	public void setResult_capacityExpansionAmount(double[][] result_capacityExpansionAmount) {
		this.result_capacityExpansionAmount = result_capacityExpansionAmount;
	}

	/**
	 * @return the result_constructionStartPrimaryFacility
	 */
	public double[][] getResult_constructionStartPrimaryFacility() {
		return result_constructionStartPrimaryFacility;
	}

	/**
	 * @param result_constructionStartPrimaryFacility the result_constructionStartPrimaryFacility to set
	 */
	public void setResult_constructionStartPrimaryFacility(double[][] result_constructionStartPrimaryFacility) {
		this.result_constructionStartPrimaryFacility = result_constructionStartPrimaryFacility;
	}

	/**
	 * @return the result_constructionStartSecondaryFacility
	 */
	public double[][] getResult_constructionStartSecondaryFacility() {
		return result_constructionStartSecondaryFacility;
	}

	/**
	 * @param result_constructionStartSecondaryFacility the result_constructionStartSecondaryFacility to set
	 */
	public void setResult_constructionStartSecondaryFacility(double[][] result_constructionStartSecondaryFacility) {
		this.result_constructionStartSecondaryFacility = result_constructionStartSecondaryFacility;
	}

	/**

	 * @param parameter_discountFactor_timing the parameter_discountFactor_timing to set
	 */
	public void setParameter_discountFactor_timing(double parameter_discountFactor_timing) {
		this.parameter_discountFactor_timing = parameter_discountFactor_timing;
	}


	/**
	 * @return the parameter_discountFactor_location
	 */
	public double getParameter_discountFactor_location() {
		return parameter_discountFactor_location;
	}





	/**
	 * @return the parameter_discountFactor_timing
	 */
	public double getParameter_discountFactor_timing() {
		return parameter_discountFactor_timing;
	}


	/**
	 * @param parameter_discountFactor_location the parameter_discountFactor_location to set
	 */
	public void setParameter_discountFactor_location(double parameter_discountFactor_location) {
		this.parameter_discountFactor_location = parameter_discountFactor_location;
	}


	/**
	 * @return the totalConstructionCost_primary
	 */
	public double getTotalConstructionCost_primary() {
		return totalConstructionCost_primary;
	}


	/**
	 * @param totalConstructionCost_primary the totalConstructionCost_primary to set
	 */
	public void setTotalConstructionCost_primary(double totalConstructionCost_primary) {
		this.totalConstructionCost_primary = totalConstructionCost_primary;
	}


	/**
	 * @return the totalSetUpCost_primary
	 */
	public double getTotalSetUpCost_primary() {
		return totalSetUpCost_primary;
	}


	/**
	 * @param totalSetUpCost_primary the totalSetUpCost_primary to set
	 */
	public void setTotalSetUpCost_primary(double totalSetUpCost_primary) {
		this.totalSetUpCost_primary = totalSetUpCost_primary;
	}


	/**
	 * @return the totalPenaltyCost_primary
	 */
	public double getTotalPenaltyCost_primary() {
		return totalPenaltyCost_primary;
	}


	/**
	 * @param totalPenaltyCost_primary the totalPenaltyCost_primary to set
	 */
	public void setTotalPenaltyCost_primary(double totalPenaltyCost_primary) {
		this.totalPenaltyCost_primary = totalPenaltyCost_primary;
	}


	/**
	 * @return the totalExpansionCost_primary
	 */
	public double getTotalExpansionCost_primary() {
		return totalExpansionCost_primary;
	}


	/**
	 * @param totalExpansionCost_primary the totalExpansionCost_primary to set
	 */
	public void setTotalExpansionCost_primary(double totalExpansionCost_primary) {
		this.totalExpansionCost_primary = totalExpansionCost_primary;
	}


	/**
	 * @return the monthsToBuildPrimaryFacilities_location
	 */
	public int getMonthsToBuildPrimaryFacilities_location() {
		return monthsToBuildPrimaryFacilities_location;
	}


	/**
	 * @param monthsToBuildPrimaryFacilities_location the monthsToBuildPrimaryFacilities_location to set
	 */
	public void setMonthsToBuildPrimaryFacilities_location(int monthsToBuildPrimaryFacilities_location) {
		this.monthsToBuildPrimaryFacilities_location = monthsToBuildPrimaryFacilities_location;
	}


	/**
	 * @return the monthsToBuildSecondaryFacilities_location
	 */
	public int getMonthsToBuildSecondaryFacilities_location() {
		return monthsToBuildSecondaryFacilities_location;
	}


	/**
	 * @param monthsToBuildSecondaryFacilities_location the monthsToBuildSecondaryFacilities_location to set
	 */
	public void setMonthsToBuildSecondaryFacilities_location(int monthsToBuildSecondaryFacilities_location) {
		this.monthsToBuildSecondaryFacilities_location = monthsToBuildSecondaryFacilities_location;
	}


	/**
	 * @return the constructionCostPrimaryFacility_location
	 */
	public double getConstructionCostPrimaryFacility_location() {
		return constructionCostPrimaryFacility_location;
	}


	/**
	 * @param constructionCostPrimaryFacility_location the constructionCostPrimaryFacility_location to set
	 */
	public void setConstructionCostPrimaryFacility_location(double constructionCostPrimaryFacility_location) {
		this.constructionCostPrimaryFacility_location = constructionCostPrimaryFacility_location;
	}


	/**
	 * @return the constructionCostSecondaryFacility_location
	 */
	public double getConstructionCostSecondaryFacility_location() {
		return constructionCostSecondaryFacility_location;
	}


	/**
	 * @param constructionCostSecondaryFacility_location the constructionCostSecondaryFacility_location to set
	 */
	public void setConstructionCostSecondaryFacility_location(double constructionCostSecondaryFacility_location) {
		this.constructionCostSecondaryFacility_location = constructionCostSecondaryFacility_location;
	}


	/**
	 * @return the clinicalTrialOutcome
	 */
	public boolean isSuccessOfClinicalTrials() {
		return successOfClinicalTrials;
	}


	/**
	 * @param clinicalTrialOutcome the clinicalTrialOutcome to set
	 */
	public void setSuccessOfClinicalTrials(boolean successOfClinicalTrials) {
		this.successOfClinicalTrials = successOfClinicalTrials;
	}

	
	/**
	 * @return the result_netPresentValue
	 */
	public double getResult_netPresentValue() {
		return result_netPresentValue;
	}


	/**
	 * @param result_netPresentValue the result_netPresentValue to set
	 */
	public void setResult_netPresentValue(double result_netPresentValue) {
		this.result_netPresentValue = result_netPresentValue;
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
	 */
	public void updateClinicalTrialOutcome () {
		
		int count = 0;
		
		for (int i = 0; i < this.testResults.length; i++) {
			
			if (this.testResults[i] == 1) {
				count++;
			}
		}
		
		if((count + this.parameter_preliminaryKnowledgeAboutSuccessfulTests) >= this.parameter_thresholdSuccessfulTests) {
		
			this.successOfClinicalTrials = true;
		}
	}
	
	
	/**
	 * Calculates test probability for current period
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
	 */
	private void calculateTotalConstructionCost () {
	
		double constructionCost = 0;
		
		for (int i = 0; i < this.investmentDecisionPrimaryFacility.length; i++) {
			
			constructionCost += this.investmentDecisionPrimaryFacility[i] * this.parameter_constructionCostPrimaryFacility;
		}
		
		this.totalConstructionCost_primary = constructionCost;
	}
	
	
	/**
	 * 
	 */
	private void calculateTotalSetUpCost () {
		
		double setUpCost = 0;
		
		for (int i = 1; i < this.investmentDecisionPrimaryFacility.length; i++) {
			
			int setUp = Math.max(this.investmentDecisionPrimaryFacility[i] - this.investmentDecisionPrimaryFacility[i-1], 0);
			
			setUpCost += this.parameter_setupCostPrimaryFacility * setUp;
		}
		
		this.totalSetUpCost_primary = setUpCost;
	}
	
	
	/**
	 * 
	 */
	private void calculateTotalPenaltyCost () {
		
		double penaltyCost = 0;
		
		if(this.successOfClinicalTrials) {
			
			int countInvestments = 0;
		
			for (int i = 0; i < this.investmentDecisionPrimaryFacility.length; i++) {
			
				if (investmentDecisionPrimaryFacility[i] == 1) {
				
					countInvestments++;
				}
			}
		
			int remainingPeriodsToBuild = this.parameter_periodsToBuildPrimaryFacilities - countInvestments;
			penaltyCost = this.parameter_penaltyCost * remainingPeriodsToBuild;
		
		}
		
		this.totalPenaltyCost_primary = penaltyCost;
	}
	
	
	/**
	 * 
	 */
	public void calculateTotalExpansionCost () {
		
		this.updateClinicalTrialOutcome();
		
		this.calculateTotalConstructionCost();
		this.calculateTotalSetUpCost();
		this.calculateTotalPenaltyCost();
		
		this.totalExpansionCost_primary = this.totalConstructionCost_primary + this.totalSetUpCost_primary + this.totalPenaltyCost_primary;
		
		
	}
	
	
	/**
	 * 
	 */
	public void calculateRemainingPeriodsToBuildPrimaryFacility(int period) {
		
		int count = 0;
		
		int index = 1;
		
		while (index <= period-1) {
			
			if(this.investmentDecisionPrimaryFacility[index] == 1) {
				count++;
			}
			
			index++;
		}
		
		this.remainingYearsToBuildPrimaryFacility[period] = this.parameter_periodsToBuildPrimaryFacilities - count;
	}
	
	
	/**
	 * 
	 * @param period
	 * @param new_decision
	 */
	public void setInvestmentDecisionPrimaryFacility (int period, int new_decision) {
		
		this.investmentDecisionPrimaryFacility[period] = new_decision;
		
	}
	
	
	/**
	 * 
	 * @param period
	 * @param strategy
	 */
	public void addNewStrategyDecision (int period, int [] strategy) {
		
		this.investmentStrategies[period] = strategy;
		
	}
	
	
	/**
	 * 
	 * @param array
	 * @return
	 */
	private static int[] clone (int[] array) {
		
		int[] copy = new int[array.length];
		
		for (int i = 0; i < array.length; i++) {
			
			copy[i] = array[i];
		}
		
		return copy;	
	}
	
	
	/**
	 * 
	 * @param array
	 * @return
	 */
	private static int[][] clone (int[][] array) {
		
		int[][] copy = new int[array.length][array[0].length];
		
		for (int i = 0; i < array.length; i++) {
			
			for (int j = 0; j < array[i].length; j++) {
				
				copy[i][j] = array[i][j];
			}
		}
		return copy;	
	}
	
	
	/**
	 * 
	 * @param array
	 * @return
	 */
	private static double[] clone (double[] array) {
		
		double[] copy = new double[array.length];
		
		for (int i = 0; i < array.length; i++) {
			
			copy[i] = array[i];
		}
		
		return copy;	
	}
	
	
	/**
	 * 
	 * @param array
	 * @return
	 */
	private static boolean[] clone (boolean[] array) {
		
		boolean[] copy = new boolean[array.length];
		
		for (int i = 0; i < array.length; i++) {
			
			copy[i] = array[i];
		}
		
		return copy;	
	}
	
	
	/**
	 * 
	 * @param array
	 * @return
	 */
	private static boolean[][] clone (boolean[][] array) {
		
		boolean[][] copy = new boolean[array.length][array[0].length];
		
		for (int i = 0; i < array.length; i++) {
			
			for (int j = 0; j < array[i].length; j++) {
				
				copy[i][j] = array[i][j];
			}	
		}
		return copy;	
	}
	
	
	/**
	 * 
	 * @param array
	 * @return
	 */
	private static double[][] clone (double[][] array) {
		
		double[][] copy = new double[array.length][array[0].length];
		
		for (int i = 0; i < array.length; i++) {
			
			for (int j = 0; j < array[i].length; j++) {
				
				copy[i][j] = array[i][j];
			}	
		}
		return copy;	
	}
	
	
	/**
	 * 
	 * @param array
	 * @return
	 */
	private static double[][][] clone (double[][][] array) {
		
		double[][][] copy = new double[array.length][array[0].length][array[0][0].length];
		
		for (int i = 0; i < array.length; i++) {
			
			for (int j = 0; j < array[i].length; j++) {
				
				for (int k = 0; k < array[i][j].length; k++) {
					
					copy[i][j][k] = array[i][j][k];
				}
			}	
		}
		return copy;	
	}
	
	
	/**
	 * 
	 * @param array
	 * @return
	 */
	private static double[][][][] clone (double[][][][] array) {
		
		double[][][][] copy = new double[array.length][array[0].length][array[0][0].length][array[0][0][0].length];
		
		for (int i = 0; i < array.length; i++) {
			
			for (int j = 0; j < array[i].length; j++) {
				
				for (int k = 0; k < array[i][j].length; k++) {
					
					for (int l = 0; l < array[i][j][k].length; l++) {
						
						copy[i][j][k][l] = array[i][j][k][l];
					}
				}
			}	
		}
		return copy;	
	}
	

	/**
	 * 
	 */
	public Data clone () {
		
		Data copy = new Data (); 
		
		copy.parameter_planningHorizon = this.parameter_planningHorizon;		
		
		copy.parameter_discountFactor_timing = this.parameter_discountFactor_timing;					
		copy.parameter_discountFactor_location = this.parameter_discountFactor_location;
		
		copy.parameter_periodsToBuildPrimaryFacilities = 	this.parameter_periodsToBuildPrimaryFacilities;	
		copy.parameter_periodsToBuildSecondaryFacilities = this.parameter_periodsToBuildSecondaryFacilities;	
		
		copy.parameter_constructionCostPrimaryFacility = this.parameter_constructionCostPrimaryFacility;			
		copy.parameter_constructionCostSecondaryFacility = this.parameter_constructionCostSecondaryFacility;
		
		copy.parameter_setupCostPrimaryFacility = this.parameter_setupCostPrimaryFacility;			
		copy.parameter_setupCostSecondaryFacility = this.parameter_setupCostSecondaryFacility;					
		
		copy.parameter_penaltyCost = this.parameter_penaltyCost;							
		
		copy.parameter_preliminaryKnowledgeAboutSuccessfulTests = this.parameter_preliminaryKnowledgeAboutSuccessfulTests;	
		copy.parameter_preliminaryKnowledgeAboutFailedTests = this.parameter_preliminaryKnowledgeAboutFailedTests;		
		copy.parameter_thresholdSuccessfulTests = this.parameter_thresholdSuccessfulTests;				
		
		copy.testResults = Data.clone(this.testResults);										
		copy.countSuccessfulTests = Data.clone(this.countSuccessfulTests);						
		copy.countFailedTests = Data.clone(this.countFailedTests);
		copy.remainingYearsToBuildPrimaryFacility = Data.clone(this.remainingYearsToBuildPrimaryFacility);			
		copy.testProbability = Data.clone(this.testProbability);								
		copy.remainingTimeofClinicalTrials = this.remainingTimeofClinicalTrials;				
		
		copy.investmentDecisionPrimaryFacility = Data.clone(this.investmentDecisionPrimaryFacility);				
		copy.investmentDecisionSecondaryFacility = this.investmentDecisionSecondaryFacility;						
		copy.investmentStrategies = Data.clone(this.investmentStrategies);
		
		copy.countPeriods = this.countPeriods;										
		
		copy.totalConstructionCost_primary = this.totalConstructionCost_primary;
		copy.totalSetUpCost_primary = this.totalSetUpCost_primary;
		copy.totalPenaltyCost_primary = this.totalPenaltyCost_primary;
		copy.totalExpansionCost_primary = this.totalExpansionCost_primary;
		copy.successOfClinicalTrials = this.successOfClinicalTrials;
		
		copy.IF = Data.clone(this.IF);												
		copy.EF = Data.clone(this.EF);												
		copy.OM = Data.clone(this.OM);												
		copy.IM = Data.clone(this.IM);													
		copy.Fn = Data.clone(this.Fn);												
		copy.PIF = Data.clone(this.PIF); 												
		copy.SIF = Data.clone(this.SIF);											

		copy.I = this.I;													
		copy.F = this.F;													
		copy.T = this.T;														
		copy.N = this.N;													
		copy.capitalBudget = Data.clone(this.capitalBudget);									
		copy.costInsuranceFreight = Data.clone(this.costInsuranceFreight);							
		copy.demand = Data.clone(this.demand);							
		copy.importDuty = Data.clone(this.importDuty);									
		copy.projectLife = this.projectLife;												
		copy.variableProductionCosts = Data.clone(this.variableProductionCosts);						
		copy.unitSellingPrice = Data.clone(this.unitSellingPrice);						
		copy.lowerLimitExpansionSize = Data.clone(this.lowerLimitExpansionSize);						
		copy.upperLimitCapacity = Data.clone(this.upperLimitCapacity);							
		copy.supply = Data.clone(this.supply);									
		copy.corporateTax = Data.clone(this.corporateTax);									
		copy.API = this.API;
		copy.materialCoefficient = Data.clone(this.materialCoefficient);								
		copy.initialCapacity = this.initialCapacity;										
		copy.monthsToBuildPrimaryFacilities_location = this.monthsToBuildPrimaryFacilities_location;				
		copy.monthsToBuildSecondaryFacilities_location = this.monthsToBuildSecondaryFacilities_location;				
		copy.constructionCostPrimaryFacility_location = this.constructionCostPrimaryFacility_location;	
		copy.constructionCostSecondaryFacility_location = this.constructionCostSecondaryFacility_location;
		
		copy.demandM = Data.clone(this.demandM);										
		copy.demandR = Data.clone(this.demandR);										
		copy.timeM = this.timeM;
		copy.timeR = this.timeR;
		
		copy.result_shippedMaterialUnitsFacilityToCustomer = Data.clone(this.result_shippedMaterialUnitsFacilityToCustomer);
		copy.result_shippedMaterialUnitsSupplierToFacility = Data.clone(this.result_shippedMaterialUnitsSupplierToFacility);
		//copy.result_depreciationChargePrimaryFacility = Data.clone(this.result_depreciationChargePrimaryFacility);
		//copy.result_depreciationChargeSecondaryFacility = Data.clone(this.result_depreciationChargeSecondaryFacility);
		copy.result_availableProductionCapacity = Data.clone(this.result_availableProductionCapacity);				
		copy.result_taxableIncome = Data.clone(this.result_taxableIncome);				
		//copy.result_consumedOrProducedMaterial = Data.clone(this.result_consumedOrProducedMaterial);			
		copy.result_consumedOrProducedAPI = Data.clone(this.result_consumedOrProducedAPI); 						
		copy.result_capitalExpenditure = Data.clone(this.result_capitalExpenditure);					
		copy.result_grossIncome = Data.clone(this.result_grossIncome);							
		copy.result_deltaCapacityExpansion = Data.clone(this.result_deltaCapacityExpansion);				
		copy.result_capacityExpansionAmount = Data.clone(this.result_capacityExpansionAmount);			

		copy.result_constructionStartPrimaryFacility = Data.clone(this.result_constructionStartPrimaryFacility);		
		copy.result_constructionStartSecondaryFacility = Data.clone(this.result_constructionStartSecondaryFacility);			

		copy.result_netPresentValue = this.result_netPresentValue;

		return copy;
	}
	
	
	
	
}



















