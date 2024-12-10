package com.isee.elfi;

import java.io.*;
import java.util.Map;
import java.util.zip.*;

public class Zipper {

    public byte[] zipFiles(Map<String, byte[]> fileContents) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(baos)) {

            for (Map.Entry<String, byte[]> entry : fileContents.entrySet()) {
                String fileName = entry.getKey();
                byte[] fileData = entry.getValue();

                // Create a new entry in the ZIP for each file
                ZipEntry zipEntry = new ZipEntry(fileName);
                zos.putNextEntry(zipEntry);
                zos.write(fileData);
                zos.closeEntry();
            }

            zos.finish();
            return baos.toByteArray();
        }
    }

    public byte[] zipFileOrDirectory(String sourcePath) throws IOException {
        File sourceFile = new File(sourcePath);
        if (!sourceFile.exists()) {
            throw new IOException("Kaynak dosya bulunamadı veya erişilemiyor: " + sourcePath);
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(baos)) {

            if (sourceFile.isFile()) {
                addFileToZip(sourceFile, zos, "");
            } else if (sourceFile.isDirectory()) {
                addDirectoryToZip(sourceFile, zos, "");
            }

            zos.finish();
            return baos.toByteArray();
        }
    }

    private void addFileToZip(File file, ZipOutputStream zos, String parentDir) throws IOException {
        String zipEntryName = parentDir + file.getName();
        try (FileInputStream fis = new FileInputStream(file)) {
            ZipEntry zipEntry = new ZipEntry(zipEntryName);
            zos.putNextEntry(zipEntry);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                zos.write(buffer, 0, length);
            }

            zos.closeEntry();
        }
    }

    private void addDirectoryToZip(File directory, ZipOutputStream zos, String parentDir) throws IOException {
        String dirName = parentDir + directory.getName() + "/";
        zos.putNextEntry(new ZipEntry(dirName));
        zos.closeEntry();

        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    addFileToZip(file, zos, dirName);
                } else if (file.isDirectory()) {
                    addDirectoryToZip(file, zos, dirName);
                }
            }
        }
    }
}
