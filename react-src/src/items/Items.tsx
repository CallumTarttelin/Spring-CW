import React, {useEffect} from 'react';
import './Items.scss';
import {useDispatch, useSelector} from "react-redux";
import {RecyclingState} from "../store/reducers";
import {fetchItems} from "../store/item/actions";
import {Statuses} from "../store/types";
import ItemSummary from "./ItemSummary/ItemSummary";
import {Link} from "react-router-dom";

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
            <Link to="/home">
                back
            </Link>
            {/*{isError && <h3>Something has gone wrong retrieving items, please try again later.</h3>}*/}
            {items.map(item => <ItemSummary item={item} key={item.id}/>)}
            <br />
            <Link to={`/${props.status}/new`}>
                new item
            </Link>
        </div>
    );
};

export default Items;
