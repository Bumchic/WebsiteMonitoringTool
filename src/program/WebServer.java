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
		try
		{
			addr = InetAddress.getByName("localhost");
			server = new ServerSocket(6789, 50, addr);
			serverfolder = Paths.get("C:\\Users\\Bumchic\\Documents\\GitHub\\WebsiteMonitoringTool\\index.html");
			while(true)
			{
				Socket client = server.accept();
				System.out.println(client.getPort());
				clienthandler clienthandler = new clienthandler(client, serverfolder);
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
	PrintWriter writer;
	BufferedReader reader;
	Path serverfolder;
	//WebsiteMonitorAdvanced monitor;
	public clienthandler(Socket client, Path serverfolder)
	{
		this.client = client;
		this.serverfolder = serverfolder;
		//monitor = new WebsiteMonitorAdvanced("http://localhost:8080");
		System.out.println("client connected");
	}
	public void run()
	{
		try {
			writer = new PrintWriter(client.getOutputStream(), true);
			reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
			sendrespond();

			
		}catch(Exception e)
		{
			e.printStackTrace();
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
