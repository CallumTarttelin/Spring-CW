import {ItemActionTypes, ItemState, SET_OFFERED, SET_WANTED, UPDATE_ITEM} from "./types";

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
        case UPDATE_ITEM:
            return {...itemState, [action.payload.status]: itemState[action.payload.status]
                    .filter(item => item.id !== action.payload.id).concat([action.payload])};
        default:
            return itemState
    }
}