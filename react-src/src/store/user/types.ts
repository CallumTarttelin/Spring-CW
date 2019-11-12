import {User} from "../types";

export interface UserState {
    user?: User;
    others: User[];
    loadedUser: boolean;
}

export const SET_USER = 'SET_USER';

interface SetUser {
    type: typeof SET_USER;
    payload?: User;
}

export type UserActionTypes = SetUser;