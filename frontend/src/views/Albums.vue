<template>
  <div class="albums-page">
    <Sidebar />
    
    <div class="main-content">
      <div class="page-header">
        <h1>专辑</h1>
      </div>
      
      <div v-if="loading" class="loading-container">
        <el-icon class="loading-icon" :size="48"><Loading /></el-icon>
        <p>加载中...</p>
      </div>
      
      <template v-else>
        <div v-if="albums.length > 0" class="albums-grid">
          <div 
            v-for="album in albums" 
            :key="album"
            class="album-card"
            @click="goToAlbum(album)"
          >
            <div class="album-cover">
              <div class="cover-placeholder">
                <el-icon :size="48"><Picture /></el-icon>
              </div>
              <div class="cover-overlay">
                <el-button type="primary" circle @click.stop="playAlbum(album)">
                  <el-icon><VideoPlay /></el-icon>
                </el-button>
              </div>
            </div>
            <div class="album-info">
              <h3 class="album-name">{{ album }}</h3>
              <p class="album-type">专辑</p>
            </div>
          </div>
        </div>
        
        <el-empty v-else description="暂无专辑，请先添加音乐">
          <el-button type="primary" @click="$router.push('/settings')">去设置</el-button>
        </el-empty>
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { usePlayerStore } from '@/stores/player'
import { songApi } from '@/api'
import { ElMessage } from 'element-plus'
import Sidebar from '@/components/Sidebar.vue'
import { Loading, Picture, VideoPlay } from '@element-plus/icons-vue'

const router = useRouter()
const playerStore = usePlayerStore()

const loading = ref(false)
const albums = ref([])

async function loadAlbums() {
  loading.value = true
  try {
    const res = await songApi.getAllAlbums()
    albums.value = res.data
  } catch (e) {
    ElMessage.error('加载专辑失败')
  } finally {
    loading.value = false
  }
}

function goToAlbum(album) {
  router.push(`/album/${encodeURIComponent(album)}`)
}

async function playAlbum(album) {
  try {
    const res = await songApi.getByAlbum(album)
    if (res.data.length > 0) {
      playerStore.playList(res.data)
    } else {
      ElMessage.warning('该专辑暂无歌曲')
    }
  } catch (e) {
    ElMessage.error('加载专辑歌曲失败')
  }
}

onMounted(() => {
  loadAlbums()
})
</script>

<style lang="scss" scoped>
.albums-page {
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

.page-header {
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

.albums-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 24px;
}

.album-card {
  cursor: pointer;
  transition: transform 0.2s ease;
  
  &:hover {
    transform: translateY(-4px);
    
    .cover-overlay {
      opacity: 1;
    }
  }
  
  .album-cover {
    width: 100%;
    padding-bottom: 100%;
    position: relative;
    border-radius: 8px;
    overflow: hidden;
    margin-bottom: 12px;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.2);
    
    .cover-placeholder {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
      display: flex;
      align-items: center;
      justify-content: center;
      color: rgba(255, 255, 255, 0.8);
    }
    
    .cover-overlay {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: rgba(0, 0, 0, 0.4);
      display: flex;
      align-items: center;
      justify-content: center;
      opacity: 0;
      transition: opacity 0.3s ease;
    }
  }
  
  .album-info {
    text-align: center;
    
    .album-name {
      font-size: 14px;
      font-weight: 600;
      margin-bottom: 4px;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }
    
    .album-type {
      font-size: 12px;
      color: rgba(255, 255, 255, 0.5);
    }
  }
}
</style>