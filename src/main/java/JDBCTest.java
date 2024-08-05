import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.PseudoColumnUsage;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import com.google.gson.Gson;

public class JDBCTest {
	
	Statement st = null;
	ResultSet rs = null;
	static String url = "jdbc:mysql://localhost:3306/Assignment_4";
	static String username = "root";
	static String password = "password8@";
	

	
	public static String verifyUser(String loginUsername, String loginPassword) {
		Connection conn = null;
		
		try {
		    Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
		    e.printStackTrace();
		    return "fail";
		}
		// Connect
		try {
			conn = DriverManager.getConnection(url, username, password);
			
			Boolean userExists = verifyLogin(loginUsername, conn);
			
			Gson gson = new Gson();
			HashMap<String,Boolean> map = new HashMap<>();
			map.put("result", userExists); 
			
			System.out.println("data: " +  map.get("result"));
			
			conn.close(); 
			return gson.toJson(map);
		}
		catch (SQLException sqle) {
			System.out.println(sqle.getMessage());
		}
		return null;
	}
	
	public static boolean verifyLogin(String registerUsername, Connection conn) {
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		System.out.println("Verifying Login for: " + registerUsername);
		
		try {
			String sql = "SELECT COUNT(*) FROM userData WHERE username = ?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, registerUsername);
			rs = ps.executeQuery();
			
			if (rs.next()) {
				int userCount = rs.getInt(1);
				System.out.println("User Count: " + userCount);
				return userCount > 0;
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage()); 
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (ps != null) {
					ps.close();
				}
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}
		return false;
	}
	
	public static String createUser(String registerUsername, String registerPassword, String registerEmail) {
		Connection conn = null;
		PreparedStatement ps = null;
		String data = null;
                
		try {
		    Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
		    e.printStackTrace();
		    return "fail";
		}
		try {
			conn = DriverManager.getConnection(url, username, password);
			
			boolean userExists = checkUserExists(registerUsername, conn);
			
			if (userExists) {
				return null;
			}
			
			// Else create User
			String sql = "INSERT INTO userData (username, password, email) VALUES (?, ?, ?)";
			ps = conn.prepareStatement(sql);
			ps.setString(1, registerUsername);
			ps.setString(2, registerPassword);
			ps.setString(3, registerEmail);
			int result = ps.executeUpdate();
			System.out.print("creating user, result: " + result);
			Boolean userCreated = false;
			
			if (result == 1) {
				userCreated = true;
			}
			
			Gson gson = new Gson();
			HashMap<String,Boolean> map = new HashMap<>();
			map.put("result", userCreated); 
			
			System.out.println("data: " +  map.get("result"));
			return gson.toJson(map);
			 
		}
		catch (SQLException sqle) {
			System.out.println(sqle.getMessage());
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}
		return null;
	}
	
	public static boolean checkUserExists(String registerUsername, Connection conn) {
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		System.out.println("checking user");
		
		try {
			String sql = "SELECT COUNT(*) FROM userData WHERE username = ?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, registerUsername);
			rs = ps.executeQuery();
			
			System.out.println("result set" + rs.getInt(1));
			if (rs.next()) {
				return rs.getInt(1) > 0;
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage()); 
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (ps != null) {
					ps.close();
				}
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}
		return false;
	}
	
