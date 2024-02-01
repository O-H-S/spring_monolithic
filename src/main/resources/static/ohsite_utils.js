var csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
var csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

async function fetchWithCSRF(url, method, body = null) {
    const headers = {
        'Content-Type': 'application/json',
        [csrfHeader]: csrfToken
    };

    let options = {
        method: method,
        headers: headers,
        credentials: 'include'
    };

    if (body) {
        options.body = JSON.stringify(body);
    }


    const response = await fetch(url, options);
    /*if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText);
    }*/
    return response;

}

async function fetchWithCredentials(url, method, body = null) {
    const headers = {

    };

    let options = {
        method: method,
        headers: headers,
        credentials: 'include'
    };



    const response = await fetch(url, options);
    /*if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText);
    }*/
    return response;

}
