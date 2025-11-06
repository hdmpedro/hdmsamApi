import '@vaadin/polymer-legacy-adapter/style-modules.js';
import '@vaadin/password-field/theme/lumo/vaadin-password-field.js';
import '@vaadin/tooltip/theme/lumo/vaadin-tooltip.js';
import '@vaadin/button/theme/lumo/vaadin-button.js';
import '@vaadin/text-field/theme/lumo/vaadin-text-field.js';
import '@vaadin/vertical-layout/theme/lumo/vaadin-vertical-layout.js';
import 'Frontend/generated/jar-resources/disableOnClickFunctions.js';
import '@vaadin/notification/theme/lumo/vaadin-notification.js';
import 'Frontend/generated/jar-resources/flow-component-renderer.js';
import '@vaadin/common-frontend/ConnectionIndicator.js';
import '@vaadin/vaadin-lumo-styles/color-global.js';
import '@vaadin/vaadin-lumo-styles/typography-global.js';
import '@vaadin/vaadin-lumo-styles/sizing.js';
import '@vaadin/vaadin-lumo-styles/spacing.js';
import '@vaadin/vaadin-lumo-styles/style.js';
import '@vaadin/vaadin-lumo-styles/vaadin-iconset.js';
import 'Frontend/generated/jar-resources/ReactRouterOutletElement.tsx';

const loadOnDemand = (key) => {
  const pending = [];
  if (key === '1e7d05536957fa69234f12e245e5af235a2ccee5637422c57a3dd2d9c2b73828') {
    pending.push(import('./chunks/chunk-cd6a3b6f6faebed1493d078a30ed7b10109a53d1122ed5addaf07959b749bfe6.js'));
  }
  if (key === '0e6886e35ebe49b2da5da8467df3b5e447122724c119952e07a40e7f360fe243') {
    pending.push(import('./chunks/chunk-5ae9a6d13b02f6ebf5a43785229bf6a3d5181f0e42e55bb142a42ac4f6ac34b3.js'));
  }
  if (key === 'c8753f588c917731c275c5c5c9f67d1ab8d818e576a10a617014ad733cd4fd23') {
    pending.push(import('./chunks/chunk-b17748ec3dad3460bb8d6c632bb2020b05ee770ec09dca32e2d59008f16210cc.js'));
  }
  if (key === 'd4d0fa0ed9eb22550a7bb6b2d2dde53cc65d488c757b669910365452aa8a20cb') {
    pending.push(import('./chunks/chunk-cb4c03448b95f33e73a78ba3c6848a1c2ddb17d1695b0107e4a5461f56d4297f.js'));
  }
  if (key === 'b29008eb7be5015d8a076ff2718aa419ee827844fd4cba4e7a5a4b9477e62ea5') {
    pending.push(import('./chunks/chunk-cb4c03448b95f33e73a78ba3c6848a1c2ddb17d1695b0107e4a5461f56d4297f.js'));
  }
  if (key === 'be54fe9b83dfd89f0959be3bb5f96ff40bbeb804c10a1641af690760ba0742bb') {
    pending.push(import('./chunks/chunk-7de2466ed7db69499142f15525dc8ba8b971a19cba373b990985d3e8183a216d.js'));
  }
  return Promise.all(pending);
}

window.Vaadin = window.Vaadin || {};
window.Vaadin.Flow = window.Vaadin.Flow || {};
window.Vaadin.Flow.loadOnDemand = loadOnDemand;
window.Vaadin.Flow.resetFocus = () => {
 let ae=document.activeElement;
 while(ae&&ae.shadowRoot) ae = ae.shadowRoot.activeElement;
 return !ae || ae.blur() || ae.focus() || true;
}