import React, { FC } from 'react';

import ToolsPanel from './ToolsPanel';
import Timeline from './Timeline';

import './style.css';

const EditPanel: FC = () => {
  return (
    <div className="edit-panel">
      <ToolsPanel />
      <Timeline />
    </div>
  );
};

export default EditPanel;
