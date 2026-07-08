
import { Outlet, useNavigate } from "react-router-dom";
import { LogOut, User as UserIcon } from "lucide-react";
import { useAuthStore } from "../store/authStore";
import { Button } from "./ui/Button";

export function Layout() {
    const { user, logout } = useAuthStore();
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        navigate("/login");
    };

    return (
        <div className="min-h-screen bg-background flex flex-col">
            <header className="sticky top-0 z-50 w-full border-b border-border bg-surface/95 backdrop-blur supports-[backdrop-filter]:bg-surface/60">
                <div className="container mx-auto px-4 h-16 flex items-center justify-between">
                    <div className="flex items-center gap-2">
                        <div className="h-8 w-8 rounded-md bg-primary flex items-center justify-center">
                            <span className="text-white font-bold tracking-tight">CCU</span>
                        </div>
                        <span className="font-semibold text-lg hidden sm:inline-block">Course Content</span>
                    </div>

                    {user && (
                        <div className="flex items-center gap-4">
                            <div className="flex items-center gap-2 text-sm text-text-muted bg-white/5 px-3 py-1.5 rounded-full border border-border">
                                <UserIcon className="h-4 w-4" />
                                <span className="hidden sm:inline-block font-medium">{user.username || user.email}</span>
                            </div>
                            <Button variant="ghost" size="sm" onClick={handleLogout} className="text-text-muted hover:text-danger hover:bg-danger/10 gap-2 transition-colors">
                                <LogOut className="h-4 w-4" />
                                <span className="hidden sm:inline-block">Logout</span>
                            </Button>
                        </div>
                    )}
                </div>
            </header>

            <main className="flex-1 container mx-auto px-4 py-8 max-w-7xl">
                <Outlet />
            </main>
        </div>
    );
}
