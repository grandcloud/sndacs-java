package com.snda.storage.integration;

import java.io.File;
import java.net.URI;

import com.snda.storage.exceptions.CSServiceException;
import com.snda.storage.security.ProviderCredentials;
import com.snda.storage.security.SNDACredentials;
import com.snda.storage.service.CSService;
import com.snda.storage.service.Constants;
import com.snda.storage.service.impl.rest.httpclient.RestCSService;
import com.snda.storage.service.model.CSObject;

public class RedirectPut {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		ProviderCredentials credredential = new SNDACredentials("access_key_id", "access_key_secret");
		CSService service = new RestCSService(credredential);

		try {
			CSObject object = new CSObject(new File("empty_file"));
			service.putObject("beijing_bucket", object);
		} catch (CSServiceException e) {
			if (e.getResponseCode() == 301) {
				System.out.println(e.getCSErrorCode());
				System.out.println(e.getCSErrorMessage());
				System.out.println(e.getCSErrorRequestId());
				System.out.println(e.getCSErrorEndpoint());
				URI uri = new URI(e.getCSErrorEndpoint());
				Constants.CS_DEFAULT_HOSTNAME = uri.getHost();
				CSObject object = new CSObject(new File("empty_file"));
				service.putObject("beijing", object);
			}
		}
	}

}
