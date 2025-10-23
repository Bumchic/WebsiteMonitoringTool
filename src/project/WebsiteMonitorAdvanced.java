package program;

import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;

public class WebsiteMonitorAdvanced {

    private static final List<String> WEBSITES = Arrays.asList(
            "https://www.google.com"
    );

   
    private static final ExecutorService executor = Executors.newFixedThreadPool(4);

    // Log file
    private static final String LOG_FILE = "monitor_log.txt";

    public static void main(String[] args) {
        System.out.println("🚀 Advanced Website Monitoring Tool Started");
        System.out.println("-------------------------------------------");

      
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(WebsiteMonitorAdvanced::checkAllWebsites, 0, 1, TimeUnit.MINUTES);
    }

 
    private static void checkAllWebsites() {
        System.out.println("\n🕒 Checking websites at " + LocalDateTime.now());
        for (String site : WEBSITES) {
            executor.submit(() -> checkWebsite(site));
        }
    }

   
    private static void checkWebsite(String siteUrl) {
        try {
            long startTime = System.currentTimeMillis();
            URL url = new URL(siteUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int responseCode = connection.getResponseCode();
            long endTime = System.currentTimeMillis();
            long responseTime = endTime - startTime;

            String status;
            if (responseCode == 200) {
                status = String.format("✅ %s is UP (%d ms)", siteUrl, responseTime);
            } else {
                status = String.format("⚠️  %s returned HTTP %d (%d ms)", siteUrl, responseCode, responseTime);
            }

            logResult(status);
        } catch (IOException e) {
            logResult(String.format("❌ %s is DOWN (%s)", siteUrl, e.getMessage()));
        }
    }

   
    private static synchronized void logResult(String message) {
        String log = String.format("[%s] %s", LocalDateTime.now(), message);
        System.out.println(log);

        try (FileWriter fw = new FileWriter(LOG_FILE, true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(log + "\n");
        } catch (IOException e) {
            System.err.println("Không ghi được log: " + e.getMessage());
        }
    }
}
