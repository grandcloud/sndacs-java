package com.snda.storage.security;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Class to contain the SNDA Cloud Storage credentials of a user.
 * 
 * @author snda
 *
 */
public class SNDACredentials extends ProviderCredentials {

	/**
	 * 
	 * @param accessKey
	 * SNDA access key for an SNDA cloud storage account.
	 * @param secretKey
	 * SNDA secret key for an SNDA cloud storage account.
	 */
	public SNDACredentials(String accessKey, String secretKey) {
		super(accessKey, secretKey);
	}
	
	/**
     * Construct credentials, and associate them with a human-friendly name.
     *
     * @param accessKey
     * SNDA access key for an SNDA Cloud Storage account.
     * @param secretKey
     * SNDA secret key for an SNDA Cloud Storage account.
     * @param friendlyName
     * a name identifying the owner of the credentials, such as 'James'.
     */
    public SNDACredentials(String accessKey, String secretKey, String friendlyName) {
        super(accessKey, secretKey, friendlyName);
    }

	@Override
	protected String getTypeName() {
		return "regular";
	}
	
	@Override
    public String getVersionPrefix() {
        return "javasdk SNDA Credentials, version: ";
    }
	
	public static void main(String[] args) throws Exception {
		
		if (args.length < 2 || args.length > 3) {
            printHelp();
            System.exit(1);
        }
        String userName = args[0];
        File encryptedFile = new File(args[1]);
        String algorithm = EncryptionUtil.DEFAULT_ALGORITHM;
        if (args.length == 3) {
            algorithm = args[2];
        }

        // Check arguments provided.
        try {
            FileOutputStream testFOS = new FileOutputStream(encryptedFile);
            testFOS.close();
        } catch (IOException e) {
            System.err.println("Unable to write to file: " + encryptedFile);
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        // Obtain credentials and password from user.
        System.out.println("Please enter your SNDA Credentials");
        System.out.print("Access Key: ");
        String sndaAccessKey = reader.readLine();
        System.out.print("Secret Key: ");
        String sndaSecretKey = reader.readLine();
        System.out.println("Please enter a password to protect your credentials file (may be empty)");
        System.out.print("Password: ");
        String password = reader.readLine();

        // Create SNDACredentials object and save the details to an encrypted file.
        SNDACredentials sndaCredentials = new SNDACredentials(sndaAccessKey, sndaSecretKey, userName);
        sndaCredentials.save(password, encryptedFile, algorithm);

        System.out.println("Successfully saved SNDA Credentials to " + encryptedFile);
        
	}
	
	/**
     * Prints help for the use of this class from the console (via the main method).
     */
    private static void printHelp() {
        System.out.println("SNDACredentials <User Name> <File Path> [algorithm]");
        System.out.println();
        System.out.println("User Name: A human-friendly name for the owner of the credentials, e.g. Horace.");
        System.out.println("File Path: Path and name for the encrypted file. Will be replaced if it already exists.");
        System.out.println("Algorithm: PBE encryption algorithm. Defaults to PBEWithMD5AndDES");
    }

}
