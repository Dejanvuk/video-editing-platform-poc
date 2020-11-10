import React, { FC, useState, useRef } from 'react';

// import { useKeyPress } from '../common/hooks';

import Modal from '../Modal';

import './style.css';

/**
 * The component which will engulf the whole screen once the modal shows up
 */
const ContainerModal: FC = () => {
  const [isModal, setIsModal] = useState(false);

  const modalRef = useRef<HTMLDivElement>(null);

  // const escPressed = useKeyPress('Escape');

  const showModal = (): void => {
    setIsModal(true);
    // disable scroll lock
    document.querySelector('html')?.classList.toggle('scroll-lock');
  };

  const closeModal = (): void => {
    setIsModal(false);
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

  return (
    <>
      <div className="menu-bar">
        <span className="bar-options">
          <div className="dropdown">
            <button type="button" className="dropbtn">
              File
            </button>
            <div className="dropdown-content">
              <button type="button" className="dropdown-item">
                New
              </button>
              <button type="button" className="dropdown-item">
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
          <button type="button" onClick={showModal}>
            Login
          </button>
        </span>
      </div>

      {isModal ? (
        <Modal
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
