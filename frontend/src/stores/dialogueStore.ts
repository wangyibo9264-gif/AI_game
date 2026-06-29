import { defineStore } from 'pinia';
import { sendNpcMessage, type NpcDialogueResponse } from '../api/sessions';

interface DialogueState {
  replies: NpcDialogueResponse[];
  loading: boolean;
  error: string | null;
}

export const useDialogueStore = defineStore('dialogueStore', {
  state: (): DialogueState => ({
    replies: [],
    loading: false,
    error: null
  }),
  actions: {
    async send(sessionId: number | string, npcId: number | string, question: string) {
      this.loading = true;
      this.error = null;
      try {
        const response = await sendNpcMessage(sessionId, npcId, question);
        this.replies.push(response);
        return response;
      } catch (error) {
        this.error = error instanceof Error ? error.message : 'Failed to send message';
        throw error;
      } finally {
        this.loading = false;
      }
    }
  }
});