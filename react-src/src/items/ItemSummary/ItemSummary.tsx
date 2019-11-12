import React from 'react';
import './ItemSummary.scss';
import {Item} from "../../store/types";
import {Link} from "react-router-dom";

interface ItemsProps {
    item: Item;
}

const ItemSummary: React.FunctionComponent<ItemsProps> = (props: ItemsProps) => {
    const {id, description} = props.item;
    return (
        <div className="ItemSummary">
            <Link to={`/item/${id}`}>
                {description}
            </Link>
        </div>
    );
};

export default ItemSummary;
