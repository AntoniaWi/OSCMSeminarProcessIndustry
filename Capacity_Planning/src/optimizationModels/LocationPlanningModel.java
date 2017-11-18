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

/**
 * Implements the Location Planning Model for the pharmaceutical industry and  and solves it. The model optimizes the location for primary and secondary facilities considering the regulatory factors custom duties and corporate tax.
 *  
 * @author antoniawiggert
 *
 */
public class LocationPlanningModel extends IloCplex {

	private Data datainstanz;

	//Objective 
	private IloObjective objective;

	//Decision variables 
	private IloNumVar[][][][] shippedMaterialUnitsFacilityToCustomer; // F_ifct
	private IloNumVar[][][][] shippedMaterialUnitsSupplierToFacility; // F_isft
	private IloNumVar[][] availableProductionCapacity; // Q_ft
	private IloNumVar[][] taxableIncome; // TI_nt
	private IloNumVar[][] consumedOrProducedAPI; // X_ft
	private IloNumVar[] capitalExpenditure; // CE_t
	private IloNumVar[][] grossIncome; // GI_ft
	private IloNumVar[][] deltaCapacityExpansion; // delta_q_ft
	private IloNumVar[][] capacityExpansionAmount; // q_ft

	private IloIntVar[][] constructionStartPrimaryFacility; // y_ft
	private IloIntVar[][] constructionStartSecondaryFacility; // z_ft

	// Constraints 
	private IloLinearNumExpr numberOfPrimaryFacilities = linearNumExpr();
	private IloLinearNumExpr numberOfSecondaryFacilities = linearNumExpr();
	private IloLinearNumExpr limitationOfConstructionStartsPrimaryFacilities = linearNumExpr();
	private IloLinearNumExpr limitationOfConstructionStartsSecondaryFacilities = linearNumExpr();
	private IloLinearNumExpr expansionSize1 = linearNumExpr();
	private IloLinearNumExpr expansionSize2 = linearNumExpr();
	private IloLinearNumExpr availableCapacity = linearNumExpr();
	private IloLinearNumExpr massbalanceEquation1 = linearNumExpr();
	private IloLinearNumExpr massbalanceEquation2 = linearNumExpr();
	private IloLinearNumExpr demandAndSupply = linearNumExpr();
	private IloLinearNumExpr capitalExpenditureConstraint = linearNumExpr();
	private IloLinearNumExpr budget = linearNumExpr();
	private IloLinearNumExpr grossIncomeConstraint = linearNumExpr();
	private IloLinearNumExpr taxableIncomeConstraint = linearNumExpr();

	/**
	 * Constructor for the here-and-now decision to locate the primary facility optimally. This constructor is called when the company decides to invest in the primary facility. 
	 * 
	 * @param datainstanz - contains all relevant information
	 * @throws IloException
	 * @throws BiffException
	 * @throws WriteException
	 * @throws IOException
	 */
	public LocationPlanningModel(Data datainstanz) throws IloException, BiffException, WriteException, IOException {

		this.datainstanz = datainstanz;

		// calculation of transfer parameter remainingTimeOfClinicalTrials and time
		// horizon T
		int tmp_remainingTime = (this.datainstanz.getParameter_planningHorizon() - this.datainstanz.getCountPeriods())
				* 12;
		this.datainstanz.setRemainingTimeofClinicalTrials(tmp_remainingTime + 12);
		datainstanz
				.setT(datainstanz.getRemainingTimeofClinicalTrials() + datainstanz.getTimeM() + datainstanz.getTimeR());

		ReadAndWrite.writeTransferParameter(datainstanz, 1);
		ReadAndWrite.calculateParameters(datainstanz, 1);

		// Initialization of decision variables
		this.constructionStartPrimaryFacility = new IloIntVar[this.datainstanz.getF()][this.datainstanz.getT()];
		this.constructionStartSecondaryFacility = new IloIntVar[this.datainstanz.getF()][this.datainstanz.getT()];
		this.shippedMaterialUnitsFacilityToCustomer = new IloNumVar[this.datainstanz.getI()][this.datainstanz
				.getF()][this.datainstanz.getF()][this.datainstanz.getT()]; // F_ifct
		this.shippedMaterialUnitsSupplierToFacility = new IloNumVar[this.datainstanz.getI()][this.datainstanz
				.getF()][this.datainstanz.getF()][this.datainstanz.getT()]; // F_isft
		this.availableProductionCapacity = new IloNumVar[this.datainstanz.getF()][this.datainstanz.getT()]; // Q_ft
		this.taxableIncome = new IloNumVar[this.datainstanz.getN()][this.datainstanz.getT()]; // TI_nt
		this.consumedOrProducedAPI = new IloNumVar[this.datainstanz.getF()][this.datainstanz.getT()]; // X_ft
		this.capitalExpenditure = new IloNumVar[this.datainstanz.getT()]; // CE_t
		this.grossIncome = new IloNumVar[this.datainstanz.getF()][this.datainstanz.getT()]; // GI_ft
		this.deltaCapacityExpansion = new IloNumVar[this.datainstanz.getF()][this.datainstanz.getT()]; // delta_q_ft
		this.capacityExpansionAmount = new IloNumVar[this.datainstanz.getF()][this.datainstanz.getT()]; // q_ft
	}

