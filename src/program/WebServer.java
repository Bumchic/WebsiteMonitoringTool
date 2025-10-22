package program;
import java.net.*;
import java.nio.file.Files;
import java.util.*;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;


public class WebServer {
	public static void main(String[] args)
	{
		ServerSocket server;
		InetAddress addr;
		Path serverfolder;
		WebsiteMonitorAdvanced monitor;
		try
		{
			addr = InetAddress.getByName("localhost");
			server = new ServerSocket(6789, 50, addr);
			monitor = new WebsiteMonitorAdvanced("http://localhost:6789");
			serverfolder = Paths.get("C:\\Users\\Bumchic\\Documents\\GitHub\\WebsiteMonitoringTool\\index.html");
			while(true)
			{
				Socket client = server.accept();
				//System.out.println(client.getPort());
				clienthandler clienthandler = new clienthandler(client, serverfolder, monitor);
				monitor.IncreaseCount();
				clienthandler.start();
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
}
class clienthandler extends Thread
{
	private Socket client;
	private PrintWriter writer;
	private BufferedReader reader;
	private Path serverfolder;
	private WebsiteMonitorAdvanced monitor;
	public clienthandler(Socket client, Path serverfolder, WebsiteMonitorAdvanced monitor)
	{
		this.client = client;
		this.serverfolder = serverfolder;
		this.monitor = monitor;
		System.out.println("client connected");
	}
	public void run()
	{
		try {
			writer = new PrintWriter(client.getOutputStream(), true);
			reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
			Thread readthread = new Thread(() -> {
				try {
					readrequest();
				}catch(Exception e)
				{
					e.printStackTrace();
				}
			});
			Thread sendthread = new Thread(() -> {
				try {
					sendrespond();
				}catch(Exception e)
				{
					e.printStackTrace();
				}
			});
			readthread.start();
			sendthread.start();
			readthread.join();
			sendthread.join();
			monitor.DecreaseCount();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public void readrequest() throws Exception
	{
		String s;
		while((s = reader.readLine()) != null)
		{
			String[] split = s.split(" ");
			//System.out.println(s);
		}
	}
	public void sendrespond() throws Exception
	{
		StringBuilder respond = new StringBuilder();
		respond.append("HTTP/1.1 200 OK\r\n");
		respond.append("ContentType: text/html\r\n");
		respond.append("\r\n");
		ArrayList<String> content = (ArrayList<String>)Files.readAllLines(serverfolder);
		for(String s : content)
		{
			respond.append(s + "\r\n");
		}
		respond.append("\r\n");
		writer.println(respond.toString());
		writer.close();
	}
}
