import React, {useEffect} from 'react';
import './Items.scss';
import {useDispatch, useSelector} from "react-redux";
import {RecyclingState} from "../store/reducers";
import {fetchItems} from "../store/item/actions";
import {Statuses} from "../store/types";
import {Link} from "react-router-dom";
import ItemList from "../itemList/ItemList";

interface ItemsProps {
    status: Statuses;
}

const Items: React.FunctionComponent<ItemsProps> = (props: ItemsProps) => {
    const dispatch = useDispatch();
    const items = useSelector((state: RecyclingState) => state.item[props.status]);

    useEffect(() => {
        dispatch(fetchItems(props.status))
    }, [props.status, dispatch]);

    return (
        <div className="Items">
            <br />
            <br />
            <ItemList items={items} />
            <br />
            <Link to={`/${props.status}/new`}>
                <button type="button">
                    new item
                </button>
            </Link>
        </div>
    );
};

export default Items;
