<script setup>
import { computed, ref } from 'vue'
import { showConfirmDialog, showSuccessToast } from 'vant'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { updatePasswordApi } from '../api'
import { roleLabelMap } from '../utils'

const router = useRouter()
const authStore = useAuthStore()
const showPassword = ref(false)
const passwordForm = ref({
  oldPassword: '',
  newPassword: ''
})

const user = computed(() => authStore.user || {})
const initial = computed(() => user.value.name?.slice(0, 1) || '业')

const logout = async () => {
  await showConfirmDialog({ title: '退出登录', message: '确定退出当前账号吗？' })
  await authStore.logout()
  showSuccessToast('已退出登录')
  router.replace('/login')
}

const updatePassword = async () => {
  await updatePasswordApi(passwordForm.value)
  showSuccessToast('密码已更新')
  showPassword.value = false
  passwordForm.value = { oldPassword: '', newPassword: '' }
}
</script>

<template>
  <div>
    <section class="banner-card" style="padding-bottom: 28px;">
      <div style="display: flex; align-items: center; gap: 14px;">
        <img
          v-if="user.avatar"
          :src="user.avatar"
          alt="avatar"
          style="width: 72px; height: 72px; border-radius: 50%; object-fit: cover; border: 3px solid rgba(255,255,255,0.28);"
        />
        <div
          v-else
          class="avatar-fallback"
          style="width: 72px; height: 72px; border-radius: 50%; font-size: 30px; background: rgba(255,255,255,0.2);"
        >
          {{ initial }}
        </div>
        <div>
          <div style="font-size: 24px; font-weight: 700;">{{ user.name }}</div>
          <div style="margin-top: 8px;" class="chip">{{ roleLabelMap[user.role] }}</div>
          <div style="margin-top: 8px; font-size: 13px; opacity: 0.9;">{{ user.phone }}</div>
        </div>
      </div>
    </section>

    <div class="glass-card" style="margin-top: 16px; overflow: hidden;">
      <van-cell title="个人资料" is-link @click="router.push('/profile-edit')" />
      <van-cell title="我的地址" :value="user.address || '未填写'" />
      <van-cell
        v-if="user.role === 'STAFF'"
        title="负责业务"
        :value="user.businessScope || '暂未填写'"
      />
      <van-cell title="修改密码" is-link @click="showPassword = true" />
      <van-cell title="退出登录" is-link style="color: #ee3f4d;" @click="logout" />
    </div>

    <van-dialog v-model:show="showPassword" title="修改密码" show-cancel-button @confirm="updatePassword">
      <div style="padding: 12px 16px 18px;">
        <van-field v-model="passwordForm.oldPassword" label="旧密码" type="password" placeholder="请输入旧密码" />
        <van-field v-model="passwordForm.newPassword" label="新密码" type="password" placeholder="请输入新密码" />
      </div>
    </van-dialog>
  </div>
</template>
