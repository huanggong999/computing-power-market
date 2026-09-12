import { HOME_URL, LOGIN_PAGE_LIST, LOGIN_URL } from '@/config'
import { createRouter, createWebHashHistory } from 'vue-router'

import NProgress from '@/config/nprogress'
import { useAuth, useUserInfo } from '@/store'
import { getToken } from '@/utils/auth'
import { initDynamicRouter } from './dynamicRouter'

let routes = [
  {
    path: '/',
    redirect: HOME_URL,
  },
  {
    path: HOME_URL,
    name: 'Home',
    component: () => import('@/view/Home/index.vue'),
    children: [
      {
        path: '',
        name: 'HomeView',
        component: () => import('@/view/homeView.vue'),
      },
      {
        path: '/application',
        name: 'Application',
        component: () => import('@/view/Application/index.vue'),
        children: [
          {
            path: '',
            name: 'applicationList',
            component: () => import('@/view/Application/List/index.vue'),
          },
          {
            path: '/application/detail',
            name: 'applicationDetail',
            component: () => import('@/view/Application/detail/index.vue'),
          },
        ],
      },
      {
        path: '/model',
        name: 'model',
        component: () => import('@/view/modelList/index.vue'),
        children: [
          {
            path: '',
            name: 'modelList',
            component: () => import('@/view/modelList/list/index.vue'),
          },
          {
            path: '/model/detail',
            name: 'modelDetail',
            component: () => import('@/view/modelList/detail/index.vue'),
          },
        ],
      },
      {
        path: '/data',
        name: 'data',
        component: () => import('@/view/dataList/index.vue'),
        children: [
          {
            path: '',
            name: 'dataList',
            component: () => import('@/view/dataList/list/index.vue'),
          },
          {
            path: '/data/detail',
            name: 'dataDetail',
            component: () => import('@/view/dataList/detail/index.vue'),
          },
        ],
      },
      {
        path: '/news',
        name: 'news',
        component: () => import('@/view/news/index.vue'),
        children: [
          {
            path: '',
            name: 'newsList',
            component: () => import('@/view/news/list/index.vue'),
          },
          {
            path: '/news/detail',
            name: 'newsDetail',
            component: () => import('@/view/news/detail/index.vue'),
          },
        ],
      },
      {
        path: '/computeList',
        name: 'computeList',
        component: () => import('@/view/computeList/index.vue'),
      },
        {
        path: '/diycomputeList',
        name: 'diycomputeList',
        component: () => import('@/view/computeList/diyCom.vue'),
      },
      {
        path: '/computeListNew',
        name: 'computeListNew',
        component: () => import('@/view/computeList/new.vue'),
      },
      {
        path: '/computeRent',
        name: 'computeRent',
        component: () => import('@/view/computeRent/index.vue'),
      },
      {
        path: '/networkProduct',
        name: 'networkProduct',
        component: () => import('@/view/networkProduct/index.vue'),
      },
      {
        path: '/networkProductDetail',
        name: 'networkProductDetail',
        component: () => import('@/view/networkProductDetail/index.vue'),
      },
      {
        path: '/networkProductForm',
        name: 'networkProductForm',
        component: () => import('@/view/networkProductForm/index.vue'),
      },
      {
        path: '/aiModel',
        name: 'aiModel',
        component: () => import('@/view/largeModel/index.vue'),
      },
      {
        path: '/modelZoo',
        name: 'modelZoo',
        component: () => import('@/view/modelZoo/index.vue'),
      },
      {
        path: '/modelscope',
        name: 'modelscope',
        component: () => import('@/view/modelZoo/modelscope.vue'),
      },
      {
        path: '/partner',
        name: 'partner',
        component: () => import('@/view/partner/index.vue'),
      },
      {
        path: '/helpDocs',
        name: 'helpDocs',
        component: () => import('@/view/helpDocs/index.vue'),
      },
      {
        path: '/invite',
        name: 'invite',
        component: () => import('@/view/invite/index.vue'),
      },
      {
        path: '/activityCenter',
        name: 'activityCenter',
        component: () => import('@/view/activityCenter/index.vue'),
        children: [
          {
            path: '',
            name: 'activityCenter',
            component: () => import('@/view/activityCenter/list/index.vue'),
          },
          {
            path: '/activityCenter/detail',
            name: 'activityDetail',
            component: () => import('@/view/activityCenter/detail/index.vue'),
          },
        ],
      },

      {
        path: '/activity',
        name: 'activity',
        component: () => import('@/view/activity/index.vue'),
      },
    ],
  },

  {
    path: LOGIN_URL,
    name: 'Login',
    component: () => import('@/view/Login/index.vue'),
  },
  {
    path: '/docsView/productAgreement',
    name: 'productAgreement',
    component: () => import('@/view/docsView/productAgreement.vue'),
  },
  {
    path: '/docsView/privacyPolicy',
    name: 'privacyPolicy',
    component: () => import('@/view/docsView/privacyPolicy.vue'),
  },
  {
    path: '/docsView/TermsOfService',
    name: 'TermsOfService',
    component: () => import('@/view/docsView/TermsOfService.vue'),
  },
  {
    path: '/docsView/InformationControl',
    name: 'InformationControl',
    component: () => import('@/view/docsView/InformationControl.vue'),
  },
  {
    path: '/layout',
    name: 'layout',
    component: () => import('@/layout/index.vue'),
    redirect: '/console',
    children: [
      {
        path: '/cloud/instance',
        name: 'InstanceList',
        component: () => import('@/view/Cloud/InstanceList/index.vue'),
        meta: {
          title: '容器实例',
          icon: 'Monitor',
          keepAlive: true
        }
      },
      {
        path: '/cloud/gpuInstance',
        name: 'GpuInstanceList',
        component: () => import('@/view/Cloud/InstanceList/index.vue'),
        meta: {
          title: 'GPU实例列表',
          icon: 'Monitor',
          keepAlive: true
        }
      }
    ],
  },
  {
    // 配置404页面
    path: '/:catchAll(.*)',
    name: '404',
    component: () => import('@/components/ErrorMessage/404.vue'),
  },
]
// 路由
const router = createRouter({
  history: createWebHashHistory(),
  routes,
})

// 白名单路由
const whiteList = ['/aiModel', '/networkProduct', '/networkProductDetail']
router.beforeEach(async (to, _, next) => {
  const User = useUserInfo()
  const Auth = useAuth()
  const Token = getToken()
  NProgress.start()
  if (whiteList.includes(to.path) && !User.IsUserLogin) {
    if (_.path === '/login') {
      return next('/home')
    }
  }
  if (!User.IsUserLogin && LOGIN_PAGE_LIST.includes(to.path)) {
    return next(LOGIN_URL)
  }
  // 公开官网页面（如算力市场）不依赖控制台动态菜单。
  // 只有进入 layout 下的控制台路由时才初始化动态路由，避免令牌过期时
  // 访问公开页面被 /pc/customer/info 的 401 误导向登录页。
  // The console entry itself is registered from the user's menu. When coming
  // from a public page such as the GPU marketplace, /console initially matches
  // the catch-all 404 route, so initialize dynamic routes before resolving it.
  const needsDynamicRoutes = to.path === '/console' || to.matched.some(
    (route) => route.name === 'layout' || route.path === '/layout' || route.name === '404'
  )
  if (Token && !Auth.authMenuList.length && needsDynamicRoutes) {
    await initDynamicRouter()
    return next(to.fullPath)
  }
  next()
})
router.afterEach(() => {
  NProgress.done()
})
// 导出
export default router
