import { beforeEach, describe, expect, it, vi } from 'vitest';
import { createPinia, setActivePinia } from 'pinia';
import { useSessionStore } from './sessionStore';

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

describe('sessionStore', () => {
  beforeEach(() => {
    setActivePinia(createPinia());
    axiosMock.get.mockReset();
    axiosMock.post.mockReset();
    axiosMock.patch.mockReset();
  });

  it('refreshes session detail after visiting a location', async () => {
    axiosMock.post.mockResolvedValueOnce({
      data: {
        success: true,
        data: {
          id: 7,
          sessionId: 5,
          locationId: 1,
          locationCode: 'CLOCK_TOWER',
          visitedAt: '2026-06-30T09:10:19'
        },
        message: 'ok'
      }
    });
    axiosMock.get.mockResolvedValueOnce({
      data: {
        success: true,
        data: {
          id: 5,
          userId: 1,
          caseId: 1,
          currentStage: 1,
          status: 'ACTIVE',
          availableLocations: [],
          availableNpcs: []
        },
        message: 'ok'
      }
    });

    const store = useSessionStore();
    const visit = await store.visit(5, 1);

    expect(axiosMock.post).toHaveBeenCalledWith('/sessions/5/locations/1/visit');
    expect(axiosMock.get).toHaveBeenCalledWith('/sessions/5');
    expect(visit.locationCode).toBe('CLOCK_TOWER');
    expect(store.session?.id).toBe(5);
  });
});