/* eslint-disable jsx-a11y/label-has-associated-control */
import React, { FC, useState } from 'react';

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
          <label>Email:</label>
          <input onChange={handleChange} type="text" name="email" />
        </div>
        <div className="password-login">
          <label>Password:</label>
          <input onChange={handleChange} type="text" name="password" />
        </div>
        <input type="submit" value="Submit" />
      </form>
    </div>
  );
};

export default LoginForm;
