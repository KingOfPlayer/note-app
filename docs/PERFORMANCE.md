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

- **Smoke**: Tek kullanıcı ile register + note oluştur + listele akışının temel sağlık kontrolünü yapar.
- **Load**: Beklenen yük altında gecikme ve hata oranlarını ölçer.
- **Stress**: Daha yüksek eş zamanlılıkta sistemin gecikme davranışını gözlemler.

## Ölçülen Metrikler

- `http_req_duration` (p95, p99) — gecikme dağılımı
- `http_req_failed` — başarısız istek oranı
- `http_reqs` — saniyedeki istek
- `iteration_duration` — bir kullanıcının tam senaryosu
- `vus` — eş zamanlı sanal kullanıcı

## Notlar

- Tüm istekler Gateway (8080) üzerinden geçer; Gateway'in proxy maliyeti her ölçümün içindedir.
- `note-service` PostgreSQL şemasını `schema.sql` ile başlatır ve temel indeksleri oluşturur.

## Sonuçların Kaydı

Test çıktısını JSON olarak almak için:
```bash
k6 run --summary-export load-tests/smoke-result.json load-tests/smoke.js
k6 run --summary-export load-tests/load-result.json  load-tests/load.js
k6 run --summary-export load-tests/stress-result.json load-tests/stress.js
```

Repo'da kayıtlı örnek sonuçlar `load-tests/*-result.json` dosyalarıdır.

## Kayıtlı Sonuç Özeti

| Dosya | Checks (pass/fail) | `http_req_failed.value` | `http_req_duration` med (ms) | p90 (ms) | p95 (ms) | max (ms) | avg (ms) | `http_reqs.rate` (req/s) | `iterations.count` | `iterations.rate` (iter/s) | `vus_max` |
|---|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|
| smoke-result.json | 104/0 | 0 | 14.63 | 153.529 | 156.111 | 191.455 | 60.8 | 2.535 | 26 | 0.845 | 1 |
| load-result.json | 11556/0 | 0 | 16.461 | 674.491 | 1361.718 | 3702.323 | 191.186 | 95.983 | 1926 | 15.997 | 50 |
| stress-result.json | 8334/0 | 0 | 34.884 | 8213.839 | 10841.72 | 18013.825 | 2096.139 | 46.219 | 2778 | 15.406 | 200 |

