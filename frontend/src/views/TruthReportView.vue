<template>
  <section class="placeholder-view" aria-label="&#x771F;&#x76F8;&#x62A5;&#x544A;">
    <p class="placeholder-kicker">&#x771F;&#x76F8;&#x62A5;&#x544A;</p>
    <h1 class="placeholder-title">&#x6574;&#x7406;&#x4F1A;&#x8BDD; #{{ sessionId }} &#x7684;&#x8BC1;&#x8BCD;&#x3002;</h1>
    <p class="placeholder-copy">&#x62A5;&#x544A;&#x8868;&#x5355;&#x5C06;&#x5728; Task 13 &#x4E2D;&#x5B9E;&#x73B0;&#x3002;</p>
  </section>
</template>

<script setup lang="ts">
defineProps<{ sessionId: string }>();
</script>