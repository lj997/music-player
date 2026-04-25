<template>
  <div class="library-page">
    <div class="page-header">
      <h1>音乐库</h1>
      <div class="header-actions">
        <el-input 
          v-model="searchKeyword" 
          placeholder="搜索歌曲、艺术家或专辑" 
          style="width: 300px"
          clearable
          @clear="searchSongs"
          @keyup.enter="searchSongs"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-button type="primary" @click="showScanDialog = true">
          <el-icon><FolderAdd /></el-icon>
          添加音乐文件夹
        </el-button>
      </div>
    </div>
    
    <div class="view-tabs">
      <el-radio-group v-model="viewMode" @change="loadSongs">
        <el-radio-button value="all">全部歌曲</el-radio-button>
        <el-radio-button value="artist">按艺术家</el-radio-button>
        <el-radio-button value="album">按专辑</el-radio-button>
      </el-radio-group>
      
      <el-button 
        type="primary" 
        :disabled="songs.length === 0"
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
        v-if="viewMode === 'all'"
        :songs="songs"
      />
      
      <div v-else-if="viewMode === 'artist'" class="group-view">
        <div 
          v-for="(groupSongs, artist) in groupedByArtist" 
          :key="artist"
          class="artist-group"
        >
          <div class="group-header" @click="toggleArtistGroup(artist)">
            <el-icon class="expand-icon">
              <component :is="expandedArtists.includes(artist) ? 'ArrowDown' : 'ArrowRight'" />
            </el-icon>
            <div class="group-cover">
              <img 
                v-if="groupSongs[0]?.coverPath"
                :src="groupSongs[0].coverPath" 
                :alt="artist"
              />
            </div>
            <div class="group-info">
              <h3>{{ artist }}</h3>
              <p>{{ groupSongs.length }} 首歌曲</p>
            </div>
            <el-button type="primary" size="small" @click.stop="playGroup(groupSongs)">
              播放全部
            </el-button>
          </div>
          <div v-if="expandedArtists.includes(artist)" class="group-content">
            <SongTable :songs="groupSongs" />
          </div>
        </div>
      </div>
      
      <div v-else-if="viewMode === 'album'" class="group-view">
        <div 
          v-for="(groupSongs, album) in groupedByAlbum" 
          :key="album"
          class="album-group"
        >
          <div class="group-header" @click="toggleAlbumGroup(album)">
            <el-icon class="expand-icon">
              <component :is="expandedAlbums.includes(album) ? 'ArrowDown' : 'ArrowRight'" />
            </el-icon>
            <div class="group-cover">
              <img 
                v-if="groupSongs[0]?.coverPath"
                :src="groupSongs[0].coverPath" 
                :alt="album"
              />
            </div>
            <div class="group-info">
              <h3>{{ album }}</h3>
              <p>{{ groupSongs[0]?.artist }} · {{ groupSongs.length }} 首歌曲</p>
            </div>
            <el-button type="primary" size="small" @click.stop="playGroup(groupSongs)">
              播放全部
            </el-button>
          </div>
          <div v-if="expandedAlbums.includes(album)" class="group-content">
            <SongTable :songs="groupSongs" />
          </div>
        </div>
      </div>
      
      <el-empty v-if="songs.length === 0 && viewMode === 'all'" description="暂无音乐，请先添加音乐文件夹">
        <el-button type="primary" @click="showScanDialog = true">添加音乐文件夹</el-button>
      </el-empty>
    </template>
    
    <el-dialog v-model="showScanDialog" title="添加音乐文件夹" width="500px">
      <div class="folder-config">
        <el-alert 
          v-if="folders.length > 0"
          title="已配置的文件夹" 
          type="info" 
          :closable="false"
          style="margin-bottom: 16px"
        />
        <div class="folder-list">
          <div v-for="folder in folders" :key="folder.id" class="folder-item">
            <span class="folder-path">{{ folder.folderPath }}</span>
            <div class="folder-actions">
              <el-button type="text" size="small" @click="scanFolder(folder)">
                扫描
              </el-button>
              <el-button type="text" size="small" style="color: #f56c6c" @click="deleteFolder(folder)">
                删除
              </el-button>
            </div>
          </div>
        </div>
        
        <el-divider />
        
        <el-form label-width="100px">
          <el-form-item label="文件夹路径">
            <el-input v-model="newFolderPath" placeholder="请输入文件夹绝对路径" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="addFolder">添加文件夹</el-button>
            <el-button type="success" @click="scanAll">扫描全部</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { usePlayerStore } from '@/stores/player'
import { useMainStore } from '@/stores/main'
import { folderApi, songApi } from '@/api'
import { ElMessage } from 'element-plus'
import SongTable from '@/components/SongTable.vue'
import {
  Search,
  FolderAdd,
  VideoPlay,
  Loading,
  ArrowDown,
  ArrowRight
} from '@element-plus/icons-vue'

const playerStore = usePlayerStore()
const mainStore = useMainStore()

