# SearchInput.vue
<template>
  <div class="search-input-wrapper">
    <div class="relative">

      <input
          :value="modelValue"
          @input="handleInput"
          type="text"
          :placeholder="placeholder"
          class="w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
          :class="{ 'opacity-50': disabled }"
          :disabled="disabled"
      />

    </div>
  </div>
</template>

<script>
export default {
  name: 'SearchInput',
  props: {
    modelValue: {
      type: String,
      default: ''
    },
    placeholder: {
      type: String,
      default: 'Search...'
    },
    debounceTime: {
      type: Number,
      default: 300
    },
    disabled: {
      type: Boolean,
      default: false
    }

  },
  data() {
    return {
      debounceTimeout: null
    }
  },
  methods: {
    handleInput(event) {
      if (this.disabled) return

      const value = event.target.value
      this.$emit('update:modelValue', value)

      if (this.debounceTimeout) {
        clearTimeout(this.debounceTimeout)
      }

      this.debounceTimeout = setTimeout(() => {
        this.$emit('search', value)
      }, this.debounceTime)
    },
  },
  // Clean up timeout on component destroy
  beforeUnmount() {
    if (this.debounceTimeout) {
      clearTimeout(this.debounceTimeout)
    }
  }
}
</script>