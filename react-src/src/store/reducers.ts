import {combineReducers} from "redux";
import itemReducer from "./items/reducers";

const rootReducer = combineReducers({
    item: itemReducer,
});

export type RecyclingState = ReturnType<typeof rootReducer>