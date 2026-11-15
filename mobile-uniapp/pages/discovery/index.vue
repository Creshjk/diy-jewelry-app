<template>
    <view class="shell">
      <view class="hero-card">
        <view class="hero-title">发现广场</view>
      </view>

    <view class="waterfall">
      <view v-for="(item, index) in posts" :key="item.id" class="panel post-card">
        <image :src="item.coverImageUrl" mode="aspectFill" class="post-image" @click="preview(index)" />
        <view class="post-title">{{ item.title }}</view>
        <view class="post-author">{{ item.nickname }}</view>
        <view class="post-desc">{{ item.description }}</view>
        <view class="post-footer">
          <text>{{ item.likeCount }} 赞</text>
          <button size="mini" class="like-btn" @click="toggleLike(item)">
            {{ item.liked ? '取消点赞' : '点赞' }}
          </button>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { requireLogin } from '../../common/guard'
import { request } from '../../common/http'
import { getCurrentUser } from '../../common/store'

export default {
  data() {
    return {
      posts: []
    }
  },
  onShow() {
    if (!requireLogin('/pages/discovery/index')) {
      return
    }
    this.loadPosts()
  },
  methods: {
    currentUserId() {
      const user = getCurrentUser()
      return user && user.id ? user.id : 1
    },
    async loadPosts() {
      this.posts = await request(`/discovery/posts?userId=${this.currentUserId()}`)
    },
    preview(index) {
      const urls = this.posts.map(item => item.coverImageUrl).filter(Boolean)
      const current = this.posts[index] && this.posts[index].coverImageUrl ? this.posts[index].coverImageUrl : ''
      if (!current) {
        return
      }
      uni.previewImage({
        urls,
        current
      })
    },
    async toggleLike(item) {
      const result = await request(`/discovery/posts/${item.id}/like?userId=${this.currentUserId()}`, {
        method: 'POST'
      })
      item.liked = result.liked
      item.likeCount = result.likeCount
    }
  }
}
</script>

<style scoped>
.waterfall {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 18rpx;
}

.post-card {
  padding: 18rpx;
}

.post-image {
  width: 100%;
  height: 300rpx;
  border-radius: 20rpx;
}

.post-title {
  margin-top: 14rpx;
  font-size: 30rpx;
  font-weight: 700;
}

.post-author,
.post-desc {
  margin-top: 8rpx;
  color: #766656;
  font-size: 24rpx;
}

.post-footer {
  margin-top: 14rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.like-btn {
  color: #a85a24;
}
</style>
