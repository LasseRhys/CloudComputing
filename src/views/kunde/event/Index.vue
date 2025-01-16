<template>
  <div class="p-6 w-full">
    <div class="mb-6 bg-white p-4 rounded-lg shadow">
      <div class="max-w-md">
        <p class="text-xl mb-2 font-bold text-gray-800">Veranstaltungen suchen</p>

        <SearchInput
            v-model="searchQuery"
            placeholder="Nach Titel oder Ort suchen"
            @search="handleSearch"
        />
      </div>
    </div>

    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      <div
          v-for="event in events"
          :key="event.id"
          class="bg-white rounded-lg shadow-lg overflow-hidden hover:shadow-xl transition-shadow duration-300"
      >
        <div class="p-4 border-b border-gray-200 bg-gray-50">
          <h3 class="text-xl font-semibold text-gray-900 truncate">{{ event.title }}</h3>
        </div>

        <div class="p-4 space-y-3">
          <div class="flex items-center text-gray-600">
            <i class="fas fa-calendar-alt mr-2"></i>
            <span>{{ formatDate(event.dateOfEvent) }}</span>
          </div>

          <div class="flex items-center text-gray-600">
            <i class="fas fa-map-marker-alt mr-2"></i>
            <span>{{ event.location }}</span>
          </div>

          <div class="flex items-center font-semibold">
            <i class="fas fa-tag mr-2"></i>
            <span>{{ formatPrice(event.price) }}</span>
          </div>

          <div class="flex items-center">
            <span
                v-if="isEventPassed(event.dateOfEvent)"
                class="px-3 py-1 rounded-full text-gray-500 font-semibold"
            >
              Bereits stattgefunden
            </span>
            <span
                v-else-if="event.availableTickets > 0"
                class="px-3 py-1 rounded-full text-emerald-500 font-semibold"
            >
              {{ event.availableTickets }} Tickets verfügbar
            </span>
            <span
                v-else
                class="px-3 py-1 rounded-full text-red-500 font-semibold"
            >
              Ausverkauft
            </span>
          </div>
        </div>

        <div class="p-4 bg-gray-50 border-t border-gray-200 flex justify-between">
          <router-link
              :to="{ name: 'EventShow', params: { id: event.id } }"
              class="text-white bg-blue-500 hover:bg-blue-600 px-4 py-2 rounded text-sm inline-flex items-center"
          >
            <i class="fas fa-eye mr-2"></i>
            Details
          </router-link>

          <router-link
              v-if="!isEventPassed(event.dateOfEvent) && event.availableTickets > 0"
              :to="{ name: 'PurchaseTicket', params: { id: event.id } }"
              class="text-white bg-green-500 hover:bg-green-600 px-4 py-2 rounded text-sm inline-flex items-center"
          >
            <i class="fas fa-ticket-alt mr-2"></i>
            Kaufen
          </router-link>
          <span
              v-else-if="isEventPassed(event.dateOfEvent)"
              class="text-gray-500 text-sm font-medium py-2"
          >
            Bereits stattgefunden
          </span>
          <span
              v-else
              class="text-red-500 text-sm font-medium py-2"
          >
            Ausverkauft
          </span>
        </div>
      </div>
    </div>

    <div class="mt-6">
      <BasePagination
          :current-page="currentPage - 1"
          :total-pages="totalPages"
          @page-change="handlePageChange"
      />
    </div>
  </div>
</template>

<script>
import {ref, onMounted, onUnmounted} from 'vue';
import BasePagination from '@/components/BasePagination.vue';
import SearchInput from '@/components/SearchComponent.vue';
import websocketService from '@/utils/websocket';
import {formatDate, formatPrice} from "@/utils/formatter";
import {useEvent} from "@/composables/useEvent";

export default {
  components: {
    BasePagination,
    SearchInput
  },

  setup() {
    const events = ref([]);
    const currentPage = ref(1);
    const rowsPerPage = ref(12);
    const totalElements = ref(0);
    const totalPages = ref(0);
    const searchQuery = ref('');
    let unsubscribeNewEvent = null;
    let unsubscribeEventUpdate = null;
    const {error, loading, getAllEvents} = useEvent();

    const isEventPassed = (dateOfEvent) => {
      return new Date(dateOfEvent) < new Date();
    };

    const handleNewEvent = (newEvent) => {
      if (!searchQuery.value ||
          newEvent.title.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
          newEvent.location.toLowerCase().includes(searchQuery.value.toLowerCase())) {
        events.value.unshift(newEvent);
      }
    };

    const handleEventUpdate = (updatedEvent) => {
      const index = events.value.findIndex(event => event.id === updatedEvent.id);
      if (index !== -1) {
        events.value[index] = updatedEvent;
      }
    };

    const fetchEvents = async () => {
      try {
        const response = await getAllEvents({
          page: currentPage.value - 1,
          size: rowsPerPage.value,
          sortBy: 'dateOfEvent',
          search: searchQuery.value
        });

        events.value = response.content;
        totalElements.value = response.totalElements;
        totalPages.value = response.totalPages;
      } catch (error) {
        console.error("Error loading data:", error.response?.data?.error);
      }
    };

    const handlePageChange = (page) => {
      currentPage.value = page + 1;
      fetchEvents();
    };

    const handleSearch = (value) => {
      searchQuery.value = value;
      currentPage.value = 1;
      fetchEvents();
    };

    onMounted(async () => {

      try {
          await fetchEvents();
      } catch (error) {
          console.error('Failed to setup WebSocket connection:', error);
      }

        try {

        await websocketService.connect();
        unsubscribeNewEvent = websocketService.on('newEvent', handleNewEvent);
        unsubscribeEventUpdate = websocketService.on('eventUpdate', handleEventUpdate);
      } catch (error) {
        console.error('Failed to setup WebSocket connection:', error);
        }

    });

    onUnmounted(() => {
      if (unsubscribeNewEvent) unsubscribeNewEvent();
      if (unsubscribeEventUpdate) unsubscribeEventUpdate();
    });

    return {
      events,
      currentPage,
      totalPages,
      searchQuery,
      handlePageChange,
      handleSearch,
      formatDate,
      formatPrice,
      isEventPassed
    };
  }
};
</script>