
import java.sql.ResultSet;
import java.util.*;
import java.sql.*;
import java.io.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
//import Jama.*;



public class rq {
    
	public static String getName(String x, String sep, String source)
	{
		String tmp = "";
		if (source.indexOf(x+"=") >=0) {
			tmp = source.substring(source.indexOf(x+"=")+x.length()+1);
			if (tmp.indexOf(sep) >= 0) {
				return(tmp.substring(0, tmp.indexOf(sep)));
			}
			else return (tmp.substring(0));
		}
		else return ("0");
	}
	private static boolean existCircle = false;

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
			// Push Vertica connection information to the connector.
			DatabaseConnector con = new DatabaseConnector(v);
			/* 
			 * The connector will use those information to connect.
			 * The class "Vertica" inherits from the base class "Database", which we can
			 * use to extend this connector to connect as many kinds of databases as we want.
			 */
			con.connect();
			/*********/	
			String inputSql = readFile("query.sql");
			System.out.println(inputSql);
			Pattern pattern = 
		Pattern.compile(
	"CREATE\\s+RECURSIVE\\s+VIEW\\s+R\\s*\\(\\s*(\\w+)\\s*\\,\\s*(\\w+)\\s*\\,\\s*(\\w+)\\s*\\,\\s*(\\w+)\\s*\\,\\s*(\\w+)\\s*\\)\\s+AS" + 
	"\\s+\\(\\s*SELECT\\s+1\\s*\\,\\s+(\\w+)\\s*\\,\\s+(\\w+)\\s*\\,\\s+1\\s*\\,\\s*(\\w+)\\s+FROM\\s+(\\w+)\\s+"  +
	"UNION\\s+ALL\\s+SELECT\\s+(\\w+)\\s*\\+\\s*1\\s*\\,\\s*R\\s*\\.\\s*(\\w+)\\s*\\,\\s*R\\s*\\.\\s*(\\w+)\\s*\\,\\s*"+
	"(\\w.*\\w)\\s+FROM\\s+R\\s+JOIN.*ON.*WHERE\\s+(\\w+)\\s*\\<\\s*(\\d+)\\s*\\)\\s*;\\s*(SELECT.*);", Pattern.CASE_INSENSITIVE
		);
			Matcher matcher = pattern.matcher(inputSql);

            boolean found = false;
            while (matcher.find()) {
                System.out.format("I found the text" +
                    " \"%s\" starting at " +
                    "index %d and ending at index %d.%n",
                    matcher.group(),
                    matcher.start(),
                    matcher.end());  
                found = true;
            }
			matcher.reset();
			matcher.find();
			for (int g=1; g<=matcher.groupCount(); g++) {
			    System.out.println( "group:" + g+  "="+matcher.group(g));
			    
			}
			// Get information from input
			String O1Str = getName("O1", ",", args[0]);
			String O2Str = getName("O2", ",", args[0]);
			boolean O1 = (O1Str.equals("Y"))?true:false;
			boolean O2 = (O2Str.equals("Y"))?true:false;
			String inTable = matcher.group(9);   //G9
			String columnI = matcher.group(2);   //G2
			String columnJ = matcher.group(3);   //G3
			String columnV = matcher.group(5);  //G5
			int maxDepth = Integer.valueOf(matcher.group(15));
			String finalQuery = matcher.group(16);

			Pattern patternforO1 = Pattern.compile(
			"SELECT\\s+DISTINCT\\s+(\\w+)\\s*((\\,\\s*\\w+\\s*)+)?(INTO\\s+(\\w+))?\\s+FROM\\s+(\\w+)\\s*", Pattern.CASE_INSENSITIVE
			);
			Matcher matcherforO1 = patternforO1.matcher(finalQuery);

			matcherforO1.reset();
			matcherforO1.find();
			for (int g=1; g<=matcherforO1.groupCount(); g++) {
			    System.out.println( "group:" + g+  "="+matcherforO1.group(g));
			    
			}

