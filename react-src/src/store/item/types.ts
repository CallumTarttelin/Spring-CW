import {Item} from "../types";

export interface ItemState {
    wanted: Item[];
    offered: Item[];
    errored: string[];
}

export const SET_WANTED = 'SET_WANTED';
export const SET_OFFERED = 'SET_OFFERED';
export const UPDATE_ITEM = 'UPDATE_ITEM';
export const ITEM_NOT_FOUND = 'ITEM_NOT_FOUND';
export const ITEM_FOUND = 'ITEM_FOUND';

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

interface ItemNotFound {
    type: typeof ITEM_NOT_FOUND;
    payload: string;
}

interface ItemFound {
    type: typeof ITEM_FOUND;
    payload: string;
}

export type ItemActionTypes = SetWantedAction | SetOfferedAction | UpdateItemAction | ItemNotFound | ItemFound;