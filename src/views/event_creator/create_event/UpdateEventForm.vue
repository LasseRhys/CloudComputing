<template>
  <div class="flex items-center justify-center">
    <div class="w-full px-4">
      <div class="relative flex flex-col min-w-0 break-words w-full mb-6 shadow-lg rounded-lg bg-blueGray-100 border-0">
        <div class="rounded-t bg-white mb-0 px-6 py-6">
          <div class="text-center">
            <h6 class="text-blueGray-800 text-xl font-bold">Update Event</h6>
          </div>
        </div>
        <div v-if="eventError" class="mt-4 p-4  text-red-500 rounded">
          <p class="font-medium">Error</p>
          <p>{{ eventError }}</p>
        </div>
        <div v-if="showSuccess" class="mt-4 mb-2 p-4 bg-emerald-200 border border-emerald-400 text-green-700 rounded mx-4">
          <div class="flex items-center">
            <svg class="w-5 h-5 mr-2" fill="currentColor" viewBox="0 0 20 20">
              <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd"/>
            </svg>
            <p class="font-medium">Success! Event has been updated successfully.</p>
          </div>
        </div>

        <div class="flex-auto px-4 lg:px-10 py-10 pt-0">
          <form @submit.prevent="handleSubmit">
            <h6 class="text-blueGray-400 text-sm mt-3 mb-6 font-bold uppercase">
              Event Information
            </h6>
            <div class="flex flex-wrap">
              <div class="w-full lg:w-6/12 px-4">
                <div class="relative w-full mb-3">
                  <label
                      class="block uppercase text-blueGray-600 text-xs font-bold mb-2"
                      for="title"
                  >
                    Titel
                  </label>
                  <input
                      id="title"
                      type="text"
                      v-model="formData.title"
                      maxlength="100"
                      required
                      class="border-0 px-3 py-3 text-blueGray-600 bg-white rounded text-sm shadow focus:outline-none focus:ring w-full ease-linear transition-all duration-150"
                  />
                </div>
              </div>
              <div class="w-full lg:w-6/12 px-4">
                <div class="relative w-full mb-3">
                  <label
                      class="block uppercase text-blueGray-600 text-xs font-bold mb-2"
                      for="location"
                  >
                    Ort
                  </label>
                  <input
                      id="location"
                      type="text"
                      v-model="formData.location"
                      required
                      class="border-0 px-3 py-3 text-blueGray-600 bg-white rounded text-sm shadow focus:outline-none focus:ring w-full ease-linear transition-all duration-150"
                  />
                </div>
              </div>
              <div class="w-full lg:w-6/12 px-4">
                <div class="relative w-full mb-3">
                  <label
                      class="block uppercase text-blueGray-600 text-xs font-bold mb-2"
                      for="date"
                  >
                    Datum & Zeit
                  </label>
                  <input
                      id="date"
                      type="datetime-local"
                      v-model="formData.dateOfEvent"
                      :min="minDateTime"
                      :max="maxDateTime"
                      required
                      class="border-0 px-3 py-3 text-blueGray-600 bg-white rounded text-sm shadow focus:outline-none focus:ring w-full ease-linear transition-all duration-150 cursor-pointer"
                  />
                  <p v-if="dateError" class="text-red-500 text-xs mt-1">
                    {{ dateError }}
                  </p>
                </div>
              </div>
              <div class="w-full lg:w-6/12 px-4">
                <div class="relative w-full mb-3">
                  <label
                      class="block uppercase text-blueGray-600 text-xs font-bold mb-2"
                      for="tickets"
                  >
                    Anzahl an Tickets
                  </label>
                  <input
                      id="tickets"
                      type="number"
                      v-model.number="formData.totalTickets"
                      @beforeinput="preventNonNumeric"
                      min="1"
                      max="100000"
                      required
                      class="border-0 px-3 py-3 text-blueGray-600 bg-white rounded text-sm shadow focus:outline-none focus:ring w-full ease-linear transition-all duration-150"
                  />
                </div>
              </div>
              <div class="w-full lg:w-6/12 px-4">
                <div class="relative w-full mb-3">
                  <label
                      class="block uppercase text-blueGray-600 text-xs font-bold mb-2"
                      for="threshold"
                  >
                    Schwellenwert
                  </label>
                  <input
                      id="threshold"
                      type="number"
                      v-model.number="formData.thresholdValue"
                      @beforeinput="preventNonNumeric"
                      min="0"
                      required
                      class="border-0 px-3 py-3 text-blueGray-600 bg-white rounded text-sm shadow focus:outline-none focus:ring w-full ease-linear transition-all duration-150"
                  />
                </div>
              </div>
              <div class="w-full lg:w-6/12 px-4">
                <div class="relative w-full mb-3">
                  <label
                      class="block uppercase text-blueGray-600 text-xs font-bold mb-2"
                      for="price"
                  >
                    Basis Preis
                  </label>
                  <input
                      id="price"
                      type="number"
                      v-model.number="formData.basePrice"
                      @beforeinput="preventNonNumeric"
                      min="0.01"
                      step="0.01"
                      required
                      class="border-0 px-3 py-3 text-blueGray-600 bg-white rounded text-sm shadow focus:outline-none focus:ring w-full ease-linear transition-all duration-150"
                  />
                  <p class="text-xs text-blueGray-400 mt-1">
                    Finaler Preis: {{ formatPrice(finalPrice) }}
                  </p>
                </div>
              </div>
              <div class="w-full lg:w-6/12 px-4">
                <div class="relative w-full mb-3">
                  <label
                      class="block uppercase text-blueGray-600 text-xs font-bold mb-2"
                      for="managers"
                  >
                    Event Managers
                  </label>
                  <select
                      id="managers"
                      v-model="formData.managerIds"
                      multiple
                      required
                      class="border-0 px-3 py-3 text-blueGray-600 bg-white rounded text-sm shadow focus:outline-none focus:ring w-full ease-linear transition-all duration-150 h-32"
                  >
                    <option v-for="manager in eventManagers" :key="manager.user.id" :value="manager.user.id">
                      {{ manager.user.firstName }} {{ manager.user.lastName }} ({{ manager.employeeNumber }})
                    </option>
                  </select>
                  <p class="text-xs text-blueGray-400 mt-1">Hold Ctrl/Cmd to select multiple managers</p>
                </div>
              </div>
            </div>

            <div class="mt-6 text-right">
              <button
                  type="submit"
                  class="bg-emerald-500 text-white active:bg-emerald-600 font-bold uppercase text-xs px-4 py-2 rounded shadow hover:shadow-md outline-none focus:outline-none mr-2 ease-linear transition-all duration-150"
              >
                Update Event
              </button>
              <button
                  type="button"
                  @click="$router.back()"
                  class="bg-blueGray-600 text-white active:bg-blueGray-600 font-bold uppercase text-xs px-4 py-2 rounded shadow hover:shadow-md outline-none focus:outline-none ease-linear transition-all duration-150"
              >
                Abbrechen
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { useManager } from "@/composables/useManager";
import { useEvent } from "@/composables/useEvent";
import {onMounted, ref, computed, watch} from 'vue';
import { useRoute } from 'vue-router';
import {formatPrice, preventNonNumeric} from "../../../utils/formatter";

