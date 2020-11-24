import React, { FC } from 'react';

import TopBar from '../TopBar';

import ProgramMonitor from '../ProgramMonitor';
import SourceMonitor from '../SourceMonitor';
import MediaBrowser from '../MediaBrowser';
import EditPanel from '../EditPanel';
import Footer from '../Footer';

import { useOauth2 } from '../common/hooks/Oauth2Hook';

import './style.css';

const App: FC = () => {
  useOauth2();

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
