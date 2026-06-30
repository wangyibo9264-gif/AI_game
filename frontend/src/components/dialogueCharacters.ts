export interface DialogueCharacterInput {
  code: string;
  name: string;
  roleName: string;
}

export interface DialogueCharacter {
  id: string;
  displayName: string;
  roleName: string;
  badge: string;
  motif: string;
  palette: {
    primary: string;
    secondary: string;
    accent: string;
  };
}

const npcPortraits: Record<string, Omit<DialogueCharacter, 'displayName' | 'roleName'>> = {
  CLOCK_KEEPER: {
    id: 'npc-clock-keeper',
    badge: '\u949f',
    motif: '\u949f\u697c\u503c\u591c',
    palette: { primary: '#6f7f69', secondary: '#24332d', accent: '#d8c9a3' }
  },
  MINER_WIDOW: {
    id: 'npc-miner-widow',
    badge: '\u77ff',
    motif: '\u7ea2\u624b\u5957',
    palette: { primary: '#8f3d36', secondary: '#2b1816', accent: '#d8c9a3' }
  },
  ARCHIVIST: {
    id: 'npc-archivist',
    badge: '\u6863',
    motif: '\u65e7\u6863\u6848',
    palette: { primary: '#8b7a54', secondary: '#2f281b', accent: '#f1b84b' }
  },
  HOTEL_OWNER: {
    id: 'npc-hotel-owner',
    badge: '\u5319',
    motif: '\u7b2c\u516b\u628a\u94a5\u5319',
    palette: { primary: '#7a5f88', secondary: '#221927', accent: '#d8c9a3' }
  },
  MAID: {
    id: 'npc-maid',
    badge: '\u94c3',
    motif: '\u524d\u53f0\u94c3',
    palette: { primary: '#a2794a', secondary: '#2b2118', accent: '#f1b84b' }
  },
  MIRROR_GUEST: {
    id: 'npc-mirror-guest',
    badge: '\u955c',
    motif: '\u955c\u4e2d\u623f\u53f7',
    palette: { primary: '#738993', secondary: '#18262b', accent: '#d8c9a3' }
  },
  FERRYMAN: {
    id: 'npc-ferryman',
    badge: '\u7968',
    motif: '\u672b\u73ed\u8239\u7968',
    palette: { primary: '#4d7d85', secondary: '#12282d', accent: '#f1b84b' }
  },
  DOCK_WORKER: {
    id: 'npc-dock-worker',
    badge: '\u706f',
    motif: '\u96fe\u6e2f\u706f',
    palette: { primary: '#5d6f8f', secondary: '#141d2c', accent: '#d8c9a3' }
  },
  TICKET_CLERK: {
    id: 'npc-ticket-clerk',
    badge: '\u8d26',
    motif: '\u8239\u7968\u540d\u518c',
    palette: { primary: '#867148', secondary: '#2d2718', accent: '#f1b84b' }
  }
};

export const playerCharacter: DialogueCharacter = {
  id: 'player',
  displayName: '\u6211',
  roleName: '\u8c03\u67e5\u5458',
  badge: '\u67e5',
  motif: '\u5c18\u9547\u6863\u6848',
  palette: { primary: '#d8c9a3', secondary: '#5c4b2f', accent: '#f1b84b' }
};

export function getDialogueCharacter(npc: DialogueCharacterInput): DialogueCharacter {
  const portrait = npcPortraits[npc.code] ?? {
    id: 'npc-unknown',
    badge: '\u8bc1',
    motif: '\u9547\u6c11\u8bc1\u8a00',
    palette: { primary: '#7c887b', secondary: '#202a24', accent: '#d8c9a3' }
  };

  return {
    ...portrait,
    displayName: npc.name,
    roleName: npc.roleName
  };
}
