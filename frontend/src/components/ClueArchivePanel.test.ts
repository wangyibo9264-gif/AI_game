import { mount } from '@vue/test-utils';
import { describe, expect, it } from 'vitest';
import ClueArchivePanel from './ClueArchivePanel.vue';

describe('ClueArchivePanel', () => {
  it('emits clue update selections', async () => {
    const wrapper = mount(ClueArchivePanel, {
      props: {
        groups: [
          {
            category: 'EVIDENCE',
            clues: [
              {
                id: 1,
                clueId: 12,
                clueCode: 'C1',
                title: 'Wet receipt',
                content: 'A clue from the alley.',
                category: 'EVIDENCE',
                importance: 'NORMAL',
                status: 'UNRESOLVED',
                collectedAt: '2026-06-29T00:00:00'
              }
            ]
          }
        ]
      }
    });

    const selects = wrapper.findAll('select');
    await selects[0].setValue('HIGH');
    await selects[1].setValue('CONFIRMED');

    expect(wrapper.emitted('updateClue')?.[0]).toEqual([12, 'HIGH', 'UNRESOLVED']);
    expect(wrapper.emitted('updateClue')?.[1]).toEqual([12, 'NORMAL', 'CONFIRMED']);
  });
});