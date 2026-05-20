<template>
  <footer class="footer">
    <div class="footer-container">
      <div class="footer-content">
        <div class="footer-section">
          <h4>关于我们</h4>
          <p>冰箱销售系统致力于为用户提供优质的冰箱产品和购物体验。</p>
        </div>
        
        <div class="footer-section">
          <h4>快速链接</h4>
          <ul>
            <li><router-link to="/home">首页</router-link></li>
            <li><router-link to="/products">商品列表</router-link></li>
            <li><router-link to="/cart">购物车</router-link></li>
          </ul>
        </div>
        
        <div class="footer-section">
          <h4>客户服务</h4>
          <ul>
            <li><a href="#">售后服务</a></li>
            <li><a href="#">配送说明</a></li>
            <li><a href="#">退换货政策</a></li>
          </ul>
        </div>
        
        <div class="footer-section">
          <h4>联系我们</h4>
          <p><el-icon><Phone /></el-icon> 客服热线：400-888-8888</p>
          <p><el-icon><Message /></el-icon> 邮箱：service@fridge.com</p>
          <p><el-icon><Location /></el-icon> 地址：北京市朝阳区xxx路xxx号</p>
        </div>
      </div>
      
      <div class="footer-bottom">
        <p>&copy; {{ currentYear }} 冰箱销售系统 版权所有</p>
      </div>
    </div>
  </footer>
</template>

<script setup>
import { computed } from 'vue'
import { Phone, Message, Location } from '@element-plus/icons-vue'

const currentYear = computed(() => new Date().getFullYear())
</script>

<style scoped>
.footer {
  background: linear-gradient(135deg, #0f172a 0%, #1e293b 100%);
  color: #bdc3c7;
  padding: 60px 0 30px;
  margin-top: auto;
}

.footer-container {
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 16px;
}

.footer-content {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1.5fr;
  gap: 48px;
  padding-bottom: 32px;
  border-bottom: 1px solid #334155;
}

.footer-section h4 {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 20px;
  color: #ffffff;
}

.footer-section p {
  font-size: 14px;
  line-height: 2;
  color: #94a3b8;
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
}

.footer-section ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.footer-section ul li {
  margin-bottom: 10px;
}

.footer-section ul li a {
  color: #94a3b8;
  font-size: 14px;
  transition: color 0.2s;
  text-decoration: none;
}

.footer-section ul li a:hover {
  color: #60a5fa;
}

.footer-bottom {
  text-align: center;
  padding-top: 30px;
}

.footer-bottom p {
  font-size: 14px;
  color: #64748b;
}

@media (max-width: 1024px) {
  .footer-content {
    grid-template-columns: repeat(2, 1fr);
    gap: 32px;
  }
}

@media (max-width: 768px) {
  .footer {
    padding: 40px 0 20px;
  }

  .footer-content {
    grid-template-columns: 1fr;
    gap: 24px;
  }

  .footer-section p {
    font-size: 13px;
  }
}
</style>
