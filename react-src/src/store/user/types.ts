import {User} from "../types";

export interface UserState {
    user?: User;
    others: User[];
    loadedUser: boolean;
}

export const SET_USER = 'SET_USER';
export const ADD_OTHER_USER = 'ADD_OTHER_USER';

interface SetUser {
    type: typeof SET_USER;
    payload?: User;
}

interface AddOtherUser {
    type: typeof ADD_OTHER_USER;
    payload: User;
}

export type UserActionTypes = SetUser | AddOtherUser;