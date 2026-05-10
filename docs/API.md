# API Dokümantasyonu

Tüm istekler Gateway üzerinden `http://localhost:8080` adresine atılır. JSON içerik tipi varsayılır (`application/json`). Kimlik gerektiren uçlar `X-User-Id` ve `X-User-Role` başlıklarını bekler.

## Auth — `/api/auth`

### POST `/api/auth/register`
İstek:
```json
{ "name": "Ali", "email": "ali@test.com", "password": "parola123" }
```
Yanıt (201):
```json
{
  "success": true,
  "message": "Kayit tamamlandi",
  "data": {
    "user": { "id": "...", "name": "Ali", "email": "ali@test.com", "role": "USER" },
    "token": "...."
  }
}
```

### POST `/api/auth/login`
İstek: `{ "email": "...", "password": "..." }` — yanıt aynı şema.

## Users — `/api/users`

| Yol | Açıklama |
|---|---|
| GET `/me` | Mevcut kullanıcı (X-User-Id zorunlu) |
| GET `/{id}` | Tek kullanıcı |
| PUT `/{id}` | Yalnızca kendi profilinizi güncelleyebilirsiniz |
| DELETE `/{id}` | Kendi hesabınızı silersiniz |
| GET `/` | Tüm kullanıcılar (yalnızca `ADMIN`, `AuthGuard` ile) |

PUT gövdesi: `{ "name": "Yeni Ad", "email": "yeni@test.com" }` — alanlar isteğe bağlıdır.

## Notes — `/api/notes`

| Yöntem | Yol | Parametreler |
|---|---|---|
| GET | `/` | `page`, `size` (varsayılan 0/20). Yanıt `PageResponse` |
| GET | `/{id}` | — |
| POST | `/` | `{ title, content, color, categoryId, pinned }` |
| PUT | `/{id}` | aynı şema |
| DELETE | `/{id}` | — |
| GET | `/search` | `type=title\|content\|all`, `q=` |
| GET | `/pinned` | — |
| GET | `/category/{id}` | — |
| POST | `/{id}/toggle-pin` | — |

### Örnek
```bash
curl -H "X-User-Id: u1" -H "X-User-Role: USER" \
  http://localhost:8080/api/notes/search?type=title&q=alisveris
```

## Categories — `/api/categories`

| Yöntem | Yol |
|---|---|
| GET | `/` |
| POST | `/` `{ name, color }` |
| PUT | `/{id}` |
| DELETE | `/{id}` |

## Files — `/api/files`

| Yöntem | Yol | Açıklama |
|---|---|---|
| POST | `/` | Multipart form: `file`, query `noteId` |
| GET | `/` | Listele (query `noteId` filtresi) |
| GET | `/{id}` | Metadata |
| GET | `/{id}/download` | Binary stream |
| DELETE | `/{id}` | Sahibine aittir, başkası silemez |

## Hata Formatı

```json
{
  "timestamp": "2026-05-10T14:32:11",
  "status": 404,
  "error": "Not Found",
  "message": "Not bulunamadi: 42"
}
```

Validation hatalarında ek olarak `fields` nesnesi döner:
```json
{
  "status": 400,
  "message": "Dogrulama hatasi",
  "fields": { "title": "Baslik bos olamaz" }
}
```

## HTTP Kod Eşlemesi

| Senaryo | Kod |
|---|---|
| Geçerli kayıt / oturum | 200 / 201 |
| Eksik / hatalı veri | 400 |
| Token / başlık yok veya geçersiz | 401 |
| Başkasının verisine erişim | 403 |
| Kayıt yok | 404 |
| Sunucu istisnası | 500 |
| Hedef servis ayakta değil | 503 |
| Timeout | 504 |
