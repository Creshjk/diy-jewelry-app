<template>
  <view class="shell">
    <view class="hero-card">
      <view class="hero-title">教程材料</view>
      <view class="hero-sub">从制作教程到材质搭配建议，帮你把设计灵感真正落到成品。</view>
    </view>

    <view class="panel content-panel">
      <view class="section-head">
        <view class="section-title">首饰教程</view>
        <view class="section-tip">点击卡片可打开教程链接</view>
      </view>
      <view v-if="tutorials.length" class="waterfall">
        <view class="waterfall-column">
          <view
            v-for="item in tutorialColumns.left"
            :key="`tutorial-left-${item.id}`"
            class="waterfall-card tutorial-card"
            @click="openLink(item.linkUrl)"
          >
            <image :src="resolveImageUrl(item.coverImageUrl)" mode="aspectFill" class="card-image tutorial-image" @click.stop="previewImage(item.coverImageUrl)" />
            <view class="card-badge">教程</view>
            <view class="card-title">{{ item.title }}</view>
            <view class="card-desc">{{ item.summary || '暂无摘要说明' }}</view>
            <view class="card-footer">
              <view class="footer-tag">图文指引</view>
              <view class="footer-link">查看教程</view>
            </view>
          </view>
        </view>
        <view class="waterfall-column">
          <view
            v-for="item in tutorialColumns.right"
            :key="`tutorial-right-${item.id}`"
            class="waterfall-card tutorial-card"
            @click="openLink(item.linkUrl)"
          >
            <image :src="resolveImageUrl(item.coverImageUrl)" mode="aspectFill" class="card-image tutorial-image alt" @click.stop="previewImage(item.coverImageUrl)" />
            <view class="card-badge">教程</view>
            <view class="card-title">{{ item.title }}</view>
            <view class="card-desc">{{ item.summary || '暂无摘要说明' }}</view>
            <view class="card-footer">
              <view class="footer-tag">图文指引</view>
              <view class="footer-link">查看教程</view>
            </view>
          </view>
        </view>
      </view>
      <view v-else class="empty-tip">暂无教程内容</view>
    </view>

    <view class="panel content-panel">
      <view class="section-head">
        <view class="section-title">材料推荐</view>
        <view class="section-tip">按佩戴部位给出材料选择建议</view>
      </view>
      <view v-if="materials.length" class="waterfall">
        <view class="waterfall-column">
          <view
            v-for="item in materialColumns.left"
            :key="`material-left-${item.id}`"
            class="waterfall-card material-card"
            @click="openLink(item.linkUrl)"
          >
            <image :src="resolveImageUrl(item.coverImageUrl)" mode="aspectFill" class="card-image material-image" @click.stop="previewImage(item.coverImageUrl)" />
            <view class="material-top">
              <view class="part-chip">{{ item.bodyPart }}</view>
              <view class="material-name">{{ item.materialName }}</view>
            </view>
            <view class="card-desc">{{ item.description || '暂无材料说明' }}</view>
            <view class="card-footer">
              <view class="footer-tag">材料建议</view>
              <view class="footer-link">查看链接</view>
            </view>
          </view>
        </view>
        <view class="waterfall-column">
          <view
            v-for="item in materialColumns.right"
            :key="`material-right-${item.id}`"
            class="waterfall-card material-card"
            @click="openLink(item.linkUrl)"
          >
            <image :src="resolveImageUrl(item.coverImageUrl)" mode="aspectFill" class="card-image material-image alt" @click.stop="previewImage(item.coverImageUrl)" />
            <view class="material-top">
              <view class="part-chip">{{ item.bodyPart }}</view>
              <view class="material-name">{{ item.materialName }}</view>
            </view>
            <view class="card-desc">{{ item.description || '暂无材料说明' }}</view>
            <view class="card-footer">
              <view class="footer-tag">材料建议</view>
              <view class="footer-link">查看链接</view>
            </view>
          </view>
        </view>
      </view>
      <view v-else class="empty-tip">暂无材料推荐</view>
    </view>
  </view>
</template>

<script>
import { requireLogin } from '../../common/guard'
import { getBackendOrigin, request } from '../../common/http'

