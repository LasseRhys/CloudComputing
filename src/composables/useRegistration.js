import { ref } from 'vue';
import router from "@/router";
import api from "@/utils/axios-auth";

export function useRegistration() {
    const loading = ref(false);
    const error = ref('');

    const registerCustomer = async (formData) => {
        try {
            loading.value = true;
            error.value = '';

            const response = await api.post('registration/customer', formData);
            await router.push('/auth/login');

            return response.data;

        } catch (err) {
            if (err.response) {
                error.value = err.response.data.error
            }
        } finally {
            loading.value = false;
        }
    };

    const registerEmployee = async (formData) => {
        try {
            loading.value = true;
            error.value = '';

            const response = await api.post('registration/employee', formData);

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
        registerCustomer,
        registerEmployee
    };
}