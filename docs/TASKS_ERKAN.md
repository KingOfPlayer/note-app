# Erkan İçin Görev Listesi

`origin/erkan` branch'inden gelen JWT + Authguard + AuthMiddleware + Crypto eklemeleri `tahsin-dev`'e merge edildi (commit `9d04b8e`). Auth flow'u tek noktada birleşti, build temiz.

## Güncel Durum (5 Mayıs 2026)

| Modül | Sahibi | Durum |
|---|---|---|
| user-service auth (BCrypt + JWT + register/login) | Erkan + Tahsin (merged) | ✅ |
| Gateway AuthMiddlewareFilter + JWTService | Erkan | ✅ |
| common-utils Generic + ConflictException | Tahsin + Erkan | ✅ |
| note-service (PostgreSQL/JDBC + Strategy/Factory) | Tahsin | ✅ |
| file-service (Mongo GridFS) | Tahsin | ✅ |
| Gateway reverse proxy (RestTemplate) | Tahsin | ✅ |
| Docker compose | Tahsin | ✅ |
| k6 load testleri | Tahsin | ✅ |
| Android: login/list/edit + custom views | Tahsin | ✅ |
| Mobil JWT entegrasyonu (Authorization: Bearer) | Tahsin | ✅ |
| Doküman (README/API/ARCHITECTURE/PERFORMANCE/SCORING) | Tahsin | ✅ |

## Senin Yapacakların

### 1. Mobil — Arama Ekranı
- `SearchActivity` + `activity_search.xml`. EditText + Spinner (title/content/all) + RecyclerView.
- `NoteApi.search(type, keyword)` zaten hazır.
- `menu_note_list.xml`'e "Ara" butonu, NoteListActivity'den intent.

### 2. Mobil — Kategori Yönetimi
- `CategoryListActivity`, `CategoryAdapter`.
- Backend `/api/categories` (GET/POST/PUT/DELETE) hazır.
- `CategoryApi.java` + `Category.java` model frontend'e ekle.

### 3. Mobil — Profil Ekranı
- `ProfileActivity`: GET /api/users/me, PUT /api/users/{id}, DELETE.
- `UserApi.java`.
- NoteListActivity menu'ye "Profil" butonu.

### 4. Mobil — Dosya Eki
- NoteEditActivity'ye "Dosya Ekle".
- Intent.ACTION_GET_CONTENT, multipart upload.
- `FileApi.java` + `FileMeta.java`.
- Yüklenen dosyalar küçük chip listesi olarak görünsün.

### 5. (Senin önerin) Not içerik tipleri
> Mesajda dedin ki: "kullanıcıya bağlı JSON içinde tutulmuş düz yazı / checklist / resim gibi tipleri içeren genel content entity, kalıtım ile..."

Bu güzel bir fikir, hocaya da farklı görünür. Önerim:
- Backend tarafında Note'a `noteType` alanı (TEXT, CHECKLIST, IMAGE_REF) ekle.
- `content` alanı zaten serbest TEXT; CHECKLIST için JSON (`[{"text":"...","done":false}]`), IMAGE_REF için file id.
- Java tarafında interface tabanlı ayrım: `NoteContent` interface, `TextContent`, `ChecklistContent`, `ImageContent` impl'leri (Strategy pattern bir kez daha).
- Mobil tarafında her tip için ayrı render path (NoteCardView'a tip parametresi).
- Bu büyük iş — sunum için 1-2 tipi yapsak yeterli (TEXT zaten var, CHECKLIST eklemek ~3-4 saat).

İstersen bu maddeyi sen al, ben SearchActivity + Profile + Dosya tarafında sana yardım ederim.

### 6. Tema/string toparlama (küçük)
- `res/values/strings.xml` içine UI metinleri taşı.
- `themes.xml`'i renklerimize uygun hale getir.

### 7. Backend integration test (opsiyonel TDD +10p)
- `note-service` için `@SpringBootTest` + testcontainers (postgresql).
- 2 test: `POST /api/notes 201`, `GET /api/notes/search?q= sonucu doğru`.

### 8. Sunum hazırlığı (sonra konuşacağız)
- Ekran görüntüleri docs/screenshots/.
- 5-7 dakikalık outline.

## Çalışma Önerisi

```
git fetch origin
git checkout tahsin-dev          # ya da master, ne dersen
git checkout -b erkan-mobil-extra
# 1, 2, 3, 4 sırayla yap
git push -u origin erkan-mobil-extra
```

## Aklında Bulunsun

- Auth flow'u artık JWT'ye dayalı. Mobil tarafta `SessionStore` token saklıyor, `ApiClient` her isteğe `Authorization: Bearer <token>` ekliyor.
- Backend tarafındaki tüm controller'lar `X-User-Id` header'ı bekliyor — bu header'ı gateway, JWT'den çıkarıp set ediyor. Yani downstream servislerde ekstra iş yok.
- API yanıtları `ApiResponse<T>` formatında: `{success, data, message, timestamp}`. Client `data` içinden okur.
- `NotFoundExecption` (typo, bilerek bırakıldı, çok import etkilenir).

## Kontrat Noktaları

- `RegisterRequest`: name, email, password (validation: not blank, email, min 6 char).
- `LoginRequest`: email, password.
- Login/Register yanıtı: `{ success, data: { user: {id, name, email, role}, token: "..." }, message }`.
- Note istekleri: title (zorunlu), content, color (#hex), categoryId (long), pinned (bool).
