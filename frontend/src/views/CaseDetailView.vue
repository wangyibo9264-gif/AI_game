<template>
  <section class="case-detail-view" aria-label="&#x6848;&#x4EF6;&#x8BE6;&#x60C5;">
    <p v-if="caseStore.loading" class="state-line">&#x6B63;&#x5728;&#x6821;&#x5BF9;&#x6863;&#x6848;...</p>
    <p v-else-if="caseStore.error" class="state-line state-line--danger">{{ caseStore.error }}</p>

    <template v-else-if="caseStore.currentCase">
      <div class="case-detail-view__hero">
        <p class="placeholder-kicker">{{ caseStore.currentCase.code }} / {{ caseStore.currentCase.difficulty }}</p>
        <h1>{{ caseStore.currentCase.title }}</h1>
        <p>{{ caseStore.currentCase.summary }}</p>
        <section class="mission-brief" aria-label="&#x672C;&#x6848;&#x4EFB;&#x52A1;">
          <h2>&#x672C;&#x6848;&#x4EFB;&#x52A1;</h2>
          <ul>
            <li v-for="requirement in taskRequirements" :key="requirement">{{ requirement }}</li>
          </ul>
        </section>
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
import { computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import RuleCard from '../components/RuleCard.vue';
import { useCaseStore } from '../stores/caseStore';
import { useSessionStore } from '../stores/sessionStore';

const props = defineProps<{ caseId: string }>();
const router = useRouter();
const caseStore = useCaseStore();
const sessionStore = useSessionStore();
const taskRequirements = computed(() => buildCaseRequirements(caseStore.currentCase?.code));

onMounted(() => {
  void caseStore.loadCase(props.caseId);
});

function buildCaseRequirements(code?: string) {
  const shared = [
    '\u6838\u5BF9\u6848\u4EF6\u65F6\u95F4\u7EBF\uFF0C\u627E\u51FA\u8C23\u8A00\u4ECE\u54EA\u4E2A\u73AF\u8282\u5F00\u59CB\u5931\u771F\u3002',
    '\u8BE2\u95EE NPC \u5E76\u6536\u96C6\u7EBF\u7D22\uFF0C\u6807\u8BB0\u5173\u952E\u8BC1\u636E\u7684\u91CD\u8981\u6027\u548C\u72B6\u6001\u3002',
    '\u5728\u7B2C 4 \u9636\u6BB5\u6574\u7406\u771F\u76F8\u62A5\u544A\uFF0C\u8BF4\u660E\u8D23\u4EFB\u4EBA\u3001\u8BC1\u636E\u548C\u602A\u8C08\u89C4\u5219\u7684\u771F\u6B63\u542B\u4E49\u3002'
  ];
  const focus: Record<string, string> = {
    CLOCK_317: '\u91CD\u70B9\u8FFD\u67E5\u4E09\u70B9\u5341\u4E03\u5206\u7684\u949F\u58F0\u4E3A\u4EC0\u4E48\u88AB\u53CD\u590D\u63D0\u8D77\u3002',
    ROSE_ROOM_8: '\u91CD\u70B9\u8FA8\u8BA4 8 \u53F7\u623F\u95F4\u91CC\u7684\u73AB\u7470\u3001\u8BBF\u5BA2\u548C\u5931\u8E2A\u8BC1\u8A00\u3002',
    FOG_LAST_FERRY: '\u91CD\u70B9\u8FD8\u539F\u6700\u540E\u4E00\u73ED\u6E21\u8F6E\u524D\u540E\uFF0C\u96FE\u4E2D\u6D88\u5931\u7684\u4EBA\u548C\u8DEF\u7EBF\u3002'
  };
  return [focus[code ?? ''] ?? '\u91CD\u70B9\u5206\u8FA8\u6848\u4EF6\u4E2D\u7684\u602A\u8C08\u8868\u8C61\u548C\u771F\u5B9E\u52A8\u673A\u3002', ...shared];
}

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

.mission-brief {
  display: grid;
  gap: 10px;
  margin-top: 18px;
  padding: 16px 0;
  border-top: 1px solid var(--color-line);
  border-bottom: 1px solid var(--color-line);
}

.mission-brief h2 {
  margin: 0;
  color: var(--color-old-paper);
  font-size: 18px;
}

.mission-brief ul {
  display: grid;
  gap: 8px;
  padding-left: 18px;
  margin: 0;
  color: var(--color-muted);
  line-height: 1.65;
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