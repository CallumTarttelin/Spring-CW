import React, {useState} from 'react';
import axios from 'axios';
import './Ask.scss';
import useForm from 'react-hook-form';
import qs from 'querystringify';
import {Item, Question} from "../../store/types";
import {fetchItem} from "../../store/item/actions";
import {useDispatch} from "react-redux";

interface AddItemProps {
    item: Item;
    question?: Question;
}

type ItemFormData = {
    message: string,
}

const Ask: React.FunctionComponent<AddItemProps> = (props: AddItemProps) => {
    const [isError, setIsError] = useState(false);
    const { register, handleSubmit, errors } = useForm<ItemFormData>();
    const { item, question } = props;
    const dispatch = useDispatch();
    const url = `/api/item/${item.id}/question${question !== undefined ? "/" + question.id : ""}`;
    const onSubmit = (formData: ItemFormData) => {
        axios.post(
            url, qs.stringify(formData),
            {withCredentials: true, headers: {'Content-type': 'application/x-www-form-urlencoded'}}
        )
            .then(() => {
                dispatch(fetchItem(item.id));
            })
            .catch(() => setIsError(true))
    };

    return (
        <div className="AddItem">
            <form onSubmit={handleSubmit(onSubmit)}>
                <input name="message" ref={register({ required: true, maxLength: 300 })} />
                {errors.message && 'Message is required, max length 300'}
                <input type="submit" />
                {isError && <p>Error with message, please check input and try again</p>}
            </form>
        </div>
    );
};

export default Ask;
