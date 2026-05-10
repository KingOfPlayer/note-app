import http from 'k6/http';
import { check, sleep } from 'k6';
import { BASE, registerUser, authHeaders, createNote } from './common.js';

export const options = {
  stages: [
    { duration: '30s', target: 50 },
    { duration: '1m', target: 100 },
    { duration: '1m', target: 200 },
    { duration: '30s', target: 0 },
  ],
  thresholds: {
    http_req_failed: ['rate<0.10'],
    http_req_duration: ['p(95)<2000'],
  },
};

export default function () {
  const user = registerUser(`stress_${__VU}_${__ITER}`);
  if (!user) return;

  const create = createNote(user, `Stres notu ${__ITER}`);
  check(create, { 'note 201': (r) => r.status === 201 });

  const list = http.get(`${BASE}/api/notes`, { headers: authHeaders(user) });
  check(list, { 'liste 200/5xx degil': (r) => r.status < 500 });

  sleep(0.2);
}
