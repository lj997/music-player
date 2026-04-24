<template>
  <div class="playlist-detail-page">
    <Sidebar />
    
    <div class="main-content">
      <div v-if="loading" class="loading-container">
        <el-icon class="loading-icon" :size="48"><Loading /></el-icon>
        <p>加载中...</p>
      </div>
      
      <template v-else>
        <div v-if="playlist" class="playlist-header">
          <div class="playlist-cover">
            <div class="cover-placeholder">
              <el-icon :size="64"><Collection /></el-icon>
            </div>
          </div>
          <div class="playlist-info">
            <div class="playlist-type">歌单</div>
            <h1 class="playlist-name">{{ playlist.name }}</h1>
            <p class="playlist-desc">{{ playlist.description || '暂无描述' }}</p>
            <div class="playlist-meta">
              <span>{{ playlist.songCount || 0 }} 首歌曲</span>
              <span>创建于 {{ formatDate(playlist.createTime) }}</span>
            </div>
            <div class="playlist-actions">
              <el-button type="primary" size="large" @click="playAll">
                <el-icon><VideoPlay /></el-icon>
                播放全部
              </el-button>
              <el-button size="large" @click="showDeleteConfirm = true">
                <el-icon><Delete /></el-icon>
                删除歌单
              </el-button>
            </div>
          </div>
        </div>
        
        <div class="song-list">
          <div class="list-header">
            <span style="width: 50px; text-align: center">#</span>
            <span style="flex: 2; min-width: 200px">标题</span>
            <span style="flex: 1; min-width: 120px">艺术家</span>
            <span style="flex: 1.5; min-width: 150px">专辑</span>
            <span style="width: 100px; text-align: center">时长</span>
            <span style="width: 150px; text-align: center">操作</span>
          </div>
          
          <div 
            v-for="(song, index) in songs" 
            :key="song.id"
            class="song-item"
            :class="{ 'playing': isCurrentSong(song.id) }"
            @dblclick="playSong(song)"
          >
            <span class="song-index">{{ index + 1 }}</span>
            <div class="song-title-cell">
              <div class="song-cover-small">
                <img 
                  :src="song.coverPath || '/default-cover.png'" 
                  :alt="song.title"
                />
              </div>
              <div class="song-text">
                <div class="title">{{ song.title }}</div>
              </div>
            </div>
            <span class="song-artist">{{ song.artist }}</span>
            <span class="song-album">{{ song.album }}</span>
            <span class="song-duration">{{ formatDuration(song.duration) }}</span>
            <div class="song-actions">
              <el-button type="text" size="small" @click="playSong(song)">播放</el-button>
              <el-button 
                type="text" 
                size="small"
                :class="{ 'is-favorite': isSongFavorite(song.id) }"
                @click="toggleFavorite(song)"
              >
                {{ isSongFavorite(song.id) ? '已收藏' : '收藏' }}
              </el-button>
              <el-button 
                type="text" 
                size="small"
                style="color: #f56c6c"
                @click="removeSong(song)"
              >
                移除
              </el-button>
            </div>
          </div>
          
          <el-empty v-if="songs.length === 0" description="歌单中还没有歌曲" />
        </div>
      </template>
    </div>
    
    <el-dialog v-model="showDeleteConfirm" title="删除歌单" width="400px">
      <p>确定要删除歌单「{{ playlist?.name }}」吗？</p>
      <p style="color: rgba(255, 255, 255, 0.6); margin-top: 8px;">此操作不可恢复</p>
      <template #footer>
        <el-button @click="showDeleteConfirm = false">取消</el-button>
        <el-button type="danger" @click="confirmDelete">确定删除</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { usePlayerStore } from '@/stores/player'
