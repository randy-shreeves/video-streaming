import { Navigate } from "react-router-dom";
import { getUserRole } from "../utils/jwt.ts";

interface ProtectedRouteProps {
    children: React.ReactNode;
    requiredRole?: string;
}

function ProtectedRoute({ children, requiredRole }: ProtectedRouteProps) {
    const token = localStorage.getItem("token");

    if (!token) {
        return <Navigate to="/" replace />;
    }

    if (requiredRole) {
        const role = getUserRole(token);
        if (role !== requiredRole) {
            return <Navigate to="/movies" replace/>;
        }
    }

    return children;
}

export default ProtectedRoute;