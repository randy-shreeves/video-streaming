type JwtPayload = {
    sub: string;
    role: string;
    exp: number;
    iat: number;
};

export function getJwtPayload(token: string): JwtPayload {
    const payload = token.split(".")[1];
    const decodedPayload = atob(payload);
    return JSON.parse(decodedPayload);
}

export function getUserRole(token: string): string {
    const payload = getJwtPayload(token);
    return payload.role;
}