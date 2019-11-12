import React from 'react';
import './ItemSummary.scss';
import {Item} from "../../store/types";
import {Link} from "react-router-dom";

interface ItemsProps {
    item: Item;
}

const ItemSummary: React.FunctionComponent<ItemsProps> = (props: ItemsProps) => {
    const {id, description, condition, status, categories, listUntilDate} = props.item;
    console.log(categories);
    return (
        <div className="ItemSummary">
            <p>{status} - {condition}</p>
            <Link to={`/item/${id}`}>
                {description}
            </Link>
            <p>{categories.join(", ")}</p>
            <p>{new Date(listUntilDate).toDateString()}</p>
        </div>
    );
};

export default ItemSummary;
