package com.isee.elfi;

import io.github.cdimascio.dotenv.Dotenv;

import java.util.Properties;
import javax.activation.DataHandler;
import javax.mail.*;
import javax.mail.Session;
import javax.mail.internet.*;
import javax.mail.util.ByteArrayDataSource;

// Email Gönderme İşlemini Yaptğımız EmailSender Sınıfımız
public class EmailSender {

    // Eklentili Mail Gönderen Fonksiyonumuz
    public static void sendEmailWithInMemoryAttachment(String toEmail, String subject, String bodyText, byte[] zipData, String zipFileName) {

        // .env dosyamızın içinde mail gönderen gmail hesabına ait mail ve şifre var
        // github üzerinden yaptığımız projeyi public bir şekilde paylaştığımız için bu özel bilgileri .env dosyasında tuttuk
        // .env dosyasından bu bilgileri çekmek için Dotenv Kütüphanesini kullandık
        Dotenv dotenv = Dotenv.load();

        // Mail bilgilerimizi tutan değişkenlerimiz
        String username = dotenv.get("mail_username");
        String password = dotenv.get("mail_password");

        // E posta göndermek için gereken ayarları tutacak bir Properties nesnesi oluşturduk
        Properties props = new Properties();
        // SMTP sunucusunun kimlik doğrulama işlemi gerektirdiğini belirtiyoruz
        props.put("mail.smtp.auth", "true");
        // Sunucunun TLS (Transport Layer Security - e-posta iletişimini şifreler ve güvenli hale getirir )
        // protokolünü desteklediğini ve kullanılacağını belirtiyoruz.
        props.put("mail.smtp.starttls.enable", "true");
        // E-posta gönderimi için kullanılacak SMTP sunucusunun adresini belirtiyoruz.
        props.put("mail.smtp.host", "smtp.gmail.com");
        // SMTP sunucusunun dinlediği port numarasını belirtiyoruz.
        props.put("mail.smtp.port", "587");

        // E-posta gönderimi için bir oturum oluşturduk
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            // Mesajımızı oluşturduk
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username)); // Gönderen Adresimizi belirttik
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail)); // Alıcı Adresi belirttik
            message.setSubject(subject); // Başlığı belirttik

            // Zip'i ek olarak ekleyeceğimiz için Multipart oluşturduk
            Multipart multipart = new MimeMultipart();

            // Yazı kısımlarını ekledik
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(bodyText); // Email'imizin içeriğindeki yazıyı belirttik
            multipart.addBodyPart(textPart); // mail gövdesine ekledik

            // Ek kısmını ekliyoruz
            MimeBodyPart attachmentPart = new MimeBodyPart();
            attachmentPart.setFileName(zipFileName); // Ek'imizin dosya ismini yazdık
            attachmentPart.setDataHandler(new DataHandler(new ByteArrayDataSource(zipData, "application/zip"))); // Zip'imizi zip türünde ek olarak ekliyoruz
            multipart.addBodyPart(attachmentPart); // mailimizin gövdesine ekliyoruz ek'imizi

            // Multipart'ı mesaja ekledik
            message.setContent(multipart);

            // Mailimizi gönderdik
            Transport.send(message);

            System.out.println("Email sent successfully with the ZIP file attached.");
        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
            e.printStackTrace();
        }
    }
}