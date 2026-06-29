<template>
  <div class="quick-question-bar" aria-label="&#x5FEB;&#x6377;&#x63D0;&#x95EE;">
    <button v-for="prompt in prompts" :key="prompt.key" type="button" @click="$emit('ask', prompt.question)">
      {{ prompt.label }}
    </button>
  </div>
</template>

<script setup lang="ts">
defineEmits<{ ask: [question: string] }>();

const prompts = [
  { key: 'time', label: '\u65F6\u95F4', question: '\u8BF7\u628A\u4F60\u8BB0\u5F97\u7684\u65F6\u95F4\u987A\u5E8F\u8BF4\u4E00\u904D\u3002' },
  { key: 'people', label: '\u4EBA\u7269', question: '\u5F53\u65F6\u8FD8\u6709\u8C01\u5728\u73B0\u573A\uFF1F' },
  { key: 'evidence', label: '\u8BC1\u7269', question: '\u6709\u6CA1\u6709\u88AB\u5FFD\u7565\u7684\u7269\u4EF6\u6216\u75D5\u8FF9\uFF1F' },
  { key: 'contradiction', label: '\u77DB\u76FE', question: '\u4F60\u7684\u8BF4\u6CD5\u548C\u5176\u4ED6\u4EBA\u54EA\u91CC\u5BF9\u4E0D\u4E0A\uFF1F' },
  { key: 'rule', label: '\u89C4\u5219', question: '\u8FD9\u6761\u602A\u8C08\u89C4\u5219\u771F\u6B63\u6307\u5411\u4EC0\u4E48\uFF1F' }
];
</script>

<style scoped>
.quick-question-bar {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.quick-question-bar button {
  min-height: 34px;
  padding: 0 12px;
  border: 1px solid var(--color-line);
  border-radius: 6px;
  color: var(--color-old-paper);
  background: rgba(216, 201, 163, 0.06);
  cursor: pointer;
}

.quick-question-bar button:hover {
  border-color: rgba(241, 184, 75, 0.5);
  color: var(--color-candle-yellow);
}
</style>