<script setup>
import { onMounted, reactive, ref } from 'vue'
import { showSuccessToast } from 'vant'
import { createLostFoundApi, getLostFoundListApi } from '../api'
import { useAuthStore } from '../stores/auth'
import { formatDateTime, lostFoundTypeMap, toBase64List } from '../utils'

const authStore = useAuthStore()
const items = ref([])
const showDialog = ref(false)
const fileList = ref([])
const loading = ref(false)
const form = reactive({
  type: 'FOUND',
  title: '',
  description: '',
  pickupLocation: '',
  contactName: authStore.user?.name || '',
  contactPhone: authStore.user?.phone || ''
})

const loadData = async () => {
  items.value = await getLostFoundListApi()
}

const submit = async () => {
  loading.value = true
  try {
    await createLostFoundApi({
      ...form,
      images: await toBase64List(fileList.value)
    })
    showSuccessToast('发布成功')
    showDialog.value = false
    await loadData()
  } finally {
    loading.value = false
  }
}

onMounted(loadData)
</script>

<template>
  <div>
    <van-nav-bar title="失物招领页">
      <template #right>
        <span v-if="authStore.isStaff" style="color: var(--brand-blue);" @click="showDialog = true">发布招领</span>
      </template>
    </van-nav-bar>

    <div>
      <div v-for="item in items" :key="item.id" class="glass-card list-card">
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <div style="font-size: 17px; font-weight: 700;">{{ item.title }}</div>
          <van-tag type="primary">{{ lostFoundTypeMap[item.type] }}</van-tag>
        </div>
        <div style="margin-top: 10px; color: var(--text-secondary); line-height: 1.7;">
          {{ item.description }}
        </div>
        <div v-if="item.images?.length" style="margin-top: 12px; display: grid; grid-template-columns: repeat(3, 1fr); gap: 8px;">
          <img v-for="(img, index) in item.images" :key="index" :src="img" alt="" style="width: 100%; height: 84px; object-fit: cover; border-radius: 12px;" />
        </div>
        <div style="margin-top: 12px; font-size: 12px; color: var(--text-secondary); line-height: 1.7;">
          认领地点：{{ item.pickupLocation }}<br />
          联系人：{{ item.contactName }} · {{ item.contactPhone }}<br />
          发布时间：{{ formatDateTime(item.createdAt) }}
        </div>
      </div>
      <div v-if="!items.length" class="glass-card empty-box">目前还没有失物招领信息。</div>
    </div>

    <van-dialog v-model:show="showDialog" title="发布失物信息" show-cancel-button :show-confirm-button="false">
      <div style="padding: 0 14px 18px;">
        <van-form @submit="submit">
          <van-field v-model="form.title" label="标题" placeholder="如 黑色钱包" />
          <van-field v-model="form.description" label="描述" type="textarea" rows="3" placeholder="描述物品特征与拾取情况" />
          <van-field v-model="form.pickupLocation" label="地点" placeholder="请输入认领地点" />
          <van-field v-model="form.contactName" label="联系人" placeholder="请输入联系人" />
          <van-field v-model="form.contactPhone" label="电话" placeholder="请输入联系电话" />
          <van-field name="type" label="类型">
            <template #input>
              <van-radio-group v-model="form.type" direction="horizontal">
                <van-radio name="FOUND">招领</van-radio>
                <van-radio name="LOST">寻物</van-radio>
              </van-radio-group>
            </template>
          </van-field>
          <van-field name="images" label="图片">
            <template #input><van-uploader v-model="fileList" :max-count="3" multiple /></template>
          </van-field>
          <div style="margin-top: 14px;">
            <van-button block round type="primary" native-type="submit" :loading="loading">确认发布</van-button>
          </div>
        </van-form>
      </div>
    </van-dialog>
  </div>
</template>
