import React from 'react';
import ReactDOM from 'react-dom';
import ItemSummary from './ItemSummary';

it('renders without crashing', () => {
    const div = document.createElement('div');
    ReactDOM.render(<ItemSummary item={{id: "id", description: "description"}} />, div);
    ReactDOM.unmountComponentAtNode(div);
});
