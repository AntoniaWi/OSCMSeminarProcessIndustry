package dataManagement;

public class ReadAndWrite {
	
	
	
	/**
	 * Should go into ReadAndWrite-Class later
	 * @param array
	 * @param title
	 */
	public static void printArrayWithPeriods (int [] array, String title) {
		
		System.out.println("\n" + title);
		
		System.out.println("--------------------------------------------------------------------------");
		
		System.out.print("Periods ");
		
		for (int i = 0; i < array.length; i++) {
			
			System.out.print("| " + i + "\t");
		}
		
		System.out.println("");
		
		System.out.print("Results ");
		
		for (int i = 0; i < array.length; i++) {
			
			System.out.print("| " + array[i] + "\t");
		}
	}
	

}
