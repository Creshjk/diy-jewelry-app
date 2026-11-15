<template>
  <view class="shell" v-if="detail.record">
    <view class="panel">
      <image :src="detail.record.resultImageUrl" mode="aspectFill" class="detail-image" />
      <view class="detail-title">{{ detail.record.prompt }}</view>
      <view class="detail-summary">{{ detail.record.aiSummary }}</view>
      <view class="chip-row">
        <view class="chip">{{ detail.record.designMode }}</view>
        <view class="chip">{{ detail.record.clarityLevel }}</view>
        <view class="chip">{{ detail.record.aspectRatio }}</view>
      </view>
    </view>
  </view>
</template>

<script>
import { requireLogin } from '../../common/guard'
import { request } from '../../common/http'

export default {
  data() {
    return {
      detail: {}
    }
  },
  onLoad(options) {
    if (!requireLogin(`/pages/history/detail?id=${options.id}`)) {
      return
    }
    this.loadDetail(options.id)
  },
  methods: {
    async loadDetail(id) {
      this.detail = await request(`/design/history/${id}`)
    }
  }
}
</script>

<style scoped>
.detail-image {
  width: 100%;
  height: 560rpx;
  border-radius: 24rpx;
}

.detail-title {
  margin-top: 18rpx;
  font-size: 34rpx;
  font-weight: 700;
}

.detail-summary {
  margin: 16rpx 0;
  line-height: 1.7;
  color: #6d5a4b;
}
</style>
