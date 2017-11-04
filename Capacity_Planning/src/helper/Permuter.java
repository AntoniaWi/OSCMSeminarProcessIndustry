package helper;

import java.util.ArrayList;
import java.util.Arrays;
import dataManagement.ReadAndWrite;

public class Permuter {

	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		ArrayList<int[]> list = new ArrayList<int[]>();
		
		permute(new int[] { 1,2,3 }, list);
		
		for (int i = 0; i < list.size(); i++) {
			
			ReadAndWrite.printArraySimple(list.get(i));
		}
		
		System.out.println("\nRedundancy\n");
		
		Permuter.deleteRedundancy(list);

		for (int i = 0; i < list.size(); i++) {
			
			ReadAndWrite.printArraySimple(list.get(i));
		}
	
	}

	
	/**
	 * 
	 * @param arr
	 */
	public static void permute(int[] arr, ArrayList<int[]> list) {
		
		permuteHelper(arr, 0, list);
	}

	
	/**
	 * 
	 * @param arr
	 * @param index
	 */
	private static void permuteHelper(int[] arr, int index, ArrayList<int[]> list) {
		
		if (index >= arr.length - 1) {
		
			int [] tmp = new int [arr.length];
			
			for (int i = 0; i < arr.length - 1; i++) {
				tmp[i] = arr[i];
			}
			
			if (arr.length > 0)
				tmp[tmp.length - 1] = arr[arr.length - 1];
			
			list.add(tmp);
			
			return;
		}

		for (int i = index; i < arr.length; i++) { // For each index in the sub array arr[index...end]

			// Swap the elements at indices index and i
			int t = arr[index];
			arr[index] = arr[i];
			arr[i] = t;

			// Recurse on the sub array arr[index+1...end]
			permuteHelper(arr, index + 1, list);

			// Swap the elements back
			t = arr[index];
			arr[index] = arr[i];
			arr[i] = t;
		}
	}
	
	
	/**
	 * 
	 * @param list
	 */
	public static void deleteRedundancy (ArrayList<int[]> list) {
		
		int length = list.get(0).length;
		
		int i = 0;
		
		while (i < list.size()) {
			
			int [] array_tmp = list.get(i);
			
			int search_index = 0;
			
			while (search_index < list.size()) {
				
				int count = 0;
				
				int [] array_search = list.get(search_index);
				
				if (search_index != i) {
					
					for (int j = 0; j < length; j++) {
						
						if (array_tmp[j] == array_search[j]) {
							
							count++;
						}
					}	
				}
				
				if (count == length) {
					
					list.remove(array_search);
				}
				
				search_index++;
			}
			
			i++;
		}
	}
}




















