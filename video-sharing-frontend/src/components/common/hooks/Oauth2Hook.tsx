import { useEffect } from 'react';
import axios from 'axios';

import { API_BASE_URL, API_USER_URL } from '../util/constants/oauth2';

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
  } catch (e) {
    console.error(e.response.data);
  }
}

export function useOauth2(): void {
  // Called during initial client request of the page and after every oauth redirect from server
  useEffect(() => {
    const windowUrl = window.location.search;
    const params = new URLSearchParams(windowUrl);
    const jwt = params.get('jwt');
    if (jwt != null) {
      processUserDetails(jwt);
    }
  }, []);
}
