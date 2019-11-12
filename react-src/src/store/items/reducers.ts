import {ItemActionTypes, ItemState, SET_OFFERED, SET_WANTED} from "./types";

const initialState: ItemState = {
    offered: [],
    wanted: [],
};

export default function itemReducer(itemState: ItemState = initialState, action: ItemActionTypes): ItemState {
    switch (action.type) {
        case SET_OFFERED:
            return {...itemState, offered: action.payload};
        case SET_WANTED:
            return {...itemState, wanted: action.payload};
        default:
            return itemState
    }
}