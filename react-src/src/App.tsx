import React from 'react';
import {BrowserRouter as Router, Route, Switch,} from "react-router-dom";
import './App.scss';
import Header from "./header/Header";
import Home from "./home/Home";
import Items, {Statuses} from "./items/Items";

const App: React.FC = () => {
  return (
    <div className="App">
      <Header />
      <Router>
          <Switch>
              <Route exact path="/" >
                  <Home />
              </Route>
              <Route path="/home" >
                  <Home />
              </Route>
              <Route path="/wanted">
                  <Items status={Statuses.Wanted}/>
              </Route>
              <Route path="/offered">
                  <Items status={Statuses.Offered} />
              </Route>
          </Switch>
      </Router>
    </div>
  );
};

export default App;
