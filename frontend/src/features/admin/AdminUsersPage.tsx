import { useQuery } from '@tanstack/react-query'
import { http } from '../../shared/api/http'
import type { User } from '../../shared/api/types'

export function AdminUsersPage() {
  const users = useQuery({ queryKey: ['admin', 'users'], queryFn: () => http<User[]>('/admin/users') })

  return (
    <section>
      <h1>Users</h1>
      {users.isPending && <p className="muted">Loading…</p>}
      {users.isError && <p role="alert" className="error">{users.error.message}</p>}
      <table>
        <thead>
          <tr><th>Name</th><th>Email</th><th>Role</th></tr>
        </thead>
        <tbody>
          {users.data?.map((u) => (
            <tr key={u.id}><td>{u.displayName}</td><td>{u.email}</td><td>{u.role}</td></tr>
          ))}
        </tbody>
      </table>
    </section>
  )
}
