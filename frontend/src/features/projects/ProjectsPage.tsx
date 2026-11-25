import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { useState, type FormEvent } from 'react'
import { Link } from 'react-router-dom'
import { projectsApi } from './projectsApi'

export function ProjectsPage() {
  const queryClient = useQueryClient()
  const [name, setName] = useState('')
  const projects = useQuery({ queryKey: ['projects'], queryFn: projectsApi.list })

  const create = useMutation({
    mutationFn: (projectName: string) => projectsApi.create(projectName),
    onSuccess: () => {
      setName('')
      return queryClient.invalidateQueries({ queryKey: ['projects'] })
    },
  })

  function onSubmit(e: FormEvent) {
    e.preventDefault()
    if (name.trim()) create.mutate(name.trim())
  }

  return (
    <section>
      <h1>Projects</h1>
      <form onSubmit={onSubmit} className="row">
        <input placeholder="New project name" value={name} onChange={(e) => setName(e.target.value)} maxLength={120} />
        <button type="submit" disabled={create.isPending}>Create</button>
      </form>
      {create.isError && <p role="alert" className="error">{create.error.message}</p>}

      {projects.isPending && <p className="muted">Loading…</p>}
      {projects.isError && <p role="alert" className="error">{projects.error.message}</p>}
      {projects.data?.length === 0 && <p className="muted">No project yet. Create your first one above.</p>}
      <ul className="cards">
        {projects.data?.map((p) => (
          <li key={p.id} className="card">
            <Link to={`/projects/${p.id}`}><strong>{p.name}</strong></Link>
            <span className="muted">{p.memberCount} member(s)</span>
          </li>
        ))}
      </ul>
    </section>
  )
}
