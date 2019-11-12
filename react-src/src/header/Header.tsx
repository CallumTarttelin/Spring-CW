import React, {useEffect} from 'react';
import './Header.scss';
import axios, {AxiosResponse} from 'axios';
import {useDispatch, useSelector} from "react-redux";
import {RecyclingState} from "../store/reducers";
import {User} from "../store/types";
import {setUser} from "../store/user/actions";
import {Link} from "react-router-dom";

const Header: React.FC = () => {
    const dispatch = useDispatch();
    const user = useSelector((state: RecyclingState) => state.user.user);

    useEffect(() => {
        if (user === undefined) {
            axios.get("/api/user", {withCredentials: true})
                .then((userAxiosResponse: AxiosResponse<User>) => {
                    dispatch(setUser(userAxiosResponse.data));
                })
                .catch(() => {
                    dispatch(setUser());
                })
        }
    });

    return (
        <div className="Header">
            <header className="Header-title">
                <h1> THIS IS AN APPLICATION</h1>
                {user !== undefined ? <p>{user.username}</p> : <Link to="/login">login</Link>}
            </header>
        </div>
    );
};

export default Header;
