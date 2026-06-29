<template>
  <section class="archive-view" aria-label="&#x6848;&#x4EF6;&#x67DC;">
    <div class="archive-view__heading">
      <p class="placeholder-kicker">&#x602A;&#x8C08;&#x8C03;&#x67E5;&#x5C40; / &#x6848;&#x4EF6;&#x67DC;</p>
      <h1>&#x5C18;&#x9547;&#x6863;&#x6848;</h1>
      <p>&#x9009;&#x62E9;&#x4E00;&#x8D77;&#x56FA;&#x5B9A;&#x6848;&#x4EF6;&#xFF0C;&#x8FDB;&#x5165;&#x8C23;&#x8A00;&#x88AB;&#x626D;&#x66F2;&#x524D;&#x7684;&#x8BC1;&#x8BCD;&#x73B0;&#x573A;&#x3002;</p>
    </div>

    <p v-if="caseStore.loading" class="state-line">&#x6B63;&#x5728;&#x7FFB;&#x627E;&#x6848;&#x5377;...</p>
    <p v-else-if="caseStore.error" class="state-line state-line--danger">{{ caseStore.error }}</p>
    <div v-else class="case-grid">
      <CaseFileCard v-for="caseFile in caseStore.cases" :key="caseFile.id" :case-file="caseFile" />
    </div>
  </section>
</template>

<script setup lang="ts">
import { onMounted } from 'vue';
import CaseFileCard from '../components/CaseFileCard.vue';
import { useCaseStore } from '../stores/caseStore';

const caseStore = useCaseStore();

onMounted(() => {
  if (caseStore.cases.length === 0) {
    void caseStore.loadCases();
  }
});
</script>

<style scoped>
.archive-view {
  display: grid;
  gap: 34px;
}

.archive-view__heading {
  max-width: 760px;
}

.archive-view__heading h1 {
  margin: 8px 0 12px;
  color: var(--color-old-paper);
  font-size: clamp(42px, 8vw, 78px);
  line-height: 1.02;
}

.archive-view__heading p:last-child,
.state-line {
  margin: 0;
  color: var(--color-muted);
  font-size: 17px;
  line-height: 1.8;
}

.state-line--danger {
  color: var(--color-rust-red);
}

.case-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 18px;
}

@media (max-width: 900px) {
  .case-grid {
    grid-template-columns: 1fr;
  }
}
</style>