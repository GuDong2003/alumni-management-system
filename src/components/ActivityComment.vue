<template>
  <div class="comment-container">
    <!-- 评论搜索 -->
    <div class="comment-search">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索评论..."
        clearable
        @clear="handleSearch"
        @keyup.enter="handleSearch"
      >
        <template #append>
          <el-button @click="handleSearch">
            <el-icon><Search /></el-icon>
          </el-button>
        </template>
      </el-input>
    </div>

    <!-- 评论表单 -->
    <div class="comment-form">
      <el-input
        v-model="commentContent"
        type="textarea"
        :rows="3"
        placeholder="写下你的评论..."
        maxlength="500"
        show-word-limit
      />
      <div class="form-actions">
        <el-button type="primary" @click="submitComment" :loading="submitting">
          发表评论
        </el-button>
      </div>
    </div>

    <!-- 评论列表 -->
    <div class="comment-list">
      <div v-if="loading" class="loading">
        <el-skeleton :rows="3" animated />
      </div>
      <template v-else>
        <div v-if="comments.length === 0" class="empty">
          <el-empty description="暂无评论" />
        </div>
        <div v-else>
          <div
            v-for="comment in comments"
            :key="comment.id"
            class="comment-item"
          >
            <!-- 评论内容 -->
            <div class="comment-content">
              <div class="comment-header">
                <span class="username">{{ comment.userName }}</span>
                <span class="time">{{ formatTime(comment.createdAt) }}</span>
              </div>
              <div class="comment-text">{{ comment.content }}</div>
              <div class="comment-actions">
                <el-button
                  type="text"
                  @click="handleReply(comment)"
                  v-if="!comment.parentId"
                >
                  回复
                </el-button>
                <el-button
                  type="text"
                  @click="likeComment(comment)"
                  :class="{ 'liked': comment.hasLiked }"
                >
                  <el-icon><Star /></el-icon>
                  {{ comment.likes }}
                </el-button>
                <el-button
                  type="text"
                  @click="handleDelete(comment)"
                  v-if="canDelete(comment)"
                >
                  删除
                </el-button>
              </div>
            </div>

            <!-- 回复列表 -->
            <div v-if="comment.replies && comment.replies.length > 0" class="reply-list">
              <div
                v-for="reply in comment.replies"
                :key="reply.id"
                class="reply-item"
              >
                <div class="reply-content">
                  <div class="reply-header">
                    <span class="username">{{ reply.userName }}</span>
                    <span class="time">{{ formatTime(reply.createdAt) }}</span>
                  </div>
                  <div class="reply-text">{{ reply.content }}</div>
                  <div class="reply-actions">
                    <el-button
                      type="text"
                      @click="likeComment(reply)"
                      :class="{ 'liked': reply.hasLiked }"
                    >
                      <el-icon><Star /></el-icon>
                      {{ reply.likes }}
                    </el-button>
                    <el-button
                      type="text"
                      @click="handleDelete(reply)"
                      v-if="canDelete(reply)"
                    >
                      删除
                    </el-button>
                  </div>
                </div>
              </div>
            </div>

            <!-- 回复表单 -->
            <div v-if="replyingTo === comment.id" class="reply-form">
              <el-input
                v-model="replyContent"
                type="textarea"
                :rows="2"
                placeholder="回复评论..."
                maxlength="300"
                show-word-limit
              />
              <div class="form-actions">
                <el-button @click="cancelReply">取消</el-button>
                <el-button type="primary" @click="submitReply" :loading="submitting">
                  回复
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </template>
    </div>

    <!-- 分页 -->
    <div class="pagination" v-if="total > 0">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Star } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { formatTime } from '@/utils/time'
import axios from '@/utils/axios'

const props = defineProps({
  activityId: {
    type: Number,
    required: true
  }
})

const userStore = useUserStore()
const comments = ref([])
const loading = ref(false)
const submitting = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const searchKeyword = ref('')
const commentContent = ref('')
const replyContent = ref('')
const replyingTo = ref(null)

