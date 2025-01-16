<template>
  <div>
    <router-link to="/notifications"
      class="text-blueGray-1000 block py-1 px-3"
      ref="btnDropdownRef"
     href=""    >

      <i class="fas fa-bell text-3xl"></i>
    </router-link>


  </div>
</template>
<script>
import { createPopper } from "@popperjs/core";

export default {
  data() {
    return {
      dropdownPopoverShow: false,
    };
  },
  methods: {
    toggleDropdown: function (event) {
      event.preventDefault();
      if (this.dropdownPopoverShow) {
        this.dropdownPopoverShow = false;
      } else {
        this.dropdownPopoverShow = true;
        createPopper(this.$refs.btnDropdownRef, this.$refs.popoverDropdownRef, {
          placement: "bottom-start",
        });
      }
    },
  },
};
</script>
