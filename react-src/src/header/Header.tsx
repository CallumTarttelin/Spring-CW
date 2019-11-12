import React, {useEffect} from 'react';
import './Header.scss';
import {useDispatch, useSelector} from "react-redux";
import {RecyclingState} from "../store/reducers";
import {Link} from "react-router-dom";
import {fetchUserIfNeeded} from "../store/user/actions";

const Header: React.FC = () => {
    const dispatch = useDispatch();
    const user = useSelector((state: RecyclingState) => state.user.user);

    useEffect(() => {
        dispatch(fetchUserIfNeeded())
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
