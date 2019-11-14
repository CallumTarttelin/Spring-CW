import axios, {AxiosResponse} from 'axios';
import {ADD_OTHER_USER, SET_USER, UserActionTypes} from "./types";
import {User} from "../types";
import {RecyclingState} from "../reducers";
import {ThunkAction} from 'redux-thunk';

export function setUser(user?: User): UserActionTypes {
    return {
        type: SET_USER,
        payload: user
    }
}

export function addOtherUser(user: User): UserActionTypes {
    return {
        type: ADD_OTHER_USER,
        payload: user
    }
}

export function fetchUser(): ThunkAction<void, RecyclingState, null, UserActionTypes> {
    return dispatch => {
        return axios.get("/api/user")
            .then((user: AxiosResponse<User>) => dispatch(setUser(user.data)))
            .catch(() => dispatch(setUser()));
    }
}

export function fetchUserIfNeeded(): ThunkAction<void, RecyclingState, null, UserActionTypes> {
    return (dispatch, getState) => {
        if (getState().user.user === undefined) {
            return dispatch(fetchUser())
        }
    }
}
