import http from 'k6/http';
import { check, sleep } from 'k6';
import { BASE, registerUser, authHeaders, createNote } from './common.js';

export const options = {
  stages: [
    { duration: '30s', target: 20 },
    { duration: '1m', target: 50 },
    { duration: '30s', target: 0 },
  ],
  thresholds: {
    http_req_failed: ['rate<0.02'],
    http_req_duration: ['p(95)<800', 'p(99)<1500'],
  },
};

export default function () {
  const user = registerUser(`${__VU}_${__ITER}`);
  if (!user) return;

  for (let i = 0; i < 3; i++) {
    const create = createNote(user, `Yuk testi notu ${i}`);
    check(create, { 'note 201': (r) => r.status === 201 });
  }

  const list = http.get(`${BASE}/api/notes?page=0&size=10`, { headers: authHeaders(user) });
  check(list, { 'liste 200': (r) => r.status === 200 });

  const search = http.get(`${BASE}/api/notes/search?q=Yuk&type=title`, { headers: authHeaders(user) });
  check(search, { 'arama 200': (r) => r.status === 200 });

  sleep(0.5);
}
