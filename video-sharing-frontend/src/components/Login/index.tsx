/* eslint-disable jsx-a11y/label-has-associated-control */
import React, { FC, useState } from 'react';

import {
  AUTHORIZATION_LINK,
  REDIRECT_URI,
} from '../common/util/constants/oauth2';

import './style.css';

const LoginForm: FC = () => {
  type Error = {
    [key: string]: string;
  };

  type SignUpForm = {
    email: string;
    password: string;
    errors: Array<Error>;
    submittedSuccessfully?: boolean;
  };

  const [signUpForm, setSignUpForm] = useState<SignUpForm | null>(null);

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
      // signIn(signUpForm);
    }
  };

  const handleChange: React.ChangeEventHandler<HTMLInputElement> = e => {
    const { name, value } = e.target;
    setSignUpForm({ ...signUpForm, [name]: value } as SignUpForm);
  };

  return (
    <div className="login-form">
      <form onSubmit={handleSubmit}>
        <div className="email-login">
          <input
            placeholder="Email"
            className="login-inputs"
            onChange={handleChange}
            type="text"
            name="email"
          />
        </div>
        <div className="password-login">
          <input
            placeholder="Password"
            className="login-inputs"
            onChange={handleChange}
            type="text"
            name="password"
          />
        </div>
        <input id="login-submit-btn" type="submit" value="Submit" />

        <hr className="or-seperator" />
        <p className="hint-text">
          Sign up with your social media account or email address
        </p>

        <div className="btn-group">
          <button
            type="button"
            id="btnFacebook"
            className="btnOauth2"
            onClick={() => {
              window.location.href = `${AUTHORIZATION_LINK}facebook?redirect_uri=${REDIRECT_URI}`;
            }}
          >
            Facebook
          </button>
          <button
            type="button"
            id="btnGoogle"
            className="btnOauth2"
            onClick={() => {
              window.location.href = `${AUTHORIZATION_LINK}google?redirect_uri=${REDIRECT_URI}`;
            }}
          >
            Google
          </button>
          <button
            type="button"
            id="btnGithub"
            className="btnOauth2"
            onClick={() => {
              window.location.href = `${AUTHORIZATION_LINK}github?redirect_uri=${REDIRECT_URI}`;
            }}
          >
            Github
          </button>
        </div>
      </form>
    </div>
  );
};

export default LoginForm;
