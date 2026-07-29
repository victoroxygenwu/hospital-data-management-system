import request from '@/config/axios'

export const getSymptomPage = (params: any) => request.get({ url: '/hospital/symptom/page', params })
export const getSymptom = (id: number) => request.get({ url: '/hospital/symptom/get?id=' + id })
export const createSymptom = (data: any) => request.post({ url: '/hospital/symptom/create', data })
export const updateSymptom = (data: any) => request.put({ url: '/hospital/symptom/update', data })
export const deleteSymptom = (id: number) => request.delete({ url: '/hospital/symptom/delete?id=' + id })
