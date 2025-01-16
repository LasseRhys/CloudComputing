<template>
    <div class="min-h-screen bg-gray-100">
        <div class="container mx-auto px-4 sm:px-6 lg:px-8 max-w-7xl">
            <!-- Loading State -->
            <div v-if="loading" class="flex justify-center items-center py-16">
                <div class="animate-spin rounded-full h-12 w-12 border-b-4 border-indigo-600"></div>
            </div>

            <div class="bg-white shadow-xl rounded-md overflow-hidden">
                <div class="px-6 py-8 space-y-8">
                    <div class="bg-white rounded-xl shadow-lg p-6 border border-gray-100">
                        <h3 class="text-xl font-semibold text-gray-900 mb-6">Veranstaltungsdetails</h3>
                        <div class="grid grid-cols-1 gap-6 sm:grid-cols-3">
                            <div class="space-y-2">
                                <label class="block text-sm font-medium text-gray-500">Veranstaltungsname</label>
                                <p class="text-lg font-medium text-gray-900">{{ event?.title }}</p>
                            </div>
                            <div class="space-y-2">
                                <label class="block text-sm font-medium text-gray-500">Datum</label>
                                <p class="text-lg font-medium text-gray-900">{{ event?.dateOfEvent || 'Wird bekannt gegeben' }}</p>
                            </div>
                            <div class="space-y-2">
                                <label class="block text-sm font-medium text-gray-500">Preis</label>
                                <p class="text-lg font-medium text-indigo-600">{{ formatPrice(event?.price) }}</p>
                            </div>
                            <div class="space-y-2">
                                <label class="block text-sm font-medium text-gray-500">Verfügbare Tickets</label>
                                <p class="text-lg font-medium text-red-500" :class="{'text-amber-600': event?.availableTickets <= 10, 'text-red-600': event?.availableTickets <= 5}">
                                    {{ event?.availableTickets || 0 }}
                                </p>
                            </div>
                        </div>
                    </div>

                    <!-- Customer Details -->
                    <div class="bg-white rounded-xl shadow-lg p-6 border border-gray-100">
                        <h3 class="text-xl font-semibold text-gray-900 mb-6">Kundendetails</h3>
                        <div v-if="customerLoading" class="animate-pulse flex space-x-4">
                            <div class="flex-1 space-y-4 py-1">
                                <div class="h-4 bg-gray-200 rounded w-3/4"></div>
                                <div class="space-y-2">
                                    <div class="h-4 bg-gray-200 rounded"></div>
                                    <div class="h-4 bg-gray-200 rounded w-5/6"></div>
                                </div>
                            </div>
                        </div>
                        <div v-else-if="customerError" class="text-sm text-red-600">
                            {{ customerError }}
                        </div>
                        <div v-else-if="customer" class="grid grid-cols-1 gap-6 sm:grid-cols-3">
                            <div class="space-y-2">
                                <label class="block text-sm font-medium text-gray-500">Kundennummer</label>
                                <p class="text-lg font-medium text-gray-900">{{ customer.customerNumber }}</p>
                            </div>
                            <div class="space-y-2">
                                <label class="block text-sm font-medium text-gray-500">Vollständiger Name</label>
                                <p class="text-lg font-medium text-gray-900">{{ customer.user.firstName }} {{ customer.user.lastName }}</p>
                            </div>
                            <div class="space-y-2">
                                <label class="block text-sm font-medium text-gray-500">E-Mail</label>
                                <p class="text-lg font-medium text-gray-900">{{ customer.user.email }}</p>
                            </div>
                        </div>
                    </div>

                    <!-- Voucher Section -->
                    <div class="bg-white rounded-xl shadow-lg p-6 border border-gray-100">
                        <h3 class="text-xl font-semibold text-gray-900 mb-6">Gutscheine</h3>

                        <!-- Available Vouchers -->
                        <div class="mt-6" v-if="vouchers && vouchers.length > 0">
                            <label class="block text-sm font-medium text-gray-500 mb-4">Verfügbare Gutscheine</label>
                            <div class="space-y-3">
                                <div v-for="voucher in unusedVouchers" :key="voucher.voucherCode"
                                     class="flex items-center p-4 border rounded-lg hover:bg-gray-50 transition-colors duration-150 shadow-sm">
                                    <input
                                            type="checkbox"
                                            :value="voucher.voucherCode"
                                            v-model="formData.voucherCodes"
                                            class="h-5 w-5 text-indigo-600 focus:ring-indigo-500 border-gray-300 rounded"
                                    />
                                    <label class="ml-4 flex items-center justify-between flex-grow">
                                        <span class="text-sm ml-2 font-medium text-gray-900">{{ voucher.voucherCode }}</span>
                                        <span class="text-sm font-semibold text-indigo-600">{{ voucher.amount }}$</span>
                                    </label>
                                </div>
                            </div>
                        </div>

                        <!-- Manual Voucher Entry -->
                        <div class="mt-6">
                            <label class="block text-sm font-medium text-gray-500 mb-2">Gutscheincode hinzufügen</label>
                            <div class="flex rounded-md shadow-sm">
                                <input
                                        v-model="newVoucherCode"
                                        type="text"
                                        class="flex-1 min-w-0 block w-full px-4 py-3 rounded-l-md border border-gray-300 focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                                        placeholder="Gutscheincode eingeben"
                                />
                                <button
                                        @click="addVoucherCode"
                                        type="button"
                                        class="inline-flex items-center px-6 py-3 border border-transparent text-sm font-medium rounded-r-md text-white bg-indigo-500 hover:bg-blue-600 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 transition-colors duration-150"
                                >
                                    Einlösen
                                </button>
                            </div>
                            <p v-if="voucherError" class="mt-2 text-sm text-red-600">{{ voucherError }}</p>
                        </div>

                        <!-- Selected Vouchers -->
                        <div v-if="formData.voucherCodes.length > 0" class="mt-6">
                            <label class="block text-sm font-medium text-gray-500 mb-3">Ausgewählte Gutscheine</label>
                            <div class="space-y-2">
                                <div v-for="code in formData.voucherCodes" :key="code"
                                     class="flex justify-between items-center p-3 bg-indigo-50 rounded-lg border border-indigo-100">
                                    <div class="flex items-center space-x-3">
                                        <span class="text-sm font-medium text-indigo-900">{{ code }}</span>
                                        <span class="text-sm font-semibold text-indigo-600 ml-2">{{ getVoucherAmount(code) }}$</span>
                                    </div>
                                    <button
                                            @click="removeVoucherCode(code)"
                                            class="text-sm font-medium text-indigo-600 hover:text-indigo-800 transition-colors duration-150"
                                    >
                                        Entfernen
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="bg-white rounded-xl shadow-lg p-6 border border-gray-100">
                        <div class="grid grid-cols-1 gap-6 sm:grid-cols-2">
                            <div class="space-y-3">
                                <label for="ticketQuantity" class="block text-sm font-medium text-gray-500">Anzahl der Tickets</label>
                                <input
                                        id="ticketQuantity"
                                        type="number"
                                        min="1"
                                        v-model.number="formData.amount"
                                        @beforeinput="preventNonNumeric"
                                        class="block w-full px-4 py-3 rounded-md border border-gray-300 focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                                        required
                                />
                            </div>
                            <div class="space-y-4">
                                <div class="bg-gray-50 p-4 rounded-lg">
                                    <p class="text-sm text-gray-500">Zwischensumme: <span class="float-right font-medium text-gray-900">{{ formatPrice(calculateSubtotal) }}</span></p>
                                    <p v-if="totalVoucherAmount > 0" class="text-sm text-indigo-600">
                                        Gutschein Rabatt: <span class="float-right font-medium">-{{ formatPrice(totalVoucherAmount) }}</span>
                                    </p>
                                    <div class="pt-2 border-t border-gray-200">
                                        <p class="text-lg font-semibold text-gray-900">
                                            Gesamtsumme: <span class="float-right">{{ formatPrice(calculateTotal) }}</span>
                                        </p>
                                    </div>
                                </div>

                                <div
                                    class="mt-3 transition-all duration-200"
                                >
                                    <div class="grid grid-cols-1 gap-6 sm:grid-cols-2">
                                        <button v-if="formData.amount > 0"
                                            @click="isTicketDetailsOpen = !isTicketDetailsOpen"
                                            class="flex items-center gap-2 mb-4 text-gray-700 hover:text-gray-900"
                                        >
                                            <span class="text-sm font-medium">Ticket Details</span>
                                            <svg
                                                class="w-5 h-5 transition-transform duration-200"
                                                :class="{ 'rotate-180': isTicketDetailsOpen }"
                                                xmlns="http://www.w3.org/2000/svg"
                                                viewBox="0 0 24 24"
                                                fill="none"
                                                stroke="currentColor"
                                                stroke-width="2"
                                                stroke-linecap="round"
                                                stroke-linejoin="round"
                                            >
                                                <polyline points="6 9 12 15 18 9"></polyline>
                                            </svg>
                                        </button>

                                            <div v-if="formData.amount > 0 && isTicketDetailsOpen" class="bg-white border border-gray-200 rounded-lg p-4">

                                                <h4 class="text-sm font-medium text-gray-700 mb-3">Preisaufschlüsselung für Tickets</h4>
                                                <div class="space-y-4">
                                                    <div v-for="index in formData.amount" :key="index">
                                                        <div class="relative">
                                                            <div class="flex mb-2 items-center justify-between">
                                                                <div class="text-xs font-semibold text-gray-700">
                                                                    Ticket {{ index }}
                                                                </div>
                                                                <div class="text-xs font-semibold" :class="getTicketPriceColor(index)">
                                                                    {{ formatPrice(getTicketPrice(index)) }}
                                                                </div>
                                                            </div>
                                                            <div class="overflow-hidden h-2 text-xs flex rounded bg-gray-200">
                                                                <div class="bg-indigo-500" :style="{ width: `${getTicketPricePercentage(index)}%` }"></div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>

                                                <!-- Savings Summary -->
                                                <div v-if="totalVoucherAmount > 0" class="mt-4 bg-indigo-50 p-3 rounded-md">
                                                    <p class="text-xs text-indigo-700">
                                                        Gesamtersparnis: {{ formatPrice(totalVoucherAmount) }}
                                                        ({{ Math.round((totalVoucherAmount / calculateSubtotal) * 100) }}%)
                                                    </p>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                            <div v-if="showSuccess" class="mt-4 mb-2 p-4 bg-emerald-200 border border-emerald-400 text-green-700 rounded mx-4">
                        <div class="flex items-center">
                            <svg class="w-5 h-5 mr-2" fill="currentColor" viewBox="0 0 20 20">
                                <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd"/>
                            </svg>
                            <p class="font-medium">Erfolg! Tickets wurden erfolgreich gekauft.</p>
                        </div>
                    </div>

                    <div v-else-if="error" class="bg-red-50 border-l-4 border-red-400 shadow-lg rounded-lg p-6 mt-4">
                        <div class="flex items-center">
                            <div class="flex-shrink-0">
                                <svg class="h-5 w-5 text-red-400" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor">
                                    <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd"/>
                                </svg>
                            </div>
                            <div class="ml-3">
                                <h3 class="text-sm font-medium text-red-800">Fehler beim Laden der Veranstaltung</h3>
                                <div class="mt-2 text-sm text-red-700">{{ error }}</div>
                            </div>
                        </div>
                    </div>

                    <div v-if="purchaseError" class="mt-6">
                        <div class="bg-red-50 border-l-4 border-red-400 p-4 rounded-lg shadow-md">
                            <div class="flex">
                                <div class="ml-3">
                                    <h3 class="text-sm font-medium text-red-500">Kauf fehlgeschlagen</h3>
                                    <div class="mt-2 text-sm text-red-500">{{ purchaseError }}</div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="flex justify-end space-x-4 pt-6">
                        <button
                                type="button"
                                :disabled="purchaseLoading"
                                @click="router().push('/events')"
                                class="px-6 py-3 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 transition-colors duration-150"
                        >
                            Abbrechen
                        </button>
                        <button
                                type="submit"
                                :disabled="purchaseLoading"
                                @click="handlePurchase"
                                class="px-6 py-3 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-500 hover:bg-blue-600 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 transition-colors duration-150 disabled:opacity-50"
                        >
                            <span v-if="purchaseLoading">Wird verarbeitet...</span>
                            <span v-else>Tickets kaufen</span>
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>
<script>
import { onMounted, ref, computed, onBeforeUnmount } from 'vue'
import { useEvent } from '@/composables/useEvent'
import { useUser } from '@/composables/useUser'
import { useVoucher } from '@/composables/useVoucher'
import { createRouter as $router, useRoute } from 'vue-router'
import { useTickets } from "@/composables/useTickets"
import router from "@/router"
import websocketService from '@/utils/websocket'
import {formatPrice, preventNonNumeric} from "@/utils/formatter";