			String tableO1 = matcherforO1.group(matcherforO1.groupCount());
			String columnO1 = "";
			String finalTableName = matcherforO1.group(matcherforO1.groupCount()-1);
			boolean isINTO = (matcherforO1.group(matcherforO1.groupCount()-2)==null)?false:true;
			if (matcherforO1.group(2) == null) {
				columnO1 = matcherforO1.group(1);
			}
			else {
				columnO1 = matcherforO1.group(1) + matcherforO1.group(2);
			}
			System.out.println(columnO1);
			
			// case1: select distinct i from R or select distinct j from R
			boolean case1 = false;
			if (columnO1.equals(columnI) || columnO1.equals(columnJ)) {
				case1 = true;
			}
			
			// case2: select distinct i,j,(...) from R
			boolean case2 = false;
			if (columnO1.indexOf(columnI) >= 0 && columnO1.indexOf(columnJ) >= 0) {
				case2 = true;
			}

			boolean vExist = (columnO1.indexOf(columnV) >= 0)?true:false;
			boolean pExist = (columnO1.indexOf(matcher.group(4)) >= 0)?true:false;
			boolean dExist = (columnO1.indexOf(matcher.group(1)) >= 0)?true:false;

			if (!tableO1.equals("R")) {
				case1=false;
				case2=false;
				vExist=true;
				pExist=true;
				dExist=true;
			}
System.out.println(case1);
System.out.println(case2);
System.out.println(vExist);
System.out.println(pExist);
System.out.println(dExist);
			
			finalQuery = finalQuery.replace(matcher.group(4),"p");
			finalQuery = finalQuery.replace(matcher.group(1),"d");

			String[] colArray=  {columnI, columnJ, columnV};

			// Create output file recording all the sql queries
			PrintWriter queryRecorder = new PrintWriter("RQ.sql", "UTF-8");
			// Verify input table
  		        if ( !checkTable( con,  inTable)) {
  			        System.out.println("Table not found");
  		    		throw new  SQLException();
  		  	}
			if ( !checkAttributes(con, inTable, colArray) ) {
				System.out.println("Column not found");
				throw new  SQLException();
			}

			columnI = "\"" + columnI + "\"";
			columnJ = "\"" + columnJ + "\"";
			columnV = "\"" + columnV + "\"";

