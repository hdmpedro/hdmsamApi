import { injectGlobalWebcomponentCss } from 'Frontend/generated/jar-resources/theme-util.js';

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
import '@vaadin/vaadin-lumo-styles/sizing.js';
import '@vaadin/vaadin-lumo-styles/spacing.js';
import '@vaadin/vaadin-lumo-styles/style.js';
import '@vaadin/vaadin-lumo-styles/vaadin-iconset.js';
import 'Frontend/generated/jar-resources/ReactRouterOutletElement.tsx';

const loadOnDemand = (key) => {
  const pending = [];
  if (key === '86f87ab8c18e68fb38208c4b3cc79000c47a8cb38e765c4dafb042b080b99b3b') {
    pending.push(import('./chunks/chunk-84a6ecde73e562f3624a6c3cee0a81296c2e9a6b5140e3c0461ce683ecbf9333.js'));
  }
  if (key === '2f145e39425b4b3352fe618ad68694e355e9964101f40c42f7647637ac8e1b87') {
    pending.push(import('./chunks/chunk-282791df85b813a8a21002eab10b2769592c24c290a08c1c572a2a230224f243.js'));
  }
  if (key === 'be54fe9b83dfd89f0959be3bb5f96ff40bbeb804c10a1641af690760ba0742bb') {
    pending.push(import('./chunks/chunk-282791df85b813a8a21002eab10b2769592c24c290a08c1c572a2a230224f243.js'));
  }
  if (key === '1e7d05536957fa69234f12e245e5af235a2ccee5637422c57a3dd2d9c2b73828') {
    pending.push(import('./chunks/chunk-79ed762e478dad24da347f621baaf0dba95cb59a89fb0e92d2e4a10e2911b960.js'));
  }
  if (key === '0ce787d9c214100f42cac64da5e7cc06144e98a81fee84c29a06fdf8dfacd161') {
    pending.push(import('./chunks/chunk-84a6ecde73e562f3624a6c3cee0a81296c2e9a6b5140e3c0461ce683ecbf9333.js'));
  }
  if (key === '673503e3ec2c9e31f3a103af48aabee07ec77b7f6b624c37f7811b587f20a991') {
    pending.push(import('./chunks/chunk-77bbb697a645cde281120b550f3da3ad793d3c3249e61b487c09ae14fd2f6d81.js'));
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