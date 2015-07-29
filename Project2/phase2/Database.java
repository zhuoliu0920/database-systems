
public class Database
{
	protected String jdbcClassName;
	protected String URLFormat;
	protected String url;
	private String serverAddress;
	private String portNumber;
	private String databaseName;
	private String userName;
	private String passWord;
	
	public String getServerAddress()
	{
		return serverAddress;
	}
	public void setServerAddress(String serverAddress)
	{
		this.serverAddress = serverAddress;
	}
	public String getUrl()
	{
		return url;
	}
	public void setUrl(String url)
	{
		this.url = url;
	}
	public String getPortNumber()
	{
		return portNumber;
	}
	public void setPortNumber(String portNumber)
	{
		this.portNumber = portNumber;
	}
	public String getDatabaseName()
	{
		return databaseName;
	}
	public void setDatabaseName(String databaseName)
	{
		this.databaseName = databaseName;
	}
	public String getUserName()
	{
		return userName;
	}
	public void setUserName(String userName)
	{
		this.userName = userName;
	}
	public String getPassWord()
	{
		return passWord;
	}
	public void setPassWord(String passWord)
	{
		this.passWord = passWord;
	}
	public String getURL()
	{
		//return String.format(URLFormat, getServerAddress(), getPortNumber(), getDatabaseName());
		return String.format(URLFormat, getUrl(), getDatabaseName());
	}

}
