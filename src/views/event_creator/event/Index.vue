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
                    <h3 class="text-xl font-semibold text-gray-900 break-words">{{ event.title }}</h3>
                </div>

                <div class="p-4 space-y-3">
                    <div class="flex items-center text-gray-600">
                        <i class="fas fa-calendar-alt mr-2 flex-shrink-0"></i>
                        <span class="break-words">{{ formatDate(event.dateOfEvent) }}</span>
                    </div>

                    <div class="flex items-center text-gray-600">
                        <i class="fas fa-map-marker-alt mr-2 flex-shrink-0"></i>
                        <span class="break-words">{{ event.location }}</span>
                    </div>

                    <div class="space-y-1">
                        <div class="flex items-center font-semibold">
                            <i class="fas fa-tag mr-2 flex-shrink-0"></i>
                            <span class="break-words">{{ formatPrice(event.price) }}</span>
                        </div>
                        <div class="flex items-center text-gray-500 text-sm ml-5">
                            <span class="break-words">Grundpreis: {{ formatPrice(event.basePrice) }}</span>
                        </div>
                    </div>

                    <div class="space-y-2">
                        <div class="flex items-center">
              <span
                      v-if="event.availableTickets > 0"
                      class="rounded-full text-emerald-500 font-semibold break-words"
              >
                {{ event.availableTickets }} Tickets verfügbar
              </span>
                            <span
                                    v-else
                                    class="rounded-full text-red-500 font-semibold break-words"
                            >
                Ausverkauft
              </span>
                        </div>

                        <div class="flex items-center">
                            <div class="w-full">
                                <div class="flex justify-between text-sm mb-1 flex-wrap gap-2">
                                    <span class="break-words">Verkauft: {{ event.soldTickets }}</span>
                                    <span class="break-words">Schwellenwert: {{ event.thresholdValue }}</span>
                                </div>
                                <div
                                        class="flex items-center space-x-2"
                                        :class="getThresholdClass(event)"
                                >
                                    <i
                                            class="fas mr-1 flex-shrink-0"
                                            :class="getThresholdIcon(event)"
                                    ></i>
                                    <span class="break-words">{{ getThresholdMessage(event) }}</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div  class="p-4 bg-gray-50 border-t border-gray-200 flex flex-wrap gap-2 justify-between">
                    <router-link v-if="hasEventManagerRole"
                            :to="{ name: 'UpdateEventForm', params: { id: event.id } }"
                            class="text-white bg-blue-500 hover:bg-blue-600 px-4 py-2 rounded text-sm inline-flex items-center"
                    >
                        <i class="fas fa-edit mr-2"></i>
                        Bearbeiten
                    </router-link>
                    <router-link
                            :to="{ name: 'EventCreatorEventShow', params: { id: event.id } }"
                            class="text-white bg-emerald-400 hover:text-blueGray-300 px-4 py-2 rounded text-sm inline-flex items-center"
                    >
                        <i class="fas fa-eye mr-2"></i>
                        Zeigen
                    </router-link>
                    <button
                            @click="handleDelete(event.id)"
                            class="text-white mt-1 bg-red-500 hover:bg-red-600 px-4 py-2 rounded text-sm inline-flex items-center"
                    >
                        <i class="fas fa-trash mr-2"></i>
                        Löschen
                    </button>
                </div>
            </div>
        </div>

        <div v-if="loading" class="flex justify-center items-center mt-4">
            <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-500"></div>
        </div>

        <div v-if="error" class="mt-4 p-4 bg-red-100 border border-red-400 text-red-700 rounded-md">
            {{ error }}
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
import {ref, onMounted, onBeforeUnmount} from 'vue';
import BasePagination from '@/components/BasePagination.vue';
import { useEvent } from "@/composables/useEvent";
import { formatDate, formatPrice } from "@/utils/formatter";
import SearchInput from "@/components/SearchComponent.vue";
import websocketService from "@/utils/websocket";
import {hasEventManagerRole} from "@/utils/roles";

