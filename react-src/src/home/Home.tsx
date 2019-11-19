import React, {useEffect} from 'react';
import './Home.scss';
import {Link} from "react-router-dom";
import {useDispatch, useSelector} from "react-redux";
import {RecyclingState} from "../store/reducers";
import {fetchItems} from "../store/item/actions";
import {Statuses} from "../store/types";
import ItemList from "../itemList/ItemList";

const Home: React.FC = () => {
    const dispatch = useDispatch();
    const wantedItems = useSelector((state: RecyclingState) => state.item.wanted.slice(0, 3));
    const offeredItems = useSelector((state: RecyclingState) => state.item.offered.slice(0, 3));

    useEffect(() => {
        dispatch(fetchItems(Statuses.Wanted));
        dispatch(fetchItems(Statuses.Offered));
    }, [dispatch]);

    return (
        <div className="Home">
            <header className="Home-header">
                <div className="Home-Items">
                    <div className="Home-Offered">
                        <Link to="/offered">
                            View offered items
                        </Link>
                        <ItemList items={offeredItems}/>
                    </div>
                    <div className="Home-Wanted">
                        <Link to="/wanted">
                            View wanted items
                        </Link>
                        <ItemList items={wantedItems}/>
                    </div>
                </div>
            </header>
        </div>
    );
};

export default Home;
