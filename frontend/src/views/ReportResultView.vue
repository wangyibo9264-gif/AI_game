<template>
  <section class="placeholder-view" aria-label="&#x7ED3;&#x5C40;&#x8BC4;&#x5206;">
    <p class="placeholder-kicker">&#x7ED3;&#x5C40;&#x8BC4;&#x5206;</p>
    <h1 class="placeholder-title">&#x62A5;&#x544A; #{{ reportId }}</h1>
    <p class="placeholder-copy">&#x8BC4;&#x5206;&#x9762;&#x677F;&#x548C;&#x7ED3;&#x5C40;&#x6587;&#x6848;&#x5C06;&#x5728; Task 13 &#x4E2D;&#x63A5;&#x5165;&#x3002;</p>
  </section>
</template>

<script setup lang="ts">
defineProps<{ reportId: string }>();
</script>