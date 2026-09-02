import { useState } from "react";
import { useNavigate } from "react-router-dom";
import "./css/Navbar.css";
import LogoutButton from "./LogoutButton";
import { getUserRole } from "../utils/jwt";

interface NavbarProps {
    backPath?: string;
    backLabel?: string;
}

function Navbar({ backPath, backLabel }: NavbarProps) {
    const navigate = useNavigate();
    const token = localStorage.getItem("token");
    const role = token ? getUserRole(token) : null;
    const [menuOpen, setMenuOpen] = useState(false);

    return (
        <nav className="navbar">
            <div className="navbar-left">
                {backPath && (
                    <button onClick={() => navigate(backPath)}>
                        ← {backLabel ?? "Back"}
                    </button>
                )}
            </div>

            <div className="navbar-right">
                <div className="navbar-menu-container">
                    <button onClick={() => setMenuOpen(!menuOpen)}>
                        ☰
                    </button>

                    {menuOpen && (
                        <div className="navbar-menu">
                            {role === "ROLE_ADMIN" && (
                                <button onClick={() => navigate("/admin/movies")}>
                                    Admin
                                </button>
                            )}
                            
                            <LogoutButton />

                            <button onClick={() => navigate("/watchlist")}>
                                Watchlist
                            </button>
                        </div>
                    )}
                </div>
            </div>
        </nav>
    );
}

export default Navbar;