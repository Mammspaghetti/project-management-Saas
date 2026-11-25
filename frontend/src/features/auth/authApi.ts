import { http } from '../../shared/api/http'
import type { AuthResponse } from '../../shared/api/types'

export const authApi = {
  login: (email: string, password: string) =>
    http<AuthResponse>('/auth/login', { method: 'POST', body: { email, password } }),
  register: (email: string, password: string, displayName: string) =>
    http<AuthResponse>('/auth/register', { method: 'POST', body: { email, password, displayName } }),
}
