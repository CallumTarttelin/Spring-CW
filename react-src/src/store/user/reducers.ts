import {UserActionTypes, UserState, SET_USER, ADD_OTHER_USER} from "./types";

const initialState: UserState = {
    user: undefined,
    others: [],
    loadedUser: false,
};

export default function userReducer(itemState: UserState = initialState, action: UserActionTypes): UserState {
    switch (action.type) {
        case SET_USER:
            return {...itemState, user: action.payload, loadedUser: true};
        case ADD_OTHER_USER:
            return {
                ...itemState,
                others: [...itemState.others.filter(user => user.username !== action.payload.username), action.payload],
            };
        default:
            return itemState
    }
}