import { getCurrentUser } from './store'

export function requireLogin(redirectUrl) {
  const user = getCurrentUser()
  if (user && user.id) {
    return true
  }
  const target = redirectUrl || '/pages/design/index'
  uni.reLaunch({
    url: `/pages/auth/index?redirect=${encodeURIComponent(target)}`
  })
  return false
}
