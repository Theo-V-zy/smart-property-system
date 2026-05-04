<script setup>
import { computed, onBeforeUnmount, onMounted, reactive, ref, watch } from 'vue'
import { showFailToast, showSuccessToast } from 'vant'
import { useRoute, useRouter } from 'vue-router'
import { assistOrderApi, createOrderApi } from '../api'
import { useAuthStore } from '../stores/auth'
import { toBase64List } from '../utils'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const loading = ref(false)
const aiLoading = ref(false)
const fileList = ref([])
const listening = ref(false)
const voiceSupported = ref(false)
const speechPreview = ref('')
const assistantResult = ref(null)
const showSubtypeSheet = ref(false)
const suppressAssistantReset = ref(false)
let recognition = null

const categoryOptions = [
  { text: '我要报修', value: 'REPAIR' },
  { text: '我要报失', value: 'LOST' },
  { text: '问题反馈', value: 'FEEDBACK' }
]

const subtypeMap = {
  REPAIR: ['水电维修', '门锁损坏', '公共设施', '其他'],
  LOST: ['证件遗失', '钥匙遗失', '贵重物品遗失', '其他'],
  FEEDBACK: ['环境卫生', '灯光昏暗', '噪音扰民', '其他']
}

const form = reactive({
  category: route.query.category || 'REPAIR',
  subtype: '',
  description: '',
  address: authStore.user?.address || ''
})

const currentSubtypeOptions = computed(() => subtypeMap[form.category] || [])
const currentCategoryLabel = computed(() => categoryOptions.find((item) => item.value === form.category)?.text || '请选择')
const assistantEngineLabel = computed(() => {
  if (!assistantResult.value) {
    return '未分析'
  }
  return assistantResult.value.engineName || (assistantResult.value.usedModel ? '外部大模型' : '本地规则助手')
})

watch(() => form.category, () => {
  if (!currentSubtypeOptions.value.includes(form.subtype)) {
    form.subtype = currentSubtypeOptions.value[0] || ''
  }
}, { immediate: true })

watch(
  () => [form.category, form.subtype, form.description, form.address],
  () => {
    if (!suppressAssistantReset.value) {
      assistantResult.value = null
    }
  }
)

onMounted(() => {
  voiceSupported.value = Boolean(window.SpeechRecognition || window.webkitSpeechRecognition)
})

onBeforeUnmount(() => {
  if (recognition) {
    recognition.stop()
    recognition = null
  }
})

const buildSpeechRecognition = () => {
  const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition
  if (!SpeechRecognition) return null
  const instance = new SpeechRecognition()
  instance.lang = 'zh-CN'
  instance.continuous = false
  instance.interimResults = true
  return instance
}

const toggleVoiceInput = () => {
  if (listening.value) {
    recognition?.stop()
    return
  }
  if (!voiceSupported.value) {
    showFailToast('当前浏览器暂不支持语音输入')
    return
  }
  speechPreview.value = ''
  recognition = buildSpeechRecognition()
  if (!recognition) {
    showFailToast('语音识别初始化失败')
    return
  }
  listening.value = true
  recognition.onresult = (event) => {
    const parts = []
    for (let index = event.resultIndex; index < event.results.length; index += 1) {
      parts.push(event.results[index][0].transcript)
    }
    speechPreview.value = parts.join('').trim()
  }
  recognition.onerror = () => {
    listening.value = false
    showFailToast('语音识别失败，请重试')
  }
  recognition.onend = () => {
    listening.value = false
    const text = speechPreview.value.trim()
    if (text) {
      form.description = text
      showSuccessToast('语音内容已覆盖到问题描述')
    }
    speechPreview.value = ''
  }
  recognition.start()
}

const runAssistant = async () => {
  const sourceText = form.description.trim()
  if (!sourceText) {
    showFailToast('请先输入或说出问题描述')
    return
  }
  aiLoading.value = true
  try {
    suppressAssistantReset.value = true
    const result = await assistOrderApi({ text: sourceText })
    form.category = result.category
    form.subtype = currentSubtypeOptions.value.includes(result.subtype) ? result.subtype : '其他'
    form.description = result.polishedDescription
    assistantResult.value = result
    showSuccessToast(result.usedModel ? 'AI已覆盖式整理工单描述' : '已生成覆盖式智能填写建议')
  } finally {
    suppressAssistantReset.value = false
    aiLoading.value = false
  }
}