export default {
  name: 'PurchaseTicket',

  setup() {
    const route = useRoute()
    const event = ref(null)
    const customer = ref(null)
    const vouchers = ref([])
    const newVoucherCode = ref('')
    const purchaseLoading = ref(false)
    const purchaseError = ref('')
    const showSuccess = ref(false);
    const isTicketDetailsOpen = ref(true)

    const { error, loading, getEvent } = useEvent()
    const { error: customerError, loading: customerLoading, getCurrentCustomer } = useUser()
    const { error: voucherError, loading: voucherLoading, getUserVoucherAll, validateVoucher } = useVoucher()
    const { purchaseTicket } = useTickets()

    const eventId = route.params.id

    let unsubscribeFromEventUpdates = null

    const handleEventUpdate = (updatedEvent) => {

      if (updatedEvent.id.toString() === eventId) {
        console.log(event)
        event.value = updatedEvent
      }
    }

    const setupWebSocket = async () => {
      try {
        if (!websocketService.isConnected) {
          await websocketService.connect()
        }

        unsubscribeFromEventUpdates = websocketService.on('eventUpdate', handleEventUpdate)

      } catch (error) {
        console.error('Failed to setup WebSocket connection:', error)
      }
    }

    onMounted(async () => {
      if (route.params.id) {
        try {
          event.value = await getEvent(eventId)
          await setupWebSocket()
        } catch (err) {
          console.error('Error fetching event:', err)
        }
        try {
          vouchers.value = await getUserVoucherAll()
        } catch (err) {
          console.error('Error fetching vouchers:', err)
        }
        try {
          customer.value = await getCurrentCustomer()
        } catch (err) {
          console.error('Error fetching Customer:', err)
        }
      }
    })

    onBeforeUnmount(() => {
      if (unsubscribeFromEventUpdates) {
        unsubscribeFromEventUpdates()
      }
    })

    const formData = ref({
      amount: 0,
      voucherCodes: [],
    })



    const totalVoucherAmount = computed(() => {
      return formData.value.voucherCodes.reduce((total, code) => {
        const voucher = vouchers.value.find(v => v.voucherCode === code)
        return total + (voucher?.amount || 0)
      }, 0)
    })

    const calculateTotal = computed(() => {
      const subtotal = calculateSubtotal.value
      const discount = totalVoucherAmount.value
      return Math.max(0, subtotal - discount)
    })

    const unusedVouchers = computed(() => {
      return vouchers.value.filter(voucher => !voucher.used)
    })

    const getBaseTicketPrice = (ticketIndex) => {
      if (!event.value) return 0;

      const basePrice = event.value.basePrice;
      const threshold = event.value.thresholdValue;
      const availableTickets = event.value.availableTickets;
      const soldTickets = event.value.soldTickets;

      // Calculate what number ticket this will be overall
      const ticketNumber = soldTickets + ticketIndex;

      // Apply 20% increase if beyond threshold
      if (ticketNumber > threshold) {
        return basePrice * 1.2;
      }

      return basePrice;
    };

    const getTicketPrice = (ticketIndex) => {
      const originalPrice = getBaseTicketPrice(ticketIndex);
      if (!originalPrice) return 0;

      let remainingVouchers = [...formData.value.voucherCodes].map(code => {
        const voucher = vouchers.value.find(v => v.voucherCode === code);
        return voucher?.amount || 0;
      });

      let currentTicketPrice = originalPrice;

      // Apply vouchers to previous tickets
      for (let i = 1; i < ticketIndex; i++) {
        let tempPrice = getBaseTicketPrice(i);
        for (let j = 0; j < remainingVouchers.length; j++) {
          if (remainingVouchers[j] >= tempPrice) {
            remainingVouchers[j] -= tempPrice;
            tempPrice = 0;
            break;
          } else {
            tempPrice -= remainingVouchers[j];
            remainingVouchers[j] = 0;
          }
        }
        remainingVouchers = remainingVouchers.filter(amount => amount > 0);
      }

      // Calculate price for current ticket with remaining vouchers
      for (let i = 0; i < remainingVouchers.length && currentTicketPrice > 0; i++) {
        if (remainingVouchers[i] >= currentTicketPrice) {
          currentTicketPrice = 0;
          break;
        } else {
          currentTicketPrice -= remainingVouchers[i];
        }
      }

      return currentTicketPrice;
    };

    const calculateSubtotal = computed(() => {
      if (!event.value?.price || !formData.value.amount) return 0;

      let total = 0;
      for (let i = 1; i <= formData.value.amount; i++) {
        total += getBaseTicketPrice(i);
      }
      return total;
    });

    const getTicketPricePercentage = (index) => {
      const originalPrice = event.value?.price || 0;
      const ticketPrice = getTicketPrice(index);
      return (ticketPrice / originalPrice) * 100;
    };

    const getTicketPriceColor = (index) => {
      const basePrice = event.value?.price || 0;
      const ticketPrice = getTicketPrice(index);
      if (ticketPrice === 0) return 'text-emerald-600';
      if (ticketPrice < basePrice) return 'text-indigo-600';
      if (ticketPrice > basePrice) return 'text-amber-600';
      return 'text-gray-700';
    };


    const addVoucherCode = async () => {
      if (!newVoucherCode.value) return;

      try {
        const validatedVoucher = await validateVoucher(newVoucherCode.value);

        if (validatedVoucher && !formData.value.voucherCodes.includes(newVoucherCode.value)) {
          formData.value.voucherCodes.push(newVoucherCode.value);
          const existingVoucherIndex = vouchers.value.findIndex(v => v.voucherCode === newVoucherCode.value);
          if (existingVoucherIndex >= 0) {
            vouchers.value[existingVoucherIndex].amount = validatedVoucher.amount;
          } else {
            vouchers.value.push({
              voucherCode: newVoucherCode.value,
              amount: validatedVoucher.amount,
              used: false
            });
          }
          newVoucherCode.value = '';
          voucherError.value = '';
        }
      } catch (err) {
        voucherError.value = err.response?.data?.error || 'Invalid voucher code';
      }
    }

    const removeVoucherCode = (code) => {
      formData.value.voucherCodes = formData.value.voucherCodes.filter(c => c !== code)
    }

    const getVoucherAmount = (code) => {
      const voucher = vouchers.value.find(v => v.voucherCode === code);
      return voucher?.amount || 0;
    }

    const handlePurchase = async () => {
      purchaseLoading.value = true;
      purchaseError.value = '';

      try {
        if (!formData.value.amount || formData.value.amount < 1) {
          throw new Error('Please select at least one ticket');
        }

        const purchaseData = {
          eventId: eventId,
          amount: formData.value.amount,
          voucherCodes: [...formData.value.voucherCodes]
        };

        const response = await purchaseTicket(purchaseData);
        if (response) {
          showSuccess.value = true;

          await new Promise(resolve => setTimeout(resolve, 1000));

          showSuccess.value = false;
          router.push({
            path: '/notifications',
            query: {
              purchase: 'success',
              reference: response.reference
            }
          });
        }

      } catch (err) {
        purchaseError.value = err.response?.data?.error || 'Failed to complete purchase. Please try again.';
      } finally {
        purchaseLoading.value = false;
      }
    }

    return {
      error,
      loading,
      customerError,
      customerLoading,
      voucherError,
      voucherLoading,
      event,
      customer,
      vouchers,
      formData,
      newVoucherCode,
      unusedVouchers,
      addVoucherCode,
      removeVoucherCode,
      calculateTotal,
      calculateSubtotal,
      totalVoucherAmount,
      handlePurchase,
      purchaseLoading,
      purchaseError,
      getVoucherAmount,
      showSuccess,
      isTicketDetailsOpen,
      getTicketPrice,
      getTicketPricePercentage,
      getTicketPriceColor,

    }
  },

  methods: {
    formatPrice,
    preventNonNumeric,

    router() {
      return router
    },
    $router,
    resetForm() {
      this.formData = {
        amount: 0,
        voucherCodes: [],
      }
      this.error = ''
    }
  }
}
</script>