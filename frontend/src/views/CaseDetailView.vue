<template>
  <section class="case-detail-view" aria-label="&#x6848;&#x4EF6;&#x8BE6;&#x60C5;">
    <p v-if="caseStore.loading" class="state-line">&#x6B63;&#x5728;&#x6821;&#x5BF9;&#x6863;&#x6848;...</p>
    <p v-else-if="caseStore.error" class="state-line state-line--danger">{{ caseStore.error }}</p>

    <template v-else-if="caseStore.currentCase">
      <div class="case-detail-view__hero">
        <p class="placeholder-kicker">{{ caseStore.currentCase.code }} / {{ caseStore.currentCase.difficulty }}</p>
        <h1>{{ caseStore.currentCase.title }}</h1>
        <p>{{ caseStore.currentCase.summary }}</p>

        <button class="start-button" type="button" :disabled="sessionStore.loading" @click="startInvestigation">
          <span v-if="sessionStore.loading">&#x6B63;&#x5728;&#x5F00;&#x542F;...</span>
          <span v-else>&#x5F00;&#x59CB;&#x8C03;&#x67E5;</span>
        </button>
        <p v-if="sessionStore.error" class="state-line state-line--danger">{{ sessionStore.error }}</p>
      </div>

      <div class="case-detail-view__content">
        <section aria-label="&#x6848;&#x4EF6;&#x89C4;&#x5219;">
          <h2>&#x602A;&#x8C08;&#x89C4;&#x5219;</h2>
          <RuleCard v-for="rule in caseStore.currentCase.rules" :key="rule.id" :rule="rule" />
        </section>

        <section aria-label="&#x5DF2;&#x77E5;&#x5730;&#x70B9;">
          <h2>&#x5DF2;&#x77E5;&#x5730;&#x70B9;</h2>
          <ul class="location-list">
            <li v-for="location in caseStore.currentCase.locations" :key="location.id">
              <span>{{ location.name }}</span>
              <small>Stage {{ location.unlockStage }}</small>
              <p>{{ location.description }}</p>
            </li>
          </ul>
        </section>
      </div>
    </template>
  </section>
</template>

<script setup lang="ts">
import { onMounted } from 'vue';
import { useRouter } from 'vue-router';
import RuleCard from '../components/RuleCard.vue';
import { useCaseStore } from '../stores/caseStore';
import { useSessionStore } from '../stores/sessionStore';

const props = defineProps<{ caseId: string }>();
const router = useRouter();
const caseStore = useCaseStore();
const sessionStore = useSessionStore();

onMounted(() => {
  void caseStore.loadCase(props.caseId);
});

async function startInvestigation() {
  const session = await sessionStore.startSession(Number(props.caseId));
  await router.push(`/sessions/${session.id}`);
}
</script>

<style scoped>
.case-detail-view {
  display: grid;
  gap: 36px;
}

.case-detail-view__hero {
  max-width: 820px;
}

.case-detail-view__hero h1 {
  margin: 8px 0 14px;
  color: var(--color-old-paper);
  font-size: clamp(38px, 7vw, 72px);
  line-height: 1.04;
}

.case-detail-view__hero p {
  color: var(--color-muted);
  line-height: 1.8;
}

.start-button {
  min-height: 44px;
  margin-top: 18px;
  padding: 0 18px;
  border: 1px solid rgba(241, 184, 75, 0.42);
  border-radius: 6px;
  color: var(--color-black-green);
  background: var(--color-candle-yellow);
  font-weight: 800;
  cursor: pointer;
}

.start-button:disabled {
  cursor: wait;
  opacity: 0.62;
}

.case-detail-view__content {
  display: grid;
  grid-template-columns: minmax(0, 1fr) minmax(280px, 0.72fr);
  gap: 42px;
}

.case-detail-view__content h2 {
  margin: 0 0 10px;
  color: var(--color-old-paper);
  font-size: 21px;
}

.location-list {
  display: grid;
  gap: 14px;
  padding: 0;
  margin: 0;
  list-style: none;
}

.location-list li {
  padding: 15px 0;
  border-top: 1px solid var(--color-line);
}

.location-list span {
  color: var(--color-old-paper);
  font-weight: 700;
}

.location-list small {
  float: right;
  color: var(--color-candle-yellow);
}

.location-list p,
.state-line {
  margin: 8px 0 0;
  color: var(--color-muted);
  line-height: 1.7;
}

.state-line--danger {
  color: var(--color-rust-red);
}

@media (max-width: 860px) {
  .case-detail-view__content {
    grid-template-columns: 1fr;
  }
}
</style>
