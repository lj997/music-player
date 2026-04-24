<template>
  <div class="settings-page">
    <Sidebar />
    
    <div class="main-content">
      <div class="page-header">
        <h1>设置</h1>
      </div>
      
      <div class="settings-section">
        <div class="section-title">音乐文件夹</div>
        <div class="section-content">
          <div class="folder-list">
            <div v-for="folder in folders" :key="folder.id" class="folder-item">
              <span class="folder-path">{{ folder.folderPath }}</span>
              <div class="folder-actions">
                <el-button type="text" size="small" @click="scanFolder(folder)">
                  <el-icon><Refresh /></el-icon>
                  扫描
                </el-button>
                <el-button 
                  type="text" 
                  size="small" 
                  style="color: #f56c6c"
                  @click="deleteFolder(folder)"
                >
                  <el-icon><Delete /></el-icon>
                  删除
                </el-button>
              </div>
            </div>
          </div>
          
          <div class="add-folder">
            <el-input 
              v-model="newFolderPath" 
              placeholder="请输入文件夹绝对路径"
              style="width: 400px"
            />
            <el-button type="primary" @click="addFolder">
              <el-icon><Plus /></el-icon>
              添加文件夹
            </el-button>
          </div>
          
          <div class="scan-all">
            <el-button type="primary" :loading="scanning" @click="scanAll">
              <el-icon><Refresh /></el-icon>
              扫描全部文件夹
            </el-button>
            <span class="scan-hint">扫描会更新音乐库中的歌曲信息</span>
          </div>
        </div>
      </div>
      
      <div class="settings-section">
        <div class="section-title">主题壁纸</div>
        <div class="section-content">
          <div class="wallpaper-grid">
            <div 
              v-for="wallpaper in wallpapers" 
              :key="wallpaper.id"
              class="wallpaper-item"
              :class="{ 'current': wallpaper.isCurrent }"
              @click="setCurrentWallpaper(wallpaper)"
            >
              <img :src="wallpaper.filePath" :alt="wallpaper.name" />
              <div class="wallpaper-info">
                <span>{{ wallpaper.name }}</span>
                <el-tag v-if="wallpaper.isCurrent" type="primary" size="small">当前</el-tag>
              </div>
              <el-button 
                type="danger" 
                size="small" 
                circle
                class="delete-btn"
                @click.stop="deleteWallpaper(wallpaper)"
              >
                <el-icon><Close /></el-icon>
              </el-button>
            </div>
            
            <div class="wallpaper-upload" @click="triggerUpload">
              <el-icon :size="48"><Plus /></el-icon>
              <span>上传壁纸</span>
              <input 
                ref="fileInput" 
                type="file" 
                accept="image/*" 
                style="display: none"
                @change="handleFileChange"
              />
            </div>
          </div>
        </div>
      </div>
      
      <div class="settings-section">
        <div class="section-title">播放设置</div>
        <div class="section-content">
          <div class="setting-item">
            <div class="setting-info">
              <div class="setting-name">默认播放模式</div>
              <div class="setting-desc">选择播放器的默认播放模式</div>
            </div>
            <el-select v-model="playMode" style="width: 140px" @change="handleModeChange">
              <el-option label="顺序播放" value="sequence" />
              <el-option label="随机播放" value="random" />
              <el-option label="单曲循环" value="loop" />
            </el-select>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { usePlayerStore } from '@/stores/player'
import { useWallpaperStore } from '@/stores/wallpaper'
import { useMainStore } from '@/stores/main'
import { folderApi } from '@/api'
import { ElMessage } from 'element-plus'
import Sidebar from '@/components/Sidebar.vue'
import {
  Refresh,
  Delete,
  Plus,
  Close
} from '@element-plus/icons-vue'

const playerStore = usePlayerStore()
const wallpaperStore = useWallpaperStore()
const mainStore = useMainStore()

const folders = ref([])
const newFolderPath = ref('')
const scanning = ref(false)
const wallpapers = ref([])
const playMode = ref(playerStore.playMode)
const fileInput = ref(null)

async function loadFolders() {
  try {
    const res = await folderApi.getAll()
    folders.value = res.data
  } catch (e) {
    ElMessage.error('加载文件夹失败')
  }
}

