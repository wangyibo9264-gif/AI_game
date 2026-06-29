import { defineStore } from 'pinia';
import { submitReport, type SubmitTruthReportRequest, type TruthReportResult } from '../api/reports';

interface ReportState {
  result: TruthReportResult | null;
  loading: boolean;
  error: string | null;
}

export const useReportStore = defineStore('reportStore', {
  state: (): ReportState => ({
    result: null,
    loading: false,
    error: null
  }),
  actions: {
    async submit(sessionId: number | string, payload: SubmitTruthReportRequest) {
      this.loading = true;
      this.error = null;
      try {
        this.result = await submitReport(sessionId, payload);
        return this.result;
      } catch (error) {
        this.error = error instanceof Error ? error.message : 'Failed to submit report';
        throw error;
      } finally {
        this.loading = false;
      }
    }
  }
});