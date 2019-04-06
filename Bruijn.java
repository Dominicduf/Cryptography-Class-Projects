/* Briand Samuel, 20010642
 * Dufour Dominic, 20140689
 */

import java.util.*;
import java.util.stream.IntStream;

public class Bruijn{
	//Génère un cycle de de Bruijn pour des mots de longueur n
	//composés de k symboles différents
	public static String B(int k, int n){
		String result = "";

		//Taille de result
		int size = (int) Math.pow(k,n);

		//Emmagasine en mémoire quel index du "mot du haut"
		//a été visité
		BitSet visited = new BitSet(size);

		//TO DO : Quelques variables à initialiser içi
		ArrayList<Integer> pre_result = new ArrayList<>();
		int[] alphabet = new int [] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
		int[] motdubas = new int[size];
		int[] motduhaut = new int[size];
		/** Here we create the words (made with our alphabet) the we'll use to get our de Bruijn sequence,
		 * mot du bas being repetitions of the alphabet of size k^n,
		 * and mot du haut being the same but whose content is sorted in ascending numerical order**/
		for(int i = 0; i < size; i++) {
			motdubas[i] = alphabet[i%10];
			motduhaut[i] = alphabet[i%10];
		}
		Arrays.sort(motduhaut);
		
		/** Here we set the correspondences between the i^th symbol of mot du haut and of the mot du bas.
		 * The "correspondance" ArrayList will have its indices being the indices of the mot du haut,
		 * and its values the corresponding indices of mot du bas. **/
		ArrayList<Integer> correspondance = new ArrayList<>();
		int index = 0;
		for(int i = 0; i < size; i++) {
			if(motduhaut[index] == motdubas[i]) {
				int tempi = i;
				if(correspondance.contains(tempi) == false) {
					correspondance.add(tempi);
					index++;
					i = 0;
				}	
			}	
			if(correspondance.size() == size) {
				break;
			}
		}
		
		/**for(int i = 0; i < size; i++) {
			System.out.println(i + " points to " + correspondance.get(i));
		}**/		
		
		while(result.length() != size){
			//TO DO
			/** Here we set the position in mot du haut where the next cycle is suppose to start.
			 * [We didn't managed to make our algorithm work with the "visited" Bitset and opted with something we could run correctly]**/
			int cycle_start_pos = 0;
			for(int i = 0; i < size; i ++) {
				int tempi = i;
				if(pre_result.contains(tempi) == false) {
					cycle_start_pos = tempi;
					break;
				}
			}
			/** Here we go through the next cycle while storing the indices it "passes by"*/
			int cycle_pos = cycle_start_pos;
			boolean cycle_fini = false;
			while(cycle_fini == false) {
				if(cycle_start_pos == correspondance.get(cycle_pos)) {
					pre_result.add(cycle_pos);
					cycle_fini = true;
				}
				else {
					pre_result.add(cycle_pos);
					cycle_pos = correspondance.get(cycle_pos);
				}
				
			}
			/** This condition acts as a check to see if we've cycle through, and stored,
			 * all/enough indices of mot du haut.
			 * Then allows the "translation" of the indices into symbols of our alphabet to form our de Bruijn sequence.**/
			if(pre_result.size() == size) {
				for(int i = 0; i < size; i++) {
					result = result + motduhaut[pre_result.get(i)];
				}
			}
			
		}
		
		return result;
	}
	

	public static void main(String args[]){

		System.out.println(B(10,4));
	}
};

 