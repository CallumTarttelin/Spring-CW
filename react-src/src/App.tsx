import React from 'react';
import {BrowserRouter as Router, Route, Switch, Redirect, useLocation} from "react-router-dom";
import './App.scss';
import Header from "./header/Header";
import Home from "./home/Home";
import Items from "./items/Items";
import ItemView from "./item/ItemView";
import {Statuses} from "./store/types";
import AddItem from "./addItem/AddItem";
import {useSelector} from "react-redux";
import {RecyclingState} from "./store/reducers";
import Login from "./login/Login";
import Signup from "./signup/Signup";
import Profile from "./profile/Profile";
import ChangeItem from "./changeItem/ChangeItem";

function PrivateRoute({ children, ...rest }: any) {
    const location = useLocation();
    const authenticated = useSelector((state: RecyclingState) => state.user.user !== undefined);
    const loadedUser = useSelector((state: RecyclingState) => state.user.loadedUser);

    return (
        <Route
            {...rest}
            render={() =>
                loadedUser ? ( authenticated ? (
                    children
                ) : (
                    <Redirect
                        to={{
                            pathname: "/login",
                            state: { from: location }
                        }}
                    />
                )) : <></>
            }
        />
    );
}

const App: React.FC = () => {
    return (
        <div className="App">
            <Router>
                <Header />
                <Switch>
                    <Route exact path="/" >
                        <Home />
                    </Route>
                    <Route path="/home" >
                        <Home />
                    </Route>
                    <Route path="/login" >
                        <Login />
                    </Route>
                    <Route path="/signup" >
                        <Signup />
                    </Route>
                    <Route exact path="/wanted">
                        <Items status={Statuses.Wanted}/>
                    </Route>
                    <Route exact path="/offered">
                        <Items status={Statuses.Offered} />
                    </Route>
                    <Route path="/item/:id">
                        <ItemView />
                    </Route>
                    <Route path="/profile/:id">
                        <Profile />
                    </Route>
                    <PrivateRoute path="/wanted/new">
                        <AddItem status={Statuses.Wanted} />
                    </PrivateRoute>
                    <PrivateRoute path="/offered/new">
                        <AddItem status={Statuses.Offered} />
                    </PrivateRoute>
                    <PrivateRoute path="/edit-item/:id">
                        <ChangeItem />
                    </PrivateRoute>
                    <PrivateRoute path="/edit-profile">
                        <Signup updating={true}/>
                    </PrivateRoute>
                    <Route path="/" >
                        <div className="NOTFOUND">
                            <h1>Page not found</h1>
                        </div>
                    </Route>
                </Switch>
            </Router>
        </div>
    );
};

export default App;
