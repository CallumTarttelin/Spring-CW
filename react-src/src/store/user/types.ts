import {User} from "../types";

export interface UserState {
    user?: User;
    others: User[];
}

export const SET_USER = 'SET_USER';

interface SetWantedAction {
    type: typeof SET_USER;
    payload?: User;
}

export type UserActionTypes = SetWantedAction;