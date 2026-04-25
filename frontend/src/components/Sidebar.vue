<template>
  <div class="sidebar">
    <div class="logo">
      <el-icon :size="32"><Collection /></el-icon>
      <span>音乐播放器</span>
    </div>
    
    <div class="nav-section">
      <div class="nav-title">库</div>
      <router-link to="/library" class="nav-item" active-class="active">
        <el-icon><FolderOpened /></el-icon>
        <span>音乐库</span>
      </router-link>
      <router-link to="/artists" class="nav-item" active-class="active">
        <el-icon><User /></el-icon>
        <span>艺术家</span>
      </router-link>
      <router-link to="/albums" class="nav-item" active-class="active">
        <el-icon><Picture /></el-icon>
        <span>专辑</span>
      </router-link>
    </div>
    
    <div class="nav-section">
      <div class="nav-title">我的</div>
      <router-link to="/playlists" class="nav-item" active-class="active">
        <el-icon><Collection /></el-icon>
        <span>歌单</span>
        <el-button type="text" class="add-btn" @click.stop="showCreatePlaylist = true">
          <el-icon><Plus /></el-icon>
        </el-button>
      </router-link>
      <router-link to="/favorites" class="nav-item" active-class="active">
        <el-icon><StarFilled /></el-icon>
        <span>我的收藏</span>
      </router-link>
      <router-link to="/history" class="nav-item" active-class="active">
        <el-icon><Refresh /></el-icon>
        <span>最近播放</span>
      </router-link>
    </div>
    
    <div class="nav-section">
      <div class="nav-title">设置</div>
      <router-link to="/settings" class="nav-item" active-class="active">
        <el-icon><Setting /></el-icon>
        <span>设置</span>
      </router-link>
    </div>
  </div>
  
  <el-dialog v-model="showCreatePlaylist" title="创建歌单" width="400px">
    <el-form :model="newPlaylist" label-width="80px">
      <el-form-item label="名称">
        <el-input v-model="newPlaylist.name" placeholder="请输入歌单名称" />
      </el-form-item>
      <el-form-item label="描述">
        <el-input 
          v-model="newPlaylist.description" 
          type="textarea" 
          :rows="3"
          placeholder="请输入歌单描述（可选）" 
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="showCreatePlaylist = false">取消</el-button>
      <el-button type="primary" @click="createPlaylist">创建</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { playlistApi } from '@/api'
import { ElMessage } from 'element-plus'
import {
  FolderOpened,
  User,
  Picture,
  Collection,
  StarFilled,
  Refresh,
  Setting,
  Plus
} from '@element-plus/icons-vue'

const router = useRouter()
const showCreatePlaylist = ref(false)
const newPlaylist = ref({
  name: '',
  description: ''
})

async function createPlaylist() {
  if (!newPlaylist.value.name.trim()) {
    ElMessage.warning('请输入歌单名称')
    return
  }
  
  try {
    await playlistApi.create(newPlaylist.value.name.trim(), newPlaylist.value.description)
    ElMessage.success('创建成功')
    showCreatePlaylist.value = false
    newPlaylist.value = { name: '', description: '' }
    router.push('/playlists')
  } catch (e) {
    ElMessage.error('创建失败')
  }
}
</script>

<style lang="scss" scoped>
.sidebar {
  position: fixed;
  left: 0;
  top: 0;
  bottom: 90px;
  width: 240px;
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(20px);
  border-right: 1px solid rgba(255, 255, 255, 0.1);
  padding: 20px 0;
  overflow-y: auto;
  z-index: 999;
}

.logo {
  display: flex;
  align-items: center;
  padding: 0 24px;
  margin-bottom: 30px;
  font-size: 18px;
  font-weight: 600;
  color: #fff;
  
  .el-icon {
    margin-right: 10px;
    color: #409eff;
  }
}

.nav-section {
  margin-bottom: 24px;
}

.nav-title {
  font-size: 12px;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.5);
  padding: 0 24px;
  margin-bottom: 8px;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.nav-item {
  display: flex;
  align-items: center;
  padding: 12px 24px;
  color: rgba(255, 255, 255, 0.7);
  text-decoration: none;
  transition: all 0.2s ease;
  position: relative;
  
  .el-icon {
    margin-right: 12px;
    font-size: 18px;
  }
  
  &:hover {
    color: #fff;
    background: rgba(255, 255, 255, 0.05);
  }
  
  &.active {
    color: #409eff;
    
    &::before {
      content: '';
      position: absolute;
      left: 0;
      top: 50%;
      transform: translateY(-50%);
      width: 4px;
      height: 20px;
      background: #409eff;
      border-radius: 0 2px 2px 0;
    }
  }
  
  .add-btn {
    margin-left: auto;
    padding: 4px;
    color: rgba(255, 255, 255, 0.5);
    
    &:hover {
      color: #409eff;
    }
  }
}
</style>