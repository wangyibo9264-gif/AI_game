<template>
  <article class="case-file-card">
    <div class="case-file-card__meta">
      <span>{{ caseFile.code }}</span>
      <span>{{ caseFile.difficulty }}</span>
    </div>
    <h2>{{ caseFile.title }}</h2>
    <p>{{ caseFile.summary }}</p>
    <div class="case-file-card__footer">
      <span>{{ caseFile.estimatedMinutes }} min</span>
      <RouterLink class="case-file-card__link" :to="`/cases/${caseFile.id}`">&#x67E5;&#x770B;&#x6863;&#x6848;</RouterLink>
    </div>
  </article>
</template>

<script setup lang="ts">
import type { CaseSummary } from '../api/cases';

defineProps<{ caseFile: CaseSummary }>();
</script>

<style scoped>
.case-file-card {
  display: grid;
  gap: 14px;
  min-height: 236px;
  padding: 22px;
  border: 1px solid var(--color-line);
  border-radius: 8px;
  background: linear-gradient(180deg, rgba(216, 201, 163, 0.08), rgba(13, 24, 20, 0.72));
  transition: border-color 160ms ease, transform 160ms ease;
}

.case-file-card:hover {
  border-color: rgba(241, 184, 75, 0.45);
  transform: translateY(-2px);
}

.case-file-card__meta,
.case-file-card__footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  color: var(--color-fog-gray);
  font-size: 12px;
  text-transform: uppercase;
}

.case-file-card h2 {
  margin: 0;
  color: var(--color-old-paper);
  font-size: 24px;
  line-height: 1.25;
}

.case-file-card p {
  margin: 0;
  color: var(--color-muted);
  line-height: 1.7;
}

.case-file-card__link {
  color: var(--color-candle-yellow);
  font-weight: 700;
}
</style>