<template>
  <view class="shell">
    <view class="hero-card">
      <view class="hero-title">先登录，再使用 DIY 饰界</view>
      <view class="hero-sub">注册或登录后才能进入 AI 设计、首饰盒和发现广场。</view>
    </view>

    <view class="panel">
      <view class="section-title">{{ isRegister ? '注册账号' : '登录账号' }}</view>
      <input v-model="form.username" class="uni-input" placeholder="请输入账号" />
      <input v-model="form.password" class="uni-input" placeholder="请输入密码" password />
      <input v-if="isRegister" v-model="form.nickname" class="uni-input" placeholder="请输入昵称" />
      <button class="primary-btn" @click="submit">{{ isRegister ? '立即注册' : '立即登录' }}</button>
      <view class="switch-link" @click="toggleMode">
        {{ isRegister ? '已有账号，去登录' : '没有账号，去注册' }}
      </view>
      <view class="auth-tip">演示账号：demo / 123456</view>
    </view>
  </view>
</template>

<script>
import { request } from '../../common/http'
import { getCurrentUser, setCurrentUser } from '../../common/store'

export default {
  data() {
    return {
      isRegister: false,
      redirect: '/pages/design/index',
      form: {
        username: '',
        password: '',
        nickname: ''
      }
    }
  },
  onLoad(options) {
    if (options && options.redirect) {
      this.redirect = decodeURIComponent(options.redirect)
    }
    const user = getCurrentUser()
    if (user && user.id) {
      this.enterApp()
    }
  },
  methods: {
    toggleMode() {
      this.isRegister = !this.isRegister
    },
    async submit() {
      if (this.isRegister) {
        const user = await request('/auth/register', {
          method: 'POST',
          data: {
            username: this.form.username,
            password: this.form.password,
            nickname: this.form.nickname,
            gender: '保密'
          }
        })
        setCurrentUser(user)
        uni.showToast({ title: '注册成功', icon: 'success' })
      } else {
        const user = await request('/auth/login', {
          method: 'POST',
          data: {
            username: this.form.username,
            password: this.form.password,
            userType: 'user'
          }
        })
        setCurrentUser(user)
        uni.showToast({ title: '登录成功', icon: 'success' })
      }
      this.enterApp()
    },
    enterApp() {
      const tabPages = [
        '/pages/design/index',
        '/pages/box/index',
        '/pages/discovery/index',
        '/pages/profile/index'
      ]
      if (tabPages.includes(this.redirect)) {
        uni.switchTab({ url: this.redirect })
        return
      }
      uni.redirectTo({ url: this.redirect })
    }
  }
}
</script>

<style scoped>
.uni-input {
  margin-top: 18rpx;
  background: #f8f2eb;
  border-radius: 18rpx;
  padding: 22rpx;
}

.switch-link {
  margin-top: 24rpx;
  color: #9c5e2b;
  text-align: center;
}

.auth-tip {
  margin-top: 18rpx;
  color: #8d7a69;
  font-size: 24rpx;
  text-align: center;
}
</style>
