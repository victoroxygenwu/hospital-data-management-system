import request from '@/config/axios'

export interface DoctorVO {
  id: number
  name: string
  deptId: number
  title: string
  phone: string
  status: number
  createTime: Date
}

// 获取医生分页列表
export const getDoctorPage = (params: any) => {
  return request.get({ url: '/hospital/doctor/page', params })
}

// 获取医生详情
export const getDoctor = (id: number) => {
  return request.get({ url: '/hospital/doctor/get?id=' + id })
}

// 创建医生
export const createDoctor = (data: DoctorVO) => {
  return request.post({ url: '/hospital/doctor/create', data })
}

// 修改医生
export const updateDoctor = (data: DoctorVO) => {
  return request.put({ url: '/hospital/doctor/update', data })
}

// 删除医生
export const deleteDoctor = (id: number) => {
  return request.delete({ url: '/hospital/doctor/delete?id=' + id })
}

// 获取某科室下的医生列表
export const getDoctorListByDeptId = (deptId: number) => {
  return request.get({ url: '/hospital/doctor/list-by-dept?deptId=' + deptId })
}
