<template>
  <!-- 页脚组件 -->
  <!-- 显示站点版权信息 -->
  <div class="footer-bar">
    <!-- 页脚内容容器，限制最大宽度并居中 -->
    <div class="main-width footer-content">
      <!-- 版权声明文字 -->
      <p>&copy; 牢大商城 - JavaWeb课程实训项目</p>
    </div>
  </div>
</template>

<script setup>
/**
 * FooterBar页脚组件
 * 提供全站统一的底部信息展示，包含版权声明
 */
</script>

<style scoped>
/**
 * 页脚主样式
 */
.footer-bar {
  background: #f0f2f5;                                     /* 浅灰色背景 */
  border-top: 1px solid var(--border-color);                /* 顶部边框 */
  padding: 24px 0;                                         /* 上下内边距24px */
  margin-top: 40px;                                        /* 上外边距40px */
}

/**
 * 页脚内容样式
 */
.footer-content {
  text-align: center;                                       /* 文字居中 */
  color: var(--text-secondary);                             /* 次要文字颜色 */
  font-size: 13px;                                          /* 字号13px */
}

/**
 * 段落样式重置
 */
.footer-content p {
  margin: 0;                                               /* 移除默认边距 */
}
</style>
