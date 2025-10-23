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
			server = new ServerSocket(8080, 50, addr);
			serverfolder = Paths.get("C:\\Users\\Bumchic\\Documents\\GitHub\\WebsiteMonitoringTool\\index.html");
			while(true)
			{
				Socket client = server.accept();
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
	public clienthandler(Socket client, Path serverfolder)
	{
		this.client = client;
		this.serverfolder = serverfolder;
		System.out.println("client connected");
	}
	public void run()
	{
		try {
			writer = new PrintWriter(client.getOutputStream(), true);
			reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
			StringBuilder clientrequestbuilder = new StringBuilder();
			String requestline= "";
			while(!(requestline = reader.readLine()).isBlank())
			{
				clientrequestbuilder.append(requestline);
				clientrequestbuilder.append("\r\n");
			}
			System.out.println(clientrequestbuilder.toString());
			String[] splitrequest = clientrequestbuilder.toString().split("\r\n");
			String[] basicrequest = splitrequest[0].split(" ");
			String method = basicrequest[0];
			String path = basicrequest[1];
			String version = basicrequest[2];
			String host = splitrequest[1].split(" ")[1];
			System.out.println(host);
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
			System.out.println(s);
			respond.append(s + "\r\n");
		}
		respond.append("\r\n");
		writer.println(respond.toString());
		writer.close();
	}
}