const selectSubtype = (value) => {
  form.subtype = value
  showSubtypeSheet.value = false
}

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
      <section class="ai-panel">
        <div class="ai-panel__top">
          <div>
            <h2 class="ai-panel__title">语音输入 + 智能报修助手</h2>
            <p class="ai-panel__desc">
              先说出问题，再让系统帮你整理成更规范的工单描述，并推荐最合适的业务类型和问题类型。
            </p>
          </div>

        </div>

        <div class="ai-panel__actions">
          <button class="ai-action-btn ai-action-btn--primary" type="button" @click="toggleVoiceInput">
            {{ listening ? '结束语音录入' : (form.description ? '重新语音覆盖' : '开始语音录入') }}
          </button>
          <button class="ai-action-btn ai-action-btn--ghost" type="button" :disabled="aiLoading" @click="runAssistant">
            {{ aiLoading ? '智能分析中...' : 'AI辅助填写' }}
          </button>
        </div>


        <div v-if="listening || speechPreview" class="voice-preview">
          <div class="voice-preview__label">{{ listening ? '正在识别语音' : '识别结果' }}</div>
          <div class="voice-preview__text">{{ speechPreview || '请开始说话，例如：厨房水管漏水，地面已经积水。' }}</div>
        </div>
      </section>

      <div class="glass-card order-form-card">
        <div class="section-title section-title--compact">
          <span>基础信息</span>
          <span class="section-title__hint">提交前可先让 AI 帮你整理</span>
        </div>

        <div class="category-grid">
          <button
            v-for="item in categoryOptions"
            :key="item.value"
            type="button"
            class="category-option"
            :class="{ 'category-option--active': form.category === item.value }"
            @click="form.category = item.value"
          >
            {{ item.text }}
          </button>
        </div>

        <van-form @submit="submit">
          <van-field
            :model-value="form.subtype"
            readonly
            label="问题类型"
            placeholder="请选择问题类型"
            is-link
            @click="showSubtypeSheet = true"
          />

          <div class="selected-subtypes">
            <button
              v-for="item in currentSubtypeOptions"
              :key="item"
              type="button"
              class="subtype-chip"
              :class="{ 'subtype-chip--active': form.subtype === item }"
              @click="selectSubtype(item)"
            >
              {{ item }}
            </button>
          </div>

          <van-field
            v-model="form.description"
            label="问题描述"
            type="textarea"
            rows="5"
            autosize
            placeholder="请详细描述现场情况、发生位置、是否影响日常使用等"
          />

          <div class="inline-actions">
            <button class="mini-action mini-action--light" type="button" @click="toggleVoiceInput">
              {{ listening ? '停止语音' : (form.description ? '重新语音覆盖' : '语音输入') }}
            </button>
            <button class="mini-action mini-action--accent" type="button" :disabled="aiLoading" @click="runAssistant">
              {{ aiLoading ? '整理中...' : '智能整理并覆盖' }}
            </button>
          </div>

          <van-field v-model="form.address" label="所在位置" placeholder="请输入楼栋房号" />
          <van-field name="images" label="现场图片">
            <template #input>
              <van-uploader v-model="fileList" :max-count="3" multiple />
            </template>
          </van-field>

          <div v-if="assistantResult" class="assistant-card">
            <div class="assistant-card__header">
              <div>
                <div class="assistant-card__title">智能填写建议</div>
                <div class="assistant-card__meta">
                  来源：{{ assistantResult.engineName }}
                </div>
              </div>
              <span class="status-tag" :class="{
                'status-pending': assistantResult.urgency === 'LOW',
                'status-processing': assistantResult.urgency === 'MEDIUM',
                'status-cancelled': assistantResult.urgency === 'HIGH'
              }">
                {{ assistantResult.urgency === 'HIGH' ? '高优先级' : assistantResult.urgency === 'MEDIUM' ? '中优先级' : '常规处理' }}
              </span>
            </div>

            <div class="assistant-card__summary">
              推荐分类：{{ currentCategoryLabel }} · {{ form.subtype }}
            </div>

            <div class="assistant-card__summary">
              {{ assistantResult.engineMessage || (assistantResult.usedModel ? '当前已调用配置好的外部模型接口完成语义整理。' : '当前已切换为本地规则分析结果。') }}
            </div>

            <div class="assistant-card__tips">
              <div
                v-for="tip in assistantResult.tips"
                :key="tip"
                class="assistant-card__tip"
              >
                {{ tip }}
              </div>
            </div>
          </div>

          <div class="submit-wrap">
            <van-button block round type="primary" native-type="submit" :loading="loading">
              提交工单
            </van-button>
          </div>
        </van-form>
      </div>

      <van-action-sheet v-model:show="showSubtypeSheet" :actions="currentSubtypeOptions.map((item) => ({ name: item }))" cancel-text="取消" close-on-click-action @select="({ name }) => selectSubtype(name)">
        <div class="sheet-header">请选择{{ currentCategoryLabel }}的问题类型</div>
      </van-action-sheet>
    </div>
  </div>
</template>

<style scoped>
.ai-panel {
  margin-bottom: 18px;
  padding: 20px;
  border-radius: 28px;
  background:
    radial-gradient(circle at top right, rgba(255, 255, 255, 0.28), transparent 28%),
    linear-gradient(135deg, #2767f5 0%, #4295ff 48%, #7fd4ff 100%);
  box-shadow: 0 28px 50px rgba(42, 98, 219, 0.28);
  color: #fff;
}

.ai-panel__top {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: flex-start;
}

.ai-panel__eyebrow {
  display: inline-flex;
  padding: 6px 12px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.18);
  font-size: 12px;
  font-weight: 600;
}

