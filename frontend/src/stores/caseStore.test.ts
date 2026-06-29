import { beforeEach, describe, expect, it, vi } from 'vitest';
import { createPinia, setActivePinia } from 'pinia';
import { useCaseStore } from './caseStore';

const axiosMock = vi.hoisted(() => ({
  get: vi.fn(),
  post: vi.fn(),
  patch: vi.fn()
}));

vi.mock('axios', () => ({
  default: {
    create: () => axiosMock
  }
}));

describe('caseStore', () => {
  beforeEach(() => {
    setActivePinia(createPinia());
    axiosMock.get.mockReset();
    axiosMock.post.mockReset();
    axiosMock.patch.mockReset();
  });

  it('populates case list from GET /cases', async () => {
    axiosMock.get.mockResolvedValueOnce({
      data: {
        success: true,
        data: [
          {
            id: 1,
            code: 'CLOCK_317',
            title: 'Clock Case',
            summary: 'A fixed rumor case.',
            difficulty: 'NORMAL',
            estimatedMinutes: 45,
            status: 'ACTIVE'
          }
        ],
        message: 'ok'
      }
    });

    const store = useCaseStore();
    await store.loadCases();

    expect(axiosMock.get).toHaveBeenCalledWith('/cases');
    expect(store.cases).toHaveLength(1);
    expect(store.cases[0].code).toBe('CLOCK_317');
    expect(store.loading).toBe(false);
    expect(store.error).toBeNull();
  });
});