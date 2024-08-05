

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class TradeServlet
 */
@WebServlet("/TradeServlet")
public class TradeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TradeServlet() {
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
		String stockTicker = request.getParameter("stockTicker");
		String quantity = request.getParameter("quantity");
		String buySellChoice = request.getParameter("buySellChoice");
		String username = request.getParameter("username");
		String tradeExecuted = null;
		
		System.out.println("stockTicker: " + stockTicker);
		System.out.println("quantity: " + quantity);
		System.out.println("buySellChoice: " + buySellChoice);
		System.out.println("username " + username);
		
		
		if (buySellChoice.equals("buy")){
			double totalCost = MyServlet.getTotal(stockTicker, quantity);
			tradeExecuted = JDBCTest.buyStock(stockTicker, quantity, totalCost, username);
		}
		else {
			System.out.println("selling");
			double totalProfit = MyServlet.getTotal(stockTicker, quantity);
			System.out.println("profit: " + totalProfit); 
			tradeExecuted = JDBCTest.sellStock(stockTicker, quantity, totalProfit, username);
		}
		
		System.out.println("Servlet received: " + tradeExecuted);
		
	    out.print(tradeExecuted);
	    out.flush();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
