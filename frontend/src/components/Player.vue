<template>
  <div class="player-container">
    <div class="player-left">
      <div 
        class="player-cover"
        :class="{ 'playing': isPlaying }"
        @click="showSongDetail"
      >
        <img 
          :src="currentSong?.coverPath || '/default-cover.png'" 
          :alt="currentSong?.title"
          @error="handleCoverError"
        />
      </div>
      <div class="player-info">
        <div class="player-title">{{ currentSong?.title || '未选择歌曲' }}</div>
        <div class="player-artist">{{ currentSong?.artist || '-' }}</div>
      </div>
      <el-button 
        type="text" 
        class="favorite-btn"
        :class="{ 'is-favorite': isFavorite }"
        @click.stop="toggleFavorite"
      >
        <el-icon :size="18">
          <StarFilled :fill="isFavorite ? '#ff6b6b' : 'none'" :color="isFavorite ? '#ff6b6b' : 'inherit'" />
        </el-icon>
      </el-button>
    </div>

    <div class="player-center">
      <div class="player-controls">
        <el-button type="text" class="control-btn" @click="playerStore.toggleMode">
          <el-tooltip :content="playerStore.currentMode?.label" placement="top">
            <el-icon :size="20">
              <component :is="modeIcon" />
            </el-icon>
          </el-tooltip>
        </el-button>
        <el-button type="text" class="control-btn" @click="playerStore.playPrev">
          <el-icon :size="22"><ArrowLeft /></el-icon>
        </el-button>
        <el-button type="primary" class="play-btn" @click="playerStore.togglePlay">
          <el-icon :size="24">
            <component :is="isPlaying ? VideoPause : VideoPlay" />
          </el-icon>
        </el-button>
        <el-button type="text" class="control-btn" @click="playerStore.playNext">
          <el-icon :size="22"><ArrowRight /></el-icon>
        </el-button>
        <el-button type="text" class="control-btn">
          <el-tooltip content="播放列表" placement="top">
            <el-icon :size="20"><List /></el-icon>
          </el-tooltip>
        </el-button>
      </div>
      
      <div class="player-progress">
        <span class="time">{{ formatTime(currentTime) }}</span>
        <el-slider 
          class="progress-slider"
          v-model="progressValue"
          :max="duration || 100"
          :format-tooltip="formatTime"
          @change="handleProgressChange"
          :show-tooltip="true"
        />
        <span class="time">{{ formatTime(duration) }}</span>
      </div>
    </div>

    <div class="player-right">
      <el-slider 
        class="volume-slider"
        v-model="volumeValue"
        :max="100"
        :show-tooltip="false"
        @change="handleVolumeChange"
      />
      <el-button type="text" class="control-btn" @click="toggleMute">
        <el-icon :size="20">
          <component :is="volumeValue > 0 ? 'Volume' : 'Mute'" />
        </el-icon>
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { computed, watch, ref, onMounted } from 'vue'
import { usePlayerStore } from '@/stores/player'
import { useMainStore } from '@/stores/main'
import { ElMessage } from 'element-plus'
import {
  StarFilled,
  ArrowLeft,
  ArrowRight,
  VideoPause,
  VideoPlay,
  List,
  Sort,
  Connection,
  Refresh,
  Volume,
  Mute
} from '@element-plus/icons-vue'

const playerStore = usePlayerStore()
const mainStore = useMainStore()

const coverError = ref(false)

const currentSong = computed(() => playerStore.currentSong)
const isPlaying = computed(() => playerStore.isPlaying)
const currentTime = computed(() => playerStore.currentTime)
const duration = computed(() => playerStore.duration)
const volume = computed(() => playerStore.volume)

const progressValue = computed({
  get: () => currentTime.value,
  set: (val) => {
    playerStore.seek(val)
  }
})

const volumeValue = computed({
  get: () => volume.value * 100,
  set: (val) => {
    playerStore.setVolume(val / 100)
  }
})

const modeIcon = computed(() => {
  const mode = playerStore.playMode
  if (mode === 'sequence') return Sort
  if (mode === 'random') return Connection
  return Refresh
})

