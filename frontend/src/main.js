/**
 * 应用入口文件
 * 负责初始化Vue应用、注册插件、挂载根组件
 */
import { createApp } from 'vue'                           // 引入Vue的createApp方法，用于创建应用实例
import ElementPlus from 'element-plus'                   // 引入Element Plus组件库
import 'element-plus/dist/index.css'                     // 引入Element Plus全局样式
import * as ElementPlusIconsVue from '@element-plus/icons-vue' // 引入Element Plus所有图标组件
import App from './App.vue'                              // 引入根组件App.vue
import router from './router'                            // 引入路由配置
import { createPinia } from 'pinia'                      // 引入Pinia状态管理库
import './assets/styles/global.css'                      // 引入全局自定义样式

// 创建Vue应用实例
const app = createApp(App)

// 遍历并注册所有Element Plus图标组件到全局，便于在模板中直接使用
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 注册Element Plus组件库
app.use(ElementPlus)
// 注册Pinia状态管理
app.use(createPinia())
// 注册路由
app.use(router)
// 将应用挂载到index.html中的#app元素上
app.mount('#app')
