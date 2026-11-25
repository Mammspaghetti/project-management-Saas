/** Minimal typed fetch wrapper: bearer token, RFC 7807 errors, 401 handling. */

export class ApiError extends Error {
  status: number
  fieldErrors: Record<string, string>

  constructor(status: number, message: string, fieldErrors: Record<string, string> = {}) {
    super(message)
    this.name = 'ApiError'
    this.status = status
    this.fieldErrors = fieldErrors
  }
}

let accessToken: string | null = null
let onUnauthorized: () => void = () => {}

export function setAccessToken(token: string | null) {
  accessToken = token
}

/** Registered by the auth provider: called when an authenticated request gets a 401 (expired token). */
export function setUnauthorizedHandler(handler: () => void) {
  onUnauthorized = handler
}

interface RequestOptions {
  method?: 'GET' | 'POST' | 'PUT' | 'PATCH' | 'DELETE'
  body?: unknown
  query?: Record<string, string | undefined>
}

export async function http<T>(path: string, { method = 'GET', body, query }: RequestOptions = {}): Promise<T> {
  const params = new URLSearchParams()
  for (const [key, value] of Object.entries(query ?? {})) {
    if (value !== undefined) params.set(key, value)
  }
  const qs = params.size > 0 ? `?${params}` : ''

  const headers: Record<string, string> = {}
  if (body !== undefined) headers['Content-Type'] = 'application/json'
  if (accessToken) headers.Authorization = `Bearer ${accessToken}`

  const response = await fetch(`/api/v1${path}${qs}`, {
    method,
    headers,
    body: body === undefined ? undefined : JSON.stringify(body),
  })

  if (response.ok) {
    return (response.status === 204 ? undefined : await response.json()) as T
  }

  if (response.status === 401 && accessToken) onUnauthorized()

  const problem = await response.json().catch(() => ({}))
  throw new ApiError(
    response.status,
    problem.detail ?? problem.title ?? `Request failed (${response.status})`,
    problem.errors ?? {},
  )
}
