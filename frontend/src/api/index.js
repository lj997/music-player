import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 30000
})

export const songApi = {
  getAll: (keyword) => api.get('/songs', { params: { keyword } }),
  getById: (id) => api.get(`/songs/${id}`),
  getAllArtists: () => api.get('/songs/artists'),
  getAllAlbums: () => api.get('/songs/albums'),
  getByArtist: (artist) => api.get(`/songs/artist/${encodeURIComponent(artist)}`),
  getByAlbum: (album) => api.get(`/songs/album/${encodeURIComponent(album)}`),
  groupByArtist: () => api.get('/songs/group/artist'),
  groupByAlbum: () => api.get('/songs/group/album'),
  count: () => api.get('/songs/count'),
  streamUrl: (id) => `/api/songs/stream/${id}`
}

export const folderApi = {
  getAll: () => api.get('/folders'),
  add: (folderPath) => api.post('/folders', { folderPath }),
  delete: (id) => api.delete(`/folders/${id}`),
  scanAll: () => api.post('/folders/scan'),
  scanFolder: (id) => api.post(`/folders/scan/${id}`)
}

export const playlistApi = {
  getAll: () => api.get('/playlists'),
  getById: (id) => api.get(`/playlists/${id}`),
  create: (name, description) => api.post('/playlists', { name, description }),
  delete: (id) => api.delete(`/playlists/${id}`),
  getSongs: (id) => api.get(`/playlists/${id}/songs`),
  addSong: (playlistId, songId) => api.post(`/playlists/${playlistId}/songs/${songId}`),
  removeSong: (playlistId, songId) => api.delete(`/playlists/${playlistId}/songs/${songId}`)
}

export const historyApi = {
  record: (songId) => api.post(`/history/${songId}`),
  getRecent: (limit) => api.get('/history', { params: { limit } })
}

export const favoriteApi = {
  add: (songId) => api.post(`/favorites/${songId}`),
  remove: (songId) => api.delete(`/favorites/${songId}`),
  isFavorite: (songId) => api.get(`/favorites/${songId}`),
  getAll: () => api.get('/favorites')
}

export const wallpaperApi = {
  getAll: () => api.get('/wallpapers'),
  getCurrent: () => api.get('/wallpapers/current'),
  upload: (formData) => api.post('/wallpapers', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  }),
  setCurrent: (id) => api.put(`/wallpapers/${id}/current`),
  delete: (id) => api.delete(`/wallpapers/${id}`)
}

export default api