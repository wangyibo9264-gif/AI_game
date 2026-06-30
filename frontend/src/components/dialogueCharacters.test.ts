import { describe, expect, it } from 'vitest';
import { getDialogueCharacter, playerCharacter } from './dialogueCharacters';

describe('dialogueCharacters', () => {
  it('returns a fixed player investigator portrait', () => {
    expect(playerCharacter.id).toBe('player');
    expect(playerCharacter.displayName).toBe('\u6211');
    expect(playerCharacter.badge).toBe('\u67e5');
  });

  it('returns distinct configured portraits for known NPC codes', () => {
    const clockNpc = getDialogueCharacter({ code: 'CLOCK_KEEPER', name: '\u6797\u5b88\u591c', roleName: '\u503c\u591c\u4eba' });
    const hotelNpc = getDialogueCharacter({ code: 'HOTEL_OWNER', name: '\u5b5f\u8001\u677f', roleName: '\u65c5\u9986\u8001\u677f' });
    const harborNpc = getDialogueCharacter({ code: 'FERRYMAN', name: '\u9648\u6446\u6e21', roleName: '\u6446\u6e21\u4eba' });

    expect(clockNpc.displayName).toBe('\u6797\u5b88\u591c');
    expect(clockNpc.badge).toBe('\u949f');
    expect(hotelNpc.badge).toBe('\u5319');
    expect(harborNpc.badge).toBe('\u7968');
    expect(new Set([clockNpc.palette.primary, hotelNpc.palette.primary, harborNpc.palette.primary]).size).toBe(3);
  });

  it('falls back to a witness portrait for unknown NPC codes', () => {
    const character = getDialogueCharacter({ code: 'UNKNOWN_NPC', name: '\u964c\u751f\u4eba', roleName: '\u9547\u6c11' });

    expect(character.id).toBe('npc-unknown');
    expect(character.displayName).toBe('\u964c\u751f\u4eba');
    expect(character.roleName).toBe('\u9547\u6c11');
    expect(character.badge).toBe('\u8bc1');
  });
});
