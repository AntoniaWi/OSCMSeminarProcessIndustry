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
	private double[][] importDuty; // importDuty[s][f] ID_sf
	private int projectLife;// projectLife L_f
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

	// Transfer parameter
	private int remainingTimeOfClinicalTrials; // T*-t* delta_t*
	private double discountfactor; // r
	private int monthsToBuildPrimaryFacility;
	private int monthsToBuildSecondaryFacility;
	private double setupCostPrimaryFacility; // Kp
	private double setupCostSecondaryFacility; // Ks
	private double constructionCostPrimaryFacility; // cp
	private double constructionCostSecondaryFacility; // cs

	/* Objective */
	private IloObjective objective;

	/* Decision variables */
	private IloNumVar[][][][] shippedMaterialUnitsFacilityToCustomer; // F_ifct
	private IloNumVar[][][][] shippedMaterialUnitsSupplierToFacility; // F_isft
	private IloNumVar[][][] depreciationChargePrimaryFacility; // NDC_p_ftaut
	private IloNumVar[][][] depreciationChargeSecondaryFacility; // NDC_s_ftaut
	private IloNumVar[][] availableProductionCapacity; // Q_ft
	private IloNumVar[][] taxableIncome; // TI_nt
	private IloNumVar[][][] consumedOrProducedMaterial; // x_ift
	private IloNumVar[][] consumedOrProducedAPI; // X_ft
	private IloNumVar[] capitalExpenditure; // CE_t
	private IloNumVar[][] grossIncome; // GI_ft
	private IloNumVar[][] deltaCapacityExpansion; // delta_q_ft
	private IloNumVar[][] capacityExpansionAmount; // q_ft

	private IloIntVar[][] constructionStartPrimaryFacility; // y_ft
	private IloIntVar[][] constructionStartSecondaryFacility; // z_ft

	/* Constraints */
	private IloLinearNumExpr numberOfPrimaryFacilities = linearNumExpr();
	private IloLinearNumExpr numberOfSecondaryFacilities = linearNumExpr();
	private IloLinearNumExpr limitationOfConstructionStartsPrimaryFacilities = linearNumExpr();
	private IloLinearNumExpr limitationOfConstructionStartsSecondaryFacilities = linearNumExpr();
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
	private IloLinearNumExpr demandAndSupply = linearNumExpr();
	private IloLinearNumExpr capitalExpenditureConstraint = linearNumExpr();
	private IloLinearNumExpr budget = linearNumExpr();
	private IloLinearNumExpr grossIncomeConstraint = linearNumExpr();
	private IloLinearNumExpr depreciationChargePrimaryFacilities = linearNumExpr();
	private IloLinearNumExpr depreciationChargeSecondaryFacilities = linearNumExpr();
	private IloLinearNumExpr taxableIncomeConstraint = linearNumExpr();

	public LocationPlanningModel(Data datainstanz) throws IloException {
		// Sets
		this.IF=datainstanz.getIF(); // IF[f] internal facilities
		this.EF=datainstanz.getEF(); // EF[f] external facilities
		this.OM=datainstanz.getOM(); // OM[f][i] outgoing material
		this.IM=datainstanz.getIM(); // IM[f][i]incoming material
		this.Fn=datainstanz.getFn(); // Fn[f][n] nations
		this.PIF=datainstanz.getPIF(); // PIF[f]
		this.SIF=datainstanz.getSIF(); // SIF[f]

		// Parameter
		this.I=datainstanz.getI(); // number of material types
		this.F=datainstanz.getF(); // number of all facilities
		this.T=datainstanz.getT(); // number of months in planning horizon
		this.N=datainstanz.getN(); // number of nations
		this.capitalBudget=datainstanz.getCapitalBudget();// capitalBudget[t] CB_t
		this.costInsuranceFreight=datainstanz.getCostInsuranceFreight(); // costInsuranceFreight[i][s][f] CIF_isf
		this.demand=datainstanz.getDemand();// demand[i][c][t] D_ict
		this.importDuty=datainstanz.getImportDuty(); // importDuty[s][f] ID_isf
		this.projectLife=datainstanz.getProjectLife();// projectLife[t] L_f
		this.variableProductionCosts=datainstanz.getVariableProductionCosts();// MC_f
		this.unitSellingPrice=datainstanz.getUnitSellingPrice();// unitSellingPrice[i][f] P_if
		this.lowerLimitExpansionSize=datainstanz.getLowerLimitExpansionSize();// lowerLimitExpansionSize[f] g_L_f
		this.upperLimitCapacity=datainstanz.getUpperLimitCapacity();// upperLimitCapacity[f] Q_U_f
		this.supply=datainstanz.getSupply();// supply[i][s] S_is
		this.corporateTax=datainstanz.getCorporateTax();// corporateTax[n]TR_n
		this.lowerLimitProductionAPI=datainstanz.getLowerLimitProductionAPI();// lowerLimitProductionAPI[f] X_L_f
		this.API=datainstanz.getAPI(); //
		this.materialCoefficient=datainstanz.getMaterialCoefficient(); // materialCoeeficient[i][f] sigma_if
		this.initialCapacity=datainstanz.getInitialCapacity(); // Q0

		// Transfer parameter
		this.remainingTimeOfClinicalTrials = datainstanz.getRemainingTimeofClinicalTrials();
		this.discountfactor = datainstanz.getParameter_discountFactor();
		this.monthsToBuildPrimaryFacility = datainstanz.getParameter_monthsToBuildPrimaryFacilities();
		this.monthsToBuildSecondaryFacility = datainstanz.getParameter_monthsToBuildSecondaryFacilities();
		this.setupCostPrimaryFacility = datainstanz.getParameter_setupCostPrimaryFacility();
		this.setupCostSecondaryFacility = datainstanz.getParameter_setupCostSecondaryFacility();
		this.constructionCostPrimaryFacility = datainstanz.getParameter_constructionCostPrimaryFacility();
		this.constructionCostSecondaryFacility = datainstanz.getParameter_constructionCostSecondaryFacility();

	}

	public static void main(String[] args)
			throws IloException, BiffException, IOException, RowsExceededException, WriteException {
		int x=0;
		Data instanz = new Data(x);
		
		for (int k=0;k<instanz.getI();k++) {//i
		for (int j=0; j<instanz.getF();j++) {//s
		for (int i=0; i<instanz.getF();i++) {//f
			
			
			
			//System.out.println("MC["+(i+1)+"] = "+instanz.getVariableProductionCosts()[i]);
				System.out.println("CIF["+(k+1)+"]["+(j+1)+"]["+(i+1)+"]= "+instanz.getCostInsuranceFreight()[k][j][i]);
		}
		}
		
		}
		/*

		LocationPlanningModel lpm = new LocationPlanningModel(instanz);

		lpm.build();
		lpm.solve();
		lpm.writeSolution(new int[] { 1, 2, 3 }, false);*/
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
		this.addConstraintCapacityExpansionOnlyIfConstructionIsPlanned();
		// 4th constraint
		this.addConstraintMinimumExpansion();
		// 5th constraint
		this.addConstraintExpansionSize();
		// 6th constraint
		this.addConstraintAvailableCapacity();
		// 7th constraint
		this.addConstraintsMassBalanceEquation();
		// 8th constraint
		this.addConstraintCapacityRestrictionForProduction();
		// 9th constraint
		this.addConstraintLowerLimitOfProduction();
		// 10th constraint
		this.addConstraintSupplyAndDemand();
		// 11th constraint
		this.addConstraintCapitalExpenditure();
		// 12th constraint
		this.addConstraintBudgetConstraint();
		// 13th constraint
		this.addConstraintGrossIncome();
		// 14th constraint
		this.addConstraintDepreciationCharge();
		// 15th constraint
		this.addConstraintTaxableIncome();

		String path = "./logs/model.lp";
		exportModel(path);

		System.out.println(
				"(WGP) complete rebuild finished, dur=" + (System.currentTimeMillis() - start) + " milli sec\n");
	}

	// add Decision variables
	private void addVarsX() throws IloException {
		for (int i = 0; i < F; i++) {
			for (int j = 0; j < T; j++) {
				if (IF[i] && PIF[i]) {
					constructionStartPrimaryFacility[i][j] = intVar(0, 1);

				}
				if (IF[i] && IF[i]) {
					constructionStartSecondaryFacility[i][j] = intVar(0, 1);
				}
			}
		}

	}

	// add Objective
	private void addObjective() throws IloException {

		IloLinearNumExpr expr = linearNumExpr();

		for (int i = 0; i < this.T; i++) {
			double discountTerm = 1 / Math.pow(1 + this.discountfactor, T);
			for (int j = 0; j < this.F; j++) {
				if (IF[j]) {
					expr.addTerm(discountTerm, this.grossIncome[j][i]);

				}
			}
		}
		for (int i = 0; i < this.T; i++) {
			double discountTerm = 1 / Math.pow(1 + this.discountfactor, T);
			for (int j = 0; j < this.F; j++) {
				if (IF[j]) {

					expr.addTerm(-discountTerm, this.capitalExpenditure[i]);
				}
			}
		}
		for (int i = 0; i < this.T; i++) {
			double discountTerm = 1 / Math.pow(1 + this.discountfactor, T);
			for (int k = 0; k < this.N; k++) {

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
		for (int i = 0; i < this.F; i++) {
			if (IF[i] && PIF[i]) {
				this.numberOfPrimaryFacilities.addTerm(1, this.constructionStartPrimaryFacility[i][1]);
			}
		}
		addEq(this.numberOfPrimaryFacilities, 1);

	}

	private void addConstraintNumberOfSecondaryFacilities() throws IloException {

		this.numberOfSecondaryFacilities.clear();

		for (int i = 0; i < this.F; i++) {
			if (IF[i] && SIF[i]) {
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

		for (int i = 0; i < this.F; i++) {
			if (IF[i] && PIF[i]) {
				for (int j = 0; j < this.T; j++) {

					this.limitationOfConstructionStartsPrimaryFacilities.addTerm(1,
							this.constructionStartPrimaryFacility[i][j]);
				}
			}
		}
		addLe(this.limitationOfConstructionStartsPrimaryFacilities, 1);

	}

	private void addConstraintOneConstructionDuringPlanningHorizonSF() throws IloException {

		this.limitationOfConstructionStartsSecondaryFacilities.clear();

		for (int i = 0; i < this.F; i++) {
			if (IF[i] && SIF[i]) {
				for (int j = 0; j < this.T; j++) {

					this.limitationOfConstructionStartsSecondaryFacilities.addTerm(1,
							this.constructionStartSecondaryFacility[i][j]);
				}
				addLe(this.limitationOfConstructionStartsSecondaryFacilities, 1);
			}

		}

	}

	/**
	 * 3rd constraint: capacity expansion only if expansion is planned
	 * 
	 * @throws IloException
	 */
	private void addConstraintCapacityExpansionOnlyIfConstructionIsPlanned() throws IloException {

		this.capacityExpansionOnlyIfPlanned.clear();

		for (int j = 0; j < this.T; j++) {
			for (int i = 0; i < this.F; i++) {
				if (IF[i] && PIF[i]) {

					double freecapacity = this.upperLimitCapacity[i] - this.initialCapacity;

					this.capacityExpansionOnlyIfPlanned.addTerm(freecapacity,
							this.constructionStartPrimaryFacility[i][j]);

					addGe(this.capacityExpansionOnlyIfPlanned, this.capacityExpansionAmount[i][j]);
				}

				else if (IF[i] && SIF[i]) {

					double freecapacity = this.upperLimitCapacity[i] - this.initialCapacity;

					this.capacityExpansionOnlyIfPlanned.addTerm(freecapacity,
							this.constructionStartSecondaryFacility[i][j]);

					addGe(this.capacityExpansionOnlyIfPlanned, this.capacityExpansionAmount[i][j]);
				}

			}

		}

	}

	/**
	 * 4th constraint: minimum expansion size
	 * 
	 * @throws IloException
	 */
	private void addConstraintMinimumExpansion() throws IloException {

		this.minimumExpansion.clear();

		for (int j = 0; j < this.T; j++) {

			for (int i = 0; i < this.F; i++) {
				if (IF[i] && PIF[i]) {
					this.minimumExpansion.addTerm(this.lowerLimitExpansionSize[i],
							this.constructionStartPrimaryFacility[i][j]);
					addLe(this.minimumExpansion, this.capacityExpansionAmount[i][j]);
				}

				else if (IF[i] && SIF[i]) {
					this.minimumExpansion.addTerm(this.lowerLimitExpansionSize[i],
							this.constructionStartSecondaryFacility[i][j]);

					addLe(this.minimumExpansion, this.capacityExpansionAmount[i][j]);
				}

			}
		}

	}

	/**
	 * 5th constraint: a) expansion amount
	 * 
	 * @throws IloException
	 */
	private void addConstraintExpansionSize() throws IloException {

		this.expansionSize1.clear();
		this.expansionSize2.clear();

		for (int i = 0; i < this.T; i++) {
			for (int j = 0; j < this.F; j++) {
				if (IF[j] && PIF[j]) {

					this.expansionSize1.addTerm(this.lowerLimitExpansionSize[j],
							this.constructionStartPrimaryFacility[j][i]);
					this.expansionSize1.addTerm(1, this.deltaCapacityExpansion[j][i]);

					addEq(this.expansionSize1, this.capacityExpansionAmount[j][i]);
				}

				else if (IF[j] && SIF[j]) {

					this.expansionSize1.addTerm(this.lowerLimitExpansionSize[j],
							this.constructionStartSecondaryFacility[j][i]);
					this.expansionSize1.addTerm(1, this.deltaCapacityExpansion[j][i]);

					addEq(this.expansionSize1, this.capacityExpansionAmount[j][i]);
				}
			}
		}

		/**
		 * 5th constraint: b) expansion amount beyond minimum
		 */

		for (int i = 0; i < this.T; i++) {
			for (int j = 0; j < this.F; j++) {
				if (IF[j] && PIF[j]) {

					double expansionBeyondMin = this.upperLimitCapacity[j] - this.initialCapacity
							- this.lowerLimitExpansionSize[j];
					this.expansionSize2.addTerm(expansionBeyondMin, this.constructionStartPrimaryFacility[j][i]);

					addGe(this.expansionSize2, this.deltaCapacityExpansion[j][i]);
				}

				else if (IF[j] && SIF[j]) {

					double expansionBeyondMin = this.upperLimitCapacity[j] - this.initialCapacity
							- this.lowerLimitExpansionSize[j];
					this.expansionSize2.addTerm(expansionBeyondMin, this.constructionStartSecondaryFacility[j][i]);

					addGe(this.expansionSize2, this.deltaCapacityExpansion[j][i]);
				}

			}
		}

	}

	/**
	 * 6th constraint: available capacity
	 * 
	 * @throws IloException
	 */
	private void addConstraintAvailableCapacity() throws IloException {

		this.availableCapacity.clear();

		for (int j = 0; j < this.T; j++) {
			for (int i = 0; i < this.F; i++) {
				if (IF[i] && PIF[i]) {

					addEq(this.availableProductionCapacity[i][0], this.initialCapacity);

					this.availableCapacity.addTerm(1, this.availableProductionCapacity[i][j - 1]);

					this.availableCapacity.addTerm(this.lowerLimitExpansionSize[i],
							this.constructionStartPrimaryFacility[i][j - this.monthsToBuildPrimaryFacility]);

					this.availableCapacity.addTerm(1,
							this.deltaCapacityExpansion[i][j - this.monthsToBuildPrimaryFacility]);

					addEq(this.availableCapacity, this.availableProductionCapacity[i][j]);
				}
				else if (IF[i] && SIF[i]) {

					addEq(this.availableProductionCapacity[i][0], this.initialCapacity);

					this.availableCapacity.addTerm(1, this.availableProductionCapacity[i][j - 1]);

					this.availableCapacity.addTerm(this.lowerLimitExpansionSize[i],
							this.constructionStartSecondaryFacility[i][j - this.monthsToBuildSecondaryFacility]);

					this.availableCapacity.addTerm(1,
							this.deltaCapacityExpansion[i][j - this.monthsToBuildSecondaryFacility]);

					addEq(this.availableCapacity, this.availableProductionCapacity[i][j]);
				}
			}
		}

	}

	/**
	 * 7th constraint: mass-balance equation
	 * 
	 * @throws IloException
	 */
	private void addConstraintsMassBalanceEquation() throws IloException {

		this.massbalanceEquation1.clear();
		this.massbalanceEquation2.clear();
		this.massbalanceEquation3.clear();

		for (int i = 0; i < this.F; i++) {
			if (IF[i]) {
				for (int j = 0; j < this.I; j++) {
					if (OM[i][j] || IM[i][j]) {
						for (int k = 0; k < this.T; T++) {

							massbalanceEquation1.addTerm(this.materialCoefficient[this.API - 1][i],
									this.consumedOrProducedMaterial[j][i][k]);
							massbalanceEquation2.addTerm(this.materialCoefficient[j][i],
									this.consumedOrProducedAPI[i][k]);

							// First equation
							addEq(this.massbalanceEquation1, this.massbalanceEquation2);

							for (int m = 0; m < this.F; m++) {
								if (IF[m]) {
									for (int l = 0; l < this.I; l++) {
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
	 * 8th constraint: capacity restriction for production
	 * 
	 * @throws IloException
	 */
	private void addConstraintCapacityRestrictionForProduction() throws IloException {

		this.capacityRestrictionForProduction.clear();

		for (int i = 0; i < this.F; i++) {
			if (IF[i]) {
				for (int j = 0; j < this.T; j++) {

					addLe(this.consumedOrProducedAPI[i][j], this.availableProductionCapacity[i][j]);
				}
			}
		}

	}

	/**
	 * 9th constraint: lower limit of production
	 * 
	 * @throws IloException
	 */
	private void addConstraintLowerLimitOfProduction() throws IloException {

		this.lowerLimitForProductionPF.clear();
		this.lowerLimitForProductionSF.clear();

		for (int j = 0; j < this.T; j++) {
			for (int i = 0; i < this.F; i++) {
				if (IF[i] && PIF[i]) {

					int tau1 = this.T - this.monthsToBuildPrimaryFacility;
					if (tau1 < 0) {
						tau1 = 0;
					}
					for (int k = 0; k < tau1; k++) {

						this.lowerLimitForProductionPF.addTerm(this.lowerLimitProductionAPI[i],
								this.constructionStartPrimaryFacility[i][k]);

					}

					addLe(this.lowerLimitForProductionPF, this.consumedOrProducedAPI[i][j]);

				}

				else if (IF[i] && SIF[i]) {
					int tau2 = this.T - this.monthsToBuildSecondaryFacility;
					if (tau2 < 0) {
						tau2 = 0;
					}
					for (int k = 0; k < tau2; k++) {

						this.lowerLimitForProductionSF.addTerm(this.lowerLimitProductionAPI[i],
								this.constructionStartSecondaryFacility[i][k]);

					}

					addLe(this.lowerLimitForProductionSF, this.consumedOrProducedAPI[i][j]);
				}
			}
		}

	}

	/**
	 * 10th constraint: demand and supply constraint
	 * 
	 * @throws IloException
	 */
	private void addConstraintSupplyAndDemand() throws IloException {

		this.demandAndSupply.clear();

		for (int i = 0; i < this.F; i++) {// f
			if (EF[i]) {
				for (int j = 0; j < this.T; j++) {// t
					for (int k = 0; k < this.I; k++) {// material i
						if (OM[i][k] || IM[i][k]) {

							for (int l = 0; l < this.F; l++) {
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
	 * 11th constraint: capital expenditure definition
	 * 
	 * @throws IloException
	 */
	private void addConstraintCapitalExpenditure() throws IloException {

		this.capitalExpenditureConstraint.clear();

		for (int i = 0; i < this.T; i++) {
			for (int j = 0; j < this.F; j++) {
				if (IF[j] && PIF[j]) {
					this.capitalExpenditureConstraint.addTerm(this.setupCostPrimaryFacility,
							this.constructionStartPrimaryFacility[j][i]);

					double variableCostPF = this.monthsToBuildPrimaryFacility * this.constructionCostPrimaryFacility;
					this.capitalExpenditureConstraint.addTerm(variableCostPF,
							this.constructionStartPrimaryFacility[j][i]);
				}

				else if (IF[j] && SIF[j]) {
					this.capitalExpenditureConstraint.addTerm(this.setupCostSecondaryFacility,
							this.constructionStartSecondaryFacility[j][i]);

					double variableCostSF = this.monthsToBuildSecondaryFacility
							* this.constructionCostSecondaryFacility;
					this.capitalExpenditureConstraint.addTerm(variableCostSF,
							this.constructionStartSecondaryFacility[j][i]);
				}

			}
			addEq(this.capitalExpenditureConstraint, this.capitalExpenditure[i]);
		}

	}

	/**
	 * 12th constraint: budget constraint
	 * 
	 * @throws IloException
	 */
	private void addConstraintBudgetConstraint() throws IloException {
		double budgetUntilTau = 0;
		this.budget.clear();

		for (int i = 0; i < this.T; i++) {
			for (int j = 0; j < this.F; j++) {
				if (IF[j] && PIF[j]) {
					for (int k = 0; k < i; k++) {// t<tau
						this.budget.addTerm(this.setupCostPrimaryFacility, this.constructionStartPrimaryFacility[j][k]);

						double variableCostPF = this.monthsToBuildPrimaryFacility
								* this.constructionCostPrimaryFacility;
						this.budget.addTerm(variableCostPF, this.constructionStartPrimaryFacility[j][k]);
					}
				}

				else if (IF[j] && SIF[j]) {
					for (int k = 0; k < i; k++) {// t<tau
						this.budget.addTerm(this.setupCostSecondaryFacility,
								this.constructionStartSecondaryFacility[j][k]);

						double variableCostSF = this.monthsToBuildSecondaryFacility
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
	 * 13th constraint: gross income of facility
	 * 
	 * @throws IloException
	 */
	private void addConstraintGrossIncome() throws IloException {

		this.grossIncomeConstraint.clear();

		for (int i = 0; i < this.T; i++) {
			for (int j = 0; j < this.F; j++) {
				if (IF[j]) {

					this.grossIncomeConstraint.addTerm(-this.variableProductionCosts[j],
							this.consumedOrProducedAPI[j][i]);

					for (int k = 0; k < this.I; k++) {
						if (OM[j][k]) {

							for (int m = 0; m < this.F; m++) {
								for (int l = 0; l < this.I; l++) {
									if (IM[m][l]) {
										// System.out.println("Check gleiches Material: "+k+" und "+l+" ?");
										this.grossIncomeConstraint.addTerm(this.unitSellingPrice[k][j],
												this.shippedMaterialUnitsFacilityToCustomer[k][j][m][i]);

									}

								}
							}
						} else if (IM[j][k]) {

							for (int m = 0; m < this.F; m++) {
								for (int l = 0; l < this.I; l++) {
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
	 * 14th constraint: depreciation charge
	 * 
	 * @throws IloException
	 */
	private void addConstraintDepreciationCharge() throws IloException {

		this.depreciationChargePrimaryFacilities.clear();
		this.depreciationChargeSecondaryFacilities.clear();

		// Primary Facilities
		for (int i = 0; i < this.T; i++) {
			for (int j = 0; j < this.F; j++) {
				if (IF[i]) {
					for (int k = 0; k < this.T; k++) {// construction start (tau)
						double lowerBound = k + this.monthsToBuildPrimaryFacility;
						double upperBound1 = k + this.monthsToBuildPrimaryFacility + this.projectLife;
						double upperBound = 0;
						if (upperBound1 < this.T) {
							upperBound = upperBound1;
						} else {
							upperBound = this.T;
						}

						if (i >= lowerBound && i <= upperBound) {

							double setupCostPF = this.setupCostPrimaryFacility / this.projectLife;
							this.depreciationChargePrimaryFacilities.addTerm(setupCostPF,
									this.constructionStartPrimaryFacility[j][k]);

							double variableCostPF = this.monthsToBuildPrimaryFacility
									* this.constructionCostPrimaryFacility / this.projectLife;
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
		for (int i = 0; i < this.T; i++) {
			for (int j = 0; j < this.F; j++) {
				if (IF[i]) {
					for (int k = 0; k < this.T; k++) {// construction start (tau)
						double lowerBound = k + this.monthsToBuildSecondaryFacility;
						double upperBound1 = k + this.monthsToBuildSecondaryFacility + this.projectLife;
						double upperBound = 0;
						if (upperBound1 < this.T) {
							upperBound = upperBound1;
						} else {
							upperBound = this.T;
						}

						if (i >= lowerBound && i <= upperBound) {

							double setupCostSF = this.setupCostSecondaryFacility / this.projectLife;
							this.depreciationChargeSecondaryFacilities.addTerm(setupCostSF,
									this.constructionStartSecondaryFacility[j][k]);

							double variableCostSF = this.monthsToBuildSecondaryFacility
									* this.constructionCostSecondaryFacility / this.projectLife;
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
	 * 15th constraint: taxable income
	 * 
	 * @throws IloException
	 */
	private void addConstraintTaxableIncome() throws IloException {

		this.taxableIncomeConstraint.clear();

		for (int i = 0; i < this.T; i++) {
			for (int j = 0; j < this.N; j++) {
				for (int k = 0; k < this.F; k++) {
					if (IF[k] && Fn[k][j]) {
						this.taxableIncomeConstraint.addTerm(1, this.grossIncome[k][i]);

						for (int l = 0; l < i - this.monthsToBuildPrimaryFacility; l++) {
							this.taxableIncomeConstraint.addTerm(-1, this.depreciationChargePrimaryFacility[k][l][i]);

						}
						for (int l = 0; l < i - this.monthsToBuildSecondaryFacility; l++) {
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