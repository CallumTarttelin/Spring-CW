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
    categories: string[] | string,
}

const AddItem: React.FunctionComponent<AddItemProps> = (props: AddItemProps) => {
    const initialIndexes: number[] = [];
    const [indexes, setIndexes] = useState(initialIndexes);
    const [counter, setCounter] = useState(0);
    const [newId, setNewId] = useState("");
    const [isError, setIsError] = useState(false);
    const { register, handleSubmit, errors } = useForm<ItemFormData>();
    const onSubmit = (formData: ItemFormData) => {
        const date = new Date(formData.listUntilDate);
        date.setMilliseconds(0);
        formData.listUntilDate = date.toISOString().replace(".000Z", "Z");
        formData.categories = formData.categories === undefined ? [] : (formData.categories as string[]);
        formData.categories = formData.categories.join(",");
        console.log(formData);
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

    const addCategory = () => {
        setIndexes((prevIndexes) => [...prevIndexes, counter]);
        setCounter(prevCounter => prevCounter + 1);
    };

    const removeCategory = (index: number) => () => {
        setIndexes(prevIndexes => [...prevIndexes.filter(item => item !== index)]);
    };

    return (
        <div className="AddItem">
            <form onSubmit={handleSubmit(onSubmit)}>
                <select name="condition" ref={register({ required: props.status === Statuses.Offered })}>
                    <option value="new">new</option>
                    <option value="lightly-used">lightly used</option>
                    <option value="used">used</option>
                    <option value="broken">broken</option>
                </select>
                {errors.condition && 'Condition is required'}
                <input name="description" ref={register({ required: true, maxLength: 120 })} />
                {errors.condition && 'Description is required, max length 120'}
                <input type="date" name="listUntilDate" ref={register({ required: true })} />
                {errors.condition && 'list until date is required'}
                {indexes.map(index => (
                    <div key={`categories[${index}]`}>
                        <input name={`categories[${index}]`} ref={register} />
                        <button type="button" onClick={removeCategory(index)}>Remove Category</button>
                    </div>
                ))}
                <button type="button" onClick={addCategory}>Add Category</button>
                <input type="submit" />
                {isError && <p>Error creating item, please check input and try again</p>}
            </form>
            {newId !== "" && <Redirect to={`/item/${newId}`} />}
        </div>
    );
};

export default AddItem;
