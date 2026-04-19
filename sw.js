const CACHE_NAME = 'EQ';

// Files to cache (adjust if needed)
const ASSETS = [
'/EQ/',
'/EQ/index.html'
];

// Install: cache app shell
self.addEventListener('install', event => {
event.waitUntil(
caches.open(CACHE_NAME).then(cache => {
return cache.addAll(ASSETS);
})
);
self.skipWaiting();
});

// Activate: clean old caches
self.addEventListener('activate', event => {
event.waitUntil(
caches.keys().then(keys => {
return Promise.all(
keys.filter(key => key !== CACHE_NAME)
.map(key => caches.delete(key))
);
})
);
self.clients.claim();
});

// Fetch: cache-first strategy
self.addEventListener('fetch', event => {
event.respondWith(
caches.match(event.request).then(response => {
return response || fetch(event.request).then(networkResponse => {
return caches.open(CACHE_NAME).then(cache => {
cache.put(event.request, networkResponse.clone());
return networkResponse;
});
});
}).catch(() => {
return caches.match('/EQ/index.html');
})
);
});
