<template>
    <div class="p-6 w-full max-w-4xl mx-auto">
        <div v-if="loading" class="flex justify-center items-center">
            <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-500"></div>
        </div>

        <div v-else-if="error" class="p-4 bg-red-100 border border-red-400 text-red-700 rounded-md">
            {{ error }}
        </div>

        <div v-else-if="event" class="bg-white rounded-lg shadow-lg overflow-hidden">
            <div class="p-6 border-b border-gray-200 bg-gray-50">
                <h1 class="text-3xl font-bold text-gray-900">{{ event.title }}</h1>
            </div>

            <div class="p-6 space-y-6">
                <div class="grid grid-cols-1 md:grid-cols-2 gap-6 pl-6">
                    <div class="space-y-3">
                        <div class="flex items-center text-gray-600">
                            <i class="fas fa-calendar-alt mr-3 text-xl"></i>
                            <div>
                                <div class="text-lg">{{ formatDate(event.dateOfEvent) }}</div>
                                <div class="text-sm text-gray-500">Datum der Veranstaltung</div>
                            </div>
                        </div>
                    </div>

                    <div class="space-y-3">
                        <div class="flex items-center text-gray-600">
                            <i class="fas fa-map-marker-alt mr-3 text-xl"></i>
                            <div>
                                <div class="text-lg">{{ event.location }}</div>
                                <div class="text-sm text-gray-500">Veranstaltungsort</div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="bg-gray-50 rounded-lg p-6 space-y-3">
                    <div class="flex items-center justify-between">
                        <div class="space-y-1">
                            <div class="flex items-center font-semibold text-xl">
                                <i class="fas fa-tag mr-2"></i>
                                <span>{{ formatPrice(event.price) }}</span>
                            </div>
                            <div class="text-gray-500 text-sm ml-6">
                                Grundpreis: {{ formatPrice(event.basePrice) }}
                            </div>
                        </div>
                    </div>
                </div>

                <div class="bg-gray-50 rounded-lg p-6">
                    <h2 class="text-xl font-semibold mb-4">Ticket Status</h2>

                    <div class="space-y-6">
                        <div class="flex items-center justify-between">
                            <span class="text-gray-600">Verfügbare Tickets:</span>
                            <span
                                v-if="event.availableTickets > 0"
                                class="text-emerald-500 font-semibold text-lg"
                            >
                {{ event.availableTickets }} Tickets
              </span>
                            <span
                                v-else
                                class="text-red-500 font-semibold text-lg"
                            >
                Ausverkauft
              </span>
                        </div>

                        <div class="flex items-center justify-between">
                            <span class="text-gray-600">Verkaufte Tickets:</span>
                            <span class="font-semibold text-lg">{{ event.soldTickets }}</span>
                        </div>


                        <div class="flex justify-between text-gray-600">
                            <span>Schwellenwert:</span>
                            <span class="font-semibold">{{ event.thresholdValue }}</span>
                        </div>

                        <div class="flex items-center justify-between space-x-2">
                            <span class="text-sm font-medium font-semibold text-gray-600">Verkaufte vs. Schwellenwert</span>
                            <div class="flex items-center space-x-2" :class="getThresholdClass(event)">
                                <i class="fas mr-1 text-lg" :class="getThresholdIcon(event)"></i>
                                <span class="font-semibold">{{ getThresholdMessage(event) }}</span>
                            </div>
                        </div>
                        <div class="relative w-32 h-32 pl-6 ml-2 mt-2">
                            <svg class="w-full h-full -rotate-90">
                                <circle
                                    cx="64"
                                    cy="64"
                                    r="60"
                                    fill="none"
                                    stroke="#e5e7eb"
                                    stroke-width="8"
                                />
                                <circle
                                    cx="64"
                                    cy="64"
                                    r="60"
                                    fill="none"
                                    :stroke="pieChartColor"
                                    stroke-width="8"
                                    :stroke-dasharray="`${pieChartProgress} ${2 * Math.PI * 60}`"
                                    class="transition-all duration-500"
                                />
                            </svg>

                        </div>
                        <div class="text-sm text-gray-500 opacity-75">
                            Fortschritt zum Schwellenwert {{ Number(progressPercentage.toFixed(2)) }}%
                        </div>
                    </div>
                </div>
            </div>

            <!-- Action Buttons -->
            <div class="p-6 bg-gray-50 border-t border-gray-200 flex justify-end space-x-4">
                <router-link v-if="hasEventManagerRole"
                    :to="{ name: 'UpdateEventForm', params: { id: event.id } }"
                    class="text-white bg-blue-500 hover:bg-blue-600 px-6 py-2 rounded text-sm inline-flex items-center"
                >
                    <i class="fas fa-edit mr-2"></i>
                    Bearbeiten
                </router-link>

                <button
                    @click="handleDelete"
                    class="text-white bg-red-500 hover:bg-red-600 px-6 py-2 rounded text-sm inline-flex items-center"
                >
                    <i class="fas fa-trash mr-2"></i>
                    Löschen
                </button>
            </div>
        </div>
    </div>
