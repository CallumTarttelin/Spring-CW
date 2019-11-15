import {ITEM_FOUND, ITEM_NOT_FOUND, ItemActionTypes, ItemState, SET_OFFERED, SET_WANTED, UPDATE_ITEM} from "./types";

const initialState: ItemState = {
    offered: [],
    wanted: [],
    errored: [],
};

export default function itemReducer(itemState: ItemState = initialState, action: ItemActionTypes): ItemState {
    switch (action.type) {
        case SET_OFFERED:
            return {...itemState, offered: action.payload};
        case SET_WANTED:
            return {...itemState, wanted: action.payload};
        case UPDATE_ITEM:
            return {
                ...itemState,
                [action.payload.status]: itemState[action.payload.status]
                    .filter(item => item.id !== action.payload.id).concat([action.payload]),
                errored: itemState.errored.filter(id => id !== action.payload.id)
            };
        case ITEM_NOT_FOUND:
            return {...itemState, errored: [...itemState.errored, action.payload]};
        case ITEM_FOUND:
            return {...itemState, errored: itemState.errored.filter(id => id !== action.payload)};
        default:
            return itemState
    }
}