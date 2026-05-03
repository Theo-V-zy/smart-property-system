<script setup>
import { onMounted, reactive, ref } from 'vue'
import { showSuccessToast } from 'vant'
import { useRoute, useRouter } from 'vue-router'
import { evaluateOrderApi, getOrderDetailApi } from '../api'
import { toBase64List } from '../utils'

const route = useRoute()
const router = useRouter()
const order = ref(null)
const fileList = ref([])
const loading = ref(false)

const form = reactive({
  serviceScore: 5,
  qualityScore: 5,
  speedScore: 5,
  content: ''
})

onMounted(async () => {
  order.value = await getOrderDetailApi(route.params.id)
})

const submit = async () => {
  loading.value = true
  try {
    await evaluateOrderApi(route.params.id, {
      ...form,
      images: await toBase64List(fileList.value)
    })
    showSuccessToast('评价已提交')
    router.replace(`/order/${route.params.id}`)
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="mobile-shell">
    <div class="page-content">
      <van-nav-bar title="服务评价页" left-arrow @click-left="router.back()" />
      <div class="glass-card" style="padding: 18px;">
        <div v-if="order" style="margin-bottom: 16px;">
          <div style="font-size: 18px; font-weight: 700;">{{ order.subtype }}</div>
          <div style="margin-top: 6px; color: var(--text-secondary);">{{ order.handleRecord || '欢迎对本次服务进行打分' }}</div>
        </div>
        <van-form @submit="submit">
          <van-field label="服务态度">
            <template #input><van-rate v-model="form.serviceScore" /></template>
          </van-field>
          <van-field label="维修质量">
            <template #input><van-rate v-model="form.qualityScore" /></template>
          </van-field>
          <van-field label="处理速度">
            <template #input><van-rate v-model="form.speedScore" /></template>
          </van-field>
          <van-field
            v-model="form.content"
            label="评价内容"
            type="textarea"
            rows="4"
            maxlength="200"
            show-word-limit
            placeholder="请填写本次服务体验"
          />
          <van-field name="images" label="上传图片">
            <template #input>
              <van-uploader v-model="fileList" :max-count="3" multiple />
            </template>
          </van-field>
          <div style="margin-top: 20px;">
            <van-button block round type="primary" native-type="submit" :loading="loading">
              提交评价
            </van-button>
          </div>
        </van-form>
      </div>
    </div>
  </div>
</template>
