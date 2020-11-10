import React, { FC } from 'react';

import ContainerModal from '../ContainerModal';

import './style.css';

const TopBar: FC = () => {
  return (
    <div className="top-bar">
      <ContainerModal />
      <div className="info-panel">
        <div className="info-panel-center">
          <button type="button">Tutorial</button>
          <button type="button">Assembly</button>
          <button type="button">Editing</button>
          <button type="button">Color</button>
          <button type="button">Effects</button>
          <button type="button">Audio</button>
          <button type="button">Graphics</button>
        </div>
      </div>
    </div>
  );
};

export default TopBar;
