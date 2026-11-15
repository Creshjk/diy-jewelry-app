<template>
  <section class="page-card content-card">
    <div class="page-header">
      <div>
        <h2 class="page-title">材料推荐</h2>
        <div class="page-desc">按佩戴部位维护材料建议，供 app 端材料推荐模块展示。</div>
      </div>
      <el-button type="warning" @click="openCreateDialog">新增材料</el-button>
    </div>

    <el-table :data="pagedMaterials" stripe class="list-table">
      <el-table-column label="封面" width="110">
        <template #default="{ row }">
          <el-image v-if="row.coverImageUrl" :src="resolveImageUrl(row.coverImageUrl)" class="table-thumb" fit="cover" />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column prop="bodyPart" label="部位" width="100" />
      <el-table-column prop="materialName" label="材料" width="140" />
      <el-table-column prop="description" label="说明" min-width="220" show-overflow-tooltip />
      <el-table-column prop="createdAt" label="创建时间" width="170">
        <template #default="{ row }">{{ formatDateTime(row.createdAt) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="140">
        <template #default="{ row }">
          <el-button type="primary" link @click="openEditDialog(row)">编辑</el-button>
          <el-button type="danger" link @click="removeMaterial(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="table-pagination">
      <el-pagination
        background
        layout="total, sizes, prev, pager, next"
        :current-page="query.current"
        :page-size="query.size"
        :total="materials.length"
        :page-sizes="[5, 10, 20, 50]"
        @current-change="changePage"
        @size-change="changeSize"
      />
    </div>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="720px">
      <el-form :model="materialForm" label-width="84px" class="dialog-form">
        <el-form-item label="部位">
          <el-input v-model="materialForm.bodyPart" placeholder="例如：脖子、手腕、头饰" />
        </el-form-item>
        <el-form-item label="材料">
          <el-input v-model="materialForm.materialName" placeholder="请输入材料名称" />
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
              v-if="materialForm.coverImageUrl"
              :src="resolveImageUrl(materialForm.coverImageUrl)"
              class="upload-preview"
              fit="cover"
            />
          </div>
        </el-form-item>
        <el-form-item label="链接">
          <el-input v-model="materialForm.linkUrl" placeholder="请输入跳转链接" />
        </el-form-item>
        <el-form-item label="说明">
          <el-input v-model="materialForm.description" type="textarea" :rows="5" placeholder="请输入材料说明" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="warning" @click="saveMaterial">保存材料</el-button>
      </template>
    </el-dialog>
  </section>
</template>

<script setup>
import { ElMessage } from 'element-plus'
import { computed, onMounted, reactive, ref } from 'vue'
import request from '../api/http'
import { formatDateTime } from '../utils/date'

const materials = ref([])
const dialogVisible = ref(false)
const backendOrigin = request.defaults.baseURL.replace(/\/api$/, '')
const dialogTitle = computed(() => (materialForm.id ? '编辑材料' : '新增材料'))

const query = reactive({
  current: 1,
  size: 5
})

const materialForm = reactive({
  id: null,
  bodyPart: '',
  materialName: '',
  coverImageUrl: '',
  linkUrl: '',
  description: '',
  status: 1
})

const pagedMaterials = computed(() => {
  const start = (query.current - 1) * query.size
  return materials.value.slice(start, start + query.size)
})

function resetMaterialForm() {
  materialForm.id = null
  materialForm.bodyPart = ''
  materialForm.materialName = ''
  materialForm.coverImageUrl = ''
  materialForm.linkUrl = ''
  materialForm.description = ''
}

function openCreateDialog() {
  resetMaterialForm()
  dialogVisible.value = true
}

function openEditDialog(row) {
  materialForm.id = row.id
  materialForm.bodyPart = row.bodyPart || ''
  materialForm.materialName = row.materialName || ''
  materialForm.coverImageUrl = row.coverImageUrl || ''
  materialForm.linkUrl = row.linkUrl || ''
  materialForm.description = row.description || ''
  materialForm.status = row.status ?? 1
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
  materialForm.coverImageUrl = data.url
  ElMessage.success('封面图上传成功')
}

function normalizePage() {
  const maxPage = Math.max(1, Math.ceil(materials.value.length / query.size))
  if (query.current > maxPage) {
    query.current = maxPage
  }
}

async function loadMaterials() {
  materials.value = await request.get('/content/materials')
  normalizePage()
}

async function saveMaterial() {
  await request.post('/content/materials', materialForm)
  ElMessage.success(materialForm.id ? '材料已更新' : '材料已保存')
  resetMaterialForm()
  dialogVisible.value = false
  loadMaterials()
}

async function removeMaterial(id) {
  await request.delete(`/content/materials/${id}`)
  ElMessage.success('材料已删除')
  loadMaterials()
}

function changePage(page) {
  query.current = page
}

function changeSize(size) {
  query.size = size
  query.current = 1
}

onMounted(loadMaterials)
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
