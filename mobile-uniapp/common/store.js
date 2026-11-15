const USER_KEY = 'diy_jewelry_user'
const DRAFT_KEY = 'diy_jewelry_draft'

export function getCurrentUser() {
  return uni.getStorageSync(USER_KEY) || null
}

export function setCurrentUser(user) {
  uni.setStorageSync(USER_KEY, user)
}

export function clearCurrentUser() {
  uni.removeStorageSync(USER_KEY)
}

export function getDesignDraft() {
  return uni.getStorageSync(DRAFT_KEY) || null
}

export function setDesignDraft(data) {
  uni.setStorageSync(DRAFT_KEY, data)
}

export function clearDesignDraft() {
  uni.removeStorageSync(DRAFT_KEY)
}
