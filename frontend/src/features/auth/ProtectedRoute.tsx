import { Navigate, Outlet, useLocation } from 'react-router-dom'
import type { GlobalRole } from '../../shared/api/types'
import { useAuth } from './AuthContext'

interface Props {
  /** When set, only users with this global role may enter (RBAC on the client: UX only, the API enforces it). */
  role?: GlobalRole
}

export function ProtectedRoute({ role }: Props) {
  const { user } = useAuth()
  const location = useLocation()

  if (!user) return <Navigate to="/login" replace state={{ from: location.pathname }} />
  if (role && user.role !== role) return <Navigate to="/" replace />
  return <Outlet />
}
