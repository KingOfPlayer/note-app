# Performans Testleri (k6)

Bu klasor k6 ile yazilmis API performans senaryolarini icerir.

## Senaryolar

| Dosya | Amac | VU | Sure |
|---|---|---|---|
| `smoke.js` | Sistemin ayakta oldugunu dogrulayan minimum yuk | 1 | 30s |
| `load.js` | Beklenen kullanici yuku altinda davranisi olcer | 20 → 50 | 2 dk |
| `stress.js` | Sistemin kirilma noktasini bulur | 50 → 200 | 3 dk |

## Esikler

- `http_req_failed`: smoke %1, load %2, stress %10 altinda kalmali
- `http_req_duration` p95: smoke 500ms, load 800ms, stress 2000ms

## Calistirma

Once tum sistem ayakta olmali:
```bash
docker compose up -d
```

Sonra (proje kokunden):
```bash
k6 run load-tests/smoke.js
k6 run load-tests/load.js
k6 run load-tests/stress.js
```

Farkli bir gateway URL kullanmak icin:
```bash
k6 run -e GATEWAY_URL=http://localhost:8080 load-tests/load.js
```

## Ne olcuyoruz?

- Auth uzerinden gercek bir kullanici olusturup token alir
- Note olustur/listele/ara akisini her iter'de kosturur
- 4xx ve 5xx oranlarini ve 95p latency'yi raporlar
