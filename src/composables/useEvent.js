import api from "@/utils/axios-auth";
import {ref} from "vue";
import router from "@/router";
export function useEvent() {

    const loading = ref(false);
    const error = ref('');

    const getEvents = async () => {
        try {
            loading.value = true;
            error.value = '';
            const response = await api.get('events/query/user');
            return response.data;

        } catch (err) {
            if (err.response) {
                error.value = err.response.data.error
            }
        } finally {
            loading.value = false;
        }
    };
    const getAllEvents = async ({
                                    page = 0,
                                    size = 10,
                                    sortBy = 'read',
                                    direction = 'asc',
                                    search = ''  // Single search parameter
                                }) => {
        try {
            loading.value = true;
            error.value = '';
            const response = await api.get('events/query', {
                params: {
                    page,
                    size,
                    sortBy,
                    direction,
                    search
                }
            });
            return response.data;

        } catch (err) {
            if (err.response) {
                error.value = err.response.data.error
            }
        } finally {
            loading.value = false;
        }
    };
    const getEvent = async (id) => {
        try {
            loading.value = true;
            error.value = '';
            const response = await api.get(`events/query/${id}`);
            return response.data;
        } catch (err) {
            if (err.response) {
                error.value = err.response.data.error;
            }
        } finally {
            loading.value = false;
        }
    };
    const getEventManaged = async (id) => {
        try {
            loading.value = true;
            error.value = '';
            const response = await api.get(`events/query/managed/${id}`);
            return response.data;
        } catch (err) {
            if (err.response) {
                error.value = err.response.data.error;
                router.push('/event-manager/events');
            }
        } finally {
            loading.value = false;
        }
    };
    const getEventsByManager = async ({
                                          page = 0,
                                          size = 10,
                                          sortBy = 'read',
                                          direction = 'asc',
                                            search = ''  // Single search parameter
                                      }) => {
        try {
            loading.value = true;
            error.value = '';
            const response = await api.get(`events/query/manager`, {
                params: {
                    page,
                    size,
                    sortBy,
                    direction,
                    search
                }
            });
            return response.data;
        } catch (err) {
            if (err.response) {
                error.value = err.response.data.error;
            }
        } finally {
            loading.value = false;
        }
    };

    const updateEvent = async (id, event) => {
        try {
            loading.value = true;
            error.value = '';
            const response = await api.put(`events/command/update/${id}`, event);
            return response.data;
        } catch (err) {
            if (err.response) {
                error.value = err.response.data.message
            }
        } finally {
            loading.value = false;
        }
    };
    const deleteEvent = async (id) => {
        try {
            loading.value = true;
            error.value = '';
            const response = await api.delete(`events/command/delete/${id}`);
            return response.data;
        } catch (err) {
            if (err.response) {
                error.value = err.response.data.message
            }
        } finally {
            loading.value = false;
        }
    };
    return {
        loading,
        error,
        getEvent,
        getEvents,
        getEventsByManager,
        updateEvent,
        getEventManaged,
        deleteEvent,
        getAllEvents
    }
}