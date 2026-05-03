<script setup>
import { onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getOrderListApi } from '../api'
import { categoryLabelMap, formatDateTime, orderStatusMap } from '../utils'

const route = useRoute()
const router = useRouter()
const activeStatus = ref(route.query.status || '')
const records = ref([])

const loadData = async () => {
  records.value = await getOrderListApi({ status: activeStatus.value || undefined })
}

watch(activeStatus, loadData)
watch(() => route.query.status, (value) => {
  activeStatus.value = value || ''
})
onMounted(loadData)
</script>

<template>
  <div>
    <van-nav-bar title="我的报修 / 反馈记录" />
    <van-tabs v-model:active="activeStatus">
      <van-tab title="全部" name="" />
      <van-tab title="处理中" name="PROCESSING" />
      <van-tab title="已完成" name="COMPLETED" />
      <van-tab title="已取消" name="CANCELLED" />
    </van-tabs>

    <div style="margin-top: 14px;">
      <div
        v-for="item in records"
        :key="item.id"
        class="glass-card list-card"
        @click="router.push(`/order/${item.id}`)"
      >
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <div>
            <div style="font-weight: 700;">{{ item.subtype }}</div>
            <div style="font-size: 12px; color: var(--text-secondary); margin-top: 6px;">
              {{ categoryLabelMap[item.category] }} · {{ formatDateTime(item.createdAt) }}
            </div>
          </div>
          <span
            class="status-tag"
            :class="{
              'status-pending': item.status === 'PENDING',
              'status-processing': item.status === 'PROCESSING',
              'status-completed': item.status === 'COMPLETED',
              'status-cancelled': item.status === 'CANCELLED'
            }"
          >
            {{ orderStatusMap[item.status] }}
          </span>
        </div>
        <div style="margin-top: 12px; color: var(--text-secondary); line-height: 1.6;">
          {{ item.description }}
        </div>
        <div style="margin-top: 12px; display: flex; justify-content: space-between; color: var(--text-secondary); font-size: 12px;">
          <span>{{ item.address }}</span>
          <span v-if="item.canEvaluate" style="color: var(--brand-blue);">可前往评价</span>
        </div>
      </div>
      <div v-if="!records.length" class="glass-card empty-box">暂时还没有记录，去提交第一条工单吧。</div>
    </div>
  </div>
</template>
