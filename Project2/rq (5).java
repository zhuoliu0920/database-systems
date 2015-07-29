
import java.sql.ResultSet;
import java.util.*;
import java.sql.*;
import java.io.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import Jama.*;



public class rq {
    
	public static String getName(String x, String sep, String source)
	{
		String tmp = "";
		if (source.indexOf(x+"=") >=0) {
			tmp = source.substring(source.indexOf(x+"=")+2);
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
			"CREATE\\s+RECURSIVE\\s+VIEW\\s+R\\((\\w+)\\,(\\w+)\\,(\\w+)\\,(\\w+)\\,(\\w+)\\)\\s+AS\\s+\\(\\s*SELECT"  +
			 "\\s+1\\,\\s+(\\w+)\\,\\s+(\\w+)\\,\\s+1\\,\\s*(\\w+)\\s+FROM\\s+(\\w+)\\s+"  +
			"UNION\\s+ALL\\s+SELECT\\s+(\\w+)\\+1\\,\\s*R\\.(\\w+)\\s*\\,\\s*R\\.(\\w+)\\s*\\,(\\w.*\\w)\\s+FROM"
            ); 

            Matcher matcher = 
            pattern.matcher(inputSql);

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
			for (int g=1; g<=4; g++) {
			    System.out.println( "group:" + g);
			    System.out.println(matcher.group(g));
			}
			 if  (inputSql != "" ) return  ;
			// Get information from input
			String inTable = getName("E", ",", args[0]);
			String columnI = getName("i", ",", args[0]);
			String columnJ = getName("j", ",", args[0]);
			String columnV = getName("v", ",", args[0]);
                        if (!args[0].matches("(.*)i=(.*)j=(.*)v=(.*)k=(.*)" ) ) {
  			        System.out.println("i, j, v, k must be provided at command line, please use command like this:\n \"java -cp .:./vertica-jdk5-6.1.3-0.jar:./Jama-1.0.3.jar rq E=graph,i=a,j=b,v=value,k=12\"");
  		    		throw new  IllegalArgumentException("bad argument");
                        }
			int maxDepth = Integer.parseInt(getName("k", ",", args[0]));
			
			
			String[] colArray=  { columnI, columnJ, columnV};

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
			// Create table T and initialize result table R
			initializeTable(con, inTable, columnI, columnJ, columnV, queryRecorder);
	
			// Create Laplacian table
			boolean lap = Boolean.parseBoolean(properties.getProperty("Laplacian"));
			if (lap) {
				generateLaplacian(con, columnI, columnJ, columnV, queryRecorder);
			}

			// Use semi-naive algorithm to update result table R
			int d = 1; 
			while (d == checkResultTable(con, queryRecorder) && d < maxDepth) {
				System.out.println("Updating result table R in depth " + (d+1) + "...");
				updateResultTable(con, columnI, columnJ, columnV, queryRecorder);
				d++;
			}
			if (d == maxDepth)
				System.out.println("Reaching maximum recursion depth, stop updating...");
			else
				System.out.println("Reaching fixpoint state, stop updating...");
			//outputResultTable(con, columnI, columnJ, columnV, "RQ.txt", queryRecorder);
			System.out.println("G has cycles=" + existCircle);
		  	//if (con != null) try { con.disconnect(); } catch(Exception e) {} */
			queryRecorder.close();
		}
	catch (Exception e) {}
	}

	private static void initializeTable(DatabaseConnector con, String E, String i, String j, String v, PrintWriter queryRecorder) {
		
		String SQL = "";
        	try {
			// Generate table T from input table E
            		SQL = 	"DROP TABLE IF EXISTS T;\n" ;
			queryRecorder.println(SQL);
            		con.execute(SQL);

			System.out.println("Generating table T from input table " + E + "...");
			SQL =	"SELECT " + i + "," + j + "," +	v + "\n" +
				"INTO T\n" +
				"FROM " + E + "\n" +
				"WHERE 1=2;\n";
			queryRecorder.println(SQL);
            		con.execute(SQL);

			SQL =	"ALTER TABLE T\n" +
				"ADD COLUMN p int;\n";
			queryRecorder.println(SQL);
            		con.execute(SQL);

			SQL =	"INSERT INTO T\n" +
				"SELECT " + i + "," + j + ",MIN(" + v + "),COUNT(*)\n" +
				"FROM " + E + "\n" +
				"GROUP BY " + i + "," + j + ";\n";
			queryRecorder.println(SQL);
            		con.execute(SQL);

			// Create result table R which stores minimal paths between nodes
            		SQL = 	"DROP TABLE IF EXISTS R;\n" ;
			queryRecorder.println(SQL);
            		con.execute(SQL);

			System.out.println("Creating and initializing result table R from T...");
			SQL =	"SELECT *\n" +
				"INTO R\n" +
				"FROM T\n" +
				"WHERE 1=2;\n";
			queryRecorder.println(SQL);
            		con.execute(SQL);

			SQL =	"ALTER TABLE R\n" +
				"ADD COLUMN d int;\n";
			queryRecorder.println(SQL);
            		con.execute(SQL);

			// Initialize result table, and eliminate duplicate edges and trivial edges
			SQL =	"INSERT INTO R\n" +
				"SELECT " + i + "," + j + "," + v + ",p,1\n" +
				"FROM T\n" +
				"WHERE " + i + " <> " + j + ";\n";
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
				"SELECT " + i + "," + j + ",MIN(" + v + "),SUM(p),MIN(d)\n" +
				"FROM (\n" +
				"SELECT r1." + i + " as " + i + ", t1." + j + " as " + j + ", r1." + v + "+t1." + v + " as" + v + ", r1.p*t1.p as p, d+1 as d\n" +
				"FROM (\n" +
				"SELECT *\n" +
				"FROM R\n" +
				"WHERE d = (SELECT MAX(d) FROM R)) AS r1\n" +
				"INNER JOIN T AS t1\n" +
				"ON r1." + j + " = t1." + i + ") AS tmp\n" +
				"GROUP BY " + i + "," + j + ";\n";
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

			// Remove circles and duplicate paths
			SQL =	"DELETE FROM R\n" +
				"WHERE " + i + " = " + j + " OR (" + i + "," + j + "," + v + ") NOT IN (\n" +
				"SELECT " + i + "," + j + "," + "MIN(" + v + ")\n" + // duplicate i,j with different v
				"FROM R\n" +
				"GROUP BY " + i + "," + j + ");\n";
			queryRecorder.println(SQL);
            		con.execute(SQL);

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

	private static void generateLaplacian(DatabaseConnector con, String i, String j, String v, PrintWriter queryRecorder) {

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
	}
	
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

