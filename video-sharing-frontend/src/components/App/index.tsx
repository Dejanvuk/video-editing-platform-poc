import React, { FC, useEffect } from 'react';

import TopBar from '../TopBar';

import ProgramMonitor from '../ProgramMonitor';
import SourceMonitor from '../SourceMonitor';
import MediaBrowser from '../MediaBrowser';
import EditPanel from '../EditPanel';
import Footer from '../Footer';

import './style.css';

const App: FC = () => {
  useEffect(() => {
    const windowUrl = window.location.search;
    const params = new URLSearchParams(windowUrl);
    console.log(params.get('jwt'));
  }, []);

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
