<template>
  <section class="score-panel" aria-label="&#x8BC4;&#x5206;&#x9762;&#x677F;">
    <div class="score-panel__total">
      <span>{{ result.totalScore }}</span>
      <small>{{ result.ending }}</small>
    </div>
    <dl class="score-panel__breakdown">
      <div>
        <dt>&#x771F;&#x76F8;</dt>
        <dd>{{ result.truthScore }}</dd>
      </div>
      <div>
        <dt>&#x7EBF;&#x7D22;</dt>
        <dd>{{ result.clueScore }}</dd>
      </div>
      <div>
        <dt>&#x89C4;&#x5219;</dt>
        <dd>{{ result.ruleScore }}</dd>
      </div>
    </dl>
    <article>
      <h2>&#x590D;&#x76D8;</h2>
      <p>{{ result.summary }}</p>
    </article>
    <article>
      <h2>&#x9057;&#x6F0F;&#x70B9;</h2>
      <p>{{ result.missedPoints }}</p>
    </article>
  </section>
</template>

<script setup lang="ts">
import type { TruthReportResult } from '../api/reports';

defineProps<{ result: TruthReportResult }>();
</script>

<style scoped>
.score-panel {
  display: grid;
  gap: 24px;
}

.score-panel__total {
  display: grid;
  gap: 8px;
}

.score-panel__total span {
  color: var(--color-candle-yellow);
  font-family: "Courier New", monospace;
  font-size: clamp(72px, 14vw, 132px);
  line-height: 0.9;
}

.score-panel__total small {
  color: var(--color-old-paper);
  font-size: 24px;
  font-weight: 800;
}

.score-panel__breakdown {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
  margin: 0;
}

.score-panel__breakdown div {
  padding: 14px;
  border-top: 1px solid var(--color-line);
}

.score-panel__breakdown dt {
  color: var(--color-muted);
  font-size: 13px;
}

.score-panel__breakdown dd {
  margin: 6px 0 0;
  color: var(--color-old-paper);
  font-size: 30px;
  font-weight: 800;
}

.score-panel article {
  border-top: 1px solid var(--color-line);
  padding-top: 16px;
}

.score-panel h2 {
  margin: 0 0 8px;
  color: var(--color-old-paper);
  font-size: 18px;
}

.score-panel p {
  margin: 0;
  color: var(--color-muted);
  line-height: 1.8;
}

@media (max-width: 640px) {
  .score-panel__breakdown {
    grid-template-columns: 1fr;
  }
}
</style>