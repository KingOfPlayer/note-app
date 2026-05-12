# Sunum Notları

Bu dosya sunum öncesi okumak için. Hocanın sorabileceği her şeye cevap verebilelim diye sınıf sınıf gezen bir brief.

## 60 Saniyede Proje

Notlarım, kullanıcıların hesap açıp not / kategori / dosya eki yönetebildiği bir mobil uygulama. Java tabanlı 4 mikroservis (gateway + user + note + file), iki veritabanı (PostgreSQL ve MongoDB), Android istemci ve Docker Compose ile tek komut açılıyor.

## Mimari Cümlesi

"İstek mobil uygulamadan gateway'e geliyor; gateway JWT'yi doğruluyor, kullanıcı bilgisini header olarak ilgili mikroservise iletiyor. Note Service PostgreSQL'e JdbcTemplate ile yazıyor, File Service MongoDB GridFS ile dosya saklıyor, User Service MongoDB'de kullanıcıları tutuyor."

## Paket Paket Ne Yapıyor

### `backend/utils/common-utils`
Tüm servislerin paylaştığı ortak iskelet. Spring autoconfigure ile her servise otomatik yükleniyor.

- `generic/` — **Generic<T> kriteri 10p**. `BaseEntity<ID>`, `GenericRepository<T,ID>`, `GenericService<T,ID>`, `AbstractCrudService<T,ID>` (Template Method pattern), `PageResponse<T>`, `ApiResponse<T>`. Note ve Category bu yapıyı kullanıyor.
- `exception/` — Custom exception sınıfları (BadRequest, Unauthorized, Forbidden, NotFound, Conflict, InternalServer, ServiceUnavailable, GatewayTimeout). `GlobalExceptionHandler` her birini doğru HTTP koduna eşliyor. Validation hataları da burada — alanları liste olarak döner.
- `exception/ErrorMessages` — Tüm yazı sabitleri burada, magic string yok.
- `authguard/` — `@AuthGuard(UserRoles.X)` annotation'ı + AOP aspect. Endpoint'in başına ekliyoruz, otomatik header kontrolü yapıyor.

### `backend/apps/user-service` (port 5001, MongoDB)
- `Entities/Models/User` — record sınıfı, MongoDB document.
- `Configs/CryptoConfig` — BCrypt encoder bean.
- `Configs/SecurityConfig` — Spring Security stateless, CSRF kapalı.
- `Services/CryptoService` — parola hash + verify.
- `Services/JWTService` — token üret (HS256, 24h geçerli).
- `Services/UserService` — register/login/update/delete iş mantığı; bcrypt + jwt birlikte.
- `RestControllers/AuthController` — `/api/auth/register`, `/api/auth/login`. Validated DTO'larla.
- `RestControllers/UserRestController` — `/api/users/me`, `/api/users/{id}`. `@AuthGuard` ile.

### `backend/apps/note-service` (port 5002, PostgreSQL + JDBC)
- `entity/Note`, `entity/Category` — BaseEntity<Long>'dan extend.
- `resources/schema.sql` — `notes` ve `categories` tabloları + indeksler. Spring boot başlangıçta çalıştırıyor.
- `repository/INoteRepository`, `repository/ICategoryRepository` — `GenericRepository<T,Long>`'dan extend.
- `repository/jdbc/JdbcNoteRepository`, `JdbcCategoryRepository` — **JDBC kriteri 10p**. `JdbcTemplate` ile manuel SQL, `RowMapper`'lar.
- `service/search/` — **Strategy pattern**. `SearchStrategy` arayüzü + 3 implementasyon (TitleSearchStrategy, ContentSearchStrategy, AllFieldsSearchStrategy). `SearchStrategyFactory` istemcinin verdiği `type` parametresine göre stratejiyi seçiyor → **Factory pattern**.
- `service/NoteService extends AbstractCrudService<Note,Long>` — `beforeCreate`/`beforeUpdate` hook'larıyla validation. **Template Method pattern**.
- `controller/NoteController` — REST endpoint'leri. Hepsinde `@AuthGuard`.
- `content/NoteContentNode` — **abstract sınıf + kalıtım**. 3 alt tip: `TextNode`, `ChecklistNode`, `ImageNode`. Jackson polymorphism (@JsonTypeInfo) ile JSON'a serialize edilebiliyor.
- `content/NoteContent` — composite, node listesini tutar.
- `content/NoteContentParser` — String → NoteContent. Hem JSON formatı hem markup syntax destekliyor (`[ ]`, `[x]`, `![file:id]`).

