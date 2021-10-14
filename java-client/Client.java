import java.net.HttpURLConnection;
import java.net.URL;
import java.io.*;
import java.util.*;
import java.time.Instant;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class Client {
	private static final String API_KEY = System.getenv("IPINT_API_KEY");
	private static final String API_SECRET = System.getenv("IPINT_API_SECRET");
	HashMap<String,String> parameters = new HashMap<String,String>();
	Request httpRequest;

	public Client() {
		String baseUrl = "https://api.ipint.io:8003";  // mainnet
		// String baseUrl = "https://api.ipint.io:8002";  // testnet
		httpRequest = new Request(baseUrl, API_KEY, API_SECRET);
	}
	
	/*
	Allow changing of base url to connect to testnet environment
	*/
	public Client(String baseUrl) {
		httpRequest = new Request(baseUrl, API_KEY, API_SECRET);
	}

	public void test() throws Exception {
		httpRequest.sendSignedRequest(parameters, "/test", "GET");
	}

	public void getInvoice(String invoice_id) throws Exception {
		parameters.put("id", invoice_id);
    	httpRequest.sendSignedRequest(parameters, "/invoice", "GET");
    	parameters.clear();
	}

}
