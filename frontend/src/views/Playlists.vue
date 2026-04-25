<template>
  <div class="playlists-page">
    <div class="page-header">
      <h1>我的歌单</h1>
      <el-button type="primary" @click="showCreateDialog = true">
        <el-icon><Plus /></el-icon>
        创建歌单
      </el-button>
    </div>
    
    <div v-if="loading" class="loading-container">
      <el-icon class="loading-icon" :size="48"><Loading /></el-icon>
      <p>加载中...</p>
    </div>
    
    <template v-else>
      <div v-if="playlists.length > 0" class="playlist-grid">
        <div 
          v-for="playlist in playlists" 
          :key="playlist.id"
          class="playlist-card"
          @click="goToPlaylist(playlist)"
        >
          <div class="playlist-cover">
            <div class="cover-placeholder">
              <el-icon :size="48"><Collection /></el-icon>
            </div>
            <div class="cover-overlay">
              <el-button type="primary" circle @click.stop="playPlaylist(playlist)">
                <el-icon><VideoPlay /></el-icon>
              </el-button>
            </div>
          </div>
          <div class="playlist-info">
            <h3 class="playlist-name">{{ playlist.name }}</h3>
            <p class="playlist-desc">{{ playlist.description || '暂无描述' }}</p>
            <p class="playlist-count">{{ playlist.songCount || 0 }} 首歌曲</p>
          </div>
          <el-dropdown trigger="click" @command="(cmd) => handleCommand(cmd, playlist)" placement="bottom-end">
            <el-button type="text" class="more-btn">
              <el-icon><MoreFilled /></el-icon>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="play">播放</el-dropdown-item>
                <el-dropdown-item command="delete" divided style="color: #f56c6c">删除歌单</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
      
      <el-empty v-else description="暂无歌单，快来创建一个吧">
        <el-button type="primary" @click="showCreateDialog = true">创建歌单</el-button>
      </el-empty>
    </template>
    
    <el-dialog v-model="showCreateDialog" title="创建歌单" width="450px">
      <el-form :model="newPlaylist" label-width="80px">
        <el-form-item label="歌单名称">
          <el-input v-model="newPlaylist.name" placeholder="请输入歌单名称" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input 
            v-model="newPlaylist.description" 
            type="textarea" 
            :rows="4"
            placeholder="请输入歌单描述（可选）" 
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" @click="createPlaylist">创建</el-button>
      </template>
    </el-dialog>
    
    <el-dialog v-model="showDeleteConfirm" title="删除歌单" width="400px">
      <p>确定要删除歌单「{{ deletingPlaylist?.name }}」吗？</p>
      <p style="color: rgba(255, 255, 255, 0.6); margin-top: 8px;">此操作不可恢复</p>
      <template #footer>
        <el-button @click="showDeleteConfirm = false">取消</el-button>
        <el-button type="danger" @click="confirmDelete">确定删除</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { usePlayerStore } from '@/stores/player'
import { playlistApi } from '@/api'
import { ElMessage } from 'element-plus'
import {
  Plus,
  Loading,
  Collection,
  VideoPlay,
  MoreFilled
} from '@element-plus/icons-vue'

const router = useRouter()
const playerStore = usePlayerStore()

const loading = ref(false)
const playlists = ref([])
const showCreateDialog = ref(false)
const showDeleteConfirm = ref(false)
const deletingPlaylist = ref(null)
const newPlaylist = ref({
  name: '',
  description: ''
})

async function loadPlaylists() {
  loading.value = true
  try {
    const res = await playlistApi.getAll()
    playlists.value = res.data
  } catch (e) {
    ElMessage.error('加载歌单失败')
  } finally {
    loading.value = false
  }
}

function goToPlaylist(playlist) {
  router.push(`/playlists/${playlist.id}`)
}

async function playPlaylist(playlist) {
  try {
    const res = await playlistApi.getSongs(playlist.id)
    if (res.data.length > 0) {
      playerStore.playList(res.data)
    } else {
      ElMessage.warning('歌单中没有歌曲')
    }
  } catch (e) {
    ElMessage.error('获取歌单歌曲失败')
  }
}

async function createPlaylist() {
  if (!newPlaylist.value.name.trim()) {
    ElMessage.warning('请输入歌单名称')
    return
  }
  
  try {
    await playlistApi.create(newPlaylist.value.name.trim(), newPlaylist.value.description)
    ElMessage.success('创建成功')
    showCreateDialog.value = false
    newPlaylist.value = { name: '', description: '' }
    await loadPlaylists()
  } catch (e) {
    ElMessage.error('创建失败')
  }
}

function handleCommand(command, playlist) {
  if (command === 'play') {
    playPlaylist(playlist)
  } else if (command === 'delete') {
    deletingPlaylist.value = playlist
    showDeleteConfirm.value = true
  }
}

async function confirmDelete() {
  if (!deletingPlaylist.value) return
  
  try {
    await playlistApi.delete(deletingPlaylist.value.id)
    ElMessage.success('删除成功')
    showDeleteConfirm.value = false
    deletingPlaylist.value = null
    await loadPlaylists()
  } catch (e) {
    ElMessage.error('删除失败')
  }
}

onMounted(() => {
  loadPlaylists()
})
</script>

<style lang="scss" scoped>
.playlists-page {
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

.playlist-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 24px;
}

.playlist-card {
  position: relative;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 12px;
  padding: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
  
  &:hover {
    background: rgba(255, 255, 255, 0.1);
    transform: translateY(-4px);
    
    .cover-overlay {
      opacity: 1;
    }
  }
  
  .more-btn {
    position: absolute;
    top: 8px;
    right: 8px;
    padding: 4px;
  }
}

.playlist-cover {
  width: 100%;
  padding-bottom: 100%;
  position: relative;
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 12px;
  
  .cover-placeholder {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
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

.playlist-info {
  .playlist-name {
    font-size: 14px;
    font-weight: 600;
    margin-bottom: 4px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
  
  .playlist-desc {
    font-size: 12px;
    color: rgba(255, 255, 255, 0.6);
    margin-bottom: 4px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
  
  .playlist-count {
    font-size: 12px;
    color: rgba(255, 255, 255, 0.5);
  }
}
</style>
