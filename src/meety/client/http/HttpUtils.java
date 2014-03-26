package meety.client.http;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;

public abstract class HttpUtils {

	public static CookieManager cookieManager;
	static {
		disableConnectionReuseIfNecessary();
		enableCookieManagement();
	}
	
	private static void disableConnectionReuseIfNecessary(){
	    // Http connection reuse which was buggy pre-froyo
	    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
	        System.setProperty("http.keepAlive", "false");
	    }
	}

	private static void enableCookieManagement(){
		cookieManager = new CookieManager();
		CookieHandler.setDefault(cookieManager);
	}

	private static class HttpRequestExecutor extends AsyncTask<String, Void, JSONObject>{

		@SuppressLint("DefaultLocale")
		@Override
		protected JSONObject doInBackground(String... params) {

			try {
				
				System.out.println("REQUEST METHOD: "+params[0]);
				String method = params[0];
				System.out.println("URL FOUND: "+params[1]);
				URL url = new URL(params[1]);
				URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(),
						url.getPort(), url.getPath(), url.getQuery(), url.getRef());
				url = uri.toURL();
				System.out.println("HEADERS: "+params[2].toString());
				String headers = params[2];
				String payload = params[3];
			
				HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
				try{
					
					if ( headers != null ){

						JSONObject JSONHeaders = new JSONObject(headers);
						Iterator<?> keys = JSONHeaders.keys();

						while ( keys.hasNext() ){

							String key = (String) keys.next();
							String value = (String) JSONHeaders.get(key);
							urlConn.setRequestProperty(key, value);
							System.out.println("ADDING HEADER: "+key+", "+value);
						}
					}
					
					if ( method.toUpperCase().equals("POST") ){
						urlConn.setRequestMethod("POST");
						System.out.println("PAYLOAD IS: " + payload);
						if ( payload != null ){
							urlConn.setDoOutput(true);
							urlConn.setChunkedStreamingMode(payload.length());
//							payload = URLEncoder.encode(payload, "utf-8");
							OutputStream out = new BufferedOutputStream(urlConn.getOutputStream(), payload.length());
							out.write(payload.getBytes());
						}
					}
					
					urlConn.connect();
					
					String responseMessage = urlConn.getResponseMessage();
					int responseCode = urlConn.getResponseCode();
					String responseBody = "";

					try {
						BufferedReader in = new BufferedReader(
						        new InputStreamReader(urlConn.getInputStream()));
						String inputLine;
						StringBuffer response = new StringBuffer();
				 
						while ((inputLine = in.readLine()) != null) {
							response.append(inputLine);
						}
						in.close();
						responseBody = response.toString();
					} catch(IOException e){
						e.printStackTrace();
					}
					
					if ( responseCode == 200 && responseBody.contains("error") ){
						responseCode = 400;
					}
					
					JSONObject JSONResponse = new JSONObject();
					JSONResponse.put("message", responseMessage);
					JSONResponse.put("code", responseCode);
					JSONResponse.put("body", responseBody);
					if ( payload == null ){
						payload = "Nothing";
					}
					JSONResponse.put("payload_received", payload);
					return JSONResponse;
				}
				finally{
					urlConn.disconnect();
				}
				
			} catch (MalformedURLException e) {
				System.err.println("MALFORMED URL");
				e.printStackTrace();
			} catch (IOException e){
				System.err.println("IO EXCEPTION");
				e.printStackTrace();
			} catch (Exception e){
				System.err.println("EXCEPTION: " + e.getMessage());
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(JSONObject result) {
			System.out.println("RESULT OF HTTP REQUEST IS: ");
			if ( result != null ){
				System.out.println(result.toString());
			}
			else{
				System.err.println("WAS NULL");
			}
		}
	}
	
	private static JSONObject doHttpRequest(String method, String url, JSONObject headers, JSONObject payload){

		HttpRequestExecutor executor = (HttpRequestExecutor) new HttpRequestExecutor();
		executor.execute(method, url,
				headers != null ? headers.toString().trim() : null,
				payload != null ? payload.toString().trim() : null);

		try {
			return executor.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static JSONObject doGETHttpRequest(String url, JSONObject headers){
		return doHttpRequest("GET", url, headers, null);
	}
	
	public static JSONObject doPOSTHttpRequest(String url, JSONObject headers){
		return doHttpRequest("POST", url, headers, null);
	}

	public static JSONObject doPOSTHttpRequest(String url, JSONObject headers, JSONObject payload){
		return doHttpRequest("POST", url, headers, payload);
	}
	
}