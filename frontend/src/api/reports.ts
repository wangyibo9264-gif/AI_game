import { http, unwrap } from './http';

export interface SubmitTruthReportRequest {
  eventSummary: string;
  responsiblePerson: string;
  keyEvidence: string;
  ruleExplanation: string;
  npcLies: string;
  conclusion: string;
}

export interface TruthReportResult {
  reportId: number;
  truthScore: number;
  clueScore: number;
  ruleScore: number;
  totalScore: number;
  ending: string;
  summary: string;
  missedPoints: string;
}

export function submitReport(
  sessionId: number | string,
  payload: SubmitTruthReportRequest
): Promise<TruthReportResult> {
  return unwrap(http.post(`/sessions/${sessionId}/reports`, payload));
}