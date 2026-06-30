# Dialogue Character Interface Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Upgrade the investigation dialogue panel into a dossier-style chat interface with NPC/player bubbles and distinct CSS-drawn character portraits.

**Architecture:** Add a small character presentation layer under `frontend/src/components/`: a pure `dialogueCharacters.ts` mapping module, a `CharacterPortrait.vue` renderer, and focused tests. Then refactor `NpcDialoguePanel.vue` to use those portraits while keeping its existing props and events so `InvestigationView.vue` does not need business-logic changes.

**Tech Stack:** Vue 3, TypeScript, Vue Test Utils, Vitest, scoped CSS.

---

### Task 1: Character Mapping

**Files:**
- Create: `frontend/src/components/dialogueCharacters.ts`
- Test: `frontend/src/components/dialogueCharacters.test.ts`

- [ ] **Step 1: Write the failing test**

Create `frontend/src/components/dialogueCharacters.test.ts`:

```ts
import { describe, expect, it } from 'vitest';
import { getDialogueCharacter, playerCharacter } from './dialogueCharacters';

describe('dialogueCharacters', () => {
  it('returns a fixed player investigator portrait', () => {
    expect(playerCharacter.id).toBe('player');
    expect(playerCharacter.displayName).toBe('我');
    expect(playerCharacter.badge).toBe('查');
  });

  it('returns distinct configured portraits for known NPC codes', () => {
    const clockNpc = getDialogueCharacter({ code: 'CLOCK_KEEPER', name: '林守夜', roleName: '值夜人' });
    const hotelNpc = getDialogueCharacter({ code: 'HOTEL_OWNER', name: '孟老板', roleName: '旅馆老板' });
    const harborNpc = getDialogueCharacter({ code: 'FERRYMAN', name: '陈摆渡', roleName: '摆渡人' });

    expect(clockNpc.displayName).toBe('林守夜');
    expect(clockNpc.badge).toBe('钟');
    expect(hotelNpc.badge).toBe('匙');
    expect(harborNpc.badge).toBe('票');
    expect(new Set([clockNpc.palette.primary, hotelNpc.palette.primary, harborNpc.palette.primary]).size).toBe(3);
  });

  it('falls back to a witness portrait for unknown NPC codes', () => {
    const character = getDialogueCharacter({ code: 'UNKNOWN_NPC', name: '陌生人', roleName: '镇民' });

    expect(character.id).toBe('npc-unknown');
    expect(character.displayName).toBe('陌生人');
    expect(character.roleName).toBe('镇民');
    expect(character.badge).toBe('证');
  });
});
```

- [ ] **Step 2: Run test to verify it fails**

Run:

```bash
npm run test -- dialogueCharacters.test.ts
```

Expected: FAIL because `dialogueCharacters.ts` does not exist.

- [ ] **Step 3: Write minimal implementation**

Create `frontend/src/components/dialogueCharacters.ts`:

```ts
export interface DialogueCharacterInput {
  code: string;
  name: string;
  roleName: string;
}

export interface DialogueCharacter {
  id: string;
  displayName: string;
  roleName: string;
  badge: string;
  motif: string;
  palette: {
    primary: string;
    secondary: string;
    accent: string;
  };
}

const npcPortraits: Record<string, Omit<DialogueCharacter, 'displayName' | 'roleName'>> = {
  CLOCK_KEEPER: {
    id: 'npc-clock-keeper',
    badge: '钟',
    motif: '钟楼值夜',
    palette: { primary: '#6f7f69', secondary: '#24332d', accent: '#d8c9a3' }
  },
  MINER_WIDOW: {
    id: 'npc-miner-widow',
    badge: '矿',
    motif: '红手套',
    palette: { primary: '#8f3d36', secondary: '#2b1816', accent: '#d8c9a3' }
  },
  ARCHIVIST: {
    id: 'npc-archivist',
    badge: '档',
    motif: '旧档案',
    palette: { primary: '#8b7a54', secondary: '#2f281b', accent: '#f1b84b' }
  },
  HOTEL_OWNER: {
    id: 'npc-hotel-owner',
    badge: '匙',
    motif: '第八把钥匙',
    palette: { primary: '#7a5f88', secondary: '#221927', accent: '#d8c9a3' }
  },
  MAID: {
    id: 'npc-maid',
    badge: '铃',
    motif: '前台铃',
    palette: { primary: '#a2794a', secondary: '#2b2118', accent: '#f1b84b' }
  },
  MIRROR_GUEST: {
    id: 'npc-mirror-guest',
    badge: '镜',
    motif: '镜中房号',
    palette: { primary: '#738993', secondary: '#18262b', accent: '#d8c9a3' }
  },
  FERRYMAN: {
    id: 'npc-ferryman',
    badge: '票',
    motif: '末班船票',
    palette: { primary: '#4d7d85', secondary: '#12282d', accent: '#f1b84b' }
  },
  DOCK_WORKER: {
    id: 'npc-dock-worker',
    badge: '灯',
    motif: '雾港灯',
    palette: { primary: '#5d6f8f', secondary: '#141d2c', accent: '#d8c9a3' }
  },
  TICKET_CLERK: {
    id: 'npc-ticket-clerk',
    badge: '账',
    motif: '船票名册',
    palette: { primary: '#867148', secondary: '#2d2718', accent: '#f1b84b' }
  }
};

export const playerCharacter: DialogueCharacter = {
  id: 'player',
  displayName: '我',
  roleName: '调查员',
  badge: '查',
  motif: '尘镇档案',
  palette: { primary: '#d8c9a3', secondary: '#5c4b2f', accent: '#f1b84b' }
};

export function getDialogueCharacter(npc: DialogueCharacterInput): DialogueCharacter {
  const portrait = npcPortraits[npc.code] ?? {
    id: 'npc-unknown',
    badge: '证',
    motif: '镇民证言',
    palette: { primary: '#7c887b', secondary: '#202a24', accent: '#d8c9a3' }
  };

  return {
    ...portrait,
    displayName: npc.name,
    roleName: npc.roleName
  };
}
```

