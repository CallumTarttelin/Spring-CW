import React, {useState} from 'react';
import axios, {AxiosResponse} from 'axios';
import { Redirect } from 'react-router-dom';
import './AddItem.scss';
import {Statuses} from "../store/types";
import useForm from 'react-hook-form';
import qs from 'querystringify';

interface AddItemProps {
    status: Statuses;
}

type ItemFormData = {
    condition?: string,
    description: string,
    listUntilDate: string,
    category: string,
}

const AddItem: React.FunctionComponent<AddItemProps> = (props: AddItemProps) => {
    const [newId, setNewId] = useState("");
    const [isError, setIsError] = useState(false);
    const { register, handleSubmit, errors } = useForm<ItemFormData>();
    const onSubmit = (formData: ItemFormData) => {
        const date = new Date(formData.listUntilDate);
        date.setMilliseconds(0);
        formData.listUntilDate = date.toISOString().replace(".000Z", "Z");
        axios.post(
            `/api/${props.status}`, qs.stringify(formData),
            {withCredentials: true, headers: {'Content-type': 'application/x-www-form-urlencoded'}}
            )
            .then((resp: AxiosResponse) => {
                const location: string = resp.headers.location;
                const split = location.split("/");
                setNewId(split[split.length - 1]);
            })
            .catch(() => setIsError(true))
    };

    return (
        <div className="AddItem">
            <form onSubmit={handleSubmit(onSubmit)}>
                <select name="condition" ref={register({ required: props.status === Statuses.Offered })}>
                    <option value="new/unused">new/unused</option>
                    <option value="used">used</option>
                    <option value="damaged">damaged</option>
                    <option value="scrap">scrap</option>
                </select>
                {errors.condition && 'Condition is required'}
                <input name="description" ref={register({ required: true, maxLength: 120 })} />
                {errors.condition && 'Description is required, max length 120'}
                <input type="date" name="listUntilDate" ref={register({ required: true })} />
                {errors.condition && 'list until date is required'}
                <select name="category" ref={register({ required: props.status === Statuses.Offered })}>
                    <option value="Household">Household</option>
                    <option value="Hobbies">Hobbies</option>
                    <option value="Toys">Toys</option>
                    <option value="Electronics">Electronics</option>
                    <option value="Garden">Garden</option>
                    <option value="Collectable">Collectable</option>
                    <option value="Other">Other</option>
                </select>
                {errors.category && 'Category is required'}
                <input type="submit" />
                {isError && <p>Error creating item, please check input and try again</p>}
            </form>
            {newId !== "" && <Redirect to={`/item/${newId}`} />}
        </div>
    );
};

export default AddItem;
