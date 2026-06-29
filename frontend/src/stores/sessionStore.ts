import { defineStore } from 'pinia';
import {
  advanceSession,
  createGuest,
  createSession,
  getSession,
  visitLocation,
  type GuestPlayer,
  type SessionDetail
} from '../api/sessions';

interface SessionState {
  guest: GuestPlayer | null;
  session: SessionDetail | null;
  loading: boolean;
  error: string | null;
}

export const useSessionStore = defineStore('sessionStore', {
  state: (): SessionState => ({
    guest: null,
    session: null,
    loading: false,
    error: null
  }),
  actions: {
    async ensureGuest() {
      if (this.guest) return this.guest;
      this.loading = true;
      this.error = null;
      try {
        this.guest = await createGuest();
        return this.guest;
      } catch (error) {
        this.error = error instanceof Error ? error.message : 'Failed to create guest';
        throw error;
      } finally {
        this.loading = false;
      }
    },
    async startSession(caseId: number) {
      const guest = await this.ensureGuest();
      this.session = await createSession(guest.id, caseId);
      return this.session;
    },
    async loadSession(sessionId: number | string) {
      this.loading = true;
      this.error = null;
      try {
        this.session = await getSession(sessionId);
      } catch (error) {
        this.error = error instanceof Error ? error.message : 'Failed to load session';
        throw error;
      } finally {
        this.loading = false;
      }
    },
    async visit(sessionId: number | string, locationId: number | string) {
      const response = await visitLocation(sessionId, locationId);
      this.session = response.session;
      return response;
    },
    async advance(sessionId: number | string) {
      this.session = await advanceSession(sessionId);
      return this.session;
    }
  }
});