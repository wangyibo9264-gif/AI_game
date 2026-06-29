<template>
  <article class="rule-card">
    <span class="rule-card__index">{{ rule.displayOrder.toString().padStart(2, '0') }}</span>
    <div>
      <h3>{{ rule.ruleCode }}</h3>
      <p>{{ rule.ruleText }}</p>
    </div>
  </article>
</template>

<script setup lang="ts">
import type { CaseRule } from '../api/cases';

defineProps<{ rule: CaseRule }>();
</script>

<style scoped>
.rule-card {
  display: grid;
  grid-template-columns: 42px 1fr;
  gap: 14px;
  padding: 16px 0;
  border-top: 1px solid var(--color-line);
}

.rule-card__index {
  color: var(--color-candle-yellow);
  font-family: "Courier New", monospace;
  font-size: 14px;
}

.rule-card h3 {
  margin: 0 0 6px;
  color: var(--color-old-paper);
  font-size: 16px;
}

.rule-card p {
  margin: 0;
  color: var(--color-muted);
  line-height: 1.7;
}
</style>