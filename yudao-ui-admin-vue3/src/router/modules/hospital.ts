import { Layout } from '@/utils/routerHelper'

const { t } = useI18n()

const hospitalRoutes = {
  path: '/hospital',
  component: Layout,
  redirect: '/hospital/register',
  name: 'Hospital',
  meta: {
    title: '医院管理',
    icon: 'ep:hospital',
    alwaysShow: true
  },
  children: [
    // ========== 诊疗业务 ==========
    {
      path: 'register',
      component: () => import('@/views/hospital/register/index.vue'),
      name: 'HospitalRegister',
      meta: {
        title: '挂号管理',
        icon: 'ep:tickets',
        noCache: false,
        roles: ['admin', 'doctor']
      }
    },
    {
      path: 'my-registration',
      component: () => import('@/views/hospital/my-registration/index.vue'),
      name: 'HospitalMyRegistration',
      meta: {
        title: '我的挂号',
        icon: 'ep:list',
        noCache: false,
        roles: ['doctor']
      }
    },
    {
      path: 'prescription',
      component: () => import('@/views/hospital/prescription/index.vue'),
      name: 'HospitalPrescription',
      meta: {
        title: '开具处方',
        icon: 'ep:edit',
        noCache: false,
        roles: ['doctor']
      }
    },
    {
      path: 'prescription-list',
      component: () => import('@/views/hospital/prescription-list/index.vue'),
      name: 'HospitalPrescriptionList',
      meta: {
        title: '处方列表',
        icon: 'ep:document',
        noCache: false,
        roles: ['admin', 'doctor', 'patient']
      }
    },
    // ========== 基础数据管理 (管理员) ==========
    {
      path: 'department',
      component: () => import('@/views/hospital/department/index.vue'),
      name: 'HospitalDepartment',
      meta: {
        title: '科室管理',
        icon: 'ep:office-building',
        noCache: false,
        roles: ['admin']
      }
    },
    {
      path: 'doctor',
      component: () => import('@/views/hospital/doctor/index.vue'),
      name: 'HospitalDoctor',
      meta: {
        title: '医生管理',
        icon: 'ep:user',
        noCache: false,
        roles: ['admin']
      }
    },
    {
      path: 'ward',
      component: () => import('@/views/hospital/ward/index.vue'),
      name: 'HospitalWard',
      meta: {
        title: '病房管理',
        icon: 'ep:home-filled',
        noCache: false,
        roles: ['admin']
      }
    },
    {
      path: 'bed',
      component: () => import('@/views/hospital/bed/index.vue'),
      name: 'HospitalBed',
      meta: {
        title: '床位管理',
        icon: 'ep:grid',
        noCache: false,
        roles: ['admin']
      }
    },
    {
      path: 'medicine',
      component: () => import('@/views/hospital/medicine/index.vue'),
      name: 'HospitalMedicine',
      meta: {
        title: '药品管理',
        icon: 'ep:first-aid-kit',
        noCache: false,
        roles: ['admin']
      }
    },
    // ========== 业务管理 (管理员) ==========
    {
      path: 'patient',
      component: () => import('@/views/hospital/patient/index.vue'),
      name: 'HospitalPatient',
      meta: {
        title: '病人管理',
        icon: 'ep:avatar',
        noCache: false,
        roles: ['admin']
      }
    },
    {
      path: 'visit',
      component: () => import('@/views/hospital/visit/index.vue'),
      name: 'HospitalVisit',
      meta: {
        title: '就诊管理',
        icon: 'ep:notebook',
        noCache: false,
        roles: ['admin']
      }
    },
    {
      path: 'bill',
      component: () => import('@/views/hospital/bill/index.vue'),
      name: 'HospitalBill',
      meta: {
        title: '账单管理',
        icon: 'ep:money',
        noCache: false,
        roles: ['admin']
      }
    },
    {
      path: 'stats',
      component: () => import('@/views/hospital/stats/index.vue'),
      name: 'HospitalStats',
      meta: {
        title: '数据统计',
        icon: 'ep:data-line',
        noCache: false,
        roles: ['admin']
      }
    },
    // ========== 院长驾驶舱 (管理员) ==========
    {
      path: 'dashboard',
      component: () => import('@/views/hospital/dashboard/index.vue'),
      name: 'HospitalDashboard',
      meta: {
        title: '院长驾驶舱',
        icon: 'ep:data-analysis',
        noCache: false,
        roles: ['admin']
      }
    }
  ]
}

export default hospitalRoutes
