package dataManagement;

public class ReadAndWrite {
	
	
	
	/**
	 * Should go into ReadAndWrite-Class later
	 * @param array
	 * @param title
	 */
	public static void printArrayWithPeriodsInt (int [] array, String title) {
		
		System.out.println("\n\n" + title);
		
		System.out.println("--------------------------------------------------------------------------");
		
		System.out.print("Period ");
		
		for (int i = 0; i < array.length; i++) {
			
			System.out.print("| " + i + "\t");
		}
		
		System.out.println("");
		
		System.out.print("Result ");
		
		for (int i = 0; i < array.length; i++) {
			
			System.out.print("| " + array[i] + "\t");
		}
	}
	
	
	/**
	 * Should go into ReadAndWrite-Class later
	 * @param array
	 * @param title
	 */
	public static void printArrayWithPeriodsDouble (double [] array, String title) {
		
		System.out.println("\n\n" + title);
		
		System.out.println("--------------------------------------------------------------------------");
		
		System.out.print("Period ");
		
		for (int i = 0; i < array.length; i++) {
			
			System.out.print("| " + i + "\t");
		}
		
		System.out.println("");
		
		System.out.print("Result ");
		
		for (int i = 0; i < array.length; i++) {
			
			double tmp = Math.round(array[i]*100.00);
			tmp = tmp / 100;
			
			System.out.print("| " + tmp + "\t");
		}
	}
	
	
	/**
	 * 
	 * @param array
	 * @param title
	 */
	public static void printArraySimple (int [] array) {
		
		System.out.print("\n\n| ");
		
		for (int i = 0; i < array.length; i++) {
			
			System.out.print(array[i] + "\t" + "|");
		}
	}
	

}
