// Mirrors the backend DTOs (see docs/api.md).

export type GlobalRole = 'USER' | 'ADMIN'
export type ProjectRole = 'OWNER' | 'MEMBER' | 'VIEWER'
export type TaskStatus = 'TODO' | 'IN_PROGRESS' | 'DONE'
export type TaskPriority = 'LOW' | 'MEDIUM' | 'HIGH'

export interface User {
  id: number
  email: string
  displayName: string
  role: GlobalRole
}

export interface AuthResponse {
  accessToken: string
  tokenType: 'Bearer'
  expiresIn: number
  user: User
}

export interface Project {
  id: number
  name: string
  description: string | null
  ownerId: number
  createdAt: string
  memberCount: number
}

export interface Member {
  userId: number
  email: string
  displayName: string
  role: ProjectRole
}

export interface Task {
  id: number
  projectId: number
  title: string
  description: string | null
  status: TaskStatus
  priority: TaskPriority
  assigneeId: number | null
  dueDate: string | null
  createdAt: string
  updatedAt: string
}

export interface TaskInput {
  title: string
  description?: string
  priority?: TaskPriority
  assigneeId?: number | null
  dueDate?: string | null
}
