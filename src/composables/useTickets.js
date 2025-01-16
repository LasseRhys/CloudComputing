import api from "@/utils/axios-auth";
import {ref} from "vue";
export function useTickets() {

    const loading = ref(false);
    const error = ref('');

    const getUserTickets = async ({
                                      page = 0,
                                      size = 10,
                                      sortBy = 'read',
                                      direction = 'asc'
                                  }= {}) => {
        try {
            loading.value = true;
            error.value = '';
            const response = await api.get('tickets/query/user', {
                params: {
                    page,
                    size,
                    sortBy,
                    direction
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
    const purchaseTicket = async (purchaseData) => {
        try {
            loading.value = true;
            error.value = '';
            const response = await api.post('tickets/command/purchase', purchaseData);
            return response.data;
        } catch (err) {
            if (err.response) {
                error.value = err.response.data.error;
            }
            throw err;
        } finally {
            loading.value = false;
        }
    };

    return {
        loading,
        error,
        getUserTickets,
        purchaseTicket
    }
}