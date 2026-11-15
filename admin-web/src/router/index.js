import { createRouter, createWebHistory } from 'vue-router'
import AdminLayout from '../layouts/AdminLayout.vue'
import AdminAuthView from '../views/AdminAuthView.vue'
import DashboardView from '../views/DashboardView.vue'
import UserManageView from '../views/UserManageView.vue'
import HistoryManageView from '../views/HistoryManageView.vue'
import PostManageView from '../views/PostManageView.vue'
import TutorialManageView from '../views/TutorialManageView.vue'
import MaterialManageView from '../views/MaterialManageView.vue'
import { isAdminLoggedIn } from '../utils/auth'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      component: AdminAuthView
    },
    {
      path: '/',
      component: AdminLayout,
      redirect: '/dashboard',
      meta: { requiresAuth: true },
      children: [
        { path: '/dashboard', component: DashboardView, meta: { requiresAuth: true } },
        { path: '/users', component: UserManageView, meta: { requiresAuth: true } },
        { path: '/history', component: HistoryManageView, meta: { requiresAuth: true } },
        { path: '/posts', component: PostManageView, meta: { requiresAuth: true } },
        { path: '/content', redirect: '/tutorials', meta: { requiresAuth: true } },
        { path: '/tutorials', component: TutorialManageView, meta: { requiresAuth: true } },
        { path: '/materials', component: MaterialManageView, meta: { requiresAuth: true } }
      ]
    }
  ]
})

router.beforeEach((to) => {
  if (to.path === '/login' && isAdminLoggedIn()) {
    return '/dashboard'
  }
  if (to.meta.requiresAuth && !isAdminLoggedIn()) {
    return '/login'
  }
  return true
})

export default router
