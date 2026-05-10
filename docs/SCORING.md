# Puanlama Tablosu Eşleşmesi

Bu doküman ders puanlama tablosu ile projedeki kanıtları eşler.

## Zorunlu (65 puan)

| Madde | Puan | Karşılık | Kanıt |
|---|---|---|---|
| API & Back-end | 10 | Spring Boot REST API + 4 mikroservis | `backend/apps/*` |
| Generic Yapılar | 10 | `BaseEntity<ID>`, `GenericRepository<T,ID>`, `GenericService<T,ID>`, `AbstractCrudService<T,ID>`, `PageResponse<T>`, `ApiResponse<T>` | `common-utils/generic/*` |
| Custom GUI | 10 | `NoteCardView` (Canvas + Paint), `ColorPaletteView` | `frontend/.../ui/widget/*` |
| JDBC + NoSQL | 10 | `note-service` PostgreSQL + JdbcTemplate; `user-service`/`file-service` MongoDB. İzole modüller. | `note-service/repository/jdbc/*`, `user-service/Repositories/*`, `file-service/service/GridFsFileService` |
| SOLID + Design Patterns | 10 | Strategy, Factory, Template Method, Repository, AOP. Interface tabanlı bağımlılık. | `service/search/*`, `AbstractCrudService`, `AuthGuardAspect` |
| Hata Yönetimi | 5 | `GlobalExceptionHandler` HTTP kod eşlemesi + validation handler'ları | `common-utils/exception/*`, `controller/*ValidationHandler.java` |
| Performans Testleri | 5 | k6 ile smoke/load/stress | `load-tests/*` |
| Analiz & Doküman | 5 | README + Mermaid + ARCHITECTURE.md + API.md + PERFORMANCE.md | `README.md`, `docs/*` |

## Ek Özellikler (35 puan)

| Madde | Puan | Karşılık | Kanıt |
|---|---|---|---|
| Mobil GUI | +5 | Android Java uygulaması (Custom GUI + 5 birlikte 15 olur) | `frontend/` |
| Dockerize | +5 | `docker compose up --build` | `docker-compose.yml`, `*/Dockerfile` |
| Mikroservis | +10 | gateway + user + note + file ayrı modüller | `backend/apps/*` |
| API Gateway | +5 | reverse proxy + service registry | `gateway-service/proxy/ProxyController` |
| TDD | +10 | (opsiyonel — zaman kalırsa eklenir) | — |

## Notlar

- "JDBC + NoSQL izole katmanlarda" gereksinimi `note-service` (JDBC/Postgres) ile `user-service` + `file-service` (Mongo) ayrımıyla karşılanır. Aynı kullanıcının verisi farklı motorlardadır ve servisler birbirinden bağımsız olarak başlatılabilir.
- "Generic Yapılar" yalnızca `Generic<T>` veri yapıları değil aynı zamanda generic service tabanı olarak kullanılır; `NoteService extends AbstractCrudService<Note, Long>` somut örnektir.
- "Custom GUI" standart `TextView`/`ImageView` ile yapılmış değildir; `View` sınıfından türetilmiş ve `onDraw(Canvas)` içinde manuel çizilmiştir.
