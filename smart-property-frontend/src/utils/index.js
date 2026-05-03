export const formatDateTime = (value) => {
  if (!value) return '--'
  return new Intl.DateTimeFormat('zh-CN', {
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  }).format(new Date(value))
}

export const categoryLabelMap = {
  REPAIR: '报修',
  LOST: '报失',
  FEEDBACK: '反馈'
}

export const orderStatusMap = {
  PENDING: '待处理',
  PROCESSING: '处理中',
  COMPLETED: '已完成',
  CANCELLED: '已取消'
}

export const lostFoundTypeMap = {
  FOUND: '失物招领',
  LOST: '寻物启事'
}

export const roleLabelMap = {
  OWNER: '业主',
  STAFF: '物业人员'
}

export const toBase64List = async (fileList) => Promise.all(
  fileList.map((item) => {
    if (item.content) return item.content
    return new Promise((resolve) => {
      const reader = new FileReader()
      reader.onload = () => resolve(reader.result)
      reader.readAsDataURL(item.file)
    })
  })
)
