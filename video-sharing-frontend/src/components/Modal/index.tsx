/* eslint-disable jsx-a11y/no-noninteractive-element-interactions */
import React, { ReactElement, FC } from 'react';
import ReactDOM from 'react-dom';

import FocusTrap from 'focus-trap-react';

import Login from '../Login';
import Signup from '../Signup';

import { MODALS } from '../common/enums/modals';

import './style.css';

interface IProps {
  type: number;
  modalRef: React.MutableRefObject<HTMLDivElement | null>;
  onKeyDown: (event: React.KeyboardEvent<HTMLElement>) => void;
  closeModal: () => void;
  onClickModal: (event: React.MouseEvent<HTMLElement>) => void;
}

const getModalType = (type: number): ReactElement | null => {
  switch (type) {
    case MODALS.new:
      return <div>NEW MENU</div>;
      break;
    case MODALS.import:
      return <div>IMPORT MENU</div>;
      break;
    case MODALS.login:
      return <Login />;
      break;
    case MODALS.signup:
      return <Signup />;
      break;
    default:
      return null;
  }
};

const Modal: FC<IProps> = ({
  type,
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
          <button id="cancel-btn" onClick={closeModal} type="button">
            Cancel
          </button>
          {getModalType(type)}
        </div>
      </aside>
    </FocusTrap>,
    document.body,
  );
};

export default Modal;
