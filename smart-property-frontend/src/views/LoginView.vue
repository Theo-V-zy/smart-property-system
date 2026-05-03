<script setup>
import { reactive, ref } from 'vue'
import { showSuccessToast } from 'vant'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const authStore = useAuthStore()
const loading = ref(false)

const form = reactive({
  role: 'OWNER',
  username: 'owner01',
  password: '123456'
})

const login = async () => {
  loading.value = true
  try {
    await authStore.login(form)
    showSuccessToast('登录成功')
    router.replace('/home')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="mobile-shell">
    <div class="page-content" style="padding-top: 18px;">
      <div class="banner-card" style="padding-bottom: 86px;">
        <div class="chip">智慧物业服务平台</div>
        <h1 class="page-title" style="margin-top: 18px; color: #fff;">掌上物业</h1>
        <p class="page-subtitle" style="color: rgba(255,255,255,0.84);">
          让业主报修、物业处理、通知发布与失物招领在手机上高效完成。
        </p>
      </div>

      <div class="glass-card" style="margin-top: -58px; padding: 22px 18px;">
        <van-radio-group v-model="form.role" direction="horizontal" style="margin-bottom: 18px;">
          <van-radio name="OWNER">业主登录</van-radio>
          <van-radio name="STAFF">物业登录</van-radio>
        </van-radio-group>

        <van-form @submit="login">
          <van-cell-group inset>
            <van-field v-model="form.username" label="账号" placeholder="请输入账号" />
            <van-field v-model="form.password" label="密码" type="password" placeholder="请输入密码" />
          </van-cell-group>
          <div style="margin: 22px 0 12px;">
            <van-button block round type="primary" native-type="submit" :loading="loading">
              立即登录
            </van-button>
          </div>
        </van-form>

        <div style="display: flex; justify-content: space-between; font-size: 13px; color: var(--text-secondary);">
          <span>体验账号：`owner01 / 123456`、`staff01 / 123456`</span>
        </div>
        <div style="margin-top: 18px; text-align: center; font-size: 14px;">
          还没有账号？
          <span style="color: var(--brand-blue);" @click="router.push('/register')">立即注册</span>
        </div>
      </div>
    </div>
  </div>
</template>
