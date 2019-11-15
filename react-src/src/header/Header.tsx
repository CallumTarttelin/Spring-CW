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
                <h1>Community Recycling</h1>
                {user !== undefined ? <Link to={`/profile/${user.username}`}>{user.username}</Link> : <Link to="/login">login</Link>}
            </header>
        </div>
    );
};

export default Header;
