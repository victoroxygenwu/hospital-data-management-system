import request from '@/config/axios'

export interface BedVO {
  id: number
  wardId: number
  bedNumber: string
  status: number
  patientId: number
  createTime: Date
}

// 获取床位分页列表
export const getBedPage = (params: any) => {
  return request.get({ url: '/hospital/bed/page', params })
}

// 获取床位详情
export const getBed = (id: number) => {
  return request.get({ url: '/hospital/bed/get?id=' + id })
}

// 创建床位
export const createBed = (data: BedVO) => {
  return request.post({ url: '/hospital/bed/create', data })
}

// 修改床位
export const updateBed = (data: BedVO) => {
  return request.put({ url: '/hospital/bed/update', data })
}

// 删除床位
export const deleteBed = (id: number) => {
  return request.delete({ url: '/hospital/bed/delete?id=' + id })
}

// 获取某病房下的床位列表
export const getBedListByWardId = (wardId: number) => {
  return request.get({ url: '/hospital/bed/list-by-ward?wardId=' + wardId })
}

// 床位分配
export const assignBed = (bedId: number, patientId: number) => {
  return request.put({ url: '/hospital/bed/assign?bedId=' + bedId + '&patientId=' + patientId })
}

// 床位释放
export const releaseBed = (bedId: number) => {
  return request.put({ url: '/hospital/bed/release?bedId=' + bedId })
}