.ai-panel__title {
  margin: 14px 0 0;
  font-size: 28px;
  line-height: 1.18;
}

.ai-panel__desc {
  margin: 12px 0 0;
  max-width: 520px;
  color: rgba(255, 255, 255, 0.9);
  font-size: 14px;
  line-height: 1.7;
}

.ai-panel__status {
  min-width: 96px;
  padding: 12px 14px;
  border-radius: 18px;
  background: rgba(8, 28, 79, 0.18);
  backdrop-filter: blur(10px);
  text-align: right;
  font-size: 12px;
}

.ai-panel__status strong {
  display: block;
  margin-top: 8px;
  font-size: 15px;
  line-height: 1.3;
}

.ai-panel__actions {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
  margin-top: 20px;
}

.ai-action-btn {
  min-height: 48px;
  border: 0;
  border-radius: 16px;
  font-size: 15px;
  font-weight: 700;
  cursor: pointer;
}

.ai-action-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.ai-action-btn--primary {
  background: #ffffff;
  color: #1852cf;
}

.ai-action-btn--ghost {
  background: rgba(255, 255, 255, 0.18);
  color: #ffffff;
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.ai-panel__tips {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 14px;
}

.ai-tip-chip {
  display: inline-flex;
  padding: 6px 12px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.14);
  font-size: 12px;
}

.voice-preview {
  margin-top: 14px;
  padding: 14px 16px;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.14);
}

.voice-preview__label {
  font-size: 12px;
  opacity: 0.86;
}

.voice-preview__text {
  margin-top: 8px;
  font-size: 14px;
  line-height: 1.7;
}

.order-form-card {
  padding: 18px;
}

.section-title--compact {
  margin: 4px 2px 16px;
}

.section-title__hint {
  font-size: 12px;
  color: var(--text-secondary);
  font-weight: 500;
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 10px;
  margin-bottom: 16px;
}

.category-option {
  min-height: 44px;
  border: 1px solid rgba(88, 115, 178, 0.16);
  border-radius: 16px;
  background: #f5f8ff;
  color: var(--text-primary);
  font-size: 14px;
  font-weight: 700;
}

.category-option--active {
  background: linear-gradient(135deg, #2f73ff, #53b7ff);
  color: #fff;
  box-shadow: 0 12px 24px rgba(47, 115, 255, 0.2);
}

.selected-subtypes {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin: 2px 0 16px 92px;
}

.subtype-chip {
  padding: 7px 12px;
  border-radius: 999px;
  border: 1px solid rgba(102, 121, 166, 0.18);
  background: #fff;
  color: var(--text-secondary);
  font-size: 13px;
  line-height: 1;
}

.subtype-chip--active {
  border-color: rgba(47, 115, 255, 0.3);
  background: rgba(47, 115, 255, 0.08);
  color: #2566ea;
  font-weight: 700;
}

.inline-actions {
  display: flex;
  gap: 10px;
  margin: 0 0 16px 92px;
}

.mini-action {
  min-width: 92px;
  min-height: 34px;
  border: 0;
  border-radius: 999px;
  font-size: 13px;
  font-weight: 700;
}

.mini-action:disabled {
  opacity: 0.7;
}

.mini-action--light {
  background: #eef4ff;
  color: #2b67e6;
}

.mini-action--accent {
  background: #fff1df;
  color: #d87718;
}

.assistant-card {
  margin-top: 12px;
  padding: 16px;
  border-radius: 22px;
  background: linear-gradient(180deg, rgba(47, 115, 255, 0.08), rgba(255, 255, 255, 0.98));
  border: 1px solid rgba(47, 115, 255, 0.12);
}

.assistant-card__header {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: flex-start;
}

.assistant-card__title {
  font-size: 16px;
  font-weight: 700;
}

.assistant-card__meta {
  margin-top: 6px;
  font-size: 12px;
  color: var(--text-secondary);
}

.assistant-card__summary {
  margin-top: 10px;
  color: var(--text-secondary);
  font-size: 13px;
  line-height: 1.7;
}

.assistant-card__tips {
  margin-top: 12px;
  display: grid;
  gap: 8px;
}

.assistant-card__tip {
  padding: 10px 12px;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.9);
  color: var(--text-secondary);
  font-size: 13px;
  line-height: 1.6;
}

.submit-wrap {
  margin-top: 24px;
}

.sheet-header {
  padding: 16px 18px 8px;
  font-size: 14px;
  font-weight: 700;
  color: var(--text-secondary);
}

@media (max-width: 420px) {
  .ai-panel__top {
    flex-direction: column;
  }

  .ai-panel__status {
    text-align: left;
  }

  .category-grid {
    grid-template-columns: 1fr;
  }

  .selected-subtypes,
  .inline-actions {
    margin-left: 0;
  }
}
</style>
