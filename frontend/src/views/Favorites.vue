<template>
  <div class="favorites-page">
    <div class="page-header">
      <h1>我的收藏</h1>
      <el-button 
        type="primary" 
        :disabled="favorites.length === 0"
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
        v-if="favorites.length > 0"
        :songs="favorites"
      />
      
      <el-empty v-else description="暂无收藏，快去收藏喜欢的歌曲吧">
        <el-button type="primary" @click="$router.push('/library')">去音乐库</el-button>
      </el-empty>
    </template>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { usePlayerStore } from '@/stores/player'
import { useMainStore } from '@/stores/main'
import SongTable from '@/components/SongTable.vue'
import { VideoPlay, Loading } from '@element-plus/icons-vue'

const playerStore = usePlayerStore()
const mainStore = useMainStore()

const loading = ref(false)
const favorites = computed(() => mainStore.favorites)

async function loadFavorites() {
  loading.value = true
  try {
    await mainStore.fetchFavorites()
  } finally {
    loading.value = false
  }
}

function playAll() {
  if (favorites.value.length > 0) {
    playerStore.playList(favorites.value)
  }
}

onMounted(() => {
  loadFavorites()
})
</script>

<style lang="scss" scoped>
.favorites-page {
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
