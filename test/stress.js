import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  vus: 10,         // Virtual users
  duration: '20s', // Durations
};

export default function () {
  let res = http.post('http://localhost:8066/account/transaction/transfer', JSON.stringify({
    fromAccountId: 'id1',
    toAccountId: 'id2',
    amount: 100,
    idempotencyKey: `${__VU}-${__ITER}`,
    initiatedBy: 'id1'
  }), {
    headers: { 'Content-Type': 'application/json' },
  });

  check(res, {
    'status is 200': (r) => r.status === 200 || r.status === 201
  });

  sleep(1);
}