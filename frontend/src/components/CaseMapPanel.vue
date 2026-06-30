<template>
  <aside class="case-map-panel" :class="`case-map-panel--${mapDefinition.theme}`" aria-label="&#x6848;&#x4EF6;&#x5730;&#x56FE;">
    <div class="case-map-panel__head">
      <h2>{{ mapDefinition.title }}</h2>
      <p>{{ mapDefinition.subtitle }}</p>
    </div>

    <div class="map-canvas">
      <svg class="map-lines" viewBox="0 0 100 100" aria-hidden="true">
        <path v-for="path in connectorPaths" :key="path" :d="path" />
        <path v-for="path in mapDefinition.decorations" :key="path" class="map-lines__decoration" :d="path" />
      </svg>

      <button
        v-for="location in positionedLocations"
        :key="location.id"
        type="button"
        :data-location-code="location.code"
        :disabled="!location.unlocked"
        :class="[
          'map-node',
          { 'map-node--active': location.id === activeLocationId, 'map-node--locked': !location.unlocked }
        ]"
        :style="{ left: `${location.x}%`, top: `${location.y}%` }"
        @click="visit(location)"
      >
        <span class="map-node__pin" aria-hidden="true"></span>
        <span class="map-node__label">{{ location.name }}</span>
        <small>Stage {{ location.unlockStage }}</small>
      </button>
    </div>

    <div class="map-note">
      <strong>{{ activeLocation?.name || mapDefinition.noteTitle }}</strong>
      <p>{{ activeLocation?.description || mapDefinition.note }}</p>
    </div>
  </aside>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import type { AvailableLocation } from '../api/sessions';

interface MapPoint {
  x: number;
  y: number;
}

interface MapDefinition {
  title: string;
  subtitle: string;
  noteTitle: string;
  note: string;
  theme: 'clock' | 'rose' | 'fog';
  points: Record<string, MapPoint>;
  connectors: Array<[string, string]>;
  decorations: string[];
}

interface PositionedLocation extends AvailableLocation, MapPoint {
  unlocked: boolean;
}

const props = defineProps<{
  caseId: number;
  locations: AvailableLocation[];
  availableLocationIds: number[];
  activeLocationId: number | null;
}>();

const emit = defineEmits<{ visit: [locationId: number] }>();

const mapDefinitions: Record<string, MapDefinition> = {
  clock: {
    title: '\u65E7\u9547\u4E2D\u5FC3\u56FE',
    subtitle: '\u949F\u697C\u3001\u9547\u653F\u5385\u4E0E\u65E7\u77FF\u53E3\u88AB\u7164\u7070\u7EBF\u8FDE\u5728\u4E00\u8D77\u3002',
    noteTitle: '\u8C03\u67E5\u5730\u56FE',
    note: '\u70B9\u51FB\u5DF2\u89E3\u9501\u7684\u5730\u70B9\uFF0C\u5207\u6362\u5F53\u524D\u8C03\u67E5\u73B0\u573A\u3002',
    theme: 'clock',
    points: {
      CLOCK_TOWER: { x: 29, y: 25 },
      TOWN_HALL: { x: 70, y: 28 },
      MINE_GATE: { x: 25, y: 74 },
      BELL_ARCHIVE: { x: 55, y: 62 }
    },
    connectors: [
      ['CLOCK_TOWER', 'TOWN_HALL'],
      ['CLOCK_TOWER', 'BELL_ARCHIVE'],
      ['BELL_ARCHIVE', 'MINE_GATE']
    ],
    decorations: ['M18 18 C28 9 45 9 57 17', 'M14 85 C31 78 41 82 57 74', 'M74 18 l7 11 l-9 6']
  },
  rose: {
    title: '\u65C5\u9986\u697C\u5C42\u56FE',
    subtitle: '\u7B2C\u516B\u95F4\u623F\u88AB\u7EA2\u7B14\u5708\u51FA\uFF0C\u540E\u697C\u68AF\u50CF\u4E00\u6761\u6697\u7EBF\u3002',
    noteTitle: '\u623F\u95F4\u5E73\u9762\u56FE',
    note: '\u524D\u5385\u3001\u7B2C\u516B\u95F4\u623F\u3001\u540E\u697C\u68AF\u4E0E\u8D26\u623F\u5171\u540C\u7EC4\u6210\u5BC6\u5BA4\u52A8\u7EBF\u3002',
    theme: 'rose',
    points: {
      ROSE_LOBBY: { x: 24, y: 62 },
      ROOM_EIGHT: { x: 66, y: 28 },
      BACK_STAIRS: { x: 80, y: 70 },
      LEDGER_OFFICE: { x: 42, y: 30 }
    },
    connectors: [
      ['ROSE_LOBBY', 'LEDGER_OFFICE'],
      ['LEDGER_OFFICE', 'ROOM_EIGHT'],
      ['ROOM_EIGHT', 'BACK_STAIRS'],
      ['BACK_STAIRS', 'ROSE_LOBBY']
    ],
    decorations: ['M12 20 H88 V82 H12 Z', 'M50 20 V82', 'M64 25 c7 -8 18 1 7 12']
  },
  fog: {
    title: '\u96FE\u6E2F\u7801\u5934\u56FE',
    subtitle: '\u5CB8\u7EBF\u88AB\u6F6E\u6C34\u5207\u5F00\uFF0C\u8239\u7968\u548C\u6E21\u8239\u628A\u5931\u8E2A\u540D\u5355\u62D6\u51FA\u96FE\u9762\u3002',
    noteTitle: '\u6E2F\u53E3\u793A\u610F\u56FE',
    note: '\u5CB8\u8FB9\u8282\u70B9\u548C\u6C34\u9762\u6E21\u8239\u5206\u5F00\u663E\u793A\uFF0C\u65B9\u4FBF\u6838\u5BF9\u822A\u7EBF\u3002',
    theme: 'fog',
    points: {
      FOG_PIER: { x: 29, y: 38 },
      FERRY_DECK: { x: 70, y: 50 },
      TICKET_HOUSE: { x: 30, y: 70 },
      HARBOR_ARCHIVE: { x: 76, y: 22 }
    },
    connectors: [
      ['FOG_PIER', 'FERRY_DECK'],
      ['FOG_PIER', 'TICKET_HOUSE'],
      ['TICKET_HOUSE', 'HARBOR_ARCHIVE']
    ],
    decorations: ['M9 50 C24 40 35 47 47 39 C58 32 70 35 90 27', 'M8 68 C25 61 39 66 54 59 C67 53 78 57 92 50', 'M58 48 q12 -8 24 0 q-12 8 -24 0']
  }
};

