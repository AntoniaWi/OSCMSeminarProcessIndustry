package dataManagement;

import helper.Event;
import ilog.concert.IloIntVar;
import ilog.concert.IloNumVar;
import jxl.read.biff.BiffException;
import jxl.write.WriteException;

import java.io.IOException;
import java.util.*;

public class Data {
	
	// Timing Model
	
	private int parameter_planningHorizon;  							// T
	
	private double parameter_discountFactor;							// alpha
	
	private int parameter_monthsToBuildPrimaryFacilities;				// s_p_0 - in whole years
	private int parameter_monthsToBuildSecondaryFacilities;			// s_s_0 - in fraction of years - if construction takes place, it starts in T+1
	
	private double parameter_constructionCostPrimaryFacility;			// c_p
	private double parameter_constructionCostSecondaryFacility;			// c_s
	
	private double parameter_setupCostPrimaryFacility;					// K_p
	private double parameter_setupCostSecondaryFacility;					// K_s
	
	private int parameter_penaltyCost;								// Phi(s_t) = Phi*s_t
	
	private int parameter_preliminaryKnowledgeAboutSuccessfulTests;	// Gamma_0
	private int parameter_preliminaryKnowledgeAboutFailedTests;		// Zeta_0
	private int parameter_thresholdSuccessfulTests;					// Gamma_c
	
	private int [] testResults;										// Delta_t
	private int [] countSuccessfulTests;								// Gamma_t
	private int [] countFailedTests;									// Zeta_t
	private int [] remainingYearsToBuildPrimaryFacility;				// s_p_t
	private double [] testProbability;								// p
	private int remainingTimeofClinicalTrials;						//delta_t
	
	private int [] investmentDecisionPrimaryFacility;					// a_p_t
	private int investmentDecisionSecondaryFacility;					// a_s_T+1 - secondary facility is built in period T+1 if clinical trails are successful
	private int[][] investmentStrategies;
	
	
	
	private int countPeriods;										// t
	
								
	//Location Planning Model

	// Sets
		private boolean[] IF; // IF[f] internal facilities
		private boolean[] EF; // EF[f] external facilities
		private boolean[][] OM; // OM[f][i] outgoing material
		private boolean[][] IM; // IM[f][i]incoming material
		private boolean[][] Fn; // Fn[f][n] nations
		private boolean[] PIF; // PIF[f]
		private boolean[] SIF; // SIF[f]

	// Parameter
		private int I; // number of material types
		private int F; // number of all facilities
		private int T; // number of months in planning horizon
		private int N; // number of nations
		private double[] capitalBudget;// capitalBudget[t] CB_t
		private double[][][] costInsuranceFreight; // costInsuranceFreight[i][s][f] CIF_isf
		private double[][][] demand;// demand[i][c][t] D_ict
		private double[][] importDuty; // importDuty[s][f] ID_isf
		private int projectLife;// projectLife[t] L_f
		private double[] variableProductionCosts;// MC_f
		private double[][] unitSellingPrice;// unitSellingPrice[i][f] P_if
		private double[] lowerLimitExpansionSize;// lowerLimitExpansionSize[f] g_L_f
		private double[] upperLimitCapacity;// upperLimitCapacity[f] Q_U_f
		private double[][] supply;// supply[i][s] S_is
		private double[] corporateTax;// corporateTax[n]TR_n
		private double[] lowerLimitProductionAPI;// lowerLimitProductionAPI[f] X_L_f
		private int API; //
		private double[][] materialCoefficient; // materialCoeeficient[i][f] sigma_if
		private int initialCapacity; // Q0
    
		//Help parameter for demand
		private double [] demandM; //demandM[f]
		private double [] demandR; //demand[f]
		private int timeM;
		private int timeR;
		
		//Result Arrays
		private double[][][][] result_shippedMaterialUnitsFacilityToCustomer; // F_ifct
		private double[][][][] result_shippedMaterialUnitsSupplierToFacility; // F_isft
		private double[][][] result_depreciationChargePrimaryFacility; // NDC_p_ftaut
		private double[][][] result_depreciationChargeSecondaryFacility; // NDC_s_ftaut
		private double[][] result_availableProductionCapacity; // Q_ft
		private double[][] result_taxableIncome; // TI_nt
		private double[][][] result_consumedOrProducedMaterial; // x_ift
		private double[][] result_consumedOrProducedAPI; // X_ft
		private double[] result_capitalExpenditure; // CE_t
		private double[][] result_grossIncome; // GI_ft
		private double[][] result_deltaCapacityExpansion; // delta_q_ft
		private double[][] result_capacityExpansionAmount; // q_ft

		private double[][] result_constructionStartPrimaryFacility; // y_ft
		private double[][] result_constructionStartSecondaryFacility; // z_ft

