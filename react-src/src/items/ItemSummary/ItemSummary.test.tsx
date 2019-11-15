import React from 'react';
import ReactDOM from 'react-dom';
import ItemSummary from './ItemSummary';
import {Statuses} from "../../store/types";

it('renders without crashing', () => {
    const div = document.createElement('div');
    ReactDOM.render(<ItemSummary item={{id: "id", description: "description", condition: "", listUntilDate: "", category: "", claimed: false, status: Statuses.Wanted, questions: [], user: {username: "", email: "", postcode: "", address: ""}}}/>, div);
    ReactDOM.unmountComponentAtNode(div);
});
