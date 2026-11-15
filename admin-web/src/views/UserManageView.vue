<template>
  <div class="page-card list-page">
    <div class="page-header">
      <div>
        <h2 class="page-title">用户管理</h2>
        <div class="page-desc">支持新增和删除用户，保留毕业设计需要的明文密码演示字段。</div>
      </div>
      <el-button type="warning" @click="dialogVisible = true">新增用户</el-button>
    </div>

    <el-table :data="users" stripe class="list-table">
      <el-table-column prop="username" label="账号" width="140" />
      <el-table-column prop="password" label="密码" width="120" />
      <el-table-column prop="nickname" label="昵称" width="140" />
      <el-table-column prop="phone" label="手机号" width="140" />
      <el-table-column prop="gender" label="性别" width="80" />
      <el-table-column prop="createdAt" label="创建时间" width="180">
        <template #default="{ row }">{{ formatDateTime(row.createdAt) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="100">
        <template #default="{ row }">
          <el-button type="danger" link @click="removeUser(row.id)">删除</el-button>
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

    <el-dialog v-model="dialogVisible" title="新增用户" width="480px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="账号"><el-input v-model="form.username" /></el-form-item>
        <el-form-item label="密码"><el-input v-model="form.password" /></el-form-item>
        <el-form-item label="昵称"><el-input v-model="form.nickname" /></el-form-item>
        <el-form-item label="手机"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item label="性别">
          <el-select v-model="form.gender" style="width: 100%">
            <el-option label="女" value="女" />
            <el-option label="男" value="男" />
            <el-option label="保密" value="保密" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="warning" @click="submitUser">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ElMessage } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'
import request from '../api/http'
import { formatDateTime } from '../utils/date'

const users = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const query = reactive({
  current: 1,
  size: 10
})
const form = reactive({
  username: '',
  password: '',
  nickname: '',
  phone: '',
  gender: '保密'
})

function resetForm() {
  form.username = ''
  form.password = ''
  form.nickname = ''
  form.phone = ''
  form.gender = '保密'
}

async function loadUsers() {
  const data = await request.get('/admin/users', { params: query })
  users.value = data.records
  total.value = data.total
}

async function submitUser() {
  await request.post('/admin/users', form)
  ElMessage.success('用户已创建')
  dialogVisible.value = false
  resetForm()
  loadUsers()
}

function changePage(page) {
  query.current = page
  loadUsers()
}

function changeSize(size) {
  query.size = size
  query.current = 1
  loadUsers()
}

async function removeUser(id) {
  await request.delete(`/admin/users/${id}`)
  ElMessage.success('用户已删除')
  loadUsers()
}

onMounted(loadUsers)
</script>
