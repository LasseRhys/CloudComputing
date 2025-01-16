<template>
  <div class="p-6 flex flex-col items-center">
    <h2 class="text-2xl  font-semibold mb-4">Notifications</h2>

    <!-- Table -->
    <table class="min-w-full  table-auto border-collapse border border-gray-200">
      <thead>
      <tr class="bg-gray-100">
        <th class="px-4 py-2 border">Nachricht</th>
        <th class="px-4 py-2 border">Datum</th>
        <th class="px-4 py-2 border">Typ</th>
        <th class="px-4 py-2 border">Status</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="notification in notifications" :key="notification.id"
          :class="{'bg-gray-50': notification.isRead}">
        <td class="px-4 py-2 border notification-message">{{ notification.message }}</td>
        <td class="px-4 py-2 border">{{ formatDate(notification.createdAt) }}</td>
        <td class="px-4 py-2 border">{{ notification.type }}</td>
        <td class="px-4 py-2 border">
            <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium"
                  :class="notification.isRead ? 'bg-green-100 text-green-800' : 'bg-yellow-100 text-yellow-800'">
              {{ notification.isRead ? 'Gelesen' : 'Ungelesen' }}
            </span>
        </td>
      </tr>
      </tbody>
    </table>

    <!-- Pagination Controls -->
    <BasePagination
        :current-page="currentPage"
        :total-pages="totalPages"
        @page-change="loadPage"
    />

    <!-- Loading State -->
    <div v-if="loading" class="flex justify-center items-center mt-4">
      <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-500"></div>
    </div>

    <!-- Error State -->
    <div v-if="error" class="mt-4 p-4 bg-red-100 text-red-700 rounded">
      {{ error }}
    </div>
  </div>
</template>

<script>
import { useNotifications } from "@/composables/useNotifications";
import BasePagination from '@/components/BasePagination.vue';
import websocketService from '@/utils/websocket';

export default {
  name: 'NotificationsTable',
  components: {
    BasePagination
  },
  setup() {
    const { loading, error, getUserNotifications, markMultipleAsRead  } = useNotifications();
    return {
      loading,
      error,
      getUserNotifications,
      markMultipleAsRead
    };
  },
  data() {
    return {
      notifications: [],
      totalPages: 0,
      currentPage: 0,
      sortConfig: {
        sortBy: 'createdAt',
        direction: 'desc'
      },
      unsubscribeWebSocket: null
    };
  },
  methods: {
    formatDate(date) {
      return new Date(date).toLocaleDateString();
    },
    async loadPage(page) {
      const response = await this.getUserNotifications({
        page,
        size: 10,
        sortBy: this.sortConfig.sortBy,
        direction: this.sortConfig.direction
      });

      if (response) {
        this.notifications = response.content;
        this.totalPages = response.totalPages;
        this.currentPage = response.number;

        const unreadNotificationIds = this.notifications
            .filter(notification => !notification.isRead)
            .map(notification => notification.id);
        if (unreadNotificationIds.length > 0) {
          console.log(unreadNotificationIds)
          await this.markMultipleAsRead(unreadNotificationIds);
        }
      }
    },
    async handleSort() {
      await this.loadPage(0);
    },
    handleNewNotification(notification) {
      // Add the new notification to the top of the list
      this.notifications.unshift({
        ...notification,
        isRead: false
      });

      // If we're at the page size limit, remove the last notification
      if (this.notifications.length > 10) {
        this.notifications.pop();
      }
    }
  },
  async mounted() {
    await this.loadPage(0);

    try {
      if (!websocketService.isConnected) {
        await websocketService.connect();
      }

      this.unsubscribeWebSocket = websocketService.on('userNotification', this.handleNewNotification);
    } catch (error) {
      console.error('Failed to connect to WebSocket:', error);
    }
  },
  beforeUnmount() {
    // Clean up WebSocket subscription when component is destroyed
    if (this.unsubscribeWebSocket) {
      this.unsubscribeWebSocket();
    }
  }
};
</script>

<style>
.notification-message {
    white-space: pre-line;
}
</style>