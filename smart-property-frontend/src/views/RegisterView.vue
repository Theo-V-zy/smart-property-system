<script setup>
import { reactive, ref } from 'vue'
import { showSuccessToast } from 'vant'
import { useRouter } from 'vue-router'
import { registerOwnerApi } from '../api'

const router = useRouter()
const loading = ref(false)

const ownerForm = reactive({
  username: '',
  password: '',
  name: '',
  phone: '',
  communityName: '幸福花园小区',
  address: ''
})

const submit = async () => {
  loading.value = true
  try {
    await registerOwnerApi(ownerForm)
    showSuccessToast('注册成功，请登录')
    router.replace('/login')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="mobile-shell">
    <div class="page-content">
      <van-nav-bar title="业主注册" left-arrow @click-left="router.back()" />
      <div class="glass-card" style="padding: 18px;">
        <div class="section-title" style="margin: 0 0 12px;">
          <span>创建业主账号</span>
        </div>
        <div style="margin-bottom: 16px; color: var(--text-secondary); font-size: 13px; line-height: 1.7;">
          为了保证物业身份安全，系统仅开放业主自助注册。物业人员账号由系统预置，用于课程演示和后台任务处理。
        </div>

        <van-form @submit="submit" style="margin-top: 16px;">
          <van-cell-group inset>
            <van-field v-model="ownerForm.username" label="账号" placeholder="请输入账号" />
            <van-field v-model="ownerForm.password" label="密码" type="password" placeholder="请输入密码" />
            <van-field v-model="ownerForm.name" label="姓名" placeholder="请输入姓名" />
            <van-field v-model="ownerForm.phone" label="手机号" placeholder="请输入手机号" />
            <van-field v-model="ownerForm.communityName" label="小区" placeholder="请输入小区名称" />
            <van-field v-model="ownerForm.address" label="住址" placeholder="如 1栋1单元101" />
          </van-cell-group>

          <div style="margin-top: 24px;">
            <van-button block round type="primary" native-type="submit" :loading="loading">
              注册业主账号
            </van-button>
          </div>
        </van-form>
      </div>
    </div>
  </div>
</template>
