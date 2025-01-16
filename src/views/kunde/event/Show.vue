<template>
    <div class="p-6 w-full max-w-4xl mx-auto">
        <div v-if="loading" class="flex justify-center items-center">
            <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-500"></div>
        </div>

        <div v-else-if="error" class="p-4 bg-red-100 border border-red-400 text-red-700 rounded-md">
            {{ error }}
        </div>

        <div v-else-if="event" class="bg-white rounded-lg shadow-lg overflow-hidden">
            <!-- Header Section -->
            <div class="bg-blue-500 text-white p-6">
                <h1 class="text-3xl font-bold text-center break-words">{{ event.title }}</h1>
            </div>

            <!-- Main Content Section -->
            <div class="p-6 space-y-6">
                <!-- Date and Location Section -->
                <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                    <div class="space-y-3">
                        <div class="flex items-center text-gray-600 justify-center md:justify-start">
                            <i class="fas fa-calendar-alt mr-3 text-xl"></i>
                            <div>
                                <div class="text-lg break-words">{{ formatDate(event.dateOfEvent) }}</div>
                                <div class="text-sm text-gray-500">Datum der Veranstaltung</div>
                            </div>
                        </div>
                    </div>

                    <div class="space-y-3">
                        <div class="flex items-center text-gray-600 justify-center md:justify-start">
                            <i class="fas fa-map-marker-alt mr-3 text-xl"></i>
                            <div>
                                <div class="text-lg break-words">{{ event.location }}</div>
                                <div class="text-sm text-gray-500">Veranstaltungsort</div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Price and Ticket Information -->
                <div class="bg-gray-50 rounded-lg p-6 space-y-4">
                    <!-- Price Display -->
                    <div class="flex justify-center items-center">
                        <div class="text-center">
                            <div class="text-3xl font-bold text-gray-900 break-words">
                                {{ formatPrice(event.price) }}
                            </div>
                            <div class="text-gray-500 text-sm mt-1">Ticketpreis</div>
                        </div>
                    </div>

                    <!-- Ticket Availability -->
                    <div class="flex justify-center items-center mt-4">
            <span
                    v-if="event.availableTickets > 0"
                    class="text-emerald-500 font-semibold text-xl break-words"
            >
              {{ event.availableTickets }} Tickets verfügbar
            </span>
                        <span
                                v-else
                                class="text-red-500 font-semibold text-xl break-words"
                        >
              Ausverkauft
            </span>
                    </div>
                </div>

                <!-- Action Buttons -->
                <div class="flex flex-col sm:flex-row justify-center items-center space-y-4 sm:space-y-0 sm:space-x-4">
                    <router-link
                            v-if="event.availableTickets > 0"
                            :to="{ name: 'PurchaseTicket', params: { id: event.id } }"
                            class="w-full sm:w-auto px-6 py-3 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition-colors duration-200 flex items-center justify-center"
                    >
                        <i class="fas fa-shopping-cart mr-2"></i>
                        Ticket kaufen
                    </router-link>

                    <button
                            v-if="event.hasVoucher"
                            @click="useVoucher"
                            class="w-full sm:w-auto px-6 py-3 bg-green-500 text-white rounded-lg hover:bg-green-600 transition-colors duration-200 flex items-center justify-center"
                    >
                        <i class="fas fa-gift mr-2"></i>
                        Gutschein einlösen
                    </button>
                </div>
            </div>
        </div>
    </div>
</template>
<script>
import { ref, onMounted, onBeforeUnmount } from 'vue';
import { useRoute } from 'vue-router';
import { useEvent } from "@/composables/useEvent";
import { formatDate, formatPrice } from "@/utils/formatter";
import websocketService from "@/utils/websocket";

export default {
  name: 'CustomerEventView',

  setup() {
    const route = useRoute();
    const { error, loading, getEvent } = useEvent();
    const event = ref(null);

    const useVoucher = () => {
      if (event.value?.hasVoucher) {
        const discount = 10;
        event.value.price -= discount;
        alert(`Gutschein eingelöst! Neuer Preis: ${formatPrice(event.value.price)}`);
        event.value.hasVoucher = false;
      }
    };

    const fetchEvent = async () => {
      try {
        event.value = await getEvent(route.params.id);
      } catch (err) {
        console.error("Error loading event:", err);
      }
    };

    const setupWebSocketListener = () => {
      const eventUpdateUnsub = websocketService.on('eventUpdate', (updatedEvent) => {
        if (updatedEvent.id.toString() === route.params.id.toString()) {

          event.value = updatedEvent;
        }

      });

      const notificationUnsub = websocketService.on('userNotification', (notification) => {
        console.log('Received notification:', notification);
      });

      return [eventUpdateUnsub, notificationUnsub];
    };

    onMounted(async () => {
      await fetchEvent();

      try {
        await websocketService.connect();
        const unsubCallbacks = setupWebSocketListener();

        onBeforeUnmount(() => {
          unsubCallbacks.forEach(cleanup => cleanup());
          websocketService.disconnect();
        });
      } catch (error) {
        console.error('Failed to connect to WebSocket:', error);
      }
    });

    return {
      event,
      error,
      loading,
      formatDate,
      formatPrice,
      useVoucher
    };
  }
};
</script>