package project;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class EmailNotifier {
    private static final String Mail = "duonganhminh05@gmail.com";  // Gmail gửi cảnh báo
    private static final String PASSWORD = "nnad gbmj wase myld";            // App password Gmail
    private static final String TO_EMAIL = "duonganhminh05@gmail.com";  // Email nhận cảnh báo

    // 👇 Hàm đúng với lời gọi trong WebsiteMonitorPro
    public static void sendAlert(String website, int failCount, String status) {
        String subject = "🚨 Website DOWN Alert";
        String body = String.format(
            "Website %s has failed %d times consecutively.\nStatus: %s\n\nTime: %s",
            website, failCount, status, new java.util.Date()
        );
        
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(Mail, PASSWORD);
            }
        });
        

        try {
            MimeMessage message = new MimeMessage(session);
            
            message.setFrom(new InternetAddress(Mail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(TO_EMAIL));
            message.setSubject(subject);
            message.setText(body);
            
            Transport.send(message);
           
            System.out.println("📨 Email alert sent to " + TO_EMAIL);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
