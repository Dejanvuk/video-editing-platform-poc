import React, { FC } from 'react';

import './style.css';

const ToolsPanel: FC = () => {
  return (
    <div className="tools-panel">
      TOOLS PANEL
      <button type="button" className="tools-btn">
        V
      </button>
      <button type="button" className="tools-btn">
        M
      </button>
      <button type="button" className="tools-btn">
        B
      </button>
      <button type="button" className="tools-btn">
        N
      </button>
      <button type="button" className="tools-btn">
        X
      </button>
      <button type="button" className="tools-btn">
        C
      </button>
      <button type="button" className="tools-btn">
        Y
      </button>
      <button type="button" className="tools-btn">
        U
      </button>
    </div>
  );
};

export default ToolsPanel;
