<template>
  <div class="dashboard-container">
    <el-card class="dashboard-card">
      <template #header>
        <div class="card-header">
          <h2>欢迎使用校友管理系统</h2>
        </div>
      </template>
      <div class="dashboard-content">
        <el-image
          src="https://example.com/your-image.jpg"
          fit="cover"
          class="dashboard-image"
        >
          <template #error>
            <div class="image-placeholder">
              <el-icon><Picture /></el-icon>
              <span>图片加载失败</span>
            </div>
          </template>
        </el-image>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { Picture } from '@element-plus/icons-vue'
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}

.dashboard-card {
  margin-bottom: 20px;
}

.card-header {
  text-align: center;
}

.dashboard-content {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
}

.dashboard-image {
  width: 100%;
  max-width: 800px;
  height: auto;
  border-radius: 8px;
}

.image-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 400px;
  background-color: #f5f7fa;
  color: #909399;
}

.image-placeholder .el-icon {
  font-size: 48px;
  margin-bottom: 10px;
}
</style> 