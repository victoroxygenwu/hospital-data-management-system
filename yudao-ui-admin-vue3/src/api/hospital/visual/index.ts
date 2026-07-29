import request from '@/config/axios'

export const getHeatmap = () => request.get({ url: '/hospital/visual/heatmap' })
export const getDeptRadar = () => request.get({ url: '/hospital/visual/dept-radar' })
export const getPatientProfile = () => request.get({ url: '/hospital/visual/patient-profile' })
export const getDiseaseSeasonal = () => request.get({ url: '/hospital/visual/disease-seasonal' })
export const getMedicineCooccurrence = () => request.get({ url: '/hospital/visual/medicine-cooccurrence' })
