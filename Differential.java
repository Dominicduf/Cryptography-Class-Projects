/* Briand Samuel, 20010642
 * Dufour Dominic, 20140689
 */

import java.util.*;

public class Differential{
	//Écrire votre numéro d'équipe içi !!!
	public static int teamNumber = 8;

	public static SPNServer server = new SPNServer();

	//Différentielle d'entrée \Delta_P
	//ex : ""0000101100000000""
	public static String plain_diff = "0000110100000000";

	//Différentielle intermédiaire \Delta_I
	//ex : "0000011000000110"
	public static String int_diff = "0100000001000000";
	//Boîte à substitutions de l'exemple de la démonstration #3
	public static String[] sub_box_exemple = new String[]{"1110", "0100", "1101", "0001", "0010", "1111", "1011", "1000",
			   												"0011", "1010", "0110", "1100", "0101", "1001", "0000", "0111"};

	public static String[] sub_box_inv_exemple = new String[]{"1110", "0011", "0100", "1000", "0001", "1100", "1010", "1111", 
				   									  			"0111", "1101", "1001", "0110", "1011", "0010", "0000", "0101"};

	//Sorties des boîtes à substitutions du SPN
	public static String[] sub_box = new String[]{"0010", "1011", "1001", "0011", "0111", "1110", "1101", "0101", 
												  "1100", "0110", "0000", "1111", "1000", "0001", "0100", "1010"};

	//Entrées des boîtes à substitutions du SPN
	public static String[] sub_box_inv = new String[]{"1010", "1101", "0000", "0011", "1110", "0111", "1001", "0100", 
												      "1100", "0010", "1111", "0001", "1000", "0110", "0101", "1011"};

	//Permutations : --> Notez que la permutation "perm" inverse est la même puisqu'elle est symmétrique
	public static int[] perm = new int[]{0, 4, 8, 12, 1, 5, 9, 13, 2, 6, 10, 14, 3, 7, 11, 15};

	public static int[] pc1 = new int[]{15, 10, 5, 0, 16, 9, 7, 1, 17, 3, 19, 8, 6, 4, 18, 12, 14, 11, 13, 2};

	public static int[] pc1_inv = new int[]{3, 7, 19, 9, 13, 2, 12, 6, 11, 5, 1, 17, 15, 18, 16, 0, 4, 8, 14, 10};

	public static int[] pc2 = new int[]{9, 7, 0, 8, 5, 1, 4, 2, 16, 12, 19, 10, 17, 15, 13, 14};

	public static int[] pc2_inv = new int[]{2, 5, 7, 6, 4, 1, 3, 0, 11, 9, 14, 15, 13, 8, 12,10};

	public static int[][] produceDiffTable(){
		int[][] result = new int[16][16]; 
		for(int i = 0; i < 16; i++){
			
			int[] line = new int[16];

			//TO DO
			/** We fill the line with zeros to be able to increment its cells without considering its previous value  **/
			line = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
			
			/** This for loop is used to go through all inputs(x's), to compute all inputs(x''s),
			    enabling us to obtain all outputs(y's and y''s) and therefore to compute all y differences(delta y) and fill the table **/
			for(int j = 0; j < 16; j++) {
				var xpp = xor(sub_box_inv[i], sub_box_inv[j]);
				var ypp = sub(xpp, sub_box);
				var yp = sub(sub_box_inv[j], sub_box);
				var deltay = xor(yp, ypp);
				//Check
				switch (deltay) {
	            case "0000":  line[0]++;
	            	break;
	                     
	            case "0001": line[1]++;
	            	break;
	                     
	            case "0010": line[2]++;
	            	break;
	                     
	            case "0011": line[3]++;
	            	break;
	                     
	            case "0100": line[4]++;
	            	break;
	                     
	            case "0101": line[5]++;
	            	break;
	                     
	            case "0110": line[6]++;
	            	break;
	                     
	            case "0111": line[7]++;
	            	break;
	                     
	            case "1000": line[8]++;
	            	break;
	                     
	            case "1001": line[9]++;
	            	break;
	                     
	            case "1010": line[10]++;
	            	break;
	            	
	            case "1011": line[11]++;
	            	break;
	                     
	            case "1100": line[12]++;
	            	break;  		
                
	            case "1101": line[13]++;
                	break;
                
	            case "1110": line[14]++;
                	break;
                
	            case "1111": line[15]++;
                	break;
                
	            default: ;
	                     break;
				}
			};

			result[i] = line;
		}

		return result;
		
	}

