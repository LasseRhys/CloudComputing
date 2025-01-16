<template>
  <div class="flex justify-center items-center min-h-screen p-4">
    <!-- Event Karte, auf 50% der Seite begrenzt -->
    <div class="bg-white shadow-lg rounded-lg overflow-hidden max-w-md w-full">
      <div class="bg-blue-500 text-white p-4 rounded-t-lg">
        <!-- Navbar für den Titel -->
        <h2 class="text-2xl font-semibold text-center">{{ event.name }}</h2>
      </div>
      <div class="p-6">
        <!-- Event Details: Datum, Ort, Verfügbare Tickets und Preis -->
        <p class="mt-2 text-center text-gray-600">Date: {{ event.date }}</p>
        <p class="mt-2 text-center text-gray-600">Location: {{ event.location }}</p>
        <p class="mt-2 text-center text-gray-600">Available Tickets: {{ event.availableTickets }}</p>
        <p class="mt-4 text-center text-xl font-bold">Price: €{{ event.price }}</p>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      // Event-Informationen als statisches Array
      event: {
        id: 1,
        name: "Music Concert",
        date: "01/01/2025",
        location: "Berlin",
        price: 50.00,
        availableTickets: 100, // Verfügbare Tickets
        hasVoucher: true // Zeigt an, ob ein Gutschein verfügbar ist
      }
    };
  },
  methods: {
   
    }
  
};
</script>

<style scoped>
/* Optional Styling für das Event-Kartenlayout */
.bg-white {
  background-color: white;
}
.shadow-lg {
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}
.rounded-lg {
  border-radius: 8px;
}
.overflow-hidden {
  overflow: hidden;
}
.text-gray-600 {
  color: #4a4a4a;
}
.bg-blue-500 {
  background-color: #3b82f6;
}
.text-white {
  color: white;
}
</style>
