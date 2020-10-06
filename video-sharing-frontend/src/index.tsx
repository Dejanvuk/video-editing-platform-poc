import "core-js/stable";
import "regenerator-runtime/runtime";

import React from 'react';
import ReactDOM from 'react-dom';
import './style.ts';
import App from './components/App';

import {Provider} from 'react-redux';
import {PersistGate} from 'redux-persist/integration/react';

import {BrowserRouter as Router} from 'react-router-dom';

import {hot} from 'react-hot-loader/root'

ReactDOM.render(
    <Router>
        <App/>
    </Router>,
    document.getElementById('root'));

export default hot(App);