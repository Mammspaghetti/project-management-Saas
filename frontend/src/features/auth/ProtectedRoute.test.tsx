import { render, screen } from '@testing-library/react'
import { MemoryRouter, Route, Routes } from 'react-router-dom'
import { beforeEach, describe, expect, it } from 'vitest'
import { AuthProvider } from './AuthContext'
import { ProtectedRoute } from './ProtectedRoute'

function renderAt(path: string) {
  return render(
    <AuthProvider>
      <MemoryRouter initialEntries={[path]}>
        <Routes>
          <Route path="/login" element={<p>login page</p>} />
          <Route path="/" element={<p>home</p>} />
          <Route element={<ProtectedRoute />}>
            <Route path="/private" element={<p>private page</p>} />
          </Route>
          <Route element={<ProtectedRoute role="ADMIN" />}>
            <Route path="/admin" element={<p>admin page</p>} />
          </Route>
        </Routes>
      </MemoryRouter>
    </AuthProvider>,
  )
}

function signIn(role: 'USER' | 'ADMIN') {
  localStorage.setItem(
    'taskflow.session',
    JSON.stringify({ token: 't', user: { id: 1, email: 'a@b.c', displayName: 'A', role } }),
  )
}

describe('ProtectedRoute', () => {
  beforeEach(() => localStorage.clear())

  it('redirects anonymous visitors to the login page', () => {
    renderAt('/private')
    expect(screen.getByText('login page')).toBeInTheDocument()
  })

  it('lets an authenticated user through', () => {
    signIn('USER')
    renderAt('/private')
    expect(screen.getByText('private page')).toBeInTheDocument()
  })

  it('keeps non-admins out of admin routes', () => {
    signIn('USER')
    renderAt('/admin')
    expect(screen.getByText('home')).toBeInTheDocument()
  })

  it('lets admins into admin routes', () => {
    signIn('ADMIN')
    renderAt('/admin')
    expect(screen.getByText('admin page')).toBeInTheDocument()
  })
})
