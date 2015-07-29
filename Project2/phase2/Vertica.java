
public class Vertica extends Database
{
	Vertica()
	{
		jdbcClassName = "com.vertica.jdbc.Driver";
		//URLFormat = "jdbc:vertica://%s:%s/%s";
         URLFormat = "%s/%s";
		//Load the Vertica jdbc class at initializing.
		try
		{
			Class.forName(jdbcClassName);
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
	}
	
}
