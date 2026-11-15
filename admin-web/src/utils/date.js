export function formatDateTime(value) {
  if (!value) return '-'
  if (typeof value === 'string') {
    return value.length >= 16 ? value.slice(0, 16) : value
  }
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return '-'
  const pad = (num) => String(num).padStart(2, '0')
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}`
}
