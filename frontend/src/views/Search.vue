<template>
  <div class="search-page">
    <div class="search-header">
      <el-input 
        v-model="searchKeyword" 
        placeholder="搜索歌曲、艺术家或专辑" 
        size="large"
        clearable
        @clear="doSearch"
        @keyup.enter="doSearch"
      >
        <template #prefix>
          <el-icon :size="20"><Search /></el-icon>
        </template>
        <template #append>
          <el-button type="primary" @click="doSearch">搜索</el-button>
        </template>
      </el-input>
    </div>
    
    <div v-if="searchKeyword.trim() && !loading" class="search-results">
      <template v-if="results.songs.length > 0">
        <div class="result-section">
          <div class="section-title">
            歌曲
            <span class="result-count">({{ results.songs.length }})</span>
          </div>
          <SongTable :songs="results.songs" />
        </div>
      </template>
      
      <div v-else-if="hasSearched" class="no-results">
        <el-empty description="没有找到相关歌曲" />
      </div>
    </div>
    
    <div v-else-if="loading" class="loading-container">
      <el-icon class="loading-icon" :size="48"><Loading /></el-icon>
      <p>搜索中...</p>
    </div>
    
    <div v-else class="search-hint">
      <el-icon :size="64" style="color: rgba(255, 255, 255, 0.3); margin-bottom: 20px"><Search /></el-icon>
      <h2>搜索音乐</h2>
      <p>输入歌曲名称、艺术家或专辑名称进行搜索</p>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useMainStore } from '@/stores/main'
import { songApi } from '@/api'
import { ElMessage } from 'element-plus'
import SongTable from '@/components/SongTable.vue'
import { Search, Loading } from '@element-plus/icons-vue'

const mainStore = useMainStore()

const searchKeyword = ref('')
const loading = ref(false)
const hasSearched = ref(false)

const results = reactive({
  songs: []
})

async function doSearch() {
  const keyword = searchKeyword.value.trim()
  
  if (!keyword) {
    results.songs = []
    hasSearched.value = false
    return
  }
  
  loading.value = true
  hasSearched.value = true
  
  try {
    const res = await songApi.getAll(keyword)
    results.songs = res.data
  } catch (e) {
    ElMessage.error('搜索失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  mainStore.fetchFavorites()
})
</script>

<style lang="scss" scoped>
.search-page {
  padding: 24px 32px;
}

.search-header {
  margin-bottom: 32px;
  
  :deep(.el-input__wrapper) {
    padding: 4px 4px 4px 16px;
    border-radius: 24px;
  }
  
  :deep(.el-input__inner) {
    font-size: 16px;
  }
  
  :deep(.el-input-group__append) {
    padding: 0;
    border: none;
    background: transparent;
  }
  
  :deep(.el-button) {
    border-radius: 0 20px 20px 0;
    height: 40px;
  }
}

.search-results {
  .result-section {
    margin-bottom: 32px;
    
    .section-title {
      font-size: 18px;
      font-weight: 600;
      margin-bottom: 16px;
      
      .result-count {
        font-size: 14px;
        font-weight: 400;
        color: rgba(255, 255, 255, 0.5);
        margin-left: 8px;
      }
    }
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

.search-hint {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px;
  text-align: center;
  
  h2 {
    font-size: 24px;
    margin-bottom: 12px;
  }
  
  p {
    font-size: 14px;
    color: rgba(255, 255, 255, 0.5);
  }
}

.no-results {
  padding: 40px;
}
</style>
