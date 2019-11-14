import React, {useEffect, useState} from 'react';
import './Profile.scss';
import axios, {AxiosError, AxiosResponse} from 'axios';
import {User} from "../store/types";
import {useDispatch, useSelector} from "react-redux";
import {addOtherUser} from "../store/user/actions";
import {Link, useParams} from 'react-router-dom';
import {RecyclingState} from "../store/reducers";

const Profile: React.FC = () => {
    let { id } = useParams();
    const username: string = id !== undefined ? id : "";
    const dispatch = useDispatch();
    const user = useSelector((state: RecyclingState) => state.user.others.find(user => user.username.toLowerCase() === username.toLowerCase()));
    const [isError, setIsError] = useState(false);


    useEffect(() => {
        axios.get(`/api/user/${username}`)
            .then((item: AxiosResponse<User>) => {
                dispatch(addOtherUser(item.data));
            })
            .catch((err: AxiosError) => {
                console.error(err);
                setIsError(true);
            });
    }, [dispatch, username]);

    return (
        <div className="Profile">
            {isError && <h3>Something has gone wrong retrieving user, please try again later.</h3>}
            {user !== undefined && <p>{user.username}</p>}
            {user === undefined && <p>loading</p>}
            <Link to="/">back</Link>
        </div>
    );
};

export default Profile;
