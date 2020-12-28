import React, { FC, useState } from 'react';

import './style.css';

const Settings: FC = () => {
  type UserDetails = {
    name: string | null;
    email: string | null;
    imageUrl: string | null;
    phoneNumber: string | null;
    password?: string;
  };

  const [userDetails, setUserDetails] = useState<UserDetails>({
    name: localStorage.getItem('name'),
    email: localStorage.getItem('email'),
    imageUrl: localStorage.getItem('imageUrl'),
    phoneNumber: localStorage.getItem('phoneNumber'),
  });

  /**
   * Handles form validation
   * @returns {boolean} - If the form is valid
   */
  const validateForm = (): boolean => {
    return true;
  };

  /**
   * Handles form submission
   * @param {React.FormEvent<HTMLFormElement>} e - The form event
   */
  const handleSubmit = async (
    e: React.FormEvent<HTMLFormElement>,
  ): Promise<void> => {
    e.preventDefault();

    if (validateForm()) {
      // saveUserDetails();
    }
  };

  const handleChange: React.ChangeEventHandler<HTMLInputElement> = e => {
    const { name, value } = e.target;
    setUserDetails({ ...userDetails, [name]: value } as UserDetails);
  };

  return (
    <div className="settings-form">
      <form onSubmit={handleSubmit}>
        <h1>MY ACCOUNT </h1>
        <a href="/change" className="">
          <img
            alt=""
            className="avi"
            src={
              userDetails?.imageUrl !== null
                ? userDetails?.imageUrl
                : 'http://www.google.com/image.jpeg'
            }
          />
        </a>
        <h2>{userDetails.name !== null ? userDetails.name : 'Name'}</h2>
        <div className="change-email">
          <h4 className="settings-h4">Email</h4>
          <input
            placeholder={
              userDetails.email !== null ? userDetails.email : 'email'
            }
            className="settings-inputs"
            onChange={handleChange}
            type="text"
            name="email"
          />
        </div>
        <div className="change-phone">
          <h4 className="settings-h4">Phone number:</h4>
          <input
            placeholder={
              userDetails.phoneNumber !== null
                ? userDetails.phoneNumber
                : "You haven't added a phone number yet"
            }
            className="settings-inputs"
            onChange={handleChange}
            type="text"
            name="email"
          />
        </div>

        <hr className="or-seperator" />

        <h1> PASSWORD AND AUTHENTICATION </h1>
      </form>
    </div>
  );
};

export default Settings;
