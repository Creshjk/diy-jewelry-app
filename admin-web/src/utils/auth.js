const ADMIN_KEY = 'diy_admin_user'

export function getAdminUser() {
  const raw = localStorage.getItem(ADMIN_KEY)
  return raw ? JSON.parse(raw) : null
}

export function setAdminUser(user) {
  localStorage.setItem(ADMIN_KEY, JSON.stringify(user))
}

export function clearAdminUser() {
  localStorage.removeItem(ADMIN_KEY)
}

export function isAdminLoggedIn() {
  return Boolean(getAdminUser())
}
