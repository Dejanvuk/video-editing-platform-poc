import React, { FC, useState, useRef, ReactElement } from 'react';

import { MODALS } from '../common/enums/modals';

import { REDIRECT_URI } from '../common/util/constants/oauth2';

// import { useKeyPress } from '../common/hooks';

import Modal from '../Modal';

import './style.css';

/**
 * The component which will engulf the whole screen once the modal shows up
 */
const ContainerModal: FC = () => {
  const [modal, setModal] = useState(MODALS.none);

  const modalRef = useRef<HTMLDivElement>(null);

  // const escPressed = useKeyPress('Escape');

  const showModal = (type: MODALS): void => {
    setModal(type);
    // disable scroll lock
    document.querySelector('html')?.classList.toggle('scroll-lock');
  };

  const closeModal = (): void => {
    setModal(MODALS.none);
    // re-enable scroll lock
    document.querySelector('html')?.classList.toggle('scroll-lock');
  };

  const onKeyDown = (event: React.KeyboardEvent<HTMLElement>): void => {
    if (event.key === 'Escape') {
      closeModal();
    }
  };

  const onClickModal = (event: React.MouseEvent<HTMLElement>): void => {
    if (
      modalRef.current &&
      !modalRef.current.contains(event.target as HTMLDivElement)
    ) {
      closeModal();
    }
  };

  const getSettings = (): ReactElement | null => {
    if (localStorage.getItem('jwt')) {
      // show the Settings button
      return (
        <button type="button" onClick={() => showModal(MODALS.settings)}>
          Settings
        </button>
      );
    }
    return null;
  };

  const getUserStatus = (): boolean => {
    return localStorage.getItem('jwt') !== null;
  };

  const logout = (): void => {
    // clear everything from local storage
    localStorage.clear();
    // redirect the user to home page
    window.location.href = `${REDIRECT_URI}`;
  };

  return (
    <>
      <div className="menu-bar">
        <span className="bar-options">
          <div className="dropdown">
            <button type="button" className="dropbtn">
              File
            </button>
            <div className="dropdown-content">
              <button
                type="button"
                className="dropdown-item"
                onClick={() => showModal(MODALS.new)}
              >
                New
              </button>
              <button
                type="button"
                className="dropdown-item"
                onClick={() => showModal(MODALS.import)}
              >
                Import
              </button>
              <button type="button" className="dropdown-item">
                FILE2
              </button>
            </div>
          </div>
          <button type="button">Edit</button>
          <button type="button">Clip</button>
          <button type="button">Sequence</button>
          <button type="button">Markers</button>
          <button type="button">Graphics</button>
          <button type="button">View</button>
          <button type="button">Window</button>
          <button type="button">More</button>
        </span>
        <span className="bar-about">
          {getSettings()}
          {getUserStatus() ? (
            <button type="button" onClick={() => logout()}>
              Logout
            </button>
          ) : (
            <button type="button" onClick={() => showModal(MODALS.login)}>
              Login
            </button>
          )}
        </span>
      </div>

      {modal !== MODALS.none ? (
        <Modal
          type={modal}
          modalRef={modalRef}
          onClickModal={onClickModal}
          onKeyDown={onKeyDown}
          closeModal={closeModal}
        />
      ) : null}
    </>
  );
};

export default ContainerModal;