const searchKeyword = ref('')
const viewMode = ref('all')
const loading = ref(false)
const songs = ref([])
const groupedByArtist = ref({})
const groupedByAlbum = ref({})
const expandedArtists = ref([])
const expandedAlbums = ref([])
const showScanDialog = ref(false)
const folders = ref([])
const newFolderPath = ref('')

async function loadSongs() {
  loading.value = true
  try {
    const keyword = searchKeyword.value.trim()
    const res = await songApi.getAll(keyword || null)
    songs.value = res.data
    
    if (viewMode.value !== 'all') {
      const groupRes = await songApi[viewMode.value === 'artist' ? 'groupByArtist' : 'groupByAlbum']()
      if (viewMode.value === 'artist') {
        groupedByArtist.value = groupRes.data
      } else {
        groupedByAlbum.value = groupRes.data
      }
    }
  } catch (e) {
    ElMessage.error('加载歌曲失败')
  } finally {
    loading.value = false
  }
}

function searchSongs() {
  loadSongs()
}

function playAll() {
  if (songs.value.length > 0) {
    playerStore.playList(songs.value)
  }
}

function playGroup(groupSongs) {
  if (groupSongs.length > 0) {
    playerStore.playList(groupSongs)
  }
}

function toggleArtistGroup(artist) {
  const index = expandedArtists.value.indexOf(artist)
  if (index > -1) {
    expandedArtists.value.splice(index, 1)
  } else {
    expandedArtists.value.push(artist)
  }
}

function toggleAlbumGroup(album) {
  const index = expandedAlbums.value.indexOf(album)
  if (index > -1) {
    expandedAlbums.value.splice(index, 1)
  } else {
    expandedAlbums.value.push(album)
  }
}

async function loadFolders() {
  try {
    const res = await folderApi.getAll()
    folders.value = res.data
  } catch (e) {
    console.error('Failed to load folders:', e)
  }
}

async function addFolder() {
  if (!newFolderPath.value.trim()) {
    ElMessage.warning('请输入文件夹路径')
    return
  }
  
  try {
    await folderApi.add(newFolderPath.value.trim())
    ElMessage.success('添加成功')
    newFolderPath.value = ''
    await loadFolders()
  } catch (e) {
    ElMessage.error('添加失败：' + (e.response?.data?.message || '请检查路径是否正确'))
  }
}

async function scanFolder(folder) {
  try {
    ElMessage.info('开始扫描...')
    await folderApi.scanFolder(folder.id)
    ElMessage.success('扫描完成')
    await loadSongs()
  } catch (e) {
    ElMessage.error('扫描失败')
  }
}

async function scanAll() {
  if (folders.value.length === 0) {
    ElMessage.warning('请先添加音乐文件夹')
    return
  }
  
  try {
    ElMessage.info('开始扫描...')
    await folderApi.scanAll()
    ElMessage.success('扫描完成')
    await loadSongs()
  } catch (e) {
    ElMessage.error('扫描失败')
  }
}

async function deleteFolder(folder) {
  try {
    await folderApi.delete(folder.id)
    ElMessage.success('删除成功')
    await loadFolders()
  } catch (e) {
    ElMessage.error('删除失败')
  }
}

watch(searchKeyword, () => {
  loadSongs()
})

onMounted(() => {
  loadSongs()
  loadFolders()
  mainStore.fetchFavorites()
})
</script>

<style lang="scss" scoped>
.library-page {
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
  
  .header-actions {
    display: flex;
    gap: 12px;
  }
}

.view-tabs {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
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

.group-view {
  .artist-group, .album-group {
    margin-bottom: 16px;
    background: rgba(255, 255, 255, 0.05);
    border-radius: 12px;
    overflow: hidden;
  }
  
  .group-header {
    display: flex;
    align-items: center;
    padding: 16px 20px;
    cursor: pointer;
    transition: background-color 0.2s ease;
    
    &:hover {
      background: rgba(255, 255, 255, 0.05);
    }
    
    .expand-icon {
      margin-right: 12px;
      color: rgba(255, 255, 255, 0.5);
    }
    
    .group-cover {
      width: 64px;
      height: 64px;
      border-radius: 8px;
      overflow: hidden;
      flex-shrink: 0;
      
      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }
    }
    
    .group-info {
      flex: 1;
      margin-left: 16px;
      
      h3 {
        font-size: 16px;
        font-weight: 600;
        margin-bottom: 4px;
      }
      
      p {
        font-size: 13px;
        color: rgba(255, 255, 255, 0.6);
      }
    }
  }
  
  .group-content {
    padding: 0 20px 16px;
  }
}

.folder-list {
  max-height: 200px;
  overflow-y: auto;
}

.folder-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 12px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 8px;
  margin-bottom: 8px;
  
  .folder-path {
    font-family: 'Consolas', monospace;
    font-size: 13px;
    word-break: break-all;
  }
  
  .folder-actions {
    flex-shrink: 0;
  }
}
</style>