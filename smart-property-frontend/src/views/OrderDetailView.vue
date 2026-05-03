<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { showSuccessToast } from 'vant'
import { useRoute, useRouter } from 'vue-router'
import { getOrderDetailApi, processOrderApi } from '../api'
import { useAuthStore } from '../stores/auth'
import { categoryLabelMap, formatDateTime, orderStatusMap } from '../utils'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const detail = ref(null)
const loading = ref(false)
const processForm = reactive({
  status: 'PROCESSING',
  handleRecord: '',
  reply: ''
})

const isStaff = computed(() => authStore.isStaff)

const loadData = async () => {
  detail.value = await getOrderDetailApi(route.params.id)
  processForm.status = detail.value.status === 'PENDING' ? 'PROCESSING' : detail.value.status
}

const submitProcess = async () => {
  loading.value = true
  try {
    await processOrderApi(route.params.id, processForm)
    showSuccessToast('处理成功')
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
      <van-nav-bar title="处理详情页" left-arrow @click-left="router.back()" />
      <template v-if="detail">
        <div class="glass-card" style="padding: 18px;">
          <div style="display: flex; justify-content: space-between; align-items: center;">
            <div>
              <div style="font-size: 20px; font-weight: 700;">{{ detail.subtype }}</div>
              <div style="margin-top: 6px; color: var(--text-secondary); font-size: 12px;">
                {{ categoryLabelMap[detail.category] }} · 工单号 {{ detail.orderNo }}
              </div>
            </div>
            <span
              class="status-tag"
              :class="{
                'status-pending': detail.status === 'PENDING',
                'status-processing': detail.status === 'PROCESSING',
                'status-completed': detail.status === 'COMPLETED',
                'status-cancelled': detail.status === 'CANCELLED'
              }"
            >
              {{ orderStatusMap[detail.status] }}
            </span>
          </div>

          <van-cell-group inset style="margin-top: 16px;">
            <van-cell title="提交人" :value="detail.ownerName" />
            <van-cell title="联系电话" :value="detail.phone" />
            <van-cell title="所在位置" :value="detail.address" />
            <van-cell title="提交时间" :value="formatDateTime(detail.createdAt)" />
          </van-cell-group>

          <div style="margin-top: 16px; line-height: 1.75;">
            <div style="font-weight: 700; margin-bottom: 8px;">问题描述</div>
            <div style="color: var(--text-secondary);">{{ detail.description }}</div>
          </div>

          <div v-if="detail.images?.length" style="margin-top: 14px; display: grid; grid-template-columns: repeat(3, 1fr); gap: 10px;">
            <img
              v-for="(img, index) in detail.images"
              :key="index"
              :src="img"
              alt="现场图片"
              style="width: 100%; height: 90px; object-fit: cover; border-radius: 14px;"
            />
          </div>

          <div v-if="detail.handleRecord || detail.reply" style="margin-top: 18px;">
            <div style="font-weight: 700; margin-bottom: 8px;">处理记录</div>
            <div class="glass-card" style="padding: 14px; background: rgba(245, 248, 255, 0.92);">
              <div style="color: var(--text-secondary); line-height: 1.7;">{{ detail.handleRecord || '暂无处理记录' }}</div>
              <div style="margin-top: 8px; color: var(--brand-blue);">{{ detail.reply }}</div>
            </div>
          </div>
        </div>

        <div v-if="isStaff" class="glass-card" style="padding: 18px; margin-top: 16px;">
          <div class="section-title" style="margin: 0 0 12px;">
            <span>更新处理状态</span>
          </div>
          <van-form @submit="submitProcess">
            <van-field name="status" label="状态">
              <template #input>
                <van-radio-group v-model="processForm.status" direction="horizontal">
                  <van-radio name="PROCESSING">处理中</van-radio>
                  <van-radio name="COMPLETED">已完成</van-radio>
                  <van-radio name="CANCELLED">已取消</van-radio>
                </van-radio-group>
              </template>
            </van-field>
            <van-field
              v-model="processForm.handleRecord"
              label="处理记录"
              type="textarea"
              rows="4"
              placeholder="请填写处理经过与结果"
            />
            <van-field
              v-model="processForm.reply"
              label="回复业主"
              type="textarea"
              rows="2"
              placeholder="给业主留一句说明"
            />
            <div style="margin-top: 18px;">
              <van-button block round type="primary" native-type="submit" :loading="loading">
                保存处理结果
              </van-button>
            </div>
          </van-form>
        </div>

        <div v-else-if="detail.canEvaluate" style="margin-top: 18px;">
          <van-button block round type="primary" @click="router.push(`/evaluate/${detail.id}`)">
            去服务评价
          </van-button>
        </div>

        <div v-if="detail.evaluation" class="glass-card" style="padding: 18px; margin-top: 16px;">
          <div style="font-weight: 700;">业主评价</div>
          <div style="margin-top: 10px; color: var(--text-secondary); line-height: 1.75;">
            服务态度 {{ detail.evaluation.serviceScore }} 分 · 维修质量 {{ detail.evaluation.qualityScore }} 分 ·
            处理速度 {{ detail.evaluation.speedScore }} 分
          </div>
          <div style="margin-top: 8px;">{{ detail.evaluation.content }}</div>
        </div>
      </template>
    </div>
  </div>
</template>
