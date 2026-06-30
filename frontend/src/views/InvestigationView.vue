<template>
  <section class="investigation-view" aria-label="&#x8C03;&#x67E5;&#x754C;&#x9762;">
    <header class="investigation-view__head">
      <div>
        <p class="placeholder-kicker">&#x8C03;&#x67E5;&#x4F1A;&#x8BDD; / Session {{ sessionId }}</p>
        <h1>&#x73B0;&#x573A;&#x8BB0;&#x5F55;</h1>
      </div>
      <RouterLink class="report-link" :to="`/sessions/${sessionId}/report`">&#x6574;&#x7406;&#x771F;&#x76F8;&#x62A5;&#x544A;</RouterLink>
    </header>

    <p v-if="stageBanner" class="stage-banner">{{ stageBanner }}</p>
    <p v-if="sessionStore.error || clueStore.error || dialogueStore.error" class="state-line state-line--danger">
      {{ sessionStore.error || clueStore.error || dialogueStore.error }}
    </p>

    <div class="investigation-layout" v-if="sessionStore.session">
      <CaseMapPanel
        :case-id="sessionStore.session.caseId"
        :locations="mapLocations"
        :available-location-ids="availableLocationIds"
        :active-location-id="activeLocationId"
        @visit="visitLocation"
      />

      <NpcDialoguePanel
        :npcs="visibleNpcs"
        :selected-npc-id="selectedNpcId"
        :messages="messages"
        :loading="dialogueStore.loading"
        @select-npc="selectedNpcId = $event"
        @ask="askNpc"
      />

      <ClueArchivePanel :groups="clueStore.groups" @update-clue="updateClue" />
    </div>

    <p v-else class="state-line">&#x6B63;&#x5728;&#x8BFB;&#x53D6;&#x8C03;&#x67E5;&#x4F1A;&#x8BDD;...</p>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import CaseMapPanel from '../components/CaseMapPanel.vue';
import ClueArchivePanel from '../components/ClueArchivePanel.vue';
import NpcDialoguePanel, { type DialogueLine } from '../components/NpcDialoguePanel.vue';
import type { ClueImportance, CollectedClueStatus } from '../api/clues';
import { useCaseStore } from '../stores/caseStore';
import { useClueStore } from '../stores/clueStore';
import { useDialogueStore } from '../stores/dialogueStore';
import { useSessionStore } from '../stores/sessionStore';

const props = defineProps<{ sessionId: string }>();
const sessionStore = useSessionStore();
const caseStore = useCaseStore();
const clueStore = useClueStore();
const dialogueStore = useDialogueStore();
const activeLocationId = ref<number | null>(null);
const selectedNpcId = ref<number | null>(null);
const messages = ref<DialogueLine[]>([]);
const stageBanner = ref('');

const currentStage = computed(() => sessionStore.session?.currentStage ?? 0);
const mapLocations = computed(() => caseStore.currentCase?.locations ?? sessionStore.session?.availableLocations ?? []);
const availableLocationIds = computed(() => sessionStore.session?.availableLocations.map((location) => location.id) ?? []);
const visibleNpcs = computed(() => {
  if (!activeLocationId.value) return sessionStore.session?.availableNpcs ?? [];
  return sessionStore.session?.availableNpcs.filter((npc) => npc.locationId === activeLocationId.value) ?? [];
});

onMounted(async () => {
  await sessionStore.loadSession(props.sessionId);
  if (sessionStore.session) {
    await caseStore.loadCase(sessionStore.session.caseId);
  }
  activeLocationId.value = sessionStore.session?.availableLocations[0]?.id ?? null;
  selectedNpcId.value = visibleNpcs.value[0]?.id ?? null;
  await clueStore.loadClues(props.sessionId);
});

async function visitLocation(locationId: number) {
  activeLocationId.value = locationId;
  await sessionStore.visit(props.sessionId, locationId);
  selectedNpcId.value = visibleNpcs.value[0]?.id ?? null;
}

async function askNpc(question: string) {
  if (!selectedNpcId.value) return;
  messages.value.push({ id: `player-${Date.now()}`, sender: 'player', content: question });
  const beforeStage = currentStage.value;
  const response = await dialogueStore.send(props.sessionId, selectedNpcId.value, question);
  messages.value.push({ id: `npc-${Date.now()}`, sender: 'npc', content: response.reply });
  if (response.newlyCollectedClues.length > 0) {
    await clueStore.loadClues(props.sessionId);
  }
  await refreshStage(beforeStage);
}

async function updateClue(clueId: number, importance: ClueImportance, status: CollectedClueStatus) {
  const beforeStage = currentStage.value;
  await clueStore.update(props.sessionId, clueId, importance, status);
  await refreshStage(beforeStage);
}

async function refreshStage(beforeStage: number) {
  const session = await sessionStore.advance(props.sessionId);
  if (session.currentStage > beforeStage) {
    stageBanner.value = `\u8C03\u67E5\u9636\u6BB5\u66F4\u65B0\uFF1AStage ${session.currentStage}`;
  }
}
</script>

<style scoped>
.investigation-view {
  display: grid;
  gap: 18px;
}

.investigation-view__head {
  display: flex;
  align-items: end;
  justify-content: space-between;
  gap: 18px;
}

.investigation-view__head h1 {
  margin: 6px 0 0;
  color: var(--color-old-paper);
  font-size: clamp(34px, 6vw, 58px);
  line-height: 1.05;
}

.report-link {
  min-height: 40px;
  padding: 10px 14px;
  border: 1px solid rgba(241, 184, 75, 0.45);
  border-radius: 6px;
  color: var(--color-candle-yellow);
  font-weight: 800;
}

.stage-banner {
  margin: 0;
  padding: 10px 12px;
  border: 1px solid rgba(241, 184, 75, 0.45);
  border-radius: 8px;
  color: var(--color-candle-yellow);
  background: rgba(241, 184, 75, 0.08);
}

.state-line {
  color: var(--color-muted);
}

.state-line--danger {
  color: var(--color-rust-red);
}

.investigation-layout {
  display: grid;
  grid-template-columns: minmax(180px, 0.72fr) minmax(320px, 1.4fr) minmax(240px, 0.9fr);
  gap: 18px;
  align-items: start;
}

@media (max-width: 980px) {
  .investigation-view__head {
    align-items: start;
    flex-direction: column;
  }

  .investigation-layout {
    grid-template-columns: 1fr;
  }
}
</style>