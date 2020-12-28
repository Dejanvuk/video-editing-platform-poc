import { useLayoutEffect } from 'react';
import axios from 'axios';

import {
  API_BASE_URL,
  API_USER_URL,
  REDIRECT_URI,
} from '../util/constants/oauth2';

type USVString = string;

interface IUser {
  name: string;
  email: string;
  imageUrl: string;
}

async function processUserDetails(jwt: USVString): Promise<void> {
  const url = `${API_BASE_URL + API_USER_URL}/me`;
  try {
    const { data: userDetails } = await axios.get<IUser>(url, {
      headers: { Authorization: jwt },
    });

    console.log(userDetails);
    if (userDetails.name !== null)
      await localStorage.setItem('name', userDetails.name);
    await localStorage.setItem('email', userDetails.email);
    await localStorage.setItem('imageUrl', userDetails.imageUrl);
    // redirect the user to home page
    window.location.href = `${REDIRECT_URI}`;
  } catch (e) {
    console.error(e.response.data);
  }
}

export function useOauth2(): void {
  // Called during initial client request of the page and after every oauth redirect from server
  useLayoutEffect(() => {
    const windowUrl = window.location.search;
    const params = new URLSearchParams(windowUrl);
    const jwt = params.get('jwt');
    if (jwt != null) {
      localStorage.setItem('jwt', jwt);
      processUserDetails(jwt);
    }
  }, []);
}