const isFavorite = computed(() => {
  if (!currentSong.value) return false
  return mainStore.favorites.some(s => s.id === currentSong.value.id)
})

function formatTime(seconds) {
  if (!seconds || isNaN(seconds)) return '0:00'
  const mins = Math.floor(seconds / 60)
  const secs = Math.floor(seconds % 60)
  return `${mins}:${secs.toString().padStart(2, '0')}`
}

function handleProgressChange(val) {
  playerStore.seek(val)
}

function handleVolumeChange(val) {
  playerStore.setVolume(val / 100)
}

function toggleMute() {
  if (volumeValue.value > 0) {
    playerStore.setVolume(0)
  } else {
    playerStore.setVolume(0.8)
  }
}

async function toggleFavorite() {
  if (!currentSong.value) return
  const result = await mainStore.toggleFavorite(currentSong.value.id)
  ElMessage.success(result ? '已添加到收藏' : '已取消收藏')
}

function showSongDetail() {
  // 可以实现显示歌曲详情的功能
}

function handleCoverError() {
  coverError.value = true
}

onMounted(() => {
  playerStore.initAudio()
})
</script>

<style lang="scss" scoped>
.player-container {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 90px;
  background: linear-gradient(180deg, rgba(20, 20, 30, 0.95) 0%, rgba(10, 10, 20, 0.98) 100%);
  backdrop-filter: blur(20px);
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  display: flex;
  align-items: center;
  padding: 0 24px;
  z-index: 1000;
}

.player-left {
  display: flex;
  align-items: center;
  width: 300px;
  min-width: 200px;
}

.player-cover {
  width: 56px;
  height: 56px;
  border-radius: 8px;
  overflow: hidden;
  flex-shrink: 0;
  position: relative;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
  
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.3s ease;
  }
  
  &.playing img {
    animation: rotate 20s linear infinite;
  }
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.player-info {
  margin-left: 12px;
  overflow: hidden;
  
  .player-title {
    font-size: 14px;
    font-weight: 500;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
  
  .player-artist {
    font-size: 12px;
    color: rgba(255, 255, 255, 0.6);
    margin-top: 4px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
}

.favorite-btn {
  margin-left: 12px;
  padding: 4px;
  
  &:hover {
    color: #ff6b6b;
  }
}

.player-center {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  max-width: 700px;
}

.player-controls {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.control-btn {
  color: rgba(255, 255, 255, 0.7);
  padding: 8px;
  
  &:hover {
    color: #fff;
  }
}

.play-btn {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background: linear-gradient(135deg, #409eff 0%, #67c23a 100%);
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0;
  
  &:hover {
    transform: scale(1.05);
  }
  
  :deep(.el-icon) {
    margin-left: 2px;
  }
}

.player-progress {
  display: flex;
  align-items: center;
  width: 100%;
  gap: 12px;
}

.time {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.6);
  width: 45px;
  text-align: center;
}

.progress-slider {
  flex: 1;
  height: 6px;
  
  :deep(.el-slider__runway) {
    background-color: rgba(255, 255, 255, 0.1);
    height: 4px;
  }
  
  :deep(.el-slider__bar) {
    background: linear-gradient(90deg, #409eff 0%, #67c23a 100%);
    height: 4px;
  }
  
  :deep(.el-slider__button) {
    width: 12px;
    height: 12px;
    border: none;
    background: #fff;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
    opacity: 0;
    transition: opacity 0.2s ease;
  }
  
  &:hover :deep(.el-slider__button) {
    opacity: 1;
  }
}

.player-right {
  display: flex;
  align-items: center;
  width: 200px;
  min-width: 150px;
  justify-content: flex-end;
  gap: 8px;
}

.volume-slider {
  width: 100px;
  
  :deep(.el-slider__runway) {
    background-color: rgba(255, 255, 255, 0.1);
    height: 4px;
  }
  
  :deep(.el-slider__bar) {
    background-color: rgba(255, 255, 255, 0.7);
    height: 4px;
  }
  
  :deep(.el-slider__button) {
    width: 10px;
    height: 10px;
    border: none;
    background: #fff;
  }
}
</style>