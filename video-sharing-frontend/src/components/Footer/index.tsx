import React, { FC } from 'react';

import './style.css';

const Footer: FC = () => {
  return (
    <div className="footer">
      <div style={{ float: 'right' }}>
        <button type="button">About</button>
        <button type="button">Help</button>
      </div>
    </div>
  );
};

export default Footer;
