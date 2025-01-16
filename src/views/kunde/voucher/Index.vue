<template>
  <div class="p-6 w-full">
    <div class="mb-6">
      <h2 class="text-2xl font-bold text-gray-800">Meine Gutscheine</h2>
    </div>

    <div class="bg-white shadow-lg overflow-hidden w-full">
      <div class="overflow-x-auto w-full">
        <table class="w-full divide-y divide-gray-200">
          <thead>
          <tr>
            <th class="px-6 py-3 bg-gray-50 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              Wert
            </th>
            <th class="px-6 py-3 bg-gray-50 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              Ablaufdatum
            </th>
            <th class="px-6 py-3 bg-gray-50 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              Status
            </th>
          </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
          <tr v-for="voucher in vouchers" :key="voucher.id" class="hover:bg-gray-50">
            <td class="px-6 py-4">
              <div class="text-sm font-medium text-gray-900">{{ formatPrice(voucher.amount) }}</div>
            </td>
            <td class="px-6 py-4">
              <div class="text-sm text-gray-500">{{ formatDate(voucher.voucherValidUntil) }}</div>
            </td>
            <td class="px-6 py-4">
                <span :class="[
                  voucher.used
                    ? 'bg-gray-100 text-gray-800'
                    : 'bg-green-100 text-green-800',
                  'px-2 inline-flex text-xs leading-5 font-semibold rounded-full'
                ]">
                  {{ voucher.used ? 'Benutzt' : 'Verfügbar' }}
                </span>
            </td>
          </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Pagination with matching style -->
    <BasePagination
        :current-page="currentPage"
        :total-pages="totalPages"
        @page-change="loadPage"
    />

    <!-- Loading State -->
    <div v-if="loading" class="flex justify-center items-center mt-4">
      <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-500"></div>
    </div>

    <!-- Error State -->
    <div v-if="error" class="mt-4 p-4 bg-red-100 border border-red-400 text-red-700 rounded-md">
      {{ error }}
    </div>
  </div>
</template>

<script>
import { useVoucher } from '@/composables/useVoucher';
import BasePagination from '@/components/BasePagination.vue';
import { formatDate, formatPrice } from '@/utils/formatter';

export default {
  name: 'VouchersTable',
  components: {
    BasePagination
  },
  setup() {
    const { error, loading, getUserVouchers } = useVoucher();
    return {
      error,
      loading,
      getUserVouchers
    };
  },
  data() {
    return {
      vouchers: [],
      totalPages: 0,
      currentPage: 0,
      sortConfig: {
        sortBy: 'used',
        direction: 'asc'
      }
    };
  },
  methods: {
    formatDate,
    formatPrice,
    async loadPage(page) {
      const response = await this.getUserVouchers({
        page,
        size: 10,
        sortBy: this.sortConfig.sortBy,
        direction: this.sortConfig.direction
      });

      if (response) {
        this.vouchers = response.content;
        this.totalPages = response.totalPages;
        this.currentPage = response.number;
      }
    }
  },
  mounted() {
    this.loadPage(0);
  }
};
</script>