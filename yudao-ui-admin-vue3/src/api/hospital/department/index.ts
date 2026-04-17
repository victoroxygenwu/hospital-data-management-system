import request from '@/config/axios'

export interface DepartmentVO {
  id: number
  name: string
  code: string
  description: string
  createTime: Date
}

// 获取科室分页列表
export const getDepartmentPage = (params: any) => {
  return request.get({ url: '/hospital/department/page', params })
}

// 获取科室详情
export const getDepartment = (id: number) => {
  return request.get({ url: '/hospital/department/get?id=' + id })
}

// 创建科室
export const createDepartment = (data: DepartmentVO) => {
  return request.post({ url: '/hospital/department/create', data })
}

// 修改科室
export const updateDepartment = (data: DepartmentVO) => {
  return request.put({ url: '/hospital/department/update', data })
}

// 删除科室
export const deleteDepartment = (id: number) => {
  return request.delete({ url: '/hospital/department/delete?id=' + id })
}

// 获取科室全列表（下拉选择用）
export const getDepartmentList = () => {
  return request.get({ url: '/hospital/department/list-all' })
}
