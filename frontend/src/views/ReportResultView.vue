<template>
  <section class="report-result-view" aria-label="&#x7ED3;&#x5C40;&#x8BC4;&#x5206;">
    <p class="placeholder-kicker">&#x7ED3;&#x5C40;&#x8BC4;&#x5206; / Report {{ reportId }}</p>
    <ScorePanel v-if="reportStore.result" :result="reportStore.result" />
    <div v-else class="report-result-view__empty">
      <h1>&#x62A5;&#x544A;&#x7ED3;&#x679C;&#x6682;&#x672A;&#x52A0;&#x8F7D;</h1>
      <p>&#x8BF7;&#x4ECE;&#x771F;&#x76F8;&#x62A5;&#x544A;&#x63D0;&#x4EA4;&#x540E;&#x8FDB;&#x5165;&#x6B64;&#x9875;&#x3002;</p>
    </div>
  </section>
</template>

<script setup lang="ts">
import ScorePanel from '../components/ScorePanel.vue';
import { useReportStore } from '../stores/reportStore';

defineProps<{ reportId: string }>();
const reportStore = useReportStore();
</script>

<style scoped>
.report-result-view {
  display: grid;
  gap: 24px;
}

.report-result-view__empty h1 {
  margin: 0 0 12px;
  color: var(--color-old-paper);
  font-size: clamp(36px, 7vw, 72px);
}

.report-result-view__empty p {
  margin: 0;
  color: var(--color-muted);
  line-height: 1.8;
}
</style>