	public static String buyStock(String stockTicker, String quantity, double totalCost, String username) {
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			Connection conn = establishConnection();
			conn.setAutoCommit(false);
			
			// Get current cash balance
			String sql = "SELECT balance FROM userData WHERE username = ?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, username);
			rs = ps.executeQuery();
			double balance;
			
			if (rs.next()) {
				balance = (rs.getBigDecimal("balance")).doubleValue();
			}
			else {
				throw new SQLException("User not found");
			}
			
			Gson gson = new Gson();
			HashMap<String, Double> map = new HashMap<>();
			
			if (balance > totalCost) {
				/* Decrease balance in userData Table*/
				double adjustedBalance = balance - totalCost ;
				sql = "UPDATE userData SET balance = ? WHERE username = ?";
				ps.close();
				ps = conn.prepareStatement(sql);
				ps.setDouble(1, adjustedBalance);
				ps.setString(2, username);
				ps.executeUpdate();
				
				/*Add stocks to userPortfolio, check if stock already exists*/
				sql = "INSERT INTO userStocks (username, stockTicker, numStocks, totalCost) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE numStocks = numStocks + ?, totalCost = totalCost + ?";
				ps.close();
				ps = conn.prepareStatement(sql);
				ps.setString(1,  username);
				ps.setString(2, stockTicker);
				ps.setInt(3, Integer.parseInt(quantity));
				ps.setDouble(4, totalCost);
				ps.setInt(5, Integer.parseInt(quantity));
				ps.setDouble(6, totalCost);
				ps.executeUpdate();
				
				conn.commit();
				
				map.put("result", totalCost);
				
			}
			else {
				conn.rollback();
				map.put("result", 0.0);
			}
			
			return gson.toJson(map);
			
		} catch (SQLException sqle) {
			System.out.println(sqle.getMessage());
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (ps != null) {
					ps.close();
				}
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}
		return null;	
	}
	
	public static String sellStock(String stockTicker, String quantity, double totalProfit,  String username) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		Gson gson = new Gson();
		HashMap<String, Double> map = new HashMap<>();
		
		try {
			Connection conn = establishConnection();
			conn.setAutoCommit(false);
			// Decrease the number of stocks I have of this stockTicker by quantity in userStocks table
			// If remaining numStocks will be 0, remove stockTicker from userStocks table completely
			String sql = "SELECT numStocks FROM userStocks WHERE username = ? AND stockTicker = ?";
			ps = conn.prepareStatement(sql);
			ps.setString(1,username);
			ps.setString(2, stockTicker);
			rs = ps.executeQuery();
			
			if (!rs.next()) {
				map.put("result", 0.0);
				throw new SQLException("Stock not owned by user");
			}
			
			int numStocks = rs.getInt("numStocks");
			int sellQuantity = Integer.parseInt(quantity);
			
			// Delete stock from table if sale zeroes out numStocks
			if (numStocks == sellQuantity) {
				sql = "DELETE FROM userStocks WHERE username = ? AND stockTicker = ?";
				ps.close();
				ps = conn.prepareStatement(sql);
				ps.setString(1, username);
				ps.setString(2, stockTicker);
				ps.executeUpdate();
			}
			else {
				sql = "UPDATE userStocks SET numStocks = numStocks - ?, totalCost = totalCost - ? WHERE username = ? AND stockTicker = ?";
				ps.close();
				ps = conn.prepareStatement(sql);
				ps.setInt(1, sellQuantity);
				ps.setDouble(2, totalProfit);
				ps.setString(3, username);
				ps.setString(4, stockTicker);
				ps.executeUpdate();
			}
			
			// Update user balance, increasing by totalProfit
			sql = "UPDATE userData SET balance = balance + ? WHERE username = ?";
			ps.close();
			ps = conn.prepareStatement(sql);
			ps.setDouble(1, totalProfit);
			ps.setString(2, username);
			ps.executeUpdate();
			
			conn.commit();
			
			// Based on the profit earned, adjust balance in userData table here
			map.put("result", totalProfit);
	
			return gson.toJson(map);
			
		} catch (SQLException sqle) {
			System.out.println(sqle.getMessage());
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (ps != null) {
					ps.close();
				}
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}
		return gson.toJson(map);	
	}
	
	public static String getStocksOwned(String username) {
		Gson gson = new Gson();
		HashMap<String, Integer> map = new HashMap<>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		
		try {
			conn = establishConnection();
			
			String sql = "SELECT stockTicker, numStocks FROM userStocks WHERE username = ?"; 
			ps = conn.prepareStatement(sql);
			ps.setString(1, username);
			rs = ps.executeQuery();
			
			boolean hasStocks = false;
			
			while (rs.next()) {
				hasStocks = true;
				String stockTicker = rs.getString("stockTicker");
				int numStocks = rs.getInt("numStocks");
				map.put(stockTicker, numStocks);
				System.out.println(numStocks + " stocks of " + stockTicker);
			}
			
			if (!hasStocks) {
				System.out.println("aqui");
				return "No Stocks owned";
			}
			conn.close();
			
			return gson.toJson(map);
			
		} catch (SQLException sqle) {
			System.out.println(sqle.getMessage());
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
				if (rs != null) {
					rs.close();
				} 
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}
		
		return gson.toJson(map);
	}
	
	public static String getBalance(String username) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		Gson gson = new Gson();
		HashMap<String, Double> map = new HashMap<>();
		
		try {
			conn = establishConnection();
			conn.setAutoCommit(false);
			
			// Get current cash balance
			String sql = "SELECT balance FROM userData WHERE username = ?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, username);
			rs = ps.executeQuery();
			double balance;
			
			if (rs.next()) {
				balance = (rs.getBigDecimal("balance")).doubleValue();
				
			}
			else {
				throw new SQLException("User not found");
			}
			
			System.out.println("Balance: " + balance);
			
			map.put("balance", balance);
			return gson.toJson(map);
			
		} catch (SQLException sqle) {
			System.out.println(sqle.getMessage());
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.close(); 
				}
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}
		return gson.toJson("Unable to retrieve balance");
	}
	
	public static double getTotalCost(String stockTicker, String username) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		
		try {
			conn = establishConnection();
			String sql = "SELECT totalCost FROM userStocks WHERE username = ? AND stockTicker = ?";
			ps = conn.prepareStatement(sql);
			ps.setString(1,username);
			ps.setString(2, stockTicker);
			rs = ps.executeQuery();
			
			if (!rs.next()) {
				throw new SQLException("Stock not owned by user");
			}
			
			double totalCost = rs.getDouble("totalCost");
			return totalCost;
		} catch (SQLException sqle) {
			System.out.println(sqle.getMessage());
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.close(); 
				}
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}
		return 0.0;
	}
	
	private static Connection establishConnection() {
		Connection conn = null;
		try {
		    Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
		    e.printStackTrace();
		    return null;
		}
		// Connect
		try {
			conn = DriverManager.getConnection(url, username, password);
		}
		catch (SQLException sqle) {
			System.out.println(sqle.getMessage());
		} 
		return conn;
	}
	

}
