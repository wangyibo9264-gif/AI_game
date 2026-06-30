<template>
  <section class="npc-dialogue-panel" aria-label="NPC &#x5BF9;&#x8BDD;">
    <div class="npc-dialogue-panel__head">
      <div>
        <p class="npc-dialogue-panel__eyebrow">&#x8BC1;&#x8A00;&#x8BB0;&#x5F55;</p>
        <h2>NPC &#x5BF9;&#x8BDD;</h2>
      </div>
      <select
        v-if="npcs.length > 0"
        :value="selectedNpc?.id ?? ''"
        @change="$emit('selectNpc', Number(($event.target as HTMLSelectElement).value))"
      >
        <option value="" disabled>&#x9009;&#x62E9; NPC</option>
        <option v-for="npc in npcs" :key="npc.id" :value="npc.id">{{ npc.name }} / {{ npc.roleName }}</option>
      </select>
      <span v-else class="npc-dialogue-panel__empty-select">&#x65E0;&#x4EBA;&#x53EF;&#x95EE;</span>
    </div>

    <div v-if="selectedNpcCharacter" class="witness-strip">
      <CharacterPortrait :character="selectedNpcCharacter" side="left" />
      <div>
        <strong>{{ selectedNpcCharacter.displayName }}</strong>
        <span>{{ selectedNpcCharacter.roleName }} / {{ selectedNpcCharacter.motif }}</span>
      </div>
    </div>

    <div class="dialogue-log">
      <div v-if="messages.length === 0 && selectedNpcCharacter" class="dialogue-empty dialogue-empty--npc">
        <CharacterPortrait :character="selectedNpcCharacter" side="left" />
        <p>&#x5C1A;&#x672A;&#x8BB0;&#x5F55;&#x8BC1;&#x8A00;&#x3002;&#x9009;&#x62E9;&#x4E00;&#x4E2A;&#x95EE;&#x9898;&#x5F00;&#x59CB;&#x8BE2;&#x95EE;&#x3002;</p>
      </div>
      <div v-else-if="messages.length === 0" class="dialogue-empty dialogue-empty--player">
        <CharacterPortrait :character="playerCharacter" side="right" />
        <p>&#x6B64;&#x5904;&#x6682;&#x65E0;&#x53EF;&#x8BE2;&#x95EE;&#x5BF9;&#x8C61;&#xFF0C;&#x5148;&#x67E5;&#x770B;&#x5730;&#x56FE;&#x4E0A;&#x7684;&#x5176;&#x4ED6;&#x5730;&#x70B9;&#x3002;</p>
      </div>

      <article
        v-for="message in messages"
        :key="message.id"
        :class="['dialogue-row', `dialogue-row--${message.sender}`]"
        :data-dialogue-speaker="message.sender"
      >
        <CharacterPortrait
          v-if="message.sender === 'npc' && selectedNpcCharacter"
          :character="selectedNpcCharacter"
          side="left"
        />
        <div class="dialogue-bubble">
          <span>{{ message.sender === 'player' ? '\u6211' : selectedNpcCharacter?.displayName ?? '\u8BC1\u8A00\u4EBA' }}</span>
          <p>{{ message.content }}</p>
        </div>
        <CharacterPortrait v-if="message.sender === 'player'" :character="playerCharacter" side="right" />
      </article>
    </div>

    <QuickQuestionBar v-if="selectedNpc" @ask="$emit('ask', $event)" />
    <p v-else class="quick-question-empty">&#x5F53;&#x524D;&#x5730;&#x70B9;&#x6CA1;&#x6709;&#x53EF;&#x8BE2;&#x95EE;&#x5BF9;&#x8C61;&#x3002;</p>

    <form class="dialogue-input" @submit.prevent="submitQuestion">
      <input
        v-model="draft"
        type="text"
        :placeholder="selectedNpc ? '\u8F93\u5165\u8FFD\u95EE...' : '\u5F53\u524D\u5730\u70B9\u6682\u65E0\u53EF\u8BE2\u95EE\u5BF9\u8C61'"
        :disabled="!selectedNpc || loading"
      />
      <button type="submit" :disabled="!draft.trim() || !canAsk">&#x8BE2;&#x95EE;</button>
    </form>
  </section>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import type { AvailableNpc } from '../api/sessions';
import CharacterPortrait from './CharacterPortrait.vue';
import { getDialogueCharacter, playerCharacter } from './dialogueCharacters';
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

