<template>
  <div class="album-detail-page">
    <Sidebar />
    
    <div class="main-content">
      <div v-if="loading" class="loading-container">
        <el-icon class="loading-icon" :size="48"><Loading /></el-icon>
        <p>加载中...</p>
      </div>
      
      <template v-else>
        <div class="album-header">
          <div class="album-cover">
            <div class="cover-placeholder">
              <el-icon :size="64"><Picture /></el-icon>
            </div>
          </div>
          <div class="album-info">
            <div class="album-type">专辑</div>
            <h1 class="album-name">{{ albumName }}</h1>
            <p class="album-artist">{{ songs[0]?.artist || '未知艺术家' }}</p>
            <p class="album-stats">{{ songs.length }} 首歌曲</p>
            <div class="album-actions">
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
        
        <el-empty v-if="songs.length === 0" description="该专辑暂无歌曲" />
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
import { Loading, Picture, VideoPlay } from '@element-plus/icons-vue'

const route = useRoute()
const playerStore = usePlayerStore()
const mainStore = useMainStore()

const albumName = computed(() => decodeURIComponent(route.params.name || ''))
const loading = ref(false)
const songs = ref([])

async function loadAlbumSongs() {
  loading.value = true
  try {
    const res = await songApi.getByAlbum(albumName.value)
    songs.value = res.data
  } catch (e) {
    ElMessage.error('加载专辑歌曲失败')
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
  loadAlbumSongs()
  mainStore.fetchFavorites()
})
</script>

<style lang="scss" scoped>
.album-detail-page {
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

.album-header {
  display: flex;
  gap: 32px;
  margin-bottom: 32px;
  padding-bottom: 24px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.album-cover {
  width: 200px;
  height: 200px;
  flex-shrink: 0;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
  
  .cover-placeholder {
    width: 100%;
    height: 100%;
    background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
    display: flex;
    align-items: center;
    justify-content: center;
    color: rgba(255, 255, 255, 0.8);
  }
}

.album-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  
  .album-type {
    font-size: 12px;
    text-transform: uppercase;
    letter-spacing: 1px;
    color: rgba(255, 255, 255, 0.6);
    margin-bottom: 8px;
  }
  
  .album-name {
    font-size: 48px;
    font-weight: 700;
    margin-bottom: 12px;
    line-height: 1.2;
  }
  
  .album-artist {
    font-size: 16px;
    color: rgba(255, 255, 255, 0.8);
    margin-bottom: 8px;
  }
  
  .album-stats {
    font-size: 14px;
    color: rgba(255, 255, 255, 0.6);
    margin-bottom: 16px;
  }
  
  .album-actions {
    margin-top: auto;
  }
}

.song-list {
  background: rgba(255, 255, 255, 0.03);
  border-radius: 12px;
  overflow: hidden;
}
</style>