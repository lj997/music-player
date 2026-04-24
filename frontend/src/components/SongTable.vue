<template>
  <div class="song-table">
    <el-table 
      :data="songs" 
      style="width: 100%"
      :row-class-name="getRowClassName"
      @row-click="handleRowClick"
      @row-dblclick="playSong"
    >
      <el-table-column type="index" width="50" align="center" />
      
      <el-table-column prop="title" label="标题" min-width="200">
        <template #default="{ row }">
          <div class="song-info">
            <div class="song-cover">
              <img 
                :src="row.coverPath || '/default-cover.png'" 
                :alt="row.title"
                @error="$event.target.src = '/default-cover.png'"
              />
              <div class="play-overlay">
                <el-icon><VideoPlay /></el-icon>
              </div>
            </div>
            <div class="song-detail">
              <div class="song-title">{{ row.title }}</div>
              <div class="song-artist">{{ row.artist }}</div>
            </div>
          </div>
        </template>
      </el-table-column>
      
      <el-table-column prop="artist" label="艺术家" min-width="150" />
      
      <el-table-column prop="album" label="专辑" min-width="180" />
      
      <el-table-column prop="duration" label="时长" width="100" align="center">
        <template #default="{ row }">
          {{ formatDuration(row.duration) }}
        </template>
      </el-table-column>
      
      <el-table-column label="操作" width="180" align="center">
        <template #default="{ row }">
          <el-button 
            type="text" 
            size="small" 
            @click.stop="playSong(row)"
          >
            播放
          </el-button>
          <el-button 
            type="text" 
            size="small" 
            :class="{ 'is-favorite': isSongFavorite(row.id) }"
            @click.stop="toggleFavorite(row)"
          >
            {{ isSongFavorite(row.id) ? '已收藏' : '收藏' }}
          </el-button>
          <el-button 
            type="text" 
            size="small"
            @click.stop="showAddToPlaylist(row)"
          >
            添加到
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <el-dialog v-model="showPlaylistDialog" title="添加到歌单" width="500px">
      <el-empty v-if="playlists.length === 0" description="暂无歌单，请先创建" />
      <div v-else class="playlist-list">
        <div 
          v-for="playlist in playlists" 
          :key="playlist.id"
          class="playlist-item"
          @click="addSongToPlaylist(playlist)"
        >
          <div class="playlist-info">
            <div class="playlist-name">{{ playlist.name }}</div>
            <div class="playlist-count">{{ playlist.songCount || 0 }} 首</div>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, ref, inject } from 'vue'
import { usePlayerStore } from '@/stores/player'
import { useMainStore } from '@/stores/main'
import { playlistApi } from '@/api'
import { ElMessage } from 'element-plus'
import { VideoPlay } from '@element-plus/icons-vue'

const props = defineProps({
  songs: {
    type: Array,
    default: () => []
  },
  currentSongId: {
    type: Number,
    default: null
  }
})

const playerStore = usePlayerStore()
const mainStore = useMainStore()

const showPlaylistDialog = ref(false)
const selectedSong = ref(null)
const playlists = ref([])

const isSongFavorite = (id) => {
  return mainStore.favorites.some(s => s.id === id)
}

const getRowClassName = ({ row }) => {
  if (playerStore.currentSong?.id === row.id) {
    return 'playing-row'
  }
  return ''
}

function formatDuration(ms) {
  if (!ms) return '0:00'
  const seconds = Math.floor(ms / 1000)
  const mins = Math.floor(seconds / 60)
  const secs = seconds % 60
  return `${mins}:${secs.toString().padStart(2, '0')}`
}

function handleRowClick(row) {
  // 点击行时的处理
}

function playSong(row) {
  playerStore.playSong(row, props.songs)
}

async function toggleFavorite(row) {
  const result = await mainStore.toggleFavorite(row.id)
  ElMessage.success(result ? '已添加到收藏' : '已取消收藏')
}

async function showAddToPlaylist(row) {
  selectedSong.value = row
  try {
    const res = await playlistApi.getAll()
    playlists.value = res.data
    showPlaylistDialog.value = true
  } catch (e) {
    ElMessage.error('获取歌单失败')
  }
}

async function addSongToPlaylist(playlist) {
  try {
    await playlistApi.addSong(playlist.id, selectedSong.value.id)
    ElMessage.success('添加成功')
    showPlaylistDialog.value = false
  } catch (e) {
    ElMessage.error('添加失败')
  }
}
</script>

<style lang="scss" scoped>
.song-table {
  width: 100%;
}

.song-info {
  display: flex;
  align-items: center;
}

.song-cover {
  width: 44px;
  height: 44px;
  border-radius: 6px;
  overflow: hidden;
  position: relative;
  flex-shrink: 0;
  
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
  
  .play-overlay {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    align-items: center;
    justify-content: center;
    opacity: 0;
    transition: opacity 0.2s ease;
    color: #fff;
  }
  
  &:hover .play-overlay {
    opacity: 1;
  }
}

.song-detail {
  margin-left: 12px;
  overflow: hidden;
  
  .song-title {
    font-size: 14px;
    font-weight: 500;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
  
  .song-artist {
    font-size: 12px;
    color: rgba(255, 255, 255, 0.6);
    margin-top: 2px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
}

.playlist-list {
  max-height: 300px;
  overflow-y: auto;
}

.playlist-item {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.2s ease;
  
  &:hover {
    background-color: rgba(64, 158, 255, 0.1);
  }
  
  .playlist-info {
    .playlist-name {
      font-size: 14px;
      font-weight: 500;
    }
    
    .playlist-count {
      font-size: 12px;
      color: rgba(255, 255, 255, 0.6);
      margin-top: 4px;
    }
  }
}

:deep(.playing-row) {
  background-color: rgba(64, 158, 255, 0.1) !important;
}

:deep(.is-favorite) {
  color: #ff6b6b;
}
</style>