const selectedNpc = computed(() => props.npcs.find((npc) => npc.id === props.selectedNpcId) ?? props.npcs[0] ?? null);
const selectedNpcCharacter = computed(() => (selectedNpc.value ? getDialogueCharacter(selectedNpc.value) : null));
const canAsk = computed(() => Boolean(selectedNpc.value) && !props.loading);

function submitQuestion() {
  const question = draft.value.trim();
  if (!question || !canAsk.value) return;
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

.npc-dialogue-panel__eyebrow {
  margin: 0 0 4px;
  color: var(--color-candle-yellow);
  font-size: 12px;
  font-weight: 800;
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

.npc-dialogue-panel__empty-select,
.quick-question-empty {
  color: var(--color-muted);
  font-size: 13px;
}

.witness-strip {
  display: grid;
  grid-template-columns: 78px 1fr;
  gap: 12px;
  align-items: center;
  min-height: 98px;
  padding: 10px 12px;
  border: 1px solid rgba(216, 201, 163, 0.18);
  border-radius: 8px;
  background: rgba(216, 201, 163, 0.05);
}

.witness-strip strong,
.witness-strip span {
  display: block;
}

.witness-strip strong {
  color: var(--color-old-paper);
  font-size: 17px;
}

.witness-strip span {
  margin-top: 4px;
  color: var(--color-muted);
  font-size: 13px;
}

.dialogue-log {
  display: grid;
  align-content: start;
  gap: 12px;
  min-height: 360px;
  max-height: 52vh;
  overflow: auto;
  padding: 14px;
  border: 1px solid var(--color-line);
  border-radius: 8px;
  background:
    repeating-linear-gradient(0deg, rgba(216, 201, 163, 0.035), rgba(216, 201, 163, 0.035) 1px, transparent 1px, transparent 28px),
    rgba(7, 17, 13, 0.46);
}

.dialogue-empty,
.dialogue-row {
  display: grid;
  grid-template-columns: 78px minmax(0, 1fr) 78px;
  gap: 12px;
  align-items: end;
}

.dialogue-empty {
  align-items: center;
  min-height: 118px;
  color: var(--color-muted);
}

.dialogue-empty--npc p {
  grid-column: 2 / 4;
}

.dialogue-empty--player {
  justify-items: end;
}

.dialogue-empty--player .character-portrait {
  grid-column: 3;
}

.dialogue-empty--player p {
  grid-column: 1 / 3;
  grid-row: 1;
  justify-self: stretch;
  margin: 0;
  padding: 12px 14px;
  border: 1px solid rgba(241, 184, 75, 0.28);
  border-radius: 8px;
  background: rgba(241, 184, 75, 0.08);
  line-height: 1.65;
}

.dialogue-row--npc .dialogue-bubble {
  grid-column: 2 / 4;
  justify-self: start;
}

.dialogue-row--player .dialogue-bubble {
  grid-column: 1 / 3;
  justify-self: end;
}

.dialogue-row--player .character-portrait {
  grid-column: 3;
}

.dialogue-bubble {
  width: fit-content;
  max-width: min(100%, 560px);
  padding: 11px 13px;
  border: 1px solid rgba(216, 201, 163, 0.18);
  border-radius: 8px;
  color: var(--color-ink);
  background: rgba(216, 201, 163, 0.08);
  box-shadow: 0 12px 28px rgba(0, 0, 0, 0.18);
}

.dialogue-row--player .dialogue-bubble {
  border-color: rgba(241, 184, 75, 0.32);
  background: rgba(241, 184, 75, 0.13);
}

.dialogue-bubble span {
  display: block;
  color: var(--color-candle-yellow);
  font-size: 12px;
  font-weight: 800;
}

.dialogue-bubble p {
  margin: 4px 0 0;
  line-height: 1.65;
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

.dialogue-input input:disabled {
  opacity: 0.62;
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

@media (max-width: 620px) {
  .npc-dialogue-panel__head {
    align-items: stretch;
    flex-direction: column;
  }

  .witness-strip {
    grid-template-columns: 58px 1fr;
  }

  .dialogue-empty,
  .dialogue-row {
    grid-template-columns: 58px minmax(0, 1fr) 58px;
    gap: 8px;
  }

  .dialogue-bubble {
    max-width: 100%;
  }
}
</style>