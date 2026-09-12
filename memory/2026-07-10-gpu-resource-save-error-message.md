# GPU Resource Save Error Message

## Symptom

When saving a GPU resource, the backend can return an error body such as:

```json
{"requestId":"2075592436230524928","data":null,"code":500,"msg":"数据已存在"}
```

The admin frontend did not show the backend error message.

## Root Cause

If the backend response is delivered as an HTTP error, Axios enters the response interceptor's rejected branch in `ux-admin-main/src/utils/request.ts`. That branch used a comma expression:

```ts
(err) => (Promise.reject(new Error(err)), tryHideFullScreenLoading())
```

The expression returned the loading helper result instead of the rejected promise, swallowing the error and never reading `err.response.data.msg`.

## Fix

The rejected branch now hides loading, extracts `err.response.data.msg` or fallback messages, shows `ElMessage.error(msg)`, and returns `Promise.reject(new Error(msg))` so callers do not continue success flows.

## Verification

`pnpm run build` in `ux-admin-main` passed on 2026-07-10. Existing Sass deprecation and Rollup chunk warnings remain unrelated.
