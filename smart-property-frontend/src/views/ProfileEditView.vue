<script setup>
import { onMounted, reactive, ref } from 'vue'
import { showSuccessToast } from 'vant'
import { useRouter } from 'vue-router'
import { getProfileApi, updateProfileApi } from '../api'

const router = useRouter()
const loading = ref(false)
const form = reactive({
  name: '',
  phone: '',
  address: '',
  businessScope: '',
  avatar: ''
})

onMounted(async () => {
  const profile = await getProfileApi()
  Object.assign(form, profile)
})

const submit = async () => {
  loading.value = true
  try {
    await updateProfileApi(form)
    showSuccessToast('资料已保存')
    router.replace('/profile')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="mobile-shell">
    <div class="page-content">
      <van-nav-bar title="个人资料编辑页" left-arrow @click-left="router.back()" />
      <div class="glass-card" style="padding: 18px;">
        <van-form @submit="submit">
          <van-field v-model="form.avatar" label="头像地址" placeholder="可粘贴网络图片地址，留空则使用默认样式" />
          <van-field v-model="form.name" label="姓名" placeholder="请输入姓名" />
          <van-field v-model="form.phone" label="手机号" placeholder="请输入手机号" />
          <van-field v-model="form.address" label="住址" placeholder="请输入楼栋房号" />
          <van-field v-model="form.businessScope" label="负责业务" placeholder="物业人员填写负责业务" />
          <div style="margin-top: 24px;">
            <van-button block round type="primary" native-type="submit" :loading="loading">
              保存资料
            </van-button>
          </div>
        </van-form>
      </div>
    </div>
  </div>
</template>
