<template>
    <view class="shell">
      <view class="hero-card">
        <view class="hero-title">一句关键词，生成你的下一款首饰灵感</view>
      </view>

    <view class="panel">
      <view class="section-title">设计输入</view>
      <input v-model="form.prompt" class="uni-input" placeholder="输入首饰关键词，例如：国风花丝耳坠" />
      <view class="chip-row" style="margin-top: 20rpx">
        <view
          v-for="item in recommendations"
          :key="item.id"
          class="chip"
          @click="usePrompt(item.promptText)"
        >
          {{ item.promptText }}
        </view>
      </view>

      <view class="form-row">
        <picker range-key="label" :range="modeOptions" @change="onModeChange">
          <view class="picker-item">模式：{{ currentModeLabel }}</view>
        </picker>
        <picker :range="clarityOptions" @change="onClarityChange">
          <view class="picker-item">清晰度：{{ form.clarityLevel }}</view>
        </picker>
      </view>

      <view class="form-row">
        <picker :range="ratioOptions" @change="onRatioChange">
          <view class="picker-item">比例：{{ form.aspectRatio }}</view>
        </picker>
        <input v-model="form.styleTags" class="uni-input" placeholder="风格标签，如：轻奢,珍珠,流苏" />
      </view>

      <input v-model="form.referenceImageUrl" class="uni-input" placeholder="参考图 URL，可为空" />
      <button class="primary-btn" :disabled="isGenerating" :loading="isGenerating" @click="generate">
        {{ isGenerating ? '生成中...' : '立即生成' }}
      </button>
      <view v-if="isGenerating" class="loading-tip">
        正在调用 AI 生成首饰设计图，请稍候...
      </view>
    </view>

    <view v-if="result.id" class="panel">
      <view class="section-title">生成结果</view>
      <image :src="result.resultImageUrl" mode="aspectFill" class="result-image" @click="previewResultImage" />
      <view class="preview-tip">点击图片可查看大图</view>
      <view class="result-summary">{{ result.aiSummary }}</view>
      <view class="chip-row">
        <view class="chip">{{ formatModeLabel(result.designMode) }}</view>
        <view class="chip">{{ result.aspectRatio }}</view>
        <view class="chip">{{ result.clarityLevel }}</view>
      </view>
    </view>
  </view>
</template>

<script>
import { requireLogin } from '../../common/guard'
import { request } from '../../common/http'
import { clearDesignDraft, getCurrentUser, getDesignDraft } from '../../common/store'

export default {
  data() {
    return {
      recommendations: [],
      result: {},
      modeOptions: [
        { label: '文生图', value: 'TEXT_TO_IMAGE' },
        { label: '图生图', value: 'IMAGE_TO_IMAGE' }
      ],
      clarityOptions: ['高清', '超清', '柔焦'],
      ratioOptions: ['1:1', '3:4', '9:16'],
      isGenerating: false,
      form: {
        userId: 1,
        prompt: '',
        styleTags: '',
        clarityLevel: '高清',
        aspectRatio: '1:1',
        designMode: 'TEXT_TO_IMAGE',
        referenceImageUrl: ''
      }
    }
  },
  computed: {
    currentModeLabel() {
      return this.formatModeLabel(this.form.designMode)
    }
  },
  onShow() {
    if (!requireLogin('/pages/design/index')) {
      return
    }
    this.syncUser()
    this.restoreDraft()
    this.loadRecommendations()
  },
  methods: {
    async loadRecommendations() {
      this.recommendations = await request('/design/recommendations')
    },
    syncUser() {
      const user = getCurrentUser()
      this.form.userId = user && user.id ? user.id : 1
    },
    restoreDraft() {
      const draft = getDesignDraft()
      if (!draft) return
      this.form.prompt = draft.prompt || ''
      this.form.styleTags = draft.styleTags || ''
      this.form.clarityLevel = draft.clarityLevel || '高清'
      this.form.aspectRatio = draft.aspectRatio || '1:1'
      this.form.designMode = draft.designMode || 'TEXT_TO_IMAGE'
      this.form.referenceImageUrl = draft.referenceImageUrl || ''
      this.result = draft
      clearDesignDraft()
    },
    usePrompt(text) {
      this.form.prompt = text
    },
    formatModeLabel(value) {
      const item = this.modeOptions.find(option => option.value === value)
      return item ? item.label : '文生图'
    },
    onModeChange(event) {
      this.form.designMode = this.modeOptions[event.detail.value].value
    },
    onClarityChange(event) {
      this.form.clarityLevel = this.clarityOptions[event.detail.value]
    },
    onRatioChange(event) {
      this.form.aspectRatio = this.ratioOptions[event.detail.value]
    },
    previewResultImage() {
      if (!this.result.resultImageUrl) {
        return
      }
      uni.previewImage({
        urls: [this.result.resultImageUrl],
        current: this.result.resultImageUrl
      })
    },
    async generate() {
      if (this.isGenerating) {
        return
      }
      if (!this.form.prompt) {
        uni.showToast({ title: '请先输入关键词', icon: 'none' })
        return
      }
      this.isGenerating = true
      uni.showLoading({
        title: '生成中',
        mask: true
      })
      try {
        this.result = await request('/design/generate', {
          method: 'POST',
          data: this.form
        })
        uni.showToast({ title: '生成完成', icon: 'success' })
      } finally {
        this.isGenerating = false
        uni.hideLoading()
      }
    }
  }
}
</script>

<style scoped>
.uni-input,
.picker-item {
  margin-top: 18rpx;
  background: #f8f2eb;
  border-radius: 18rpx;
  padding: 22rpx;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16rpx;
  margin-top: 10rpx;
}

.result-image {
  width: 100%;
  height: 540rpx;
  border-radius: 24rpx;
  margin-bottom: 18rpx;
}

.result-summary {
  color: #675748;
  line-height: 1.7;
  margin-bottom: 18rpx;
}

.loading-tip {
  margin-top: 18rpx;
  color: #8f6137;
  font-size: 24rpx;
  text-align: center;
}

.preview-tip {
  margin-bottom: 14rpx;
  color: #9a6d43;
  font-size: 24rpx;
  text-align: center;
}
</style>
