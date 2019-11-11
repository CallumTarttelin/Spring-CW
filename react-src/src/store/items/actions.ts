import {ADD_ITEM, ItemActionTypes, REMOVE_ITEM_BY_ID, SET_ITEMS} from "./types";
import {Item} from "../types";

export function setItems(items: Item[]): ItemActionTypes {
    return {
        type: SET_ITEMS,
        payload: items
    }
}

export function addItem(items: Item): ItemActionTypes {
    return {
        type: ADD_ITEM,
        payload: items
    }
}

export function removeItemById(id: string): ItemActionTypes {
    return {
        type: REMOVE_ITEM_BY_ID,
        payload: id
    }
}