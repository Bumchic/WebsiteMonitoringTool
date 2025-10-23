package project;

import java.io.*;
import java.util.*;

public class WebsiteMonitorConsoleChart {

    private static final String CSV_FILE = "monitor_log.csv";

    public static void main(String[] args) {
        System.out.println("📊 Console Chart - Website Response Times");
        System.out.println("==========================================");

        Map<String, Long> latestResponse = readLatestResponses();

        if (latestResponse.isEmpty()) {
            System.out.println("⚠️ Không tìm thấy dữ liệu trong file " + CSV_FILE);
            return;
        }

        // Tìm giá trị lớn nhất để scale biểu đồ
        long max = latestResponse.values().stream().max(Long::compareTo).orElse(100L);
        int maxBarLength = 50;

        // In biểu đồ
        for (Map.Entry<String, Long> entry : latestResponse.entrySet()) {
            String site = entry.getKey();
            long time = entry.getValue();

            int barLength = (int) ((double) time / max * maxBarLength);
            String bar = "█".repeat(Math.max(1, barLength));
            System.out.printf("%-40s | %-50s %d ms%n", site, bar, time);
        }

        System.out.println("==========================================");
        System.out.println("✅ Biểu đồ hiển thị xong!");
    }

    // 📖 Đọc log CSV và lấy giá trị phản hồi mới nhất của mỗi website
    private static Map<String, Long> readLatestResponses() {
        Map<String, Long> map = new LinkedHashMap<>();
        File file = new File(CSV_FILE);

        System.out.println("📂 Đang đọc file: " + file.getAbsolutePath());

        if (!file.exists()) {
            System.out.println("❌ File không tồn tại!");
            return map;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            br.readLine(); // bỏ qua header
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    String site = parts[1].replace("\"", "");
                    long responseTime;
                    try {
                        responseTime = Long.parseLong(parts[3]);
                    } catch (NumberFormatException e) {
                        responseTime = 0;
                    }
                    map.put(site, responseTime);
                }
            }
        } catch (IOException e) {
            System.err.println("Lỗi khi đọc file: " + e.getMessage());
        }

        return map;
    }
}
