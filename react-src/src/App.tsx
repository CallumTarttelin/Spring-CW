import React from 'react';
import {
    BrowserRouter as Router,
    Switch,
    Route,
} from "react-router-dom";
import './App.css';
import Header from "./header/Header";
import Home from "./home/Home";

const App: React.FC = () => {
  return (
    <div className="App">
      <Header />
      <Router>
          <Switch>
              <Route path="/">
                  <Home />
              </Route>
          </Switch>
      </Router>
    </div>
  );
};

export default App;
