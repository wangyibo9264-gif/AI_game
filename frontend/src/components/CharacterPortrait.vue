<template>
  <figure
    class="character-portrait"
    :class="`character-portrait--${side}`"
    :data-character-id="character.id"
    :style="portraitStyle"
  >
    <div class="character-portrait__bust" aria-hidden="true">
      <span class="character-portrait__badge">{{ character.badge }}</span>
    </div>
    <figcaption>
      <strong>{{ character.displayName }}</strong>
      <span>{{ character.roleName }}</span>
      <small>{{ character.motif }}</small>
    </figcaption>
  </figure>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import type { DialogueCharacter } from './dialogueCharacters';

const props = defineProps<{
  character: DialogueCharacter;
  side: 'left' | 'right';
}>();

const portraitStyle = computed(() => ({
  '--portrait-primary': props.character.palette.primary,
  '--portrait-secondary': props.character.palette.secondary,
  '--portrait-accent': props.character.palette.accent
}));
</script>

<style scoped>
.character-portrait {
  display: grid;
  justify-items: center;
  gap: 6px;
  width: 78px;
  margin: 0;
  color: var(--color-ink);
  text-align: center;
}

.character-portrait__bust {
  position: relative;
  display: grid;
  place-items: end center;
  width: 62px;
  height: 82px;
  padding-bottom: 10px;
  border: 1px solid rgba(216, 201, 163, 0.28);
  border-radius: 30px 30px 14px 14px;
  background:
    radial-gradient(circle at 50% 24%, var(--portrait-accent) 0 17px, transparent 18px),
    linear-gradient(160deg, var(--portrait-primary), var(--portrait-secondary));
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.36);
}

.character-portrait__bust::before,
.character-portrait__bust::after {
  position: absolute;
  bottom: 18px;
  width: 12px;
  height: 28px;
  border-radius: 999px;
  background: rgba(7, 17, 13, 0.28);
  content: '';
}

.character-portrait__bust::before {
  left: 10px;
  transform: rotate(13deg);
}

.character-portrait__bust::after {
  right: 10px;
  transform: rotate(-13deg);
}

.character-portrait__badge {
  position: relative;
  z-index: 1;
  display: grid;
  place-items: center;
  width: 28px;
  height: 28px;
  border: 1px solid rgba(7, 17, 13, 0.32);
  border-radius: 50%;
  color: var(--color-black-green);
  background: var(--portrait-accent);
  font-size: 14px;
  font-weight: 900;
}

.character-portrait figcaption {
  display: grid;
  gap: 2px;
}

.character-portrait strong {
  color: var(--color-old-paper);
  font-size: 12px;
}

.character-portrait span,
.character-portrait small {
  color: var(--color-muted);
  font-size: 11px;
  line-height: 1.25;
}

@media (max-width: 620px) {
  .character-portrait {
    width: 58px;
  }

  .character-portrait__bust {
    width: 48px;
    height: 62px;
  }

  .character-portrait small {
    display: none;
  }
}
</style>
