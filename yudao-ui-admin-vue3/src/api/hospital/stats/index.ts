import request from '@/config/axios'

// 就诊趋势统计
export const getVisitTrend = (params?: any) => {
  return request.get({ url: '/hospital/stats/visit-trend', params })
}

// 床位使用率统计
export const getWardUsage = () => {
  return request.get({ url: '/hospital/stats/ward-usage' })
}

// 药品库存统计
export const getMedicineStock = () => {
  return request.get({ url: '/hospital/stats/medicine-stock' })
}
