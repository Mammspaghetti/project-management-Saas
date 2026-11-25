import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { useState, type FormEvent } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import type { ProjectRole, Task, TaskStatus } from '../../shared/api/types'
import { useAuth } from '../auth/AuthContext'
import { projectsApi } from './projectsApi'

const COLUMNS: { status: TaskStatus; label: string }[] = [
  { status: 'TODO', label: 'To do' },
  { status: 'IN_PROGRESS', label: 'In progress' },
  { status: 'DONE', label: 'Done' },
]

export function ProjectBoardPage() {
  const projectId = Number(useParams().projectId)
  const { user } = useAuth()
  const navigate = useNavigate()
  const queryClient = useQueryClient()
  const [title, setTitle] = useState('')
  const [memberEmail, setMemberEmail] = useState('')
  const [memberRole, setMemberRole] = useState<ProjectRole>('MEMBER')

  const project = useQuery({ queryKey: ['project', projectId], queryFn: () => projectsApi.get(projectId) })
  const tasks = useQuery({ queryKey: ['tasks', projectId], queryFn: () => projectsApi.tasks(projectId) })
  const members = useQuery({ queryKey: ['members', projectId], queryFn: () => projectsApi.members(projectId) })

  // Client-side RBAC is a UX affordance only: the API enforces the same rules.
  const myRole = members.data?.find((m) => m.userId === user?.id)?.role
  const isAdmin = user?.role === 'ADMIN'
  const canWrite = isAdmin || myRole === 'OWNER' || myRole === 'MEMBER'
  const canManage = isAdmin || myRole === 'OWNER'

  const refreshTasks = () => queryClient.invalidateQueries({ queryKey: ['tasks', projectId] })
  const refreshMembers = () => queryClient.invalidateQueries({ queryKey: ['members', projectId] })

  const addTask = useMutation({
    mutationFn: (t: string) => projectsApi.createTask(projectId, { title: t }),
    onSuccess: () => {
      setTitle('')
      return refreshTasks()
    },
  })
  const moveTask = useMutation({
    mutationFn: ({ task, status }: { task: Task; status: TaskStatus }) =>
      projectsApi.setTaskStatus(projectId, task.id, status),
    onSuccess: refreshTasks,
  })
  const removeTask = useMutation({
    mutationFn: (taskId: number) => projectsApi.removeTask(projectId, taskId),
    onSuccess: refreshTasks,
  })
  const addMember = useMutation({
    mutationFn: () => projectsApi.addMember(projectId, memberEmail, memberRole),
    onSuccess: () => {
      setMemberEmail('')
      return refreshMembers()
    },
  })
  const removeMember = useMutation({
    mutationFn: (userId: number) => projectsApi.removeMember(projectId, userId),
    onSuccess: refreshMembers,
  })
  const deleteProject = useMutation({
    mutationFn: () => projectsApi.remove(projectId),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ['projects'] })
      navigate('/')
    },
  })

  if (project.isPending) return <p className="muted">Loading…</p>
  if (project.isError) return <p role="alert" className="error">{project.error.message}</p>

  const error = [addTask, moveTask, removeTask, addMember, removeMember, deleteProject].find((m) => m.isError)?.error

  function onAddTask(e: FormEvent) {
    e.preventDefault()
    if (title.trim()) addTask.mutate(title.trim())
  }

  function onAddMember(e: FormEvent) {
    e.preventDefault()
    addMember.mutate()
  }

  return (
    <section>
      <header className="row between">
        <h1>{project.data.name}</h1>
        {canManage && (
          <button className="danger" onClick={() => confirm('Delete this project and all its tasks?') && deleteProject.mutate()}>
            Delete project
          </button>
        )}
      </header>
      {project.data.description && <p className="muted">{project.data.description}</p>}
      {error && <p role="alert" className="error">{error.message}</p>}

      {canWrite && (
        <form onSubmit={onAddTask} className="row">
          <input placeholder="New task title" value={title} onChange={(e) => setTitle(e.target.value)} maxLength={200} />
          <button type="submit" disabled={addTask.isPending}>Add task</button>
        </form>
      )}

      <div className="board">
        {COLUMNS.map(({ status, label }) => (
          <div key={status} className="column" aria-label={label}>
            <h2>{label}</h2>
            {tasks.data?.filter((t) => t.status === status).map((task) => (
              <article key={task.id} className="card task">
                <strong>{task.title}</strong>
                <span className={`badge ${task.priority.toLowerCase()}`}>{task.priority}</span>
                {canWrite && (
                  <div className="row">
                    <select
                      aria-label={`Status of ${task.title}`}
                      value={task.status}
                      onChange={(e) => moveTask.mutate({ task, status: e.target.value as TaskStatus })}
                    >
                      {COLUMNS.map((c) => <option key={c.status} value={c.status}>{c.label}</option>)}
                    </select>
                    <button className="link" onClick={() => removeTask.mutate(task.id)}>Delete</button>
                  </div>
                )}
              </article>
            ))}
          </div>
        ))}
      </div>

      <h2>Members</h2>
      <ul className="members">
        {members.data?.map((m) => (
          <li key={m.userId} className="row between">
            <span>{m.displayName} <span className="muted">({m.email})</span></span>
            <span className="row">
              <span className="badge">{m.role}</span>
              {canManage && m.role !== 'OWNER' && (
                <button className="link" onClick={() => removeMember.mutate(m.userId)}>Remove</button>
              )}
            </span>
          </li>
        ))}
      </ul>
      {canManage && (
        <form onSubmit={onAddMember} className="row">
          <input type="email" placeholder="Member email" value={memberEmail} onChange={(e) => setMemberEmail(e.target.value)} required />
          <select aria-label="Role" value={memberRole} onChange={(e) => setMemberRole(e.target.value as ProjectRole)}>
            <option value="MEMBER">Member</option>
            <option value="VIEWER">Viewer</option>
          </select>
          <button type="submit" disabled={addMember.isPending}>Invite</button>
        </form>
      )}
    </section>
  )
}
