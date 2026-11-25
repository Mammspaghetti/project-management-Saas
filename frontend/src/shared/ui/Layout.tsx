import { Link, Outlet } from 'react-router-dom'
import { useAuth } from '../../features/auth/AuthContext'

export function Layout() {
  const { user, logout } = useAuth()

  return (
    <>
      <nav className="topbar">
        <Link to="/" className="brand">TaskFlow</Link>
        <span className="row">
          {user?.role === 'ADMIN' && <Link to="/admin/users">Admin</Link>}
          <span className="muted">{user?.displayName}</span>
          <button className="link" onClick={logout}>Sign out</button>
        </span>
      </nav>
      <main className="container">
        <Outlet />
      </main>
    </>
  )
}
