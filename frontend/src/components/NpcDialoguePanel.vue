<template>
  <section class="npc-dialogue-panel" aria-label="NPC &#x5BF9;&#x8BDD;">
    <div class="npc-dialogue-panel__head">
      <h2>NPC &#x5BF9;&#x8BDD;</h2>
      <select :value="selectedNpcId ?? ''" @change="$emit('selectNpc', Number(($event.target as HTMLSelectElement).value))">
        <option value="" disabled>&#x9009;&#x62E9; NPC</option>
        <option v-for="npc in npcs" :key="npc.id" :value="npc.id">{{ npc.name }} / {{ npc.roleName }}</option>
      </select>
    </div>

    <div class="dialogue-log">
      <p v-if="messages.length === 0" class="dialogue-empty">&#x5C1A;&#x672A;&#x8BB0;&#x5F55;&#x8BC1;&#x8A00;&#x3002;</p>
      <article v-for="message in messages" :key="message.id" :class="['dialogue-message', `dialogue-message--${message.sender}`]">
        <span>{{ message.sender }}</span>
        <p>{{ message.content }}</p>
      </article>
    </div>

    <QuickQuestionBar @ask="$emit('ask', $event)" />

    <form class="dialogue-input" @submit.prevent="submitQuestion">
      <input v-model="draft" type="text" placeholder="Ask a question" />
      <button type="submit" :disabled="!draft.trim() || loading">&#x8BE2;&#x95EE;</button>
    </form>
  </section>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import type { AvailableNpc } from '../api/sessions';
import QuickQuestionBar from './QuickQuestionBar.vue';

export interface DialogueLine {
  id: string;
  sender: 'player' | 'npc';
  content: string;
}

const props = defineProps<{
  npcs: AvailableNpc[];
  selectedNpcId: number | null;
  messages: DialogueLine[];
  loading: boolean;
}>();
const emit = defineEmits<{ selectNpc: [npcId: number]; ask: [question: string] }>();
const draft = ref('');

function submitQuestion() {
  const question = draft.value.trim();
  if (!question || props.loading) return;
  emit('ask', question);
  draft.value = '';
}
</script>

<style scoped>
.npc-dialogue-panel {
  display: grid;
  gap: 14px;
  min-width: 0;
}

.npc-dialogue-panel__head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.npc-dialogue-panel h2 {
  margin: 0;
  color: var(--color-old-paper);
  font-size: 18px;
}

.npc-dialogue-panel select,
.dialogue-input input {
  min-height: 38px;
  border: 1px solid var(--color-line);
  border-radius: 6px;
  color: var(--color-ink);
  background: rgba(7, 17, 13, 0.84);
}

.dialogue-log {
  display: grid;
  align-content: start;
  gap: 10px;
  min-height: 320px;
  max-height: 48vh;
  overflow: auto;
  padding: 14px;
  border: 1px solid var(--color-line);
  border-radius: 8px;
  background: rgba(7, 17, 13, 0.46);
}

.dialogue-empty {
  color: var(--color-muted);
}

.dialogue-message {
  max-width: 84%;
}

.dialogue-message span {
  color: var(--color-candle-yellow);
  font-size: 12px;
}

.dialogue-message p {
  margin: 4px 0 0;
  padding: 10px 12px;
  border-radius: 8px;
  color: var(--color-ink);
  background: rgba(216, 201, 163, 0.08);
  line-height: 1.65;
}

.dialogue-message--player {
  justify-self: end;
}

.dialogue-message--player p {
  background: rgba(241, 184, 75, 0.14);
}

.dialogue-input {
  display: grid;
  grid-template-columns: 1fr 86px;
  gap: 10px;
}

.dialogue-input input {
  width: 100%;
  padding: 0 12px;
}

.dialogue-input button {
  border: 0;
  border-radius: 6px;
  color: var(--color-black-green);
  background: var(--color-candle-yellow);
  font-weight: 800;
  cursor: pointer;
}

.dialogue-input button:disabled {
  cursor: not-allowed;
  opacity: 0.58;
}
</style>