- [ ] **Step 4: Run test to verify it passes**

Run:

```bash
npm run test -- dialogueCharacters.test.ts
```

Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add frontend/src/components/dialogueCharacters.ts frontend/src/components/dialogueCharacters.test.ts
git commit -m "feat: add dialogue character profiles"
```

### Task 2: Character Portrait Component

**Files:**
- Create: `frontend/src/components/CharacterPortrait.vue`
- Test: `frontend/src/components/CharacterPortrait.test.ts`

- [ ] **Step 1: Write the failing test**

Create `frontend/src/components/CharacterPortrait.test.ts`:

```ts
import { mount } from '@vue/test-utils';
import { describe, expect, it } from 'vitest';
import CharacterPortrait from './CharacterPortrait.vue';
import { playerCharacter } from './dialogueCharacters';

describe('CharacterPortrait', () => {
  it('renders the character badge, name, role, and motif', () => {
    const wrapper = mount(CharacterPortrait, {
      props: {
        character: playerCharacter,
        side: 'right'
      }
    });

    expect(wrapper.find('[data-character-id="player"]').exists()).toBe(true);
    expect(wrapper.text()).toContain('我');
    expect(wrapper.text()).toContain('调查员');
    expect(wrapper.text()).toContain('尘镇档案');
    expect(wrapper.find('.character-portrait--right').exists()).toBe(true);
  });
});
```

- [ ] **Step 2: Run test to verify it fails**

Run:

```bash
npm run test -- CharacterPortrait.test.ts
```

Expected: FAIL because `CharacterPortrait.vue` does not exist.

- [ ] **Step 3: Write minimal implementation**

Create `frontend/src/components/CharacterPortrait.vue`:

```vue
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
```

- [ ] **Step 4: Run test to verify it passes**

Run:

```bash
npm run test -- CharacterPortrait.test.ts
```

Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add frontend/src/components/CharacterPortrait.vue frontend/src/components/CharacterPortrait.test.ts
git commit -m "feat: render dialogue character portraits"
```

### Task 3: Dossier Bubble Dialogue Panel

**Files:**
- Modify: `frontend/src/components/NpcDialoguePanel.vue`
- Test: `frontend/src/components/NpcDialoguePanel.test.ts`

- [ ] **Step 1: Write the failing tests**

Create `frontend/src/components/NpcDialoguePanel.test.ts`:

