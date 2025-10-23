package project;
import project.EmailNotifier;

import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import javax.net.ssl.SSLHandshakeException;

public class WebsiteMonitorPro {

    private static final List<String> WEBSITES = Arrays.asList(
            "https://www.google.com",
            "https://www.vnexpress.net",
            "https://www.abcdefgxyz1234.com",
            "https://10.255.255.1",
            "https://expired.badssl.com/",
            "https://httpstat.us/404",
            "https://httpstat.us/500",
            "https://httpstat.us/505",
            "http://example.com:81",
            "ftp://speedtest.tele2.net"
    );

    private static final ExecutorService executor = Executors.newFixedThreadPool(4);
    private static final String CSV_FILE = "monitor_log.csv";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final Map<String, Integer> errorCountMap = new ConcurrentHashMap<>();

    public static void main(String[] args) {
    	EmailNotifier.sendAlert("gogle.com", 3, "Website failed " + 3 + " consecutive checks");
        System.out.println("🚀 WebsiteMonitorPro started");
        System.out.println("------------------------------------------");

        initCSVFile();

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(WebsiteMonitorPro::checkAllWebsites, 0, 1, TimeUnit.MINUTES);
    }

    private static void initCSVFile() {
        File file = new File(CSV_FILE);
        if (!file.exists()) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
                bw.write("Time,Website,StatusCode,ResponseTime(ms),Result,ErrorMessage\n");
            } catch (IOException e) {
                System.err.println("Không thể tạo file CSV: " + e.getMessage());
            }
        }
    }

    private static void checkAllWebsites() {
        System.out.println("\n🕒 Checking websites at " + LocalDateTime.now().format(FORMATTER));
        for (String site : WEBSITES) {
            executor.submit(() -> checkWebsite(site));
        }
    }

    private static void checkWebsite(String siteUrl) {
        HttpURLConnection connection = null;
        int responseCode = -1;
        long responseTime = 0;

        try {
            long startTime = System.currentTimeMillis();
            URL url = new URL(siteUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            responseCode = connection.getResponseCode();
            responseTime = System.currentTimeMillis() - startTime;

            if (responseCode == 200) {
                printColored("✅ " + siteUrl + " is UP (" + responseTime + " ms)", "green");
                writeCSV(siteUrl, responseCode, responseTime, "UP", "");
                errorCountMap.put(siteUrl, 0);
            } else {
                printColored("⚠️  " + siteUrl + " returned HTTP " + responseCode + " (" + responseTime + " ms)", "yellow");
                writeCSV(siteUrl, responseCode, responseTime, "WARNING", "HTTP " + responseCode);
                increaseError(siteUrl);
            }

        } catch (UnknownHostException e) {
            handleError(siteUrl, "DNS Error", e);
        } catch (SocketTimeoutException e) {
            handleError(siteUrl, "Timeout Error", e);
        } catch (ConnectException e) {
            handleError(siteUrl, "Connection Error", e);
        } catch (SSLHandshakeException e) {
            handleError(siteUrl, "SSL Error", e);
        } catch (IOException e) {
            handleError(siteUrl, "I/O Error", e);
        } catch (Exception e) {
            handleError(siteUrl, "Unknown Error", e);
        } finally {
            if (connection != null) connection.disconnect();
        }
    }

    private static void handleError(String siteUrl, String errorType, Exception e) {
        printColored("❌ " + siteUrl + " failed (" + errorType + "): " + e.getMessage(), "red");
        writeCSV(siteUrl, -1, 0, "DOWN", errorType + ": " + e.getMessage());
        increaseError(siteUrl);
    }

    private static void increaseError(String siteUrl) {
        int count = errorCountMap.getOrDefault(siteUrl, 0) + 1;
        errorCountMap.put(siteUrl, count);

        if (count >= 3) {
            printColored("🚨 ALERT: " + siteUrl + " has failed 3 times consecutively!", "magenta");
            EmailNotifier.sendAlert(siteUrl, count, "Website failed " + count + " consecutive checks");
        }
    }

    private static synchronized void writeCSV(String site, int code, long time, String result, String message) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(CSV_FILE, true))) {
            String line = String.format("\"%s\",\"%s\",%d,%d,\"%s\",\"%s\"\n",
                    LocalDateTime.now().format(FORMATTER), site, code, time, result, message.replace("\"", "'"));
            bw.write(line);
        } catch (IOException e) {
            System.err.println("Không ghi được CSV: " + e.getMessage());
        }
    }

    private static void printColored(String text, String color) {
        final String RESET = "\u001B[0m";
        final String RED = "\u001B[31m";
        final String GREEN = "\u001B[32m";
        final String YELLOW = "\u001B[33m";
        final String MAGENTA = "\u001B[35m";

        String coloredText;
        switch (color) {
            case "green": coloredText = GREEN + text + RESET; break;
            case "yellow": coloredText = YELLOW + text + RESET; break;
            case "red": coloredText = RED + text + RESET; break;
            case "magenta": coloredText = MAGENTA + text + RESET; break;
            default: coloredText = text; break;
        }
        System.out.println(coloredText);
    }
}
