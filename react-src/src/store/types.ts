export enum Statuses {
    Offered = "offered",
    Wanted = "wanted",
}

export interface Item {
    id: string;
    condition: string;
    description: string;
    status: Statuses;
    listUntilDate: string;
    category: string;
    questions: Question[];
    user: User;
}

export interface Question {
    id: string;
    sentBy: User;
    message: string;
    responses: Response[];
}

export interface Response {
    sentBy: User;
    message: string;
}

export interface User {
    username: string;
    address: string;
    postcode: string;
    email: string;
}

export interface EmailSettings {
    verified: boolean;
    notifyQuestions: boolean;
    notifyResponses: boolean;
}