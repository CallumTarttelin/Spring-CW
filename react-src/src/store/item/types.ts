import {Item} from "../types";

export interface ItemState {
    wanted: Item[];
    offered: Item[];
}

export const SET_WANTED = 'SET_WANTED';
export const SET_OFFERED = 'SET_OFFERED';
export const UPDATE_ITEM = 'UPDATE_ITEM';

interface SetWantedAction {
    type: typeof SET_WANTED;
    payload: Item[];
}

interface SetOfferedAction {
    type: typeof SET_OFFERED;
    payload: Item[];
}

interface UpdateItemAction {
    type: typeof UPDATE_ITEM;
    payload: Item;
}

export type ItemActionTypes = SetWantedAction | SetOfferedAction | UpdateItemAction;