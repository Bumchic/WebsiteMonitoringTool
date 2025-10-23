package project;
import java.io.IOException;
import java.net.*;


public class WebsiteMonitorer{
	private URL url;
	public WebsiteMonitorer(String url) throws MalformedURLException
	{
		this.url = new URL(url);
	}
	public boolean CheckIfAlive()
	{
		int timeout = 5000;
		//this class connect with url.openConnection then check respond code
		try {
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setRequestMethod("HEAD");
			conn.setConnectTimeout(timeout);
			conn.setReadTimeout(timeout);
			int respondcode = conn.getResponseCode();
			if(respondcode == HttpURLConnection.HTTP_OK)
			{
				return true;
			}
		} catch (IOException e) {
			return false;
		} catch(Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}

}
