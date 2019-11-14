import React, {useState} from 'react';
import axios, {AxiosResponse} from 'axios';
import { Redirect } from 'react-router-dom';
import './Signup.scss';
import {User} from "../store/types";
import useForm from 'react-hook-form';
import qs from 'querystringify';
import {fetchUser, setUser} from "../store/user/actions";
import {useDispatch, useSelector} from "react-redux";
import {RecyclingState} from "../store/reducers";

type UserFormData = {
    username: string,
    password: string,
    email: string,
    address: string,
    postcode: string,
}

interface SignupProps {
    updating?: boolean;
}

const Signup: React.FunctionComponent<SignupProps> = (props: SignupProps) => {
    const dispatch = useDispatch();
    const [isError, setIsError] = useState(false);
    const [redirect, setRedirect] = useState(false);
    const { register, handleSubmit, errors } = useForm<UserFormData>();

    const updating: boolean = props.updating !== undefined && props.updating;
    let loggedIn = useSelector((state: RecyclingState) => state.user.user);
    const user = (loggedIn === undefined || ! updating) ?
        {username: "username", email: "email", address: "address", postcode: "postcode"} : loggedIn;

    function stripEmpty(object: {[index: string]: any}): {[index: string]: any} {
        for (const key in object) {
            if (! object[key]) {
                delete object[key]
            }
        }
        return object
    }

    const onSubmit = (formData: UserFormData) => {
        if (updating) {
            axios.patch(
                `/api/user`, qs.stringify(stripEmpty(formData)),
                {headers: {'Content-type': 'application/x-www-form-urlencoded'}}
            )
                .then(_ => {
                    dispatch(fetchUser());
                    setRedirect(true)
                })
                .catch(() => setIsError(true))
        } else {
            axios.post(
                `/api/user`, qs.stringify(formData),
                {headers: {'Content-type': 'application/x-www-form-urlencoded'}}
            )
                .then((_: AxiosResponse) => {
                    const data = new FormData();
                    data.set("username", formData.username);
                    data.set("password", formData.password);
                    axios.post("/api/login", data)
                        .then((userAxiosResponse: AxiosResponse<User>) => {
                            dispatch(setUser(userAxiosResponse.data));
                        })
                        .catch(() => {
                            dispatch(setUser());
                        });
                    setRedirect(true);
                })
                .catch(() => setIsError(true))
        }
    };


    return (
        <div className="AddItem">
            <form onSubmit={handleSubmit(onSubmit)}>
                <input name="username" ref={register({ required: ! updating, maxLength: 120 })} placeholder={user.username} />
                {errors.username && 'Username is required, max length 120'}
                <input type="email" name="email" ref={register({ required: ! updating})} placeholder={user.email} />
                {errors.email && 'Email is required'}
                <input  name="address" ref={register({ required: ! updating })} placeholder={user.address} />
                {errors.address && 'Address is required'}
                <input name="postcode" ref={register({required: ! updating})} placeholder={user.postcode} />
                {errors.postcode && 'Postcode is required'}
                <input type="password" name="password" ref={register({required: ! updating})} placeholder="password"/>
                {errors.password && 'list until date is required'}
                <input type="submit" />
                {isError && <p>Error signing up, please check input and try again</p>}
            </form>
            {redirect && <Redirect to={`/`} />}
        </div>
    );
};

export default Signup;
