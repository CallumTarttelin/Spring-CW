import React from 'react';
import logo from './logo.svg';
import './Home.scss';
import {Link} from "react-router-dom";

const Home: React.FC = () => {
    return (
        <div className="Home">
            <header className="Home-header">
                <img src={logo} className="Home-logo" alt="logo" />
                <Link to="/offered">
                    View offered items
                </Link>
                <Link to="/wanted">
                    View wanted items
                </Link>
            </header>
        </div>
    );
};

export default Home;
