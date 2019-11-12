import {UserActionTypes, SET_USER} from "./types";
import {User} from "../types";

export function setUser(user?: User): UserActionTypes {
    return {
        type: SET_USER,
        payload: user
    }
}
