import * as React from "react";
import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { Card, CardHeader, CardTitle, CardContent, CardFooter } from "../components/ui/Card";
import { Button } from "../components/ui/Button";
import { Input } from "../components/ui/Input";
import { useAuthStore } from "../store/authStore";
import { apiClient } from "../api/axios";
import { toast } from "react-hot-toast";

export function Register() {
    const [username, setUsername] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [isLoading, setIsLoading] = useState(false);
    const { login } = useAuthStore();
    const navigate = useNavigate();

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setIsLoading(true);

        try {
            const { data } = await apiClient.post("/api/v1/auth/register", { username, email, password });
            if (data.success && data.data?.accessToken) {
                login(data.data.accessToken, data.data.refreshToken, { email, username });
                toast.success("Registration successful");
                navigate("/");
            } else {
                toast.error(data.message || "Failed to register");
            }
        } catch (error: any) {
            toast.error(error.response?.data?.message || "An error occurred during registration");
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="min-h-[80vh] flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8">
            <Card className="w-full max-w-md bg-[#111111] border border-[#262626] shadow-sm rounded-2xl">
                <CardHeader className="space-y-1 text-center pb-6">
                    <div className="mx-auto w-12 h-12 bg-primary rounded-xl flex items-center justify-center mb-2 shadow-sm">
                        <svg className="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M18 9v3m0 0v3m0-3h3m-3 0h-3m-2-5a4 4 0 11-8 0 4 4 0 018 0zM3 20a6 6 0 0112 0v1H3v-1z" /></svg>
                    </div>
                    <CardTitle className="text-2xl font-bold tracking-tight text-white">Create an account</CardTitle>
                    <p className="text-sm text-gray-400">Join Course Manager today</p>
                </CardHeader>
                <form onSubmit={handleSubmit}>
                    <CardContent className="space-y-5">
                        <Input
                            label="Username"
                            type="text"
                            placeholder="johndoe"
                            required
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                            disabled={isLoading}
                            autoComplete="username"
                        />
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
                            autoComplete="new-password"
                        />
                    </CardContent>
                    <CardFooter className="flex flex-col space-y-4 pt-4">
                        <Button type="submit" className="w-full text-md py-6 shadow-sm font-semibold rounded-lg" isLoading={isLoading}>
                            Create Account
                        </Button>
                        <div className="text-center text-sm mt-4">
                            <span className="text-gray-400">Already have an account? </span>
                            <Link to="/login" className="text-yellow-500 hover:text-yellow-400 font-medium transition-all">
                                Sign in instead
                            </Link>
                        </div>
                    </CardFooter>
                </form>
            </Card>
        </div>
    );
}