const mapDefinition = computed(() => {
  const codes = props.locations.map((location) => location.code).join(',');
  if (codes.includes('ROSE_') || props.caseId === 2) return mapDefinitions.rose;
  if (codes.includes('FOG_') || props.caseId === 3) return mapDefinitions.fog;
  return mapDefinitions.clock;
});

const availableIds = computed(() => new Set(props.availableLocationIds));

const positionedLocations = computed<PositionedLocation[]>(() => {
  const fallbackPoints = [
    { x: 24, y: 26 },
    { x: 72, y: 30 },
    { x: 28, y: 72 },
    { x: 70, y: 70 }
  ];

  return props.locations.map((location, index) => ({
    ...location,
    ...(mapDefinition.value.points[location.code] ?? fallbackPoints[index % fallbackPoints.length]),
    unlocked: availableIds.value.has(location.id)
  }));
});

const connectorPaths = computed(() => {
  return mapDefinition.value.connectors
    .map(([fromCode, toCode]) => {
      const from = mapDefinition.value.points[fromCode];
      const to = mapDefinition.value.points[toCode];
      if (!from || !to) return '';
      const controlX = (from.x + to.x) / 2;
      const controlY = Math.min(from.y, to.y) - 8;
      return `M${from.x} ${from.y} Q${controlX} ${controlY} ${to.x} ${to.y}`;
    })
    .filter(Boolean);
});

const activeLocation = computed(() => {
  return props.locations.find((location) => location.id === props.activeLocationId) ?? null;
});

function visit(location: PositionedLocation) {
  if (!location.unlocked) return;
  emit('visit', location.id);
}
</script>

<style scoped>
.case-map-panel {
  display: grid;
  align-content: start;
  gap: 12px;
}

.case-map-panel__head {
  display: grid;
  gap: 5px;
}

.case-map-panel h2 {
  margin: 0;
  color: var(--color-old-paper);
  font-size: 18px;
}

.case-map-panel__head p,
.map-note p {
  margin: 0;
  color: var(--color-muted);
  font-size: 13px;
  line-height: 1.65;
}

