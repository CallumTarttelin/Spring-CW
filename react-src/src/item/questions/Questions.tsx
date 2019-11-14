import React from 'react';
import './Questions.scss';
import {Item} from "../../store/types";
import QuestionView from "../question/QuestionView";

interface QuestionsProps {
    item: Item;
}

const Questions: React.FunctionComponent<QuestionsProps> = (props: QuestionsProps) => {
    const { item } = props;
    return (
        <div className="Questions">
            {item.questions.map(question => <QuestionView item={item} question={question} key={question.id}/>)}
        </div>
    );
};

export default Questions;
