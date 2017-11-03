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
	private int f; // number of all facilities
	private int fi;// number of internal facilities
	private int g; // number of external facilities
	private int s; // number of suppliers //TODO: stimmt das?
	private int c; // number of customers //TODO: stimmt das?
	private int t; // number of fiscal years in planning horizon
	private int tau; //// TODO:??
	private int n; // number of nations

	// Sets
	private int[] F; // F[f]: all(internal and external) facilities
	private boolean[] IF; // IF[f] internal facilities
	private boolean[] EF; // EF[f] external facilities
	private boolean[] OM;// OM[f] outgoing material
	private boolean[] IM;// IM[f] incoming material

	// Daten
	private double[] capitalBudget;// capitalBudget[t] CB_t
	private double[][][][] costInsuranceFreight; // costInsuranceFreight[i][s][f][t] CIF_isft
	private double[][][] demand;// demand[i][c][t] D_ict
	private double[][][][] importDuty[][][][]; // importDuty[i][s][f][t] ID_isft
	private double[] projectLife;// projectLife[t] L_f
	private double variableProductionCostsPrimaryFacility;// MC_p_f
	private double variableProductionCostsSecondaryFacility;// MC_s_f
	private double[][][][] unitSellingPrice;// unitSellingPrice[i][f][g][t] P_ifgt
	private double[] lowerLimitExpansionSize;// lowerLimitExpansionSize[f] g_L_f
	// private double[] initialCapacity;// initialCapacity[f] at time zero Q_f0
	private double[] upperLimitCapacity;// upperLimitCapacity[f] Q_U_f
	private double[][] supply;// supply[i][s] S_ist
	private double[][] corporateTax;// corporateTax[n][t] TR_nt
	private double[] lowerLimitProductionAPI;// lowerLimitProductionAPI[f] X_L_f
	private int API; // TODO: one material from set i pi
	private double[][] materialCoefficient; // materialCoeeficient[i][f] sigma_if
	private double[][] capacityExpansionAmount;// capacityExpansionAmount[f][t] q_ft
	private int planninghorizonLPM; // T

	// Transfer parameter
	private int remainingTimeOfClinicalTrials;// T*-t* delta_t*
	private double discountfactor;
	private double capacityDemand;
	private int yearsToBuildPrimaryFacility;
	private double yearsToBuildSecondaryFacility;
	private double setupCostPrimaryFacility;
	private double setupCostSecondaryFacility;
	private double constructionCostPrimaryFacility;
	private double constructionCostSecondaryFacility;

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
	private IloNumVar[] capitalExpenditure;

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
	private IloLinearNumExpr lowerLimitForProductionPF = linearNumExpr();
	private IloLinearNumExpr lowerLimitForProductionSF = linearNumExpr();
	private IloLinearNumExpr lowerLimitForProductionSUM = linearNumExpr();
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
		this.capitalBudget = datainstanz.getCapitalBudget();
		this.costInsuranceFreight = datainstanz.getCostInsuranceFreight();
		this.demand = datainstanz.getDemand();
		this.importDuty = datainstanz.getImportDuty();
		this.projectLife = datainstanz.getProjectLife();
		this.variableProductionCostsPrimaryFacility = datainstanz.getVariableProductionCostsPrimaryFacility();
		this.variableProductionCostsSecondaryFacility = datainstanz.getVariableProductionCostsSecondaryFacility();
		this.unitSellingPrice = datainstanz.getUnitSellingPrice();
		this.lowerLimitExpansionSize = datainstanz.getLowerLimitExpansionSize();
		// this.initialCapacity = datainstanz.getInitialCapacity();
		this.upperLimitCapacity = datainstanz.getUpperLimitCapacity();
		this.supply = datainstanz.getSupply();
		this.corporateTax = datainstanz.getCorporateTax();
		this.lowerLimitProductionAPI = datainstanz.getLowerLimitProductionAPI();
		this.API = datainstanz.getAPI();
		this.materialCoefficient = datainstanz.getMaterialCoefficient();
		this.capacityExpansionAmount = datainstanz.getCapacityExpansionAmount();
		this.planninghorizonLPM = datainstanz.getPlanninghorizonLPM();

		// Transfer parameter
		this.remainingTimeOfClinicalTrials = datainstanz.getRemainingTimeOfClinicalTrials();
		this.discountfactor = datainstanz.getParameter_discountFactor();
		this.capacityDemand = datainstanz.getParameter_capacityDemand();
		this.yearsToBuildPrimaryFacility = datainstanz.getParameter_yearsToBuildPrimaryFacilities();
		this.yearsToBuildSecondaryFacility = datainstanz.getParameter_yearsToBuildSecondaryFacilities();
		this.setupCostPrimaryFacility = datainstanz.getParameter_setupCostPrimaryFacility();
		this.setupCostSecondaryFacility = datainstanz.getParameter_setupCostSecondaryFacility();
		this.constructionCostPrimaryFacility = datainstanz.getParameter_constructionCostPrimaryFacility();
		this.constructionCostSecondaryFacility = datainstanz.getParameter_constructionCostSecondaryFacility();

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
		// 1st constraint
		this.addConstraintNumberOfPrimaryFacilities();
		this.addConstraintNumberOfSecondaryFacilities();
		// 2nd constraint
		this.addConstraintOneConstructionDuringPlanningHorizonPF();
		this.addConstraintOneConstructionDuringPlanningHorizonSF();
		// 3rd constraint
		this.addConstraintNoDoubleOccupationOfFacilities();
		// 4th constraint
		this.addConstraintCapacityExpansionOnlyIfConstructionIsPlanned();
		// 5th constraint
		this.addConstraintMinimumExpansion();
		// 6th constraint
		// this.addConstraintExpansionSize();
		// 7th constraint
		this.addConstraintAvailableCapacity();
		// 8th constraint
		this.addConstraintsMassBalanceEquation();
		// 9th constraint
		this.addConstraintCapacityRestrictionForProduction();
		// 10th constraint
		this.addConstraintLowerLimitOfProduction();
		// 11th constraint
		this.addConstraintSupplyAndDemand();
		// 12th constraint
		this.addConstraintCapitalExpenditure();
		// 13th constraint
		this.addConstraintBudgetConstraint();
		//14th constraint
		this.addConstraintGrossIncome();
		// 15th constraint
		this.addConstraintDepreciationCharge();
		// 16th constraint
		this.addConstraintTaxableIncome();
		// TODO:nächste Nebenbedingungen

		String path = "./logs/model.lp";
		exportModel(path);

		System.out.println(
				"(WGP) complete rebuild finished, dur=" + (System.currentTimeMillis() - start) + " milli sec\n");
	}

	// add Decision variables
	private void addVarsX() throws IloException {
		for (int i = 0; i < f; i++) {
			for (int j = 0; j < t; j++) {
				if (IF[i]) {
					constructionStartPrimaryFacility[i][j] = intVar(0, 1);
					constructionStartSecondaryFacility[i][j] = intVar(0, 1);
				} 
			}
		}

	}
	//add Objective 
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

	// Nebenbedingungen

	/**
	 * 1st constraint: choose exactly one facility as primary facility/ Choose at
	 * least one facility as secondary facility
	 * 
	 * @throws IloException
	 */
	private void addConstraintNumberOfPrimaryFacilities() throws IloException {

		this.numberOfPrimaryFacilities.clear();
		for (int i = 0; i < this.f; i++) {
			if (IF[i]) {
				this.numberOfPrimaryFacilities.addTerm(1, this.constructionStartPrimaryFacility[i][1]);
			}
		}
		addEq(this.numberOfPrimaryFacilities, 1);

	}

	private void addConstraintNumberOfSecondaryFacilities() throws IloException {

		this.numberOfSecondaryFacilities.clear();

		for (int i = 0; i < this.f; i++) {
			if (IF[i]) {
				this.numberOfSecondaryFacilities.addTerm(1,
						this.constructionStartSecondaryFacility[i][1 + this.remainingTimeOfClinicalTrials]);
			}
		}
		addGe(this.numberOfSecondaryFacilities, 1);

	}

	/**
	 * 2nd constraint: start exactly one construction during the planning horizon
	 * for primary facilities/ start production for secondary facilities not more
	 * than once during the planning horizon
	 * 
	 * @throws IloException
	 */
	private void addConstraintOneConstructionDuringPlanningHorizonPF() throws IloException {

		this.limitationOfConstructionStartsPrimaryFacilities.clear();

		for (int i = 0; i < this.f; i++) {
			if (IF[i]) {
				for (int j = 0; j < this.t; j++) {

					this.limitationOfConstructionStartsPrimaryFacilities.addTerm(1,
							this.constructionStartPrimaryFacility[i][j]);
				}
			}
		}
		addLe(this.limitationOfConstructionStartsPrimaryFacilities, 1);

	}

	private void addConstraintOneConstructionDuringPlanningHorizonSF() throws IloException {

		this.limitationOfConstructionStartsSecondaryFacilities.clear();

		for (int i = 0; i < this.f; i++) {
			if (IF[i]) {
				for (int j = 0; j < this.t; j++) {

					this.limitationOfConstructionStartsSecondaryFacilities.addTerm(1,
							this.constructionStartSecondaryFacility[i][j]);
				}
				addLe(this.limitationOfConstructionStartsSecondaryFacilities, 1);
			}
			
		}

	}

	/**
	 * 3rd constraint: one facility can be either a primary or a secondary facility
	 * but not both at the same time
	 * 
	 * @throws IloException
	 */
	private void addConstraintNoDoubleOccupationOfFacilities() throws IloException {

		this.noDoubleOccupationOfFacilities.clear();

		for (int i = 0; i < this.f; i++) {
			if (IF[i]) {
				for (int j = 0; j < this.t; j++) {

					this.noDoubleOccupationOfFacilities.addTerm(1, this.constructionStartPrimaryFacility[i][j]);
					this.noDoubleOccupationOfFacilities.addTerm(1, this.constructionStartSecondaryFacility[i][j]);
				}

				addLe(this.noDoubleOccupationOfFacilities, 1);
			}
		}

	}

	/**
	 * 4th constraint: capacity expansion only if expansion is planned
	 * 
	 * @throws IloException
	 */
	private void addConstraintCapacityExpansionOnlyIfConstructionIsPlanned() throws IloException {

		this.capacityExpansionOnlyIfPlanned.clear();

		for (int i = 0; i < this.f; i++) {
			if (IF[i]) {
				for (int j = 0; j < this.t; j++) {
					double freecapacity = this.upperLimitCapacity[i];

					this.capacityExpansionOnlyIfPlanned.addTerm(freecapacity,
							this.constructionStartPrimaryFacility[i][j]);
					this.capacityExpansionOnlyIfPlanned.addTerm(freecapacity,
							this.constructionStartSecondaryFacility[i][j]);

					addGe(this.capacityExpansionOnlyIfPlanned, this.capacityExpansionAmount[i][j]);
				}

			}
		}

	}

	/**
	 * 5th constraint: minimum expansion size
	 * 
	 * @throws IloException
	 */
	private void addConstraintMinimumExpansion() throws IloException {

		this.minimumExpansion.clear();

		for (int i = 0; i < this.f; i++) {
			if (IF[i]) {
				for (int j = 0; j < this.t; j++) {
					this.minimumExpansion.addTerm(this.lowerLimitExpansionSize[i],
							this.constructionStartPrimaryFacility[i][j]);
					this.minimumExpansion.addTerm(this.lowerLimitExpansionSize[i],
							this.constructionStartSecondaryFacility[i][j]);

					addLe(this.minimumExpansion, this.capacityExpansionAmount[i][j]);
				}
			}
		}

	}

	/**
	 * 6th constraint: expansion size
	 * 
	 * @throws IloException
	 */
	/*
	 * private void addConstraintExpansionSize() throws IloException {
	 * 
	 * IloLinearNumExpr numberSecondaryFacilities = linearNumExpr();
	 * IloLinearNumExpr one = linearNumExpr(); one.setConstant(1);
	 * 
	 * 
	 * // TODO: funktioniert noch nicht
	 * 
	 * 
	 * 
	 * 
	 * // create constraint this.expansionSize.clear(); for (int j = 0; j < this.t;
	 * j++) { for (int i = 0; i < this.f; i++) {
	 * 
	 * numberSecondaryFacilities.clear(); for (int k = 0; k < this.f; k++) { if
	 * (IF[k]) { numberSecondaryFacilities.addTerm(1,
	 * this.constructionStartSecondaryFacility[k][j]); } }
	 * 
	 * 
	 * //IloNumExpr capacityDemandPartlySF = one/numberSecondaryFacilities; if
	 * (IF[i]) {
	 * 
	 * this.expansionSize.addTerm(this.capacityDemand,
	 * this.constructionStartPrimaryFacility[i][j]);
	 * //this.expansionSize.addTerm(capacityDemandPartlySF,
	 * this.constructionStartSecondaryFacility[i][j]);
	 * 
	 * addEq(this.expansionSize, this.capacityExpansionAmount[i][j]); } } }
	 * 
	 * }
	 */

	/**
	 * 7th constraint: available capacity
	 * 
	 * @throws IloException
	 */
	private void addConstraintAvailableCapacity() throws IloException {

		this.availableCapacity.clear();

		for (int i = 0; i < this.f; i++) {
			if (IF[i]) {
				for (int j = 0; j < this.t; j++) {

					int tau1 = this.t - this.yearsToBuildPrimaryFacility;
					if (tau1 < 0) {
						tau1 = 0;
					}
					for (int k = 0; k < tau1; k++) {

						this.availableCapacity.addTerm(this.capacityExpansionAmount[i][k],
								this.constructionStartPrimaryFacility[i][k]);

					}

					double tau2 = this.t - this.yearsToBuildSecondaryFacility;// TODO: oder zu int casten?
					if (tau2 < 0) {
						tau2 = 0;
					}
					for (int k = 0; k < tau2; k++) {

						this.availableCapacity.addTerm(this.capacityExpansionAmount[i][k],
								this.constructionStartSecondaryFacility[i][k]);

					}
					addEq(this.availableCapacity, this.availableProductionCapacity[i][j]);
				}
			}
		}

	}

	/**
	 * 8th constraint: mass-balance equation
	 * 
	 * @throws IloException
	 */
	private void addConstraintsMassBalanceEquation() throws IloException {

		this.massbalanceEquation1.clear();
		this.massbalanceEquation2.clear();
		this.massbalanceEquation3.clear();

		// TODO: Wie umsetzen mit Sets OM IM?

	}

	/**
	 * 9th constraint: capacity restriction for production
	 * 
	 * @throws IloException
	 */
	private void addConstraintCapacityRestrictionForProduction() throws IloException {

		this.capacityRestrictionForProduction.clear();

		for (int i = 0; i < this.f; i++) {
			if (IF[i]) {
				for (int j = 0; j < this.t; j++) {

					addLe(this.consumedOrProducedAPI[i][j], this.availableProductionCapacity[i][j]);
				}
			}
		}

	}

	/**
	 * 10th constraint: lower limit of production
	 * 
	 * @throws IloException
	 */
	private void addConstraintLowerLimitOfProduction() throws IloException {

		this.lowerLimitForProductionPF.clear();
		this.lowerLimitForProductionSF.clear();
		this.lowerLimitForProductionSUM.clear();

		for (int i = 0; i < this.f; i++) {
			if (IF[i]) {
				for (int j = 0; j < this.t; j++) {

					int tau1 = this.t - this.yearsToBuildPrimaryFacility;
					if (tau1 < 0) {
						tau1 = 0;
					}
					for (int k = 0; k < tau1; k++) {

						this.lowerLimitForProductionPF.addTerm(this.lowerLimitProductionAPI[i],
								this.constructionStartPrimaryFacility[i][tau]);// TODO:Darf man die Summe
																				// ausmultiplizieren?

					}

					this.lowerLimitForProductionSUM.add(this.lowerLimitForProductionPF);

					double tau2 = this.t - this.yearsToBuildSecondaryFacility;// TODO: oder zu int casten?
					if (tau2 < 0) {
						tau2 = 0;
					}
					for (int k = 0; k < tau2; k++) {

						this.lowerLimitForProductionSF.addTerm(this.lowerLimitProductionAPI[i],
								this.constructionStartSecondaryFacility[i][tau]);// TODO:Darf man die Summe
																					// ausmultiplizieren?

					}
					this.lowerLimitForProductionSUM.add(this.lowerLimitForProductionSF);

					addLe(this.lowerLimitForProductionSUM, this.consumedOrProducedAPI[i][j]);
				}
			}
		}

	}

	/**
	 * 11th constraint: demand and supply constraint
	 * 
	 * @throws IloException
	 */
	private void addConstraintSupplyAndDemand() throws IloException {

		this.demandAndSupply.clear();

		for (int i = 0; i < this.f; i++) {// f
			if (EF[i]) {
				for (int j = 0; j < this.t; j++) {//t
					for (int k=0;k<this.i;k++) {//material i
						if(OM[i]||IM[i]) {
							
							for (int l=0;l<this.f;l++) {
								if(IF[l]&&OM[l]) {//facility to customer
									demandAndSupply.addTerm(1, this.shippedMaterialUnitsFacilityToCustomer[k][l][i][j]);
									
								}
								if(IF[l]&&IM[l]) {//supplier to facility
									demandAndSupply.addTerm(1, this.shippedMaterialUnitsSupplierToFacility[k][i][l][j]);
									
								}
							}
							double sumDS = this.supply[k][i]+this.demand[k][i][j];
							
							addLe(demandAndSupply, sumDS);
							
						}
						
					}
					
				}
			}
		}

	}

	/**
	 * 12th constraint: capital expenditure definition
	 * 
	 * @throws IloException
	 */
	private void addConstraintCapitalExpenditure() throws IloException {

		this.capitalExpenditureConstraint.clear();

		for (int i = 0; i < this.t; i++) {
			for (int j = 0; j < this.f; j++) {
				if (IF[j]) {
					this.capitalExpenditureConstraint.addTerm(this.setupCostPrimaryFacility,
							this.constructionStartPrimaryFacility[j][i]);

					double variableCostPF = this.yearsToBuildPrimaryFacility
							* this.variableProductionCostsPrimaryFacility;
					this.capitalExpenditureConstraint.addTerm(variableCostPF,
							this.constructionStartPrimaryFacility[j][i]);

					this.capitalExpenditureConstraint.addTerm(this.setupCostSecondaryFacility,
							this.constructionStartSecondaryFacility[j][i]);

					double variableCostSF = this.yearsToBuildSecondaryFacility
							* this.variableProductionCostsSecondaryFacility;
					this.capitalExpenditureConstraint.addTerm(variableCostSF,
							this.constructionStartSecondaryFacility[j][i]);
				}

			}
			addEq(this.capitalExpenditureConstraint, this.capitalExpenditure[i]);
		}

	}

	/**
	 * 13th constraint: budget constraint
	 * 
	 * @throws IloException
	 */
	private void addConstraintBudgetConstraint() throws IloException {
		double budgetUntilTau=0;
		this.budget.clear();

		for (int i = 0; i < this.t; i++) {
			for (int j = 0; j < this.f; j++) {
				if (IF[i]) {
					for (int k = 0; k < i; k++) {//t<tau
						this.budget.addTerm(this.setupCostPrimaryFacility, this.constructionStartPrimaryFacility[j][k]);

						double variableCostPF = this.yearsToBuildPrimaryFacility
								* this.variableProductionCostsPrimaryFacility;
						this.budget.addTerm(variableCostPF, this.constructionStartPrimaryFacility[j][k]);

						this.budget.addTerm(this.setupCostSecondaryFacility,
								this.constructionStartSecondaryFacility[j][k]);

						double variableCostSF = this.yearsToBuildSecondaryFacility
								* this.variableProductionCostsSecondaryFacility;
						this.budget.addTerm(variableCostSF, this.constructionStartSecondaryFacility[j][k]);
						
						budgetUntilTau=+this.capitalBudget[k];
					}
				}
			}
			addLe(this.budget,budgetUntilTau);
		}
	}

	/**
	 * 14th constraint: gross income of facility
	 * 
	 * @throws IloException
	 */
	private void addConstraintGrossIncome() throws IloException {

		this.grossIncome.clear();

	}

	/**
	 * 15th constraint: depreciation charge
	 * 
	 * @throws IloException
	 */
	private void addConstraintDepreciationCharge() throws IloException {

		this.depreciationChargePrimaryFacilities.clear();
		this.depreciationChargeSecondaryFacilities.clear();

	}

	/**
	 * 16th constraint: taxable income
	 * 
	 * @throws IloException
	 */
	private void addConstraintTaxableIncome() throws IloException {

		this.taxableIncomeConstraint.clear();

	}

	// TODO:nächste Nebenbedingungen
	
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