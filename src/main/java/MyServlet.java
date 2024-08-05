

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.io.InputStreamReader;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


/**
 * Servlet implementation class MyServlet
 */
@WebServlet("/MyServlet")
public class MyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static String quoteString = "https://finnhub.io/api/v1/quote?symbol=";
	private static String profileString = "https://finnhub.io/api/v1/stock/profile2?symbol=";
	private static String key = "&token=cnseeahr01qtn496io5gcnseeahr01qtn496io60";
       
    
    public MyServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		String stockTicker = request.getParameter("stockTicker");
		System.out.println("ticker: " + stockTicker);
		
		URI quoteURI = null;
		URI profileURI = null;
		String fullQuote = quoteString + stockTicker + key;
		String fullProfile = profileString + stockTicker + key;
		
		System.out.println("fullQuote: " + fullQuote);
		System.out.println("fullProfile: " + fullProfile);
		
		try {
			quoteURI = new URI(fullQuote);
			profileURI = new URI(fullProfile);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("quote URI: " + quoteURI);
		URL quoteURL = quoteURI.toURL(); 
		URL profileURL = profileURI.toURL();
		
		// Merge the Json Strings into 1 Json String;
		String quoteString = getJsonString(quoteURL);
		String profileString = getJsonString(profileURL);
		String mergedString = "{" +
                "\"quote\": " + quoteString + "," +
                "\"profile\": " + profileString +
            "}";
		
		System.out.println("quote: " + quoteString);
		System.out.println("profile: " + profileString);
		System.out.println("merged: " + mergedString);
	
	    out.print(mergedString);
	    out.flush();
		
	}
	
	private String getJsonString(URL url) {
		String jsonString = null;
		try{
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			jsonString = br.readLine(); 
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		}
		return jsonString;
	}
	
	public static double getTotal(String stockTicker, String quantity) {
		double numStocks = Double.parseDouble(quantity);
		URI quoteURI = null;
		URL quoteURL = null;
		String fullQuote = quoteString + stockTicker + key;
		try {
			quoteURI = new URI(fullQuote);
			quoteURL = quoteURI.toURL(); 
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			HttpURLConnection connection = (HttpURLConnection) quoteURL.openConnection();
			connection.setRequestMethod("GET");
			BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String jsonString = br.readLine(); 
			JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
			double currentPrice = jsonObject.get("c").getAsDouble();
			System.out.println("Total: " + numStocks * currentPrice);
			return numStocks * currentPrice;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
