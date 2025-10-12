package program;
import java.net.*;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		int success = 0;
		while(success == 0)
		{
			try
			{
				Scanner scanner = new Scanner(System.in);
				System.out.println("Input url:");
				System.out.print("");
				String website = scanner.nextLine();
				WebsiteMonitorer monitor = new WebsiteMonitorer(website);
				success = 1;
			} catch(UnknownHostException e)
			{
				System.out.println("Website unknown");
			}
			
		}
		

	}
	

}
