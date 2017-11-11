package optimizationModels;

import ilog.concert.*;
import ilog.cplex.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import dataManagement.Data;
import dataManagement.ReadAndWrite;
import jxl.read.biff.BiffException;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class LocationPlanningModel extends IloCplex {

	private Data datainstanz;
	
	/*// Sets
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
	private double[][][] costInsuranceFreight; // costInsuranceFreight[i][s][f]
												// CIF_isf
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
	private double[][] materialCoefficient; // materialCoeeficient[i][f]
											// sigma_if
	private int initialCapacity; // Q0

	// Transfer parameter
	private int remainingTimeOfClinicalTrials; // T*-t* delta_t*
	private double discountfactor; // r
	private int monthsToBuildPrimaryFacility;
	private int monthsToBuildSecondaryFacility;
	private double setupCostPrimaryFacility; // Kp
	private double setupCostSecondaryFacility; // Ks
	private double constructionCostPrimaryFacility; // cp
	private double constructionCostSecondaryFacility; // cs*/

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
	private IloLinearNumExpr lowerLimitForProductionPF1 = linearNumExpr();
	private IloLinearNumExpr lowerLimitForProductionPF2 = linearNumExpr();
	private IloLinearNumExpr lowerLimitForProductionSF = linearNumExpr();
	private IloLinearNumExpr demandAndSupply = linearNumExpr();
	private IloLinearNumExpr capitalExpenditureConstraint = linearNumExpr();
	private IloLinearNumExpr budget = linearNumExpr();
	private IloLinearNumExpr grossIncomeConstraint = linearNumExpr();
	private IloLinearNumExpr depreciationChargePrimaryFacilities = linearNumExpr();
	private IloLinearNumExpr depreciationChargeSecondaryFacilities = linearNumExpr();
	private IloLinearNumExpr taxableIncomeConstraint = linearNumExpr();

	public LocationPlanningModel(Data datainstanz) throws IloException, BiffException, WriteException, IOException {
		
		this.datainstanz = datainstanz;
		

		datainstanz.setT(datainstanz.getRemainingTimeofClinicalTrials() + datainstanz.getTimeM() + datainstanz.getTimeR());

		ReadAndWrite.writeTransferParameter(datainstanz);
		ReadAndWrite.createAndWriteDict(datainstanz);
		
		// Initialization of decision variables
		this.constructionStartPrimaryFacility = new IloIntVar[this.datainstanz.getF()][this.datainstanz.getT()];
		this.constructionStartSecondaryFacility = new IloIntVar[this.datainstanz.getF()][this.datainstanz.getT()];
		this.shippedMaterialUnitsFacilityToCustomer = new IloNumVar[this.datainstanz.getI()][this.datainstanz.getF()][this.datainstanz.getF()][this.datainstanz.getT()]; // F_ifct
		this.shippedMaterialUnitsSupplierToFacility = new IloNumVar[this.datainstanz.getI()][this.datainstanz.getF()][this.datainstanz.getF()][this.datainstanz.getT()]; // F_isft
		this.depreciationChargePrimaryFacility = new IloNumVar[this.datainstanz.getF()][this.datainstanz.getT()][this.datainstanz.getT()]; // NDC_p_ftaut
		this.depreciationChargeSecondaryFacility = new IloNumVar[this.datainstanz.getF()][this.datainstanz.getT()][this.datainstanz.getT()]; // NDC_s_ftaut
		this.availableProductionCapacity = new IloNumVar[this.datainstanz.getF()][this.datainstanz.getT()]; // Q_ft
		this.taxableIncome = new IloNumVar[this.datainstanz.getN()][this.datainstanz.getT()]; // TI_nt
		this.consumedOrProducedMaterial = new IloNumVar[this.datainstanz.getI()][this.datainstanz.getF()][this.datainstanz.getT()]; // x_ift
		this.consumedOrProducedAPI = new IloNumVar[this.datainstanz.getF()][this.datainstanz.getT()]; // X_ft
		this.capitalExpenditure = new IloNumVar[this.datainstanz.getT()]; // CE_t
		this.grossIncome = new IloNumVar[this.datainstanz.getF()][this.datainstanz.getT()]; // GI_ft
		this.deltaCapacityExpansion = new IloNumVar[this.datainstanz.getF()][this.datainstanz.getT()]; // delta_q_ft
		this.capacityExpansionAmount = new IloNumVar[this.datainstanz.getF()][this.datainstanz.getT()]; // q_ft
	}

	
	
	// TODO: should be deleted in the end, here only for testing
	
	public static void main(String[] args)
			throws IloException, BiffException, IOException, RowsExceededException, WriteException {
		int x = 0;
		Data instanz = new Data(x);

		LocationPlanningModel lpm = new LocationPlanningModel(instanz);

		lpm.build();
		lpm.solve();
		lpm.writeSolution(new int[] { 1, 2, 3 }, instanz);
		ReadAndWrite.writeSolution(instanz);
		// lpm.ergebnisschreibenRobust(lpm);
	}
	
	
	/**
	 * Runs the Location Planning Model 
	 * @throws IloException
	 * @throws BiffException
	 * @throws IOException
	 * @throws RowsExceededException
	 * @throws WriteException
	 */
	public void run () throws IloException, BiffException, IOException, RowsExceededException, WriteException {
		
		int tmp_remainingTime = (this.datainstanz.getParameter_planningHorizon() - this.datainstanz.getCountPeriods());
		this.datainstanz.setRemainingTimeofClinicalTrials(tmp_remainingTime+1);
		
		this.build();
		this.solve();
		this.writeSolution(new int[] { 1, 2, 3 }, datainstanz);
		ReadAndWrite.writeSolution(this.datainstanz);
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
		// this.addConstraintCapacityExpansionOnlyIfConstructionIsPlanned();
		// 4th constraint
		// this.addConstraintMinimumExpansion();
		// 5th constraint
		this.addConstraintExpansionSize();
		// 6th constraint
		this.addConstraintAvailableCapacity();
		// 7th constraint
		this.addConstraintsMassBalanceEquation();
		// 8th constraint
		this.addConstraintCapacityRestrictionForProduction();
		// 9th constraint
		//this.addConstraintLowerLimitOfProduction();
		// 10th constraint
		this.addConstraintSupplyAndDemand();
		// 11th constraint
		this.addConstraintCapitalExpenditure();
		// 12th constraint
		this.addConstraintBudgetConstraint();
		// 13th constraint
		this.addConstraintGrossIncome();
		// 14th constraint
		// this.addConstraintDepreciationCharge();
		// 15th constraint
		this.addConstraintTaxableIncome();

		// String path = "./logs/model.lp"; exportModel(path);

		System.out.println(
				"(WGP) complete rebuild finished, dur=" + (System.currentTimeMillis() - start) + " milli sec\n");
	}

	// add Decision variables
	private void addVarsX() throws IloException {
		for (int i = 0; i < datainstanz.getF(); i++) {// f
			for (int k = 0; k < datainstanz.getF(); k++) {// c,s
				for (int n = 0; n < datainstanz.getN(); n++) {
					for (int j = 0; j < datainstanz.getT(); j++) {// t
						for (int m = 0; m < datainstanz.getT(); m++) {// tau
							for (int l = 0; l < datainstanz.getI(); l++) {// i
								if (datainstanz.getIF()[i] && datainstanz.getPIF()[i]) {
									this.constructionStartPrimaryFacility[i][j] = intVar(0, 1);
									this.depreciationChargePrimaryFacility[i][m][j] = numVar(0, 1000000);

								} else if (datainstanz.getIF()[i] && datainstanz.getSIF()[i]) {
									this.constructionStartSecondaryFacility[i][j] = intVar(0, 1);
									this.depreciationChargeSecondaryFacility[i][m][j] = numVar(0, 1000000);
								}
								this.capacityExpansionAmount[i][j] = numVar(0, 1000000);
								this.shippedMaterialUnitsFacilityToCustomer[l][i][k][j] = numVar(0, 1000000);
								this.shippedMaterialUnitsSupplierToFacility[l][k][i][j] = numVar(0, 1000000);
								this.availableProductionCapacity[i][j] = numVar(0, 1000000);
								this.taxableIncome[n][j] = numVar(0, 1000000000);
								this.consumedOrProducedMaterial[l][i][j] = numVar(0, 10000000);
								this.consumedOrProducedAPI[i][j] = numVar(0, 10000000);
								this.capitalExpenditure[j] = numVar(0, 1000000000);
								this.grossIncome[i][j] = numVar(0, 1000000000);
								this.deltaCapacityExpansion[i][j] = numVar(0, 10000000);
							}
						}

					}
				}
			}
		}
	}

	// add Objective
	private void addObjective() throws IloException {

		IloLinearNumExpr expr = linearNumExpr();

		for (int i = 0; i < this.datainstanz.getT(); i++) {
			double discountTerm = 1 / Math.pow(1 + this.datainstanz.getParameter_discountFactor_location(), (i + 1));
			for (int j = 0; j < this.datainstanz.getF(); j++) {
				if (datainstanz.getIF()[j]) {
					expr.addTerm(discountTerm, this.grossIncome[j][i]);

				}
			}
		}

		// System.out.println(expr);

		for (int i = 0; i < this.datainstanz.getT(); i++) {
			double discountTerm = -1 / Math.pow(1 + datainstanz.getParameter_discountFactor_location(), (i + 1));
			for (int j = 0; j < this.datainstanz.getF(); j++) {
				if (datainstanz.getIF()[j]) {

					expr.addTerm(discountTerm, this.capitalExpenditure[i]);

				}
			}
		}

		// System.out.println(expr);
		for (int i = 0; i < this.datainstanz.getT(); i++) {
			double discountTerm = 1 / Math.pow(1 + datainstanz.getParameter_discountFactor_location(), (i + 1));
			for (int k = 0; k < this.datainstanz.getN(); k++) {

				double taxHelp = discountTerm * datainstanz.getCorporateTax()[k];
				expr.addTerm(-taxHelp, this.taxableIncome[k][i]);
			}
		}

		// System.out.println(expr);

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

		for (int i = 0; i < datainstanz.getF(); i++) {
			if (datainstanz.getIF()[i] && datainstanz.getPIF()[i]) {

				this.numberOfPrimaryFacilities.addTerm(1, this.constructionStartPrimaryFacility[i][0]);

			}
		}
		System.out.println(this.numberOfPrimaryFacilities);
		addEq(this.numberOfPrimaryFacilities, 1);

	}

	private void addConstraintNumberOfSecondaryFacilities() throws IloException {

		this.numberOfSecondaryFacilities.clear();

		for (int i = 0; i < this.datainstanz.getF(); i++) {
			if (datainstanz.getIF()[i] && datainstanz.getSIF()[i]) {
				this.numberOfSecondaryFacilities.addTerm(1,
						this.constructionStartSecondaryFacility[i][this.datainstanz.getParameter_monthsToBuildPrimaryFacilities()- this.datainstanz.getParameter_monthsToBuildSecondaryFacilities()]);
			}
		}
		//addEq(this.numberOfSecondaryFacilities, 2);
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

		for (int i = 0; i < this.datainstanz.getF(); i++) {
			if (datainstanz.getIF()[i] && datainstanz.getPIF()[i]) {
				for (int j = 0; j < this.datainstanz.getT(); j++) {

					this.limitationOfConstructionStartsPrimaryFacilities.addTerm(1,
							this.constructionStartPrimaryFacility[i][j]);
				}
			}
		}
		addLe(this.limitationOfConstructionStartsPrimaryFacilities, 1);

	}

	private void addConstraintOneConstructionDuringPlanningHorizonSF() throws IloException {

		this.limitationOfConstructionStartsSecondaryFacilities.clear();

		for (int i = 0; i < this.datainstanz.getF(); i++) {
			this.limitationOfConstructionStartsSecondaryFacilities.clear();
			if (datainstanz.getIF()[i] && datainstanz.getSIF()[i]) {
				for (int j = 0; j < this.datainstanz.getT(); j++) {

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

		for (int j = 0; j < this.datainstanz.getT(); j++) {
			for (int i = 0; i < this.datainstanz.getF(); i++) {
				this.capacityExpansionOnlyIfPlanned.clear();
				if (this.datainstanz.getIF()[i] && this.datainstanz.getPIF()[i]) {

					double freecapacity = this.datainstanz.getUpperLimitCapacity()[i] - this.datainstanz.getInitialCapacity();

					this.capacityExpansionOnlyIfPlanned.addTerm(freecapacity,
							this.constructionStartPrimaryFacility[i][j]);

					addGe(this.capacityExpansionOnlyIfPlanned, this.capacityExpansionAmount[i][j]);
				}

				else if (this.datainstanz.getIF()[i] && this.datainstanz.getSIF()[i]) {

					double freecapacity = this.datainstanz.getUpperLimitCapacity()[i] - this.datainstanz.getInitialCapacity();

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

		for (int j = 0; j < this.datainstanz.getT(); j++) {

			for (int i = 0; i < this.datainstanz.getF(); i++) {
				this.minimumExpansion.clear();
				if (this.datainstanz.getIF()[i] && this.datainstanz.getPIF()[i]) {
					this.minimumExpansion.addTerm(this.datainstanz.getLowerLimitExpansionSize()[i],
							this.constructionStartPrimaryFacility[i][j]);
					addLe(this.minimumExpansion, this.capacityExpansionAmount[i][j]);
				}

				else if (this.datainstanz.getIF()[i] && this.datainstanz.getSIF()[i]) {
					this.minimumExpansion.addTerm(this.datainstanz.getLowerLimitExpansionSize()[i],
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

		for (int i = 0; i < this.datainstanz.getT(); i++) {
			for (int j = 0; j < this.datainstanz.getF(); j++) {

				this.expansionSize1.clear();

				if (this.datainstanz.getIF()[j] && this.datainstanz.getPIF()[j]) {

					this.expansionSize1.addTerm(this.datainstanz.getLowerLimitExpansionSize()[j],
							this.constructionStartPrimaryFacility[j][i]);
					this.expansionSize1.addTerm(1, this.deltaCapacityExpansion[j][i]);

					addEq(this.expansionSize1, this.capacityExpansionAmount[j][i]);
				}

				else if (this.datainstanz.getIF()[j] && datainstanz.getSIF()[j]) {

					this.expansionSize1.addTerm(this.datainstanz.getLowerLimitExpansionSize()[j],
							this.constructionStartSecondaryFacility[j][i]);
					this.expansionSize1.addTerm(1, this.deltaCapacityExpansion[j][i]);

					addEq(this.expansionSize1, this.capacityExpansionAmount[j][i]);
				}
			}
		}

		/**
		 * 5th constraint: b) expansion amount beyond minimum
		 */

		for (int i = 0; i < this.datainstanz.getT(); i++) {
			for (int j = 0; j < this.datainstanz.getF(); j++) {

				this.expansionSize2.clear();

				if (this.datainstanz.getIF()[j] && datainstanz.getPIF()[j]) {

					double expansionBeyondMin = this.datainstanz.getUpperLimitCapacity()[j] - this.datainstanz.getInitialCapacity()
							- this.datainstanz.getLowerLimitExpansionSize()[j];
					this.expansionSize2.addTerm(expansionBeyondMin, this.constructionStartPrimaryFacility[j][i]);

					addGe(this.expansionSize2, this.deltaCapacityExpansion[j][i]);
				}

				else if (this.datainstanz.getIF()[j] && this.datainstanz.getSIF()[j]) {

					double expansionBeyondMin = this.datainstanz.getUpperLimitCapacity()[j] - this.datainstanz.getInitialCapacity()
							- this.datainstanz.getLowerLimitExpansionSize()[j];
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
		for (int i = 0; i < this.datainstanz.getF(); i++) {
			for (int j = 0; j < this.datainstanz.getT(); j++) {
				this.availableCapacity.clear();
				if (this.datainstanz.getIF()[i] && datainstanz.getPIF()[i]) {
					if (j < this.datainstanz.getParameter_monthsToBuildPrimaryFacilities()) {
						addEq(this.availableProductionCapacity[i][j], datainstanz.getInitialCapacity());

					} else {
						this.availableCapacity.addTerm(1, this.availableProductionCapacity[i][j - 1]);

						this.availableCapacity.addTerm(this.datainstanz.getLowerLimitExpansionSize()[i],
								this.constructionStartPrimaryFacility[i][j - this.datainstanz.getParameter_monthsToBuildPrimaryFacilities()]);

						this.availableCapacity.addTerm(1,
								this.deltaCapacityExpansion[i][j - this.datainstanz.getParameter_monthsToBuildPrimaryFacilities()]);

						addEq(this.availableCapacity, this.availableProductionCapacity[i][j]);
					}
				}
				if (datainstanz.getIF()[i] && datainstanz.getSIF()[i]) {
					if (j < this.datainstanz.getParameter_monthsToBuildSecondaryFacilities()) {
						addEq(this.availableProductionCapacity[i][j], this.datainstanz.getInitialCapacity());
					} else {
						this.availableCapacity.addTerm(1, this.availableProductionCapacity[i][j - 1]);

						this.availableCapacity.addTerm(this.datainstanz.getLowerLimitExpansionSize()[i],
								this.constructionStartSecondaryFacility[i][j - this.datainstanz.getParameter_monthsToBuildSecondaryFacilities()]);

						this.availableCapacity.addTerm(1,
								this.deltaCapacityExpansion[i][j - this.datainstanz.getParameter_monthsToBuildSecondaryFacilities()]);

						addEq(this.availableCapacity, this.availableProductionCapacity[i][j]);
					}
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

		// this.massbalanceEquation1.clear();
		this.massbalanceEquation2.clear();
		this.massbalanceEquation3.clear();

		/*for (int i = 0; i < this.F; i++) {
			for (int j = 0; j < this.I; j++) {
				for (int k = 0; k < this.T; k++) {
					for (int m = 0; m < this.F; m++) {
						if (IF[i] && IF[m]) {

							addEq(this.shippedMaterialUnitsSupplierToFacility[j][m][i][k],
									this.shippedMaterialUnitsFacilityToCustomer[j][i][m][k]);

						}
					}
				}
			}
		}*/

		for (int i = 0; i < this.datainstanz.getF(); i++) {
			for (int j = 0; j < this.datainstanz.getI(); j++) {
				if (datainstanz.getIF()[i]) {
					if (datainstanz.getOM()[i][j] || datainstanz.getIM()[i][j]) {
					for (int k = 0; k < this.datainstanz.getT(); k++) {
						// this.massbalanceEquation1.clear();
						this.massbalanceEquation2.clear();
						this.massbalanceEquation3.clear();
						/*
						 * massbalanceEquation1.addTerm(this.materialCoefficient[this.API - 1][i],
						 * this.consumedOrProducedMaterial[j][i][k]);
						 */
						massbalanceEquation2.addTerm(this.datainstanz.getMaterialCoefficient()[j][i], this.consumedOrProducedAPI[i][k]);

						// First equation
						// addEq(this.massbalanceEquation1, this.massbalanceEquation2);

						for (int m = 0; m < this.datainstanz.getF(); m++) {
							// if (IF[m]) {
							// for (int l = 0; l < this.I; l++) {

							if (datainstanz.getOM()[m][j] && datainstanz.getIM()[i][j]) {
								massbalanceEquation3.addTerm(this.datainstanz.getMaterialCoefficient()[this.datainstanz.getAPI() - 1][i],
										this.shippedMaterialUnitsSupplierToFacility[j][m][i][k]);
							}

							else if (datainstanz.getIM()[m][j] && datainstanz.getOM()[i][j]) {

								massbalanceEquation3.addTerm(this.datainstanz.getMaterialCoefficient()[this.datainstanz.getAPI() - 1][i],
										this.shippedMaterialUnitsFacilityToCustomer[j][i][m][k]);
							}
							/*if (OM[i][j] && IM[m][j]) {
								massbalanceEquation3.addTerm(this.materialCoefficient[this.API - 1][i],
										this.shippedMaterialUnitsSupplierToFacility[j][i][m][k]);
							}

							else if (IM[i][j] && OM[m][j]) {

								massbalanceEquation3.addTerm(this.materialCoefficient[this.API - 1][i],
										this.shippedMaterialUnitsFacilityToCustomer[j][m][i][k]);
							}*/
							// }

							// }

							/*
							 * if (k == 0) { System.out.println(this.massbalanceEquation3);
							 * System.out.println( "facility " + (i + 1) + " s/c " + (m + 1) + " material "
							 * + (j + 1)); }
							 */
							addEq(this.shippedMaterialUnitsSupplierToFacility[j][m][i][k],
									this.shippedMaterialUnitsFacilityToCustomer[j][m][i][k]);
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

		// this.capacityRestrictionForProduction.clear();

		for (int i = 0; i < this.datainstanz.getF(); i++) {
			if (datainstanz.getIF()[i]) {
				for (int j = 0; j < this.datainstanz.getT(); j++) {

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

		//this.lowerLimitForProductionPF.clear();
		this.lowerLimitForProductionPF1.clear();
		this.lowerLimitForProductionSF.clear();

		
		  for (int j = 0; j < this.datainstanz.getT(); j++) { for (int i = 0; i < this.datainstanz.getF(); i++) {
		 this.lowerLimitForProductionPF1.clear();
		  this.lowerLimitForProductionSF.clear(); if (this.datainstanz.getIF()[i] && this.datainstanz.getPIF()[i]) {
		  
		  int tau1 = this.datainstanz.getT() - this.datainstanz.getParameter_monthsToBuildPrimaryFacilities(); if (tau1 < 0) { tau1 =
		  0; } for (int k = 0; k < tau1; k++) {
		  
		 this.lowerLimitForProductionPF1.addTerm(this.datainstanz.getLowerLimitProductionAPI()[i],
		 this.constructionStartPrimaryFacility[i][k]);
		  
		 }
		  
		  addLe(this.lowerLimitForProductionPF1, this.consumedOrProducedAPI[i][j]);
		 
		  }
		  
		  else if (datainstanz.getIF()[i] && datainstanz.getSIF()[i]) { int tau2 = this.datainstanz.getT() -
		  this.datainstanz.getParameter_monthsToBuildSecondaryFacilities(); if (tau2 < 0) { tau2 = 0; } for (int k =
		  0; k < tau2; k++) {
		  
		  this.lowerLimitForProductionSF.addTerm(this.datainstanz.getLowerLimitProductionAPI()[i],
		  this.constructionStartSecondaryFacility[i][k]);
		  
		  }
		  
		  addLe(this.lowerLimitForProductionSF, this.consumedOrProducedAPI[i][j]); } }
		  }
		 

		
		  for (int i=0; i<this.datainstanz.getParameter_monthsToBuildPrimaryFacilities(); i++) { for (int j=0;
		  j<this.datainstanz.getF(); j++) { this.lowerLimitForProductionPF1.clear(); if (datainstanz.getIF()[j]&&datainstanz.getPIF()[j])
		  { this.lowerLimitForProductionPF1.addTerm(1,
		  this.consumedOrProducedAPI[j][i]); }
		  addEq(this.lowerLimitForProductionPF1,0); } }
		 

		/*for (int i = (this.monthsToBuildPrimaryFacility); i < this.T; i++) {
			for (int j = 0; j < this.F; j++) {
				if (IF[j] && PIF[j]) {
					this.lowerLimitForProductionPF2.clear();
					this.lowerLimitForProductionPF2.addTerm(this.lowerLimitProductionAPI[j],
							this.constructionStartPrimaryFacility[j][0]);
					addLe(this.lowerLimitForProductionPF2, this.consumedOrProducedAPI[j][i]);
				}
			}
		}*/
	}

	/**
	 * 10th constraint: demand and supply constraint
	 * 
	 * @throws IloException
	 */
	private void addConstraintSupplyAndDemand() throws IloException {

		this.demandAndSupply.clear();

		for (int i = 0; i < this.datainstanz.getF(); i++) {// f
			if (this.datainstanz.getEF()[i]) {
				for (int j = 0; j < this.datainstanz.getT(); j++) {// t
					for (int k = 0; k < this.datainstanz.getI(); k++) {// material i
						demandAndSupply.clear();
						if (this.datainstanz.getOM()[i][k] || datainstanz.getIM()[i][k]) {

							for (int l = 0; l < this.datainstanz.getF(); l++) {
								if (this.datainstanz.getIF()[l] && datainstanz.getOM()[l][k]) {// facility to customer
									demandAndSupply.addTerm(1, this.shippedMaterialUnitsFacilityToCustomer[k][l][i][j]);

								} else if (datainstanz.getIF()[l] && datainstanz.getIM()[l][k]) {// supplier to facility
									demandAndSupply.addTerm(1, this.shippedMaterialUnitsSupplierToFacility[k][i][l][j]);

								}
							}
							double sumDS = this.datainstanz.getSupply()[k][i] + this.datainstanz.getDemand()[k][i][j];

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

		for (int i = 0; i < this.datainstanz.getT(); i++) {
			this.capitalExpenditureConstraint.clear();
			for (int j = 0; j < this.datainstanz.getF(); j++) {
				if (datainstanz.getIF()[j] && datainstanz.getPIF()[j]) {
					this.capitalExpenditureConstraint.addTerm(this.datainstanz.getParameter_setupCostPrimaryFacility(),
							this.constructionStartPrimaryFacility[j][i]);

					double variableCostPF = this.datainstanz.getParameter_monthsToBuildPrimaryFacilities() * this.datainstanz.getParameter_constructionCostPrimaryFacility();
					this.capitalExpenditureConstraint.addTerm(variableCostPF,
							this.constructionStartPrimaryFacility[j][i]);
				}

				else if (datainstanz.getIF()[j] && datainstanz.getSIF()[j]) {
					this.capitalExpenditureConstraint.addTerm(this.datainstanz.getParameter_setupCostSecondaryFacility(),
							this.constructionStartSecondaryFacility[j][i]);

					double variableCostSF = this.datainstanz.getParameter_monthsToBuildSecondaryFacilities()
							* this.datainstanz.getParameter_constructionCostSecondaryFacility();
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

		for (int i = 0; i < this.datainstanz.getT(); i++) {
			budget.clear();
			for (int j = 0; j < this.datainstanz.getF(); j++) {
				if (this.datainstanz.getIF()[j] && datainstanz.getPIF()[j]) {
					for (int k = 0; k < i; k++) {// t<tau
						this.budget.addTerm(this.datainstanz.getParameter_setupCostPrimaryFacility(), this.constructionStartPrimaryFacility[j][k]);

						double variableCostPF = this.datainstanz.getParameter_monthsToBuildPrimaryFacilities()
								* this.datainstanz.getParameter_constructionCostPrimaryFacility();
						this.budget.addTerm(variableCostPF, this.constructionStartPrimaryFacility[j][k]);
						budgetUntilTau = budgetUntilTau + this.datainstanz.getCapitalBudget()[k];
					}
				}

				else if (datainstanz.getIF()[j] && datainstanz.getSIF()[j]) {
					for (int k = 0; k < i; k++) {// t<tau
						this.budget.addTerm(this.datainstanz.getParameter_setupCostSecondaryFacility(),
								this.constructionStartSecondaryFacility[j][k]);

						double variableCostSF = this.datainstanz.getParameter_monthsToBuildSecondaryFacilities()
								* this.datainstanz.getParameter_constructionCostSecondaryFacility();
						this.budget.addTerm(variableCostSF, this.constructionStartSecondaryFacility[j][k]);

						budgetUntilTau = budgetUntilTau + this.datainstanz.getCapitalBudget()[k];
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

		for (int i = 0; i < this.datainstanz.getT(); i++) {
			for (int j = 0; j < this.datainstanz.getF(); j++) {
				this.grossIncomeConstraint.clear();
				if (this.datainstanz.getIF()[j]) {

					this.grossIncomeConstraint.addTerm(-this.datainstanz.getVariableProductionCosts()[j],
							this.consumedOrProducedAPI[j][i]);

					for (int k = 0; k < this.datainstanz.getI(); k++) {
						if (this.datainstanz.getOM()[j][k]) {

							for (int m = 0; m < this.datainstanz.getF(); m++) {
								for (int l = 0; l < this.datainstanz.getI(); l++) {
									if (this.datainstanz.getIM()[m][k] && k == l) {
										// System.out.println("Check gleiches Material: "+(k+1)+" und "+(l+1)+" ?");
										this.grossIncomeConstraint.addTerm(this.datainstanz.getUnitSellingPrice()[k][j],
												this.shippedMaterialUnitsFacilityToCustomer[k][j][m][i]);

									}

								}
							}
						} else if (datainstanz.getIM()[j][k]) {

							for (int m = 0; m < this.datainstanz.getF(); m++) {
								for (int l = 0; l < this.datainstanz.getI(); l++) {
									if (this.datainstanz.getOM()[m][k] && k == l) {
										// System.out.println("Check gleiches Material: " + (k + 1) + " und " + (l + 1)
										// + " ?");

										double costCoefficient = this.datainstanz.getCostInsuranceFreight()[k][m][j]
												+ (this.datainstanz.getCostInsuranceFreight()[k][m][j] * this.datainstanz.getImportDuty()[m][j]);

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
		for (int i = 0; i < this.datainstanz.getT(); i++) {
			for (int j = 0; j < this.datainstanz.getF(); j++) {
				if (datainstanz.getIF()[j] && datainstanz.getPIF()[j]) {
					for (int k = 0; k < this.datainstanz.getT(); k++) {// construction start
														// (tau)
						this.depreciationChargePrimaryFacilities.clear();
						double lowerBound = k + this.datainstanz.getParameter_monthsToBuildPrimaryFacilities();
						double upperBound1 = k + this.datainstanz.getParameter_monthsToBuildPrimaryFacilities() + this.datainstanz.getProjectLife();
						double upperBound = 0;
						if (upperBound1 < this.datainstanz.getT()) {
							upperBound = upperBound1;
						} else {
							upperBound = this.datainstanz.getT();
						}

						if (i >= lowerBound && i <= upperBound) {

							double setupCostPF = this.datainstanz.getParameter_setupCostPrimaryFacility() / this.datainstanz.getProjectLife();
							this.depreciationChargePrimaryFacilities.addTerm(setupCostPF,
									this.constructionStartPrimaryFacility[j][k]);

							double variableCostPF = this.datainstanz.getParameter_monthsToBuildPrimaryFacilities()
									* this.datainstanz.getParameter_constructionCostPrimaryFacility() / this.datainstanz.getProjectLife();
							this.depreciationChargePrimaryFacilities.addTerm(variableCostPF,
									this.constructionStartPrimaryFacility[j][k]);
							/*
							 * if (j==0 && k<10) { System.out.println( "facility " +(j+1)+ " tau "+ (k+1) +
							 * " t " + (i+1)); System.out.println(this.
							 * depreciationChargePrimaryFacilities);}
							 */

							addEq(this.depreciationChargePrimaryFacilities,
									this.depreciationChargePrimaryFacility[j][k][i]);

						}

						else {
							addEq(this.depreciationChargePrimaryFacilities,
									this.depreciationChargePrimaryFacility[j][k][i]);
							/*
							 * if (j==0&& k<10) { System.out.println("facility " +(j+1)+ " tau "+ (k+1) +
							 * " t " + (i+1)); System.out.println(this.
							 * depreciationChargePrimaryFacilities); }
							 */
						}

					}
				}
			}

		}

		// Secondary Facilities
		for (int i = 0; i < this.datainstanz.getT(); i++) {
			for (int j = 0; j < this.datainstanz.getF(); j++) {
				if (datainstanz.getIF()[j] && datainstanz.getSIF()[j]) {
					for (int k = 0; k < this.datainstanz.getT(); k++) {// construction start
														// (tau)
						this.depreciationChargeSecondaryFacilities.clear();
						double lowerBound = k + this.datainstanz.getParameter_monthsToBuildSecondaryFacilities();
						double upperBound1 = k + this.datainstanz.getParameter_monthsToBuildSecondaryFacilities() + this.datainstanz.getProjectLife();
						double upperBound = 0;
						if (upperBound1 < this.datainstanz.getT()) {
							upperBound = upperBound1;
						} else {
							upperBound = this.datainstanz.getT();
						}

						if (i >= lowerBound && i <= upperBound) {

							double setupCostSF = this.datainstanz.getParameter_setupCostSecondaryFacility() / this.datainstanz.getProjectLife();
							this.depreciationChargeSecondaryFacilities.addTerm(setupCostSF,
									this.constructionStartSecondaryFacility[j][k]);

							double variableCostSF = this.datainstanz.getParameter_monthsToBuildSecondaryFacilities()
									* this.datainstanz.getParameter_constructionCostSecondaryFacility() / this.datainstanz.getProjectLife();
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
		for (int j = 0; j < this.datainstanz.getN(); j++) {
			for (int i = 0; i < this.datainstanz.getT(); i++) {

				this.taxableIncomeConstraint.clear();
				for (int k = 0; k < this.datainstanz.getF(); k++) {
					if (datainstanz.getIF()[k] && datainstanz.getFn()[k][j]) {
						this.taxableIncomeConstraint.addTerm(1, this.grossIncome[k][i]);

						/*
						 * for (int l = 0; l < i ; l++) { if(PIF[k]) {
						 * this.taxableIncomeConstraint.addTerm(-1,
						 * this.depreciationChargePrimaryFacility[k][l][i]); } else {
						 * this.taxableIncomeConstraint.addTerm(-1,
						 * this.depreciationChargeSecondaryFacility[k][l][i]); } }
						 */

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

	public boolean solve() throws IloException {

		try {
			return solve(new int[0]);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;

	}

	public boolean solve(int[] numbers) throws IloException, IOException {
		writeMatrix(numbers);

		if (!super.solve()) {
			return false;
		}
		// writeSolution(numbers);
		return true;

	}

	public void writeSolution(int[] numbers, Data instanz)
			throws IloException, IOException, BiffException, WriteException {

		String path = "./logs/_WGP_";

		for (int k : numbers)
			path += k + "_";
		path += "sol.txt";
		File logFile = new File(path);
		logFile.delete();
		logFile.createNewFile();
		FileWriter fstream = new FileWriter(logFile, true);
		BufferedWriter out = new BufferedWriter(fstream);

		out.write("objective value=" + getObjValue() + "\n");
		out.write("variable values\n");

		out.write("\n Decision\n");

		// for writing into the solution file use: out.write ("");
		// for writing into the Excel file transfer decision variable values
		// into result arrays of data instance and then use writeSolution() in
		// ReadAndWrite class

		// y and z
		double yft[][] = new double[this.datainstanz.getF()][this.datainstanz.getT()];
		double zft[][] = new double[this.datainstanz.getF()][this.datainstanz.getT()];

		for (int j = 0; j < this.datainstanz.getF(); j++) {
			for (int k = 0; k < this.datainstanz.getT(); k++) {

				if (this.datainstanz.getIF()[j] && datainstanz.getPIF()[j]) {
					yft[j][k] = (int) getValue(this.constructionStartPrimaryFacility[j][k]);
					zft[j][k] = 0;
					if (getValue(this.constructionStartPrimaryFacility[j][k]) == 1) {
						out.write(" Primary Facility " + (j + 1) + " is build in " + (k + 1) + ". y = "
								+ getValue(this.constructionStartPrimaryFacility[j][k]) + "\n");

					}
				} else if (datainstanz.getIF()[j] && datainstanz.getSIF()[j]) {
					zft[j][k] = getValue(this.constructionStartSecondaryFacility[j][k]);
					yft[j][k] = 0;
					if (getValue(this.constructionStartSecondaryFacility[j][k]) == 1) {

						out.write(" Secondary Facility " + (j + 1) + " is build in " + (k + 1) + ". z = "
								+ getValue(this.constructionStartSecondaryFacility[j][k]) + "\n");
						System.out.println(" Secondary Facility " + (j + 1) + " is build in " + (k + 1) + ". z = "
								+ getValue(this.constructionStartSecondaryFacility[j][k]) + " ");
						System.out.println(zft[j][k]);
					}

				} else {
					zft[j][k] = 0;
					yft[j][k] = 0;
				}
			}
		}

		instanz.setResult_constructionStartPrimaryFacility(yft);
		instanz.setResult_constructionStartSecondaryFacility(zft);

		// TInt
		double[][] TInt = new double[instanz.getN()][instanz.getT()];

		for (int j = 0; j < this.datainstanz.getN(); j++) {
			for (int k = 0; k < this.datainstanz.getT(); k++) {
				TInt[j][k] = getValue(this.taxableIncome[j][k]);
			}
		}
		instanz.setResult_taxableIncome(TInt);

		// GIft
		double[][] GIft = new double[instanz.getF()][instanz.getT()];

		for (int j = 0; j < this.datainstanz.getF(); j++) {
			for (int k = 0; k < this.datainstanz.getT(); k++) {
				if (datainstanz.getIF()[j]) {

					GIft[j][k] = getValue(this.grossIncome[j][k]);
				}

				else {
					GIft[j][k] = 0;
				}
			}
		}
		instanz.setResult_grossIncome(GIft);

		// CEt
		double[] CEt = new double[instanz.getT()];

		for (int k = 0; k < this.datainstanz.getT(); k++) {

			CEt[k] = getValue(this.capitalExpenditure[k]);
		}

		instanz.setResult_capitalExpenditure(CEt);

		// Qft, delta_qft, Xft
		double[][] Qft = new double[instanz.getF()][instanz.getT()];
		double[][] delta_qft = new double[instanz.getF()][instanz.getT()];
		double[][] Xft = new double[instanz.getF()][instanz.getT()];

		for (int i = 0; i < this.datainstanz.getF(); i++) {
			for (int j = 0; j < this.datainstanz.getT(); j++) {
				if (this.datainstanz.getIF()[i]) {
					Qft[i][j] = getValue(this.availableProductionCapacity[i][j]);
					delta_qft[i][j] = getValue(this.deltaCapacityExpansion[i][j]);
					Xft[i][j] = getValue(this.consumedOrProducedAPI[i][j]);
				} else {
					Qft[i][j] = 0;
					delta_qft[i][j] = 0;
					Xft[i][j] = 0;
				}
			}
		}

		instanz.setResult_availableProductionCapacity(Qft);
		instanz.setResult_deltaCapacityExpansion(delta_qft);
		instanz.setResult_consumedOrProducedAPI(Xft);

		// F_ifct and F_isft

		for (int i = 0; i < instanz.getI(); i++) {
			for (int j = 0; j < instanz.getF(); j++) {
				for (int k = 0; k < instanz.getF(); k++) {
					for (int l = 0; l < instanz.getT(); l++) {
						if (datainstanz.getIF()[j]) {
							if (datainstanz.getOM()[j][i] && datainstanz.getIM()[k][i]) {
								if (getValue(this.shippedMaterialUnitsFacilityToCustomer[i][j][k][l]) > 0) {

									out.write("Material " + (i + 1) + " is shipped from facility " + (j + 1)
											+ " to customer " + (k + 1) + " in period " + (l + 1) + " ."
											+ getValue(this.shippedMaterialUnitsFacilityToCustomer[i][j][k][l]) + "\n");
								} else {

									/*
									 * out.write("Material " + (i + 1) + " is NOT shipped from facility " + (j + 1)
									 * + " to customer " + (k + 1) + " in period " + (l + 1) + " ." +
									 * getValue(this.shippedMaterialUnitsFacilityToCustomer[i][j][k][l]) + "\n");
									 */

								}

							} else if (datainstanz.getIM()[j][i] && datainstanz.getOM()[k][i]) {
								if (getValue(this.shippedMaterialUnitsSupplierToFacility[i][k][j][l]) > 0) {

									out.write("Material " + (i + 1) + " is shipped from supplier " + (k + 1)
											+ " to facility " + (j + 1) + " in period " + (l + 1) + " ."
											+ getValue(this.shippedMaterialUnitsSupplierToFacility[i][k][j][l]) + "\n");
								}
							}

						}
					}
				}
			}
		}

		//
		out.close();

		System.out.println("(WGP) wrote sol to file " + path + "\n");

	}

}