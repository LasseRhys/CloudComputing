<template>
  <div>
    <event-creator-sidebar />
    <div class="relative md:ml-64 bg-blueGray-100">
      <event-creator-navbar />
      <!-- <header-stats /> -->
      <div class="mt-4 px-4 md:px-10 mx-auto w-full -m-24">
        <router-view />
        <footer-event-creator />
      </div>
    </div>
  </div>
</template>
<script>

import EventCreatorNavbar from "@/components/Navbars/EventCreatorNavbar.vue";
import EventCreatorSidebar from "@/components/Sidebars/EventCreatorSidebar.vue";
// import HeaderStats from "@/components/Headers/HeaderStats.vue";
import FooterEventCreator from "@/components/Footers/FooterEventCreator.vue";

export default {
  name: "EventCreator-layout",
  components: {
    EventCreatorNavbar,
    EventCreatorSidebar,
    // HeaderStats,
    FooterEventCreator,
  },
};
</script>