### `backend/apps/file-service` (port 5003, MongoDB GridFS)
- `service/GridFsFileService` — `GridFsTemplate` ile binary saklama. Metadata'ya `userId` yazılıyor, başkası indirme/silme yapamıyor (Forbidden).
- `controller/FileController` — multipart upload, download stream, listele, sil.

### `backend/apps/gateway-service` (port 5000, dış 8080)
- `proxy/ProxyController` — `/api/**` yolunu yakalayıp `ServiceRegistry`'den hedefini bulup `RestTemplate` ile forward ediyor.
- `config/ServiceRegistry` — `/api/users → user-service`, `/api/notes → note-service`, vs.
- `Middlewares/AuthMiddlewareFilter` — `OncePerRequestFilter`, HIGHEST_PRECEDENCE. Önce X-User-* header'larını siler (spoofing engeli), sonra `Authorization: Bearer <jwt>` varsa doğrular, geçerliyse `X-User-Id`/`X-User-Role`/`X-User-Email` header'larını ekliyor.
- `Middlewares/MutableHttpServletRequest` — Servlet API'de header değiştirmek için wrapper.
- `Services/JWTService` — token doğrulama tarafı (HS256, aynı secret).

### `frontend` (Android, Java)
- `ui/widget/NoteCardView` — **Custom Graphics 10p**. `View`'den extend, `onDraw(Canvas)` içinde Paint API ile yuvarlatılmış kart + gölge + pin ikonu (Path) + ellipsize'lı başlık + içerik özeti + tarih. Standart `TextView` veya `ImageView` kullanmıyor.
- `ui/widget/ColorPaletteView` — Yine custom drawn. Yatay daire dizisi, seçilen rengin etrafında halka, dokunma olayını işliyor.
- `model/Note`, `model/Category`, `model/FileMeta` — DTO'lar.
- `model/NoteContentNode` — Mobil tarafta da aynı kalıtım yapısı. Static inner class olarak Text/Checklist/Image node'ları.
- `model/NoteContent` — JSONObject ile parse/serialize.
- `api/ApiClient` — OkHttp; `Authorization: Bearer <token>` header'ını otomatik ekliyor. multipart desteği var.
- `api/AuthApi`, `NoteApi`, `CategoryApi`, `UserApi`, `FileApi` — Her servis için ayrı.
- `session/SessionStore` — SharedPreferences ile token + userId saklama.
- `util/BackgroundExecutor` — UI thread'i bloklamadan API çağrısı (ExecutorService + Handler).
- `ui/` — LoginActivity, RegisterActivity, NoteListActivity (RecyclerView), NoteEditActivity (dosya eki dahil), SearchActivity (canlı arama), CategoryListActivity, ProfileActivity.

### `load-tests/`
k6 smoke / load / stress senaryoları. Register + create note + list + search akışını döngüye sokup p95 + fail rate ölçüyor.

## Hocanın Sorabileceği Sorular ve Cevaplar

**S: "Generic yapıyı nasıl kullandınız?"**
> `common-utils/generic` altında `BaseEntity<ID>` ve `GenericRepository<T extends BaseEntity<ID>, ID>` var. `JdbcNoteRepository` ve `JdbcCategoryRepository` bu arayüzü implement ediyor. `AbstractCrudService<T,ID>` template method pattern ile ortak CRUD'u veriyor; `NoteService` ve `CategoryService` ondan extend edip sadece kendine özgü iş mantığını ekliyor.

