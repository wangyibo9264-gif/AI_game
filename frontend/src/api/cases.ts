import { http, unwrap } from './http';

export interface CaseSummary {
  id: number;
  code: string;
  title: string;
  summary: string;
  difficulty: string;
  estimatedMinutes: number;
  status: string;
}

export interface CaseRule {
  id: number;
  ruleCode: string;
  ruleText: string;
  displayOrder: number;
}

export interface CaseLocation {
  id: number;
  code: string;
  name: string;
  description: string;
  unlockStage: number;
}

export interface CaseDetail extends CaseSummary {
  rules: CaseRule[];
  locations: CaseLocation[];
}

export function listCases(): Promise<CaseSummary[]> {
  return unwrap(http.get('/cases'));
}

export function getCase(caseId: number | string): Promise<CaseDetail> {
  return unwrap(http.get(`/cases/${caseId}`));
}