async function loadWallpapers() {
  await wallpaperStore.fetchAll()
  wallpapers.value = wallpaperStore.wallpapers
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
    ElMessage.error('添加失败：请检查路径是否正确')
  }
}

async function scanFolder(folder) {
  try {
    scanning.value = true
    ElMessage.info('开始扫描...')
    await folderApi.scanFolder(folder.id)
    ElMessage.success('扫描完成')
    await mainStore.fetchSongs()
  } catch (e) {
    ElMessage.error('扫描失败')
  } finally {
    scanning.value = false
  }
}

async function scanAll() {
  if (folders.value.length === 0) {
    ElMessage.warning('请先添加音乐文件夹')
    return
  }
  
  try {
    scanning.value = true
    ElMessage.info('开始扫描...')
    await folderApi.scanAll()
    ElMessage.success('扫描完成')
    await mainStore.fetchSongs()
  } catch (e) {
    ElMessage.error('扫描失败')
  } finally {
    scanning.value = false
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

function triggerUpload() {
  fileInput.value?.click()
}

async function handleFileChange(e) {
  const file = e.target.files?.[0]
  if (!file) return
  
  try {
    const name = file.name.replace(/\.[^/.]+$/, '')
    await wallpaperStore.upload(name, file)
    ElMessage.success('上传成功')
    await loadWallpapers()
  } catch (e) {
    ElMessage.error('上传失败')
  } finally {
    if (fileInput.value) {
      fileInput.value.value = ''
    }
  }
}

async function setCurrentWallpaper(wallpaper) {
  if (wallpaper.isCurrent) return
  
  try {
    await wallpaperStore.setCurrent(wallpaper.id)
    ElMessage.success('已设置为当前壁纸')
    await loadWallpapers()
  } catch (e) {
    ElMessage.error('设置失败')
  }
}

async function deleteWallpaper(wallpaper) {
  try {
    await wallpaperStore.deleteWallpaper(wallpaper.id)
    ElMessage.success('删除成功')
    await loadWallpapers()
  } catch (e) {
    ElMessage.error('删除失败')
  }
}

function handleModeChange(val) {
  playerStore.setMode(val)
}

onMounted(() => {
  loadFolders()
  loadWallpapers()
})
</script>

<style lang="scss" scoped>
.settings-page {
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
  margin-bottom: 32px;
  
  h1 {
    font-size: 28px;
    font-weight: 600;
  }
}

.settings-section {
  margin-bottom: 40px;
  
  .section-title {
    font-size: 16px;
    font-weight: 600;
    margin-bottom: 16px;
    padding-bottom: 8px;
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  }
  
  .section-content {
    padding-left: 8px;
  }
}

.folder-list {
  margin-bottom: 20px;
}

.folder-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 8px;
  margin-bottom: 8px;
  
  .folder-path {
    font-family: 'Consolas', monospace;
    font-size: 13px;
    word-break: break-all;
  }
}

.add-folder {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.scan-all {
  display: flex;
  align-items: center;
  gap: 16px;
  
  .scan-hint {
    font-size: 13px;
    color: rgba(255, 255, 255, 0.5);
  }
}

.wallpaper-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
}

.wallpaper-item {
  position: relative;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.2s ease;
  
  &:hover {
    transform: scale(1.02);
    
    .delete-btn {
      opacity: 1;
    }
  }
  
  &.current {
    outline: 3px solid #409eff;
  }
  
  img {
    width: 100%;
    height: 120px;
    object-fit: cover;
  }
  
  .wallpaper-info {
    padding: 10px 12px;
    background: rgba(0, 0, 0, 0.6);
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    span {
      font-size: 13px;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }
  }
  
  .delete-btn {
    position: absolute;
    top: 8px;
    right: 8px;
    opacity: 0;
    transition: opacity 0.2s ease;
  }
}

.wallpaper-upload {
  height: 166px;
  border: 2px dashed rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
  
  &:hover {
    border-color: #409eff;
    background: rgba(64, 158, 255, 0.05);
  }
  
  span {
    margin-top: 8px;
    font-size: 13px;
    color: rgba(255, 255, 255, 0.6);
  }
}

.setting-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 8px;
  
  .setting-name {
    font-size: 14px;
    font-weight: 500;
  }
  
  .setting-desc {
    font-size: 12px;
    color: rgba(255, 255, 255, 0.5);
    margin-top: 4px;
  }
}
</style>