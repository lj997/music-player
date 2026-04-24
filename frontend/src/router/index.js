import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/library'
  },
  {
    path: '/library',
    name: 'Library',
    component: () => import('@/views/Library.vue'),
    meta: { title: '音乐库' }
  },
  {
    path: '/search',
    name: 'Search',
    component: () => import('@/views/Search.vue'),
    meta: { title: '搜索' }
  },
  {
    path: '/playlists',
    name: 'Playlists',
    component: () => import('@/views/Playlists.vue'),
    meta: { title: '歌单' }
  },
  {
    path: '/playlists/:id',
    name: 'PlaylistDetail',
    component: () => import('@/views/PlaylistDetail.vue'),
    meta: { title: '歌单详情' }
  },
  {
    path: '/history',
    name: 'History',
    component: () => import('@/views/History.vue'),
    meta: { title: '最近播放' }
  },
  {
    path: '/favorites',
    name: 'Favorites',
    component: () => import('@/views/Favorites.vue'),
    meta: { title: '我的收藏' }
  },
  {
    path: '/settings',
    name: 'Settings',
    component: () => import('@/views/Settings.vue'),
    meta: { title: '设置' }
  },
  {
    path: '/artists',
    name: 'Artists',
    component: () => import('@/views/Artists.vue'),
    meta: { title: '艺术家' }
  },
  {
    path: '/artist/:name',
    name: 'ArtistDetail',
    component: () => import('@/views/ArtistDetail.vue'),
    meta: { title: '艺术家详情' }
  },
  {
    path: '/albums',
    name: 'Albums',
    component: () => import('@/views/Albums.vue'),
    meta: { title: '专辑' }
  },
  {
    path: '/album/:name',
    name: 'AlbumDetail',
    component: () => import('@/views/AlbumDetail.vue'),
    meta: { title: '专辑详情' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - 本地音乐播放器` : '本地音乐播放器'
  next()
})

export default router