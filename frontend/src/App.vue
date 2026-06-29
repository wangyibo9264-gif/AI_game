<template>
  <div class="app-shell">
    <header class="topbar" aria-label="&#x5C18;&#x9547;&#x6863;&#x6848;&#x5BFC;&#x822A;">
      <RouterLink class="brand" to="/">
        <span class="brand-mark" aria-hidden="true"></span>
        <span>&#x5C18;&#x9547;&#x6863;&#x6848;</span>
      </RouterLink>
      <nav class="topbar-links" aria-label="&#x4E3B;&#x8981;&#x5BFC;&#x822A;">
        <RouterLink to="/">&#x6848;&#x4EF6;&#x67DC;</RouterLink>
      </nav>
    </header>

    <main class="app-main">
      <RouterView />
    </main>
  </div>
</template>