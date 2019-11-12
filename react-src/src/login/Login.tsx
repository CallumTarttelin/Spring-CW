import React, {useState} from 'react';
import './Login.scss';
import useForm from 'react-hook-form';
import axios, {AxiosError, AxiosResponse} from 'axios';
import {User} from "../store/types";
import {useDispatch} from "react-redux";
import {setUser} from "../store/user/actions";

const Login: React.FC = () => {
    const dispatch = useDispatch();
    const { register, handleSubmit } = useForm();
    const [isError, setIsError] = useState(false);

    const onSubmit = (formData: Record<string, string>) => {
        const data = new FormData();
        data.set("username", formData.username);
        data.set("password", formData.password);
        axios.post("/api/login", data)
            .then((user: AxiosResponse<User>) => {
                console.log(user);
                dispatch(setUser(user.data))
            })
            .catch((err: AxiosError) => {
                console.error(err);
                setIsError(true)
            });
    };

    return (
        <div className="Login">
            <form className="Login-form" onSubmit={handleSubmit(onSubmit)}>
                <h2 className="form-signin-heading">Please sign in</h2>
                {isError && <p>Error logging in, please check your credentials and try again</p>}
                <p>
                    <label htmlFor="username">Username</label>
                    <input type="text" id="username" name="username" className="form-control" placeholder="Username"
                           autoFocus ref={register({required: true})}/>
                </p>
                <p>
                    <label htmlFor="password" >Password</label>
                    <input type="password" id="password" name="password" className="form-control" placeholder="Password"
                           ref={register({required: true})}/>
                </p>
                <button className="btn btn-lg btn-primary btn-block" type="submit">Sign in</button>
            </form>
        </div>
    );
};

export default Login;
