<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getHomeSummaryApi } from '../api'
import { useAuthStore } from '../stores/auth'
import { formatDateTime } from '../utils'

const router = useRouter()
const authStore = useAuthStore()
const summary = ref({
  quickStats: [],
  noticeList: [],
  pendingCount: 0,
  processingCount: 0,
  completedCount: 0,
  lostFoundCount: 0
})

const actions = computed(() => (
  authStore.isOwner
    ? [
        { title: '报修报失', icon: 'service-o', color: 'linear-gradient(135deg, #3a7cff, #5ecbff)', onClick: () => router.push('/order-submit') },
        { title: '问题反馈', icon: 'comment-circle-o', color: 'linear-gradient(135deg, #ff9c58, #ffd36d)', onClick: () => router.push({ path: '/order-submit', query: { category: 'FEEDBACK' } }) },
        { title: '服务评价', icon: 'star-o', color: 'linear-gradient(135deg, #42c58c, #68e4b0)', onClick: () => router.push('/records?status=COMPLETED') },
        { title: '失物招领', icon: 'guide-o', color: 'linear-gradient(135deg, #7d63ff, #9db6ff)', onClick: () => router.push('/lost-found') },
        { title: '小区通知', icon: 'bullhorn-o', color: 'linear-gradient(135deg, #28a2ff, #85d3ff)', onClick: () => router.push('/notices') },
        { title: '个人中心', icon: 'wap-home-o', color: 'linear-gradient(135deg, #ff7c82, #ffb37a)', onClick: () => router.push('/profile') }
      ]
    : [
        { title: '任务管理', icon: 'service-o', color: 'linear-gradient(135deg, #3a7cff, #5ecbff)', onClick: () => router.push('/tasks') },
        { title: '发布通知', icon: 'bullhorn-o', color: 'linear-gradient(135deg, #ff9c58, #ffd36d)', onClick: () => router.push('/notices') },
        { title: '发布招领', icon: 'guide-o', color: 'linear-gradient(135deg, #42c58c, #68e4b0)', onClick: () => router.push('/lost-found') },
        { title: '处理工单', icon: 'add-o', color: 'linear-gradient(135deg, #7d63ff, #9db6ff)', onClick: () => router.push('/tasks?status=PENDING') },
        { title: '住户信息', icon: 'friends-o', color: 'linear-gradient(135deg, #ff7c82, #ffb37a)', onClick: () => router.push('/residents') },
        { title: '个人中心', icon: 'star-o', color: 'linear-gradient(135deg, #28a2ff, #85d3ff)', onClick: () => router.push('/profile') }
      ]
))

onMounted(async () => {
  summary.value = await getHomeSummaryApi()
})
</script>

<template>
  <div>
    <section class="banner-card">
      <div style="display: flex; justify-content: space-between; align-items: flex-start;">
        <div>
          <div class="chip">{{ authStore.isOwner ? '业主端工作台' : '物业端工作台' }}</div>
          <h1 class="page-title" style="margin-top: 14px; color: #fff;">
            {{ authStore.user?.communityName || '幸福花园小区' }}
          </h1>
          <p class="page-subtitle" style="color: rgba(255,255,255,0.86);">
            {{ authStore.user?.name }}，欢迎回来。今天也把社区服务安排得明明白白。
          </p>
        </div>
        <div style="text-align: right;">
          <div style="font-size: 30px; font-weight: 700;">28°C</div>
          <div style="font-size: 12px; opacity: 0.9;">晴转多云</div>
        </div>
      </div>
      <div class="grid-2" style="margin-top: 18px;">
        <div class="glass-card" style="padding: 16px; background: rgba(255,255,255,0.16); color: #fff;">
          <div style="font-size: 13px; opacity: 0.88;">待处理</div>
          <div style="font-size: 28px; font-weight: 700; margin-top: 8px;">{{ summary.pendingCount }}</div>
        </div>
        <div class="glass-card" style="padding: 16px; background: rgba(255,255,255,0.16); color: #fff;">
          <div style="font-size: 13px; opacity: 0.88;">失物信息</div>
          <div style="font-size: 28px; font-weight: 700; margin-top: 8px;">{{ summary.lostFoundCount }}</div>
        </div>
      </div>
    </section>

    <div class="section-title">
      <span>快捷服务</span>
    </div>
    <div class="glass-card" style="padding: 14px;">
      <div class="grid-4" style="grid-template-columns: repeat(3, 1fr);">
        <div v-for="item in actions" :key="item.title" class="action-tile" @click="item.onClick">
          <div class="action-icon" :style="{ background: item.color }">
            <van-icon :name="item.icon" />
          </div>
          <div style="font-size: 13px; font-weight: 600;">{{ item.title }}</div>
        </div>
      </div>
    </div>

    <div class="section-title">
      <span>小区通知</span>
      <span style="font-size: 13px; color: var(--brand-blue);" @click="router.push('/notices')">更多</span>
    </div>
    <div class="glass-card" style="padding: 16px;">
      <div
        v-for="notice in summary.noticeList"
        :key="notice.id"
        style="padding: 12px 0; border-bottom: 1px solid rgba(111, 123, 144, 0.12);"
      >
        <div style="display: flex; justify-content: space-between; gap: 10px;">
          <div style="font-weight: 700;">{{ notice.title }}</div>
          <span style="font-size: 12px; color: var(--text-secondary);">{{ formatDateTime(notice.createdAt) }}</span>
        </div>
        <div style="margin-top: 8px; color: var(--text-secondary); font-size: 13px; line-height: 1.6;">
          {{ notice.content }}
        </div>
      </div>
    </div>
  </div>
</template>
