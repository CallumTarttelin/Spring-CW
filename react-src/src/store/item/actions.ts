import axios, {AxiosResponse} from 'axios';
import {ItemActionTypes, SET_OFFERED, SET_WANTED, UPDATE_ITEM} from "./types";
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
                } else {
                    console.log(status, Statuses);
                }
            })
    }
}
