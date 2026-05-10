# Performans Raporu

## Senaryolar

| Test | VU profili | Süre | Amaç |
|---|---|---|---|
| smoke.js | 1 sabit | 30 s | Sistem ayakta mı |
| load.js | 0 → 20 → 50 → 0 | 2 dk | Beklenen yük |
| stress.js | 50 → 100 → 200 → 0 | 3 dk | Kırılma noktası |

## Eşik Tanımları

```js
// smoke
http_req_failed:    rate < 0.01
http_req_duration:  p(95) < 500ms

// load
http_req_failed:    rate < 0.02
http_req_duration:  p(95) < 800ms,  p(99) < 1500ms

// stress
http_req_failed:    rate < 0.10
http_req_duration:  p(95) < 2000ms
```

## Çalıştırma

```bash
# 1) sistem ayakta olsun
docker compose up -d

# 2) testler
k6 run load-tests/smoke.js
k6 run load-tests/load.js
k6 run load-tests/stress.js
```

## Beklenen Davranış

- **Smoke**: Tek kullanıcı, register + create note + list akışı 100ms ortalama altında kalmalı.
- **Load**: 50 VU'da p95 < 800ms; gateway + JDBC + Mongo darboğaz oluşturmamalı.
- **Stress**: 200 VU sonrasında failure oranı yükselir; 5xx oranı %10'u geçtiği eşik kırılma noktası kabul edilir.

## Ölçülen Metrikler

- `http_req_duration` (p95, p99) — gecikme dağılımı
- `http_req_failed` — başarısız istek oranı
- `http_reqs` — saniyedeki istek
- `iteration_duration` — bir kullanıcının tam senaryosu
- `vus` — eş zamanlı sanal kullanıcı

## Notlar

- Tüm istekler Gateway (8080) üzerinden geçer; Gateway'in proxy maliyeti her ölçümün içindedir.
- `note-service` JDBC tarafında `notes` tablosuna `is_pinned, updated_at` indeksleri eklendi; sayfalama ve arama bu indekslerden yararlanır.
- `user-service` her register'da BCrypt cost 10 kullanır; CPU bağımlı ölçümlerde register oranı sınırlıdır.
- Stres testinde failure'lar genelde Mongo yazıcı sıkışmasından gelir; `mongo` servisi tek instance olduğu için yatay ölçeklenebilir değildir.

## Sonuçların Kaydı

Test çıktısını JSON olarak almak için:
```bash
k6 run --out json=docs/perf-load.json load-tests/load.js
```

Test koşumu sonrası ekran görüntüleri `docs/screenshots/` altında saklanabilir.
