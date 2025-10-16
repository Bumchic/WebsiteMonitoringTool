package program;
import java.net.*;
import java.util.Scanner;

public class Main {
	private static WebsiteMonitorer monitor;
	private static Scanner scanner = new Scanner(System.in);
	public static void main(String[] args) {
		InitWebsiteMonitorer();
		while(true)
		{
		//ask user to input command
		System.out.println("Input Command: ");
		String command = scanner.nextLine();
		if(command.equals("exit"))
		{
			break;
		}
		ProcessCommand(command);
		}
	}
	private static void InitWebsiteMonitorer()
	{
		//Ask user to input url
		int success = 0;
		while(success == 0)
		{
			try {
				System.out.println("Input url (example: https://www.Website.com):");
				System.out.print("");
				String website = scanner.nextLine();
				monitor = new WebsiteMonitorer(website);
				success = 1;
			} catch (MalformedURLException e) {
				System.out.println("Url doesnt exist or wrong format");
			}
		}
			
	}
	private static void ProcessCommand(String command)
	{
		//Process command of user
		String[] commandarr = command.split(" ");
		switch(commandarr[0])
		{
		case "Alive":
			boolean output = monitor.CheckIfAlive();
			if(output)
			{
				System.out.println("Website is up");
			}else
			{
				System.out.println("Website is down");
			}
			break;
		case "Change":
			monitor = null;
			InitWebsiteMonitorer();
			break;
		default:
			System.out.println("Unknown command");
		}
	}
}
