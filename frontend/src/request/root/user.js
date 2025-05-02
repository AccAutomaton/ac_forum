import request from "@/request/normal/index.js";

export const rootRegisterUser = (username, password, email) =>
    request({
        url: '/root/register',
        method: 'PUT',
        data: {
            username: username,
            password: password,
            email: email,
        }
    }).then(response => response["data"])