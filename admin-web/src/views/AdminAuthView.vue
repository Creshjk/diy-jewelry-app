<template>
  <div class="auth-page">
    <div class="auth-hero">
      <div class="auth-badge">DIY饰界后台入口</div>
      <h1>管理端登录与注册</h1>
      <p>登录后查看用户、AI 历史、作品与教程材料。注册页用于快速创建新的管理员演示账号。</p>
    </div>

    <div class="auth-panel">
      <el-tabs v-model="activeTab" stretch>
        <el-tab-pane label="管理员登录" name="login">
          <el-form :model="loginForm" label-position="top">
            <el-form-item label="账号">
              <el-input v-model="loginForm.username" placeholder="请输入管理员账号" />
            </el-form-item>
            <el-form-item label="密码">
              <el-input v-model="loginForm.password" type="password" show-password placeholder="请输入密码" />
            </el-form-item>
            <el-button type="warning" class="submit-btn" @click="handleLogin">进入后台</el-button>
            <div class="hint">默认演示账号：`admin / admin123`</div>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="管理员注册" name="register">
          <el-form :model="registerForm" label-position="top">
            <el-form-item label="显示名称">
              <el-input v-model="registerForm.displayName" placeholder="例如：饰界管理员" />
            </el-form-item>
            <el-form-item label="账号">
              <el-input v-model="registerForm.username" placeholder="请输入新管理员账号" />
            </el-form-item>
            <el-form-item label="密码">
              <el-input v-model="registerForm.password" type="password" show-password placeholder="请输入密码" />
            </el-form-item>
            <el-button type="warning" class="submit-btn" @click="handleRegister">创建管理员</el-button>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script setup>
import { ElMessage } from 'element-plus'
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import request from '../api/http'
import { setAdminUser } from '../utils/auth'

const router = useRouter()
const activeTab = ref('login')

const loginForm = reactive({
  username: 'admin',
  password: 'admin123'
})

const registerForm = reactive({
  displayName: '',
  username: '',
  password: ''
})

async function handleLogin() {
  const user = await request.post('/auth/login', {
    ...loginForm,
    userType: 'admin'
  })
  setAdminUser(user)
  ElMessage.success('登录成功')
  router.push('/dashboard')
}

async function handleRegister() {
  await request.post('/auth/admin/register', registerForm)
  ElMessage.success('管理员创建成功，请登录')
  activeTab.value = 'login'
  loginForm.username = registerForm.username
  loginForm.password = registerForm.password
  registerForm.displayName = ''
  registerForm.username = ''
  registerForm.password = ''
}
</script>

<style scoped>
.auth-page {
  min-height: 100vh;
  display: grid;
  grid-template-columns: 1.1fr 0.9fr;
  background:
    radial-gradient(circle at top left, rgba(255, 228, 191, 0.75), transparent 26%),
    linear-gradient(135deg, #f7efe4 0%, #ecf1f8 48%, #ecdfd2 100%);
}

.auth-hero {
  padding: 72px 64px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  color: #2f241b;
}

.auth-badge {
  display: inline-flex;
  width: fit-content;
  padding: 8px 14px;
  border-radius: 999px;
  background: rgba(182, 112, 56, 0.14);
  color: #9d5c26;
  font-size: 13px;
  letter-spacing: 0.08em;
}

.auth-hero h1 {
  margin: 18px 0 14px;
  font-size: 52px;
  line-height: 1.12;
}

.auth-hero p {
  max-width: 520px;
  color: #706255;
  font-size: 17px;
  line-height: 1.8;
}

.auth-panel {
  margin: auto 48px auto 0;
  padding: 28px;
  border-radius: 32px;
  background: rgba(255, 255, 255, 0.84);
  backdrop-filter: blur(18px);
  box-shadow: 0 22px 56px rgba(87, 67, 49, 0.14);
}

.submit-btn {
  width: 100%;
  margin-top: 10px;
}

.hint {
  margin-top: 14px;
  color: #7d6c5f;
  font-size: 13px;
}

@media (max-width: 980px) {
  .auth-page {
    grid-template-columns: 1fr;
  }

  .auth-hero {
    padding: 48px 24px 24px;
  }

  .auth-panel {
    margin: 0 24px 24px;
  }
}
</style>
