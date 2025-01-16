<template>
  <div class="p-6 w-full">
    <div class="mb-6">
      <h2 class="text-2xl font-bold text-gray-800">My Vouchers</h2>
    </div>

    <div class="bg-white shadow-lg overflow-hidden w-full">
      <div class="overflow-x-auto w-full">
        <table class="w-full divide-y divide-gray-200">
          <thead>
          <tr>
            <th class="px-6 py-3 bg-gray-50 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              Code
            </th>
            <th class="px-6 py-3 bg-gray-50 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              Kunde ID
            </th>
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
              <div class="text-sm font-medium text-gray-900">{{ voucher.voucherCode	 }}</div>
            </td>
            <td class="px-6 py-4">
              <div class="text-sm font-medium text-gray-900">{{ voucher.customerId	 === -1 ? 'Nicht zugewiesen' : voucher.customerId }}</div>
            </td>
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

    <BasePagination
        :current-page="currentPage"
        :total-pages="totalPages"
        @page-change="loadPage"
    />

    <div v-if="loading" class="flex justify-center items-center mt-4">
      <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-500"></div>
    </div>

    <div v-if="error" class="mt-4 p-4 bg-red-100 border border-red-400 text-red-700 rounded-md">
      {{ error }}
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useVoucher } from '@/composables/useVoucher';
import { formatDate, formatPrice } from '@/utils/formatter';
import BasePagination from "@/components/BasePagination.vue";

const { error, loading, getAllVouchers } = useVoucher();

const vouchers = ref([]);
const totalPages = ref(0);
const currentPage = ref(0);
const sortConfig = ref({
  sortBy: 'used',
  direction: 'asc'
});

const loadPage = async (page) => {
  const response = await getAllVouchers({
    page,
    size: 10,
    sortBy: sortConfig.value.sortBy,
    direction: sortConfig.value.direction
  });

  if (response) {
    vouchers.value = response.content;
    totalPages.value = response.totalPages;
    currentPage.value = response.number;
  }
};

onMounted(() => {
  loadPage(0);
});
</script>


