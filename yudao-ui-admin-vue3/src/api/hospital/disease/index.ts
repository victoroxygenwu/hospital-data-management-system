import request from '@/config/axios'

export const getDiseasePage = (params: any) => request.get({ url: '/hospital/disease/page', params })
export const getDisease = (id: number) => request.get({ url: '/hospital/disease/get?id=' + id })
export const createDisease = (data: any) => request.post({ url: '/hospital/disease/create', data })
export const updateDisease = (data: any) => request.put({ url: '/hospital/disease/update', data })
export const deleteDisease = (id: number) => request.delete({ url: '/hospital/disease/delete?id=' + id })
