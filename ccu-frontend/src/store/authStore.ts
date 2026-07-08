import { create } from 'zustand';
import Cookies from 'js-cookie';
import { apiClient } from '../api/axios';

export interface User {
    username: string;
    email: string;
}

interface AuthState {
    user: User | null;
    isAuthenticated: boolean;
    isLoading: boolean;
    login: (accessToken: string, refreshToken: string, user: User) => void;
    logout: () => void;
    checkAuth: () => Promise<void>;
}

export const useAuthStore = create<AuthState>((set) => ({
    user: null,
    isAuthenticated: !!Cookies.get('accessToken'),
    isLoading: true,
    login: (accessToken: string, refreshToken: string, user: User) => {
        Cookies.set('accessToken', accessToken, { secure: true, sameSite: 'strict', expires: 7 });
        if (refreshToken) Cookies.set('refreshToken', refreshToken, { secure: true, sameSite: 'strict', expires: 7 });
        set({ isAuthenticated: true, user });
    },
    logout: async () => {
        const refreshToken = Cookies.get('refreshToken');
        if (refreshToken) {
            try {
                await apiClient.post('/api/v1/auth/logout', { refreshToken });
            } catch (error) {
                console.error("Logout failed at server", error);
            }
        }
        Cookies.remove('accessToken');
        Cookies.remove('refreshToken');
        set({ user: null, isAuthenticated: false });
    },
    checkAuth: async () => {
        const token = Cookies.get('accessToken');
        if (!token) {
            set({ isLoading: false, isAuthenticated: false, user: null });
            return;
        }
        try {
            const response = await apiClient.get('/api/v1/users/me');
            if (response.data.success) {
                set({ user: response.data.data, isAuthenticated: true, isLoading: false });
            } else {
                Cookies.remove('accessToken');
                set({ user: null, isAuthenticated: false, isLoading: false });
            }
        } catch (error) {
            Cookies.remove('accessToken');
            set({ user: null, isAuthenticated: false, isLoading: false });
        }
    },
}));
