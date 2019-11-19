import axios, {AxiosError, AxiosResponse} from 'axios';
import {ITEM_FOUND, ITEM_NOT_FOUND, ItemActionTypes, SET_OFFERED, SET_WANTED, UPDATE_ITEM} from "./types";
import {Item, Statuses} from "../types";
import {ThunkAction} from "redux-thunk";
import {RecyclingState} from "../reducers";

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
        payload: { ...item }
    }
}

export function itemNotFound(item: string): ItemActionTypes {
    return {
        type: ITEM_NOT_FOUND,
        payload: item
    }
}

export function itemFound(item: string): ItemActionTypes {
    return {
        type: ITEM_FOUND,
        payload: item
    }
}

export function fetchItems(status: Statuses): ThunkAction<void, RecyclingState, null, ItemActionTypes> {
    return dispatch => {
        return axios.get(`/api/${status.valueOf()}`)
            .then((items: AxiosResponse<Item[]>) => {
                if (status === Statuses.Offered) {
                    dispatch(setOfferedItems(items.data));
                } else if (status === Statuses.Wanted) {
                    dispatch(setWantedItems(items.data));
                }
            })
    }
}

export function fetchItem(id: string): ThunkAction<void, RecyclingState, null, ItemActionTypes> {
    return dispatch => {
        itemFound(id);
        return axios.get(`/api/item/${id}`)
            .then((itemData: AxiosResponse<Item>) => {
                dispatch(updateItem(itemData.data));
            })
            .catch((err: AxiosError) => {
                console.log(err.response !== undefined ? err.response.status : err.response);
                if (err.response !== undefined && err.response.status === 404) {
                    dispatch(itemNotFound(id));
                } else {
                    console.error(err);
                }
            })
    }
}
