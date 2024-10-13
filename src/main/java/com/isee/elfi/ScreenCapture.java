package com.isee.elfi;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ScreenCapture {

    // Ekran görüntüsünün kaydedileceği dizin
    private static final String SAVE_DIRECTORY = "C:\\Users\\Mert\\Documents\\GitHub\\ELFI-VIRUS\\";

    public static void captureScreen() {
        try {
            // Headless mod kontrolü
            if (GraphicsEnvironment.isHeadless()) {
                System.out.println("Sistem headless modda, ekran görüntüsü alınamıyor.");
                return;
            }

            System.out.println("Ekran boyutlarını alıyorum...");

            // Ekran boyutlarını al
            Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            System.out.println("Ekran boyutları alındı: " + screenRect.toString());

            System.out.println("Ekran görüntüsü yakalanıyor...");
            BufferedImage capture = new Robot().createScreenCapture(screenRect);
            System.out.println("Ekran görüntüsü başarıyla yakalandı.");

            // Dosya ismini zaman damgasına göre oluştur ve aynı dizine kaydet
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            File file = new File(SAVE_DIRECTORY + "screenshot_" + timestamp + ".png");
            System.out.println("Ekran görüntüsü kaydedilecek dosya: " + file.getAbsolutePath());

            // Ekran görüntüsünü kaydet
            ImageIO.write(capture, "png", file);
            System.out.println("Ekran görüntüsü başarıyla kaydedildi: " + file.getAbsolutePath());
        } catch (AWTException e) {
            System.out.println("Robot sınıfı kullanılarak ekran yakalama işlemi başarısız oldu.");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Ekran görüntüsünü dosyaya kaydederken bir hata oluştu.");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Beklenmeyen bir hata oluştu.");
            e.printStackTrace();
        }
    }
}