export default {
  name: 'UpdateEventForm',
  methods: {preventNonNumeric, formatPrice},
  setup() {
    const route = useRoute();
    const eventManagers = ref([]);
    const dateError = ref('');
    const { error: managerError, loading: managerLoading, getEventManagers } = useManager();
    const { error: eventError, loading: eventLoading, getEventManaged, updateEvent } = useEvent();
    const showSuccess = ref(false);

    const formData = ref({
      id: '',
      title: '',
      location: '',
      dateOfEvent: '',
      totalTickets: null,
      thresholdValue: 0,
      basePrice: null,
      price: 0,
      managerIds: []
    });

    const finalPrice = computed(() => {
      const basePrice = formData.value.basePrice || 0;
      const isAboveThreshold = formData.value.totalTickets > formData.value.thresholdValue;
      const THRESHOLD_MULTIPLIER = 1.2;
      return isAboveThreshold ? basePrice * THRESHOLD_MULTIPLIER : basePrice;
    });

    watch([
      () => formData.value.totalTickets,
      () => formData.value.thresholdValue,
      () => formData.value.basePrice
    ], () => {
    });

    const minDateTime = computed(() => {
      const now = new Date();
      now.setFullYear(now.getFullYear());
      now.setMinutes(now.getMinutes() - now.getTimezoneOffset());
      return now.toISOString().slice(0, 16);
    });

    // Set max date to 3 years from now
    const maxDateTime = computed(() => {
      const maxDate = new Date();
      maxDate.setFullYear(maxDate.getFullYear() + 3);
      maxDate.setMinutes(maxDate.getMinutes() - maxDate.getTimezoneOffset());
      return maxDate.toISOString().slice(0, 16);
    });

    const fetchEventManagers = async () => {
      try {
        eventManagers.value = await getEventManagers();
        console.log(eventManagers)
      } catch (err) {
        console.error('Error fetching event managers:', err);
      }
    };

    const fetchEvent = async () => {
      try {
        const eventData = await getEventManaged(route.params.id);
        if (eventData) {
          const dateObj = new Date(eventData.dateOfEvent);
          dateObj.setMinutes(dateObj.getMinutes() - dateObj.getTimezoneOffset());
          const formattedDate = dateObj.toISOString().slice(0, 16);

          const roundedBasePrice = eventData.basePrice ? Number(eventData.basePrice.toFixed(2)) : null;
          formData.value = {
            ...eventData,
            dateOfEvent: formattedDate,
            basePrice: roundedBasePrice,
            managerIds: eventData.managerIds || []
          };
        }
      } catch (err) {
        console.error('Error fetching event:', err);
      }
    };

    const handleSubmit = async () => {
      if (dateError.value) {
        return;
      }

      try {
        const dateTime = new Date(formData.value.dateOfEvent);
        const isoDateTime = dateTime.toISOString().slice(0, 19);

        const eventData = {
          ...formData.value,
          dateOfEvent: isoDateTime,
          basePrice: Number(formData.value.basePrice.toFixed(2)),
        };

        const response = await updateEvent(formData.value.id, eventData);

        if (response) {
          showSuccess.value = true;

          await new Promise(resolve => setTimeout(resolve, 3000));

          showSuccess.value = false;

        }

      } catch (err) {
        console.error('Error updating event:', err);
      }
    };

    onMounted(() => {
      fetchEventManagers();
      fetchEvent();
    });

    return {
      formData,
      eventManagers,
      managerError,
      eventError,
      managerLoading,
      eventLoading,
      minDateTime,
      maxDateTime,
      dateError,
      handleSubmit,
      finalPrice,
      showSuccess
    };
  }
}
</script>