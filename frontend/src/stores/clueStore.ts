import { defineStore } from 'pinia';
import {
  listClues,
  updateClue,
  type ClueArchiveGroup,
  type CollectedClueStatus,
  type ClueImportance
} from '../api/clues';

interface ClueState {
  groups: ClueArchiveGroup[];
  loading: boolean;
  error: string | null;
}

export const useClueStore = defineStore('clueStore', {
  state: (): ClueState => ({
    groups: [],
    loading: false,
    error: null
  }),
  actions: {
    async loadClues(sessionId: number | string) {
      this.loading = true;
      this.error = null;
      try {
        this.groups = await listClues(sessionId);
      } catch (error) {
        this.error = error instanceof Error ? error.message : 'Failed to load clues';
        throw error;
      } finally {
        this.loading = false;
      }
    },
    async update(sessionId: number | string, clueId: number | string, importance: ClueImportance, status: CollectedClueStatus) {
      const updated = await updateClue(sessionId, clueId, { importance, status });
      this.groups = this.groups.map((group) => ({
        ...group,
        clues: group.clues.map((clue) => (clue.clueId === updated.clueId ? updated : clue))
      }));
      return updated;
    }
  }
});