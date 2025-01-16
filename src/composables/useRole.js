import api from "@/utils/axios-auth";
import {ref} from "vue";
export function useRole() {

    const loading = ref(false);
    const error = ref('');

    const getRoles = async () => {
        try {
            loading.value = true;
            error.value = '';
            const response = await api.get('roles/query/roles');
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
        getRoles
    }
}