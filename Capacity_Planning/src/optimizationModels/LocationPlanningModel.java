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
	private int t; // number of fiscal years in planning horizon
	private int n; // number of nations

	// Sets
	//private int[] F; // F[f]: all(internal and external) facilities
	private boolean[] IF; // IF[f] internal facilities
	private boolean[] EF; // EF[f] external facilities
	//private int[] I; // I[i] all material types
	private boolean[][] OM;// OM[f][i] outgoing material
	private boolean[][] IM;// IM[f][i]incoming material
	private boolean[][] Fn;// Fn[f][n] nations

	// Daten
	private double[] capitalBudget;// capitalBudget[t] CB_t
	private double[][][] costInsuranceFreight; // costInsuranceFreight[i][s][f] CIF_isf
	private double[][][] demand;// demand[i][c][t] D_ict
	private double[][] importDuty; // importDuty[s][f] ID_isft
	private double[] projectLife;// projectLife[t] L_f
	private double[] variableProductionCosts;// MC_f
	private double[][] unitSellingPrice;// unitSellingPrice[i][f] P_if
	private double[] lowerLimitExpansionSize;// lowerLimitExpansionSize[f] g_L_f
	// private double[] initialCapacity;// initialCapacity[f] at time zero Q_f0
	private double[] upperLimitCapacity;// upperLimitCapacity[f] Q_U_f
	private double[][] supply;// supply[i][s] S_ist
	private double[] corporateTax;// corporateTax[n]TR_n
	private double[] lowerLimitProductionAPI;// lowerLimitProductionAPI[f] X_L_f
	private int API; // TODO: one material from set i pi
	private double[][] materialCoefficient; // materialCoeeficient[i][f] sigma_if
	

	// Transfer parameter
	private int remainingTimeOfClinicalTrials;// T*-t* delta_t*
	private double discountfactor;
	//private double capacityDemand;
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
	private IloNumVar[][] grossIncome;
	private IloNumVar[][] deltaCapacityExpansion;
	private IloNumVar[][] capacityExpansionAmount;

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
	private IloLinearNumExpr expansionSize1 = linearNumExpr();
	private IloLinearNumExpr expansionSize2 = linearNumExpr();
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
	private IloLinearNumExpr grossIncomeConstraint = linearNumExpr();
	private IloLinearNumExpr depreciationChargePrimaryFacilities = linearNumExpr();
	private IloLinearNumExpr depreciationChargeSecondaryFacilities = linearNumExpr();
	private IloLinearNumExpr taxableIncomeConstraint = linearNumExpr();

	public LocationPlanningModel(Data datainstanz) throws IloException {
		// Indices
		i = datainstanz.getI();
		f = datainstanz.getF();
		t = datainstanz.getT();
		n = datainstanz.getN();

		// Sets

		// Daten
		this.capitalBudget = datainstanz.getCapitalBudget();
		this.costInsuranceFreight = datainstanz.getCostInsuranceFreight();
		this.demand = datainstanz.getDemand();
		this.importDuty = datainstanz.getImportDuty();
		this.projectLife = datainstanz.getProjectLife();
		this.variableProductionCosts = datainstanz.getVariableProductionCosts();
		this.unitSellingPrice = datainstanz.getUnitSellingPrice();
		this.lowerLimitExpansionSize = datainstanz.getLowerLimitExpansionSize();
		// this.initialCapacity = datainstanz.getInitialCapacity();
		this.upperLimitCapacity = datainstanz.getUpperLimitCapacity();
		this.supply = datainstanz.getSupply();
		this.corporateTax = datainstanz.getCorporateTax();
		this.lowerLimitProductionAPI = datainstanz.getLowerLimitProductionAPI();
		this.API = datainstanz.getAPI();
		this.materialCoefficient = datainstanz.getMaterialCoefficient();
		
		

		// Transfer parameter
		this.remainingTimeOfClinicalTrials = datainstanz.getRemainingTimeOfClinicalTrials();
		this.discountfactor = datainstanz.getParameter_discountFactor();
		//this.capacityDemand = datainstanz.getParameter_capacityDemand();
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
		this.addConstraintExpansionSize();
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
		// 14th constraint
		this.addConstraintGrossIncome();
		// 15th constraint
		this.addConstraintDepreciationCharge();
		// 16th constraint
		this.addConstraintTaxableIncome();

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

	// add Objective
	private void addObjective() throws IloException {

		IloLinearNumExpr expr = linearNumExpr();

		for (int i = 0; i < this.t; i++) {
			double discountTerm = 1 / Math.pow(1 + this.discountfactor, t);
			for (int j = 0; j < this.f; j++) {
				if (IF[j]) {
					expr.addTerm(discountTerm, this.grossIncome[j][i]);
					
				}
			}
		}
		for (int i = 0; i < this.t; i++) {
			double discountTerm = 1 / Math.pow(1 + this.discountfactor, t);
			for (int j = 0; j < this.f; j++) {
				if (IF[j]) {
					
					expr.addTerm(-discountTerm, this.capitalExpenditure[i]);
				}
			}
		}
		for (int i = 0; i < this.t; i++) {
			double discountTerm = 1 / Math.pow(1 + this.discountfactor, t);
			for (int k = 0; k < this.n; k++) {

				double taxHelp = -discountTerm * this.corporateTax[k];
				expr.addTerm(taxHelp, this.taxableIncome[k][i]);
			}
		}

		objective = addMaximize();
		objective.setExpr(expr);
		System.out.println(objective);

	}

	// constraints

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
					double freecapacity = this.upperLimitCapacity[i];//TODO: braucht man das noch?

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
	private void addConstraintExpansionSize() throws IloException {
		
		this.expansionSize1.clear();
		this.expansionSize2.clear();
		
		for (int i = 0; i < this.t; i++) {
			for (int j = 0; j < this.f; j++) {
				if(IF[j]) {
					
					this.expansionSize1.addTerm(this.lowerLimitExpansionSize[j], this.constructionStartPrimaryFacility[j][i]);
					this.expansionSize1.addTerm(this.lowerLimitExpansionSize[j], this.constructionStartSecondaryFacility[j][i]);
					this.expansionSize1.addTerm(1, this.deltaCapacityExpansion[j][i]);
					
					addEq(this.expansionSize1, this.capacityExpansionAmount[j][i]);
				}
			}
		}
		
		for (int i = 0; i < this.t; i++) {
			for (int j = 0; j < this.f; j++) {
				if(IF[j]) {
				
					double expansionBeyondMin = this.upperLimitCapacity[j] - this.lowerLimitExpansionSize[j];
					this.expansionSize2.addTerm(expansionBeyondMin, this.constructionStartPrimaryFacility[j][i]);
					this.expansionSize2.addTerm(expansionBeyondMin, this.constructionStartSecondaryFacility[j][i]);
				
					addGe(this.expansionSize2, this.deltaCapacityExpansion[j][i]);
				}
					
			}
		}
		
	}
	
	
	/*
	 * private void addConstraintExpansionSize() throws IloException {
	 * 
	 * IloLinearNumExpr numberSecondaryFacilities = linearNumExpr();
	 * IloLinearNumExpr one = linearNumExpr(); one.setConstant(1);
	 * 
	 * 
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

						//TODO: nicht mehr linear!!
						this.availableCapacity.addTerm(this.capacityExpansionAmount[i][k],
								this.constructionStartPrimaryFacility[i][k]);
						
					}
						//TODO: nicht mehr linear!!
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

		for (int i = 0; i < this.f; i++) {
			if (IF[i]) {
				for (int j = 0; j < this.i; j++) {
					if (OM[i][j] || IM[i][j]) {
						for (int k = 0; k < this.t; t++) {

							massbalanceEquation1.addTerm(this.materialCoefficient[this.API - 1][i],
									this.consumedOrProducedMaterial[j][i][k]);
							massbalanceEquation2.addTerm(this.materialCoefficient[j][i],
									this.consumedOrProducedAPI[i][k]);

							// First equation
							addEq(this.massbalanceEquation1, this.massbalanceEquation2);

							for (int m = 0; m < this.f; m++) {
								if (IF[m]) {
									for (int l = 0; l < this.i; l++) {
										if (OM[m][l] || IM[m][l]) {
											if (OM[m][l]) {
												massbalanceEquation3.addTerm(this.materialCoefficient[this.API - 1][i],
														this.shippedMaterialUnitsSupplierToFacility[l][m][i][k]);
											}

											if (IM[m][l]) {

												massbalanceEquation3.addTerm(this.materialCoefficient[this.API - 1][i],
														this.shippedMaterialUnitsFacilityToCustomer[l][i][m][k]);
											}
										}
									}
								}
							}
							// Second equation
							addEq(this.massbalanceEquation2, this.massbalanceEquation3);
						}
					}
				}
			}
		}

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
								this.constructionStartPrimaryFacility[i][k]);

					}

					this.lowerLimitForProductionSUM.add(this.lowerLimitForProductionPF);

					double tau2 = this.t - this.yearsToBuildSecondaryFacility;// TODO: oder zu int casten?
					if (tau2 < 0) {
						tau2 = 0;
					}
					for (int k = 0; k < tau2; k++) {

						this.lowerLimitForProductionSF.addTerm(this.lowerLimitProductionAPI[i],
								this.constructionStartSecondaryFacility[i][k]);

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
				for (int j = 0; j < this.t; j++) {// t
					for (int k = 0; k < this.i; k++) {// material i
						if (OM[i][k] || IM[i][k]) {

							for (int l = 0; l < this.f; l++) {
								if (IF[l] && OM[l][k]) {// facility to customer
									demandAndSupply.addTerm(1, this.shippedMaterialUnitsFacilityToCustomer[k][l][i][j]);

								}
								if (IF[l] && IM[l][k]) {// supplier to facility
									demandAndSupply.addTerm(1, this.shippedMaterialUnitsSupplierToFacility[k][i][l][j]);

								}
							}
							double sumDS = this.supply[k][i] + this.demand[k][i][j];

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

					double variableCostPF = this.yearsToBuildPrimaryFacility * this.constructionCostPrimaryFacility;
					this.capitalExpenditureConstraint.addTerm(variableCostPF,
							this.constructionStartPrimaryFacility[j][i]);

					this.capitalExpenditureConstraint.addTerm(this.setupCostSecondaryFacility,
							this.constructionStartSecondaryFacility[j][i]);

					double variableCostSF = this.yearsToBuildSecondaryFacility * this.constructionCostSecondaryFacility;
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
		double budgetUntilTau = 0;
		this.budget.clear();

		for (int i = 0; i < this.t; i++) {
			for (int j = 0; j < this.f; j++) {
				if (IF[i]) {
					for (int k = 0; k < i; k++) {// t<tau
						this.budget.addTerm(this.setupCostPrimaryFacility, this.constructionStartPrimaryFacility[j][k]);

						double variableCostPF = this.yearsToBuildPrimaryFacility * this.constructionCostPrimaryFacility;
						this.budget.addTerm(variableCostPF, this.constructionStartPrimaryFacility[j][k]);

						this.budget.addTerm(this.setupCostSecondaryFacility,
								this.constructionStartSecondaryFacility[j][k]);

						double variableCostSF = this.yearsToBuildSecondaryFacility
								* this.constructionCostSecondaryFacility;
						this.budget.addTerm(variableCostSF, this.constructionStartSecondaryFacility[j][k]);

						budgetUntilTau = +this.capitalBudget[k];
					}
				}
			}
			addLe(this.budget, budgetUntilTau);
		}
	}

	/**
	 * 14th constraint: gross income of facility
	 * 
	 * @throws IloException
	 */
	private void addConstraintGrossIncome() throws IloException {

		this.grossIncomeConstraint.clear();

		for (int i = 0; i < this.t; i++) {
			for (int j = 0; j < this.f; j++) {
				if (IF[j]) {

					this.grossIncomeConstraint.addTerm(-this.variableProductionCosts[j],
							this.consumedOrProducedAPI[j][i]);

					for (int k = 0; k < this.i; k++) {
						if (OM[j][k]) {

							for (int m = 0; m < this.f; m++) {
								for (int l = 0; l < this.i; l++) {
									if (IM[m][l]) {
										// System.out.println("Check gleiches Material: "+k+" und "+l+" ?");
										this.grossIncomeConstraint.addTerm(this.unitSellingPrice[k][j],
												this.shippedMaterialUnitsFacilityToCustomer[k][j][m][i]);

									}

								}
							}
						} else if (IM[j][k]) {

							for (int m = 0; m < this.f; m++) {
								for (int l = 0; l < this.i; l++) {
									if (OM[m][l]) {
										// System.out.println("Check gleiches Material: "+k+" und "+l+" ?");

										double costCoefficient = this.costInsuranceFreight[k][m][j]
												+ (this.costInsuranceFreight[k][m][j] * this.importDuty[m][j]);
										this.grossIncomeConstraint.addTerm(-costCoefficient,
												this.shippedMaterialUnitsSupplierToFacility[k][m][j][i]);

									}

								}
							}
						}
					}
					addEq(this.grossIncomeConstraint, this.grossIncome[j][i]);
				}

			}
		}

	}

	/**
	 * 15th constraint: depreciation charge
	 * 
	 * @throws IloException
	 */
	private void addConstraintDepreciationCharge() throws IloException {

		this.depreciationChargePrimaryFacilities.clear();
		this.depreciationChargeSecondaryFacilities.clear();

		// Primary Facilities
		for (int i = 0; i < this.t; i++) {
			for (int j = 0; j < this.f; j++) {
				if (IF[i]) {
					for (int k = 0; k < this.t; k++) {// construction start (tau)
						double lowerBound = k + this.yearsToBuildPrimaryFacility;
						double upperBound1 = k + this.yearsToBuildPrimaryFacility + this.projectLife[j];
						double upperBound = 0;
						if (upperBound1 < this.t) {
							upperBound = upperBound1;
						} else {
							upperBound = this.t;
						}

						if (i >= lowerBound && i <= upperBound) {

							double setupCostPF = this.setupCostPrimaryFacility / this.projectLife[j];
							this.depreciationChargePrimaryFacilities.addTerm(setupCostPF,
									this.constructionStartPrimaryFacility[j][k]);

							double variableCostPF = this.yearsToBuildPrimaryFacility
									* this.constructionCostPrimaryFacility / this.projectLife[j];
							this.depreciationChargePrimaryFacilities.addTerm(variableCostPF,
									this.constructionStartPrimaryFacility[j][k]);

							addEq(this.depreciationChargePrimaryFacilities,
									this.depreciationChargePrimaryFacility[j][k][i]);
						}

						else {
							addEq(this.depreciationChargePrimaryFacility[j][k][i], 0);
						}

					}
				}
			}

		}

		// Secondary Facilities
		for (int i = 0; i < this.t; i++) {
			for (int j = 0; j < this.f; j++) {
				if (IF[i]) {
					for (int k = 0; k < this.t; k++) {// construction start (tau)
						double lowerBound = k + this.yearsToBuildSecondaryFacility;
						double upperBound1 = k + this.yearsToBuildSecondaryFacility + this.projectLife[j];
						double upperBound = 0;
						if (upperBound1 < this.t) {
							upperBound = upperBound1;
						} else {
							upperBound = this.t;
						}

						if (i >= lowerBound && i <= upperBound) {

							double setupCostSF = this.setupCostSecondaryFacility / this.projectLife[j];
							this.depreciationChargeSecondaryFacilities.addTerm(setupCostSF,
									this.constructionStartSecondaryFacility[j][k]);

							double variableCostSF = this.yearsToBuildSecondaryFacility
									* this.constructionCostSecondaryFacility / this.projectLife[j];
							this.depreciationChargeSecondaryFacilities.addTerm(variableCostSF,
									this.constructionStartSecondaryFacility[j][k]);

							addEq(this.depreciationChargeSecondaryFacilities,
									this.depreciationChargeSecondaryFacility[j][k][i]);
						}

						else {
							addEq(this.depreciationChargeSecondaryFacility[j][k][i], 0);
						}

					}
				}
			}

		}

	}

	/**
	 * 16th constraint: taxable income
	 * 
	 * @throws IloException
	 */
	private void addConstraintTaxableIncome() throws IloException {

		this.taxableIncomeConstraint.clear();

		for (int i = 0; i < this.t; i++) {
			for (int j = 0; j < this.n; j++) {
				for (int k = 0; k < this.f; k++) {
					if (IF[k] && Fn[k][j]) {
						this.taxableIncomeConstraint.addTerm(1, this.grossIncome[k][i]);

						for (int l = 0; l < i - this.yearsToBuildPrimaryFacility; l++) {
							this.taxableIncomeConstraint.addTerm(-1, this.depreciationChargePrimaryFacility[k][l][i]);

						}
						for (int l = 0; l < i - this.yearsToBuildSecondaryFacility; l++) {
							this.taxableIncomeConstraint.addTerm(-1, this.depreciationChargeSecondaryFacility[k][l][i]);

						}
					}
				}
				addLe(this.taxableIncomeConstraint, this.taxableIncome[j][i]);
			}
		}

	}

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