import { useMainStore } from '@/stores/main'
import { playlistApi } from '@/api'
import { ElMessage } from 'element-plus'
import Sidebar from '@/components/Sidebar.vue'
import {
  Loading,
  Collection,
  VideoPlay,
  Delete
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const playerStore = usePlayerStore()
const mainStore = useMainStore()

const playlistId = computed(() => route.params.id)
const loading = ref(false)
const playlist = ref(null)
const songs = ref([])
const showDeleteConfirm = ref(false)

const isCurrentSong = (id) => playerStore.currentSong?.id === id
const isSongFavorite = (id) => mainStore.favorites.some(s => s.id === id)

function formatDate(dateStr) {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

function formatDuration(ms) {
  if (!ms) return '0:00'
  const seconds = Math.floor(ms / 1000)
  const mins = Math.floor(seconds / 60)
  const secs = seconds % 60
  return `${mins}:${secs.toString().padStart(2, '0')}`
}

async function loadPlaylist() {
  loading.value = true
  try {
    const [playlistRes, songsRes] = await Promise.all([
      playlistApi.getById(playlistId.value),
      playlistApi.getSongs(playlistId.value)
    ])
    playlist.value = playlistRes.data
    songs.value = songsRes.data
  } catch (e) {
    ElMessage.error('加载歌单失败')
  } finally {
    loading.value = false
  }
}

function playAll() {
  if (songs.value.length > 0) {
    playerStore.playList(songs.value)
  }
}

function playSong(song) {
  playerStore.playSong(song, songs.value)
}

async function toggleFavorite(song) {
  const result = await mainStore.toggleFavorite(song.id)
  ElMessage.success(result ? '已添加到收藏' : '已取消收藏')
}

async function removeSong(song) {
  try {
    await playlistApi.removeSong(playlistId.value, song.id)
    ElMessage.success('已从歌单移除')
    await loadPlaylist()
  } catch (e) {
    ElMessage.error('移除失败')
  }
}

async function confirmDelete() {
  try {
    await playlistApi.delete(playlistId.value)
    ElMessage.success('删除成功')
    router.push('/playlists')
  } catch (e) {
    ElMessage.error('删除失败')
  }
}

onMounted(() => {
  loadPlaylist()
  mainStore.fetchFavorites()
})
</script>

<style lang="scss" scoped>
.playlist-detail-page {
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

.playlist-header {
  display: flex;
  gap: 32px;
  margin-bottom: 32px;
  padding-bottom: 24px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.playlist-cover {
  width: 230px;
  height: 230px;
  flex-shrink: 0;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
  
  .cover-placeholder {
    width: 100%;
    height: 100%;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    display: flex;
    align-items: center;
    justify-content: center;
    color: rgba(255, 255, 255, 0.8);
  }
}

.playlist-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  
  .playlist-type {
    font-size: 12px;
    text-transform: uppercase;
    letter-spacing: 1px;
    color: rgba(255, 255, 255, 0.6);
    margin-bottom: 8px;
  }
  
  .playlist-name {
    font-size: 48px;
    font-weight: 700;
    margin-bottom: 12px;
    line-height: 1.2;
  }
  
  .playlist-desc {
    font-size: 14px;
    color: rgba(255, 255, 255, 0.7);
    margin-bottom: 16px;
  }
  
  .playlist-meta {
    font-size: 13px;
    color: rgba(255, 255, 255, 0.6);
    
    span {
      margin-right: 16px;
    }
  }
  
  .playlist-actions {
    margin-top: auto;
    display: flex;
    gap: 12px;
  }
}

.song-list {
  background: rgba(255, 255, 255, 0.03);
  border-radius: 12px;
  overflow: hidden;
}

.list-header {
  display: flex;
  align-items: center;
  padding: 12px 20px;
  background: rgba(255, 255, 255, 0.05);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  font-size: 12px;
  color: rgba(255, 255, 255, 0.6);
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.song-item {
  display: flex;
  align-items: center;
  padding: 12px 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
  cursor: pointer;
  transition: background-color 0.2s ease;
  
  &:hover {
    background: rgba(255, 255, 255, 0.05);
  }
  
  &.playing {
    background: rgba(64, 158, 255, 0.1);
  }
  
  .song-index {
    width: 50px;
    text-align: center;
    font-size: 13px;
    color: rgba(255, 255, 255, 0.5);
  }
  
  .song-title-cell {
    flex: 2;
    min-width: 200px;
    display: flex;
    align-items: center;
    
    .song-cover-small {
      width: 40px;
      height: 40px;
      border-radius: 4px;
      overflow: hidden;
      flex-shrink: 0;
      
      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }
    }
    
    .song-text {
      margin-left: 12px;
      overflow: hidden;
      
      .title {
        font-size: 14px;
        font-weight: 500;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
      }
    }
  }
  
  .song-artist {
    flex: 1;
    min-width: 120px;
    font-size: 13px;
    color: rgba(255, 255, 255, 0.7);
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
  
  .song-album {
    flex: 1.5;
    min-width: 150px;
    font-size: 13px;
    color: rgba(255, 255, 255, 0.7);
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
  
  .song-duration {
    width: 100px;
    text-align: center;
    font-size: 13px;
    color: rgba(255, 255, 255, 0.7);
  }
  
  .song-actions {
    width: 150px;
    display: flex;
    justify-content: center;
    gap: 4px;
    
    .is-favorite {
      color: #ff6b6b;
    }
  }
}
</style>