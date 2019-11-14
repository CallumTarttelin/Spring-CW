import React, {useEffect, useState} from 'react';
import './ItemView.scss';
import axios, {AxiosError, AxiosResponse} from 'axios';
import { useParams } from "react-router-dom";
import {useDispatch, useSelector} from "react-redux";
import { RecyclingState } from "../store/reducers";
import {Item} from "../store/types";
import {updateItem} from "../store/item/actions";
import Ask from "./ask/Ask";
import Questions from "./questions/Questions";

const ItemView: React.FC = () => {
    const { id } = useParams();
    const dispatch = useDispatch();
    const item = useSelector((state: RecyclingState) => (
        state.item.wanted.concat(state.item.offered).find(item => item.id === id)
    ));
    const [isError, setIsError] = useState(false);

    useEffect(() => {
        axios.get(`/api/item/${id}`)
            .then((item: AxiosResponse<Item>) => {
                dispatch(updateItem(item.data))
            })
            .catch((err: AxiosError) => {
                console.error(err);
                setIsError(true);
            });
    }, [dispatch, id]);
    return (
        <div className="Item">
            {isError && <h3>Something has gone wrong retrieving items, please try again later.</h3>}
            {item !== undefined && <p>{item.description}</p>}
            {item === undefined && <p>loading</p>}
            {item !== undefined && <Questions item={item}/>}
            {item !== undefined && <Ask item={item}/>}
        </div>
    );
};

export default ItemView;
