import React, {useState} from 'react';
import './QuestionView.scss';
import {Item, Question} from "../../store/types";
import Ask from "../ask/Ask";

interface QuestionProps {
    item: Item;
    question: Question;
}

const QuestionView: React.FunctionComponent<QuestionProps> = (props: QuestionProps) => {
    const { message, responses } = props.question;
    const [show, setShow] = useState(false);
    const showForm = () => {
        setShow(true);
    };
    return (
        <div className="QuestionView">
            <p>{message}</p>
            <div className="Question-Responses">
                {responses.map(response => <div className="Response">{response.message}</div>)}
            </div>
            <button onClick={showForm}>Reply</button>
            {show && <Ask item={props.item} question={props.question} /> }
        </div>
    );
};

export default QuestionView;
