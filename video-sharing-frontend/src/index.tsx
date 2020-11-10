import 'core-js/stable';
import 'regenerator-runtime/runtime';

import React from 'react';
import ReactDOM from 'react-dom';
import './style.css';

// import { Provider } from 'react-redux';
// import { PersistGate } from 'redux-persist/integration/react';

// import { BrowserRouter as Router } from 'react-router-dom';

import { hot } from 'react-hot-loader/root';
import App from './components/App';

ReactDOM.render(<App />, document.getElementById('root'));

export default hot(App);
