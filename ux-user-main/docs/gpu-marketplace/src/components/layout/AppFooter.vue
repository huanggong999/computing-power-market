<template>
  <footer class="app-footer">
    <div class="footer-content">
      <div class="footer-main">
        <div class="footer-section">
          <h4>关于我们</h4>
          <ul>
            <li><a href="#">公司介绍</a></li>
            <li><a href="#">联系我们</a></li>
            <li><a href="#">加入我们</a></li>
          </ul>
        </div>

        <div class="footer-section">
          <h4>产品服务</h4>
          <ul>
            <li><a href="/market">算力市场</a></li>
            <li><a href="/console">控制台</a></li>
            <li><a href="#">API文档</a></li>
          </ul>
        </div>

        <div class="footer-section">
          <h4>帮助支持</h4>
          <ul>
            <li><a href="/help">帮助中心</a></li>
            <li><a href="#">使用指南</a></li>
            <li><a href="#">常见问题</a></li>
          </ul>
        </div>

        <div class="footer-section contact-section">
          <h4>联系方式</h4>
          <div class="contact-info">
            <p><PhoneOutlined /> 400-888-8888</p>
            <p><MailOutlined /> support@gpumarket.com</p>
            <p><EnvironmentOutlined /> 北京市海淀区中关村科技园</p>
          </div>
          <div class="social-links">
            <a href="#" class="social-link"><WechatOutlined /></a>
            <a href="#" class="social-link"><WeiboOutlined /></a>
            <a href="#" class="social-link"><GithubOutlined /></a>
          </div>
        </div>
      </div>

      <div class="footer-bottom">
        <div class="footer-links">
          <a href="#">隐私政策</a>
          <a href="#">服务条款</a>
          <a href="#">Cookie政策</a>
          <a href="#">网站地图</a>
        </div>
        <p class="copyright">
          © {{ new Date().getFullYear() }} 算力市场. All rights reserved. <br/>
          ICP备案号: 京ICP备12345678号-1
        </p>
      </div>
    </div>
  </footer>
</template>

<script setup>
import {
  PhoneOutlined,
  MailOutlined,
  EnvironmentOutlined,
  WechatOutlined,
  WeiboOutlined,
  GithubOutlined,
} from '@ant-design/icons-vue'
</script>

<style scoped>
.app-footer {
  background: linear-gradient(135deg, rgba(0, 21, 41, 0.98), rgba(0, 33, 64, 0.98));
  border-top: 1px solid rgba(26, 58, 92, 0.6);
  padding: 60px 0 20px;
  margin-top: 40px;
}

.footer-content {
  max-width: 1440px;
  margin: 0 auto;
  padding: 0 24px;
}

.footer-main {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 40px;
  padding-bottom: 40px;
  border-bottom: 1px solid rgba(26, 58, 92, 0.4);
}

.footer-section h4 {
  color: var(--text-primary);
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 20px;
}

.footer-section ul {
  list-style: none;
}

.footer-section ul li {
  margin-bottom: 12px;
}

.footer-section ul li a {
  color: var(--text-secondary);
  font-size: 14px;
  transition: color 0.3s;
  text-decoration: none;
}

.footer-section ul li a:hover {
  color: var(--primary-color);
}

.contact-section {
  grid-column: span 2;
}

.contact-info p {
  color: var(--text-secondary);
  font-size: 14px;
  margin-bottom: 10px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.social-links {
  display: flex;
  gap: 16px;
  margin-top: 20px;
}

.social-link {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(22, 119, 255, 0.1);
  border-radius: 50%;
  color: var(--text-secondary);
  font-size: 18px;
  transition: all 0.3s;
}

.social-link:hover {
  background: var(--primary-color);
  color: white;
  transform: translateY(-2px);
}

.footer-bottom {
  padding-top: 20px;
  text-align: center;
}

.footer-links {
  display: flex;
  justify-content: center;
  gap: 30px;
  margin-bottom: 16px;
}

.footer-links a {
  color: var(--text-secondary);
  font-size: 14px;
  transition: color 0.3s;
  text-decoration: none;
}

.footer-links a:hover {
  color: var(--primary-color);
}

.copyright {
  color: var(--text-secondary);
  font-size: 13px;
  line-height: 1.8;
}

/* 响应式适配 */
@media (max-width: 1200px) {
  .footer-main {
    grid-template-columns: repeat(3, 1fr);
  }

  .contact-section {
    grid-column: span 1;
  }
}

@media (max-width: 768px) {
  .footer-main {
    grid-template-columns: repeat(2, 1fr);
    gap: 30px;
  }

  .footer-links {
    flex-wrap: wrap;
    gap: 15px;
  }
}

@media (max-width: 480px) {
  .footer-main {
    grid-template-columns: 1fr;
  }
}
</style>
