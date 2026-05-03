<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const tabs = computed(() => (
  authStore.isOwner
    ? [
        { name: '/home', label: '首页', icon: 'home-o' },
        { name: '/records', label: '服务', icon: 'todo-list-o' },
        { name: '/lost-found', label: '招领', icon: 'search' },
        { name: '/profile', label: '我的', icon: 'user-o' }
      ]
    : [
        { name: '/home', label: '首页', icon: 'home-o' },
        { name: '/tasks', label: '工单', icon: 'todo-list-o' },
        { name: '/lost-found', label: '招领', icon: 'search' },
        { name: '/profile', label: '我的', icon: 'user-o' }
      ]
))

const active = computed({
  get: () => route.path,
  set: (value) => router.push(value)
})
</script>

<template>
  <div class="mobile-shell">
    <div class="page-content">
      <router-view />
    </div>
    <van-tabbar v-model="active" route>
      <van-tabbar-item
        v-for="tab in tabs"
        :key="tab.name"
        :name="tab.name"
      >
        <template #icon>
          <van-icon :name="tab.icon" />
        </template>
        {{ tab.label }}
      </van-tabbar-item>
    </van-tabbar>
  </div>
</template>
