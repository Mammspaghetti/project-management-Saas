import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { BrowserRouter, Route, Routes } from 'react-router-dom'
import { AdminUsersPage } from '../features/admin/AdminUsersPage'
import { AuthProvider } from '../features/auth/AuthContext'
import { LoginPage, RegisterPage } from '../features/auth/AuthPages'
import { ProtectedRoute } from '../features/auth/ProtectedRoute'
import { ProjectBoardPage } from '../features/projects/ProjectBoardPage'
import { ProjectsPage } from '../features/projects/ProjectsPage'
import { Layout } from '../shared/ui/Layout'

const queryClient = new QueryClient({
  defaultOptions: { queries: { retry: false, refetchOnWindowFocus: false } },
})

export function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <AuthProvider>
        <BrowserRouter>
          <Routes>
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />

            <Route element={<ProtectedRoute />}>
              <Route element={<Layout />}>
                <Route index element={<ProjectsPage />} />
                <Route path="projects/:projectId" element={<ProjectBoardPage />} />
                <Route element={<ProtectedRoute role="ADMIN" />}>
                  <Route path="admin/users" element={<AdminUsersPage />} />
                </Route>
              </Route>
            </Route>
          </Routes>
        </BrowserRouter>
      </AuthProvider>
    </QueryClientProvider>
  )
}
