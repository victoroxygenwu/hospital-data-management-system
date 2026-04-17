import request from '@/config/axios'

export interface PrescriptionItemVO {
  id: number
  prescriptionId: number
  medicineId: number
  quantity: number
  usage: string
  dosage: string
  frequency: string
  days: number
  remark: string
}

export interface PrescriptionVO {
  id: number
  visitId: number
  patientId: number
  doctorId: number
  status: number
  diagnosis: string
  items: PrescriptionItemVO[]
  createTime: Date
}

// 获取处方分页列表
export const getPrescriptionPage = (params: any) => {
  return request.get({ url: '/hospital/prescription/page', params })
}

// 获取处方详情（含明细）
export const getPrescription = (id: number) => {
  return request.get({ url: '/hospital/prescription/get?id=' + id })
}

// 创建处方
export const createPrescription = (data: PrescriptionVO) => {
  return request.post({ url: '/hospital/prescription/create', data })
}

// 修改处方
export const updatePrescription = (data: PrescriptionVO) => {
  return request.put({ url: '/hospital/prescription/update', data })
}

// 删除处方
export const deletePrescription = (id: number) => {
  return request.delete({ url: '/hospital/prescription/delete?id=' + id })
}

// 发药
export const dispensePrescription = (id: number) => {
  return request.put({ url: '/hospital/prescription/dispense?id=' + id })
}