	/**
	 * Constructor for the wait-and-see decision to review the allocation and number of secondary facilities at the end of the clinical trials when there was already a location planning for the primary facility 
	 * 
	 * @param datainstanz - contains all relevant information
	 * @param primaryFacility - which is fixed when construction started earlier (here-and-now decision during clinical trials)  
	 * @throws IloException
	 * @throws BiffException
	 * @throws WriteException
	 * @throws IOException
	 */
	public LocationPlanningModel(Data datainstanz, int primaryFacility)
			throws IloException, BiffException, WriteException, IOException {

		this.datainstanz = datainstanz;

		int numberOfConstructionPeriods = DecisionReviewModel
				.countTrueValuesInArray(datainstanz.getInvestmentDecisionPrimaryFacility());

		int tmp_remainingTime = numberOfConstructionPeriods * 12;
		this.datainstanz.setRemainingTimeofClinicalTrials(tmp_remainingTime);
		datainstanz
				.setT(datainstanz.getRemainingTimeofClinicalTrials() + datainstanz.getTimeM() + datainstanz.getTimeR());

		ReadAndWrite.writeTransferParameter(datainstanz, 2);
		ReadAndWrite.calculateParameters(datainstanz, 2);

		// Initialization of decision variables
		this.constructionStartPrimaryFacility = new IloIntVar[this.datainstanz.getF()][this.datainstanz.getT()];// y_ft
		this.constructionStartSecondaryFacility = new IloIntVar[this.datainstanz.getF()][this.datainstanz.getT()];// z_ft
		this.shippedMaterialUnitsFacilityToCustomer = new IloNumVar[this.datainstanz.getI()][this.datainstanz
				.getF()][this.datainstanz.getF()][this.datainstanz.getT()]; // F_ifct
		this.shippedMaterialUnitsSupplierToFacility = new IloNumVar[this.datainstanz.getI()][this.datainstanz
				.getF()][this.datainstanz.getF()][this.datainstanz.getT()]; // F_isft
		this.availableProductionCapacity = new IloNumVar[this.datainstanz.getF()][this.datainstanz.getT()]; // Q_ft
		this.taxableIncome = new IloNumVar[this.datainstanz.getN()][this.datainstanz.getT()]; // TI_nt
		this.consumedOrProducedAPI = new IloNumVar[this.datainstanz.getF()][this.datainstanz.getT()]; // X_ft
		this.capitalExpenditure = new IloNumVar[this.datainstanz.getT()]; // CE_t
		this.grossIncome = new IloNumVar[this.datainstanz.getF()][this.datainstanz.getT()]; // GI_ft
		this.deltaCapacityExpansion = new IloNumVar[this.datainstanz.getF()][this.datainstanz.getT()]; // delta_q_ft
		this.capacityExpansionAmount = new IloNumVar[this.datainstanz.getF()][this.datainstanz.getT()]; // q_ft
	}

