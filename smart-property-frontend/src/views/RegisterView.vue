<script setup>
import { reactive, ref } from 'vue'
import { showSuccessToast } from 'vant'
import { useRouter } from 'vue-router'
import { registerOwnerApi, registerStaffApi } from '../api'

const router = useRouter()
const active = ref('OWNER')
const loading = ref(false)

const ownerForm = reactive({
  username: '',
  password: '',
  name: '',
  phone: '',
  communityName: '幸福花园小区',
  address: ''
})

const staffForm = reactive({
  username: '',
  password: '',
  name: '',
  phone: '',
  businessScope: ''
})

const submit = async () => {
  loading.value = true
  try {
    if (active.value === 'OWNER') {
      await registerOwnerApi(ownerForm)
    } else {
      await registerStaffApi(staffForm)
    }
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
      <van-nav-bar title="注册页" left-arrow @click-left="router.back()" />
      <div class="glass-card" style="padding: 18px;">
        <van-tabs v-model:active="active" animated>
          <van-tab title="业主注册" name="OWNER" />
          <van-tab title="物业人员注册" name="STAFF" />
        </van-tabs>

        <van-form @submit="submit" style="margin-top: 16px;">
          <van-cell-group inset v-if="active === 'OWNER'">
            <van-field v-model="ownerForm.username" label="账号" placeholder="请输入账号" />
            <van-field v-model="ownerForm.password" label="密码" type="password" placeholder="请输入密码" />
            <van-field v-model="ownerForm.name" label="姓名" placeholder="请输入姓名" />
            <van-field v-model="ownerForm.phone" label="手机号" placeholder="请输入手机号" />
            <van-field v-model="ownerForm.communityName" label="小区" placeholder="请输入小区名称" />
            <van-field v-model="ownerForm.address" label="住址" placeholder="如 1栋1单元101" />
          </van-cell-group>

          <van-cell-group inset v-else>
            <van-field v-model="staffForm.username" label="账号" placeholder="请输入账号" />
            <van-field v-model="staffForm.password" label="密码" type="password" placeholder="请输入密码" />
            <van-field v-model="staffForm.name" label="姓名" placeholder="请输入姓名" />
            <van-field v-model="staffForm.phone" label="手机号" placeholder="请输入手机号" />
            <van-field v-model="staffForm.businessScope" label="负责业务" placeholder="如 维修、保洁" />
          </van-cell-group>

          <div style="margin-top: 24px;">
            <van-button block round type="primary" native-type="submit" :loading="loading">
              提交注册
            </van-button>
          </div>
        </van-form>
      </div>
    </div>
  </div>
</template>
