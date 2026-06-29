import { http, unwrap } from './http';

export type ClueCategory = 'PERSON' | 'TIME' | 'LOCATION' | 'EVIDENCE' | 'RULE' | 'CONTRADICTION';
export type ClueImportance = 'LOW' | 'NORMAL' | 'HIGH';
export type CollectedClueStatus = 'UNRESOLVED' | 'CONFIRMED' | 'DISMISSED';

export interface CollectedClue {
  id: number;
  clueId: number;
  clueCode: string;
  title: string;
  content: string;
  category: ClueCategory;
  importance: ClueImportance;
  status: CollectedClueStatus;
  collectedAt: string;
}

export interface ClueArchiveGroup {
  category: ClueCategory;
  clues: CollectedClue[];
}

export interface UpdateCollectedClueRequest {
  importance: ClueImportance;
  status: CollectedClueStatus;
}

export function listClues(sessionId: number | string): Promise<ClueArchiveGroup[]> {
  return unwrap(http.get(`/sessions/${sessionId}/clues`));
}

export function updateClue(
  sessionId: number | string,
  clueId: number | string,
  payload: UpdateCollectedClueRequest
): Promise<CollectedClue> {
  return unwrap(http.patch(`/sessions/${sessionId}/clues/${clueId}`, payload));
}