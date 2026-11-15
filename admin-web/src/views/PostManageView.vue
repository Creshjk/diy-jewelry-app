<template>
  <div class="page-card list-page">
    <div class="page-header">
      <div>
        <h2 class="page-title">作品管理</h2>
        <div class="page-desc">管理广场内的公开作品和点赞热度。</div>
      </div>
    </div>
    <el-table :data="posts" stripe class="list-table">
      <el-table-column label="封面" width="120">
        <template #default="{ row }">
          <el-image :src="row.coverImageUrl" class="table-thumb" fit="cover" />
        </template>
      </el-table-column>
      <el-table-column prop="title" label="标题" width="200" />
      <el-table-column prop="nickname" label="发布者" width="120" />
      <el-table-column prop="description" label="描述" min-width="240" />
      <el-table-column prop="likeCount" label="点赞数" width="100" />
      <el-table-column prop="createdAt" label="发布时间" width="180">
        <template #default="{ row }">{{ formatDateTime(row.createdAt) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="170">
        <template #default="{ row }">
          <el-button type="primary" link @click="openDetail(row.id)">详情</el-button>
          <el-button type="danger" link @click="removePost(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div class="table-pagination">
      <el-pagination
        background
        layout="total, sizes, prev, pager, next"
        :current-page="query.current"
        :page-size="query.size"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        @current-change="changePage"
        @size-change="changeSize"
      />
    </div>

    <el-dialog v-model="detailVisible" title="作品详情" width="1120px" top="5vh" class="detail-dialog">
      <div v-loading="detailLoading" class="detail-wrap" v-if="detail">
        <div class="detail-media">
          <el-image :src="detail.coverImageUrl || detail.resultImageUrl" fit="cover" class="detail-image" :preview-src-list="[detail.coverImageUrl || detail.resultImageUrl]" />
          <div class="detail-side">
            <h3>{{ detail.title }}</h3>
            <p>{{ detail.description || '暂无作品描述' }}</p>
            <div class="detail-tags">
              <el-tag>{{ detail.likeCount || 0 }} 赞</el-tag>
              <el-tag type="warning">{{ detail.designMode || '未知模式' }}</el-tag>
              <el-tag type="info">{{ detail.aspectRatio || '未知比例' }}</el-tag>
            </div>
          </div>
        </div>

        <el-descriptions :column="2" border class="detail-desc">
          <el-descriptions-item label="发布者">{{ detail.publisherName }}</el-descriptions-item>
          <el-descriptions-item label="账号">{{ detail.publisherUsername || '-' }}</el-descriptions-item>
          <el-descriptions-item label="发布时间">{{ formatDateTime(detail.createdAt) }}</el-descriptions-item>
          <el-descriptions-item label="作品ID">{{ detail.id }}</el-descriptions-item>
          <el-descriptions-item label="设计记录ID">{{ detail.designRecordId || '-' }}</el-descriptions-item>
          <el-descriptions-item label="清晰度">{{ detail.clarityLevel || '-' }}</el-descriptions-item>
          <el-descriptions-item label="主题关键词">{{ detail.prompt || '-' }}</el-descriptions-item>
          <el-descriptions-item label="风格标签">{{ detail.styleTags || '-' }}</el-descriptions-item>
          <el-descriptions-item label="AI设计说明" :span="2">{{ detail.aiSummary || '-' }}</el-descriptions-item>
          <el-descriptions-item label="参考图" :span="2">
            <el-link v-if="detail.referenceImageUrl" :href="detail.referenceImageUrl" target="_blank" type="primary">查看参考图</el-link>
            <span v-else>-</span>
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ElMessage } from 'element-plus'
import { onMounted, ref } from 'vue'
import request from '../api/http'
import { formatDateTime } from '../utils/date'

const posts = ref([])
const total = ref(0)
const detailVisible = ref(false)
const detailLoading = ref(false)
const detail = ref(null)
const query = ref({
  current: 1,
  size: 10
})

async function loadPosts() {
  const data = await request.get('/admin/posts', { params: query.value })
  posts.value = data.records
  total.value = data.total
}

async function openDetail(id) {
  detailVisible.value = true
  detailLoading.value = true
  try {
    detail.value = await request.get(`/admin/posts/${id}`)
  } finally {
    detailLoading.value = false
  }
}

async function removePost(id) {
  await request.delete(`/discovery/posts/${id}`)
  ElMessage.success('作品已删除')
  loadPosts()
}

function changePage(page) {
  query.value.current = page
  loadPosts()
}

function changeSize(size) {
  query.value.size = size
  query.value.current = 1
  loadPosts()
}

onMounted(loadPosts)
</script>

<style scoped>
.detail-wrap {
  display: grid;
  gap: 18px;
  max-height: 74vh;
  overflow: auto;
  padding-right: 8px;
}

.detail-media {
  display: grid;
  grid-template-columns: 420px minmax(0, 1fr);
  gap: 22px;
  align-items: start;
}

.detail-image {
  width: 100%;
  height: 420px;
  border-radius: 22px;
}

.detail-side h3 {
  margin: 0 0 12px;
  font-size: 28px;
}

.detail-side p {
  margin: 0;
  color: #6e6257;
  line-height: 1.7;
  font-size: 15px;
}

.detail-tags {
  margin-top: 16px;
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.detail-desc {
  margin-top: 8px;
}

:deep(.detail-dialog .el-dialog) {
  border-radius: 26px;
  overflow: hidden;
}

:deep(.detail-dialog .el-dialog__header) {
  padding: 24px 28px 8px;
}

:deep(.detail-dialog .el-dialog__body) {
  padding: 12px 28px 28px;
}

@media (max-width: 900px) {
  .detail-media {
    grid-template-columns: 1fr;
  }
}
</style>