	//Retroune 16 bits aléatoires en String
	public static String getRandomPlaintext(){
		String text = Integer.toBinaryString((int) Math.floor(Math.random()* 65536));

		while(text.length() != 16){
			text = "0" + text;
		}

		return text;
	}

	//Permute l'input en utilisant la permutation perm
	//À utiliser aussi avec pc1, pc1_inv, etc.
	public static String permute(String input, int[] perm){
		String output = "";

		for(int i = 0; i < perm.length; i++){
			output += input.charAt(perm[i]);
		}

		return output;
	}

	//Prend une entrée de 4 bits et retourne la valeur
	//associée dans l'argument sub_box
	public static String sub(String input, String[] sub_box_exemple){
		int value = 0;

		for(var i = 0; i < input.length(); i++){
			value <<= 1;

			if(input.charAt(i) == '1'){
				value += 1;
			}
		}

		return sub_box_exemple[value];
	}

	//Retourne l'input ayant fait "amount" rotation(s) vers la gauche
	public static String left_shift(String input, int amount){
		return input.substring(amount) + input.substring(0, amount);
	}

	//Retourne l'input ayant fait "amount" rotation(s) vers la droite
	public static String right_shift(String input, int amount){
		return input.substring(input.length() - amount) + input.substring(0, input.length() - amount);
	}

	//Retourne [k_1, k_2, k_3, k_4, k_5] calculées à partir  
	//de la clef maître "master" selon la génération de  
	//sous-clefs de la troisième démonstration
	public static String[] gen_keys(String master, int n){
		String[] result = new String[n];

		String pc1_res = permute(master, pc1);

		String left = pc1_res.substring(0,10);
		String right = pc1_res.substring(10);

		for(int i = 0; i < n; i++){
			int shift = i % 2 + 1;

			left = left_shift(left, shift);
			right = left_shift(right, shift);

			String temp = left + right;

			result[i] = permute(temp, pc2);
		}

		return result;
	}

	//Retourne un ou-exclusif entre les chaînes de caractères a et b
	public static String xor(String a, String b){
		if(a.length() != b.length()){
			return null;
		}
		var result = "";

		for(var i = 0; i < a.length(); i++){
			result += a.charAt(i) ^ b.charAt(i);
		}

		return result;
	}


	public static String encrypt(String plaintext, String[] subkeys){
		String cipher = plaintext;
			
		for(int i = 0; i < 4; i++){
			//sub-key mixing
			//TO DO
			cipher = xor(cipher, subkeys[i]);
		
			//substitution
			//TO DO
			String s41 = sub(cipher.substring(0, 4), sub_box);
			String s42 = sub(cipher.substring(4, 8), sub_box);
			String s43 = sub(cipher.substring(8, 12), sub_box);
			String s44 = sub(cipher.substring(12, 16), sub_box);
			
			cipher = s41 + s42 + s43 + s44;

			//permutation
			//TO DO
			cipher = permute(cipher, perm);
		}

		//Final sub-key mixing (5th sub-key)
		//TO DO
		cipher = xor(cipher, subkeys[4]);
		
		return cipher;
	}

