import api from "@/utils/axios-auth";
import {ref} from "vue";
export function useVoucher() {

    const loading = ref(false);
    const error = ref('');

    const getUserVouchers = async ({
                                       page = 0,
                                       size = 10,
                                       sortBy = 'used',
                                       direction = 'asc',
                                   }) => {
        try {
            loading.value = true;
            error.value = '';
            const response = await api.get('vouchers/my-vouchers', {
                    params: {
                        page,
                        size,
                        sortBy,
                        direction
                    }
                }
                );
            return response.data;

        } catch (err) {
            if (err.response) {
                error.value = err.response.data.error
            }
        } finally {
            loading.value = false;
        }
    };
    const getUserVoucherAll = async () => {
        try {
            loading.value = true;
            error.value = '';
            const response = await api.get('vouchers/user-vouchers');
            return response.data;

        } catch (err) {
            if (err.response) {
                error.value = err.response.data.error
            }
        } finally {
            loading.value = false;
        }
    };
    const validateVoucher = async (code) => {
        try {
            loading.value = true;
            error.value = '';
            const response = await api.get(`vouchers/${code}/validate`);
            return response.data;

        } catch (err) {
            if (err.response) {
                error.value = err.response.data.message
            }
        } finally {
            loading.value = false;
        }
    };
    const bulkCreateVoucher = async (count) => {
        try {
            loading.value = true;
            error.value = '';
            const response = await api.post('vouchers/bulk', count);
            return response.data;
        } catch (err) {
            if (err.response) {
                error.value = err.response.data.error;
            }
        } finally {
            loading.value = false;
        }
    };

    const getVoucherTypes = async () => {
        try {
            loading.value = true;
            error.value = '';
            const response = await api.get('vouchers/types');
            return response.data;
        } catch (err) {
            if (err.response) {
                error.value = err.response.data.error;
            }
        } finally {
            loading.value = false;
        }
    };
    const getAllVouchers = async ({
                                      page = 0,
                                      size = 10,
                                      sortBy = 'used',
                                      direction = 'asc',
                                  }) => {
        try {
            loading.value = true;
            error.value = '';
            const response = await api.get('vouchers/all', {
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

    return {
        loading,
        error,
        getUserVouchers,
        getUserVoucherAll,
        validateVoucher,
        bulkCreateVoucher,
        getVoucherTypes,
        getAllVouchers
    }

}