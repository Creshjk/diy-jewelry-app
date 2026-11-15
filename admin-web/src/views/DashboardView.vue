<template>
  <div class="dashboard">
    <section class="stats-grid">
      <article v-for="item in statCards" :key="item.label" class="page-card stat-card">
        <div class="stat-label">{{ item.label }}</div>
        <div class="stat-value">{{ item.value }}</div>
        <div class="stat-tip">{{ item.tip }}</div>
      </article>
    </section>

    <section class="double-grid">
      <div class="page-card">
        <div class="page-header">
          <div>
            <h2 class="page-title">最新设计记录</h2>
            <div class="page-desc">用户近期提交的 AI 首饰设计请求</div>
          </div>
        </div>
        <el-table :data="recentDesigns.records" stripe>
          <el-table-column prop="nickname" label="用户" width="120" />
          <el-table-column prop="prompt" label="设计主题" />
          <el-table-column prop="createdAt" label="时间" width="180">
            <template #default="{ row }">{{ formatDateTime(row.createdAt) }}</template>
          </el-table-column>
        </el-table>
        <div class="table-pagination">
          <el-pagination
            background
            layout="total, sizes, prev, pager, next"
            :current-page="recentQuery.current"
            :page-size="recentQuery.size"
            :total="recentDesigns.total"
            :page-sizes="[5, 10, 20, 50]"
            @current-change="changeRecentPage"
            @size-change="changeRecentSize"
          />
        </div>
      </div>

      <div class="page-card">
        <div class="page-header">
          <div>
            <h2 class="page-title">热门作品</h2>
            <div class="page-desc">点赞数最高的社区作品</div>
          </div>
        </div>
        <el-table :data="hotPosts.records" stripe>
          <el-table-column prop="title" label="作品标题" />
          <el-table-column prop="likeCount" label="点赞数" width="100" />
          <el-table-column prop="createdAt" label="发布时间" width="180">
            <template #default="{ row }">{{ formatDateTime(row.createdAt) }}</template>
          </el-table-column>
        </el-table>
        <div class="table-pagination">
          <el-pagination
            background
            layout="total, sizes, prev, pager, next"
            :current-page="hotQuery.current"
            :page-size="hotQuery.size"
            :total="hotPosts.total"
            :page-sizes="[5, 10, 20, 50]"
            @current-change="changeHotPage"
            @size-change="changeHotSize"
          />
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import request from '../api/http'
import { formatDateTime } from '../utils/date'

const dashboard = ref({})
const recentDesigns = ref({ records: [], total: 0 })
const hotPosts = ref({ records: [], total: 0 })
const recentQuery = ref({ current: 1, size: 5 })
const hotQuery = ref({ current: 1, size: 5 })

const statCards = computed(() => [
  { label: '注册用户', value: dashboard.value.userCount || 0, tip: '当前平台累计注册人数' },
  { label: 'AI 生成次数', value: dashboard.value.designCount || 0, tip: '文生图与图生图总请求数' },
  { label: '广场作品', value: dashboard.value.postCount || 0, tip: '已发布到发现页的作品数' },
  { label: '点赞总量', value: dashboard.value.likeCount || 0, tip: '社区互动热度' }
])

async function loadDashboard() {
  dashboard.value = await request.get('/admin/dashboard')
}

async function loadRecentDesigns() {
  recentDesigns.value = await request.get('/admin/dashboard/recent-designs', { params: recentQuery.value })
}

async function loadHotPosts() {
  hotPosts.value = await request.get('/admin/dashboard/hot-posts', { params: hotQuery.value })
}

function changeRecentPage(page) {
  recentQuery.value.current = page
  loadRecentDesigns()
}

function changeRecentSize(size) {
  recentQuery.value.size = size
  recentQuery.value.current = 1
  loadRecentDesigns()
}

function changeHotPage(page) {
  hotQuery.value.current = page
  loadHotPosts()
}

function changeHotSize(size) {
  hotQuery.value.size = size
  hotQuery.value.current = 1
  loadHotPosts()
}

onMounted(() => {
  loadDashboard()
  loadRecentDesigns()
  loadHotPosts()
})
</script>

<style scoped>
.dashboard {
  display: grid;
  gap: 20px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 18px;
}

.stat-card {
  background:
    radial-gradient(circle at top right, rgba(255, 226, 193, 0.6), transparent 35%),
    rgba(255, 255, 255, 0.76);
}

.stat-label {
  color: #8d755f;
  font-size: 14px;
}

.stat-value {
  margin-top: 14px;
  font-size: 40px;
  font-weight: 800;
}

.stat-tip {
  margin-top: 10px;
  color: #7b6a5e;
}

.double-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 20px;
}

@media (max-width: 1100px) {
  .stats-grid,
  .double-grid {
    grid-template-columns: 1fr;
  }
}
</style>