</template>

<script>
import { ref, onMounted, onBeforeUnmount, computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useEvent } from "@/composables/useEvent";
import { formatDate, formatPrice } from "@/utils/formatter";
import websocketService from "@/utils/websocket";
import {hasEventManagerRole} from "@/utils/roles";

export default {
    name: 'SingleEventView',


    setup() {
        const route = useRoute();
        const router = useRouter();
        const { error, loading, getEvent, deleteEvent } = useEvent();
        const event = ref(null);

        // New computed properties for visualizations
        const progressPercentage = computed(() => {
            if (!event.value) return 0;
            return (event.value.soldTickets / event.value.thresholdValue) * 100;
        });

        const progressBarColor = computed(() => {
            const percentage = progressPercentage.value;
            if (percentage >= 110) return 'bg-emerald-500';
            if (percentage >= 90) return 'bg-blue-500';
            return 'bg-red-500';
        });

        const pieChartColor = computed(() => {
            const percentage = progressPercentage.value;
            if (percentage >= 110) return '#10B981';
            if (percentage >= 90) return '#3B82F6';
            return '#EF4444';
        });

        const pieChartProgress = computed(() => {
            const circumference = 2 * Math.PI * 60;
            return (progressPercentage.value / 100) * circumference;
        });

        const getThresholdClass = (event) => {
            const ratio = event.soldTickets / event.thresholdValue;
            if (ratio >= 1.1) {
                return 'text-emerald-500';
            } else if (ratio <= 0.9) {
                return 'text-red-500';
            }
            return 'text-blue-500';
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

        const fetchEvent = async () => {
            try {
                event.value = await getEvent(route.params.id);
            } catch (err) {
                console.error("Error loading event:", err);
            }
        };

        const handleDelete = async () => {
            try {
                const confirmed = await confirm('Sind Sie sicher, dass Sie diese Veranstaltung löschen möchten?');

                if (confirmed) {
                    await deleteEvent(route.params.id);
                    router.push({ name: 'EventCreatorEvents' });
                }
            } catch (error) {
                console.error("Error deleting event:", error);
            }
        };

        const setupWebSocketListener = () => {
            const eventUpdateUnsub = websocketService.on('eventUpdate', (updatedEvent) => {
                if (updatedEvent.id.toString() === route.params.id.toString()) {
                    event.value = { ...event.value, ...updatedEvent };
                }
            });

            return eventUpdateUnsub;
        };

        onMounted(async () => {
            await fetchEvent();

            try {
                await websocketService.connect();
                const unsubCallback = setupWebSocketListener();

                onBeforeUnmount(() => {
                    unsubCallback();
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
            getThresholdClass,
            getThresholdIcon,
            getThresholdMessage,
            handleDelete,
            // New visualization properties
            progressPercentage,
            progressBarColor,
            pieChartColor,
            pieChartProgress,
            hasEventManagerRole
        };
    }
};
</script>