import { flushPromises, mount } from '@vue/test-utils';
import { createPinia, setActivePinia } from 'pinia';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import CaseDetailView from './CaseDetailView.vue';

const routerPush = vi.hoisted(() => vi.fn());
const apiMocks = vi.hoisted(() => ({
  getCase: vi.fn(),
  listCases: vi.fn(),
  createGuest: vi.fn(),
  createSession: vi.fn(),
  getSession: vi.fn(),
  visitLocation: vi.fn(),
  advanceSession: vi.fn(),
  sendNpcMessage: vi.fn()
}));

vi.mock('vue-router', () => ({
  useRouter: () => ({ push: routerPush }),
  RouterLink: { template: '<a><slot /></a>' }
}));

vi.mock('../api/cases', () => ({
  getCase: apiMocks.getCase,
  listCases: apiMocks.listCases
}));

vi.mock('../api/sessions', () => ({
  createGuest: apiMocks.createGuest,
  createSession: apiMocks.createSession,
  getSession: apiMocks.getSession,
  visitLocation: apiMocks.visitLocation,
  advanceSession: apiMocks.advanceSession,
  sendNpcMessage: apiMocks.sendNpcMessage
}));

describe('CaseDetailView', () => {
  beforeEach(() => {
    setActivePinia(createPinia());
    routerPush.mockReset();
    Object.values(apiMocks).forEach((mock) => mock.mockReset());
    apiMocks.getCase.mockResolvedValue({
      id: 1,
      code: 'CLOCK_317',
      title: 'Clock Case',
      summary: 'A rumor around a stopped clock.',
      difficulty: 'NORMAL',
      estimatedMinutes: 45,
      status: 'ACTIVE',
      rules: [{ id: 11, ruleCode: 'R1', ruleText: 'Do not trust the clock.', displayOrder: 1 }],
      locations: [{ id: 21, code: 'ALLEY', name: 'Back Alley', description: 'A wet alley.', unlockStage: 1 }]
    });
    apiMocks.createGuest.mockResolvedValue({ id: 7, displayName: 'guest-7', guest: true });
    apiMocks.createSession.mockResolvedValue({
      id: 9,
      userId: 7,
      caseId: 1,
      currentStage: 1,
      status: 'ACTIVE',
      availableLocations: [],
      availableNpcs: []
    });
  });

  it('creates a guest session and routes into investigation', async () => {
    const wrapper = mount(CaseDetailView, {
      props: { caseId: '1' },
      global: { plugins: [createPinia()] }
    });

    await flushPromises();
    await wrapper.get('.start-button').trigger('click');
    await flushPromises();

    expect(wrapper.get('.mission-brief').text()).toContain('\u672C\u6848\u4EFB\u52A1');
    expect(wrapper.get('.mission-brief').text()).toContain('\u91CD\u70B9\u8FFD\u67E5\u4E09\u70B9\u5341\u4E03\u5206');
    expect(apiMocks.getCase).toHaveBeenCalledWith('1');
    expect(apiMocks.createGuest).toHaveBeenCalledTimes(1);
    expect(apiMocks.createSession).toHaveBeenCalledWith(7, 1);
    expect(routerPush).toHaveBeenCalledWith('/sessions/9');
  });
});