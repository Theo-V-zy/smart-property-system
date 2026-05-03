<script setup>
import { computed, reactive, ref } from 'vue'
import { showSuccessToast } from 'vant'
import { useRoute, useRouter } from 'vue-router'
import { createOrderApi } from '../api'
import { useAuthStore } from '../stores/auth'
import { toBase64List } from '../utils'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const loading = ref(false)
const fileList = ref([])

const categoryOptions = [
  { text: '我要报修', value: 'REPAIR' },
  { text: '我要报失', value: 'LOST' },
  { text: '问题反馈', value: 'FEEDBACK' }
]

const subtypeMap = {
  REPAIR: ['水电维修', '门锁损坏', '公共设施'],
  LOST: ['证件遗失', '钥匙遗失', '贵重物品遗失'],
  FEEDBACK: ['环境卫生', '灯光昏暗', '噪音扰民']
}

const form = reactive({
  category: route.query.category || 'REPAIR',
  subtype: '',
  description: '',
  address: authStore.user?.address || ''
})

const currentSubtypeOptions = computed(() => subtypeMap[form.category] || [])

const submit = async () => {
  loading.value = true
  try {
    await createOrderApi({
      ...form,
      images: await toBase64List(fileList.value)
    })
    showSuccessToast('提交成功')
    router.replace('/records')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="mobile-shell">
    <div class="page-content">
      <van-nav-bar title="报修 / 报失提交" left-arrow @click-left="router.back()" />
      <div class="glass-card" style="padding: 18px;">
        <van-form @submit="submit">
          <van-field name="category" label="业务类型">
            <template #input>
              <van-radio-group v-model="form.category" direction="horizontal">
                <van-radio v-for="item in categoryOptions" :key="item.value" :name="item.value">
                  {{ item.text }}
                </van-radio>
              </van-radio-group>
            </template>
          </van-field>
          <van-field
            v-model="form.subtype"
            is-link
            readonly
            label="问题类型"
            placeholder="请选择问题类型"
            @click="form.subtype = currentSubtypeOptions[0]"
          />
          <div style="display: flex; gap: 8px; flex-wrap: wrap; margin: 0 16px 12px;">
            <van-tag
              v-for="item in currentSubtypeOptions"
              :key="item"
              plain
              :type="form.subtype === item ? 'primary' : 'default'"
              @click="form.subtype = item"
            >
              {{ item }}
            </van-tag>
          </div>
          <van-field
            v-model="form.description"
            label="问题描述"
            type="textarea"
            rows="4"
            autosize
            placeholder="请详细描述现场情况与诉求"
          />
          <van-field v-model="form.address" label="所在位置" placeholder="请输入楼栋房号" />
          <van-field name="images" label="现场图片">
            <template #input>
              <van-uploader v-model="fileList" :max-count="3" multiple />
            </template>
          </van-field>
          <div style="margin-top: 24px;">
            <van-button block round type="primary" native-type="submit" :loading="loading">
              提交工单
            </van-button>
          </div>
        </van-form>
      </div>
    </div>
  </div>
</template>
