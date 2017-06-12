package image.explorer.model;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.Certificate;
import java.io.*;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLPeerUnverifiedException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class HttpsClient{
	private final String apiKey = "AIzaSyAch3pM6FyrM5__449fxXZz_ipx7dFeIAQ";
	private boolean log = false;
	
	public static void main(String[] args) {
		HttpsClient https = new HttpsClient();
		https.log = true;
		Object json = https.get("47.304098", "-122.415034");
		System.out.println("Formatted Address: " + getFormattedAddress(json, "47.304098", "-122.415034"));
		System.out.println("City State: " + getAddressComponent(json, false, "locality") + ", " + getAddressComponent(json, true, "administrative_area_level_1"));
		//System.out.println(output);
	}
	
	public static String getAddressComponent(Object obj, boolean shorthand, String key) {
		JSONObject json = (JSONObject)obj;
		JSONArray array = (JSONArray)json.get("address_components");
		for(int i = 0; i < array.size(); i++) {
			if(((String)((JSONArray)((JSONObject)array.get(i)).get("types")).get(0)).equals(key))
				json = (JSONObject)array.get(i);
		}
		if(shorthand)
			return (String)json.get("short_name");
		else
			return (String)json.get("long_name");
	}
	
	public static String getFormattedAddress(Object obj, String lat, String lng) {
		JSONObject json = (JSONObject)obj;
		return (String)json.get("formatted_address");
	}
	
	public Object get(String lat, String lng) {
		Object address = null;
		String https_url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + 
							lat + "," + lng + 
						   "&key=" + apiKey;
		URL url;
		try {
			url = new URL(https_url);
			HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
			if(log)
				print_https_cert(con);
			String output = print_content(con);
			JSONParser parser = new JSONParser();
			JSONObject obj  = (JSONObject)parser.parse(output);
			JSONArray array = (JSONArray)obj.get("results");
			address = array.get(0);
			//System.out.println(address);
		} 
		catch (MalformedURLException e) { e.printStackTrace(); } 
		catch (IOException e) { e.printStackTrace(); }
		catch (org.json.simple.parser.ParseException e) { e.printStackTrace(); }
		return address;
	}
	
	private void print_https_cert(HttpsURLConnection con){
		if(con!=null){
			try {
				System.out.println("Response Code : " + con.getResponseCode());
				System.out.println("Cipher Suite : " + con.getCipherSuite());
				System.out.println("\n");
				Certificate[] certs = con.getServerCertificates();
				for(Certificate cert : certs) {
					System.out.println("Cert Type : " + cert.getType());
					System.out.println("Cert Hash Code : " + cert.hashCode());
					System.out.println("Cert Public Key Algorithm : " + cert.getPublicKey().getAlgorithm());
					System.out.println("Cert Public Key Format : " + cert.getPublicKey().getFormat());
					System.out.println("\n");
				}
			} 
			catch (SSLPeerUnverifiedException e) { e.printStackTrace(); } 
			catch (IOException e){ e.printStackTrace(); }
		}
	}
	
	private String print_content(HttpsURLConnection con){
		String output = "";
		if(con!=null){
			try {
				if(log)
					System.out.println("****** Content of the URL ********");
				BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String input;
				while ((input = br.readLine()) != null){
					if(log)
						System.out.println(input);
					output += input + "\r\n";
				}
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
				output = null;
			}
		}
		return output;
	}
}