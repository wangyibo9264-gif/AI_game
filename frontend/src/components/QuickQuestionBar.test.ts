import { mount } from '@vue/test-utils';
import { describe, expect, it } from 'vitest';
import QuickQuestionBar from './QuickQuestionBar.vue';

describe('QuickQuestionBar', () => {
  it('emits a question when a prompt is clicked', async () => {
    const wrapper = mount(QuickQuestionBar);

    await wrapper.find('button').trigger('click');

    expect(wrapper.emitted('ask')?.[0]?.[0]).toBe('\u8BF7\u628A\u4F60\u8BB0\u5F97\u7684\u65F6\u95F4\u987A\u5E8F\u8BF4\u4E00\u904D\u3002');
  });
});