**S: "Hangi tasarım desenlerini kullandınız?"**
> Strategy (arama stratejileri), Factory (`SearchStrategyFactory`), Template Method (`AbstractCrudService.beforeCreate/beforeUpdate` hook'ları), Repository (her domain için ayrı repository), Aspect (`AuthGuardAspect` AOP ile yetki kontrolü).

**S: "Neden JDBC seçtiniz JPA değil?"**
> İsterler "JDBC ve gerçek NoSQL motoru" diyordu. JPA üzerinden değil, doğrudan `JdbcTemplate` + `RowMapper` ile SQL yazdık ki tablo yapısı + indeksler + sayfalama açık şekilde görünsün. Performans gerektiğinde de SQL'i elle optimize edebiliyoruz.

**S: "JDBC ve NoSQL nasıl izole?"**
> İki ayrı servis. `note-service` Postgres ile (relational), `user-service` ve `file-service` MongoDB ile (document + GridFS). Ortak kod yok, sadece common-utils. Aynı uygulamada iki ayrı veritabanı motoru çalışıyor.

**S: "Auth nasıl çalışıyor?"**
> Kullanıcı `/api/auth/login`'a istek atıyor, user-service BCrypt ile parolayı doğrulayıp JWT üretiyor. Mobil token'ı saklıyor, her isteğe `Authorization: Bearer <jwt>` ekliyor. Gateway'deki `AuthMiddlewareFilter` token'ı verify ediyor, kullanıcı bilgisini X-User-* header'larına dönüştürüp aşağı servise forward ediyor. Aşağı servis `@AuthGuard(UserRoles.USER)` annotation'ı ile bu header'ları kontrol ediyor — bunu AOP ile yapıyoruz, controller kodunu kirletmiyor.

**S: "Custom GUI ne demek? Standart Android bileşeni kullanmadınız mı?"**
> `NoteCardView` ve `ColorPaletteView`, `View` sınıfından doğrudan türetildi. İçinde `TextView`, `ImageView`, `LinearLayout` yok. `onDraw(Canvas)` metodunda Paint API ile kart arka planı, gölgesi, yuvarlatılmış köşeleri, pin ikonu (Path ile özel şekil), metin (TextPaint), tarih çiziliyor. Renk paleti yatay daire dizisi olarak çiziliyor ve dokunma olayını koordinat hesabı ile işliyor.

**S: "Mikroservisler arası iletişim nasıl?"**
> Şu an gateway tek giriş noktası, doğrudan servis-servis çağrı yok. Servisler birbirini bilmiyor, sadece gateway routing yapıyor. İleride iletişim gerekirse RestTemplate veya Feign eklenebilir.

**S: "Hata yönetimi?"**
> `common-utils/exception` altında custom exception hiyerarşisi var, her biri bir HTTP koduna mapleniyor (`GlobalExceptionHandler`). Validation hataları aynı handler'da; alan bazlı hata listesi dönüyor. Tüm hata mesajları `ErrorMessages` sınıfında sabit, magic string yok.

**S: "Performans testlerinde ne ölçtünüz?"**
> k6 ile 3 senaryo: smoke (1 VU, 30s), load (20→50 VU, 2dk), stress (50→200 VU, 3dk). Her senaryo register + create + list + search döngüsü. p95 ve fail rate eşik tanımlı. `docs/PERFORMANCE.md`'de detay var.

**S: "Note içeriği için kalıtım yapısı?"**
> `NoteContentNode` abstract sınıfından 3 alt tip türettik: `TextNode`, `ChecklistNode`, `ImageNode`. Jackson polymorphism ile JSON'a serialize ediyoruz (`@JsonTypeInfo`/`@JsonSubTypes`). Backend tarafında `NoteContentParser` String'i node listesine çeviriyor. Mobil tarafta da aynı sınıflar var (static inner class). `GET /api/notes/{id}/parsed` endpoint'i parse edilmiş halini döner. Resimler `file-service`'e yükleniyor, fileId node'a yazılıyor.

**S: "Docker'da ne var?"**
> 6 container: postgres, mongo, gateway, user-service, note-service, file-service. Healthcheck'ler tanımlı, depends_on ile sıralı kalkıyor. `docker compose up --build` tek komutla her şey.

## Demo Akışı (Sunumda)

1. **Backend'i göster**: `docker compose ps` çalıştır, 6 container UP olduğunu göster.
2. **Mobil'i göster**: Telefonda APK kur, kayıt ol, login ol, not oluştur, dosya ekle, kategori oluştur, arama yap, profili gör.
3. **Custom View'ı göster**: Not kartının çiziminde "bu TextView değil, Canvas'ta elle çizilmiş" diye vurgula. Renk paletinde dokunmayla halkanın hareketini göster.
4. **JDBC'yi göster**: `docker exec -it noteapp-postgres psql -U noteapp -d notes_db -c "SELECT * FROM notes LIMIT 5"`. Tablo + indeks göster.
5. **NoSQL'i göster**: `docker exec -it noteapp-mongo mongosh user_service --eval "db.users.find()"`. JSON document göster.
6. **k6 koş**: `k6 run load-tests/smoke.js`. p95 ve fail rate sonucunu göster.
7. **Mermaid diagramı**: README açıp gösterilebilir.

## Sunmaya Çalışmamak Gereken Şeyler

- "AI kullandık" demek YOK.
- Detaylara takılıp 5 dakikayı 1 sınıfa harcamak YOK. Geniş resim → Mermaid → custom view → demo.
- Tahmin etmediğin bir soru gelirse "şöyle düşündük ama emin değilim" deyip geç. "Bilmiyorum, sonra bakarım" diyebilirsin.

## Açıklayamadığın Bir Şey Gelirse

Aklında tut: tüm kod GitHub'da, paket adlarını biliyorsun, hangi sınıfın hangi paket altında olduğunu biliyorsun. Soruda kaybolursan paket-paket gezme stratejisine geç ("şu sınıf şu paketin altında, şunu yapıyor").
