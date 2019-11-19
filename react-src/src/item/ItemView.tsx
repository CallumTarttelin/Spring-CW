import React, {useEffect} from 'react';
import './ItemView.scss';
import {Link, useParams} from "react-router-dom";
import {useDispatch, useSelector} from "react-redux";
import { RecyclingState } from "../store/reducers";
import {fetchItem} from "../store/item/actions";
import Ask from "./ask/Ask";
import Questions from "./questions/Questions";

const ItemView: React.FC = () => {
    const { id } = useParams();
    const dispatch = useDispatch();
    const item = useSelector((state: RecyclingState) => (
        state.item.wanted.concat(state.item.offered).find(item => item.id === id)
    ));
    const isError = useSelector((state: RecyclingState) =>
        state.item.errored.find(itemId => itemId === id) !== undefined) || id === undefined;

    useEffect(() => {
        if (id !== undefined) {
            dispatch(fetchItem(id));
        }
    }, [dispatch, id]);

    return (
        <div className="Item">
            {isError && <h3>Something has gone wrong retrieving items, please try again later.</h3>}
            {item === undefined && <p>loading</p>}
            {item !== undefined && (
                <>
                    {item.claimed && <h3>CLAIMED</h3>}
                    <Link to={`/profile/${item.user.username}`}>{item.user.username}</Link>
                    <p>{item.status} - {item.condition}</p>
                    <p>{item.description}</p>

                    <Questions item={item}/>
                    <Ask item={item}/>
                    <br />
                    <br />
                    <Link to={`/edit-item/${item.id}`}><button>Modify Item</button></Link>
                </>
            )}
        </div>
    );
};

export default ItemView;
