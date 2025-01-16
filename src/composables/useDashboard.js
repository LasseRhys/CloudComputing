
import api from "@/utils/axios-auth";
import {ref} from "vue";
export function useDashboard() {

    const loading = ref(false);
    const error = ref('');

    const getDashboardInformation = async () => {
        try {
            loading.value = true;
            error.value = '';
            const response = await api.get('customers/dashboard-information');
            return response.data;

        } catch (err) {
            if (err.response) {
                error.value = err.response.data.error
            }
        } finally {
            loading.value = false;
        }
    };
    return {
        loading,
        error,
        getDashboardInformation
    }
}