export default {
  components: {
    SearchInput,
    BasePagination
  },
  setup() {
    const { error, loading, getEventsByManager, deleteEvent } = useEvent();

    const events = ref([]);
    const currentPage = ref(1);
    const rowsPerPage = ref(12);
    const totalElements = ref(0);
    const totalPages = ref(0);
    const sortConfig = ref({
      sortBy: 'dateOfEvent',
      direction: 'asc'
    });
    const searchQuery = ref('');

    const getThresholdClass = (event) => {
      const ratio = event.soldTickets / event.thresholdValue;
      if (ratio >= 1.1) {
        return 'text-emerald-500';
      } else if (ratio <= 0.9) {
        return 'text-red-500';
      }
      return '';
    };

    const getThresholdIcon = (event) => {
      const ratio = event.soldTickets / event.thresholdValue;
      if (ratio >= 1.1) {
        return 'fa-arrow-trend-up';
      } else if (ratio <= 0.9) {
        return 'fa-arrow-trend-down';
      }
      return 'fa-equals';
    };

    const getThresholdMessage = (event) => {
      const ratio = event.soldTickets / event.thresholdValue;
      const percentage = Math.abs((ratio - 1) * 100).toFixed(1);

      if (ratio >= 1.1) {
        return `${percentage}% über Schwellenwert`;
      } else if (ratio <= 0.9) {
        return `${percentage}% unter Schwellenwert`;
      }
      return 'Nahe am Schwellenwert';
    };

    const fetchEvents = async () => {
      try {
        const response = await getEventsByManager({
          page: currentPage.value - 1,
          size: rowsPerPage.value,
          sortBy: sortConfig.value.sortBy,
          direction: sortConfig.value.direction,
          search: searchQuery.value
        });

        events.value = response.content;
        totalElements.value = response.totalElements;
        totalPages.value = response.totalPages;
      } catch (err) {
        console.error("Error loading events:", err);
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

    const handleDelete = async (id) => {
      try {
        const confirmed = await confirm('Are you sure you want to delete this item?');

        if (confirmed) {
          await deleteEvent(id);
          events.value = events.value.filter(event => event.id !== id);

          if (events.value.length === 0 && currentPage.value > 1) {
            currentPage.value--;
            await fetchEvents();
          }
          totalElements.value--;
          totalPages.value = Math.ceil(totalElements.value / rowsPerPage.value);
        }
      } catch (error) {
        console.error("Error deleting event:", error);
      }
    };

    const setupWebSocketListeners = () => {
      // Handle new events
      const newEventUnsub = websocketService.on('newEvent', (newEvent) => {
        if (events.value.length < rowsPerPage.value) {
          events.value.unshift(newEvent);
          totalElements.value++;
          totalPages.value = Math.ceil(totalElements.value / rowsPerPage.value);
        }
      });

      const eventUpdateUnsub = websocketService.on('eventUpdate', (updatedEvent) => {
        const index = events.value.findIndex(event => event.id === updatedEvent.id);
        if (index !== -1) {
          events.value[index] = { ...events.value[index], ...updatedEvent };
        }
      });

      const notificationUnsub = websocketService.on('userNotification', (notification) => {
      });

      return [newEventUnsub, eventUpdateUnsub, notificationUnsub];
    };

    // Lifecycle hooks
    onMounted(async () => {
      await fetchEvents();

      try {
        await websocketService.connect();
        const unsubCallbacks = setupWebSocketListeners();

        onBeforeUnmount(() => {
          unsubCallbacks.forEach(cleanup => cleanup());
          websocketService.disconnect();
        });
      } catch (error) {
        console.error('Failed to connect to WebSocket:', error);
      }
    });

    return {
      events,
      currentPage,
      rowsPerPage,
      totalElements,
      totalPages,
      sortConfig,
      searchQuery,
      error,
      loading,

      // Methods
      formatPrice,
      formatDate,
      getThresholdClass,
      getThresholdIcon,
      getThresholdMessage,
      handlePageChange,
      handleSearch,
      handleDelete,
      fetchEvents,
        hasEventManagerRole
    };
  }
};
</script>