// 获取评论列表
const fetchComments = async () => {
  loading.value = true
  try {
    const response = await axios.get(`/api/activities/${props.activityId}/comments`)
    comments.value = response.data
  } catch (error) {
    ElMessage.error('获取评论失败')
    console.error('获取评论失败:', error)
  } finally {
    loading.value = false
  }
}

// 提交评论
const submitComment = async () => {
  if (!commentContent.value.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }

  submitting.value = true
  try {
    await axios.post(`/api/activities/${props.activityId}/comments`, {
      content: commentContent.value.trim()
    })
    ElMessage.success('评论发布成功')
    commentContent.value = ''
    fetchComments()
  } catch (error) {
    ElMessage.error('评论发布失败')
    console.error('评论发布失败:', error)
  } finally {
    submitting.value = false
  }
}

// 回复评论
const handleReply = (comment) => {
  replyingTo.value = comment.id
  replyContent.value = ''
}

const cancelReply = () => {
  replyingTo.value = null
  replyContent.value = ''
}

// 提交回复
const submitReply = async () => {
  if (!replyContent.value.trim()) {
    ElMessage.warning('请输入回复内容')
    return
  }

  submitting.value = true
  try {
    await axios.post(`/api/activities/${props.activityId}/comments`, {
      content: replyContent.value.trim(),
      parentId: replyingTo.value
    })
    ElMessage.success('回复发布成功')
    replyingTo.value = null
    replyContent.value = ''
    fetchComments()
  } catch (error) {
    ElMessage.error('回复发布失败')
    console.error('回复发布失败:', error)
  } finally {
    submitting.value = false
  }
}

// 点赞评论
const likeComment = async (comment) => {
  try {
    if (comment.hasLiked) {
      await axios.delete(`/api/activities/${props.activityId}/comments/${comment.id}/likes`)
    } else {
      await axios.post(`/api/activities/${props.activityId}/comments/${comment.id}/likes`)
    }
    await fetchComments()
  } catch (error) {
    console.error('点赞失败:', error)
    ElMessage.error('点赞失败')
  }
}

// 删除评论
const handleDelete = async (comment) => {
  try {
    await ElMessageBox.confirm('确定要删除这条评论吗？', '提示', {
      type: 'warning'
    })
    await axios.delete(`/api/activities/${props.activityId}/comments/${comment.id}`)
    ElMessage.success('删除成功')
    fetchComments()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
      console.error('删除评论失败:', error)
    }
  }
}

// 搜索评论
const handleSearch = () => {
  currentPage.value = 1
  fetchComments()
}

// 分页处理
const handleSizeChange = (val) => {
  pageSize.value = val
  fetchComments()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchComments()
}

// 检查删除权限
const canDelete = (comment) => {
  const userRole = userStore.role
  if (userRole === 'SUPER_ADMIN') {
    return true
  }
  if (userRole === 'ADMIN') {
    return comment.userId !== userStore.id
  }
  return comment.userId === userStore.id
}

onMounted(() => {
  fetchComments()
})
</script>

<style scoped>
.comment-container {
  padding: 20px;
}

.comment-search {
  margin-bottom: 20px;
}

.comment-form {
  margin-bottom: 20px;
}

.form-actions {
  margin-top: 10px;
  text-align: right;
}

.comment-list {
  margin-bottom: 20px;
}

.comment-item {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f9f9f9;
  border-radius: 4px;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 14px;
}

.username {
  font-weight: bold;
  color: #409EFF;
}

.time {
  color: #909399;
}

.comment-text {
  margin-bottom: 10px;
  line-height: 1.5;
}

.comment-actions {
  display: flex;
  gap: 10px;
}

.reply-list {
  margin-top: 15px;
  margin-left: 20px;
  padding-left: 20px;
  border-left: 2px solid #e4e7ed;
}

.reply-item {
  margin-bottom: 15px;
}

.reply-form {
  margin-top: 10px;
  margin-left: 20px;
}

.liked {
  color: #409EFF;
}

.loading {
  padding: 20px;
}

.empty {
  padding: 40px;
  text-align: center;
}

.pagination {
  margin-top: 20px;
  text-align: center;
}
</style> 