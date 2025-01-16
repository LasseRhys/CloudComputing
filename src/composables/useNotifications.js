import api from "@/utils/axios-auth";
import { ref } from "vue";

export function useNotifications() {
    const loading = ref(false);
    const error = ref('');
    const notifications = ref([]);
    const totalPages = ref(0);
    const totalElements = ref(0);
    const currentPage = ref(0);

    const getUserNotifications = async ({
                                            page = 0,
                                            size = 10,
                                            sortBy = 'read',
                                            direction = 'asc'
                                        } = {}) => {
        try {
            console.log("test")

            loading.value = true;
            error.value = '';
            const response = await api.get('notifications', {
                params: {
                    page,
                    size,
                    sortBy,
                    direction
                }
            });
            notifications.value = response.data.content;
            totalPages.value = response.data.totalPages;
            totalElements.value = response.data.totalElements;
            currentPage.value = response.data.number;

            return response.data;
        } catch (err) {
            if (err.response) {
                error.value = err.response.data.error;
            }
        } finally {
            loading.value = false;
        }
    };
    const markAsRead = async (id) => {
        try {
            loading.value = true;
            error.value = '';
            const response = await api.put(`notifications/${id}/read`);
            return response.data;
        } catch (err) {
            if (err.response) {
                error.value = err.response.data.message
            }
        } finally {
            loading.value = false;
        }
    };
    const markMultipleAsRead = async (notificationIds) => {
        try {
            loading.value = true;
            error.value = '';
            const response = await api.put('notifications/mark-multiple-read', {
                notificationIds: notificationIds
            });
            return response.data;
        } catch (err) {
            if (err.response) {
                error.value = err.response.data.message;
            }
        } finally {
            loading.value = false;
        }
    };
    return {
        loading,
        error,
        notifications,
        totalPages,
        totalElements,
        currentPage,
        getUserNotifications,
        markAsRead,
        markMultipleAsRead
    };
}