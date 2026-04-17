import request from '@/config/axios'

export interface PatientVO {
  id: number
  name: string
  gender: number
  birthDate: string
  phone: string
  idCard: string
  address: string
  createTime: Date
}

// 获取病人分页列表
export const getPatientPage = (params: any) => {
  return request.get({ url: '/hospital/patient/page', params })
}

// 获取病人详情
export const getPatient = (id: number) => {
  return request.get({ url: '/hospital/patient/get?id=' + id })
}

// 创建病人
export const createPatient = (data: PatientVO) => {
  return request.post({ url: '/hospital/patient/create', data })
}

// 修改病人
export const updatePatient = (data: PatientVO) => {
  return request.put({ url: '/hospital/patient/update', data })
}

// 删除病人
export const deletePatient = (id: number) => {
  return request.delete({ url: '/hospital/patient/delete?id=' + id })
}

// 获取病人的就诊记录
export const getPatientVisits = (id: number) => {
  return request.get({ url: '/hospital/patient/' + id + '/visits' })
}