			// Optimize (SELECT DISTINCT)
			// case1: (select distinct i, or select distinct j)
			if (O1 && case1) {
				String SQL = "";
				System.out.println("Creating and initializing recursive table R...");

            			SQL = 	"DROP TABLE IF EXISTS R;\n" ;
				queryRecorder.println(SQL);
            			con.execute(SQL);
			
				SQL =	"SELECT " + columnI + "\n" +
					"INTO R\n" +
					"FROM " + inTable + "\n" +
					"WHERE 1=2;\n";
				queryRecorder.println(SQL);
	       	     		con.execute(SQL);

				SQL =	"INSERT INTO R\n" +
					"SELECT DISTINCT " + columnI + "\n" +
					"FROM " + inTable + ";\n";
				queryRecorder.println(SQL);
	       	     		con.execute(SQL);

				generateFinal( con, finalQuery, isINTO, finalTableName, queryRecorder);
				queryRecorder.close();
			}
			// case2: (select distinct (d,)?i,j)
			else if (O1 && case2) {
				initializeTableO1(con, inTable, columnI, columnJ, columnV, vExist, pExist, queryRecorder);
				int d = 1; 
				while (d == checkResultTable(con, queryRecorder) && d < maxDepth) {
					System.out.println("Updating result table R in depth " + (d+1) + "...");
					updateResultTableO1(con, columnI, columnJ, columnV, vExist, pExist, queryRecorder);
					if (!dExist) { // (select distinct i,j or i,j,p or i,j,v or i,j,p,v)
						rmDuplicateRows(con, columnI, columnJ, queryRecorder);
					}
					d++;
				}
				if  (d == maxDepth)
					System.out.println("Reaching maximum recursion depth, stop updating...");
				else
					System.out.println("Reaching fixpoint state, stop updating...");
				
				System.out.println("G has cycles=" + existCircle);
				generateFinal( con, finalQuery, isINTO, finalTableName, queryRecorder);
				queryRecorder.close();
			}
			// other cases: no optimization
			else {
				// Create table T and initialize result table R
				initializeTable(con, inTable, columnI, columnJ, columnV, queryRecorder);
 
				// Use semi-naive algorithm to update result table R
				int d = 1; 
				while (d == checkResultTable(con, queryRecorder) && d < maxDepth) {
					System.out.println("Updating result table R in depth " + (d+1) + "...");
					updateResultTable(con, columnI, columnJ, columnV, queryRecorder);
					d++;
				}
				if  (d == maxDepth)
					System.out.println("Reaching maximum recursion depth, stop updating...");
				else
					System.out.println("Reaching fixpoint state, stop updating...");

				System.out.println("G has cycles=" + existCircle);
				generateFinal( con, finalQuery, isINTO, finalTableName, queryRecorder);
				queryRecorder.close();
			}
		}
	catch (Exception e) {}
	}

	private static void initializeTable(DatabaseConnector con, String E, String i, String j, String v, PrintWriter queryRecorder) {
		
		String SQL = "";
        	try {
			// Create recursive table R 
			System.out.println("Creating and initializing recursive table R...");

            		SQL = 	"DROP TABLE IF EXISTS R;\n" ;
			queryRecorder.println(SQL);
            		con.execute(SQL);

			SQL =	"SELECT " + i + "," + j + "," +	v + "\n" +
				"INTO R\n" +
				"FROM " + E + "\n" +
				"WHERE 1=2;\n";
			queryRecorder.println(SQL);
            		con.execute(SQL);

			SQL =	"ALTER TABLE R\n" +
				"ADD COLUMN p int;\n";
			queryRecorder.println(SQL);
            		con.execute(SQL);

			SQL =	"ALTER TABLE R\n" +
				"ADD COLUMN d int;\n";
			queryRecorder.println(SQL);
            		con.execute(SQL);

			SQL = 	"INSERT INTO R\n" +
				"SELECT " + i + "," + j + "," +	v + ",1,1\n" +
				"FROM " + E + ";\n";
			queryRecorder.println(SQL);
            		con.execute(SQL);

            		SQL = 	"DROP TABLE IF EXISTS Rbase;\n" ;
			queryRecorder.println(SQL);
            		con.execute(SQL);

			SQL =	"SELECT *\n" +
				"INTO Rbase\n" +
				"FROM R\n" +
				"WHERE 1=2;\n";
			queryRecorder.println(SQL);
            		con.execute(SQL);

			SQL = 	"INSERT INTO Rbase\n" +
				"SELECT *\n" +
				"FROM R\n" +
				"WHERE " + i + "<>" + j + ";\n";
			queryRecorder.println(SQL);
            		con.execute(SQL);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void updateResultTable(DatabaseConnector con, String i, String j, String v, PrintWriter queryRecorder) {
		
		String SQL = "";
		ResultSet rs = null;
		try {
			// Insert paths with depth d+1 into result table
            		SQL =	"INSERT INTO R\n" +
				"SELECT r1." + i + " as " + i + ", t1." + j + " as " + j + ", r1." + v + "+t1." + v + " as " + v + ", r1.p*t1.p as p, r1.d+1 as d\n" +
				"FROM (\n" +
				"(SELECT *\n" +
				"FROM R\n" +
				"WHERE d = (SELECT MAX(d) FROM R) AND " + i + "<>" + j + ") AS r1\n" +
				"INNER JOIN Rbase AS t1\n" +
				"ON r1." + j + " = t1." + i + ") AS tmp;\n";
			queryRecorder.println(SQL);
            		con.execute(SQL);

			// Detect circles in paths with depth d+1
			SQL =	"SELECT *\n" +
				"FROM R\n" +
				"WHERE d = (SELECT MAX(d) FROM R) AND " + i + "= " + j + ";\n";
			queryRecorder.println(SQL);
			rs = con.executeQuery(SQL);
			if (rs.next()) {
				existCircle = true;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void initializeTableO1(DatabaseConnector con, String E, String i, String j, String v, boolean vExist, boolean pExist, PrintWriter queryRecorder) {
		
		String SQL = "";
		String cols = i + "," + j;
		if (vExist == true)
			cols += "," + v;
        	try {
			// Create recursive table R 
			System.out.println("Creating and initializing recursive table R...");

            		SQL = 	"DROP TABLE IF EXISTS R;\n" ;
			queryRecorder.println(SQL);
            		con.execute(SQL);

			SQL =	"SELECT " + cols + "\n" +
				"INTO R\n" +
				"FROM " + E + "\n" +
				"WHERE 1=2;\n";
			queryRecorder.println(SQL);
            		con.execute(SQL);

			SQL =	"ALTER TABLE R\n" +
				"ADD COLUMN d int;\n";
			queryRecorder.println(SQL);
       			con.execute(SQL);

			if (pExist == true) {
				SQL =	"ALTER TABLE R\n" +
					"ADD COLUMN p int;\n";
				queryRecorder.println(SQL);
            			con.execute(SQL);

				SQL = 	"INSERT INTO R\n" +
					"SELECT DISTINCT " + cols + ",1,1\n" +
					"FROM " + E + ";\n";
				queryRecorder.println(SQL);
       		     		con.execute(SQL);
			}
			else {
				SQL = 	"INSERT INTO R\n" +
					"SELECT DISTINCT " + cols + ",1\n" +
					"FROM " + E + ";\n";
				queryRecorder.println(SQL);
            			con.execute(SQL);
			}

			// create a table which saves the base case
            		SQL = 	"DROP TABLE IF EXISTS Rbase;\n" ;
			queryRecorder.println(SQL);
            		con.execute(SQL);

			SQL =	"SELECT *\n" +
				"INTO Rbase\n" +
				"FROM R\n" +
				"WHERE 1=2;\n";
			queryRecorder.println(SQL);
            		con.execute(SQL);

			SQL = 	"INSERT INTO Rbase\n" +
				"SELECT *\n" +
				"FROM R\n" +
				"WHERE " + i + "<>" + j + ";\n";
			queryRecorder.println(SQL);
            		con.execute(SQL);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void updateResultTableO1(DatabaseConnector con, String i, String j, String v, boolean vExist, boolean pExist, PrintWriter queryRecorder) {
		
		String SQL = "";
		String target = "";
		ResultSet rs = null;
		try {
			target = "r1." + i + " as " + i + ", t1." + j + " as " + j + ", ";
			if (vExist == true)
				target += "r1." + v + "+t1." + v + " as " + v + ", ";
			if (pExist == true)
				target += "r1.p*t1.p as p, ";
			target += "r1.d+1 as d\n";
			// Insert paths with depth d+1 into result table
            		SQL =	"INSERT INTO R\n" +
				"SELECT DISTINCT " + target +
				"FROM (\n" +
				"(SELECT *\n" +
				"FROM R\n" +
				"WHERE d = (SELECT MAX(d) FROM R) AND " + i + "<>" + j + ") AS r1\n" +
				"INNER JOIN Rbase AS t1\n" +
				"ON r1." + j + " = t1." + i + ") AS tmp;\n";
			queryRecorder.println(SQL);
            		con.execute(SQL);

			// Detect circles in paths with depth d+1
			if (existCircle == false) {
				SQL =	"SELECT *\n" +
					"FROM R\n" +
					"WHERE d = (SELECT MAX(d) FROM R) AND " + i + "= " + j + ";\n";
				queryRecorder.println(SQL);
				rs = con.executeQuery(SQL);
				if (rs.next()) {
					existCircle = true;
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void rmDuplicateRows(DatabaseConnector con, String i, String j, PrintWriter queryRecorder) {
		String SQL = "";
		try {
			// Remove circles?
			// Remove duplicate paths (with different d)
			SQL =	"DELETE FROM R\n" +
				"WHERE (d," + i + "," + j + ") NOT IN (\n" +
				"SELECT MIN(d)," + i + "," + j + "\n" + // duplicate i,j with different d
				"FROM R\n" +
				"GROUP BY " + i + "," + j + ");\n";
			queryRecorder.println(SQL);
            		con.execute(SQL);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static int checkResultTable(DatabaseConnector con, PrintWriter queryRecorder) {
		
		String SQL = "";
		ResultSet rs = null;
		try {
			SQL =	"SELECT MAX(d)\n" +
				"FROM R;\n";
			queryRecorder.println(SQL);
			rs = con.executeQuery(SQL);
			if (rs.next()) {
				return (rs.getInt("MAX"));	
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	private static void outputResultTable(DatabaseConnector con, String i, String j, String v, String outFileName, PrintWriter queryRecorder) {
		
		String SQL = "";
		ResultSet rs = null;
		try {
			SQL = "SELECT " + i + "," + j + "," + v + " FROM R;\n";
			System.out.println("Outputing shortest paths from table R into file " + outFileName + "...");
			queryRecorder.println(SQL);
            		rs = con.executeQuery(SQL);
			PrintWriter resultTable = new PrintWriter(outFileName, "UTF-8");
			resultTable.print(i + " " + j + " " + v + "\n");
			while (rs.next()) {
				for (int k = 1; k < 4; k++) {
					String temp = rs.getString(k);
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

/*	private static void generateLaplacian(DatabaseConnector con, String i, String j, String v, PrintWriter queryRecorder) {

		String SQL = "";
		ResultSet rs = null;
		int numNode = 0;
		try {
			System.out.println("Generating Laplacian matrix table...");
			// Determine the number of nodes
            		SQL =	"SELECT COUNT(*)\n" + 
				"FROM (\n" + 
				"SELECT " + i + " AS node FROM T\n" +
				"UNION\n" + 
				"SELECT " + j + " AS node FROM T) AS tmp;\n";
			queryRecorder.println(SQL);
            		rs = con.executeQuery(SQL);
			if (rs.next()) 	
				numNode = rs.getInt(1);

			// Create Laplacian matrix table
			String[] nodes;
			nodes = new String[numNode];

            		SQL = 	"DROP TABLE IF EXISTS Laplacian;\n";
			queryRecorder.println(SQL);
            		con.execute(SQL);

			String tmpSQL = "CREATE TABLE Laplacian (\n";
			SQL =	"SELECT " + i + " AS node FROM T\n" +
				"UNION\n" + 
				"SELECT " + j + " AS node FROM T;\n";
			queryRecorder.println(SQL);
            		rs = con.executeQuery(SQL);
			int k = 0;
			while (rs.next()) {
				nodes[k] = rs.getString(1);
				if (k == numNode -1) 
					tmpSQL = tmpSQL + "\"" + nodes[k] + "\"" + " int);\n";
				else
					tmpSQL = tmpSQL + "\"" + nodes[k] + "\"" + " int,\n";
				k = k+1;
			}
			SQL = tmpSQL;
			queryRecorder.println(SQL);
            		con.execute(SQL);

			// Insert values into Laplacian Matrix table
			int l = 0;
			int[] tmpRow = new int[numNode];
			double[][] lapMat = new double[numNode][numNode];
			for (k = 0; k < numNode; k++) {
				tmpSQL = "INSERT INTO Laplacian VALUES (";
				for (l = 0; l < numNode; l++) {
					if (l == k) {
						SQL =	"SELECT COUNT(*) FROM\n" +
							"(SELECT DISTINCT " + i + "," + j + "\n" +
							"FROM T\n" +
							"WHERE " + i + "<>" + j + " AND " + i + "=\'" + nodes[k] + "\' OR (\n" +
							j + "=\'" + nodes[k] + "\' AND " + i + " NOT IN (\n" +
							"SELECT " + j + " FROM T WHERE " + i + "=\'" + nodes[k] + "\'))) as tmp;\n";
						queryRecorder.println(SQL);
            					rs = con.executeQuery(SQL);
						if (rs.next()) {
							tmpRow[l] = rs.getInt(1);
							lapMat[k][l] = (double) tmpRow[l];
						}
						else {
							tmpRow[l] = 0;
							lapMat[k][l] = 0;
						}
					}
					else {
						SQL =	"SELECT " + i + "," + j + "\n" +
							"FROM T\n" +
							"WHERE (" + i + "=\'" + nodes[k] + "\' AND " + j + "=\'" + nodes[l] + "\') OR (" + j + "=\'" + nodes[k] + "\' AND " + i + "=\'" + nodes[l] + "\');\n";
						queryRecorder.println(SQL);
            					rs = con.executeQuery(SQL);
						if (rs.next()) {
							tmpRow[l] = -1;
							lapMat[k][l] = -1;
						}
						else {
							tmpRow[l] = 0;
							lapMat[k][l] = 0;
						}
					}
					tmpSQL = tmpSQL + tmpRow[l] + ",";
				}
				SQL = tmpSQL.substring(0,tmpSQL.length()-1) + ");\n";
				queryRecorder.println(SQL);
            			con.execute(SQL);
			}

			// Find 2nd smallest eigenvalue of Laplacian matrix
			Matrix L = new Matrix(lapMat);
			EigenvalueDecomposition ED = new EigenvalueDecomposition(L);
			double[] eig = ED.getRealEigenvalues();
			System.out.println("The second smallest eigenvalues for Laplacian matrix is: " + eig[1]);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	} */
	
/*	private static String arrayToString(String[] array) {
		String st = "";
		for (int i = 0; i < array.length; i++) {
			st += array[i];
			if (i < array.length -1) {
				st += ",";
			}
		}
		return st;
	}
*/

private static void generateFinal(DatabaseConnector con, String queryFinal, boolean isINTO, String finalTableName, PrintWriter queryRecorder) {
		
		String SQL = "";
        	try {
			if (isINTO == false) {
        	    		SQL = 	"DROP TABLE IF EXISTS Q;\n" ;
				queryRecorder.println(SQL);
	            		con.execute(SQL);
 
				System.out.println("Generating table Q" );
				SQL ="SELECT  * \n" +
					"INTO Q\n" +
					"FROM (" + queryFinal +") as F; \n" ; 
				
				queryRecorder.println(SQL);
        	    		con.execute(SQL);
			}
			else {
        	    		SQL = 	"DROP TABLE IF EXISTS " + finalTableName + ";\n" ;
				queryRecorder.println(SQL);
	            		con.execute(SQL);
 
				System.out.println("Generating table " + finalTableName);
				SQL = queryFinal; 
				
				queryRecorder.println(SQL);
        	    		con.execute(SQL);
 			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
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

	private static boolean checkAttributes(DatabaseConnector con, String tableName, String[] colArray) {
		DatabaseMetaData metadata = con.getMetaData();
		ResultSet resultSet;
		
		try {
			for (int i = 0; i < colArray.length; i++) {
				resultSet = metadata.getColumns(null, null, tableName, colArray[i]);
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
	
private static  String readFile( String fileName)
   {
      BufferedReader buffr;
	  String outputLine ="";
	  
	  
      try
      {
      	buffr = new BufferedReader( new FileReader ( fileName) );
      	String line, longline="";
      	while ((line = buffr.readLine()) != null)
     	      {
     	             outputLine += line+ " ";;
	           
     	      }	
	    
       }  catch (Exception e )
       { e.printStackTrace();}   
       return outputLine;	   
   }
   
}

