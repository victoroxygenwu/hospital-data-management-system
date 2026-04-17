import request from '@/config/axios'

export interface BillVO {
  id: number
  patientId: number
  visitId: number
  prescriptionId: number
  totalAmount: number
  payStatus: number
  payMethod: string
  payTime: Date
  createTime: Date
}

// 获取账单分页列表
export const getBillPage = (params: any) => {
  return request.get({ url: '/hospital/bill/page', params })
}

// 获取账单详情
export const getBill = (id: number) => {
  return request.get({ url: '/hospital/bill/get?id=' + id })
}

// 创建账单
export const createBill = (data: BillVO) => {
  return request.post({ url: '/hospital/bill/create', data })
}

// 修改账单
export const updateBill = (data: BillVO) => {
  return request.put({ url: '/hospital/bill/update', data })
}

// 删除账单
export const deleteBill = (id: number) => {
  return request.delete({ url: '/hospital/bill/delete?id=' + id })
}

// 支付账单
export const payBill = (id: number, payMethod: string) => {
  return request.put({ url: '/hospital/bill/pay?id=' + id + '&payMethod=' + payMethod })
}
