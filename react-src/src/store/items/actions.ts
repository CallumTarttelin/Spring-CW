import {ItemActionTypes, SET_OFFERED, SET_WANTED} from "./types";
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
