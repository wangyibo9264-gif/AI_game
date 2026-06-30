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
  it('renders a case-specific map and emits visits only for unlocked locations', async () => {
    const wrapper = mount(CaseMapPanel, {
      props: {
        caseId: 1,
        locations,
        availableLocationIds: [1, 2],
        activeLocationId: 1
      }
    });

    expect(wrapper.text()).toContain('旧镇中心图');
    expect(wrapper.find('[data-location-code="CLOCK_TOWER"]').classes()).toContain('map-node--active');
    expect(wrapper.find('[data-location-code="MINE_GATE"]').attributes('disabled')).toBeDefined();

    await wrapper.find('[data-location-code="TOWN_HALL"]').trigger('click');
    await wrapper.find('[data-location-code="MINE_GATE"]').trigger('click');

    expect(wrapper.emitted('visit')).toEqual([[2]]);
  });
});