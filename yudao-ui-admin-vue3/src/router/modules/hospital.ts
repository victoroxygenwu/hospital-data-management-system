import { Layout } from '@/utils/routerHelper'

const { t } = useI18n()

const hospitalRoutes = {
  path: '/hospital',
  component: Layout,
  redirect: '/hospital/register',
  name: 'Hospital',
  meta: {
    title: '诊疗管理',
    icon: 'ep:hospital',
    alwaysShow: true
  },
  children: [
    {
      path: 'register',
      component: () => import('@/views/hospital/register/index.vue'),
      name: 'HospitalRegister',
      meta: {
        title: '挂号管理',
        icon: 'ep:tickets',
        noCache: false
      }
    },
    {
      path: 'my-registration',
      component: () => import('@/views/hospital/my-registration/index.vue'),
      name: 'HospitalMyRegistration',
      meta: {
        title: '我的挂号',
        icon: 'ep:list',
        noCache: false
      }
    },
    {
      path: 'prescription',
      component: () => import('@/views/hospital/prescription/index.vue'),
      name: 'HospitalPrescription',
      meta: {
        title: '开具处方',
        icon: 'ep:edit',
        noCache: false
      }
    },
    {
      path: 'prescription-list',
      component: () => import('@/views/hospital/prescription-list/index.vue'),
      name: 'HospitalPrescriptionList',
      meta: {
        title: '处方列表',
        icon: 'ep:document',
        noCache: false
      }
    },
    {
      path: 'dashboard',
      component: () => import('@/views/hospital/dashboard/index.vue'),
      name: 'HospitalDashboard',
      meta: {
        title: '院长驾驶舱',
        icon: 'ep:data-analysis',
        noCache: false
      }
    }
  ]
}

export default hospitalRoutes
