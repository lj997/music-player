import { defineStore } from 'pinia'
import { ref } from 'vue'
import { wallpaperApi } from '@/api'

export const useWallpaperStore = defineStore('wallpaper', () => {
  const wallpapers = ref([])
  const currentWallpaper = ref(null)

  async function fetchAll() {
    try {
      const res = await wallpaperApi.getAll()
      wallpapers.value = res.data
    } catch (e) {
      console.error('Failed to fetch wallpapers:', e)
    }
  }

  async function fetchCurrentWallpaper() {
    try {
      const res = await wallpaperApi.getCurrent()
      currentWallpaper.value = res.data
    } catch (e) {
      currentWallpaper.value = null
    }
  }

  async function upload(name, file) {
    try {
      const formData = new FormData()
      formData.append('name', name)
      formData.append('file', file)
      const res = await wallpaperApi.upload(formData)
      await fetchAll()
      await fetchCurrentWallpaper()
      return res.data
    } catch (e) {
      console.error('Failed to upload wallpaper:', e)
      throw e
    }
  }

  async function setCurrent(id) {
    try {
      await wallpaperApi.setCurrent(id)
      await fetchAll()
      await fetchCurrentWallpaper()
    } catch (e) {
      console.error('Failed to set current wallpaper:', e)
      throw e
    }
  }

  async function deleteWallpaper(id) {
    try {
      await wallpaperApi.delete(id)
      await fetchAll()
      await fetchCurrentWallpaper()
    } catch (e) {
      console.error('Failed to delete wallpaper:', e)
      throw e
    }
  }

  return {
    wallpapers,
    currentWallpaper,
    fetchAll,
    fetchCurrentWallpaper,
    upload,
    setCurrent,
    deleteWallpaper
  }
})