	public static String getPartialSubkey(){
		int[] counts = new int[256];

		ArrayList<String> plaintexts = new ArrayList<>();

		for(int i = 0; i < 1000; i++){
			//Créaton de paires de messages clairs qui satisfont
			//la différentielle d'entrée \Delta_P

			//TO DO
			String tempTxt = getRandomPlaintext();
			String tempTxt_P = xor(tempTxt, plain_diff);
			plaintexts.add(tempTxt);
			plaintexts.add(tempTxt_P);
		}

		//Encryption de ces messages clairs
		ArrayList<String> ciphers = server.encrypt(plaintexts,teamNumber);
		
		/** Here we create all our 256 32bits partial sub keys and add them to an array list **/
		String[] partial_subkeys_half = new String[]{"00000000", "00000010", "00001000",
				"00100000", "10000000", "10101010", "10101000", "10100000",
				"00101000", "00100010", "10000010", "10001000", "00001010",
				"00101010", "10001010", "10100010"}; 
		
		ArrayList<String> partial_subkeys = new ArrayList<>();
		
		for(int i = 0; i < 16; i++) {
			for(int k = 0; k < 16; k++) {
				partial_subkeys.add(partial_subkeys_half[i] + partial_subkeys_half[k]);
			}
		}	
		
		for(int j = 0; j < 256; j++){
			//Affectation du nombre de fois que chaque sous-clef partielle
			//j possible nous donne la différentielle intermédiaire 
			//"int_diff" à counts[j]

			//TO DO
			
			/** Here we XOR ciphers with the partial sub key of the current iteration of the loop, and add them to a new array list**/
			ArrayList<String> ciphers1 = new ArrayList<>();
			ArrayList<String> ciphers2 = new ArrayList<>();
			ArrayList<String> ciphers3 = new ArrayList<>();
			
			String current_partial_subkey = partial_subkeys.get(j);
			
			for(int c = 0; c < 2000; c++) {
				ciphers1.add(xor(current_partial_subkey, ciphers.get(c)));
			}
			
			/** Here we permute all 2000 ciphers with permute function and perm parameter and add them to a new array list**/

			for(int c = 0; c < 2000; c++) {
				String p = permute(ciphers1.get(c), perm);
				ciphers2.add(p);	
			}
			
			/** Here we breakdown our recently permuted ciphers into bits of 4 to enable their reversed substitution through the 4 S-boxes
			 * ( with sub function and sub_box_inv_exemple parameter). We then take their outputs, build them back into 16 bits ciphers 
			 * and add them to a new array list**/
			for(int c = 0; c < 2000; c++) {
				String s41 = sub(ciphers2.get(c).substring(0, 4), sub_box_inv);
				String s42 = sub(ciphers2.get(c).substring(4, 8), sub_box_inv);
				String s43 = sub(ciphers2.get(c).substring(8, 12), sub_box_inv);
				String s44 = sub(ciphers2.get(c).substring(12, 16), sub_box_inv);
				
				ciphers3.add(s41 + s42 + s43 + s44);	
			}
			
			/** Here we XOR the pairs of ciphers and check if they match the intermediate differential, 
			 * and add to the count of the current iteration if they do**/
			int keycount = 0;
			
			for(int c = 0; c < 2000; c = c+2){
				String diff = xor(ciphers3.get(c), ciphers3.get(c+1));
				if(diff.equals(int_diff)) {
					keycount++;
				}
				
			}
			counts[j] = keycount;
		}

		//Déterminer la fréquence de clef la plus haute
		//TO DO
		/** Here we find the key by looking for the highest integer in counts[] and saving it's index.
		 * Then we turn the saved index into a binary string to get the key with the highest frequency, and print it.**/
		int key_index = 0;
		int key_freq = 0;
		for(int i = 0; i < counts.length; i ++) {
			if(key_freq < counts[i]) {
				key_freq = counts[i];
				key_index = i;
			}
		}
		String highest_freq_key = partial_subkeys.get(key_index);
		System.out.println("La clef avec la fréquence la plus haute est " + highest_freq_key);
		String partialSubkey = highest_freq_key.substring(1, 2) + "X" + highest_freq_key.substring(3, 4) + "X" + highest_freq_key.substring(5, 6) + "X" + highest_freq_key.substring(7,8) + "X" + highest_freq_key.substring(9,10) + "X" + highest_freq_key.substring(11,12) + "X" + highest_freq_key.substring(13,14)+ "X" + highest_freq_key.substring(15,16) + "X";

		return partialSubkey;
	}

