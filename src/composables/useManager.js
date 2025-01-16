import api from "@/utils/axios-auth";
import {ref} from "vue";
export function useManager() {

    const loading = ref(false);
    const error = ref('');

    const getEventManagers = async () => {
        try {
            loading.value = true;
            error.value = '';
            const response = await api.get('employees/event-managers');
            return response.data;

        } catch (err) {
            if (err.response) {
                error.value = err.response.data.error
            }
        } finally {
            loading.value = false;
        }
    };
    const createEvent = async (event) => {
        try {
            loading.value = true;
            error.value = '';
            const response = await api.post(`events/command/create`, event);
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
        getEventManagers,
        createEvent
    }
}