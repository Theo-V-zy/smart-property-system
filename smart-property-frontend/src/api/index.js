import http from './http'

export const loginApi = (payload) => http.post('/auth/login', payload)
export const registerOwnerApi = (payload) => http.post('/auth/register/owner', payload)
export const registerStaffApi = (payload) => http.post('/auth/register/staff', payload)
export const getCurrentUserApi = () => http.get('/auth/me')
export const logoutApi = () => http.post('/auth/logout')

export const getHomeSummaryApi = () => http.get('/home/summary')
export const getNoticeListApi = () => http.get('/notices')
export const createNoticeApi = (payload) => http.post('/notices', payload)

export const getOrderListApi = (params) => http.get('/orders', { params })
export const createOrderApi = (payload) => http.post('/orders', payload)
export const assistOrderApi = (payload) => http.post('/orders/assistant', payload)
export const getOrderDetailApi = (id) => http.get(`/orders/${id}`)
export const processOrderApi = (id, payload) => http.put(`/orders/${id}/process`, payload)
export const evaluateOrderApi = (id, payload) => http.post(`/orders/${id}/evaluate`, payload)

export const getLostFoundListApi = (params) => http.get('/lost-found', { params })
export const createLostFoundApi = (payload) => http.post('/lost-found', payload)

export const getProfileApi = () => http.get('/users/profile')
export const getOwnerListApi = (params) => http.get('/users/owners', { params })
export const getOwnerDetailApi = (id) => http.get(`/users/owners/${id}`)
export const updateProfileApi = (payload) => http.put('/users/profile', payload)
export const updatePasswordApi = (payload) => http.put('/users/password', payload)
