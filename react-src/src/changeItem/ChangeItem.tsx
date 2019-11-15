import React, {useEffect, useState} from 'react';
import axios from 'axios';
import { Redirect, useParams } from 'react-router-dom';
import './ChangeItem.scss';
import {Item, Statuses} from "../store/types";
import useForm from 'react-hook-form';
import qs from 'querystringify';
import {useDispatch, useSelector} from "react-redux";
import {RecyclingState} from "../store/reducers";
import {formatDate, stripEmpty} from "../store/helpers";
import {fetchItem} from "../store/item/actions";

type ItemFormData = {
    condition: string,
    description: string,
    listUntilDate: string,
    category: string,
}

const ChangeItem: React.FC = () => {
    const { id } = useParams();
    const dispatch = useDispatch();
    const [redirect, setRedirect] = useState("");
    const { register, handleSubmit, errors } = useForm<ItemFormData>();
    const found = useSelector((state: RecyclingState) =>
        state.item.errored.find(itemId => itemId === id) === undefined
    );
    const foundItem: Item | undefined = useSelector((state: RecyclingState) =>
        state.item.wanted.concat(state.item.offered).find(item => item.id === id)
    );
    const item = (foundItem === undefined) ? undefined : {
        ...foundItem,
        listUntilDate: new Date(foundItem.listUntilDate).toISOString().substr(0, 10)

    };
    console.log(item);
    const username = useSelector((state: RecyclingState) =>
        state.user.user !== undefined ? state.user.user.username : undefined
    );
    console.log(item, username, found);
    const [isError, setIsError] = useState((item !== undefined && item.user.username !== username) || ! found);

    useEffect(() => {
        if (id !== undefined) {
            dispatch(fetchItem(id));
        }
    }, [dispatch, id]);

    const onSubmit = (formData: ItemFormData) => {
        formData.listUntilDate = formatDate(formData.listUntilDate);
        axios.patch(
            `/api/item/${id}`, qs.stringify(stripEmpty(formData)),
            {withCredentials: true, headers: {'Content-type': 'application/x-www-form-urlencoded'}}
        )
            .then(() => setRedirect(`/item/${id}`))
            .catch(() => setIsError(true))
    };

    const onDelete = () => {
        axios.delete(`/api/item/${id}`, {withCredentials: true})
            .then(() => setRedirect("/home"))
            .catch(() => setIsError(true))
    };

    const toggleClaim = (claimed: boolean) => () => {
        axios.post(`/api/item/${id}/${claimed ? "unclaim" : "claim"}`, {withCredentials: true})
            .then(() => setRedirect(`/item/${id}`))
            .catch(() => setIsError(true))
    };

    return (
        <div className="AddItem">
            {! isError && item !== undefined && <form onSubmit={handleSubmit(onSubmit)}>
                <label>
                    Condition:<br />
                    <select name="condition" ref={register({ required: true })} defaultValue={item.condition}>
                        <option value="new/unused">new/unused</option>
                        <option value="used">used</option>
                        <option value="damaged">damaged</option>
                        <option value="scrap">scrap</option>
                        {item.status === Statuses.Wanted && <option value="any">any</option>}
                    </select>
                    {errors.condition && 'Condition is required'}
                </label>
                <label>
                    Description:<br />
                    <input name="description" ref={register({ required: true, maxLength: 120 })} placeholder={item.description}/>
                    {errors.description && 'Description is required, max length 120'}
                </label>
                <label>
                    List Until:<br />
                    <input type="date" name="listUntilDate" ref={register({ required: true })} defaultValue={item.listUntilDate}/>
                    {errors.listUntilDate && 'list until date is required'}
                </label>
                <label>
                   Category:<br />
                   <select name="category" ref={register({required: true})} defaultValue={item.category}>
                       <option value="Household">Household</option>
                       <option value="Hobbies">Hobbies</option>
                       <option value="Toys">Toys</option>
                       <option value="Electronics">Electronics</option>
                       <option value="Garden">Garden</option>
                       <option value="Collectable">Collectable</option>
                       <option value="Other">Other</option>
                   </select>
                    {errors.category && 'Category is required'}
                </label>
                <input type="submit" value="Modify Item"/>
                <br />
                <button type="button" onClick={onDelete}>Delete Item</button>
                <br />
                {! item.claimed && <button type="button" onClick={toggleClaim(false)}>Claim Item</button>}
                {item.claimed && <button type="button" onClick={toggleClaim(true)}>Unclaim Item</button>}
                {redirect !== '' && <Redirect to={`/item/${item.id}`} />}
            </form>}
            {isError && <p>Error with request, please check input and try again</p>}
            {! isError && item === undefined && <p>Loading</p>}
        </div>
    );
};

export default ChangeItem;
