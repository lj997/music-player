import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { songApi, folderApi, playlistApi, favoriteApi, wallpaperApi, historyApi } from '@/api'

export const useMainStore = defineStore('main', () => {
  const songs = ref([])
  const artists = ref([])
  const albums = ref([])
  const playlists = ref([])
  const favorites = ref([])
  const history = ref([])
  const folders = ref([])
  const wallpapers = ref([])
  const currentWallpaper = ref(null)
  const loading = ref(false)
  const sidebarCollapsed = ref(false)

  const songCount = computed(() => songs.value.length)

  async function fetchSongs(keyword = null) {
    loading.value = true
    try {
      const res = await songApi.getAll(keyword)
      songs.value = res.data
    } catch (e) {
      console.error('Failed to fetch songs:', e)
    } finally {
      loading.value = false
    }
  }

  async function fetchArtists() {
    try {
      const res = await songApi.getAllArtists()
      artists.value = res.data
    } catch (e) {
      console.error('Failed to fetch artists:', e)
    }
  }

  async function fetchAlbums() {
    try {
      const res = await songApi.getAllAlbums()
      albums.value = res.data
    } catch (e) {
      console.error('Failed to fetch albums:', e)
    }
  }

  async function fetchPlaylists() {
    try {
      const res = await playlistApi.getAll()
      playlists.value = res.data
    } catch (e) {
      console.error('Failed to fetch playlists:', e)
    }
  }

  async function fetchFavorites() {
    try {
      const res = await favoriteApi.getAll()
      favorites.value = res.data
    } catch (e) {
      console.error('Failed to fetch favorites:', e)
    }
  }

  async function fetchHistory(limit = 30) {
    try {
      const res = await historyApi.getRecent(limit)
      history.value = res.data
    } catch (e) {
      console.error('Failed to fetch history:', e)
    }
  }

  async function fetchFolders() {
    try {
      const res = await folderApi.getAll()
      folders.value = res.data
    } catch (e) {
      console.error('Failed to fetch folders:', e)
    }
  }

  async function fetchWallpapers() {
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

  async function toggleFavorite(songId) {
    const isFav = favorites.value.some(s => s.id === songId)
    try {
      if (isFav) {
        await favoriteApi.remove(songId)
        favorites.value = favorites.value.filter(s => s.id !== songId)
      } else {
        await favoriteApi.add(songId)
        await fetchFavorites()
      }
      return !isFav
    } catch (e) {
      console.error('Failed to toggle favorite:', e)
      return isFav
    }
  }

  function toggleSidebar() {
    sidebarCollapsed.value = !sidebarCollapsed.value
  }

  return {
    songs,
    artists,
    albums,
    playlists,
    favorites,
    history,
    folders,
    wallpapers,
    currentWallpaper,
    loading,
    sidebarCollapsed,
    songCount,
    fetchSongs,
    fetchArtists,
    fetchAlbums,
    fetchPlaylists,
    fetchFavorites,
    fetchHistory,
    fetchFolders,
    fetchWallpapers,
    fetchCurrentWallpaper,
    toggleFavorite,
    toggleSidebar
  }
})