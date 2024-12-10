package com.isee.elfi;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class ScreenCapture {
    // Static list to store captured images as byte arrays
    private static final List<byte[]> imageList = new ArrayList<>();

    public static void captureScreen() {
        try {
            // Headless mode check
            if (GraphicsEnvironment.isHeadless()) {
                System.out.println("The system is in headless mode, unable to capture the screen.");
                return;
            }

            System.out.println("Retrieving screen dimensions...");

            // Get screen dimensions
            Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            System.out.println("Screen dimensions retrieved: " + screenRect.toString());

            System.out.println("Capturing the screen...");
            BufferedImage capture = new Robot().createScreenCapture(screenRect);
            System.out.println("Screen captured successfully.");

            // Convert the captured image to a byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(capture, "png", baos);
            baos.flush();
            byte[] imageBytes = baos.toByteArray();
            baos.close();

            // Add the byte array to the static list
            imageList.add(imageBytes);
            System.out.println("Image added to the list. Current list size: " + imageList.size());

        } catch (AWTException e) {
            System.out.println("Failed to capture the screen using the Robot class.");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("An error occurred while converting the image to a byte array.");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("An unexpected error occurred.");
            e.printStackTrace();
        }
    }

    // Method to get the list of images
    public static List<byte[]> getImageList() {
        return imageList;
    }
}


//import javax.imageio.ImageIO;
//import java.awt.*;
//import java.awt.image.BufferedImage;
//import java.io.File;
//import java.io.IOException;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//
//public class ScreenCapture {
//
//    public static void captureScreen() {
//        try {
//            // Headless mod kontrolü
//            if (GraphicsEnvironment.isHeadless()) {
//                System.out.println("Sistem headless modda, ekran görüntüsü alınamıyor.");
//                return;
//            }
//
//            System.out.println("Ekran boyutlarını alıyorum...");
//
//            // Ekran boyutlarını al
//            Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
//            System.out.println("Ekran boyutları alındı: " + screenRect.toString());
//
//            System.out.println("Ekran görüntüsü yakalanıyor...");
//            BufferedImage capture = new Robot().createScreenCapture(screenRect);
//            System.out.println("Ekran görüntüsü başarıyla yakalandı.");
//
//            // Dosya ismini zaman damgasına göre oluştur ve aynı dizine kaydet
//            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
//            File file = new File(Constants.resourcesPath + "screenshot_" + timestamp + ".png");
//            System.out.println("Ekran görüntüsü kaydedilecek dosya: " + file.getAbsolutePath());
//
//            // Ekran görüntüsünü kaydet
//            ImageIO.write(capture, "png", file);
//            System.out.println("Ekran görüntüsü başarıyla kaydedildi: " + file.getAbsolutePath());
//        } catch (AWTException e) {
//            System.out.println("Robot sınıfı kullanılarak ekran yakalama işlemi başarısız oldu.");
//            e.printStackTrace();
//        } catch (IOException e) {
//            System.out.println("Ekran görüntüsünü dosyaya kaydederken bir hata oluştu.");
//            e.printStackTrace();
//        } catch (Exception e) {
//            System.out.println("Beklenmeyen bir hata oluştu.");
//            e.printStackTrace();
//        }
//    }
//}
