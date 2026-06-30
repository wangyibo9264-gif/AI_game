import { mount } from '@vue/test-utils';
import { describe, expect, it } from 'vitest';
import CaseMapPanel from './CaseMapPanel.vue';

const locations = [
  { id: 1, code: 'CLOCK_TOWER', name: 'Clock Tower', description: 'Old tower.', unlockStage: 1 },
  { id: 2, code: 'TOWN_HALL', name: 'Town Hall', description: 'Records office.', unlockStage: 1 },
  { id: 3, code: 'MINE_GATE', name: 'Mine Gate', description: 'Closed mine.', unlockStage: 2 },
  { id: 4, code: 'BELL_ARCHIVE', name: 'Bell Archive', description: 'Archive room.', unlockStage: 3 }
];

describe('CaseMapPanel', () => {
  it('renders a case-specific map and emits visits or locked clicks', async () => {
    const wrapper = mount(CaseMapPanel, {
      props: {
        caseId: 1,
        locations,
        availableLocationIds: [1, 2],
        activeLocationId: 1
      }
    });

    expect(wrapper.text()).toContain('\u65E7\u9547\u4E2D\u5FC3\u56FE');
    expect(wrapper.find('[data-location-code="CLOCK_TOWER"]').classes()).toContain('map-node--active');
    expect(wrapper.find('[data-location-code="MINE_GATE"]').attributes('aria-disabled')).toBe('true');

    await wrapper.find('[data-location-code="TOWN_HALL"]').trigger('click');
    await wrapper.find('[data-location-code="MINE_GATE"]').trigger('click');

    expect(wrapper.emitted('visit')).toEqual([[2]]);
    expect(wrapper.emitted('locked')?.[0]?.[0]).toMatchObject({ id: 3, unlockStage: 2 });
  });
});