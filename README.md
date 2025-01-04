
Bu proje, İleri Programlama dersi kapsamında deneysel bir çalışma olarak geliştirilmiştir. Tamamen akademik ve öğretici amaçlar gütmektedir. Proje, kullanıcı etkileşimlerini (tuş kaydı, ekran görüntüsü, ses kaydı) toplayarak bu verileri belirli bir e-posta adresine göndermektedir. Etik dışı ya da zarar verici amaçlar için kullanılmamalıdır!

Özellikler

Kullanıcının tuş kayıtlarını (KeyLogger) toplar.

Belirli aralıklarla ekran görüntüsü alır.

Ses ve video kaydı yapar.

Toplanan verileri ZIP formatında sıkıştırır ve hedef bir e-posta adresine gönderir.

Gereksinimler

Java 8 veya üzeri

Maven

Nasıl Kullanılır?

1. Depoyu Klonlayın

2. .env Dosyasını Oluşturun

Projenin "resources" klasörüne bir .env dosyası ekleyin ve içeriğini aşağıdaki gibi doldurun:

mail_username=ornek@gmail.com
mail_password=uygulama_giris_anahtari

Not: Gmail üzerinden "Uygulama Giriş Anahtarı" oluşturmalısınız. Gmail hesabınızda 2FA (iki adımlı doğrulama) etkin olmalı ve "Güvenli Erişim" ayarından anahtarı alabilirsiniz.
Not: Mailin Gönderileceği E-posta adresini main dosyasının içerisinde toemaile yazın

3. Maven Bağımlılıklarını Yükleyin

$ mvn clean install

4. Uygulamayı Çalıştırın

$ mvn spring-boot:run

5. Kapatma

Uygulama otomatik olarak 30 saniye sonra kapanacak ve toplanan veriler e-posta ile gönderilecektir.

Uyarılar

Bu proje tamamen akademik amaçlarla geliştirilmiştir.

Etik kurallara uygun olmayan kullanımlar kesinlikle yasaktır.

Uygulama, izin alınmış ortamlarda test edilmelidir.

Lisans

Bu proje özgün bir çalışma olup kötü niyetli kullanımlar için geliştirilmemiştir. Herhangi bir sorumluluk geliştiricilere ait değildir.

