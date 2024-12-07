package com.isee.elfi;

import java.util.Properties;
import javax.mail.*;
import javax.mail.Session;
import javax.mail.internet.*;


public class EmailSender {

    public static void main(String[] args) {
        // Gönderici ve alıcı bilgileri
        final String username = "mertayhandev@gmail.com"; // Gönderici e-posta
        final String password = "sbvjxbrjabjrskci"; // Gmail için uygulama şifresi gerekebilir
        String toEmail = "mertayhandev@gmail.com"; // Alıcı e-posta
        String subject = "Dosya Gönderimi"; // E-posta konusu
        String bodyText = "Merhaba, aşağıdaki dosyayı inceleyebilirsiniz."; // E-posta içeriği

        // SMTP ayarları
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        // Oturum aç
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            // Mesajı oluştur
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username)); // Gönderici adresi
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail)); // Alıcı adresi
            message.setSubject(subject); // Konu
            message.setText(bodyText); // İçerik

            // Mesajı gönder
            Transport.send(message);

            System.out.println("E-posta başarıyla gönderildi!");

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}