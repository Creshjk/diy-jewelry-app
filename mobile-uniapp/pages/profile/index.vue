<template>
    <view class="shell">
      <view class="hero-card">
        <view class="hero-title">我的账号</view>
      </view>

    <view v-if="currentUser" class="panel">
      <view class="section-title">个人信息</view>

      <view class="avatar-wrap">
        <image :src="form.avatar || currentUser.avatar" mode="aspectFill" class="avatar-image" @click="chooseAvatar" />
        <view class="avatar-tip">点击头像可更换</view>
      </view>

      <view class="user-line">账号：{{ currentUser.username }}</view>
      <input v-model="form.nickname" class="uni-input" placeholder="请输入昵称" />
      <input v-model="form.phone" class="uni-input" placeholder="请输入手机号" />

      <picker :range="genderOptions" :value="genderIndex" @change="changeGender">
        <view class="picker-item">性别：{{ form.gender || '保密' }}</view>
      </picker>

      <button class="primary-btn" @click="saveProfile">保存资料</button>
      <button class="logout-btn" @click="logout">退出登录</button>
    </view>
  </view>
</template>

<script>
import { requireLogin } from '../../common/guard'
import { request } from '../../common/http'
import { clearCurrentUser, getCurrentUser, setCurrentUser } from '../../common/store'

export default {
  data() {
    return {
      currentUser: null,
      genderOptions: ['女', '男', '保密'],
      form: {
        id: null,
        nickname: '',
        avatar: '',
        gender: '保密',
        phone: ''
      }
    }
  },
  computed: {
    genderIndex() {
      const index = this.genderOptions.indexOf(this.form.gender)
      return index >= 0 ? index : 2
    }
  },
  onShow() {
    if (!requireLogin('/pages/profile/index')) {
      return
    }
    this.loadProfile()
  },
  methods: {
    syncUser() {
      this.currentUser = getCurrentUser()
    },
    async loadProfile() {
      this.syncUser()
      const profile = await request(`/auth/profile?userId=${this.currentUser.id}`)
      this.currentUser = profile
      this.form.id = profile.id
      this.form.nickname = profile.nickname || ''
      this.form.avatar = profile.avatar || ''
      this.form.gender = profile.gender || '保密'
      this.form.phone = profile.phone || ''
      setCurrentUser(profile)
    },
    chooseAvatar() {
      uni.chooseImage({
        count: 1,
        sizeType: ['compressed'],
        sourceType: ['album', 'camera'],
        success: (res) => {
          if (res.tempFilePaths && res.tempFilePaths.length) {
            this.form.avatar = res.tempFilePaths[0]
          }
        }
      })
    },
    changeGender(event) {
      this.form.gender = this.genderOptions[event.detail.value]
    },
    async saveProfile() {
      const profile = await request('/auth/profile', {
        method: 'PUT',
        data: this.form
      })
      this.currentUser = profile
      setCurrentUser(profile)
      uni.showToast({
        title: '资料已保存',
        icon: 'success'
      })
    },
    logout() {
      clearCurrentUser()
      uni.reLaunch({
        url: '/pages/auth/index?redirect=%2Fpages%2Fdesign%2Findex'
      })
    }
  }
}
</script>

<style scoped>
.avatar-wrap {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 20rpx;
}

.avatar-image {
  width: 180rpx;
  height: 180rpx;
  border-radius: 50%;
  border: 6rpx solid rgba(208, 140, 72, 0.22);
}

.avatar-tip {
  margin-top: 14rpx;
  color: #9a6d43;
  font-size: 24rpx;
}

.user-line {
  margin-top: 14rpx;
  color: #6c5b4b;
}

.uni-input,
.picker-item {
  margin-top: 18rpx;
  background: #f8f2eb;
  border-radius: 18rpx;
  padding: 22rpx;
}

.logout-btn {
  margin-top: 18rpx;
  border-radius: 999rpx;
  background: #ffffff;
  color: #a7652c;
  border: 2rpx solid #d9b189;
}
</style>
