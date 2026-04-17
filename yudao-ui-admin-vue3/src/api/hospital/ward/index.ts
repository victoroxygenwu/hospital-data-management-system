import request from '@/config/axios'

export interface WardVO {
  id: number
  deptId: number
  name: string
  wardType: string
  floor: number
  createTime: Date
}

// 获取病房分页列表
export const getWardPage = (params: any) => {
  return request.get({ url: '/hospital/ward/page', params })
}

// 获取病房详情
export const getWard = (id: number) => {
  return request.get({ url: '/hospital/ward/get?id=' + id })
}

// 创建病房
export const createWard = (data: WardVO) => {
  return request.post({ url: '/hospital/ward/create', data })
}

// 修改病房
export const updateWard = (data: WardVO) => {
  return request.put({ url: '/hospital/ward/update', data })
}

// 删除病房
export const deleteWard = (id: number) => {
  return request.delete({ url: '/hospital/ward/delete?id=' + id })
}

// 获取某科室下的病房列表
export const getWardListByDeptId = (deptId: number) => {
  return request.get({ url: '/hospital/ward/list-by-dept?deptId=' + deptId })
}

// 获取病房下的床位列表
export const getWardBeds = (id: number) => {
  return request.get({ url: '/hospital/ward/' + id + '/beds' })
}
