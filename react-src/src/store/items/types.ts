import {Item} from "../types";

export interface ItemState {
    items: Item[];
}

export const SET_ITEMS = 'SET_ITEMS';
export const ADD_ITEM = 'ADD_ITEM';
export const REMOVE_ITEM_BY_ID = 'REMOVE_ITEM_BY_ID';

interface SetItemsAction {
    type: typeof SET_ITEMS;
    payload: Item[];
}

interface AddItemAction {
    type: typeof ADD_ITEM;
    payload: Item;
}

interface RemoveItemByIdAction {
    type: typeof REMOVE_ITEM_BY_ID;
    payload: string;
}

export type ItemActionTypes = SetItemsAction | AddItemAction | RemoveItemByIdAction;