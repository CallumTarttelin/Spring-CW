export function stripEmpty(object: {[index: string]: any}): {[index: string]: any} {
    for (const key in object) {
        if (! object[key]) {
            delete object[key]
        }
    }
    return object
}

export function formatDate(dateString: string): string {
    const date = new Date(dateString);
    date.setMilliseconds(0);
    return date.toISOString().replace(".000Z", "Z");
}
