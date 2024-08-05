

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.mysql.cj.conf.ConnectionUrl.Type;

/**
 * Servlet implementation class portfolioServlet
 */
@WebServlet("/portfolioServlet")
public class portfolioServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static String quoteString = "https://finnhub.io/api/v1/quote?symbol=";
	private static String profileString = "https://finnhub.io/api/v1/stock/profile2?symbol=";
	private static String key = "&token=cnseeahr01qtn496io5gcnseeahr01qtn496io60";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public portfolioServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		String username = request.getParameter("username");
		
		String stockPortfolio = JDBCTest.getStocksOwned(username);
		System.out.println("Servlet received: " + stockPortfolio);
		
		if (stockPortfolio.equals("No Stocks owned")) {
			out.print("{\"error\": \"No stocks owned\"}");
			out.flush();
			return;
		}
		
		Gson gson = new Gson();
		java.lang.reflect.Type type = new TypeToken<HashMap<String, Integer>>(){}.getType();
		HashMap<String, Integer> stocksOwned = gson.fromJson(stockPortfolio, type);
		
		StringBuilder jsonBuilder = new StringBuilder();
		jsonBuilder.append("{");
		
		for (Map.Entry<String, Integer> entry : stocksOwned.entrySet()) {
			String stockTicker = entry.getKey();
			Integer quantity = entry.getValue();
			
			System.out.println("Servlet Map: " + quantity + " stocks of " + stockTicker);
			
			try {
				String stockInfo = getStockInfo(stockTicker, quantity, username);
				jsonBuilder.append("\"").append(stockTicker).append("\":").append(stockInfo).append(",");
			} catch (Exception e) {
				System.out.println("Failed to fetch stock infor for: " + stockTicker);
				e.printStackTrace();
			}
			
		}
		
		if (jsonBuilder.length() > 1) {
			jsonBuilder.setLength(jsonBuilder.length() - 1);
		}
		jsonBuilder.append("}");
		
		System.out.println("Json Being sent: " + jsonBuilder.toString());
		
		out.print(jsonBuilder.toString());
		out.flush();	
		
	}

	public static String getStockInfo(String stockTicker, Integer quantity, String username) {
		double numStocks = Double.valueOf(quantity);
		URI quoteURI = null;
		URL quoteURL = null;
		URI profileURI = null;
		URL profileURL = null;
		
		String fullQuote = quoteString + stockTicker + key;
		String fullProfile = profileString + stockTicker + key;
		try {
			quoteURI = new URI(fullQuote);
			quoteURL = quoteURI.toURL(); 
			profileURI = new URI(fullProfile);
			profileURL = profileURI.toURL();
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
			double change = jsonObject.get("d").getAsDouble();
			double marketValue = currentPrice * numStocks;
			double totalCost = JDBCTest.getTotalCost(stockTicker, username);
			double avgCostPerShare = totalCost / numStocks; 
		
			// Create Json String of {stock1: stockTicker, quantity, change, totalCost}
			JsonObject quoteObject = new JsonObject();
			quoteObject.addProperty("stockTicker", stockTicker);
			quoteObject.addProperty("quantity", quantity);
			quoteObject.addProperty("change", change);
			quoteObject.addProperty("currentPrice", currentPrice);
			quoteObject.addProperty("marketValue", marketValue);
			quoteObject.addProperty("totalCost", totalCost);
			quoteObject.addProperty("avgCostPerShare", avgCostPerShare);
			
			HttpURLConnection profileConnection = (HttpURLConnection) profileURL.openConnection();
			profileConnection.setRequestMethod("GET");
			br = new BufferedReader(new InputStreamReader(profileConnection.getInputStream()));
			String profileJsonString = br.readLine(); 
			JsonObject profileJsonObject = JsonParser.parseString(profileJsonString).getAsJsonObject();
			System.out.println("before"); 
			String companyName = profileJsonObject.get("name").getAsString();
			System.out.println("after");
			
			quoteObject.addProperty("companyName", companyName);
			
			return quoteObject.toString();
					
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return "No stocks owned";
		}
		return "No stocks owned";
	}
	
	private 

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
