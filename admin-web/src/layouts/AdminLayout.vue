<template>
  <div class="layout-shell">
    <aside class="sidebar">
      <div class="brand">
        <div class="brand-mark">DI</div>
        <div>
          <div class="brand-title">DIY饰界</div>
          <div class="brand-sub">首饰设计后台</div>
        </div>
      </div>
      <el-menu :default-active="route.path" router class="menu">
        <el-menu-item index="/dashboard">数据看板</el-menu-item>
        <el-menu-item index="/users">用户管理</el-menu-item>
        <el-menu-item index="/history">对话历史</el-menu-item>
        <el-menu-item index="/posts">作品管理</el-menu-item>
        <el-menu-item index="/tutorials">教程管理</el-menu-item>
        <el-menu-item index="/materials">材料推荐</el-menu-item>
      </el-menu>
    </aside>
    <main class="content">
      <div class="hero">
      <div>
        <h1>DIY饰界首饰设计平台综合管理系统</h1>
      </div>
      <div class="hero-user" v-if="adminUser">
        <div class="hero-user-name">{{ adminUser.displayName }}</div>
        <div class="hero-user-role">{{ adminUser.role }}</div>
        <el-button size="small" plain @click="logout">退出</el-button>
      </div>
    </div>
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { useRoute } from 'vue-router'
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { clearAdminUser, getAdminUser } from '../utils/auth'

const route = useRoute()
const router = useRouter()
const adminUser = computed(() => getAdminUser())

function logout() {
  clearAdminUser()
  router.push('/login')
}
</script>

<style scoped>
.layout-shell {
  min-height: 100vh;
  display: grid;
  grid-template-columns: 260px 1fr;
}

.sidebar {
  padding: 28px 20px;
  background: linear-gradient(180deg, rgba(48, 33, 20, 0.92), rgba(94, 69, 53, 0.88));
  color: #fff7f0;
}

.brand {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 28px;
}

.brand-mark {
  width: 48px;
  height: 48px;
  border-radius: 16px;
  display: grid;
  place-items: center;
  background: linear-gradient(135deg, #f7cf9e, #d08438);
  color: #3f250d;
  font-weight: 800;
}

.brand-title {
  font-size: 18px;
  font-weight: 700;
}

.brand-sub {
  font-size: 13px;
  opacity: 0.72;
}

.menu {
  border-right: none;
  background: transparent;
}

.menu :deep(.el-menu-item) {
  color: rgba(255, 247, 240, 0.88);
  border-radius: 14px;
  margin-bottom: 8px;
}

.menu :deep(.el-menu-item.is-active) {
  color: #40210d;
  background: linear-gradient(135deg, #f3d9bb, #d89b5c);
}

.content {
  padding: 28px;
}

.hero {
  margin-bottom: 20px;
  padding: 26px 28px;
  border-radius: 28px;
  background:
    radial-gradient(circle at top right, rgba(255, 237, 214, 0.88), transparent 35%),
    linear-gradient(135deg, rgba(113, 79, 46, 0.92), rgba(28, 41, 58, 0.92));
  color: #fff;
  box-shadow: 0 20px 48px rgba(46, 33, 23, 0.18);
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 20px;
}

.hero-label {
  display: inline-flex;
  padding: 6px 12px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.14);
  font-size: 12px;
  letter-spacing: 0.08em;
}

.hero h1 {
  margin: 12px 0 0;
  font-size: 30px;
  max-width: 700px;
  line-height: 1.3;
}

.hero-user {
  text-align: right;
}

.hero-user-name {
  font-size: 20px;
  font-weight: 700;
}

.hero-user-role {
  margin: 6px 0 12px;
  color: rgba(255, 255, 255, 0.72);
}

@media (max-width: 960px) {
  .layout-shell {
    grid-template-columns: 1fr;
  }

  .sidebar {
    padding-bottom: 8px;
  }

  .hero {
    flex-direction: column;
  }
}
</style>
