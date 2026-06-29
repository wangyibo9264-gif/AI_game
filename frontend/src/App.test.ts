import { describe, expect, it } from 'vitest';
import { mount } from '@vue/test-utils';
import { createMemoryHistory, createRouter } from 'vue-router';
import App from './App.vue';

const TestView = { template: '<div data-testid="router-view">&#x6848;&#x4EF6;&#x67DC;&#x5DF2;&#x6302;&#x8F7D;</div>' };

describe('App', () => {
  it('renders router view without crashing', async () => {
    const router = createRouter({
      history: createMemoryHistory(),
      routes: [{ path: '/', component: TestView }]
    });

    router.push('/');
    await router.isReady();

    const wrapper = mount(App, {
      global: {
        plugins: [router]
      }
    });

    expect(wrapper.get('[data-testid="router-view"]').text()).toContain('\u6848\u4EF6\u67DC\u5DF2\u6302\u8F7D');
  });
});