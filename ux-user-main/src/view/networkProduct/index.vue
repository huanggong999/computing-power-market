<template>
  <Banner :is-search="false" :banner-type="2" />
  <ShowBox title="SaaS标准版">
    <template v-slot:box>
      <div class="card-box">
        <div class="card-list-3">
          <ProductCard v-for="item in productList" :data="item" :key="item.id" :id="item.id" :form-id="item.formId"
            :name="item.name" :detail="item.detail" :referPrice="item.referPrice" :image="item.image"
            :crossedPrice="item.crossedPrice" :payPrice="item.payPrice" :pay-form-id="item.payFormId" :isIpCountDisplay="item.isIpCountDisplay"></ProductCard>
        </div>
      </div>
    </template>
  </ShowBox>
</template>

<script setup lang="ts" name="HomeView">
import { getNetWorkProductListAPI } from '@/api/networkProduct'
import { useTable } from '@/hooks/useTable'

const { tableData: productList, pageData, getList } = useTable({
  requestApi: getNetWorkProductListAPI,
  requestAuto: false
})
onMounted(() => {
  pageData.value.pageSize = 100
  getList()
})
</script>
<style lang="scss" scoped>
.b {
  height: 556px;
  padding-top: 100px;
  background: url('@/assets/images/home-top.png') no-repeat;
  background-size: 100%;
  z-index: 5;
}

.bg-circle {
  width: 1025px;
  height: 1025px;
  background: #89c9ff;
  opacity: 0.2;
  filter: blur(50px);
  position: absolute;
  top: 1822px;
  left: 448px;
  z-index: -1;
}

.card-box {
  .card-list-3 {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 34px;
  }

  .card-list-4 {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 58px;
    row-gap: 58px;
  }
}

.app-nav-box {
  margin: 41px 0 111px;

  .app-nav-list {
    display: flex;
    gap: 30px;

    img {
      width: 470px;
      height: 125px;
      background: radial-gradient(#d5e6ff 0%, #edf2ff 55%, #e6fdff 100%);
      border-radius: 16px 16px 16px 16px;
      border: 1px solid #ffffff;

      &:hover {
        cursor: pointer;
      }
    }
  }
}

.data-list {
  margin-bottom: 109px;
}
</style>
