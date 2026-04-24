import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { songApi, historyApi } from '@/api'

export const usePlayerStore = defineStore('player', () => {
  const audio = ref(null)
  const currentSong = ref(null)
  const playlist = ref([])
  const currentIndex = ref(0)
  const isPlaying = ref(false)
  const currentTime = ref(0)
  const duration = ref(0)
  const volume = ref(0.8)
  const playMode = ref('sequence')

  const modes = [
    { value: 'sequence', label: '顺序播放', icon: 'Sort' },
    { value: 'random', label: '随机播放', icon: 'Share' },
    { value: 'loop', label: '单曲循环', icon: 'RefreshRight' }
  ]

  const currentMode = computed(() => modes.find(m => m.value === playMode.value))

  function initAudio() {
    if (audio.value) return
    audio.value = new Audio()
    audio.value.volume = volume.value
    
    audio.value.addEventListener('timeupdate', () => {
      currentTime.value = audio.value.currentTime
    })
    
    audio.value.addEventListener('durationchange', () => {
      duration.value = audio.value.duration
    })
    
    audio.value.addEventListener('ended', () => {
      playNext()
    })
    
    audio.value.addEventListener('error', (e) => {
      console.error('Audio error:', e)
      isPlaying.value = false
    })
  }

  async function playSong(song, songs = null) {
    initAudio()
    
    if (songs && songs.length > 0) {
      playlist.value = songs
      currentIndex.value = songs.findIndex(s => s.id === song.id)
      if (currentIndex.value === -1) currentIndex.value = 0
    }
    
    currentSong.value = song
    
    try {
      await historyApi.record(song.id)
    } catch (e) {
      console.error('Failed to record history:', e)
    }
    
    audio.value.src = songApi.streamUrl(song.id)
    audio.value.load()
    try {
      await audio.value.play()
      isPlaying.value = true
    } catch (e) {
      console.error('Play failed:', e)
      isPlaying.value = false
    }
  }

  function togglePlay() {
    if (!audio.value || !currentSong.value) return
    
    if (isPlaying.value) {
      audio.value.pause()
      isPlaying.value = false
    } else {
      audio.value.play()
      isPlaying.value = true
    }
  }

  function playPrev() {
    if (playlist.value.length === 0) return
    
    if (playMode.value === 'random') {
      currentIndex.value = Math.floor(Math.random() * playlist.value.length)
    } else {
      currentIndex.value = (currentIndex.value - 1 + playlist.value.length) % playlist.value.length
    }
    
    playSong(playlist.value[currentIndex.value], playlist.value)
  }

  function playNext() {
    if (playlist.value.length === 0) return
    
    if (playMode.value === 'loop') {
      audio.value.currentTime = 0
      audio.value.play()
    } else if (playMode.value === 'random') {
      currentIndex.value = Math.floor(Math.random() * playlist.value.length)
      playSong(playlist.value[currentIndex.value], playlist.value)
    } else {
      currentIndex.value = (currentIndex.value + 1) % playlist.value.length
      playSong(playlist.value[currentIndex.value], playlist.value)
    }
  }

  function seek(time) {
    if (audio.value) {
      audio.value.currentTime = time
      currentTime.value = time
    }
  }

  function setVolume(vol) {
    volume.value = vol
    if (audio.value) {
      audio.value.volume = vol
    }
  }

  function toggleMode() {
    const index = modes.findIndex(m => m.value === playMode.value)
    playMode.value = modes[(index + 1) % modes.length].value
  }

  function setMode(mode) {
    if (modes.some(m => m.value === mode)) {
      playMode.value = mode
    }
  }

  function playList(songs, startIndex = 0) {
    if (songs.length === 0) return
    playlist.value = songs
    currentIndex.value = startIndex
    playSong(songs[startIndex], songs)
  }

  return {
    audio,
    currentSong,
    playlist,
    currentIndex,
    isPlaying,
    currentTime,
    duration,
    volume,
    playMode,
    modes,
    currentMode,
    initAudio,
    playSong,
    togglePlay,
    playPrev,
    playNext,
    seek,
    setVolume,
    toggleMode,
    setMode,
    playList
  }
})