export default {
  data() {
    return {
      tutorials: [],
      materials: []
    }
  },
  computed: {
    tutorialColumns() {
      return this.splitColumns(this.tutorials)
    },
    materialColumns() {
      return this.splitColumns(this.materials)
    }
  },
  onShow() {
    if (!requireLogin('/pages/content/index')) {
      return
    }
    this.loadData()
  },
  methods: {
    async loadData() {
      this.tutorials = await request('/content/tutorials')
      this.materials = await request('/content/materials')
    },
    splitColumns(list) {
      const left = []
      const right = []
      list.forEach((item, index) => {
        if (index % 2 === 0) {
          left.push(item)
        } else {
          right.push(item)
        }
      })
      return { left, right }
    },
    previewImage(url) {
      const imageUrl = this.resolveImageUrl(url)
      if (!imageUrl) {
        return
      }
      uni.previewImage({
        urls: [imageUrl],
        current: imageUrl
      })
    },
    resolveImageUrl(url) {
      if (!url) {
        return ''
      }
      if (/^https?:\/\//.test(url)) {
        return url
      }
      return `${getBackendOrigin()}${url}`
    },
    openLink(url) {
      if (!url) {
        uni.showToast({
          title: '当前内容未配置链接',
          icon: 'none'
        })
        return
      }
      // #ifdef APP-PLUS
      plus.runtime.openURL(url)
      // #endif
      // #ifndef APP-PLUS
      uni.setClipboardData({ data: url })
      uni.showToast({ title: '链接已复制', icon: 'none' })
      // #endif
    }
  }
}
</script>

<style scoped>
.content-panel {
  overflow: hidden;
}

.section-head {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  margin-bottom: 18rpx;
}

.section-tip {
  font-size: 22rpx;
  color: #9b7c63;
}

.waterfall {
  display: flex;
  gap: 18rpx;
  align-items: flex-start;
}

.waterfall-column {
  flex: 1;
}

.waterfall-card {
  position: relative;
  margin-bottom: 18rpx;
  padding: 16rpx;
  border-radius: 26rpx;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.96), rgba(250, 242, 234, 0.92));
  box-shadow: 0 12rpx 34rpx rgba(96, 67, 40, 0.1);
}

.tutorial-card {
  border: 2rpx solid rgba(233, 203, 173, 0.45);
}

.material-card {
  border: 2rpx solid rgba(213, 191, 164, 0.42);
}

.card-image {
  width: 100%;
  border-radius: 22rpx;
  background: #eadbcc;
  display: block;
}

.tutorial-image {
  height: 260rpx;
}

.tutorial-image.alt {
  height: 318rpx;
}

.material-image {
  height: 220rpx;
}

.material-image.alt {
  height: 286rpx;
}

.card-badge {
  position: absolute;
  top: 30rpx;
  left: 30rpx;
  padding: 8rpx 18rpx;
  border-radius: 999rpx;
  background: rgba(41, 32, 26, 0.7);
  color: #fff8f0;
  font-size: 22rpx;
}

.card-title {
  margin-top: 16rpx;
  font-size: 30rpx;
  font-weight: 700;
  line-height: 1.45;
}

.material-top {
  margin-top: 16rpx;
}

.part-chip {
  display: inline-flex;
  padding: 8rpx 18rpx;
  border-radius: 999rpx;
  background: #f1dfca;
  color: #87552f;
  font-size: 22rpx;
}

.material-name {
  margin-top: 12rpx;
  font-size: 30rpx;
  font-weight: 700;
  line-height: 1.45;
}

.card-desc {
  margin-top: 12rpx;
  color: #715f51;
  line-height: 1.7;
  font-size: 24rpx;
}

.card-footer {
  margin-top: 18rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.footer-tag {
  padding: 8rpx 16rpx;
  border-radius: 999rpx;
  background: #f6e9d9;
  color: #9c673c;
  font-size: 22rpx;
}

.footer-link {
  color: #b97037;
  font-size: 24rpx;
  font-weight: 700;
}

.empty-tip {
  padding: 34rpx 0 18rpx;
  color: #8d7662;
  text-align: center;
  font-size: 26rpx;
}
</style>
