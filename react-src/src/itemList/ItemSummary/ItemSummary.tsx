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
        <Link to={`/item/${id}`} className="ItemSummary-link">
            <div className="ItemSummary-inner">
                <div className="ItemSummary-Header">
                    <span className="ItemSummary-left">{status} - {condition}</span>
                    <span className="ItemSummary-right">{new Date(listUntilDate).toDateString()}</span>
                </div>
                <p>{category}</p>
                <p>{description}</p>
            </div>
        </Link>
        </div>
    );
};

export default ItemSummary;