.map-canvas {
  position: relative;
  min-height: 360px;
  overflow: hidden;
  border: 1px solid rgba(216, 201, 163, 0.23);
  border-radius: 8px;
  background:
    linear-gradient(90deg, rgba(7, 17, 13, 0.13) 1px, transparent 1px),
    linear-gradient(0deg, rgba(7, 17, 13, 0.11) 1px, transparent 1px),
    radial-gradient(circle at 28% 24%, rgba(241, 184, 75, 0.11), transparent 25%),
    #d0bd8f;
  background-size: 22px 22px, 22px 22px, auto, auto;
  box-shadow: inset 0 0 42px rgba(7, 17, 13, 0.34);
}

.map-canvas::before,
.map-canvas::after {
  position: absolute;
  content: '';
  pointer-events: none;
}

.map-canvas::before {
  inset: 14px;
  border: 1px dashed rgba(78, 45, 35, 0.36);
  border-radius: 7px;
}

.map-canvas::after {
  inset: 0;
  background: linear-gradient(135deg, transparent 48%, rgba(155, 51, 44, 0.16) 49%, transparent 51%);
  opacity: 0.45;
}

.map-lines {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  fill: none;
  stroke: rgba(78, 45, 35, 0.55);
  stroke-dasharray: 4 5;
  stroke-linecap: round;
  stroke-width: 1.25;
}

.map-lines__decoration {
  stroke: rgba(7, 17, 13, 0.36);
  stroke-dasharray: none;
  stroke-width: 0.8;
}

.map-node {
  position: absolute;
  display: grid;
  gap: 2px;
  min-width: 116px;
  max-width: 138px;
  min-height: 54px;
  padding: 9px 10px 9px 28px;
  border: 1px solid rgba(78, 45, 35, 0.42);
  border-radius: 7px;
  color: #251b13;
  text-align: left;
  background: rgba(242, 229, 188, 0.82);
  box-shadow: 0 8px 18px rgba(7, 17, 13, 0.18);
  cursor: pointer;
  transform: translate(-50%, -50%) rotate(-1deg);
  transition: border-color 160ms ease, box-shadow 160ms ease, transform 160ms ease;
  z-index: 1;
}

.map-node:hover:not(:disabled) {
  border-color: rgba(155, 51, 44, 0.78);
  box-shadow: 0 10px 24px rgba(7, 17, 13, 0.24);
  transform: translate(-50%, -52%) rotate(0deg);
}

.map-node:disabled {
  cursor: not-allowed;
}

.map-node__pin {
  position: absolute;
  left: 10px;
  top: 13px;
  width: 10px;
  height: 10px;
  border: 1px solid rgba(78, 45, 35, 0.58);
  background: rgba(241, 184, 75, 0.9);
  transform: rotate(45deg);
}

.map-node__label {
  color: #24160f;
  font-size: 14px;
  font-weight: 800;
  line-height: 1.2;
}

.map-node small {
  color: rgba(78, 45, 35, 0.78);
  font-size: 11px;
}

.map-node--active {
  border-color: rgba(155, 51, 44, 0.95);
  background: rgba(247, 222, 164, 0.94);
  box-shadow: 0 0 0 3px rgba(155, 51, 44, 0.15), 0 10px 24px rgba(7, 17, 13, 0.24);
}

.map-node--active .map-node__pin {
  background: var(--color-rust-red);
}

.map-node--locked {
  opacity: 0.48;
  filter: grayscale(0.45);
}

.map-node--locked .map-node__pin {
  background: rgba(78, 45, 35, 0.36);
}

.map-note {
  display: grid;
  gap: 5px;
  padding: 12px 0 0;
  border-top: 1px solid var(--color-line);
}

.map-note strong {
  color: var(--color-old-paper);
}

.case-map-panel--rose .map-canvas {
  background:
    linear-gradient(90deg, rgba(85, 37, 42, 0.14) 1px, transparent 1px),
    linear-gradient(0deg, rgba(85, 37, 42, 0.12) 1px, transparent 1px),
    radial-gradient(circle at 66% 28%, rgba(155, 51, 44, 0.14), transparent 24%),
    #d2bd9d;
  background-size: 24px 24px, 24px 24px, auto, auto;
}

.case-map-panel--fog .map-canvas {
  background:
    linear-gradient(100deg, rgba(216, 201, 163, 0.18), transparent 38%),
    repeating-linear-gradient(0deg, rgba(56, 76, 71, 0.12) 0 2px, transparent 2px 16px),
    #c7b991;
}

@media (max-width: 980px) {
  .map-canvas {
    min-height: 300px;
  }

  .map-node {
    min-width: 104px;
    max-width: 122px;
  }
}
</style>