<template>
  <div class="min-h-screen bg-gray-100">
    <div class="container mx-auto px-4 sm:px-6 lg:px-8 max-w-7xl">
      <div v-if="loading" class="flex justify-center items-center py-16">
        <div class="animate-spin rounded-full h-12 w-12 border-b-4 border-indigo-600"></div>
      </div>

      <div v-if="error" class="bg-red-50 border-l-4 border-red-400 shadow-lg rounded-lg p-6 mt-4">
        <div class="flex items-center">
          <div class="flex-shrink-0">
            <svg class="h-5 w-5 text-red-400" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor">
              <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd"/>
            </svg>
          </div>
          <div class="ml-3">
            <h3 class="text-sm font-medium text-red-800">Fehler beim Erstellen der Gutscheine</h3>
            <div class="mt-2 text-sm text-red-700">{{ error }}</div>
          </div>
        </div>
      </div>

      <div v-if="showSuccess" class="mt-4 mb-2 p-4 bg-emerald-200 border border-emerald-400 text-green-700 rounded mx-4">
        <div class="flex items-center">
          <svg class="w-5 h-5 mr-2" fill="currentColor" viewBox="0 0 20 20">
            <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd"/>
          </svg>
          <p class="font-medium">Erfolg! Die Gutscheine wurden erfolgreich erstellt.</p>
        </div>
      </div>

      <div class="bg-white shadow-xl rounded-md overflow-hidden">
        <div class="px-6 py-8 space-y-8">
          <div class="bg-white rounded-xl shadow-lg p-6 border border-gray-100">
            <h3 class="text-xl font-semibold text-gray-900 mb-6">Gutscheine erstellen</h3>

            <div class="space-y-6">
              <div class="grid grid-cols-1 gap-6 sm:grid-cols-2">
                <div class="space-y-3">
                  <label for="voucherCount" class="block text-sm font-medium text-gray-500">
                    Anzahl der zu erstellenden Gutscheine
                  </label>
                  <input
                      id="voucherCount"
                      type="number"
                      v-model.number="formData.count"
                      @beforeinput="preventNonNumeric"
                      min="1"
                      class="block w-full px-4 py-3 rounded-md border border-gray-300 focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                      required
                  />
                </div>

                <div class="space-y-3">
                  <label for="voucherAmount" class="block text-sm font-medium text-gray-500">
                    Gutscheinwert (€)
                  </label>
                  <input
                      id="voucherAmount"
                      type="number"
                      v-model.number="formData.amount"
                      @beforeinput="preventNonNumeric"
                      min="0"
                      step="0.01"
                      class="block w-full px-4 py-3 rounded-md border border-gray-300 focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                      required
                  />
                </div>
              </div>

              <div class="space-y-3">
                <label for="validityDays" class="block text-sm font-medium text-gray-500">
                  Gültigkeitsdauer in Tagen
                </label>
                <input
                    id="validityDays"
                    type="number"
                    v-model.number="formData.validityDays"
                    @beforeinput="preventNonNumeric"
                    min="1"
                    class="block w-full px-4 py-3 rounded-md border border-gray-300 focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                    required
                />
              </div>

              <div class="space-y-3">
                <label for="voucherType" class="block text-sm font-medium text-gray-500">
                  Gutscheintyp
                </label>
                <select
                    id="voucherType"
                    v-model="formData.type"
                    class="block w-full px-4 py-3 rounded-md border border-gray-300 focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                    required
                >
                  <option value="" disabled>Typ auswählen</option>
                  <option v-for="type in voucherTypes" :key="type" :value="type">
                    {{ type }}
                  </option>
                </select>
              </div>
            </div>
          </div>

          <div v-if="formData.count > 0" class="bg-white rounded-xl shadow-lg p-6 border border-gray-100">
            <h3 class="text-xl font-semibold text-gray-900 mb-6">Zusammenfassung</h3>
            <div class="space-y-4">
              <p class="text-sm text-gray-500">
                Sie erstellen:
                <span class="font-medium text-gray-900">{{ formData.count }}</span> Gutscheine
              </p>
              <p class="text-sm text-gray-500">
                Jeder Gutschein hat einen Wert von:
                <span class="font-medium text-indigo-600">{{ formData.amount }}€</span>
              </p>
              <p class="text-sm text-gray-500">
                Gesamtwert:
                <span class="font-medium text-indigo-600">{{ totalValue }}€</span>
              </p>
            </div>
          </div>

          <div class="flex justify-end space-x-4 pt-6">
            <button
                type="button"
                @click="resetForm"
                class="px-6 py-3 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 transition-colors duration-150"
            >
              Zurücksetzen
            </button>
            <button
                type="submit"
                @click="handleCreateVouchers"
                class="px-6 py-3 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-500 hover:bg-indigo-600 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 transition-colors duration-150 disabled:opacity-50"
            >
              <span v-if="loading">Wird erstellt...</span>
              <span v-else>Gutscheine erstellen</span>
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import {computed, onMounted, ref} from 'vue'
import {useVoucher} from '@/composables/useVoucher'
import {preventNonNumeric} from "@/utils/formatter";

export default {
  name: 'CreateVouchers',
  methods: {preventNonNumeric},

  setup() {
    const voucherTypes = ref([]);
    const { loading, error, getVoucherTypes, bulkCreateVoucher } = useVoucher()
    const showSuccess = ref(false);

    const formData = ref({
      count: 1,
      amount: 0,
      validityDays: '',
      type: ''
    })

    const totalValue = computed(() => {
      return (formData.value.count * formData.value.amount).toFixed(2)
    })

    onMounted(async () => {
      try {
        voucherTypes.value = await getVoucherTypes();
      } catch (err) {
        console.error('Error fetching voucher types:', err)
      }
    })

    const isFormValid = computed(() => {
      console.log(formData)
      return formData.value.count > 0 &&
          formData.value.amount > 0 &&
          formData.value.validityDays &&
          formData.value.type
    })

    const handleCreateVouchers = async () => {
      if (!isFormValid.value) return

      try {
        const voucherData = {
          count: formData.value.count,
          amount: formData.value.amount,
          validityDays: formData.value.validityDays,
          type: formData.value.type
        }
        onMounted(async () => {
          try {
            await getVoucherTypes()
          } catch (err) {
            console.error('Error fetching voucher types:', err)
          }
        })
        const response = await bulkCreateVoucher(voucherData)
        if (response) {
          showSuccess.value = true;
          resetForm();

          await new Promise(resolve => setTimeout(resolve, 3000));

          showSuccess.value = false;

        }
      } catch (err) {
        console.error('Error creating vouchers:', err)
      }
    }

    const resetForm = () => {
      formData.value = {
        count: 1,
        amount: 0,
        validityDays: '',
        type: ''
      }
    }

    return {
      loading,
      error,
      formData,
      totalValue,
      isFormValid,
      handleCreateVouchers,
      resetForm,
      voucherTypes,
      showSuccess
    }
  }
}
</script>