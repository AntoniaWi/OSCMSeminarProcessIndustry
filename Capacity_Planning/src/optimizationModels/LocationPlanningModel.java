package optimizationModels;

import ilog.concert.*;
import ilog.cplex.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import dataManagement.Data;
import jxl.read.biff.BiffException;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class LocationPlanningModel extends IloCplex {
	// Indices
	private int i; // number of material types
	private int f; // number of internal facilities
	private int g; // number of external facilities
	private int s; // number of suppliers //TODO: stimmt das?
	private int c; // number of customers //TODO: stimmt das?
	private int t; // number of fiscal years in planning horizon
	private int tau; //// TODO:??
	private int n; // number of nations

	// Sets

	// Daten
	private double[] capitalBudget;// capitalBudget[t] CB_t
	private double[] capitalExpenditure; // capitalExpenditure[t] CE_t
	private double[][][][] costInsuranceFreight; // costInsuranceFreight[i][s][f][t] CIF_isft
	private double[][][] demand;// demand[i][c][t] D_ict
	private double[][][][] importDuty[][][][]; // importDuty[i][s][f][t] ID_isft
	private double[] projectLife;// projectLife[t] L_f
	private double variableProductionCostsPrimaryFacility;// MC_p_f
	private double variableProductionCostsSecondaryFacility;// MC_s_f
	private double[][][][] unitSellingPrice;// unitSellingPrice[i][f][g][t] P_ifgt
	private double[] lowerLimitExpansionSize;// lowerLimitExpansionSize[f] g_L_f
	private double[] initialCapacity;// initialCapacity[f] at time zero Q_f0
	private double[] upperLimitCapacity;// upperLimitCapacity[f] Q_U_f
	private double[][][] supply;// supply[i][s][t] S_ist
	private double[][] corporateTax;// corporateTax[n][t] TR_nt
	private double[] lowerLimitProductionAPI;// lowerLimitProductionAPI[f] X_L_f
	private int API; // TODO: one material from set i pi
	private double[][] materialCoefficient; // materialCoeeficient[i][f] sigma_if
	private double[][] capacityExpansionAmount;// capacityExpansionAmount[f][t] q_ft
	private double discountfactor;
	private double capacityDemand;

	// Transfer parameter
	private int remainingTimeOfClinicalTrials;// T*-t* delta_t*

	/* Objective */
	private IloObjective objective;

	/* Decision variables */
	private IloNumVar[][][][] shippedMaterialUnitsFacilityToCustomer;
	private IloNumVar[][][][] shippedMaterialUnitsSupplierToFacility;
	private IloNumVar[][][] depreciationChargePrimaryFacility;
	private IloNumVar[][][] depreciationChargeSecondaryFacility;
	private IloNumVar[][] availableProductionCapacity;
	private IloNumVar[][] taxableIncome;
	private IloNumVar[][][] consumedOrProducedMaterial;
	private IloNumVar[][] consumedOrProducedAPI;

	private IloIntVar[][] constructionStartPrimaryFacility;
	private IloIntVar[][] constructionStartSecondaryFacility;

	/* Constraints */
	private IloLinearNumExpr numberOfPrimaryFacilities = linearNumExpr();
	private IloLinearNumExpr numberOfSecondaryFacilities = linearNumExpr();
	private IloLinearNumExpr limitationOfConstructionStartsPrimaryFacilities = linearNumExpr();
	private IloLinearNumExpr limitationOfConstructionStartsSecondaryFacilities = linearNumExpr();
	private IloLinearNumExpr noDoubleOccupationOfFacilities = linearNumExpr();
	private IloLinearNumExpr capacityExpansionOnlyIfPlanned = linearNumExpr();
	private IloLinearNumExpr minimumExpansion = linearNumExpr();
	private IloLinearNumExpr expansionSize = linearNumExpr();
	private IloLinearNumExpr availableCapacity = linearNumExpr();
	private IloLinearNumExpr massbalanceEquation1 = linearNumExpr();
	private IloLinearNumExpr massbalanceEquation2 = linearNumExpr();
	private IloLinearNumExpr massbalanceEquation3 = linearNumExpr();
	private IloLinearNumExpr capacityRestrictionForProduction = linearNumExpr();
	private IloLinearNumExpr lowerLimitForProduction = linearNumExpr();
	private IloLinearNumExpr demandAndSupply = linearNumExpr();
	private IloLinearNumExpr capitalExpenditureConstraint = linearNumExpr();
	private IloLinearNumExpr budget = linearNumExpr();
	private IloLinearNumExpr grossIncome = linearNumExpr();
	private IloLinearNumExpr depreciationChargePrimaryFacilities = linearNumExpr();
	private IloLinearNumExpr depreciationChargeSecondaryFacilities = linearNumExpr();
	private IloLinearNumExpr taxableIncomeConstraint = linearNumExpr();

	public LocationPlanningModel(Data datainstanz) throws IloException {
		// Indices
		i = datainstanz.getI();
		f = datainstanz.getF();
		g = datainstanz.getG();
		s = datainstanz.getS();
		c = datainstanz.getC();
		t = datainstanz.getT();
		tau = datainstanz.getTau();
		n = datainstanz.getN();

		// Sets

		// Daten
		capitalBudget = datainstanz.getCapitalBudget();
		capitalExpenditure = datainstanz.getCapitalExpenditure();
		costInsuranceFreight = datainstanz.getCostInsuranceFreight();
		demand = datainstanz.getDemand();
		importDuty = datainstanz.getImportDuty();
		projectLife = datainstanz.getProjectLife();
		variableProductionCostsPrimaryFacility = datainstanz.getVariableProductionCostsPrimaryFacility();
		variableProductionCostsSecondaryFacility = datainstanz.getVariableProductionCostsSecondaryFacility();
		unitSellingPrice = datainstanz.getUnitSellingPrice();
		lowerLimitExpansionSize = datainstanz.getLowerLimitExpansionSize();
		initialCapacity = datainstanz.getInitialCapacity();
		upperLimitCapacity = datainstanz.getUpperLimitCapacity();
		supply = datainstanz.getSupply();
		corporateTax = datainstanz.getCorporateTax();
		lowerLimitProductionAPI = datainstanz.getLowerLimitProductionAPI();
		API = datainstanz.getAPI();
		materialCoefficient = datainstanz.getMaterialCoefficient();
		capacityExpansionAmount = datainstanz.getCapacityExpansionAmount();
		discountfactor = datainstanz.getParameter_discountFactor();
		capacityDemand=datainstanz.getParameter_capacityDemand();
		
		// Transfer parameter
		remainingTimeOfClinicalTrials = datainstanz.getRemainingTimeOfClinicalTrials();

	}

	public static void main(String[] args)
			throws IloException, BiffException, IOException, RowsExceededException, WriteException {

		// boolean robust1=true;
		Data instanz = new Data();

		// instanz.DatenEinlesen(instanz,robust1);

		LocationPlanningModel lpm = new LocationPlanningModel(instanz);

		lpm.build();
		lpm.solve();
		lpm.writeSolution(new int[] { 1, 2, 3 }, false);
		// lpm.ergebnisschreibenRobust(lpm);
	}

	public void build() throws IloException {
		long start = System.currentTimeMillis();

		/* Variables */
		addVarsX();

		/* Objective */
		addObjective();

		/* Constraints */
		//1st constraint
		this.addConstraintNumberOfPrimaryFacilities();
		this.addConstraintNumberOfSecondaryFacilities();
		//2nd constraint
		this.addConstraintOneConstructionDuringPlanningHorizonPF();
		this.addConstraintOneConstructionDuringPlanningHorizonSF();
		//3rd constraint
		this.addConstraintNoDoubleOccupationOfFacilities();
		//4th constraint
		this.addConstraintCapacityExpansionOnlyIfConstructionIsPlanned();
		//5th constraint
		this.addConstraintMinimumExpansion();
		//6th constraint
		this.addConstraintExpansionSize();
		//TODO:nächste Nebenbedingungen
		
		String path = "./logs/model.lp";
		exportModel(path);

		System.out.println(
				"(WGP) complete rebuild finished, dur=" + (System.currentTimeMillis() - start) + " milli sec\n");
	}

	// add Decision variables
	private void addVarsX() throws IloException {
		for (int i = 0; i < f; i++) {
			for (int j = 0; j < t; j++) {
				constructionStartPrimaryFacility[i][j] = intVar(0, 1);
				constructionStartSecondaryFacility[i][j] = intVar(0, 1);
			}
		}

	}

	private void addObjective() throws IloException {

		IloLinearNumExpr expr = linearNumExpr();
		IloLinearNumExpr obj = linearNumExpr();

		for (int i = 0; i < this.f; i++) {

			for (int j = 0; j < this.t; j++) {

				// expr.addTerm(1/Math.pow(1+this.discountfactor,j) );

			}

		}

		objective = addMaximize();

		objective.setExpr(expr);
		System.out.println(objective);

	}

	private void addConstraintNumberOfPrimaryFacilities() throws IloException {

		this.numberOfPrimaryFacilities.clear();
		for (int i = 0; i < this.f; i++) {
			this.numberOfPrimaryFacilities.addTerm(1, this.constructionStartPrimaryFacility[i][1]);
		}
		addEq(this.numberOfPrimaryFacilities, 1);

	}

	private void addConstraintNumberOfSecondaryFacilities() throws IloException {

		this.numberOfSecondaryFacilities.clear();

		for (int i = 0; i < this.f; i++) {
			this.numberOfSecondaryFacilities.addTerm(1,
					this.constructionStartSecondaryFacility[i][1 + this.remainingTimeOfClinicalTrials]);
		}
		addEq(this.numberOfSecondaryFacilities, 1);

	}

	private void addConstraintOneConstructionDuringPlanningHorizonPF() throws IloException {

		this.limitationOfConstructionStartsPrimaryFacilities.clear();

		for (int i = 0; i < this.f; i++) {
			for (int j = 0; j < this.t; j++) {
				this.limitationOfConstructionStartsPrimaryFacilities.addTerm(1,
						this.constructionStartPrimaryFacility[i][j]);
			}
		}
		addLe(this.limitationOfConstructionStartsPrimaryFacilities, 1);

	}
	
	private void addConstraintOneConstructionDuringPlanningHorizonSF() throws IloException {

		this.limitationOfConstructionStartsSecondaryFacilities.clear();

		for (int i = 0; i < this.f; i++) {
			for (int j = 0; j < this.t; j++) {
				this.limitationOfConstructionStartsSecondaryFacilities.addTerm(1,
						this.constructionStartSecondaryFacility[i][j]);
			}
		}
		addLe(this.limitationOfConstructionStartsSecondaryFacilities, 1);

	}
	
	private void addConstraintNoDoubleOccupationOfFacilities() throws IloException {

		this.noDoubleOccupationOfFacilities.clear();

		for (int i = 0; i < this.f; i++) {
			for (int j = 0; j < this.t; j++) {
				
				this.noDoubleOccupationOfFacilities.addTerm(1, this.constructionStartPrimaryFacility[i][j]);
				this.noDoubleOccupationOfFacilities.addTerm(1, this.constructionStartSecondaryFacility[i][j]);
				
						
			}
			addLe(this.noDoubleOccupationOfFacilities, 1);
		}
		

	}
	private void addConstraintCapacityExpansionOnlyIfConstructionIsPlanned() throws IloException {

		this.capacityExpansionOnlyIfPlanned.clear();

		for (int i = 0; i < this.f; i++) {
			for (int j = 0; j < this.t; j++) {
				double freecapacity=this.upperLimitCapacity[i]- this.initialCapacity[i];
				this.capacityExpansionOnlyIfPlanned.addTerm(freecapacity, this.constructionStartPrimaryFacility[i][j]);
				this.capacityExpansionOnlyIfPlanned.addTerm(freecapacity, this.constructionStartSecondaryFacility[i][j]);
				
				addGe(this.capacityExpansionOnlyIfPlanned, this.capacityExpansionAmount[i][j]);		
			}
			
		}
		

	}
	
	private void addConstraintMinimumExpansion() throws IloException {

		this.minimumExpansion.clear();

		for (int i = 0; i < this.f; i++) {
			for (int j = 0; j < this.t; j++) {
				this.minimumExpansion.addTerm(this.lowerLimitExpansionSize[i], this.constructionStartPrimaryFacility[i][j]);
				this.minimumExpansion.addTerm(this.lowerLimitExpansionSize[i], this.constructionStartSecondaryFacility[i][j]);
				
				addLe(this.minimumExpansion, this.capacityExpansionAmount[i][j]);		
			}
			
		}
		

	}
	private void addConstraintExpansionSize() throws IloException {

		this.expansionSize.clear();

		for (int i = 0; i < this.f; i++) {
			for (int j = 0; j < this.t; j++) {
				this.expansionSize.addTerm(this.capacityDemand, this.constructionStartPrimaryFacility[i][j]);
				this.expansionSize.addTerm(this.capacityDemand, this.constructionStartSecondaryFacility[i][j]);
				
				addEq(this.expansionSize, this.capacityExpansionAmount[i][j]);		
			}
			
		}
		

	}
	
	//TODO:nächste Nebenbedingungen
	public void writeMatrix(int[] numbers) throws IloException {
		String path = "./logs/model.lp";

		exportModel(path);

	}

	@Override

	public boolean solve() throws IloException {
		return solve(new int[0]);
	}

	public boolean solve(int[] numbers) throws IloException {
		writeMatrix(numbers);

		if (!super.solve()) {
			return false;
		}
		// writeSolution(numbers, false);
		return true;
	}

	public void writeSolution(int[] numbers, boolean includingZeros) throws IloException, IOException {

		String path = "./logs/_WGP_";

		// for (int k : numbers)
		// path += k + "_";
		path += "sol.txt";
		File logFile = new File(path);
		logFile.delete();
		logFile.createNewFile();
		FileWriter fstream = new FileWriter(logFile, true);
		BufferedWriter out = new BufferedWriter(fstream);
		/*
		 * out.write("objective value=" + getObjValue() + "\n");
		 * out.write("variable values\n");
		 * 
		 * out.write("\n Entscheidung\n"); for( int j=0; j<v;j++){ for(int k =0;
		 * k<l;k++){
		 * 
		 * 
		 * if (getValue(x[j][k])==1){ out.write(" Komponente "+(j+1)+ " : Lieferant " +
		 * (k+1) + " x = " +getValue(x[j][k])+"\n"); } } } out.close();
		 * System.out.println("(WGP) wrote sol to file " + path + "\n");
		 */
	}

}