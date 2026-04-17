import request from '@/config/axios'

export interface VisitVO {
  id: number
  patientId: number
  doctorId: number
  deptId: number
  visitDate: string
  status: number
  diagnosis: string
  createTime: Date
}

// 获取就诊分页列表
export const getVisitPage = (params: any) => {
  return request.get({ url: '/hospital/visit/page', params })
}

// 获取就诊详情
export const getVisit = (id: number) => {
  return request.get({ url: '/hospital/visit/get?id=' + id })
}

// 创建就诊记录
export const createVisit = (data: VisitVO) => {
  return request.post({ url: '/hospital/visit/create', data })
}

// 修改就诊记录
export const updateVisit = (data: VisitVO) => {
  return request.put({ url: '/hospital/visit/update', data })
}

// 删除就诊记录
export const deleteVisit = (id: number) => {
  return request.delete({ url: '/hospital/visit/delete?id=' + id })
}
