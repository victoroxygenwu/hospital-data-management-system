import request from '@/config/axios'

export interface MedicineVO {
  id: number
  name: string
  specification: string
  unit: string
  price: number
  stock: number
  manufacturer: string
  createTime: Date
}

// 获取药品分页列表
export const getMedicinePage = (params: any) => {
  return request.get({ url: '/hospital/medicine/page', params })
}

// 获取药品详情
export const getMedicine = (id: number) => {
  return request.get({ url: '/hospital/medicine/get?id=' + id })
}

// 创建药品
export const createMedicine = (data: MedicineVO) => {
  return request.post({ url: '/hospital/medicine/create', data })
}

// 修改药品
export const updateMedicine = (data: MedicineVO) => {
  return request.put({ url: '/hospital/medicine/update', data })
}

// 删除药品
export const deleteMedicine = (id: number) => {
  return request.delete({ url: '/hospital/medicine/delete?id=' + id })
}
