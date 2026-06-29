import { defineStore } from 'pinia';
import { getCase, listCases, type CaseDetail, type CaseSummary } from '../api/cases';

interface CaseState {
  cases: CaseSummary[];
  currentCase: CaseDetail | null;
  loading: boolean;
  error: string | null;
}

export const useCaseStore = defineStore('caseStore', {
  state: (): CaseState => ({
    cases: [],
    currentCase: null,
    loading: false,
    error: null
  }),
  actions: {
    async loadCases() {
      this.loading = true;
      this.error = null;
      try {
        this.cases = await listCases();
      } catch (error) {
        this.error = error instanceof Error ? error.message : 'Failed to load cases';
        throw error;
      } finally {
        this.loading = false;
      }
    },
    async loadCase(caseId: number | string) {
      this.loading = true;
      this.error = null;
      try {
        this.currentCase = await getCase(caseId);
      } catch (error) {
        this.error = error instanceof Error ? error.message : 'Failed to load case';
        throw error;
      } finally {
        this.loading = false;
      }
    }
  }
});