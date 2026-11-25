import { createContext, useCallback, useContext, useEffect, useMemo, useState, type ReactNode } from 'react'
import { setAccessToken, setUnauthorizedHandler } from '../../shared/api/http'
import type { AuthResponse, User } from '../../shared/api/types'
import { authApi } from './authApi'

interface Session {
  token: string
  user: User
}

interface AuthContextValue {
  user: User | null
  login: (email: string, password: string) => Promise<void>
  register: (email: string, password: string, displayName: string) => Promise<void>
  logout: () => void
}

const STORAGE_KEY = 'taskflow.session'
const AuthContext = createContext<AuthContextValue | null>(null)

function loadSession(): Session | null {
  try {
    const raw = localStorage.getItem(STORAGE_KEY)
    return raw ? (JSON.parse(raw) as Session) : null
  } catch {
    return null
  }
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const [session, setSession] = useState<Session | null>(() => {
    const stored = loadSession()
    setAccessToken(stored?.token ?? null)
    return stored
  })

  const start = useCallback((res: AuthResponse) => {
    const next = { token: res.accessToken, user: res.user }
    setAccessToken(next.token)
    localStorage.setItem(STORAGE_KEY, JSON.stringify(next))
    setSession(next)
  }, [])

  const logout = useCallback(() => {
    setAccessToken(null)
    localStorage.removeItem(STORAGE_KEY)
    setSession(null)
  }, [])

  useEffect(() => setUnauthorizedHandler(logout), [logout])

  const value = useMemo<AuthContextValue>(
    () => ({
      user: session?.user ?? null,
      login: async (email, password) => start(await authApi.login(email, password)),
      register: async (email, password, displayName) => start(await authApi.register(email, password, displayName)),
      logout,
    }),
    [session, start, logout],
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

// eslint-disable-next-line react-refresh/only-export-components
export function useAuth(): AuthContextValue {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used inside <AuthProvider>')
  return ctx
}
