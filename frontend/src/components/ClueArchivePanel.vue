<template>
  <aside class="clue-archive-panel" aria-label="&#x7EBF;&#x7D22;&#x6863;&#x6848;">
    <h2>&#x7EBF;&#x7D22;&#x6863;&#x6848;</h2>
    <p v-if="groups.length === 0" class="clue-empty">&#x5C1A;&#x672A;&#x6536;&#x96C6;&#x7EBF;&#x7D22;&#x3002;</p>
    <section v-for="group in groups" :key="group.category" class="clue-group">
      <h3>{{ group.category }}</h3>
      <article v-for="clue in group.clues" :key="clue.clueId" class="clue-item">
        <strong>{{ clue.title }}</strong>
        <p>{{ clue.content }}</p>
        <div class="clue-controls">
          <select :value="clue.importance" @change="emitUpdate(clue.clueId, ($event.target as HTMLSelectElement).value, clue.status)">
            <option value="LOW">LOW</option>
            <option value="NORMAL">NORMAL</option>
            <option value="HIGH">HIGH</option>
          </select>
          <select :value="clue.status" @change="emitUpdate(clue.clueId, clue.importance, ($event.target as HTMLSelectElement).value)">
            <option value="UNRESOLVED">UNRESOLVED</option>
            <option value="CONFIRMED">CONFIRMED</option>
            <option value="DISMISSED">DISMISSED</option>
          </select>
        </div>
      </article>
    </section>
  </aside>
</template>

<script setup lang="ts">
import type { ClueArchiveGroup, ClueImportance, CollectedClueStatus } from '../api/clues';

defineProps<{ groups: ClueArchiveGroup[] }>();
const emit = defineEmits<{ updateClue: [clueId: number, importance: ClueImportance, status: CollectedClueStatus] }>();

function emitUpdate(clueId: number, importance: string, status: string) {
  emit('updateClue', clueId, importance as ClueImportance, status as CollectedClueStatus);
}
</script>

<style scoped>
.clue-archive-panel {
  display: grid;
  align-content: start;
  gap: 14px;
}

.clue-archive-panel h2,
.clue-group h3 {
  margin: 0;
  color: var(--color-old-paper);
}

.clue-archive-panel h2 {
  font-size: 18px;
}

.clue-empty {
  color: var(--color-muted);
}

.clue-group {
  display: grid;
  gap: 10px;
}

.clue-group h3 {
  color: var(--color-candle-yellow);
  font-size: 13px;
}

.clue-item {
  display: grid;
  gap: 8px;
  padding: 12px;
  border: 1px solid var(--color-line);
  border-radius: 8px;
  background: rgba(13, 24, 20, 0.62);
}

.clue-item strong {
  color: var(--color-old-paper);
}

.clue-item p {
  margin: 0;
  color: var(--color-muted);
  line-height: 1.55;
}

.clue-controls {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 8px;
}

.clue-controls select {
  min-width: 0;
  min-height: 34px;
  border: 1px solid var(--color-line);
  border-radius: 6px;
  color: var(--color-ink);
  background: rgba(7, 17, 13, 0.84);
}
</style>