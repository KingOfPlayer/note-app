# Uygulamayı Telefonda Çalıştırma

## Önkoşullar
- PC ve telefon **aynı WiFi**'da olmalı.
- PC'de Docker Desktop çalışıyor olmalı.
- Android Studio kurulu, telefonda USB debugging açık.

## 1. Backend'i kaldır (PC'de)

```bash
cd note-app
docker compose up --build -d
docker compose ps
```

6 container UP olmalı: postgres, mongo, gateway, user-service, note-service, file-service.

Hızlı test:
```bash
curl http://localhost:8080/api/auth/register -H "Content-Type: application/json" -d "{\"name\":\"Test\",\"email\":\"a@a.com\",\"password\":\"123456\"}"
```
JSON yanıt + 201 dönmeli.

## 2. PC IP'sini doğrula

Windows'ta:
```
ipconfig
```
"Wireless LAN adapter Wi-Fi" altında **IPv4 Address** satırı. Şu an `192.168.1.105` olarak ayarlı (`frontend/app/src/main/java/com/note_app/app/util/AppContext.java` → `BASE_URL`).

IP değiştiyse o dosyayı güncelle, Gradle sync yap.

## 3. Telefonda Çalıştır

1. Telefonu USB ile PC'ye bağla.
2. Telefonda "Bu bilgisayara güven" pop-up'ı çıkar → güven.
3. Android Studio'da projeyi aç (`frontend/` klasörü).
4. Cihaz seçici dropdown'da telefonun göründüğünü doğrula.
5. **Run** butonuna bas (yeşil oklu).

Studio APK'yı build edip telefona yükler ve otomatik açar. İlk seferde Gradle bağımlılıkları indirmek ~5 dakika sürebilir.

## 4. Test Senaryosu

1. Kayıt ol (yeni e-posta + parola 6+ karakter).
2. Otomatik login → not listesi (boş) açılır.
3. Sağ alttaki + butonu → yeni not yarat (başlık + içerik + renk seç → kaydet).
4. Sabitleme: not düzenleme ekranında "Sabitle" checkbox.
5. Toolbar menü:
   - **Ara**: kelime girip canlı arama yap.
   - **Kategoriler**: yeni kategori ekle.
   - **Profil**: bilgileri güncelle.
   - **Çıkış**: oturumu kapat.
6. Not düzenleme ekranında "Dosya Ekle" → galeri/dosya seçici → yükleme tamamlandıktan sonra ek listesinde görünür.

## 5. Bağlanamadığında

- **Telefon "bağlanılamıyor" diyor**: PC firewall 8080 portunu engelliyor olabilir. Windows Defender → Inbound Rules → port 8080 izin ver.
- **Servisler 503 dönüyor**: `docker compose ps` ile UP olduğunu kontrol et. Henüz healthcheck geçmediyse 30sn bekle.
- **Login sonrası "Forbidden" / 401**: Token süresi dolmuş (24 saat), çıkış yapıp tekrar giriş yap.

## 6. APK Üretmek (telefona PC'siz kurmak için)

Android Studio'da: **Build → Build Bundle(s) / APK(s) → Build APK(s)**. Çıktı:
`frontend/app/build/outputs/apk/debug/app-debug.apk`

Bu APK'yı telefona kopyalayıp tıklayınca kurar. "Bilinmeyen kaynaklardan yüklemeye izin ver" sorulur, evet.
