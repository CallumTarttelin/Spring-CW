import React from 'react';
import './ItemSummary.scss';
import {Item} from "../../store/types";
import {Link} from "react-router-dom";

interface ItemsProps {
    item: Item;
}

const ItemSummary: React.FunctionComponent<ItemsProps> = (props: ItemsProps) => {
    const {id, description, condition, status, category, listUntilDate} = props.item;
    return (
        <div className="ItemSummary">
            <p>{status} - {condition}</p>
            <Link to={`/item/${id}`}>
                {description}
            </Link>
            <p>{category}</p>
            <p>{new Date(listUntilDate).toDateString()}</p>
        </div>
    );
};

export default ItemSummary;
