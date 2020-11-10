import React, { FC } from 'react';

import TopBar from '../TopBar';

import ProgramMonitor from '../ProgramMonitor';
import SourceMonitor from '../SourceMonitor';
import MediaBrowser from '../MediaBrowser';
import EditPanel from '../EditPanel';
import Footer from '../Footer';

import './style.css';

const App: FC = () => {
  return (
    <div id="main-app">
      <TopBar />
      <SourceMonitor />
      <ProgramMonitor />
      <MediaBrowser />
      <EditPanel />
      <Footer />
    </div>
  );
};

export default App;
