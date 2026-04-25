<template>
  <div class="history-page">
    <div class="page-header">
      <h1>最近播放</h1>
      <el-button 
        type="primary" 
        :disabled="history.length === 0"
        @click="playAll"
      >
        <el-icon><VideoPlay /></el-icon>
        播放全部
      </el-button>
    </div>
    
    <div v-if="loading" class="loading-container">
      <el-icon class="loading-icon" :size="48"><Loading /></el-icon>
      <p>加载中...</p>
    </div>
    
    <template v-else>
      <SongTable 
        v-if="history.length > 0"
        :songs="history"
      />
      
      <el-empty v-else description="暂无播放记录，快去听歌吧" />
    </template>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { usePlayerStore } from '@/stores/player'
import { useMainStore } from '@/stores/main'
import { historyApi } from '@/api'
import { ElMessage } from 'element-plus'
import SongTable from '@/components/SongTable.vue'
import { VideoPlay, Loading } from '@element-plus/icons-vue'

const playerStore = usePlayerStore()
const mainStore = useMainStore()

const loading = ref(false)
const history = ref([])

async function loadHistory() {
  loading.value = true
  try {
    const res = await historyApi.getRecent(30)
    history.value = res.data
  } catch (e) {
    ElMessage.error('加载播放记录失败')
  } finally {
    loading.value = false
  }
}

function playAll() {
  if (history.value.length > 0) {
    playerStore.playList(history.value)
  }
}

onMounted(() => {
  loadHistory()
  mainStore.fetchFavorites()
})
</script>

<style lang="scss" scoped>
.history-page {
  padding: 24px 32px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  
  h1 {
    font-size: 28px;
    font-weight: 600;
  }
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px;
  
  .loading-icon {
    animation: spin 1s linear infinite;
    color: #409eff;
  }
  
  p {
    margin-top: 16px;
    color: rgba(255, 255, 255, 0.6);
  }
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
</style>
