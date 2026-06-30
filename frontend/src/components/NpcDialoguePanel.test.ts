import { mount } from '@vue/test-utils';
import { describe, expect, it } from 'vitest';
import type { AvailableNpc } from '../api/sessions';
import NpcDialoguePanel, { type DialogueLine } from './NpcDialoguePanel.vue';

const npcs: AvailableNpc[] = [
  { id: 1, code: 'CLOCK_KEEPER', name: '\u6797\u5b88\u591c', roleName: '\u503c\u591c\u4eba', locationId: 1, unlockStage: 1 }
];

const messages: DialogueLine[] = [
  { id: 'm1', sender: 'npc', content: '\u949f\u58f0\u54cd\u8fc7\u4e09\u6b21\u540e\uff0c\u96fe\u4f1a\u5012\u6d41\u3002' },
  { id: 'm2', sender: 'player', content: '\u4f60\u5f53\u665a\u5728\u54ea\u91cc\uff1f' }
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
    expect(wrapper.text()).toContain('\u6797\u5b88\u591c');
    expect(wrapper.text()).toContain('\u6211');
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
    expect(wrapper.text()).toContain('\u6b64\u5904\u6682\u65e0\u53ef\u8be2\u95ee\u5bf9\u8c61');
    expect(wrapper.find('button[type="submit"]').attributes('disabled')).toBeDefined();

    await wrapper.find('form').trigger('submit.prevent');

    expect(wrapper.emitted('ask')).toBeUndefined();
  });
});
