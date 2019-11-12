import {UserActionTypes, UserState, SET_USER} from "./types";

const initialState: UserState = {
    user: undefined,
    others: [],
};

export default function userReducer(itemState: UserState = initialState, action: UserActionTypes): UserState {
    switch (action.type) {
        case SET_USER:
            return {...itemState, user: action.payload};
        default:
            return itemState
    }
}