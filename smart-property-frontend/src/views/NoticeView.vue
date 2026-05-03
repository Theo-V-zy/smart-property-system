<script setup>
import { onMounted, reactive, ref } from 'vue'
import { showSuccessToast } from 'vant'
import { useRouter } from 'vue-router'
import { createNoticeApi, getNoticeListApi } from '../api'
import { useAuthStore } from '../stores/auth'
import { formatDateTime } from '../utils'

const authStore = useAuthStore()
const router = useRouter()
const notices = ref([])
const showDialog = ref(false)
const loading = ref(false)
const form = reactive({
  title: '',
  content: ''
})

const loadData = async () => {
  notices.value = await getNoticeListApi()
}

const submit = async () => {
  loading.value = true
  try {
    await createNoticeApi(form)
    showSuccessToast('通知已发布')
    form.title = ''
    form.content = ''
    showDialog.value = false
    await loadData()
  } finally {
    loading.value = false
  }
}

onMounted(loadData)
</script>

<template>
  <div class="mobile-shell">
    <div class="page-content">
      <van-nav-bar title="小区通知页" left-arrow @click-left="router.back()">
        <template #right>
          <span v-if="authStore.isStaff" style="color: var(--brand-blue);" @click="showDialog = true">发布通知</span>
        </template>
      </van-nav-bar>

      <div v-for="item in notices" :key="item.id" class="glass-card list-card">
        <div style="display: flex; justify-content: space-between; gap: 10px;">
          <div style="font-size: 18px; font-weight: 700;">{{ item.title }}</div>
          <van-tag v-if="item.pinned" type="danger">置顶</van-tag>
        </div>
        <div style="margin-top: 12px; color: var(--text-secondary); line-height: 1.8;">
          {{ item.content }}
        </div>
        <div style="margin-top: 12px; font-size: 12px; color: var(--text-secondary);">
          {{ item.publisherName }} · {{ formatDateTime(item.createdAt) }}
        </div>
      </div>

      <van-dialog v-model:show="showDialog" title="发布小区通知" show-cancel-button :show-confirm-button="false">
        <div style="padding: 0 14px 18px;">
          <van-form @submit="submit">
            <van-field v-model="form.title" label="标题" placeholder="请输入通知标题" />
            <van-field v-model="form.content" label="内容" type="textarea" rows="4" placeholder="请输入通知内容" />
            <div style="margin-top: 14px;">
              <van-button block round type="primary" native-type="submit" :loading="loading">立即发布</van-button>
            </div>
          </van-form>
        </div>
      </van-dialog>
    </div>
  </div>
</template>
