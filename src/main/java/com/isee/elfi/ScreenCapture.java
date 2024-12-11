package com.isee.elfi;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

// Ekran Fotoğrafı Almak için kullandığımız ScreenCapture classımız
public class ScreenCapture {
    // Ekran fotoğraflarını hafızada geçici olarak tutmak için oluşturduğumuz listemiz
    private static final List<byte[]> imageList = new ArrayList<>();

    // Ekran Görüntüsünü kaydetmeye başlayan fonksiyonumuz
    public static void captureScreen() {
        try {
            // Headless (GUIveya fiziksel bir ekran çıktısı gerektirmeyen bir ortam) Modundaysa Fonksiyonun geri kalanına bakma
            if (GraphicsEnvironment.isHeadless()) {
                return;
            }

            // Ekran Boyutlarını Al
            Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());

            // Robot sınıfı, Java'da klavye, fare ve ekran gibi düşük seviyeli sistem olaylarını kontrol etmek için kullanılır.
            // Ekran Fotoğrafını aldık
            BufferedImage capture = new Robot().createScreenCapture(screenRect);

            // Kaydedilen fotoğrafı byte[] a dönüştür
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            // baos'a png formatında yazdık
            ImageIO.write(capture, "png", baos);
            // tampon belleği (buffer) boşaltır ve hedefe gönderir
            baos.flush();
            //imageBytes'a atanır verimizin byte[] hali
            byte[] imageBytes = baos.toByteArray();
            baos.close();

            // Fotoğrafları tuttuğumuz listeye ekleriz verimizi
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

    // Kaydedilen fotoğrafları döndüren fonksiyonumuz
    public static List<byte[]> getImageList() {
        return imageList;
    }
}