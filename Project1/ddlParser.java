import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileWriter;
import java.io.PrintWriter;

public class ddlParser {
   public class Attribute
   {
		public String attName ;
		public boolean isKey;
		public Attribute( String  name, boolean key)
		{  attName= name;
		   isKey = key;
		}
   }	
   public class Table
   {
		public String tableName ;
		public Attribute[] attList;
		public int attCount;
		public Table(String name)
		{
		   tableName=name;
		   attList = new Attribute[20];
		   attCount=0;
		}
		
		public String[] keyNames()
		{ String[] arr = new String[keyCount()];
		  int c=0;
		  for (int i=0; i< attCount; i++) 
				if (attList[i].isKey)
				{ 
					arr[c++] =attList[i].attName;
				}	
          return arr;				
		}
		public String[] nonkeyNames()
		{ String[] arr = new String[nonkeyCount()];
		  int c=0;
		  for (int i=0; i< attCount; i++) 
				if (!attList[i].isKey)
				{ 
					arr[c++] =attList[i].attName;
				}	
           return arr;					
		}
		public int keyCount()
		{   int c=0;
			for (int i=0; i< attCount; i++) 
				if (attList[i].isKey) c++;
				return c;
		}
		
		public int nonkeyCount()
		{   int c=0;
			for (int i=0; i< attCount; i++) 
				if (!attList[i].isKey) c++;
				return c;
		}
		
		
		public void addAttribute(String name, boolean key)
		{ attList[attCount] = new Attribute(name, key);
		  attCount++; 
		}
		
		public void print()
		{
		   System.out.println(tableName);
		    for(int i=0; i<attCount; i++){
              System.out.println(attList[i].attName + " " + attList[i].isKey );
           } 
		}
   }
  
   private static final int MAX_KEYS = 4;
   private static final int MAX_ATTRIBUTES = 16;
   private static final String DELIM = "    ";
   private static final String JAVA_INI = "java.ini";
   private static final String NF_SQL = "NF.sql";
   private static final String NF_TXT = "NF.txt";
   public  String  fileDDL;
  
   public  Table[] tblList ;
   public int tblCount ;
/** Constructor. */
   public ddlParser()
   { tblList = new Table[20];
     tblCount =0;
   }
   
   public static boolean foundstop(String str)
   { 
     String str2 = str.trim();
     if ( str2.indexOf(';') >0 )
     {
       // System.out.println("Detected");
        return true;
     }
     return false;
   }
   public static  void print() 
   { 
      System.out.println("Hello World!"); // Display the string.
      
   }
   
   public  Table parseDDL(String cmdDDL)
   {
      int i=0;
      String strTable="";
      Table tbl;
      
      while  (Character.isLetterOrDigit(cmdDDL.charAt(i)))
      { strTable = strTable+ cmdDDL.charAt(i);
        i++;
      }
      while (cmdDDL.charAt(i) == ' ')
          i++;
      if (cmdDDL.charAt(i) == '(')
      { i++;
        // Set the table name
          tbl = new Table(strTable);
      }
      else
      { 
         System.out.println("REturning XX");
        return  new Table("&");
      }
      
      while (true)
      {
	      String strColumn="";
	      System.out.println(i);
	      while  (Character.isLetterOrDigit(cmdDDL.charAt(i)))
	      { strColumn = strColumn+ cmdDDL.charAt(i);
	        i++;
	      }
	      while (cmdDDL.charAt(i) == ' ')
	          i++;
	      
	      if (cmdDDL.charAt(i) == '(')
	      { i++;	      
		      while (cmdDDL.charAt(i) == ' ')
		          i++;		      
		      if (cmdDDL.charAt(i)=='k')
		      {
		         i++;
		          while (cmdDDL.charAt(i) == ' ')
			          i++;			      
			      if (cmdDDL.charAt(i)==')' )
			      // KEY IS being closed
			      // Add strColumn , with attribute K
			      {
			         tbl.addAttribute(strColumn,true);
			         i++;
			         if (cmdDDL.charAt(i) == ',')
			         { i++;}			         
			      }
		      }
		      else
		      { return  new Table("&");
		      }
	      }
	      else if (cmdDDL.charAt(i) == ',')
	      {  // Add strColumn , no K and continue
	         tbl.addAttribute(strColumn,false); i++;
	      }
	      else if (cmdDDL.charAt(i) == ')' )
                  
	      {  // Add strColumn and exit
	           if 		  (strColumn.length() >0 ) tbl.addAttribute(strColumn,false);           
	         return tbl;
	      }
	      else
	      { return  new Table("&");
	      }
      }
   }
   public  void readDDL( )
   {
      BufferedReader buffr;
	  
      try
      {
      	buffr = new BufferedReader( new FileReader ( fileDDL) );
      	String line, longline="";
      	// while not EOF
      	while ((line = buffr.readLine()) != null)
     	      {
     	        longline= longline+line; 
     	        System.out.println(longline);
              if (foundstop( longline) ) 
              {
     				Table tbl=parseDDL(longline);
					 tblList[tblCount]= tbl;
					 tblCount++;
     				 tbl.print();
					 
     				longline = "";
              }
              else  longline = longline ;
     	      }	
       }  catch (Exception e )
       { e.printStackTrace();}       
   }
   
}

