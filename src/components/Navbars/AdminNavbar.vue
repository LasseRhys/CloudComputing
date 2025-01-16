<template>
  <!-- Navbar -->
  <nav class="border-gray-200 bg-gray-50 dark:bg-gray-800 dark:border-gray-700">
    <div class="max-w-screen-xl flex flex-wrap items-center justify-between mx-auto p-4">
      <a href="#" class="flex items-center space-x-3 rtl:space-x-reverse">
        <span class="self-center text-xl font-semibold whitespace-nowrap dark:text-white">{{ currentPage }}</span>
      </a>
      <!-- User -->
      <ul class="flex-col md:flex-row list-none items-center hidden md:flex">
        <notification-dropdown />
        <user-dropdown />
      </ul>
    </div>
    <hr class="mb-4 border-b-1 border-blueGray-200" />
  </nav>
</template>

<script>
import UserDropdown from "@/components/Dropdowns/UserDropdown.vue";
import NotificationDropdown from "@/components/Dropdowns/NotificationDropdown.vue";
import {mapGetters} from "vuex";

export default {
  components: {
    UserDropdown,
    NotificationDropdown
  },
  computed: {
    ...mapGetters('auth', ['isAuthenticated']),

    currentPage() {
      switch (this.$route.path) {
          // Dashboard-Routen
        case '/dashboard':
          return 'Übersicht';

          // Kunden-Routen
        case '/tickets':
          return 'Meine Tickets';
        case '/vouchers':
          return 'Meine Gutscheine';
        case '/events':
          return 'Veranstaltungen';
        case '/profile':
          return 'Profil';
        case '/notifications':
          return 'Benachrichtigungen';
        case '/purchase-ticket':
          return 'Ticket kaufen';

          // Veranstalter-Routen
        case '/event-creator/dashboard':
          return 'Veranstalter Übersicht';
        case '/event-creator/profile':
          return 'Veranstalter Profil';
        case '/event-creator/events':
          return 'Veranstaltungen verwalten';
        case '/event-creator/create':
          return 'Neue Veranstaltung erstellen';
        case '/event-creator/event':
          return 'Event';
          // Admin-Routen
        case '/admin/employee-register':
          return 'Mitarbeiter registrieren';
        case '/admin/events':
          return 'Veranstaltungen verwalten';
        case '/admin/vouchers':
          return 'Gutscheine verwalten';
        case '/admin/vouchers/create':
          return 'Gutschein erstellen';

          // Authentifizierungs-Routen
        case '/auth/login':
          return 'Anmelden';
        case '/auth/register':
          return 'Registrieren';

          // Dynamische Routen mit Parametern
        default:
          // Parameter-Routen behandeln
          if (this.$route.name === 'EventShow') return 'Veranstaltungsdetails';
          if (this.$route.name === 'PurchaseTicket') return 'Ticket kaufen';
          if (this.$route.name === 'UpdateEventForm') return 'Veranstaltung bearbeiten';
          if (this.$route.name === 'EventCreatorEventShow') return "Veranstaltung"
          return 'Unbekannte Seite';
      }
    }
  }
};
</script>