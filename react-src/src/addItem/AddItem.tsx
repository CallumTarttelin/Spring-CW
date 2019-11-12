import React, {useState} from 'react';
import './AddItem.scss';
import {Statuses} from "../store/types";
import useForm from 'react-hook-form';

interface AddItemProps {
    status: Statuses;
}

const AddItem: React.FunctionComponent<AddItemProps> = (props: AddItemProps) => {
    const initialIndexes: number[] = [];
    const [indexes, setIndexes] = useState(initialIndexes);
    const [counter, setCounter] = useState(0);
    const { register, handleSubmit, errors } = useForm();
    const onSubmit = console.log;

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
                <input type="datetime" name="listUntilDate" ref={register({ required: true })} />
                {errors.condition && 'list until date is required'}
                {indexes.map(index => (
                    <fieldset name={`category[${index}]`} key={`category[${index}]`}>
                        <input name={`category[${index}].category`} ref={register} />
                        <button type="button" onClick={removeCategory(index)} />
                    </fieldset>
                ))}
                <button type="button" onClick={addCategory} />
                <input type="submit" />
            </form>
        </div>
    );
};

export default AddItem;
