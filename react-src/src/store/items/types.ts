import {Item} from "../types";

export interface ItemState {
    wanted: Item[];
    offered: Item[];
}

export const SET_WANTED = 'SET_WANTED';
export const SET_OFFERED = 'SET_OFFERED';

interface SetWantedAction {
    type: typeof SET_WANTED;
    payload: Item[];
}

interface SetOfferedAction {
    type: typeof SET_OFFERED;
    payload: Item[];
}

export type ItemActionTypes = SetWantedAction | SetOfferedAction;