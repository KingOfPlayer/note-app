import http from 'k6/http';
import { check, sleep } from 'k6';
import { BASE, registerUser, authHeaders, createNote } from './common.js';

export const options = {
  vus: 1,
  duration: '30s',
  thresholds: {
    http_req_failed: ['rate<0.01'],
    http_req_duration: ['p(95)<500'],
  },
};

export default function () {
  const user = registerUser(__VU);
  if (!user) return;

  const created = createNote(user, 'Smoke testi notu');
  check(created, {
    'note olusturuldu (201)': (r) => r.status === 201,
  });

  const list = http.get(`${BASE}/api/notes`, { headers: authHeaders(user) });
  check(list, {
    'liste 200': (r) => r.status === 200,
    'en az 1 not var': (r) => r.json().data.items.length >= 1,
  });

  sleep(1);
}
