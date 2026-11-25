import { http } from '../../shared/api/http'
import type { Member, Project, ProjectRole, Task, TaskInput, TaskStatus } from '../../shared/api/types'

export const projectsApi = {
  list: () => http<Project[]>('/projects'),
  get: (id: number) => http<Project>(`/projects/${id}`),
  create: (name: string, description?: string) =>
    http<Project>('/projects', { method: 'POST', body: { name, description } }),
  remove: (id: number) => http<void>(`/projects/${id}`, { method: 'DELETE' }),

  members: (id: number) => http<Member[]>(`/projects/${id}/members`),
  addMember: (id: number, email: string, role: ProjectRole) =>
    http<Member>(`/projects/${id}/members`, { method: 'POST', body: { email, role } }),
  removeMember: (id: number, userId: number) =>
    http<void>(`/projects/${id}/members/${userId}`, { method: 'DELETE' }),

  tasks: (id: number) => http<Task[]>(`/projects/${id}/tasks`),
  createTask: (id: number, input: TaskInput) =>
    http<Task>(`/projects/${id}/tasks`, { method: 'POST', body: input }),
  setTaskStatus: (id: number, taskId: number, status: TaskStatus) =>
    http<Task>(`/projects/${id}/tasks/${taskId}/status`, { method: 'PATCH', body: { status } }),
  removeTask: (id: number, taskId: number) =>
    http<void>(`/projects/${id}/tasks/${taskId}`, { method: 'DELETE' }),
}
