/* Briand Samuel, 20010642
 * Dufour Dominic, 20140689
 */

import java.math.BigInteger;
import java.util.*;
import java.io.*;
import java.math.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class RSA{

	public static String encryptThisString(String input) 
    { 
        try { 
            // getInstance() method is called with algorithm SHA-1 
            MessageDigest md = MessageDigest.getInstance("SHA-1"); 
  
            // digest() method is called 
            // to calculate message digest of the input string 
            // returned as array of byte 
            byte[] messageDigest = md.digest(input.getBytes()); 
  
            // Convert byte array into signum representation 
            BigInteger no = new BigInteger(1, messageDigest); 
  
            // Convert message digest into hex value 
            String hashtext = no.toString(16); 
  
            // Add preceding 0s to make it 32 bit 
            while (hashtext.length() < 32) { 
                hashtext = "0" + hashtext; 
            } 
  
            // return the HashText 
            return hashtext; 
        } 
  
        // For specifying wrong message digest algorithms 
        catch (NoSuchAlgorithmException e) { 
            throw new RuntimeException(e); 
        } 
    }
	
	
	//Hex string to byte array conversion function
	public static byte[] hexTobyte(String mystr) {
		String str = mystr;
	    byte[] val = new byte[str.length() / 2];
	    for (int i = 0; i < val.length; i++) {
	       int index = i * 2;
	       int j = Integer.parseInt(str.substring(index, index + 2), 16);
	       val[i] = (byte) j;
	    }		
		return val;
	}
	
	//OAEP
	public static BigInteger OAEP(String M, BigInteger N) throws IOException {
		
		BigInteger n;
		int k, Lm;
		//Passing modulus n
		n = N;
		//Passing message m
		String m = M;
		//Getting k the length of the modulus n in	bytes
		k = n.toByteArray().length;
		//Getting Lm the length of the message in bytes
		Lm = m.getBytes().length;
		//SHA-1 byte output length
		int hashbytelength = 20;
		
		//Label associated with the message, in bytes.
		String L = "mymessage";
		String HashL = encryptThisString(L);
		byte[] HashLbytes = new byte[hashbytelength];
		HashLbytes = hexTobyte(HashL);
		
		//Generating string PS of length k − |M| − 2|H| − 2 of zeroed bytes
		int PSlength = k-Lm-(2*hashbytelength)-2;
		byte [] PS = new byte[PSlength];
		
		//0x01 in bytes
		String oxoi = Integer.toHexString(1);
		byte[] OXOI = new byte[1];
		OXOI = oxoi.getBytes();

		//The message in bytes
		byte[]message = m.getBytes();

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		outputStream.write(HashLbytes);
		outputStream.write(PS);
		outputStream.write(OXOI);
		outputStream.write(message);

		byte DB[] = outputStream.toByteArray();
		
		//Generate a random byte string seed of length hashbytelength
		byte[] seed = new byte[hashbytelength];
		new Random().nextBytes(seed);
		
		//Creating dbMask = MGF(seed, k −|H|−1)
		String strseed = new String(seed);
		String Hashseed = encryptThisString(strseed);
		byte[] hexseed = hexTobyte(Hashseed);
		ByteArrayOutputStream outputStream2 = new ByteArrayOutputStream();
		outputStream2.write(hexseed);
		for(int i=hashbytelength; i<(k-hashbytelength-1); i++) {
			String o = Integer.toHexString(0);
			byte[] O = new byte[0];
			O = o.getBytes();
			outputStream2.write(O);
		}
		byte dbMask[] = outputStream2.toByteArray();
		
		
		//Creating maskedDB
		byte[] maskedDB = new byte[k-hashbytelength-1];
		for(int i=0; i<(k-hashbytelength-1); i++) {
			maskedDB[i] = (byte) (dbMask[i] ^ DB[i]);
		}
		
		//Creating seedMask
		String maskedDBstr = new String(maskedDB);
		String hexseedMaskstr = encryptThisString(maskedDBstr);
		byte[] seedMask = hexTobyte(hexseedMaskstr);
		
		//Creating maskedSeed
		byte[] maskedSeed = new byte[hashbytelength];
		for(int i=0; i<(hashbytelength); i++) {
			maskedSeed[i] = (byte) (seed[i] ^ seedMask[i]);
		}
		
		
		//Encode message EM
		ByteArrayOutputStream outputStream3 = new ByteArrayOutputStream();
		
		String o = Integer.toHexString(0);
		byte[] O = new byte[1];
		O = o.getBytes();
		
		outputStream3.write(O);
		outputStream3.write(maskedSeed);
		outputStream3.write(maskedDB);

		byte EM[] = outputStream3.toByteArray();
		System.out.println(EM.length + " H " + k);
		
		String hexM = "";
		for(int i=0; i<EM.length; i++) {
			hexM = hexM + Integer.toHexString(EM[i]);
		}
		
		BigInteger valueEM = new BigInteger(hexM, 16);
		
		return valueEM;

	}
	
	
	
	
	//squareAndMultiply()
	public static BigInteger squareAndMultiply(BigInteger base,BigInteger exposant, BigInteger N) {
		
		BigInteger r = base;
		String expoBin = exposant.toString(2);
		BigInteger one = new BigInteger("1");
		BigInteger zero = new BigInteger("0");
		if(N.compareTo(one) == 0) {
			r = zero;
			return zero;
		}
		for(int i = 1 ; i <= expoBin.length()-1; i++ ) {
			r = (r.multiply(r)).mod(N);
			if (expoBin.charAt(i) == '1'){
				r = (r.multiply(base)).mod(N);
			}
		}
		return r;
	}
	

	//EEA() : Completed
	public static BigInteger EEA(BigInteger x, BigInteger y) {
		BigInteger pgcd, a, b;

		if(x.compareTo(y) == 1) {
			a = x;
			b = y;
		}
		else {
			b = x;
			a = y;
		}
		BigInteger zero = new BigInteger("0");
		if(b.compareTo(zero) == 0) {
			pgcd = a;
		}
		else {
			BigInteger c = a.mod(b);
			pgcd = EEA(b, c);
		}
		
		return pgcd;
	}
	
	
	// Permet de trouver d�composition d'un chiffre pair en nombre premier. Le code ce base sur un algorithme trouv� sur 
		// internet (avec plusieurs modifications): https://www.geeksforgeeks.org/print-all-prime-factors-of-a-given-number/
	    public static ArrayList<String> primeFactors(BigInteger n) { 
	       ArrayList<String> primefactors = new ArrayList<String>();
	    	// Print the number of 2s that divide n
	       	BigInteger zero = new BigInteger("0");
	       	BigInteger deux = new BigInteger("2");
	       	
	        while ((n.mod(deux)).equals(zero)) {  
	            String factors = "2";
	            primefactors.add(factors);
	            n = n.divide(deux);
	        } 
	  
	        // n must be odd at this point.  So we can 
	        // skip one element (Note i = i +2) 
	        for (BigInteger i = new BigInteger("3"); i.compareTo(n.sqrt()) <= 0; i= i.add(deux)) { 
	            // While i divides n, print i and divide n 
	            while ((n.mod(i)).equals(zero)) { 
	                String factors = i.toString();
	                primefactors.add(factors);
	                n = n.divide(i); 
	            } 
	        } 
	  
	        // This condition is to handle the case when 
	        // n is a prime number greater than 2 
	        if (n.compareTo(deux) == 1) {
	        	String factors = n.toString();
	        	primefactors.add(factors);
	        	
	        }
	        return primefactors;
	    }
		
	    
	    //millerRabin()
	  	// Permet de faire le test de Miller-Rabin pour vÃ©rifier si un nombre est premier
	    public static boolean millerRabin(BigInteger ptilt, int S) {
			// Premet de trouver u et r pour effectuer l'algo de Miller-Rabin
			BigInteger un = new BigInteger("1");
			BigInteger deux = new BigInteger("2");
			BigInteger trois = new BigInteger("3");
			ArrayList<String> primefactors = primeFactors(ptilt.subtract(un)); // Retourne la dï¿½composition en facteur pemier de ptilt 
			int u = 0;
			BigInteger r = new BigInteger("1");
			for(int i = 0; i < primefactors.size() ; i++ ) {
				BigInteger prime = new BigInteger(primefactors.get(i));
				if (prime.equals(deux)) {
					u++;
				} else { 
					 r = r.multiply(prime);
				}
				
				}
			// Début de l'algorithme de Miller-Rabin pour tester le primalité d'un nombre
			for(int i = 1; i <= S ; i++ ) {
				// Génération d'un nombre aléatoire entre 2 et ptilt-2
				Random rnd = new Random();
				BigInteger rand = new BigInteger(ptilt.toString(2).length(), rnd);
				while((rand.compareTo(deux) == -1) || (rand.compareTo(ptilt.subtract(deux)) == 1)) {
					rand = new BigInteger(ptilt.toString(2).length(), rnd);
				}
				BigInteger a = new BigInteger(rand.toString());
				BigInteger z = squareAndMultiply(a,r,ptilt);
				if ((z.equals(un) == false) && (z.equals(ptilt.subtract(un))== false)) {
					for(int j = 1; j <= u-1; j++) {
						z = squareAndMultiply(z,deux,ptilt);
						if (z.equals(un)) {
							return false;
						}
					}
					if ((z.equals(ptilt.subtract(un))) == false) {
						return false;
					}
				}
				}
			return true;
		}
	
	
	
	//genKeys()
	public static BigInteger genKeys(BigInteger x, BigInteger y, BigInteger z) {
		BigInteger p, q, n, un, pMun, qMun, phin, e, d;
		
		p = x;
		q = y;

		n = p.multiply(q); //p*q
		
		un = new BigInteger("1");
		
		pMun = p.subtract(un); //p-1
		qMun = q.subtract(un); //q-1
		
		phin = pMun.multiply(qMun); //(p-1)(q-1)
		
		
		//Exposant publique e
		e = z;
		d = new BigInteger("0");

		if(EEA(e, phin).compareTo(un) == 0) {
			d = e.modInverse(phin);
		}
		else {
			System.out.println("e and phi(n) are not relatively prime, please change the value of input e and restart.");
		}

		System.out.println("e = " + e);
		System.out.println("d = " + d);
		
		
		if(p.toString(2).length() >= 512 == true){ // to check if p more than 512 bits
			System.out.println("p is of correct minimal bit size");
		}
		
		
		if (millerRabin(p, 5) == true){ //to check if primer number
			System.out.println("p is prime");
		}
		
		
		if(q.toString(2).length() >= 512 == true){ // to check if q more than 512 bits
			System.out.println("q is of correct minimal bit size");
		}
		
		
		if (millerRabin(q, 5) == true){ //to check if primer number
			System.out.println("q is prime");
		}
		
		//Returning private key d
		return d;
	}
	
	
	public static BigInteger CRT(BigInteger p, BigInteger q, BigInteger d, BigInteger x) {
		
		// Étape 1
		BigInteger X_P = (x).mod(p);
		BigInteger X_Q = (x).mod(q);
		// Étape 2
		BigInteger un = new BigInteger("1");
		BigInteger d_P = (d).mod(p.subtract(un));
		BigInteger d_Q = (d).mod(q.subtract(un));
		BigInteger Y_P = squareAndMultiply(X_P,d_P,p);
		BigInteger Y_Q = squareAndMultiply(X_Q,d_Q,q);
		//Étape 3
		BigInteger minusone = new BigInteger("-1");
		BigInteger C_P = q.modPow(minusone,p);
		BigInteger C_Q = p.modPow(minusone,q);
		BigInteger qCP =  q.multiply(C_P);
		BigInteger pCQ =  p.multiply(C_Q);
		BigInteger qCPYP = qCP.multiply(Y_P);
		BigInteger pCQYQ = pCQ.multiply(Y_Q);
		BigInteger m = (qCPYP.add(pCQYQ)).mod(p.multiply(q));
		return m;
	}
	
	
public static void main(String args[]) throws IOException{
	
	//Test squareAndMultiply()
	System.out.println("Test squareAndMultiply()");
	BigInteger base = new BigInteger("7");
	BigInteger exponent = new BigInteger("19");
	BigInteger n = new BigInteger("11");
	System.out.println(base + "^" + exponent + " mod " + n + " = " + squareAndMultiply(base, exponent, n)); // correct output: 30
	
	
	//Test EEA()
	System.out.println("Test EEA()");
	BigInteger a = new BigInteger("42");
	BigInteger b = 	new BigInteger("12");
	
	System.out.println("The PGCD of " + a + " and " + b + " is " + EEA(a,b));
	
	
	//Test millerRabin()
	System.out.println("Test millerRabin()");
	int s = 5;
	boolean answer = millerRabin(n, s);
	System.out.println(n + " is a "+ answer + " prime");
	
	//Test genKeys();
	System.out.println("Test genKeys()");
	BigInteger p, q, e;
	e = new BigInteger("17");
	p = new BigInteger("E0DFD2C2A288ACEBC705EFAB30E4447541A8C5A47A37185C5A9CB98389CE4DE19199AA3069B404FD98C801568CB9170EB712BF10B4955CE9C9DC8CE6855C6123", 16);	
	q = new BigInteger("59");
	
	BigInteger d = genKeys(p, q, e);
	
	//Test OAEP()
	System.out.println("Test OAEP()");
	String message = "hello world";
	BigInteger EncodedMessage = OAEP(message, n);
	System.out.println(EncodedMessage);	
	

	System.out.println("Test Encrypt");
	BigInteger cipher = squareAndMultiply(EncodedMessage, e, n); // correct output: 30
	System.out.println(cipher);	

	
	//Test CRT()
	System.out.println("Test CRT()");
	BigInteger EncodedMessageDecrypt = CRT(p, q, d, cipher);
	System.out.println(EncodedMessageDecrypt);
	
	
}


}
