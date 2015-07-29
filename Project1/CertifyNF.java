
import java.sql.ResultSet;
import java.util.*;
import java.sql.*;
import java.io.*;



public class CertifyNF {
    
	public static String getFileName(String s)
	{
	   if (s.indexOf(61) >=0)
	        return s.substring(s.indexOf(61)+1);
	   else return ("0");
	}
	private static String rf = "";
    
	
	
	public static void main(String[] args) {	
        Properties properties = new Properties();
		try {
			properties.load(new FileInputStream("java.ini"));
		}
		catch (Exception e) {
			e.printStackTrace();
		}	
		try {
			// Set the Vertica connection information
			Vertica v = new Vertica();
			//v.setServerAddress("129.7.243.246");
			//v.setPortNumber("5433");
			v.setUrl(properties.getProperty("url"));
			v.setUserName(properties.getProperty("username"));
			v.setPassWord(properties.getProperty("password"));
			v.setDatabaseName(properties.getProperty("database"));
			System.out.println(v.getURL());
			System.out.println(v.getUserName());
				System.out.println(v.getPassWord());
			// Push Vertica connection information to the connector.
			DatabaseConnector con = new DatabaseConnector(v);
			/* 
			 * The connector will use those information to connect.
			 * The class "Vertica" inherits from the base class "Database", which we can
			 * use to extend this connector to connect as many kinds of databases as we want.
			 */
			con.connect();
			/*********/	
			// Declare the JDBC objects.
			
			// Create a variable for the connection string.
			
			ddlParser parser = new ddlParser();
			String strReason = "";
	        	parser.fileDDL = getFileName(args[0]);
	        	parser.readDDL();
		
			// Create output file recording all the sql queries
			PrintWriter queryRecorder = new PrintWriter("NF.sql", "UTF-8");
			// Create a result table
			String tableRes = "NF";
			createResultTable(con, tableRes, queryRecorder);
	
			// Start checking normal forms
			for ( int i =0; i < parser.tblCount; i++) {
				String table = parser.tblList[i].tableName;
				if (parser.tblList[i].keyCount()+parser.tblList[i].nonkeyCount()==0) {
				
				    updateResultTable(con, tableRes, "\'"+table+"\'", "\'1NF\'", "\'N\'", "\'Table does not have columns\'",queryRecorder);
					continue;
				
				}
				String[] keysArray =  parser.tblList[i].keyNames();
				String[] nonKeysArray = parser.tblList[i].nonkeyNames();
		
				if (check1NF(con,table,keysArray,nonKeysArray,queryRecorder))
				{
					if (check2NF(con,table,keysArray,nonKeysArray,queryRecorder))
					{
						if (check3NF(con,table,keysArray,nonKeysArray,queryRecorder))
						{
							if (checkBCNF(con,table,keysArray,nonKeysArray,queryRecorder))
							{
								updateResultTable(con, tableRes, "\'"+table+"\'", "\'3NF\'", "\'Y\'", "\'\'",queryRecorder);
								updateResultTable(con, tableRes, "\'"+table+"\'", "\'BCNF\'", "\'Y\'", "\'\'",queryRecorder);
							}
							else
							{ //NOT BCNF
								updateResultTable(con, tableRes, "\'"+table+"\'", "\'3NF\'", "\'Y\'", "\'\'",queryRecorder);
								updateResultTable(con, tableRes, "\'"+table+"\'", "\'BCNF\'", "\'N\'", "\'"+rf+"\'",queryRecorder);
							}
						}
						else
						{ //NOT 3NF
							updateResultTable(con, tableRes, "\'"+table+"\'", "\'3NF\'", "\'N\'", "\'"+rf+"\'",queryRecorder);
							updateResultTable(con, tableRes, "\'"+table+"\'", "\'BCNF\'", "\'N\'", "\'not 3NF\'",queryRecorder);
						}
					}
					else
					{ //NOT 2NF
						updateResultTable(con, tableRes, "\'"+table+"\'", "\'3NF\'", "\'N\'", "\'not 2NF,"+rf+"\'",queryRecorder);
						updateResultTable(con, tableRes, "\'"+table+"\'", "\'BCNF\'", "\'N\'", "\'not 3NF\'",queryRecorder);
					}
				}
				else
				{ // NOT 1NF
					updateResultTable(con, tableRes, "\'"+table+"\'", "\'3NF\'", "\'N\'", "\'not 1NF,"+rf+"\'",queryRecorder);
					updateResultTable(con, tableRes, "\'"+table+"\'", "\'BCNF\'", "\'N\'", "\'not 3NF\'",queryRecorder);
				}
			}
			// Create output file recording the result 
			outputResultTable(con, "NF.txt", queryRecorder);	
		  	//if (con != null) try { con.disconnect(); } catch(Exception e) {}
			queryRecorder.close();
		}
	catch (Exception e) {}
	}

