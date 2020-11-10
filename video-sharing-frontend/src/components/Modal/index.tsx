/* eslint-disable jsx-a11y/no-noninteractive-element-interactions */
import React, { FC } from 'react';
import ReactDOM from 'react-dom';

import FocusTrap from 'focus-trap-react';

import Login from '../Login';

import './style.css';

interface IProps {
  modalRef: React.MutableRefObject<HTMLDivElement | null>;
  onKeyDown: (event: React.KeyboardEvent<HTMLElement>) => void;
  closeModal: () => void;
  onClickModal: (event: React.MouseEvent<HTMLElement>) => void;
}

const Modal: FC<IProps> = ({
  modalRef,
  onClickModal,
  onKeyDown,
  closeModal,
}) => {
  return ReactDOM.createPortal(
    <FocusTrap>
      <aside
        role="dialog"
        tabIndex={-1}
        aria-modal="true"
        className="modal-cover"
        onClick={onClickModal}
        onKeyDown={onKeyDown}
      >
        <div className="modal-area" ref={modalRef}>
          <button
            style={{ float: 'right', display: 'block' }}
            onClick={closeModal}
            type="button"
          >
            Cancel
          </button>
          <Login />
        </div>
      </aside>
    </FocusTrap>,
    document.body,
  );
};

export default Modal;
