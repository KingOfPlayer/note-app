import http from 'k6/http';
import { check } from 'k6';

export const BASE = __ENV.GATEWAY_URL || 'http://localhost:8080';

export function registerUser(suffix) {
  const email = `k6_${suffix}_${Date.now()}@test.com`;
  const payload = JSON.stringify({
    name: `K6 Kullanici ${suffix}`,
    email: email,
    password: 'parola123',
  });
  const res = http.post(`${BASE}/api/auth/register`, payload, {
    headers: { 'Content-Type': 'application/json' },
  });
  check(res, { 'register 201': (r) => r.status === 201 });
  if (res.status !== 201) return null;
  const body = res.json();
  return {
    id: body.data.user.id,
    email: email,
    token: body.data.token,
  };
}

export function authHeaders(user) {
  return {
    'Content-Type': 'application/json',
    'Authorization': 'Bearer ' + user.token,
  };
}

export function createNote(user, title) {
  const payload = JSON.stringify({
    title: title || `Not ${Math.random().toString(36).slice(2, 8)}`,
    content: 'k6 testi tarafindan olusturulan icerik',
    color: '#FFD54F',
    pinned: false,
  });
  return http.post(`${BASE}/api/notes`, payload, { headers: authHeaders(user) });
}
