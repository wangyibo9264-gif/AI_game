<template>
  <aside class="location-panel" aria-label="&#x5730;&#x70B9;">
    <h2>&#x5730;&#x70B9;</h2>
    <button
      v-for="location in locations"
      :key="location.id"
      type="button"
      :class="['location-item', { 'location-item--active': location.id === activeLocationId }]"
      @click="$emit('visit', location.id)"
    >
      <span>{{ location.name }}</span>
      <small>Stage {{ location.unlockStage }}</small>
      <p>{{ location.description }}</p>
    </button>
  </aside>
</template>

<script setup lang="ts">
import type { AvailableLocation } from '../api/sessions';

defineProps<{ locations: AvailableLocation[]; activeLocationId: number | null }>();
defineEmits<{ visit: [locationId: number] }>();
</script>

<style scoped>
.location-panel {
  display: grid;
  align-content: start;
  gap: 10px;
}

.location-panel h2 {
  margin: 0 0 6px;
  color: var(--color-old-paper);
  font-size: 18px;
}

.location-item {
  display: grid;
  gap: 6px;
  width: 100%;
  padding: 14px;
  border: 1px solid var(--color-line);
  border-radius: 8px;
  color: inherit;
  text-align: left;
  background: rgba(13, 24, 20, 0.62);
  cursor: pointer;
}

.location-item--active,
.location-item:hover {
  border-color: rgba(241, 184, 75, 0.48);
}

.location-item span {
  color: var(--color-old-paper);
  font-weight: 800;
}

.location-item small {
  color: var(--color-candle-yellow);
}

.location-item p {
  margin: 0;
  color: var(--color-muted);
  line-height: 1.55;
}
</style>