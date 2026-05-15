# Performans Testleri (k6)

Bu klasör k6 ile yazılmış API performans senaryolarını içerir.

## Senaryolar

| Dosya | Amaç | VU | Süre |
|---|---|---|---|
| `smoke.js` | Sistemin ayakta olduğunu doğrulayan minimum yük | 1 | 30s |
| `load.js` | Beklenen kullanıcı yükü altında davranışı ölçer | 0 → 20 → 50 → 0 | 2 dk |
| `stress.js` | Yük arttıkça gecikme davranışını ölçer | 0 → 50 → 100 → 200 → 0 | 3 dk |

## Eşikler

- `http_req_failed`: smoke `rate<0.01`, load `rate<0.02`, stress `rate<0.10`
- `http_req_duration`: smoke `p(95)<500ms`, load `p(95)<800ms` ve `p(99)<1500ms`, stress `p(95)<2000ms`

## Çalıştırma

Önce tüm sistem ayakta olmalı:
```bash
docker compose up -d
```

Sonra (proje kökünden):
```bash
k6 run load-tests/smoke.js
k6 run load-tests/load.js
k6 run load-tests/stress.js
```

Farklı bir gateway URL kullanmak için:
```bash
k6 run -e GATEWAY_URL=http://localhost:8080 load-tests/load.js
```

Sonuçları JSON olarak kaydetmek için:
```bash
k6 run --summary-export load-tests/smoke-result.json load-tests/smoke.js
k6 run --summary-export load-tests/load-result.json  load-tests/load.js
k6 run --summary-export load-tests/stress-result.json load-tests/stress.js
```