	private static void createResultTable(DatabaseConnector con, String name, PrintWriter queryRecorder) {
		
		String SQL = "";
        	try {
            		SQL = 	"DROP TABLE IF EXISTS " + name + "; \n" ;
					queryRecorder.println(SQL);
            		con.execute(SQL);
			SQL=	"CREATE TABLE " + name + " (\n" +
				"Name VARCHAR(100), \n" +
				"Form VARCHAR(4), \n" +
				"Y_N CHAR(1), \n" +
				"Reason VARCHAR(200));\n";
			System.out.println("Creating result table NF...");
			queryRecorder.println(SQL);
            		con.execute(SQL);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void updateResultTable(DatabaseConnector con, String name, String tableName, String tableForm, String formY_N, String reason, PrintWriter queryRecorder) {
		
		String SQL = "";
		try {
            		SQL = "INSERT INTO " + name + " \n" +
				"VALUES (" + tableName + "," +
				tableForm + "," +
				formY_N + "," +
				reason + ");\n";

			System.out.println("Updating result table NF...");
			queryRecorder.println(SQL);
            		con.execute(SQL);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void outputResultTable(DatabaseConnector con, String outFileName, PrintWriter queryRecorder) {
		
		String SQL = "";
		ResultSet rs = null;
		try {
			SQL = "SELECT * FROM NF;";
			System.out.println("Outputing result table NF into file NF.txt...");
			queryRecorder.println(SQL);
            		rs = con.executeQuery(SQL);
			PrintWriter resultTable = new PrintWriter(outFileName, "UTF-8");
			resultTable.print("Table Form Y_N Reason\n");
			while (rs.next()) {
				for (int i = 1; i < 5; i++) {
					String temp = rs.getString(i);
					resultTable.print(temp+" ");
				}
				resultTable.print("\n");
			}
			resultTable.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static LinkedHashSet<LinkedHashSet<String>> powerset(String[] set) {

		LinkedHashSet<LinkedHashSet<String>> power = new LinkedHashSet<LinkedHashSet<String>>();
		int elements = set.length;
		int powerElements = (int) Math.pow(2,elements);

		//run a binary counter for the number of power elements
		for (int i = 1; i < powerElements-1; i++) {
			//convert the binary number to a string containing n digits
			String binary = intToBinary(i, elements);
			//create a new set
			LinkedHashSet<String> innerSet = new LinkedHashSet<String>();
			//convert each digit in the current binary number to the corresponding element
			//in the given set
			for (int j = 0; j < binary.length(); j++) {
 				if (binary.charAt(j) == '1')
					innerSet.add(set[j]);
			}
			//add the new set to the power set
			power.add(innerSet);
		}
	return power;
	}
 
	private static String intToBinary(int binary, int digits) {
     
		String temp = Integer.toBinaryString(binary);
		int foundDigits = temp.length();
		String returner = temp;
		for (int i = foundDigits; i < digits; i++) {
			returner = "0" + returner;
		}
     
		return returner;
	} 

	private static String arrayToString(String[] array) {
		String st = "";
		for (int i = 0; i < array.length; i++) {
			st += array[i];
			if (i < array.length -1) {
				st += ",";
			}
		}
		return st;
	}

	private static String listToString(LinkedHashSet<String> list) {
		String st = "";
                Iterator<String> iter = list.iterator();
		while(iter.hasNext()) {
			st += iter.next();
			st += ",";
		}
		st = st.substring(0, st.length()-1);
		return st;
	}

	private static boolean checkTable(DatabaseConnector con, String tableName) {
		DatabaseMetaData metadata = con.getMetaData();
		ResultSet resultSet;
		try{
			resultSet = metadata.getTables(null, null, tableName, null);
			if(resultSet.next()) return true;
			else return false;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private static boolean checkAttributes(DatabaseConnector con, String tableName, String[] keysArray, String[] nonKeysArray) {
		DatabaseMetaData metadata = con.getMetaData();
		ResultSet resultSet;
		
		try {
			for (int i = 0; i < keysArray.length; i++) {
				resultSet = metadata.getColumns(null, null, tableName, keysArray[i]);
				if(resultSet.next()) {}
				else return false;
			}
			for (int i = 0; i < nonKeysArray.length; i++) {
				resultSet = metadata.getColumns(null, null, tableName, nonKeysArray[i]);
				if(resultSet.next()) {}
				else return false;
			}
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	} 

	private static boolean check1NF(DatabaseConnector con, String table, String[] keysArray, String[] nonKeysArray, PrintWriter queryRecorder) {
		// Declare the JDBC objects.
		Statement stmt = null;
		ResultSet rs = null;
		String SQL = "";

		System.out.println("Checking 1NF for table " + table + "...");
		
		// Check if table and attributes exist
		if (!checkTable(con,table)) {
			rf = "table does not exist";
			return false;
		}
		if (!checkAttributes(con,table,keysArray,nonKeysArray)) {
			rf = "some attributes do not exist";
			return false;
		}
		
		rf = " ";

		boolean ans = true;
		boolean ansNew = true;
		String keys = arrayToString(keysArray);
		

        	try {
            		// Check duplicate keys.
            		SQL = "SELECT " + keys + ", COUNT(*) " +
				     "FROM " + table + " \n" +
				     "GROUP BY " + keys + " \n" +
				     "HAVING COUNT(*) > 1;\n";
            		//stmt = con.createStatement();
			queryRecorder.println(SQL);
            		rs = con.executeQuery(SQL);
			// If rs.next() is false, then there is no duplicate key in the table, we keep saying it is true that this table is of 1NF.
			ans = ans && !rs.next();
			if (ans == false) rf = "duplicate keys ";

			//Check NULL in keys.
			for (int i = 0; i < keysArray.length; i++) {
            			SQL = "SELECT * " +
					"FROM " + table + " \n" +
					"WHERE " + keysArray[i] + " IS NULL;\n"; 
            			//stmt = con.createStatement();
				queryRecorder.println(SQL);
            			rs = con.executeQuery(SQL);
				// If rs.next() is false, then there is no NULL in i_th key attribute, we keep saying it is true that this table is of 1NF.

				ansNew = !rs.next();
				if (ansNew == false && ans == true) {
					rf = "NULL in keys ";
					break;
				}	
				else if (ansNew == false && ans == false) {
					rf = "duplicate Keys,NULL in Keys";
					break;
				}
			}
			ans = ans && ansNew;
        	} 
        
		// Handle any errors that may have occurred.
		catch (Exception e) {
			e.printStackTrace();
		}

		finally {
	    		//if (stmt != null) try { stmt.close(); } catch(Exception e) {}
			if (rs != null) try { rs.close(); } catch(Exception e) {}
		}
	   	return ans;
	}
	
	private static boolean check2NF(DatabaseConnector con, String table, String[] keysArray, String[] nonKeysArray, PrintWriter queryRecorder) {
		// Declare the JDBC objects.
		Statement stmt = null;
		ResultSet rs = null;
		String SQL = "";

		rf = " ";

		boolean ans = true;
		boolean returnedData= false;
		LinkedHashSet<LinkedHashSet<String>> powersetKeys = powerset(keysArray);
		String subKeys;

		Iterator<LinkedHashSet<String>> iter = powersetKeys.iterator();

		System.out.println("Checking 2NF for table " + table + "...");

		while(iter.hasNext()) {
        		try {
				subKeys = listToString(iter.next());
				for (int i = 0; i < nonKeysArray.length; i++) {
            				// Create and execute an SQL statement that returns some data.
            				SQL = "SELECT " + subKeys + ", COUNT(*) " +
					     	"FROM " + table + " \n" +
					     	"GROUP BY " + subKeys + " \n" +
				     	     	"HAVING COUNT(DISTINCT " + nonKeysArray[i] + ") > 1;\n";
            				//stmt = con.createStatement();
					queryRecorder.println(SQL);
            				rs = con.executeQuery(SQL);
							
					// If rs.next() is false, then there exist one partial functional dependency, so it is not 2NF.
					returnedData =rs.next();
					ans = ans && returnedData;
					if (!returnedData) {
						rf = subKeys + "->" + nonKeysArray[i];
						break;
					}
				}
				if (!returnedData) break;
			}
				// Handle any errors that may have occurred.
				catch (Exception e) {
					e.printStackTrace();
				}
	        }
	 //  	if (stmt != null) try { stmt.close(); } catch(Exception e) {}
		if (rs != null) try { rs.close(); } catch(Exception e) {}
	   	return ans;
	}
        
	private static boolean check3NF(DatabaseConnector con, String table, String[] keysArray, String[] nonKeysArray, PrintWriter queryRecorder) {
		// Declare the JDBC objects.
		Statement stmt = null;
		ResultSet rs = null;
		String SQL = "";

		rf = " ";

		boolean ans = true;
		boolean returnedData = false;
		LinkedHashSet<LinkedHashSet<String>> powersetNonKeys = powerset(nonKeysArray);
		LinkedHashSet<String> tempSubsetNonKeys = new LinkedHashSet<String>();
		String subNonKeys = "";

		Iterator<LinkedHashSet<String>> iter = powersetNonKeys.iterator();

		System.out.println("Checking 3NF for table " + table + "...");

		while(iter.hasNext()) {
        		try {
				tempSubsetNonKeys = iter.next();
				subNonKeys = listToString(tempSubsetNonKeys);
				for (int i = 0; i < nonKeysArray.length; i++) {
					if (tempSubsetNonKeys.contains(nonKeysArray[i]));
					else {
            					// Create and execute an SQL statement that returns some data.
            					SQL = "SELECT " + subNonKeys + ", COUNT(*) " +
					     		"FROM " + table + " \n" +
					     		"GROUP BY " + subNonKeys + " \n" +
				     	     		"HAVING COUNT(DISTINCT " + nonKeysArray[i] + ") > 1;\n";
            		//			stmt = con.createStatement();
						queryRecorder.println(SQL);
						rs = con.executeQuery(SQL);
						// If rs.next() is false, then there exist one transitive functional dependency, so it is not 3NF.
						returnedData =rs.next();
						ans = ans && returnedData;
						if (!returnedData) {
							rf = subNonKeys + "->" + nonKeysArray[i];
							break;
						}
					}
				}
				if (!returnedData) break;
			}
				// Handle any errors that may have occurred.
				catch (Exception e) {
					e.printStackTrace();
				}
	        }
	  // 	if (stmt != null) try { stmt.close(); } catch(Exception e) {}
		if (rs != null) try { rs.close(); } catch(Exception e) {}
	   	return ans;
	}
	
	private static boolean checkBCNF(DatabaseConnector con, String table, String[] keysArray, String[] nonKeysArray, PrintWriter queryRecorder) {
		// Declare the JDBC objects.
		Statement stmt = null;
		ResultSet rs = null;
		String SQL = "";

		rf = " ";

		boolean ans = true;
		boolean returnedData = false;
		String nonKeys = arrayToString(nonKeysArray);

		System.out.println("Checking BCNF for table " + table + "...");

        	try {
			if (keysArray.length == 1) {}
			else {
				for (int i = 0; i < keysArray.length; i++) {
						// Create and execute an SQL statement that returns some data.
						SQL = "SELECT " + nonKeys + ", COUNT(*) " +
						"FROM " + table + " \n" +
						"GROUP BY " + nonKeys + " \n" +
						"HAVING COUNT(DISTINCT " + keysArray[i] + ") > 1;\n";
						queryRecorder.println(SQL);
						// If rs.next() is false, then there exist one transitive functional dependency between nonkey attributes and key attribute, so it is not BCNF.
						rs = con.executeQuery(SQL);
						returnedData =rs.next();
						ans = ans && returnedData;
						if (!returnedData) {
							rf = nonKeys + "->" + keysArray[i];
							break;
						}
				}
			}
		}
		// Handle any errors that may have occurred.
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
	   		//if (stmt != null) try { stmt.close(); } catch(Exception e) {}
			if (rs != null) try { rs.close(); } catch(Exception e) {}
		}
	   	return ans;
	}
}

