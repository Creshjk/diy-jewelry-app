<template>
  <section class="page-card content-card">
    <div class="page-header">
      <div>
        <h2 class="page-title">教程管理</h2>
        <div class="page-desc">维护图文教程、摘要和外链地址，供 app 端教程材料页展示。</div>
      </div>
      <el-button type="warning" @click="openCreateDialog">新增教程</el-button>
    </div>

    <el-table :data="pagedTutorials" stripe class="list-table">
      <el-table-column label="封面" width="110">
        <template #default="{ row }">
          <el-image v-if="row.coverImageUrl" :src="resolveImageUrl(row.coverImageUrl)" class="table-thumb" fit="cover" />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column prop="title" label="标题" min-width="180" />
      <el-table-column prop="summary" label="摘要" min-width="220" show-overflow-tooltip />
      <el-table-column prop="createdAt" label="创建时间" width="170">
        <template #default="{ row }">{{ formatDateTime(row.createdAt) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="140">
        <template #default="{ row }">
          <el-button type="primary" link @click="openEditDialog(row)">编辑</el-button>
          <el-button type="danger" link @click="removeTutorial(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="table-pagination">
      <el-pagination
        background
        layout="total, sizes, prev, pager, next"
        :current-page="query.current"
        :page-size="query.size"
        :total="tutorials.length"
        :page-sizes="[5, 10, 20, 50]"
        @current-change="changePage"
        @size-change="changeSize"
      />
    </div>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="720px">
      <el-form :model="tutorialForm" label-width="84px" class="dialog-form">
        <el-form-item label="标题">
          <el-input v-model="tutorialForm.title" placeholder="请输入教程标题" />
        </el-form-item>
        <el-form-item label="封面图">
          <div class="upload-block">
            <el-upload
              action="#"
              :auto-upload="false"
              :show-file-list="false"
              accept="image/*"
              :on-change="handleCoverChange"
            >
              <el-button type="primary" plain>选择图片</el-button>
            </el-upload>
            <div class="upload-tip">支持上传 jpg、png 等图片，上传后自动作为封面图保存。</div>
            <el-image
              v-if="tutorialForm.coverImageUrl"
              :src="resolveImageUrl(tutorialForm.coverImageUrl)"
              class="upload-preview"
              fit="cover"
            />
          </div>
        </el-form-item>
        <el-form-item label="摘要">
          <el-input v-model="tutorialForm.summary" placeholder="请输入摘要" />
        </el-form-item>
        <el-form-item label="链接">
          <el-input v-model="tutorialForm.linkUrl" placeholder="请输入跳转链接" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="tutorialForm.content" type="textarea" :rows="5" placeholder="请输入教程正文" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="warning" @click="saveTutorial">保存教程</el-button>
      </template>
    </el-dialog>
  </section>
</template>

<script setup>
import { ElMessage } from 'element-plus'
import { computed, onMounted, reactive, ref } from 'vue'
import request from '../api/http'
import { formatDateTime } from '../utils/date'

const tutorials = ref([])
const dialogVisible = ref(false)
const backendOrigin = request.defaults.baseURL.replace(/\/api$/, '')
const dialogTitle = computed(() => (tutorialForm.id ? '编辑教程' : '新增教程'))

const query = reactive({
  current: 1,
  size: 5
})

const tutorialForm = reactive({
  id: null,
  title: '',
  coverImageUrl: '',
  summary: '',
  linkUrl: '',
  content: '',
  status: 1
})

const pagedTutorials = computed(() => {
  const start = (query.current - 1) * query.size
  return tutorials.value.slice(start, start + query.size)
})

function resetTutorialForm() {
  tutorialForm.id = null
  tutorialForm.title = ''
  tutorialForm.coverImageUrl = ''
  tutorialForm.summary = ''
  tutorialForm.linkUrl = ''
  tutorialForm.content = ''
}

function openCreateDialog() {
  resetTutorialForm()
  dialogVisible.value = true
}

function openEditDialog(row) {
  tutorialForm.id = row.id
  tutorialForm.title = row.title || ''
  tutorialForm.coverImageUrl = row.coverImageUrl || ''
  tutorialForm.summary = row.summary || ''
  tutorialForm.linkUrl = row.linkUrl || ''
  tutorialForm.content = row.content || ''
  tutorialForm.status = row.status ?? 1
  dialogVisible.value = true
}

function resolveImageUrl(url) {
  if (!url) {
    return ''
  }
  if (/^https?:\/\//.test(url)) {
    return url
  }
  return `${backendOrigin}${url}`
}

async function handleCoverChange(uploadFile) {
  if (!uploadFile.raw) {
    return
  }
  const formData = new FormData()
  formData.append('file', uploadFile.raw)
  const data = await request.post('/files/upload-image', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
  tutorialForm.coverImageUrl = data.url
  ElMessage.success('封面图上传成功')
}

function normalizePage() {
  const maxPage = Math.max(1, Math.ceil(tutorials.value.length / query.size))
  if (query.current > maxPage) {
    query.current = maxPage
  }
}

async function loadTutorials() {
  tutorials.value = await request.get('/content/tutorials')
  normalizePage()
}

async function saveTutorial() {
  await request.post('/content/tutorials', tutorialForm)
  ElMessage.success(tutorialForm.id ? '教程已更新' : '教程已保存')
  resetTutorialForm()
  dialogVisible.value = false
  loadTutorials()
}

async function removeTutorial(id) {
  await request.delete(`/content/tutorials/${id}`)
  ElMessage.success('教程已删除')
  loadTutorials()
}

function changePage(page) {
  query.current = page
}

function changeSize(size) {
  query.size = size
  query.current = 1
}

onMounted(loadTutorials)
</script>

<style scoped>
.content-card {
  min-height: calc(100vh - 210px);
  display: flex;
  flex-direction: column;
}

.dialog-form {
  padding-right: 12px;
}

.upload-block {
  display: grid;
  gap: 12px;
}

.upload-tip {
  color: #8c735f;
  font-size: 13px;
}

.upload-preview {
  width: 180px;
  height: 180px;
  border-radius: 18px;
  border: 1px solid rgba(218, 190, 162, 0.8);
}
</style>
