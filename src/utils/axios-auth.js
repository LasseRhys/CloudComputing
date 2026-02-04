// src/utils/axios-auth.js
import axios from 'axios';
import store from '@/store';
import router from "@/router";

const api = axios.create({
    baseURL: '/api/',
    withCredentials: true
});

let isRefreshing = false;
let failedQueue = [];

const processQueue = (error) => {
    failedQueue.forEach(prom => {
        if (error) {
            prom.reject(error);
        } else {
            prom.resolve();
        }
    });
    failedQueue = [];
};

api.interceptors.response.use(
    (response) => response,
    async (error) => {
        const originalRequest = error.config;
        if (error.response?.status !== 401 || originalRequest._retry) {
            return Promise.reject(error);
        }

        if (isRefreshing) {
            return new Promise((resolve, reject) => {
                failedQueue.push({ resolve, reject });
            })
                .then(() => api(originalRequest))
                .catch((err) => Promise.reject(err));
        }

        originalRequest._retry = true;
        isRefreshing = true;

        try {
            await store.dispatch('auth/refreshToken');
            processQueue(null);
            return api(originalRequest);
        } catch (refreshError) {
            processQueue(refreshError, null);
            await store.dispatch('auth/logout');
            router.push('auth/login');
            return Promise.reject(refreshError);
        } finally {
            isRefreshing = false;
        }
    }
);

export default api;