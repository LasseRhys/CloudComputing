<template>
  <div class="flex flex-col items-center justify-start min-h-screen mt-6">
    <div class="grid grid-cols-1 sm:grid-cols-1 lg:grid-cols-3 gap-6 w-full px-4">
      <div class="relative flex flex-col min-w-0 break-words bg-white rounded mb-6 xl:mb-0 shadow-lg">
        <div class="flex-auto p-4">
          <div class="flex flex-wrap">
            <div class="relative w-full pr-4 max-w-full flex-grow flex-1">
              <h5 class="text-blueGray-400 uppercase font-bold text-xs">
                Gebuchte Veranstaltungen
              </h5>
              <span class="font-semibold text-xl text-blueGray-700">
                {{ dashboardInfo?.eventDashboard?.count || 0 }} Veranstaltungen
              </span>
            </div>
            <router-link to="/tickets" class="relative w-auto pl-4 flex-initial">
              <div class="text-white p-3 text-center inline-flex items-center justify-center w-12 h-12 shadow-lg rounded-full bg-emerald-500">
                <i class="fas fa-ticket-alt"></i>
              </div>
            </router-link>
          </div>
          <p class="text-sm text-blueGray-400 mt-4">
            <router-link to="/tickets" class="text-emerald-500 hover:text-emerald-600">
              Tickets anzeigen →
            </router-link>
          </p>
        </div>
      </div>

      <div class="relative flex flex-col min-w-0 break-words bg-white rounded mb-6 xl:mb-0 shadow-lg">
        <div class="flex-auto p-4">
          <div class="flex flex-wrap">
            <div class="relative w-full pr-4 max-w-full flex-grow flex-1">
              <h5 class="text-blueGray-400 uppercase font-bold text-xs">
                Nächste Veranstaltung
              </h5>
              <span class="font-semibold text-xl text-blueGray-700">
                {{ dashboardInfo?.nextEvent?.title || 'Keine anstehenden Veranstaltungen' }}
              </span>
            </div>
            <router-link to="/events" class="relative w-auto pl-4 flex-initial">
              <div class="text-white p-3 text-center inline-flex items-center justify-center w-12 h-12 shadow-lg rounded-full bg-red-500">
                <i class="fas fa-calendar"></i>
              </div>
            </router-link>
          </div>
          <div v-if="dashboardInfo?.nextEvent" class="text-sm text-blueGray-400 mt-4">
            <p>{{ formatDate(dashboardInfo.nextEvent.dateOfEvent) }}</p>
            <p>{{ dashboardInfo.nextEvent.location }}</p>
            <router-link to="/events" class="text-red-500 hover:text-red-700">
              Alle Veranstaltungen anzeigen →
            </router-link>
          </div>
          <p v-else class="text-sm text-blueGray-400 mt-4">
            <router-link to="/events" class="text-red-500 hover:text-red-700">
              Verfügbare Veranstaltungen durchsuchen →
            </router-link>
          </p>
        </div>
      </div>

      <div class="relative flex flex-col min-w-0 break-words bg-white rounded mb-6 xl:mb-0 shadow-lg">
        <div class="flex-auto p-4">
          <div class="flex flex-wrap">
            <div class="relative w-full pr-4 max-w-full flex-grow flex-1">
              <h5 class="text-blueGray-400 uppercase font-bold text-xs">
                Verfügbare Gutscheine
              </h5>
              <span class="font-semibold text-xl text-blueGray-700">
                {{ formatPrice(dashboardInfo?.voucherDashboard?.value || 0) }}
              </span>
            </div>
            <router-link to="/vouchers" class="relative w-auto pl-4 flex-initial">
              <div class="text-white p-3 text-center inline-flex items-center justify-center w-12 h-12 shadow-lg rounded-full bg-blue-500">
                <i class="fas fa-gift"></i>
              </div>
            </router-link>
          </div>
          <p class="text-sm text-blueGray-400 mt-4">
            {{ dashboardInfo?.voucherDashboard?.count || 0 }} Gutscheine verfügbar
            <router-link to="/vouchers" class="block text-blue-500 hover:text-blue-700 mt-1">
              Gutscheine anzeigen →
            </router-link>
          </p>
        </div>
      </div>

    </div>
  </div>
</template>


<script setup>
import { ref, onMounted } from 'vue'
import { useDashboard } from "@/composables/useDashboard"
import {formatDate, formatPrice} from "../../../utils/formatter";

const { error, loading, getDashboardInformation } = useDashboard()
const dashboardInfo = ref(null)



onMounted(async () => {
  try {
    dashboardInfo.value = await getDashboardInformation()
  } catch (err) {
    console.error('Error fetching dashboard information:', err)
  }
})
</script>