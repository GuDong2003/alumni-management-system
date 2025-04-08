import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  server: {
    port: 9091,
    host: '0.0.0.0',  // 允许所有IP访问
    open: true,       // 自动打开浏览器
    proxy: {
      '/api': {
        target: 'http://localhost:9090',  // 后端API地址
        changeOrigin: true
      }
    }
  },
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  }
}) 