	/**
	 * 
	 */
	public Data () {

		this.parameter_planningHorizon = 3;
		
		this.parameter_discountFactor = 0.9;
		
		this.parameter_monthsToBuildPrimaryFacilities = 3;
		this.parameter_monthsToBuildSecondaryFacilities = 3;
		
		this.parameter_constructionCostPrimaryFacility = 1000;
		this.parameter_constructionCostSecondaryFacility = 25;
		
		this.parameter_setupCostPrimaryFacility = 100;
		this.parameter_setupCostSecondaryFacility = 2;
		
		this.parameter_penaltyCost = 10000;
		
		this.parameter_preliminaryKnowledgeAboutSuccessfulTests = 1;
		this.parameter_preliminaryKnowledgeAboutFailedTests = 1;
		this.parameter_thresholdSuccessfulTests = 2;
		
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
		this.remainingYearsToBuildPrimaryFacility[0] = this.parameter_monthsToBuildPrimaryFacilities;
		
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
		
				
	}
	
	
	/**
	 * 
	 * @param x
	 * @throws BiffException
	 * @throws IOException
	 * @throws WriteException
	 */
	public Data (int x) throws BiffException, IOException, WriteException {
		ReadAndWrite.readConst(this);
		ReadAndWrite.readDataTiming(this);
		ReadAndWrite.readF(this);
		ReadAndWrite.readFinN(this);
		ReadAndWrite.readIMf(this);
		ReadAndWrite.readOMf(this);
		ReadAndWrite.readTRn(this);
		ReadAndWrite.readMassbalance(this);
		ReadAndWrite.readDataF(this);
		ReadAndWrite.readSis(this);
		ReadAndWrite.readPif(this);
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
		this.remainingYearsToBuildPrimaryFacility[0] = this.parameter_monthsToBuildPrimaryFacilities;
		
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
		
		
		
		// Nach TimingModel aufrufen
		
		ReadAndWrite.createAndWriteDict(this);
		
	
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
	 * @return the parameter_monthsToBuildPrimaryFacilities
	 */
	public int getParameter_monthsToBuildPrimaryFacilities() {
		return parameter_monthsToBuildPrimaryFacilities;
	}


	/**
	 * @param parameter_monthsToBuildPrimaryFacilities the parameter_yearsToBuildPrimaryFacilities to set
	 */
	public void setParameter_monthsToBuildPrimaryFacilities(int parameter_monthsToBuildPrimaryFacilities) {
		this.parameter_monthsToBuildPrimaryFacilities = parameter_monthsToBuildPrimaryFacilities;
	}


	/**
	 * @return the parameter_monthsToBuildSecondaryFacilities
	 */
	public int getParameter_monthsToBuildSecondaryFacilities() {
		return parameter_monthsToBuildSecondaryFacilities;
	}


	/**
	 * @param parameter_monthsToBuildSecondaryFacilities the parameter_yearsToBuildSecondaryFacilities to set
	 */
	public void setParameter_monthsToBuildSecondaryFacilities(int parameter_monthsToBuildSecondaryFacilities) {
		this.parameter_monthsToBuildSecondaryFacilities = parameter_monthsToBuildSecondaryFacilities;
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
	public double[][] getUnitSellingPrice() {
		return unitSellingPrice;
	}


	/**
	 * @param unitSellingPrice the unitSellingPrice to set
	 */
	public void setUnitSellingPrice(double[][] unitSellingPrice) {
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
	 * @return the result_depreciationChargePrimaryFacility
	 */
	public double[][][] getResult_depreciationChargePrimaryFacility() {
		return result_depreciationChargePrimaryFacility;
	}

	/**
	 * @param result_depreciationChargePrimaryFacility the result_depreciationChargePrimaryFacility to set
	 */
	public void setResult_depreciationChargePrimaryFacility(double[][][] result_depreciationChargePrimaryFacility) {
		this.result_depreciationChargePrimaryFacility = result_depreciationChargePrimaryFacility;
	}

	/**
	 * @return the result_depreciationChargeSecondaryFacility
	 */
	public double[][][] getResult_depreciationChargeSecondaryFacility() {
		return result_depreciationChargeSecondaryFacility;
	}

	/**
	 * @param result_depreciationChargeSecondaryFacility the result_depreciationChargeSecondaryFacility to set
	 */
	public void setResult_depreciationChargeSecondaryFacility(double[][][] result_depreciationChargeSecondaryFacility) {
		this.result_depreciationChargeSecondaryFacility = result_depreciationChargeSecondaryFacility;
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
	 * @return the result_consumedOrProducedMaterial
	 */
	public double[][][] getResult_consumedOrProducedMaterial() {
		return result_consumedOrProducedMaterial;
	}

	/**
	 * @param result_consumedOrProducedMaterial the result_consumedOrProducedMaterial to set
	 */
	public void setResult_consumedOrProducedMaterial(double[][][] result_consumedOrProducedMaterial) {
		this.result_consumedOrProducedMaterial = result_consumedOrProducedMaterial;
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
	
	// TODO RAMONA: create several toString Method for console output
	

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
		
		this.remainingYearsToBuildPrimaryFacility[period] = this.parameter_monthsToBuildPrimaryFacilities - count;
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
	
		
	
}



















