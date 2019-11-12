import React, {useEffect, useState} from 'react';
import './Items.scss';
import {useDispatch, useSelector} from "react-redux";
import {RecyclingState} from "../store/reducers";
import axios, {AxiosError, AxiosResponse} from 'axios';
import {setOfferedItems, setWantedItems} from "../store/item/actions";
import {Item, Statuses} from "../store/types";
import ItemSummary from "./ItemSummary/ItemSummary";
import {Link} from "react-router-dom";

interface ItemsProps {
    status: Statuses;
}

const Items: React.FunctionComponent<ItemsProps> = (props: ItemsProps) => {
    const status = props.status.valueOf();
    const dispatch = useDispatch();
    const items = useSelector((state: RecyclingState) => state.item[props.status]);
    const [isError, setIsError] = useState(false);

    useEffect(() => {
        axios.get<Item[]>(`/api/${status}`)
            .then((items: AxiosResponse<Item[]>) => {
                if (status === Statuses.Offered.valueOf()) {
                    dispatch(setOfferedItems(items.data));
                } else if (status === Statuses.Wanted.valueOf()) {
                    dispatch(setWantedItems(items.data));
                } else {
                    console.log(status, Statuses);
                }
            })
            .catch((err: AxiosError) => {
                console.error(err);
                setIsError(true);
            });
    }, [items, status, dispatch]);

    return (
        <div className="Items">
            <Link to="/home">
                back
            </Link>
            {isError && <h3>Something has gone wrong retrieving items, please try again later.</h3>}
            {items.map(item => <ItemSummary item={item} key={item.id}/>)}
        </div>
    );
};

export default Items;
