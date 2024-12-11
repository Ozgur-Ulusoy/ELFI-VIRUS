package com.isee.elfi;

import java.io.*;
import java.util.Map;
import java.util.zip.*;

// Topladığımız bilgileri zip'lemesi için oluşturduğumuz Zipper classımız
public class Zipper {

    // Verilen dosyaları sıkıştırarak byte dizisi olarak döndüren fonksiyonumuz
    public byte[] zipFiles(Map<String, byte[]> fileContents) throws IOException {
        // ByteArrayOutputStream kullanarak sıkıştırılmış veriyi bellekte tutuyoruz
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(baos)) {

            // Map'teki her bir dosya için ZIP içerikleri oluşturuyoruz
            for (Map.Entry<String, byte[]> entry : fileContents.entrySet()) {
                String fileName = entry.getKey(); // Dosya ismini alıyoruz
                byte[] fileData = entry.getValue(); // Dosya verilerini alıyoruz

                // ZIP dosyasına yeni bir giriş ekliyoruz
                ZipEntry zipEntry = new ZipEntry(fileName);
                zos.putNextEntry(zipEntry);  // ZIP'e giriş ekleniyor
                zos.write(fileData); // Dosya verisini yazıyoruz
                zos.closeEntry(); // Girişi kapatıyoruz
            }

            zos.finish(); // ZIP işlemini tamamlıyoruz
            return baos.toByteArray(); // Sıkıştırılmış veriyi byte dizisi olarak döndürüyoruz
        }
    }
}
