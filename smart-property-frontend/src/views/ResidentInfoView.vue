<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getOwnerDetailApi, getOwnerListApi } from '../api'

const router = useRouter()
const keyword = ref('')
const residents = ref([])
const loading = ref(false)
const detail = ref(null)
const showDetail = ref(false)

const loadData = async () => {
  loading.value = true
  try {
    residents.value = await getOwnerListApi({
      keyword: keyword.value || undefined
    })
  } finally {
    loading.value = false
  }
}

const openDetail = async (id) => {
  detail.value = await getOwnerDetailApi(id)
  showDetail.value = true
}

onMounted(loadData)
</script>

<template>
  <div class="mobile-shell">
    <div class="page-content">
      <van-nav-bar title="住户信息页" left-arrow @click-left="router.back()" />

      <div class="glass-card" style="padding: 14px; margin-bottom: 14px;">
        <van-search
          v-model="keyword"
          shape="round"
          placeholder="搜索姓名、手机号、住址"
          @search="loadData"
          @clear="loadData"
        />
      </div>

      <div v-if="loading" class="glass-card empty-box">正在加载住户信息...</div>
      <div
        v-for="item in residents"
        :key="item.id"
        class="glass-card list-card"
        @click="openDetail(item.id)"
      >
        <div style="display: flex; gap: 14px; align-items: center;">
          <img
            v-if="item.avatar"
            :src="item.avatar"
            alt="avatar"
            style="width: 58px; height: 58px; border-radius: 50%; object-fit: cover;"
          />
          <div
            v-else
            class="avatar-fallback"
            style="width: 58px; height: 58px; border-radius: 50%; font-size: 22px;"
          >
            {{ item.name?.slice(0, 1) || '住' }}
          </div>
          <div style="flex: 1;">
            <div style="font-size: 18px; font-weight: 700;">{{ item.name }}</div>
            <div style="margin-top: 6px; color: var(--text-secondary); font-size: 13px;">
              {{ item.phone }}
            </div>
            <div style="margin-top: 4px; color: var(--text-secondary); font-size: 13px; line-height: 1.5;">
              {{ item.communityName }} {{ item.address }}
            </div>
          </div>
          <van-icon name="arrow" color="#9aa6bf" />
        </div>
      </div>

      <div v-if="!loading && !residents.length" class="glass-card empty-box">
        当前没有匹配的住户信息。
      </div>

      <van-dialog v-model:show="showDetail" title="住户详情" :show-confirm-button="false" show-cancel-button>
        <div v-if="detail" style="padding: 8px 18px 18px;">
          <div style="display: flex; flex-direction: column; align-items: center; padding: 8px 0 14px;">
            <img
              v-if="detail.avatar"
              :src="detail.avatar"
              alt="avatar"
              style="width: 76px; height: 76px; border-radius: 50%; object-fit: cover;"
            />
            <div
              v-else
              class="avatar-fallback"
              style="width: 76px; height: 76px; border-radius: 50%; font-size: 28px;"
            >
              {{ detail.name?.slice(0, 1) || '住' }}
            </div>
            <div style="margin-top: 10px; font-size: 20px; font-weight: 700;">{{ detail.name }}</div>
          </div>

          <van-cell-group inset>
            <van-cell title="手机号" :value="detail.phone || '--'" />
            <van-cell title="小区名称" :value="detail.communityName || '--'" />
            <van-cell title="楼栋房号" :value="detail.address || '--'" />
            <van-cell title="登录账号" :value="detail.username || '--'" />
          </van-cell-group>
        </div>
      </van-dialog>
    </div>
  </div>
</template>
