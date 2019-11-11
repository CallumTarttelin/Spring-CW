import {ItemActionTypes, ItemState} from "./types";

const initialState: ItemState = {
    items: []
};

export default function itemReducer(itemState: ItemState = initialState, action: ItemActionTypes): ItemState {
    switch (action.type) {
        case "SET_ITEMS":
            return {items: action.payload};
        case "ADD_ITEM":
            return {items: [...itemState.items, action.payload]};
        case "REMOVE_ITEM_BY_ID":
            return {items: itemState.items.filter(item => item.id !== action.payload)};
        default:
            return itemState
    }
}