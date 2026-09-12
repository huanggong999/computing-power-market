<template>
  <div class="model-page">
    <div class="left">
      <div class="title mb14">文档中心</div>
      <div class="search-part">
        <el-input :prefix-icon="Search" plain class="mb20" v-model="docsListSearch" placeholder="搜索目录文档标题"></el-input>
        <el-button @click="searchDocs">搜索</el-button>
      </div>

      <el-tree style="max-width: 600px" :data="docsList" @node-click="handleNodeClick" :props="defaultProps"
        node-key="id" />
    </div>
    <div class="middle">
      <Breadcrumb :router-list="routerList"></Breadcrumb>
      <div class="docs-card" v-show="targetDocs.name">
        <div class="title">{{ targetDocs.name }}</div>
        <div class="time">
          最近更新时间: {{ targetDocs.createTime }} ·首次发布时间:
          {{ targetDocs.updateTime }}
        </div>
      </div>
      <v-md-preview ref="previewRef" :text="targetDocs.introduce" height="700"></v-md-preview>
    </div>
    <div class="right">
      <!-- <el-input
        :prefix-icon="Search"
        plain
        class="mb20"
        v-model="catalogSearch"
        placeholder="搜索目录文档标题"
      ></el-input> -->
      <div class="catalog">
        <div class="title">本页目录</div>
        <div ref="directoryRef">
          <div v-for="anchor in catalogList" :key="anchor.id" :style="{
            padding: `5px 0 5px ${anchor.indent * 20}px`,
            color: anchor.indent === 0 ? 'black' : '#83889D',
            'font-size': '16px',
          }" @click="directoryClick(anchor)" class="directory-item" :id="anchor.id">
            <span v-if="anchor.indent === 0"></span> {{ anchor.title }}
          </div>
        </div>
      </div>
    </div>
    <div class="bg-div"></div>
  </div>
</template>

<script setup lang="ts" name="aiModel">
import { getDocsDetailByIdApi, getDocsTypeListApi } from '@/api/docs'
import { IDocsDetail } from '@/api/types/docs'
import { Search } from '@element-plus/icons-vue'
import { nextTick, onMounted } from 'vue'
import { useRoute } from 'vue-router'
// 获取文档路由参数
const route = useRoute()
watch(() => route.query, (q) => {
  if (q.id) {
    getDocsDetailByIdApi(q.id).then((res) => {
      targetDocs.value = res.data
      nextTick(() => directoryInit())
    })
  }

}, {
  immediate: true,
});

// 左-搜索、文档列表
const docsListSearch = ref()
const docsList: any = ref()
// const filteredData = computed(() => {
//   const filterNodes = (nodes: any[]): any[] => {
//     return nodes
//       .filter((node) => {
//         return node.name.toLowerCase().includes(docsListSearch.value)
//       })
//       .map((node) => {
//         if (node.children) {
//           node.children = filterNodes(node.children)
//         }
//         return node
//       })
//   }
//   return filterNodes(docsList)
// })
const customNodeClass = ({ id }: any) => {
  return id === selectedDocs.value ? 'selected' : ''
}
const defaultProps = {
  children: 'children',
  label: 'name',
  // class: customNodeClass,
}
const selectedDocs = ref()
let lastSelectedNode = ref()
const handleNodeClick = (node: any) => {
  console.log(node)
  if (!node.children) {
    selectedDocs.value = node.id
    const selectedNode = document.querySelector(
      `.el-tree-node[data-key="${node.id}"]`
    )
    console.log(selectedNode)
    if (selectedNode) {
      if (lastSelectedNode.value) {
        lastSelectedNode.value.style.color = '' // 清除颜色样式
      }
      let child = selectedNode.querySelector('.el-tree-node__label')
      child && (child.style.color = '#3972fd')
      lastSelectedNode.value = child
    }
    getDocsDetailByIdApi(node.id).then((res) => {
      targetDocs.value = res.data
      nextTick(() => directoryInit())
    })
  }
}
// 搜索文档树
const searchDocs = () => {
  getDocsTypeListApi({ name: docsListSearch.value }).then((res: any) => {
    docsList.value = res.data
  })
}
// 中-md文档
let previewRef = ref()
const routerList = ref([
  { name: '首页', path: '/home' },
  { name: '文档中心', path: '' },
])
const targetDocs = ref<IDocsDetail>({
  name: '',
  typeId: 0,
})

// 右-搜索、目录
interface ICatalogTitle {
  id: string
  title?: string
  lineIndex?: string
  indent: number
  pixel?: number
}
const catalogSearch = ref('')
let catalogList = ref<ICatalogTitle[]>([])
let directoryId = ref('')
//初始化目录树
const directoryInit = () => {
  const anchors = previewRef.value.$el.querySelectorAll('h1,h2,h3,h4,h5,h6')
  const arr = Array.from(anchors).filter(
    (title: any) => !!title!.innerText.trim()
  )

  if (!arr.length) {
    catalogList.value = []
    return
  }
  const hTags = Array.from(
    new Set(arr.map((title: any) => title.tagName))
  ).sort()

  catalogList.value = arr.map((el: any) => ({
    id: 'directory-' + el.getAttribute('data-v-md-line'),
    title: el.innerText,
    lineIndex: el.getAttribute('data-v-md-line'),
    indent: hTags.indexOf(el.tagName),
    // pixel: el.getBoundingClientRect().top - 60,
  }))
}
//目录点击事件
const directoryClick = (anchor: any) => {
  const { lineIndex } = anchor
  const heading = previewRef.value.$el.querySelector(
    `[data-v-md-line="${lineIndex}"]`
  )
  // 滚动浏览器视窗至详情区域
  // window.scrollTo({
  //   top: 400,
  //   behavior: 'smooth',
  // })
  // 滚动详情滚动条至目标标题处
  if (heading) {
    // removeScrollEventListener()
    directoryId.value = anchor.id
    previewRef.value.scrollToTarget({
      target: heading,
      scrollContainer: document.querySelector('.main-container'),
      top: 60,
    })
  }
}

// 获取文档内容
// 初始化页面数据(先加载第一篇文档)
const initPage = () => {
  getDocsTypeListApi().then((res: any) => {
    docsList.value = res.data
  })
}
onMounted(async () => {
  initPage()
  // getApplication(id.value)
  // 初始化目录
  // setTimeout(() => {
  //   directoryInit()
  // }, 1500)
  // addScrollEventListener()
})
</script>
<style lang="scss" scoped>
@import './index.scss';
</style>
