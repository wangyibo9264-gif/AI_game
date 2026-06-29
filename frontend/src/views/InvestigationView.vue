<template>
  <section class="placeholder-view" aria-label="&#x8C03;&#x67E5;&#x754C;&#x9762;">
    <p class="placeholder-kicker">&#x8C03;&#x67E5;&#x4F1A;&#x8BDD;</p>
    <h1 class="placeholder-title">&#x4F1A;&#x8BDD; #{{ sessionId }}</h1>
    <p class="placeholder-copy">&#x5730;&#x70B9;&#x3001;NPC &#x5BF9;&#x8BDD;&#x548C;&#x7EBF;&#x7D22;&#x6863;&#x6848;&#x5C06;&#x5728; Task 12 &#x4E2D;&#x7EC4;&#x6210;&#x4E09;&#x680F;&#x8C03;&#x67E5;&#x754C;&#x9762;&#x3002;</p>
  </section>
</template>

<script setup lang="ts">
defineProps<{ sessionId: string }>();
</script>