	public static String getPartialMasterkey(String partialSubkey, int n){
		String result = "";
		//Retrouver la clef maître partielle grâce au résultat
		//de getPartialSubkey() en insérant des 'X' aux bits inconnues
		//TO DO
		/** We permute the partial subkey of round 5 with PC2_inv **/
		String partialsubkeyPC2 = permute(partialSubkey, pc2_inv);
		
		/** We add "X" to the 4, 7, 12 and 19 bits because these bits are not used by PC2 when permuting to form the last subkey (we form a key of 20 bits) **/
		String partialsubkeyPC2_20 = partialsubkeyPC2.substring(0,3) + "X" + partialsubkeyPC2.substring(3,5) + "X" + partialsubkeyPC2.substring(5,9) + "X" + partialsubkeyPC2.substring(9,15) + "X" + partialsubkeyPC2.substring(15);
		
		/** We separate the key in 2 halves**/
		String partialsubkeyPC2_left = partialsubkeyPC2_20.substring(0, partialsubkeyPC2_20.length()/2);
		String partialsubkeyPC2_right = partialsubkeyPC2_20.substring(partialsubkeyPC2_20.length()/2);
		
		/** We do the inverse of the left shift (right shift) 5 times following the order giving by amountreverse (the amount of times we shift). This order is given in the 3rd demonstration **/
		int[] amountreverse = new int[] {2, 2, 2, 1, 1};
		for(int i = 0; i < n-1; i ++) {
			partialsubkeyPC2_left = right_shift(partialsubkeyPC2_left, amountreverse[i]);
			partialsubkeyPC2_right = right_shift(partialsubkeyPC2_right, amountreverse[i]);
			}
		/** We join the 2 halves and we permute the result with pc1_inv and this gives us the partial master key **/
		String partialsubkeybeforePC1_16 = partialsubkeyPC2_left + partialsubkeyPC2_right;
		result = permute(partialsubkeybeforePC1_16, pc1_inv);
		
		return result;
	}

	public static String bruteForce(String partialMasterkey){
		String result = "";
		boolean found = false;

		//Generating random plaintext
		String text = getRandomPlaintext();
		/** Encrypting the random plain text with the server **/
		String res_server = server.encrypt(text,teamNumber);
		String potentialmasterkey = "";
		
		for(int i = 0; i < 4096 && !found; i++){
			//Déterminer lesquelles des 2^12 (4096) possibilités de bits
			//manquantes donnent la bonne clef maître
			/** We generate the 2^12 possibilities for the master keys and we replace X's in the partialMasterkey with them  **/
			String key = String.format("%12s", Integer.toBinaryString(i)).replace(' ', '0');
			char[] keyCHAR = key.toCharArray();
			String compare = "X";
			char c = compare.charAt(0);
			char[] partialMasterkeyCHAR = partialMasterkey.toCharArray();
			for(int j = 0; j < keyCHAR.length; j++){
				for(int k = 0; k < partialMasterkeyCHAR.length; k++){
					if(partialMasterkeyCHAR[k] == c ) {
						partialMasterkeyCHAR[k] = keyCHAR[j];
						break;
					}
				}	
			}
			potentialmasterkey = new String(partialMasterkeyCHAR);
			//TO DO
			/** We generate the subkeys with our potential master key **/
			String[] genkeys= gen_keys(potentialmasterkey, 5);
			/** We encrypt the random plaintext with our subkeys generated from our potential master key **/
			String potentialresult = encrypt(text, genkeys);
			/** We check if the plaintext encrypted with our potential master key equals the text encrypted with the server. If yes, we found the master key **/
			if(potentialresult.equals(res_server)){
				break;
			}
		}
		result = potentialmasterkey;
		return result;
	}

	public static void main(String args[]){
		//Génération de la table des fréquences des différentielles de sortie
		//pour chaque différentielle d'entrée
		System.out.println(Arrays.deepToString(produceDiffTable()));

		//Calcul de la sous-clef partielle k_5^*
		String partialSubkey = getPartialSubkey();
		System.out.println("Sous-clef partielle k_5^* : " + partialSubkey);

		//Calcul de la clef maître partielle k^* 
		String partialMasterkey = getPartialMasterkey(partialSubkey, 5);
		System.out.println("Clef maître partielle k^* : " + partialMasterkey);

		//Calcul de la clef maître par fouille exhaustive 
		String masterkey = bruteForce(partialMasterkey);
		System.out.println("Clef maître k :             " + masterkey);

		//Information utile --> clef de l'exemple de la démo 3 : 00100100001111010101
	}

}