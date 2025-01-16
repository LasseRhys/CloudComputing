<template>
  <div>
    <component :is="currentSidebar" />
    <div class="relative md:ml-64 bg-blueGray-100">
      <component :is="currentNavbar" />
      <div class="mt-4 px-4 md:px-10 mx-auto w-full -m-24">
        <router-view></router-view>
        <component :is="currentFooter" />
      </div>
    </div>
  </div>
</template>

<script>
import { defineComponent, computed } from 'vue'
import { useStore } from 'vuex'

import KundeNavbar from "@/components/Navbars/KundeNavbar.vue"
import KundeSidebar from "@/components/Sidebars/KundeSidebar.vue"
import FooterKunde from "@/components/Footers/FooterKunde.vue"

import EventCreatorNavbar from "@/components/Navbars/EventCreatorNavbar.vue"
import EventCreatorSidebar from "@/components/Sidebars/EventCreatorSidebar.vue"
import FooterEventCreator from "@/components/Footers/FooterEventCreator.vue"

import AdminNavbar from "@/components/Navbars/AdminNavbar.vue"
import AdminSidebar from "@/components/Sidebars/AdminSidebar.vue"
import FooterAdmin from "@/components/Footers/FooterAdmin.vue"

export default defineComponent({
  name: "DynamicLayout",
  components: {
    KundeNavbar,
    KundeSidebar,
    FooterKunde,
    EventCreatorNavbar,
    EventCreatorSidebar,
    FooterEventCreator,
    AdminNavbar,
    AdminSidebar,
    FooterAdmin,
  },
  setup() {
    const store = useStore()

    const userRoles = computed(() => store.getters['auth/userRoles'])

    const currentSidebar = computed(() => {
      if (userRoles.value.includes('ROLE_ADMINISTRATOR')) {
        return 'AdminSidebar'
      }
      return userRoles.value.includes('ROLE_EVENT_CREATOR') || userRoles.value.includes('ROLE_EVENT_MANAGER')
          ? 'EventCreatorSidebar'
          : 'KundeSidebar'
    })

    const currentNavbar = computed(() => {
      if (userRoles.value.includes('ROLE_ADMINISTRATOR')) {
        return 'AdminNavbar'
      }
      return userRoles.value.includes('ROLE_EVENT_CREATOR') || userRoles.value.includes('ROLE_EVENT_MANAGER')
          ? 'EventCreatorNavbar'
          : 'KundeNavbar'
    })

    const currentFooter = computed(() => {
      if (userRoles.value.includes('ROLE_ADMINISTRATOR')) {
        return 'FooterAdmin'
      }
      return userRoles.value.includes('ROLE_EVENT_CREATOR') || userRoles.value.includes('ROLE_EVENT_MANAGER')
          ? 'FooterEventCreator'
          : 'FooterKunde'
    })

    return {
      currentSidebar,
      currentNavbar,
      currentFooter
    }
  }
})
</script>