import {ItemActionTypes, SET_OFFERED, SET_WANTED, UPDATE_ITEM} from "./types";
import {Item} from "../types";

export function setWantedItems(items: Item[]): ItemActionTypes {
    return {
        type: SET_WANTED,
        payload: items
    }
}

export function setOfferedItems(items: Item[]): ItemActionTypes {
    return {
        type: SET_OFFERED,
        payload: items
    }
}


export function updateItem(item: Item): ItemActionTypes {
    return {
        type: UPDATE_ITEM,
        payload: item
    }
}
