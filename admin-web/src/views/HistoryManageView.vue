<template>
  <div class="page-card list-page">
    <div class="page-header">
      <div>
        <h2 class="page-title">AI 对话历史</h2>
        <div class="page-desc">查看用户的首饰设计关键词、模式和生成结果。</div>
      </div>
    </div>
    <el-table :data="records" stripe class="list-table">
      <el-table-column prop="nickname" label="用户" width="120" />
      <el-table-column prop="prompt" label="关键词" min-width="220" />
      <el-table-column prop="designMode" label="模式" width="150" />
      <el-table-column prop="clarityLevel" label="清晰度" width="90" />
      <el-table-column prop="aspectRatio" label="比例" width="90" />
      <el-table-column label="结果图" width="100">
        <template #default="{ row }">
          <el-image :src="row.resultImageUrl" class="table-thumb" fit="cover" />
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="时间" width="180">
        <template #default="{ row }">{{ formatDateTime(row.createdAt) }}</template>
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
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import request from '../api/http'
import { formatDateTime } from '../utils/date'

const records = ref([])
const total = ref(0)
const query = reactive({
  current: 1,
  size: 10
})

async function loadRecords() {
  const data = await request.get('/admin/design-records', { params: query })
  records.value = data.records
  total.value = data.total
}

function changePage(page) {
  query.current = page
  loadRecords()
}

function changeSize(size) {
  query.size = size
  query.current = 1
  loadRecords()
}

onMounted(loadRecords)
</script>
