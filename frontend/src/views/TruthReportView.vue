<template>
  <section class="truth-report-view" aria-label="&#x771F;&#x76F8;&#x62A5;&#x544A;">
    <div class="truth-report-view__head">
      <p class="placeholder-kicker">&#x771F;&#x76F8;&#x62A5;&#x544A; / Session {{ sessionId }}</p>
      <h1>&#x628A;&#x8C23;&#x96FE;&#x540E;&#x7684;&#x4EBA;&#x5199;&#x4E0B;&#x6765;&#x3002;</h1>
    </div>

    <div class="truth-report-layout">
      <form class="report-form" @submit.prevent="submit">
        <label v-for="field in fields" :key="field.key">
          <span>{{ field.label }}</span>
          <textarea v-model="form[field.key]" :placeholder="field.placeholder" required />
        </label>
        <p v-if="submitDisabled" class="state-line state-line--danger">&#x9700;&#x8981;&#x81F3;&#x5C11;&#x6536;&#x96C6;&#x4E00;&#x6761;&#x7EBF;&#x7D22;&#x624D;&#x80FD;&#x63D0;&#x4EA4;&#x62A5;&#x544A;&#x3002;</p>
        <p v-if="reportStore.error" class="state-line state-line--danger">{{ reportStore.error }}</p>
        <button type="submit" :disabled="submitDisabled || reportStore.loading">
          <span v-if="reportStore.loading">&#x63D0;&#x4EA4;&#x4E2D;...</span>
          <span v-else>&#x63D0;&#x4EA4;&#x771F;&#x76F8;</span>
        </button>
      </form>

      <aside class="selected-clues" aria-label="&#x5DF2;&#x6536;&#x96C6;&#x7EBF;&#x7D22;">
        <h2>&#x5DF2;&#x6536;&#x96C6;&#x7EBF;&#x7D22;</h2>
        <p v-if="clueStore.loading" class="state-line">&#x6B63;&#x5728;&#x8BFB;&#x53D6;&#x7EBF;&#x7D22;...</p>
        <p v-else-if="collectedClues.length === 0" class="state-line">&#x7EBF;&#x7D22;&#x6863;&#x6848;&#x8FD8;&#x662F;&#x7A7A;&#x7684;&#x3002;</p>
        <ul v-else>
          <li v-for="clue in collectedClues" :key="clue.clueId">
            <strong>{{ clue.title }}</strong>
            <span>{{ clue.category }} / {{ clue.importance }}</span>
          </li>
        </ul>
      </aside>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive } from 'vue';
import { useRouter } from 'vue-router';
import type { SubmitTruthReportRequest } from '../api/reports';
import { useClueStore } from '../stores/clueStore';
import { useReportStore } from '../stores/reportStore';

const props = defineProps<{ sessionId: string }>();
const router = useRouter();
const clueStore = useClueStore();
const reportStore = useReportStore();

const form = reactive<SubmitTruthReportRequest>({
  eventSummary: '',
  responsiblePerson: '',
  keyEvidence: '',
  ruleExplanation: '',
  npcLies: '',
  conclusion: ''
});

const fields: Array<{ key: keyof SubmitTruthReportRequest; label: string; placeholder: string }> = [
  { key: 'eventSummary', label: '\u4E8B\u4EF6\u7ECF\u8FC7', placeholder: '\u8C23\u8A00\u662F\u600E\u4E48\u88AB\u5236\u9020\u6216\u6269\u6563\u7684\uFF1F' },
  { key: 'responsiblePerson', label: '\u8D23\u4EFB\u4EBA', placeholder: '\u8C01\u662F\u5173\u952E\u63A8\u624B\uFF1F' },
  { key: 'keyEvidence', label: '\u5173\u952E\u8BC1\u636E', placeholder: '\u54EA\u4E9B\u7EBF\u7D22\u652F\u6491\u4F60\u7684\u5224\u65AD\uFF1F' },
  { key: 'ruleExplanation', label: '\u89C4\u5219\u89E3\u8BFB', placeholder: '\u602A\u8C08\u89C4\u5219\u771F\u6B63\u6307\u4EE3\u4EC0\u4E48\uFF1F' },
  { key: 'npcLies', label: 'NPC \u8C0E\u8A00', placeholder: '\u8C01\u6492\u4E86\u8C0E\uFF0C\u4E3A\u4EC0\u4E48\uFF1F' },
  { key: 'conclusion', label: '\u7ED3\u8BBA', placeholder: '\u5199\u4E0B\u4F60\u7684\u6700\u7EC8\u5224\u65AD\u3002' }
];

const collectedClues = computed(() => clueStore.groups.flatMap((group) => group.clues));
const submitDisabled = computed(() => collectedClues.value.length === 0);

onMounted(() => {
  void clueStore.loadClues(props.sessionId);
});

async function submit() {
  if (submitDisabled.value) return;
  const result = await reportStore.submit(props.sessionId, { ...form });
  await router.push(`/reports/${result.reportId}`);
}
</script>

<style scoped>
.truth-report-view {
  display: grid;
  gap: 28px;
}

.truth-report-view__head h1 {
  max-width: 820px;
  margin: 8px 0 0;
  color: var(--color-old-paper);
  font-size: clamp(36px, 7vw, 72px);
  line-height: 1.04;
}

.truth-report-layout {
  display: grid;
  grid-template-columns: minmax(0, 1.2fr) minmax(260px, 0.7fr);
  gap: 28px;
  align-items: start;
}

.report-form {
  display: grid;
  gap: 16px;
}

.report-form label {
  display: grid;
  gap: 8px;
}

.report-form span,
.selected-clues h2 {
  color: var(--color-old-paper);
  font-weight: 800;
}

.report-form textarea {
  min-height: 98px;
  resize: vertical;
  padding: 12px;
  border: 1px solid var(--color-line);
  border-radius: 8px;
  color: var(--color-ink);
  background: rgba(7, 17, 13, 0.74);
  line-height: 1.7;
}

.report-form button {
  min-height: 44px;
  border: 0;
  border-radius: 6px;
  color: var(--color-black-green);
  background: var(--color-candle-yellow);
  font-weight: 900;
  cursor: pointer;
}

.report-form button:disabled {
  cursor: not-allowed;
  opacity: 0.56;
}

.selected-clues {
  display: grid;
  gap: 12px;
  padding-left: 22px;
  border-left: 1px solid var(--color-line);
}

.selected-clues h2 {
  margin: 0;
  font-size: 18px;
}

.selected-clues ul {
  display: grid;
  gap: 10px;
  padding: 0;
  margin: 0;
  list-style: none;
}

.selected-clues li {
  display: grid;
  gap: 4px;
  padding: 12px 0;
  border-top: 1px solid var(--color-line);
}

.selected-clues strong {
  color: var(--color-old-paper);
}

.selected-clues span,
.state-line {
  color: var(--color-muted);
}

.state-line--danger {
  color: var(--color-rust-red);
}

@media (max-width: 860px) {
  .truth-report-layout {
    grid-template-columns: 1fr;
  }

  .selected-clues {
    padding-left: 0;
    border-left: 0;
  }
}
</style>