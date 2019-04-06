/* Briand Samuel, 20010642
 * Dufour Dominic, 20140689
 */

import java.io.IOException; 
import java.nio.charset.*; 
import java.nio.file.Files; 
import java.nio.file.Paths;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

public class Decrypt{
	static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}

	public static String decrypt(String text, String key){
		String result = "";

		//TO DO
	    String alpha[]= {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
		int keyIndex = 0;
		/**This for loop is to go through every single character of the text.**/
	    for(int i = 0; i < text.length(); i++ ) {
	    	/** Here we're checking if the character from the text is indeed a letter that should be decrypted. **/
			if(Character.isLetter(text.charAt(i)) && text.charAt(i) != 'á' && text.charAt(i) != 'é' && text.charAt(i) != 'ë' 
					&& text.charAt(i) != 'í' && text.charAt(i) != 'à' && text.charAt(i) != 'ó'&& text.charAt(i) != 'ú' 
					&& text.charAt(i) != 'è' && text.charAt(i) != 'î' && text.charAt(i) != 'ô' && text.charAt(i) != 'ç' 
					&& text.charAt(i) != 'â' && text.charAt(i) != 'ê' && text.charAt(i) != 'ï' && text.charAt(i) != 'ý') {
				int decal = 0;
				/** Here we calculate the shift according to the current character of the key. **/
				for(int j = 0; j < alpha.length; j++){
					
					if(Character.toString(key.charAt(keyIndex)).equals(alpha[j])) {
						decal = j+1;
					}
				}
				int textIndex = 0;
				/** Here we obtain the index of the replacing character in the alphabet.**/
				for(int j = 0; j < alpha.length; j++) {
					if(Character.toString(text.charAt(i)).equals(alpha[j])){
						textIndex = j-decal;
						if(textIndex < 0) {
							textIndex = textIndex + 26;
						}
						break;
					}
				}
				/** Here we are replacing the encrypted character by the original character
				 * and updating the key character being used to decrypt.**/
				keyIndex++;
				keyIndex = keyIndex % key.length();
				text = text.substring(0, i) + alpha[textIndex] + text.substring(i + 1);
			}
		}
	    result = text;
		return result;
	}

	public static int getKeySize(String text, double tolerance){
		int keySize = 0;
		/** We remove all characters that are not in the alphabet**/
		text = text.replaceAll("[^a-zA-Z0-9]+", "");
		text = text.replaceAll("[0-9]", "");
		
		double Tolcomp = 1;
		/** We iterate in this while loop until we find the tolerance we find (Tolcomp) is in the accetable range (Tolerance)  **/
		while (Tolcomp > tolerance) {
			 double sum = 0;
			 Tolcomp = 0;
			 keySize = keySize + 1;
			 String Stream = "";
			
			 	/** For each keySize, we extract ONE cipher stream **/	 
				for(int i = 0; i < text.length(); i=i+keySize){	
					
					String tempstream = Character.toString(text.charAt(i));
					Stream = Stream + tempstream;
				}
				
			    String alpha[]= {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
			    double count[]= new double[26];
			    double streamlength = Stream.length();
			    /** We compute the frequency for each character of the alphabet appearing in the stream, Ex: count[0] = frequency of a in the stream **/
				for(int i = 0; i < alpha.length;i++){	
					double temp = Stream.length() - Stream.replace(alpha[i], "").length();
					count[i]= temp / streamlength;
			    }
				/** We compute the sum of frequencies squared **/
		        for (int i = 0; i < count.length; i++) {
		        	sum +=  Math.pow(count[i], 2); 
		        } 
		        /** We compute our tolerance and this value will be compared at the beginning of the while loop**/
		       Tolcomp = Math.abs(0.065 - sum); 
		}

		return keySize;
	}

	public static String getKey(String text, int keySize){
		
		text = text.replaceAll("[^a-zA-Z0-9]+", "");
		text = text.replaceAll("[0-9]", "");
		
		String result = "";
		String alpha[]= {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
		//FrÃ©quences thÃ©orique des lettres en anglais: f[0]=a, f[1]=b, etc.
		double[] f = new double[]{0.082,0.015,0.028,0.043,0.127,0.022,0.02,
				0.061,0.07,0.02,0.08,0.04,0.024,0.067,0.015,0.019,0.001,0.06,
				0.063,0.091,0.028,0.02,0.024,0.002,0.02,0.001};
		String resultint [] = new String[keySize];
		/** Every iteration of this for loop is responsible for finding the character a position i in the key.**/
		for (int i = 0; i < keySize; i++) {
			String Stream = "";
			/**Here, for every iteration of the previous for loop,
			 * we create the stream that corresponds to the current character of the key.**/
			for(int j = i; j < text.length(); j = j+keySize){	
				String tempstream = Character.toString(text.charAt(j));
				Stream = Stream + tempstream;
			}
		    double count[]= new double[26];
		    double h [] = new double[26];	
		   /** Here we create shifted stream for every letter of the alphabet.
		    * For each shifted stream we calculate occurrence frequency of every letter of the alphabet and store them in an array (count),
		    * and then we multiply these values with their theoretical occurrence frequency and store the result in an array (h). **/
		    for(int k = 1; k <= alpha.length; k++) {


		    	String StreamDecal = "";

		    	for (int j = 0; j < Stream.length(); j++) {
		    		int decal = 0;
		    		for(int o = 0; o < alpha.length; o++){
		    			String streamchar = Character.toString(Stream.charAt(j));
		    			String alphachar = alpha[o];
						if(streamchar.equals(alphachar)) {
							decal = o-k;
							if(decal < 0) {
								decal = decal + 26;
							}
							break;
						}
					}
		    		StreamDecal = StreamDecal + alpha[decal];
		    	}

				for(int j = 0; j < alpha.length;j++){	
					double temp = StreamDecal.length() - StreamDecal.replace(alpha[j], "").length();
					count[j]= temp / StreamDecal.length();
			    }
				
				for(int j = 0; j < count.length;j++){	
					h[k-1] += count[j]*f[j];
				}
		    }

			int largest = 0;
			/**Here we obtain the largest value in the array, indicating what is the character of the key at position i,
			 * and then pass it to an array holding the result of the key.**/
			for ( int j = 0; j < h.length; j++ ){
				if ( h[j] > h[largest] ) {
					largest = j;
				}
			  }
			
			resultint[i] = alpha[largest];
		}
		
		for(int i = 0; i < resultint.length; i++) {
			result = result + resultint[i];
		}
		
		return result;
	}

	public static void main(String args[]){
		String text = "";

		try{
			text += readFile("cipher.txt", StandardCharsets.UTF_8);
		}catch(IOException e) {
			System.out.println("Can't load file");
		}

		//TO DO: Vous devez trouver la tolÃ©rance nÃ©cessaire
		//Ã  utiliser pour trouver la longueur de la clef
		double tolerance = 0.005;

		int keySize = getKeySize(text, tolerance);
		System.out.println(keySize);

		String key = getKey(text, keySize);
		System.out.println(key);
		

		text = decrypt(text, key);

		try (PrintWriter out = new PrintWriter("result.txt")) {
		    out.println(text);
		}catch(IOException e) {
			System.out.println("Can't write file");
		}

	}
	
}