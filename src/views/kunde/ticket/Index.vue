<template>
  <div class="p-6 w-full">
    <div class="mb-6">
      <h2 class="text-2xl font-bold text-gray-800">Gekaufte Tickets</h2>
    </div>

    <div class="bg-white shadow-lg overflow-hidden w-full">
      <div class="overflow-x-auto w-full">
        <table class="w-full divide-y divide-gray-200">
          <thead>
          <tr>
            <th class="px-6 py-3 bg-gray-50 text-left text-x font-medium text-gray-500 uppercase tracking-wider">
              Veranstaltungsname
            </th>
            <th class="px-6 py-3 bg-gray-50 text-left text-x font-medium text-gray-500 uppercase tracking-wider">
              Datum
            </th>
            <th class="px-6 py-3 bg-gray-50 text-left text-x font-medium text-gray-500 uppercase tracking-wider">
              Ort
            </th>
            <th class="px-6 py-3 bg-gray-50 text-left text-x font-medium text-gray-500 uppercase tracking-wider">
              Finaler Preis
            </th>
            <th class="px-6 py-3 bg-gray-50 text-left text-x font-medium text-gray-500 uppercase tracking-wider">
              Tage bis Veranstaltung
            </th>
          </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
          <tr v-for="ticket in tickets" :key="ticket.id" class="hover:bg-gray-50">
            <td class="px-6 py-4">
              <div class="text-l font-medium text-gray-900">{{ ticket.eventTitle }}</div>
            </td>
            <td class="px-6 py-4">
              <div class="text-l text-gray-500">{{ formatDate(ticket.date) }}</div>
            </td>
            <td class="px-6 py-4">
              <div class="text-l text-gray-500">{{ ticket.location }}</div>
            </td>
            <td class="px-6 py-4">
              <div class="text-l font-medium text-gray-900">{{ formatPrice(ticket.finalPrice) }}</div>
            </td>
            <td class="px-6 py-4">
                <span :class="getDaysLeftClass(calculateDaysLeft(ticket.date))"
                      class="px-2 inline-flex text-l leading-5 font-semibold rounded-full">
                  {{ calculateDaysLeft(ticket.date) }}
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
import { useTickets } from '@/composables/useTickets';
import BasePagination from '@/components/BasePagination.vue';
import { formatDate, formatPrice } from '@/utils/formatter';

export default {
  name: 'TicketsTable',
  components: {
    BasePagination
  },
  setup() {


    const { error, loading, getUserTickets } = useTickets();
    return {
      error,
      loading,
      getUserTickets,
    };
  },
  data() {
    return {
      tickets: [],
      totalPages: 0,
      currentPage: 0,
      sortConfig: {
        sortBy: 'purchaseDate',
        direction: 'desc'
      }
    };
  },
  methods: {
    formatPrice,
    formatDate,
    async loadPage(page) {
      const response = await this.getUserTickets({
        page,
        size: 10,
        sortBy: this.sortConfig.sortBy,
        direction: this.sortConfig.direction
      });

      if (response) {
        this.tickets = response.content;
        this.totalPages = response.totalPages;
        this.currentPage = response.number;
      }
    },
    calculateDaysLeft(eventDate) {
      const today = new Date();
      const event = new Date(eventDate);

      today.setHours(0, 0, 0, 0);
      event.setHours(0, 0, 0, 0);

      const diffTime = event.getTime() - today.getTime();
      const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

      if (diffDays < 0) {
        return 'Event passed';
      } else if (diffDays === 0) {
        return 'Today!';
      } else {
        return `${diffDays} days`;
      }
    },
    getDaysLeftClass(daysLeft) {
      if (daysLeft === 'Event passed') {
        return 'bg-gray-100 text-gray-800';
      } else if (daysLeft === 'Today!') {
        return 'bg-red-100 text-red-800';
      } else {
        const days = parseInt(daysLeft);
        if (days <= 7) {
          return 'bg-yellow-100 text-yellow-800';
        } else {
          return 'bg-green-100 text-green-800';
        }
      }
    }
  },
  mounted() {
    this.loadPage(0);
  },
  created() {


  }
};
</script>