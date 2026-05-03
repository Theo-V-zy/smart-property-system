import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const routes = [
  { path: '/', redirect: '/home' },
  {
    path: '/login',
    component: () => import('../views/LoginView.vue'),
    meta: { public: true }
  },
  {
    path: '/register',
    component: () => import('../views/RegisterView.vue'),
    meta: { public: true }
  },
  {
    path: '/',
    component: () => import('../views/MainLayout.vue'),
    children: [
      { path: 'home', component: () => import('../views/HomeView.vue') },
      { path: 'records', component: () => import('../views/RecordsView.vue') },
      { path: 'tasks', component: () => import('../views/TaskManageView.vue') },
      { path: 'lost-found', component: () => import('../views/LostFoundView.vue') },
      { path: 'profile', component: () => import('../views/ProfileView.vue') }
    ]
  },
  { path: '/order-submit', component: () => import('../views/SubmitOrderView.vue') },
  { path: '/order/:id', component: () => import('../views/OrderDetailView.vue') },
  { path: '/evaluate/:id', component: () => import('../views/EvaluationView.vue') },
  { path: '/notices', component: () => import('../views/NoticeView.vue') },
  { path: '/profile-edit', component: () => import('../views/ProfileEditView.vue') }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach(async (to) => {
  const authStore = useAuthStore()
  if (to.meta.public) {
    if (authStore.token && (to.path === '/login' || to.path === '/register')) {
      return '/home'
    }
    return true
  }
  if (!authStore.token) {
    return '/login'
  }
  if (!authStore.user) {
    await authStore.fetchCurrentUser()
  }
  return true
})

export default router
