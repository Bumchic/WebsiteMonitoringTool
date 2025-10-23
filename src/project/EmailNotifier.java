package project;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class EmailNotifier {
    private static final String USERNAME = "duonganhminh05@gmail.com";  // Gmail gửi cảnh báo
    private static final String PASSWORD = "nnad gbmj wase myld";            // App password Gmail
    private static final String TO_EMAIL = "duonganhminh05@gmail.com";  // Email nhận cảnh báo

    // 👇 Hàm đúng với lời gọi trong WebsiteMonitorPro
    public static void sendAlert(String website, int failCount, String status) {
        String subject = "🚨 Website DOWN Alert";
        String body = String.format(
            "Website %s has failed %d times consecutively.\nStatus: %s\n\nTime: %s",
            website, failCount, status, new java.util.Date()
        );
        System.out.println("body made");
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
       // props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        System.out.println("prop init");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        });
        System.out.println("session made");

        try {
            MimeMessage message = new MimeMessage(session);
            System.out.println("new mess");
            message.setFrom(new InternetAddress(USERNAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(TO_EMAIL));
            message.setSubject(subject);
            message.setText(body);
            System.out.println("mess made");
            Transport.send(message);
           
            System.out.println("📨 Email alert sent to " + TO_EMAIL);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
