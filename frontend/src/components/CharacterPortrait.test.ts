import { mount } from '@vue/test-utils';
import { describe, expect, it } from 'vitest';
import CharacterPortrait from './CharacterPortrait.vue';
import { playerCharacter } from './dialogueCharacters';

describe('CharacterPortrait', () => {
  it('renders the character badge, name, role, and motif', () => {
    const wrapper = mount(CharacterPortrait, {
      props: {
        character: playerCharacter,
        side: 'right'
      }
    });

    expect(wrapper.find('[data-character-id="player"]').exists()).toBe(true);
    expect(wrapper.text()).toContain('\u6211');
    expect(wrapper.text()).toContain('\u8c03\u67e5\u5458');
    expect(wrapper.text()).toContain('\u5c18\u9547\u6863\u6848');
    expect(wrapper.find('.character-portrait--right').exists()).toBe(true);
  });
});
