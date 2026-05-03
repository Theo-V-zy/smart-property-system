<script setup>
import { onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getOrderListApi } from '../api'
import { categoryLabelMap, formatDateTime, orderStatusMap } from '../utils'

const route = useRoute()
const router = useRouter()
const activeStatus = ref(route.query.status || 'PENDING')
const tasks = ref([])

const loadData = async () => {
  tasks.value = await getOrderListApi({ status: activeStatus.value || undefined })
}

watch(activeStatus, loadData)
watch(() => route.query.status, (value) => {
  activeStatus.value = value || 'PENDING'
})
onMounted(loadData)
</script>

<template>
  <div>
    <van-nav-bar title="物业端任务管理" />
    <van-tabs v-model:active="activeStatus">
      <van-tab title="待处理" name="PENDING" />
      <van-tab title="处理中" name="PROCESSING" />
      <van-tab title="已完成" name="COMPLETED" />
      <van-tab title="全部" name="" />
    </van-tabs>

    <div style="margin-top: 14px;">
      <div
        v-for="item in tasks"
        :key="item.id"
        class="glass-card list-card"
        @click="router.push(`/order/${item.id}`)"
      >
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <div>
            <div style="font-weight: 700;">{{ item.subtype }}</div>
            <div style="font-size: 12px; color: var(--text-secondary); margin-top: 6px;">
              {{ item.ownerName }} · {{ categoryLabelMap[item.category] }}
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
        <div style="margin-top: 10px; color: var(--text-secondary); line-height: 1.6;">
          {{ item.description }}
        </div>
        <div style="margin-top: 12px; font-size: 12px; color: var(--text-secondary);">
          {{ item.address }} · {{ formatDateTime(item.createdAt) }}
        </div>
      </div>
      <div v-if="!tasks.length" class="glass-card empty-box">当前筛选条件下没有待处理任务。</div>
    </div>
  </div>
</template>
