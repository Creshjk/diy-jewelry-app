<template>
  <view class="shell">
    <view class="hero-card">
      <view class="hero-title">个人首饰盒</view>
      <view class="hero-sub">查看生成历史、回溯参数、重新编辑并一键发布到发现广场。</view>
    </view>

    <view v-for="item in records" :key="item.id" class="panel">
      <image :src="item.resultImageUrl" mode="aspectFill" class="box-image" @click="previewHistoryImage(item.resultImageUrl)" />
      <view class="preview-tip">点击图片可查看大图</view>
      <view class="box-title">{{ item.prompt }}</view>
      <view class="box-meta">{{ item.designMode }} · {{ item.clarityLevel }} · {{ item.aspectRatio }}</view>
      <view class="box-actions">
        <button size="mini" @click="viewDetail(item.id)">查看</button>
        <button size="mini" @click="restore(item)">回到设计页</button>
        <button size="mini" type="warn" @click="publish(item)">发布</button>
        <button size="mini" type="default" @click="remove(item.id)">删除</button>
      </view>
    </view>
  </view>
</template>

<script>
import { requireLogin } from '../../common/guard'
import { request } from '../../common/http'
import { getCurrentUser, setDesignDraft } from '../../common/store'

export default {
  data() {
    return {
      records: []
    }
  },
  onShow() {
    if (!requireLogin('/pages/box/index')) {
      return
    }
    this.loadRecords()
  },
  methods: {
    currentUserId() {
      const user = getCurrentUser()
      return user && user.id ? user.id : 1
    },
    async loadRecords() {
      this.records = await request(`/design/history?userId=${this.currentUserId()}`)
    },
    restore(item) {
      setDesignDraft(item)
      uni.switchTab({ url: '/pages/design/index' })
    },
    viewDetail(id) {
      uni.navigateTo({ url: `/pages/history/detail?id=${id}` })
    },
    previewHistoryImage(url) {
      if (!url) {
        return
      }
      uni.previewImage({
        urls: [url],
        current: url
      })
    },
    async publish(item) {
      await request('/discovery/posts', {
        method: 'POST',
        data: {
          userId: this.currentUserId(),
          designRecordId: item.id,
          title: item.prompt,
          description: item.aiSummary
        }
      })
      uni.showToast({ title: '已发布到发现', icon: 'success' })
      this.loadRecords()
    },
    async remove(id) {
      await request(`/design/history/${id}`, { method: 'DELETE' })
      uni.showToast({ title: '已删除', icon: 'success' })
      this.loadRecords()
    }
  }
}
</script>

<style scoped>
.box-image {
  width: 100%;
  height: 420rpx;
  border-radius: 24rpx;
}

.box-title {
  margin-top: 18rpx;
  font-size: 32rpx;
  font-weight: 700;
}

.box-meta {
  margin-top: 10rpx;
  color: #7c6b5b;
}

.box-actions {
  display: flex;
  gap: 12rpx;
  flex-wrap: wrap;
  margin-top: 18rpx;
}

.preview-tip {
  margin-top: 12rpx;
  color: #9a6d43;
  font-size: 24rpx;
  text-align: center;
}
</style>
