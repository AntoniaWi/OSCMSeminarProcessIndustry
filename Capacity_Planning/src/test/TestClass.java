package test;

public class TestClass {
	

	
	public static void main(String[] args) {
	
		double p = 0.5;
		
		double left_value = 13100;
		
		double right_value = 7600;
		
		
		
	    double result = (p * left_value) + ((1-p) * right_value);
	    
	    System.out.println("Result: " + result);
	    
	    
	    double result_2 = result + 1100;
	    
	    System.out.println("Result 2: " + result_2);
	    
	}
}


// https://stackoverflow.com/questions/30387185/print-out-all-permutations-of-an-array