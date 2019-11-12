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
    categories: string[];
    questions: Question[];
    user: User;
}

interface Question {
    id: string;
    sentBy: User;
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