	/**
	 * Runs the Location Planning Model for the first investment decision: locations for primary and secondary facilities are optimized.
	 * 
	 * @throws IloException
	 * @throws BiffException
	 * @throws IOException
	 * @throws RowsExceededException
	 * @throws WriteException
	 */
	public void run() throws IloException, BiffException, IOException, RowsExceededException, WriteException {

		this.build();
		this.solve();
		this.writeSolution(new int[] { 1, 2, 3 }, datainstanz);
		//ReadAndWrite.writeSolutionInResultFile(this.datainstanz);
		// lpm.ergebnisschreibenRobust(lpm);

	}

	/**
	 * Runs the Location Planning Model at the end of the clinical trials when the outcome is positive and known: the location for the primary facility is already fixed, only the locations and the number of secondary facilities are optimized.
	 * 
	 * @param primaryFacility
	 * @throws IloException
	 * @throws BiffException
	 * @throws IOException
	 * @throws RowsExceededException
	 * @throws WriteException
	 */
	public void run(int primaryFacility)
			throws IloException, BiffException, IOException, RowsExceededException, WriteException {

		this.build(primaryFacility);
		this.solve();
		this.writeSolution(new int[] { 1, 2, 3, 4 }, datainstanz);
		//ReadAndWrite.writeSolutionInResultFile(this.datainstanz);

	}

	/**
	 * Builds the Location Planning Model for the first investment decision
	 * 
	 * @throws IloException
	 */
	public void build() throws IloException {
		long start = System.currentTimeMillis();

		/* Variables */
		addDecisionVars();

		/* Objective */
		addObjective();

		/* Constraints */
		// 1st constraint
		this.addConstraintNumberOfPrimaryFacilities();
		this.addConstraintNumberOfSecondaryFacilities();
		// 2nd constraint
		this.addConstraintOneConstructionDuringPlanningHorizonPF();
		this.addConstraintOneConstructionDuringPlanningHorizonSF();
		// 5th constraint
		this.addConstraintExpansionSize();
		// 6th constraint
		this.addConstraintAvailableCapacity();
		// 7th constraint
		this.addConstraintsMassBalanceEquation();
		// 8th constraint
		this.addConstraintCapacityRestrictionForProduction();
		// 10th constraint
		this.addConstraintSupplyAndDemand();
		// 11th constraint
		this.addConstraintCapitalExpenditure();
		// 12th constraint
		this.addConstraintBudgetConstraint();
		// 13th constraint
		this.addConstraintGrossIncome();
		// 15th constraint
		this.addConstraintTaxableIncome();

		// String path = "./logs/model.lp"; exportModel(path);

		System.out.println(
				"(WGP) complete rebuild finished, dur=" + (System.currentTimeMillis() - start) + " milli sec\n");
	}

	/**
	 * Builds the Location Planning Model at the end of the clinical trials with positive outcome, when the location for the primary facility is already known and fixed. 
	 * 
	 * @param primaryFacility - which is fixed as construction has already started
	 *            
	 * @throws IloException
	 */
	public void build(int primaryFacility) throws IloException {
		long start = System.currentTimeMillis();

		/* Variables */
		addDecisionVars();

		/* Objective */
		addObjective();

		/* Constraints */
		// 1st constraint
		this.addConstraintFixPrimaryFacility(primaryFacility);
		this.addConstraintNumberOfPrimaryFacilities();
		this.addConstraintNumberOfSecondaryFacilities();
		// 2nd constraint
		this.addConstraintOneConstructionDuringPlanningHorizonPF();
		this.addConstraintOneConstructionDuringPlanningHorizonSF();
		// 5th constraint
		this.addConstraintExpansionSize();
		// 6th constraint
		this.addConstraintAvailableCapacity();
		// 7th constraint
		this.addConstraintsMassBalanceEquation();
		// 8th constraint
		this.addConstraintCapacityRestrictionForProduction();
		// 10th constraint
		this.addConstraintSupplyAndDemand();
		// 11th constraint
		this.addConstraintCapitalExpenditure();
		// 12th constraint
		this.addConstraintBudgetConstraint();
		// 13th constraint
		this.addConstraintGrossIncome();
		// 15th constraint
		this.addConstraintTaxableIncome();

		System.out.println(
				"(WGP) complete rebuild finished, dur=" + (System.currentTimeMillis() - start) + " milli sec\n");
	}

