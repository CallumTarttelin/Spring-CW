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
                    <Route exact path="/wanted">
                        <Items status={Statuses.Wanted}/>
                    </Route>
                    <Route exact path="/offered">
                        <Items status={Statuses.Offered} />
                    </Route>
                    <Route path="/item/:id">
                        <ItemView />
                    </Route>
                    <PrivateRoute path="/wanted/new">
                        <AddItem status={Statuses.Wanted} />
                    </PrivateRoute>
                    <PrivateRoute path="/offered/new">
                        <AddItem status={Statuses.Offered} />
                    </PrivateRoute>
                </Switch>
            </Router>
        </div>
    );
};

export default App;
