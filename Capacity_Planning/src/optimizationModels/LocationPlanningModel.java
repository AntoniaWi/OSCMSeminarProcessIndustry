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
    private double discountfactor;
    
    //Transfer parameter
    private int remainingTimeOfClinicalTrials;//T*-t*									delta_t*
    
    


    /* Objective */
    private IloObjective objective;

    /* Decision variables */
    private IloNumVar [][][][] shippedMaterialUnitsFacilityToCustomer;
    private IloNumVar [][][][] shippedMaterialUnitsSupplierToFacility;
    private IloNumVar [][][] depreciationChargePrimaryFacility;		
    private IloNumVar [][][] depreciationChargeSecondaryFacility;
    private IloNumVar [][] availableProductionCapacity;
    private IloNumVar [][] taxableIncome;
    private IloNumVar [][][] consumedOrProducedMaterial;
    private IloNumVar [][] consumedOrProducedAPI;
    
    private IloIntVar[][] constructionStartPrimaryFacility;
    private IloIntVar[][] constructionStartSecondaryFacility;
    
    

    /* Constraints */
    private IloLinearNumExpr numberOfPrimaryFacilities = linearNumExpr();
    private IloLinearNumExpr numberOfSecondaryFacilities = linearNumExpr();
    private IloLinearNumExpr limitationOfConstructionStartsPrimaryFacilities = linearNumExpr();
    private IloLinearNumExpr limitationOfConstructionStartsSecondaryFacilities = linearNumExpr();
    private IloLinearNumExpr noDoubleOccupationOfFacilities = linearNumExpr();
    private IloLinearNumExpr capacityExpansionOnlyIfPlanned = linearNumExpr();
    private IloLinearNumExpr mimimumExpansion = linearNumExpr();
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
        i=datainstanz.getI(); 
        f=datainstanz.getF(); 
        g=datainstanz.getG(); 
        s=datainstanz.getS();
        c=datainstanz.getC(); 
        t=datainstanz.getT();
        tau=datainstanz.getTau();
        n=datainstanz.getN();
       
        
        //Sets 
        
        
        // Daten
        capitalBudget=datainstanz.getCapitalBudget();
        capitalExpenditure=datainstanz.getCapitalExpenditure();
        costInsuranceFreight=datainstanz.getCostInsuranceFreight();
        demand=datainstanz.getDemand();
        importDuty=datainstanz.getImportDuty();
        	projectLife=datainstanz.getProjectLife();
        variableProductionCostsPrimaryFacility=datainstanz.getVariableProductionCostsPrimaryFacility();
        variableProductionCostsSecondaryFacility=datainstanz.getVariableProductionCostsSecondaryFacility();
        unitSellingPrice=datainstanz.getUnitSellingPrice();
        lowerLimitExpansionSize=datainstanz.getLowerLimitExpansionSize();
        initialCapacity=datainstanz.getInitialCapacity();
        upperLimitCapacity=datainstanz.getUpperLimitCapacity();
        supply=datainstanz.getSupply();
        corporateTax=datainstanz.getCorporateTax();
        lowerLimitProductionAPI=datainstanz.getLowerLimitProductionAPI();
        API=datainstanz.getAPI();
        materialCoefficient=datainstanz.getMaterialCoefficient();
        capacityExpansionAmount=datainstanz.getCapacityExpansionAmount();
        discountfactor=datainstanz.getParameter_discountFactor();
    
        //Transfer parameter
        remainingTimeOfClinicalTrials=datainstanz.getRemainingTimeOfClinicalTrials();						
    
       

    }
    
    public static void main(String[] args) throws IloException, BiffException, IOException, RowsExceededException, WriteException {
    	
	//boolean robust1=true;
    	Data instanz= new Data();
    	
   // 	instanz.DatenEinlesen(instanz,robust1);
    	
    	LocationPlanningModel lpm = new LocationPlanningModel(instanz);
    	
    	   	
   	lpm.build();
   	lpm.solve();
   	lpm.writeSolution(new int[] { 1, 2, 3 }, false);
   	//lpm.ergebnisschreibenRobust(lpm);
       }

    public void build() throws IloException {
	long start = System.currentTimeMillis();

	/* Variables */
	addVarsX();
	
	/* Objective */
	addObjective();

	/* Constraints */
	
	
	String path = "./logs/model.lp";
	exportModel(path);
	

	System.out.println("(WGP) complete rebuild finished, dur="
		+ (System.currentTimeMillis() - start) + " milli sec\n");
    }

	// add Decision variables
    private void addVarsX() throws IloException {
		for(int i=0;i<f;i++) {
			for (int j=0;j<t;j++) {
				constructionStartPrimaryFacility[i][j] = intVar(0, 1);
				constructionStartSecondaryFacility[i][j]=intVar(0, 1);
			}
		}
					
		
	}

	
    

    private void addObjective() throws IloException {

	IloLinearNumExpr expr = linearNumExpr();
	IloLinearNumExpr obj = linearNumExpr();
	

	
	for (int i = 0; i < this.f; i++) {
	     
	     for (int j = 0; j < this.t; j++) {
	    	 
		 
		  //expr.addTerm(1/Math.pow(1+this.discountfactor,j) );
		
		    
		
		  

		
	    }
	     
	     

	 
	}
	
	
	objective = addMaximize();

	objective.setExpr(expr);
	System.out.println(objective);

	


	
    }


    private void addConstraintNumberOfPrimaryFacilities() throws IloException {
	
	
	    for (int i = 0; i < this.f; i++) {
		this.numberOfPrimaryFacilities.addTerm(1, this.constructionStartPrimaryFacility[i][1]);
	    }
	    addEq(this.numberOfPrimaryFacilities, 1);
	  
	}
    
    private void addConstraintNumberOfSecondaryFacilities() throws IloException {
    	
    	
	    for (int i = 0; i < this.f; i++) {
		this.numberOfPrimaryFacilities.addTerm(1, this.constructionStartPrimaryFacility[i][1]);
	    }
	    addEq(this.numberOfPrimaryFacilities, 1);
	  
	}
    
    
    private void addConstraintLocalContent() throws IloException {

	IloLinearNumExpr rhs = linearNumExpr();
	
	double hilfsvariable1;
	double hilfsvariable2;
	   
	for (int i=0; i<s; i++){
	    localContent.clear();
	    rhs.clear();
		for (int k = 0; k < v; k++) {
		    for (int j = 0; j < l; j++) {
			
			hilfsvariable1 = anrechenbarerLocalContent[k][j]*kosten[k][j]*wechselkurs[i][j];
			hilfsvariable2= kosten[k][j]*wechselkurs[i][j];
			localContent.addTerm(hilfsvariable1, x[k][j]);
			rhs.addTerm(hilfsvariable2,x[k][j]);
		
	    }
	
		
	
		}
	addGe(localContent, prod(lcr,rhs));
	   
	    
		 
	
	
    }
    }
    private void addConstraintAngebot() throws IloException {
	
	
	    for (int j = 0; j < v; j++) {
		for (int k = 0; k < l; k++) {

		    this.addLe(x[j][k], angebot[j][k]);
		    
		}
	    }
	
    }

    public void writeMatrix(int[]numbers) throws IloException {
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
	//writeSolution(numbers, false);
	return true;
    }
    

    public void writeSolution(int[] numbers, boolean includingZeros)
	    throws IloException, IOException {
	
	    String path = "./logs/_WGP_";
	    
	    //for (int k : numbers)
		//path += k + "_";
	    path += "sol.txt";
	    File logFile = new File(path);
	    logFile.delete();
	    logFile.createNewFile();
	    FileWriter fstream = new FileWriter(logFile, true);
	    BufferedWriter out = new BufferedWriter(fstream);
	    out.write("objective value=" + getObjValue() + "\n");
	    out.write("variable values\n");
	    
	    	out.write("\n Entscheidung\n");
		for( int j=0; j<v;j++){
		    for(int k =0; k<l;k++){
		
				
	if (getValue(x[j][k])==1){
		    out.write(" Komponente "+(j+1)+ " : Lieferant " + (k+1) + " x = " +getValue(x[j][k])+"\n");
		}
	    }
		}
	    out.close();
	    System.out.println("(WGP) wrote sol to file " + path + "\n");
	
    }

   

   
   

    public String[][] getAuslieferland() {
		return auslieferland;
	}

	public void setAuslieferland(String[][] auslieferland) {
		this.auslieferland = auslieferland;
	}

	public IloIntVar[][] getX() {
		return x;
	}

	public void setX(IloIntVar[][] x) {
		this.x = x;
	}

	public IloLinearNumExpr getLocalContent() {
		return localContent;
	}

	public void setLocalContent(IloLinearNumExpr localContent) {
		this.localContent = localContent;
	}

	public int getL() {
		return l;
	}

	public int getS() {
		return s;
	}

	public int getV() {
		return v;
	}

	public double[][] getWechselkurs() {
		return wechselkurs;
	}

	public double[][] getKosten() {
		return kosten;
	}

	public int[] getBedarf() {
		return bedarf;
	}

	public int[][] getKapa() {
		return kapa;
	}

	public int[][] getAngebot() {
		return angebot;
	}

	public double[][] getTiefenlokalisierung() {
		return anrechenbarerLocalContent;
	}

	public double getLcr() {
		return lcr;
	}

	

	public IloObjective getObjective() {
		return objective;
	}

	/**
	 * @return the szenarioOptimum
	 */
	public double[] getSzenarioOptimum() {
	    return szenarioOptimum;
	}

	/**
	 * @param szenarioOptimum the szenarioOptimum to set
	 */
	public void setSzenarioOptimum(double[] szenarioOptimum) {
	    this.szenarioOptimum = szenarioOptimum;
	}

	/**
     * @param l the l to set
     */
    public void setL(int l) {
        this.l = l;
    }

    /**
     * @param s the s to set
     */
    public void setS(int s) {
        this.s = s;
    }

    /**
     * @param v the v to set
     */
    public void setV(int v) {
        this.v = v;
    }

    /**
     * @param wechselkurs the wechselkurs to set
     */
    public void setWechselkurs(double[][] wechselkurs) {
        this.wechselkurs = wechselkurs;
    }

    /**
     * @param kosten the kosten to set
     */
    public void setKosten(double[][] kosten) {
        this.kosten = kosten;
    }

    /**
     * @param bedarf the bedarf to set
     */
    public void setBedarf(int[] bedarf) {
        this.bedarf = bedarf;
    }

    /**
     * @param kapa the kapa to set
     */
    public void setKapa(int[][] kapa) {
        this.kapa = kapa;
    }

    /**
     * @param angebot the angebot to set
     */
    public void setAngebot(int[][] angebot) {
        this.angebot = angebot;
    }

    /**
     * @param tiefenlokalisierung the tiefenlokalisierung to set
     */
    public void setTiefenlokalisierung(double[][] tiefenlokalisierung) {
        this.anrechenbarerLocalContent = tiefenlokalisierung;
    }

    /**
     * @param lcr the lcr to set
     */
    public void setLcr(double lcr) {
        this.lcr = lcr;
    }

    
    
    /**
     * @param objective the objective to set
     */
    public void setObjective(IloObjective objective) {
        this.objective = objective;
    }
}



