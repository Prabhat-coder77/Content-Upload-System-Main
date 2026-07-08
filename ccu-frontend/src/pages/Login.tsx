import * as React from "react";
import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { Card, CardHeader, CardTitle, CardContent, CardFooter } from "../components/ui/Card";
import { Button } from "../components/ui/Button";
import { Input } from "../components/ui/Input";
import { useAuthStore } from "../store/authStore";
import { apiClient } from "../api/axios";
import { toast } from "react-hot-toast";

export function Login() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [isLoading, setIsLoading] = useState(false);
    const { login } = useAuthStore();
    const navigate = useNavigate();

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setIsLoading(true);

        try {
            const { data } = await apiClient.post("/api/v1/auth/login", { email, password });
            if (data.success && data.data?.accessToken) {
                login(data.data.accessToken, data.data.refreshToken, { email, username: email.split('@')[0] });
                toast.success("Successfully logged in");
                navigate("/");
            } else {
                toast.error(data.message || "Failed to login");
            }
        } catch (error: any) {
            toast.error(error.response?.data?.message || "An error occurred during login");
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="min-h-[80vh] flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8">
            <Card className="w-full max-w-md bg-[#111111] border border-[#262626] shadow-sm rounded-2xl">
                <CardHeader className="space-y-1 text-center pb-6">
                    <div className="mx-auto w-12 h-12 bg-primary rounded-xl flex items-center justify-center mb-2 shadow-sm">
                        <svg className="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-6 9l2 2 4-4" /></svg>
                    </div>
                    <CardTitle className="text-2xl font-bold tracking-tight text-white">Welcome back</CardTitle>
                    <p className="text-sm text-gray-400">Sign in to Course Manager</p>
                </CardHeader>
                <form onSubmit={handleSubmit}>
                    <CardContent className="space-y-5">
                        <Input
                            label="Email Address"
                            type="email"
                            placeholder="name@example.com"
                            required
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            disabled={isLoading}
                            autoComplete="email"
                        />
                        <Input
                            label="Password"
                            type="password"
                            placeholder="••••••••"
                            required
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            disabled={isLoading}
                            autoComplete="current-password"
                        />
                    </CardContent>
                    <CardFooter className="flex flex-col space-y-4 pt-4">
                        <Button type="submit" className="w-full text-md py-6 shadow-sm font-semibold rounded-lg" isLoading={isLoading}>
                            Sign in
                        </Button>
                        <div className="text-center text-sm mt-4">
                            <span className="text-gray-400">Don't have an account? </span>
                            <Link to="/register" className="text-yellow-500 hover:text-yellow-400 font-medium transition-all">
                                Create one
                            </Link>
                        </div>
                    </CardFooter>
                </form>
            </Card>
        </div>
    );
}
