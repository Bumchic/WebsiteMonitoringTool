package program;
import java.net.*;


public class WebsiteMonitorer {
	private InetAddress addr;
	public WebsiteMonitorer(String address) throws UnknownHostException
	{
		this.addr = InetAddress.getByName(address);
		System.out.println(addr.getHostName());
		
	}
//	public boolean CheckIfAlive()
//	{
//		addr.g
//	}
}
