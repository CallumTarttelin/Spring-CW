import React from 'react';
import ReactDOM from 'react-dom';
import Items from './Items';
import {Statuses} from "../store/types";

it('renders without crashing', () => {
    const div = document.createElement('div');
    ReactDOM.render(<Items status={Statuses.Wanted} />, div);
    ReactDOM.unmountComponentAtNode(div);
});
