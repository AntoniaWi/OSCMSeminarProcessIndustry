package helper;

import java.util.ArrayList;


/**
 * Provides permutation function for the generation of all possible strategies in period t 
 * @author RamonaZauner
 *
 */
public class Permuter {

	
	/**
	 * Creates an array list with all permutations (no duplicates) of an array
	 * @param array on which the permutation is executed
	 * @param list in which permutations are saved
	 */
	public static void permute(int[] array, ArrayList<int[]> list) {
		
		permuteHelper(array, 0, list);
		deleteRedundancy(list);
	}

	
	/**
	 * Creates an array list of all permutations of an array
	 * @param array for permutations
	 * @param index parameter for recursion
	 */
	private static void permuteHelper(int[] array, int index, ArrayList<int[]> list) {
		
		// Return new array with permutation
		
		if (index >= array.length - 1) {
		
			int [] tmp = new int [array.length];
			
			for (int i = 0; i < array.length - 1; i++) {
				tmp[i] = array[i];
			}
			
			if (array.length > 0)
				tmp[tmp.length - 1] = array[array.length - 1];
			
			list.add(tmp);
			
			return;
		}

		// Recursive swapping
		
		for (int i = index; i < array.length; i++) { 

			// Swap elements at position index and i
			int t = array[index];
			array[index] = array[i];
			array[i] = t;

			// Recursion on sub-array
			permuteHelper(array, index + 1, list);

			// Undo swapping
			t = array[index];
			array[index] = array[i];
			array[i] = t;
		}
	}
	
	
	/**
	 * Deletes identical permutations (duplicates) from the list
	 * @param list
	 */
	private static void deleteRedundancy (ArrayList<int[]> list) {
		
		int length = list.get(0).length;
		
		// List of permutations that should be deleted
		
		ArrayList<int[]> remove = new ArrayList<int[]>();
		
		for (int i = 0; i < list.size(); i++) {
			
			int [] array_tmp = list.get(i);
			
			int search_index = i+1;
			
			while (search_index < list.size()) {
				
				int count = 0;
				
				int [] array_search = list.get(search_index);
					
				for (int j = 0; j < length; j++) {
						
					if (array_tmp[j] == array_search[j]) {
							
						count++;
					}
				}	
			
				// If all entries are the same, the arrays are identical and the found array should be removed
				
				if (count == length) {
				
					remove.add(array_search);
				}
			
				search_index++;
			}
		}
		
		list.removeAll(remove);
	}
}