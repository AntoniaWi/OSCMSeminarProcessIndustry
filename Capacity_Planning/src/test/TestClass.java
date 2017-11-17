package test;

public class TestClass {
	

	
	public static void main(String[] args) {
		
		double startTime = System.currentTimeMillis();
		
		double r_yearly = 0.05;
		double r_monthly = 0.05/12.0;
		
		double discount_yearly = (1 / (1 + r_yearly));
		double discount_monthly = (1 / (1 + r_monthly));
		
		System.out.println("Yearly interest rate: " +  r_yearly);
		System.out.println("Yearly discount factor: " +  discount_yearly);
		
		System.out.println("Monthly interest rate: " +  r_monthly);
		System.out.println("Monthly discount factor: " +  discount_monthly);
		
		double discount_monthly_yearly = Math.pow(discount_monthly, 12);
		
		System.out.println("Yearly discount factor coming from month: " +  discount_monthly_yearly);
	
		
		
		double d = 490 / (12*5);
		System.out.println(d);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println("Run time: " + (endTime - startTime));
		System.out.println("Run time: " + ((endTime - startTime)/1000));
	}
}

