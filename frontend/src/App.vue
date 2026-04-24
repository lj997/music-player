<template>
  <div class="app-container" :style="wallpaperStyle">
    <div class="app-main">
      <router-view />
    </div>
    <Player />
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { usePlayerStore } from '@/stores/player'
import { useWallpaperStore } from '@/stores/wallpaper'
import Player from '@/components/Player.vue'

const playerStore = usePlayerStore()
const wallpaperStore = useWallpaperStore()

const wallpaperStyle = computed(() => {
  if (wallpaperStore.currentWallpaper) {
    return {
      backgroundImage: `url(${wallpaperStore.currentWallpaper.filePath})`,
      backgroundSize: 'cover',
      backgroundPosition: 'center',
      backgroundAttachment: 'fixed'
    }
  }
  return {
    background: 'linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%)'
  }
})

onMounted(() => {
  wallpaperStore.fetchCurrentWallpaper()
})
</script>

<style lang="scss" scoped>
.app-container {
  min-height: 100vh;
  width: 100%;
  position: relative;
}

.app-main {
  padding-bottom: 100px;
  min-height: 100vh;
  box-sizing: border-box;
}
</style>