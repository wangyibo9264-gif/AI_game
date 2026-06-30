import { flushPromises, mount } from '@vue/test-utils';
import { createPinia, setActivePinia } from 'pinia';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import InvestigationView from './InvestigationView.vue';

const apiMocks = vi.hoisted(() => ({
  getCase: vi.fn(),
  listCases: vi.fn(),
  getSession: vi.fn(),
  visitLocation: vi.fn(),
  advanceSession: vi.fn(),
  createGuest: vi.fn(),
  createSession: vi.fn(),
  sendNpcMessage: vi.fn(),
  listClues: vi.fn(),
  updateClue: vi.fn()
}));

vi.mock('../api/cases', () => ({
  getCase: apiMocks.getCase,
  listCases: apiMocks.listCases
}));

vi.mock('../api/sessions', () => ({
  getSession: apiMocks.getSession,
  visitLocation: apiMocks.visitLocation,
  advanceSession: apiMocks.advanceSession,
  createGuest: apiMocks.createGuest,
  createSession: apiMocks.createSession,
  sendNpcMessage: apiMocks.sendNpcMessage
}));

vi.mock('../api/clues', () => ({
  listClues: apiMocks.listClues,
  updateClue: apiMocks.updateClue
}));

describe('InvestigationView', () => {
  beforeEach(() => {
    setActivePinia(createPinia());
    Object.values(apiMocks).forEach((mock) => mock.mockReset());
    apiMocks.getSession.mockResolvedValue({
      id: 5,
      userId: 1,
      caseId: 1,
      currentStage: 1,
      status: 'ACTIVE',
      availableLocations: [{ id: 1, code: 'CLOCK_TOWER', name: 'Clock Tower', description: 'Old tower.', unlockStage: 1 }],
      availableNpcs: []
    });
    apiMocks.getCase.mockResolvedValue({
      id: 1,
      code: 'CLOCK_317',
      title: 'Clock Case',
      summary: 'A rumor around a stopped clock.',
      difficulty: 'NORMAL',
      estimatedMinutes: 45,
      status: 'ACTIVE',
      rules: [],
      locations: [
        { id: 1, code: 'CLOCK_TOWER', name: 'Clock Tower', description: 'Old tower.', unlockStage: 1 },
        { id: 3, code: 'MINE_GATE', name: 'Mine Gate', description: 'Closed mine.', unlockStage: 2 }
      ]
    });
    apiMocks.listClues.mockResolvedValue([]);
  });

  it('shows mission requirements from a button and explains locked locations', async () => {
    const wrapper = mount(InvestigationView, {
      props: { sessionId: '5' },
      global: {
        plugins: [createPinia()],
        stubs: {
          RouterLink: { props: ['to'], template: '<a :href="to"><slot /></a>' },
          CaseMapPanel: {
            props: ['caseId', 'locations', 'availableLocationIds', 'activeLocationId'],
            emits: ['locked'],
            template: '<button class="locked-location" @click="$emit(\'locked\', locations[1])">locked</button>'
          },
          NpcDialoguePanel: true,
          ClueArchivePanel: true
        }
      }
    });

    await flushPromises();
    expect(wrapper.find('.mission-drawer').exists()).toBe(false);

    await wrapper.get('.mission-toggle').trigger('click');
    expect(wrapper.get('.mission-drawer').text()).toContain('\u91CD\u70B9\u8FFD\u67E5\u4E09\u70B9\u5341\u4E03\u5206');

    await wrapper.get('.locked-location').trigger('click');
    expect(wrapper.get('.unlock-hint').text()).toContain('Stage 2');
    expect(wrapper.get('.unlock-hint').text()).toContain('\u6536\u96C6\u5173\u952E\u7EBF\u7D22');
  });
});