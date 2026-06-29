import { flushPromises, mount } from '@vue/test-utils';
import { createPinia, setActivePinia } from 'pinia';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import TruthReportView from './TruthReportView.vue';

const routerPush = vi.hoisted(() => vi.fn());
const apiMocks = vi.hoisted(() => ({
  listClues: vi.fn(),
  updateClue: vi.fn(),
  submitReport: vi.fn()
}));

vi.mock('vue-router', () => ({
  useRouter: () => ({ push: routerPush })
}));

vi.mock('../api/clues', () => ({
  listClues: apiMocks.listClues,
  updateClue: apiMocks.updateClue
}));

vi.mock('../api/reports', () => ({
  submitReport: apiMocks.submitReport
}));

describe('TruthReportView', () => {
  beforeEach(() => {
    setActivePinia(createPinia());
    routerPush.mockReset();
    Object.values(apiMocks).forEach((mock) => mock.mockReset());
    apiMocks.listClues.mockResolvedValue([
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
            importance: 'HIGH',
            status: 'CONFIRMED',
            collectedAt: '2026-06-29T00:00:00'
          }
        ]
      }
    ]);
    apiMocks.submitReport.mockResolvedValue({
      reportId: 33,
      truthScore: 30,
      clueScore: 40,
      ruleScore: 15,
      totalScore: 85,
      ending: 'Truth Lit',
      summary: 'Solid report.',
      missedPoints: 'None'
    });
  });

  it('submits report with collected clues and routes to result', async () => {
    const wrapper = mount(TruthReportView, {
      props: { sessionId: '9' },
      global: { plugins: [createPinia()] }
    });

    await flushPromises();
    const fields = wrapper.findAll('textarea');
    for (const field of fields) {
      await field.setValue('filled answer');
    }
    await wrapper.find('form').trigger('submit.prevent');
    await flushPromises();

    expect(apiMocks.listClues).toHaveBeenCalledWith('9');
    expect(apiMocks.submitReport).toHaveBeenCalledWith('9', {
      eventSummary: 'filled answer',
      responsiblePerson: 'filled answer',
      keyEvidence: 'filled answer',
      ruleExplanation: 'filled answer',
      npcLies: 'filled answer',
      conclusion: 'filled answer'
    });
    expect(routerPush).toHaveBeenCalledWith('/reports/33');
  });
});