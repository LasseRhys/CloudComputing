<template>
  <div class="flex justify-center items-center mt-4 space-x-2 bg-gray-50 p-4 rounded-lg">
    <button
        class="px-2 py-1 bg-gray-300 rounded hover:bg-gray-400 disabled:opacity-50 disabled:cursor-not-allowed"
        @click="$emit('page-change', currentPage - 1)"
        :disabled="currentPage === 0"
    >
      &larr;
    </button>

    <div class="flex space-x-1">
      <template v-for="(page, index) in displayedPagesWithEllipsis" :key="index">
        <button
            v-if="page !== '...'"
            @click="$emit('page-change', page)"
            class="px-3 py-1 rounded border"
            :class="{
              'bg-blue-500 text-white': currentPage === page,
              'bg-gray-300 hover:bg-gray-400': currentPage !== page,
            }"
        >
          {{ page + 1 }}
        </button>
        <span
            v-else
            class="px-2 py-1"
        >
          {{ page }}
        </span>
      </template>
    </div>

    <button
        class="px-2 py-1 bg-gray-300 rounded hover:bg-gray-400 disabled:opacity-50 disabled:cursor-not-allowed"
        @click="$emit('page-change', currentPage + 1)"
        :disabled="currentPage >= totalPages - 1"
    >
      &rarr;
    </button>
  </div>
</template>

<script>
export default {
  name: 'BasePagination',
  props: {
    currentPage: {
      type: Number,
      required: true
    },
    totalPages: {
      type: Number,
      required: true
    }
  },
  emits: ['page-change'],
  computed: {
    displayedPages() {
      const total = this.totalPages;
      const current = this.currentPage;
      const pages = new Set();

      pages.add(0);

      for (let i = Math.max(current - 1, 1);
           i <= Math.min(current + 1, total - 2); i++) {
        pages.add(i);
      }

      if (total > 1) {
        pages.add(total - 1);
      }

      return [...pages].sort((a, b) => a - b);
    },
    displayedPagesWithEllipsis() {
      const pages = this.displayedPages;
      const result = [];

      for (let i = 0; i < pages.length; i++) {
        result.push(pages[i]);
        if (i < pages.length - 1 && pages[i + 1] - pages[i] > 1) {
          result.push('...');
        }
      }

      return result;
    }
  }
};
</script>