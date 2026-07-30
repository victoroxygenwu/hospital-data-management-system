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
    },
    // ========== 模块一：知识图谱 (管理员) ==========
    {
      path: 'knowledge',
      name: 'HospitalKnowledge',
      meta: {
        title: '知识图谱',
        icon: 'ep:connection',
        alwaysShow: true,
        roles: ['admin']
      },
      children: [
        {
          path: 'disease',
          component: () => import('@/views/hospital/disease/index.vue'),
          name: 'HospitalDisease',
          meta: { title: '疾病字典', icon: 'ep:first-aid-kit', roles: ['admin'] }
        },
        {
          path: 'symptom',
          component: () => import('@/views/hospital/symptom/index.vue'),
          name: 'HospitalSymptom',
          meta: { title: '症状字典', icon: 'ep:warning', roles: ['admin'] }
        },
        {
          path: 'kg-relations',
          component: () => import('@/views/hospital/kg-relations/index.vue'),
          name: 'HospitalKgRelations',
          meta: { title: '关联管理', icon: 'ep:share', roles: ['admin'] }
        },
        {
          path: 'knowledge-graph',
          component: () => import('@/views/hospital/knowledge-graph/index.vue'),
          name: 'HospitalKnowledgeGraph',
          meta: { title: '图谱可视化', icon: 'ep:data-line', roles: ['admin'] }
        },
        {
          path: 'ai-assist-diagnosis',
          component: () => import('@/views/hospital/ai-assist-diagnosis/index.vue'),
          name: 'HospitalAiAssistDiagnosis',
          meta: { title: 'AI 辅助诊断', icon: 'ep:magic-stick', roles: ['admin'] }
        },
        {
          path: 'ai-review',
          component: () => import('@/views/hospital/ai-review/index.vue'),
          name: 'HospitalAiReview',
          meta: { title: 'AI 处方审核', icon: 'ep:document-checked', roles: ['admin'] }
        }
      ]
    },
    // ========== 模块二：数据看板 (管理员) ==========
    {
      path: 'hospital-visual',
      name: 'HospitalVisual',
      meta: {
        title: '数据看板',
        icon: 'ep:data-line',
        alwaysShow: true,
        roles: ['admin']
      },
      children: [
        {
          path: 'heatmap',
          component: () => import('@/views/hospital/visual/heatmap/index.vue'),
          name: 'HospitalVisualHeatmap',
          meta: { title: '接诊热力图', icon: 'ep:calendar', roles: ['admin'] }
        },
        {
          path: 'dept-radar',
          component: () => import('@/views/hospital/visual/dept-radar/index.vue'),
          name: 'HospitalVisualDeptRadar',
          meta: { title: '科室雷达图', icon: 'ep:pie-chart', roles: ['admin'] }
        },
        {
          path: 'patient-profile',
          component: () => import('@/views/hospital/visual/patient-profile/index.vue'),
          name: 'HospitalVisualPatientProfile',
          meta: { title: '患者画像', icon: 'ep:user', roles: ['admin'] }
        },
        {
          path: 'disease-seasonal',
          component: () => import('@/views/hospital/visual/disease-seasonal/index.vue'),
          name: 'HospitalVisualDiseaseSeasonal',
          meta: { title: '疾病趋势', icon: 'ep:trend-charts', roles: ['admin'] }
        },
        {
          path: 'medicine-cooccurrence',
          component: () => import('@/views/hospital/visual/medicine-cooccurrence/index.vue'),
          name: 'HospitalVisualMedicineCooccurrence',
          meta: { title: '药品关联', icon: 'ep:connection', roles: ['admin'] }
        }
      ]
    }
  ]
}

export default hospitalRoutes
