import request from '@/config/axios'

export const getDiseaseSymptomPage = (params: any) => request.get({ url: '/hospital/disease-symptom/page', params })
export const createDiseaseSymptom = (data: any) => request.post({ url: '/hospital/disease-symptom/create', data })
export const deleteDiseaseSymptom = (id: number) => request.delete({ url: '/hospital/disease-symptom/delete?id=' + id })

export const getDiseaseMedicinePage = (params: any) => request.get({ url: '/hospital/disease-medicine/page', params })
export const createDiseaseMedicine = (data: any) => request.post({ url: '/hospital/disease-medicine/create', data })
export const updateDiseaseMedicine = (data: any) => request.put({ url: '/hospital/disease-medicine/update', data })
export const deleteDiseaseMedicine = (id: number) => request.delete({ url: '/hospital/disease-medicine/delete?id=' + id })

export const getDiseaseNetwork = (id: number) => request.get({ url: '/hospital/knowledge-graph/disease-network?id=' + id })

export const assistDiagnosis = (data: { symptomDescription: string }) =>
  request.post({ url: '/hospital/ai/assist-diagnosis', data })

export const prescriptionReview = (data: { prescriptionId: number }) =>
  request.post({ url: '/hospital/ai/prescription-review', data })
