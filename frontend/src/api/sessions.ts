import { http, unwrap } from './http';
import type { CollectedClue } from './clues';

export interface GuestPlayer {
  id: number;
  displayName: string;
  guest: boolean;
}

export interface AvailableLocation {
  id: number;
  code: string;
  name: string;
  description: string;
  unlockStage: number;
}

export interface AvailableNpc {
  id: number;
  code: string;
  name: string;
  roleName: string;
  locationId: number;
  unlockStage: number;
}

export interface SessionDetail {
  id: number;
  userId: number;
  caseId: number;
  currentStage: number;
  status: string;
  availableLocations: AvailableLocation[];
  availableNpcs: AvailableNpc[];
}

export interface VisitLocationResponse {
  session: SessionDetail;
  location: AvailableLocation;
}

export interface NpcDialogueResponse {
  reply: string;
  mood: string;
  newlyCollectedClues: CollectedClue[];
  ruleHints: string[];
  suspicionDelta: number;
}

export function createGuest(): Promise<GuestPlayer> {
  return unwrap(http.post('/guest'));
}

export function createSession(userId: number, caseId: number): Promise<SessionDetail> {
  return unwrap(http.post('/sessions', { userId, caseId }));
}

export function getSession(sessionId: number | string): Promise<SessionDetail> {
  return unwrap(http.get(`/sessions/${sessionId}`));
}

export function visitLocation(sessionId: number | string, locationId: number | string): Promise<VisitLocationResponse> {
  return unwrap(http.post(`/sessions/${sessionId}/locations/${locationId}/visit`));
}

export function advanceSession(sessionId: number | string): Promise<SessionDetail> {
  return unwrap(http.post(`/sessions/${sessionId}/advance`));
}

export function sendNpcMessage(
  sessionId: number | string,
  npcId: number | string,
  question: string
): Promise<NpcDialogueResponse> {
  return unwrap(http.post(`/sessions/${sessionId}/npcs/${npcId}/messages`, { question }));
}