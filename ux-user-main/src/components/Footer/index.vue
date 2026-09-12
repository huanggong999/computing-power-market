<template>
  <footer class="footer pt40 pb30">
    <div class="footer-top">
      <div class="link-box">
        <div class="link">
          <div class="p1">社区服务</div>
          <div class="p2 url" @click="toLocalPage('/application')">应用</div>
          <div class="p2 url" @click="toLocalPage('/computeList')">算力</div>
          <div class="p2 url" @click="toLocalPage('/news')">新闻中心</div>
          <div class="p2 url" @click="toLocalPage('/activityCenter')">活动中心</div>
        </div>
        <div class="link">
          <div class="p1">应用中心</div>
          <div class="p2 url" @click="toLocalPage('/networkProduct')">AGI-C</div>
          <div class="p2 url" @click="toLocalPage('/aiModel')">AI 智算大模型</div>

        </div>
        <div class="link">
          <div class="p1">帮助中心</div>
          <div class="p2 url" @click="toLocalPage('/helpDocs')">帮助文档</div>

        </div>
        <div class="link">
          <div class="p1">生态伙伴</div>
          <a class="p2 url no-style" href="https://www.volcengine.com/" target="_blank">火山引擎</a>
          <div class="p2 url" @click="toLocalPage('/partner')">合作伙伴</div>
        </div>
      </div>
      <div class="info-box">
        <div class="left">
          <!-- <div class="info">
            <div class="p2">业务咨询</div>
            <div class="p1">huangyuhua@eyun.ai</div>
          </div> -->
          <div class="info">
            <div class="p2">联系我们</div>
            <div class="p1">www.eyun.ai</div>
            <div class="p1">contact@eyun.ai</div>
          </div>
          <!-- <div class=x"info">
            <div class="p2">公司地址</div>
            <div class="p1">
              深圳市福田区福田街道彩田路3069号星河世纪A栋3602
            </div>
          </div> -->
        </div>
        <div class="imgs">
          <div class="code">
            <img src="../../assets/images/tech-service.png" />
            <div class="tips">技术联系客服</div>
          </div>
          <div class="code">
            <img src="../../assets/images/business-service.png" />
            <div class="tips">商务联系客服</div>
          </div>
          <div class="code">
            <img src="../../assets/images/gzh.jpg" />
            <div class="tips">公众号</div>
          </div>
        </div>
      </div>
    </div>
    <div class="footer-bottom flx-center">
      © 逸云数智科技（深圳）有限公司版权所有
      <a class="ml30 no-style" href="https://beian.miit.gov.cn/" target="_blank">
        粤ICP备2024324197号-2</a>
    </div>
  </footer>
</template>

<script setup lang="ts" name="Footer">
import { toPage } from '@/utils'
const toLocalPage = (url: string) => {
  toPage(url)
  window.scrollTo({ top: 0, behavior: 'smooth' })
}
</script>
<style lang="scss" scoped>
.footer {
  margin-top: 50px;
  background: #f7f8fb;
  height: 270px;
  display: flex;
  flex-direction: column;
  padding: 0 !important;
  width: 100vw;

  .footer-top {
    height: 214px;
    display: grid;
    grid-template-columns: 725px 1fr;
    flex-direction: row;
    box-sizing: border-box;
    padding: 40px 225px 14px;

    .link-box {
      display: flex;
      justify-content: space-between;
      border-right: 1px solid #eaeaea;
      padding-right: 112px;

      .link {
        display: flex;
        flex-direction: column;
        gap: 15px;
      }
    }

    .info-box {
      padding-left: 112px;
      display: flex;
      gap: 58px;

      .left {
        display: flex;
        flex-direction: column;
        gap: 10px;

        .info {
          display: flex;
          flex-direction: column;
          gap: 8px;
        }
      }

      .imgs {
        display: flex;
        gap: 34px;

        .code {
          display: flex;
          flex-direction: column;
          justify-content: center;
          align-items: center;

          img {
            width: 124px;
            height: 124px;
            background: #ffffff;
            border-radius: 12px 12px 12px 12px;
          }

          .tips {
            margin-top: 12px;
            font-size: 14px;
            color: #000000;
          }
        }
      }
    }
  }

  .footer-bottom {
    height: 56px;

    color: #666666;
    font-size: 14px;
    text-align: center;
  }

  .p1 {
    font-family: PingFang SC, PingFang SC;
    font-weight: 500;
    font-size: 16px;
    color: #000000;
  }

  .p2 {
    font-family: PingFang SC, PingFang SC;
    font-weight: 400;
    font-size: 14px;
    color: #666666;
  }

  .url {
    cursor: pointer;

    &:hover {
      color: #3972fd;
    }
  }

  a.no-style {
    text-decoration: none;
    /* 去除下划线 */

    /* 继承父元素的颜色，或者你可以设置为任何你想要的颜色 */
  }
}
</style>
