import { afterEach, describe, expect, it, vi } from 'vitest'
import { ApiError, http, setAccessToken, setUnauthorizedHandler } from './http'

function mockFetch(status: number, body?: unknown) {
  const fn = vi.fn().mockResolvedValue(new Response(body === undefined ? null : JSON.stringify(body), { status }))
  vi.stubGlobal('fetch', fn)
  return fn
}

describe('http client', () => {
  afterEach(() => {
    vi.unstubAllGlobals()
    setAccessToken(null)
    setUnauthorizedHandler(() => {})
  })

  it('sends the bearer token and parses JSON', async () => {
    const fetchMock = mockFetch(200, [{ id: 1 }])
    setAccessToken('abc')

    await expect(http('/projects')).resolves.toEqual([{ id: 1 }])
    const [url, init] = fetchMock.mock.calls[0]
    expect(url).toBe('/api/v1/projects')
    expect(init.headers.Authorization).toBe('Bearer abc')
  })

  it('maps RFC 7807 problems to ApiError with field errors', async () => {
    mockFetch(400, { detail: 'Validation failed', errors: { email: 'must be a well-formed email address' } })

    const error = (await http('/auth/register', { method: 'POST', body: {} }).catch((e: unknown) => e)) as ApiError
    expect(error).toBeInstanceOf(ApiError)
    expect(error.status).toBe(400)
    expect(error.fieldErrors.email).toBeDefined()
  })

  it('notifies the unauthorized handler when an authenticated call gets a 401', async () => {
    const handler = vi.fn()
    setUnauthorizedHandler(handler)
    setAccessToken('expired')
    mockFetch(401, { detail: 'expired' })

    await http('/projects').catch(() => {})
    expect(handler).toHaveBeenCalledOnce()
  })

  it('returns undefined on 204', async () => {
    mockFetch(204)
    await expect(http('/projects/1', { method: 'DELETE' })).resolves.toBeUndefined()
  })
})
