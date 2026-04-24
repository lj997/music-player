<template>
  <div class="artist-detail-page">
    <Sidebar />
    
    <div class="main-content">
      <div v-if="loading" class="loading-container">
        <el-icon class="loading-icon" :size="48"><Loading /></el-icon>
        <p>加载中...</p>
      </div>
      
      <template v-else>
        <div class="artist-header">
          <div class="artist-cover">
            <div class="cover-placeholder">
              <el-icon :size="64"><User /></el-icon>
            </div>
          </div>
          <div class="artist-info">
            <div class="artist-type">艺术家</div>
            <h1 class="artist-name">{{ artistName }}</h1>
            <p class="artist-stats">{{ songs.length }} 首歌曲</p>
            <div class="artist-actions">
              <el-button type="primary" size="large" @click="playAll">
                <el-icon><VideoPlay /></el-icon>
                播放全部
              </el-button>
            </div>
          </div>
        </div>
        
        <div class="song-list">
          <SongTable :songs="songs" />
        </div>
        
        <el-empty v-if="songs.length === 0" description="该艺术家暂无歌曲" />
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { usePlayerStore } from '@/stores/player'
import { useMainStore } from '@/stores/main'
import { songApi } from '@/api'
import { ElMessage } from 'element-plus'
import Sidebar from '@/components/Sidebar.vue'
import SongTable from '@/components/SongTable.vue'
import { Loading, User, VideoPlay } from '@element-plus/icons-vue'

const route = useRoute()
const playerStore = usePlayerStore()
const mainStore = useMainStore()

const artistName = computed(() => decodeURIComponent(route.params.name || ''))
const loading = ref(false)
const songs = ref([])

async function loadArtistSongs() {
  loading.value = true
  try {
    const res = await songApi.getByArtist(artistName.value)
    songs.value = res.data
  } catch (e) {
    ElMessage.error('加载艺术家歌曲失败')
  } finally {
    loading.value = false
  }
}

function playAll() {
  if (songs.value.length > 0) {
    playerStore.playList(songs.value)
  }
}

onMounted(() => {
  loadArtistSongs()
  mainStore.fetchFavorites()
})
</script>

<style lang="scss" scoped>
.artist-detail-page {
  display: flex;
  min-height: 100vh;
}

.main-content {
  margin-left: 240px;
  padding: 24px 32px;
  width: calc(100% - 240px);
  height: calc(100vh - 90px);
  overflow-y: auto;
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

.artist-header {
  display: flex;
  gap: 32px;
  margin-bottom: 32px;
  padding-bottom: 24px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.artist-cover {
  width: 200px;
  height: 200px;
  flex-shrink: 0;
  border-radius: 50%;
  overflow: hidden;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
  
  .cover-placeholder {
    width: 100%;
    height: 100%;
    background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
    display: flex;
    align-items: center;
    justify-content: center;
    color: rgba(255, 255, 255, 0.8);
  }
}

.artist-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  
  .artist-type {
    font-size: 12px;
    text-transform: uppercase;
    letter-spacing: 1px;
    color: rgba(255, 255, 255, 0.6);
    margin-bottom: 8px;
  }
  
  .artist-name {
    font-size: 48px;
    font-weight: 700;
    margin-bottom: 12px;
    line-height: 1.2;
  }
  
  .artist-stats {
    font-size: 14px;
    color: rgba(255, 255, 255, 0.7);
    margin-bottom: 16px;
  }
  
  .artist-actions {
    margin-top: auto;
  }
}

.song-list {
  background: rgba(255, 255, 255, 0.03);
  border-radius: 12px;
  overflow: hidden;
}
</style>