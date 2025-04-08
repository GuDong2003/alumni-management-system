import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { formatDateTime } from '@/utils/date'
import axios from '@/utils/axios'
import ActivityComment from '@/components/ActivityComment.vue'

const fetchActivities = async () => {
  loading.value = true
  try {
    const params = new URLSearchParams()
    
    // 添加分页参数
    params.append('page', currentPage.value - 1) 
    params.append('size', pageSize.value)
    
    const response = await axios.get(`/api/activities?${params.toString()}`)
    
    if (response.data && response.data.content !== undefined) {
      // Spring Data 分页响应格式
      activities.value = await processActivitiesData(response.data.content)
      total.value = response.data.totalElements || 0
    } else if (Array.isArray(response.data)) {
      // 非分页响应格式，需要手动处理分页
      const allActivities = await processActivitiesData(response.data)
      total.value = allActivities.length
      
      // 手动分页
      const startIndex = (currentPage.value - 1) * pageSize.value
      const endIndex = startIndex + pageSize.value
      activities.value = allActivities.slice(startIndex, endIndex)
    } else {
      console.error('接口返回的数据格式不正确:', response.data)
      activities.value = []
      total.value = 0
    }
  } catch (error) {
    console.error('获取活动列表失败:', error)
    ElMessage.error(error.message || '获取活动列表失败')
    activities.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该活动吗？', '提示', {
      type: 'warning'
    })
    
    await axios.delete(`/api/activities/${row.id}`)
    
    ElMessage.success('删除成功')
    fetchActivities()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除活动失败:', error)
      ElMessage.error(error.message || '删除活动失败')
    }
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    const url = dialogType.value === 'add' ? '/api/activities' : `/api/activities/${form.value.id}`
    const method = dialogType.value === 'add' ? 'post' : 'put'
    
    await axios({
      url,
      method,
      data: form.value
    })
    
    ElMessage.success(dialogType.value === 'add' ? '添加成功' : '更新成功')
    dialogVisible.value = false
    fetchActivities()
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error(error.message || '提交失败')
  }
}

const handleSearch = async () => {
  currentPage.value = 1;
  loading.value = true
  try {
    const params = new URLSearchParams()
    
    params.append('page', currentPage.value - 1)
    params.append('size', pageSize.value)
    
    if (searchForm.value.title) {
      params.append('title', searchForm.value.title)
    }
    if (searchForm.value.type) {
      params.append('type', searchForm.value.type)
    }
    if (searchForm.value.status) {
      params.append('status', searchForm.value.status)
    }
    if (searchForm.value.dateRange && searchForm.value.dateRange.length === 2) {
      const startDate = new Date(searchForm.value.dateRange[0])
      const endDate = new Date(searchForm.value.dateRange[1])
      startDate.setHours(0, 0, 0)
      endDate.setHours(23, 59, 59)
      
      params.append('startTime', startDate.toISOString().slice(0, 19))
      params.append('endTime', endDate.toISOString().slice(0, 19))
    }
    
    // 根据是否有搜索条件决定使用哪个接口
    const url = hasSearchConditions() 
      ? `/api/activities/search` 
      : `/api/activities`;
    
    const response = await axios.get(`${url}?${params.toString()}`)
    
    if (response.data && response.data.content !== undefined) {
      activities.value = await processActivitiesData(response.data.content)
      total.value = response.data.totalElements || 0
    } else if (Array.isArray(response.data)) {
      // 非分页响应格式，需要手动处理分页
      const allActivities = await processActivitiesData(response.data)
      total.value = allActivities.length
      
      // 手动分页
      const startIndex = (currentPage.value - 1) * pageSize.value
      const endIndex = startIndex + pageSize.value
      activities.value = allActivities.slice(startIndex, endIndex)
    } else {
      console.error('接口返回的数据格式不正确:', response.data)
      activities.value = []
      total.value = 0
    }
  } catch (error) {
    console.error('搜索活动失败:', error)
    ElMessage.error(error.message || '搜索活动失败')
    activities.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
} 