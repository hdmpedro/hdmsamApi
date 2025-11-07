import '@vaadin/polymer-legacy-adapter/style-modules.js';
import '@vaadin/password-field/src/vaadin-password-field.js';
import '@vaadin/tooltip/src/vaadin-tooltip.js';
import '@vaadin/button/src/vaadin-button.js';
import '@vaadin/text-field/src/vaadin-text-field.js';
import '@vaadin/vertical-layout/src/vaadin-vertical-layout.js';
import 'Frontend/generated/jar-resources/disableOnClickFunctions.js';
import '@vaadin/notification/src/vaadin-notification.js';
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
    pending.push(import('./chunks/chunk-79ed762e478dad24da347f621baaf0dba95cb59a89fb0e92d2e4a10e2911b960.js'));
  }
  if (key === 'be54fe9b83dfd89f0959be3bb5f96ff40bbeb804c10a1641af690760ba0742bb') {
    pending.push(import('./chunks/chunk-d55a0b92f42c32a5fa7da064eb68ac5306be1bc428975620133d4b64a7697c7e.js'));
  }
  if (key === 'd4d0fa0ed9eb22550a7bb6b2d2dde53cc65d488c757b669910365452aa8a20cb') {
    pending.push(import('./chunks/chunk-84a6ecde73e562f3624a6c3cee0a81296c2e9a6b5140e3c0461ce683ecbf9333.js'));
  }
  if (key === '0e6886e35ebe49b2da5da8467df3b5e447122724c119952e07a40e7f360fe243') {
    pending.push(import('./chunks/chunk-77bbb697a645cde281120b550f3da3ad793d3c3249e61b487c09ae14fd2f6d81.js'));
  }
  if (key === 'b29008eb7be5015d8a076ff2718aa419ee827844fd4cba4e7a5a4b9477e62ea5') {
    pending.push(import('./chunks/chunk-84a6ecde73e562f3624a6c3cee0a81296c2e9a6b5140e3c0461ce683ecbf9333.js'));
  }
  if (key === 'c8753f588c917731c275c5c5c9f67d1ab8d818e576a10a617014ad733cd4fd23') {
    pending.push(import('./chunks/chunk-282791df85b813a8a21002eab10b2769592c24c290a08c1c572a2a230224f243.js'));
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