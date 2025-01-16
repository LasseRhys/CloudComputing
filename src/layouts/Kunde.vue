<template>
  <div>
    <kunde-sidebar />
    <div class="relative md:ml-64 bg-blueGray-100">
      <kunde-navbar />
      <!-- <header-stats /> -->
      <div class="mt-4 px-4 md:px-10 mx-auto w-full -m-24">
        <router-view />
        <footer-kunde />
      </div>
    </div>
  </div>
</template>
<script>

import KundeNavbar from "@/components/Navbars/KundeNavbar.vue";
import KundeSidebar from "@/components/Sidebars/KundeSidebar.vue";
// import HeaderStats from "@/components/Headers/HeaderStats.vue";
import FooterKunde from "@/components/Footers/FooterKunde.vue";

export default {
  name: "kunde-layout",
  components: {
    KundeNavbar,
    KundeSidebar,
    // HeaderStats,
    FooterKunde,
  },
};
</script>