	/**
	 * Initialization of decision variables
	 * 
	 * @throws IloException
	 */
	public void addDecisionVars() throws IloException {
		for (int i = 0; i < datainstanz.getF(); i++) {// f
			for (int k = 0; k < datainstanz.getF(); k++) {// c,s
				for (int n = 0; n < datainstanz.getN(); n++) {
					for (int j = 0; j < datainstanz.getT(); j++) {// t
						for (int m = 0; m < datainstanz.getT(); m++) {// tau
							for (int l = 0; l < datainstanz.getI(); l++) {// i
								if (datainstanz.getIF()[i] && datainstanz.getPIF()[i]) {
									this.constructionStartPrimaryFacility[i][j] = intVar(0, 1);

								} else if (datainstanz.getIF()[i] && datainstanz.getSIF()[i]) {
									this.constructionStartSecondaryFacility[i][j] = intVar(0, 1);

								}
								this.capacityExpansionAmount[i][j] = numVar(0, 1000000);
								this.shippedMaterialUnitsFacilityToCustomer[l][i][k][j] = numVar(0, 1000000);
								this.shippedMaterialUnitsSupplierToFacility[l][k][i][j] = numVar(0, 1000000);
								this.availableProductionCapacity[i][j] = numVar(0, 1000000);
								this.taxableIncome[n][j] = numVar(0, 1000000000);
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

	/**
	 * Objective function
	 * 
	 * @throws IloException
	 */
	public void addObjective() throws IloException {

		IloLinearNumExpr expr = linearNumExpr();

		for (int i = 0; i < this.datainstanz.getT(); i++) {
			double discountTerm = 1 / Math.pow((1 + this.datainstanz.getParameter_discountFactor_location()), (i + 1));
			for (int j = 0; j < this.datainstanz.getF(); j++) {
				if (datainstanz.getIF()[j]) {
					expr.addTerm(discountTerm, this.grossIncome[j][i]);

				}
			}
		}

		for (int i = 0; i < this.datainstanz.getT(); i++) {

			double discountTerm = -1 / Math.pow(1 + datainstanz.getParameter_discountFactor_location(), (i + 1));

			expr.addTerm(discountTerm, this.capitalExpenditure[i]);

		}

		for (int i = 0; i < this.datainstanz.getT(); i++) {
			double discountTerm = 1 / Math.pow(1 + datainstanz.getParameter_discountFactor_location(), (i + 1));
			for (int k = 0; k < this.datainstanz.getN(); k++) {

				double taxHelp = discountTerm * datainstanz.getCorporateTax()[k];
				expr.addTerm(-taxHelp, this.taxableIncome[k][i]);
			}
		}

		objective = addMaximize();
		objective.setExpr(expr);

	}



	/**
	 * Constraint: fix primary facility
	 * 
	 * @throws IloException
	 */
	public void addConstraintFixPrimaryFacility(int f) throws IloException {

		addEq(this.constructionStartPrimaryFacility[f][0], 1);

	}

	/**
	 * Constraint: choose exactly one facility as primary facility
	 * 
	 * @throws IloException
	 */
	public void addConstraintNumberOfPrimaryFacilities() throws IloException {

		this.numberOfPrimaryFacilities.clear();

		for (int i = 0; i < datainstanz.getF(); i++) {
			if (datainstanz.getIF()[i] && datainstanz.getPIF()[i]) {

				this.numberOfPrimaryFacilities.addTerm(1, this.constructionStartPrimaryFacility[i][0]);

			}
		}

		addEq(this.numberOfPrimaryFacilities, 1);

	}
	
	/**
	 * Constraint: choose at least one facility as secondary facility
	 * 
	 * @throws IloException
	 */

	public void addConstraintNumberOfSecondaryFacilities() throws IloException {

		this.numberOfSecondaryFacilities.clear();

		for (int i = 0; i < this.datainstanz.getF(); i++) {
			if (datainstanz.getIF()[i] && datainstanz.getSIF()[i]) {
				this.numberOfSecondaryFacilities.addTerm(1,
						this.constructionStartSecondaryFacility[i][this.datainstanz
								.getMonthsToBuildPrimaryFacilities_location()
								- this.datainstanz.getMonthsToBuildSecondaryFacilities_location()]);

			}

		}

		addGe(this.numberOfSecondaryFacilities, 1);
	}

	/**
	 * Constraint: start exactly once the construction during the planning horizon
	 * for primary facilities
	 * 
	 * @throws IloException
	 */
	public void addConstraintOneConstructionDuringPlanningHorizonPF() throws IloException {

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
	
	/**
	 * Constraint: start construction for secondary facilities not more
	 * than once during the planning horizon
	 * 
	 * @throws IloException
	 */
	public void addConstraintOneConstructionDuringPlanningHorizonSF() throws IloException {

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
	 * Constraint: expansion amount 
	 * 
	 * @throws IloException
	 */
	public void addConstraintExpansionSize() throws IloException {

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

		
		 //expansion amount beyond minimum
		 

		for (int i = 0; i < this.datainstanz.getT(); i++) {
			for (int j = 0; j < this.datainstanz.getF(); j++) {

				this.expansionSize2.clear();

				if (this.datainstanz.getIF()[j] && datainstanz.getPIF()[j]) {

					double expansionBeyondMin = this.datainstanz.getUpperLimitCapacity()[j]
							- this.datainstanz.getInitialCapacity() - this.datainstanz.getLowerLimitExpansionSize()[j];
					this.expansionSize2.addTerm(expansionBeyondMin, this.constructionStartPrimaryFacility[j][i]);

					addGe(this.expansionSize2, this.deltaCapacityExpansion[j][i]);
				}

				else if (this.datainstanz.getIF()[j] && this.datainstanz.getSIF()[j]) {

					double expansionBeyondMin = this.datainstanz.getUpperLimitCapacity()[j]
							- this.datainstanz.getInitialCapacity() - this.datainstanz.getLowerLimitExpansionSize()[j];
					this.expansionSize2.addTerm(expansionBeyondMin, this.constructionStartSecondaryFacility[j][i]);

					addGe(this.expansionSize2, this.deltaCapacityExpansion[j][i]);
				}

			}
		}

	}

	/**
	 * Constraint: available capacity
	 * 
	 * @throws IloException
	 */
	public void addConstraintAvailableCapacity() throws IloException {

		this.availableCapacity.clear();
		for (int i = 0; i < this.datainstanz.getF(); i++) {
			for (int j = 0; j < this.datainstanz.getT(); j++) {
				this.availableCapacity.clear();
				if (this.datainstanz.getIF()[i] && datainstanz.getPIF()[i]) {
					if (j < this.datainstanz.getMonthsToBuildPrimaryFacilities_location()) {
						addEq(this.availableProductionCapacity[i][j], datainstanz.getInitialCapacity());

					} else {
						this.availableCapacity.addTerm(1, this.availableProductionCapacity[i][j - 1]);

						this.availableCapacity.addTerm(this.datainstanz.getLowerLimitExpansionSize()[i],
								this.constructionStartPrimaryFacility[i][j
										- this.datainstanz.getMonthsToBuildPrimaryFacilities_location()]);

						this.availableCapacity.addTerm(1, this.deltaCapacityExpansion[i][j
								- this.datainstanz.getMonthsToBuildPrimaryFacilities_location()]);

						addEq(this.availableCapacity, this.availableProductionCapacity[i][j]);
					}
				}
				if (datainstanz.getIF()[i] && datainstanz.getSIF()[i]) {
					if (j < this.datainstanz.getMonthsToBuildSecondaryFacilities_location()) {
						addEq(this.availableProductionCapacity[i][j], this.datainstanz.getInitialCapacity());
					} else {
						this.availableCapacity.addTerm(1, this.availableProductionCapacity[i][j - 1]);

						this.availableCapacity.addTerm(this.datainstanz.getLowerLimitExpansionSize()[i],
								this.constructionStartSecondaryFacility[i][j
										- this.datainstanz.getMonthsToBuildSecondaryFacilities_location()]);

						this.availableCapacity.addTerm(1, this.deltaCapacityExpansion[i][j
								- this.datainstanz.getMonthsToBuildSecondaryFacilities_location()]);

						addEq(this.availableCapacity, this.availableProductionCapacity[i][j]);
					}
				}
			}
		}

	}

	/**
	 * Constraint: mass-balance equation
	 * 
	 * @throws IloException
	 */
	public void addConstraintsMassBalanceEquation() throws IloException {

		this.massbalanceEquation1.clear();
		this.massbalanceEquation2.clear();

		for (int i = 0; i < this.datainstanz.getF(); i++) {
			for (int j = 0; j < this.datainstanz.getI(); j++) {
				if (datainstanz.getIF()[i]) {
					if (datainstanz.getOM()[i][j] || datainstanz.getIM()[i][j]) {
						for (int k = 0; k < this.datainstanz.getT(); k++) {
							this.massbalanceEquation1.clear();
							this.massbalanceEquation2.clear();
							
							massbalanceEquation1.addTerm(this.datainstanz.getMaterialCoefficient()[j][i],
									this.consumedOrProducedAPI[i][k]);


							for (int m = 0; m < this.datainstanz.getF(); m++) {
						

								if (datainstanz.getOM()[m][j] && datainstanz.getIM()[i][j]) {
									massbalanceEquation2.addTerm(
											this.datainstanz.getMaterialCoefficient()[this.datainstanz.getAPI() - 1][i],
											this.shippedMaterialUnitsSupplierToFacility[j][m][i][k]);
								}

								else if (datainstanz.getIM()[m][j] && datainstanz.getOM()[i][j]) {

									massbalanceEquation2.addTerm(
											this.datainstanz.getMaterialCoefficient()[this.datainstanz.getAPI() - 1][i],
											this.shippedMaterialUnitsFacilityToCustomer[j][i][m][k]);
								}
								
								addEq(this.shippedMaterialUnitsSupplierToFacility[j][m][i][k],
										this.shippedMaterialUnitsFacilityToCustomer[j][m][i][k]);
							}
							
							addEq(this.massbalanceEquation1, this.massbalanceEquation2);

						}

					}

				}
			}
		}
	}

	/**
	 * Constraint: capacity restriction for production
	 * 
	 * @throws IloException
	 */
	public void addConstraintCapacityRestrictionForProduction() throws IloException {


		for (int i = 0; i < this.datainstanz.getF(); i++) {
			if (datainstanz.getIF()[i]) {
				for (int j = 0; j < this.datainstanz.getT(); j++) {

					addLe(this.consumedOrProducedAPI[i][j], this.availableProductionCapacity[i][j]);

				}
			}
		}

	}

	

	/**
	 * Constraint: demand fulfillment and supply limit
	 * 
	 * @throws IloException
	 */
	public void addConstraintSupplyAndDemand() throws IloException {

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
	 * Constraint: capital expenditure 
	 * 
	 * @throws IloException
	 */
	public void addConstraintCapitalExpenditure() throws IloException {

		this.capitalExpenditureConstraint.clear();

		for (int i = 0; i < this.datainstanz.getT(); i++) {
			this.capitalExpenditureConstraint.clear();
			for (int j = 0; j < this.datainstanz.getF(); j++) {
				if (datainstanz.getIF()[j] && datainstanz.getPIF()[j]) {
					this.capitalExpenditureConstraint.addTerm(this.datainstanz.getParameter_setupCostPrimaryFacility(),
							this.constructionStartPrimaryFacility[j][i]);

					double variableCostPF = this.datainstanz.getMonthsToBuildPrimaryFacilities_location()
							* this.datainstanz.getConstructionCostPrimaryFacility_location();
					this.capitalExpenditureConstraint.addTerm(variableCostPF,
							this.constructionStartPrimaryFacility[j][i]);
				}

				else if (datainstanz.getIF()[j] && datainstanz.getSIF()[j]) {
					this.capitalExpenditureConstraint.addTerm(
							this.datainstanz.getParameter_setupCostSecondaryFacility(),
							this.constructionStartSecondaryFacility[j][i]);

					double variableCostSF = this.datainstanz.getMonthsToBuildSecondaryFacilities_location()
							* this.datainstanz.getConstructionCostSecondaryFacility_location();
					this.capitalExpenditureConstraint.addTerm(variableCostSF,
							this.constructionStartSecondaryFacility[j][i]);
				}

			}
			addEq(this.capitalExpenditureConstraint, this.capitalExpenditure[i]);
		}

	}

	/**
	 * Constraint: budget limitation
	 * 
	 * @throws IloException
	 */
	public void addConstraintBudgetConstraint() throws IloException {
		double budgetUntilTau = 0;
		this.budget.clear();

		for (int i = 0; i < this.datainstanz.getT(); i++) {
			budget.clear();
			for (int j = 0; j < this.datainstanz.getF(); j++) {
				if (this.datainstanz.getIF()[j] && datainstanz.getPIF()[j]) {
					for (int k = 0; k < i; k++) {// t<tau
						this.budget.addTerm(this.datainstanz.getParameter_setupCostPrimaryFacility(),
								this.constructionStartPrimaryFacility[j][k]);

						double variableCostPF = this.datainstanz.getMonthsToBuildPrimaryFacilities_location()
								* this.datainstanz.getConstructionCostPrimaryFacility_location();
						this.budget.addTerm(variableCostPF, this.constructionStartPrimaryFacility[j][k]);
						budgetUntilTau = budgetUntilTau + this.datainstanz.getCapitalBudget()[k];
					}
				}

				else if (datainstanz.getIF()[j] && datainstanz.getSIF()[j]) {
					for (int k = 0; k < i; k++) {// t<tau
						this.budget.addTerm(this.datainstanz.getParameter_setupCostSecondaryFacility(),
								this.constructionStartSecondaryFacility[j][k]);

						double variableCostSF = this.datainstanz.getMonthsToBuildSecondaryFacilities_location()
								* this.datainstanz.getConstructionCostSecondaryFacility_location();
						this.budget.addTerm(variableCostSF, this.constructionStartSecondaryFacility[j][k]);

						budgetUntilTau = budgetUntilTau + this.datainstanz.getCapitalBudget()[k];
					}
				}
			}
			addLe(this.budget, budgetUntilTau);
		}
	}

	/**
	 * Constraint: gross income 
	 * 
	 * @throws IloException
	 */
	public void addConstraintGrossIncome() throws IloException {

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
										this.grossIncomeConstraint.addTerm(
												this.datainstanz.getUnitSellingPrice()[k][j][i],
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
												+ (this.datainstanz.getCostInsuranceFreight()[k][m][j]
														* this.datainstanz.getImportDuty()[m][j]);

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
	 * Constraint: taxable income
	 * 
	 * @throws IloException
	 */
	public void addConstraintTaxableIncome() throws IloException {

		this.taxableIncomeConstraint.clear();
		for (int j = 0; j < this.datainstanz.getN(); j++) {
			for (int i = 0; i < this.datainstanz.getT(); i++) {

				this.taxableIncomeConstraint.clear();
				for (int k = 0; k < this.datainstanz.getF(); k++) {
					if (datainstanz.getIF()[k] && datainstanz.getFn()[k][j]) {
						this.taxableIncomeConstraint.addTerm(1, this.grossIncome[k][i]);

					}

				}

				addLe(this.taxableIncomeConstraint, this.taxableIncome[j][i]);

			}
		}

	}

	/**
	 * Writes model in log-file
	 * @param numbers
	 * @throws IloException
	 */
	public void writeMatrix(int[] numbers) throws IloException {
		String path = "./logs/model.lp";

		exportModel(path);

	}

	/**
	 * Solves model 
	 */
	public boolean solve() throws IloException {

		try {
			return solve(new int[0]);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;

	}
	
	/**
	 * Solves model and give result in int numbers
	 * @param numbers
	 * @return
	 * @throws IloException
	 * @throws IOException
	 */

	public boolean solve(int[] numbers) throws IloException, IOException {
		writeMatrix(numbers);

		if (!super.solve()) {
			return false;
		}

		return true;

	}

	/**
	 * Writes solution in log-file and saves result in result-parameters
	 * @param numbers
	 * @param instanz
	 * @throws IloException
	 * @throws IOException
	 * @throws BiffException
	 * @throws WriteException
	 */
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
		instanz.setResult_netPresentValue(getObjValue());

		out.write("variable values\n");

		out.write("\n Decision\n");



		// y and z
		double yft[][] = new double[this.datainstanz.getF()][this.datainstanz.getT()];
		double zft[][] = new double[this.datainstanz.getF()][this.datainstanz.getT()];

		for (int j = 0; j < this.datainstanz.getF(); j++) {
			for (int k = 0; k < this.datainstanz.getT(); k++) {

				if (this.datainstanz.getIF()[j] && datainstanz.getPIF()[j]) {
					yft[j][k] = (int) (this.getValue(this.constructionStartPrimaryFacility[j][k]) + 0.1);
					zft[j][k] = 0;
					if (getValue(this.constructionStartPrimaryFacility[j][k]) > 0) {
						out.write(" Primary Facility " + (j + 1) + " is build in " + (k + 1) + ". y = " + yft[j][k]
								+ "\n");

					}
				} else if (datainstanz.getIF()[j] && datainstanz.getSIF()[j]) {
					zft[j][k] = (int) (this.getValue(this.constructionStartSecondaryFacility[j][k]) + 0.1);
					yft[j][k] = 0;
					if (getValue(this.constructionStartSecondaryFacility[j][k]) > 0) {

						out.write(" Secondary Facility " + (j + 1) + " is build in " + (k + 1) + ". z = " + zft[j][k]
								+ "\n");

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
		double[][] qft = new double[instanz.getF()][instanz.getT()];

		for (int i = 0; i < this.datainstanz.getF(); i++) {
			for (int j = 0; j < this.datainstanz.getT(); j++) {
				if (this.datainstanz.getIF()[i]) {
					Qft[i][j] = getValue(this.availableProductionCapacity[i][j]);
					delta_qft[i][j] = getValue(this.deltaCapacityExpansion[i][j]);
					Xft[i][j] = getValue(this.consumedOrProducedAPI[i][j]);
					qft[i][j] = getValue(this.capacityExpansionAmount[i][j]);
				} else {
					Qft[i][j] = 0;
					delta_qft[i][j] = 0;
					Xft[i][j] = 0;
					qft[i][j] = 0;
				}
			}
		}

		instanz.setResult_availableProductionCapacity(Qft);
		instanz.setResult_deltaCapacityExpansion(delta_qft);
		instanz.setResult_consumedOrProducedAPI(Xft);
		instanz.setResult_capacityExpansionAmount(qft);

		// F_ifct and F_isft
		double[][][][] Fifct = new double[instanz.getI()][instanz.getF()][instanz.getF()][instanz.getT()];
		double[][][][] Fisft = new double[instanz.getI()][instanz.getF()][instanz.getF()][instanz.getT()];
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

									Fifct[i][j][k][l] = getValue(
											this.shippedMaterialUnitsFacilityToCustomer[i][j][k][l]);

								} else {

									Fifct[i][j][k][l] = 0;

								}

							} else if (datainstanz.getIM()[j][i] && datainstanz.getOM()[k][i]) {
								if (getValue(this.shippedMaterialUnitsSupplierToFacility[i][k][j][l]) > 0) {

									out.write("Material " + (i + 1) + " is shipped from supplier " + (k + 1)
											+ " to facility " + (j + 1) + " in period " + (l + 1) + " ."
											+ getValue(this.shippedMaterialUnitsSupplierToFacility[i][k][j][l]) + "\n");

									Fisft[i][k][j][l] = getValue(
											this.shippedMaterialUnitsSupplierToFacility[i][k][j][l]);
								}

								else {
									Fisft[i][k][j][l] = 0;
								}
							}

							else {
								Fisft[i][k][j][l] = 0;
								Fifct[i][j][k][l] = 0;
							}

						}
					}
				}
			}
		}

		instanz.setResult_shippedMaterialUnitsFacilityToCustomer(Fifct);
		instanz.setResult_shippedMaterialUnitsSupplierToFacility(Fisft);

		
		out.close();

		System.out.println("(WGP) wrote sol to file " + path + "\n");

	}

}