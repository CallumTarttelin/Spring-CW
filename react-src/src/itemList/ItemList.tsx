import React from 'react';
import './ItemList.scss';
import {Item} from "../store/types";
import ItemSummary from "./ItemSummary/ItemSummary";

interface ItemsProps {
    items: Item[];
}

const ItemList: React.FunctionComponent<ItemsProps> = (props: ItemsProps) => {
    return (
        <div className="ItemList">
            {props.items.map(item => <ItemSummary item={item} key={item.id}/>)}
        </div>
    );
};

export default ItemList;