```ts
import { mount } from '@vue/test-utils';
import { describe, expect, it } from 'vitest';
import NpcDialoguePanel, { type DialogueLine } from './NpcDialoguePanel.vue';
import type { AvailableNpc } from '../api/sessions';

const npcs: AvailableNpc[] = [
  { id: 1, code: 'CLOCK_KEEPER', name: '林守夜', roleName: '值夜人', locationId: 1, unlockStage: 1 }
];

const messages: DialogueLine[] = [
  { id: 'm1', sender: 'npc', content: '钟声响过三次后，雾会倒流。' },
  { id: 'm2', sender: 'player', content: '你当晚在哪里？' }
];

describe('NpcDialoguePanel', () => {
  it('renders NPC and player messages as sided dossier bubbles with portraits', () => {
    const wrapper = mount(NpcDialoguePanel, {
      props: {
        npcs,
        selectedNpcId: 1,
        messages,
        loading: false
      }
    });

    expect(wrapper.find('[data-dialogue-speaker="npc"]').classes()).toContain('dialogue-row--npc');
    expect(wrapper.find('[data-dialogue-speaker="player"]').classes()).toContain('dialogue-row--player');
    expect(wrapper.find('[data-character-id="npc-clock-keeper"]').exists()).toBe(true);
    expect(wrapper.find('[data-character-id="player"]').exists()).toBe(true);
    expect(wrapper.text()).toContain('林守夜');
    expect(wrapper.text()).toContain('我');
  });

  it('shows only the player empty state and disables asking when no NPC is available', async () => {
    const wrapper = mount(NpcDialoguePanel, {
      props: {
        npcs: [],
        selectedNpcId: null,
        messages: [],
        loading: false
      }
    });

    expect(wrapper.find('[data-character-id="player"]').exists()).toBe(true);
    expect(wrapper.find('[data-character-id^="npc-"]').exists()).toBe(false);
    expect(wrapper.text()).toContain('此处暂无可询问对象');
    expect(wrapper.find('button[type="submit"]').attributes('disabled')).toBeDefined();

    await wrapper.find('form').trigger('submit.prevent');

    expect(wrapper.emitted('ask')).toBeUndefined();
  });
});
```

- [ ] **Step 2: Run test to verify it fails**

Run:

```bash
npm run test -- NpcDialoguePanel.test.ts
```

Expected: FAIL because the existing component still renders plain message rows and no portraits.

- [ ] **Step 3: Implement the dossier bubble panel**

Replace `frontend/src/components/NpcDialoguePanel.vue` with a version that:

```vue
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
```

The template must:

```vue
<template>
  <section class="npc-dialogue-panel" aria-label="NPC 对话">
    <div class="npc-dialogue-panel__head">
      <div>
        <p class="npc-dialogue-panel__eyebrow">证言记录</p>
        <h2>NPC 对话</h2>
      </div>
      <select
        v-if="npcs.length > 0"
        :value="selectedNpc?.id ?? ''"
        @change="$emit('selectNpc', Number(($event.target as HTMLSelectElement).value))"
      >
        <option value="" disabled>选择 NPC</option>
        <option v-for="npc in npcs" :key="npc.id" :value="npc.id">{{ npc.name }} / {{ npc.roleName }}</option>
      </select>
      <span v-else class="npc-dialogue-panel__empty-select">无人可问</span>
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
        <p>尚未记录证言。选择一个问题开始询问。</p>
      </div>
      <div v-else-if="messages.length === 0" class="dialogue-empty dialogue-empty--player">
        <CharacterPortrait :character="playerCharacter" side="right" />
        <p>此处暂无可询问对象，先查看地图上的其他地点。</p>
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
          <span>{{ message.sender === 'player' ? '我' : selectedNpcCharacter?.displayName ?? '证言人' }}</span>
          <p>{{ message.content }}</p>
        </div>
        <CharacterPortrait v-if="message.sender === 'player'" :character="playerCharacter" side="right" />
      </article>
    </div>

    <QuickQuestionBar @ask="$emit('ask', $event)" />

    <form class="dialogue-input" @submit.prevent="submitQuestion">
      <input
        v-model="draft"
        type="text"
        :placeholder="selectedNpc ? '输入追问...' : '当前地点暂无可询问对象'"
        :disabled="!selectedNpc || loading"
      />
      <button type="submit" :disabled="!draft.trim() || !canAsk">询问</button>
    </form>
  </section>
</template>
```

The scoped CSS must keep the component responsive and use stable dimensions for portrait rows, bubbles, and controls.

- [ ] **Step 4: Run test to verify it passes**

Run:

```bash
npm run test -- NpcDialoguePanel.test.ts
```

Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add frontend/src/components/NpcDialoguePanel.vue frontend/src/components/NpcDialoguePanel.test.ts
git commit -m "feat: add dossier dialogue bubbles"
```

### Task 4: Final Verification

**Files:**
- Verify: `frontend/src/components/*`

- [ ] **Step 1: Run the full frontend test suite**

Run:

```bash
npm run test
```

Expected: all frontend tests pass.

- [ ] **Step 2: Run production build**

Run:

```bash
npm run build
```

Expected: TypeScript check and Vite build finish with exit code 0.

- [ ] **Step 3: Review git status**

Run:

```bash
git status --short
```

Expected: only unrelated pre-existing files remain modified, if any.

- [ ] **Step 4: Commit any final polish if needed**

If Task 4 required CSS or test polish, commit only files related to this dialogue feature:

```bash
git add frontend/src/components/CharacterPortrait.vue frontend/src/components/NpcDialoguePanel.vue frontend/src/components/*.test.ts frontend/src/components/dialogueCharacters.ts
git commit -m "style: polish dialogue character interface"
```
