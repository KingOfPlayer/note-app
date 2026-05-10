# Erkan İçin Görev Listesi

Tahsin'in `tahsin-dev` branch'i ile master'a getirdikleri:
- Generic altyapı, JDBC tabanlı note-service, file-service GridFS, gateway proxy, user-service auth genişletmeleri
- Docker + k6 + dokümanlar
- Android tarafında login/kayıt, not listesi, custom NoteCardView ve ColorPaletteView

Aşağıdaki görevler **senin** branch'inden gelmesi planlananlar. Commit dağılımı bu sayede dengeli olur.

## 1. Mobil — Arama Ekranı (SearchActivity)

- Yeni `SearchActivity` ekle (`ui/SearchActivity.java` + `res/layout/activity_search.xml`).
- Üstte bir `EditText`, yanında `Spinner` (title / content / all), aşağıda `RecyclerView` ile sonuçlar.
- `NoteApi.search(type, keyword)` zaten hazır.
- `NoteListActivity`'nin toolbar menüsüne "Ara" itemi ekle (`menu_note_list.xml`).

**Yaratacağın dosyalar:**
- `frontend/app/src/main/java/com/note_app/app/ui/SearchActivity.java`
- `frontend/app/src/main/res/layout/activity_search.xml`

**Düzenleyeceğin:**
- `frontend/app/src/main/AndroidManifest.xml` (yeni activity tanımı)
- `frontend/app/src/main/res/menu/menu_note_list.xml` (Ara item)

## 2. Mobil — Kategori Yönetimi

- `CategoryListActivity` + adapter.
- Kullanıcının kategorilerini listeler, yeni eklenmesini sağlar.
- Backend `/api/categories` (GET, POST, PUT, DELETE) hazır — sadece istemci tarafı.
- `CategoryApi.java` (frontend'de) yaz: `list()`, `create(name, color)`, `delete(id)`.

**Yaratacağın dosyalar:**
- `frontend/app/src/main/java/com/note_app/app/api/CategoryApi.java`
- `frontend/app/src/main/java/com/note_app/app/model/Category.java`
- `frontend/app/src/main/java/com/note_app/app/ui/CategoryListActivity.java`
- `frontend/app/src/main/res/layout/activity_category_list.xml`

## 3. Mobil — Profil Ekranı

- `ProfileActivity`: kullanıcı bilgilerini gösterir, ad/email düzenler, hesap silmeyi sağlar.
- API: `GET /api/users/me`, `PUT /api/users/{id}`, `DELETE /api/users/{id}`.
- `UserApi.java` ekle.

**Yaratacağın dosyalar:**
- `frontend/app/src/main/java/com/note_app/app/api/UserApi.java`
- `frontend/app/src/main/java/com/note_app/app/ui/ProfileActivity.java`
- `frontend/app/src/main/res/layout/activity_profile.xml`

## 4. Mobil — Dosya Eki

- Not düzenleme ekranında "Dosya Ekle" butonu.
- Sistem dosya seçici aç (`Intent.ACTION_GET_CONTENT`).
- Seçileni `POST /api/files?noteId=...` ile yükle.
- Aynı not için yüklenen dosyaları listele (küçük liste / chip).

**Düzenleyeceğin:**
- `frontend/app/src/main/java/com/note_app/app/ui/NoteEditActivity.java`
- `frontend/app/src/main/res/layout/activity_note_edit.xml`

**Yaratacağın:**
- `frontend/app/src/main/java/com/note_app/app/api/FileApi.java`
- `frontend/app/src/main/java/com/note_app/app/model/FileMeta.java`

## 5. Mobil — Tema ve String Toparlama

- `res/values/strings.xml` içine tüm UI metinlerini taşı (kod içinden hard-coded string'leri çıkar).
- `res/values/themes.xml` ve `themes-night.xml` üzerinde renk paletini projeye uygun hale getir (kart rengi, primary, accent).
- Uygulama ikonunu özelleştir (`mipmap-*/ic_launcher.png`).

## 6. Backend — En az 2 Integration Test

- `note-service` için bir `@SpringBootTest` ile h2 yerine testcontainers (postgresql) ile en az 2 test:
  - `POST /api/notes` 201 döner
  - `GET /api/notes/search?q=` arama doğru çalışır
- Maven test komutu çalışmalı: `./mvnw -pl apps/note-service test`.

**Yaratacağın:**
- `backend/apps/note-service/src/test/java/com/note_app/noteservice/NoteIntegrationTest.java`
- Gerekirse pom'a `testcontainers` ve `spring-boot-testcontainers` bağımlılığı ekle.

## 7. Doküman — Ekran Görüntüleri ve Sunum

- `docs/screenshots/` altına en az 4 ekran görüntüsü ekle (login, list, edit, search).
- `docs/PERFORMANCE.md` içinde k6 koşumlarından alınan gerçek özet metrikleri (median, p95, fail rate) tabloya yaz.
- 5-7 dakikalık sunum için bir `docs/PRESENTATION.md` outline yaz (Tahsin sunumda anlatacak; sen outline ve geçişleri hazırla).

## 8. Yan İşler (zaman kalırsa)

- **CI**: `.github/workflows/build.yml` Maven build koşturan basit bir workflow.
- **README badge'leri**: build status, lisans, vb.
- **Frontend Lint**: `./gradlew lint` ile temel uyarıları temizle.

## Çalışma Akışı Önerisi

```
git checkout master
git pull
git checkout -b erkan-mobil-search
# 1. ve 2. maddeleri yap, parça parça commit'le
git push -u origin erkan-mobil-search
```

Her madde 2-4 küçük commit olsun, mesajlar Türkçe ve sade. Master'a PR açmadan önce `tahsin-dev`'i merge edip çakışma var mı bak.

## İletişim

Tahsin tarafında değişen kritik kontratlar:
- Tüm istekler artık `X-User-Id` ve `X-User-Role` başlıklarını bekliyor.
- Yanıt formatı `ApiResponse<T>` — `{ success, data, message, timestamp }`. Mobil tarafta `data` içine bakman gerekecek.
- Auth artık `/api/auth/register` ve `/api/auth/login` (eski `/api/users/register` halen çalışır geriye dönük uyum için).
